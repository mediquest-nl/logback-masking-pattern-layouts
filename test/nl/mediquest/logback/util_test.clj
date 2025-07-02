(ns nl.mediquest.logback.util-test
  (:require
   [clojure.test :refer [deftest are]]
   [nl.mediquest.logback.util :as sut]))

(deftest scrub-test
  (are [expected input] (= expected (sut/scrub input sut/default-re->replacement))
    ;; should scrub
    "password=***** bar yada"             "password=foo bar yada"
    "password:***** bar yada"             "password:foo bar yada"
    "password ***** bar yada"             "password foo bar yada"
    "password: ***** bar yada"            "password: foo bar yada"
    "password   ***** bar yada"           "password   foo bar yada"
    "password   ***** bar yada"           "password   \"foo\" bar yada"
    "password:***** bar yada"             "password:\"foo\" bar yada"
    "PaSSwoRD:***** bar yada"             "PaSSwoRD:\"foo\" bar yada"
    "email:***** bar yada"                "email:\"my-email.nl\" bar yada"
    "e-mail:***** bar yada"               "e-mail:\"my-email.nl\" bar yada"
    "PaSSwoRD:***** bar yada"             "PaSSwoRD:\"foo\" bar yada"
    " pw=*****#(*\") "                    " pw=EUHIU#(*\") "
    "  name=***** Piet"                   "  name=Erwin Piet"
    "  name=*****, dit dan weer wel"      "  name=\"Erwin Piet\", dit dan weer wel"
    "  naam=***** Piet"                   "  naam=Erwin Piet"
    "{email: <email>, data}"              "{email: foo@bar.baz, data}"
    "Tel.: <telefoon>"                    "Tel.: 0612345678"
    "Bsn: *****"                          "Bsn: \"012345678\""
    "IBAN: \"<iban>7101\""                "IBAN: \"NL88TRIO0298087101\""
    "IBAN: \"<iban>7102\""                "IBAN: \"NL 88 TRIO 0298987102\""
    "perinatologie 21017142210 [VSV]"     "perinatologie 21017142210 [VSV]"
    "creditcard: \"<creditcard> lalala\"" "creditcard: \"5500 0000 0000 0004 lalala\""
    "postgresql://my_user:*****@"         "postgresql://my_user:PassW0Rd1@"
    "postgresql://my_user:*****@"         "postgresql://my_user:\nPassW0Rd1\n@"
    ":value {:jdbc-url \"jdbc:postgresql://127.0.0.1:5432/config_db?user=config_reader&password=*****\"}}"
    ":value {:jdbc-url \"jdbc:postgresql://127.0.0.1:5432/config_db?user=config_reader&password=xxx\"}}"
    ;; Should not scrub
    "2016093109"                          "2016093109"
    "long timestamp 1234567890123"        "long timestamp 1234567890123"))
