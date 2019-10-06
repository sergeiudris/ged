(ns ged.rest.evs
  (:require [clojure.repl :as repl]
            [re-frame.core :as rf]
            [day8.re-frame.tracing :refer-macros [fn-traced defn-traced]]
            [ajax.core :as ajax]
            [clojure.string :as str]))

#_(repl/dir xml)

#_(xml/indent)

(rf/reg-event-db
 ::selected-url
 (fn-traced [db [_ ea]]
            (assoc db :ged.db.rest/selected-url ea)))

(rf/reg-event-fx
 ::fetch-selected-url
 (fn-traced [{:keys [db]} [_ ea]]
            (let [selected-url (:ged.db.rest/selected-url db)]
              {:dispatch [:ged.evs/request
                          {:method :get
                           :params {}
                           :headers {"Content-Type" "application/json"
                            ; "Authorization"  (ged.api.geoserver/auth-creds)
                                     }
                           :path (str "/geoserver" selected-url)
                           :response-format
                           (ajax/json-response-format {:keywords? true})
                           #_(ajax/raw-response-format)
                           :on-success [::fetch-selected-url-res]
                           :on-failure [::fetch-selected-url-res]}]
               :db (merge db {})})))

(rf/reg-event-db
 ::fetch-selected-url-res
 (fn-traced [db [_ ea]]
            (assoc db :ged.db.rest/fetch-selected-url-res ea)))

(rf/reg-event-fx
 ::tx-feature
 [(rf/inject-cofx :ged.rest.core/get-editor-val [:data])]
 (fn-traced [{:keys [db get-editor-val]} [_ ea]]
            (let [tx-type (:tx-type ea)
                  v (js/JSON.parse get-editor-val)
                  path (:ged.db.rest/selected-item-path db)
                  body (js/JSON.stringify v)]
              {:dispatch [:ged.evs/request
                          {:method tx-type
                           :body body
                           :headers {"Content-Type" "application/json"
                            ; "Authorization"  (ged.api.geoserver/auth-creds)
                                     }
                  ; :path "/geoserver/wfs?exceptions=application/json&outputFormat=application/json"
                           :path (str "/geoserver" path)
                           :response-format
                           #_(ajax/raw-response-format)
                           (ajax/json-response-format {:keywords? true})
                           :on-success [::tx-res]
                           :on-failure [::tx-res]}]
               :db (merge db {})})))

(rf/reg-event-fx
 ::tx-res
 (fn-traced [{:keys [db]} [_ id ea]]
            {:db (assoc db :ged.db.rest/tx-res ea)
             :dispatch [:ged.rest.core/set-editor-json [:response ea]]}))


(rf/reg-event-db
 ::search-input
 (fn-traced [db [_ ea]]
            (let [key :ged.db.rest/search-input
                  value ea]
              (assoc db key value))))

(rf/reg-event-fx
 ::search-table-mdata
 (fn-traced [{:keys [db]} [_ ea]]
            (let [key :ged.db.rest/search-table-mdata]
              {:dispatch-n (list nil [:ged.rest.events/search {}])
               :db (assoc db key ea)})))

(defn href->path
  [href]
  (-> (str/split href "/geoserver") (second)))

(rf/reg-event-fx
 ::select-feature
 (fn-traced [{:keys [db]} [_ ea]]
   (let [href (:href (js->clj ea :keywordize-keys true))]
     (if href
       (let [path (href->path href)]
         {:dispatch [:ged.evs/request
                     {:method :get
                      :headers {"Content-Type" "application/json"
                            ; "Authorization"  (ged.api.geoserver/auth-creds)
                                }
                      :path (str "/geoserver" path)
                      :response-format
                      #_(ajax/raw-response-format)
                      (ajax/json-response-format {:keywords? true})
                      :on-success [::select-feature-succ]
                      :on-failure [::select-feature-fail]}]
          :db (merge db {:ged.db.rest/selected-item-href href
                         :ged.db.rest/selected-item-path path})})
       {:dispatch [:ged.rest.core/set-editor-json [:data ea]]
        :db (assoc db :ged.db.rest/select-feature ea)})
     )))

(rf/reg-event-fx
 ::select-feature-succ
 (fn-traced [{:keys [db]} [_ ea]]
            {:dispatch [:ged.rest.core/set-editor-json [:data (clj->js ea)]]}))

(rf/reg-event-fx
 ::select-feature-fail
 (fn-traced [{:keys [db]} [_ ea]]
            {:dispatch [:ged.rest.core/set-editor-json [:response (clj->js ea)]]}))

#_(str/split 
   "http://localhost:8800/geoserver/rest/workspaces/dev/featuretypes/ne_10m_admin_0_countries.json"
   "/geoserver"
   )

