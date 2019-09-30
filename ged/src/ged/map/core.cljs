(ns ged.map.core
  (:require  [reagent.core :as r]
             [cljs.repl :as repl]
             [cljs.pprint :as pp]
             [re-frame.core :as rf]
             [ged.map.ol :as ol]))


(def ^:export astate (atom {:olmap nil}))

(defn get-key
  [key]
  (key @astate))

(defn set-map
  [olmap]
  (swap! astate assoc :olmap olmap))

(defn get-olmap
  []
  (get-key :olmap))