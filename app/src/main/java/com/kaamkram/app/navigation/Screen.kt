package com.kaamkram.app.navigation

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.People
import androidx.compose.ui.graphics.vector.ImageVector
import com.kaamkram.app.R

sealed class Screen(
    val route: String,
    @StringRes val titleRes: Int,
    val icon: ImageVector? = null
) {
    // Bottom Nav Screens
    data object Today : Screen("today", R.string.nav_today, Icons.Default.CalendarToday)
    data object Jobs : Screen("jobs", R.string.nav_jobs, Icons.Default.Build)
    data object Customers : Screen("customers", R.string.nav_customers, Icons.Default.People)
    data object Money : Screen("money", R.string.nav_money, Icons.Default.AccountBalanceWallet)
    data object More : Screen("more", R.string.nav_more, Icons.Default.MoreHoriz)

    // Sub-screens
    data object NewJob : Screen("jobs/new", R.string.job_new)
    data object JobDetail : Screen("jobs/{jobId}", R.string.job_title) {
        fun createRoute(jobId: String) = "jobs/$jobId"
    }

    data object NewCustomer : Screen("customers/new", R.string.customer_new)
    data object CustomerDetail : Screen("customers/{customerId}", R.string.customer_title) {
        fun createRoute(customerId: String) = "customers/$customerId"
    }

    data object EstimateDetail : Screen("estimates/{jobId}", R.string.estimate_title) {
        fun createRoute(jobId: String) = "estimates/$jobId"
    }

    data object InvoiceDetail : Screen("invoices/{jobId}", R.string.invoice_title) {
        fun createRoute(jobId: String) = "invoices/$jobId"
    }

    data object BusinessProfile : Screen("profile", R.string.profile_title)

    companion object {
        val bottomNavScreens = listOf(Today, Jobs, Customers, Money, More)
    }
}
