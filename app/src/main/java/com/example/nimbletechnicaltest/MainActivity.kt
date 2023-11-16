package com.example.nimbletechnicaltest

import android.app.IntentService
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import okhttp3.Call
import okhttp3.Callback
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import okio.IOException
import org.json.JSONObject


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        /*val handler = Handler(Looper.getMainLooper())

        val runnable = Runnable {
            val bitmap = Blurry.with(this)
                .radius(40)
                .sampling(4).animate(10000)
                .capture(findViewById(R.id.blurimg)).get()
            findViewById<ImageView>(R.id.blurimg).setImageDrawable(
                BitmapDrawable(
                    resources,
                    bitmap
                )
            )
            Toast.makeText(applicationContext, "Pasaron 10 segundos", Toast.LENGTH_SHORT).show()
        }

        handler.postDelayed(runnable, 5000)*/





        val getCorreo: (EditText) = findViewById(R.id.email)
        val getpass: (EditText) = findViewById(R.id.pass)


        val client = OkHttpClient().newBuilder().build()

        fun postRegistrer(callback: Callback): Call {

            val userObject = JSONObject()
            val user = JSONObject()

            user.put("email", "ADMIN@gmail.com")
            user.put("name", "Developer")
            user.put("password", "12345678")
            user.put("password_confirmation", "12345678")

            userObject.put("user", user)
            userObject.put("client_id", "ofzl-2h5ympKa0WqqTzqlVJUiRsxmXQmt5tkgrlWnOE")
            userObject.put("client_secret", "lMQb900L-mTeU-FVTCwyhjsfBwRCxwwbCitPob96cuU")
            val json = "application/json; charset=utf-8".toMediaTypeOrNull()
            val body = userObject.toString().toRequestBody(json)

            val request = Request.Builder()
                .url("https://survey-api.nimblehq.co/api/v1/registrations")
                .method("POST", body)
                .build()
            val call = client.newCall(request)
            call.enqueue(callback)
            return call
        }

        fun postLogin(callback: Callback, email: String, password: String): Call {

            val userObject = JSONObject()

            userObject.put("grant_type", "password")
            userObject.put("email", email)
            userObject.put("password", password)
            userObject.put("client_id", "ofzl-2h5ympKa0WqqTzqlVJUiRsxmXQmt5tkgrlWnOE")
            userObject.put("client_secret", "lMQb900L-mTeU-FVTCwyhjsfBwRCxwwbCitPob96cuU")
            val json = "application/json; charset=utf-8".toMediaTypeOrNull()
            val body = userObject.toString().toRequestBody(json)

            val request = Request.Builder()
                .url("https://survey-api.nimblehq.co/api/v1/oauth/token")
                .method("POST", body)
                .build()
            val call = client.newCall(request)
            call.enqueue(callback)
            return call
        }


        findViewById<View>(R.id.button).setOnClickListener {

            val callback = object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    runOnUiThread(){
                        Toast.makeText(
                            applicationContext,
                            "Connection failed",
                            Toast.LENGTH_LONG
                        ).show()
                    }

                }

                override fun onResponse(call: Call, response: Response) {
                    when (response.code) {
                        200 -> {
                            val responseStr = response.body?.string()
                            runOnUiThread {

                                try {
                                    val jsonResponse = responseStr?.let { it1 -> JSONObject(it1) }
                                    val data = jsonResponse?.getJSONObject("data")
                                    val attributes = data?.getJSONObject("attributes")

                                    val accessToken = attributes?.getString("access_token")
                                    val refreshToken = attributes?.getString("refresh_token")
                                    val expiresIn = attributes?.getInt("expires_in")
                                    val created_at = attributes?.getInt("created_at")

                                    val sharedPreferences =
                                        getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)
                                    val editor = sharedPreferences.edit()



                                    editor.putString("access_token", accessToken)
                                    editor.putString("refresh_token", refreshToken)
                                    editor.putLong(
                                        "token_expires_at", expiresIn?.toLong()!!
                                    )
                                    editor.putLong("created_at", created_at?.toLong()!!)
                                    editor.apply()

                                    if (accessToken != null) {
                                        Log.e("TOKENSign", accessToken + "\n" + refreshToken)
                                        SurveyListHolder.surveyList.clear()

                                        val tokenRenewalHelper = TokenRenewalHelper()
                                        tokenRenewalHelper.getSurveys(this@MainActivity,accessToken)
                                    }else{
                                        Toast.makeText(this@MainActivity,"Intente nuevamente",Toast.LENGTH_LONG).show()
                                    }




                                } finally {

                                }


                            }
                        }

                        400 -> {
                            runOnUiThread {


                                Toast.makeText(
                                    applicationContext,
                                    "The provided authorization grant is invalid\nYour email or password is incorrect. Please try again",
                                    Toast.LENGTH_LONG
                                ).show()
                            }

                        }

                        403 -> {
                            Toast.makeText(
                                applicationContext,
                                "Client authentication failed due to unknown client",
                                Toast.LENGTH_LONG
                            ).show()
                        }

                    }
                }
            }
            postLogin(callback, getCorreo.getText().toString(), getpass.getText().toString())
            Toast.makeText(
                applicationContext,
                getCorreo.getText().toString() + getpass.getText().toString(),
                Toast.LENGTH_LONG
            ).show()


        }

    }


}