package com.android.cbeam.model

data class DistributedLoadV(val magnitude: Int, val positionRange: DoubleArray): Load  {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as DistributedLoadV

        if (magnitude != other.magnitude) return false
        return positionRange.contentEquals(other.positionRange)
    }

    override fun hashCode(): Int {
        var result = magnitude.hashCode()
        result = 31 * result + positionRange.contentHashCode()
        return result
    }
}