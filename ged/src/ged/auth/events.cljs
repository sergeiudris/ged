(ns ged.auth.events
  (:require [re-frame.core :as rf]
            [day8.re-frame.tracing :refer-macros [fn-traced defn-traced]]
            [goog.dom]))


(rf/reg-event-db
 ::set
 (fn-traced [db [_ ea]]
            (let []
              db)))

(rf/reg-event-fx
 ::login
 (fn [{:keys [db]} [_ ea]]
   (let [username (:username ea)
         password (:password ea)]
     {:db
      (merge db {:ged.core/username username
                 :ged.core/password password})})))

