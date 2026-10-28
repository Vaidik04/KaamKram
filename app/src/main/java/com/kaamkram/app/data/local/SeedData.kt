package com.kaamkram.app.data.local

import com.kaamkram.app.data.local.entity.PriceBookItemEntity

object SeedData {

    fun getElectricianSeedItems(): List<PriceBookItemEntity> = listOf(
        PriceBookItemEntity(
            id = "elec_fan_install",
            tradeCategory = "ELECTRICIAN",
            name = "Ceiling Fan Installation / Replacement",
            description = "Unmounting old fan, assembling & mounting new ceiling fan with regulator test",
            defaultUnitPricePaise = 25000L, // ₹250
            unit = "unit",
            isPartMaterial = false,
            sortOrder = 1
        ),
        PriceBookItemEntity(
            id = "elec_switchboard",
            tradeCategory = "ELECTRICIAN",
            name = "Switchboard Wiring / Point Repair",
            description = "Repairing or replacing single switch/socket point including wiring check",
            defaultUnitPricePaise = 15000L, // ₹150
            unit = "point",
            isPartMaterial = false,
            sortOrder = 2
        ),
        PriceBookItemEntity(
            id = "elec_mcb_replace",
            tradeCategory = "ELECTRICIAN",
            name = "MCB / Distribution Board Repair",
            description = "Diagnosing tripping MCB, replacing single/double pole breaker",
            defaultUnitPricePaise = 35000L, // ₹350
            unit = "unit",
            isPartMaterial = false,
            sortOrder = 3
        ),
        PriceBookItemEntity(
            id = "elec_inverter_conn",
            tradeCategory = "ELECTRICIAN",
            name = "Inverter & Battery Setup",
            description = "Installing inverter unit, connecting battery terminals with acid/distilled water check",
            defaultUnitPricePaise = 60000L, // ₹600
            unit = "unit",
            isPartMaterial = false,
            sortOrder = 4
        ),
        PriceBookItemEntity(
            id = "elec_wire_bundle",
            tradeCategory = "ELECTRICIAN",
            name = "1.5 sq mm Copper Wire (90m Roll)",
            description = "ISI marked fire-resistant single core copper house wire",
            defaultUnitPricePaise = 185000L, // ₹1,850
            unit = "roll",
            isPartMaterial = true,
            standardCostPaise = 150000L,
            sortOrder = 5
        )
    )

    fun getAcHvacSeedItems(): List<PriceBookItemEntity> = listOf(
        PriceBookItemEntity(
            id = "ac_jet_service",
            tradeCategory = "AC_HVAC",
            name = "Split AC Foam Jet Deep Servicing",
            description = "Indoor & outdoor coil washing with high pressure pump, blower wash, filter cleaning",
            defaultUnitPricePaise = 69900L, // ₹699
            unit = "unit",
            isPartMaterial = false,
            sortOrder = 1
        ),
        PriceBookItemEntity(
            id = "ac_gas_refill",
            tradeCategory = "AC_HVAC",
            name = "R32 / R410A Refrigerant Full Gas Charging",
            description = "Nitrogen pressure testing, leak braze repair, vacuuming, and complete gas charge",
            defaultUnitPricePaise = 250000L, // ₹2,500
            unit = "unit",
            isPartMaterial = false,
            sortOrder = 2
        ),
        PriceBookItemEntity(
            id = "ac_capacitor_replace",
            tradeCategory = "AC_HVAC",
            name = "Outdoor Run Capacitor (45/50 MFD)",
            description = "Dual run motor starting capacitor replacement with warranty",
            defaultUnitPricePaise = 75000L, // ₹750
            unit = "unit",
            isPartMaterial = true,
            standardCostPaise = 35000L,
            sortOrder = 3
        )
    )

    fun getPlumberSeedItems(): List<PriceBookItemEntity> = listOf(
        PriceBookItemEntity(
            id = "plumb_tap_leak",
            tradeCategory = "PLUMBER",
            name = "Tap Leak Repair / Spindle Change",
            description = "Opening mixer or bib cock, replacing brass spindle/washer",
            defaultUnitPricePaise = 15000L, // ₹150
            unit = "point",
            isPartMaterial = false,
            sortOrder = 1
        ),
        PriceBookItemEntity(
            id = "plumb_cistern_repair",
            tradeCategory = "PLUMBER",
            name = "Flush Tank / Cistern Syphon Repair",
            description = "Replacing inlet valve or flush flapper siphon in toilet tank",
            defaultUnitPricePaise = 30000L, // ₹300
            unit = "unit",
            isPartMaterial = false,
            sortOrder = 2
        )
    )

    fun getRoWaterSeedItems(): List<PriceBookItemEntity> = listOf(
        PriceBookItemEntity(
            id = "ro_filter_kit",
            tradeCategory = "RO_WATER",
            name = "RO Full Service & Filter Replacement Kit",
            description = "Sediment filter, carbon block, post-carbon filter, and sanitization",
            defaultUnitPricePaise = 120000L, // ₹1,200
            unit = "kit",
            isPartMaterial = true,
            standardCostPaise = 70000L,
            sortOrder = 1
        ),
        PriceBookItemEntity(
            id = "ro_membrane_change",
            tradeCategory = "RO_WATER",
            name = "75/80 GPD RO Membrane Replacement",
            description = "High TDS membrane replacement with auto-flush test",
            defaultUnitPricePaise = 180000L, // ₹1,800
            unit = "unit",
            isPartMaterial = true,
            standardCostPaise = 110000L,
            sortOrder = 2
        )
    )

    fun getAllInitialSeedItems(): List<PriceBookItemEntity> {
        return getElectricianSeedItems() + getAcHvacSeedItems() + getPlumberSeedItems() + getRoWaterSeedItems()
    }
}
