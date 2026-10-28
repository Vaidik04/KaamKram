package com.kaamkram.app.domain.model

data class Customer(
    val id: String,
    val name: String,
    val phone: String,
    val alternatePhone: String? = null,
    val notes: String? = null,
    val createdAtEpochMs: Long = System.currentTimeMillis(),
    val updatedAtEpochMs: Long = System.currentTimeMillis()
)

data class Site(
    val id: String,
    val customerId: String,
    val addressLine: String,
    val landmark: String? = null,
    val city: String = "",
    val postalCode: String? = null,
    val notes: String? = null,
    val createdAtEpochMs: Long = System.currentTimeMillis()
)
