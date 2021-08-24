package com.udacity

import android.app.DownloadManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.*
import android.content.pm.PackageManager
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.widget.RadioButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.udacity.databinding.ActivityMainBinding
import com.udacity.databinding.ContentMainBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit


class MainActivity : AppCompatActivity() {

    private var downloadID: Long = 0

    private lateinit var binding: ActivityMainBinding
    private lateinit var bindingInclude: ContentMainBinding
    private lateinit var notificationManager: NotificationManager
    private lateinit var pendingIntent: PendingIntent
    private lateinit var action: NotificationCompat.Action
    val scope = CoroutineScope(Dispatchers.IO)


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        bindingInclude = binding.contentMainId
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)


//        val binding: ActivityMainBinding = DataBindingUtil.setContentView(
//            this, R.layout.activity_main)
//        when {
//            ContextCompat.checkSelfPermission(
//                applicationContext,
//                android.Manifest.permission.WRITE_EXTERNAL_STORAGE
//            ) == PackageManager.PERMISSION_GRANTED -> {
//                // You can use the API that requires the permission.
//            }
//        }
//            else -> {
//                // You can directly ask for the permission.
//                // The registered ActivityResultCallback gets the result of this request.
//                requestPermissionLauncher.launch(
//                    Manifest.permission.REQUESTED_PERMISSION)
//            }
//        }

        if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
            != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissions(arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE),0)
        }
        registerReceiver(receiver, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))
        bindingInclude.customButton.setOnClickListener {
            radioGroup()
        }
        createNotificationChannel("1",this)
    }

    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val id = intent?.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)
        }
    }
    private fun radioGroup() {

        if (bindingInclude.radioGroup.checkedRadioButtonId == -1) {
            Toast.makeText(this, "Please, select an option.", Toast.LENGTH_LONG).show()

        } else {
            val selectedOption = bindingInclude.radioGroup.checkedRadioButtonId
            val radioButton: RadioButton = findViewById(selectedOption)

            when (radioButton) {
                bindingInclude.glide -> {
//                    downloadFileName = getString(R.string.glide_radio_button_text)
                    download(glide)
                }
                bindingInclude.project3 -> {
//                    downloadFileName = getString(R.string.loadApp_radio_button_text)
                    download(project)
                }
                bindingInclude.pytorch -> {
                    download(pytorch)
                }
                else -> {
//                    downloadFileName = getString(R.string.retrofit_radio_button_text)
                    download(retrofit)
                }
            }
        }
    }

    private fun download(url: String) {
        var parsed = Uri.parse(url)
        val request =
            DownloadManager.Request(parsed)
                .setTitle(getString(R.string.app_name))
                .setDescription(getString(R.string.app_description))
                .setRequiresCharging(false)
                .setAllowedOverMetered(true)
                .setAllowedOverRoaming(true)

        val downloadManager = getSystemService(DOWNLOAD_SERVICE) as DownloadManager
        downloadID =
            downloadManager.enqueue(request)// enqueue puts the download request in the queue.
        scope.launch {
            // New coroutine that can call suspend functions
            var query: DownloadManager.Query
            var c: Cursor
            while (true) {
                delay(TimeUnit.MILLISECONDS.toMillis(500))
                query = DownloadManager.Query()
                query.setFilterById(downloadID)
                c = downloadManager.query(query)
                if (c.moveToFirst()) {
                    val sizeIndex: Int = c.getColumnIndex(DownloadManager.COLUMN_TOTAL_SIZE_BYTES)
                    val downloadedIndex: Int =
                        c.getColumnIndex(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR)
                    val size: Float = c.getInt(sizeIndex).toFloat()
                    val downloaded: Float = c.getInt(downloadedIndex).toFloat()
                    var progress = 0.0f
                    if (size != -1.0f) progress = downloaded * 100.0f / size
                    // At this point you have the progress as a percentage.
                    bindingInclude.customButton.setProgressValue(progress)
                    Log.i("progress", progress.toString())
                    if (progress >= 100.0f) {
                        bindingInclude.customButton.setDownloadComplete()
                        break
                    }
                }
            }
            val status = c.getInt(c.getColumnIndex(DownloadManager.COLUMN_STATUS))
            if (status == DownloadManager.STATUS_SUCCESSFUL) {
                sendNotification(applicationContext, url, "Success")
            } else {
                sendNotification(applicationContext, url, "Failed")
            }

        }
    }
    fun createNotificationChannel(channelId: String, context: Context) {

        val notificationChannel = NotificationChannel(
            channelId,
            "Load App",
            NotificationManager.IMPORTANCE_DEFAULT
        )

        notificationChannel.apply {
            setShowBadge(true)
            description = "Loading App Download Notification"
        }

        val notificationManager = context.getSystemService(NotificationManager::class.java)
        notificationManager.createNotificationChannel(notificationChannel)
    }


    fun sendNotification(context: Context, fileName: String, status: String) {
        val notificationIntent = Intent(context, DetailActivity::class.java).apply {
            flags =
                Intent.FLAG_ACTIVITY_CLEAR_TOP
            putExtra("FILE_NAME", fileName)
            putExtra("STATUS", status)
        }
        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            notificationIntent,
            PendingIntent.FLAG_UPDATE_CURRENT)

        val notification = context.let {
            NotificationCompat.Builder(it,"1")
                .setSmallIcon(R.drawable.ic_assistant_black_24dp)
                .setContentTitle("Load App")
                .setContentText("Your Download Has Completed")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)
                .addAction(
                    R.drawable.ic_launcher_background, "Notification action",
                    pendingIntent
                )
                .build()
        }

        val notificationManagerCompat = NotificationManagerCompat.from(context)
        notificationManagerCompat.notify(1, notification)
    }

        companion object {
        private const val glide =
            "https://github.com/bumptech/glide/archive/master.zip"
        private const val project =
            "https://github.com/udacity/nd940-c3-advanced-android-programming-project-starter/archive/master.zip"
        private const val retrofit =
            "https://github.com/square/retrofit/archive/master.zip"
        private const val pytorch =
            "https://github.com/pytorch/pytorch/archive/master.zip"

        private const val CHANNEL_ID = "channelId"
    }

}
