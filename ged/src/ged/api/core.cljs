(ns ged.api.core
  (:require [clojure.string :as str]))

(defn auth-creds
  []
  (str "Basic " (js/btoa (str "admin" ":" "myawesomegeoserver"))))

(defn deep-merge [a & maps]
  (if (map? a)
    (apply merge-with deep-merge a maps)
    (apply merge-with deep-merge maps)))

(defn fetch-geosrv
  [path opts]
  (->
   (js/fetch (str "/geoserver" path)
             (clj->js (deep-merge
                       {"method" "GET"
                        "headers" {"Authorization"  (auth-creds)}}
                       opts)))
   ))

(defn fetch-geosrv-edn
  ([path]
   (fetch-geosrv-edn path {}))
  ([path opts]
   (->
    (fetch-geosrv path opts)
    (.then (fn [res] (.json res)))
    (.then (fn [j] (js->clj j  :keywordize-keys true))))))

; curl -u admin:myawesomegeoserver -XGET http://localhost:8801/geoserver/rest/layers.json

; headers.set ('Authorization', 'Basic '+ Buffer.from (username + ":" + password) .toString ('base64'));
;window.btoa (username + ':' + password)

#_"http://localhost:8600/geoserver/rest/layers.json"
#_"http://localhost:8801/geoserver/rest/layers.json"
#_"http://localhost:8080/v1"
#_"http://localhost:8801"
#_"http://localhost:8600/geoserver/web/"
#_"http://localhost:8600/geoserver/web/wicket/bookmarkable/org.geoserver.web.demo.MapPreviewPage?2"
#_(->
   (js/fetch "http://localhost:8801"
             (clj->js {"method" "GET"
                      ; "headers" {"Authorization"
                      ;            (str "Basic " (js/btoa (str "admin" ":" "myawesomegeoserver")))
                      ;            }
                       }))
   (.then (fn [res] (.text res)))
   (.then (fn [r] (js/console.log r))))

#_(->
   (js/fetch "/hello"
             (clj->js {"method" "GET"}))
   (.then (fn [res] (.text res)))
   (.then (fn [r] (js/console.log r))))

#_(->
   (js/fetch "/geoserver/rest/layers.json"
             (clj->js {"method" "GET"}))
   (.then (fn [res] (.text res)))
   (.then (fn [r] (js/console.log r))))

#_(->
   (js/fetch "/geoserver/rest/layers.json"
             (clj->js {"method" "GET"
                       "headers" {"Authorization"
                                  (str "Basic " (js/btoa (str "admin" ":" "myawesomegeoserver")))}}))
   (.then (fn [res] (.json res)))
   (.then (fn [j] (js->clj j  :keywordize-keys true)))
   (.then (fn [r] (js/console.log r))))


#_(->
   (fetch-geosrv-edn "/rest/layers.json")
   (.then (fn [r] (js/console.log r)))
   )