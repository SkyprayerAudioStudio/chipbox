package net.sigmabeta.chipbox.ui.onboarding

import android.os.Bundle
import net.sigmabeta.chipbox.ui.ActivityPresenter
import net.sigmabeta.chipbox.ui.BaseView
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class OnboardingPresenter @Inject constructor() : ActivityPresenter() {
    var view: OnboardingView? = null

    /**
     * Public Methods
     */


    /**
     * ActivityPresenter
     */

    override fun onReenter() = Unit

    override fun onReCreate(arguments: Bundle?, savedInstanceState: Bundle) = Unit

    override fun onTempDestroy() = Unit

    /**
     * BasePresenter
     */

    override fun onClick(id: Int) = Unit

    override fun setup(arguments: Bundle?) {
        // TODO Check if user's already been onboarded.

        val pageId = arguments?.getInt(OnboardingActivity.ARGUMENT_PAGE_ID)

        when (pageId) {
            else -> view?.showTitlePage()
        }
    }

    override fun teardown() = Unit

    override fun updateViewState() = Unit

    override fun getView(): BaseView? = view

    override fun setView(view: BaseView) {
        if (view is OnboardingView) this.view = view
    }

    override fun clearView() {
        view = null
    }

    /**
     * Private Methods
     */
}