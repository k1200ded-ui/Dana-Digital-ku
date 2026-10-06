package com.example.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.TransactionType
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionScreen(
    viewModel: TransactionViewModel
) {
    val uiState by viewModel.uiState.collectAsState()

    var title by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("Makanan") }
    var amount by remember { mutableStateOf("") }
    var note by remember { mutableStateOf("") }
    var type by remember { mutableStateOf(TransactionType.EXPENSE) }
    var expandedCategory by remember { mutableStateOf(false) }

    val categories = listOf(
        "Makanan",
        "Transport",
        "Tagihan",
        "Hiburan",
        "Belanja",
        "Kesehatan",
        "Investasi",
        "Lainnya"
    )

    val numberFormat = NumberFormat.getNumberInstance(Locale("id", "ID"))

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "CashFlow Daily",
                        fontWeight = FontWeight.Bold
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    title = ""
                    category = "Makanan"
                    amount = ""
                    note = ""
                    type = TransactionType.EXPENSE
                }
            ) {
                Icon(Icons.Filled.Add, contentDescription = "Add")
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Summary Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            "Saldo Hari Ini",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        Text(
                            text = "Rp ${numberFormat.format(uiState.todayBalance.toLong())}",
                            style = MaterialTheme.typography.headlineMedium.copy(
                                fontSize = 28.sp,
                                fontWeight = FontWeight.Bold
                            ),
                            color = if (uiState.todayBalance >= 0) Color.Green else Color.Red
                        )

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 8.dp),
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            Column(
                                modifier = Modifier.weight(1f)
                            ) {
                                Text(
                                    "Pemasukan",
                                    style = MaterialTheme.typography.labelSmall
                                )
                                Text(
                                    "Rp ${numberFormat.format(uiState.todayIncome.toLong())}",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = Color.Green,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Column(
                                modifier = Modifier.weight(1f)
                            ) {
                                Text(
                                    "Pengeluaran",
                                    style = MaterialTheme.typography.labelSmall
                                )
                                Text(
                                    "Rp ${numberFormat.format(uiState.todayExpense.toLong())}",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = Color.Red,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }

            // Form Input
            item {
                Card {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            "Tambah Transaksi",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )

                        OutlinedTextField(
                            value = title,
                            onValueChange = { title = it },
                            label = { Text("Judul") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )

                        // Category Dropdown
                        ExposedDropdownMenuBox(
                            expanded = expandedCategory,
                            onExpandedChange = { expandedCategory = it },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            OutlinedTextField(
                                value = category,
                                onValueChange = {},
                                readOnly = true,
                                label = { Text("Kategori") },
                                modifier = Modifier
                                    .menuAnchor()
                                    .fillMaxWidth(),
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedCategory) }
                            )
                            ExposedDropdownMenu(
                                expanded = expandedCategory,
                                onDismissRequest = { expandedCategory = false },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                categories.forEach { cat ->
                                    DropdownMenuItem(
                                        text = { Text(cat) },
                                        onClick = {
                                            category = cat
                                            expandedCategory = false
                                        }
                                    )
                                }
                            }
                        }

                        OutlinedTextField(
                            value = amount,
                            onValueChange = { amount = it },
                            label = { Text("Jumlah (Rp)") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )

                        OutlinedTextField(
                            value = note,
                            onValueChange = { note = it },
                            label = { Text("Catatan (Opsional)") },
                            modifier = Modifier.fillMaxWidth(),
                            minLines = 2,
                            maxLines = 3
                        )

                        // Type Selection
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            FilterChip(
                                selected = type == TransactionType.INCOME,
                                onClick = { type = TransactionType.INCOME },
                                label = { Text("Pemasukan") },
                                modifier = Modifier.weight(1f)
                            )
                            FilterChip(
                                selected = type == TransactionType.EXPENSE,
                                onClick = { type = TransactionType.EXPENSE },
                                label = { Text("Pengeluaran") },
                                modifier = Modifier.weight(1f)
                            )
                        }

                        Button(
                            onClick = {
                                val nominal = amount.toDoubleOrNull() ?: 0.0
                                if (nominal > 0 && title.isNotBlank()) {
                                    viewModel.addTransaction(
                                        title = title,
                                        category = category,
                                        type = type,
                                        amount = nominal,
                                        note = note
                                    )
                                    title = ""
                                    category = "Makanan"
                                    amount = ""
                                    note = ""
                                    type = TransactionType.EXPENSE
                                }
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Simpan Transaksi")
                        }
                    }
                }
            }

            // Transactions History
            if (uiState.transactions.isNotEmpty()) {
                item {
                    Text(
                        "Riwayat Transaksi",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }

                items(uiState.transactions) { item ->
                    TransactionItem(item, viewModel, numberFormat)
                }
            }

            item {
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Composable
fun TransactionItem(
    item: com.example.data.TransactionEntity,
    viewModel: TransactionViewModel,
    numberFormat: NumberFormat
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    item.title,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold
                )
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        item.category,
                        style = MaterialTheme.typography.labelSmall,
                        modifier = Modifier
                            .background(
                                MaterialTheme.colorScheme.surfaceVariant,
                                RoundedCornerShape(4.dp)
                            )
                            .padding(4.dp)
                    )
                    Text(
                        SimpleDateFormat("HH:mm", Locale("id", "ID")).format(Date(item.date)),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.outline
                    )
                }
                if (item.note.isNotBlank()) {
                    Text(
                        item.note,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.outline
                    )
                }
            }

            Column(
                modifier = Modifier.padding(start = 12.dp),
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    "${if (item.type == TransactionType.INCOME) "+" else "-"} Rp ${numberFormat.format(item.amount.toLong())}",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    color = if (item.type == TransactionType.INCOME) Color.Green else Color.Red
                )
                IconButton(
                    onClick = { viewModel.deleteTransaction(item) },
                    modifier = Modifier.size(24.dp)
                ) {
                    Icon(
                        Icons.Filled.Delete,
                        contentDescription = "Delete",
                        tint = MaterialTheme.colorScheme.error,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}
