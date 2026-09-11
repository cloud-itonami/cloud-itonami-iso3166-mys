(ns marketentry.facts-test
  (:require [clojure.test :refer [deftest is testing]]
            [marketentry.facts :as facts]))

(deftest mys-has-spec-basis
  (let [sb (facts/spec-basis "MYS")]
    (is (some? sb))
    (is (string? (:provenance sb)))
    (is (seq (:required-evidence sb)))
    (is (some? (facts/rep-spec-basis "MYS")))
    (is (some? (facts/corporate-number-spec-basis "MYS")))))

(deftest unknown-jurisdiction-has-no-spec-basis
  (is (nil? (facts/spec-basis "ATL")))
  (is (nil? (facts/spec-basis "ZZZ"))))

(deftest required-evidence-satisfied
  (let [sb (facts/spec-basis "MYS")
        all (:required-evidence sb)]
    (is (true? (facts/required-evidence-satisfied? "MYS" all)))
    (is (not (facts/required-evidence-satisfied? "MYS" (take 1 all))))
    (is (nil? (facts/required-evidence-satisfied? "ATL" all)))))

(deftest coverage-is-honest
  (let [c (facts/coverage ["MYS" "USA" "ATL"])]
    (is (= 3 (:requested c)))
    (is (= 2 (:covered c)))
    (is (= ["ATL"] (:missing-jurisdictions c)))))

(deftest mys-legal-basis-matches-statute-facts
  (testing "marketentry.facts reuses (never re-derives or contradicts) the
  Companies Act 2016 citation already verified in statute.facts"
    (is (= "Companies Act 2016" (:legal-basis (facts/spec-basis "MYS"))))
    (is (= "mys.act-777-2016-companies-act" (:legal-basis-cites (facts/spec-basis "MYS"))))
    (is (= "https://lom.agc.gov.my/act-detail.php?act=777"
           (:corporate-number-provenance (facts/spec-basis "MYS"))))))

(deftest comparative-jurisdictions-are-not-cross-contaminated
  ;; USA/SGP/IDN are legitimate comparative-jurisdiction entries in the
  ;; same catalog (same pattern as sibling actors) -- each must carry
  ;; its OWN jurisdiction's own legal basis, not Malaysia's or each
  ;; other's.
  (testing "Singapore cites its own Government Procurement Act, not Malaysia's Companies Act"
    (is (= "GPA" (:legal-basis (facts/spec-basis "SGP")))))
  (testing "Indonesia cites its own procurement regulation"
    (is (= "Perpres pengadaan barang/jasa pemerintah" (:legal-basis (facts/spec-basis "IDN")))))
  (testing "Malaysia, Singapore and Indonesia don't share a legal-basis value"
    (is (distinct? (:legal-basis (facts/spec-basis "MYS"))
                    (:legal-basis (facts/spec-basis "SGP"))
                    (:legal-basis (facts/spec-basis "IDN"))))))
