package io.github.movementspeed.nhglib.core.messaging

import io.github.movementspeed.nhglib.utils.data.Bundle
import io.github.movementspeed.nhglib.utils.data.StringUtils

/**
 * Created by Fausto Napoli on 01/11/2016.
 * Data structure for a message.
 */
class Message @JvmOverloads constructor(name: String, var data: Bundle? = null) {
    var id: Int = 0

    init {
        id = StringUtils.idFromString(name)
    }

    override fun equals(o: Any?): Boolean {
        if (this === o) return true
        if (o == null || javaClass != o.javaClass) return false

        val message = o as Message?
        return id == message!!.id
    }

    override fun hashCode(): Int {
        var result = id
        result = 31 * result + if (data != null) data!!.hashCode() else 0
        return result
    }

    fun `is`(name: String): Boolean {
        val id = StringUtils.idFromString(name)
        return this.id == id
    }
}
