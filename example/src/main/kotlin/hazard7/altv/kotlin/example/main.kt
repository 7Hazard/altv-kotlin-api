package hazard7.altv.kotlin.example

import hazard7.altv.kotlin.Resource
import hazard7.altv.kotlin.entities.Player
import hazard7.altv.kotlin.entities.Vehicle
import hazard7.altv.kotlin.events.ServerEvent
import hazard7.altv.kotlin.logInfo
import hazard7.altv.kotlin.math.Float3
import hazard7.altv.kotlin.nextTick
import jnr.ffi.Pointer
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

    val veh = Vehicle("voltic2", Float3(-15, 20, 75), Float3(0, 0, 0))
    Vehicle("towtruck", Float3(10, 10, 75), Float3(0, 0, 0))
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
//            logInfo("bajja")
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

    class MyVehicle : Vehicle {
        constructor(pointer: Pointer) : super(pointer)
        constructor(modelname: String)
            : super(modelname, Float3(), Float3())

        val test = "test"
    }
    class MyVehicleFactory : Resource.ObjectFactory<MyVehicle> {
        override fun create(pointer: Pointer): MyVehicle = MyVehicle(pointer)
    }
    res.setVehicleFactory(MyVehicleFactory())
    val dveh = MyVehicle("voltic2")
    logInfo("Testing derived vehicle: ${dveh.test}")

    class MyPlayer constructor(pointer: Pointer) : Player(pointer)
    {
        val test = "test"
    }
    class MyPlayerFactory : Resource.ObjectFactory<MyPlayer> {
        override fun create(pointer: Pointer) = MyPlayer(pointer)
    }
    res.setPlayerFactory(MyPlayerFactory())

    res.onPlayerConnect {
        // Inheritance
        val xplayer = it.getPlayer(res) as MyPlayer
        logInfo("Testing inherited player: ${xplayer.test}")

        logInfo("Player ${xplayer.name} connected!")
        xplayer.spawn(Float3(0, 0, 72))
        xplayer.setModel("s_m_y_airworker")
        xplayer.giveWeapon("CombatPistol", 500, true)

        Vehicle("oppressor2", Float3(2, 4, 75), Float3(0, 0, 0))
        Vehicle("sultan", Float3(2, 2, 75), Float3(0, 0, 0))
        Vehicle("voltic2", Float3(4, 4, 75), Float3(0, 0, 0))

        xplayer.emit("welcome")

        xplayer.emit("arraytest", arrayOf(77, "titi"))
        xplayer.emit("listtest", listOf(88, "toto"))
        xplayer.emit("maptest", hashMapOf("val1" to 55, "val2" to 66))

        true
    }

    res.onPlayerDisconnect {
        val player = it.getPlayer(res)
        logInfo("Player ${player.name} disconnected!")
        true
    }

    res.onPlayerDied {
        val player = it.getPlayer(res)
        // wait 2 sec then revive, in a coroutine
        GlobalScope.launch {
            logInfo("Player ${player.name} died at ${player.pos}!")

            delay(2000)
            player.spawn(player.pos).await()
            player.health = 50
            logInfo("Revived player to ${player.health} hp")
        }

        true
    }

    res.onPlayerRecievedDamage {
        val player = it.getPlayer(res)
        logInfo("Player ${player.name} was damaged for ${it.damage} hp!")
        true
    }

    res.onPlayerEnteredVehicle {
        val player = it.getPlayer(res)
        logInfo("Player ${player.name} entered a vehicle!")
        player.giveWeapon("weapon_revolver", 50, true)
        true
    }

    res.onPlayerLeftVehicle {
        val player = it.getPlayer(res)
        logInfo("Player ${player.name} left a vehicle!")
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

    res.onClientEvent("test") {
        val player = it.getPlayer(res)
        val v = it.getArg<Int>(0)
        logInfo("PLAYER ${player.name} triggered test event with $v")
    }
//    res.onClientEvent("test", {
//        logInfo("BAAAD")
//    })

    Vehicle("police3", Float3(10, 10, 75), Float3())

    ServerEvent.emit("test", Random(System.currentTimeMillis()).nextInt(69, 421))

    res.onConsoleCommand("hi", {
        if(it.size > 0)
            logInfo("Hello ${it[0]}")
    })

    logInfo("Started example resource!")
}
