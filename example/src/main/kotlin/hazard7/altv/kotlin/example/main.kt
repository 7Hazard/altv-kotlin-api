package hazard7.altv.kotlin.example

import hazard7.altv.kotlin.Resource
import hazard7.altv.kotlin.entities.Player
import hazard7.altv.kotlin.entities.Vehicle
import hazard7.altv.kotlin.events.ServerEvent
import hazard7.altv.kotlin.logInfo
import hazard7.altv.kotlin.math.Float3
import hazard7.altv.kotlin.nextTick
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.random.Random

fun main(res: Resource)
{
    // is js by default
//    res.clientType = "js"

    GlobalScope.launch {
        nextTick {
            logInfo("OK 1")
            delay(1000)
            logInfo("OK 2")
        }.await()
    }

    val veh = Vehicle("adder", Float3(10, 10, 75), Float3(0, 0, 0))
    Vehicle("buzzard", Float3(-15, 20, 75), Float3(0, 0, 0))
//    Vehicle("annihilator", Float3(-15, 30, 75), Float3(0, 0, 0))

//    GlobalScope.launch {
//        while(true)
//        {
//            delay(100)
//            logInfo("Pre rot:  ${veh.rot}")
////            veh.pos = Float3(10, 10, 80)
//            veh.rot += 0.1f
//            logInfo("Post rot: ${veh.rot}")
//        }
//    }

//    GlobalScope.launch {
//        veh.bodyHealth = 700u;
//        logInfo("after new: ${veh.bodyHealth}")
//        while (true)
//        {
//            delay(100)
//            val newhealth = Random(System.currentTimeMillis()).nextInt(500, 1000).toUInt()
//            logInfo("current: ${veh.bodyHealth}, new: ${newhealth}")
//            veh.bodyHealth = newhealth
//            logInfo("after new: ${veh.bodyHealth}")
//        }
//    }

    res.onTick {
//        if(veh.owner != null)
//        {
//            logInfo("OWNER ${veh.owner!!.name}")
//        } else {
//            logInfo("bögfitta")
//        }
//        val owner = veh.owner
//        veh.setOwner(null, true)

//        val rot = veh.rot
//        rot.z += 0.2f
//        veh.rot = rot
//        logInfo("x: ${veh.rot.x}, y: ${veh.rot.y}, z: ${veh.rot.z}")

//        logInfo("Current health ${veh.bodyHealth}")
//        veh.bodyHealth-=3u
//        logInfo("new health ${veh.bodyHealth}")

//        veh.setOwner(owner, false)
    }

    res.onPlayerConnect {
        logInfo("Player ${it.player.name} connected!")
        it.player.spawn(Float3(0, 0, 72))
        it.player.setModel("s_m_y_airworker")
        it.player.giveWeapon("CombatPistol", 500, true)

        Vehicle("oppressor2", Float3(2, 4, 75), Float3(0, 0, 0))
        Vehicle("sultan", Float3(2, 2, 75), Float3(0, 0, 0))
        Vehicle("voltic2", Float3(4, 4, 75), Float3(0, 0, 0))

        it.player.emit("welcome")

        true
    }

    res.onPlayerDisconnect {
        logInfo("Player ${it.player.name} disconnected!")
        true
    }

    res.onPlayerDied {
        // wait 2 sec then revive, in a coroutine
        GlobalScope.launch {
            logInfo("Player ${it.player.name} died at ${it.player.pos}!")

            delay(2000)
            it.player.spawn(it.player.pos).await()
            it.player.health = 50
            logInfo("Revived player to ${it.player.health} hp")
        }

        true
    }

    res.onPlayerRecievedDamage {
        logInfo("Player ${it.target.name} was damaged for ${it.damage} hp!")
        true
    }

    res.onPlayerEnteredVehicle {
        logInfo("Player ${it.player.name} entered a vehicle!")
        it.player.giveWeapon("weapon_revolver", 50, true)
        true
    }

    res.onPlayerLeftVehicle {
        logInfo("Player ${it.player.name} left a vehicle!")
        true
    }

    suspend fun b(v: Int) {
        delay(300)
        logInfo("3ST TEST EVENT CALLED, RAND VAL $v")
    }
    suspend fun a(v: Int) {
        delay(100)
        logInfo("1ST TEST EVENT CALLED, RAND VAL $v")
    }
    res.onServerEvent("test", ::a)
    res.onServerEvent("test", ::b)
    res.onServerEvent("test") { v: Int ->
        logInfo("2ND TEST EVENT CALLED, RAND VAL $v")
    }

    res.onClientEvent("test", { player: Player, v: Int ->
        logInfo("PLAYER ${player.name} triggered test event with $v")
    })
//    res.onClientEvent("test", {
//        logInfo("BAAAD")
//    })

    Vehicle("police3", Float3(10, 10, 75), Float3())

    ServerEvent.emit("test", Random(System.currentTimeMillis()).nextInt(69, 421))

    logInfo("Started example resource!")
}
