package pw.mmk.GattoExplotano

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun MyCard(modifier: Modifier = Modifier, content: @Composable() (ColumnScope.() -> Unit)) {
    Card(
        modifier = modifier,
    ) {
        Column(modifier = modifier.padding(16.dp)) {
            content()
        }
    }
}

@Composable
fun VideoUploadCard(
    name: String,
    modifier: Modifier = Modifier,
    selected: Boolean = false,
    onClick: () -> Any,
) {
    MyCard(modifier = modifier) {
        Text(
            name,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
        )
        Spacer(Modifier.size(12.dp))

        if (selected) {
            FilledTonalButton(onClick = { onClick() }) {
                Text(stringResource(R.string.act_change_file))
            }
        } else {
            Button(onClick = { onClick() }) {
                Text(stringResource(R.string.act_select_file))
            }
        }
    }
}

@Composable
fun VideoUploaderRow(
    modifier: Modifier = Modifier,
    onChange: (baseVideoUri: Uri?, screenVideoUri: Uri?) -> Any,
    baseSelected: Boolean = false,
    screenSelected: Boolean = false,
) {
    val baseVideoPickerLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) {
            onChange(it, null)
        }
    val screenVideoPickerLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) {
            onChange(null, it)
        }

    Row(modifier = Modifier.height(160.dp)) {
        VideoUploadCard(
            modifier = modifier.weight(1f),
            name = stringResource(R.string.act_upload_base_video),
            selected = baseSelected,
            onClick = { baseVideoPickerLauncher.launch(arrayOf("video/*")) }
        )
        Spacer(Modifier.size(16.dp))
        VideoUploadCard(
            modifier = modifier.weight(1f),
            name = stringResource(R.string.act_upload_screen_video),
            selected = screenSelected,
            onClick = { screenVideoPickerLauncher.launch(arrayOf("video/*")) }
        )
    }
}

@Composable
fun BaseLayout(
    onProcess: (baseVideo: Uri, screen: Uri) -> Any,
    status: Float?
) {
    var baseVideoUri by rememberSaveable { mutableStateOf<Uri?>(null) }
    var screenVideoUri by rememberSaveable { mutableStateOf<Uri?>(null) }

    Column(modifier = Modifier.padding(16.dp)) {
        Image(
            painter = painterResource(R.drawable.logo),
            contentDescription = "",
            contentScale = ContentScale.Fit,
            modifier = Modifier
                .width(160.dp)
                .align(Alignment.CenterHorizontally)
        )
        Spacer(Modifier.size(32.dp))
        Text(
            text = stringResource(R.string.app_title),
            color = MaterialTheme.colorScheme.primary,
            fontSize = 32.sp,
            fontWeight = FontWeight.Light,
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
        )
        Spacer(Modifier.size(32.dp))
        VideoUploaderRow(
            baseSelected = baseVideoUri != null,
            screenSelected = screenVideoUri != null,
            onChange = { base: Uri?, screen: Uri? -> run{
                if (base != null) {
                    baseVideoUri = base;
                }

                if (screen != null) {
                    screenVideoUri = screen;
                }
            }}
        )
        Spacer(Modifier.size(32.dp))
        Text(
            stringResource(R.string.app_process_info),
            modifier = Modifier.align(Alignment.CenterHorizontally),
            color = MaterialTheme.colorScheme.outline,
        )
        Spacer(Modifier.size(16.dp))
        Button(
            modifier = Modifier.align(Alignment.CenterHorizontally),
            onClick = {
                if(baseVideoUri != null && screenVideoUri != null) {
                    onProcess(baseVideoUri!!, screenVideoUri!!)
                }
            },
            enabled = status == null
                    && baseVideoUri != null
                    && screenVideoUri != null,
        ) {
            Text(stringResource(R.string.act_process))
        }

        if (status != null) {
            Spacer(modifier = Modifier.size(16.dp))
            LinearProgressIndicator(
                progress = { status / 100 },
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MyPreview() {
    GattoExplotanoAppTheme {
        BaseLayout(
            onProcess = { uri: Uri, uri1: Uri -> },
            status = 2.0f
        )
    }
}