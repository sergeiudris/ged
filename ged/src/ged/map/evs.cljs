(ns ged.map.evs
  (:require [re-frame.core :as rf]
            [clojure.string :as str]
            [day8.re-frame.tracing :refer-macros [fn-traced defn-traced]]
            [ajax.core :as ajax]
            [ged.wfs :refer [wfs-tx-jsons-str wfs-get-features-body-str]]
            [cognitect.transit :as t]))

(rf/reg-event-fx
 ::tab-button
 (fn-traced [{:keys [db]} [_ ea]]
            (let [kw   (keyword ea)
                  key :ged.db.map/tab-button
                  v-old (key db)
                  v (if (= kw v-old) nil kw)
                  nxdb (assoc db key v)]
              {:db nxdb
               :dispatch [:assoc-in-store [[key] v]]})))

(rf/reg-event-fx
 ::fetch-all-layers
 (fn-traced
  [{:keys [db]} [_ ea]]
  (let []
    {:dispatch
     [:ged.evs/request
      {:method :get
       :params {}
       :headers {"Content-Type" "application/json"}
       :path "/geoserver/rest/layers.json"
       :response-format (ajax/json-response-format {:keywords? true})
       :on-success [::fetch-all-layers-res]
       :on-failure [::fetch-all-layers-res]}]
     :db db})))

(rf/reg-event-fx
 ::fetch-all-layers-res
 (fn-traced [{:keys [db]} [_ ea]]
            {:db (assoc db :ged.db.map/fetch-all-layers-res ea)}))

(rf/reg-event-fx
 ::all-layers-search-input
 (fn-traced [{:keys [db]} [_ ea]]
            {:db (->
                  db
                  (update-in [:ged.db.map/all-layers-table-mdata :pagination] assoc :current 1)
                  (assoc :ged.db.map/all-layers-search-input ea)) 
             }))

(rf/reg-event-fx
 ::selected-layers-checked
 (fn-traced [{:keys [db]} [_ ea]]
            (let [v (js->clj ea)
                  key :ged.db.map/selected-layers-checked
                  nx (assoc db key v)]
              {:db nx
               :dispatch [:assoc-in-store [[key] (key nx)]]})))

