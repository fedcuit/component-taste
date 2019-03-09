(ns integrant-taste.system
  (:require [com.stuartsierra.component :as component]
            [integrant-taste.schema :as schema]
            [integrant-taste.server :as server]))

(defn new-system
  []
  (merge (component/system-map)
         (server/new-server)
         (schema/new-schema-provider)))