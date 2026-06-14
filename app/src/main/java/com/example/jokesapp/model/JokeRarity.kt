package com.example.jokesapp.model

enum class JokeRarity(
    val label: String,
    val emoji: String,
    val minCoins: Int,
    val maxCoins: Int,
) {
    COMMON(   "Common",    "⬜",  1,   3),
    RARE(     "Rare",      "🟦",  5,  10),
    EPIC(     "Epic",      "🟪", 15,  25),
    LEGENDARY("Legendary", "🌟", 50, 100),
}

fun rollRarity(): JokeRarity = when ((1..100).random()) {
    in 1..2   -> JokeRarity.LEGENDARY
    in 3..10  -> JokeRarity.EPIC
    in 11..30 -> JokeRarity.RARE
    else      -> JokeRarity.COMMON
}

fun rollRarityBoosted(): JokeRarity = when ((1..100).random()) {
    in 1..5   -> JokeRarity.LEGENDARY
    in 6..20  -> JokeRarity.EPIC
    in 21..50 -> JokeRarity.RARE
    else      -> JokeRarity.COMMON
}
