(ns ged.map.ol
  (:require
   ["ol/proj/Projection" :default OlProjection]
   ["ol/proj" :refer [fromLonLat]]
   ["ol/Map" :default OlMap]
   ["ol/View" :default OlView]
   ["ol/source/OSM" :default OlOSM]
   ["ol/layer/Tile" :default OlTileLayer]
   ["ol/layer/Vector" :default OlVectorLayer]
   )
  )

(defn tile-layer-osm
  []
  (OlTileLayer.
   #js {"source" (OlOSM.)}))

(defn create-view
  [opts]
  (OlView.
   (clj->js
    (merge {
            :projection "EPSG:3857"
            :center [0 0]
            ; :maxZoom 28
            :zoom 0
            }
           opts))))

(defn create-map
  [{:keys [el-id target]
    :or {el-id "map-container"}}]
  (OlMap.
   (clj->js {:layers [(tile-layer-osm)]
             :target (or target (js/document.getElementById el-id))
             :view (create-view {})})))

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