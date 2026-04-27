package com.slideremote.android.app

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.slideremote.android.feature.bluetooth.BluetoothIntroScreen
import com.slideremote.android.feature.bluetooth.BluetoothUnsupportedScreen
import com.slideremote.android.feature.companion.CompanionDownloadScreen
import com.slideremote.android.feature.connection.QrScanScreen
import com.slideremote.android.feature.connection.WifiPairingScreen
import com.slideremote.android.feature.demo.DemoRemoteScreen
import com.slideremote.android.feature.home.HomeScreen
import com.slideremote.android.feature.remote.RemoteScreen

private object Routes {
    const val HOME = "home"
    const val BLUETOOTH_INTRO = "bluetooth_intro"
    const val BLUETOOTH_UNSUPPORTED = "bluetooth_unsupported"
    const val COMPANION_DOWNLOAD = "companion_download"
    const val WIFI_PAIRING = "wifi_pairing"
    const val QR_SCAN = "qr_scan"
    const val REMOTE = "remote"
    const val DEMO = "demo"
}

@Composable
fun AppNavHost(navController: NavHostController = rememberNavController()) {
    NavHost(
        navController = navController,
        startDestination = Routes.HOME
    ) {
        composable(Routes.HOME) {
            HomeScreen(
                onBluetoothDirect = { navController.navigateSingleTop(Routes.BLUETOOTH_INTRO) },
                onWifiHotspot = { navController.navigateSingleTop(Routes.COMPANION_DOWNLOAD) },
                onDemo = { navController.navigateSingleTop(Routes.DEMO) }
            )
        }
        composable(Routes.BLUETOOTH_INTRO) {
            BluetoothIntroScreen(
                onBack = navController::popBackStack,
                onUseWifi = { navController.navigateSingleTop(Routes.COMPANION_DOWNLOAD) },
                onUnsupported = { navController.navigateSingleTop(Routes.BLUETOOTH_UNSUPPORTED) }
            )
        }
        composable(Routes.BLUETOOTH_UNSUPPORTED) {
            BluetoothUnsupportedScreen(
                onBack = navController::popBackStack,
                onUseWifi = { navController.navigateSingleTop(Routes.COMPANION_DOWNLOAD) }
            )
        }
        composable(Routes.COMPANION_DOWNLOAD) {
            CompanionDownloadScreen(
                onBack = navController::popBackStack,
                onContinue = { navController.navigateSingleTop(Routes.WIFI_PAIRING) }
            )
        }
        composable(Routes.WIFI_PAIRING) {
            WifiPairingScreen(
                onBack = navController::popBackStack,
                onScanQr = { navController.navigateSingleTop(Routes.QR_SCAN) },
                onConnected = { navController.navigateSingleTop(Routes.REMOTE) }
            )
        }
        composable(Routes.QR_SCAN) {
            QrScanScreen(
                onBack = navController::popBackStack,
                onUseManual = { navController.popBackStack(Routes.WIFI_PAIRING, inclusive = false) },
                onConnected = { navController.navigateSingleTop(Routes.REMOTE) }
            )
        }
        composable(Routes.REMOTE) {
            RemoteScreen(onBack = navController::popBackStack)
        }
        composable(Routes.DEMO) {
            DemoRemoteScreen(onBack = navController::popBackStack)
        }
    }
}

private fun NavHostController.navigateSingleTop(route: String) {
    navigate(route) {
        launchSingleTop = true
    }
}
