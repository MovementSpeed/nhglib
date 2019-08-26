package io.github.movementspeed.nhglib.utils.debug

import com.badlogic.gdx.Gdx
import io.github.movementspeed.nhglib.Nhg

/**
 * Created by Fausto Napoli on 19/10/2016.
 */
fun log(caller: Any, message: String) {
    if (Nhg.debugLogs && canLog()) {
        Gdx.app.log(getCallerString(caller), message)
    }
}

fun log(caller: Any, message: String, vararg objects: Any) {
    if (Nhg.debugLogs && canLog()) {
        val formattedMessage = String.format(message, *objects)
        Gdx.app.log(getCallerString(caller), formattedMessage)
    }
}

private fun canLog(): Boolean {
    return true
}

private fun getCallerString(caller: Any): String {
    var callerString: String

    if (caller is String) {
        callerString = caller
    } else {
        callerString = caller.javaClass.name
        val lastIndexOfDot = callerString.lastIndexOf(".")
        callerString = callerString.substring(lastIndexOfDot + 1, callerString.length)
    }

    return callerString
}