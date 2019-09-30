FROM ubuntu:18.04

RUN apt-get update && \
    apt-get install -y \
            sudo  \
            curl  

RUN apt-get update && \
    apt-get install -y openjdk-8-jdk

WORKDIR /tmp

RUN curl -sL https://deb.nodesource.com/setup_11.x | sudo -E bash - && \
    sudo apt-get install -y nodejs 

ENV CLJ_SCRIPT=linux-install-1.10.1.466.sh
RUN curl -O https://download.clojure.org/install/$CLJ_SCRIPT && \
    chmod +x $CLJ_SCRIPT && \
    sudo ./$CLJ_SCRIPT

WORKDIR /opt/app

# 51mb
COPY deps.edn .
RUN clojure -A:shadow:dev:prod -Stree


COPY deps.edn package.json c shadow-cljs.edn ./
COPY src src
COPY test test
COPY resources resources  
RUN bash c prod


FROM ubuntu:18.04

RUN apt-get update && \
    apt-get install -y \
            sudo  \
            curl  

RUN apt-get update && \
    apt-get install -y openjdk-8-jre

RUN curl -sL https://deb.nodesource.com/setup_11.x | sudo -E bash - && \
    sudo apt-get install -y nodejs 

RUN npm i -g shadow-cljs

ENV CLJ_SCRIPT=linux-install-1.10.1.466.sh
RUN curl -O https://download.clojure.org/install/$CLJ_SCRIPT && \
    chmod +x $CLJ_SCRIPT && \
    sudo ./$CLJ_SCRIPT

WORKDIR /opt/app

COPY --from=0 /opt/app/resources resources
COPY --from=0 /opt/app/src src
COPY --from=0 /root/.m2 /root/.m2 
COPY --from=0 /root/.clojure /root/.clojure
COPY --from=0 /opt/app/shadow-cljs.edn /opt/app/c /opt/app/deps.edn ./

EXPOSE 9500 7888 9630 8801 8899

# # 64mb 
# FROM ubuntu:18.04

# # 107 
# RUN apt-get update && \
#     apt-get install -y \
#             sudo  \
#             curl  

# # 474
# RUN apt-get update && \
#     apt-get install -y openjdk-8-jdk

# # 592
# RUN curl -sL https://deb.nodesource.com/setup_11.x | sudo -E bash - && \
#     sudo apt-get install -y nodejs 

# # 629
# RUN npm i -g shadow-cljs
