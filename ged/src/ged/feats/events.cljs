(ns ged.feats.events
  (:require [re-frame.core :as rf]
            [clojure.repl :as repl]
            [day8.re-frame.tracing :refer-macros [fn-traced defn-traced]]
            [ged.api.geoserver]
            [ged.feats.core :refer [editor-get-val
                                    editor-set-json!
                                    editor-response-set!
                                    editor-request-set!
                                    prettify-xml]]
            [ajax.core :as ajax]
            [clojure.string :as str]
            [clojure.data.xml :as xml]
            ["ol/format/filter" :as olf]
            ))

#_(repl/dir xml)

#_(xml/indent)

(rf/reg-event-fx
 ::search
 (fn-traced [{:keys [db]} [_ ea]]
   (let [ftype-input (:ged.feats/feature-type-input db)
         [fpref ftype] (try (str/split ftype-input \:)
                            (catch js/Error e
                              (do (js/console.warn e)
                                  ["undefined:undefined"])))
         input (:ged.feats/search-input db)
         s (or (:input ea) input)
         table-mdata (:ged.feats/search-table-mdata db)
         total (get-in db [:ged.feats/search-res :total])
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
     (do (editor-request-set! (prettify-xml body)))
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
      :db (merge db {:ged.feats/search-input s
                     :ged.feats/search-table-mdata
                     (if (:input ea)
                       (merge table-mdata {:pagination (merge pag {:current 1})})
                       table-mdata)})})))

(rf/reg-event-db
 ::search-res
 (fn-traced [db [_ ea]]
            (assoc db :ged.feats/search-res ea)))

(rf/reg-event-fx
 ::tx-feature
 (fn-traced [{:keys [db]} [_ ea]]
   (let [ftype-input (:ged.feats/feature-type-input db)
         [fpref ftype] (try (str/split ftype-input \:)
                            (catch js/Error e
                              (do (js/console.warn e)
                                  ["undefined:undefined"])))
         fns (:ged.feats/feature-ns db)
         tx-type (:tx-type ea)
         proxy-path (:ged.settings/proxy-path db)
         v (js/JSON.parse (editor-get-val))
         body (ged.api.geoserver/wfs-tx-jsons-str
               {tx-type [v]
                :featureNS fns
                :featurePrefix fpref
                :featureType ftype})]
     (do (editor-request-set! (prettify-xml body)))
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
 (fn-traced [{:keys [db]} [_ id ea]]
   (do (editor-response-set! (prettify-xml ea) ))
   {:dispatch [:ged.map.events/refetch-wms-layer id]
    :db (assoc db :ged.feats/tx-res ea)}
   ))

(rf/reg-event-db
 ::tx-res-fail
 (fn-traced [db [_ ea]]
   (do (editor-response-set! (prettify-xml ea)))
   (assoc db :ged.feats/tx-res ea)))




(rf/reg-event-db
 ::search-input
 (fn-traced [db [_ ea]]
            (let [key :ged.feats/search-input
                  value ea]
              (assoc db key value))))

(rf/reg-event-fx
 ::search-table-mdata
 (fn-traced [{:keys [db]} [_ ea]]
   (let [key :ged.feats/search-table-mdata]
     {:dispatch [:ged.feats.events/search {}]
      :db (assoc db key ea)})))

(rf/reg-event-fx
 ::select-feature
 (fn-traced [{:keys [db]} [_ ea]]
   (let [key :ged.feats/select-feature]
     (do (editor-set-json! ea))
     {:db (assoc db key ea)})))

(rf/reg-event-fx
 ::feature-type-input
 (fn-traced [{:keys [db]} [_ ea]]
   (let [key :ged.feats/feature-type-input]
     {:db (assoc db key ea)})))

(rf/reg-event-fx
 ::feature-ns
 (fn-traced [{:keys [db]} [_ ea]]
   (let [key :ged.feats/feature-ns]
     {:db (assoc db key ea)})))

