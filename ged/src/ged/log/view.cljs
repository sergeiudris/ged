(ns ged.log.view
  (:require  [reagent.core :as r]
             [re-frame.core :as rf]
             [cljs.repl :as repl]
             [cljs.pprint :as pp]
             [ged.log.evs :as evs]
             [ged.log.subs :as subs]
             [ged.log.core]
             ["antd/lib/row" :default AntRow]
             ["antd/lib/col" :default AntCol]
             ["antd/lib/select" :default AntSelect]
             ["antd/lib/input" :default AntInput]
             ["antd/lib/button" :default AntButton]
             ["antd/lib/table" :default AntTable]
             ["antd/lib/tag" :default AntTag]
   

             #_["antd/lib/button" :default ant-Button]
             #_["antd/lib/table" :default AntTable]))


(def ant-row (r/adapt-react-class AntRow ))
(def ant-col (r/adapt-react-class AntCol))
(def ant-select (r/adapt-react-class AntSelect))
(def ant-select-option (r/adapt-react-class (.-Option AntSelect)))
(def ant-input (r/adapt-react-class AntInput))
(def ant-button (r/adapt-react-class AntButton))
(def ant-table (r/adapt-react-class AntTable))
(def ant-tag (r/adapt-react-class AntTag))


(def columns
  [{:key :uuid
    :title "uuid"
    :dataIndex :uuid}

   {:title "tag"
    :key :tag
    :align "center"
    :render (fn [t r i]
              (js/console.log r)
              (r/as-element
               [:div
                (when (aget r "http-xhrio")
                  [ant-tag {:color "blue"} "http"])
                (when (and (aget r "http-xhrio") (aget r "result"))
                  [ant-tag {:color "red"} "fail"])]))}

   {:title ""
    :key :action
    :width "32px"
    :render (fn [txt rec idx]
              (r/as-element
               [ant-button
                {:size "small"
                 :type "primary"
                 :title "select"
                 :on-click #(rf/dispatch [])}
                "->"]))}])

(defn log-table
  []
  (let [alog (rf/subscribe [::subs/log])
        atable-mdata (rf/subscribe [::subs/log-table-mdata])]
    (fn []
      (let [{:keys [data total]} @alog
            pagination (:pagination @atable-mdata)]
        [ant-table
         {:show-header true
          :size "small"
          :row-key :uuid
          :title (fn [_]
                   (r/as-element
                    [:section
                     [ant-button
                      {:on-click #(rf/dispatch [:ged.evs/clear-log])
                       :icon "stop" :size "small"
                       :title "clear"}]
                     ]))
          :columns columns
          :dataSource data
          :on-change (fn [pag fil sor ext]
                       (rf/dispatch [::evs/all-layers-table-mdata
                                     (js->clj {:pagination pag
                                               :filters fil
                                               :sorter sor
                                               :extra ext} :keywordize-keys true)]))
          :scroll {;  :x "max-content" 
                                ;  :y 256
                   }
          ; :rowSelection {
          ;                :on-change (fn [keys rows ea]
          ;                             (js/console.log keys rows ea))}
          :pagination (clj->js
                       (merge pagination {:total total
                                          :showTotal (fn [t rng] t)}))}]))))


(defn panel []
  (let []
    (fn []
      (let []
        [:div {:style {:height "100%" :width "100%" :display "flex"}}
         [:section {:style {:width "43%"}}
          [log-table]]
         [:section {:style {:width "4%"}}]
         [:section {:style {:width "43%"}}]]))))

(defn module-actions []
  [])
