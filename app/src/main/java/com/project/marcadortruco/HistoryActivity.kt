package com.project.marcadortruco

import android.os.Bundle
import android.graphics.Typeface
import android.text.SpannableString
import android.text.Spanned
import android.text.style.StyleSpan
import android.view.View
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.project.marcadortruco.databinding.ActivityHistoryBinding

class HistoryActivity : AppCompatActivity() {
    private lateinit var binding: ActivityHistoryBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityHistoryBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.textHistoricoJogador1.text =
            getString(R.string.history_item_format, GameState.nomeJogador1, GameState.vitoriasJogador1)
        binding.textHistoricoJogador2.text =
            getString(R.string.history_item_format, GameState.nomeJogador2, GameState.vitoriasJogador2)

        preencherPartidas()

        binding.buttonExpandirPartidas.setOnClickListener {
            alternarPartidas()
        }

        binding.buttonVoltar.setOnClickListener {
            finish()
        }
    }

    private fun preencherPartidas() {
        binding.textSemPartidas.visibility =
            if (GameState.partidas.isEmpty()) View.VISIBLE else View.GONE

        GameState.partidas.forEachIndexed { index, partida ->
            val textView = TextView(this)
            textView.text = criarTextoPartida(index + 1, partida)
            textView.textSize = 18f
            textView.setTextColor(getColor(R.color.truco_text))
            textView.setPadding(0, 8, 0, 8)
            binding.layoutPartidas.addView(textView)
        }
    }

    private fun criarTextoPartida(numeroPartida: Int, partida: GameState.Partida): SpannableString {
        val texto = getString(
            R.string.match_score_format,
            numeroPartida,
            partida.pontosJogador1,
            partida.pontosJogador2
        )
        val placarVencedor = if (partida.jogadorVencedor == 1) {
            partida.pontosJogador1.toString()
        } else {
            partida.pontosJogador2.toString()
        }
        val inicio = if (partida.jogadorVencedor == 1) {
            texto.indexOf(placarVencedor)
        } else {
            texto.lastIndexOf(placarVencedor)
        }

        return SpannableString(texto).apply {
            setSpan(
                StyleSpan(Typeface.BOLD),
                inicio,
                inicio + placarVencedor.length,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )
        }
    }

    private fun alternarPartidas() {
        val deveMostrar = binding.layoutPartidas.visibility == View.GONE
        binding.layoutPartidas.visibility = if (deveMostrar) View.VISIBLE else View.GONE
        binding.buttonExpandirPartidas.setText(
            if (deveMostrar) {
                R.string.hide_match_scores_button
            } else {
                R.string.show_match_scores_button
            }
        )
    }
}
