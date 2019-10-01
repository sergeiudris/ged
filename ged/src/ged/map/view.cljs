(ns ged.map.view
  (:require  [reagent.core :as r]
             [cljs.repl :as repl]
             [cljs.pprint :as pp]
             [re-frame.core :as rf]
             [ged.map.subs :as subs]
             [ged.map.events :as events]
             ["antd/lib/button" :default ant-Button]
             ["antd/lib/button/button-group" :default AntButtonGroup]
             ["antd/lib/radio" :default AntRadio]
             ["antd/lib/icon" :default AntIcon]
   
             [ged.map.ol :as ol]
             [ged.map.core :refer [get-olmap] :as core]))

(def ant-button (r/adapt-react-class ant-Button))
(def ant-button-group (r/adapt-react-class AntButtonGroup))
(def ant-radio (r/adapt-react-class AntRadio))
(def ant-radio-group (r/adapt-react-class (.-Group AntRadio)))
(def ant-radio-button (r/adapt-react-class (.-Button AntRadio)))
(def ant-icon (r/adapt-react-class AntIcon))






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

#_(js/console.log (:map @state))

(defn ol-map
  [x y z]
  (let []
    (r/create-class
     {:display-name "ol-map"
      :component-did-mount
      (fn [this]
        (println "component-did-mount")
        (if (not (get-olmap))
          (do
            (js/console.log "creating new map..")
            (core/set-map (ol/create-map {:el-id "map-container"}))
            #_(.addLayer (get-olmap) (ol/wms-layer geoserver-host "dev:usa_major_cities"))
            #_(.addLayer (get-olmap) (ol/wms-layer geoserver-host "dev:usa_major_cities_2"))
            (set! (.. js/window -map) (get-olmap)))
          (do
            (js/console.log "setting new map target..")
            (ol/set-target (get-olmap) "map-container"))))
      :component-did-update
      (fn [this old-argv]
        (let [new-argv (rest (r/argv this))]
          (js/console.log new-argv old-argv)))
      :component-will-unmount
      (fn [this]
        (ol/set-target (get-olmap) nil))
      :reagent-render
      (fn [x y z]
        [:div#map-container {:style {:width "100%"
                                     :height "100%"
                                     :border "1px solid #dedede"}}])})))

(defn ol-map-layers
  []
  (let [geoserver-host (rf/subscribe [:ged.subs/geoserver-host])
        ids-ref (rf/subscribe [:ged.map.subs/checked-layer-ids])]
    (r/create-class
     {:display-name "ol-map-layers"
      :component-did-mount
      (fn [this]
        (let [ids @ids-ref
              host @geoserver-host]
          (doseq [id ids]
            (when (not (ol/id->layer (get-olmap) id))
              (ol/add-layer (get-olmap) host id))
            ))
        )
      :component-did-update
      (fn [this old-argv]
        (let [new-argv (rest (r/argv this))]
          (js/console.log new-argv old-argv)))
      :component-will-unmount
      (fn [this])
      :reagent-render
      (fn []
        nil)})))


(defn action-buttons
  []
  (let []
    (fn []
      [:section {
                 :style {:position "absolute" :left 0 :top "30vh"}}
       [ant-button-group {:size "default"}
        [ant-button {:icon "reload"
                     :title "refetch layers"
                     :on-click (fn []
                                 (rf/dispatch [:ged.map.events/refetch-wms-layers]))}]]
       ]
      ))
  )

(defn active->button-type
  [active?]
  (if active? "primary" "default"))

(defn key->button-type
  [btn-key key]
  (active->button-type (= btn-key key)))

(defn tab-buttons
  []
  (let [atab (rf/subscribe [:ged.map.subs/tab-button]) ]
    (fn []
      (let [tab @atab]
        [:section {:class "tab-buttons-container"
                   :style {:position "absolute" :left 0 :top 64}}
         [ant-button-group {:style {:display "flex" :flex-direction "column"}
                            :value tab
                            :on-click (fn [ev]
                                         (rf/dispatch
                                          [:ged.map.events/tab-button
                                           (.. ev -target -value)]))
                            :size "default"}
          [ant-button {:value "selected-layers"
                       :icon "profile"
                       :type (key->button-type :selected-layers tab)
                       :title "selected layers"}
           #_[ant-icon {:type "profile"}]]
          [ant-button {:value "all-layers"
                       :type (key->button-type :all-layers tab)
                       :icon "unordered-list"
                       :title "all layers"}
           #_[ant-icon {:type "unordered-list"}]]
          [ant-button {:value "wfs-search"
                       :title "wfs search"
                       :type (key->button-type :wfs-search tab)
                       :icon "search"}
           #_[ant-icon {:type "search" :title "wfs search"}]]]]
        )
      
      )))

(defn panel []
  (let [module-count @(rf/subscribe [::subs/module-count])
        base-url @(rf/subscribe [:ged.subs/base-url])
        ]
    [:div {:style {:height "100%" :margin-left 0}}
     [action-buttons]
     [tab-buttons]
     [ol-map 1 2 3]
     [ol-map-layers]]))

