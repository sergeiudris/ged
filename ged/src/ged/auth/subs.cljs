(ns ged.auth.subs
  (:require [re-frame.core :as rf]))

(rf/reg-sub
 ::auth
 (fn [db _]
   (:ged.auth/auth db)))



