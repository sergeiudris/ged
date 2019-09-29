(ns ged.core
  (:require [cljs.repl :as repl]
            [cljs.pprint :as pp]
            [clojure.string :as str]
            [reagent.core :as r]
            [re-frame.core :as rf]
            [re-pressed.core :as rp]
            [ged.events :as events]
            [ged.routes :as routes]
            [ged.config :as config]
            [ged.subs :as subs]
            [ged.view]
            [tools.core]
            [ged.api.geoserver]
            [ged.api.core]))

#_(sample.lib.logic/example)

#_(repl/dir rf)

#_(re-frame.loggers/get-loggers)

(defn dev-setup []
  (when config/debug?
    (println "dev mode" config/debug?)))

(defn mount-root []
  (rf/clear-subscription-cache!)
  (r/render [ged.view/ui]
            (.getElementById js/document "app")))

(defn ^:export main []
  (routes/app-routes)
  (rf/dispatch-sync [::events/initialize-db])
  (rf/dispatch-sync [::rp/add-keyboard-event-listener "keydown"])
  (dev-setup)
  (mount-root))

(defn ^:dev/after-load after-load []
  #_(js/console.log "--after load")
  (mount-root))

; (defonce _ (main))

(def clog (.-log js/console))

(comment

  (ns sniff.core)

  (repl/dir cljs.repl)

  (clog 3)

  (->
   (js/fetch "http://localhost:7881/attrs")
   (.then (fn [r]
            (.json r)))
   (.then (fn [r]
            ; (clog r)
            (prn r))))

  (pp/pprint 3)

  ;
  )