package net.sigmabeta.chipbox.ui.main

import android.os.Bundle
import kotlinx.android.synthetic.main.activity_main.*
import net.sigmabeta.chipbox.R
import net.sigmabeta.chipbox.ui.ChromeActivity
import net.sigmabeta.chipbox.ui.FragmentContainer
import net.sigmabeta.chipbox.ui.ListFragment
import timber.log.Timber
import javax.inject.Inject

class MainActivity : ChromeActivity<MainPresenter, MainView>(), MainView, FragmentContainer {
    lateinit var presenter: MainPresenter
        @Inject set

    var pagerAdapter: MainTabPagerAdapter? = null

    override fun setTitle(title: String) = Unit

    /**
     * ChromeActivity
     */

    override fun getScrollingContentView() = pager_categories

    override fun isScrolledToBottom() = getFragment()?.isScrolledToBottom() ?: false

    override fun shouldShowBackButton() = false

    /**
     * BaseView
     */

    override fun showLoadingState() = Unit

    override fun showContent() = Unit

    override fun getPresenterImpl() = presenter

    /**
     * BaseActivity
     */

    override fun configureViews() {
        super.configureViews()
        setUpViewPagerTabs()
    }

    override fun getContentLayoutId() = R.layout.activity_main

    override fun getContentLayout() = coordinator_main

    override fun inject() = getTypedApplication().appComponent.inject(this)

    override fun getSharedImage() = null

    override fun shouldDelayTransitionForFragment() = false

    /**
     * Activity
     */

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.ThemeDrawerNoTransition)
        super.onCreate(savedInstanceState)
    }

    /**
     * Implementation Details
     */

    private fun getFragment(): ListFragment<*,*,*,*,*>? {
        val selectedPosition = pager_categories.currentItem

        Timber.v("Selected fragment position is %d", selectedPosition)
        val adapter = pagerAdapter

        return if (adapter != null) {
            adapter.fragments[selectedPosition]
        } else {
            null
        }
    }

    private fun setUpViewPagerTabs() {
        pagerAdapter = MainTabPagerAdapter(supportFragmentManager, this)
        pager_categories.adapter = pagerAdapter

        tabs_categories.setupWithViewPager(pager_categories)
    }
}

