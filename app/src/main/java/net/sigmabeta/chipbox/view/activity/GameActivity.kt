package net.sigmabeta.chipbox.view.activity

import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_game.*
import net.sigmabeta.chipbox.BuildConfig
import net.sigmabeta.chipbox.R
import net.sigmabeta.chipbox.dagger.injector.ActivityInjector
import net.sigmabeta.chipbox.model.objects.Game
import net.sigmabeta.chipbox.model.objects.Track
import net.sigmabeta.chipbox.presenter.GamePresenter
import net.sigmabeta.chipbox.view.adapter.SongListAdapter
import net.sigmabeta.chipbox.view.interfaces.GameView
import net.sigmabeta.chipbox.view.interfaces.SongListView
import javax.inject.Inject

class GameActivity : BaseActivity(), GameView, SongListView {
    lateinit var presenter: GamePresenter
        @Inject set

    var adapter: SongListAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_game)

        val launcher = intent

        val gameId = launcher.getLongExtra(ARGUMENT_GAME_ID, -1)

        val layoutManager = LinearLayoutManager(this)

        val adapter = SongListAdapter(this, this, false)
        this.adapter = adapter

        list_tracks.adapter = adapter
        list_tracks.layoutManager = layoutManager

        presenter.onCreate(gameId)
    }

    override fun onResume() {
        super.onResume()

        presenter.onResume()
    }

    override fun onPause() {
        super.onPause()

        presenter.onPause()
    }

    /**
     * GameView
     */

    override fun setCursor(cursor: Cursor) {
        adapter?.changeCursor(cursor)
    }

    override fun setGame(game: Game) {
        val imagePath = game.artLocal

        if (imagePath != null) {
            Picasso.with(this)
                    .load(imagePath)
                    .centerCrop()
                    .fit()
                    .error(R.drawable.img_album_art_blank)
                    .into(image_hero_boxart)
        } else {
            Picasso.with(this)
                    .load(R.drawable.img_album_art_blank)
                    .centerCrop()
                    .fit()
                    .error(R.drawable.img_album_art_blank)
                    .into(image_hero_boxart)
        }

        collapsing_toolbar.title = game.title
    }

    override fun getCursor(): Cursor? {
        return adapter?.cursor
    }

    override fun setPlayingTrack(track: Track) {
        adapter?.playingTrackId = track.id
    }

    override fun setPlaybackState(state: Int) {
        // no-op for now
    }

    /**
     * SongListView
     */

    override fun onItemClick(track: Track, position: Int) {
        presenter.onItemClick(track, position)
    }

    override fun launchPlayerActivity() {
        // No-op
    }

    override fun getImagePath(gameId: Long): String? {
        // No-op
        return null
    }

    override fun refreshList() {
        adapter?.notifyDataSetChanged()
    }

    /**
     * BaseActivity
     */

    override fun inject() {
        ActivityInjector.inject(this)
    }


    companion object {
        val ACTIVITY_TAG = "${BuildConfig.APPLICATION_ID}.game"

        val ARGUMENT_GAME_ID = "${ACTIVITY_TAG}.game_id"

        fun launch(context: Context, gameId: Long) {
            val launcher = Intent(context, GameActivity::class.java)

            launcher.putExtra(ARGUMENT_GAME_ID, gameId)

            context.startActivity(launcher)
        }
    }
}