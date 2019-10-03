(ns ged.settings.view
  (:require  [reagent.core :as r]
             [re-frame.core :as rf]
             [cljs.repl :as repl]
             [cljs.pprint :as pp]
             [ged.settings.events]
             [ged.settings.subs]
             [ged.settings.core]
             ["antd/lib/row" :default AntRow]
             ["antd/lib/col" :default AntCol]
             ["antd/lib/select" :default AntSelect]
             ["antd/lib/input" :default AntInput]
             ["antd/lib/button" :default AntButton]

             #_["antd/lib/button" :default ant-Button]
             #_["antd/lib/table" :default AntTable]))


(def ant-row (r/adapt-react-class AntRow ))
(def ant-col (r/adapt-react-class AntCol))
(def ant-select (r/adapt-react-class AntSelect))
(def ant-select-option (r/adapt-react-class (.-Option AntSelect)))
(def ant-input (r/adapt-react-class AntInput))
(def ant-button (r/adapt-react-class AntButton))


(defn panel []
  (let [ 
        proxy-path (rf/subscribe [:ged.subs/proxy-path])
        proxy-geoserver-host (rf/subscribe [:ged.subs/proxy-geoserver-host])
        geoserver-host (rf/subscribe [:ged.subs/geoserver-host ] )
        ]
    (fn []
      (let []
        [:section
         #_[:div "settings"]
         #_[:br]
         #_[ant-row
            [ant-col {:span 3} "world: "]
            [ant-col {:span 4}
             [ant-select {:default-value "hello"
                          :style {:width "120px"}
                          :on-change (fn [v] (js/console.log v))}
              [ant-select-option {:value "hello"} "hello"]
              [ant-select-option {:value "hi"} "hi"]]]]
         #_[ant-row
          [ant-col {:span 3} "proxy path"]
          [ant-col {:span 8}
           [ant-input {:value @proxy-path
                       :on-change
                       #(rf/dispatch [:ged.settings.events/set
                                      :ged.settings/proxy-path
                                      (.. % -target -value)])}]]
          [ant-col {:span 4}
           [ant-button
            {:on-click (fn [] (rf/dispatch [:ged.events/apply-server-settings]) )}
            "apply"]]]
         [:br]
         [ant-row
          [ant-col {:span 3 
                    :title "will be used for proxying CORS requests"
                    } "proxy geoserver host"]
          [ant-col {:span 8}
           [ant-input {:value @proxy-geoserver-host
                       :on-change
                       #(rf/dispatch [:ged.settings.events/set
                                      :ged.settings/proxy-geoserver-host
                                      (.. % -target -value)])}]]
          [ant-col {:span 4}
           [ant-button 
            {:on-click (fn [] (rf/dispatch [:ged.events/apply-server-settings]))}
            "apply"]
           ]
          ]
         
         [:br]
         [ant-row
          [ant-col {:span 3
                    :title "will be used for wms requests (layer tiles) that do not require CORS"
                    } "geoserver host"]
          [ant-col {:span 8}
           [ant-input {:value @geoserver-host
                       :on-change
                       #(rf/dispatch [:ged.settings.events/set 
                                      :ged.settings/geoserver-host
                                      (.. % -target -value)])}]]]
         ;
         ]
        ;
        ))))

(defn module-actions []
  [])
