package com.example.travelplaner.utils

import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.pow
import kotlin.math.roundToLong

fun Double.withDecimals(decimalsCount: Int): Double {
    val divider = 10f.pow(decimalsCount)
    return (this * divider).roundToLong().toDouble() / divider
}

private const val pattern = "dd.MM.yyyy"
private val simpleDateFormat = SimpleDateFormat(pattern, Locale.US)
fun Date.asSimpleString(): String {
    return simpleDateFormat.format(this)
}