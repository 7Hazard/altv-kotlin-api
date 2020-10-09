import * as alt from "alt-client";
import * as native from "natives";

// alt.log("KOTLIN TEST");

// let state = altv.getPermissionState(Permission.ScreenCapture)
// altv.log(`CURRENT STATE: ${PermissionState[state]}`)

// async function takePicture()
// {
//   // let result = await altv.requestPermission(Permission.ScreenCapture)
//   // altv.log(`EARLY PERMISSION STATE IS ${PermissionState[result]}`)

//   let state = altv.saveScreenshot("test")
//   altv.log(`SCREENSHOT PERMISSION STATE WAS ${PermissionState[state]}`)
// }

// takePicture()

// alt.setInterval(()=>{
//   alt.saveScreenshot("test")
//   // let base64 = alt.saveScreenshotBase64()
//   // alt.log("js BASE64: "+base64)
// }, 2000)

// let hr = 0
// alt.everyTick(()=>{
//   // alt.log("TICK")
//   if(hr >= 24) hr = 0
//   native.setClockTime(hr++, 0, 0)
// })

// let view = new alt.WebView("https://google.com", false)
// view.isVisible = false

// alt.on("render", ()=>{
//   alt.log("RENDER")
// })

let g = [];
for (let i = 0; i < 10; i++) {
  g.push("TEST " + i);
}
alt.emitServer("test", ...g);

function getveh() {
  return alt.Player.local.vehicle.scriptID;
}
let eng;
alt.on("consoleCommand", async (cmd, ...args: any[]) => {
  if (cmd == "carmem") {
    let buf = alt.getEntityMemoryByID(alt.Player.local.vehicle.scriptID);
    alt.log(buf.address().toString(16));
  } else if (cmd == "left") {
    native.setVehicleIndicatorLights(
      alt.Player.local.vehicle.scriptID,
      1,
      true
    );
  } else if (cmd == "right") {
    native.setVehicleIndicatorLights(
      alt.Player.local.vehicle.scriptID,
      0,
      true
    );
  } else if (cmd == "loff") {
    native.setVehicleIndicatorLights(
      alt.Player.local.vehicle.scriptID,
      1,
      false
    );
  } else if (cmd == "roff") {
    native.setVehicleIndicatorLights(
      alt.Player.local.vehicle.scriptID,
      0,
      false
    );
  } else if (cmd == "alarm") {
    native.startVehicleAlarm(alt.Player.local.vehicle.scriptID);
  } else if (cmd == "interior") {
    native.setVehicleInteriorlight(getveh(), true);
  } else if (cmd == "finterior") {
    native.setVehicleInteriorlight(getveh(), false);
  } else if (cmd == "night") {
    native.setClockTime(0, 0, 0);
  } else if (cmd == "day") {
    native.setClockTime(12, 0, 0);
  } else if (cmd == "respawn") {
    alt.emitServer("respawn");
  } else if (cmd == "fix") {
    native.setVehicleFixed(getveh());
  } else if (cmd == "veh") {
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
  } else if (cmd == "wep") {
    // native.giveWeaponToPed(
    //   alt.Player.local.scriptID,
    //   alt.hash(args[0]),
    //   500,
    //   false,
    //   true
    // );
    alt.emitServer("weapon", alt.hash(args[0]), 500, true);
  } else if (cmd == "tank") {
    native.setVehicleCanLeakPetrol(getveh(), true);
    native.setDisableVehiclePetrolTankFires(getveh(), true);
    native.setDisableVehiclePetrolTankDamage(getveh(), false);
  } else if (cmd == "engine") {
    if (eng) alt.clearEveryTick(eng);
    eng = alt.everyTick(() => {
      native.setVehicleCheatPowerIncrease(getveh(), parseFloat(args[0]));
    });
  } else if (cmd == "noeng") {
    if (eng) alt.clearEveryTick(eng);
  } else if (cmd == "gravity") {
    let veh = alt.Player.local.vehicle;
    alt.log(`Current gravity: ${veh.gravity}`);
    veh.gravity = parseFloat(args[0]);
    alt.log(`New gravity: ${veh.gravity}`);
  } else if (cmd == "ss") {
    let img = await alt.takeScreenshot();
    alt.log("SCREENSHOT BASE64: " + img);
    // view.emit("test", img)
    alt.emitServer("img", img);
  } else if (cmd == "ssg") {
    let img = await alt.takeScreenshotGameOnly();
    alt.log("SCREENSHOT BASE64: " + img);
  } else if (cmd == "respawn") {
    alt.emitServer("respawn");
  } else if (cmd == "ev") {
    alt.emitServer("test", ...g);
  }
});
