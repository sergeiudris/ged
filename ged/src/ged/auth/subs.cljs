(ns ged.auth.subs
  (:require [re-frame.core :as rf]))

(rf/reg-sub
 ::auth
 (fn [db _]
   (:ged.db.auth/auth db)))



(rf/reg-sub
 ::profiles-table-mdata
 (fn [db _]
   (:ged.db.auth/profiles-table-mdata db)))