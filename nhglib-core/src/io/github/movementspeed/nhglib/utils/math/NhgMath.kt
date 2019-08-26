package io.github.movementspeed.nhglib.utils.math

fun normalize(value: Float, fromA: Float, toA: Float, fromB: Float, toB: Float) = (toB - toA) / (fromB - fromA) * (value - fromB) + toB
