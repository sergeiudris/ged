(ns ged.rest.view
  (:require  [reagent.core :as r]
             [re-frame.core :as rf]
             [cljs.repl :as repl]
             [cljs.pprint :as pp]
             [ged.rest.events]
             [ged.rest.subs]
             [ged.rest.core]
             [ged.rest.editor :refer [editor-data
                                       editor-request
                                       editor-response]]
             ["antd/lib/icon" :default AntIcon]
             ["antd/lib/button" :default AntButton]
             ["antd/lib/button/button-group" :default AntButtonGroup]
             ["antd/lib/input" :default AntInput]
             ["antd/lib/progress" :default AntProgress]
             ["antd/lib/input/Search" :default AntInputSearch]
             ["antd/lib/table" :default AntTable]
             ["antd/lib/auto-complete" :default AntAutoComplete]))


(def ant-icon (r/adapt-react-class AntIcon))
(def ant-button (r/adapt-react-class AntButton))
(def ant-button-group (r/adapt-react-class AntButtonGroup))
(def ant-input (r/adapt-react-class AntInput))
(def ant-input-search (r/adapt-react-class AntInputSearch))
(def ant-auto-complete (r/adapt-react-class AntAutoComplete))
(def ant-auto-complete-option (r/adapt-react-class (.-Option AntAutoComplete)))
(def ant-table (r/adapt-react-class AntTable))


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
                    (rf/dispatch [:ged.rest.events/search {:input s}]))
        on-change (fn [s]
                    #_(prn "s:" (.. evt -target -value))
                    (swap! state assoc :input s))
        on-key-up (fn [evt]
                    (when (= (.-key evt) "Enter")
                      (on-search (.. evt -target -value))))]
    (fn [_]
      [ant-auto-complete
       {:style {:width "32%"}
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

(defn feature-type-input
  []
  (let [sref (rf/subscribe
              [:ged.rest.subs/feature-type-input])]
    (fn []
      [ant-input {:style {:width "16%" :margin "0 0 0 8px"}
                  :value @sref
                  :on-change
                  (fn [ev]
                    (rf/dispatch
                     [:ged.rest.events/feature-type-input
                      (.. ev -target -value)]))
                  :placeholder "topp:states"}])))

(defn feature-ns
  []
  (let [sref (rf/subscribe
              [:ged.rest.subs/feature-ns])]
    (fn []
      [ant-input {:style {:width "16%" :margin "0 0 0 8px"}
                  :value @sref
                  :on-change
                  (fn [ev]
                    (rf/dispatch
                     [:ged.rest.events/feature-ns
                      (.. ev -target -value)]))
                  :placeholder "http://www.opengis.net/wfs/dev"}])))

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
                              [:ged.rest.events/select-feature
                               rec])}
                 "select"]]))}
   #_{:title ""
      :key "empty"}])

(def columns (vec (concat feature-columns extra-columns)))

(defn table
  []
  (let [search-res (rf/subscribe [:ged.rest.subs/search-res])
        table-mdata (rf/subscribe [:ged.rest.subs/search-table-mdata])]
    (fn []
      (let [items (:features @search-res)
            total (:totalFeatures @search-res)
            ents items
            #_(mapv #(-> % :entity (dissoc :db/id)) items)
            pagination (:pagination @table-mdata)]
        [ant-table {:show-header true
                    :size "small"
                    :row-key :id
                    :className "rest-table"
                    :columns columns
                    :dataSource ents
                    :on-change (fn [pag fil sor ext]
                                 (rf/dispatch [:ged.rest.events/search-table-mdata
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
  #_(rf/dispatch [:ged.rest.events/nutrients])
  #_(rf/dispatch [:ged.rest.events/nhi-dri])
  (let []
    (fn []
      [:section
       #_[search]
       [:div
        [auto-complete {}]
        #_[feature-type-input]
        #_[feature-ns]]
       #_[buttons]
       [:br]
       [table]
       [:br]
       [:section {:class "editors-container"}
        [editor-data]
        [editor-request]
        [editor-response]]
       [:br]
       [ant-button-group {:size "small"}
        [ant-button {:on-click
                     #(rf/dispatch [:ged.rest.events/tx-feature {:tx-type :inserts}])
                     :style {:width "96px"}}
         "insert"]
        [ant-button {:on-click
                     #(rf/dispatch [:ged.rest.events/tx-feature {:tx-type :updates}])
                     :style {:width "96px"}}
         "update"]
        [ant-button {:on-click
                     #(rf/dispatch [:ged.rest.events/tx-feature {:tx-type :deletes}])
                     :style {:width "96px"}}
         "delete"]]
       [:br]


       #_[ged.rest.sample/sample-table]])))

