(ns marketentry.registry-test
  (:require [clojure.test :refer [deftest is testing]]
            [marketentry.registry :as registry]))

(deftest engagement-fee-recompute
  (let [e {:base-fee 500000 :monthly-rate 30000 :monitoring-months 12 :claimed-fee 860000.0}]
    (is (== 860000.0 (registry/compute-engagement-fee e)))
    (is (true? (registry/engagement-fee-matches-claim? e))))
  (let [bad {:base-fee 500000 :monthly-rate 30000 :monitoring-months 12 :claimed-fee 999000.0}]
    (is (false? (registry/engagement-fee-matches-claim? bad)))))

(deftest register-draft-and-submit
  (let [d (registry/register-draft "eng-1" "MYS" 0)
        s (registry/register-submit "eng-1" "MYS" 0)]
    (is (= "MYS-DFT-000000" (get d "draft_number")))
    (is (= "MYS-SUB-000000" (get s "submit_number")))
    (is (nil? (get-in d ["certificate" "proof"])))
    (is (= "draft-unsigned" (get-in s ["certificate" "status"])))))

(deftest register-requires-ids
  (is (thrown? Exception (registry/register-draft "" "MYS" 0)))
  (is (thrown? Exception (registry/register-submit "eng-1" "" 0))))

(deftest add-years-iso-is-a-plain-calendar-offset
  (is (= "2023-01-01" (registry/add-years-iso "2020-01-01" 3)))
  (is (= "2029-07-23" (registry/add-years-iso "2026-07-23" 3))))

(deftest mof-certificate-expiry-recompute
  (testing "expiry = issued date + the official 3-year ePerolehan validity period"
    (is (= "2023-01-01" (registry/compute-mof-certificate-expiry
                         {:mof-certificate-issued-date "2020-01-01"})))
    (is (= 3 registry/mof-certificate-validity-years)))
  (testing "not yet expired: as-of date is before the recomputed expiry"
    (is (false? (registry/mof-certificate-expired?
                 {:mof-certificate-issued-date "2025-01-01"
                  :filing-as-of-date "2026-07-23"}))))
  (testing "expired: as-of date is on/after the recomputed expiry"
    (is (true? (registry/mof-certificate-expired?
                {:mof-certificate-issued-date "2020-01-01"
                 :filing-as-of-date "2026-07-23"}))))
  (testing "expired exactly ON the expiry date itself (boundary -- validity window is inclusive-then-lapsed)"
    (is (true? (registry/mof-certificate-expired?
                {:mof-certificate-issued-date "2020-01-01"
                 :filing-as-of-date "2023-01-01"})))))
