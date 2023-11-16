package com.example.nimbletechnicaltest

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ObjectAnimator
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.transition.Fade
import android.util.Log
import android.view.View
import android.view.animation.AlphaAnimation
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.AppCompatButton
import androidx.viewpager.widget.ViewPager
import androidx.viewpager2.widget.ViewPager2
import com.facebook.shimmer.ShimmerFrameLayout
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.tbuonomo.viewpagerdotsindicator.WormDotsIndicator
import okhttp3.Call
import okhttp3.Callback
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import okhttp3.internal.notify
import okio.IOException
import org.chromium.base.ThreadUtils
import org.json.JSONObject
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.abs

class Home : AppCompatActivity() {


    private lateinit var remainingTimeTextView: TextView
    private lateinit var shimmer: ShimmerFrameLayout
    private lateinit var viewPager: ViewPager2
    private lateinit var prueba: RelativeLayout
    private lateinit var wormDotsIndicator: WormDotsIndicator
    private lateinit var takesurvey: AppCompatButton

    private var tokenExpiresAtC: Long = 0
    private lateinit var fecha: TextView
    private var act: String = "hello"
    private var refreshToken: String = "hello"
    private var created_at: Long = 0
    private var know: Boolean = false
    private lateinit var countDownTimer: CountDownTimer


    private fun startCountDownTimer(timeInMilliseconds: Long) {
        countDownTimer = object : CountDownTimer(timeInMilliseconds, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                val currentTimestamp = System.currentTimeMillis() / 1000
                val timeRemaining = tokenExpiresAtC - currentTimestamp

                if (timeRemaining > 0) {
                    val minutes = (timeRemaining / 60).toInt()
                    val seconds = (timeRemaining % 60).toInt()
                    val timeRemainingText = String.format("%02d:%02d", minutes, seconds)
                    remainingTimeTextView.text = timeRemainingText
                } else {

                    //remainingTimeTextView.text = "expiró"

                    if (!know) {
                        know = true
                        newToken()

                    }
                }
            }

            override fun onFinish() {


            }
        }
        countDownTimer.start()

    }

    private fun newToken() {

        var retryCount = 0

        val sharedPreferences = getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)
        val refreshToken = sharedPreferences.getString("refresh_token", null)
        val actken = sharedPreferences.getString("access_token", null)


        val client = OkHttpClient().newBuilder().build()

        val userObject = JSONObject()
        userObject.put("grant_type", "refresh_token")
        userObject.put("refresh_token", refreshToken)
        userObject.put("client_id", "ofzl-2h5ympKa0WqqTzqlVJUiRsxmXQmt5tkgrlWnOE")
        userObject.put("client_secret", "lMQb900L-mTeU-FVTCwyhjsfBwRCxwwbCitPob96cuU")

        val json = "application/json; charset=utf-8".toMediaTypeOrNull()

        val body = userObject.toString().toRequestBody(json)

        val request = Request.Builder()
            .url("https://survey-api.nimblehq.co/api/v1/oauth/token")
            .method("POST", body)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                ThreadUtils.runOnUiThread() {
                    retryCount++
                    if (retryCount < 3) {
                        when (e) {
                            is SocketTimeoutException -> {
                                Toast.makeText(
                                    this@Home,
                                    "conexión fallida con el servidor reintentando",
                                    Toast.LENGTH_LONG
                                ).show()
                            }

                            is UnknownHostException -> {
                                Toast.makeText(
                                    this@Home,
                                    "Acceda a una red wifi\ncon internet, o perderá acceso\na las encuestas",
                                    Toast.LENGTH_LONG
                                ).show()

                            }
                        }
                    }


                }
            }

            override fun onResponse(call: Call, response: Response) {

                if (response.code == 200) {
                    SurveyListHolder.surveyList.clear()
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
                        know = false

                    } catch (_: Exception) {
                        // Manejar errores
                    }

                    runOnUiThread {
                        if (call.isExecuted()) {
                            val tokenRenewalHelper = TokenRenewalHelper()
                            if (actken != null) {
                                tokenRenewalHelper.getSurveysRefresh(this@Home, actken, viewPager)
                            }
                            refreshCounter()


                        }
                    }

                }else if(response.code == 400){
                    runOnUiThread {
                        Toast.makeText(
                            this@Home,
                            "The provided authorization grant is invalid",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            }
        })
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        val sharedPreferences = getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);

        val date = Date()
        val formatoFecha = SimpleDateFormat("EEEE, MMMM dd", Locale.getDefault())
        val fechaFormateada = formatoFecha.format(date)
        val fechaFormateadaConMayusculas = fechaFormateada.replaceFirstChar { it.uppercase() }
        val fadeoutTranformation = FadeoutTranformation()
        shimmer = findViewById(R.id.shimmer);
        shimmer.visibility = View.VISIBLE
        shimmer.startShimmer()
        prueba = findViewById(R.id.prueba)
        viewPager = findViewById(R.id.viewPager)
        wormDotsIndicator = findViewById(R.id.worm_dots_indicator)
        takesurvey = findViewById(R.id.takesurvey)



        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                viewPager.setPageTransformer(fadeoutTranformation)

                val Title = SurveyListHolder.surveyList[position].title
                val Description = SurveyListHolder.surveyList[position].description
                val Image = SurveyListHolder.surveyList[position].cover_image_url
                takesurvey.setOnClickListener{
                    val intent = Intent(this@Home, TakeSurvey::class.java)
                    intent.putExtra("title", Title)
                    intent.putExtra("description", Description)
                    intent.putExtra("cover_image_url", Image)
                    startActivity(intent)
                }

            }
        })








    fecha = findViewById(R.id.fecha)
    fecha.text = fechaFormateadaConMayusculas

    remainingTimeTextView = findViewById(R.id.remainingTimeTextView)
    act = sharedPreferences.getString("access_token", null).toString()
    refreshToken = sharedPreferences.getString("refresh_token", null).toString()
    created_at = sharedPreferences.getLong("created_at", 0)


    val twoHoursInSeconds = 6900
    tokenExpiresAtC = created_at + twoHoursInSeconds
    startCountDownTimer(tokenExpiresAtC)


    val handler = Handler()
    val delayMillis = 3000
    handler.postDelayed(
    {


        if (act == "hello") {
            Toast.makeText(this, "No se ha obtenido el token", Toast.LENGTH_SHORT).show()
        } else {

                shimmer.stopShimmer()
                val fadeOut = ObjectAnimator.ofFloat(
                    shimmer, "alpha", 1f, 0f
                )

                fadeOut.duration = 500

                fadeOut.addListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationEnd(animation: Animator) {
                        shimmer.visibility = View.GONE
                        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);

                        viewPager.adapter =
                            TaskPagerAdapter(this@Home, SurveyListHolder.surveyList)
                        wormDotsIndicator.attachTo(viewPager)
                        prueba.visibility = View.VISIBLE


                    }


                })

                fadeOut.start()



        }

    }, delayMillis.toLong())


}

