(ns ged.settings.view
  (:require  [reagent.core :as r]
             [re-frame.core :as rf]
             [cljs.repl :as repl]
             [cljs.pprint :as pp]
             [ged.settings.events]
             [ged.settings.subs]
             [ged.settings.spec]
             [ged.settings.core]
             ["antd/lib/row" :default AntRow]
             ["antd/lib/col" :default AntCol]
             ["antd/lib/select" :default AntSelect]
   
             #_["antd/lib/button" :default ant-Button]
             #_["antd/lib/table" :default AntTable]))


(def ant-row (r/adapt-react-class AntRow ))
(def ant-col (r/adapt-react-class AntCol))
(def ant-select (r/adapt-react-class AntSelect))
(def ant-select-option (r/adapt-react-class (.-Option AntSelect)))


(comment

  ;
  )

(defn settings-panel []
  (let []
    (fn []
      (let []
        [:section
         [:div "settings"]
         [:br]
         [ant-row
          [ant-col {:span 3} "world: "]
          [ant-col {:span 4}
           [ant-select {:default-value "hello"
                        :style {:width "120px"}
                        :on-change (fn [vl] (js/console.log vl))}
            [ant-select-option {:value "hello"} "hello"]
            [ant-select-option {:value "hi"} "hi"]]]
          ]
         ;
         ]
        ;
        ))))

(defn module-actions []
  [])
