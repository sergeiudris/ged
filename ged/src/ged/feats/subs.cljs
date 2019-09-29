(ns ged.feats.subs
  (:require [re-frame.core :as rf]))



(rf/reg-sub
 ::search-res
 (fn [db _]
   (:ged.feats/search-res db)))

(rf/reg-sub
 ::search-input
 (fn [db _]
   (:ged.feats/search-input db)))

(rf/reg-sub
 ::search-table-mdata
 (fn [db _]
   (:ged.feats/search-table-mdata db)))

(rf/reg-sub
 ::feature-type-input
 (fn [db _]
   (:ged.feats/feature-type-input db)))
