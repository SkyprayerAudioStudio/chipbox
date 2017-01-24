package net.sigmabeta.chipbox.ui.games

import android.os.Bundle
import net.sigmabeta.chipbox.R
import net.sigmabeta.chipbox.dagger.scope.ActivityScoped
import net.sigmabeta.chipbox.model.domain.Game
import net.sigmabeta.chipbox.model.domain.Track
import net.sigmabeta.chipbox.model.repository.Repository
import net.sigmabeta.chipbox.ui.BaseView
import net.sigmabeta.chipbox.ui.FragmentPresenter
import net.sigmabeta.chipbox.util.logError
import javax.inject.Inject

@ActivityScoped
class GameGridPresenter @Inject constructor(val repository: Repository) : FragmentPresenter() {
    var view: GameListView? = null

    var platform = Track.PLATFORM_ALL

    var games: List<Game>? = null

    fun onItemClick(position: Int) {
        val id = games?.get(position)?.id ?: return
        view?.launchGameActivity(id)
    }

    fun refresh(arguments: Bundle) = setupHelper(arguments)

    /**
     * FragmentPresenter
     */

    override fun setup(arguments: Bundle?) {
        setupHelper(arguments)
    }

    override fun onReCreate(arguments: Bundle?, savedInstanceState: Bundle) {
        logError("Recreate")

        if (games == null) {
            setupHelper(arguments)
        }
    }

    override fun teardown() {
        games = null
        platform = Track.PLATFORM_UNDEFINED
    }

    override fun updateViewState() {
        games?.let {
            if (it.size > 0) {
                showContent(it)
            } else {
                showEmptyState()
            }
        } ?: let {
            view?.showLoadingSpinner()
        }

        val titleResource = when (platform) {
            Track.PLATFORM_32X -> R.string.platform_name_32x
            Track.PLATFORM_GAMEBOY -> R.string.platform_name_gameboy
            Track.PLATFORM_GENESIS -> R.string.platform_name_genesis
            Track.PLATFORM_NES -> R.string.platform_name_nes
            Track.PLATFORM_SNES -> R.string.platform_name_snes
            else -> -1
        }

        if (titleResource != -1) {
            view?.setActivityTitle(titleResource)
        }

        view?.clearClickedViewHolder()
    }

    override fun onClick(id: Int) {
        when (id) {
            R.id.button_empty_state -> view?.showFilesScreen()
        }
    }

    override fun getView(): BaseView? = view

    override fun setView(view: BaseView) {
        if (view is GameListView) this.view = view
    }

    override fun clearView() {
        view = null
    }

    private fun setupHelper(arguments: Bundle?) {
        platform = arguments?.getLong(GameGridFragment.ARGUMENT_PLATFORM_INDEX) ?: Track.PLATFORM_UNDEFINED

        view?.showLoadingSpinner()
        view?.hideEmptyState()

        val request = if (platform == Track.PLATFORM_ALL) {
            repository.getGames()
        } else {
            repository.getGamesForPlatform(platform)
        }
        val subscription = request
                .subscribe(
                        {
                            games = it

                            if (it.isNotEmpty()) {
                                showContent(it)
                            } else {
                                showEmptyState()
                            }
                        },
                        {
                            showEmptyState()
                            view?.showErrorSnackbar("Error: ${it.message}", null, null)
                        }
                )

        subscriptions.add(subscription)
    }

    private fun showContent(games: List<Game>) {
        view?.setGames(games)
        view?.hideLoadingSpinner()
        view?.hideEmptyState()
        view?.showContent()
    }

    private fun showEmptyState() {
        view?.hideLoadingSpinner()
        view?.hideContent()
        view?.showEmptyState()
    }
}