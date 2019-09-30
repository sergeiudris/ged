(ns ged.rest.core
  (:require [cljs.repl :as repl]))

(def editor-data-ref (atom nil))

(def editor-response-ref (atom nil))

(def editor-request-ref (atom nil))


(defn editor-get-val
  []
  (if @editor-data-ref (.getValue @editor-data-ref) nil))

(defn set-json!
  [editor-atom json]
  (.setValue
   (.-session @editor-atom)
   (js/JSON.stringify json nil "\t")
          ; JSON.stringify(jsonDoc, null, '\t')
   ))

(defn editor-set-json!
  [json]
  (set-json! editor-data-ref json))

(defn editor-set-str!
  [s]
  (.setValue
   (.-session @editor-data-ref)
   (or s "")))


(defn editor-response-set-json!
  [json]
  (set-json! editor-response-ref json))

(defn editor-response-set!
  [vl]
  (.setValue
   (.-session @editor-response-ref)
   vl))

(defn editor-request-set!
  [vl]
  (.setValue
   (.-session @editor-request-ref)
   vl))