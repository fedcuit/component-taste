(ns component-taste.system
  (:require [com.stuartsierra.component :refer [system-map using]]
            [component-taste.schema :refer [map->SchemaProvider]]
            [component-taste.server :refer [map->Server]]))

(defn new-system
  []
  (system-map
   :schema (map->SchemaProvider {})
   :server (using (map->Server {}) [:schema])))