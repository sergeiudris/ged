(ns ged.log.subs
  (:require [re-frame.core :as rf]))

(rf/reg-sub
 ::log
 (fn [db _]
   (let [data (:ged.db.core/log-que db)
         pag (get-in db [:ged.db.log/log-table-mdata :pagination])
         {:keys [current pageSize]} pag]
     {:total (count data)
      :data (->> data
                 (reverse)
                 (drop (* (dec current) pageSize))
                 (take pageSize)
                 (vec))})))

(rf/reg-sub
 ::log-table-mdata
 (fn [db _]
   (:ged.db.log/log-table-mdata db)))
