package com.udacity

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationManagerCompat
import androidx.databinding.DataBindingUtil
import com.udacity.databinding.ActivityDetailBinding
import com.udacity.databinding.ActivityMainBinding
import com.udacity.databinding.ContentDetailBinding

class DetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailBinding
    private lateinit var bindingInclude: ContentDetailBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        bindingInclude = binding.contentDetailId
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)


        bindingInclude.fileNameTextView.text = intent.getStringExtra("FILE_NAME")
        bindingInclude.statusTextView.text = intent.getStringExtra("STATUS")
        bindingInclude.statusTextView.setTextColor(
            if (intent.getStringExtra("STATUS") == "Success") Color.GREEN else Color.RED)

        bindingInclude.okayButton.setOnClickListener { startActivity(Intent(this, MainActivity::class.java)) }

        val notificationManagerCompat = NotificationManagerCompat.from(this)
        notificationManagerCompat.cancel(1)

    }

}
