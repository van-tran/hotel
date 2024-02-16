# HOTEL data information

## Problem & Solution ##
* Dynamic response format
    * Different property names, typo, content format flaw
      - Flatten nested json format into Map (name -> value). Hence, we could normalize & merge values between hotel info from different sources
      - Standardize attribute name and value according to Regex-based configuration. E.g: facilities would be converted to emenities, or trim leading space  
    * Different nested levels
      - Create new Node Level (named `others`) to make final response has the same hierarchy levels
    * Further updating properties 
      - Configurations for property standards, mapper would be loaded dynamically from configuration database. In the scope of this project, we load from resource files. 

* Source APIs haven't implemented filter/pagination. It leads to fetching overhead all data even when looking for specific hotel IDs or destination IDs
  - Caching (e.g redis) to save data fetching & computation 
  - Interval refresh cache every 3 minutes ( should be configured & adjust based on real business logic)

## Run ##

* Project has default configuration within .env file
*
* After init gradle,
* Run test cases : `./gradlew bootTestRun`
* Build Jar file :  `./gradlew bootJar`
* Then, run following command to deploy docker containers :
  `docker compose up --build -d`
  
  
