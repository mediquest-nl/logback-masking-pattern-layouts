(ns nl.mediquest.logback.masking-pattern-layout
  "Logback PatternLayout that masks sensitive data with replacements from the
  re->replacement map."
  (:gen-class
   :extends ch.qos.logback.classic.PatternLayout
   :name nl.mediquest.logback.MaskingPatternLayout)
  (:require
   [nl.mediquest.logback.util :refer [scrub]]))

(defn -doLayout [_ event]
  (str (scrub (.getFormattedMessage event)) "\n"))
