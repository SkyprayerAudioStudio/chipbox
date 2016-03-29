package net.sigmabeta.chipbox.ui.playlist

import android.app.Activity
import android.app.ActivityOptions
import android.content.Intent
import android.support.v4.content.ContextCompat
import android.view.View
import kotlinx.android.synthetic.main.activity_playlist.*
import net.sigmabeta.chipbox.BuildConfig
import net.sigmabeta.chipbox.ChipboxApplication
import net.sigmabeta.chipbox.R
import net.sigmabeta.chipbox.ui.ActivityPresenter
import net.sigmabeta.chipbox.ui.BaseActivity
import net.sigmabeta.chipbox.ui.player.PlayerFragment
import net.sigmabeta.chipbox.util.convertDpToPx
import javax.inject.Inject

class PlaylistActivity : BaseActivity(), PlaylistActivityView {
    lateinit var presenter: PlaylistActivityPresenter
        @Inject set

    /**
     * PlaylistView
     */

    override fun showPlaylistFragment() {
        var fragment = PlaylistFragment.newInstance()

        supportFragmentManager.beginTransaction()
                .add(R.id.frame_fragment, fragment, PlayerFragment.FRAGMENT_TAG)
                .commit()
    }

    /**
     * BaseActivity
     */

    override fun inject() = ChipboxApplication.appComponent.inject(this)

    override fun getPresenter(): ActivityPresenter = presenter

    override fun configureViews() {
        layout_player_controls.elevation = convertDpToPx(6.0f, this)
        val color = ContextCompat.getColor(this, android.R.color.white)
        layout_player_controls.setBackgroundColor(color)
    }

    override fun getLayoutId() = R.layout.activity_playlist

    override fun getContentLayout() = frame_fragment

    override fun getSharedImage() = layout_player_controls

    override fun shouldDelayTransitionForFragment() = false

    companion object {
        val ACTIVITY_TAG = "${BuildConfig.APPLICATION_ID}.playlist"

        fun launch(activity: Activity, sharedView: View) {
            val launcher = Intent(activity, PlaylistActivity::class.java)

            val options = ActivityOptions.makeSceneTransitionAnimation(activity, sharedView, "layout_player_controls")

            activity.startActivity(launcher, options.toBundle())
        }
    }
}