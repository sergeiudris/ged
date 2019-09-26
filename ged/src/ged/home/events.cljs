(ns ged.home.events
  (:require [re-frame.core :as rf]
            [day8.re-frame.tracing :refer-macros [fn-traced defn-traced]])
  )

; headers.set ('Authorization', 'Basic '+ Buffer.from (username + ":" + password) .toString ('base64'));
;window.btoa (username + ':' + password)


#_(js/fetch "http://localhost:8600/geoserver/rest/layers.json"
            (clj->js {"method" "GET"
             "headers" {
                        "Authorization" 
                        (str "Basic " (js/btoa (str "admin" ":" "myawesomegeoserver")))
                  ;
                       }}) )

(rf/reg-event-db
 ::inc-module-count
 (fn-traced [db [_ active-panel]]
            (let [kw :ged.core/module-count]
              (assoc db kw (inc (kw db))))
            ))