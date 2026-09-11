package com.gotobootcamp.games.viewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.gotobootcamp.games.data.GameRepository
import com.gotobootcamp.games.model.Game

class GameViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = GameRepository(application.applicationContext)

    fun getGames(): List<Game> {
        return repository.getGames()
    }
}
