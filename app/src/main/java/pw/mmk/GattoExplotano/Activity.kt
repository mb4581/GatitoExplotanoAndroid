package pw.mmk.GattoExplotano

import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier

class Activity : ComponentActivity() {
    private val videoUnit = CloudVideoProcessing(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent { MyApplication( unit = videoUnit ) }
    }
}

@Composable
fun MyApplication(unit: IVideoProcessing) {
    var status by rememberSaveable { mutableStateOf<Float?>(null) }

    unit.progressCallback {
        status = it
    }

    GattoExplotanoAppTheme {
        Scaffold { innerPadding -> run{
            Column(modifier = Modifier.padding(innerPadding)) {
                BaseLayout(
                    onProcess = {base: Uri, screen: Uri -> run{
                        unit.start(base, screen)
                    }},
                    status = status,
                )
            }
        } }
    }
}
