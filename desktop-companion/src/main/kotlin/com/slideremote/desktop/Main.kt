package com.slideremote.desktop

import com.slideremote.desktop.ui.CompanionWindow
import javax.swing.SwingUtilities

fun main() {
    SwingUtilities.invokeLater {
        CompanionWindow().show()
    }
}

