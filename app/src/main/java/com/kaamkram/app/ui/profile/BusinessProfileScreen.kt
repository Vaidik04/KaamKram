package com.kaamkram.app.ui.profile

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.kaamkram.app.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BusinessProfileScreen(
    onNavigateBack: () -> Unit,
    viewModel: BusinessProfileViewModel = hiltViewModel()
) {
    val businessName by viewModel.businessName.collectAsState()
    val ownerName by viewModel.ownerName.collectAsState()
    val phone by viewModel.phone.collectAsState()
    val upiId by viewModel.upiId.collectAsState()
    val address by viewModel.address.collectAsState()
    val tradeCategory by viewModel.tradeCategory.collectAsState()
    val gstin by viewModel.gstin.collectAsState()
    val isGstRegistered by viewModel.isGstRegistered.collectAsState()
    val invoicePrefix by viewModel.invoicePrefix.collectAsState()

    var tradeDropdownExpanded by remember { mutableStateOf(false) }
    val tradeOptions = listOf(
        "ELECTRICIAN" to stringResource(R.string.trade_electrician),
        "AC_HVAC" to stringResource(R.string.trade_ac_hvac),
        "PLUMBER" to stringResource(R.string.trade_plumber),
        "RO_WATER" to stringResource(R.string.trade_ro_water),
        "CCTV_SECURITY" to stringResource(R.string.trade_cctv_security),
        "OTHER" to stringResource(R.string.trade_other)
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.profile_title),
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
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    viewModel.saveProfile(onSuccess = onNavigateBack)
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
            OutlinedTextField(
                value = businessName,
                onValueChange = viewModel::onBusinessNameChanged,
                label = { Text(stringResource(R.string.profile_business_name)) },
                placeholder = { Text("e.g. Verma Electricals & AC Works") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            OutlinedTextField(
                value = ownerName,
                onValueChange = viewModel::onOwnerNameChanged,
                label = { Text(stringResource(R.string.profile_owner_name)) },
                placeholder = { Text("Your full name") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            OutlinedTextField(
                value = phone,
                onValueChange = viewModel::onPhoneChanged,
                label = { Text(stringResource(R.string.profile_phone)) },
                placeholder = { Text("9876543210") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            OutlinedTextField(
                value = upiId,
                onValueChange = viewModel::onUpiIdChanged,
                label = { Text(stringResource(R.string.profile_upi_id)) },
                placeholder = { Text("yourname@okaxis / 9876543210@paytm") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            ExposedDropdownMenuBox(
                expanded = tradeDropdownExpanded,
                onExpandedChange = { tradeDropdownExpanded = it }
            ) {
                val currentTradeLabel = tradeOptions.firstOrNull { it.first == tradeCategory }?.second ?: tradeCategory
                OutlinedTextField(
                    value = currentTradeLabel,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text(stringResource(R.string.profile_trade_category)) },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = tradeDropdownExpanded) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor()
                )
                ExposedDropdownMenu(
                    expanded = tradeDropdownExpanded,
                    onDismissRequest = { tradeDropdownExpanded = false }
                ) {
                    tradeOptions.forEach { (code, label) ->
                        DropdownMenuItem(
                            text = { Text(label) },
                            onClick = {
                                viewModel.onTradeCategoryChanged(code)
                                tradeDropdownExpanded = false
                            }
                        )
                    }
                }
            }

            OutlinedTextField(
                value = address,
                onValueChange = viewModel::onAddressChanged,
                label = { Text(stringResource(R.string.profile_address)) },
                placeholder = { Text("Shop address or home base") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 2
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "GST Registered Business?",
                    style = MaterialTheme.typography.bodyLarge
                )
                Switch(
                    checked = isGstRegistered,
                    onCheckedChange = viewModel::onGstToggle
                )
            }

            if (isGstRegistered) {
                OutlinedTextField(
                    value = gstin,
                    onValueChange = viewModel::onGstinChanged,
                    label = { Text(stringResource(R.string.profile_gstin)) },
                    placeholder = { Text("07AAAAA0000A1Z5") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
            }

            OutlinedTextField(
                value = invoicePrefix,
                onValueChange = viewModel::onInvoicePrefixChanged,
                label = { Text("Invoice Prefix") },
                placeholder = { Text("INV") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
        }
    }
}
