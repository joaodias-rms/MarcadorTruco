package com.project.marcadortruco

object GameState {
    data class Partida(
        val pontosJogador1: Int,
        val pontosJogador2: Int,
        val jogadorVencedor: Int
    )

    data class Jogada(
        val jogador: Int,
        val pontosAnteriores: Int
    )

    var nomeJogador1 = ""
    var nomeJogador2 = ""
    var pontosJogador1 = 0
    var pontosJogador2 = 0
    var vitoriasJogador1 = 0
    var vitoriasJogador2 = 0
    val partidas = mutableListOf<Partida>()
    val jogadas = mutableListOf<Jogada>()

    fun zerarPontuacao() {
        pontosJogador1 = 0
        pontosJogador2 = 0
        jogadas.clear()
    }

    fun somarPontos(jogador: Int, pontos: Int) {
        if (jogador == 1) {
            val pontosAnteriores = pontosJogador1
            pontosJogador1 = (pontosJogador1 + pontos).coerceAtMost(PONTOS_MAXIMOS)
            jogadas.add(Jogada(jogador, pontosAnteriores))
        } else {
            val pontosAnteriores = pontosJogador2
            pontosJogador2 = (pontosJogador2 + pontos).coerceAtMost(PONTOS_MAXIMOS)
            jogadas.add(Jogada(jogador, pontosAnteriores))
        }
    }

    fun desfazerUltimaJogada(): Boolean {
        val ultimaJogada = jogadas.removeLastOrNull() ?: return false

        if (ultimaJogada.jogador == 1) {
            pontosJogador1 = ultimaJogada.pontosAnteriores
        } else {
            pontosJogador2 = ultimaJogada.pontosAnteriores
        }

        return true
    }

    fun confirmarVitoria(jogador: Int) {
        if (jogador == 1) {
            vitoriasJogador1++
        } else {
            vitoriasJogador2++
        }

        partidas.add(
            Partida(
                pontosJogador1 = pontosJogador1,
                pontosJogador2 = pontosJogador2,
                jogadorVencedor = jogador
            )
        )
        zerarPontuacao()
    }

    fun inicializarNomes(nomePadraoJogador1: String, nomePadraoJogador2: String) {
        if (nomeJogador1.isEmpty()) {
            nomeJogador1 = nomePadraoJogador1
        }

        if (nomeJogador2.isEmpty()) {
            nomeJogador2 = nomePadraoJogador2
        }
    }

    fun zerarTudo(nomePadraoJogador1: String, nomePadraoJogador2: String) {
        nomeJogador1 = nomePadraoJogador1
        nomeJogador2 = nomePadraoJogador2
        pontosJogador1 = 0
        pontosJogador2 = 0
        vitoriasJogador1 = 0
        vitoriasJogador2 = 0
        partidas.clear()
        jogadas.clear()
    }

    private const val PONTOS_MAXIMOS = 12
}
