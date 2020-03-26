(ns nl.mediquest.logback.util-test
  (:require
   [clojure.test :refer [deftest are]]
   [nl.mediquest.logback.util :as sut]))

(deftest scrub-test
  (are [expected input] (= expected (sut/scrub input))
    "password=***** bar yada"             "password=foo bar yada"
    "password:***** bar yada"             "password:foo bar yada"
    "password ***** bar yada"             "password foo bar yada"
    "password: ***** bar yada"            "password: foo bar yada"
    "password   ***** bar yada"           "password   foo bar yada"
    "password   ***** bar yada"           "password   \"foo\" bar yada"
    "password:***** bar yada"             "password:\"foo\" bar yada"
    "PaSSwoRD:***** bar yada"             "PaSSwoRD:\"foo\" bar yada"
    " pw=*****#(*\") "                    " pw=EUHIU#(*\") "
    "  name=***** Piet"                   "  name=Erwin Piet"
    "  name=*****, dit dan weer wel"      "  name=\"Erwin Piet\", dit dan weer wel"
    "<AGB> e <AGB>"                       "12345678 e 12345669"
    "{email: <email>, data}"              "{email: foo@bar.baz, data}"
    "Tel.: <telefoon>"                    "Tel.: 0612345678"
    "AGB= <AGB> en agb: <AGB>"            "AGB= 12345679 en agb: 12345669"
    "Bsn: \"<bsn>\""                      "Bsn: \"012345678\""
    "IBAN: \"<iban>7101\""                "IBAN: \"NL88TRIO0298087101\""
    "IBAN: \"<iban>7102\""                "IBAN: \"NL 88 TRIO 0298987102\""
    "creditcard: \"<creditcard> lalala\"" "creditcard: \"5500 0000 0000 0004 lalala\""
    ":value {:jdbc-url \"jdbc:postgresql://127.0.0.1:5432/config_db?user=config_reader&password=*****\"}}"
    ":value {:jdbc-url \"jdbc:postgresql://127.0.0.1:5432/config_db?user=config_reader&password=xxx\"}}"))
