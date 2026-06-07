package com.example.choramarreco

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isGone
import com.example.choramarreco.utils.showGameDialog
import com.example.choramarreco.databinding.ActivityHistoryBinding

class HistoryActivity : AppCompatActivity() {
    private lateinit var binding: ActivityHistoryBinding
    private lateinit var prefs: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityHistoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        prefs = getSharedPreferences(GameActivity.PREFS_NAME, MODE_PRIVATE)

        renderHistory()

        binding.btnStartMatch.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            }
            startActivity(intent)
        }

        binding.btnClearHistory.setOnClickListener {
            showClearHistoryDialog()
        }
    }

    private fun renderHistory() {
        val rankedPlayers = getRankedPlayers(prefs)

        showTopPlayers(rankedPlayers)
        showRemainingPlayers(rankedPlayers)
    }

    private fun showClearHistoryDialog() {
        showGameDialog(
            title = getString(R.string.dialog_clear_history_title),
            message = getString(R.string.dialog_clear_history_message),
            positiveText = getString(R.string.dialog_yes),
            negativeText = getString(R.string.dialog_cancel),
            onPositiveClick = {
                clearHistory()
            }
        )
    }

    private fun clearHistory() {
        prefs.edit().clear().apply()
        renderHistory()

        Toast.makeText(
            this,
            "Histórico reiniciado com sucesso!",
            Toast.LENGTH_SHORT
        ).show()
    }

    private fun getRankedPlayers(prefs: SharedPreferences): List<Pair<String, Int>> {
        val players = getRegisteredPlayers(prefs)

        return players
            .map { name ->
                name to prefs.getInt(getWinsKey(name), 0)
            }
            .sortedByDescending { (_, wins) -> wins }
    }

    private fun showTopPlayers(rankedPlayers: List<Pair<String, Int>>) {
        val champion = rankedPlayers.getOrNull(0)
        val contender = rankedPlayers.getOrNull(1)

        binding.tvPlayer1Name.text = champion?.first ?: "-"
        binding.tvWins1.text = champion?.second?.toString() ?: "0"

        binding.tvPlayer2Name.text = contender?.first ?: "-"
        binding.tvWins2.text = contender?.second?.toString() ?: "0"
    }

    private fun showRemainingPlayers(rankedPlayers: List<Pair<String, Int>>) {
        val remainingPlayers = rankedPlayers.drop(2)

        binding.listRank.isGone = remainingPlayers.isEmpty()
        binding.layoutPlayersList.removeAllViews()

        remainingPlayers.forEachIndexed { index, (name, wins) ->
            val itemView = layoutInflater.inflate(
                R.layout.item_player_history,
                binding.layoutPlayersList,
                false
            )

            itemView.findViewById<TextView>(R.id.tvRank).text = getString(R.string.format_rank, index + 3)
            itemView.findViewById<TextView>(R.id.tvPlayerName).text = name
            itemView.findViewById<TextView>(R.id.tvPlayerWins).text = getString(R.string.format_wins, wins)

            binding.layoutPlayersList.addView(itemView)
        }
    }

    private fun getWinsKey(playerName: String): String {
        return "wins_${playerName.trim()}"
    }

    private fun getRegisteredPlayers(prefs: SharedPreferences): Set<String> {
        return try {
            prefs.getStringSet(KEY_REGISTERED_PLAYERS, emptySet()).orEmpty()
        } catch (_: ClassCastException) {
            val oldValue = prefs.getString(KEY_REGISTERED_PLAYERS, "").orEmpty()

            val migratedPlayers = oldValue
                .split(",")
                .map { it.trim() }
                .filter { it.isNotBlank() }
                .toSet()

            prefs.edit()
                .remove(KEY_REGISTERED_PLAYERS)
                .putStringSet(KEY_REGISTERED_PLAYERS, migratedPlayers)
                .apply()

            migratedPlayers
        }
    }

    companion object {
        const val KEY_PLAYER_1 = "player1"
        const val KEY_PLAYER_2 = "player2"
        const val KEY_REGISTERED_PLAYERS = "registered_players"
    }
}