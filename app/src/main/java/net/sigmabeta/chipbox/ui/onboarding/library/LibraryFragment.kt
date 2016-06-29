package net.sigmabeta.chipbox.ui.onboarding.library

import kotlinx.android.synthetic.main.fragment_library.*
import net.sigmabeta.chipbox.BuildConfig
import net.sigmabeta.chipbox.R
import net.sigmabeta.chipbox.dagger.scope.ActivityScoped
import net.sigmabeta.chipbox.ui.BaseFragment
import net.sigmabeta.chipbox.ui.FragmentPresenter
import net.sigmabeta.chipbox.ui.file.FilesActivity
import net.sigmabeta.chipbox.ui.onboarding.OnboardingView
import javax.inject.Inject

@ActivityScoped
class LibraryFragment : BaseFragment(), LibraryView {
    lateinit var presenter: LibraryPresenter
        @Inject set

    /**
     * LibraryView
     */

    override fun onNextClicked() {
        (activity as OnboardingView).showNextScreen()
    }

    override fun onSkipClicked() {
        (activity as OnboardingView).skip()
    }

    override fun onAddClicked() {
        FilesActivity.launch(activity)
    }

    override fun updateCurrentScreen() {
        (activity as OnboardingView).updateCurrentScreen(TAG)
    }

    /**
     * BaseFragment
     */

    override fun inject() {
        val container = activity
        if (container is OnboardingView) {
            container.getFragmentComponent().inject(this)
        }
    }

    override fun getPresenter(): FragmentPresenter = presenter

    override fun getLayoutId() = R.layout.fragment_library

    override fun getContentLayout() = layout_content

    override fun getSharedImage() = null

    override fun configureViews() {
        button_next.setOnClickListener(this)
        button_skip.setOnClickListener(this)
        button_add.setOnClickListener(this)
    }

    override fun getFragmentTag(): String = TAG

    companion object {
        val TAG = "${BuildConfig.APPLICATION_ID}.library"

        fun newInstance() = LibraryFragment()
    }
}