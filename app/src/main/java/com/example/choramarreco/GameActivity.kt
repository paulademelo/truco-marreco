package com.example.choramarreco

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.choramarreco.utils.showGameDialog
import com.example.choramarreco.databinding.ActivityGameBinding
import com.example.choramarreco.utils.animateScoreChange
import com.example.choramarreco.utils.setAnimatedClickListener

class GameActivity : AppCompatActivity() {
    private lateinit var binding: ActivityGameBinding
    private var isDialogShowing = false
    private var score1 = 0
    private var score2 = 0
    private val scoreHistory1 = mutableListOf<Int>()
    private val scoreHistory2 = mutableListOf<Int>()

    private lateinit var player1: String
    private lateinit var player2: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityGameBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        player1 = intent.getStringExtra(KEY_PLAYER_1)
            ?.trim()
            ?.ifBlank { getString(R.string.default_player_1) }
            ?: getString(R.string.default_player_1)

        player2 = intent.getStringExtra(KEY_PLAYER_2)
            ?.trim()
            ?.ifBlank { getString(R.string.default_player_2) }
            ?: getString(R.string.default_player_2)

        binding.cardTeam1.tvTeamName.text = player1
        binding.cardTeam2.tvTeamName.text = player2

        setupChipButtons()

        binding.btnReset.setAnimatedClickListener {
            showResetDialog()
        }

