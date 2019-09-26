#!/bin/bash

dc(){

    docker-compose --compatibility \
        -f docker-compose.yml \
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

link_ui() {
    SPACE=ui
    mkdir -p spaces/$SPACE
    ln -s ../../ged/src spaces/$SPACE/ged
    ln -s ../../ged/shadow-cljs.edn spaces/$SPACE/shadow-cljs.edn
    ln -s ../../ged/resources/public/css spaces/$SPACE/css
    ln -s ../../.vscode spaces/$SPACE/.vscode
}

"$@"