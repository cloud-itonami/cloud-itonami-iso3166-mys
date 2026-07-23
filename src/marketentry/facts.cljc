(ns marketentry.facts
  "Malaysia market-entry catalog.

  Unlike a from-scratch sibling, this repo already had a verified
  `statute.facts` catalog (ADR-2607141700 Wave 0) BEFORE marketentry
  existed, citing the Companies Act 2016 (Act 777) via
  lom.agc.gov.my (Malaysia's official Attorney-General's Chambers
  legislation portal). The `:legal-basis`/`:corporate-number-legal-
  basis`/`:corporate-number-provenance` fields below for \"MYS\" are
  PULLED BY REFERENCE from that same catalog entry
  (`\"mys.act-777-2016-companies-act\"`) via `companies-act-2016`,
  never re-typed as a second hardcoded title/URL/date that could
  silently drift out of sync with `statute.facts`.

  Independently verified this session (fetched 2026-07-23, never
  fabricated):
    - `https://www.ssm.com.my/` -- Suruhanjaya Syarikat Malaysia (SSM) /
      Companies Commission of Malaysia: registers companies (ROC) and
      businesses (ROB) under the Companies Act 2016 and the
      Registration of Businesses Act 1956.
    - `https://www.eperolehan.gov.my/en/online-registration` -- ePerolehan,
      the Ministry of Finance Malaysia's official government e-procurement
      portal: a supplier \"MOF Certificate\" / \"MOF Account\" is required
      to transact with the Federal Government for goods/services; stated
      fee RM450, stated validity period '3 years' before renewal.
    - `https://myprocurement.treasury.gov.my/about/systems` -- MyProcurement,
      the Ministry of Finance (Treasury) system of which ePerolehan is
      documented as 'an alternative medium for suppliers to
      register/update the Ministry of Finance Malaysia (MOF) Certificate'
      -- independently confirms the same MOF Certificate concept from a
      second official domain.
    - `https://www.hasil.gov.my/` -- Lembaga Hasil Dalam Negeri Malaysia
      (HASiL) / Inland Revenue Board: taxpayer registration and TIN
      assignment for businesses.
    - `https://www.miti.gov.my/index.php/pages/view/10290?mid=1309` --
      MITI (Ministry of Investment, Trade and Industry): 'the sole agency
      for BCPLC status recognition to companies controlled by Bumiputera
      and meet all the criteria as contained in LB1.1 Treasury Circular'
      -- confirms a real Bumiputera-equity-status certification regime
      exists, administered against an official Treasury Circular
      (exact equity-percentage threshold is inside that circular PDF,
      which this session could not directly fetch/read -- NOT asserted
      here; see governor.cljc for how this bounds the flagship check's
      scope honestly).

  `https://kuskop.gov.my/.../PANDUAN-PENGIKTIRAFAN-SIJIL-TARAF-BUMIPUTERA.pdf`
  (Ministry of Entrepreneur Development and Cooperatives' Bumiputera
  Certificate of Status guideline) was found via search but could NOT be
  fetched this session (TLS hostname mismatch: cert names
  protege.gov.my/www.kuskop.gov.my, not kuskop.gov.my) -- disclosed here
  as unreachable, not worked around.

  An entry not in this table has NO spec-basis, full stop; extend
  `catalog`, do not invent an id/url/date."
  (:require [statute.facts :as statute]))

(defn- companies-act-2016
  "The Companies Act 2016 (Act 777) entry already verified in this
  repo's `statute.facts/catalog` -- read by reference, not duplicated."
  []
  (first (filter #(= (:statute/id %) "mys.act-777-2016-companies-act")
                 (statute/spec-basis "MYS"))))

(def catalog
  (let [ca (companies-act-2016)]
    {"MYS" {:name "Malaysia"
            :owner-authority "Ministry of Finance (Kementerian Kewangan Malaysia) / ePerolehan"
            :legal-basis (:statute/title ca)
            :legal-basis-cites (:statute/id ca)
            :national-spec "ePerolehan / MyProcurement MOF Certificate + SSM company/business registration number"
            :provenance "https://www.eperolehan.gov.my/en/online-registration"
            :required-evidence ["SSM company/business registration extract (Companies Act 2016 / Registration of Businesses Act 1956)"
                                 "MOF Certificate registration record (ePerolehan/MyProcurement)"
                                 "LHDN tax registration record (Income Tax No. / TIN)"
                                 "Authorized-representative record"]
            :rep-owner-authority "Ministry of Finance (ePerolehan) / contracting agencies"
            :rep-legal-basis "Malaysian legal entity (SSM registration) typically required for MOF Certificate / ePerolehan participation"
            :rep-provenance "https://www.eperolehan.gov.my/"
            :corporate-number-owner-authority "SSM (Suruhanjaya Syarikat Malaysia / Companies Commission of Malaysia)"
            :corporate-number-legal-basis (:statute/title ca)
            :corporate-number-provenance (:statute/url ca)}
     "USA" {:name "United States" :owner-authority "GSA/SAM.gov" :legal-basis "FAR"
            :national-spec "SAM.gov" :provenance "https://sam.gov/"
            :required-evidence ["EIN record" "SAM.gov registration record" "State business registration record" "SAM UEI verification record"]}
     "SGP" {:name "Singapore" :owner-authority "GeBIZ" :legal-basis "GPA"
            :national-spec "GeBIZ" :provenance "https://www.gebiz.gov.sg/"
            :required-evidence ["UEN record" "GeBIZ registration" "GST record" "Authorized-representative record"]}
     "IDN" {:name "Indonesia" :owner-authority "LKPP / SPSE e-procurement" :legal-basis "Perpres pengadaan barang/jasa pemerintah"
            :national-spec "SPSE supplier registration + NIB/NPWP" :provenance "https://lpse.lkpp.go.id/"
            :required-evidence ["NIB/NPWP record" "SPSE registration record" "AHU company extract" "Authorized-representative record"]}}))

(defn spec-basis [iso3] (get catalog iso3))
(defn coverage
  ([] (coverage (keys catalog)))
  ([iso3s]
   (let [have (filter catalog iso3s) missing (remove catalog iso3s)]
     {:requested (count iso3s) :covered (count have)
      :covered-jurisdictions (vec (sort have))
      :missing-jurisdictions (vec (sort missing))
      :note "R0 catalog seed"})))
(defn required-evidence-satisfied? [iso3 submitted]
  (when-let [{:keys [required-evidence]} (spec-basis iso3)]
    (= (count required-evidence) (count (filter (set submitted) required-evidence)))))
(defn evidence-checklist [iso3] (:required-evidence (spec-basis iso3) []))
(defn rep-spec-basis [iso3]
  (when-let [sb (spec-basis iso3)]
    (when (:rep-owner-authority sb)
      (select-keys sb [:rep-owner-authority :rep-legal-basis :rep-provenance]))))
(defn corporate-number-spec-basis [iso3]
  (when-let [sb (spec-basis iso3)]
    (when (:corporate-number-owner-authority sb)
      (select-keys sb [:corporate-number-owner-authority :corporate-number-legal-basis :corporate-number-provenance]))))
