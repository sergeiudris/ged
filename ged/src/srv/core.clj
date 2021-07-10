(ns srv.core
  (:require [clojure.repl :refer :all]
            ; [srv.server]
            [srv.dev-http]
            [clj-http.client :as client]
            #_[slingshot.slingshot :refer [throw+ try+]]))


(comment
  
  (client/get "http://geoserver:8080" {})

  (->
   (client/get "http://geoserver:8080/geoserver/rest/layers.json"
               {:basic-auth ["admin" "myawesomegeoserver"]})
   (pp/pprint))
  
  ; fail
  (client/get "http://geoserver:8080/geoserver/rest/layers.json"
              {:basic-auth ["admin" "wrongpassword"]})
  
  (dir client)
  
  (client/get "http://localhost:8801"
              )
  
  (client/get
   "http://geoserver:8080/geoserver/wfs"
   {
    ;:accept :json 
    :query-params {"service" "wfs"
                   "version" "2.0.0"
                   "request" "GetFeature"
                   "count" 1
                   "startIndex" 2
                   "typeNames" "dev:usa_major_cities"
                   "exceptions" "application/json"
                   "outputFormat" "application/json"
                   }}
   )
  
  
  
  ;
  )
