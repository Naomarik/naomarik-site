(ns user
  (:require
   [naomarik.main :as main]
   [naomarik.env :as env]
   [clojure.tools.namespace.repl :refer [refresh]])
  )

(defn cider-reset []
  ;; (refresh)
  ;; (reload/reload)
  )

(comment
  (main/run :prod)
  (main/run :dev))
