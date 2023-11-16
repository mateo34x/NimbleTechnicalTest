package com.example.nimbletechnicaltest

import android.os.Bundle
import android.transition.Fade
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide

class TakeSurvey : AppCompatActivity() {


    private lateinit var title : TextView
    private lateinit var description : TextView
    private lateinit var background_survey : ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_take_survey)
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out)


        val getSharedTitle = intent.getStringExtra("title")
        val getSharedesciption = intent.getStringExtra("description")
        val getSharedImage = intent.getStringExtra("cover_image_url")
        title = findViewById(R.id.Title)
        description = findViewById(R.id.description)
        background_survey = findViewById(R.id.backgroundS)

        title.text = getSharedTitle
        description.text = getSharedesciption


        getSharedImage.let {
            Glide.with(this)
                .load(it + "l")
                .placeholder(R.drawable.placeholdertwo)
                .error(R.drawable.placeholdertwo)
                .into(background_survey)
        }


    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }
}