package io.github.movementspeed.nhglib.core.ecs.utils

import com.artemis.*
import com.artemis.utils.ImmutableBag

/**
 * Created by Fausto Napoli on 07/12/2016.
 */
class Entities {
    private var entityWorld: World? = null

    val entitySystems: ImmutableBag<BaseSystem>
        get() = entityWorld!!.systems

    fun update(delta: Float) {
        entityWorld!!.setDelta(delta)
        entityWorld!!.process()
    }

    fun setEntityWorld(entityWorld: World) {
        this.entityWorld = entityWorld
    }

    fun removeComponent(entity: Int, type: Class<out Component>) {
        entityWorld!!.edit(entity).remove(type)
    }

    fun createEntity(): Int {
        return entityWorld!!.create()
    }

    fun createEntity(archetype: Archetype): Int {
        return entityWorld!!.create(archetype)
    }

    fun createArchetype(vararg components: Class<out Component>): Archetype {
        return ArchetypeBuilder()
                .add(*components)
                .build(entityWorld!!)
    }

    fun <T : BaseSystem> getEntitySystem(systemClass: Class<T>): T {
        return entityWorld!!.getSystem(systemClass)
    }

    fun <T : Component> createComponent(entity: Int, type: Class<T>): T {
        return entityWorld!!.getMapper(type).create(entity)
    }

    fun <T : Component> getComponent(entity: Int, type: Class<T>): T {
        return entityWorld!!.getMapper(type).get(entity)
    }

    fun <T : Component> getMapper(type: Class<T>): ComponentMapper<T> {
        return entityWorld!!.getMapper(type)
    }
}
