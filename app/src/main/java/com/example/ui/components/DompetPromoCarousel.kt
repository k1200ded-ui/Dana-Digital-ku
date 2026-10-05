package com.example.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.theme.DompetBluePrimary
import com.example.ui.theme.DompetGoldReward

@Composable
fun DompetPromoCarousel(
    onPromoClick: (String) -> Unit
) {
    var selectedIndex by remember { mutableIntStateOf(0) }

    val banners = listOf(
        Triple(
            R.drawable.banner_promo_qris_1791198364008,
            "Cashback QRIS 50% di Seluruh Indonesia",
            "Maksimal Rp 25.000 untuk transaksi perdana di resto & kafe favoritmu!"
        ),
        Triple(
            R.drawable.banner_security_safe_1791198376013,
            "DompetKu Protection • Jaminan 100% Uang Kembali",
            "Transaksi aman terlindungi sistem enkripsi Bank Indonesia & OJK."
        )
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 8.dp)
            .testTag("dompet_promo_carousel")
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Promo & Penawaran Spesial",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = "Lihat Semua",
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = DompetBluePrimary,
                modifier = Modifier.clickable { onPromoClick("SEMUA_PROMO") }
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Banner Card
        val currentBanner = banners[selectedIndex]
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(160.dp)
                .clickable { onPromoClick("BANNER_$selectedIndex") }
                .testTag("promo_banner_card"),
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
                Image(
                    painter = painterResource(id = currentBanner.first),
                    contentDescription = currentBanner.second,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )

                // Gradient overlay at bottom for readability
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Color.Black.copy(alpha = 0.35f)
                        )
                )

                // Text overlay
                Column(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(14.dp)
                ) {
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = DompetGoldReward
                    ) {
                        Text(
                            text = if (selectedIndex == 0) "SPESIAL QRIS" else "PROTEKSI OJK",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = currentBanner.second,
                        color = Color.White,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = currentBanner.third,
                        color = Color.White.copy(alpha = 0.85f),
                        fontSize = 11.sp,
                        maxLines = 1
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Dots indicator
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            banners.indices.forEach { index ->
                Box(
                    modifier = Modifier
                        .padding(horizontal = 4.dp)
                        .size(
                            width = if (selectedIndex == index) 20.dp else 7.dp,
                            height = 7.dp
                        )
                        .clip(RoundedCornerShape(4.dp))
                        .background(
                            if (selectedIndex == index) DompetBluePrimary else MaterialTheme.colorScheme.surfaceVariant
                        )
                        .clickable { selectedIndex = index }
                )
            }
        }
    }
}
