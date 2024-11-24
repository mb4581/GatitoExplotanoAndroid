package pw.mmk.GattoExplotano

import android.net.Uri

interface IVideoProcessing {
    fun progressCallback(content: (it: Float?) -> Unit)
    fun start(baseFileUri: Uri, screenFileUri: Uri)
}
