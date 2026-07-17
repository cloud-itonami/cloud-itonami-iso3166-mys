(ns culture.facts
  "Country-level regional-culture catalog for Malaysia (MYS) -- national
  dishes, protected products, beverages, crafts, festivals and heritage
  sites, per ADR-2607171400 addendum 2 (cloud-itonami-municipality-
  culture-catalog Wave 1, in com-junkawasaki/root). Sibling namespace to
  `marketentry.facts` / `statute.facts` (ADR-2607141700); city-level
  counterparts live in the cloud-itonami-municipality-* repos.

  Catalog is keyed by UPPERCASE ISO3 (mirrors `statute.facts`); entries
  carry no :culture/municipality (that attribute is city-level only).

  Every entry cites a source URL that was actually fetched and read on
  :culture/retrieved-at -- never fabricated. Summaries state only what the
  cited source confirms. An item not in this table has NO spec-basis, full
  stop; extend `catalog`, do not invent an id/url.")

(def catalog
  "iso3 -> vector of culture entries."
  {"MYS"
   [{:culture/id "mys.dish.nasi-lemak"
     :culture/name "Nasi lemak"
     :culture/country "MYS"
     :culture/kind :dish
     :culture/summary "Traditional Malay dish of rice cooked in coconut milk and pandan leaf, commonly found in Malaysia where it is considered the national dish."
     :culture/url "https://en.wikipedia.org/wiki/Nasi_lemak"
     :culture/url-provenance :wikipedia-en
     :culture/retrieved-at "2026-07-17"}
    {:culture/id "mys.dish.roti-canai"
     :culture/name "Roti canai"
     :culture/country "MYS"
     :culture/kind :dish
     :culture/summary "Unleavened flatbread of Indian origin that became a popular breakfast and snack dish in Malaysia, and a component of Malaysia's breakfast culture inscribed on UNESCO's Intangible Cultural Heritage list in 2024."
     :culture/url "https://en.wikipedia.org/wiki/Roti_canai"
     :culture/url-provenance :wikipedia-en
     :culture/retrieved-at "2026-07-17"}
    {:culture/id "mys.dish.char-kway-teow"
     :culture/name "Char kway teow"
     :culture/country "MYS"
     :culture/kind :dish
     :culture/summary "Stir-fried rice-noodle dish from Maritime Southeast Asia whose place of origin is given as Malaysia and Singapore, particularly popular in both countries."
     :culture/url "https://en.wikipedia.org/wiki/Char_kway_teow"
     :culture/url-provenance :wikipedia-en
     :culture/retrieved-at "2026-07-17"}
    {:culture/id "mys.dish.nasi-kandar"
     :culture/name "Nasi kandar"
     :culture/country "MYS"
     :culture/kind :dish
     :culture/summary "Popular northern Malaysian dish from Penang of steamed rice with curries and side dishes, originally introduced by Tamil Muslim traders from India."
     :culture/url "https://en.wikipedia.org/wiki/Nasi_kandar"
     :culture/url-provenance :wikipedia-en
     :culture/retrieved-at "2026-07-17"}
    {:culture/id "mys.beverage.teh-tarik"
     :culture/name "Teh tarik"
     :culture/country "MYS"
     :culture/kind :beverage
     :culture/summary "Hot milk tea of black tea and condensed milk, described as the unofficial national drink of Malaysia."
     :culture/url "https://en.wikipedia.org/wiki/Teh_tarik"
     :culture/url-provenance :wikipedia-en
     :culture/retrieved-at "2026-07-17"}
    {:culture/id "mys.product.musang-king"
     :culture/name "Musang King"
     :culture/name-local "Raja Kunyit"
     :culture/country "MYS"
     :culture/kind :product
     :culture/summary "Malaysian durian cultivar originally known as Raja Kunyit, first planted in Kelantan and later propagated in Raub, Pahang."
     :culture/url "https://en.wikipedia.org/wiki/Musang_King"
     :culture/url-provenance :wikipedia-en
     :culture/retrieved-at "2026-07-17"}
    {:culture/id "mys.craft.songket"
     :culture/name "Songket"
     :culture/country "MYS"
     :culture/kind :craft
     :culture/summary "Hand-woven brocade textile patterned with gold or silver metallic threads; it originated in Palembang, Sumatra and spread across Maritime Southeast Asia, and was recognized by UNESCO from Malaysia in 2021."
     :culture/url "https://en.wikipedia.org/wiki/Songket"
     :culture/url-provenance :wikipedia-en
     :culture/retrieved-at "2026-07-17"}
    {:culture/id "mys.craft.wau-bulan"
     :culture/name "Wau bulan"
     :culture/country "MYS"
     :culture/kind :craft
     :culture/summary "Elaborately designed Malaysian moon kite traditionally flown in Kelantan, one of Malaysia's national symbols and featured on the 1989-series fifty-cent coin."
     :culture/url "https://en.wikipedia.org/wiki/Wau_bulan"
     :culture/url-provenance :wikipedia-en
     :culture/retrieved-at "2026-07-17"}
    {:culture/id "mys.festival.kaamatan"
     :culture/name "Kaamatan"
     :culture/country "MYS"
     :culture/kind :festival
     :culture/summary "Harvest festival celebrated on 30 and 31 May annually in the state of Sabah, Malaysia, by the Kadazan, Dusun and Murut peoples."
     :culture/url "https://en.wikipedia.org/wiki/Kaamatan"
     :culture/url-provenance :wikipedia-en
     :culture/retrieved-at "2026-07-17"}
    {:culture/id "mys.heritage.melaka-george-town"
     :culture/name "Melaka and George Town, Historic Cities of the Straits of Malacca"
     :culture/country "MYS"
     :culture/kind :heritage
     :culture/summary "Two historic urban centres in Malaysia inscribed as a UNESCO World Heritage Site in 2008, together illustrating 500 years of cultural and trade exchange between East and West."
     :culture/url "https://en.wikipedia.org/wiki/Melaka_and_George_Town,_Historic_Cities_of_the_Straits_of_Malacca"
     :culture/url-provenance :wikipedia-en
     :culture/retrieved-at "2026-07-17"}]})

(defn spec-basis [iso3] (get catalog iso3))

(defn coverage
  ([] (coverage (keys catalog)))
  ([iso3s]
   (let [have (filter catalog iso3s)
         missing (remove catalog iso3s)]
     {:requested (count iso3s)
      :covered (count have)
      :covered-jurisdictions (vec (sort have))
      :missing-jurisdictions (vec (sort missing))
      :note (str "cloud-itonami-iso3166-mys culture catalog "
                 "(ADR-2607171400 addendum 2, Wave 1): " (count (get catalog "MYS"))
                 " MYS entries, each with a fetched-and-read citation. "
                 "Extend `culture.facts/catalog`, never fabricate an id/url.")})))

(defn by-kind [iso3 kind]
  (filterv #(= (:culture/kind %) kind) (spec-basis iso3)))