        binding.btnFinishGame.setAnimatedClickListener {
            val intent = Intent(this, HistoryActivity::class.java).apply {
                putExtra(HistoryActivity.KEY_PLAYER_1, player1)
                putExtra(HistoryActivity.KEY_PLAYER_2, player2)
            }
            startActivity(intent)
        }
    }

    private fun saveWinToPrefs(winnerName: String) {
        val prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE)

        val registeredPlayers = getRegisteredPlayers(prefs)

        registeredPlayers.add(player1.trim())
        registeredPlayers.add(player2.trim())

        val winnerKey = getWinsKey(winnerName)
        val currentWins = prefs.getInt(winnerKey, 0)

        prefs.edit()
            .putStringSet(HistoryActivity.KEY_REGISTERED_PLAYERS, registeredPlayers)
            .putInt(winnerKey, currentWins + 1)
            .apply()
    }

    private fun getWinsKey(playerName: String): String {
        return "wins_${playerName.trim()}"
    }


    private fun setupChipButtons() {
        setupTeamButtons(
            team = 1,
            btnPlus1 = binding.cardTeam1.btnPlus1,
            btnPlus3 = binding.cardTeam1.btnPlus3,
            btnPlus6 = binding.cardTeam1.btnPlus6,
            btnPlus9 = binding.cardTeam1.btnPlus9,
            btnPlus12 = binding.cardTeam1.btnPlus12,
            btnUndo = binding.cardTeam1.btnUndo,
        )

        setupTeamButtons(
            team = 2,
            btnPlus1 = binding.cardTeam2.btnPlus1,
            btnPlus3 = binding.cardTeam2.btnPlus3,
            btnPlus6 = binding.cardTeam2.btnPlus6,
            btnPlus9 = binding.cardTeam2.btnPlus9,
            btnPlus12 = binding.cardTeam2.btnPlus12,
            btnUndo = binding.cardTeam2.btnUndo,
        )
    }

    private fun setupTeamButtons(
        team: Int,
        btnPlus1: View,
        btnPlus3: View,
        btnPlus6: View,
        btnPlus9: View,
        btnPlus12: View,
        btnUndo: View,
    ) {
        btnPlus1.setAnimatedClickListener { addScore(team, 1) }
        btnPlus3.setAnimatedClickListener { addScore(team, 3) }
        btnPlus6.setAnimatedClickListener { addScore(team, 6) }
        btnPlus9.setAnimatedClickListener { addScore(team, 9) }
        btnPlus12.setAnimatedClickListener { addScore(team, 12) }
        btnUndo.setAnimatedClickListener { showUndoDialog(team) }
    }

    private fun addScore(player: Int, points: Int) {
        if (isDialogShowing) return

        val newScore = if (player == 1) {
            scoreHistory1.add(score1)
            score1 += points
            binding.cardTeam1.tvScore.text = score1.toString()
            binding.cardTeam1.tvScore.animateScoreChange()
            score1
        } else {
            scoreHistory2.add(score2)
            score2 += points
            binding.cardTeam2.tvScore.text = score2.toString()
            binding.cardTeam2.tvScore.animateScoreChange()
            score2
        }

        if (newScore >= 12) {
            val winner = if (player == 1) player1 else player2
            showWinnerDialog(winner)
        }
    }

    private fun showResetDialog() {
        showGameDialog(
            title = getString(R.string.dialog_reset_title),
            message = getString(R.string.dialog_reset_message),
            positiveText = getString(R.string.dialog_yes),
            negativeText = getString(R.string.dialog_no),
            onPositiveClick = {
                finish()
            }
        )
    }

    private fun showWinnerDialog(winner: String) {
        isDialogShowing = true

        saveWinToPrefs(winner)

        showGameDialog(
            title = getString(R.string.dialog_winner_title, winner),
            message = getString(R.string.dialog_winner_message, winner),
            positiveText = getString(R.string.btn_play_again),
            neutralText = getString(R.string.btn_view_history),
            cancelable = false,
            onPositiveClick = {
                isDialogShowing = false
                resetScores()
            },
            onNeutralClick = {
                isDialogShowing = false
                resetScores()
                val intent = Intent(this, HistoryActivity::class.java)
                startActivity(intent)
            }
        )
    }

    private fun showUndoDialog(player: Int) {
        val name = if (player == 1) player1 else player2
        val hasHistory = if (player == 1) {
            scoreHistory1.isNotEmpty()
        } else {
            scoreHistory2.isNotEmpty()
        }

        if (!hasHistory) {
            Toast.makeText(
                this,
                "Não há pontuação para desfazer.",
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        showGameDialog(
            title = getString(R.string.dialog_undo_title),
            message = getString(R.string.dialog_undo_message, name),
            positiveText = getString(R.string.dialog_yes),
            negativeText = getString(R.string.dialog_no),
            onPositiveClick = {
                if (player == 1) {
                    score1 = scoreHistory1.removeLast()
                    binding.cardTeam1.tvScore.text = score1.toString()
                    binding.cardTeam1.tvScore.animateScoreChange()
                } else {
                    score2 = scoreHistory2.removeLast()
                    binding.cardTeam2.tvScore.text = score2.toString()
                    binding.cardTeam2.tvScore.animateScoreChange()
                }
            }
        )
    }

    private fun resetScores() {
        score1 = 0
        score2 = 0
        scoreHistory1.clear()
        scoreHistory2.clear()
        binding.cardTeam1.tvScore.text = "0"
        binding.cardTeam2.tvScore.text = "0"
    }

    private fun getRegisteredPlayers(prefs: SharedPreferences): MutableSet<String> {
        return try {
            prefs.getStringSet(HistoryActivity.KEY_REGISTERED_PLAYERS, emptySet())
                .orEmpty()
                .toMutableSet()
        } catch (_: ClassCastException) {
            val oldValue = prefs.getString(HistoryActivity.KEY_REGISTERED_PLAYERS, "").orEmpty()

            val migratedPlayers = oldValue
                .split(",")
                .map { it.trim() }
                .filter { it.isNotBlank() }
                .toMutableSet()

            prefs.edit()
                .remove(HistoryActivity.KEY_REGISTERED_PLAYERS)
                .putStringSet(HistoryActivity.KEY_REGISTERED_PLAYERS, migratedPlayers)
                .apply()

            migratedPlayers
        }
    }

    override fun onResume() {
        super.onResume()
        isDialogShowing = false
    }

    companion object {
        const val KEY_PLAYER_1 = "player1"
        const val KEY_PLAYER_2 = "player2"
        const val PREFS_NAME = "chora_marreco_prefs"
    }
}