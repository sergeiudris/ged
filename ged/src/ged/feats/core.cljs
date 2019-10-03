(ns ged.feats.core
  (:require [cljs.repl :as repl])
  )

(def editor-feature-ref (atom nil))

(def editor-response-ref (atom nil))

(def editor-request-ref (atom nil))

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

(defn editor-response-set!
  [v]
  (.setValue
   (.-session @editor-response-ref)
   v))

(defn editor-request-set!
  [v]
  (.setValue
   (.-session @editor-request-ref)
   v))