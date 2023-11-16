package com.example.nimbletechnicaltest

import android.content.Intent
import android.os.Bundle
import android.transition.Fade
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.AppCompatButton
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide

class TaskFragment : Fragment() {

    private lateinit var titleTextView: TextView
    private lateinit var descriptionTextView: TextView



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_main, container, false)
        titleTextView = view.findViewById(R.id.section_label)
        descriptionTextView = view.findViewById(R.id.descripcion)
        val background: ImageView = view.findViewById(R.id.view)


        val title = arguments?.getString("title") ?: ""
        val description = arguments?.getString("description") ?: ""
        val img_url = arguments?.getString("cover_image_url") ?: ""




        titleTextView.text = title
        descriptionTextView.text = description

        img_url.let {
            Glide.with(this)
                .load(it + "l")
                .placeholder(R.drawable.placeholdertwo) // Puedes cambiar esto por tu recurso de carga
                .error(R.drawable.placeholdertwo) // Puedes cambiar esto por tu recurso de error
                .into(background)
        }


        return view
    }


}