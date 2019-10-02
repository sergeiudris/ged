(ns ged.map.events
  (:require [re-frame.core :as rf]
            [clojure.string :as str]
            [day8.re-frame.tracing :refer-macros [fn-traced defn-traced]]
            [ged.map.core :refer [get-olmap] :as core]
            [ged.map.ol :as ol]
            [ged.local-storage :as ls]
            [ajax.core :as ajax]
   
   )
  )

(rf/reg-event-db
 ::inc-module-count
 (fn-traced [db [_ active-panel]]
            (let [kw :ged.core/module-count]
              (assoc db kw (inc (kw db))))
            ))


(rf/reg-event-fx
 ::refetch-wms-layers
 (fn-traced
  [{:keys [db]} [_ ea]]
  (let [lrs (.getArray  (.getLayers (get-olmap)))]
    (doseq [lr  lrs]
      (when (.get lr "id")
        (.updateParams (.getSource lr) #js {:r (Math/random)})))
    {:db db})
  #_{:db db}))

(rf/reg-event-fx
 ::refetch-wms-layer
 (fn-traced [{:keys [db]} [_ ea]]
   (let [lr (ol/id->layer (get-olmap) ea)]
     (when lr
       (do (.updateParams (.getSource lr) #js {:r (Math/random)}))))
   {:db db}
   #_{:db db}))


(rf/reg-event-db
 ::tab-button
 (fn-traced [db [_ ea]]
            (let [kw   (keyword ea)
                  key :ged.map/tab-button
                  old-vl (key db)
                  vl (if (= kw old-vl) nil kw)
                  nxdb (assoc db key vl)]
              (do (ls/assoc-in-store! [key] vl))
              nxdb)))


(rf/reg-event-fx
 ::fetch-all-layers
 (fn-traced
  [{:keys [db]} [_ ea]]
  (let [proxy-path (:ged.settings/proxy-path db)]
    {:dispatch
     [:ged.events/request
      {:method :get
       :params {}
       :headers {"Content-Type" "application/json"
                            ; "Authorization"  (ged.api.geoserver/auth-creds)
                 }
       :path (str proxy-path "/rest/layers.json")
       :response-format (ajax/json-response-format {:keywords? true})
       :on-success [::fetch-all-layers-res]
       :on-fail [::fetch-all-layers-res]}]

     :db db})))

(rf/reg-event-fx
 ::fetch-all-layers-res
 (fn-traced [{:keys [db]} [_ ea]]
            {:db (assoc db :ged.map/fetch-all-layers-res ea)}))

(rf/reg-event-fx
 ::selected-layers-checked
 (fn-traced [{:keys [db]} [_ ea]]
            (let [key :ged.map/selected-layers-checked
                  nx (assoc db key ea)]
              (do (ls/assoc-in-store! [key] (key nx)))
              {:db nx})))

(rf/reg-event-fx
 ::all-layers-checked
 (fn-traced [{:keys [db]} [_ ea]]
            {:db (assoc db :ged.map/all-layers-checked ea)}))

(rf/reg-event-fx
 ::add-selected-layers-ids
 (fn-traced [{:keys [db]} [_ ea]]
            (let [key :ged.map/selected-layers-ids
                  old-vl (key db)
                  nx (assoc db key
                            (->> (concat old-vl ea)
                                 (distinct)
                                 (vec)))]
              (do (ls/assoc-in-store! [key] (key nx)))
              {:db nx})))


(rf/reg-event-fx
 ::remove-selected-layers-id
 (fn-traced [{:keys [db]} [_ ea]]
            (let [key :ged.map/selected-layers-ids
                  old-vl (key db)
                  id ea
                  nx (assoc db key
                            (filterv #(not= % id) old-vl))]
              (do (ls/assoc-in-store! [key] (key nx)))
              {:db nx})))

(rf/reg-event-db
 ::wfs-search-layer-input
 (fn-traced [db [_ ea]]
            (let [vl ea
                  key :ged.map/wfs-search-layer-input]
              (do (ls/assoc-in-store! [key] vl))
              (assoc db key vl))))

(rf/reg-event-db
 ::wfs-search-area-type
 (fn-traced [db [_ ea]]
            (let [kw (keyword ea)
                  key :ged.map/wfs-search-area-type
                  old-vl (key db)
                  vl (if (= kw old-vl) nil kw)
                  nxdb (assoc db key vl)]
              (do (ls/assoc-in-store! [key] vl))
              nxdb)))


(rf/reg-event-fx
 ::wfs-search
 (fn-traced [{:keys [db]} [_ ea]]
            (let [{:keys [filter]} ea
                  ftype-input (:ged.map/wfs-search-layer-input db)
                  last-filter (:ged.map/wfs-search-last-filter db)
                  wfs-filter (or filter last-filter)
                  [fpref ftype] (try (str/split ftype-input \:)
                                     (catch js/Error e
                                       (do (js/console.warn e)
                                           ["undefined:undefined"])))
                  table-mdata (:ged.map/wfs-search-table-mdata db)
                  total (get-in db [:ged.map/wfs-search-res :total])
                  pag (:pagination table-mdata)
                  proxy-path (:ged.settings/proxy-path db)
                  {:keys [current pageSize]} pag
                  limit (or pageSize 10)
                  offset (or (* pageSize (dec current)) 0)
                  body (ged.api.geoserver/wfs-get-features-body-str
                        (merge
                         {:offset offset
                          :limit limit
                          :featurePrefix fpref
                          :featureTypes [ftype]}
                         (when wfs-filter
                           {:filter wfs-filter})))]
              #_(do (editor-request-set! (prettify-xml body)))
              {:dispatch [:ged.events/request
                          {:method :post
                           :params {}
                           :body body
                           :headers {"Content-Type" "application/json"
                            ; "Authorization"  (ged.api.geoserver/auth-creds)
                                     }
                           :path (str proxy-path "/wfs")
                           :response-format (ajax/json-response-format {:keywords? true})
                           :on-success [::wfs-search-res]
                           :on-fail [::wfs-search-res]}]
               :db (merge db {:ged.map/wfs-search-last-filter wfs-filter
                              :ged.map/search-table-mdata
                              (merge table-mdata {:pagination (merge pag {:current 1})})})})))

(rf/reg-event-db
 ::wfs-search-res
 (fn-traced [db [_ ea]]
            (assoc db :ged.map/wfs-search-res ea)))

(rf/reg-event-fx
 ::wfs-search-table-mdata
 (fn-traced [{:keys [db]} [_ ea]]
            (let [key :ged.map/wfs-search-table-mdata]
              {:dispatch [:ged.map.events/wfs-search {}]
               :db (assoc db key ea)})))

(rf/reg-event-fx
 ::modify-layer
 (fn-traced [{:keys [db]} [_ ea]]
            (let [key :ged.map/modify-layer-id]
              {:dispatch [:ged.map.events/tab-button :modify ]
               :db (merge db
                          {key ea})})))

(rf/reg-event-fx
 ::modify-wfs-click
 (fn-traced [{:keys [db]} [_ ea]]
            (let [{:keys [filter]} ea
                  ftype-input (:ged.map/modify-layer-id db)
                  last-filter (:ged.map/modify-wfs-click-last-filter db)
                  wfs-filter (or filter last-filter)
                  [fpref ftype] (try (str/split ftype-input \:)
                                     (catch js/Error e
                                       (do (js/console.warn e)
                                           ["undefined:undefined"])))
                  proxy-path (:ged.settings/proxy-path db)
                  body (ged.api.geoserver/wfs-get-features-body-str
                        (merge
                         {:offset 0
                          :limit 100
                          :featurePrefix fpref
                          :featureTypes [ftype]}
                         (when wfs-filter
                           {:filter wfs-filter})))]
              #_(do (editor-request-set! (prettify-xml body)))
              {:dispatch [:ged.events/request
                          {:method :post
                           :params {}
                           :body body
                           :headers {"Content-Type" "application/json"
                            ; "Authorization"  (ged.api.geoserver/auth-creds)
                                     }
                           :path (str proxy-path "/wfs")
                           :response-format (ajax/json-response-format {:keywords? true})
                           :on-success [::modify-wfs-click-res]
                           :on-fail [::modify-wfs-click-res]}]
               :db (merge db {:ged.map/modify-wfs-click-last-filter wfs-filter})})))

(rf/reg-event-fx
 ::modify-wfs-click-res
 (fn-traced [{:keys [db]} [_ ea]]
            (let [fts (:features ea)]
              (merge
               {:db (assoc db :ged.map/modify-wfs-click-res ea)}
               (when-not (empty? fts)
                 {:dispatch [::modifying? true]})))))

(rf/reg-event-db
 ::modifying?
 (fn-traced [db [_ ea]]
            (assoc db :ged.map/modifying? ea)))

(rf/reg-event-fx
 ::tx-features
 (fn-traced [{:keys [db]} [_ ea]]
            (let [ftype-input (:ged.map/modify-layer-id db)
                  [fpref ftype] (try (str/split ftype-input \:)
                                     (catch js/Error e
                                       (do (js/console.warn e)
                                           ["undefined:undefined"])))
                  fns (:ged.map/modify-layer-ns db)
                  {:keys [updates]} ea
                  proxy-path (:ged.settings/proxy-path db)
                  updates (core/get-modify-features)
                  body (ged.api.geoserver/wfs-tx-jsons-str
                        {:deletes nil
                         :inserts nil
                         :updates updates
                         :featureNS fns
                         :featurePrefix fpref
                         :featureType ftype})]
              (js/console.log "updates" updates)
              {:dispatch [:ged.events/request
                          {:method :post
                           :body body
                           :headers {"Content-Type" "application/json"}
                           :path (str proxy-path "/wfs")
                           :response-format
                           (ajax/raw-response-format)
                  ; (ajax/json-response-format {:keywords? true})
                           :on-success [::tx-res-succ (str fpref ":" ftype)]
                           :on-fail [::tx-res-fail]}]
               :db (merge db {})})))

(rf/reg-event-fx
 ::tx-res-succ
 (fn-traced [{:keys [db]} [_ id ea]]
            {:dispatch [:ged.map.events/refetch-wms-layer id]
             :db (merge db
                        {:ged.map/tx-res ea
                         :ged.map/modifying? false}) }))

(rf/reg-event-db
 ::tx-res-fail
 (fn-traced [db [_ ea]]
            (assoc db :ged.map/tx-res ea)))