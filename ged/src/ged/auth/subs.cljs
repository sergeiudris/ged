(ns ged.auth.subs
  (:require [re-frame.core :as rf]))

(rf/reg-sub
 ::auth
 (fn [db _]
   (:ged.db.auth/auth db)))



(rf/reg-sub
 ::profiles
 (fn [db _]
   (:ged.db.auth/profiles db)))

(rf/reg-sub
 ::active-profile
 (fn [db _]
   (->>
    (:ged.db.auth/profiles db)
    (vals)
    (filterv (fn [pf]
               (:active? pf)))
    (first))))