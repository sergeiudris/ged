
(ns srv.dev-http
  (:require [clojure.java.io :as io]
            [clojure.pprint :as pp]
            [clojure.string :as str]
            #_[srv.server]))

(def not-found
  {:status 404
   :headers {"content-type" "text/plain"}
   :body "Not found."})

(def rqs (atom nil))

#_(do (pp/pprint @rqs))

#_(prn "---dev-http")

#_(srv.server/run-dev)



(defn handle [{:keys [uri http-roots http-config] :as req}]
  (reset! rqs req)
  (prn uri)
  (cond 
    (= uri "/api") 
    {:status 200
     :headers (get http-config :push-state/headers {"content-type" "text/html; charset=utf-8"})
     :body "hello"}
    :else 
    (let [accept (get-in req [:headers "accept"])]
      (if (and accept (not (str/includes? accept "text/html")))
        not-found
        (let [index-name
              (get http-config :push-state/index "index.html")

              headers
              (get http-config :push-state/headers {"content-type" "text/html; charset=utf-8"})

              index-file
              (reduce
               (fn [_ http-root]
                 (let [file (io/file http-root index-name)]
                   (when (and file (.exists file))
                     (reduced file))))
               nil
               http-roots)]

          (if-not index-file
          ;; FIXME: serve some kind of default page instead
            (assoc not-found :body "Not found. Missing index.html.")
            {:status 200
             :headers headers
             :body (slurp index-file)}))))
    )
  
  )