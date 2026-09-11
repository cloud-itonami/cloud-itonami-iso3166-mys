(ns marketentry.governor
  "Market-Entry Compliance Governor -- the independent compliance layer
  that earns the MarketEntry-LLM the right to commit. The LLM has no
  notion of jurisdictional procurement law, whether a Malaysian MOF
  Certificate is actually still within its official validity window,
  whether a claimed engagement fee actually equals base + months x
  rate, whether an SSM company/business registration number has been
  verified for a filing that requires it, or when a draft stops being
  a draft and becomes a real-world portal submission, so this MUST be
  a separate system able to *reject* a proposal and fall back to HOLD.

  `:itonami.blueprint/governor` is `:market-entry-compliance-governor`
  (shared family keyword on blueprints; this is one *running*
  implementation of that governor for the iso3166 family).

  This blueprint's own text (docs/business-model.md Trust Controls:
  'any actual portal registration or filing submission requires
  Market-Entry Compliance Governor clearance and always escalates to
  human sign-off'; 'a false or fabricated regulatory-requirement claim
  is a HARD hold') names exactly the checks below.

  Seven checks, in priority order, ALL HARD violations: a human
  approver CANNOT override them. The confidence/actuation gate is
  SOFT: it asks a human to look (low confidence / actuation), and the
  human may approve -- but see `marketentry.phase`: for `:stake
  :actuation/draft-filing`/`:actuation/submit-filing` NO phase ever
  allows auto-commit either. Two independent layers agree that
  actuation is always a human call.

    1. Spec-basis                  -- did the jurisdiction proposal cite
                                       an OFFICIAL source
                                       (`marketentry.facts`), or invent
                                       one?
    2. Evidence incomplete         -- for `:filing/draft`/
                                       `:filing/submit`, has the
                                       jurisdiction actually been
                                       assessed with a full evidence
                                       checklist on file?
    3. MOF Certificate expired     -- for `:filing/submit`, when the
                                       engagement declares
                                       `:requires-mof-certificate? true`
                                       (near-universal for Malaysian
                                       public-sector tenders --
                                       ePerolehan's official
                                       online-registration page states an
                                       MOF Certificate is required 'for
                                       Suppliers to transact with the
                                       Federal Government for Goods and
                                       Services ... RM20,000 or more', with
                                       a stated 3-year validity period
                                       before renewal --
                                       eperolehan.gov.my/en/online-
                                       registration, fetched 2026-07-23;
                                       independently corroborated on
                                       myprocurement.treasury.gov.my/
                                       about/systems), INDEPENDENTLY
                                       recompute whether the certificate's
                                       own `:mof-certificate-issued-date`
                                       + the official 3-year validity
                                       window has already lapsed by the
                                       engagement's own declared
                                       `:filing-as-of-date`.

                                       FLAGSHIP genuinely new check for
                                       the iso3166 family: a TEMPORAL
                                       validity-window recompute, not a
                                       static boolean presence/absence
                                       flag (the `*-entity-missing`/
                                       `*-unverified` shape ~60 sibling
                                       governors already use), not a
                                       monetary recompute
                                       (`engagement-fee-mismatch`, which
                                       every sibling including this one
                                       already has as check 4 below), not
                                       a jurisdiction/geographic match
                                       (Honduras's `camara-jurisdiction-
                                       mismatch`), not a sector-conditional
                                       ownership ban (Panama's
                                       `retail-trade-restriction`), not an
                                       approval-gate on a declared state
                                       transition (Ecuador's
                                       `legal-rep-change-unapproved`), and
                                       not a double-election/incentive-tier
                                       recompute (Cambodia's
                                       `qip-incentive-election-mismatch`).
                                       Confirmed absent fleet-wide at
                                       build time: sampled 40 iso3166
                                       sibling `governor.cljc` files
                                       spanning the Americas, Europe, Asia,
                                       the Middle East and Africa (every
                                       `defn-` check function name and
                                       every `:rule` keyword collected and
                                       deduplicated -- none does date
                                       arithmetic), plus an org-wide GitHub
                                       code search for
                                       'quota'/'equity-threshold'/
                                       'percentage-mismatch'/'bumiputera'-
                                       style shapes, all zero hits.
                                       Deliberately does NOT assert a
                                       Bumiputera-equity percentage
                                       threshold: MITI's own page names
                                       itself 'the sole agency for BCPLC
                                       status recognition ... per all the
                                       criteria in LB1.1 Treasury Circular'
                                       but does not itself state the
                                       percentage, and the Ministry of
                                       Entrepreneur Development and
                                       Cooperatives' STB guideline PDF
                                       could not be fetched this session
                                       (TLS hostname mismatch on
                                       kuskop.gov.my) -- an honest
                                       coverage gap, not fabricated.
    4. Engagement fee mismatch     -- for `:filing/submit`,
                                       INDEPENDENTLY recompute whether
                                       the engagement's own `:claimed-
                                       fee` equals `base-fee +
                                       monthly-rate x monitoring-
                                       months` -- honest reapplication
                                       of the ground-truth-recompute
                                       discipline sibling actors use.
    5. SSM registration unverified -- for `:filing/submit`, when the
                                       engagement declares
                                       `:requires-ssm?
                                       true`, INDEPENDENTLY check
                                       `:ssm-verified?`.
                                       CONDITIONAL on the engagement's
                                       own ground truth. Grounded in SSM
                                       (Suruhanjaya Syarikat Malaysia /
                                       Companies Commission of Malaysia)
                                       company/business registration under
                                       the Companies Act 2016 (Act 777) --
                                       the SAME citation already verified
                                       in this repo's `statute.facts`,
                                       reused via `marketentry.facts`, not
                                       re-derived.
    6. Confidence floor / actuation
       gate                          -- LLM confidence below threshold,
                                       OR the op is `:filing/draft`/
                                       `:filing/submit` (REAL acts)
                                       -> escalate.

  Two more guards, double-draft/double-submit prevention, are enforced
  off dedicated `:drafted?`/`:submitted?` facts (never a `:status`
  value)."
  (:require [marketentry.facts :as facts]
            [marketentry.registry :as registry]
            [marketentry.store :as store]))

(def confidence-floor 0.6)

(def high-stakes
  "Stakes grave enough to always require a human, even when clean.
  Drafting a real portal package and submitting a real portal
  registration are the two real-world actuation events this actor
  performs."
  #{:actuation/draft-filing :actuation/submit-filing})

;; ----------------------------- checks -----------------------------

(defn- spec-basis-violations
  "A `:jurisdiction/assess` (or `:filing/draft`/`:filing/submit`)
  proposal with no spec-basis citation is a HARD violation -- never
  invent a jurisdiction's market-entry requirements."
  [{:keys [op]} proposal]
  (when (contains? #{:jurisdiction/assess :filing/draft :filing/submit} op)
    (let [value (:value proposal)]
      (when (or (empty? (:cites proposal))
                (and (contains? value :spec-basis) (nil? (:spec-basis value))))
        [{:rule :no-spec-basis
          :detail "公式spec-basisの引用が無い提案は法域要件として扱えない"}]))))

(defn- evidence-incomplete-violations
  "For `:filing/draft`/`:filing/submit`, the jurisdiction's required
  registration evidence must actually be satisfied."
  [{:keys [op subject]} st]
  (when (contains? #{:filing/draft :filing/submit} op)
    (let [e (store/engagement st subject)
          assessment (store/assessment-of st subject)]
      (when-not (and assessment
                     (facts/required-evidence-satisfied?
                      (:jurisdiction e) (:checklist assessment)))
        [{:rule :evidence-incomplete
          :detail "法域の必要書類(SSM登録/MOF Certificate/LHDN登録/代理人確認等)が充足していない状態での提案"}]))))

(defn- mof-certificate-expired-violations
  "For `:filing/submit`, when the engagement declares
  `:requires-mof-certificate? true`, INDEPENDENTLY recompute whether the
  certificate's own `:mof-certificate-issued-date` + the official 3-year
  ePerolehan validity window has already lapsed by the engagement's own
  declared `:filing-as-of-date` -- the flagship genuinely new check this
  vertical adds (a TEMPORAL validity-window recompute; see namespace
  docstring for the full grounding and fleet-wide novelty check)."
  [{:keys [op subject]} st]
  (when (= op :filing/submit)
    (let [e (store/engagement st subject)]
      (when (and (true? (:requires-mof-certificate? e))
                 (registry/mof-certificate-expired? e))
        [{:rule :mof-certificate-expired
          :detail (str subject " のMOF Certificate(ePerolehan/MyProcurement、3年有効)は発行日("
                       (:mof-certificate-issued-date e)
                       ")から独立再計算した有効期限("
                       (registry/compute-mof-certificate-expiry e)
                       ")が申請予定日("
                       (:filing-as-of-date e)
                       ")時点で既に満了 -- 提出提案は進められない")}]))))

(defn- engagement-fee-mismatch-violations
  "For `:filing/submit`, INDEPENDENTLY recompute whether the
  engagement's own claimed fee equals base + months x rate."
  [{:keys [op subject]} st]
  (when (= op :filing/submit)
    (let [e (store/engagement st subject)]
      (when-not (registry/engagement-fee-matches-claim? e)
        [{:rule :engagement-fee-mismatch
          :detail (str subject " の申告手数料(" (:claimed-fee e)
                      ")が独立再計算値(" (registry/compute-engagement-fee e) ")と一致しない")}]))))

(defn- ssm-unverified-violations
  "For `:filing/submit`, when the engagement declares
  `:requires-ssm? true`, INDEPENDENTLY check
  `:ssm-verified?` -- CONDITIONAL on the engagement's own
  ground truth."
  [{:keys [op subject]} st]
  (when (= op :filing/submit)
    (let [e (store/engagement st subject)]
      (when (and (true? (:requires-ssm? e))
                 (not (true? (:ssm-verified? e))))
        [{:rule :ssm-unverified
          :detail (str subject " はSSM登録確認を要するが未確認 -- 提出提案は進められない")}]))))

(defn- already-drafted-violations
  "For `:filing/draft`, refuses to draft the SAME engagement twice."
  [{:keys [op subject]} st]
  (when (= op :filing/draft)
    (when (store/engagement-already-drafted? st subject)
      [{:rule :already-drafted
        :detail (str subject " は既にドラフト済み")}])))

(defn- already-submitted-violations
  "For `:filing/submit`, refuses to submit the SAME engagement twice."
  [{:keys [op subject]} st]
  (when (= op :filing/submit)
    (when (store/engagement-already-submitted? st subject)
      [{:rule :already-submitted
        :detail (str subject " は既に提出済み")}])))

(defn check
  "Censors a MarketEntry-LLM proposal against the governor rules.
  Returns {:ok? bool :violations [..] :confidence c :escalate? bool
  :high-stakes? bool :hard? bool}."
  [request _context proposal st]
  (let [hard (into []
                   (concat (spec-basis-violations request proposal)
                           (evidence-incomplete-violations request st)
                           (mof-certificate-expired-violations request st)
                           (engagement-fee-mismatch-violations request st)
                           (ssm-unverified-violations request st)
                           (already-drafted-violations request st)
                           (already-submitted-violations request st)))
        conf (:confidence proposal 0.0)
        low? (< conf confidence-floor)
        stakes? (boolean (high-stakes (:stake proposal)))
        hard? (boolean (seq hard))]
    {:ok?          (and (not hard?) (not low?) (not stakes?))
     :violations   hard
     :confidence   conf
     :hard?        hard?
     :escalate?    (and (not hard?) (or low? stakes?))
     :high-stakes? stakes?}))

(defn hold-fact
  "The audit fact written when a proposal is rejected (HOLD)."
  [request context verdict]
  {:t          :governor-hold
   :op         (:op request)
   :actor      (:actor-id context)
   :subject    (:subject request)
   :disposition :hold
   :basis      (mapv :rule (:violations verdict))
   :violations (:violations verdict)
   :confidence (:confidence verdict)})
