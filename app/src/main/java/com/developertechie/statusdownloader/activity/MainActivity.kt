package com.developertechie.statusdownloader.activity

import android.Manifest
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.developertechie.statusdownloader.R
import com.developertechie.statusdownloader.fragment.StatusFragment
import com.developertechie.statusdownloader.manager.TabFragmentManager
import com.developertechie.statusdownloader.utils.Utils.Companion.hasPermission
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.tabs.TabLayout
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*

class MainActivity : AppCompatActivity() {

    val PERSMISSION = arrayOf(
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.WRITE_EXTERNAL_STORAGE
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        accessPermission()
    }

    private fun accessPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkWriteExternalPersmisison()) {
                loadFragment()
            } else {
                showAlertForPermission()
            }
        } else {
            loadFragment()
        }
    }

    private fun showAlertForPermission() {
        val builder: AlertDialog.Builder = AlertDialog.Builder(this)
            .setMessage("Status Downloader needs storage permission to work properly")
            .setTitle("Allow Access")
            .setPositiveButton("Allow") { dialog, which ->
                if (!hasPermission(this, PERSMISSION)) {
                    ActivityCompat.requestPermissions(this, PERSMISSION, 1)
                }
                dialog.cancel()
            }
        builder.create().show()
    }

    private fun checkWriteExternalPersmisison(): Boolean {
        val permission = Manifest.permission.READ_EXTERNAL_STORAGE
        return checkCallingOrSelfPermission(permission) == PackageManager.PERMISSION_GRANTED
    }

    private fun loadFragment() {
        val manager =  TabFragmentManager(supportFragmentManager)
        pager.adapter = manager

        tabLayout.setupWithViewPager(pager)

    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (permissions.size == 0) {
            return
        }

        for (permission in permissions) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, permission)) {
                //denied
                Log.e("denied", permission)
                Toast.makeText(
                    this,
                    "Permission denied to read External Storage",
                    Toast.LENGTH_SHORT
                ).show()
                finish()
            } else {
                if (ActivityCompat.checkSelfPermission(
                        this,
                        permission
                    ) == PackageManager.PERMISSION_GRANTED
                ) {
                    //allowed
                    Log.e("allowed", permission)
                    loadFragment()
                } else {
                    //set to never ask again
                    Log.e("set to never ask again", permission)
                }
            }
        }

    }
}
