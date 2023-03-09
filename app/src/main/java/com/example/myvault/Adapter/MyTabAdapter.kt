package com.example.myvault.Adapter

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import com.example.myvault.imgFrag
import com.example.myvault.pdfFrag
import com.example.myvault.profFrag

internal class MyTabAdapter(var context: Context, fm: FragmentManager, var totalTabs: Int): FragmentStatePagerAdapter(fm) {

    override fun getItem(position: Int): Fragment {
        return when(position){
            0 -> {
                pdfFrag()
            }

            1 -> {
                imgFrag()
            }

        else -> getItem(position)
        }
    }
    override fun getCount(): Int {
        return totalTabs
    }
}