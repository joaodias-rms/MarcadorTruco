package com.project.marcadortruco

import android.content.Intent
import android.os.Bundle
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
            GameState.zerarTudo(
                getString(R.string.default_player_1),
                getString(R.string.default_player_2)
            )
            atualizarTela()
            Toast.makeText(this, R.string.history_reset_toast, Toast.LENGTH_SHORT).show()
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
                GameState.confirmarVitoria(jogador)
                atualizarTela()
            }
            .setNegativeButton(R.string.undo_last_play_button) { _, _ ->
                GameState.desfazerUltimaJogada()
                atualizarTela()
                Toast.makeText(this, R.string.undo_score_toast, Toast.LENGTH_SHORT).show()
            }
            .setCancelable(false)
            .show()
    }

    private fun atualizarTela() {
        binding.textNomeJogador1.text = GameState.nomeJogador1
        binding.textNomeJogador2.text = GameState.nomeJogador2
        binding.textPontosJogador1.text = GameState.pontosJogador1.toString()
        binding.textPontosJogador2.text = GameState.pontosJogador2.toString()
    }
}
