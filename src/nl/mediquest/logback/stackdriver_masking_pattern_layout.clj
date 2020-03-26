(ns nl.mediquest.logback.stackdriver-masking-pattern-layout
  "Logback PatternLayout that logs in Stackdriver JSON format to stdout.
  It masks sensitive data with replacements from the re->replacement map."
  (:gen-class
   :extends ch.qos.logback.classic.PatternLayout
   :name nl.mediquest.logback.StackdriverMaskingPatternLayout)
  (:require
   [cheshire.core :as json]
   [nl.mediquest.logback.util :refer [scrub]])
  (:import
   (ch.qos.logback.classic Level)))

(def logback-level->gcloud-level
  {Level/ALL "DEBUG"
   Level/TRACE "DEBUG"
   Level/INFO "INFO"
   Level/WARN "WARNING"
   Level/ERROR "ERROR"})

(defn severity [event]
  (let [level (.getLevel event)]
    (get logback-level->gcloud-level level "DEFAULT")))

(defn -doLayout [_ event]
  (str (json/encode
        {:message (scrub (.getFormattedMessage event))
         :severity (severity event)
         :thread (.getThreadName event)
         :logger (.getLoggerName event)})
       "\n"))
