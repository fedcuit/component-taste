(ns integrant-taste.server
  (:require [com.stuartsierra.component :refer [Lifecycle using]]
            [com.walmartlabs.lacinia.pedestal :as lp]
            [io.pedestal.http :as http]))

(defrecord Server [schema-provider server]
  Lifecycle
  (start [this]
    (if server
      this
      (assoc this :server (-> (:schema schema-provider)
                              (lp/service-map {:graphiql true})
                              http/create-server
                              http/start))))
  (stop [this]
    (when server (http/stop server))
    (assoc this :server nil)))

(defn new-server
  []
  {:server (using (map->Server {}) [:schema-provider])})