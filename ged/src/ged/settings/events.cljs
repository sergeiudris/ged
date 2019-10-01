(ns ged.settings.events
  (:require [re-frame.core :as rf]
            [day8.re-frame.tracing :refer-macros [fn-traced defn-traced]]
            [goog.dom]
            [ged.settings.core]
            [ged.local-storage :as ls]))


(rf/reg-event-fx
 ::set
 (fn-traced [{:keys [db]} [_ key vl]]
            (let []
              (do
                (ls/assoc-in-store! [key] vl))
              {:db (assoc db key vl)})))