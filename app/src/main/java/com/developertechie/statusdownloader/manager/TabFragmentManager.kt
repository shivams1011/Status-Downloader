package com.developertechie.statusdownloader.manager

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import com.developertechie.statusdownloader.fragment.StatusFragment
import com.developertechie.statusdownloader.utils.Constants.StatusDownloadInfo.DIR_NAME
import com.developertechie.statusdownloader.utils.Constants.StatusDownloadInfo.STATUS_FOLDER

/**
 * Created by ShivamSharma on 2019-10-21.
 */
class TabFragmentManager(fm: FragmentManager) : FragmentStatePagerAdapter(fm) {
    private val tabTitle = arrayOf("Recent Status", "Saved Status")
    override fun getItem(position: Int): Fragment {
        when(position){
            0 -> return StatusFragment(STATUS_FOLDER)
            1->  return StatusFragment(DIR_NAME)
        }
        return StatusFragment(STATUS_FOLDER)
    }

    override fun getCount(): Int {
       return tabTitle.size
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return tabTitle.get(position)
    }
}