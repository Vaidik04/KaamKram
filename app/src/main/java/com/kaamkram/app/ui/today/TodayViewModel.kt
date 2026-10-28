package com.kaamkram.app.ui.today

import androidx.lifecycle.ViewModel
import com.kaamkram.app.data.local.dao.InvoiceDao
import com.kaamkram.app.data.local.dao.JobDao
import com.kaamkram.app.data.local.entity.JobEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class TodayViewModel @Inject constructor(
    private val jobDao: JobDao,
    private val invoiceDao: InvoiceDao
) : ViewModel() {

    private val todayIsoDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())

    val dueTodayCount: Flow<Int> = jobDao.countDueOnDate(todayIsoDate)
    val inProgressCount: Flow<Int> = jobDao.countInProgress()
    val pendingAmountPaise: Flow<Long> = invoiceDao.observeTotalPendingPaise()
    val todayJobs: Flow<List<JobEntity>> = jobDao.getJobsScheduledOn(todayIsoDate)
}
