(ns nl.mediquest.logback.util-test
  (:require
   [clojure.test :refer [deftest are]]
   [nl.mediquest.logback.util :as sut]))

(deftest scrub-test
  (are [input expected] (= expected (sut/scrub input))
    "password=foo bar yada"                          "password=***** bar yada"
    "password:foo bar yada"                          "password:***** bar yada"
    "password foo bar yada"                          "password ***** bar yada"
    "password: foo bar yada"                         "password: ***** bar yada"
    "password   foo bar yada"                        "password   ***** bar yada"
    "password   \"foo\" bar yada"                    "password   ***** bar yada"
    "password:\"foo\" bar yada"                      "password:***** bar yada"
    "PaSSwoRD:\"foo\" bar yada"                      "PaSSwoRD:***** bar yada"
    " pw=EUHIU#(*\") "                               " pw=*****#(*\") "
    "  name=Erwin Piet"                              "  name=***** Piet"
    "  name=\"Erwin Piet\", dit dan weer wel"        "  name=*****, dit dan weer wel"
    "12345678 e 12345669"                            "<AGB> e <AGB>"
    "{email: foo@bar.baz, data}"                     "{email: <email>, data}"
    "Tel.: 0612345678"                               "Tel.: <telefoon>"
    "AGB= 12345679 en agb: 12345669"                 "AGB= <AGB> en agb: <AGB>"
    "Bsn: \"012345678\""                             "Bsn: \"<bsn>\""
    "IBAN: \"NL88TRIO0298087101\""                   "IBAN: \"<iban>7101\""
    "IBAN: \"NL 88 TRIO 0298987102\""                "IBAN: \"<iban>7102\""
    "creditcard: \"5500 0000 0000 0004 lalala\""     "creditcard: \"<creditcard> lalala\""
    ":value {:jdbc-url \"jdbc:postgresql://127.0.0.1:5432/config_db?user=config_reader&password=xxx\"}}"
    ":value {:jdbc-url \"jdbc:postgresql://127.0.0.1:5432/config_db?user=config_reader&password=*****\"}}"))
