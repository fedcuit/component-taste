(ns component-taste.core
  (:require [com.stuartsierra.component :as component]
            [component-taste.system :refer [new-system]])
  (:gen-class))

(defn -main
  [& args]
  (component/start-system (new-system)))