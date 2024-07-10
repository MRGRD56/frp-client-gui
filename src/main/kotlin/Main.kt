import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.OpenInNew
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Stop
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.layout
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import component.ExposedAppLogs
import component.window.WindowTitle
import entity.ExposableAppEntity
import frp.Frp
import model.ExposableApp
import model.RuntimeAppStatus
import model.RuntimeExposablePort
import modifier.border
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.transactions.transaction
import table.ExposableApps
import java.awt.Desktop
import java.net.URI

fun handleAppChange(app: ExposableApp) {
    transaction {
        ExposableAppEntity.findByIdAndUpdate(app.id, ExposableAppEntity.fromDto(app))
    }
}

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterialApi::class, ExperimentalTextApi::class)
@Composable
@Preview
fun App() {
    val exposablePorts = remember {
        mutableStateListOf<RuntimeExposablePort>()
    }

    var selectedExposablePort by remember { mutableStateOf<RuntimeExposablePort?>(null) }

    var isLoaded by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        Database.connect("jdbc:sqlite:frpc_gui.db", driver = "org.sqlite.JDBC")

        transaction {
            SchemaUtils.create(
                ExposableApps
            )

            ExposableAppEntity.all().forEach {
                val app = ExposableApp.fromEntity(it)
                app.observeChanges(::handleAppChange)
                exposablePorts.add(
                    RuntimeExposablePort(app)
                )
            }

            isLoaded = true
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            exposablePorts.forEach {
                if (it.isRunning) it.stop()
            }
        }
    }

