package pw.mmk.GattoExplotano

import androidx.activity.result.contract.ActivityResultContracts
import java.io.FileOutputStream

class FileSaver(context: Activity, private val suggestedName: String) {
    private var dataToSave: ByteArray? = null;
    private val saveFileRequest = context.registerForActivityResult(
        ActivityResultContracts.CreateDocument("video/*")
    ) {
        if(it != null) {
            context.contentResolver.openFileDescriptor(it, "w")?.use {
                FileOutputStream(it.fileDescriptor).use {
                    it.write(dataToSave);
                }
            }
        }
    }

    fun save(data: ByteArray) {
        dataToSave = data;
        saveFileRequest.launch(suggestedName)
    }
}