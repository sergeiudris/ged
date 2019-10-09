(ns ged.log.evs
  (:require [re-frame.core :as rf]
            [day8.re-frame.tracing :refer-macros [fn-traced defn-traced]]
            [goog.dom]
            [ged.log.core]))


(rf/reg-event-fx
 ::set
 (fn-traced [{:keys [db]} [_ key v]]
            (let []
              {:db (assoc db key v)
               :dispatch [:assoc-in-store [[key] v]]})))

(rf/reg-event-fx
 ::log-table-mdata
 (fn-traced [{:keys [db]} [_ ea]]
            (let [key :ged.db.log/log-table-mdata]
              {:db (assoc db key ea)})))