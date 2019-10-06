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
             ["antd/lib/table" :default AntTable]))


(def ant-row (r/adapt-react-class AntRow ))
(def ant-col (r/adapt-react-class AntCol))
(def ant-select (r/adapt-react-class AntSelect))
(def ant-select-option (r/adapt-react-class (.-Option AntSelect)))
(def ant-input (r/adapt-react-class AntInput))
(def ant-input-password (r/adapt-react-class (.-Password AntInput)))

(def ant-button (r/adapt-react-class AntButton))
(def ant-table (r/adapt-react-class AntTable))



(def profiles-base-columns
  [{:title "host"
    :key "host"
    :dataIndex "host"}
   {:title "user"
    :key "user"
    :dataIndex "user"}
   {:title "pass"
    :key "pass"
    :dataIndex "pass"}
   {:title "active"
    :key "active?"
    :dataIndex :active?}])

(def profiles-extra-columns
  [{:title ""
    :key "action"
    :width "32px"
    :render
    (fn [txt rec idx]
      (let [on-click (fn [ea]
                       (let [key (keyword (.-key ea))
                             name (aget rec "name")]
                         (cond
                           (= key :remove)
                           (rf/dispatch
                            [::evs/remove-selected-layers-id name]))))]
        (r/as-element
         [ant-button
          {:size "small"
           :icon "down"}])))}])

(def profiles-columns
  (vec (concat
        profiles-base-columns
        profiles-extra-columns)))

(defn profiles-table
  []
  (let [adata (rf/subscribe [::subs/wfs-search-res])
        table-mdata (rf/subscribe [::subs/wfs-search-table-mdata])]
    (fn []
      (let [items (:features @adata)
            total (:totalFeatures @adata)
            ents items
            #_(mapv #(-> % :entity (dissoc :db/id)) items)
            pagination (:pagination @table-mdata)
            first-item (first items)]
        #_(js/console.log first-item)
        [ant-table {:show-header true
                    :size "small"
                    :row-key :id
                    :style {:height "91%" :overflow-y "auto"}
                    :columns profiles-columns
                    :dataSource ents
                    ; :defaultExpandedRowKeys [(:id first-item)]
                    :on-change (fn [pag fil sor ext]
                                 (rf/dispatch [::evs/wfs-search-table-mdata
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
                    :defaultExpandAllRows false
                    :expandedRowRender
                    (fn [rec]
                      (r/as-element
                       [:div {:style {:max-height "50vh" :overflow-y "auto"}}
                        (js/JSON.stringify (aget rec "properties") nil "\t")
                        #_(str (js->clj (aget rec "properties")))]))
                    :pagination (merge pagination
                                       {:total total
                                            ; :on-change #(js/console.log %1 %2)
                                        })}]))))


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

