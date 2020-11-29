import * as alt from "alt-client";
import * as native from "natives";
import * as sub from "./sub";

// let view = new alt.WebView("https://google.com", false);
// view.focus();
// alt.showCursor(true)
// alt.setTimeout(() => {
//   view.isVisible = false;
// }, 1000);

// native.setPlayerWantedLevel(alt.Player.local.scriptID, 4, false)
// native.setPlayerWantedLevelNow(alt.Player.local.scriptID, false)

sub.dostuff();

// alt.on("render", function () {
//   alt.clearGFX();

//   // alt.drawGFX(1, 1);

//   // alt.log("VEHICLE COUNT: " + alt.Vehicle.all.length);
//   for (let veh of alt.Vehicle.all) {
//     let [onScreen, sx, sy] = native.getScreenCoordFromWorldCoord(
//       veh.pos.x,
//       veh.pos.y,
//       veh.pos.z,
//       0,
//       0
//     );
//     if (onScreen) {
//       alt.log("VEHICLE ON SCREEN");
//       sx = sx*10
//       sy = sy*10
//       alt.log("x:" + sx + " y:"+sy);
//       alt.drawGFX(sx, sy);
//     }
//   }
// });

// alt.on("render", ()=>{
//   alt.log("RENDER")
// })

// let g = [];
// for (let i = 0; i < 10; i++) {
//   g.push("TEST " + i);
// }
// alt.emitServer("test", g);

// alt.everyTick(() => {
//   if (alt.Player.local.isInVehicle) alt.log("IN VEHICLE");
//   else alt.log("NOT IN VEHICLE");
// });

// alt.on("render", () => {
//   alt.log("VEHICLE COUNT: " + alt.Vehicle.all.length);
//   for (let veh of alt.Vehicle.all) {
//     let [onScreen, sx, sy] = native.getScreenCoordFromWorldCoord(
//       veh.pos.x,
//       veh.pos.y,
//       veh.pos.z,
//       0,
//       0
//     );
//     if (onScreen) {
//       alt.log("VEHICLE ON SCREEN");
//       alt.drawGFX(sx, sy);
//     }
//   }
// });

let blip = new alt.RadiusBlip(0, 0, 0, 30);
alt.setTimeout(()=>{
  alt.log("DELETING BLIP")
  blip.destroy()
}, 5000)

alt.emitServer("test", 5)

alt.onServer("arraytest", (array)=>{
  alt.log("ARRAY TEST, LEN "+array.length)
  for (let i = 0; i < array.length; i++) {
    alt.log(`${i}: ${array[i]}`)
  }
})
alt.onServer("listtest", (array)=>{
  alt.log("LIST TEST, LEN "+array.length)
  for (let i = 0; i < array.length; i++) {
    alt.log(`${i}: ${array[i]}`)
  }
})
alt.onServer("maptest", (map)=>{
  alt.log("MAP TEST")
  alt.log(`val1: ${map["val1"]}`)
  alt.log(`val2: ${map["val2"]}`)
  // for (const [key, val] of map) {
  //   alt.log(`key: ${key}, val: ${val}`)
  // }
})

