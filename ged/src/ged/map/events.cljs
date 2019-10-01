(ns ged.map.events
  (:require [re-frame.core :as rf]
            [day8.re-frame.tracing :refer-macros [fn-traced defn-traced]]
            [ged.map.core :refer [get-olmap]]
            [ged.map.ol :as ol]
            [ged.local-storage :as ls]
            [ajax.core :as ajax])
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
              (do (ls/assoc-in-store! key (key nx)))
              {:db nx})))

