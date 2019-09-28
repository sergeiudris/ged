
(ns srv.dev-http
  (:require [clojure.java.io :as io]
            [clojure.pprint :as pp]
            [clojure.string :as str]
            [clj-http.client :as client]
            [shadow.undertow.impl :refer [RespondBody]]
            #_[srv.server])
    (:import (io.undertow.util HeaderMap HeaderValues Headers HttpString)
             (io.undertow.server HttpServerExchange)
             (java.nio ByteBuffer)
             (io.undertow.io Sender)
             (java.io InputStream File)
             (io.undertow.server.handlers ResponseCodeHandler))
  )

(defn deep-merge [a & maps]
  (if (map? a)
    (apply merge-with deep-merge a maps)
    (apply merge-with deep-merge maps)))

(def not-found
  {:status 404
   :headers {"content-type" "text/plain"}
   :body "Not found."})

; https://stackoverflow.com/questions/13924842/extend-clojure-protocol-to-a-primitive-array
; https://github.com/thheller/shadow-cljs/blob/61af1cce91398c77f941a3b057cbb840b384eaf6/src/main/shadow/undertow/impl.clj

(def ut-body (atom nil))
(def ut-exchange (atom nil))

#_(do (pp/pprint @ut-body))
#_(do (pp/pprint @ut-exchange))


(extend-protocol RespondBody
  #_(Class/forName "[B") #_(class (float-array 0))

  (Class/forName "[B")
  (respond [body ^HttpServerExchange exchange]
    (do
      (reset! ut-body body)
      (reset! ut-exchange exchange)
      (.send ^Sender (.getResponseSender exchange) body)))

  ; String
  ; (respond [body ^HttpServerExchange exchange]
  ;   (.send ^Sender (.getResponseSender exchange) body))

  ; InputStream
  ; (respond [body ^HttpServerExchange exchange]
  ;   (with-open [^InputStream b body]
  ;     (io/copy b (.getOutputStream exchange))))

  ; File
  ; (respond [f exchange]
  ;   (respond (io/input-stream f) exchange))
  ; 
  )


(def rqs (atom nil))
(def rqs-opts (atom nil))
(def ex (atom nil))
(def rawrsp (atom nil))




(def GEOSERVER_HOST "http://geoserver:8080/geoserver" )

#_(do (pp/pprint @rqs))

#_(do (pp/pprint @rqs-opts))

#_(do (pp/pprint @ex))

#_(do (pp/pprint @rawrsp))





#_(prn "---dev-http")

#_(not-empty "")

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
                      server-name server-port body headers query-string] :as req}]
  (reset! rqs req)
  (prn uri)
  (cond
    (= uri "/hello")
    {:status 200
     :headers {"content-type" "text/html; charset=utf-8"}
     :body "world!!"}
    
    (str/starts-with? uri "/geoserver")
    (let [path (subs uri (count "/geoserver"))
          url (if (not-empty query-string)   (str path "?" query-string) path)
          req-opts {:throw-entire-message? true
                    :throw-exceptions true
                    :method request-method
                    :url (str GEOSERVER_HOST url)
                    ; :as :byte-array
                    :body body
                    :headers  (dissoc headers "content-length")
                    ; :basic-auth ["admin" "myawesomegeoserver"]
                    }
          rawres (try (client/request     req-opts)
                      (catch Exception e
                        (do
                          (reset! ex e)
                          {:status 500
                           :headers {"content-type" "text/html; charset=utf-8"}
                           :body (str e)})))
          hdrs (:headers rawres)]
      (reset! rawrsp rawres)
      (reset! rqs-opts req-opts)
      (deep-merge
       rawres
       {;:body (.getBytes (:body rawres))
        ; :body "hello"
        :headers {"Access-Control-Allow-Origin" "*"
                  "Access-Control-Allow-Headers" "*" #_"content-type"}}))
    
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