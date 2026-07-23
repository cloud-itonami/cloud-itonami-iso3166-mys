(ns marketentry.registry
  "Pure-function market-entry filing-draft + filing-submit record
  construction -- an append-only market-entry book-of-record draft.

  Like every sibling actor's registry, there is no single international
  reference-number standard for a public-procurement market-entry
  filing -- every jurisdiction assigns its own format. This namespace
  does NOT invent one; it builds a jurisdiction-scoped sequence number
  and validates the record's required fields, the same honest,
  non-fabricating discipline `marketentry.facts` uses.

  `engagement-fee-matches-claim?` is an HONEST reapplication of the
  SAME ground-truth-recompute DISCIPLINE sibling actors use (verify a
  claimed monetary total against the entity's own recorded quantity x
  unit fields), reapplied to a market-entry engagement fee line.

  `compute-mof-certificate-expiry` / `mof-certificate-expired?` are the
  FLAGSHIP genuinely new check this vertical adds -- see
  `marketentry.governor` docstring for the full grounding. In shape
  they reapply the SAME ground-truth-recompute discipline as
  `compute-engagement-fee`, but to a TEMPORAL validity window (issue
  date + a fixed validity period, compared against the engagement's own
  declared as-of date) instead of a monetary total. This is a
  calendar-offset calculation, not a full Gregorian/leap-aware date
  library -- the same proportionate-rigor stance
  `compute-engagement-fee` takes ('a single flat base + months x rate
  calculation, not a full pricing engine').

  This namespace is pure data + pure functions -- no I/O, no network
  call to any real procurement portal, no wall-clock read (`now` is
  never called; validity is always evaluated against the engagement's
  OWN declared `:filing-as-of-date`, so the result is deterministic and
  reproducible in tests). It builds the RECORD an operator would keep,
  not the act of submitting a portal registration itself (that is
  `marketentry.operation`'s `:filing/submit`, always human-gated -- see
  README Actuation)."
  (:require [clojure.string :as str]
            #?(:clj  [clojure.edn :as edn]
               :cljs [cljs.reader :as edn])))

(defn- unsigned-certificate
  "Every certificate this actor produces is UNSIGNED -- signature is
  the market-entry operator's act, not this actor's."
  [kind subject record-id]
  {"@context" ["https://www.w3.org/ns/credentials/v2"]
   "type" ["VerifiableCredential" kind]
   "credentialSubject" {"id" subject "record" record-id}
   "proof" nil
   "issued_by_registry" false
   "status" "draft-unsigned"})

(defn- zero-pad [n w]
  (let [s (str n)]
    (str (apply str (repeat (max 0 (- w (count s))) "0")) s)))

(defn compute-engagement-fee
  "The ground-truth engagement fee for `engagement`'s own `:base-fee`
  and `:monitoring-months` x `:monthly-rate` -- a single flat
  base + months x rate calculation, not a full pricing engine."
  [{:keys [base-fee monthly-rate monitoring-months]}]
  (+ (double base-fee)
     (* (double monthly-rate) (double monitoring-months))))

(defn engagement-fee-matches-claim?
  "Does `engagement`'s own `:claimed-fee` equal the independently
  recomputed `compute-engagement-fee`?"
  [{:keys [claimed-fee] :as engagement}]
  (== (double claimed-fee) (compute-engagement-fee engagement)))

;; ----------------------- MOF Certificate validity window -----------------------
;;
;; ISO "YYYY-MM-DD" strings compare correctly with plain string
;; `compare` as long as every component is zero-padded to a fixed
;; width, which is exactly what `iso-date` below guarantees -- so date
;; arithmetic here never needs a date library, JVM/JS interop, or a
;; third-party dependency (portable .cljc, per this workspace's runtime
;; priority rules).

