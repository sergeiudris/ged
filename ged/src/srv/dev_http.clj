
(ns srv.dev-http
  (:require [clojure.java.io :as io]
            [clojure.pprint :as pp]
            [clojure.string :as str]
            [clj-http.client :as client]
            #_[srv.server]))

(def not-found
  {:status 404
   :headers {"content-type" "text/plain"}
   :body "Not found."})

(def rqs (atom nil))

(def GEOSERVER_HOST "http://geoserver:8080/geoserver" )

#_(do (pp/pprint @rqs))

()

#_(prn "---dev-http")

#_(srv.server/run-dev)

#_(->
   (client/request
    (merge {:method :get
            :url (str GEOSERVER_HOST "/rest/layers.json")}
           {:basic-auth ["admin" "myawesomegeoserver"]}))
   (pp/pprint)
   )

#_ (subs "/geoserver/rest/layers.json" (count "/geoserver"))


(defn handle [{:keys [uri http-roots http-config request-method
                      server-name server-port headers] :as req}]
  (reset! rqs req)
  (prn uri)
  (cond
    (= uri "/hello")
    {:status 200
     :headers {"content-type" "text/html; charset=utf-8"}
     :body "world!!"}
    
    (str/starts-with? uri "/geoserver")
    (let [path (subs uri (count "/geoserver") )
          rawres (client/request
                  (merge {:method :get
                          :url (str GEOSERVER_HOST path)}
                         {
                          :headers headers
                          ; :basic-auth ["admin" "myawesomegeoserver"]
                          }))
          hdrs (:headers rawres)]
      (merge
       rawres
       {:headers (merge {"Access-Control-Allow-Origin" "*"
                         "Access-Control-Allow-Headers" "*" #_"content-type"} hdrs)}))
    
    :else
    (let [accept (get-in req [:headers "accept"])]
      (if (and accept (not (str/includes? accept "text/html")))
        not-found
        (let [index-name
              (get http-config :push-state/index "index.html")

              hdrs
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
             :headers hdrs
             :body (slurp index-file)})))))
  
  )