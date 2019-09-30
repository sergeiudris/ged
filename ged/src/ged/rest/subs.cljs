(ns ged.rest.subs
  (:require [re-frame.core :as rf]))



(rf/reg-sub
 ::search-res
 (fn [db _]
   (:ged.rest/search-res db)))

(rf/reg-sub
 ::search-input
 (fn [db _]
   (:ged.rest/search-input db)))

(rf/reg-sub
 ::search-table-mdata
 (fn [db _]
   (:ged.rest/search-table-mdata db)))



