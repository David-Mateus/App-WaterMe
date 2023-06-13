package com.example.water


import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat


class SecondeActivity : AppCompatActivity() {
    private val CHANNEL_ID = "channel_id"
    private val NOTIFICATION_ID = 1
    private var qtdMlWater = 0
    // variaveis para inicilizar depois
    private lateinit var prefs: SharedPreferences

    private lateinit var btn200Ml: ImageButton
    private lateinit var btn450Ml: ImageButton
    private lateinit var btn750Ml: ImageButton
    private lateinit var txtWaterMl: TextView
    private lateinit var btnImage: ImageButton
    private lateinit var txtLastWater: TextView
    private lateinit var runnable: Runnable
    private lateinit var handler: Handler

    companion object {
        private const val PREFS_NAME = "database"
        private const val RESULT_KEY = "result"
        private const val VALUE_KEY = "value"
        private const val TEXT_LAST_WATER_KEY = "textLastWater"
    }
    val valueWater = mutableListOf<Int>()
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_seconde)

        createNotificationChannel()

        prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE)

        btn200Ml = findViewById(R.id.btn_image_200)
        btn450Ml = findViewById(R.id.btn_image_450)
        btn750Ml = findViewById(R.id.btn_image_750)
        txtWaterMl = findViewById(R.id.text_ml)
        btnImage = findViewById(R.id.reset_btn)
        txtLastWater = findViewById(R.id.textView2)


        val result = prefs.getString(RESULT_KEY, null)
        val valueStatic = prefs.getString(VALUE_KEY, null)
        val valueLastWater = prefs.getString(TEXT_LAST_WATER_KEY, null)


        valueLastWater?.let {
            txtLastWater.text = "Ultimo consumo de Água: $it ML"
        }
        valueStatic?.let {
            qtdMlWater = it.toInt()
            txtWaterMl.text = result
        }




        setButtonClick(btn200Ml, 200)
        setButtonClick(btn450Ml, 450)
        setButtonClick(btn750Ml, 750)

        btnImage.setOnClickListener {
            resertWater()

        }
        runnable = Runnable { showToast() }
        handler = Handler()
        handler.postDelayed(runnable,3600000)
    }

    private fun setButtonClick(button: ImageButton, mlValue: Int) {

        button.setOnClickListener {
            waterMl(mlValue)

        }
    }

    private fun waterMl(numberMl: Int) {

        qtdMlWater += numberMl
        valueWater.add(qtdMlWater)
        val message = resources.getString(R.string.water_ml_message, qtdMlWater)
        txtWaterMl.text = message

        val editor = prefs.edit()
        editor.putString(RESULT_KEY, txtWaterMl.text.toString())
        editor.putString(VALUE_KEY, qtdMlWater.toString())
        editor.apply()
    }
    // algum erro aqui depois verificar
    fun resertWater(){
        qtdMlWater = 0

        val lastWater = valueWater.last().toString()
        txtLastWater.text = "Ultimo consumo de Água: $lastWater ML"

        val editor2 = prefs.edit()
        editor2.putString(TEXT_LAST_WATER_KEY, lastWater)
        editor2.apply()

    }
    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel() {
        // Verifique a versão do SDK do dispositivo
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channelName = "Channel Name"
            val channelDescription = "Channel Description"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, channelName, importance).apply {
                description = channelDescription
            }

            // Registre o canal com o sistema
            val notificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun showNotification() {
        val builder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.baseline_water)
            .setContentTitle("Water")
            .setContentText("Lembre-se de beber água!")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)

        // Crie um objeto NotificationManagerCompat
        val notificationManager = NotificationManagerCompat.from(this)

        // Emita a notificação usando o ID exclusivo
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {

            return
        }
        notificationManager.notify(NOTIFICATION_ID, builder.build())
    }
    private fun showToast(){

        showNotification()
        handler.postDelayed(runnable, 3600000)


    }
}
