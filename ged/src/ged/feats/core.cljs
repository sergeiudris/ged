(ns ged.feats.core
  (:require [cljs.repl :as repl]
            [re-frame.core :as rf]
            [day8.re-frame.tracing :refer-macros [fn-traced defn-traced]])
  )

(def prettify-xml
  (fn [sourceXml]
    (let
     [xmlDoc (.parseFromString (new js/DOMParser) sourceXml "application/xml")
      xsltDoc
      (.parseFromString
       (new js/DOMParser)
       (.join
        #js
         ["<xsl:stylesheet xmlns:xsl=\"http://www.w3.org/1999/XSL/Transform\">"
          "  <xsl:strip-space elements=\"*\"/>"
          "  <xsl:template match=\"para[content-style][not(text())]\">"
          "    <xsl:value-of select=\"normalize-space(.)\"/>"
          "  </xsl:template>" "  <xsl:template match=\"node()|@*\">"
          "    <xsl:copy><xsl:apply-templates select=\"node()|@*\"/></xsl:copy>"
          "  </xsl:template>" "  <xsl:output indent=\"yes\"/>"
          "</xsl:stylesheet>"]
        "\n")
       "application/xml")
      xsltProcessor (new js/XSLTProcessor)]
      (.importStylesheet xsltProcessor xsltDoc)
      (def resultDoc (.transformToDocument xsltProcessor xmlDoc))
      (def resultXml (.serializeToString (new js/XMLSerializer) resultDoc))
      resultXml)))

(def editors (atom {:data nil
                    :response nil
                    :request nil}))

(defn get-editor
  [k]
  (k @editors))

(defn get-editor-val
  [k]
  (when (get-editor k)
    (.getValue (get-editor k))))

(defn set-editor!
  [k ref]
  (swap! editors assoc k ref))

(defn set-editor-val!
  [k v]
  (.setValue
   (.-session (get-editor k))
   v))

(defn set-editor-str!
  [k v]
  (set-editor-val! k (or v "")))

(defn set-editor-json!
  [k v]
  (set-editor-val! k (js/JSON.stringify v nil "\t")))

(defn set-editor-xml!
  [k v]
  (set-editor-val! k (prettify-xml v)))

(defn set-editor-preserve!
  [k ref]
  (let [v (get-editor-val k)]
    (swap! editors assoc k ref)
    (when v
      (set-editor-val! k v))))

; re-frame events

(rf/reg-event-fx
 ::set-editor-val
 (fn-traced [{:keys [db]} [_ ea]]
            (let [[k v] ea]
              (do (set-editor-val! k v)))
            {}))

(rf/reg-event-fx
 ::set-editor-json
 (fn-traced [{:keys [db]} [_ ea]]
            (let [[k v] ea]
              (do (set-editor-json! k v)))
            {}))

(rf/reg-event-fx
 ::set-editor-xml
 (fn-traced [{:keys [db]} [_ ea]]
            (let [[k v] ea]
              (do (set-editor-xml! k v)))
            {}))

(rf/reg-event-fx
 ::set-editor-str
 (fn-traced [{:keys [db]} [_ ea]]
            (let [[k v] ea]
              (do (set-editor-str! k v)))
            {}))

(rf/reg-event-fx
 ::set-editor-preserve
 (fn-traced [{:keys [db]} [_ ea]]
            (let [[k ref] ea]
              (do (set-editor-preserve! k ref)))
            {}))

; re-frame iceptors

(rf/reg-cofx
 ::get-editor-val
 (fn [cofx [k]]
   (assoc cofx :get-editor-val (do (get-editor-val k)))))


