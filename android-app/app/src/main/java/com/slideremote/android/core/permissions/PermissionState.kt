package com.slideremote.android.core.permissions

data class PermissionState(
    val permission: String,
    val granted: Boolean,
    val shouldShowRationale: Boolean = false
)

