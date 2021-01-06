package com.example.reminds.ui.activity

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.NavigationUI
import com.example.reminds.R
import com.example.reminds.ui.sharedviewmodel.MainActivityViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    val viewModel: MainActivityViewModel by viewModels()
    private lateinit var navController: NavController
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(findViewById(R.id.toolbar))
        /*show back press button*/
        navController = findNavController(R.id.nav_host_fragment)
        NavigationUI.setupActionBarWithNavController(this, navController)
        /*  val callback = object : ActionMode.Callback {

              override fun onCreateActionMode(mode: ActionMode?, menu: Menu?): Boolean {
                  menuInflater.inflate(R.menu.top_app_bar, menu)
                  return true
              }

              override fun onPrepareActionMode(mode: ActionMode?, menu: Menu?): Boolean {
                  return false
              }

              override fun onActionItemClicked(mode: ActionMode?, item: MenuItem?): Boolean {
                  return when (item?.itemId) {
                     *//* R.id.share -> {
                        // Handle share icon press
                        true
                    }
                    R.id.delete -> {
                        // Handle delete icon press
                        true
                    }
                    R.id.more -> {
                        // Handle more item (inside overflow menu) press
                        true
                    }*//*
                    else -> false
                }
            }

            override fun onDestroyActionMode(mode: ActionMode?) {
            }
        }

        val actionMode = startSupportActionMode(callback)
        actionMode?.title = "1 selected"*/
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.favorite -> true
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        return onNavigateUp()
    }
}