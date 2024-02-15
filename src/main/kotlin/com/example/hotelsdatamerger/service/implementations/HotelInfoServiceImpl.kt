package com.example.hotelsdatamerger.service.implementations

import com.example.hotelsdatamerger.dto.*
import com.example.hotelsdatamerger.repo.rest.HotelRetrofitClient
import com.example.hotelsdatamerger.repo.source.IConfigurationRepo
import com.example.hotelsdatamerger.service.IContentService
import com.example.hotelsdatamerger.service.IHotelInfoService
import com.example.hotelsdatamerger.utils.AttributeProcessor
import com.example.hotelsdatamerger.utils.JsonUtils
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class HotelInfoServiceImpl(
    val jsonUtils: JsonUtils,
    val hotelRetrofitClient: HotelRetrofitClient,
    val configurationRepo: IConfigurationRepo,
    val contentService: IContentService,
) : IHotelInfoService {

    // Initializing instance of Logger for Service
    val logger: Logger = LoggerFactory.getLogger(HotelInfoServiceImpl::class.java)

    val attributeRegex: List<AttributeDefinition> by lazy {
        configurationRepo.getAttributeSet()
    }

    val hotelIDKeyName: String by lazy { configurationRepo.getHotelIDKeyName() }


    suspend fun extractKeySet(hotelInfoItems: List<InfoObject>): Set<String> =
        hotelInfoItems.flatMap { items ->
            items.attributes.flatMap {
                when (it) {
                    is AttributeContent.PlainString<*>, is AttributeContent.ListString -> listOf(it.name)
                    is AttributeContent.ListNestedObject -> listOf(it.name) + extractKeySet(it.value)
                }
            }
        }.toSet()

    fun standardizeAttributeName(infoObject: InfoObject, attributeMapper: Map<String, AttributeInfo>): InfoObject {
        return infoObject.attributes
            .map { attr ->
                val (refinedName, virtualNode) = attributeMapper.get(attr.name)?.let { it.name to it.virtualNode }
                    ?: (attr.name to attr.virtualNode)

                when (attr) {
                    is AttributeContent.PlainString<*> -> AttributeContent.PlainString(
                        refinedName,
                        attr.value,
                        virtualNode
                    )

                    is AttributeContent.ListString -> AttributeContent.ListString(refinedName, attr.value, virtualNode)
                    is AttributeContent.ListNestedObject ->
                        AttributeContent.ListNestedObject(
                            refinedName,
                            attr.value.map { standardizeAttributeName(it, attributeMapper) })
                }
            }
            .let {
                InfoObject(it)
            }
    }

    fun standardizeAttributeName(infoObject: InfoObject, lstOfProcessors: List<AttributeProcessor>): InfoObject {
        return infoObject.attributes
            .map { attr ->
                val refinedAttributeInfo = lstOfProcessors.fold(AttributeInfo(attr.name, attr.virtualNode)) { attr, processor ->
                    processor.standardizeKey(attr)
                }

                when (attr) {
                    is AttributeContent.PlainString<*> -> {
                        val refinedContentValue = lstOfProcessors.fold(attr.value) { contentValue, processor ->
                            processor.standardizeValue(refinedAttributeInfo, contentValue)
                        }
                        AttributeContent.PlainString(
                            refinedAttributeInfo.name,
                            refinedContentValue,
                            refinedAttributeInfo.virtualNode
                        )
                    }

                    is AttributeContent.ListString -> AttributeContent.ListString(
                        refinedAttributeInfo.name,
                        attr.value.map {
                            lstOfProcessors.fold(it) { contentValue, processor ->
                                processor.standardizeValue(refinedAttributeInfo, contentValue)
                            }
                        },
                        refinedAttributeInfo.virtualNode
                    )

                    is AttributeContent.ListNestedObject ->
                        AttributeContent.ListNestedObject(
                            refinedAttributeInfo.name,
                            attr.value.map { standardizeAttributeName(it, lstOfProcessors) })
                }
            }
            .let {
                InfoObject(it)
            }
    }

    override suspend fun standardizeAttributeKeys(hotelInfo: List<InfoObject>): List<FlatHotelInfo> {
        val attributeMapper = extractKeySet(hotelInfo)
            .map { attributeName ->
                attributeRegex.fold(AttributeInfo(attributeName, false)) { attr, attributeMapping ->
                    var virtualNode = attr.virtualNode
                    attr.name.replace(Regex(attributeMapping.regex)) { matchResult ->
                        virtualNode = virtualNode || attributeMapping.virtualNode
                        attributeMapping.replacement
                    }
                        .takeIf { it != attributeName }
                        ?.let {
                            AttributeInfo(it, virtualNode)
                        } ?: attr
                }.let {
                    attributeName to it
                }
            }
            .toMap()
        val attributeProcessors = listOf(
            AttributeProcessor.KeyTransformer(attributeMapper),
            AttributeProcessor.ValueTransformer(configurationRepo.getContentMapper())
        )
        return hotelInfo.parallelStream()
            .map {
//                val refinedAttributes = standardizeAttributeName(it, attributeMapper)
                val refinedAttributes = standardizeAttributeName(it, attributeProcessors)
                val hotelID = refinedAttributes.attributes.find {
                    it is AttributeContent.PlainString<*>
                            && it.name == hotelIDKeyName
                }?.let {
                    (it as AttributeContent.PlainString<*>).value
                } ?: let {
                    logger.error("Missing hotel id")
                    ""
                }
                FlatHotelInfo(hotelID.toString(), refinedAttributes)
            }.toList()
    }

    override suspend fun mergeAttributesFromDifferentSource(
        hotelID: String,
        lsOfHotelInfo: List<FlatHotelInfo>
    ): FlatHotelInfo {
        return lsOfHotelInfo
            .flatMap { it.content.attributes }
            .filterNot { it.name.isNullOrBlank() }
            .let { attributeNodes ->
                // merge all attribute has plain string value
                val plainStringNodes: List<AttributeContent> = attributeNodes
                    .filterIsInstance<AttributeContent.PlainString<*>>()
                    .groupBy { it.name }
                    .mapValues {
                        it.value.maxByOrNull { contentService.scoreContent(it.value) }
                    }.values
                    .filterNotNull()
                    .toList()

                val lstStringNodes = processListStringAttributes(
                    attributeNodes
                        .filterIsInstance<AttributeContent.ListString>()
                )

                val lstObjectNodes = processListObjectAttributes(
                    attributeNodes
                        .filterIsInstance<AttributeContent.ListNestedObject>()
                )

                plainStringNodes + lstStringNodes + lstObjectNodes
            }
            .let {
                FlatHotelInfo(hotelID, InfoObject(it))
            }
    }

    fun processListStringAttributes(attributeNodes: List<AttributeContent.ListString>): List<AttributeContent> {
        // deduplicate attributes have collection String values
        val lstStringNodes: List<AttributeContent.ListString> = attributeNodes
            .groupBy { it.name }
            .mapValues { (name, values) ->
                values.flatMap {
                    it.value
                }.groupBy {
                    contentService.normalize(it)
                }.mapValues {
                    it.value.maxByOrNull { contentService.scoreContent(it) }
                }.values
                    .filterNotNull()
                    .let {
                        AttributeContent.ListString(
                            name,
                            it.toList(),
                            values.fold(false) { flag, node -> node.virtualNode || flag })
                    }
            }
            .values
            .toList()

        // merge and create new node when having different structure
        val mergedNodes = lstStringNodes.filter {
            it.virtualNode
        }
            .map { attributeNode ->
                val namePrefix = attributeNode.name.substringBeforeLast('.')
                val existingValues = attributeNodes.filter {
                    !it.virtualNode &&
                            it.name.startsWith(namePrefix)
                }.map { it as AttributeContent.ListString }
                    .flatMap { it.value }
                    .map {
                        contentService.normalize(it)
                    }.toSet()
                AttributeContent.ListString(
                    attributeNode.name,
                    attributeNode.value.filterNot {
                        existingValues.contains(contentService.normalize(it))
                    },
                    attributeNode.virtualNode
                )
            }

        return lstStringNodes.filterNot { it.virtualNode } + mergedNodes
    }

    fun processListObjectAttributes(attributeNodes: List<AttributeContent.ListNestedObject>): List<AttributeContent> {
        // deduplicate attributes have collection String values
        val lstObjectNodes: List<AttributeContent.ListNestedObject> = attributeNodes
            .groupBy { it.name }
            .mapValues { (name, values) ->
                values.flatMap {
                    it.value
                }.groupBy {
                    contentService.normalize(it)
                }.mapValues {
                    it.value.maxByOrNull { contentService.scoreContent(it) }
                }.values
                    .filterNotNull()
                    .let {
                        AttributeContent.ListNestedObject(name, it.toList())
                    }
            }
            .values
            .toList()

        // merge and create new node when having different structure
        val mergedNodes = lstObjectNodes.filter {
            it.virtualNode
        }
            .map { attributeNode ->
                val namePrefix = attributeNode.name.substringBeforeLast('.')
                val existingValues = attributeNodes.filter {
                    !it.virtualNode &&
                            it.name.startsWith(namePrefix)
                }
                    .flatMap { it.value }
                    .map {
                        contentService.normalize(it)
                    }.toSet()
                AttributeContent.ListNestedObject(attributeNode.name, attributeNode.value.filterNot {
                    existingValues.contains(contentService.normalize(it))
                })
            }
        return lstObjectNodes.filterNot { it.virtualNode } + mergedNodes
    }
    //
//
//    suspend fun parseHotelAttribute(hotelInfo: List<List<Pair<String, String>>>) {
//
//        val attributeMapper = hotelInfo.flatMap { hotelAttributes ->
//            hotelAttributes.map { it.first.trim() }
//        }.toSet()
//            .map { flatKey ->
//                attributeTemplate.values
//                    .find {
//                        it.alternativeNamesRegex.any { Regex(it).matches(flatKey) } ||
//                                it.includedAttributesRegex.any { Regex(it).matches(flatKey) }
//                    }.also { matchingAttribute ->
//                        if (matchingAttribute == null)
//                            logger.info("can't find any attribute match the key {}", flatKey)
//                    }
//                    ?.let { flatKey to it.name }
//            }.filterNotNull()
//            .toMap()
//
//        hotelInfo.map {
//            it.map { infoPair ->
//                attributeMapper.get(infoPair.first)
//                    ?.let { attributeName ->
//                        FlatHotelAttribute(attributeName, infoPair)
//                    }
//            }
//                .filterNotNull()
//                .groupBy { it.name }
//                .also {
//
//                }
//        }
//    }
}