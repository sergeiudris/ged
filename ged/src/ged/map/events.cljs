(ns ged.map.events
  (:require [re-frame.core :as rf]
            [day8.re-frame.tracing :refer-macros [fn-traced defn-traced]]
            [ged.map.core]
            [ged.map.ol])
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
   (let []
     (js/console.log ea)
     {:db db})))