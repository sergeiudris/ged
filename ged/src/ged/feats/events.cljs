(ns ged.feats.events
  (:require [re-frame.core :as rf]
            [day8.re-frame.tracing :refer-macros [fn-traced defn-traced]]
            [ged.api.geoserver]
            [ajax.core :as ajax]))

(rf/reg-event-fx
 ::search
 (fn [{:keys [db]} [_ eargs]]
   (let [input (:ged.feats/search-input db)
         s (or (:input eargs) input)
         table-mdata (:ged.feats/search-table-mdata db)
         total (get-in db [:ged.feats/search-res :total])
         pag (:pagination table-mdata)
         {:keys [current pageSize]} pag
         limit (or pageSize 10)
         offset (or (* pageSize (dec current)) 0)]
     {:dispatch [:ged.events/request
                 {:method :post
                  :params {}
                  :body (ged.api.geoserver/wfs-get-features-body-str
                         {:offset offset
                          :limit limit
                          :featurePrefix "dev"
                          :featureTypes ["usa_major_cities"]})
                  :headers {"Content-Type" "application/json"
                            "Authorization"  (ged.api.geoserver/auth-creds)}
                  :path "/geoserver/wfs"
                  :response-format (ajax/json-response-format {:keywords? true})
                  :on-success [::search-res]
                  :on-fail [::search-res]}]
      :db (merge db {:ged.feats/search-input s
                     :ged.feats/search-table-mdata
                     (if (:input eargs)
                       (merge table-mdata {:pagination (merge pag {:current 1})})
                       table-mdata)})})))

(rf/reg-event-db
 ::search-res
 (fn-traced [db [_ val]]
            (assoc db :ged.feats/search-res val)))

(rf/reg-event-db
 ::search-input
 (fn-traced [db [_ eargs]]
            (let [key :ged.feats/search-input
                  value eargs]
              (assoc db key value))))

(rf/reg-event-fx
 ::search-table-mdata
 (fn [{:keys [db]} [_ eargs]]
   (let [key :ged.feats/search-table-mdata]
     {:dispatch [:ged.feats.events/search {}]
      :db (assoc db key eargs)})))