(ns ged.map.view
  (:require  [reagent.core :as r]
             [cljs.repl :as repl]
             [cljs.pprint :as pp]
             [re-frame.core :as rf]
             [ged.map.subs :as subs]
             [ged.map.events :as events]
             ["antd/lib/button" :default AntButton]
             ["antd/lib/button/button-group" :default AntButtonGroup]
             ["antd/lib/radio" :default AntRadio]
             ["antd/lib/icon" :default AntIcon]
             ["antd/lib/row" :default AntRow]
             ["antd/lib/col" :default AntCol]
             ["antd/lib/table" :default AntTable]
             ["antd/lib/dropdown" :default AntDropdown]
             ["antd/lib/menu" :default AntMenu]
             ["antd/lib/input" :default AntInput]
             [ged.map.ol :as ol]
             ["ol/format/filter" :as olf]
             [ged.map.core :refer [get-olmap] :as core]))

(def ant-button (r/adapt-react-class AntButton))
(def ant-button-group (r/adapt-react-class AntButtonGroup))
(def ant-radio (r/adapt-react-class AntRadio))
(def ant-radio-group (r/adapt-react-class (.-Group AntRadio)))
(def ant-radio-button (r/adapt-react-class (.-Button AntRadio)))
(def ant-icon (r/adapt-react-class AntIcon))
(def ant-row (r/adapt-react-class AntRow))
(def ant-col (r/adapt-react-class AntCol))
(def ant-table (r/adapt-react-class AntTable))
(def ant-dropdown (r/adapt-react-class AntDropdown))
(def ant-menu (r/adapt-react-class AntMenu))
(def ant-menu-item (r/adapt-react-class (.-Item AntMenu)))
(def ant-menu-divider (r/adapt-react-class (.-Divider AntMenu)))
(def ant-input (r/adapt-react-class AntInput))


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
        (when (get-olmap)
          (ol/set-target (get-olmap) nil)))
      :reagent-render
      (fn [x y z]
        [:div#map-container {:style {:width "100%"
                                     :height "100%"
                                     :border "1px solid #dedede"}}])})))


(defn ol-wms-layer
  []
  (let []
    (r/create-class
     {:display-name "ol-wms-layer"
      :component-did-mount
      (fn [this]
        (let [{:keys [id geoserver-host]} (r/props this)]
          (do (ol/upsert-wms-layer (get-olmap) geoserver-host id))))
      :component-will-unmount
      (fn [this]
        (let [{:keys [id]} (r/props this)]
          (do (ol/remove-layer (get-olmap) id))))
      :reagent-render
      (fn []
        nil)})))

; https://github.com/Day8/re-frame/blob/master/docs/Using-Stateful-JS-Components.md
#_(defn ol-map-layers-inner
  []
  (let []
    (r/create-class
     {:display-name "ol-map-layers"
      :component-did-mount
      (fn [this]
        (let [{:keys [checked-layer-ids geoserver-host]} (r/props this)]
          (js/console.log "view ids" checked-layer-ids)
          (ol/upsert-wfs-layers (get-olmap) geoserver-host checked-layer-ids)
          ))
      :component-did-update
      (fn [this old-argv]
        (let [new-argv (rest (r/argv this))
              {:keys [checked-layer-ids geoserver-host]} (r/props this)]
          (js/console.log "component-did-update" checked-layer-ids)
          #_(js/console.log new-argv old-argv)))
      :component-will-unmount
      (fn [this])
      :reagent-render
      (fn []
        nil)})))

(defn ol-map-layers
  []
  (let [geoserver-host (rf/subscribe [:ged.subs/geoserver-host])
        ids-ref (rf/subscribe [:ged.map.subs/checked-layer-ids])]
    (fn []
      (let [host @geoserver-host
            ids @ids-ref
            ]
        [:<>
         (map (fn [id]
                [ol-wms-layer
                 {:key id
                  :id id
                  :geoserver-host host}])
              ids)]
        
        )
      #_[ol-map-layers-inner
       {:checked-layer-ids @ids-ref
        :geoserver-host @geoserver-host}])))

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
                       :icon "search"}]
          [ant-button {:value "modify"
                       :title "modify"
                       :type (key->button-type :modify tab)
                       :icon "edit"}]
          ]])
      )))

