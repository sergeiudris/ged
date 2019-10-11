
## development guide

- it is recommended using a unix system, MacOS or Linux (ubuntu 18.04 for example)
- that said, everything runs in docker, so should work on Windows

#### install

- install 
    - docker
    - docker-compose 
    - git 
    - vscode (optional, use other editor instead, this guide uses vscode)

- clone the repo:
```shell
# clone
git clone https://github.com/seeris/ged
```

- open repository with vscode editor
```shell
# 'code' is a vscode command to open a folder with vscode
code ged
```

- `c` file in the root contains functions (commands) for varoius tasks
- press `ctrl j` to open vscode's `panel`
- select `terminal` tab
- press `ctrl shift 5` to add another terminal (or use button 'split terminal')
- the first terminal will be used:
    - to start/stop docker containers
    - commit changes
- the second termnial will be used:
    - to start/stop ged development server
- in the first terminal:

```shell

# start geoserver and ged dev mode
bash c up

```
- once the services start, navigate to `localhost:8600/geoserver/web` 
  to make sure geoserver has started
- username:password for geoserver are `admin` : `myawesomegeoserver`
- using `localhost:8600/geoserver/web` ui:
    - create a `dev` workspace and `pgdb` store
    - uncheck 'Return bounding box with every feature' in WFS service
- [import sample layers](#importing-sample-layers) into geoserver
 


#### importing sample layers

- `.data` directory in the root of the repository is accessible in geoserver container at `/opt/data` path
- put layers into `.data` directory
- use [geoserver import guide](https://docs.geoserver.org/latest/en/user/extensions/importer/using.html)
  to import files from `/opt/data`
- example layers can be found at `git clone https://github.com/Esri/arcgis-runtime-samples-data`
