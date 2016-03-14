package net.sigmabeta.chipbox.ui.player

import android.content.Context
import android.content.Intent
import android.view.View
import android.widget.FrameLayout
import kotlinx.android.synthetic.main.activity_player.*
import net.sigmabeta.chipbox.ChipboxApplication
import net.sigmabeta.chipbox.R
import net.sigmabeta.chipbox.ui.ActivityPresenter
import net.sigmabeta.chipbox.ui.BaseActivity
import net.sigmabeta.chipbox.ui.FragmentContainer
import javax.inject.Inject

class PlayerActivity : BaseActivity(), PlayerActivityView, FragmentContainer {
    lateinit var presenter: PlayerActivityPresenter
        @Inject set

    /**
     * PlayerView
     */

    override fun showPlayerFragment() {
        var fragment = PlayerFragment.newInstance()

        supportFragmentManager.beginTransaction()
                .add(R.id.frame_fragment, fragment, PlayerFragment.FRAGMENT_TAG)
                .commit()
    }

    /**
     * FragmentContainer
     */

    override fun setTitle(title: String) {
        setTitle(title)
    }

    /**
     * BaseActivity
     */

    override fun inject() {
        ChipboxApplication.appComponent.inject(this)
    }

    override fun getPresenter(): ActivityPresenter {
        return presenter
    }

    override fun configureViews() {
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_STABLE or
                View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_player
    }

    override fun getContentLayout(): FrameLayout {
        return frame_fragment
    }

    companion object {
        fun launch(context: Context) {
            val launcher = Intent(context, PlayerActivity::class.java)

            context.startActivity(launcher)
        }

        fun getLauncher(context: Context): Intent {
            return Intent(context, PlayerActivity::class.java)
        }
    }
}