package com.developertechie.statusdownloader.utils

import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Environment
import androidx.core.app.ActivityCompat
import com.developertechie.statusdownloader.model.FileDetail
import java.io.*
import java.util.*

/**
 * Created by ShivamSharma on 2019-09-15.
 */
class Utils {
    companion object {
        private val extensions = arrayOf("png", "jpg", "mp4", "jpeg")

        fun hasPermission(context: Context, vararg permissions: Array<out String>): Boolean {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                for (permission in permissions) {
                    if (ActivityCompat.checkSelfPermission(
                            context,
                            permission.toString()
                        ) != PackageManager.PERMISSION_GRANTED
                    ) {
                        return false
                    }
                }
            }
            return true
        }

        fun loadmp3(YourFolderPath: String): List<FileDetail> {

            val fileList_ = ArrayList<FileDetail>()

            val file1 = File(YourFolderPath)

            val filesList = file1.listFiles()

            if (filesList != null && filesList.isNotEmpty()) {
                filesList.sortBy { it.lastModified() }
                for (f in filesList) {

                    val date = Date()
                    val calendar = Calendar.getInstance()
                    calendar.time = date
                    calendar.add(Calendar.DAY_OF_YEAR, -2)
                    val newDate = calendar.time

                    if (f.lastModified() > newDate.time) {

                        for (i in extensions.indices) {

                            if (f.absolutePath.endsWith(extensions[i])) {

                                val name = f.name
                                val extension = extensions[i]
                                val uri = Uri.parse(f.absolutePath)

                                fileList_.add(
                                    FileDetail(
                                        name,
                                        extension,
                                        File(uri.toString())
                                    )
                                )
                            }
                        }
                    }
                }
            }
            return fileList_
        }

        fun saveFile(fileUri: File) {
            var file = File(
                Environment.getExternalStorageDirectory(),
                Constants.StatusDownloadInfo.DIR_NAME
            )
            if (!file.exists()) {
                file.mkdirs()
            }
            file = File(""+file+"/"+ fileUri.name)
            copy(fileUri, file)
        }

        fun copy(src: File, dest: File) {
            val inputStream: InputStream = FileInputStream (src)
            val outputStream: OutputStream = FileOutputStream(dest)
            val buf = ByteArray(1024)
            var len = 0
            while (inputStream.read(buf).also { len = it } >=0){
                outputStream.write(buf, 0, len)
            }
            inputStream.close()
            outputStream.close()
        }

    }

}