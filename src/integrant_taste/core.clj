(ns integrant-taste.core
  (:require [clojure.edn :as edn]
            [ring.adapter.jetty :refer [run-jetty]]
            [ring.util.response :refer [response]]
            [ring.middleware.defaults :refer [wrap-defaults site-defaults]]
            [ring.middleware.json :refer [wrap-json-response]]
            [com.walmartlabs.lacinia :refer [execute]]
            [com.walmartlabs.lacinia.util :refer [attach-resolvers]]
            [com.walmartlabs.lacinia.schema :as ls])
  (:gen-class))

(def resolvers
  {
   :get-hero  (constantly {})
   :get-droid (constantly {})
   })

(def app-schema
  (-> "resources/schema.edn"
      slurp
      edn/read-string
      (attach-resolvers resolvers)
      ls/compile)
  )

(defn gql
  [request]
  (let [result (execute app-schema "{hero {name}}" nil nil)]
    (response result)
    ))

(def app
  (-> gql
      wrap-json-response
      (wrap-defaults site-defaults)))

(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (run-jetty app {:port 8000})
  )
