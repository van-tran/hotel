package com.example.hotelsdatamerger.configuration

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonTypeInfo
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.jsontype.impl.StdTypeResolverBuilder
import com.fasterxml.jackson.module.kotlin.KotlinModule
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer


@Configuration
class RedisConfiguration {

    @Autowired
    lateinit var objectMapper: ObjectMapper


    @Value("\${spring.redis.host}")
    lateinit var redisHost : String

    @Value("\${spring.redis.port}")
    var redisPort : Int = 6379

    @Value("\${spring.redis.password}")
    lateinit var redisPass : String

    @Bean
    fun jedisConnectionFactory(): JedisConnectionFactory {
        val jedisConFactory = JedisConnectionFactory()
        jedisConFactory.hostName = redisHost
        jedisConFactory.port = redisPort
        jedisConFactory.setPassword(redisPass)

        return jedisConFactory
    }
    @Bean
    fun redisTemplate(): RedisTemplate<String, Any> {
        val template = RedisTemplate<String, Any>()
        template.setDefaultSerializer(GenericJackson2JsonRedisSerializer(getRedisObjectMapper()))
        template.connectionFactory = jedisConnectionFactory()
        return template
    }

    private fun getRedisObjectMapper(): ObjectMapper {
        val objectMapper = objectMapper.copy()
        var defaultTypeResolver: StdTypeResolverBuilder = ObjectMapper.DefaultTypeResolverBuilder(
            ObjectMapper.DefaultTyping.EVERYTHING,
            objectMapper.polymorphicTypeValidator
        )
        defaultTypeResolver = defaultTypeResolver.init(JsonTypeInfo.Id.CLASS, null)
        defaultTypeResolver = defaultTypeResolver.inclusion(JsonTypeInfo.As.PROPERTY)
        objectMapper.setDefaultTyping(defaultTypeResolver)
        return objectMapper
    }


}