(def mof-certificate-validity-years
  "MOF Certificate validity period, per ePerolehan's official
  online-registration page (Ministry of Finance Malaysia; 'A fee of
  RM450 will be charged for new registration or renewal with validity
  period of 3 years' -- https://www.eperolehan.gov.my/en/online-
  registration, fetched 2026-07-23; independently corroborated on
  https://myprocurement.treasury.gov.my/about/systems, which documents
  ePerolehan as 'an alternative medium for suppliers to register/update
  the Ministry of Finance Malaysia (MOF) Certificate')."
  3)

(defn- parse-int [s] (edn/read-string s))

(defn- parse-iso-date [s]
  (let [[y m d] (str/split s #"-")]
    [(parse-int y) (parse-int m) (parse-int d)]))

(defn- zero-pad2 [n] (if (< n 10) (str "0" n) (str n)))

(defn- iso-date [[y m d]]
  (str y "-" (zero-pad2 m) "-" (zero-pad2 d)))

(defn add-years-iso
  "`iso-date-str` + `years`, as an ISO \"YYYY-MM-DD\" string. Calendar-
  offset only (no leap-day reconciliation) -- proportionate to this
  actor's other arithmetic (see `compute-engagement-fee`)."
  [iso-date-str years]
  (let [[y m d] (parse-iso-date iso-date-str)]
    (iso-date [(+ y years) m d])))

(defn compute-mof-certificate-expiry
  "The ground-truth MOF Certificate expiry for `engagement`'s own
  `:mof-certificate-issued-date`, independently recomputed as issued
  date + `mof-certificate-validity-years` (the official 3-year
  ePerolehan validity period) -- never taken from a self-reported
  expiry claim."
  [{:keys [mof-certificate-issued-date]}]
  (add-years-iso mof-certificate-issued-date mof-certificate-validity-years))

(defn mof-certificate-expired?
  "Has `engagement`'s own declared `:filing-as-of-date` (the date the
  filing/submit act is being performed as of) already reached or passed
  the independently recomputed MOF Certificate expiry (issued date + 3
  years)? A TEMPORAL validity-window recompute -- the flagship check
  this vertical adds, distinct in shape from every other check in the
  fleet (never a static boolean flag, a monetary recompute, or a
  jurisdiction/geographic match; see `marketentry.governor`)."
  [{:keys [filing-as-of-date] :as engagement}]
  (let [expiry (compute-mof-certificate-expiry engagement)]
    (>= (compare filing-as-of-date expiry) 0)))

(defn register-draft
  "Validate + construct the FILING-DRAFT registration DRAFT -- the
  market-entry operator's own act of preparing a portal registration
  package. Pure function -- does not touch any real procurement
  portal."
  [engagement-id jurisdiction sequence]
  (when-not (and engagement-id (not= engagement-id ""))
    (throw (ex-info "draft: engagement_id required" {})))
  (when-not (and jurisdiction (not= jurisdiction ""))
    (throw (ex-info "draft: jurisdiction required" {})))
  (when (< sequence 0)
    (throw (ex-info "draft: sequence must be >= 0" {})))
  (let [draft-number (str (str/upper-case jurisdiction) "-DFT-" (zero-pad sequence 6))
        record {"record_id" draft-number
                "kind" "filing-draft"
                "engagement_id" engagement-id
                "jurisdiction" jurisdiction
                "immutable" true}]
    {"record" record "draft_number" draft-number
     "certificate" (unsigned-certificate "FilingDraft" draft-number draft-number)}))

(defn register-submit
  "Validate + construct the FILING-SUBMIT registration DRAFT -- the
  market-entry operator's own act of actually submitting a portal
  registration (always human-gated upstream)."
  [engagement-id jurisdiction sequence]
  (when-not (and engagement-id (not= engagement-id ""))
    (throw (ex-info "submit: engagement_id required" {})))
  (when-not (and jurisdiction (not= jurisdiction ""))
    (throw (ex-info "submit: jurisdiction required" {})))
  (when (< sequence 0)
    (throw (ex-info "submit: sequence must be >= 0" {})))
  (let [submit-number (str (str/upper-case jurisdiction) "-SUB-" (zero-pad sequence 6))
        record {"record_id" submit-number
                "kind" "filing-submit"
                "engagement_id" engagement-id
                "jurisdiction" jurisdiction
                "immutable" true}]
    {"record" record "submit_number" submit-number
     "certificate" (unsigned-certificate "FilingSubmit" submit-number submit-number)}))

(defn append [history result]
  (conj (vec history) (get result "record")))
