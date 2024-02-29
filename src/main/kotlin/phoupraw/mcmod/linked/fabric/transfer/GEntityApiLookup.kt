package phoupraw.mcmod.linked.fabric.transfer

import net.fabricmc.fabric.api.event.Event
import net.fabricmc.fabric.api.event.EventFactory
import net.minecraft.entity.Entity
import net.minecraft.entity.EntityType
import net.minecraft.util.Identifier
import phoupraw.mcmod.linked.lang.GenericApiProvider

class GEntityApiLookup<A, C>(val id: Identifier) : GenericApiProvider<Entity, A, C> {
    val preliminary: Event<GenericApiProvider<Entity, A, C>> = newEvent()
    val fallback: Event<GenericApiProvider<Entity, A, C>> = newEvent()
    val keySpecific: Map<EntityType<*>, Event<GenericApiProvider<Entity, A, C>>>
        get() = _keySpecific
    private val _keySpecific: MutableMap<EntityType<*>, Event<GenericApiProvider<Entity, A, C>>> = mutableMapOf()
    @Suppress("UNCHECKED_CAST")
    operator fun <T : Entity> get(key: EntityType<T>): Event<GenericApiProvider<T, A, C>> = _keySpecific.getOrPut(key) { newEvent() } as Event<GenericApiProvider<T, A, C>>
    fun <T : Entity> register(vararg keys: EntityType<T>, find: GenericApiProvider<T, A, C>) {
        for (key in keys) {
            get(key).register(find)
        }
    }

    override fun invoke(key: Entity, context: C): A? {
        preliminary.invoker()(key, context)?.also { return it }
        keySpecific[key.type]?.invoker()?.invoke(key, context)?.also { return it }
        fallback.invoker()(key, context)?.also { return it }
        return null
    }

    private fun <T : Entity> newEvent(): Event<GenericApiProvider<T, A, C>> {
        return EventFactory.createArrayBacked(GenericApiProvider::class.java) { finds ->
            invoker@{ key, context ->
                for (find in finds) {
                    find(key, context)?.also { return@invoker it }
                }
                null
            }
        }
    }
}