(defproject component-taste "0.1.0-SNAPSHOT"
  :license {:name "EPL-2.0 OR GPL-2.0-or-later WITH Classpath-exception-2.0"
            :url  "https://www.eclipse.org/legal/epl-2.0/"}
  :dependencies [
                 [org.clojure/clojure "1.9.0"]
                 [com.walmartlabs/lacinia-pedestal "0.5.0"]
                 [com.stuartsierra/component "0.3.2"]
                 [io.aviso/logging "0.2.0"]
                 ]
  :plugins [[lein-cljfmt "0.6.4"]]
  :main ^:skip-aot component-taste.core
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all}
             :dev {:dependencies [[org.clojure/tools.namespace "0.2.11"]]}
             :repl {:main user}})