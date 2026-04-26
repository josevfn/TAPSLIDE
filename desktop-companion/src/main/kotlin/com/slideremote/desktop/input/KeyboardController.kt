package com.slideremote.desktop.input

import com.slideremote.desktop.protocol.RemoteCommand
import java.awt.AWTException
import java.awt.Robot

class KeyboardController {
    private val robot: Robot by lazy {
        try {
            Robot().apply {
                autoDelay = 20
            }
        } catch (exception: AWTException) {
            throw IllegalStateException("Nao foi possivel inicializar o controle de teclado.", exception)
        }
    }

    fun execute(command: RemoteCommand): Result<Unit> {
        val keyCode = DesktopCommandMapper.keyCodeFor(command)
            ?: return Result.success(Unit)

        return runCatching {
            robot.keyPress(keyCode)
            Thread.sleep(35)
            robot.keyRelease(keyCode)
        }
    }
}

