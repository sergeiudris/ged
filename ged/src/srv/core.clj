(ns srv.core
  (:require [clojure.repl]
            [srv.server]
            [clj-http.client :as client]
            [slingshot.slingshot :refer [throw+ try+]]))

(comment
  
  (client/get "http://geoserver:8080" {:basic-auth ["admin" "geoserver"]})
  ;
  )

