(ns statute.facts
  "General-law compliance catalog for Malaysia (MYS) -- a 42nd
  country-level entry (see cloud-itonami-iso3166-jpn/-usa/-gbr/-deu/-fra/
  -can/-aus/-kor/-nld/-ita/-esp/-swe/-nor/-dnk/-fin/-prt/-bel/-bra/-mex/
  -chl/-arg/-zaf/-col/-ury/-cri/-pan/-ecu/-pry/-gtm/-hnd/-ind/-ken/-tha/
  -are/-vnm/-idn/-phl/-egy/-tur/-nga/-sau for the first forty-one) per
  ADR-2607141700 (cloud-itonami-compliance-fact-federation). Unlike
  those siblings, this repo had NO pre-existing marketentry.facts
  namespace/deps.edn/src/test (a blueprint+governance-docs-only shell)
  -- statute.facts is added here as the FIRST implementation, with a
  minimal new deps.edn (no langgraph dependency, since statute.facts
  itself has zero external deps).

  Reuses this tick-window's already-verified capital-status finding
  from cloud-itonami-municipality-mys-kuala-lumpur (tick 122): Malaysia
  operates a stable, SETTLED dual-capital arrangement (Kuala
  Lumpur/Putrajaya), unlike Egypt/Indonesia's ongoing transitions.

  Companies Act 2016 (Act 777) -- title, Royal Assent (31 August
  2016), and Gazette publication (15 September 2016) dates directly
  confirmed via lom.agc.gov.my's (Malaysia's official Attorney
  General's Chambers legislation portal, 'Laws of Malaysia') own
  act-detail page, which states verbatim: Royal Assent '31/08/2016',
  Publication '15/09/2016', with the main body coming into force
  31-01-2017 per P.U.(B) 50/2017 (staged commencement -- some
  provisions, e.g. Division 8 of Part III and section 241, commenced
  even later, on 1 March 2018 and 15 March 2019 respectively).
  :enacted-date uses the Royal Assent date. Two PDF mirrors
  (ssm.com.my's official reprint exceeded the 10MB fetch limit;
  investmalaysia.gov.my's mirror rendered Bahasa Malaysia text as
  illegible boxes via font-subsetting) were tried first and abandoned
  in favor of the lom.agc.gov.my HTML page.

  Personal Data Protection Act 2010 (Act 709) -- title and Act number
  directly confirmed via investmalaysia.gov.my's hosted PDF mirror of
  the official 'Laws of Malaysia' reprint, via the Read-tool saved-path
  fallback (WebFetch itself reported the PDF as illegible/binary); its
  Section 1(2) states the Act's actual commencement was deferred to a
  date fixed by ministerial notification, and the reprint's own
  bracketed date line confirms this was '[15 November 2013, P.U. (B)
  464/2013]' -- used as :enacted-date rather than the earlier 2010
  Royal Assent/Gazette-publication date, since the Act had no legal
  effect until this later commencement date.

  An entry not in this table has NO spec-basis, full stop; extend
  `catalog`, do not invent an id/url/date.")

(def catalog
  "ISO3166 alpha-3 -> vector of statute entries."
  {"MYS"
   [{:statute/id "mys.act-777-2016-companies-act"
     :statute/title "Companies Act 2016"
     :statute/jurisdiction "MYS"
     :statute/kind :law
     :statute/law-number "Act 777"
     :statute/url "https://lom.agc.gov.my/act-detail.php?act=777"
     :statute/url-provenance :official-lom-agc-gov-my
     :statute/enacted-date "2016-08-31"
     :statute/retrieved-at "2026-07-17"
     :statute/topic #{:corporate-governance :incorporation}}
    {:statute/id "mys.act-709-2010-personal-data-protection-act"
     :statute/title "Personal Data Protection Act 2010"
     :statute/jurisdiction "MYS"
     :statute/kind :law
     :statute/law-number "Act 709"
     :statute/url "https://www.investmalaysia.gov.my/media/3x4fsqum/personal-data-protection-act-2010.pdf"
     :statute/url-provenance :laws-of-malaysia-official-reprint-mirror
     :statute/enacted-date "2013-11-15"
     :statute/retrieved-at "2026-07-17"
     :statute/topic #{:data-protection :privacy}}]})

(defn spec-basis [jurisdiction] (get catalog jurisdiction))

(defn coverage
  ([] (coverage (keys catalog)))
  ([jurisdictions]
   (let [have (filter catalog jurisdictions)
         missing (remove catalog jurisdictions)]
     {:requested (count jurisdictions)
      :covered (count have)
      :covered-jurisdictions (vec (sort have))
      :missing-jurisdictions (vec (sort missing))
      :note (str "cloud-itonami-iso3166-mys statute.facts Wave 0 (ADR-2607141700): "
                 (count (get catalog "MYS")) " Malaysia entries seeded "
                 "with lom.agc.gov.my/investmalaysia.gov.my citations. "
                 "Extend `statute.facts/catalog`, never fabricate an id/url.")})))

(defn by-topic [jurisdiction topic]
  (filterv #(contains? (:statute/topic %) topic) (spec-basis jurisdiction)))
