(ns user
  (:require
    [component-taste.system :as system]
    [clojure.tools.namespace.repl :refer [refresh]]
    [com.stuartsierra.component :as component]))

(defonce system (system/new-system))

(defn start
  []
  (alter-var-root #'system component/start-system)
  :started)

(defn stop
  []
  (when system (alter-var-root #'system component/stop-system))
  :stopped)

(defn reload
  []
  (stop)
  (refresh :after 'user/start))