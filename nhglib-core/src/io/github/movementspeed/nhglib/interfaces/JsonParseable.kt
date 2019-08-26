package io.github.movementspeed.nhglib.interfaces

import com.badlogic.gdx.utils.JsonValue

/**
 * Created by Fausto Napoli on 19/12/2016.
 */
interface JsonParseable<T> {
    fun parse(jsonValue: JsonValue)
    fun get(): T
}
