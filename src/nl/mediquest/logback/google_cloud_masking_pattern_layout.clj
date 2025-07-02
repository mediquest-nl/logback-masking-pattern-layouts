(ns nl.mediquest.logback.google-cloud-masking-pattern-layout
  "Logback PatternLayout that logs in Google Cloud Logging JSON format to stdout.

  Scrubs via regexes and their replacements from regex and replacement
  XML-elements found in the Logback configuration. When an element
  `useDefaultMediquestReplacements` is added with a true value it will also
  scrub using the default-re->replacement patterns, after the custom ones are
  applied."
  (:gen-class
   :extends ch.qos.logback.classic.PatternLayout
   :exposes-methods {doLayout superDoLayout}
   :name nl.mediquest.logback.GoogleCloudMaskingPatternLayout
   :state state
   :init init
   :methods [[setRegex [String] void]
             [setReplacement [String] void]
             [setUseDefaultMediquestReplacements [Boolean] void]])
  (:require
   [cheshire.core :as json]
   [clojure.string :as string]
   [nl.mediquest.logback.util :refer [scrub default-re->replacement]])
  (:import
   (ch.qos.logback.classic Level)
   (ch.qos.logback.classic.pattern ThrowableProxyConverter)))

(def tpc
  (doto (ThrowableProxyConverter.)
    (.setOptionList ["full"])))

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

(defn add-stacktrace? [stacktrace]
  (and stacktrace (not (string/blank? stacktrace))))

(defn add-stacktrace [msg stacktrace]
  (str msg \newline stacktrace))

(defn message [msg stacktrace re->replacement]
  (let [enriched-msg (cond-> msg
                       (add-stacktrace? stacktrace)
                       (add-stacktrace stacktrace))]
    (scrub enriched-msg re->replacement)))

(defn -doLayout [this event]
  (let [msg (. this superDoLayout event)
        stacktrace (. tpc convert event)
        re->replacement (get-re->replacement @(.state this))]
    (str (json/encode
          {:message (message msg stacktrace re->replacement)
           :severity (severity event)
           :thread (.getThreadName event)
           :logger (.getLoggerName event)})
         "\n")))