;all layers 

(def all-layers-base-colums
  [{:title "name"
    :key :name
    :dataIndex :name}])

(def all-layers-extra-columns
  [{:title ""
    :key "action"
    :width "32px"
    :render
    (fn [txt rec idx]
      (let [on-click
            (fn [ea]
              (let [key (keyword (.-key ea))]
                (cond
                  (= key :select)
                  (do (rf/dispatch
                       [:ged.map.events/add-selected-layers-ids [(aget rec "name")]])))))
            menu
            (r/as-element
             [ant-menu {:on-click on-click
                        :size "small"}
              [ant-menu-item {:key "select"} "select"]])]
        (r/as-element
         [ant-dropdown
          {:overlay menu :trigger ["click"]}
          [ant-button
           {:size "small"
            :icon "down"}]])))}])

(def all-layers-colums
  (vec (concat all-layers-base-colums
               all-layers-extra-columns)))

(defn all-layers
  []
  (let [avisible (rf/subscribe [:ged.map.subs/all-layers-visible])
        all-layers (rf/subscribe [:ged.map.subs/all-layers])
        achecked (rf/subscribe [:ged.map.subs/all-layers-checked])
        ]
    (fn []
      (let [visible? @avisible
            lrs @all-layers
            checked @achecked]
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
             :rowSelection {:selectedRowKeys checked
                            :on-change (fn [keys rows ea]
                                         (rf/dispatch
                                          [:ged.map.events/all-layers-checked keys])
                                         #_(js/console.log keys rows ea))}
             :pagination false}]])))))

; selected layers

(def selected-layers-base-colums
  [{:title "name"
    :key :name
    :dataIndex :name}])


(def selected-layers-extra-columns
  [{:title ""
    :key "action"
    :width "32px"
    :render
    (fn [txt rec idx]
      (let [on-click 
            (fn [ea]
              (let [key (keyword (.-key ea))
                    name (aget rec "name")]
                (cond
                  (= key :remove)
                  (rf/dispatch
                   [:ged.map.events/remove-selected-layers-id name])
                  (= key :modify)
                  (rf/dispatch
                   [:ged.map.events/modify-layer name]))))
            menu
            (r/as-element
             [ant-menu {:on-click on-click
                        :size "small"}
              [ant-menu-item {:key "modify"} "modify"]
              [ant-menu-divider]
              [ant-menu-item{:key "remove"} "remove"]])]
        (r/as-element
         [ant-dropdown
          {:overlay menu :trigger ["click"]}
          [ant-button
           {:size "small"
            :icon "down"}]]))
      
      )}])

(def selected-layers-colums
  (vec (concat selected-layers-base-colums
               selected-layers-extra-columns)))

(defn selected-layers
  []
  (let [avisible (rf/subscribe [:ged.map.subs/selected-layers-visible])
        alayers (rf/subscribe [:ged.map.subs/selected-layers])
        achecked (rf/subscribe [:ged.map.subs/selected-layers-checked])
        ]
    (fn []
      (let [visible? @avisible
            data @alayers
            checked @achecked]
        (when visible?
          [:section {:class "all-layers-container"}
           [ant-row "selected layers"]
           [ant-row
            [ant-col {:style {:text-align "right"}}
             [ant-button-group {:size "small"}
              #_[ant-button {:icon "reload" :title "update"
                           :on-click
                           #(rf/dispatch
                             [:ged.map.events/fetch-all-layers])}]]]]
           [ant-table
            {:show-header true
             :size "small"
             :row-key :name
             :className ""
             :columns selected-layers-colums
             :dataSource data
             :on-change (fn [pag fil sor ext]
                          (rf/dispatch [:ged.feats.events/search-table-mdata
                                        (js->clj {:pagination pag
                                                  :filters fil
                                                  :sorter sor
                                                  :extra ext} :keywordize-keys true)]))
             :scroll {;  :x "max-content" 
                                ;  :y 256
                      }
             :rowSelection {:selectedRowKeys checked
                            :on-change (fn [keys rows ea]
                                         (rf/dispatch
                                          [:ged.map.events/selected-layers-checked keys])
                                         #_(js/console.log keys rows ea))}
             :pagination false}]])))))


