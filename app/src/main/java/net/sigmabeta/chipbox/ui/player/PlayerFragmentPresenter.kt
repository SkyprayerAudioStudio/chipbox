package net.sigmabeta.chipbox.ui.player

import android.media.session.PlaybackState
import android.os.Bundle
import net.sigmabeta.chipbox.backend.Player
import net.sigmabeta.chipbox.dagger.scope.ActivityScoped
import net.sigmabeta.chipbox.model.domain.Game
import net.sigmabeta.chipbox.model.domain.Track
import net.sigmabeta.chipbox.model.events.GameEvent
import net.sigmabeta.chipbox.model.events.PositionEvent
import net.sigmabeta.chipbox.model.events.StateEvent
import net.sigmabeta.chipbox.model.events.TrackEvent
import net.sigmabeta.chipbox.ui.BaseView
import net.sigmabeta.chipbox.ui.FragmentPresenter
import net.sigmabeta.chipbox.util.getTimeStringFromMillis
import net.sigmabeta.chipbox.util.logError
import net.sigmabeta.chipbox.util.logWarning
import rx.android.schedulers.AndroidSchedulers
import javax.inject.Inject

@ActivityScoped
class PlayerFragmentPresenter @Inject constructor(val player: Player) : FragmentPresenter() {
    var view: PlayerFragmentView? = null

    var game: Game? = null

    var track: Track? = null

    var seekbarTouched = false

    fun onFabClick() {
        view?.showPlaylist()
    }

    fun onSeekbarTouch() {
        seekbarTouched = true
    }

    fun onSeekbarRelease(progress: Int) {
        player.seek(progress)
        seekbarTouched = false
    }

    /**
     * FragmentPresenter
     */

    override fun onReCreate(arguments: Bundle?, savedInstanceState: Bundle) = Unit

    override fun setup(arguments: Bundle?) = Unit

    override fun teardown() {
        track = null
        seekbarTouched = false
    }

    override fun updateViewState() {
        updateHelper()

        val subscription = player.updater.asObservable()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    when (it) {
                        is TrackEvent -> displayTrack(it.track, true)
                        is PositionEvent -> displayPosition(it.millisPlayed)
                        is StateEvent -> displayState(it.state)
                        is GameEvent -> displayGame(it.game, false, true)
                        else -> logWarning("[PlayerFragmentPresenter] Unhandled ${it}")
                    }
                }

        subscriptions.add(subscription)
    }

    private fun updateHelper() {
        player.playingTrack?.let {
            displayTrack(it, false)
        } ?: let {
            logError("[PlayerFragmentPresenter] No track to display.")
        }

        player.playingGame?.let {
            displayGame(it, true, false)
        }

        displayState(player.state)

        displayPosition(player.playbackTimePosition)
    }

    override fun getView(): BaseView? = view

    override fun setView(view: BaseView) {
        if (view is PlayerFragmentView) this.view = view
    }

    override fun clearView() {
        view = null
    }

    private fun displayGame(game: Game?, force: Boolean, animate: Boolean) {
        if (force || this.game != game) {
            view?.setGameBoxArt(game?.artLocal, !force)
            view?.setGameTitle(game?.title ?: "Unknown", animate)
        }

        this.game = game
    }

    private fun displayTrack(track: Track, animate: Boolean) {
        this.track = track

        view?.setTrackTitle(track.title.orEmpty(), animate)
        view?.setArtist(track.artistText.orEmpty(), animate)
        view?.setTrackLength(getTimeStringFromMillis(track.trackLength ?: 0), animate)

        displayPosition(0)
    }

    private fun displayPosition(millisPlayed: Long) {
        if (!seekbarTouched) {
            val percentPlayed = 100 * millisPlayed / (track?.trackLength ?: 100)
            view?.setProgress(percentPlayed.toInt())
        }

        val timeString = getTimeStringFromMillis(millisPlayed)
        view?.setTimeElapsed(timeString)
    }

    private fun displayState(state: Int) {
        when (state) {
            PlaybackState.STATE_STOPPED -> {
                displayPosition(0)
            }
        }
    }
}