(rf/reg-event-fx
 ::selected-layers-checked-remove-ids
 (fn-traced [{:keys [db]} [_ ea]]
            (let [ids ea
                  k :ged.db.map/selected-layers-checked
                  v (k db)
                  nv (filterv (fn [id] (not (some #(= % id) ids)))  v)
                  nx (assoc db k nv)]
              {:db nx
               :dispatch [:assoc-in-store [[k] nv]]})))

(rf/reg-event-fx
 ::all-layers-checked
 (fn-traced [{:keys [db]} [_ ea]]
            (let [v (js->clj ea)]
              {:db (assoc db :ged.db.map/all-layers-checked v)})
            ))

(rf/reg-event-fx
 ::add-selected-layers-ids
 (fn-traced [{:keys [db]} [_ ea]]
            (let [key :ged.db.map/selected-layers-ids
                  v-old (key db)
                  nx (assoc db key
                            (->> (concat v-old ea)
                                 (distinct)
                                 (vec)))]
              {:db nx
               :dispatch [:assoc-in-store [[key] (key nx)]]})))


(rf/reg-event-fx
 ::remove-selected-layers-id
 (fn-traced [{:keys [db]} [_ ea]]
            (let [k :ged.db.map/selected-layers-ids
                  v-old (k db)
                  id ea
                  nx (assoc db k
                            (filterv #(not= % id) v-old))]
              {:db nx
               :dispatch-n (list
                            [:assoc-in-store [[k] (k nx)]]
                            [::selected-layers-checked-remove-ids [id]])})))

(rf/reg-event-fx
 ::wfs-search-layer-input
 (fn-traced [{:keys [db]} [_ ea]]
            (let [v ea
                  k :ged.db.map/wfs-search-layer-input]
              {:db (assoc db k v)
               :dispatch [:assoc-in-store [[k] v]]})))

(rf/reg-event-fx
 ::wfs-search-area-type
 (fn-traced [{:keys [db]} [_ ea]]
            (let [kw (keyword ea)
                  k :ged.db.map/wfs-search-area-type
                  v-old (k db)
                  v (if (= kw v-old) nil kw)
                  nxdb (assoc db k v)]
              {:db nxdb
               :dispatch [:assoc-in-store [[k] v]]})))

(rf/reg-event-fx
 ::wfs-search
 (fn-traced [{:keys [db]} [_ ea]]
            (let [{:keys [filter]} ea
                  ftype-input (:ged.db.map/wfs-search-layer-input db)
                  last-filter (:ged.db.map/wfs-search-last-filter db)
                  wfs-filter (or filter last-filter)
                  [fpref ftype] (try (str/split ftype-input \:)
                                     (catch js/Error e
                                       (do (js/console.warn e)
                                           ["undefined:undefined"])))
                  table-mdata (:ged.db.map/wfs-search-table-mdata db)
                  total (get-in db [:ged.db.map/wfs-search-res :total])
                  pag (:pagination table-mdata)
                  {:keys [current pageSize]} pag
                  limit (or pageSize 10)
                  offset (or (* pageSize (dec current)) 0)
                  body (wfs-get-features-body-str
                        (merge
                         {:offset offset
                          :limit limit
                          :featurePrefix fpref
                          :featureTypes [ftype]}
                         (when wfs-filter
                           {:filter wfs-filter})))]
              #_(do (editor-request-set! (prettify-xml body)))
              {:dispatch
               [:ged.req/request {:profiles [:wfs-get-feature]
                       :params {}
                       :body body
                       :headers {"Content-Type" "application/json"}
                       :on-success [::wfs-search-res]
                       :on-failure [::wfs-search-res]}]
               #_[:ged.evs/request
                  {:method :post
                   :params {}
                   :body body
                   :headers {"Content-Type" "application/json"}
                   :path (str proxy-path "/wfs")
                   :response-format
                   (ajax/json-response-format {:keywords? true})
                   #_(ajax/transit-response-format {:reader (t/reader :json)})
                   :on-success [::wfs-search-res]
                   :on-failure [::wfs-search-res]}]
               :db (merge db {:ged.db.map/wfs-search-last-filter wfs-filter
                              :ged.db.map/search-table-mdata
                              (merge table-mdata {:pagination (merge pag {:current 1})})})})))

(rf/reg-event-db
 ::wfs-search-res
 (fn-traced [db [_ ea]]
            (assoc db :ged.db.map/wfs-search-res ea)))

(rf/reg-event-fx
 ::wfs-search-table-mdata
 (fn-traced [{:keys [db]} [_ ea]]
            (let [key :ged.db.map/wfs-search-table-mdata]
              {:dispatch [:ged.map.events/wfs-search {}]
               :db (assoc db key ea)})))

(rf/reg-event-fx
 ::all-layers-table-mdata
 (fn-traced [{:keys [db]} [_ ea]]
            (let [key :ged.db.map/all-layers-table-mdata]
              {:db (assoc db key ea)})))

(rf/reg-event-fx
 ::modify-layer
 (fn-traced [{:keys [db]} [_ ea]]
            (let [key :ged.db.map/modify-layer-id]
              {:dispatch [:ged.map.events/tab-button :modify ]
               :db (merge db
                          {key ea})})))

(rf/reg-event-fx
 ::modify-wfs-click
 (fn-traced [{:keys [db]} [_ ea]]
            (let [{:keys [filter]} ea
                  ftype-input (:ged.db.map/modify-layer-id db)
                  last-filter (:ged.db.map/modify-wfs-click-last-filter db)
                  wfs-filter (or filter last-filter)
                  [fpref ftype] (try (str/split ftype-input \:)
                                     (catch js/Error e
                                       (do (js/console.warn e)
                                           ["undefined:undefined"])))
                  body (wfs-get-features-body-str
                        (merge
                         {:offset 0
                          :limit 100
                          :featurePrefix fpref
                          :featureTypes [ftype]}
                         (when wfs-filter
                           {:filter wfs-filter})))]
              #_(do (editor-request-set! (prettify-xml body)))
              {:dispatch [:ged.evs/request
                          {:method :post
                           :params {}
                           :body body
                           :headers {"Content-Type" "application/json"}
                           :path "/geoserver/wfs"
                           :response-format (ajax/json-response-format {:keywords? true})
                           :on-success [::modify-wfs-click-res]
                           :on-failure [::modify-wfs-click-res]}]
               :db (merge db {:ged.db.map/modify-wfs-click-last-filter wfs-filter})})))

(rf/reg-event-fx
 ::modify-wfs-click-res
 (fn-traced [{:keys [db]} [_ ea]]
            (let [fts (:features ea)]
              (merge
               {:db (assoc db :ged.db.map/modify-wfs-click-res ea)}
               (when-not (empty? fts)
                 {:dispatch [::modifying? true]})))))

(rf/reg-event-db
 ::modifying?
 (fn-traced [db [_ ea]]
            (assoc db :ged.db.map/modifying? ea)))

(rf/reg-event-fx
 ::tx-features
 [(rf/inject-cofx :ged.map.core/modify-features)]
 (fn-traced [{:keys [db modify-features]} [_ ea]]
            (let [ftype-input (:ged.db.map/modify-layer-id db)
                  [fpref ftype] (try (str/split ftype-input \:)
                                     (catch js/Error e
                                       (do (js/console.warn e)
                                           ["undefined:undefined"])))
                  fns (:ged.db.map/modify-layer-ns db)
                  {:keys [updates]} ea
                  updates modify-features
                  body (wfs-tx-jsons-str
                        {:deletes nil
                         :inserts nil
                         :updates updates
                         :featureNS fns
                         :featurePrefix fpref
                         :featureType ftype})]
              {:dispatch [:ged.evs/request
                          {:method :post
                           :body body
                           :headers {"Content-Type" "application/json"}
                           :path "/geoserver/wfs"
                           :response-format
                           (ajax/raw-response-format)
                  ; (ajax/json-response-format {:keywords? true})
                           :on-success [::tx-res-succ (str fpref ":" ftype)]
                           :on-failure [::tx-res-fail]}]
               :db (merge db {})})))

(rf/reg-event-fx
 ::tx-res-succ
 (fn-traced [{:keys [db]} [_ id ea]]
            {:dispatch [:ged.map.core/refetch-wms-layer id]
             :db (merge db
                        {:ged.db.map/tx-res ea
                         :ged.db.map/modifying? false})}))

(rf/reg-event-db
 ::tx-res-fail
 (fn-traced [db [_ ea]]
            (assoc db :ged.db.map/tx-res ea)))