function getveh() {
  return alt.Player.local.vehicle.scriptID;
}
let eng;
alt.on("consoleCommand", async (cmd, ...args: any[]) => {
  if(cmd == "maptest") {
    alt.emitServer("maptest")
  }
  else if (cmd == "carmem") {
    // let buf = alt.getEntityMemoryByID(alt.Player.local.vehicle.scriptID);
    // alt.log(buf.address().toString(16));
  }
  else if (cmd == "left") {
    native.setVehicleIndicatorLights(
      alt.Player.local.vehicle.scriptID,
      1,
      true
    );
  }
  else if (cmd == "right") {
    native.setVehicleIndicatorLights(
      alt.Player.local.vehicle.scriptID,
      0,
      true
    );
  }
  else if (cmd == "loff") {
    native.setVehicleIndicatorLights(
      alt.Player.local.vehicle.scriptID,
      1,
      false
    );
  }
  else if (cmd == "roff") {
    native.setVehicleIndicatorLights(
      alt.Player.local.vehicle.scriptID,
      0,
      false
    );
  }
  else if (cmd == "alarm") {
    native.startVehicleAlarm(alt.Player.local.vehicle.scriptID);
  }
  else if (cmd == "interior") {
    native.setVehicleInteriorlight(getveh(), true);
  }
  else if (cmd == "finterior") {
    native.setVehicleInteriorlight(getveh(), false);
  }
  else if (cmd == "night") {
    native.setClockTime(0, 0, 0);
  }
  else if (cmd == "day") {
    native.setClockTime(12, 0, 0);
  }
  else if (cmd == "respawn") {
    alt.emitServer("respawn");
  }
  else if (cmd == "fix") {
    native.setVehicleFixed(getveh());
  }
  else if (cmd == "veh") {
    let pos = alt.Player.local.pos;
    alt.log(args[0]);
    alt.loadModel(alt.hash(args[0]));
    native.createVehicle(
      alt.hash(args[0]),
      pos.x,
      pos.y,
      pos.z,
      0,
      false,
      false,
      false
    );
  }
  else if (cmd == "wep") {
    // native.giveWeaponToPed(
    //   alt.Player.local.scriptID,
    //   alt.hash(args[0]),
    //   500,
    //   false,
    //   true
    // );
    alt.emitServer("weapon", alt.hash(args[0]), 500, true);
  }
  else if (cmd == "tank") {
    native.setVehicleCanLeakPetrol(getveh(), true);
    native.setDisableVehiclePetrolTankFires(getveh(), true);
    native.setDisableVehiclePetrolTankDamage(getveh(), false);
  }
  else if (cmd == "engine") {
    if (eng) alt.clearEveryTick(eng);
    eng = alt.everyTick(() => {
      native.setVehicleCheatPowerIncrease(getveh(), parseFloat(args[0]));
    });
  }
  else if (cmd == "noeng") {
    if (eng) alt.clearEveryTick(eng);
  }
  else if (cmd == "gravity") {
    // let veh = alt.Player.local.vehicle;
    // alt.log(`Current gravity: ${veh.gravity}`);
    // veh.gravity = parseFloat(args[0]);
    // alt.log(`New gravity: ${veh.gravity}`);
  }
  else if (cmd == "ss") {
    let img = await alt.takeScreenshot();
    alt.log("SCREENSHOT BASE64: " + img);
    // view.emit("test", img)
    alt.emitServer("img", img);
  }
  else if (cmd == "ssg") {
    let img = await alt.takeScreenshotGameOnly();
    alt.log("SCREENSHOT BASE64: " + img);
  }
  else if (cmd == "respawn") {
    alt.emitServer("respawn");
  }
  else if (cmd == "ev") {
    alt.emitServer("test", ...args);
  }
  else if (cmd == "showcursor") {
    alt.showCursor(args[0]);
  }
  else if (cmd == "focus") {
    // if (args[0] == true) view.focus();
    // else view.unfocus();
  }
  else if (cmd == "proof") {
    alt.log("SETTING PROOFS");
    native.setEntityProofs(
      alt.Player.local.scriptID,
      true,
      true,
      true,
      true,
      true,
      false,
      false,
      true
    );
  }
  else if ((cmd = "unproof")) {
    native.setEntityProofs(
      alt.Player.local.scriptID,
      false,
      false,
      false,
      false,
      false,
      false,
      false,
      false
    );
    alt.log("REMOVED PROOFS");
  }
});

