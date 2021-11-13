package indi.conastin.textmodify.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import indi.conastin.textmodify.databinding.ActivityAboutBinding

class AboutActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // binding创建layout
        val binding = ActivityAboutBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}