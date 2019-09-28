(ns ged.feats.subs
  (:require [re-frame.core :as rf]))



(rf/reg-sub
 ::search-res
 (fn [db _]
   (:ui.count/search-res db)))

(rf/reg-sub
 ::search-input
 (fn [db _]
   (:ui.count/search-input db)))

(rf/reg-sub
 ::search-table-mdata
 (fn [db _]
   (:ui.count/search-table-mdata db)))
