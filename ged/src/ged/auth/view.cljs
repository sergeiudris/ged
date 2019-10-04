(ns ged.auth.view
  (:require  [reagent.core :as r]
             [re-frame.core :as rf]
             [cljs.repl :as repl]
             [cljs.pprint :as pp]
             [ged.auth.evs :as evs]
             [ged.auth.subs :as subs]
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
(def ant-input-password (r/adapt-react-class (.-Password AntInput)))

(def ant-button (r/adapt-react-class AntButton))


(defn panel []
  (let [username (rf/subscribe [:ged.subs/username])
        password (rf/subscribe [:ged.subs/password])
        uname (r/atom nil)
        pass (r/atom nil)]
    (fn []
      (let []
        [:section
         #_[:div "auth"]
         [ant-row
          [ant-col {:span 4} "username"]
          [ant-col {:span 8}
           [ant-input {:value (or @uname @username)
                       :on-change
                       #(reset! uname (.. % -target -value))}]]]
         [:br]
         [ant-row
          [ant-col {:span 4} "password"]
          [ant-col {:span 8}
           [ant-input-password {:visibilityToggle true
                                :value (or @pass @password)
                                :on-change
                                #(reset! pass (.. % -target -value))}]]]

         [:br]
         
         [ant-row
          [ant-col {:span 12}
           [ant-row {:type "flex" :justify "end"}
            [ant-col {:span 3 :style {:text-align "right"}}
             [ant-button
              {:title "Geoserver auth is stateless, credentials will be used in every request"
               :on-click (fn [] (rf/dispatch
                                 [::evs/login
                                  {:username (or @uname @username)
                                   :password (or @pass @password)}]))}
              "apply"]]]]]
         ;
         ]
        ;
        ))))

