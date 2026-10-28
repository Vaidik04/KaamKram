package com.kaamkram.app.data.repository

import com.kaamkram.app.data.local.dao.CustomerDao
import com.kaamkram.app.data.local.entity.CustomerEntity
import com.kaamkram.app.data.local.entity.SiteEntity
import com.kaamkram.app.domain.model.Customer
import com.kaamkram.app.domain.model.Site
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CustomerRepository @Inject constructor(
    private val customerDao: CustomerDao
) {
    fun getAllCustomers(): Flow<List<Customer>> =
        customerDao.getAllCustomers().map { entities -> entities.map { it.toDomain() } }

    fun searchCustomers(query: String): Flow<List<Customer>> =
        customerDao.searchCustomers(query).map { entities -> entities.map { it.toDomain() } }

    suspend fun getCustomerById(id: String): Customer? =
        customerDao.getCustomerById(id)?.toDomain()

    suspend fun createCustomer(
        name: String,
        phone: String,
        alternatePhone: String? = null,
        notes: String? = null,
        initialAddress: String? = null
    ): Customer {
        val customerId = UUID.randomUUID().toString()
        val customerEntity = CustomerEntity(
            id = customerId,
            name = name.trim(),
            phone = phone.trim(),
            alternatePhone = alternatePhone?.trim()?.ifEmpty { null },
            notes = notes?.trim()?.ifEmpty { null }
        )
        customerDao.insertCustomer(customerEntity)

        if (!initialAddress.isNullOrBlank()) {
            val siteEntity = SiteEntity(
                id = UUID.randomUUID().toString(),
                customerId = customerId,
                addressLine = initialAddress.trim()
            )
            customerDao.insertSite(siteEntity)
        }

        return customerEntity.toDomain()
    }

    fun getSitesForCustomer(customerId: String): Flow<List<Site>> =
        customerDao.getSitesForCustomer(customerId).map { entities ->
            entities.map {
                Site(
                    id = it.id,
                    customerId = it.customerId,
                    addressLine = it.addressLine,
                    landmark = it.landmark,
                    city = it.city,
                    postalCode = it.postalCode,
                    notes = it.notes,
                    createdAtEpochMs = it.createdAtEpochMs
                )
            }
        }

    private fun CustomerEntity.toDomain() = Customer(
        id = id,
        name = name,
        phone = phone,
        alternatePhone = alternatePhone,
        notes = notes,
        createdAtEpochMs = createdAtEpochMs,
        updatedAtEpochMs = updatedAtEpochMs
    )
}
