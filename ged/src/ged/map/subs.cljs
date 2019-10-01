(ns ged.map.subs
  (:require [re-frame.core :as rf]))

(rf/reg-sub
 ::module-count
 (fn [db _]
   (:ged.core/module-count db)))


(rf/reg-sub
 ::checked-layer-ids
 (fn [db _]
   (:ged.map/checked-layer-ids db)))

(rf/reg-sub
 ::tab-button
 (fn [db _]
   (:ged.map/tab-button db)))

(rf/reg-sub
 ::all-layers-visible
 (fn [query-v _]
   [(rf/subscribe [::tab-button])])
 (fn [[tab-button] qv _]
   (= tab-button :all-layers)))

(rf/reg-sub
 ::selected-layers-visible
 (fn [query-v _]
   [(rf/subscribe [::tab-button])])
 (fn [[tab-button] qv _]
   (= tab-button :selected-layers)))

(rf/reg-sub
 ::wfs-search-visible
 (fn [query-v _]
   [(rf/subscribe [::tab-button])])
 (fn [[tab-button] qv _]
   (= tab-button :wfs-search)))