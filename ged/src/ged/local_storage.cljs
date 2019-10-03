(ns ged.local-storage
  (:require [clojure.repl :as repl]
            [re-frame.core :as rf]))



(defn deep-merge [a & maps]
  (if (map? a)
    (apply merge-with deep-merge a maps)
    (apply merge-with deep-merge maps)))

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

#_(deep-merge-store! {:x 3} {:y 4})
#_(read-db)
#_ (remove-db!)

#_(assoc-in-store! [:s] 3 )

; https://clojuredocs.org/clojure.core/some-%3E%3E



#_(repl/dir rf)
