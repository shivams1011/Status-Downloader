package com.developertechie.statusdownloader.fragment

import android.os.Bundle
import android.os.Environment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.transition.Transition
import androidx.transition.TransitionManager
import com.developertechie.statusdownloader.R
import com.developertechie.statusdownloader.activity.ImageVideoActivity
import com.developertechie.statusdownloader.adapter.StatusAdapter
import com.developertechie.statusdownloader.adapter.StatusAdapter.Companion.MP4
import com.developertechie.statusdownloader.databinding.FragmentStatusBinding
import com.developertechie.statusdownloader.model.FileDetail
import com.developertechie.statusdownloader.utils.Constants.StatusDownloadInfo.DIR_NAME
import com.developertechie.statusdownloader.utils.Constants.StatusDownloadInfo.STATUS_FOLDER
import com.developertechie.statusdownloader.utils.Utils
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.fragment_status.*
import java.io.File

/**
 * Created by ShivamSharma on 2019-09-15.
 */
class StatusFragment(private val folderName: String) : Fragment() {

    private lateinit var binding: FragmentStatusBinding
    val fileList = ArrayList<FileDetail>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_status, container, false
        )

        setupRecycelerView(binding)
        getFileFromLocal(binding)
        getSwipeRefreshLayout(binding)
        return binding.root
    }

    private fun getSwipeRefreshLayout(binding: FragmentStatusBinding) {
        binding.swipeRefreshLayout.setOnRefreshListener {
            getFileFromLocal(binding)
            Toast.makeText(context, "Refresh Successful", Toast.LENGTH_SHORT).show()
            swipeRefreshLayout.isRefreshing = false
        }
    }

    private fun setupRecycelerView(binding: FragmentStatusBinding) {
        binding.recyclerView.layoutManager = GridLayoutManager(context, 2)
        val adapter = StatusAdapter(fileList, folderName)
        binding.recyclerView.adapter = adapter

        val compositeDisposable = CompositeDisposable()

        compositeDisposable.add(adapter.getPublishSubject.subscribe {
            context?.let { it1 -> ImageVideoActivity.newInstance(it, it1) }
        })
    }

    private fun getFileFromLocal(binding: FragmentStatusBinding) {
        fileList.clear()

        var rootPath: File
        if (folderName.equals(STATUS_FOLDER)) {
            rootPath = File(Environment.getExternalStorageDirectory(), STATUS_FOLDER)
            fileList.addAll(Utils.loadmp3(rootPath.absolutePath).reversed())
        } else {
            rootPath = File(Environment.getExternalStorageDirectory(), DIR_NAME)
            fileList.addAll(Utils.loadmp3(rootPath.absolutePath).reversed())
        }

        if (fileList.isEmpty()) {
            binding.noContentView.visibility = View.VISIBLE
        } else {
            binding.noContentView.visibility = View.GONE
        }
        binding.recyclerView.adapter?.notifyDataSetChanged()
    }
}