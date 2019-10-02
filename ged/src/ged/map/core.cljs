(ns ged.map.core
  (:require  [reagent.core :as r]
             [cljs.repl :as repl]
             [cljs.pprint :as pp]
             [re-frame.core :as rf]
             [ged.map.ol :as ol]))


(def ^:export astate (atom {:olmap nil
                            :select nil
                            :modify nil
                            :draw nil
                            :layer nil
                            :source nil
                            }))

(defn get-key
  [key]
  (key @astate))

(defn set-map
  [olmap]
  (swap! astate assoc :olmap olmap))

(defn merge-state
  [m]
  (swap! astate merge m))

(defn get-modify-session
  []
  (let [state @astate]
    {:select (:select state)
     :modify (:modify state)
     :layer (:layer state)
     :source (:source state)}))

(defn get-modify-features
  []
  (->
   (.getFeatures (:source @astate))
   (ol/features->geojson)
   (aget "features")))

(defn get-olmap
  []
  (get-key :olmap))