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

; edn deprecated
; https://github.com/JulianBirch/cljs-ajax/blob/master/docs/formats.md#edn
(rf/reg-event-fx
 ::request
;  [(re-frame/)]
 ;[(rf/inject-cofx ::inject/sub [:entity-request-data])]
 (fn [{:keys [db event] :as ctx} [_ eargs]]
   (let [base-url (get-in db [:ged.core/api :base-url])
         {:keys [method path on-success on-fail 
                 params body]} eargs
         uri (str base-url path)]
     {:http-xhrio {:method method
                   :uri uri
                   :response-format (ajax.edn/edn-response-format)
                   #_(ajax/raw-response-format)
                   :on-success on-success
                   :format :edn
                   :body body
                   :headers {"content-type" "application/edn"}
                  ;  :params {:data "{:hello 'world}"}
                   :params params
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
