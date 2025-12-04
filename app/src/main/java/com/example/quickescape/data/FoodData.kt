package com.example.quickescape.data

import com.example.quickescape.data.model.Food

object FoodData {
    val foods = listOf(
        // Coban Rondo - Malang area foods
        Food(
            id = "food_001",
            locationId = "coban_rondo",
            locationName = "Coban Rondo",
            name = "Pecel Madiun",
            price = 10000,
            image = "https://res.cloudinary.com/dwl82bdtv/image/upload/v1764835783/Pecel_Madiun_mplk1j.jpg",
            description = "Pecel khas Madiun dengan bumbu kacang yang khas",
            category = "Salad"
        ),
        Food(
            id = "food_002",
            locationId = "coban_rondo",
            locationName = "Coban Rondo",
            name = "Soto Malang",
            price = 15000,
            image = "https://res.cloudinary.com/dwl82bdtv/image/upload/v1764835908/Soto_Ayam_Lamongan_c4pf9e.jpg",
            description = "Soto khas Malang dengan daging sapi dan jeroan",
            category = "Soup"
        ),
        Food(
            id = "food_003",
            locationId = "coban_rondo",
            locationName = "Coban Rondo",
            name = "Bakwan Malang",
            price = 8000,
            image = "https://res.cloudinary.com/dwl82bdtv/image/upload/v1764835715/Bakmi_Jawa_puz1cd.jpg",
            description = "Bakwan goreng khas Malang dengan sayuran segar",
            category = "Fried"
        ),

        // Malioboro - Yogyakarta foods
        Food(
            id = "food_004",
            locationId = "malioboro",
            locationName = "Malioboro",
            name = "Gudeg Yogya",
            price = 18000,
            image = "https://res.cloudinary.com/dwl82bdtv/image/upload/v1764835761/Gudeg_cb4bzm.jpg",
            description = "Gudeg khas Yogyakarta dengan nangka muda dan bumbu manis",
            category = "Curry"
        ),
        Food(
            id = "food_005",
            locationId = "malioboro",
            locationName = "Malioboro",
            name = "Sayur Asem",
            price = 9000,
            image = "https://res.cloudinary.com/dwl82bdtv/image/upload/v1764835901/Sayur_Asem_tiyeta.jpg",
            description = "Sayur asem segar khas Jawa dengan rasa asam segar",
            category = "Soup"
        ),
        Food(
            id = "food_006",
            locationId = "malioboro",
            locationName = "Malioboro",
            name = "Ketoprak Yogya",
            price = 12000,
            image = "https://res.cloudinary.com/dwl82bdtv/image/upload/v1764835764/Ketoprak_zi4iyq.jpg",
            description = "Ketoprak khas Yogyakarta dengan bumbu kacang dan kerupuk",
            category = "Salad"
        ),

        // Gunung Bromo foods
        Food(
            id = "food_007",
            locationId = "gunung_bromo",
            locationName = "Gunung Bromo",
            name = "Bakso Malang",
            price = 15000,
            image = "https://res.cloudinary.com/dwl82bdtv/image/upload/v1764835716/Bakso_tc4n1e.jpg",
            description = "Bakso khas Malang dengan kuah gurih dan bakso kenyal",
            category = "Meatball"
        ),
        Food(
            id = "food_008",
            locationId = "gunung_bromo",
            locationName = "Gunung Bromo",
            name = "Rawon",
            price = 20000,
            image = "https://res.cloudinary.com/dwl82bdtv/image/upload/v1764835894/Rendang_ax4dw2.jpg",
            description = "Rawon daging sapi dengan kuah hitam khas Jawa Timur",
            category = "Soup"
        ),

        // Pantai Kuta - Bali foods
        Food(
            id = "food_009",
            locationId = "pantai_kuta",
            locationName = "Pantai Kuta",
            name = "Ayam Betutu",
            price = 45000,
            image = "https://res.cloudinary.com/dwl82bdtv/image/upload/v1764835713/Ayam_Betutu_e74wbx.jpg",
            description = "Ayam bumbu khas Bali yang dibungkus daun dan dipanggang",
            category = "Grilled"
        ),
        Food(
            id = "food_010",
            locationId = "pantai_kuta",
            locationName = "Pantai Kuta",
            name = "Sate Lilit",
            price = 22000,
            image = "https://res.cloudinary.com/dwl82bdtv/image/upload/v1764835898/Sate_Lilit_s6f1bp.jpg",
            description = "Sate lilit khas Bali dengan daging cincang dan bumbu khas",
            category = "Grilled"
        ),

        // Kawah Ijen foods
        Food(
            id = "food_011",
            locationId = "kawah_ijen",
            locationName = "Kawah Ijen",
            name = "Rujak Cingur",
            price = 12000,
            image = "https://res.cloudinary.com/dwl82bdtv/image/upload/v1764835894/Rujak_Cingur_zbveoc.jpg",
            description = "Rujak khas Jawa Timur dengan cingur dan sayuran segar",
            category = "Salad"
        ),
        Food(
            id = "food_012",
            locationId = "kawah_ijen",
            locationName = "Kawah Ijen",
            name = "Soto Lamongan",
            price = 14000,
            image = "https://res.cloudinary.com/dwl82bdtv/image/upload/v1764835908/Soto_Ayam_Lamongan_c4pf9e.jpg",
            description = "Soto ayam khas Lamongan dengan kuah bening dan koya",
            category = "Soup"
        ),

        // Candi Borobudur foods
        Food(
            id = "food_013",
            locationId = "candi_borobudur",
            locationName = "Candi Borobudur",
            name = "Gudeg Jogja",
            price = 18000,
            image = "https://res.cloudinary.com/dwl82bdtv/image/upload/v1764835761/Gudeg_cb4bzm.jpg",
            description = "Gudeg khas Yogyakarta dengan nangka muda dan bumbu manis",
            category = "Curry"
        ),
        Food(
            id = "food_014",
            locationId = "candi_borobudur",
            locationName = "Candi Borobudur",
            name = "Opor Ayam",
            price = 20000,
            image = "https://res.cloudinary.com/dwl82bdtv/image/upload/v1764835781/Opor_Ayam_qns80m.jpg",
            description = "Opor ayam khas Jawa dengan kuah santan yang gurih",
            category = "Curry"
        ),

        // Pantai Parangtritis foods
        Food(
            id = "food_015",
            locationId = "pantai_parangtritis",
            locationName = "Pantai Parangtritis",
            name = "Gado-gado",
            price = 12000,
            image = "https://res.cloudinary.com/dwl82bdtv/image/upload/v1764835720/Gado-gado_t660da.jpg",
            description = "Salad Indonesia dengan bumbu kacang yang lezat",
            category = "Salad"
        ),
        Food(
            id = "food_016",
            locationId = "pantai_parangtritis",
            locationName = "Pantai Parangtritis",
            name = "Kerak Telor",
            price = 16000,
            image = "https://res.cloudinary.com/dwl82bdtv/image/upload/v1764835763/Kerak_Telor_spkqgn.jpg",
            description = "Kerak telor khas Betawi dengan telur dan kelapa sangrai",
            category = "Traditional"
        ),

        // Taman Sari foods
        Food(
            id = "food_017",
            locationId = "taman_sari",
            locationName = "Taman Sari",
            name = "Ketoprak",
            price = 10000,
            image = "https://res.cloudinary.com/dwl82bdtv/image/upload/v1764835764/Ketoprak_zi4iyq.jpg",
            description = "Ketoprak Jakarta dengan tahu, tauge, dan bumbu kacang",
            category = "Salad"
        ),
        Food(
            id = "food_018",
            locationId = "taman_sari",
            locationName = "Taman Sari",
            name = "Sate Ayam",
            price = 17000,
            image = "https://res.cloudinary.com/dwl82bdtv/image/upload/v1764835897/Sate_Ayam_c6ya6u.jpg",
            description = "Sate ayam dengan bumbu kacang yang gurih",
            category = "Grilled"
        ),

        // Hutan Pinus Umbulrejo foods
        Food(
            id = "food_019",
            locationId = "hutan_pinus_umbulrejo",
            locationName = "Hutan Pinus Umbulrejo",
            name = "Bakmi Jawa",
            price = 13000,
            image = "https://res.cloudinary.com/dwl82bdtv/image/upload/v1764835715/Bakmi_Jawa_puz1cd.jpg",
            description = "Mie kuning khas Jawa dengan bumbu yang gurih",
            category = "Noodle"
        ),
        Food(
            id = "food_020",
            locationId = "hutan_pinus_umbulrejo",
            locationName = "Hutan Pinus Umbulrejo",
            name = "Nasi Goreng",
            price = 15000,
            image = "https://res.cloudinary.com/dwl82bdtv/image/upload/v1764835773/Nasi_Goreng_djiodq.jpg",
            description = "Nasi goreng khas Indonesia dengan bumbu rempah",
            category = "Rice"
        ),

        // Pantai Balekambang foods
        Food(
            id = "food_021",
            locationId = "pantai_balekambang",
            locationName = "Pantai Balekambang",
            name = "Bubur Ayam",
            price = 12000,
            image = "https://res.cloudinary.com/dwl82bdtv/image/upload/v1764835718/Bubur_Ayam_ifbhhi.jpg",
            description = "Bubur nasi dengan ayam suwir dan bumbu kuning",
            category = "Porridge"
        ),
        Food(
            id = "food_022",
            locationId = "pantai_balekambang",
            locationName = "Pantai Balekambang",
            name = "Asinan Bogor",
            price = 8000,
            image = "https://res.cloudinary.com/dwl82bdtv/image/upload/v1764835713/Asinan_Bogor_tg1v8x.jpg",
            description = "Asinan segar khas Bogor dengan buah-buahan",
            category = "Salad"
        ),

        // Museum Brawijaya foods
        Food(
            id = "food_023",
            locationId = "museum_brawijaya",
            locationName = "Museum Brawijaya",
            name = "Tahu Sumedang",
            price = 6000,
            image = "https://res.cloudinary.com/dwl82bdtv/image/upload/v1764835914/Tahu_Sumedang_kmkcv5.jpg",
            description = "Tahu goreng khas Sumedang yang renyah",
            category = "Fried"
        ),
        Food(
            id = "food_024",
            locationId = "museum_brawijaya",
            locationName = "Museum Brawijaya",
            name = "Es Cendol Dawet",
            price = 10000,
            image = "https://res.cloudinary.com/dwl82bdtv/image/upload/v1764835717/Es_Cendol_Dawet_ftm2du.jpg",
            description = "Minuman segar khas Jawa dengan cendol dan santan",
            category = "Dessert"
        ),

        // Sumber Maron foods
        Food(
            id = "food_025",
            locationId = "sumber_maron",
            locationName = "Sumber Maron",
            name = "Ayam Penyet",
            price = 18000,
            image = "https://res.cloudinary.com/dwl82bdtv/image/upload/v1764835714/Ayam_Penyet_wna4nm.jpg",
            description = "Ayam goreng yang dipenyet dengan sambal terasi",
            category = "Fried"
        ),
        Food(
            id = "food_026",
            locationId = "sumber_maron",
            locationName = "Sumber Maron",
            name = "Lontong Balap",
            price = 11000,
            image = "https://res.cloudinary.com/dwl82bdtv/image/upload/v1764835769/Lontong_Balap_z6cpyd.jpg",
            description = "Lontong dengan tauge, tahu, dan kuah petis",
            category = "Rice Cake"
        ),

        // Candi Prambanan foods
        Food(
            id = "food_027",
            locationId = "candi_prambanan",
            locationName = "Candi Prambanan",
            name = "Serabi",
            price = 8000,
            image = "https://res.cloudinary.com/dwl82bdtv/image/upload/v1764835904/Serabi_zuc62j.jpg",
            description = "Kue tradisional Jawa dengan topping manis",
            category = "Traditional"
        ),
        Food(
            id = "food_028",
            locationId = "candi_prambanan",
            locationName = "Candi Prambanan",
            name = "Nasi Kuning",
            price = 14000,
            image = "https://res.cloudinary.com/dwl82bdtv/image/upload/v1764835775/Nasi_Kuning_wyde5r.jpg",
            description = "Nasi kuning khas Jawa dengan lauk pauk",
            category = "Rice"
        ),

        // Keraton Yogyakarta foods
        Food(
            id = "food_029",
            locationId = "keraton_yogyakarta",
            locationName = "Keraton Yogyakarta",
            name = "Martabak Manis",
            price = 20000,
            image = "https://res.cloudinary.com/dwl82bdtv/image/upload/v1764835770/Martabak_Manis_ffuslg.jpg",
            description = "Martabak manis dengan berbagai topping",
            category = "Dessert"
        ),
        Food(
            id = "food_030",
            locationId = "keraton_yogyakarta",
            locationName = "Keraton Yogyakarta",
            name = "Martabak Telor",
            price = 15000,
            image = "https://res.cloudinary.com/dwl82bdtv/image/upload/v1764835771/Martabak_Telor_fjojyx.jpg",
            description = "Martabak telur dengan daging cincang",
            category = "Grilled"
        ),

        // Gunung Merapi foods
        Food(
            id = "food_031",
            locationId = "gunung_merapi",
            locationName = "Gunung Merapi",
            name = "Nasi Liwet",
            price = 16000,
            image = "https://res.cloudinary.com/dwl82bdtv/image/upload/v1764835776/Nasi_Liwet_rvlogz.jpg",
            description = "Nasi liwet khas Solo dengan kuah gurih",
            category = "Rice"
        ),
        Food(
            id = "food_032",
            locationId = "gunung_merapi",
            locationName = "Gunung Merapi",
            name = "Kupat Tahu",
            price = 12000,
            image = "https://res.cloudinary.com/dwl82bdtv/image/upload/v1764835766/Kupat_Tahu_kwb07o.jpg",
            description = "Ketupat dengan tahu dan bumbu kacang",
            category = "Rice Cake"
        ),

        // Tanah Lot foods
        Food(
            id = "food_033",
            locationId = "tanah_lot",
            locationName = "Tanah Lot",
            name = "Sambal Matah",
            price = 25000,
            image = "https://res.cloudinary.com/dwl82bdtv/image/upload/v1764835896/Sambal_Matah_epog01.jpg",
            description = "Sambal khas Bali dengan bawang merah dan sereh",
            category = "Condiment"
        ),
        Food(
            id = "food_034",
            locationId = "tanah_lot",
            locationName = "Tanah Lot",
            name = "Nasi Uduk",
            price = 18000,
            image = "https://res.cloudinary.com/dwl82bdtv/image/upload/v1764835780/Nasi_Uduk_fivnsw.jpg",
            description = "Nasi uduk dengan lauk pauk lengkap",
            category = "Rice"
        ),

        // Ubud Monkey Forest foods
        Food(
            id = "food_035",
            locationId = "ubud_monkey_forest",
            locationName = "Ubud Monkey Forest",
            name = "Rendang",
            price = 35000,
            image = "https://res.cloudinary.com/dwl82bdtv/image/upload/v1764835894/Rendang_ax4dw2.jpg",
            description = "Rendang daging sapi khas Minang yang pedas gurih",
            category = "Curry"
        ),
        Food(
            id = "food_036",
            locationId = "ubud_monkey_forest",
            locationName = "Ubud Monkey Forest",
            name = "Lemper",
            price = 12000,
            image = "https://res.cloudinary.com/dwl82bdtv/image/upload/v1764835767/Lemper_a6fjss.jpg",
            description = "Ketan dengan isian ayam dibungkus daun pisang",
            category = "Traditional"
        ),

        // Tegallalang Rice Terrace foods
        Food(
            id = "food_037",
            locationId = "tegallalang_rice_terrace",
            locationName = "Tegallalang Rice Terrace",
            name = "Nasi Padang",
            price = 25000,
            image = "https://res.cloudinary.com/dwl82bdtv/image/upload/v1764835778/Nasi_Padang_sova6j.jpg",
            description = "Nasi putih dengan berbagai lauk khas Padang",
            category = "Rice"
        ),
        Food(
            id = "food_038",
            locationId = "tegallalang_rice_terrace",
            locationName = "Tegallalang Rice Terrace",
            name = "Kwetiau Siram Goreng",
            price = 20000,
            image = "https://res.cloudinary.com/dwl82bdtv/image/upload/v1764835767/Kwetiau_Siram_Goreng_u4fffh.jpg",
            description = "Kwetiau goreng dengan kuah siram yang gurih",
            category = "Noodle"
        ),

        // Uluwatu Temple foods
        Food(
            id = "food_039",
            locationId = "uluwatu_temple",
            locationName = "Uluwatu Temple",
            name = "Sate Padang",
            price = 22000,
            image = "https://res.cloudinary.com/dwl82bdtv/image/upload/v1764835899/Sate_Padang_g8qfdk.jpg",
            description = "Sate daging dengan bumbu kuah kental khas Padang",
            category = "Grilled"
        ),
        Food(
            id = "food_040",
            locationId = "uluwatu_temple",
            locationName = "Uluwatu Temple",
            name = "Tahu Gejrot",
            price = 9000,
            image = "https://res.cloudinary.com/dwl82bdtv/image/upload/v1764835913/Tahu_Gejrot_ldqo0x.jpg",
            description = "Tahu goreng dengan kuah asam manis pedas",
            category = "Fried"
        ),

        // Pantai Sanur foods
        Food(
            id = "food_041",
            locationId = "pantai_sanur",
            locationName = "Pantai Sanur",
            name = "Gulai Ikan",
            price = 28000,
            image = "https://res.cloudinary.com/dwl82bdtv/image/upload/v1764835762/Gulai_Ikan_xq52qs.jpg",
            description = "Ikan dalam kuah santan dengan rempah khas",
            category = "Curry"
        ),
        Food(
            id = "food_042",
            locationId = "pantai_sanur",
            locationName = "Pantai Sanur",
            name = "Otak-otak",
            price = 15000,
            image = "https://res.cloudinary.com/dwl82bdtv/image/upload/v1764835782/Otak-otak_douqt4.jpg",
            description = "Ikan giling dengan bumbu dibungkus daun pisang",
            category = "Grilled"
        ),

        // Gunung Batur foods
        Food(
            id = "food_043",
            locationId = "gunung_batur",
            locationName = "Gunung Batur",
            name = "Soto Betawi",
            price = 18000,
            image = "https://res.cloudinary.com/dwl82bdtv/image/upload/v1764835911/Soto_Betawi_dfrpwk.jpg",
            description = "Soto khas Betawi dengan kuah santan",
            category = "Soup"
        ),
        Food(
            id = "food_044",
            locationId = "gunung_batur",
            locationName = "Gunung Batur",
            name = "Pempek",
            price = 20000,
            image = "https://res.cloudinary.com/dwl82bdtv/image/upload/v1764835784/Pempek_zet7mt.jpg",
            description = "Pempek khas Palembang dengan kuah cuko",
            category = "Fish Cake"
        ),

        // Nusa Penida foods
        Food(
            id = "food_045",
            locationId = "nusa_penida",
            locationName = "Nusa Penida",
            name = "Ayam Taliwang",
            price = 30000,
            image = "https://res.cloudinary.com/dwl82bdtv/image/upload/v1764835719/Ayam_Taliwang_m6nv1b.jpg",
            description = "Ayam bakar khas Lombok dengan bumbu pedas",
            category = "Grilled"
        ),
        Food(
            id = "food_046",
            locationId = "nusa_penida",
            locationName = "Nusa Penida",
            name = "Soto Banjar",
            price = 16000,
            image = "https://res.cloudinary.com/dwl82bdtv/image/upload/v1764835910/Soto_Banjar_cosmxb.jpg",
            description = "Soto khas Banjarmasin dengan ayam dan perkedel",
            category = "Soup"
        ),

        // Seminyak Beach foods
        Food(
            id = "food_047",
            locationId = "seminyak_beach",
            locationName = "Seminyak Beach",
            name = "Bika Ambon",
            price = 15000,
            image = "https://res.cloudinary.com/dwl82bdtv/image/upload/v1764835721/Bika_Ambon_hiueai.jpg",
            description = "Kue khas Medan dengan tekstur berpori",
            category = "Dessert"
        ),
        Food(
            id = "food_048",
            locationId = "seminyak_beach",
            locationName = "Seminyak Beach",
            name = "Mie Aceh",
            price = 22000,
            image = "https://res.cloudinary.com/dwl82bdtv/image/upload/v1764835772/Mie_Aceh_mzgrob.jpg",
            description = "Mie kuah pedas khas Aceh dengan daging",
            category = "Noodle"
        ),

        // Tirta Empul foods
        Food(
            id = "food_049",
            locationId = "tirta_empul",
            locationName = "Tirta Empul",
            name = "Ayam Woku",
            price = 28000,
            image = "https://res.cloudinary.com/dwl82bdtv/image/upload/v1764835714/Ayam_Woku_efatte.jpg",
            description = "Ayam bumbu woku khas Manado yang pedas",
            category = "Curry"
        ),
        Food(
            id = "food_050",
            locationId = "tirta_empul",
            locationName = "Tirta Empul",
            name = "Bubur Manado",
            price = 14000,
            image = "https://res.cloudinary.com/dwl82bdtv/image/upload/v1764835736/Bubur_Manado_y6eo0v.jpg",
            description = "Bubur tinutuan khas Manado dengan sayuran",
            category = "Porridge"
        ),

        // Tegenungan Waterfall foods
        Food(
            id = "food_051",
            locationId = "tegenungan_waterfall",
            locationName = "Tegenungan Waterfall",
            name = "Sop Buntut",
            price = 35000,
            image = "https://res.cloudinary.com/dwl82bdtv/image/upload/v1764835902/Sop_Buntut_zzvzuq.jpg",
            description = "Sup buntut sapi dengan kuah bening yang gurih",
            category = "Soup"
        ),
        Food(
            id = "food_052",
            locationId = "tegenungan_waterfall",
            locationName = "Tegenungan Waterfall",
            name = "Coto Makassar",
            price = 25000,
            image = "https://res.cloudinary.com/dwl82bdtv/image/upload/v1764835760/Coto_Makassar_hewr4t.jpg",
            description = "Sup daging khas Makassar dengan bumbu kacang",
            category = "Soup"
        ),

        // Pantai Pandawa foods
        Food(
            id = "food_053",
            locationId = "pantai_pandawa",
            locationName = "Pantai Pandawa",
            name = "Sop Konro",
            price = 30000,
            image = "https://res.cloudinary.com/dwl82bdtv/image/upload/v1764835905/Sop_Konro_l94n7e.jpg",
            description = "Sup iga sapi khas Makassar dengan bumbu hitam",
            category = "Soup"
        ),
        Food(
            id = "food_054",
            locationId = "pantai_pandawa",
            locationName = "Pantai Pandawa",
            name = "Sop Saudara",
            price = 18000,
            image = "https://res.cloudinary.com/dwl82bdtv/image/upload/v1764835907/Sop_Saudara_bhtuqn.jpg",
            description = "Sup daging khas Makassar dengan mie dan telur",
            category = "Soup"
        ),

        // Jakarta locations foods
        // Monas foods
        Food(
            id = "food_055",
            locationId = "monas",
            locationName = "Monas",
            name = "Ketoprak Jakarta",
            price = 12000,
            image = "https://res.cloudinary.com/dwl82bdtv/image/upload/v1764835764/Ketoprak_zi4iyq.jpg",
            description = "Ketoprak khas Jakarta dengan tahu dan kerupuk",
            category = "Salad"
        ),
        Food(
            id = "food_056",
            locationId = "monas",
            locationName = "Monas",
            name = "Kerak Telor Jakarta",
            price = 18000,
            image = "https://res.cloudinary.com/dwl82bdtv/image/upload/v1764835763/Kerak_Telor_spkqgn.jpg",
            description = "Kerak telor khas Betawi dengan kelapa sangrai",
            category = "Traditional"
        ),

        // Kota Tua Jakarta foods
        Food(
            id = "food_057",
            locationId = "kota_tua_jakarta",
            locationName = "Kota Tua Jakarta",
            name = "Gado-gado Jakarta",
            price = 15000,
            image = "https://res.cloudinary.com/dwl82bdtv/image/upload/v1764835720/Gado-gado_t660da.jpg",
            description = "Salad Indonesia dengan bumbu kacang yang lezat",
            category = "Salad"
        ),
        Food(
            id = "food_058",
            locationId = "kota_tua_jakarta",
            locationName = "Kota Tua Jakarta",
            name = "Soto Ayam Betawi",
            price = 16000,
            image = "https://res.cloudinary.com/dwl82bdtv/image/upload/v1764835908/Soto_Ayam_Lamongan_c4pf9e.jpg",
            description = "Soto ayam khas Betawi dengan kuah gurih",
            category = "Soup"
        ),

        // Ancol Dreamland foods
        Food(
            id = "food_059",
            locationId = "ancol_dreamland",
            locationName = "Ancol Dreamland",
            name = "Bakso Jakarta",
            price = 18000,
            image = "https://res.cloudinary.com/dwl82bdtv/image/upload/v1764835716/Bakso_tc4n1e.jpg",
            description = "Bakso dengan kuah gurih dan mie",
            category = "Meatball"
        ),
        Food(
            id = "food_060",
            locationId = "ancol_dreamland",
            locationName = "Ancol Dreamland",
            name = "Nasi Uduk Betawi",
            price = 20000,
            image = "https://res.cloudinary.com/dwl82bdtv/image/upload/v1764835780/Nasi_Uduk_fivnsw.jpg",
            description = "Nasi uduk khas Betawi dengan lauk lengkap",
            category = "Rice"
        ),

        // Taman Mini Indonesia Indah foods
        Food(
            id = "food_061",
            locationId = "taman_mini_indonesia_indah",
            locationName = "Taman Mini Indonesia Indah",
            name = "Rendang Padang",
            price = 35000,
            image = "https://res.cloudinary.com/dwl82bdtv/image/upload/v1764835894/Rendang_ax4dw2.jpg",
            description = "Rendang daging sapi khas Padang yang autentik",
            category = "Curry"
        ),
        Food(
            id = "food_062",
            locationId = "taman_mini_indonesia_indah",
            locationName = "Taman Mini Indonesia Indah",
            name = "Martabak Har",
            price = 25000,
            image = "https://res.cloudinary.com/dwl82bdtv/image/upload/v1764835771/Martabak_Telor_fjojyx.jpg",
            description = "Martabak telur dengan isian daging yang lezat",
            category = "Grilled"
        ),

        // Bandung locations foods
        // Kawah Putih foods
        Food(
            id = "food_063",
            locationId = "kawah_putih",
            locationName = "Kawah Putih",
            name = "Tahu Sumedang Asli",
            price = 8000,
            image = "https://res.cloudinary.com/dwl82bdtv/image/upload/v1764835914/Tahu_Sumedang_kmkcv5.jpg",
            description = "Tahu goreng khas Sumedang yang renyah dan gurih",
            category = "Fried"
        ),
        Food(
            id = "food_064",
            locationId = "kawah_putih",
            locationName = "Kawah Putih",
            name = "Mie Ayam Bandung",
            price = 14000,
            image = "https://res.cloudinary.com/dwl82bdtv/image/upload/v1764835715/Bakmi_Jawa_puz1cd.jpg",
            description = "Mie ayam khas Bandung dengan pangsit",
            category = "Noodle"
        ),

        // Tangkuban Perahu foods
        Food(
            id = "food_065",
            locationId = "tangkuban_perahu",
            locationName = "Tangkuban Perahu",
            name = "Batagor Bandung",
            price = 12000,
            image = "https://res.cloudinary.com/dwl82bdtv/image/upload/v1764835716/Bakso_tc4n1e.jpg",
            description = "Baso tahu goreng khas Bandung dengan bumbu kacang",
            category = "Fried"
        ),
        Food(
            id = "food_066",
            locationId = "tangkuban_perahu",
            locationName = "Tangkuban Perahu",
            name = "Soto Bandung",
            price = 16000,
            image = "https://res.cloudinary.com/dwl82bdtv/image/upload/v1764835911/Soto_Betawi_dfrpwk.jpg",
            description = "Soto khas Bandung dengan daging dan lobak",
            category = "Soup"
        ),

        // Gedung Sate foods
        Food(
            id = "food_067",
            locationId = "gedung_sate",
            locationName = "Gedung Sate",
            name = "Siomay Bandung",
            price = 10000,
            image = "https://res.cloudinary.com/dwl82bdtv/image/upload/v1764835782/Otak-otak_douqt4.jpg",
            description = "Siomay khas Bandung dengan bumbu kacang",
            category = "Steamed"
        ),
        Food(
            id = "food_068",
            locationId = "gedung_sate",
            locationName = "Gedung Sate",
            name = "Cireng",
            price = 8000,
            image = "https://res.cloudinary.com/dwl82bdtv/image/upload/v1764835914/Tahu_Sumedang_kmkcv5.jpg",
            description = "Aci digoreng khas Sunda yang kenyal",
            category = "Fried"
        ),

        // Farmhouse Lembang foods
        Food(
            id = "food_069",
            locationId = "farmhouse_lembang",
            locationName = "Farmhouse Lembang",
            name = "Pisang Molen",
            price = 12000,
            image = "https://res.cloudinary.com/dwl82bdtv/image/upload/v1764835770/Martabak_Manis_ffuslg.jpg",
            description = "Pisang yang dibungkus kulit lumpia dan digoreng",
            category = "Dessert"
        ),
        Food(
            id = "food_070",
            locationId = "farmhouse_lembang",
            locationName = "Farmhouse Lembang",
            name = "Bandrek",
            price = 10000,
            image = "https://res.cloudinary.com/dwl82bdtv/image/upload/v1764835717/Es_Cendol_Dawet_ftm2du.jpg",
            description = "Minuman hangat khas Sunda dengan jahe",
            category = "Beverage"
        ),

        // Other Java locations foods
        // Pantai Pangandaran foods
        Food(
            id = "food_071",
            locationId = "pantai_pangandaran",
            locationName = "Pantai Pangandaran",
            name = "Pepes Ikan",
            price = 25000,
            image = "https://res.cloudinary.com/dwl82bdtv/image/upload/v1764835762/Gulai_Ikan_xq52qs.jpg",
            description = "Ikan bumbu yang dibungkus daun pisang",
            category = "Steamed"
        ),
        Food(
            id = "food_072",
            locationId = "pantai_pangandaran",
            locationName = "Pantai Pangandaran",
            name = "Rujak Pantai",
            price = 10000,
            image = "https://res.cloudinary.com/dwl82bdtv/image/upload/v1764835894/Rujak_Cingur_zbveoc.jpg",
            description = "Rujak segar dengan buah-buahan tropis",
            category = "Salad"
        ),

        // Green Canyon foods
        Food(
            id = "food_073",
            locationId = "green_canyon",
            locationName = "Green Canyon",
            name = "Nasi Liwet Sunda",
            price = 18000,
            image = "https://res.cloudinary.com/dwl82bdtv/image/upload/v1764835776/Nasi_Liwet_rvlogz.jpg",
            description = "Nasi liwet khas Sunda dengan lauk tradisional",
            category = "Rice"
        ),
        Food(
            id = "food_074",
            locationId = "green_canyon",
            locationName = "Green Canyon",
            name = "Ayam Bakar Sunda",
            price = 22000,
            image = "https://res.cloudinary.com/dwl82bdtv/image/upload/v1764835714/Ayam_Penyet_wna4nm.jpg",
            description = "Ayam bakar dengan bumbu khas Sunda",
            category = "Grilled"
        ),

        // Continue adding foods for remaining locations...
        // Gunung Semeru foods
        Food(
            id = "food_075",
            locationId = "gunung_semeru",
            locationName = "Gunung Semeru",
            name = "Rawon Surabaya",
            price = 22000,
            image = "https://res.cloudinary.com/dwl82bdtv/image/upload/v1764835894/Rendang_ax4dw2.jpg",
            description = "Rawon daging sapi dengan kuah hitam khas Jawa Timur",
            category = "Soup"
        ),
        Food(
            id = "food_076",
            locationId = "gunung_semeru",
            locationName = "Gunung Semeru",
            name = "Bakso Malang Asli",
            price = 18000,
            image = "https://res.cloudinary.com/dwl82bdtv/image/upload/v1764835716/Bakso_tc4n1e.jpg",
            description = "Bakso khas Malang dengan aneka varian",
            category = "Meatball"
        ),

        // Pulau Sempu foods
        Food(
            id = "food_077",
            locationId = "pulau_sempu",
            locationName = "Pulau Sempu",
            name = "Ikan Bakar Jimbaran",
            price = 35000,
            image = "https://res.cloudinary.com/dwl82bdtv/image/upload/v1764835762/Gulai_Ikan_xq52qs.jpg",
            description = "Ikan laut segar dibakar dengan bumbu khas",
            category = "Grilled"
        ),
        Food(
            id = "food_078",
            locationId = "pulau_sempu",
            locationName = "Pulau Sempu",
            name = "Sayur Asem Segar",
            price = 12000,
            image = "https://res.cloudinary.com/dwl82bdtv/image/upload/v1764835901/Sayur_Asem_tiyeta.jpg",
            description = "Sayur asem dengan sayuran segar dan rasa asam",
            category = "Soup"
        ),

        // Pantai Klayar foods
        Food(
            id = "food_079",
            locationId = "pantai_klayar",
            locationName = "Pantai Klayar",
            name = "Gudeg Jogja Asli",
            price = 20000,
            image = "https://res.cloudinary.com/dwl82bdtv/image/upload/v1764835761/Gudeg_cb4bzm.jpg",
            description = "Gudeg khas Yogyakarta dengan cita rasa manis",
            category = "Curry"
        ),
        Food(
            id = "food_080",
            locationId = "pantai_klayar",
            locationName = "Pantai Klayar",
            name = "Sate Klathak",
            price = 25000,
            image = "https://res.cloudinary.com/dwl82bdtv/image/upload/v1764835897/Sate_Ayam_c6ya6u.jpg",
            description = "Sate kambing khas Yogyakarta tanpa bumbu kacang",
            category = "Grilled"
        ),

        // Candi Sukuh foods
        Food(
            id = "food_081",
            locationId = "candi_sukuh",
            locationName = "Candi Sukuh",
            name = "Nasi Pecel Solo",
            price = 14000,
            image = "https://res.cloudinary.com/dwl82bdtv/image/upload/v1764835783/Pecel_Madiun_mplk1j.jpg",
            description = "Nasi dengan sayuran rebus dan sambal pecel",
            category = "Rice"
        ),
        Food(
            id = "food_082",
            locationId = "candi_sukuh",
            locationName = "Candi Sukuh",
            name = "Serabi Solo",
            price = 10000,
            image = "https://res.cloudinary.com/dwl82bdtv/image/upload/v1764835904/Serabi_zuc62j.jpg",
            description = "Serabi khas Solo dengan berbagai topping",
            category = "Traditional"
        ),

        // Taman Safari Prigen foods
        Food(
            id = "food_083",
            locationId = "taman_safari_prigen",
            locationName = "Taman Safari Prigen",
            name = "Soto Lamongan Asli",
            price = 16000,
            image = "https://res.cloudinary.com/dwl82bdtv/image/upload/v1764835908/Soto_Ayam_Lamongan_c4pf9e.jpg",
            description = "Soto ayam khas Lamongan dengan koya dan kerupuk",
            category = "Soup"
        ),
        Food(
            id = "food_084",
            locationId = "taman_safari_prigen",
            locationName = "Taman Safari Prigen",
            name = "Lontong Kupang",
            price = 15000,
            image = "https://res.cloudinary.com/dwl82bdtv/image/upload/v1764835769/Lontong_Balap_z6cpyd.jpg",
            description = "Lontong dengan kuah kupang khas Surabaya",
            category = "Rice Cake"
        )
    )
}
