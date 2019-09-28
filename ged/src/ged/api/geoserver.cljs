(ns ged.api.geoserver
  (:require [clojure.string :as str]
            ["ol/format/filter" :as OlFilter ]
            ["ol/format/WFS" :default OlFormatWFS]
            ["ol/format/GeoJSON" :default OlFormatGeoJSON]))

#_(OlFilter/like "name" "Mississippi*"  )

; https://openlayers.org/en/v6.0.0/examples/vector-wfs-getfeature.html

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
                       {"headers" {"Authorization"  (auth-creds)}}
                       opts)))))

(defn fetch-geosrv-edn
  ([path]
   (fetch-geosrv-edn path {}))
  ([path opts]
   (->
    (fetch-geosrv path opts)
    (.then (fn [res] (.json res)))
    (.then (fn [j] (js->clj j  :keywordize-keys true))))))

(defn wfs-get-features-body
  [{:keys [offset limit featurePrefix featureTypes]}]
  (.writeGetFeature (OlFormatWFS.)
                    (clj->js
                     {"srsName" "EPSG:3857"
                      "featurePrefix" featurePrefix
                      "featureTypes" featureTypes
                      "outputFormat" "application/json"
                      "startIndex" offset
                      "count" limit})))

(defn xml->str
  [x]
  (.serializeToString (js/XMLSerializer.) x))

#_(->>
   (wfs-get-features-body {:offset 0
                           :limit 10
                           :featurePrefix "dev"
                           :featureTypes ["usa_major_cities"]})
   (.serializeToString (js/XMLSerializer.)))

#_(->
   (fetch-geosrv-edn
    "/wfs"
    {:method "post"
     :body (->
            (wfs-get-features-body {:to-string? true
                                    :offset 0
                                    :limit 10
                                    :featurePrefix "dev"
                                    :featureTypes ["usa_major_cities"]})
            (xml->str))})
   (.then (fn [r] (js/console.log r))))


