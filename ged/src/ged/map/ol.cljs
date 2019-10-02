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
   ["ol/geom/Circle" :default OlGeomCircle]
   ["ol/Feature" :default OlFeature]
   ["ol/format/GeoJSON" :default OlFormatGeoJSON]
   ["ol/format/WKT" :default OlFormatWKT]
   ["ol/geom/Polygon" :as OlGeomPolygon]
   ["ol/interaction/Draw" :default OlDrawInteraction :as OlDraw]
   ["ol/source/Vector" :default OlVectorSource]
   ["ol/layer/Vector" :default OlVectorLayer]
   ["ol/interaction/Select" :default OlInteractionSelect]
   ["ol/interaction/Modify" :default OlInteractionModify]
   
   )
  )

#_OlDraw/createBox

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
            :maxZoom 28

            ; :center [0 0]
            ; :zoom 0
            :center [-11000000, 4600000]
            :zoom 4
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
   (.getArray)
   (filter (fn [lr] (= (.get lr "id") id)))
   (first)))

(defn refetch-wms-layer
  [olmap id]
  (js/console.log "update wms layer:" id)
  (->
   (id->layer olmap id)
   (.getSource)
   (.updateParams #js {})))

(defn add-wms-layer
  [olmap geoserver-host id]
  (.addLayer olmap (wms-layer geoserver-host id)))

(defn upsert-wms-layer
  [olmap geoserver-host id]
  (let [lr (id->layer olmap id)]
    (when-not lr
      (add-wms-layer olmap geoserver-host id))))


(defn remove-layer
  [olmap id]
  (let [lr (id->layer olmap id)]
    (.removeLayer olmap lr)))


#_(some #(= "1" %) ["1" "2"])

#_(when-not true (prn 3))


(defn px->meters
  [olmap px]
  (let [resolution (.getResolution (.getView olmap))]
    (->
     (* px resolution)
     (/  2))))

#_(defn point->cir-poly-geom
  [olmap coords radius-px]
  (OlGeomPolygon/circular coords (px->meters olmap radius-px) 16 #_6371008.8))

(defn point->cir-poly-geom
  [olmap coords radius-px]
  (OlGeomPolygon/fromCircle
   (OlGeomCircle. (clj->js coords) (px->meters olmap radius-px))
   16))

(defn point-coords->circlular-polygon
  [{:keys [olmap coords radius]}]
  (OlFeature.
   (clj->js
    {"geometry" (point->cir-poly-geom olmap coords  radius)})))

(defn feature->geojson
  [ft]
  
  (->
   (OlFormatGeoJSON. #js {})
   (.writeFeature ft)))

(defn features->geojson
  [fts]
  (let [json (->
            (OlFormatGeoJSON.
             #js {"extractGeometryName" true
                  "geometryName" true})
            (.writeFeaturesObject fts))
        edn (js->clj json :keywordize-keys true)
        fts (mapv
             (fn [ft] (assoc ft :geometry_name "the_geom"))
             (:features edn))]
    (clj->js (merge
              edn
              {:features fts}))))

(defn point-coords->circle-geojson
  [opts]
  (->
   (point-coords->circlular-polygon opts)
   (feature->geojson)))

(defn feature->wkt
  [ft]
  (->
   (OlFormatWKT.)
   (.writeFeature ft)))

(defn point->wkt-cir-poly
  [opts]
  #_(js/console.log opts)
  (->>
   (point-coords->circlular-polygon opts)
   (feature->wkt)))

#_(OlGeomPolygon/circular [0 0 ] 8 16 )


(defn add-box-interaction
  [olmap opts]
  (let [{:keys [on-draw-end]} opts
        source (OlVectorSource. #js {"wrapX" false})
        layer (OlVectorLayer.
               #js {"source" source})
        draw (OlDrawInteraction.
              #js {"type" "Circle"
                   "source" source
                   "geometryFunction" (OlDraw/createBox)})]
    (do
      (.addLayer olmap layer)
      (.on draw "drawend" on-draw-end )
      (.addInteraction olmap draw))
    {:source source
     :layer layer
     :interaction draw}))

(defn remove-interaction
  [olmap opts]
  (let [{:keys [interaction source layer]} opts]
    (do
      (.removeInteraction olmap interaction)
      (.removeLayer olmap layer))))

(defn add-modify-session
  [olmap ftedn]
 
  (let [json (clj->js ftedn)
        ft (.readFeature (OlFormatGeoJSON.
                          #js {"extractGeometryName" true}) json)
        source (OlVectorSource. #js {"wrapX" false
                                     "features" #js [ft]})
        layer (OlVectorLayer.
               #js {"source" source})
        select (OlInteractionSelect.
                {"features" (.getFeatures source)})
        modify (OlInteractionModify.
                #js {"features" (.getFeatures select)})]
    (do
      (.addLayer olmap layer)
      (.addInteraction olmap select)
      (.addInteraction olmap modify)
      #_(.on modify "modifyend" (fn [ev] (js/console.log ev))))
    {:select select
     :modify modify
     :layer layer
     :source source}))

(defn remove-modify-session
  [olmap opts]
  (let [{:keys [select modify
                layer source]} opts]
    (do
      (.removeLayer olmap layer)
      (.removeInteraction olmap select)
      (.removeInteraction olmap modify))))

(defn get-modify-features
  [modify]
  (->
   modify
   (.getOverlay)
   (.getSource)
   (.getFeatures)))