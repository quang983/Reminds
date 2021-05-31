package com.example.reminds.ui.fragment.focus.home

import androidx.hilt.lifecycle.ViewModelInject
import com.example.reminds.common.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

enum class STATE {
    RESUME, PAUSE, FINISH, INDIE
}

@HiltViewModel
class FocusTodoHomeViewModel @Inject constructor() : BaseViewModel() {
}