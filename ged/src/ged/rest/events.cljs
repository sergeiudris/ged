(ns ged.rest.events
  (:require [re-frame.core :as rf]
            [clojure.repl :as repl]
            [day8.re-frame.tracing :refer-macros [fn-traced defn-traced]]
            [ged.api.geoserver]
            [ged.rest.core :refer [editor-get-val
                                   editor-set-json!
                                   editor-response-set!
                                   editor-request-set!
                                   ]]
            [ajax.core :as ajax]
            [clojure.string :as str]
            [clojure.data.xml :as xml]
            ["ol/format/filter" :as olf]))

#_(repl/dir xml)

#_(xml/indent)

(rf/reg-event-fx
 ::search
 (fn [{:keys [db]} [_ eargs]]
   (let [ftype-input (:ged.rest/feature-type-input db)
         [fpref ftype] (try (str/split ftype-input \:)
                            (catch js/Error e
                              (do (js/console.warn e)
                                  ["undefined:undefined"])))
         input (:ged.rest/search-input db)
         s (or (:input eargs) input)
         table-mdata (:ged.rest/search-table-mdata db)
         total (get-in db [:ged.rest/search-res :total])
         pag (:pagination table-mdata)
         proxy-path (:ged.settings/proxy-path db)
         {:keys [current pageSize]} pag
         limit (or pageSize 10)
         offset (or (* pageSize (dec current)) 0)
         body (ged.api.geoserver/wfs-get-features-body-str
               (merge
                {:offset offset
                 :limit limit
                 :featurePrefix fpref
                 :featureTypes [ftype]}
                (when (not-empty s)
                  {:filter (olf/like "NAME" (str "*" s "*") "*" "." "!" false)})))]
     #_(do (editor-request-set! (prettify-xml body)))
     {:dispatch [:ged.events/request
                 {:method :post
                  :params {}
                  :body body
                  :headers {"Content-Type" "application/json"
                            ; "Authorization"  (ged.api.geoserver/auth-creds)
                            }
                  :path (str proxy-path "/wfs")
                  :response-format (ajax/json-response-format {:keywords? true})
                  :on-success [::search-res]
                  :on-fail [::search-res]}]
      :db (merge db {:ged.rest/search-input s
                     :ged.rest/search-table-mdata
                     (if (:input eargs)
                       (merge table-mdata {:pagination (merge pag {:current 1})})
                       table-mdata)})})))

(rf/reg-event-db
 ::search-res
 (fn-traced [db [_ val]]
            (assoc db :ged.rest/search-res val)))

(rf/reg-event-fx
 ::tx-feature
 (fn [{:keys [db]} [_ eargs]]
   (let [ftype-input (:ged.rest/feature-type-input db)
         [fpref ftype] (try (str/split ftype-input \:)
                            (catch js/Error e
                              (do (js/console.warn e)
                                  ["undefined:undefined"])))
         fns (:ged.rest/feature-ns db)
         tx-type (:tx-type eargs)
         proxy-path (:ged.settings/proxy-path db)
         vl (js/JSON.parse (editor-get-val))
         body (ged.api.geoserver/wfs-tx-jsons-str
               {tx-type [vl]
                :featureNS fns
                :featurePrefix fpref
                :featureType ftype})]
     #_(do (editor-request-set! (prettify-xml body)))
     {:dispatch [:ged.events/request
                 {:method :post
                  :body body
                  :headers {"Content-Type" "application/json"
                            ; "Authorization"  (ged.api.geoserver/auth-creds)
                            }
                  ; :path "/geoserver/wfs?exceptions=application/json&outputFormat=application/json"
                  :path (str proxy-path "/wfs")
                  :response-format
                  (ajax/raw-response-format)
                  ; (ajax/json-response-format {:keywords? true})
                  :on-success [::tx-res-succ (str fpref ":" ftype)]
                  :on-fail [::tx-res-fail]}]
      :db (merge db {})})))

(rf/reg-event-fx
 ::tx-res-succ
 (fn [{:keys [db]} [_ id ea]]
   #_(do (editor-response-set! (prettify-xml ea)))
   {:dispatch [:ged.map.events/refetch-wms-layer id]
    :db (assoc db :ged.rest/tx-res ea)}))

(rf/reg-event-db
 ::tx-res-fail
 (fn [db [_ eargs]]
   #_(do (editor-response-set! (prettify-xml eargs)))
   (assoc db :ged.rest/tx-res eargs)))




(rf/reg-event-db
 ::search-input
 (fn-traced [db [_ eargs]]
            (let [key :ged.rest/search-input
                  value eargs]
              (assoc db key value))))

(rf/reg-event-fx
 ::search-table-mdata
 (fn [{:keys [db]} [_ eargs]]
   (let [key :ged.rest/search-table-mdata]
     {:dispatch [:ged.rest.events/search {}]
      :db (assoc db key eargs)})))

(rf/reg-event-fx
 ::select-feature
 (fn [{:keys [db]} [_ eargs]]
   (let [key :ged.rest/select-feature]
     (do (editor-set-json! eargs))
     {:db (assoc db key eargs)})))



