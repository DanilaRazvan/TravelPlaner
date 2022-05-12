package com.example.travelplaner.utils

import kotlin.math.pow
import kotlin.math.roundToLong

fun Double.withDecimals(decimalsCount: Int): Double {
    val divider = 10f.pow(decimalsCount)
    return (this * divider).roundToLong().toDouble() / divider
}