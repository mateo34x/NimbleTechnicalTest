package com.example.nimbletechnicaltest

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import androidx.viewpager2.widget.ViewPager2
import com.google.gson.Gson
import okhttp3.Call
import okhttp3.Callback
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import okio.IOException
import org.chromium.base.ThreadUtils.runOnUiThread
import org.json.JSONObject
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import java.util.concurrent.TimeUnit

class TokenRenewalHelper {
    var retryCount = 0
    fun renewToken(context: Context, mensaje: String) {
        val sharedPreferences = context.getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)
        val refreshToken = sharedPreferences.getString("refresh_token", null)


        if (refreshToken != null) {
            val client = OkHttpClient().newBuilder().build()

            val userObject = JSONObject()
            userObject.put("grant_type", "refresh_token")
            userObject.put("refresh_token", refreshToken)
            userObject.put("client_id", "ofzl-2h5ympKa0WqqTzqlVJUiRsxmXQmt5tkgrlWnOE")
            userObject.put("client_secret", "lMQb900L-mTeU-FVTCwyhjsfBwRCxwwbCitPob96cuU")

            val json = "application/json; charset=utf-8".toMediaTypeOrNull()

            val body = userObject.toString().toRequestBody(json)

            val request = Request.Builder().url("https://survey-api.nimblehq.co/api/v1/oauth/token")
                .method("POST", body).build()

            client.newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    // Manejar fallos
                }

