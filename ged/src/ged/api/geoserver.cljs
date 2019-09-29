(ns ged.api.geoserver
  (:require [clojure.string :as str]
            ["ol/format/filter" :as OlFilter ]
            ["ol/format/WFS" :default OlFormatWFS]
            ["ol/format/GeoJSON" :default OlFormatGeoJSON]))

#_(OlFilter/like "name" "Mississippi*"  )

#_(OlFilter/ilike "name" "Mississippi*")

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
  [{:keys [offset limit
           featurePrefix featureTypes
           filter]}]
  (.writeGetFeature (OlFormatWFS.)
                    (clj->js
                     (merge
                      {"srsName" "EPSG:3857"
                       "featurePrefix" featurePrefix
                       "featureTypes" featureTypes
                       "outputFormat" "application/json"
                       "startIndex" offset
                       "maxFeatures" limit
                       "count" limit}
                      (when filter {"filter" filter})))))

(defn xml->str
  [x]
  (.serializeToString (js/XMLSerializer.) x))

(defn wfs-get-features-body-str
  [opts]
  (->
   (wfs-get-features-body opts)
   (xml->str)))

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
            (wfs-get-features-body {
                                    :offset 0
                                    :limit 10
                                    :featurePrefix "dev"
                                    :featureTypes ["usa_major_cities"]})
            (xml->str))})
   (.then (fn [r] (js/console.log r))))


(defn jsons->features
  [jsons]
  (when jsons
    (->
     (OlFormatGeoJSON.
      #js {;"geometryName" "the_geom"
           "extractGeometryName" true
           })
     (.readFeatures
      #js {"type" "FeatureCollection"
           "features" (clj->js jsons)}))))


(defn wfs-transaction-body
  [{:keys [featurePrefix featureType
           featureNS
           srsName
           inserts updates deletes]
    ; :or {inserts #js []
    ;      updates #js []
    ;      deletes #js []}
    }]
  (.writeTransaction (OlFormatWFS.)
                     inserts  updates deletes
                     (clj->js
                      {"srsName" (or srsName "EPSG:3857")
                       "featureNS" featureNS
                       "featurePrefix" featurePrefix
                       "featureType" featureType
                       "outputFormat" "application/json"
                       "version" "1.1.0"
                      ;  "gmlOptions" { 
                      ;                "featureNS" "http://www.opengis.net/wfs/dev"
                      ;                "featureType" (str featurePrefix ":" featureType )
                      ;                "srsName" (or srsName "EPSG:3857")
                                     
                      ;                }
                       
                       })))

(defn wfs-tx-jsons
  [opts]
  (let []
    (wfs-transaction-body
     (merge
      opts
      {:inserts (jsons->features (:inserts opts))
       :updates (jsons->features (:updates opts))
       :deletes (jsons->features (:deletes opts))}))))

(defn wfs-tx-jsons-str
  [opts]
  (->
   (wfs-tx-jsons  opts)
   (xml->str)
   #_(str/replace "<Name>geometry</Name>" "<Name>the_geom</Name>")
   ))
