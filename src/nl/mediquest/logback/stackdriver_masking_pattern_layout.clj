(ns nl.mediquest.logback.stackdriver-masking-pattern-layout
  "Logback PatternLayout that logs in Stackdriver JSON format to stdout.

  Scrubs via regexes and their replacements from regex and replacement
  XML-elements found in the Logback configuration. When an element
  `useDefaultMediquestReplacements` is added with a true value it will also
  scrub using the default-re->replacement patterns, after the custom ones are
  applied."
  (:gen-class
   :extends ch.qos.logback.classic.PatternLayout
   :exposes-methods {doLayout superDoLayout}
   :name nl.mediquest.logback.StackdriverMaskingPatternLayout
   :state state
   :init init
   :methods [[setRegex [String] void]
             [setReplacement [String] void]
             [setUseDefaultMediquestReplacements [Boolean] void]])
  (:require
   [cheshire.core :as json]
   [nl.mediquest.logback.util :refer [scrub default-re->replacement]])
  (:import
   (ch.qos.logback.classic Level)))

(defn -init
  []
  [[] (atom {:regexes [] :replacements []})])

(defn -setRegex [this re]
  (swap! (.state this) update :regexes conj (re-pattern re)))

(defn -setReplacement [this replacement]
  (swap! (.state this) update :replacements conj replacement))

(defn -setUseDefaultMediquestReplacements [this use?]
  (swap! (.state this) assoc :use-default-replacements? use?))

(defn use-default-replacements? [{:keys [use-default-replacements?] :as _state}]
  use-default-replacements?)

(defn get-custom-re->replacements [{:keys [regexes replacements] :as _state}]
  (apply array-map (interleave regexes replacements)))

(defn get-re->replacement [state]
  (merge (get-custom-re->replacements state)
         (when (use-default-replacements? state)
           default-re->replacement)))

(def logback-level->gcloud-level
  {Level/ALL "DEBUG"
   Level/TRACE "DEBUG"
   Level/DEBUG "DEBUG"
   Level/INFO "INFO"
   Level/WARN "WARNING"
   Level/ERROR "ERROR"})

(defn severity [event]
  (let [level (.getLevel event)]
    (get logback-level->gcloud-level level "DEFAULT")))

(defn -doLayout [this event]
  (let [msg (. this superDoLayout event)
        re->replacement (get-re->replacement @(.state this))]
    (str (json/encode
          {:message (scrub msg re->replacement)
           :severity (severity event)
           :thread (.getThreadName event)
           :logger (.getLoggerName event)})
         "\n")))
