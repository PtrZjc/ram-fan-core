# Rick and Morty fan tool - core  
### Introduction 
The intention of this tool is to use the free public Rick and Morty api and enable user tracking his favourite characters and locations from the series.   
### Planned core capabilities  
The backend will use the data captured from free [Rick and Morty API](https://rickandmortyapi.com/) and enable user to make more complicated queries regarding the various things occurring in the series.
The backend will have exposed REST API (defined in OpenApi 3.0) to be used by *Rick and Morty fan tool - page*, which is planned to developed as a part of learning Angular.  
### Technologies  
- Java 11
- Postgres + JOOQ + liquibase
- Spock as testing framework
### First setup 
- With the first run, two schemas will be made  - `ram_data` and `ram_liquibase`
- During each application launch (and later - periodically), a series of requests are made to original **Rick and Morty API** service to fetch and cache all missing records. Consumers of *ram-fan-core* will always get the cached objects if possible.