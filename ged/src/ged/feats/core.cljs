(ns ged.feats.core
  (:require [cljs.repl :as repl])
  )

(def editor-feature-ref (atom nil))

(def editor-response-ref (atom nil))


(defn editor-get-val
  []
  (if @editor-feature-ref (.getValue @editor-feature-ref) nil))

(defn set-json!
  [editor-atom json]
  (.setValue
   (.-session @editor-atom)
   (js/JSON.stringify json nil "\t")
          ; JSON.stringify(jsonDoc, null, '\t')
   ))

(defn editor-set-json!
  [json]
  (set-json! editor-feature-ref json))

(defn editor-set-str!
  [s]
  (.setValue
   (.-session @editor-feature-ref)
   (or s "")))


(defn editor-response-set-json!
  [json]
  (set-json! editor-response-ref json))