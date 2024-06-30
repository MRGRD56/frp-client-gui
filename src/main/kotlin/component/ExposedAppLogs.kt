package component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalTextApi::class)
@Composable
fun ExposedAppLogs() {
    Box(modifier = Modifier.fillMaxSize().background(Color(0x17, 0x17, 0x17))) {
        BasicTextField(
            value = """
                            2024/06/28 01:00:53 [I] [root.go:139] start frpc service for config file [C:\_public\frp_0.52.3_windows_amd64\frpc.toml]
                            2024/06/28 01:00:53 [I] [service.go:299] [d1eb514d62903016] login to server success, get run id [d1eb514d62903016]
                            2024/06/28 01:00:53 [I] [proxy_manager.go:156] [d1eb514d62903016] proxy added: [http-broadcastbot]
                            2024/06/28 01:00:53 [I] [control.go:173] [d1eb514d62903016] [http-broadcastbot] start proxy success
                        """.trimIndent(),
            onValueChange = {},
            readOnly = true,
            textStyle = TextStyle(
                fontFamily = FontFamily("JetBrains Mono"),
                fontSize = 11.sp,
                color = MaterialTheme.colors.onBackground,
            ),
//                colors = TextFieldDefaults.textFieldColors(
//                    backgroundColor = Color(0x17, 0x17, 0x17),
//                    focusedIndicatorColor = Color.Transparent,
//                    unfocusedIndicatorColor = Color.Transparent,
//                    disabledIndicatorColor = Color.Transparent
//                ),
            modifier = Modifier
                .fillMaxSize(), //TODO fix the paddings
            decorationBox = { innerTextField ->
                Box(modifier = Modifier.fillMaxSize().padding(horizontal = 10.dp, vertical = 4.dp)) {
                    innerTextField()
                }
//                            VerticalScrollbar(adapter = rememberScrollbarAdapter(rememberScrollState(0)))
            }
//                shape = MaterialTheme.shapes.small,
        )
//                    VerticalScrollbar(adapter = rememberScrollbarAdapter(rememberScrollState(0)))
    }
}