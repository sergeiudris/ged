(ns srv.core
  (:require [clojure.repl]
            [srv.server]
            [clj-http.client :as client]
            [slingshot.slingshot :refer [throw+ try+]]))

(comment
  
  (client/get "http://geoserver:8080" {})

  (client/get "http://geoserver:8080/geoserver/rest/layers.json" 
              {:basic-auth ["admin" "myawesomegeoserver"]})
  
  ; fail
  (client/get "http://geoserver:8080/geoserver/rest/layers.json"
              {:basic-auth ["admin" "wrongpassword"]})
  
  ;
  )
