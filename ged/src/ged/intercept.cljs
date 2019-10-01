(ns ged.intercept
  (:require [re-frame.core :as rf]
            [ged.local-storage :as ls]))


(defn with-ls-interceptor
  [ky]
  (rf/after (fn [db]
              (js/console.warn "icept db " ky db))))
