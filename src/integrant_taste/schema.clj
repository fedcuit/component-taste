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

(defn- resolve-element-by-id
  [members-map ctx args parent]
  (let [{:keys [id]} args]
    (get members-map id)))

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

(defn- average
  [ns]
  (let [sum (reduce + 0 ns)
        cnt (count ns)]
    (/ sum cnt)))

(defn- game-ratings
  [ratings game_id]
  (filter #(= (:game_id %) game_id) ratings))

(defn- rating-summary
  [ratings]
  (fn [ctx args game]
    (let [{:keys [id]} game
          ratings (game-ratings ratings id)]
      {:count (count ratings) :average (average (map :rating ratings))})))

(defn- game-rating->game
  [games-map]
  (fn [ctx args rating]
    (let [{:keys [game]} rating]
      (get games-map game))))

(defn- member-ratings
  [ratings]
  (fn [ctx args member]
    (let [{:keys [id]} member]
      (->> ratings
           (filter #(= id (:member_id %)))))))

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
        members-map (entity-map cgg-data :members)
        designers-map (entity-map cgg-data :designers)]
    {:query/game-by-id         (partial resolve-game-by-id games-map)
     :query/member-by-id       (partial resolve-element-by-id members-map)
     :BoardGame/designers      (partial resolve-board-game-designers designers-map)
     :BoardGame/rating-summary (rating-summary (:ratings cgg-data))
     :GameRating/game          (game-rating->game games-map)
     :Designer/games           (partial resolve-designer-games games-map)
     :Member/ratings           (member-ratings (:ratings cgg-data))}))

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