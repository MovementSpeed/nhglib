package io.github.movementspeed.nhglib.utils.data

/**
 * Created by Fausto Napoli on 01/11/2016.
 */
fun idFromString(string: String): Int {
    return string.hashCode()
}
