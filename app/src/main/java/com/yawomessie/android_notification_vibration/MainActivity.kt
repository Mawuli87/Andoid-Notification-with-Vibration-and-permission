package com.yawomessie.android_notification_vibration

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.TaskStackBuilder
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.yawomessie.android_notification_vibration.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity(), View.OnClickListener {
    private lateinit var binding: ActivityMainBinding

    private lateinit var requestLauncher: ActivityResultLauncher<String>



    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)



        requestLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) {
            if (it) {
                showNotification(
                    this@MainActivity,
                    getString(R.string.notification_title), getString(R.string.notification_message), 110
                )

                val detailIntent = Intent(this@MainActivity, SecondActivity::class.java)
                detailIntent.putExtra(SecondActivity.EXTRA_TITLE, getString(R.string.detail_title))
                detailIntent.putExtra(SecondActivity.EXTRA_MESSAGE, getString(R.string.detail_message))
                startActivity(detailIntent)
            } else {
                //show error message
             showErrorMessage()

            }
        }
        binding.btnOpenDetail.setOnClickListener(this)



    }


    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onClick(v: View?) {
        if (v?.id == binding.btnOpenDetail.id) {
          askForNotificationPermission()

            }

    }

    @RequiresApi(Build.VERSION_CODES.S)
    private fun showNotification(context: Context, title: String, message: String, notifId: Int) {
        val CHANNEL_ID = "Channel_1"
        val CHANNEL_NAME = "Navigation channel"

        val notifDetailIntent = Intent(this, SecondActivity::class.java)
        notifDetailIntent.putExtra(SecondActivity.EXTRA_TITLE, title)
        notifDetailIntent.putExtra(SecondActivity.EXTRA_MESSAGE, message)

        val pendingIntent = TaskStackBuilder.create(this)
            .addParentStack(SecondActivity::class.java)
            .addNextIntent(notifDetailIntent)
            .getPendingIntent(110,PendingIntent.FLAG_MUTABLE or PendingIntent.FLAG_UPDATE_CURRENT)

        val notificationManagerCompat =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setContentTitle(title)
            .setSmallIcon(R.drawable.ic_baseline_email_24)
            .setContentText(message)
            .setColor(ContextCompat.getColor(context, android.R.color.black))
            .setVibrate(longArrayOf(1000, 1000, 1000, 1000, 1000))
            .setSound(alarmSound)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_DEFAULT
            )

            channel.enableVibration(true)
            channel.vibrationPattern = longArrayOf(1000, 1000, 1000, 1000, 1000)

            builder.setChannelId(CHANNEL_ID)

            notificationManagerCompat.createNotificationChannel(channel)
        }

        val notification = builder.build()

        notificationManagerCompat.notify(notifId, notification)
    }


    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private fun askForNotificationPermission() {
    requestLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)

    }

    private fun showErrorMessage() {
        Toast.makeText(
            this,
            "Permission is not granted",
            Toast.LENGTH_SHORT
        ).show()
    }
}