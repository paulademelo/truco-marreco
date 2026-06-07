package com.example.choramarreco

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.choramarreco.databinding.ActivityHomeBinding
import com.example.choramarreco.utils.setAnimatedClickListener


class HomeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityHomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.btnCoin.tvCoinTitle.text = getString(R.string.insert)
        binding.btnCoin.tvCoinSubtitle.text = getString(R.string.names)
        binding.btnCoin.root.setAnimatedClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }
}