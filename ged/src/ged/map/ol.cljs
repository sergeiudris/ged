(ns ged.map.ol
  (:require
   ["ol/proj/Projection" :default OlProjection]
   ["ol/proj" :refer [fromLonLat]]
   ["ol/Map" :default OlMap]
   ["ol/View" :default OlView])
  )

(defn create-map
  [{:keys [el-id target]
    :or {el-id "map-container"
          }}]
  (OlMap.
   (clj->js {:layers []
             :target (js/document.getElementById el-id)

             :view (OlView.
                    {:center [0 0]
                     :maxZoom 19
                     :zoom 0})})))

(defn set-target
  [olmap el-id]
  (.setTarget olmap (js/document.getElementById el-id)))