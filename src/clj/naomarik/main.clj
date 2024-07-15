(ns naomarik.main
  (:require
   [clojure.edn :as edn]
   [clojure.java.shell :refer [sh]]
   [clojure.java.io :as io]
   [clojure.string :as str]
   [naomarik.env :as env]
   [naomarik.pages :as pages]
   [org.httpkit.server :as srv]))

(defonce server (atom nil))

(defn- route-with-param [s]
  (let [[_ route id] (re-find #"(/[^/]*)/?(.*)?" s)
        id (if (empty? id) nil id)]
    {:route route
     :id id}))

(defn- handler [mode {:keys [uri request-method] :as req}]
  (let [{:keys [route id]} (route-with-param uri)
        request-method (if (and (= mode :dev) (= request-method :head))
                         :get ;; quick hack for livejs
                         request-method)]

    (condp = [request-method route id]
      [:get "/" nil]
      {:body (pages/home-page req)
       :headers {"Content-Type" "text/html"}
       :status 200}

      [:get "/projects" nil]
      {:body (pages/projects req)
       :headers {"Content-Type" "text/html"}
       :status 200}

      [:get "/projects" id]
      {:body (pages/project req id)
       :headers {"Content-Type" "text/html"}
       :status 200}

      [:get "/verbiage" nil]
      {:body (pages/verbiage req)
       :headers {"Content-Type" "text/html"}
       :status 200}

      [:get "/verbiage" id]
      {:body (pages/post req id)
       :headers {"Content-Type" "text/html"}
       :status 200}

      [:get "/aboot" nil]
      {:body (pages/aboot req)
       :headers {"Content-Type" "text/html"}
       :status 200}

      [:get "/img" id]
      {:body (io/file (io/resource (str "build/img/" id)))
       :headers {"Content-Type" "image/webp"}
       :status 200}

      [:get "/js" "index.min.js"]
      {:body (slurp (io/file "build/js/index.min.js"))
       :headers {"Content-Type" "application/javascript"}
       :status 200}

      [:get "/css" id]
      {:body (io/file (io/resource (str "build/css/" id)))
       :headers {"Content-Type" "text/css"}
       :status 200}

      [:get "/dev-css" id] ;; TODO dev only
      {:body (io/file (io/resource (str "dev/" id)))
       :headers {"Content-Type" "text/css"}
       :status 200}

      {:body (str "page " uri " not found")
       :status 404})))

(defn- read-config []
  (if (.exists (io/file "config.edn"))
    (edn/read-string (slurp "config.edn"))
    {:port 3001}))

(defn run [mode]
  (assert (#{:dev :prod} mode))
  (reset! env/sha (str/trim (slurp "ver")))
  (reset! env/mode mode)
  (let [{:keys [port]} (read-config)]
    (when-let [server @server]
      (server))
    (reset! server
            (srv/run-server (partial handler mode) {:port port}))
    (println "httpkit started on port:" port)))

(defn -main []
  (run :prod))

(comment
  ;; restart server
  (run :dev)
  (run :prod)
  (do
    (when-let [instance @server] (instance))
    #_(reset! server nil)))
