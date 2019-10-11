# ged

<div align="center" >
<a href="./doc/resources/tour-20191011/overview-1 2019-10-11 08-16.gif?raw=true">
<img width="80%" src="./doc/resources/tour-20191011/overview-1 2019-10-11 08-16.gif" alt=" no image :(" ></img>
</a>
</div>


- a tool
- a complementary geoserver UI for searching and editing features
- pronounced *"jed"*


status: usable


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
- open `localhost:8800` in browser to use ged, `localhost:8600/geoserver/web` to use geoserver ui
- [import sample layers](./doc/development-guide.md#importing-sample-layers)


## features

[preview](./doc/features.md)

- profiles (persisted in localStorage) to work with multiple geoservers or simply different sessions
- search, view geoserver layers 
- search features on the map (box, click)
- search features via attributes or ecql filter directly
- edit feature attributes
- modify features on the map
- edit feature types (layers) via /rest
- request proxying to solve CORS problems
- optional /wms proxying
- inspect /wfs transactions' body and response xml

*tried with geoserver 2.15, but in theory should work with other versions<br />


## rationale

- geoserver UI covers all cases, except 
    - viewing multiple layers on the map simultaneously
    - searching and editing features using WFS
- ged is a *tool* that complements geoserver's built-in UI by providing missing features

## links

- [features preview](./doc/features.md)

- [development guide](./doc/development-guide.md)


