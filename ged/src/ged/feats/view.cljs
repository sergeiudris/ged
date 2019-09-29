(ns ged.feats.view
  (:require  [reagent.core :as r]
             [cljs.repl :as repl]
             [cljs.pprint :as pp]
             [re-frame.core :as rf]
             [ged.feats.subs :as subs]
             [ged.feats.events :as events]
             [ged.feats.sample :refer [sample-table]]
             [ged.feats.editor :refer [editor-feature 
                                       editor-request
                                       editor-response]]
             ["antd/lib/icon" :default AntIcon]
             ["antd/lib/button" :default AntButton]
             ["antd/lib/button/button-group" :default AntButtonGroup]
             ["antd/lib/input" :default AntInput]
             ["antd/lib/progress" :default AntProgress]
             ["antd/lib/input/Search" :default AntInputSearch]
             ["antd/lib/table" :default AntTable]
             ["antd/lib/auto-complete" :default AntAutoComplete]

             #_[ged.core.extra :refer [extra-component]]))



(def ant-icon (r/adapt-react-class AntIcon))
(def ant-button (r/adapt-react-class AntButton))
(def ant-button-group (r/adapt-react-class AntButtonGroup))
(def ant-input (r/adapt-react-class AntInput))
(def ant-input-search (r/adapt-react-class AntInputSearch))
(def ant-auto-complete (r/adapt-react-class AntAutoComplete))
(def ant-auto-complete-option (r/adapt-react-class (.-Option AntAutoComplete)))
(def ant-table (r/adapt-react-class AntTable))


(defn search
  []
  (let [on-search (fn [s]
                    (prn s))]
    (fn []
      [ant-input-search {:style {:width "50%"}
                         :placeholder "search"
                         :on-search on-search}])))

(defn auto-complete-suffix
  [{:keys [on-click]}]
  [ant-button
   {:class "search-btn"
    :style {:margin-right "-12px"}
    :size "default"
    :on-click on-click
    :type "default"}
   [ant-icon {:type "search"}]])

(defn auto-complete
  [{:keys []}]
  (let [state (r/atom {:input ""})
        on-select (fn [s]
                    (prn "selected " s))
        on-search (fn [s]
                    (rf/dispatch [:ged.feats.events/search {:input s}]))
        on-change (fn [s]
                    #_(prn "s:" (.. evt -target -value))
                    (swap! state assoc :input s))
        on-key-up (fn [evt]
                    (when (= (.-key evt) "Enter")
                      (on-search (.. evt -target -value))))]
    (fn [_]
      [ant-auto-complete
       {:style {:width "50%"}
        :size "default"
        :placeholder "search"
        :on-search on-change
        :on-select on-select
        :option-label-prop "text"}
       [ant-input
        {:value (:input @state)
         :on-press-enter on-key-up
        ;  :on-key-up on-key-up
         :suffix (r/as-element [auto-complete-suffix
                                {:on-click #(on-search (:input @state))}])}]])))

(def feature-columns
  [{:title "id"
    :key "id"
    :dataIndex "id"}
   {:title "geometry_name"
    :key "geometry_name"
    :dataIndex "geometry_name"}])

(def extra-columns
  [{:title "action"
    :key "action"
    :width "64px"
    :render (fn [txt rec idx]
              (r/as-element
               [ant-button-group
                {:size "small"}
                [ant-button
                 {;:icon "plus"
                  :type "primary"

                  :on-click #(rf/dispatch
                              [:ged.feats.events/select-feature
                               rec])
                  }
                 "select"]]))}
   #_{:title ""
      :key "empty"}])

(def columns (vec (concat feature-columns extra-columns)))

(defn table
  []
  (let [search-res (rf/subscribe [:ged.feats.subs/search-res])
        table-mdata (rf/subscribe [:ged.feats.subs/search-table-mdata])]
    (fn []
      (let [items (:features @search-res)
            total (:totalFeatures @search-res)
            ents items
            #_(mapv #(-> % :entity (dissoc :db/id)) items)
            pagination (:pagination @table-mdata)]
        (js/console.log @search-res)
        [ant-table {:show-header true
                    :size "small"
                    :row-key :id
                    :className "feats-table"
                    :columns columns
                    :dataSource ents
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
                    :pagination (merge pagination
                                       {:total total
                                            ; :on-change #(js/console.log %1 %2)
                                        })}]))))

(defn panel
  []
  #_(js/console.log 'count-panel-fn)
  #_(rf/dispatch [:ged.feats.events/nutrients])
  #_(rf/dispatch [:ged.feats.events/nhi-dri])
  (let []
    (fn []
      [:section
       #_[search]
       [auto-complete {}]
       #_[buttons]
       [:br]
       [table]
       [:br]
       [:section {:class "editors-container" }
        [editor-feature]
        [editor-request]
        [editor-response]
        ]
       [:br]
       [ant-button-group {:size "small"}
        [ant-button {:style {:width "96px"}}
         "insert"]
        [ant-button {:on-click #(rf/dispatch [:ged.feats.events/tx-feature {:tx-type :updates}])
                     :style {:width "96px"}}
         "update"]
        [ant-button {:on-click #(rf/dispatch [:ged.feats.events/tx-feature {:tx-type :deletes}])
                     :style {:width "96px"}}
         "delete"]]
       [:br]
       

       #_[ged.feats.sample/sample-table]])))

