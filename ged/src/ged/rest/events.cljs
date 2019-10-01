(ns ged.rest.events
  (:require [re-frame.core :as rf]
            [clojure.repl :as repl]
            [day8.re-frame.tracing :refer-macros [fn-traced defn-traced]]
            [ged.api.geoserver]
            [ged.rest.core :refer [editor-get-val
                                   editor-set-json!
                                   editor-response-set!
                                   editor-request-set!
                                   editor-response-set-json!]]
            [ajax.core :as ajax]
            [clojure.string :as str]
            [clojure.data.xml :as xml]
            ["ol/format/filter" :as olf]))

#_(repl/dir xml)

#_(xml/indent)

(rf/reg-event-db
 ::selected-url
 (fn-traced [db [_ ea]]
            #_(do (editor-response-set-json! ea))
            (assoc db :ged.rest/selected-url ea)))

(rf/reg-event-fx
 ::fetch-selected-url
 (fn-traced [{:keys [db]} [_ eargs]]
   (let [proxy-path (:ged.settings/proxy-path db)
         selected-url (:ged.rest/selected-url db)
         ]
     #_(do (editor-request-set! (prettify-xml body)))
     {:dispatch [:ged.events/request
                 {:method :get
                  :params {}
                  :headers {"Content-Type" "application/json"
                            ; "Authorization"  (ged.api.geoserver/auth-creds)
                            }
                  :path (str proxy-path selected-url)
                  :response-format
                  (ajax/json-response-format {:keywords? true})
                  #_(ajax/raw-response-format)
                  :on-success [::fetch-selected-url-res]
                  :on-fail [::fetch-selected-url-res]}]
      :db (merge db {})})))

(rf/reg-event-db
 ::fetch-selected-url-res
 (fn-traced [db [_ ea]]
            #_(do (editor-response-set-json! ea))
            (assoc db :ged.rest/fetch-selected-url-res ea)))

(rf/reg-event-fx
 ::tx-feature
 (fn-traced [{:keys [db]} [_ eargs]]
   (let [tx-type (:tx-type eargs)
         proxy-path (:ged.settings/proxy-path db)
         vl (js/JSON.parse (editor-get-val))
         path (:ged.rest/selected-item-path db)
         body (js/JSON.stringify vl)]
     #_(do (editor-request-set! (prettify-xml body)))
     {:dispatch [:ged.events/request
                 {:method tx-type
                  :body body
                  :headers {"Content-Type" "application/json"
                            ; "Authorization"  (ged.api.geoserver/auth-creds)
                            }
                  ; :path "/geoserver/wfs?exceptions=application/json&outputFormat=application/json"
                  :path (str proxy-path path)
                  :response-format
                  #_(ajax/raw-response-format)
                  (ajax/json-response-format {:keywords? true})
                  :on-success [::tx-res]
                  :on-fail [::tx-res]}]
      :db (merge db {})})))

(rf/reg-event-fx
 ::tx-res
 (fn-traced [{:keys [db]} [_ id ea]]
   (do (editor-response-set-json! ea))
   {:db (assoc db :ged.rest/tx-res ea)}))





(rf/reg-event-db
 ::search-input
 (fn-traced [db [_ eargs]]
            (let [key :ged.rest/search-input
                  value eargs]
              (assoc db key value))))

(rf/reg-event-fx
 ::search-table-mdata
 (fn-traced [{:keys [db]} [_ eargs]]
   (let [key :ged.rest/search-table-mdata]
     {:dispatch [:ged.rest.events/search {}]
      :db (assoc db key eargs)})))

(defn href->path
  [href]
  (-> (str/split href "/geoserver") (second)))

(rf/reg-event-fx
 ::select-feature
 (fn-traced [{:keys [db]} [_ ea]]
   (let [href (:href (js->clj ea :keywordize-keys true))]
     (if href
       (let [path (href->path href)
             proxy-path (:ged.settings/proxy-path db)]
         {:dispatch [:ged.events/request
                     {:method :get
                      :headers {"Content-Type" "application/json"
                            ; "Authorization"  (ged.api.geoserver/auth-creds)
                                }
                      :path (str proxy-path path)
                      :response-format
                      #_(ajax/raw-response-format)
                      (ajax/json-response-format {:keywords? true})
                      :on-success [::select-feature-succ]
                      :on-fail [::select-feature-fail]}]
          :db (merge db {:ged.rest/selected-item-href href
                         :ged.rest/selected-item-path path})})
       (do
         (do (editor-set-json! ea))
         {:db (assoc db :ged.rest/select-feature ea)}))
     )))

(rf/reg-event-fx
 ::select-feature-succ
 (fn-traced [{:keys [db]} [_ ea]]
   (do (editor-set-json! (clj->js ea)))
   {:db db}))

(rf/reg-event-db
 ::select-feature-fail
 (fn-traced [db [_ ea]]
   (do (editor-response-set-json! (clj->js ea)))
   db))

#_(str/split 
   "http://localhost:8800/geoserver/rest/workspaces/dev/featuretypes/ne_10m_admin_0_countries.json"
   "/geoserver"
   )

