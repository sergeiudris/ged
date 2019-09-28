(ns ged.view
  (:use-macros [cljs.core.async.macros :only [go]])
  (:require [cljs.repl :as repl]
            [cljs.pprint :as pp]
            [reagent.core :as r]
            [re-frame.core :as rf]
            [shadow.loader :as loader]
            [ged.subs :as subs]
            [ged.home.view]
            [cljs.core.async :refer [<! timeout]]
            [ged.config :as config]
            [clojure.string]
            [ged.routes]
            [ged.layout :as layout]
   ))

(defn fn-to-call-on-load []
  (js/console.log "module loaded"))

(defn fn-to-call-on-error []
  (js/console.log "module load failed"))


(defn not-found-panel []
  [:div
   [:span "not found :("]])

; @(resolve 'clojure.repl/dir)  wrong , macro
; ((resolve 'clojure.core/first) [1 2]) works

(defn- resolve-module
  [module-name]
  (case module-name
    "home" {:panel [(resolve 'ged.home.view/home-panel)]
            :actions nil}
    "settings" {:panel [(resolve 'ged.settings.view/settings-panel)]}
    [:div (str "no panel for module: " module-name)]))

(defn module->panel
  [module-name]
  (->
   (resolve-module module-name)
   (:panel)))

(defn module->actions
  [module-name]
  (or (->
       (resolve-module module-name)
       (:actions)) (fn [] nil)))

(defn panel->module-name
  [panel-name]
  (if panel-name
    (-> (name panel-name) (clojure.string/split #"-") first)
    nil))

(defn panel-defered
  [module-name]
  (let [comp-state (r/atom {})]
    (fn [module-name]
      (let [panel (@comp-state module-name)]
        ; (prn module-name)
        ; (prn panel)
        (cond
          (loader/loaded? module-name) (module->panel module-name)
          panel [panel]
          :else
          (do
            (go
              (<! (timeout (if config/debug? 100 0)))
              (-> (loader/load module-name)
                  (.then
                   (fn []
                     (rf/dispatch [:ged.events/inc-module-count])
                     (swap! comp-state update-in [module-name] (module->panel module-name)))
                   (fn [] (js/console.log (str "module load failed: " module-name))))))
            #_[:div "nothing"]
            [:div "loading..."])
          ;
          )))))

#_(defn- panels [panel-name]
    (case panel-name
      :count-panel [count-panel]
      :query-panel [query-panel]
      :cred-panel
      #_[cred-panel-defered "cred"] ; 506kb main.js 123kb cred.js
      [cred-panel] ; 624kb
      :entity-panel [entity-panel]
      :timeline-panel [timeline-panel]
      [not-found-panel]))

(defn- panels [panel-name]
  (case panel-name
    :home-panel [ged.home.view/home-panel]
    :settings-panel [panel-defered "settings"]
    [:div (str "no panel: " panel-name)]))


(defn show-panel [panel-name]
  [panels panel-name])

(defn main-panel []
  (let [active-panel (rf/subscribe [::subs/active-panel])]
    [show-panel @active-panel]))

(defn act-panel-con
  []
  (let [active-panel (rf/subscribe [::subs/active-panel])
        module-count @(rf/subscribe [::subs/module-count]) ; triggers render
        module-name (panel->module-name @active-panel)
        actions-fn (module->actions module-name)
        ]
    #_(prn "act-panel-con: " @active-panel)
    #_(prn "act-panel-con: " @module-count)
    #_(prn (actions-fn))
    #_[act-panel {:module-actions  (actions-fn) }]
    )
  )
; (keyword (str (name (:handler matched-route)) "-panel"))



(defn ui
  []
  [layout/layout
   [layout/menu
    {:on-select (fn [eargs]
                  (let [eargs-clj (js->clj eargs :keywordize-keys true)
                        {:keys [key]} eargs-clj]
                    (ged.routes/set-path! (str "/" (panel->module-name (keyword key))))
                    #_(rf/dispatch [:ged.events/set-active-panel (keyword key)])))}]
   [main-panel]
   ]
  #_[main-panel])