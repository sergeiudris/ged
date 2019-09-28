(ns ged.feats.view
  (:require  [reagent.core :as r]
             [cljs.repl :as repl]
             [cljs.pprint :as pp]
             [re-frame.core :as rf]
             [ged.feats.subs :as subs]
             [ged.feats.events :as events]
             [ged.feats.sample :refer [sample-table]]
             ["antd/lib/button" :default ant-Button]
             #_[ged.core.extra :refer [extra-component]]))



(def ant-button (r/adapt-react-class ant-Button))





(defn panel []
  (let [module-count @(rf/subscribe [::subs/module-count])
        base-url @(rf/subscribe [:ged.subs/base-url] )
        ]
    [:div
     [sample-table]]))

