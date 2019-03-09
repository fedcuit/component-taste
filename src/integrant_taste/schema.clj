(ns integrant-taste.schema
  (:require
   [clojure.java.io :as io]
   [clojure.edn :as edn]
   [com.stuartsierra.component :refer [Lifecycle]]
   [com.walmartlabs.lacinia.util :as util]
   [com.walmartlabs.lacinia.schema :as schema]))

(defn- resolve-game-by-id
  [games-map ctx args parent]
  (let [{:keys [id]} args]
    (get games-map id)))

(defn- resolve-board-game-designers
  [designers-map ctx args parent]
  (let [{:keys [designers]} parent]
    (map designers-map designers)))

(defn- resolve-designer-games
  [games-map ctx args parent]
  (let [{:keys [id]} parent]
    (->> games-map
         vals
         (filter #(contains? (:designers %) id)))))

(defn- entity-map
  "Get a list of entities from map and convert it into a map keyed by :id."
  [m k]
  (reduce #(assoc %1 (:id %2) %2) {} (get m k)))

(defn- resolver-map
  []
  (let [cgg-data (-> (io/resource "cgg-data.edn")
                     slurp
                     edn/read-string)
        games-map (entity-map cgg-data :games)
        designers-map (entity-map cgg-data :designers)]
    {:query/game-by-id    (partial resolve-game-by-id games-map)
     :BoardGame/designers (partial resolve-board-game-designers designers-map)
     :Designer/games      (partial resolve-designer-games games-map)}))

(defn load-schema
  []
  (-> (io/resource "schema.edn")
      slurp
      edn/read-string
      (util/attach-resolvers (resolver-map))
      schema/compile))

(defrecord SchemaProvider [schema]
  Lifecycle
  (start [this]
    (assoc this :schema (load-schema)))
  (stop [this]
    (assoc this :schema nil)))

(defn new-schema-provider
  []
  {:schema-provider (map->SchemaProvider {})})