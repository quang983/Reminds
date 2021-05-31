package com.example.reminds.ui.fragment.tabsetting

import android.os.Bundle
import android.view.View
import androidx.core.content.res.ResourcesCompat
import androidx.hilt.navigation.fragment.hiltNavGraphViewModels
import com.example.reminds.R
import com.example.reminds.common.BaseFragment
import com.example.reminds.databinding.FragmentSettingTabBinding
import com.example.reminds.ui.fragment.upcoming.UpcomingViewModel
import com.example.reminds.utils.setOnClickListenerBlock
import com.google.android.play.core.review.ReviewManagerFactory
import dagger.hilt.android.AndroidEntryPoint
import www.sanju.motiontoast.MotionToast

@AndroidEntryPoint
class SettingTabFragment : BaseFragment<FragmentSettingTabBinding>() {

    override fun getViewBinding(): FragmentSettingTabBinding {
        return FragmentSettingTabBinding.inflate(layoutInflater)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initLayout()
    }

    private fun initLayout() {
        mBinding.tvPermission.setOnClickListenerBlock {
            showRatingApp()
        }
    }


    private fun showRatingApp() {
        val manager = ReviewManagerFactory.create(requireActivity())
        val request = manager.requestReviewFlow()
        request.addOnCompleteListener { request ->
            if (request.isSuccessful) {
                // We got the ReviewInfo object
                val reviewInfo = request.result
                val flow = manager.launchReviewFlow(requireActivity(), reviewInfo)
                flow.addOnCompleteListener {
                    MotionToast.createToast(
                        requireActivity(),
                        getString(R.string.notify_title),
                        resources.getString(R.string.success_review_app_done),
                        MotionToast.TOAST_SUCCESS,
                        MotionToast.GRAVITY_BOTTOM,
                        MotionToast.SHORT_DURATION,
                        ResourcesCompat.getFont(requireActivity(), R.font.roboto_medium))
                }
            } else {
                MotionToast.createToast(
                    requireActivity(),
                    getString(R.string.notify_title),
                    resources.getString(R.string.success_review_app),
                    MotionToast.TOAST_INFO,
                    MotionToast.GRAVITY_BOTTOM,
                    MotionToast.SHORT_DURATION,
                    ResourcesCompat.getFont(requireActivity(), R.font.roboto_medium)
                )
                // There was some problem, continue regardless of the result.
            }
        }
    }
}