private fun refreshCounter() {
    val sharedPreferences = getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)
    act = sharedPreferences.getString("access_token", null).toString()
    refreshToken = sharedPreferences.getString("refresh_token", null).toString()
    created_at = sharedPreferences.getLong("created_at", 0)

    shimmer.alpha = 1f
    shimmer.visibility = View.VISIBLE
    shimmer.startShimmer()
    prueba.visibility = View.GONE


    val twoHoursInSeconds = 6900
    tokenExpiresAtC = created_at + twoHoursInSeconds
    startCountDownTimer(tokenExpiresAtC)


    val handler = Handler()
    val delayMillis = 3000

    handler.postDelayed({


        shimmer.stopShimmer()
        val fadeOut = ObjectAnimator.ofFloat(
            shimmer, "alpha", 1f, 0f
        )

        fadeOut.duration = 500

        fadeOut.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                shimmer.visibility = View.GONE
                val nuevo = TaskPagerAdapter(this@Home, SurveyListHolder.surveyList)
                viewPager.adapter = nuevo
                wormDotsIndicator.attachTo(viewPager)
                prueba.visibility = View.VISIBLE

            }
        })

        fadeOut.start()


    }, delayMillis.toLong())


}



override fun onBackPressed() {
    super.onBackPressed()
    finish()
}

override fun onDestroy() {
    super.onDestroy()
    SurveyListHolder.surveyList.clear()
}

override fun onResume() {
    super.onResume()
    overridePendingTransition(R.anim.fade_in, R.anim.fade_out);

}




}