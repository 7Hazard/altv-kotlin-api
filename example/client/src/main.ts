import * as altv from "alt-client"
import * as native from "natives"

enum PermissionState
{
  Always,
  Once,
  Denied,
  Never,
  Failed
}

enum Permission
{
  None,
  ScreenCapture,
  All
}

altv.log("KOTLIN TEST")

let state = altv.getPermissionState(Permission.ScreenCapture)
altv.log(`CURRENT STATE: ${PermissionState[state]}`)

async function takePicture()
{
  let result = await altv.requestPermission(Permission.ScreenCapture)
  altv.log(`EARLY PERMISSION STATE IS ${PermissionState[result]}`)

  let state = altv.saveScreenshot("test")
  altv.log(`SCREENSHOT PERMISSION STATE WAS ${PermissionState[state]}`)
}

takePicture()

