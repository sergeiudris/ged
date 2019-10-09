(ns ged.log.subs
  (:require [re-frame.core :as rf]))

(rf/reg-sub
 ::log
 (fn [db _]
   (let [log-que (:ged.db.core/log-que db)]
     {:data log-que
      :total (count log-que)})))

(rf/reg-sub
 ::log-table-mdata
 (fn [db _]
   (:ged.db.log/log-table-mdata db)))

