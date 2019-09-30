(ns ged.ftype.subs
  (:require [re-frame.core :as rf]))

(rf/reg-sub
 ::ftype
 (fn [db _]
   (:ged.ftype/ftype db)))



