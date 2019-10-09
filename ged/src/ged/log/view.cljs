(ns ged.log.view
  (:require  [reagent.core :as r]
             [re-frame.core :as rf]
             [cljs.repl :as repl]
             [cljs.pprint :as pp]
             [ged.log.evs :as evs]
             [ged.log.subs :as subs]
             [ged.log.core]
             [ged.core :refer [->clj prettify-xml pretty-json
                               pretty-json-str pretty-edn]]
             ["antd/lib/row" :default AntRow]
             ["antd/lib/col" :default AntCol]
             ["antd/lib/select" :default AntSelect]
             ["antd/lib/input" :default AntInput]
             ["antd/lib/button" :default AntButton]
             ["antd/lib/table" :default AntTable]
             ["antd/lib/tag" :default AntTag]

             ["react-ace/lib/index.js" :default ReactAce]
             ["brace" :as brace]
             ["brace/mode/clojure.js"]
             ["brace/mode/graphqlschema.js"]
             ["brace/mode/json.js"]
             ["brace/mode/xml.js"]
             ["brace/mode/sql.js"]
             ["brace/theme/github.js"]


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
(def react-ace (r/adapt-react-class ReactAce))



(defn item-http-success?
  [v]
  (and (get v :http-xhrio) (get v :response)))

(defn item-http-failure?
  [v]
  (and (get v :http-xhrio) (get v :result)))

(defn item-http?
  [v]
  (get v :http-xhrio))



(defn item->ace-mode-body
  [item]
  (case  (:expected-body-fmt item)
    :xml "xml"
    :json "json"
    "text"))

(defn item->ace-mode-response
  [item]
  (case  (:expected-success-fmt item)
    :xml "xml"
    :json "json"
    "text"))


(def columns
  [{:key :uuid
    :title "uuid"
    :dataIndex :uuid}

   {:key :ts-created
    :title "when"
    :dataIndex :ts-created
    :render (fn [t r i]
              (r/as-element
               [:span (str (-> (js/Date.now) (- t) (/ 1000) (Math/round)) "s")]))}

   {:title "tag"
    :key :tag
    :align "center"
    :render (fn [t r i]
              (r/as-element
               [:div
                (when (item-http? (->clj r))
                  [ant-tag {:color "blue"} "http"])
                (when (item-http-failure? (->clj r))
                  [ant-tag {:color "red"} "fail"])
                (when (item-http-success? (->clj r))
                  [ant-tag {:color "green"} "ok"])]))}

   {:title ""
    :key :action
    :width "32px"
    :render (fn [t r i]
              (r/as-element
               [ant-button
                {:size "small"
                 :type "primary"
                 :title "select"
                 :on-click (fn []
                             (js/console.log r)
                             (rf/dispatch [::evs/select-item r]))}
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
                       (rf/dispatch [::evs/log-table-mdata
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

(defn info-pane-http-failure
  [opts]
  (let []
    (fn [{:keys [item]}]
      (let []
        [:div (aget item "uuid")]
        )
      )
    )
  )

(defn info-pane-http-success
  [opts]
  (let []
    (fn [{:keys [item]}]
      (let []
        [:<>
         [:div (get item :uuid)]
         [react-ace {:name "editor-item"
                     :mode "clojure"
                     :theme "github"
                     :className ""
                     :width "100%"
                     :height "31%"
                    ;  :default-value default-value
                     :value (pretty-edn (:http-xhrio item))
                     :editor-props {"$blockScrolling" js/Infinity}}]
         [:div "body:"]
         [react-ace {:name "editor-item-body"
                     :mode (item->ace-mode-body item)
                     :theme "github"
                     :className ""
                     :width "100%"
                     :height "31%"
                    ;  :default-value default-value
                     :value (str (get-in item [:http-xhrio :body]))
                     :editor-props {"$blockScrolling" js/Infinity}}]
         [:div "response:"]
         [react-ace {:name "editor-item-response"
                     :mode (item->ace-mode-response item)
                     :theme "github"
                     :className ""
                     :width "100%"
                     :height "31%"
                    ;  :default-value default-value
                     :value (pretty-json-str (str (:response item))) 
                     :editor-props {"$blockScrolling" js/Infinity}}]]))))

(defn info-pane-raw-edn
  [opts]
  (let []
    (fn [{:keys [item]}]
      (let []
        [:div {:style {:height "100%" :overflow-y "auto"}} 
         (pretty-edn item)]))))

(defn info-pane
  []
  (let [aitem (rf/subscribe [::subs/selected-item])]
    (fn []
      (let [item @aitem]
        (cond
          (item-http-failure? item) [info-pane-http-failure {:item item}]
          (item-http-success? item) [info-pane-http-success {:item item}]
          :else [info-pane-raw-edn {:item item}]))
      )
    )
  )

(defn panel []
  (let []
    (fn []
      (let []
        [:div {:style {:height "100%" :width "100%" :display "flex"}}
         [:section {:style {:width "47%"}}
          [log-table]]
         [:section {:style {:width "4%"}}]
         [:section {:style {:width "47%"}}
          [info-pane]
          ]]))))

(defn module-actions []
  [])
