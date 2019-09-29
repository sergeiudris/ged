(ns ged.feats.editor
  (:require  [reagent.core :as r]
             [re-frame.core :as rf]
             [cljs.repl :as repl]
             [cljs.pprint :as pp]
             [ged.feats.core :refer [editor-get-val editor-set-str!]]
             ["react-ace/lib/index.js" :default ReactAce]
             ["brace" :as brace]
             ["brace/mode/clojure.js"]
             ["brace/mode/graphqlschema.js"]
             ["brace/mode/json.js"]
             ["brace/mode/xml.js"]
             ["brace/theme/github.js"]))


(def react-ace (r/adapt-react-class ReactAce))

(defn editor-feature
  []
  (let [default-value ""
        value (r/atom default-value)]
    (fn []
      [react-ace {:name "editor-feature"
                  :mode "json"
                  :theme "github"
                  :className "editor-feature"
                  :width "32vw"
                    ;  :default-value default-value
                  :value @value
                  :on-load (fn [edr]
                             (let [vl (editor-get-val)]
                               (reset! ged.feats.core/editor-feature-ref edr)
                               (editor-set-str! vl)))
                  :on-change (fn [val evt] (do
                                             #_(js/console.log val)
                                             (reset! value val)
                                             #_(rf/dispatch [:ui.dbquery.events/editor-val val])))
                  :editor-props {
                                 "$blockScrolling" js/Infinity
                                 }}])))


(defn editor-response
  []
  (let [default-value ""
        value (r/atom default-value)]
    (fn []
      [react-ace {:name "editor-response"
                  :mode "xml"
                  :theme "github"
                  :className "editor-response"
                  :width "32vw"
                    ;  :default-value default-value
                  :value @value
                  :on-load (fn [edr]
                             (reset! ged.feats.core/editor-response-ref edr))
                  :on-change (fn [val evt] (do
                                             #_(js/console.log val)
                                             (reset! value val)
                                             #_(rf/dispatch [:ui.dbquery.events/editor-val val])))
                  :editor-props {"$blockScrolling" js/Infinity}}])))

(defn editor-request
  []
  (let [default-value ""
        value (r/atom default-value)]
    (fn []
      [react-ace {:name "editor-request"
                  :mode "xml"
                  :theme "github"
                  :className "editor-request"
                  :width "32vw"
                    ;  :default-value default-value
                  :value @value
                  :on-load (fn [edr]
                             (let [vl (editor-get-val)]
                               (reset! ged.feats.core/editor-request-ref edr)
                               (editor-set-str! vl)))
                  :on-change (fn [val evt] (do
                                             #_(js/console.log val)
                                             (reset! value val)
                                             #_(rf/dispatch [:ui.dbquery.events/editor-val val])))
                  :editor-props {"$blockScrolling" js/Infinity}}])))