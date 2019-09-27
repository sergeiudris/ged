(ns srv.service
  (:require [io.pedestal.http :as http]
            [io.pedestal.http.route :as route]
            [io.pedestal.http.body-params :as body-params]
            [ring.util.response :as ring-resp]
            [io.pedestal.http.ring-middlewares :refer [cookies]]
            [cheshire.core :as json]
            [clojure.pprint :as pp]
            [clj-http.client :as client]
            [slingshot.slingshot :refer [throw+ try+]]))

(defn about-page
  [request]
  (ring-resp/response (format "Clojure %s - served from %s"
                              (clojure-version)
                              (route/url-for ::about-page))))

(defn redirect-page
  [request]
  (prn request)
  (-> (ring-resp/redirect  "http://localhost:7080" 301)
      (ring-resp/set-cookie "hello" "world")  
      ; (assoc :session {:name "asd"})
      )
  )



(comment

  (as-> nil x
    (try+
     (client/post ""
                  {:body (json/generate-string {:login    ""
                                                :password ""})
                   :content-type :json
                   :accept :json
                   :headers {}
                   })
     (catch [:status 500] {:keys [request-time headers body]}
       (pp/pprint ; (json/parse-string body) 
                  body
                  )))
    (do 
      (prn (:status x))
      (pp/pprint (keys x))
      (pp/pprint (:cookies x))
      (pp/pprint (-> x :body json/parse-string (get "username") ))
      )
    )

  ;
  )

(defn stew-page
  [req]
  (prn req)
  (let [
        ; {
        ;  login :login
        ;  pssw  :pssw} (:params req)
         pr (try+
             (client/post ""
                          {:body         (json/generate-string {:login    ""
                                                                :password ""})
                           :content-type :json
                           :accept       :json
                           :headers      {}})
             (catch [:status 500] {:keys [request-time headers body status]}
               (pp/pprint ; (json/parse-string body) 
                status)))
        cookies (-> (:cookies pr) json/generate-string (json/parse-string true) )
        ]
  (-> (ring-resp/response  (json/generate-string {:rand (rand-int 5)
                                                  :status (:status pr)
                                                  :cookies ((str cookies))
                                                  :body   (-> pr :body json/parse-string)}))
      ; (ring-resp/set-cookie "hello" "world")
      ; (assoc :cookies cookies)
      (assoc :cookies {"session_id" {:value "session-id-hash"}})
      )))

(def n (atom 0))

(defn home-page
  [request]
  (swap! n inc )
  (ring-resp/response (str (clojure-version) "Hello World! #" @n )))

;; Defines "/" and "/about" routes with their associated :get handlers.
;; The interceptors defined after the verb map (e.g., {:get home-page}
;; apply to / and its children (/about).
(def common-interceptors [(body-params/body-params) http/html-body cookies])

;; Tabular routes
(def routes #{["/" :get (conj common-interceptors `home-page)]
              ["/about" :get (conj common-interceptors `about-page)]
              ["/r" :get (conj common-interceptors `redirect-page)]
              ["/s" :get (conj common-interceptors `stew-page)]
              ;
              })

;; Map-based routes
;(def routes `{"/" {:interceptors [(body-params/body-params) http/html-body]
;                   :get home-page
;                   "/about" {:get about-page}}})

;; Terse/Vector-based routes
;(def routes
;  `[[["/" {:get home-page}
;      ^:interceptors [(body-params/body-params) http/html-body]
;      ["/about" {:get about-page}]]]])


;; Consumed by myapp.server/create-server
;; See http/default-interceptors for additional options you can configure
(def service {:env :prod
              ;; You can bring your own non-default interceptors. Make
              ;; sure you include routing and set it up right for
              ;; dev-mode. If you do, many other keys for configuring
              ;; default interceptors will be ignored.
              ;; ::http/interceptors []
              ::http/routes routes

              ;; Uncomment next line to enable CORS support, add
              ;; string(s) specifying scheme, host and port for
              ;; allowed source(s):
              ;;
              ;; "http://localhost:8080"
              ;;
              ;;::http/allowed-origins ["scheme://host:port"]
              ::http/allowed-origins ["*"]

              ;; Tune the Secure Headers
              ;; and specifically the Content Security Policy appropriate to your service/application
              ;; For more information, see: https://content-security-policy.com/
              ;;   See also: https://github.com/pedestal/pedestal/issues/499
              ;;::http/secure-headers {:content-security-policy-settings {:object-src "'none'"
              ;;                                                          :script-src "'unsafe-inline' 'unsafe-eval' 'strict-dynamic' https: http:"
              ;;                                                          :frame-ancestors "'none'"}}
              
              ;; Root for resource interceptor that is available by default.
              ::http/resource-path "/public"

              ;; Either :jetty, :immutant or :tomcat (see comments in project.clj)
              ;;  This can also be your own chain provider/server-fn -- http://pedestal.io/reference/architecture-overview#_chain_provider
              ::http/type :jetty
              ;;::http/host "localhost"
              ::http/host "0.0.0.0"
              ::http/port 8080
              ;; Options to pass to the container (Jetty)
              ::http/container-options {:h2c? true
                                        :h2? false
                                        ;:keystore "test/hp/keystore.jks"
                                        ;:key-password "password"
                                        ;:ssl-port 8443
                                        :ssl? false}
              ;
              })

