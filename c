#!/bin/bash

dc(){

    docker-compose --compatibility \
        -f dev.yml \
        "$@"
}

up(){
    dc up -d --build
}

down(){
    dc down 
}

term(){
   dc exec $1 bash -c "bash;"
}

spaces() {
    SPACE=ui
    mkdir -p spaces/$SPACE
    ln -s ../../ged/src/ged spaces/$SPACE/ged
    ln -s ../../ged/src/tools spaces/$SPACE/tools
    ln -s ../../ged/shadow-cljs.edn spaces/$SPACE/shadow-cljs.edn
    ln -s ../../ged/resources/public/css spaces/$SPACE/css
    ln -s ../../ged/src/srv spaces/$SPACE/srv
    ln -s ../../.vscode spaces/$SPACE/.vscode
}

cp_webxml(){
    # dc exec geoserver bash -c "rm /opt/data/web.xml"
    sudo cp geoserver/tomcat/webapps/geoserver/WEB-INF/web.xml geoserver/.data
    dc exec geoserver bash -c "cp /opt/data/web.xml /usr/local/tomcat/webapps/geoserver/WEB-INF/web.xml"
}

cat_webxml(){
    dc exec geoserver bash -c "cat /usr/local/tomcat/webapps/geoserver/WEB-INF/web.xml"
}

prod(){

    docker-compose --compatibility \
        -f prod.yml \
        "$@"
}

tagpush(){
    docker tag seeris/ged seeris/ged:dev
    docker push seeris/ged:dev
    docker push seeris/ged

}

hostip(){
    getent hosts unix.stackexchange.com | awk '{ print $1 }'
}

"$@"