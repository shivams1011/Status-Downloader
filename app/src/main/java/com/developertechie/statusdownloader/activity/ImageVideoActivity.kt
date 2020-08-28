package com.developertechie.statusdownloader.activity

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.MediaController
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import com.bumptech.glide.Glide
import com.developertechie.statusdownloader.R
import com.developertechie.statusdownloader.adapter.StatusAdapter.Companion.MP4
import com.developertechie.statusdownloader.model.FileDetail
import com.developertechie.statusdownloader.utils.Utils

import kotlinx.android.synthetic.main.activity_image_video.*
import kotlinx.android.synthetic.main.content_image_video.*
import java.io.File

class ImageVideoActivity : AppCompatActivity() {

    private var file: File? = null
    private var fileType: String? = null

    companion object{
        private val ARG_FILE_URI: String? = "arg_file_uri"
        private val ARG_FILE_TYPE: String? = "arg_file_type"

        fun newInstance(x: FileDetail, context: Context): Unit {
            val intent = Intent(context, ImageVideoActivity::class.java)
            intent.putExtra(ARG_FILE_TYPE, x.type)
            intent.putExtra(ARG_FILE_URI, x.fileUri)
            context.startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_image_video)
        setSupportActionBar(toolbar)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        extractData()
        setImageOrVideoAccToUri()
    }

    private fun setImageOrVideoAccToUri() {
        if (fileType.equals(MP4)) {
            setAndPlayVideo()
        } else {
            setImage()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {

        when (item?.itemId) {
            R.id.action_share -> {
                if (fileType.equals(MP4)){
                    file?.let { shareImageVideo("video/mp4",getString(R.string.share_video), it) }
                } else{
                    file?.let { shareImageVideo("image/jpeg",getString(R.string.share_image), it) }
                }
            }
            R.id.action_save -> {
                file?.let { Utils.saveFile(it) }
                showMessage(getString(R.string.saved))
            }
            android.R.id.home -> finish()

        }
        return super.onOptionsItemSelected(item);
    }

    private fun showMessage(msg: String) {
        Toast.makeText(
            this,
            msg,
            Toast.LENGTH_SHORT
        ).show()
    }

    private fun setImage() {
        imgMainActivity.visibility = View.VISIBLE
        videoMain.visibility = View.GONE
        Glide.with(applicationContext).load(file!!.toUri()).into(imgMainActivity)
    }

    private fun setAndPlayVideo() {
        imgMainActivity.visibility = View.GONE
        videoMain.visibility = View.VISIBLE
        val mediaController = MediaController(this)
        mediaController.setAnchorView(videoMain)
        videoMain.setMediaController(mediaController)
        videoMain.setVideoURI(file!!.toUri())
        videoMain.start()
    }

    private fun extractData() {
        fileType = intent.getStringExtra(ARG_FILE_TYPE)
        file = intent.getSerializableExtra(ARG_FILE_URI) as File?
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    private fun shareImageVideo(intentType: String, shareType: String, fileUri: File) {
        val intent = Intent(Intent.ACTION_SEND)
        intent.setType(intentType)
        intent.putExtra(Intent.EXTRA_STREAM, Uri.parse(fileUri.absolutePath))
        startActivity(
            Intent.createChooser(
                intent,
                shareType
            )
        )
    }

}
