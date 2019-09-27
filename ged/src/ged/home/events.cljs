(ns ged.home.events
  (:require [re-frame.core :as rf]
            [day8.re-frame.tracing :refer-macros [fn-traced defn-traced]])
  )

; curl -u admin:myawesomegeoserver -XGET http://localhost:8801/geoserver/rest/layers.json

; headers.set ('Authorization', 'Basic '+ Buffer.from (username + ":" + password) .toString ('base64'));
;window.btoa (username + ':' + password)

#_"http://localhost:8600/geoserver/rest/layers.json"
#_"http://localhost:8801/geoserver/rest/layers.json"
#_"http://localhost:8080/v1"
#_"http://localhost:8801"
#_"http://localhost:8600/geoserver/web/"
#_"http://localhost:8600/geoserver/web/wicket/bookmarkable/org.geoserver.web.demo.MapPreviewPage?2"
#_(->
   (js/fetch "http://localhost:8801"
             (clj->js {"method" "GET"
                      ; "headers" {"Authorization"
                      ;            (str "Basic " (js/btoa (str "admin" ":" "myawesomegeoserver")))
                      ;            }
                       }))
   (.then (fn [res] (.text res) ))
   (.then (fn [r] (js/console.log r)))
   
   )

(rf/reg-event-db
 ::inc-module-count
 (fn-traced [db [_ active-panel]]
            (let [kw :ged.core/module-count]
              (assoc db kw (inc (kw db))))
            ))