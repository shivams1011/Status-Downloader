package com.developertechie.statusdownloader.adapter

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Environment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.developertechie.statusdownloader.R
import com.developertechie.statusdownloader.model.FileDetail
import com.developertechie.statusdownloader.utils.Constants
import com.developertechie.statusdownloader.utils.Utils.Companion.saveFile
import io.reactivex.subjects.PublishSubject
import kotlinx.android.synthetic.main.layout_status_item.view.*
import java.io.File

/**
 * Created by ShivamSharma on 2019-09-22.
 */
class StatusAdapter(var fileList: ArrayList<FileDetail>, var folderName: String) :
    RecyclerView.Adapter<StatusAdapter.ViewHolder>() {

    var contex: Context? = null
    val publishSubject = PublishSubject.create<FileDetail>()

    companion object {
        val MP4 = "mp4"
    }

    class ViewHolder(view: ViewDataBinding) : RecyclerView.ViewHolder(view.root) {
        fun bindData(
            file: FileDetail,
            publishSubject: PublishSubject<FileDetail>
        ) {
            setPlayIcon(file.type)
            setImage(file.fileUri)

            itemView.imageMain.setOnClickListener {
                publishSubject.onNext(file)
            }
        }

        private fun setImage(fileUri: File) {
            Glide.with(itemView.context).load(fileUri).into(itemView.imageMain)
        }

        private fun setPlayIcon(type: String) {
            if (MP4.equals(type)) {
                itemView.imgPlayIcon.visibility = View.VISIBLE
            } else {
                itemView.imgPlayIcon.visibility = View.GONE
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StatusAdapter.ViewHolder {
        contex = parent.context
        val binding = DataBindingUtil.inflate<ViewDataBinding>(
            LayoutInflater.from(parent.context),
            R.layout.layout_status_item,
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return fileList.size
    }

    override fun onBindViewHolder(holder: StatusAdapter.ViewHolder, position: Int) {
        holder.bindData(fileList.get(position), publishSubject)

        if (folderName.equals(Constants.StatusDownloadInfo.STATUS_FOLDER)) {
            holder.itemView.saveItem.txtSave.text = contex?.getString(R.string.save)
            holder.itemView.saveItem.imgSaveIcon.setImageResource(R.drawable.ic_save_sky_blue_24dp)
        } else {
            holder.itemView.saveItem.txtSave.text = contex?.getString(R.string.delete)
            holder.itemView.saveItem.imgSaveIcon.setImageResource(R.drawable.ic_delete_black_24dp)
        }

        holder.itemView.saveItem.setOnClickListener {
            if (folderName.equals(Constants.StatusDownloadInfo.STATUS_FOLDER)) {
                saveFile(fileList.get(position).fileUri)
                Toast.makeText(
                    holder.itemView.context,
                    contex?.getString(R.string.saved),
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                showAlert(fileList.get(position), position)
            }
        }

        holder.itemView.shareItem.setOnClickListener {
            if (fileList.get(position).type.equals(MP4)) {
                shareVideo(holder, fileList.get(position).fileUri)
            } else {
                shareImage(holder, fileList.get(position).fileUri)
            }
        }
    }

    private fun showAlert(
        fileDetail: FileDetail,
        position: Int
    ) {
        val alertDialog = contex?.let {
            AlertDialog.Builder(it).setMessage(contex?.getString(R.string.alert_item_delete_msg))
                .setPositiveButton(contex?.getString(R.string.yes)) { dialog, which ->
                    if (removeItem(fileDetail)) {
                        Toast.makeText(contex, contex?.getString(R.string.item_delete_successfull_msg), Toast.LENGTH_SHORT)
                            .show()
                        fileList.remove(fileDetail)
                        notifyDataSetChanged()
                    }
                    dialog.dismiss()
                }
                .setNegativeButton(contex?.getString(R.string.no)) { dialog, which ->
                    dialog.dismiss()
                }
        }

        alertDialog?.show()
    }

    public val getPublishSubject : PublishSubject<FileDetail> get() = publishSubject

    private fun removeItem(fileDetail: FileDetail): Boolean {
        val dir: File = getFileDirectory()
        val file = File(dir, fileDetail.fileName)
        return file.delete()
    }

    private fun getFileDirectory(): File {
        return File(
            Environment.getExternalStorageDirectory(),
            Constants.StatusDownloadInfo.DIR_NAME
        )
    }

    private fun shareImage(holder: ViewHolder, fileUri: File) {
        val intent = Intent(Intent.ACTION_SEND)
        intent.setType("image/jpeg")
        intent.putExtra(Intent.EXTRA_STREAM, Uri.parse(fileUri.absolutePath))
        holder.itemView.context.startActivity(
            Intent.createChooser(
                intent,
                contex?.getString(R.string.share_image)
            )
        )
    }

    private fun shareVideo(holder: ViewHolder, fileUri: File) {
        val intent = Intent(Intent.ACTION_SEND)
        intent.setType("video/mp4")
        intent.putExtra(Intent.EXTRA_STREAM, Uri.parse(fileUri.absolutePath))
        holder.itemView.context.startActivity(
            Intent.createChooser(
                intent,
                contex?.getString(R.string.share_video)
            )
        )
    }
}