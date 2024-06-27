import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.TooltipArea
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.window.WindowDraggableArea
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.OpenInNew
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.*
import model.ExposablePort
import model.RuntimeExposablePort
import java.awt.Desktop
import java.net.URI

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterialApi::class)
@Composable
@Preview
fun App() {
    val exposablePorts = remember {
        mutableStateListOf<RuntimeExposablePort>(
            RuntimeExposablePort(
                ExposablePort(
                    protocol = "HTTP",
                    localAddress = "127.0.0.1",
                    localPort = 8521,
                    subdomain = "myfirstapp",
                    name = "http-myfirstapp"
                )
            ), RuntimeExposablePort(
                ExposablePort(
                    protocol = "HTTP",
                    localAddress = "192.168.0.125",
                    localPort = 80,
                    subdomain = "backend",
                    name = "My Backend",
                    isCustomName = true
                )
            ), RuntimeExposablePort(
                ExposablePort(
                    protocol = "TCP",
                    localAddress = "127.0.0.1",
                    localPort = 9189,
                    subdomain = "somethingidk",
                    name = "tcp-somethingidk"
                )
            )
        )
    }

    Row(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxHeight().widthIn(max = 350.dp).padding(8.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            exposablePorts.forEachIndexed { index, exposablePort ->
                Card(modifier = Modifier.fillMaxWidth(), onClick = {

                }) {
                    Row(Modifier.fillMaxWidth()) {
                        Column(Modifier.padding(12.dp, 8.dp).weight(1f)) {
                            Row {
                                Text(exposablePort.app.protocol, style = MaterialTheme.typography.h6, color = MaterialTheme.colors.primary, modifier = Modifier.padding(start = 6.dp, top = 4.dp, end = 5.dp, bottom = 4.dp))
                                Text(exposablePort.app.formatShortLocalSocket() ?: "", style = MaterialTheme.typography.h6, color = MaterialTheme.colors.primaryVariant, modifier = Modifier.padding(start = 5.dp, top = 4.dp, end = 6.dp, bottom = 4.dp))
                            }
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                TooltipArea(
                                    tooltip = {
                                        Surface(
                                            modifier = Modifier.shadow(4.dp),
                                            shape = RoundedCornerShape(4.dp)
                                        ) {
                                            Text(
                                                text = exposablePort.app.getFullDomain("tun.kiriru.su") ?: "",
                                                modifier = Modifier.padding(10.dp)
                                            )
                                        }
                                    }
                                ) {
                                    if (exposablePort.isRunning)
                                        Text(exposablePort.app.name, modifier = Modifier.padding(start = 6.dp, top = 4.dp, end = 10.dp, bottom = 4.dp), color = Color(0x43, 0xa0, 0x47))
                                    else
                                        Text(exposablePort.app.name, modifier = Modifier.padding(start = 6.dp, top = 4.dp, end = 10.dp, bottom = 4.dp))
                                }
                                IconButton(
                                    onClick = {
                                        Desktop.getDesktop().browse(URI(exposablePort.app.getFullUrl("tun.kiriru.su", "https")))
                                    },
                                    modifier = Modifier.size(18.dp),
                                    enabled = exposablePort.isRunning
                                ) {
                                    if (exposablePort.isRunning)
                                        Icon(Icons.AutoMirrored.Filled.OpenInNew, "Open in browser", tint = Color(0x43, 0xa0, 0x47))
                                    else
                                        Icon(Icons.AutoMirrored.Filled.OpenInNew, "Open in browser")
                                }
                            }
                        }
                        Column(Modifier.padding(end = 2.dp, top = 2.dp)) {
                            IconButton(onClick = {
                                exposablePorts[index] = exposablePort.copy(isRunning = !exposablePort.isRunning)
                            }) {
                                if (exposablePort.isRunning) {
                                    Icon(Icons.Filled.Stop, "Stop")
                                } else {
                                    Icon(Icons.Filled.PlayArrow, "Start")
                                }
                            }
                        }
                    }
                }
            }
        }

        Box(modifier = Modifier.fillMaxHeight().padding(vertical = 8.dp)) {
            Divider(
                modifier = Modifier
                    .fillMaxHeight()
                    .width(1.dp)
            )
        }

        Column(modifier = Modifier.fillMaxHeight().weight(0.6f)) {

        }
    }

//    var text by remember { mutableStateOf("Hello, World!") }
//
//    Button(onClick = {
//        text = "Hello, Desktop!"
//    }) {
//        Text(text)
//    }
}

fun main() = application {
    val windowState = rememberWindowState(
        position = WindowPosition(Alignment.Center)
    )

    val interactionSource = remember { MutableInteractionSource() }

    Window(
        onCloseRequest = ::exitApplication,
        undecorated = true,
        state = windowState
    ) {

        MaterialTheme(
            colors = darkColors(
                primary = Color(0x21, 0x96, 0xf3),
                primaryVariant = Color(0x19, 0x76, 0xd2),
                secondary = Color(0x00, 0xbc, 0xd4)
            )
        ) {
            Scaffold {
                Column(
                    modifier = Modifier.fillMaxSize(),
                ) {
                    Row(modifier = Modifier.fillMaxWidth().height(32.dp).background(Color(0x28, 0x28, 0x28))) {
                        Column(verticalArrangement = Arrangement.Center, modifier = Modifier.fillMaxSize()) {
                            Row(horizontalArrangement = Arrangement.SpaceAround, modifier = Modifier.fillMaxSize()) {
                                Row(horizontalArrangement = Arrangement.Start, modifier = Modifier.weight(1f)) {
                                    WindowDraggableArea(
                                        modifier = Modifier.fillMaxSize()
                                            .pointerInput(interactionSource) {
                                                detectTapGestures(
                                                    onDoubleTap = {
                                                        windowState.placement = if (windowState.placement == WindowPlacement.Maximized)
                                                            WindowPlacement.Floating else WindowPlacement.Maximized
                                                    }
                                                )
                                            },
                                    ) {
                                        Text("FRP Client GUI", fontSize = 13.sp, modifier = Modifier.padding(start = 12.dp).offset(y = 1.dp))
                                    }
                                }

                                Row(horizontalArrangement = Arrangement.Start, modifier = Modifier.wrapContentWidth().fillMaxHeight()) {
                                    Column(verticalArrangement = Arrangement.Center, modifier = Modifier.fillMaxHeight()) {
                                        IconButton(
                                            onClick = {
                                                windowState.isMinimized = true
                                            },
                                            modifier = Modifier.size(18.dp).offset(y = (-2).dp),
//                                        colors = ButtonDefaults.textButtonColors(
//                                            contentColor = MaterialTheme.colors.onSurface,
//                                        ),
//                                        shape = RectangleShape
                                        ) {
                                            Icon(Icons.Filled.Settings, "Settings", tint = Color(0x75, 0x75, 0x75))
                                        }
                                    }

                                    Box(modifier = Modifier.padding(24.dp, 4.dp, 8.dp, 4.dp)) {
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
                                            windowState.placement = if (windowState.placement == WindowPlacement.Maximized)
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

                    Column(
                        modifier = Modifier.fillMaxSize()
                    ) {
                        App()
                    }
                }
            }
        }
    }
}
