package com.kaamkram.app.ui.jobs

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.kaamkram.app.R

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun CreateJobScreen(
    onNavigateBack: () -> Unit,
    onJobCreated: (String) -> Unit,
    viewModel: CreateJobViewModel = hiltViewModel()
) {
    val customerName by viewModel.customerName.collectAsState()
    val customerPhone by viewModel.customerPhone.collectAsState()
    val siteAddress by viewModel.siteAddress.collectAsState()
    val jobTitle by viewModel.jobTitle.collectAsState()
    val jobDescription by viewModel.jobDescription.collectAsState()
    val scheduledDate by viewModel.scheduledDate.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()

    val quickJobSuggestions = listOf(
        "Ceiling Fan Installation",
        "Switchboard Point Repair",
        "Split AC Servicing",
        "AC Gas Charging",
        "Tap Leakage Repair",
        "RO Water Filter Change"
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.job_new),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = stringResource(R.string.action_back))
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    viewModel.createJob(onSuccess = onJobCreated)
                }
            ) {
                Icon(Icons.Default.Check, contentDescription = stringResource(R.string.action_save))
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "1. Customer & Site",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )

            OutlinedTextField(
                value = customerName,
                onValueChange = viewModel::onCustomerNameChanged,
                label = { Text(stringResource(R.string.customer_name)) },
                placeholder = { Text("e.g. Ramesh Kumar") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            OutlinedTextField(
                value = customerPhone,
                onValueChange = viewModel::onCustomerPhoneChanged,
                label = { Text(stringResource(R.string.customer_phone)) },
                placeholder = { Text("9876543210") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            OutlinedTextField(
                value = siteAddress,
                onValueChange = viewModel::onSiteAddressChanged,
                label = { Text(stringResource(R.string.job_site_address)) },
                placeholder = { Text("House/Flat No, Landmark, Area") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "2. Work Details",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )

            // Quick suggestion chips
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                quickJobSuggestions.forEach { suggestion ->
                    SuggestionChip(
                        onClick = { viewModel.onJobTitleChanged(suggestion) },
                        label = { Text(suggestion) }
                    )
                }
            }

            OutlinedTextField(
                value = jobTitle,
                onValueChange = viewModel::onJobTitleChanged,
                label = { Text("Job Title / Task Description") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            OutlinedTextField(
                value = jobDescription,
                onValueChange = viewModel::onJobDescriptionChanged,
                label = { Text(stringResource(R.string.job_notes)) },
                placeholder = { Text("Customer complained about noise / timing preferences") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 2
            )

            OutlinedTextField(
                value = scheduledDate,
                onValueChange = viewModel::onScheduledDateChanged,
                label = { Text(stringResource(R.string.job_scheduled_date)) },
                placeholder = { Text("YYYY-MM-DD") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            if (!errorMessage.isNullOrEmpty()) {
                Text(
                    text = errorMessage!!,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}
