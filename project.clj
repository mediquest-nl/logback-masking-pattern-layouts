(defproject nl.mediquest/logback.masking-pattern-layouts "2.0.0"
  :description "Logback appenders for scrubbing sensitive data from logs"
  :url "https://github.com/mediquest-nl/logback-masking-pattern-layouts"
  :license {:name "The MIT License"}
  :aot [nl.mediquest.logback.masking-pattern-layout
        nl.mediquest.logback.google-cloud-masking-pattern-layout]
  :dependencies [[org.clojure/clojure "1.12.1"]
                 [cheshire "6.0.0"]
                 [ch.qos.logback/logback-classic "1.2.3" :exclusions [org.slf4j/slf4j-api]]]
  :profiles {:dev {:dependencies [[org.clojure/clojure "1.12.1"]]}}
  :test-selectors {:unit (complement :integration)
                   :integration :integration})
