(ns user
  (:require
    [integrant-taste.schema :as s]
    [com.walmartlabs.lacinia :as lacinia]
    [clojure.tools.namespace.repl :refer [refresh]]))

(def schema (s/load-schema))

(defn q
  [query-string]
  (lacinia/execute schema query-string nil nil))