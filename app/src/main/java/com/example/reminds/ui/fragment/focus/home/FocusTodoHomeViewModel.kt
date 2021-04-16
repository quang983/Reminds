package com.example.reminds.ui.fragment.focus.home

import androidx.hilt.lifecycle.ViewModelInject
import com.example.reminds.common.BaseViewModel

enum class STATE {
    RESUME, PAUSE, FINISH, INDIE
}

class FocusTodoHomeViewModel @ViewModelInject constructor() : BaseViewModel() {
}