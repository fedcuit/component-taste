(ns integrant-taste.core
  (:require [clojure.edn :as edn]
            [ring.adapter.jetty :refer [run-jetty]]
            [ring.util.response :refer [response]]
            [ring.middleware.defaults :refer [wrap-defaults api-defaults]]
            [ring.middleware.json :refer [wrap-json-response wrap-json-body]]
            [compojure.core :refer :all]
            [compojure.route :as route]
            [com.walmartlabs.lacinia :refer [execute]]
            [integrant-taste.schema :refer [load-schema]])
  (:gen-class))

(def schema (load-schema))

(defroutes graphql
  (context "/graphql" []
    (GET "/" [] "GraphiQL")
    (POST "/" {{:keys [query variables]} :body}
      (response (execute schema query variables nil)))))

(defroutes app
  (GET "/" [] "Hello Compojure")
  graphql
  (route/not-found "Page Not Found"))

(def handler
  (-> app
      (wrap-json-body {:keywords? true})
      wrap-json-response
      (wrap-defaults api-defaults)))

(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (run-jetty handler {:port 8000}))
