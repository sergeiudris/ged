(ns ged.ftype.events
  (:require [re-frame.core :as rf]
            [day8.re-frame.tracing :refer-macros [fn-traced defn-traced]]
            [goog.dom]
            [ged.ftype.core]))


(rf/reg-event-db
 ::set
 (fn-traced [db [_ key vl]]
            (let []
              (assoc db key vl))))