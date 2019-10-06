(ns ged.feats.evs
  (:require [re-frame.core :as rf]
            [clojure.repl :as repl]
            [day8.re-frame.tracing :refer-macros [fn-traced defn-traced]]
            [ged.wfs :refer [wfs-tx-jsons-str wfs-get-features-body-str]]
            [ajax.core :as ajax]
            [clojure.string :as str]
            ["ol/format/filter" :as olf]))

#_(repl/dir xml)

#_(xml/indent)

(rf/reg-event-fx
 ::search
 (fn-traced [{:keys [db]} [_ ea]]
   (let [ftype-input (:ged.db.feats/feature-type-input db)
         [fpref ftype] (try (str/split ftype-input \:)
                            (catch js/Error e
                              (do (js/console.warn e)
                                  ["undefined:undefined"])))
         input (:ged.db.feats/search-input db)
         s (or (:input ea) input)
         table-mdata (:ged.db.feats/search-table-mdata db)
         total (get-in db [:ged.db.feats/search-res :total])
         pag (:pagination table-mdata)
         proxy-path (:ged.db.auth/proxy-path db)
         {:keys [current pageSize]} pag
         limit (or pageSize 10)
         offset (or (* pageSize (dec current)) 0)
         body (wfs-get-features-body-str
               (merge
                {:offset offset
                 :limit limit
                 :featurePrefix fpref
                 :featureTypes [ftype]}
                (when (not-empty s)
                  {:filter (olf/like "NAME" (str "*" s "*") "*" "." "!" false)})))]
     {:dispatch-n (list
                 [:ged.evs/request
                  {:method :post
                   :params {}
                   :body body
                   :headers {"Content-Type" "application/json"}
                   :path (str proxy-path "/wfs")
                   :response-format (ajax/json-response-format {:keywords? true})
                   :on-success [::search-res]
                   :on-failure [::search-res]}]
                 [:ged.feats.core/set-editor-xml [:request body]])
      :db (merge db {:ged.db.feats/search-input s
                     :ged.db.feats/search-table-mdata
                     (if (:input ea)
                       (merge table-mdata {:pagination (merge pag {:current 1})})
                       table-mdata)})})))

(rf/reg-event-db
 ::search-res
 (fn-traced [db [_ ea]]
            (assoc db :ged.db.feats/search-res ea)))

(rf/reg-event-fx
 ::tx-feature
 [(rf/inject-cofx :ged.feats.core/get-editor-val [:data])]
 (fn-traced [{:keys [db get-editor-val]} [_ ea]]
            (let [ftype-input (:ged.db.feats/feature-type-input db)
                  [fpref ftype] (try (str/split ftype-input \:)
                                     (catch js/Error e
                                       (do (js/console.warn e)
                                           ["undefined:undefined"])))
                  fns (:ged.db.feats/feature-ns db)
                  tx-type (:tx-type ea)
                  proxy-path (:ged.db.auth/proxy-path db)
                  v (js/JSON.parse get-editor-val)
                  body (wfs-tx-jsons-str
                        {tx-type [v]
                         :featureNS fns
                         :featurePrefix fpref
                         :featureType ftype})]
              {:dispatch-n (list
                            [:ged.evs/request
                             {:method :post
                              :body body
                              :headers {"Content-Type" "application/json"}
                              :path (str proxy-path "/wfs")
                              :response-format
                              (ajax/raw-response-format) #_(ajax/json-response-format {:keywords? true})
                              :on-success [::tx-res-succ (str fpref ":" ftype)]
                              :on-failure [::tx-res-fail]}]
                            [:ged.feats.core/set-editor-xml [:request body]])
               :db (merge db {})})))

(rf/reg-event-fx
 ::tx-res-succ
 (fn-traced [{:keys [db]} [_ id ea]]
            {:db (assoc db :ged.db.feats/tx-res ea)
             :dispatch-n (list
                          [:ged.feats.core/set-editor-xml [:response ea]]
                          [:ged.map.core/refetch-wms-layer id])}))


(rf/reg-event-fx
 ::tx-res-fail
 (fn-traced [{:keys [db]} [_ ea]]
            {:db (assoc db :ged.db.feats/tx-res ea)
             :dispatch [:ged.feats.core/set-editor-xml [:response ea]]}))

(rf/reg-event-fx
 ::search-input
 (fn-traced [{:keys [db]} [_ ea]]
            (let [k :ged.db.feats/search-input
                  v ea]
              {:db (assoc db k v)
               })))

(rf/reg-event-fx
 ::search-table-mdata
 (fn-traced [{:keys [db]} [_ ea]]
   (let [key :ged.db.feats/search-table-mdata]
     {:dispatch [:ged.feats.events/search {}]
      :db (assoc db key ea)})))

(rf/reg-event-fx
 ::select-feature
 (fn-traced [{:keys [db]} [_ ea]]
            (let [key :ged.db.feats/select-feature]
              {:db (assoc db key ea)
               :dispatch [:ged.feats.core/set-editor-json [:data ea]]})))

(rf/reg-event-fx
 ::feature-type-input
 (fn-traced [{:keys [db]} [_ ea]]
   (let [v ea
         k :ged.db.feats/feature-type-input]
     {:db (assoc db k v)
      :dispatch [:assoc-in-store [[k] v]]})))

(rf/reg-event-fx
 ::feature-ns
 (fn-traced [{:keys [db]} [_ ea]]
            (let [key :ged.db.feats/feature-ns]
              {:db (assoc db key ea)})))

