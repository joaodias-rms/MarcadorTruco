package com.project.marcadortruco

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.animation.OvershootInterpolator
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.project.marcadortruco.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        GameState.inicializarNomes(
            getString(R.string.default_player_1),
            getString(R.string.default_player_2)
        )
        configurarBotoesPontuacao()

        binding.buttonHistorico.setOnClickListener {
            startActivity(Intent(this, HistoryActivity::class.java))
        }

        binding.buttonInformarNomes.setOnClickListener {
            startActivity(Intent(this, NamesActivity::class.java))
        }

        binding.buttonZerarHistorico.setOnClickListener {
            confirmarZerarTudo()
        }

        binding.buttonDesfazerJogada.setOnClickListener {
            if (GameState.desfazerUltimaJogada()) {
                atualizarTela()
                Toast.makeText(this, R.string.undo_score_toast, Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, R.string.no_score_to_undo_toast, Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        atualizarTela()
    }

    private fun configurarBotoesPontuacao() {
        binding.buttonJ1Mais1.setOnClickListener { somarPontos(1, 1) }
        binding.buttonJ1Mais3.setOnClickListener { somarPontos(1, 3) }
        binding.buttonJ1Mais6.setOnClickListener { somarPontos(1, 6) }
        binding.buttonJ1Mais9.setOnClickListener { somarPontos(1, 9) }
        binding.buttonJ1Mais12.setOnClickListener { somarPontos(1, 12) }

        binding.buttonJ2Mais1.setOnClickListener { somarPontos(2, 1) }
        binding.buttonJ2Mais3.setOnClickListener { somarPontos(2, 3) }
        binding.buttonJ2Mais6.setOnClickListener { somarPontos(2, 6) }
        binding.buttonJ2Mais9.setOnClickListener { somarPontos(2, 9) }
        binding.buttonJ2Mais12.setOnClickListener { somarPontos(2, 12) }
    }

    private fun somarPontos(jogador: Int, pontos: Int) {
        GameState.somarPontos(jogador, pontos)
        atualizarTela()
        animarPlacar(if (jogador == 1) binding.textPontosJogador1 else binding.textPontosJogador2)

        if (jogador == 1 && GameState.pontosJogador1 >= 12) {
            mostrarAlertaVitoria(GameState.nomeJogador1, 1)
            return
        }

        if (jogador == 2 && GameState.pontosJogador2 >= 12) {
            mostrarAlertaVitoria(GameState.nomeJogador2, 2)
        }
    }

    private fun mostrarAlertaVitoria(nomeJogador: String, jogador: Int) {
        AlertDialog.Builder(this)
            .setTitle(R.string.end_match_title)
            .setMessage(
                getString(
                    R.string.winner_message,
                    nomeJogador,
                    GameState.nomeJogador1,
                    GameState.pontosJogador1,
                    GameState.pontosJogador2,
                    GameState.nomeJogador2
                )
            )
            .setPositiveButton(R.string.confirm_win_button) { _, _ ->
                val liderAnterior = obterLiderHistorico()
                GameState.confirmarVitoria(jogador)
                atualizarTela()
                mostrarToastMudancaHistorico(liderAnterior)
            }
            .setNegativeButton(R.string.undo_last_play_button) { _, _ ->
                GameState.desfazerUltimaJogada()
                atualizarTela()
                Toast.makeText(this, R.string.undo_score_toast, Toast.LENGTH_SHORT).show()
            }
            .setCancelable(false)
            .show()
    }

    private fun confirmarZerarTudo() {
        AlertDialog.Builder(this)
            .setTitle(R.string.reset_confirm_title)
            .setMessage(R.string.reset_confirm_message)
            .setPositiveButton(R.string.reset_confirm_button) { _, _ ->
                GameState.zerarTudo(
                    getString(R.string.default_player_1),
                    getString(R.string.default_player_2)
                )
                atualizarTela()
                Toast.makeText(this, R.string.history_reset_toast, Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton(R.string.cancel_button, null)
            .show()
    }

    private fun atualizarTela() {
        binding.textNomeJogador1.text = GameState.nomeJogador1
        binding.textNomeJogador2.text = GameState.nomeJogador2
        binding.textPontosJogador1.text = GameState.pontosJogador1.toString()
        binding.textPontosJogador2.text = GameState.pontosJogador2.toString()
        binding.textProgressoJogador1.text =
            getString(R.string.score_progress_format, GameState.pontosJogador1)
        binding.textProgressoJogador2.text =
            getString(R.string.score_progress_format, GameState.pontosJogador2)
        binding.progressJogador1.setProgress(GameState.pontosJogador1, true)
        binding.progressJogador2.setProgress(GameState.pontosJogador2, true)
        atualizarHistoricoVisual()
    }

    private fun animarPlacar(textView: TextView) {
        textView.animate().cancel()
        textView.scaleX = 1f
        textView.scaleY = 1f
        textView.animate()
            .scaleX(1.16f)
            .scaleY(1.16f)
            .setDuration(120)
            .setInterpolator(OvershootInterpolator())
            .withEndAction {
                textView.animate()
                    .scaleX(1f)
                    .scaleY(1f)
                    .setDuration(120)
                    .start()
            }
            .start()
    }

    private fun atualizarHistoricoVisual() {
        val totalPartidas = GameState.vitoriasJogador1 + GameState.vitoriasJogador2
        val winrateJogador1 = calcularPorcentagem(GameState.vitoriasJogador1, totalPartidas)
        val winrateJogador2 = calcularPorcentagem(GameState.vitoriasJogador2, totalPartidas)
        val derrotasJogador1 = calcularPorcentagem(GameState.vitoriasJogador2, totalPartidas)
        val derrotasJogador2 = calcularPorcentagem(GameState.vitoriasJogador1, totalPartidas)

        binding.textWinrateJogador1.text =
            getString(R.string.winrate_format, winrateJogador1, derrotasJogador1)
        binding.textWinrateJogador2.text =
            getString(R.string.winrate_format, winrateJogador2, derrotasJogador2)

        binding.imageCoroaJogador1.visibility =
            if (GameState.vitoriasJogador1 > GameState.vitoriasJogador2) View.VISIBLE else View.GONE
        binding.imageCoroaJogador2.visibility =
            if (GameState.vitoriasJogador2 > GameState.vitoriasJogador1) View.VISIBLE else View.GONE
    }

    private fun calcularPorcentagem(valor: Int, total: Int): Int {
        if (total == 0) return 0
        return ((valor.toFloat() / total) * 100).toInt()
    }

    private fun mostrarToastMudancaHistorico(liderAnterior: Int) {
        val liderAtual = obterLiderHistorico()
        if (liderAtual == liderAnterior) return

        when (liderAtual) {
            1 -> Toast.makeText(
                this,
                getString(R.string.leader_toast, GameState.nomeJogador1),
                Toast.LENGTH_LONG
            ).show()

            2 -> Toast.makeText(
                this,
                getString(R.string.leader_toast, GameState.nomeJogador2),
                Toast.LENGTH_LONG
            ).show()

            0 -> {
                val totalPartidas = GameState.vitoriasJogador1 + GameState.vitoriasJogador2
                if (totalPartidas > 0) {
                    Toast.makeText(this, R.string.wins_tied_toast, Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun obterLiderHistorico(): Int {
        return when {
            GameState.vitoriasJogador1 > GameState.vitoriasJogador2 -> 1
            GameState.vitoriasJogador2 > GameState.vitoriasJogador1 -> 2
            else -> 0
        }
    }
}