(defn wfs-search-layer-input
  []
  (let [ainput (rf/subscribe [:ged.map.subs/wfs-search-layer-input])]
    (fn []
      (let [input @ainput
            on-change
            (fn [ev]
              (rf/dispatch [:ged.map.events/wfs-search-layer-input
                            (.. ev -target -value)]))]
        [ant-input {:value input
                    :on-change on-change
                    :placeholder "topp:states"
                    :style {:width "100%"}}]
        )
      )
    )
  )

(defn wfs-search-buttons
  []
  (let [aarea (rf/subscribe [:ged.map.subs/wfs-search-area-type])]
    (fn []
      (let [area @aarea
            on-click (fn [ev]
                       (rf/dispatch
                        [:ged.map.events/wfs-search-area-type
                         (.. ev -target -value)]))]
        [ant-button-group {:size "small" :on-click on-click}
         [ant-button {:title "search a point"
                      :icon "environment"
                      :type (key->button-type :area-point area)
                      :value "area-point"}]
         [ant-button {:title "search an area"
                      :icon "border"
                      :type (key->button-type :area-box area)
                      :value "area-box"}]]))))

(defn wfs-search-map-click-inner
  []
  (let []
    (r/create-class
     {:component-did-mount
      (fn [this]
        (let [{:keys [on-click]} (r/props this)]
          (.on (get-olmap) "singleclick"
               on-click)))
      :component-will-unmount
      (fn [this]
        #_(js/console.log "will unmount click")
        (let [{:keys [on-click]} (r/props this)]
          (when (get-olmap)
            (.un (get-olmap) "singleclick"
                 on-click))))
      :reagent-render (fn [] nil)})))

(defn wfs-search-map-click
  []
  (let [amap-click? (rf/subscribe [:ged.map.subs/wfs-search-map-click?]) ]
    (fn []
      (let [map-click? @amap-click?
            on-click
            (fn [ev]
              (let [coords (.. ev -coordinate)
                    geom (ol/point->cir-poly-geom (get-olmap) coords 16)
                    filter (olf/intersects "the_geom"  geom)
                    ; wkt (ol/point->wkt-cir-poly {:coords coords :radius 8})
                    ]
                (rf/dispatch [:ged.map.events/wfs-search {:filter filter}]))
              )]
        (when map-click?
          [wfs-search-map-click-inner {:on-click on-click}])
        ))))

(defn wfs-search-area-box-inner
  []
  (let [astate (r/atom nil)]
    (r/create-class
     {:component-did-mount
      (fn [this]
        (let [{:keys [on-draw-end]} (r/props this)]
          (do
            (->>
             (ol/add-box-interaction (get-olmap) {:on-draw-end on-draw-end})
             (reset! astate)))
          ))
      :component-will-unmount
      (fn [this]
        (let [{:keys [on-draw-end]} (r/props this)]
          (when (get-olmap)
            (ol/remove-interaction (get-olmap) @astate))))
      :reagent-render (fn [] nil)})))

(defn wfs-search-area-box
  []
  (let [aactive? (rf/subscribe [:ged.map.subs/wfs-search-area-box?])]
    (fn []
      (let [active? @aactive?
            on-draw-end
            (fn [ev]
              (let [; coords (.. ev -coordinate)
                    geom (.getGeometry (.-feature ev))
                    filter (olf/intersects "the_geom"  geom)
                    ; wkt (ol/point->wkt-cir-poly {:coords coords :radius 8})
                    ]
                (rf/dispatch [:ged.map.events/wfs-search {:filter filter}])))]
        (when active?
          [wfs-search-area-box-inner {:on-draw-end on-draw-end}])))))


