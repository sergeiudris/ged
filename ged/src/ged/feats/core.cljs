(ns ged.feats.core
  (:require [cljs.repl :as repl])
  )

(def editor-feature-ref (atom nil))

(defn editor-get-val
  []
  (if @editor-feature-ref (.getValue @editor-feature-ref) nil))

(defn editor-set-json!
  [json]
  (.setValue
   (.-session @editor-feature-ref)
   (js/JSON.stringify json nil "\t")
          ; JSON.stringify(jsonDoc, null, '\t')
   ))

(defn editor-set-str!
  [s]
  (.setValue
   (.-session @editor-feature-ref)
   (or s "")))