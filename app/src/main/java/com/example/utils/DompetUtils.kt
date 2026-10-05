package com.example.utils

import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object DompetUtils {

    fun formatRupiah(amount: Long): String {
        val localeID = Locale("in", "ID")
        val numberFormat = NumberFormat.getNumberInstance(localeID)
        return "Rp " + numberFormat.format(amount)
    }

    fun formatTimestamp(timestamp: Long): String {
        val sdf = SimpleDateFormat("dd MMM yyyy, HH:mm 'WIB'", Locale("in", "ID"))
        return sdf.format(Date(timestamp))
    }

    fun formatDateOnly(timestamp: Long): String {
        val sdf = SimpleDateFormat("dd MMMM yyyy", Locale("in", "ID"))
        return sdf.format(Date(timestamp))
    }

    fun detectProvider(phone: String): String {
        val clean = phone.replace(Regex("[^0-9]"), "")
        return when {
            clean.startsWith("0811") || clean.startsWith("0812") || clean.startsWith("0813") ||
                    clean.startsWith("0821") || clean.startsWith("0822") || clean.startsWith("0852") ||
                    clean.startsWith("0853") || clean.startsWith("0823") -> "Telkomsel"

            clean.startsWith("0814") || clean.startsWith("0815") || clean.startsWith("0816") ||
                    clean.startsWith("0855") || clean.startsWith("0856") || clean.startsWith("0857") ||
                    clean.startsWith("0858") -> "Indosat Ooredoo"

            clean.startsWith("0817") || clean.startsWith("0818") || clean.startsWith("0819") ||
                    clean.startsWith("0859") || clean.startsWith("0877") || clean.startsWith("0878") -> "XL Axiata"

            clean.startsWith("0895") || clean.startsWith("0896") || clean.startsWith("0897") ||
                    clean.startsWith("0898") || clean.startsWith("0899") -> "Tri (3)"

            clean.startsWith("0881") || clean.startsWith("0882") || clean.startsWith("0883") ||
                    clean.startsWith("0884") || clean.startsWith("0885") || clean.startsWith("0886") ||
                    clean.startsWith("0887") || clean.startsWith("0888") -> "Smartfren"

            else -> "Seluler Indonesia"
        }
    }
}
