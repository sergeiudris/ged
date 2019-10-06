(ns ged.auth.evs
  (:require [re-frame.core :as rf]
            [day8.re-frame.tracing :refer-macros [fn-traced defn-traced]]
            [goog.dom]
            [ged.core :refer  [deep-merge]]))



(rf/reg-event-fx
 ::add-profile
 (fn-traced
  [{:keys [db]} [_ ea]]
  (let [pfs (:ged.db.auth/profiles db)
        k (->> pfs keys (apply max) inc)
        pf {:key k
            :host "http://geoserver:8080/geoserver"
            :proxy-host "http://localhost:8600/geoserver"
            :user "admin"
            :pass "geoserver"}]
    {:db (update-in db [:ged.db.auth/profiles] assoc k pf)})))

(rf/reg-event-fx
 ::remove-profile
 (fn-traced
  [{:keys [db]} [_ ea]]
  (let [k (aget ea "key") ]
    {:db (update-in db [:ged.db.auth/profiles] dissoc k)})))

(rf/reg-event-fx
 ::update-profile
 (fn-traced
  [{:keys [db]} [_ ea]]
  (let [k (:key ea)]
    {:db (update-in db [:ged.db.auth/profiles k] merge ea)})))

(rf/reg-event-fx
 ::activate-profile
 (fn-traced
  [{:keys [db]} [_ ea]]
  (js/console.log ea)
  (let []
    {:db db})))

(rf/reg-event-fx
 ::update-profiles
 (fn-traced
  [{:keys [db]} [_ ea]]
  (let []
    {:db (update-in db [:ged.db.auth/profiles] deep-merge ea)})))

