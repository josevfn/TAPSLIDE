package com.slideremote.android.feature.remote

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Mouse
import androidx.compose.material.icons.filled.TouchApp
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.positionChange
import androidx.compose.ui.unit.dp
import com.slideremote.android.core.model.MouseAction
import kotlin.math.roundToInt

@Composable
fun MousePad(
    onMouseMove: (Int, Int) -> Unit,
    onMouseAction: (MouseAction) -> Unit,
    onScroll: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .clip(RoundedCornerShape(8.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant)
                .pointerInput(Unit) {
                    detectDragGestures(
                        onDrag = { change, dragAmount ->
                            change.consume()
                            val delta = change.positionChange()
                            val x = (dragAmount.x.takeIf { it != 0f } ?: delta.x).roundToInt()
                            val y = (dragAmount.y.takeIf { it != 0f } ?: delta.y).roundToInt()
                            onMouseMove(x, y)
                        }
                    )
                }
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = Icons.Default.Mouse,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary
            )
            Text(
                text = "Mouse",
                style = MaterialTheme.typography.titleSmall,
                modifier = Modifier.padding(top = 6.dp)
            )
            Text(
                text = "Arraste para mover o ponteiro",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            FilledTonalButton(
                onClick = { onMouseAction(MouseAction.LEFT_CLICK) },
                modifier = Modifier.weight(1f)
            ) {
                Icon(Icons.Default.TouchApp, contentDescription = null)
                Text("Clique", modifier = Modifier.padding(start = 6.dp))
            }
            OutlinedButton(
                onClick = { onMouseAction(MouseAction.RIGHT_CLICK) },
                modifier = Modifier.weight(1f)
            ) {
                Text("Direito")
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(44.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedButton(
                onClick = { onMouseAction(MouseAction.DOUBLE_CLICK) },
                modifier = Modifier.weight(1f)
            ) {
                Text("Duplo")
            }
            OutlinedButton(
                onClick = { onScroll(-3) },
                modifier = Modifier.weight(1f)
            ) {
                Text("Rolar cima")
            }
            OutlinedButton(
                onClick = { onScroll(3) },
                modifier = Modifier.weight(1f)
            ) {
                Text("Rolar baixo")
            }
        }
    }
}

