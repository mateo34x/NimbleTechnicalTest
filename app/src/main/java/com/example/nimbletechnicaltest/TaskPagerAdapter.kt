package com.example.nimbletechnicaltest

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import kotlin.math.abs

class TaskPagerAdapter(activity: AppCompatActivity, private val tasks: MutableList<Surveys>) :
    FragmentStateAdapter(activity) {

    override fun getItemCount(): Int = tasks.size

    override fun createFragment(position: Int): Fragment {
        val fragment = TaskFragment()
        fragment.arguments = Bundle().apply {
            putInt("position",position)
            putString("title", tasks[position].title)
            putString("description", tasks[position].description)
            putString("cover_image_url", tasks[position].cover_image_url)
        }



        return fragment
    }



    }