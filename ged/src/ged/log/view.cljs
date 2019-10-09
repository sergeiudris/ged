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
   

             #_["antd/lib/button" :default ant-Button]
             #_["antd/lib/table" :default AntTable]))


(def ant-row (r/adapt-react-class AntRow ))
(def ant-col (r/adapt-react-class AntCol))
(def ant-select (r/adapt-react-class AntSelect))
(def ant-select-option (r/adapt-react-class (.-Option AntSelect)))
(def ant-input (r/adapt-react-class AntInput))
(def ant-button (r/adapt-react-class AntButton))
(def ant-table (r/adapt-react-class AntTable))


(def columns
  [{:key :uuid
    :title "uuid"
    :dataIndex :uuid}])

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
        [:section
         [log-table]
         ;
         ]
        ;
        ))))

(defn module-actions []
  [])
