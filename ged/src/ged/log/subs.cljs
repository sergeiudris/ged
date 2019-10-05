(ns ged.log.subs
  (:require [re-frame.core :as rf]))

(rf/reg-sub
 ::log
 (fn [db _]
   (:ged.log/log db)))



