
## development guide

- it is recommended to use a unix system, MacOS or Linux (ubuntu 18.04 for example)
- that said, everything runs in docker, so should work on Windows

#### setup

- install
    - docker
    - docker-compose 
    - git 
    - vscode (optional, use other editor instead, this guide uses vscode)

- clone the repo:
```shell
# clone
```

#### develop

- open repository with vscode editor
```shell
# 'code' is a vscode command to open a folder with vscode
code ged
```

- `c` file in the root contains functions (commands) for varoius tasks
- press `ctrl+j` to open vscode's `panel`
- select `terminal` tab
- press `ctrl+shift+5` to add another terminal (or use the button 'split terminal')
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

- once the services have started, navigate to `localhost:8600/geoserver/web` 
  to make sure geoserver is running
- if not, run `docker-compose restart geoserver`
- [import sample layers](#importing-sample-layers) into geoserver
- in the second terminal:

```shell
# open ged container's shell into /opt/app
bash c term ged
# start ged dev server
bash c dev

```
- wait for 'Build complelted', open `localhost:8800` in Chrome 
- press F12 to open Chrome development tools
- at this point, clojure/clojurescript REPLs are connected to both the app running in the browser and the development web server (shadow-cljs dev server)
- for better experience, you'll need a second editor window (for actaully focusing on the code, without 'noise' files)
- from system's terminal (not from vscode) open another vscode window, this time for `ged/spaces/ui`

```shell
# open ged/spaces/ui
code ged/spaces/ui
```
- `ged/spaces/ui` is a symbolic directory, containing links to code files specifically
- install vscode's `Calva` extension for clojure/clojuscript support (press reload if needed)
- press `ctrl+alt+c ctrl+alt+c` or press `nREPL` button (bottom left) and select 'connect to a running REPL server in your project'
- type `localhost:8888` - this is a REPL port
- select `[:app]` build
- press `ctrl+j` to hide the panel - you won't use the panel in the second window 
- now you can navigate between files, use REPL and hot-reload
- at this point you have two vscode windows open
    - use the first window (repo's root) to start/stop containers, commit, start/stop ged dev server, change build files
    - use the second window (ged/spaces/ui) (ged/spaces/ui) to actually write code

#### branches

- checkout a branch using issue number, for example issue/#23
- commit changes
- create a pull request to the master branch

#### links

- [dev links](./links.md)

#### importing sample layers

- `geoserver/.data` directory in the root of the repository is accessible in geoserver container at `/opt/data` path

- put layers into `geoserver/.data` directory

- use [geoserver import guide](https://docs.geoserver.org/latest/en/user/extensions/importer/using.html)
  to import files from `/opt/data`

- geoserver UI available at `localhost:8600/geoserver/web`, username:password  are `admin` : `myawesomegeoserver` or `admin` : `geoserver`

- example layers can be found at `git clone https://github.com/Esri/arcgis-runtime-samples-data`

- before importing you might need to:
    - use `localhost:8600/geoserver/web` ui
    - create for example a `dev` workspace and `pgdb` store
    - uncheck 'Return bounding box with every feature' in WFS service
