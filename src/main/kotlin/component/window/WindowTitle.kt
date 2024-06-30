package component.window

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.window.WindowDraggableArea
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.CropSquare
import androidx.compose.material.icons.filled.HorizontalRule
import androidx.compose.material.icons.filled.Settings
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.ApplicationScope
import androidx.compose.ui.window.FrameWindowScope
import androidx.compose.ui.window.WindowPlacement
import androidx.compose.ui.window.WindowState
import component.common.SmallIconButton

context (FrameWindowScope, ApplicationScope)
@Composable
fun WindowTitle(
    windowState: WindowState
) {
    val interactionSource = remember { MutableInteractionSource() }

    Row(modifier = Modifier.fillMaxWidth().height(32.dp).background(Color(0x28, 0x28, 0x28))) {
        Column(verticalArrangement = Arrangement.Center, modifier = Modifier.fillMaxSize()) {
            Row(horizontalArrangement = Arrangement.SpaceAround, modifier = Modifier.fillMaxSize()) {
                Row(horizontalArrangement = Arrangement.Start, modifier = Modifier.weight(1f)) {
                    WindowDraggableArea(
                        modifier = Modifier.fillMaxSize()
                            .pointerInput(interactionSource) {
                                detectTapGestures(
                                    onDoubleTap = {
                                        windowState.placement =
                                            if (windowState.placement == WindowPlacement.Maximized)
                                                WindowPlacement.Floating else WindowPlacement.Maximized
                                    }
                                )
                            },
                    ) {
                        Text(
                            "FRP Client GUI",
                            fontSize = 13.sp,
                            modifier = Modifier.padding(start = 12.dp).offset(y = 1.dp)
                        )
                    }
                }

                Row(
                    horizontalArrangement = Arrangement.Start,
                    modifier = Modifier.wrapContentWidth().fillMaxHeight()
                ) {
                    Column(
                        verticalArrangement = Arrangement.Center,
                        modifier = Modifier.fillMaxHeight()
                    ) {
                        SmallIconButton(
                            onClick = {
//                                                windowState.isMinimized = true
                            },
                            modifier = Modifier.size(18.dp).offset(y = (-1).dp),
//                                        colors = ButtonDefaults.textButtonColors(
//                                            contentColor = MaterialTheme.colors.onSurface,
//                                        ),
//                                        shape = RectangleShape
                        ) {
                            Icon(Icons.Filled.Settings, "Settings", tint = Color(0x75, 0x75, 0x75))
                        }
                    }

                    Box(modifier = Modifier.padding(18.dp, 4.dp, 8.dp, 4.dp)) {
                        Divider(
                            modifier = Modifier
                                .fillMaxHeight()
                                .width(1.dp)
                        )
                    }

                    TextButton(
                        onClick = {
                            windowState.isMinimized = true
                        },
                        modifier = Modifier.width(47.dp),
                        colors = ButtonDefaults.textButtonColors(
                            contentColor = MaterialTheme.colors.onSurface,
                        ),
                        shape = RectangleShape
                    ) {
                        Icon(Icons.Filled.HorizontalRule, "Minimize")
                    }

                    TextButton(
                        onClick = {
                            windowState.placement =
                                if (windowState.placement == WindowPlacement.Maximized)
                                    WindowPlacement.Floating else WindowPlacement.Maximized
                        },
                        modifier = Modifier.width(47.dp),
                        colors = ButtonDefaults.textButtonColors(
                            contentColor = MaterialTheme.colors.onSurface,
                        ),
                        shape = RectangleShape
                    ) {
                        Icon(Icons.Filled.CropSquare, "Maximize", modifier = Modifier.size(13.dp))
                    }

                    TextButton(
                        onClick = ::exitApplication,
                        modifier = Modifier.width(47.dp),
                        colors = ButtonDefaults.textButtonColors(
                            contentColor = MaterialTheme.colors.onSurface,
                        ),
                        shape = RectangleShape
                    ) {
                        Icon(Icons.Filled.Close, "Close")
                    }
                }
            }
        }
    }
}