let tasklist = {
  0: "CTaskHandsUp",
  1: "CTaskClimbLadder",
  3: "CTaskCombatRoll",
  4: "CTaskAimGunOnFoot",
  5: "CTaskMovePlayer",
  6: "CTaskPlayerOnFoot",
  8: "CTaskWeapon",
  9: "CTaskPlayerWeapon",
  10: "CTaskPlayerIdles",
  12: "CTaskAimGun",
  15: "CTaskDoNothing",
  16: "CTaskGetUp",
  17: "CTaskGetUpAndStandStill",
  18: "CTaskFallOver",
  19: "CTaskFallAndGetUp",
  20: "CTaskCrawl",
  25: "CTaskComplexOnFire",
  26: "CTaskDamageElectric",
  29: "CTaskClearLookAt",
  34: "CTaskMoveStandStill",
  35: "CTaskComplexControlMovement",
  38: "CTaskAmbientClips",
  39: "CTaskMoveInAir",
  44: "CTaskExitVehicle",
  45: "CTaskForceMotionState",
  47: "CTaskGoToAndClimbLadder",
  48: "CTaskClimbLadderFully",
  50: "CTaskVault",
  51: "CTaskDropDown",
  52: "CTaskAffectSecondaryBehaviour",
  53: "CTaskAmbientLookAtEvent",
  54: "CTaskOpenDoor",
  55: "CTaskShovePed",
  56: "CTaskSwapWeapon",
  57: "CTaskGeneralSweep",
  62: "CTaskArrestPed",
  63: "CTaskArrestPed2",
  64: "CTaskBusted",
  65: "CTaskFirePatrol",
  68: "CTaskAmbulancePatrol",
  76: "CTaskWitness",
  78: "CTaskArmy",
  93: "CTaskCarReactToVehicleCollision",
  95: "CTaskCarReactToVehicleCollisionGetOut",
  97: "CTaskDyingDead",
  104: "CTaskChatScenario",
  106: "CTaskCowerScenario",
  107: "CTaskDeadBodyScenario",
  117: "CTaskCoupleScenario",
  126: "CTaskCower",
  127: "CTaskCrouch",
  128: "CTaskMelee",
  129: "CTaskMoveMeleeMovement",
  130: "CTaskMeleeActionResult",
  137: "CTaskComplexEvasiveStep",
  140: "CTaskComplexStuckInAir",
  146: "CTaskDuckAndCover",
  147: "CTaskAggressiveRubberneck",
  150: "CTaskInVehicleBasic",
  151: "CTaskCarDriveWander",
  152: "CTaskLeaveAnyCar",
  153: "CTaskComplexGetOffBoat",
  155: "CTaskCarSetTempAction",
  156: "CTaskBringVehicleToHalt",
  157: "CTaskCarDrive",
  159: "CTaskPlayerDrive",
  160: "CTaskEnterVehicle",
  161: "CTaskEnterVehicleAlign",
  162: "CTaskOpenVehicleDoorFromOutside",
  163: "CTaskEnterVehicleSeat",
  164: "CTaskCloseVehicleDoorFromInside",
  165: "CTaskInVehicleSeatShuffle",
  167: "CTaskExitVehicleSeat",
  168: "CTaskCloseVehicleDoorFromOutside",
  169: "CTaskControlVehicle",
  170: "CTaskMotionInAutomobile",
  173: "CTaskMotionInVehicle",
  178: "CTaskGetOnTrain",
  179: "CTaskGetOffTrain",
  195: "CTaskGoToCarDoorAndStandStill",
  196: "CTaskMoveGoToVehicleDoor",
  197: "CTasksArrayetPedInVehicle",
  198: "CTasksArrayetPedOutOfVehicle",
  199: "CTaskVehicleMountedWeapon",
  200: "CTaskVehicleGun",
  205: "CTaskMoveGoToPoint",
  206: "CTaskMoveAchieveHeading",
  208: "CTaskComplexGoToPointAndStandStillTimed",
  209: "CTaskMoveFollowPointRoute",
  215: "CTaskExhaustedFlee",
  218: "CTasksArraymartFlee",
  219: "CTaskFlyAway",
  223: "CTaskFollowLeaderInFormation",
  224: "CTaskGoToPointAnyMeans",
  226: "CTaskFollowLeaderAnyMeans",
  228: "CTaskFlyToPoint",
  229: "CTaskFlyingWander",
  230: "CTaskGoToPointAiming",
  238: "CTaskMoveFollowNavMesh",
  239: "CTaskMoveGoToPointOnRoute",
  240: "CTaskEscapeBlast",
  252: "CTaskMoveSlideToCoord",
  256: "CTaskGetOutOfWater",
  261: "CTaskFollowWaypointRecording",
  264: "CTaskMotionPed",
  268: "CTaskHumanLocomotion",
  270: "CTaskMotionStrafing",
  272: "CTaskMotionAiming",
  273: "CTaskBirdLocomotion",
  274: "CTaskFlightlessBirdLocomotion",
  278: "CTaskFishLocomotion",
  279: "CTaskQuadLocomotion",
  281: "CTaskMotionSwimming",
  285: "CTaskMotionAimingTransition",
  287: "CTaskCover",
  288: "CTaskMotionInCover",
  289: "CTaskAimAndThrowProjectile",
  290: "CTaskGun",
  291: "CTaskAimFromGround",
  295: "CTaskAimGunVehicleDriveBy",
  296: "CTaskAimGunScripted",
  298: "CTaskReloadGun",
  300: "CTaskEnterCover",
  301: "CTaskExitCover",
  302: "CTaskAimGunFromCoverIntro",
  303: "CTaskAimGunFromCoverOutro",
  304: "CTaskAimGunBlindFire",
  307: "CTaskCombatClosestTargetInArea",
  308: "CTaskCombatAdditionalTask",
  309: "CTaskInCover",
  313: "CTaskAimSweep",
  320: "CTaskAgitated",
  321: "CTaskAgitatedAction",
  322: "CTaskConfront",
  327: "CTaskCrouchToggle",
  334: "CTaskParachute",
  339: "CTaskCombatSeekCover",
  341: "CTaskCombatFlank",
  342: "CTaskCombat",
  343: "CTaskCombatMounted",
  351: "CTaskThreatResponse",
  363: "CTaskDraggingToSafety",
  364: "CTaskDraggedToSafety",
  366: "CTaskMoveWithinAttackWindow",
  367: "CTaskMoveWithinDefensiveArea",
  370: "CTaskBoatChase",
  371: "CTaskBoatCombat",
  372: "CTaskBoatStrafe",
  383: "CTaskAdvance",
  384: "CTaskCharge",
  387: "CTaskAnimatedHitByExplosion",
  391: "CTaskNMBrace",
  394: "CTaskNMShot",
  395: "CTaskNMHighFall",
  399: "CTaskNMExplosion",
  402: "CTaskNMDangle",
  406: "CTaskBlendFromNM",
  407: "CTaskNMControl",
  414: "CTaskNMSit",
  416: "CTaskNMSimple",
  417: "CTaskRageRagdoll",
  420: "CTaskJumpVault",
  421: "CTaskJump",
  422: "CTaskFall",
  425: "CTaskChat",
  431: "CTaskBomb",
  432: "CTaskDetonator",
  434: "CTaskAnimatedAttach",
  440: "CTaskCutScene",
  443: "CTaskDiveToGround",
  444: "CTaskReactAndFlee",
  446: "CTaskCallPolice",
  465: "CTaskVehicleDeadDriver",
  468: "CTaskVehicleStop",
  480: "CTaskVehicleCrash",
  493: "CTaskVehiclePlayerDriveAutomobile",
  494: "CTaskVehiclePlayerDriveBike",
  495: "CTaskVehiclePlayerDriveBoat",
  496: "CTaskVehiclePlayerDriveSubmarine",
  497: "CTaskVehiclePlayerDriveSubmarineCar",
  498: "CTaskVehiclePlayerDrivePlane",
  499: "CTaskVehiclePlayerDriveHeli",
  500: "CTaskVehiclePlayerDriveAutogyro",
  501: "CTaskVehiclePlayerDriveDiggerArm",
  502: "CTaskVehiclePlayerDriveTrain",
  527: "CTaskVehicleTransformToSubmarine",
  528: "CTaskAnimatedFallback",
};

// alt.everyTick(() => {
//   for (let player of alt.Player.all) {
//     if (!player.scriptID) return;
//     let taskNames = "";
//     for (let i = 0; i < 8; i++) {
//       let taskID = player.getTaskTreeNodeInfo(i);
//       let taskName = tasklist[taskID] ? tasklist[taskID] : "EMPTY_SLOT";
//       taskNames += taskName + "~n~";
//     }

//     native.setDrawOrigin(player.pos.x, player.pos.y, player.pos.z, false);
//     native.beginTextCommandDisplayText("STRING");
//     native.setTextFont(4);
//     native.setTextCentre(true);
//     native.setTextScale(0.4, 0.4);
//     native.setTextProportional(true);
//     native.setTextColour(255, 255, 255, 255);
//     native.addTextComponentSubstringPlayerName(taskNames);
//     native.endTextCommandDisplayText(0, 0, 0);
//     native.clearDrawOrigin();
//   }
// });
