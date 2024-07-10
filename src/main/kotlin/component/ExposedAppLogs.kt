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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import model.RuntimeExposablePort

@OptIn(ExperimentalTextApi::class)
@Composable
fun ExposedAppLogs(app: RuntimeExposablePort) {
    val bufferState by app.logs.state.collectAsState()

    Box(modifier = Modifier.fillMaxSize().background(Color(0x17, 0x17, 0x17))) {
        BasicTextField(
            value = bufferState,
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