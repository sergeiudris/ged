(ns ged.map.subs
  (:require [re-frame.core :as rf]))

(rf/reg-sub
 ::module-count
 (fn [db _]
   (:ged.core/module-count db)))




(rf/reg-sub
 ::selected-layers-checked
 (fn [db _]
   (:ged.map/selected-layers-checked db)))

(rf/reg-sub
 ::all-layers-checked
 (fn [db _]
   (:ged.map/all-layers-checked db)))

(rf/reg-sub
 ::checked-layer-ids
 (fn [qv _]
   [(rf/subscribe  [::all-layers-checked])
    (rf/subscribe  [::selected-layers-checked])])
 (fn [[all selected] _]
   (let []
     (->>
      (concat all selected)
      (distinct)
      (vec)))))

(rf/reg-sub
 ::tab-button
 (fn [db _]
   (:ged.map/tab-button db)))

(rf/reg-sub
 ::all-layers-visible
 (fn [query-v _]
   [(rf/subscribe [::tab-button])])
 (fn [[tab-button] qv _]
   (= tab-button :all-layers)))

(rf/reg-sub
 ::selected-layers-visible
 (fn [query-v _]
   [(rf/subscribe [::tab-button])])
 (fn [[tab-button] qv _]
   (= tab-button :selected-layers)))

(rf/reg-sub
 ::wfs-search-visible
 (fn [query-v _]
   [(rf/subscribe [::tab-button])])
 (fn [[tab-button] qv _]
   (= tab-button :wfs-search)))

(rf/reg-sub
 ::all-layers
 (fn [db _]
   (let [data (:ged.map/fetch-all-layers-res db)]
     (get-in data [:layers :layer]))))

(rf/reg-sub
 ::selected-layers
 (fn [db _]
   (let [ids (:ged.map/selected-layers-ids db)]
     (mapv (fn [id]
             {:name id
              :href nil}) ids))))

(rf/reg-sub
 ::wfs-search-layer-input
 (fn [db _]
   (:ged.map/wfs-search-layer-input db)))

(rf/reg-sub
 ::wfs-search-area-type
 (fn [db _]
   (:ged.map/wfs-search-area-type db)))

(rf/reg-sub
 ::wfs-search-res
 (fn [db _]
   (:ged.map/wfs-search-res db)))

(rf/reg-sub
 ::wfs-search-map-click?
 (fn [db _]
   (= (:ged.map/wfs-search-area-type db) :area-point)
   ))

(rf/reg-sub
 ::wfs-search-table-mdata
 (fn [db _]
   (:ged.map/wfs-search-table-mdata db)))
