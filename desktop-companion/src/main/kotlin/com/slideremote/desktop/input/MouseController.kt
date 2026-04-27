package com.slideremote.desktop.input

import com.slideremote.desktop.protocol.MouseAction
import java.awt.AWTException
import java.awt.MouseInfo
import java.awt.Robot
import java.awt.event.InputEvent
import kotlin.math.roundToInt

class MouseController {
    private val robot: Robot by lazy {
        try {
            Robot().apply {
                autoDelay = 5
            }
        } catch (exception: AWTException) {
            throw IllegalStateException("Nao foi possivel inicializar o controle de mouse.", exception)
        }
    }

    fun execute(
        action: MouseAction,
        deltaX: Int = 0,
        deltaY: Int = 0,
        scrollY: Int = 0
    ): Result<Unit> {
        return runCatching {
            when (action) {
                MouseAction.MOVE -> move(deltaX, deltaY)
                MouseAction.LEFT_CLICK -> click(InputEvent.BUTTON1_DOWN_MASK, 1)
                MouseAction.RIGHT_CLICK -> click(InputEvent.BUTTON3_DOWN_MASK, 1)
                MouseAction.DOUBLE_CLICK -> click(InputEvent.BUTTON1_DOWN_MASK, 2)
                MouseAction.SCROLL -> robot.mouseWheel(scrollY)
            }
        }
    }

    private fun move(deltaX: Int, deltaY: Int) {
        if (deltaX == 0 && deltaY == 0) return
        val location = MouseInfo.getPointerInfo().location
        val sensitivity = 1.35f
        robot.mouseMove(
            location.x + (deltaX * sensitivity).roundToInt(),
            location.y + (deltaY * sensitivity).roundToInt()
        )
    }

    private fun click(mask: Int, times: Int) {
        repeat(times) {
            robot.mousePress(mask)
            Thread.sleep(25)
            robot.mouseRelease(mask)
            Thread.sleep(60)
        }
    }
}

