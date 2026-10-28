package com.kaamkram.app.ui.jobs

import android.content.Intent
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.compose.material.icons.filled.VerifiedUser
import com.kaamkram.app.R
import com.kaamkram.app.core.money.MoneyFormatter
import com.kaamkram.app.data.local.entity.WarrantyEntity
import com.kaamkram.app.domain.model.Estimate
import com.kaamkram.app.domain.model.Invoice
import com.kaamkram.app.domain.model.Job
import com.kaamkram.app.domain.model.JobEvent
import com.kaamkram.app.domain.model.JobStatus
import com.kaamkram.app.domain.model.Payment
import com.kaamkram.app.domain.model.PaymentMode
import com.kaamkram.app.domain.model.PhotoCategory
import com.kaamkram.app.domain.model.PhotoEvidence
import com.kaamkram.app.domain.model.PhotoSource
import com.kaamkram.app.ui.theme.OverdueRed
import com.kaamkram.app.ui.theme.SuccessGreen
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun JobDetailScreen(
    jobId: String,
    onNavigateBack: () -> Unit,
    onNavigateToCreateEstimate: (String) -> Unit,
    onNavigateToCreateInvoice: (String) -> Unit,
    viewModel: JobDetailViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val job by viewModel.job.collectAsState()
    val customer by viewModel.customer.collectAsState()
    val estimate by viewModel.estimate.collectAsState()
    val currentEstimateVersion by viewModel.currentEstimateVersion.collectAsState()
    val invoice by viewModel.invoice.collectAsState()
    val events by viewModel.events.collectAsState(initial = emptyList())
    val photos by viewModel.photos.collectAsState(initial = emptyList())
    val warranties by viewModel.warranties.collectAsState(initial = emptyList())

    var showPaymentDialog by remember { mutableStateOf(false) }
    var showWarrantyDialog by remember { mutableStateOf(false) }

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            viewModel.addPhotoEvidence(uri, PhotoCategory.AFTER, PhotoSource.IMPORTED_FROM_GALLERY)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = job?.jobNumber ?: stringResource(R.string.job_title),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(R.string.action_back))
                    }
                }
            )
        }
    ) { padding ->
        if (job == null || customer == null) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Customer & Contact Card
                item {
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = customer!!.name,
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = customer!!.phone,
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                                IconButton(
                                    onClick = {
                                        val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:${customer!!.phone}"))
                                        context.startActivity(intent)
                                    }
                                ) {
                                    Icon(Icons.Default.Call, contentDescription = stringResource(R.string.action_call))
                                }
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Scheduled: ${job!!.scheduledDate ?: "Not specified"}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                // Workflow Actions based on Job State Machine
                item {
                    WorkflowActionCard(
                        job = job!!,
                        estimate = estimate,
                        onCreateEstimateClick = { onNavigateToCreateEstimate(job!!.id) },
                        onStartWorkClick = { viewModel.updateJobStatus(JobStatus.IN_PROGRESS) },
                        onCompleteWorkClick = { viewModel.updateJobStatus(JobStatus.COMPLETED) },
                        onApproveEstimateClick = { viewModel.approveEstimate() },
                        onCreateInvoiceClick = { onNavigateToCreateInvoice(job!!.id) }
                    )
                }

                // Work Evidence Photos Section
                item {
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = stringResource(R.string.evidence_title),
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )
                                OutlinedButton(onClick = { imagePickerLauncher.launch("image/*") }) {
                                    Icon(Icons.Default.AddPhotoAlternate, contentDescription = null)
                                    Spacer(modifier = Modifier.padding(horizontal = 4.dp))
                                    Text("Add Photo")
                                }
                            }

                            if (photos.isNotEmpty()) {
                                Spacer(modifier = Modifier.height(12.dp))
                                LazyRow(
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    items(photos, key = { it.id }) { photo ->
                                        PhotoEvidenceChip(photo = photo)
                                    }
                                }
                            }
                        }
                    }
                }

                // Invoice Section
                if (invoice != null) {
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant
                            )
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = invoice!!.invoiceNumber,
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold
                                    )
                                    SuggestionChip(
                                        onClick = {},
                                        label = { Text(invoice!!.status.name) }
                                    )
                                }
                                Spacer(modifier = Modifier.height(8.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Column {
                                        Text(
                                            text = stringResource(R.string.invoice_total),
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                        Text(
                                            text = MoneyFormatter.formatPaise(invoice!!.grandTotalPaise),
                                            style = MaterialTheme.typography.titleLarge,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                    Column(horizontalAlignment = Alignment.End) {
                                        Text(
                                            text = stringResource(R.string.invoice_balance),
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                        Text(
                                            text = MoneyFormatter.formatPaise(invoice!!.balanceDuePaise),
                                            style = MaterialTheme.typography.titleLarge,
                                            fontWeight = FontWeight.Bold,
                                            color = if (invoice!!.balanceDuePaise > 0) OverdueRed else SuccessGreen
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.height(12.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Button(
                                        modifier = Modifier.weight(1f),
                                        onClick = {
                                            viewModel.generateAndShareInvoicePdf(context) { pdfFile ->
                                                val uri = FileProvider.getUriForFile(
                                                    context,
                                                    "${context.packageName}.fileprovider",
                                                    pdfFile
                                                )
                                                val shareIntent = Intent(Intent.ACTION_SEND).apply {
                                                    type = "application/pdf"
                                                    putExtra(Intent.EXTRA_STREAM, uri)
                                                    addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                                                }
                                                context.startActivity(Intent.createChooser(shareIntent, "Share Invoice PDF"))
                                            }
                                        }
                                    ) {
                                        Icon(Icons.Default.Share, contentDescription = null)
                                        Spacer(modifier = Modifier.padding(horizontal = 4.dp))
                                        Text(stringResource(R.string.invoice_share_pdf))
                                    }

                                    if (invoice!!.balanceDuePaise > 0) {
                                        Button(
                                            modifier = Modifier.weight(1f),
                                            onClick = { showPaymentDialog = true }
                                        ) {
                                            Icon(Icons.Default.AccountBalanceWallet, contentDescription = null)
                                            Spacer(modifier = Modifier.padding(horizontal = 4.dp))
                                            Text(stringResource(R.string.payment_record_action))
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                // Estimate / Quote Section
                item {
                    if (currentEstimateVersion != null) {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.primaryContainer
                            )
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "Quote Rev ${currentEstimateVersion!!.versionNumber}",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onPrimaryContainer
                                    )
                                    SuggestionChip(
                                        onClick = {},
                                        label = { Text(estimate?.status?.name ?: "DRAFT") }
                                    )
                                }
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = MoneyFormatter.formatPaise(currentEstimateVersion!!.grandTotalPaise),
                                    style = MaterialTheme.typography.headlineMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                                Spacer(modifier = Modifier.height(12.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Button(
                                        modifier = Modifier.weight(1f),
                                        onClick = {
                                            viewModel.generateAndShareQuotePdf(context) { pdfFile ->
                                                val uri = FileProvider.getUriForFile(
                                                    context,
                                                    "${context.packageName}.fileprovider",
                                                    pdfFile
                                                )
                                                val shareIntent = Intent(Intent.ACTION_SEND).apply {
                                                    type = "application/pdf"
                                                    putExtra(Intent.EXTRA_STREAM, uri)
                                                    addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                                                }
                                                context.startActivity(Intent.createChooser(shareIntent, "Share Quote PDF"))
                                            }
                                        }
                                    ) {
                                        Icon(Icons.Default.Share, contentDescription = null)
                                        Spacer(modifier = Modifier.padding(horizontal = 4.dp))
                                        Text(stringResource(R.string.estimate_share_pdf))
                                    }
                                }
                            }
                        }
                    } else if (invoice == null) {
                        OutlinedButton(
                            modifier = Modifier.fillMaxWidth(),
                            onClick = { onNavigateToCreateEstimate(job!!.id) }
                        ) {
                            Icon(Icons.Default.Description, contentDescription = null)
                            Spacer(modifier = Modifier.padding(horizontal = 4.dp))
                            Text(stringResource(R.string.estimate_new))
                        }
                    }
                }

                // Warranties Section
                item {
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Service & Parts Warranties",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )
                                OutlinedButton(onClick = { showWarrantyDialog = true }) {
                                    Icon(Icons.Default.VerifiedUser, contentDescription = null)
                                    Spacer(modifier = Modifier.padding(horizontal = 4.dp))
                                    Text("Issue")
                                }
                            }

                            if (warranties.isNotEmpty()) {
                                Spacer(modifier = Modifier.height(12.dp))
                                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                    warranties.forEach { w ->
                                        Card(
                                            modifier = Modifier.fillMaxWidth(),
                                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                                        ) {
                                            Column(modifier = Modifier.padding(12.dp)) {
                                                Row(
                                                    modifier = Modifier.fillMaxWidth(),
                                                    horizontalArrangement = Arrangement.SpaceBetween
                                                ) {
                                                    Text(
                                                        text = w.serviceOrItemName,
                                                        style = MaterialTheme.typography.bodyMedium,
                                                        fontWeight = FontWeight.Bold
                                                    )
                                                    Text(
                                                        text = "${w.durationDays} Days",
                                                        style = MaterialTheme.typography.labelMedium,
                                                        color = MaterialTheme.colorScheme.primary
                                                    )
                                                }
                                                Spacer(modifier = Modifier.height(4.dp))
                                                Text(
                                                    text = "Valid: ${w.startDate} to ${w.expiryDate}",
                                                    style = MaterialTheme.typography.bodySmall,
                                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                                )
                                                if (!w.terms.isNullOrBlank()) {
                                                    Text(
                                                        text = "Terms: ${w.terms}",
                                                        style = MaterialTheme.typography.bodySmall
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                // Timeline Section
                item {
                    Text(
                        text = stringResource(R.string.job_timeline),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }

                items(events, key = { it.id }) { event ->
                    TimelineItemRow(event = event)
                }
            }
        }
    }

    if (showPaymentDialog && invoice != null) {
        RecordPaymentDialog(
            defaultAmountRupees = (invoice!!.balanceDuePaise / 100).toString(),
            onDismiss = { showPaymentDialog = false },
            onConfirm = { amountRupees, mode, ref ->
                viewModel.recordPayment(amountRupees, mode, ref)
                showPaymentDialog = false
            }
        )
    }

    if (showWarrantyDialog && job != null) {
        IssueWarrantyDialog(
            defaultItemName = job!!.title,
            onDismiss = { showWarrantyDialog = false },
            onConfirm = { serviceName, durationDays, terms ->
                viewModel.issueWarranty(serviceName, durationDays, terms)
                showWarrantyDialog = false
            }
        )
    }
}

@Composable
fun IssueWarrantyDialog(
    defaultItemName: String,
    onDismiss: () -> Unit,
    onConfirm: (serviceName: String, durationDays: Int, terms: String?) -> Unit
) {
    var serviceName by remember { mutableStateOf(defaultItemName) }
    var durationDaysStr by remember { mutableStateOf("90") }
    var terms by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Issue Warranty Certificate") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = serviceName,
                    onValueChange = { serviceName = it },
                    label = { Text("Service or Part Name") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                OutlinedTextField(
                    value = durationDaysStr,
                    onValueChange = { durationDaysStr = it.filter { ch -> ch.isDigit() } },
                    label = { Text("Warranty Duration (Days)") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf(30, 90, 180, 365).forEach { days ->
                        SuggestionChip(
                            onClick = { durationDaysStr = days.toString() },
                            label = { Text("${days}d") }
                        )
                    }
                }

                OutlinedTextField(
                    value = terms,
                    onValueChange = { terms = it },
                    label = { Text("Terms & Conditions (Optional)") },
                    placeholder = { Text("e.g. Physical damage or misuse excluded") },
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 3
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val days = durationDaysStr.toIntOrNull() ?: 30
                    if (serviceName.isNotBlank() && days > 0) {
                        onConfirm(serviceName.trim(), days, terms.trim().ifEmpty { null })
                    }
                }
            ) {
                Text("Issue Warranty")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.action_cancel))
            }
        }
    )
}

@Composable
fun PhotoEvidenceChip(photo: PhotoEvidence) {
    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(modifier = Modifier.padding(8.dp)) {
            Text(
                text = photo.category.name,
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = if (photo.source == PhotoSource.CAPTURED_IN_APP) "Captured" else "Imported",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecordPaymentDialog(
    defaultAmountRupees: String,
    onDismiss: () -> Unit,
    onConfirm: (amountRupees: String, mode: PaymentMode, reference: String?) -> Unit
) {
    var amount by remember { mutableStateOf(defaultAmountRupees) }
    var selectedMode by remember { mutableStateOf(PaymentMode.UPI) }
    var referenceNumber by remember { mutableStateOf("") }
    var modeDropdownExpanded by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.payment_record_action)) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = amount,
                    onValueChange = { amount = it },
                    label = { Text(stringResource(R.string.payment_amount)) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                ExposedDropdownMenuBox(
                    expanded = modeDropdownExpanded,
                    onExpandedChange = { modeDropdownExpanded = it }
                ) {
                    OutlinedTextField(
                        value = selectedMode.name,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text(stringResource(R.string.payment_mode)) },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = modeDropdownExpanded) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor(MenuAnchorType.PrimaryNotEditable)
                    )
                    ExposedDropdownMenu(
                        expanded = modeDropdownExpanded,
                        onDismissRequest = { modeDropdownExpanded = false }
                    ) {
                        PaymentMode.values().forEach { mode ->
                            DropdownMenuItem(
                                text = { Text(mode.name) },
                                onClick = {
                                    selectedMode = mode
                                    modeDropdownExpanded = false
                                }
                            )
                        }
                    }
                }

                OutlinedTextField(
                    value = referenceNumber,
                    onValueChange = { referenceNumber = it },
                    label = { Text(stringResource(R.string.payment_upi_ref)) },
                    placeholder = { Text("e.g. UTR / Transaction ID") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { onConfirm(amount, selectedMode, referenceNumber.ifEmpty { null }) }
            ) {
                Text(stringResource(R.string.action_save))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.action_cancel))
            }
        }
    )
}

@Composable
fun WorkflowActionCard(
    job: Job,
    estimate: Estimate?,
    onCreateEstimateClick: () -> Unit,
    onStartWorkClick: () -> Unit,
    onCompleteWorkClick: () -> Unit,
    onApproveEstimateClick: () -> Unit,
    onCreateInvoiceClick: () -> Unit
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Next Steps • ${job.status.name}",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))

            when (job.status) {
                JobStatus.REQUESTED -> {
                    Button(
                        modifier = Modifier.fillMaxWidth(),
                        onClick = onCreateEstimateClick
                    ) {
                        Text(stringResource(R.string.estimate_new))
                    }
                }
                JobStatus.QUOTED -> {
                    Button(
                        modifier = Modifier.fillMaxWidth(),
                        onClick = onApproveEstimateClick
                    ) {
                        Icon(Icons.Default.Check, contentDescription = null)
                        Spacer(modifier = Modifier.padding(horizontal = 4.dp))
                        Text(stringResource(R.string.estimate_approve_action))
                    }
                }
                JobStatus.APPROVED, JobStatus.SCHEDULED -> {
                    Button(
                        modifier = Modifier.fillMaxWidth(),
                        onClick = onStartWorkClick
                    ) {
                        Icon(Icons.Default.PlayArrow, contentDescription = null)
                        Spacer(modifier = Modifier.padding(horizontal = 4.dp))
                        Text("Start Work on Site")
                    }
                }
                JobStatus.IN_PROGRESS -> {
                    Button(
                        modifier = Modifier.fillMaxWidth(),
                        onClick = onCompleteWorkClick
                    ) {
                        Icon(Icons.Default.Check, contentDescription = null)
                        Spacer(modifier = Modifier.padding(horizontal = 4.dp))
                        Text("Mark Work Completed")
                    }
                }
                JobStatus.COMPLETED -> {
                    Button(
                        modifier = Modifier.fillMaxWidth(),
                        onClick = onCreateInvoiceClick
                    ) {
                        Icon(Icons.Default.Receipt, contentDescription = null)
                        Spacer(modifier = Modifier.padding(horizontal = 4.dp))
                        Text(stringResource(R.string.invoice_new))
                    }
                }
                else -> {
                    Text(
                        text = "Job is in state: ${job.status.name}",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        }
    }
}

@Composable
fun TimelineItemRow(event: JobEvent) {
    val dateStr = SimpleDateFormat("dd MMM, hh:mm a", Locale.getDefault()).format(Date(event.timestampEpochMs))
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = event.eventType.name,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = dateStr,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = event.summary,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}
