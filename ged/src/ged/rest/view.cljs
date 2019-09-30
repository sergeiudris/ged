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
             ["antd/lib/row" :default AntRow]
             ["antd/lib/col" :default AntCol]
             ["antd/lib/icon" :default AntIcon]
             ["antd/lib/button" :default AntButton]
             ["antd/lib/button/button-group" :default AntButtonGroup]
             ["antd/lib/input" :default AntInput]
             ["antd/lib/select" :default AntSelect]
             ["antd/lib/progress" :default AntProgress]
             ["antd/lib/input/Search" :default AntInputSearch]
             ["antd/lib/table" :default AntTable]
             ["antd/lib/auto-complete" :default AntAutoComplete]))


(def ant-icon (r/adapt-react-class AntIcon))
(def ant-button (r/adapt-react-class AntButton))
(def ant-button-group (r/adapt-react-class AntButtonGroup))
(def ant-input (r/adapt-react-class AntInput))
(def ant-select (r/adapt-react-class AntSelect))
(def ant-select-option (r/adapt-react-class (.-Option AntSelect)))
(def ant-row (r/adapt-react-class AntRow))
(def ant-col (r/adapt-react-class AntCol))

(def ant-input-search (r/adapt-react-class AntInputSearch))
(def ant-auto-complete (r/adapt-react-class AntAutoComplete))
(def ant-auto-complete-option (r/adapt-react-class (.-Option AntAutoComplete)))
(def ant-table (r/adapt-react-class AntTable))




(defn select-endpoint
  []
  (let [selected-url (rf/subscribe [:ged.rest.subs/selected-url])
        on-select (fn [ev]
                    (js/console.log ev)
                    (rf/dispatch [:ged.rest.events/selected-url ev]))]
    (fn []
      [ant-select {:value @selected-url
                   :on-select on-select
                   :style {:width "100%"}}
       [ant-select-option
        {:value "/rest/workspaces/dev/featuretypes.json"}
        "/rest/workspaces/dev/featuretypes.json"]
       [ant-select-option
        {:value "/rest/workspaces/dev/datastores/pgdb/featuretypes.json"}
        "/rest/workspaces/dev/datastores/pgdb/featuretypes.json"]
       ])))

(defn btn-fetch
  []
  [ant-button 
   {:icon "reload"
    :on-click #(rf/dispatch [:ged.rest.events/fetch-selected-url])
    }
   ]
  )

(def base-columns
  [{:title "name"
    :key "name"
    :dataIndex "name"}
   ])

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

(def columns (vec (concat base-columns extra-columns)))

(def row-key :name)

(defn table
  []
  (let [lst (rf/subscribe [:ged.rest.subs/fetch-selected-url-list])
        table-mdata (rf/subscribe [:ged.rest.subs/search-table-mdata])]
    (fn []
      (let [items @lst
            total (count items)
            ents items
            #_(mapv #(-> % :entity (dissoc :db/id)) items)
            pagination (:pagination @table-mdata)]
        [ant-table {:show-header true
                    :size "small"
                    :row-key row-key
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
        [ant-row
         [ant-col {:span 10} [select-endpoint]]
         [ant-col {:span 2} [btn-fetch]]
         ]
        #_[feature-type-input]
        #_[feature-ns]]
       #_[buttons]
       [:br]
       [table]
       [:br]
       [:section {:class "editors-container"}
        [editor-data]
        ; [editor-request]
        [editor-response]]
       [:br]
       [ant-button-group {:size "small"}
        [ant-button {:on-click
                     #(rf/dispatch [:ged.rest.events/tx-feature {:tx-type :post}])
                     :style {:width "96px"}}
         "post"]
        [ant-button {:on-click
                     #(rf/dispatch [:ged.rest.events/tx-feature {:tx-type :put}])
                     :style {:width "96px"}}
         "put"]
        [ant-button {:on-click
                     #(rf/dispatch [:ged.rest.events/tx-feature {:tx-type :delete}])
                     :style {:width "96px"}}
         "delete"]]
       [:br]


       #_[ged.rest.sample/sample-table]])))

