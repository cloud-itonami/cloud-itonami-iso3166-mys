# Business Model: Independent Public-Sector Market-Entry & Procurement Compliance Service — Malaysia

## Classification

- Repository: `cloud-itonami-iso3166-mys`
- ISO 3166: `MYS` (Malaysia)
- Activity: public-procurement market-entry and ongoing regulatory-
  compliance navigation for an already-incorporated operator
- Social impact: [:mof-certificate-validity-clarity :public-spend-transparency :cross-border-friction-reduction]

## Customer

- an already-incorporated `cloud-itonami-cofog-{code}` /
  `cloud-itonami-isco-{code}` / `cloud-itonami-unspsc-{segment}` /
  `cloud-itonami-{ISIC}` operator wanting to bid on a Malaysian
  public contract
- a foreign SME or civic-tech vendor entering the public sector in
  Malaysia for the first time
- a `cloud-itonami-M6910` client that has just completed incorporation and
  now needs public-sector market access

## Offer

- registration walkthrough for ePerolehan / MyProcurement, Malaysia's
  official government e-procurement system operated by the Ministry of
  Finance (Kementerian Kewangan Malaysia), including obtaining and
  RENEWING the MOF Certificate (3-year validity, RM450 registration/
  renewal fee)
- business/tax registration checklist: SSM (Suruhanjaya Syarikat
  Malaysia / Companies Commission of Malaysia) company or business
  registration under the Companies Act 2016 (Act 777) / Registration of
  Businesses Act 1956; LHDN (Lembaga Hasil Dalam Negeri Malaysia) tax
  registration and TIN assignment
- MOF Certificate validity-window monitoring: proactive alerts before a
  client's MOF Certificate lapses its 3-year window, so a submission is
  never blocked by a routine, avoidable expiry
- ongoing regulatory-change monitoring subscription
- compliance-audit export package for the client's own records

## Revenue

- per-engagement market-entry fee (one-time registration + checklist
  completion)
- recurring regulatory-change / MOF Certificate renewal-window
  monitoring subscription
- compliance-audit export package

## Trust Controls

- any actual portal registration or filing submission requires
  Market-Entry Compliance Governor clearance and always escalates to
  human sign-off (`:filing/submit` is never automated at any phase)
- a false or fabricated regulatory-requirement claim is a HARD hold that
  cannot be overridden by human approval alone — it must be corrected
  against a cited official source first
- an MOF Certificate whose independently recomputed 3-year validity
  window has already lapsed by the declared filing date is a HARD hold
  that cannot be overridden by human approval alone — it must be
  renewed via ePerolehan/MyProcurement first (the flagship check; see
  `src/marketentry/governor.cljc`)
- this service does **not** provide legal or tax advice; characterization
  and filing on the client's behalf beyond checklist/draft assistance
  routes to Malaysian-licensed counsel or a registered agent
- every requirement cites the official portal or regulation, never
  invented

## Honest gaps (disclosed, not papered over)

- Bumiputera equity-status certification (STB / BCPLC) is a REAL
  Malaysian government-procurement mechanism (MITI: "the sole agency
  for BCPLC status recognition... per all the criteria in LB1.1
  Treasury Circular") but this actor does NOT assert or gate on a
  specific Bumiputera equity percentage threshold: the Treasury
  Circular's exact criteria and the Ministry of Entrepreneur
  Development and Cooperatives' STB guideline PDF could not be
  independently fetched this session (the latter failed on a TLS
  hostname mismatch, not a bot-detection block). A future iteration
  that re-fetches and reads those primary sources could add a
  Bumiputera-status check; today's catalog does not claim coverage it
  hasn't verified.

## Boundary with adjacent actors (read before forking)

- **`com-etzhayyim-ooyake`** (etzhayyim/root): read-only civic-wayfinding
  mirror of government structure, non-commercial, barred from acting as
  or for the government (G3 impersonation ban). This blueprint is
  commercial and never claims to be an official channel.
- **`matsurigoto`** (etzhayyim/root): sovereign e-government statecraft —
  literally the government, for etzhayyim's own covenant or an adopting
  nation-state. This blueprint is an independent operator the government
  contracts with or that bids into its procurement — never the
  government.
- **`com-etzhayyim-toritsugi`** (etzhayyim/root): guides a consenting
  INDIVIDUAL citizen through their OWN procedure, non-profit,
  donation-only. This blueprint's client is a business operator, not an
  individual citizen, and it is commercial.
- **`legal-entity.etzhayyim.com`**: read-only aggregated company-registry
  data, no execution. This blueprint executes (gated) registrations.
- **`cloud-itonami-M6910`**: helps a client BECOME a legal entity
  (incorporation, ISIC 6910) — a prior, different regulatory phase
  (company law). This blueprint assumes incorporation is already done and
  handles public-procurement market entry (a different regulatory domain).
- **`cloud-itonami-cofog-{code}`**: a jurisdiction-agnostic operator
  template for ONE public function. This blueprint is the orthogonal
  jurisdiction-specific axis — the two compose (fork a COFOG-function
  blueprint AND this one to operate in Malaysia).
