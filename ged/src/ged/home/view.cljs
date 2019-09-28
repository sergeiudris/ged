(ns ged.home.view
  (:require  [reagent.core :as r]
             [cljs.repl :as repl]
             [cljs.pprint :as pp]
             [re-frame.core :as rf]
             [ged.home.subs :as subs]
             [ged.home.events :as events]
             ["antd/lib/button" :default ant-Button]
             #_[ged.core.extra :refer [extra-component]]))

(defonce cnt (atom 0))

(def ant-button (r/adapt-react-class ant-Button))

(comment
  
  (swap! cnt inc)
  ;
  )

(defn stateful-comp
  []
  (let [component-state (r/atom {:count 0})]
    (fn []
      [:div ;; That returns hiccup
       [:p "count is: " (get @component-state :count)]
       [:button {:on-click #(swap! component-state update-in [:count] inc)} "Increment"]])))

(defn panel []
  (let [module-count @(rf/subscribe [::subs/module-count])
        base-url @(rf/subscribe [:ged.subs/base-url] )
        ]
    [:div
     [:p (str "I am a component!! " @cnt)]
     [:p (str "loaded modules count: " module-count) ]
     [:base-url (str "base url is: " base-url) ]
     [:p.someclass
      "I have " [:strong "bold"]
      [:span {:style {:color "red"}} " and red "] "text."]
     [ant-button {:on-click (fn [] (rf/dispatch [::events/inc-module-count]) )
                  } "inc module count"]
     #_[extra-component]
     [stateful-comp]
     [:div
      (map (fn [x]
             [:div {:key x :id x} x]
             ) (range 1 10) )
      ]
     ]))

