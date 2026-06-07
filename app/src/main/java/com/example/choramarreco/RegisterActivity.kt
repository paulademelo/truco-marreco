package com.example.choramarreco

import android.content.Intent
import android.os.Bundle
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.choramarreco.databinding.ActivityRegisterBinding
import com.example.choramarreco.utils.setAnimatedClickListener

class RegisterActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        savedInstanceState?.let {
            binding.player1.setText(it.getString(KEY_PLAYER_1))
            binding.player2.setText(it.getString(KEY_PLAYER_2))
        }

        binding.btnCoin.root.setAnimatedClickListener {
            val player1 = binding.player1.text.toString()
            val player2 = binding.player2.text.toString()
            val intent = Intent(this, GameActivity::class.java).apply {
                putExtra(GameActivity.KEY_PLAYER_1, player1)
                putExtra(GameActivity.KEY_PLAYER_2, player2)
            }

            startActivity(intent)
        }

        binding.player2.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                binding.player2.clearFocus()
                val inputMethodManager = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                inputMethodManager.hideSoftInputFromWindow(binding.player2.windowToken, 0)
                true
            } else false
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(KEY_PLAYER_1, binding.player1.text.toString())
        outState.putString(KEY_PLAYER_2, binding.player2.text.toString())
    }

    companion object {
        private const val KEY_PLAYER_1 = "player1"
        private const val KEY_PLAYER_2 = "player2"
    }
}