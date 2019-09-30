(ns ged.auth.view
  (:require  [reagent.core :as r]
             [re-frame.core :as rf]
             [cljs.repl :as repl]
             [cljs.pprint :as pp]
             [ged.auth.events]
             [ged.auth.subs]
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
         #_[:div "auth"]
         [ant-row
          [ant-col {:span 3} "username"]
          [ant-col {:span 8}
           [ant-input {:value @proxy-path
                       :on-change
                       #(js/console.log (.. % -target -value))}]]
          ]
         [:br]
         [ant-row
          [ant-col {:span 3} "password"]
          [ant-col {:span 8}
           [ant-input {:value @proxy-geoserver-host
                       :on-change
                       #(js/console.log (.. % -target -value))}]]]
         
         [:br]
         [ant-row
          [ant-col {:span 4}
           [ant-button
            {:on-click (fn [] (rf/dispatch [:ged.events/apply-server-settings]))}
            "login"]]
          [ant-col {:span 4}
           [ant-button
            {:on-click (fn [] (rf/dispatch [:ged.events/apply-server-settings]))}
            "logout"]]]
         ;
         ]
        ;
        ))))

