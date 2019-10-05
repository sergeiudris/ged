(ns ged.http
  (:require [clojure.string :as str]
            [re-frame.core :as rf]
            [ged.core :refer [deep-merge]]
            [day8.re-frame.tracing :refer-macros [fn-traced defn-traced]]))

; https://developer.mozilla.org/en-US/docs/Web/API/AbortController
; https://developer.mozilla.org/en-US/docs/Web/API/Fetch_API


(defn f?
  [ctx v]
  (if (fn? v) (v ctx) v))

(defn merge-with-rules
  [ctx rules & maps]
  (when (some identity maps)
    (let [merge-entry (fn [m e]

                        (let [k (key e)
                              v (val e)
                              f (get rules k)]
                          (if (contains? m k)
                            (assoc m k (f ctx [k (get m k) v] ))
                            (assoc m k v))))
          merge2 (fn [m1 m2]
                   (reduce merge-entry (or m1 {}) (seq m2)))]
      (reduce merge2 maps))))

#_(let [rules {:url (fn furl [ctx [k a b]]
                      (js/console.log ctx)
                      (str (f? ctx a) (f? ctx b)))}
        m1 {:url (fn [] "/geoserver")}
        m2 {:url "/wfs"}]
    (merge-with-rules {:db {}} rules m1 m2))

#_(fn? :url)
#_(fn? (fn [] "" ))
#_(fn? "")

#_(merge-with (fn [a b] (js/console.log a b)) {:a 1} {:a 2}  )
#_(reduce (fn [a v] (js/console.log v) a ) {} (seq {:a 1}) )
#_(apply + (list 1 2))


(def http-conf
  {:merge-rules {:url (fn [a b] (str (f? a) (f? b)))}
   :profiles {:json {:default? true
                     :headers {"Content-Type" "application/json"}}
              :proxy-path
              {:url (fn [{:keys [db]}] (:ged.settings/proxy-path db))}

              :wfs-get-feature
              {:profiles [:json :proxy-path]
               :url "/wfs"
               :on-success [:ged.log.evs/http-on-success]
               :on-failure [:ged.log.evs/http-on-failure]}
              :geoserver-rest {}}}
  )


(defn merge-profiles
  [pf & pfs]
  (apply merge-with deep-merge pf pfs))

(rf/reg-cofx
 :http-conf
 (fn [cofx ea]
   (assoc cofx :http-profiles http-conf)))

(defn combined-profile
  [rq profiles]
  (->> (:profiles rq) (map #(% profiles)) (apply merge-profiles)))

(rf/reg-event-fx
 :http
 [(rf/inject-cofx :http-conf)]
 (fn-traced
  [{:keys [db http-conf] :as cofx} [_ ea]]
  (let [pf (combined-profile ea (:http-profiles http-conf))]
    (js/console.log ea)
    (js/console.log http-conf)
    #_(do
        (->
         (js/fetch (:url ea)
                   (:init ea))
         (.catch (fn [e]
                   (rf/dispatch (:on-failure ea))))
         (.then (fn [r]
                  (rf/dispatch (:on-success ea))))))
    {})))
