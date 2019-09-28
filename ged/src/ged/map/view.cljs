(ns ged.map.view
  (:require  [reagent.core :as r]
             [cljs.repl :as repl]
             [cljs.pprint :as pp]
             [re-frame.core :as rf]
             [ged.map.subs :as subs]
             [ged.map.events :as events]
             ["antd/lib/button" :default ant-Button]
             ["ol/Map" :default OlMap]
             ["ol/View" :default OlView]
             ))

(def ant-button (r/adapt-react-class ant-Button))

(def state (atom {:map nil}))

(defn my-component
  [x y z]
  (let [some (r/atom {})      ;; <-- closed over by lifecycle fns
        can  (fn [])]
    (r/create-class                 ;; <-- expects a map of functions 
     {:display-name  "my-component"      ;; for more helpful warnings & errors

      :component-did-mount               ;; the name of a lifecycle function
      (fn [this]
        (println "component-did-mount")) ;; your implementation

      :component-did-update              ;; the name of a lifecycle function
      (fn [this old-argv]                ;; reagent provides you the entire "argv", not just the "props"
        (let [new-argv (rest (r/argv this))]
          (js/console.log new-argv old-argv)))

        ;; other lifecycle funcs can go in here


      :reagent-render        ;; Note:  is not :render
      (fn [x y z]           ;; remember to repeat parameters
        [:div (str x " " y " " z)])})))

(defn my-div []
  (let [this (r/current-component)]
    (into [:div.custom (r/props this)]
          (r/children this))))

#_(js/console.log
   (js/document.getElementById
    "map-container"))


(defn create-map
  [{:keys [target]
    :or {target (js/document.getElementById
                 "map-container")}}]
  (OlMap.
    (clj->js {:layers []
        :target target

        :view (OlView.
               {:center [0 0]
                    :maxZoom 19
                    :zoom 0})})))

(defn ol-map
  [x y z]
  (let []
    (r/create-class
     {:display-name "ol-map"
      :component-did-mount
      (fn [this]
        (when (not (:map @state))
          (swap! state assoc :map
                 (create-map {})))
        (println "component-did-mount"))
      :component-did-update
      (fn [this old-argv]
        (let [new-argv (rest (r/argv this))]
          (js/console.log new-argv old-argv)))
      :reagent-render
      (fn [x y z]
        [:div#map-container {:style {:width "100%"
                                     :height "100%"
                                     :border "1px solid #dedede"
                                     }} ])})))



(defn panel []
  (let [module-count @(rf/subscribe [::subs/module-count])
        base-url @(rf/subscribe [:ged.subs/base-url] )
        ]
    [:div {:style {:height "100%"}}
     [ol-map 1 2 3]
     ]))

