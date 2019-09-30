(ns ged.map.ol
  (:require
   ["ol/proj/Projection" :default OlProjection]
   ["ol/proj" :refer [fromLonLat]]
   ["ol/Map" :default OlMap]
   ["ol/View" :default OlView]
   ["ol/source/OSM" :default OlOSM]
   ["ol/layer/Tile" :default OlTileLayer]
   ["ol/layer/Vector" :default OlVectorLayer]
   ["ol/source/TileWMS" :default TileWMS]
   )
  )

(defn deep-merge [a & maps]
  (if (map? a)
    (apply merge-with deep-merge a maps)
    (apply merge-with deep-merge maps)))

(defn tile-layer-osm
  []
  (OlTileLayer.
   #js {"source" (OlOSM.)}))

(defn create-view
  [opts]
  (OlView.
   (clj->js
    (merge {:projection "EPSG:3857"
            :center [0 0]
            :maxZoom 28
            :zoom 0
            ; :center [-11000000, 4600000]
            ; :zoom 4
            }
           opts))))

(defn create-map
  [{:keys [el-id target]
    :or {el-id "map-container"}}]
  (OlMap.
   (clj->js {:layers [(tile-layer-osm)]
             :target (or target (js/document.getElementById el-id))
             :view (create-view {})
             :pixelRatio 1})))

(defn to-data-url
  [url]
  (->
   (js/fetch url
             (clj->js {;"headers" {"Authorization"  (auth-creds)}
                       :method "get"}))
   (.then (fn [res] (.blob res)))
   (.then (fn [blob]
            (js/Promise.
             (fn [resolve reject]
               (let [r (js/FileReader.)]
                 (do
                   (set! (.. r -onloadend) #(resolve (. r -result)))
                   (set! (.. r -onerror) reject)
                   (.readAsDataURL r blob)))))))))
  

(defn tile-loader-from-string-body
  [tile src]
  (->
   (to-data-url src)
   (.then (fn [durl]
            (js/console.log durl)
            (set! (.. (.getImage tile) -src) durl))))
  #_(->
     (js/fetch src
               (clj->js {;"headers" {"Authorization"  (auth-creds)}
                         :method "get"}))
     (.then (fn [res] (.text res)))
     (.then (fn [r] (let []
                      #_(js/console.log r)
                      #_(set! (.. (.getImage tile) -src) url)
                      (set! (.. (.getImage tile) -src) (str "data:image/png;" r)))))))

(defn wms-source
  [geoserver-host opts]
  (TileWMS.
   (clj->js
    (deep-merge
     {:url
     #_"http://localhost:8801/geoserver/wms" ; works... unclear how
      (str geoserver-host "/geoserver/wms")
      #_"/geoserver/wms"
      ; :tileLoadFunction tile-loader-from-string-body
      :params {;"LAYERS" "dev:usa_major_cities"
               "TILED" true
               "SRS" "EPSG:3857"
              ;  "exceptions" "application/vnd.ogc.se_inimage"
               "exceptions" "application/json"
               "TRANSPARENT" true
               "FORMAT" "image/png"
               "REQUEST" "GetMap"
               "VERSION" "1.1.1"
               "SERVICE" "WMS"}
      :serverType "geoserver"}
     opts))))

(defn wms-layer
  ([geoserver-host id]
   (wms-layer geoserver-host {:id id} {:params  {"LAYERS" id}}))
  ([geoserver-host opts src-opts]
   (OlTileLayer.
    (clj->js
     (deep-merge
      {:source (wms-source geoserver-host src-opts)}
      opts)))))



#_(js/console.log (clj->js {:view (OlView.
                                   {:center [0 0]
                                    :maxZoom 28
                                    :zoom 0})} ))
#_(js/console.log OlView)

#_(js/console.log (OlView.
                   {:center [0 0]
                    :maxZoom 28
                    :zoom 0}))

(defn set-target
  [olmap el-id]
  (.setTarget olmap (js/document.getElementById el-id)))


(defn id->layer
  [olmap id]
  (->>
   (.getLayers olmap)
   (filter (fn [lr] (= (.get lr "id") id)))
   (first)))

(defn update-wms-layer
  [olmap id]
  (js/console.log "update wms layer:" id)
  (->
   (id->layer olmap id)
   (.getSource)
   (.updateParams #js {})))

(defn add-layer
  [olmap geoserver-host id]
  (.addLayer olmap (wms-layer geoserver-host id)))