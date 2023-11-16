package com.example.nimbletechnicaltest

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.widget.Toast
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class SplashScreen : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)




        val sharedPreferences = getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)
        val created_at = sharedPreferences.getLong("created_at", 0)
        val act = sharedPreferences.getString("access_token", null).toString()


        val currentTimestamp = System.currentTimeMillis() / 1000
        val timeDifference = currentTimestamp - created_at
        val twoHoursInSeconds = 6900

        if (act != "null") {
            if (timeDifference < twoHoursInSeconds) {

                val handler = Handler()
                val delayMillis = 1000

                handler.postDelayed({
                    SurveyListHolder.surveyList = getSurveyList(this)
                    if (SurveyListHolder.surveyList.size > 0) {
                        val intent = Intent(this, Home::class.java)
                        startActivity(intent)
                        finish()
                    }

                }, delayMillis.toLong())





            } else {

                SurveyListHolder.surveyList.clear()

                val tokenRenewalHelper = TokenRenewalHelper()
                tokenRenewalHelper.renewToken(this@SplashScreen, "Expiró fuera de la app")



            }

        } else {
            val handler = Handler()
            val delayMillis = 1000

            handler.postDelayed({
                val intent = Intent(this@SplashScreen, MainActivity::class.java)
                startActivity(intent)
                finish()

            }, delayMillis.toLong())
        }

        Log.e("valuetoken", act)



}

    private fun getSurveyList(context: Context): MutableList<Surveys> {
        val sharedPreferences = getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)

        val surveylistlocal = sharedPreferences.getString("surveylocal",null)

        val gson = Gson()
        val type = object : TypeToken<MutableList<Surveys>>() {}.type
        return gson.fromJson(surveylistlocal, type) ?: mutableListOf()
    }




}