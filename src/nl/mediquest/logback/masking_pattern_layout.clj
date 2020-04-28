(ns nl.mediquest.logback.masking-pattern-layout
  "Logback PatternLayout that masks sensitive data with replacements from the
  re->replacement map."
  (:gen-class
   :extends ch.qos.logback.classic.PatternLayout
   :exposes-methods {doLayout superDoLayout}
   :name nl.mediquest.logback.MaskingPatternLayout)
  (:require
   [nl.mediquest.logback.util :refer [scrub]]))

(defn -doLayout [this event]
  (let [message (. this superDoLayout event)]
    (str (scrub message) "\n")))
