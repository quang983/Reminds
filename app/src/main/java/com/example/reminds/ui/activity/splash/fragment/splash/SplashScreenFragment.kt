package com.example.reminds.ui.activity.splash.fragment.splash

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.View
import com.example.framework.local.cache.CacheImpl
import com.example.reminds.common.BaseFragment
import com.example.reminds.common.Constants.ONBOARDING_SHOW
import com.example.reminds.databinding.FragmentSplashBinding
import com.example.reminds.ui.activity.MainActivity
import com.example.reminds.utils.finish
import com.example.reminds.utils.navigate

class SplashScreenFragment : BaseFragment<FragmentSplashBinding>() {
    override fun getViewBinding(): FragmentSplashBinding {
        return FragmentSplashBinding.inflate(layoutInflater)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val shared = requireContext().getSharedPreferences(CacheImpl.SHARED_NAME, Context.MODE_PRIVATE)
        if (shared.getBoolean(ONBOARDING_SHOW, false)) {
            Handler().postDelayed({
                startActivity(Intent(requireActivity(), MainActivity::class.java))
                finish()
            }, 500)
        } else {
            Handler().postDelayed({
                shared.edit().putBoolean(ONBOARDING_SHOW, true).apply()
                navigate(SplashScreenFragmentDirections.actionSplashFragmentToOnboardingDialogFragment())
            }, 1000)
        }
    }
}