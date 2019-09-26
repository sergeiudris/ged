(ns tools.comp.layout
  (:require [cljs.repl :as repl]
            [cljs.pprint :as pp]
            [reagent.core :as r]
            [re-frame.core :as rf]
            [clojure.string]
            ["antd/lib/layout" :default AntLayout]
            ["antd/lib/layout/Sider" :default AntSider]
            #_["antd/lib/menu" :default AntMenu]
            #_["antd/lib/icon" :default AntIcon]))

(def ant-layout (r/adapt-react-class AntLayout))
(def ant-layout-content (r/adapt-react-class (.-Content AntLayout)))
(def ant-layout-header (r/adapt-react-class (.-Header AntLayout)))

(def ant-sider (r/adapt-react-class AntSider))
#_(def ant-menu (r/adapt-react-class AntMenu))
#_(def ant-menu-item (r/adapt-react-class (.-Item AntMenu)))
#_(def ant-icon (r/adapt-react-class AntIcon))

(defn ant-layout-sider-2col
  [menu content]
  [ant-layout {:style {:min-height "100vh"}}
   [ant-sider {:collapsible true :theme "light" :default-collapsed true}
    [:div {:class "logo"}
     [:img {:class "logo-img" :src "./img/logo-4.png"}]
     [:div {:class "logo-name"} "ged"]
     ]
    menu]
   [ant-layout
    {:style {:padding-top 24}}
    #_[ant-layout-header {:style {:background "#fff" :padding 0}}
     ""
     ]
    [ant-layout-content {:class "main-content"
                         :style {:margin "0 16px"}}
     content]]
   ])