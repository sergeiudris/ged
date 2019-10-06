(ns ged.feats.subs
  (:require [re-frame.core :as rf]))



(rf/reg-sub
 ::search-res
 (fn [db _]
   (:ged.db.feats/search-res db)))

(rf/reg-sub
 ::search-input
 (fn [db _]
   (:ged.db.feats/search-input db)))

(rf/reg-sub
 ::search-table-mdata
 (fn [db _]
   (:ged.db.feats/search-table-mdata db)))

(rf/reg-sub
 ::feature-type-input
 (fn [db _]
   (:ged.db.feats/feature-type-input db)))

(rf/reg-sub
 ::feature-ns
 (fn [db _]
   (:ged.db.feats/feature-ns db)))