                override fun onResponse(call: Call, response: Response) {
                    if (response.code == 200) {
                        val responseStr = response.body?.string()

                        try {
                            val jsonResponse = responseStr?.let { JSONObject(it) }
                            val data = jsonResponse?.getJSONObject("data")
                            val attributes = data?.getJSONObject("attributes")

                            val accessToken = attributes?.getString("access_token")
                            val refreshToken = attributes?.getString("refresh_token")
                            val expiresIn = attributes?.getLong("expires_in")
                            val created_at = attributes?.getLong("created_at")

                            val editor = sharedPreferences.edit()


                            editor.putString("access_token", accessToken)
                            editor.putString("refresh_token", refreshToken)
                            editor.putLong("token_expires_at", expiresIn!!)
                            editor.putLong("created_at", created_at!!)
                            editor.apply()

                            if (accessToken != null) {
                                runOnUiThread() {
                                    getSurveys(context, accessToken)
                                }
                                Log.e(
                                    "TOKENACT", accessToken + "\n" + refreshToken + "\n" + mensaje

                                )
                            }


                        } catch (_: Exception) {
                            // Manejar errores
                        }


                    }
                }
            })
        } else {
            val intent = Intent(context, MainActivity::class.java)
            context.startActivity(intent)
        }

    }




    fun getSurveys(context: Context, token: String): MutableList<Surveys> {


        val client = OkHttpClient().newBuilder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .callTimeout(30, TimeUnit.SECONDS)
            .build()
        val request = Request.Builder()
            .url("https://survey-api.nimblehq.co/api/v1/surveys?page[number]=1&page[size]=10").get()
            .addHeader("content_type", "application/json; charset=utf-8")
            .addHeader("Authorization", "Bearer " + token)

            .build()


        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: java.io.IOException) {

                runOnUiThread() {
                    retryCount++
                    if (retryCount < 3) {
                        when (e) {
                            is SocketTimeoutException -> {
                                Toast.makeText(
                                    context,
                                    "conexión fallida con el servidor reintentando",
                                    Toast.LENGTH_LONG
                                ).show()
                                getSurveys(context, token)
                            }

                            is UnknownHostException -> {
                                Toast.makeText(
                                    context,
                                    "Acceda a una red wifi\ncon internet",
                                    Toast.LENGTH_LONG
                                ).show()
                                getSurveys(context, token)

                            }
                        }
                    }


                }
            }

            override fun onResponse(call: Call, response: Response) {


                val responseStr = response.body?.string()

                if (response.code == 200) {

                    try {
                        val jsonResponse = responseStr?.let { JSONObject(it) }
                        val dataArray = jsonResponse?.getJSONArray("data")

                        dataArray?.let {
                            for (i in 0 until it.length()) {
                                val surveyObject = it.getJSONObject(i)

                                val id = surveyObject.getString("id")
                                val type = surveyObject.getString("type")

                                val attributes = surveyObject.getJSONObject("attributes")
                                val title = attributes.getString("title")
                                val description = attributes.getString("description")
                                val surveyType = attributes.getString("survey_type")
                                val coverImageUrl = attributes.getString("cover_image_url")


                                SurveyListHolder.surveyList.add(
                                    Surveys(
                                        id,
                                        type,
                                        title,
                                        description,
                                        surveyType,
                                        coverImageUrl
                                    )
                                )


                                val sharedPreferences =
                                    context.getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)
                                val editor = sharedPreferences.edit()

                                val gson = Gson()
                                val json = gson.toJson(SurveyListHolder.surveyList)

                                editor.putString("surveylocal", json)
                                editor.apply()


                            }
                        }


                    } catch (_: IOException) {

                    }

                    runOnUiThread {
                        if (call.isExecuted()) {
                            val surveyTitles = SurveyListHolder.surveyList.map { it.title }

                            val surveyTitlesString = surveyTitles.joinToString("\n")

                            Log.d("SurveyList", "Títulos de las encuestas:\n$surveyTitlesString")

                            if (SurveyListHolder.surveyList.size > 0) {
                                val intent = Intent(context, Home::class.java)
                                context.startActivity(intent)
                                if (context is Activity) {
                                    (context as Activity)
                                    context.finish()
                                }
                            }


                        }
                    }

                }
            }

        })

        return SurveyListHolder.surveyList
    }


    fun getSurveysRefresh(context: Context, token: String, viewPager2: ViewPager2): MutableList<Surveys> {

        SurveyListHolder.surveyList.clear()
        val client = OkHttpClient().newBuilder().build()
        val request = Request.Builder()
            .url("https://survey-api.nimblehq.co/api/v1/surveys?page[number]=1&page[size]=10").get()
            .addHeader("content_type", "application/json; charset=utf-8")
            .addHeader("Authorization", "Bearer " + token)
            .build()


        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: java.io.IOException) {
                if (call.isExecuted()) {
                    runOnUiThread() {
                        retryCount++
                        if (retryCount < 3) {
                            when (e) {
                                is SocketTimeoutException -> {
                                    Toast.makeText(
                                        context,
                                        "conexión fallida con el servidor reintentando",
                                        Toast.LENGTH_LONG
                                    ).show()
                                    getSurveys(context, token)
                                }

                                is UnknownHostException -> {
                                    Toast.makeText(
                                        context,
                                        "Acceda a una red wifi\ncon internet",
                                        Toast.LENGTH_LONG
                                    ).show()
                                    getSurveys(context, token)

                                }
                            }
                        }


                    }

                }
            }

            override fun onResponse(call: Call, response: Response) {


                val responseStr = response.body?.string()

                if (response.code == 200) {

                    try {
                        val jsonResponse = responseStr?.let { JSONObject(it) }
                        val dataArray = jsonResponse?.getJSONArray("data")

                        dataArray?.let {
                            for (i in 0 until it.length()) {
                                val surveyObject = it.getJSONObject(i)

                                val id = surveyObject.getString("id")
                                val type = surveyObject.getString("type")

                                val attributes = surveyObject.getJSONObject("attributes")
                                val title = attributes.getString("title")
                                val description = attributes.getString("description")
                                val surveyType = attributes.getString("survey_type")
                                val coverImageUrl = attributes.getString("cover_image_url")


                                SurveyListHolder.surveyList.add(
                                    Surveys(
                                        id,
                                        type,
                                        title,
                                        description,
                                        surveyType,
                                        coverImageUrl
                                    )
                                )

                                val sharedPreferences =
                                    context.getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)
                                val editor = sharedPreferences.edit()

                                val gson = Gson()
                                val json = gson.toJson(SurveyListHolder.surveyList)

                                // Guardar la lista en SharedPreferences
                                editor.putString("surveylocal", json)
                                editor.apply()

                                runOnUiThread {

                                    viewPager2.adapter = null
                                    val surveyTitles = SurveyListHolder.surveyList.map { it.title }

                                    // Crear un string que contenga solo los títulos, separados por saltos de línea
                                    val surveyTitlesString = surveyTitles.joinToString("\n")

                                    // Mostrar solo los títulos de las encuestas en un solo mensaje en el log
                                    Log.d(
                                        "SurveyList",
                                        "Títulos de las encuestas:\n$surveyTitlesString"
                                    )


                                }

                            }
                        }


                    } catch (_: Exception) {

                    }


                }
            }

        })

        return SurveyListHolder.surveyList
    }



}
