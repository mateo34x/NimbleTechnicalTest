package com.example.nimbletechnicaltest

import android.view.View
import android.view.animation.AnimationUtils
import android.widget.TextView
import androidx.viewpager2.widget.ViewPager2
import kotlin.math.abs

class FadeoutTranformation : ViewPager2.PageTransformer {
    override fun transformPage(page: View, position: Float) {
        page.translationX = - position*page.width
        page.alpha = 1 - abs(position)
        page.findViewById<TextView>(R.id.section_label).animation = AnimationUtils.loadAnimation(page.context,R.anim.fade_in)
        page.findViewById<TextView>(R.id.descripcion).animation = AnimationUtils.loadAnimation(page.context,R.anim.fade_in)
    }
}