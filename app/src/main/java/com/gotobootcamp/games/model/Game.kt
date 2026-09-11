package com.gotobootcamp.games.model

data class Game(
    val id: String? = "",
    val title: String? = "",
    val thumbnail: String? = "",
    val shortDescription: String? = "",
    val gameUrl: String? = "",
    val genre: String? = "",
    val platform: String? = "",
    val publisher: String? = "",
    val developer: String? = "",
    val releaseDate: String? = "",
    val profileUrl: String? = ""
)