//    val exposablePorts = remember {
//        mutableStateListOf<RuntimeExposablePort>(
//            RuntimeExposablePort(
//                ExposableApp(
//                    protocol = "HTTP",
//                    localAddress = "127.0.0.1",
//                    localPort = 8521u,
//                    subdomain = "my-first-app",
//                    name = "My First App"
//                )
//            ), RuntimeExposablePort(
//                ExposableApp(
//                    protocol = "HTTP",
//                    localAddress = "192.168.0.125",
//                    localPort = 80u,
//                    subdomain = "backend",
//                    name = "My Backend",
//                    isCustomName = true
//                )
//            ), RuntimeExposablePort(
//                ExposableApp(
//                    protocol = "TCP",
//                    localAddress = "127.0.0.1",
//                    localPort = 9189u,
//                    subdomain = "something-idk",
//                    name = "Something Idk"
//                )
//            )
//        )
//    }

    if (!isLoaded) {
        return
    }

    Row(
        modifier = Modifier.fillMaxSize()
            .border(color = Color(0x28, 0x28, 0x28), start = 2.dp, bottom = 2.dp, end = 2.dp)
    ) {
        var appToDelete by remember { mutableStateOf<RuntimeExposablePort?>(null) }

        appToDelete?.let { app ->
            AlertDialog(
                title = {
                    Text("Delete app")
                },
                text = {
                    Text("Are you sure you want to delete the app \"${app.app.name.ifEmpty { "<unnamed>" }}\"?")
                },
                onDismissRequest = {
                    appToDelete = null
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            if (app.isRunning) {
                                app.stop()
                            }

                            transaction {
                                ExposableApps.deleteWhere { this.id eq app.app.id }
                            }

                            exposablePorts.remove(app)
                            selectedExposablePort = null

                            appToDelete = null
                        },
                        colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colors.error)
                    ) {
                        Text("Delete")
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = {
                            appToDelete = null
                        }
                    ) {
                        Text("Dismiss")
                    }
                }
            )
        }

        Box(
            modifier = Modifier
                .fillMaxHeight()
                .run {
                    selectedExposablePort ?: return@run this

                    widthIn(max = 350.dp)
                }
        ) {
            val leftScroll = rememberScrollState()

            Column(
                modifier = Modifier
                    .fillMaxHeight()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxHeight()
                        .verticalScroll(leftScroll)
                        .weight(weight = 1f, fill = false)
                        .padding(8.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    exposablePorts.forEachIndexed { index, exposablePort ->
                        val isSelected = selectedExposablePort == exposablePort

                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            onClick = {
                                selectedExposablePort = if (isSelected) null else exposablePort
                            },
                            backgroundColor = if (isSelected) Color(0x27, 0x27, 0x27) else MaterialTheme.colors.surface
                        ) {
                            val statusColor: Color? = when (exposablePort.status) {
                                RuntimeAppStatus.STOPPED -> null
                                RuntimeAppStatus.STARTING -> Color(0xfd, 0xd8, 0x35)
                                RuntimeAppStatus.SUCCESS -> Color(0x43, 0xa0, 0x47)
                                RuntimeAppStatus.FAILED -> Color(0xe5, 0x39, 0x35)
                            }

                            Row(Modifier.fillMaxWidth()) {
                                Column(Modifier.padding(12.dp, 8.dp).weight(1f)) {
                                    Row {
                                        Text(
                                            exposablePort.app.protocol,
                                            style = MaterialTheme.typography.h6,
                                            color = MaterialTheme.colors.primary,
                                            modifier = Modifier.padding(
                                                start = 6.dp,
                                                top = 4.dp,
                                                end = 5.dp,
                                                bottom = 4.dp
                                            )
                                        )
                                        Text(
                                            exposablePort.app.formatShortLocalSocket() ?: "",
                                            style = MaterialTheme.typography.h6,
                                            color = MaterialTheme.colors.primaryVariant,
                                            modifier = Modifier.padding(
                                                start = 5.dp,
                                                top = 4.dp,
                                                end = 6.dp,
                                                bottom = 4.dp
                                            )
                                        )
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
                                            if (statusColor != null)
                                                Text(
                                                    exposablePort.app.name,
                                                    modifier = Modifier.padding(
                                                        start = 6.dp,
                                                        top = 4.dp,
                                                        end = 10.dp,
                                                        bottom = 4.dp
                                                    ),
                                                    color = statusColor
                                                )
                                            else
                                                Text(
                                                    exposablePort.app.name,
                                                    modifier = Modifier.padding(
                                                        start = 6.dp,
                                                        top = 4.dp,
                                                        end = 10.dp,
                                                        bottom = 4.dp
                                                    )
                                                )
                                        }
                                        IconButton(
                                            onClick = {
                                                Desktop.getDesktop()
                                                    .browse(URI(exposablePort.app.getFullUrl("tun.kiriru.su", "https")))
                                            },
                                            modifier = Modifier.size(18.dp),
                                            enabled = exposablePort.isRunning
                                        ) {
                                            if (statusColor != null)
                                                Icon(
                                                    Icons.AutoMirrored.Filled.OpenInNew,
                                                    "Open in browser",
                                                    tint = statusColor
                                                )
                                            else
                                                Icon(Icons.AutoMirrored.Filled.OpenInNew, "Open in browser")
                                        }
                                    }
                                }
                                Column(Modifier.padding(end = 2.dp, top = 2.dp)) {
                                    IconButton(onClick = {
                                        if (exposablePort.isRunning) {
                                            exposablePort.stop()
                                        } else {
                                            exposablePort.start()
                                        }
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
            }

            VerticalScrollbar(
                modifier = Modifier.align(Alignment.CenterEnd)
                    .fillMaxHeight(),
                adapter = rememberScrollbarAdapter(leftScroll)
            )

            ExtendedFloatingActionButton(
                modifier = Modifier.layout { measurable, constraints ->
                    val placeable = measurable.measure(constraints)
                    layout(placeable.width, placeable.height) {
                        placeable.placeRelative(
                            IntOffset(
                                constraints.maxWidth - placeable.width - 24,
                                constraints.maxHeight - placeable.height - 24
                            )
                        )
                    }
                },
                onClick = {
                    val newApp = ExposableApp()

                    transaction {
                        ExposableAppEntity.new(ExposableAppEntity.fromDto(newApp))
                    }

                    newApp.observeChanges(::handleAppChange)

                    val newRuntimeApp = RuntimeExposablePort(newApp)
                    exposablePorts.add(newRuntimeApp)
                    selectedExposablePort = newRuntimeApp
                },
                icon = { Icon(Icons.Filled.Add, "Add a new app") },
                text = { Text(text = "Add new app") },
            )
        }

        selectedExposablePort?.let { currentApp ->

            Box(modifier = Modifier.fillMaxHeight().padding(vertical = 8.dp)) {
                Divider(
                    modifier = Modifier
                        .fillMaxHeight()
                        .width(1.dp)
                )
            }

            Column(modifier = Modifier.fillMaxHeight().weight(0.6f).padding(8.dp)) {
                Row(modifier = Modifier.weight(0.7f)) {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Row {
                            TextField(
                                value = currentApp.app.name,
                                onValueChange = {
                                    currentApp.app.isCustomName = true
                                    currentApp.app.name = it
                                },
                                label = { Text("Name") }
                            )
                        }
                        Row {
                            var expanded by remember { mutableStateOf(false) }

                            ExposedDropdownMenuBox(
                                expanded = expanded,
                                onExpandedChange = {
                                    expanded = !expanded
                                }
                            ) {
                                TextField(
                                    readOnly = true,
                                    value = currentApp.app.protocol,
                                    onValueChange = { },
                                    label = { Text("Protocol") },
                                    trailingIcon = {
                                        ExposedDropdownMenuDefaults.TrailingIcon(
                                            expanded = expanded
                                        )
                                    },
                                    colors = ExposedDropdownMenuDefaults.textFieldColors()
                                )
                                ExposedDropdownMenu(
                                    expanded = expanded,
                                    onDismissRequest = {
                                        expanded = false
                                    }
                                ) {
                                    Frp.availableProtocols.forEach { selectionOption ->
                                        DropdownMenuItem(
                                            onClick = {
                                                currentApp.app.protocol = selectionOption
                                                expanded = false
                                            }
                                        ) {
                                            Text(text = selectionOption)
                                        }
                                    }
                                }
                            }
                        }
                        Row {
                            TextField(
                                keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Decimal),
                                value = currentApp.app.localAddress,
                                onValueChange = { currentApp.app.localAddress = it },
                                label = { Text("Local Address") }
                            )
                        }
                        Row {
                            TextField(
                                keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                                value = currentApp.app.localPort?.toString() ?: "",
                                onValueChange = {
                                    if (it.isBlank()) {
                                        currentApp.app.localPort = null
                                        return@TextField
                                    }

                                    currentApp.app.localPort = it.toUShortOrNull() ?: currentApp.app.localPort
                                },
                                label = { Text("Local Port") }
                            )
                        }
                        Row {
                            TextField(
                                keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Decimal),
                                value = currentApp.app.subdomain,
                                onValueChange = {
                                    currentApp.app.subdomain = it
                                    if (!currentApp.app.isCustomName) {
                                        currentApp.app.name = currentApp.app.getAutogeneratedName()
                                    }
                                },
                                label = { Text("Subdomain") }
                            )
                        }
                        Row(
                            modifier = Modifier.padding(top = 8.dp)
                        ) {
                            Button(
                                onClick = {
                                    appToDelete = currentApp
                                },
                                colors = ButtonDefaults.buttonColors(
                                    contentColor = MaterialTheme.colors.onError,
                                    backgroundColor = MaterialTheme.colors.error
                                )
                            ) {
                                Text("Delete")
                            }
                        }
                    }
                }

                if (currentApp.isRunning) {
                    Box(modifier = Modifier.fillMaxWidth()) {
                        Divider(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(1.dp)
                        )
                    }

                    Row(
                        modifier = Modifier.weight(0.3f).padding(top = 8.dp)
                            .border(1.dp, Color(0x29, 0x29, 0x29), shape = MaterialTheme.shapes.small)
                    ) {
                        ExposedAppLogs(currentApp)
                    }
                }
            }
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
        position = WindowPosition(Alignment.Center),
        width = 1000.dp,
        height = 700.dp
    )

    Window(
        onCloseRequest = ::exitApplication,
        undecorated = true,
        state = windowState,
        title = "FRP Client GUI",
        icon = painterResource("frpc-gui.ico")
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
                    WindowTitle(windowState)

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
