package com.example.reminds.ui.activity.focus

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.findNavController
import com.example.reminds.R
import com.example.reminds.databinding.ActivityFocusTodoBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FocusTodoActivity : AppCompatActivity() {
    private lateinit var binding: ActivityFocusTodoBinding
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFocusTodoBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        setupNavController()
    }

    private fun setupNavController() {
        navController = findNavController(R.id.nav_host_focus_fragment)
    }

}