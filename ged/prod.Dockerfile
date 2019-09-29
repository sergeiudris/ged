FROM ubuntu:18.04

RUN apt-get update && \
    apt-get install -y openjdk-8-jdk

WORKDIR /tmp

RUN curl -sL https://deb.nodesource.com/setup_11.x | sudo -E bash - && \
    sudo apt-get install -y nodejs 

WORKDIR /opt/app

COPY deps.edn .
RUN clojure -A:cache -Stree

EXPOSE 9500 7888 9630 8801 8899