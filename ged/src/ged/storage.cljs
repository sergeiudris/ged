(ns ged.storage
  (:require [clojure.repl :as repl]
            [re-frame.core :as rf]
            [day8.re-frame.tracing :refer-macros [fn-traced defn-traced]]
            [ged.core :refer [deep-merge]]
            [ged.db]))

(defn ls-key
  []
  "ged-reframe-db")

(defn ^:export read-db
  []
  (some->>
   (.getItem js/localStorage (ls-key))
   (cljs.reader/read-string)))

(defn write-db!
  [v]
  (.setItem js/localStorage (ls-key) (str v)))

(defn ^:export remove-db!
  []
  (.removeItem js/localStorage (ls-key)))

(defn assoc-in-store!
  [path v]
  (let [db (read-db)
        nx (assoc-in db path v)]
    (write-db! nx)))

(defn deep-merge-store!
  [& maps]
  (let [db (read-db)
        nx (apply (partial deep-merge db) maps)]
    (write-db! nx)))

(defn read-profile-db
  [k]
  (let [db (read-db)]
    (get-in db [:profile-dbs k])))

(defn read-profiles
  []
  (let [db (read-db)]
    (get-in db [:profiles])))

(defn read-active-profile-key
  [k]
  (let [db (read-db)]
    (get-in db [:active-profile-key])))

#_(deep-merge-store! {:x 3} {:y 4})
#_(read-db)
#_ (remove-db!)

#_(assoc-in-store! [:s] 3 )

; https://clojuredocs.org/clojure.core/some-%3E%3E

#_(repl/dir rf)


; re-frame events

(def default-active-profile-key 0)

(rf/reg-cofx
 :stored-db
 (fn [{:keys [db] :as cofx} ea]
   (let [stored-db  (read-db)
         apk (or (:active-profile-key stored-db) default-active-profile-key)
         profile-dbs  (:profile-dbs stored-db)
         profiles  (:profiles stored-db)
         profile-db (get-in stored-db [:profile-dbs apk])]
     (assoc cofx :stored-db
            (merge db
                   profile-db
                   {:ged.db.core/profiles profiles
                    :ged.db.core/active-profile-key apk})))))

(rf/reg-event-fx
 :assoc-in-store
 (fn-traced [{:keys [db]} [_ ea]]
            (do
              (let [[path v] ea
                    apk (:ged.db.core/active-profile-key db)
                    combined-path (into [:profile-dbs apk] path)]
                (assoc-in-store! combined-path v)))
            {}))


(rf/reg-event-fx
 ::activate-profile
 (fn-traced
  [{:keys [db]} [_ ea]]
  (let [apk (aget ea "key")
        profile-db (read-profile-db apk)
        profiles (read-profiles)]
    (do (assoc-in-store! [:ged.db.core/active-profile-key] apk))
    {:db (merge
          db
          (or profile-db ged.db/default-db)
          {:ged.db.core/active-profile-key apk
           :ged.db.core/profiles (merge (:ged.db.core/profiles db) profiles)})})))

(rf/reg-event-fx
 ::update-profiles
 (fn-traced
  [{:keys [db]} [_ ea]]
  (let [pfs (deep-merge (:ged.db.core/profiles db) ea)]
    (do
      (assoc-in-store! [:profiles] pfs))
    {:db (update-in db [:ged.db.core/profiles] assoc pfs)
     :dispatch-n (list
                  [:ant-message {:msg "profiles updated" :dur 1}])})))