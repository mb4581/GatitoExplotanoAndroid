package pw.mmk.GattoExplotano

import android.net.Uri
import android.os.Handler
import android.os.Looper
import android.util.Log
import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.core.FileDataPart
import com.github.kittinunf.fuel.core.Method
import org.json.JSONObject
import java.io.File
import kotlin.io.path.outputStream

const val SERVER_URL = "http://gsapp.mm1n.ru"

class CloudVideoProcessing(private val context: Activity) : IVideoProcessing {
    private val resultVideoSaver = FileSaver(context, "video.mp4")

    var callback: (it: Float?) -> Any = {}

    override fun progressCallback(content: (it: Float?) -> Unit) {
        callback = content;
    }

    override fun start(baseFileUri: Uri, screenFileUri: Uri) {
        val miscParams = listOf("config" to "{\"color\": \"#00FF00\"}")

        val baseFileTmp = getFilePathFromContentUri(baseFileUri)
        val screenFileTmp = getFilePathFromContentUri(screenFileUri);

        callback(5f);
        Fuel.upload("$SERVER_URL/api/requests", Method.PUT, miscParams)
            .add { FileDataPart(baseFileTmp, name ="base_video") }
            .add { FileDataPart(screenFileTmp, name = "overlay_video") }
            .responseString {request, _, result -> run{
                val id = JSONObject(result.get()).getString("id");
                Log.d("MyApp", "Created id=$id, polling for result...")

                baseFileTmp.delete()
                screenFileTmp.delete()

                callback(25f)
                pollUntilComplete(id)
            }}
    }

    private fun pollUntilComplete(id: String) {
        val mainHandler = Handler(Looper.getMainLooper())

        mainHandler.post(object : Runnable {
            val runnable = this;
            override fun run() {
                Fuel.get("$SERVER_URL/api/requests/$id")
                    .responseString {_, _, result -> run{
                        val status = JSONObject(result.get()).getString("status");
                        Log.d("MyApp", "$id == $status")

                        if(status == "ready") {
                            callback(75f)
                            downloadAndSave(id);
                        } else {
                            mainHandler.postDelayed(runnable, 3000)
                        }
                    }}
            }
        })
    }

    private fun downloadAndSave(id: String) {
        Fuel.get("$SERVER_URL/api/requests/$id/output.mp4")
            .response { _, _, result -> run{
                resultVideoSaver.save(result.get())
                callback(null)
            }}
    }

    private fun getFilePathFromContentUri(uri: Uri): File {
        val file = kotlin.io.path.createTempFile()
        uri.let { context.contentResolver.openInputStream(uri) }.use { input ->
            file.outputStream().use { output ->
                input?.copyTo(output)
            }
        }

        return file.toFile();
    }
}