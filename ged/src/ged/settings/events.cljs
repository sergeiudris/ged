(ns ged.settings.events
  (:require [re-frame.core :as rf]
            [ged.settings.spec :as sp]
            [clojure.spec.alpha :as s]
            [day8.re-frame.tracing :refer-macros [fn-traced defn-traced]]
            [goog.dom]
            [ged.settings.core]))


(rf/reg-event-db
 ::set
 (fn-traced [db [_ key vl]]
            (let []
              (assoc db key vl))))