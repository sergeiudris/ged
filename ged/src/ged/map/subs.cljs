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