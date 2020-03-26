(ns nl.mediquest.logback.util
  (:require
   [clojure.string :as string]))

;; Regexes used with string/replace, applied from top to bottom
(def re->replacement
  (array-map
   ;; Common patterns
   #"(?:[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*|\"(?:[\x01-\x08\x0b\x0c\x0e-\x1f\x21\x23-\x5b\x5d-\x7f]|\\[\x01-\x09\x0b\x0c\x0e-\x7f])*\")@(?:(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?|\[(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?|[a-z0-9-]*[a-z0-9]:(?:[\x01-\x08\x0b\x0c\x0e-\x1f\x21-\x5a\x53-\x7f]|\\[\x01-\x09\x0b\x0c\x0e-\x7f])+)\])" "<email>"
   #"0[6]{1}(\-)?[^0\D]{1}\d{7}" "<telefoon>"
   #"(?i)[A-Z]{2}\s?\d{2}\s?[A-Z]{0,4}\s?\d{4}\s?\d{0,2}" "<iban>"
   #"\b(?:\d[ -]*?){13,16}\b" "<creditcard>"
   #"[0-9]{9}" "<bsn>" ;; Possibly catches bit too much
   #"[0-9]{8}" "<AGB>" ;; Possibly catches bit too much
   ;; Found in code
   #"(?i)(password(?:=\s*|\:\s*|\s*|=\s*))\w+" "$1*****"
   #"(?i)(password(?:=\s*|\:\s*|\s*|=\s*))\".*\"" "$1*****"
   #"(?i)(pw(?:=\s*|\:\s*|\s*|=\s*))\w+" "$1*****"
   #"(?i)(pw(?:=\s*|\:\s*|\s*|=\s*))\".*\"" "$1*****"
   #"(?i)(agb(?:=\s*|\:\s*|\s*|=\s*))\w+" "$1*****"
   #"(?i)(agb(?:=\s*|\:\s*|\s*|=\s*))\".*\"" "$1*****"
   #"(?i)(name(?:=\s*|\:\s*|\s*|=\s*))\w+" "$1*****"
   #"(?i)(name(?:=\s*|\:\s*|\s*|=\s*))\".*\"" "$1*****"))

(defn scrub [message]
  (reduce-kv string/replace message re->replacement))
