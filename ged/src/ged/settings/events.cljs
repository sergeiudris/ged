(ns ged.settings.events
  (:require [re-frame.core :as rf]
            [day8.re-frame.tracing :refer-macros [fn-traced defn-traced]]
            [goog.dom]
            [ged.settings.core]
            [ged.local-storage :as ls]))


(rf/reg-event-fx
 ::set
 (fn-traced [{:keys [db]} [_ key v]]
            (let []
              (do
                (ls/assoc-in-store! [key] v))
              {:db (assoc db key v)})))