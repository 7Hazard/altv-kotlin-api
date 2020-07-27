package hazard7.altv.kotlin.example

import hazard7.altv.kotlin.Resource
import hazard7.altv.kotlin.entities.Vehicle
import hazard7.altv.kotlin.events.ServerEvent
import hazard7.altv.kotlin.logInfo
import hazard7.altv.kotlin.math.Float3
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.random.Random

fun main(res: Resource)
{
    // is js by default
//    res.clientType = "js"

//    val veh = Vehicle("adder", Float3(10, 10, 75), Float3(0, 0, 0))
////    GlobalScope.launch {
////        while(true)
////        {
////            delay(100)
////            logInfo("Pre rot:  ${veh.rot}")
//////            veh.pos = Float3(10, 10, 80)
////            veh.rot += 0.1f
////            logInfo("Post rot: ${veh.rot}")
////        }
////    }
//    res.onTick {
//        logInfo("Pre rot:  ${veh.rot}")
//        veh.rot += 0.1f
//        logInfo("Post rot: ${veh.rot}")
//    }

    res.onPlayerConnect {
        logInfo("Player ${it.player.name} connected!")
        it.player.spawn(Float3(0, 0, 72))
        it.player.setModel("s_m_y_airworker")

        Vehicle("oppressor2", Float3(2, 2, 75), Float3(0, 0, 0))

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
            it.player.spawn(it.player.pos)
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
        true
    }

    res.onPlayerLeftVehicle {
        logInfo("Player ${it.player.name} left a vehicle!")
        true
    }

    res.onServerEvent("test") { v: Int ->
        logInfo("TEST EVENT CALLED, RAND VAL $v")
    }

    ServerEvent.send("test", Random(System.currentTimeMillis()).nextInt(69, 421))

    logInfo("Started example resource!")
}
