(ns ged.events
  (:require
   [re-frame.core :as rf]
   [day8.re-frame.http-fx]
   [ged.db ]
   #_[vimsical.re-frame.cofx.inject :as inject]
   [ajax.core :as ajax]
   [ajax.edn]
   [day8.re-frame.tracing :refer-macros [fn-traced defn-traced]]))

(rf/reg-event-db
 ::initialize-db
 (fn-traced [_ _]
            ged.db/default-db))

(rf/reg-event-db
 ::set-active-panel
 (fn-traced [db [_ active-panel]]
            (assoc db :ged.core/active-panel active-panel)))

(rf/reg-event-db
 ::set-re-pressed-example
 (fn [db [_ value]]
   (assoc db :re-pressed-example value)))

(rf/reg-event-db
 ::inc-module-count
 (fn-traced [db [_ active-panel]]
            (let [kw :ged.core/module-count]
              (assoc db kw (inc (kw db))))))

(rf/reg-event-fx
 ::apply-server-settings
 (fn [{:keys [db]} [_ eargs]]
   (let [geoserver-host (:ged.settings/geoserver-host db)
         proxy-path (:ged.settings/proxy-path db)
         body (str  {:geoserver-host geoserver-host
                     :proxy-path proxy-path})]
     {:dispatch [:ged.events/request
                 {:method :post
                  :body body
                  :headers {"Content-Type" "text/html; charset=utf-8"}
                  ; :path "/geoserver/wfs?exceptions=application/json&outputFormat=application/json"
                  :path "/update-settings"
                  :response-format
                  (ajax/raw-response-format)
                  ; (ajax/json-response-format {:keywords? true})
                  :on-success [::apply-server-settings-res]
                  :on-fail [::apply-server-settings-res]}]
      :db (merge db {})})))

(rf/reg-event-db
 :apply-server-settings-res
 (fn-traced [db [_ eargs]]
            (js/console.log eargs)
            db))

; edn deprecated
; https://github.com/JulianBirch/cljs-ajax/blob/master/docs/formats.md#edn
(rf/reg-event-fx
 ::request
;  [(re-frame/)]
 ;[(rf/inject-cofx ::inject/sub [:entity-request-data])]
 (fn [{:keys [db event] :as ctx} [_ eargs]]
   (let [base-url (get-in db [:ged.core/api :base-url])
         {:keys [method path on-success on-fail 
                 params url-params body headers response-format]} eargs
         uri (str base-url path)]
     {:http-xhrio {:method method
                   :uri uri
                  ;  :response-format (ajax.edn/edn-response-format)
                   :response-format (or response-format (ajax/json-response-format {:keywords? true})) 
                   #_(ajax/raw-response-format)
                   :on-success on-success
                   :format :edn
                   :body body
                   :headers headers
                  ;  :params {:data "{:hello 'world}"}
                   :params params
                   :url-params url-params
                   :on-fail on-fail}
      :db db}
     ;
     )))



(rf/reg-event-db
 :http-no-on-failure
 (fn-traced [db [_ eargs]]
            (js/console.warn ":http-no-on-failure event "
                             eargs)
            db))

(rf/reg-event-db
 :request-res
 (fn-traced [db [_ db-key res]]
            (assoc db db-key res)))
