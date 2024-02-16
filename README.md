# HOTEL data information

## Problem & Solution ##
* Dynamic response format
    * Different property names
    * Different nested levels
* Typo, content format flaw
* Source APIs haven't implemented filter/pagination. It leads to fetching overhead all data even when looking for specific hotel IDs or destination IDs

## Run ##

* Project has default configuration within .env file
*
* After init gradle,
* Run test cases : ./gradlew bootTestRun
* Build Jar file :  ./gradlew bootJar
* Then, run following commands to deploy docker containers :
  docker compose build
  docker compose up -d
  
  
