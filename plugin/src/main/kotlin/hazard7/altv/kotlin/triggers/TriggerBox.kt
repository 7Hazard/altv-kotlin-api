package hazard7.altv.kotlin.triggers

import hazard7.altv.kotlin.entities.Entity

/**
 * Trigger volume to detect whether entities are inside
 * Checking for entities inside the volume are made on demand with check
 */
class TriggerBox {
    companion object {
        var all = mutableListOf<TriggerBox>()
    }

    // Check for entities inside box, invoke handler for every entity
    fun check(handler: (ent: Entity) -> Unit)
    {

    }
}
