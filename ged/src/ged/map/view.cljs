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
             ["antd/lib/row" :default AntRow]
             ["antd/lib/col" :default AntCol]
             ["antd/lib/table" :default AntTable]
             [ged.map.ol :as ol]
             [ged.map.core :refer [get-olmap] :as core]))

(def ant-button (r/adapt-react-class ant-Button))
(def ant-button-group (r/adapt-react-class AntButtonGroup))
(def ant-radio (r/adapt-react-class AntRadio))
(def ant-radio-group (r/adapt-react-class (.-Group AntRadio)))
(def ant-radio-button (r/adapt-react-class (.-Button AntRadio)))
(def ant-icon (r/adapt-react-class AntIcon))
(def ant-row (r/adapt-react-class AntRow))
(def ant-col (r/adapt-react-class AntCol))
(def ant-table (r/adapt-react-class AntTable))

#_(js/console.log
   (js/document.getElementById
    "map-container"))

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
              (ol/add-layer (get-olmap) host id)))))
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
      [:section {:style {:position "absolute" :right 64 :top 32}}
       [ant-button-group {:size "default"}
        [ant-button
         {:icon "reload"
          :title "refetch layers"
          :on-click (fn []
                      (rf/dispatch [:ged.map.events/refetch-wms-layers]))}]]])))

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
          [ant-button {:value "all-layers"
                       :type (key->button-type :all-layers tab)
                       :icon "unordered-list"
                       :title "all layers"}]
          [ant-button {:value "selected-layers"
                       :icon "profile"
                       :type (key->button-type :selected-layers tab)
                       :title "selected layers"}]
          [ant-button {:value "wfs-search"
                       :title "wfs search"
                       :type (key->button-type :wfs-search tab)
                       :icon "search"}]]])
      )))

(def all-layers-base-colums
  [{:title "name"
    :key :name
    :dataIndex :name}])

(def all-layers-extra-columns
  [{:title ""
    :key "action"
    :width "64px"
    :render
    (fn [txt rec idx]
      (r/as-element
       [ant-button
        {:size "small"
         :icon "menu"
         :on-click #(rf/dispatch
                     [:ged.feats.events/select-feature
                      rec])}]))}])

(def all-layers-colums
  (vec (concat all-layers-base-colums
               all-layers-extra-columns)))

(defn all-layers
  []
  (let [avisible (rf/subscribe [:ged.map.subs/all-layers-visible])
        all-layers (rf/subscribe [:ged.map.subs/all-layers])]
    (fn []
      (let [visible? @avisible
            lrs @all-layers]
        (when visible?
          [:section {:class "all-layers-container"}
           [ant-row "all layers"]
           [ant-row
            [ant-col {:style {:text-align "right"}}
             [ant-button-group {:size "small"}
              [ant-button {:icon "reload" :title "update"
                           :on-click
                           #(rf/dispatch
                             [:ged.map.events/fetch-all-layers])}]]]]
           [ant-table
            {:show-header true
             :size "small"
             :row-key :name
             :className ""
             :columns all-layers-colums
             :dataSource lrs
             :on-change (fn [pag fil sor ext]
                          (rf/dispatch [:ged.feats.events/search-table-mdata
                                        (js->clj {:pagination pag
                                                  :filters fil
                                                  :sorter sor
                                                  :extra ext} :keywordize-keys true)]))
             :scroll {;  :x "max-content" 
                                ;  :y 256
                      }
          ; :rowSelection {:on-change (fn [keys rows]
          ;                             (prn keys)
          ;                             )}
             :pagination false}]])))))

(defn selected-layers
  []
  (let [avisible (rf/subscribe [:ged.map.subs/selected-layers-visible])]
    (fn []
      (let [visible? @avisible]
        (when visible?
          [:section {:class "all-layers-container"}
           "selcted layers"
           ])))))

(defn wfs-search
  []
  (let [avisible (rf/subscribe [:ged.map.subs/wfs-search-visible])]
    (fn []
      (let [visible? @avisible]
        (when visible?
          [:section {:class "all-layers-container"}
           "wfs search"])))))

(defn panel []
  (let [module-count @(rf/subscribe [::subs/module-count])
        base-url @(rf/subscribe [:ged.subs/base-url])
        ]
    [:div {:style {:height "100%" :margin-left 0}}
     [action-buttons]
     [tab-buttons]
     [ol-map 1 2 3]
     [ol-map-layers]
     [all-layers]
     [selected-layers]
     [wfs-search]]))

