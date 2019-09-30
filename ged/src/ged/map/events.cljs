(ns ged.map.events
  (:require [re-frame.core :as rf]
            [day8.re-frame.tracing :refer-macros [fn-traced defn-traced]]
            [ged.map.core :refer [get-olmap]]
            [ged.map.ol :as ol])
  )

(rf/reg-event-db
 ::inc-module-count
 (fn-traced [db [_ active-panel]]
            (let [kw :ged.core/module-count]
              (assoc db kw (inc (kw db))))
            ))


(rf/reg-event-fx
 ::refetch-wms-layers
 (fn [{:keys [db]} [_ ea]]
   (let [lrs (.getArray  (.getLayers (get-olmap)))]
     (doseq [lr  lrs]
       (when (.get lr "id")
         (do
           (.updateParams (.getSource lr) #js {:r (Math/random)}))))
     {:db db})
   #_{:db db}
   ))

(rf/reg-event-fx
 ::refetch-wms-layer
 (fn [{:keys [db]} [_ ea]]
   (let [lr (ol/id->layer (get-olmap) ea)]
     (when lr
       (do (.updateParams (.getSource lr) #js {:r (Math/random)}))))
   {:db db}
   #_{:db db}))