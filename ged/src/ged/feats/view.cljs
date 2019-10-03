(ns ged.feats.view
  (:require  [reagent.core :as r]
             [cljs.repl :as repl]
             [cljs.pprint :as pp]
             [re-frame.core :as rf]
             [ged.feats.subs :as subs]
             [ged.feats.evs :as evs]
             [ged.feats.core]

             ["react-ace/lib/index.js" :default ReactAce]
             ["brace" :as brace]
             ["brace/mode/clojure.js"]
             ["brace/mode/graphqlschema.js"]
             ["brace/mode/json.js"]
             ["brace/mode/xml.js"]
             ["brace/theme/github.js"]

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

(def react-ace (r/adapt-react-class ReactAce))

(defn editor-feature
  []
  (let [default-value ""
        av (r/atom default-value)]
    (fn []
      [react-ace {:name "editor-feature"
                  :mode "json"
                  :theme "github"
                  :className "editor-feature"
                  :width "32vw"
                    ;  :default-value default-value
                  :value @av
                  :on-load (fn [ref]
                             (rf/dispatch [:ged.feats.core/set-editor-preserve [:data ref]]))
                  :on-change (fn [v ev] (reset! av v))
                  :editor-props {"$blockScrolling" js/Infinity}}])))

(defn editor-response
  []
  (let [default-value ""
        av (r/atom default-value)]
    (fn []
      [react-ace {:name "editor-response"
                  :mode "xml"
                  :theme "github"
                  :className "editor-response"
                  :width "32vw"
                    ;  :default-value default-value
                  :value @av
                  :on-load (fn [ref]
                             (rf/dispatch [:ged.feats.core/set-editor-preserve [:response ref]]))
                  :on-change (fn [v ev] (reset! av v))
                  :editor-props {"$blockScrolling" js/Infinity}}])))

(defn editor-request
  []
  (let [default-value ""
        av (r/atom default-value)]
    (fn []
      [react-ace {:name "editor-request"
                  :mode "xml"
                  :theme "github"
                  :className "editor-request"
                  :width "32vw"
                    ;  :default-value default-value
                  :value @av
                  :on-load (fn [ref]
                             (rf/dispatch [:ged.feats.core/set-editor-preserve [:request ref]]))
                  :on-change (fn [v ev] (reset! av v))
                  :editor-props {"$blockScrolling" js/Infinity}}])))

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
                    (rf/dispatch [::evs/search {:input s}]))
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
               [::subs/feature-type-input])]
    (fn []
      [ant-input {:style {:width "16%" :margin "0 0 0 8px"}
                  :value @sref
                  :on-change
                  (fn [ev]
                    (rf/dispatch
                     [::evs/feature-type-input
                      (.. ev -target -value)]))
                  :placeholder "topp:states"}])))

(defn feature-ns
  []
  (let [sref (rf/subscribe
              [::subs/feature-ns])]
    (fn []
      [ant-input {:style {:width "16%" :margin "0 0 0 8px"}
                  :value @sref
                  :on-change
                  (fn [ev]
                    (rf/dispatch
                     [::evs/feature-ns
                      (.. ev -target -value)]))
                  :placeholder "http://www.opengis.net/wfs/dev"}])))

(def feature-columns
  [{:title "id"
    :key "id"
    :dataIndex "id"}
   {:title "preview"
    :key "preview"
    :width "70%"
    :render 
    (fn [txt rec idx]
      (r/as-element
       [:div {:style  {:white-space "nowrap"
                       :max-width "60vw"
                       :overflow-x "hidden"}}
        (js/JSON.stringify (aget rec "properties")  )]))
    }])

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
                              [::evs/select-feature
                               rec])
                  }
                 "select"]]))}
   #_{:title ""
      :key "empty"}])

(def columns (vec (concat feature-columns extra-columns)))

(defn table
  []
  (let [search-res (rf/subscribe [::subs/search-res])
        table-mdata (rf/subscribe [::subs/search-table-mdata])]
    (fn []
      (let [items (:features @search-res)
            total (:totalFeatures @search-res)
            ents items
            #_(mapv #(-> % :entity (dissoc :db/id)) items)
            pagination (:pagination @table-mdata)]
        [ant-table {:show-header true
                    :size "small"
                    :row-key :id
                    :className "feats-table"
                    :columns columns
                    :dataSource ents
                    :on-change (fn [pag fil sor ext]
                                 (rf/dispatch [::evs/search-table-mdata
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
  #_(rf/dispatch [::evs/nutrients])
  #_(rf/dispatch [::evs/nhi-dri])
  (let []
    (fn []
      [:section
       #_[search]
       [:div
        [auto-complete {}]
        [feature-type-input]
        [feature-ns]
        ]
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
        [ant-button {:on-click 
                     #(rf/dispatch [::evs/tx-feature {:tx-type :inserts}])
                     :style {:width "96px"}}
         "insert"]
        [ant-button {:on-click 
                     #(rf/dispatch [::evs/tx-feature {:tx-type :updates}])
                     :style {:width "96px"}}
         "update"]
        [ant-button {:on-click
                     #(rf/dispatch [::evs/tx-feature {:tx-type :deletes}])
                     :style {:width "96px"}}
         "delete"]]
       [:br]
       

       #_[ged.feats.sample/sample-table]])))

