package com.slideremote.desktop.input

import com.slideremote.desktop.protocol.RemoteCommand
import java.awt.event.KeyEvent

object DesktopCommandMapper {
    fun keyCodeFor(command: RemoteCommand): Int? {
        return when (command) {
            RemoteCommand.NEXT_SLIDE -> KeyEvent.VK_RIGHT
            RemoteCommand.PREVIOUS_SLIDE -> KeyEvent.VK_LEFT
            RemoteCommand.START_PRESENTATION -> KeyEvent.VK_F5
            RemoteCommand.END_PRESENTATION -> KeyEvent.VK_ESCAPE
            RemoteCommand.BLACK_SCREEN -> KeyEvent.VK_B
            RemoteCommand.WHITE_SCREEN -> KeyEvent.VK_W
            RemoteCommand.HEARTBEAT,
            RemoteCommand.PING,
            RemoteCommand.PONG -> null
        }
    }
}

