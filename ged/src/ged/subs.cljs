(ns ged.subs
  (:require [re-frame.core :as rf])
  )

(rf/reg-sub
 ::active-panel
 (fn [db _]
   (:ged.db.core/active-panel db)))

(rf/reg-sub
 ::module-count
 (fn [db _]
   (:ged.db.core/module-count db)))

(rf/reg-sub
 ::api
 (fn [db _]
   (get-in db [:ged.db.core/api])))


#_(fn [_ _]  [(rf/subscribe [:active-attribute]) (rf/subscribe [:entities-table-state])] )
#_(fn [[active-attribute entities-table-state] _])

(rf/reg-sub
 ::base-url
 (fn [_ _] [ (rf/subscribe [::api])])
 (fn [[ api] _]
   (get-in api [:base-url])))


(rf/reg-sub
 ::proxy-path
 (fn [db _]
   (:ged.db.auth/proxy-path db)))

(rf/reg-sub
 ::proxy-geoserver-host
 (fn [db _]
   (:ged.db.auth/proxy-geoserver-host db)))

(rf/reg-sub
 ::geoserver-host
 (fn [db _]
   (:ged.db.auth/geoserver-host db)))

(rf/reg-sub
 ::username
 (fn [db _]
   (:ged.db.core/username db)))

(rf/reg-sub
 ::password
 (fn [db _]
   (:ged.db.core/password db)))