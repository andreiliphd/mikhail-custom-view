package com.udacity

import android.app.DownloadManager
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
            while (true) {
                delay(TimeUnit.MILLISECONDS.toMillis(500))
                val query = DownloadManager.Query()
                query.setFilterById(downloadID)

                val c: Cursor = downloadManager.query(query)
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
        }




//        fun ContentResolver.registerObserver(
//            uri: Uri,
//            observer: (selfChange: Boolean) -> Unit
//        ): ContentObserver {
//            // 1
//            val contentObserver = object : ContentObserver(Handler()) {
//                override fun onChange(selfChange: Boolean) {
//                    observer(selfChange)
//                    Log.i("observer", selfChange.toString())
//                }
//            }
//            // 2
//            registerContentObserver(parsed, true, contentObserver)
//            return contentObserver
//        }
//        contentResolver.registerContentObserver(myDownloads, true, DownloadObserver())

    }

    object RepeatHelper {
        fun repeatDelayed(delay: Long, todo: () -> Unit) {
            val handler = Handler()
            handler.postDelayed(object : Runnable {
                override fun run() {
                    todo()
                    handler.postDelayed(this, delay)
                }
            }, delay)
        }
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
