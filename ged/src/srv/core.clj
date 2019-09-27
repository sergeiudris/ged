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
  
  ;
  )
