package com.android.cbeam.model

data class Support(val position: Double, val supportType: Triple<Int, Int, Int>)
{
    val supportTypeText: String
        get() {
            return when (supportType) {
                Triple(1, 1, 0) -> "Pinned Support"
                Triple(1, 1, 1) -> "Fixed Support"
                Triple(0, 1, 0) -> "Roller Support"
                else -> "Unknown"
            }
        }
}
