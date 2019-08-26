package io.github.movementspeed.nhglib.utils.data

/**
 * Created by Fausto Napoli on 25/10/2016.
 */
class Bundle : HashMap<String, Any>() {

    @JvmOverloads
    fun getBoolean(key: String, defaultValue: Boolean = false): Boolean {
        var res = defaultValue
        val value = get(key)

        if (value is Boolean) {
            res = value
        }

        return res
    }

    @JvmOverloads
    fun getByte(key: String, defaultValue: Byte = (-1).toByte()): Byte {
        var res = defaultValue
        val value = get(key)

        if (value is Byte) {
            res = value
        }

        return res
    }

    @JvmOverloads
    fun getShort(key: String, defaultValue: Short = (-1).toShort()): Short {
        var res = defaultValue
        val value = get(key)

        if (value is Short) {
            res = value
        }

        return res
    }

    @JvmOverloads
    fun getInteger(key: String, defaultValue: Int = -1): Int {
        var res = defaultValue
        val value = get(key)

        if (value is Int) {
            res = value
        }

        return res
    }

    @JvmOverloads
    fun getLong(key: String, defaultValue: Long = -1L): Long {
        var res = defaultValue
        val value = get(key)

        if (value is Long) {
            res = value
        }

        return res
    }

    @JvmOverloads
    fun getFloat(key: String, defaultValue: Float = -1f): Float {
        var res = defaultValue
        val value = get(key)

        if (value is Float) {
            res = value
        }

        return res
    }

    @JvmOverloads
    fun getDouble(key: String, defaultValue: Double = -1.0): Double {
        var res = defaultValue
        val value = get(key)

        if (value is Double) {
            res = value
        }

        return res
    }

    @JvmOverloads
    fun getCharacter(key: String, defaultValue: Char = ' '): Char {
        var res = defaultValue
        val value = get(key)

        if (value is Char) {
            res = value
        }

        return res
    }

    @JvmOverloads
    fun getString(key: String, defaultValue: String? = null): String? {
        var res: String? = null
        val value = get(key)

        if (value is String) {
            res = value
        }

        if (res == null) {
            res = defaultValue
        }

        return res
    }
}
