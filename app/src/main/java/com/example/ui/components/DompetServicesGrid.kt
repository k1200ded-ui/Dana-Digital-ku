package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Gamepad
import androidx.compose.material.icons.filled.MedicalServices
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*

data class ServiceItem(
    val title: String,
    val icon: ImageVector,
    val bgTint: Color,
    val iconTint: Color,
    val category: String,
    val tag: String
)

@Composable
fun DompetServicesGrid(
    onSelectService: (String) -> Unit
) {
    val services = listOf(
        ServiceItem("Pulsa & Data", Icons.Default.PhoneAndroid, Color(0xFFE8F1FD), DompetBluePrimary, "BILL_PULSA", "service_pulsa"),
        ServiceItem("Listrik PLN", Icons.Default.Bolt, Color(0xFFFFF7E0), Color(0xFFD97706), "BILL_PLN", "service_pln"),
        ServiceItem("BPJS Kesehatan", Icons.Default.MedicalServices, Color(0xFFE6F7F0), DompetGreenSuccess, "BILL_BPJS", "service_bpjs"),
        ServiceItem("Air PDAM", Icons.Default.WaterDrop, Color(0xFFE0F7FA), Color(0xFF0288D1), "BILL_PDAM", "service_pdam"),
        ServiceItem("Internet & TV", Icons.Default.Wifi, Color(0xFFEDE7F6), Color(0xFF673AB7), "BILL_INTERNET", "service_internet"),
        ServiceItem("Uang E-Money", Icons.Default.CreditCard, Color(0xFFFCE4EC), Color(0xFFC2185B), "BILL_EMONEY", "service_emoney"),
        ServiceItem("Games", Icons.Default.Gamepad, Color(0xFFFFF3E0), Color(0xFFE65100), "BILL_GAMES", "service_games"),
        ServiceItem("Donasi & Zakat", Icons.Default.Favorite, Color(0xFFE8F5E9), Color(0xFF2E7D32), "DONATION", "service_donation")
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 8.dp)
            .testTag("services_grid_card"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Layanan DompetKu",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "Lengkap & Bebas Antre",
                    fontSize = 11.sp,
                    color = DompetBluePrimary,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 4 columns x 2 rows
            for (rowIndex in 0..1) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    for (colIndex in 0..3) {
                        val item = services[rowIndex * 4 + colIndex]
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier
                                .weight(1f)
                                .clickable { onSelectService(item.category) }
                                .testTag(item.tag)
                                .padding(vertical = 6.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(46.dp)
                                    .clip(CircleShape)
                                    .background(item.bgTint),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = item.icon,
                                    contentDescription = item.title,
                                    tint = item.iconTint,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = item.title,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Medium,
                                textAlign = TextAlign.Center,
                                color = MaterialTheme.colorScheme.onSurface,
                                maxLines = 1
                            )
                        }
                    }
                }
                if (rowIndex == 0) {
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }
    }
}
