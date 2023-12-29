package com.lzq.dawn.util.permission.util

import android.Manifest
import android.annotation.TargetApi
import android.os.Build
import com.lzq.dawn.util.permission.PermissionL
import com.lzq.dawn.util.permission.task.ExternalStorageTask
import com.lzq.dawn.util.permission.task.BackgroundLocationTask


/**
 * 权限
 * className :PermissionConstants
 * createTime :2022/5/16 16:18
 * @Author :  Lzq
 *
 * 这里使用了  to
 * 查找资料得知 A to B这样的语法就是得到了一个包含A，B数据的Pair对象
 * mapOf方法接收的是Pair类型的可变参数列表。
 */

/**
 * 所有的特殊权限
 *
 */
val osSpecialPermissions= setOf(
    Manifest.permission.SYSTEM_ALERT_WINDOW,
    Manifest.permission.WRITE_SETTINGS,
    BackgroundLocationTask.ACCESS_BACKGROUND_LOCATION,
    ExternalStorageTask.MANAGE_EXTERNAL_STORAGE,
    Manifest.permission.REQUEST_INSTALL_PACKAGES,
    PermissionL.Permission.POST_NOTIFICATIONS,

)

/**
 * 根据官网得知从Android Q开始，我们不能通过权限来获取权限组。所以需要对应权限和权限组的关系。
 * @Link https://developer.android.com/about/versions/10/privacy/changes#permission-groups-removed
 */
@TargetApi(Build.VERSION_CODES.Q)
val permissionMapOnQ = mapOf(
    Manifest.permission.READ_CALENDAR to Manifest.permission_group.CALENDAR,
    Manifest.permission.WRITE_CALENDAR to Manifest.permission_group.CALENDAR,
    Manifest.permission.READ_CALL_LOG to Manifest.permission_group.CALL_LOG,
    Manifest.permission.WRITE_CALL_LOG to Manifest.permission_group.CALL_LOG,
    "android.permission.PROCESS_OUTGOING_CALLS" to Manifest.permission_group.CALL_LOG,
    Manifest.permission.CAMERA to Manifest.permission_group.CAMERA,
    Manifest.permission.READ_CONTACTS to Manifest.permission_group.CONTACTS,
    Manifest.permission.WRITE_CONTACTS to Manifest.permission_group.CONTACTS,
    Manifest.permission.GET_ACCOUNTS to Manifest.permission_group.CONTACTS,
    Manifest.permission.ACCESS_FINE_LOCATION to Manifest.permission_group.LOCATION,
    Manifest.permission.ACCESS_COARSE_LOCATION to Manifest.permission_group.LOCATION,
    Manifest.permission.ACCESS_BACKGROUND_LOCATION to Manifest.permission_group.LOCATION,
    Manifest.permission.RECORD_AUDIO to Manifest.permission_group.MICROPHONE,
    Manifest.permission.READ_PHONE_STATE to Manifest.permission_group.PHONE,
    Manifest.permission.READ_PHONE_NUMBERS to Manifest.permission_group.PHONE,
    Manifest.permission.CALL_PHONE to Manifest.permission_group.PHONE,
    Manifest.permission.ANSWER_PHONE_CALLS to Manifest.permission_group.PHONE,
    Manifest.permission.ADD_VOICEMAIL to Manifest.permission_group.PHONE,
    Manifest.permission.USE_SIP to Manifest.permission_group.PHONE,
    Manifest.permission.ACCEPT_HANDOVER to Manifest.permission_group.PHONE,
    Manifest.permission.BODY_SENSORS to Manifest.permission_group.SENSORS,
    Manifest.permission.ACTIVITY_RECOGNITION to Manifest.permission_group.ACTIVITY_RECOGNITION,
    Manifest.permission.SEND_SMS to Manifest.permission_group.SMS,
    Manifest.permission.RECEIVE_SMS to Manifest.permission_group.SMS,
    Manifest.permission.READ_SMS to Manifest.permission_group.SMS,
    Manifest.permission.RECEIVE_WAP_PUSH to Manifest.permission_group.SMS,
    Manifest.permission.RECEIVE_MMS to Manifest.permission_group.SMS,
    Manifest.permission.READ_EXTERNAL_STORAGE to Manifest.permission_group.STORAGE,
    Manifest.permission.WRITE_EXTERNAL_STORAGE to Manifest.permission_group.STORAGE,
    Manifest.permission.ACCESS_MEDIA_LOCATION to Manifest.permission_group.STORAGE,
)

/**
 * Android R 和 Android Q 没有新增权限
 */
@TargetApi(Build.VERSION_CODES.R)
val permissionMapOnR = mapOf(
    Manifest.permission.MANAGE_EXTERNAL_STORAGE to Manifest.permission_group.STORAGE,
).toMutableMap().apply {
    putAll(permissionMapOnQ)
}.toMap()
/**
 * Android S 新增了三个蓝牙权限
 */
@TargetApi(Build.VERSION_CODES.S)
val permissionMapOnS = mapOf(
    Manifest.permission.BLUETOOTH_SCAN to Manifest.permission_group.NEARBY_DEVICES,
    Manifest.permission.BLUETOOTH_ADVERTISE to Manifest.permission_group.NEARBY_DEVICES,
    Manifest.permission.BLUETOOTH_CONNECT to Manifest.permission_group.NEARBY_DEVICES,
).toMutableMap().apply {
    putAll(permissionMapOnR)
}.toMap()

/**
 * Android T 添加了3个蓝牙相关的运行时权限.
 */
@TargetApi(Build.VERSION_CODES.TIRAMISU)
val permissionMapOnT = mapOf(
    Manifest.permission.READ_MEDIA_IMAGES to Manifest.permission_group.READ_MEDIA_VISUAL,
    Manifest.permission.READ_MEDIA_VIDEO to Manifest.permission_group.READ_MEDIA_VISUAL,
    Manifest.permission.READ_MEDIA_AUDIO to Manifest.permission_group.READ_MEDIA_AURAL,
    Manifest.permission.POST_NOTIFICATIONS to Manifest.permission_group.NOTIFICATIONS,
    Manifest.permission.NEARBY_WIFI_DEVICES to Manifest.permission_group.NEARBY_DEVICES,
    Manifest.permission.BODY_SENSORS_BACKGROUND to Manifest.permission_group.SENSORS,
).toMutableMap().apply {
    putAll(permissionMapOnS)
}.toMap()