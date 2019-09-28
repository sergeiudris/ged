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

(defn wms-source
  [opts]
  (TileWMS.
   (clj->js
    (deep-merge
     {:url "/geoserver/wms"
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
  ([ids]
   (wms-layer {} {:params  {"LAYERS" ids}}))
  ([opts src-opts]
  (OlTileLayer.
   (clj->js
    (deep-merge
     {:source (wms-source src-opts)}
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