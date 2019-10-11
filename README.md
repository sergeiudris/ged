# ged

<div align="center" >
<a href="./doc/resources/tour-20191011/Peek 2019-10-11 08-16.gif?raw=true">
<img width="80%" src="./doc/resources/tour-20191011/Peek 2019-10-11 08-16.gif" alt=" no image :(" ></img>
</a>
</div>


- a tool
- a complementary geoserver UI for searching and editing features
- pronounced *"jed"*


status: usable


## content

- [quick start](#quick-start)
- [features](#features)

## quick start 

- install [docker](https://docs.docker.com/install/), [docker-compose](https://docs.docker.com/compose/install/), [git](https://git-scm.com/) 
- in terminal:

```shell

# clone repo 
git clone https://github.com/seeris/ged

# change directory to repo root
cd ged

# start
docker-compose up -d

# stop 
docker-compose down

```
- open `localhost:8800` in browser


## features

- profiles (persisted in localStorage) to work with multiple geoservers or simply different sessions
- view geoserver layers on the map (check/uncheck multiple layers)
- search layers
- select layers
- search features by attributes , select on the map or specify ecql filter
- edit feature properties
- modify features (map editing)
- edit feature types (layers) via /rest
- request proxying to solve CORS problems
- optional /wms proxying
- inspect /wfs transactions' body and response xml


*tried with geoserver 2.15, but in theory should work with other versions<br />
