(ns ged.evs
  (:require
   [clojure.string :as str]
   [re-frame.core :refer [reg-event-db reg-event-fx inject-cofx path after] :as rf]
   [day8.re-frame.http-fx]
   [ged.db :refer [default-db]]
   #_[vimsical.re-frame.cofx.inject :as inject]
   [ajax.core :as ajax]
   [ajax.edn]
   [day8.re-frame.tracing :refer-macros [fn-traced defn-traced]]
   [ged.core :refer [basic-creds]]
   ["antd/lib/message" :default AntMessage]
   [ged.storage]
   ))


#_(defn my-reg-event-db            ;; a replacement for reg-event-db

   ;; 2-arity with no interceptors 
  ([id handler]
   (my-reg-event-db id nil handler))

   ;; 3-arity with interceptors
  ([id interceptors handler]
   (re-frame.core/reg-event-db   ;; which uses reg-event-db 
    id
    [omni-ceptor interceptors] ;; <-- inject `omni-ceptor`
    handler)))


(rf/reg-event-fx
 ::initialize-db
 [(inject-cofx :storagedb)]
 (fn-traced [{:keys [db storagedb] :as coef} av]
            {:db (merge ged.db/default-db storagedb)}))

(rf/reg-event-db
 ::set-active-panel
 (fn-traced [db [_ active-panel]]
            (assoc db :ged.core/active-panel active-panel)))

(rf/reg-event-fx
 :ant-message
 (fn-traced [{:keys [db]} [_ ea]]
   (let [msg (:msg ea)]
     (.info AntMessage msg 0.5)
     {})))

(rf/reg-event-db
 ::set-re-pressed-example
 (fn-traced [db [_ value]]
   (assoc db :re-pressed-example value)))

(rf/reg-event-db
 ::inc-module-count
 (fn-traced [db [_ active-panel]]
            (let [kw :ged.core/module-count]
              (assoc db kw (inc (kw db))))))

(rf/reg-event-fx
 ::apply-server-settings
 (fn-traced [{:keys [db]} [_ eargs]]
   (let [geoserver-host (:ged.settings/proxy-geoserver-host db)
         proxy-path (:ged.settings/proxy-path db)
         body (str  {:proxy-geoserver-host geoserver-host
                     :proxy-path proxy-path})]
     {:dispatch [:ged.evs/request
                 {:method :post
                  :body body
                  :headers {"Content-Type" "application/json" #_"text/html; charset=utf-8"}
                  ; :path "/geoserver/wfs?exceptions=application/json&outputFormat=application/json"
                  :path "/update-settings"
                  :response-format
                  (ajax/raw-response-format)
                  ; (ajax/json-response-format {:keywords? true})
                  :on-success [::apply-server-settings-res]
                  :on-failure [::apply-server-settings-res]}]
      :db (merge db {})})))

(rf/reg-event-fx
 ::apply-server-settings-res
 (fn-traced [{:keys [db]} [_ eargs]]
   {:dispatch [:ant-message {:msg "settings applied"}]}))

; edn deprecated
; https://github.com/JulianBirch/cljs-ajax/blob/master/docs/formats.md#edn
(rf/reg-event-fx
 ::request
;  [(re-frame/)]
 ;[(rf/inject-cofx ::inject/sub [:entity-request-data])]
 (fn-traced [{:keys [db event] :as ctx} [_ eargs]]
            (let [base-url (get-in db [:ged.core/api :base-url])
                  {:keys [method path on-success on-fail
                          params url-params body headers response-format]} eargs
                  uri (str base-url path)
                  proxy-path (:ged.settings/proxy-path db)
                  geoserver-req? (str/starts-with? uri proxy-path)
                  uname (:ged.core/username db)
                  pass (:ged.core/password db)]
              {:http-xhrio {:method method
                            :uri uri
                  ;  :response-format (ajax.edn/edn-response-format)
                            :response-format (or response-format (ajax/json-response-format {:keywords? true}))
                            #_(ajax/raw-response-format)
                            :on-success on-success
                            :format :edn
                            :body body
                            :headers (merge headers
                                            (when geoserver-req?
                                              {"Authorization" (basic-creds uname pass)}))
                  ;  :params {:data "{:hello 'world}"}
                            :params params
                            :url-params url-params
                            :on-failure on-fail}}
     ;
              )))


(rf/reg-event-fx
 :xhrio-failure
 (fn [{:keys [db]} [_ ea]]
   (js/console.log ":xhrio-failure")
   (js/console.log ea)
   {}))

(rf/reg-event-db
 :http-no-on-failure
 (fn [db [_ ea]]
   (js/console.warn ":http-no-on-failure event "
                    ea)
   db))

(rf/reg-event-db
 :request-res
 (fn-traced [db [_ db-key res]]
            (assoc db db-key res)))


