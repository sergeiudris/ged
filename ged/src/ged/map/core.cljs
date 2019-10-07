(ns ged.map.core
  (:require  [reagent.core :as r]
             [cljs.repl :as repl]
             [cljs.pprint :as pp]
             [re-frame.core :as rf]
             [day8.re-frame.tracing :refer-macros [fn-traced defn-traced]]
             [ged.map.ol :as ol]
             ["ol/format/filter" :as olf]))

(def ^:export astate
  (atom {:olmap nil
         :mapbox nil
         :modify-session {:select nil
                          :modify nil
                          :draw nil
                          :layer nil
                          :source nil}}))

(defn get-key
  [k]
  (k @astate))

(defn assoc-key!
  [k v]
  (swap! astate assoc k v))

(defn set-olmap!
  [olmap]
  (assoc-key! :olmap olmap))

(defn merge-state!
  [m]
  (swap! astate merge m))

(defn get-olmap
  []
  (get-key :olmap))

(defn set-modify-session!
  [m]
  (assoc-key! :modify-session m))

(defn get-modify-session
  []
  (:modify-session @astate))

(defn get-modify-features
  []
  (->
   (.getFeatures (:source (get-modify-session)))
   (ol/features->geojson)
   (aget "features")))


; re-frame events


(rf/reg-event-fx
 ::create-olmap
 (fn-traced [{:keys [db]} [_ ea]]
            (if-not (get-olmap)
              (do
                (->
                 (ol/create-map {:el-id ea})
                 (set-olmap!))
                (set! (.. js/window -map) (get-olmap)))
              (do (ol/set-target (get-olmap) ea)))
            {}))

(rf/reg-event-fx
 ::set-olmap-target
 (fn-traced [{:keys [db]} [_ ea]]
            (do
              (when (get-olmap)
                (ol/set-target (get-olmap) ea)))
            {}))

(rf/reg-event-fx
 ::refetch-wms-layers
 (fn-traced
  [{:keys [db]} [_ ea]]
  (do
    (let [lrs (.getArray  (.getLayers (get-olmap)))]
      (doseq [lr  lrs]
        (when (.get lr "id")
          (.updateParams (.getSource lr) #js {:r (Math/random)})))))
  {}))

(rf/reg-event-fx
 ::refetch-wms-layer
 (fn-traced
  [{:keys [db]} [_ ea]]
  (do
    (let [lr (ol/id->layer (get-olmap) ea)]
      (when lr
        (.updateParams (.getSource lr) #js {:r (Math/random)}))))
  {}))

(rf/reg-event-fx
 ::sync-layer-ids
 (fn-traced
  [{:keys [db]} [_ ea]]
  (do
    (let [[ids {:keys [geoserver-host wms-use-auth?]}] ea
          lrs (.getArray (.getLayers (get-olmap)))]
      (doseq [lr lrs]
        (let [id (.get lr "id")]
          (when id
            (when-not (some #(= id %) ids)
              (.removeLayer (get-olmap) lr)))))
      (doseq [id ids]
        (when-not (ol/id->layer (get-olmap) id)
          (ol/upsert-wms-layer (get-olmap) {:geoserver-host  geoserver-host
                                            :wms-use-auth? wms-use-auth?
                                            :id id})))))
  {}))

(defn id->layer
  [olmap id]
  (->>
   (.getLayers olmap)
   (.getArray)
   (filter (fn [lr] (= (.get lr "id") id)))
   (first)))

(rf/reg-event-fx
 ::mapclick-listen
 (fn-traced
  [{:keys [db]} [_ ea]]
  (do
    (let [{:keys [on-click]} ea]
      (when (get-olmap)
        (.on (get-olmap) "singleclick" on-click))))
  {}))

(rf/reg-event-fx
 ::mapclick-unlisten
 (fn-traced
  [{:keys [db]} [_ ea]]
  (do
    (let [{:keys [on-click]} ea]
      (when (get-olmap)
        (.un (get-olmap) "singleclick" on-click))))
  {}))

(rf/reg-event-fx
 ::wfs-search-mapclick
 (fn-traced
  [{:keys [db]} [_ ea]]
  (do
    (let [[ev] ea
          coords (.. ev -coordinate)
          geom (ol/point->cir-poly-geom (get-olmap) coords 16)
          filter (olf/intersects "the_geom"  geom)
                    ; wkt (ol/point->wkt-cir-poly {:coords coords :radius 8})
          ]
      {:dispatch [:ged.map.evs/wfs-search {:filter filter}]}))))

(rf/reg-event-fx
 ::wfs-modify-mapclick
 (fn-traced
  [{:keys [db]} [_ ea]]
  (do
    (let [[ev] ea
          coords (.. ev -coordinate)
          geom (ol/point->cir-poly-geom (get-olmap) coords 16)
          filter (olf/intersects "the_geom"  geom)
                    ; wkt (ol/point->wkt-cir-poly {:coords coords :radius 8})
          ]
      {:dispatch [:ged.map.evs/modify-wfs-click {:filter filter}]}))))

(rf/reg-event-fx
 ::wfs-search-mapbox-listen
 (fn-traced
  [{:keys [db]} [_ ea]]
  (do
    (let [{:keys [on-draw-end]} ea]
      (->>
       (ol/add-box-interaction (get-olmap) {:on-draw-end on-draw-end})
       (assoc-key! :mapbox))))
  {}))

(rf/reg-event-fx
 ::wfs-search-mapbox-unlisten
 (fn-traced
  [{:keys [db]} [_ ea]]
  (do
    (let []
      (when (get-olmap)
        (ol/remove-interaction (get-olmap) (get-key :mapbox)))))
  {}))

(rf/reg-event-fx
 ::wfs-search-mapbox
 (fn-traced
  [{:keys [db]} [_ ea]]
  (do
    (let [[ev] ea
          geom (.getGeometry (.-feature ev))
          filter (olf/intersects "the_geom"  geom)]
      {:dispatch [:ged.map.evs/wfs-search {:filter filter}]}))))

(rf/reg-event-fx
 ::start-modify-session
 (fn-traced
  [{:keys [db]} [_ ea]]
  (do
    (let [[ftedn] ea]
      (assoc-key! :modify-session (ol/add-modify-session (get-olmap) ftedn))
      {}))))

(rf/reg-event-fx
 ::stop-modify-session
 (fn-traced
  [{:keys [db]} [_ ea]]
  (do
    (let [session (get-modify-session)]
      (when (and (get-olmap) session)
        (do (ol/remove-modify-session (get-olmap) session)))
      {}))))


; re-frame iceptors

(rf/reg-cofx
 ::olmap
 (fn [cofx _]
   (assoc cofx :olmap (do (get-olmap)))))

(rf/reg-cofx
 ::modify-features
 (fn [cofx [k]]
   (assoc cofx :modify-features (do (get-modify-features)))))
