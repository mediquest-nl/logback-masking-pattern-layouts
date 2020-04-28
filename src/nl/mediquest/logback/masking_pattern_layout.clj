(ns nl.mediquest.logback.masking-pattern-layout
  "Logback PatternLayout that masks sensitive data.

  Scrubs via regexes and their replacements from regex and replacement
  XML-elements found in the Logback configuration. When an element
  `useDefaultMediquestReplacements` is added with a true value it will also
  scrub using the default-re->replacement patterns, after the custom ones are
  applied."
  (:gen-class
   :extends ch.qos.logback.classic.PatternLayout
   :exposes-methods {doLayout superDoLayout}
   :name nl.mediquest.logback.MaskingPatternLayout
   :state state
   :init init
   :methods [[setRegex [String] void]
             [setReplacement [String] void]
             [setUseDefaultMediquestReplacements [Boolean] void]])
  (:require
   [nl.mediquest.logback.util :refer [scrub default-re->replacement]]))

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

(defn -doLayout [this event]
  (let [msg (. this superDoLayout event)
        re->replacement (get-re->replacement @(.state this))]
    (str (scrub msg re->replacement) "\n")))
