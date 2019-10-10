(ns ged.map.subs
  (:require [re-frame.core :as rf]
            [clojure.string :as str]))

(rf/reg-sub
 ::module-count
 (fn [db _]
   (:ged.db.core/module-count db)))




(rf/reg-sub
 ::selected-layers-checked
 (fn [db _]
   (:ged.db.map/selected-layers-checked db)))

(rf/reg-sub
 ::all-layers-checked
 (fn [db _]
   (:ged.db.map/all-layers-checked db)))

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
   (:ged.db.map/tab-button db)))

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
   (let [data (:ged.db.map/fetch-all-layers-res db)
         pag (get-in db [:ged.db.map/all-layers-table-mdata :pagination])
         {:keys [current pageSize]} pag
         xs (->> data :layers :layer)
         input (str/lower-case (:ged.db.map/all-layers-search-input db))
         xs-flt (if-not (empty? input)
                  (filterv
                   #(str/includes? (str/lower-case (:name %)) input)
                  ;  #(re-find (js/RegExp. input "i") (:name %))
                   xs)
                  xs)
         ]
     {:total (count xs-flt)
      :items (->> xs-flt
                  (drop (* (dec current) pageSize))
                  (take pageSize)
                  (vec))})))

(rf/reg-sub
 ::selected-layers
 (fn [db _]
   (let [ids (:ged.db.map/selected-layers-ids db)]
     (mapv (fn [id]
             {:name id
              :href nil}) ids))))

(rf/reg-sub
 ::wfs-search-layer-input
 (fn [db _]
   (:ged.db.map/wfs-search-layer-input db)))

(rf/reg-sub
 ::wfs-search-area-type
 (fn [db _]
   (:ged.db.map/wfs-search-area-type db)))

(rf/reg-sub
 ::wfs-search-res
 (fn [db _]
   (:ged.db.map/wfs-search-res db)))

(rf/reg-sub
 ::wfs-search-map-click?
 (fn [db _]
   (and
    (= (:ged.db.map/wfs-search-area-type db) :area-point)
    (= (:ged.db.map/tab-button db) :wfs-search))))

(rf/reg-sub
 ::wfs-search-area-box?
 (fn [db _]
   (and
    (= (:ged.db.map/wfs-search-area-type db) :area-box)
    (= (:ged.db.map/tab-button db) :wfs-search))))

(rf/reg-sub
 ::wfs-search-table-mdata
 (fn [db _]
   (:ged.db.map/wfs-search-table-mdata db)))

(rf/reg-sub
 ::all-layers-table-mdata
 (fn [db _]
   (:ged.db.map/all-layers-table-mdata db)))

(rf/reg-sub
 ::all-layers-search-input
 (fn [db _]
   (:ged.db.map/all-layers-search-input db)))


(rf/reg-sub
 ::modify-visible
 (fn [query-v _]
   [(rf/subscribe [::tab-button])])
 (fn [[tab-button] qv _]
   (= tab-button :modify)))

(rf/reg-sub
 ::modify-layer-id
 (fn [db _]
   (:ged.db.map/modify-layer-id db)))

(rf/reg-sub
 ::modify-layer-ns
 (fn [db _]
   (get-in db [:ged.db.map/infer-feature-ns-res :namespace :uri])))

(rf/reg-sub
 ::modify-wfs-click?
 (fn [db _]
   (and
    (not (:ged.db.map/modifying? db))
    (:ged.db.map/modify-layer-id db)
    (= (:ged.db.map/tab-button db) :modify))))

(rf/reg-sub
 ::modify-features
 (fn [db _]
   (let [data (:ged.db.map/modify-wfs-click-res db)
         fts (:features data)]
     (vec (take 1 fts)))))

(rf/reg-sub
 ::modifying?
 (fn [db _]
   (:ged.db.map/modifying? db)))

(rf/reg-sub
 ::wfs-search-selected-key
 (fn [db _]
   (:ged.db.map/wfs-search-selected-key db)))

(rf/reg-sub
 ::wfs-search-selected-feature
 (fn [ea _]
   [(rf/subscribe [::wfs-search-res])
    (rf/subscribe [::wfs-search-selected-key])])
 (fn [[data k] _]
   (let [xs (:features data)]
     (->> xs
          (filterv
           #(= (:id %) k))
          (first)))))

(rf/reg-sub
 ::wfs-search-fetch-ns
 (fn [db _]
   (get-in db [:ged.db.map/wfs-search-fetch-ns-res
               :namespace :uri])))

