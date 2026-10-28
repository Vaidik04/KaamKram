package com.kaamkram.app.ui.jobs

import androidx.lifecycle.ViewModel
import com.kaamkram.app.data.local.dao.JobDao
import com.kaamkram.app.data.local.entity.JobEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@HiltViewModel
class JobsViewModel @Inject constructor(
    private val jobDao: JobDao
) : ViewModel() {

    val allJobs: Flow<List<JobEntity>> = jobDao.getAllJobs()
}