(def wfs-search-base-columns
  [{:title "id"
    :key "id"
    :dataIndex "id"}
   #_{:title "geometry_name"
    :key "geometry_name"
    :dataIndex "geometry_name"}])

(def wfs-search-extra-columns
  [{:title ""
    :key "action"
    :width "32px"
    :render
    (fn [txt rec idx]
      (let [on-click
            (fn [ea]
              (let [key (keyword (.-key ea))
                    name (aget rec "name")]
                (cond
                  (= key :remove)
                  (rf/dispatch
                   [:ged.map.events/remove-selected-layers-id name]))))
            menu
            (r/as-element
             [ant-menu {:on-click on-click
                        :size "small"}
              [ant-menu-item {:key "edit"} "edit"]
              [ant-menu-divider]
              [ant-menu-item {:key "remove"} "remove"]])]
        (r/as-element
         [ant-dropdown
          {:overlay menu :trigger ["click"]}
          [ant-button
           {:size "small"
            :icon "down"}]])))}])

(def wfs-search-columns
  (vec (concat
        wfs-search-base-columns
        #_wfs-search-extra-columns)))

(defn wfs-search-table
  []
  (let [adata (rf/subscribe [:ged.map.subs/wfs-search-res])
        table-mdata (rf/subscribe [:ged.map.subs/wfs-search-table-mdata])]
    (fn []
      (let [items (:features @adata)
            total (:totalFeatures @adata)
            ents items
            #_(mapv #(-> % :entity (dissoc :db/id)) items)
            pagination (:pagination @table-mdata)
            first-item (first items)
            ]
        #_(js/console.log first-item)
        [ant-table {:show-header true
                    :size "small"
                    :row-key :id
                    :style {:height "91%" :overflow-y "auto"}
                    :columns wfs-search-columns
                    :dataSource ents
                    ; :defaultExpandedRowKeys [(:id first-item)]
                    :on-change (fn [pag fil sor ext]
                                 (rf/dispatch [:ged.map.events/wfs-search-table-mdata
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
                    :defaultExpandAllRows false
                    :expandedRowRender
                    (fn [rec]
                      (r/as-element
                       [:div {:style {:max-height "50vh" :overflow-y "auto" }}
                        (js/JSON.stringify (aget rec "properties") nil "\t")
                        #_(str (js->clj (aget rec "properties")))]))
                    :pagination (merge pagination
                                       {:total total
                                            ; :on-change #(js/console.log %1 %2)
                                        })}]))))


(defn wfs-search
  []
  (let [avisible (rf/subscribe [:ged.map.subs/wfs-search-visible])]
    (fn []
      (let [visible? @avisible]
        (when visible?
          [:section {:class "all-layers-container"}
           [:div "wfs search"]
           [ant-row
            [ant-col {:style {:text-align "right"}}
             [wfs-search-buttons]]]
           [ant-row
            [ant-col
             [wfs-search-layer-input]]]
           [wfs-search-table]
           [wfs-search-map-click]
           [wfs-search-area-box]
           ])))))

(defn modify-buttons
  []
  (let []
    (fn []
      (let [on-click (fn [ev]
                       (rf/dispatch
                        [:ged.map.events/modify-commit ]))]
        [ant-button-group {:size "small"}
         [ant-button {:title "commit changes"
                      :icon "save"
                      :type "default"}]]
        ))))

(defn modify
  []
  (let [avisible (rf/subscribe [:ged.map.subs/modify-visible])
        alayer-id (rf/subscribe [:ged.map.subs/modify-layer-id])
        ]
    (fn []
      (let [visible? @avisible
            input @alayer-id]
        (when visible?
          [:section {:class "all-layers-container"}
           [:div "modify"]
           [ant-row
            [ant-col {:style {:text-align "right"}}
             [modify-buttons]]]
           [ant-row
            [ant-col 
             [ant-input {:value input
                         :read-only true
                        ;  :disabled true
                         :placeholder "topp:states"
                         :style {:width "100%"}}]
             ]]])))))


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
     [wfs-search]
     [modify]
     ]))

