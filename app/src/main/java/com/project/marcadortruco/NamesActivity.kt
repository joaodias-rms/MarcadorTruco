package com.project.marcadortruco

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.project.marcadortruco.databinding.ActivityNamesBinding

class NamesActivity : AppCompatActivity() {
    private lateinit var binding: ActivityNamesBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityNamesBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.editNomeJogador1.setText(GameState.nomeJogador1)
        binding.editNomeJogador2.setText(GameState.nomeJogador2)

        binding.buttonConfirmarNomes.setOnClickListener {
            val novoNomeJogador1 = binding.editNomeJogador1.text.toString().trim()
            val novoNomeJogador2 = binding.editNomeJogador2.text.toString().trim()

            GameState.nomeJogador1 = novoNomeJogador1.ifEmpty { getString(R.string.default_player_1) }
            GameState.nomeJogador2 = novoNomeJogador2.ifEmpty { getString(R.string.default_player_2) }
            finish()
        }

        binding.buttonCancelar.setOnClickListener {
            finish()
        }
    }
}
