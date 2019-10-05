(ns ged.http
  (:require [clojure.string :as str]
            [re-frame.core :as rf]
            [day8.re-frame.tracing :refer-macros [fn-traced defn-traced]]))

; https://developer.mozilla.org/en-US/docs/Web/API/AbortController
; https://developer.mozilla.org/en-US/docs/Web/API/Fetch_API

(rf/reg-event-fx
 :http
 (fn-traced
  [{:keys [db]} [_ ea]]
  (let []
    (js/console.log ea)
    (do
      (->
       (js/fetch (:url ea)
                 (:init ea))
       (.catch (fn [e]
                 (rf/dispatch (:on-failure ea))))
       (.then (fn [r]
                (rf/dispatch (:on-success ea)))))
      )
    {})))
