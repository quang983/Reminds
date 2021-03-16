package com.example.reminds.ui.activity

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.*
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.NavigationUI
import com.example.common.base.model.AlarmNotificationEntity
import com.example.reminds.R
import com.example.reminds.databinding.ActivityMainBinding
import com.example.reminds.service.INSERT_OBJECT_TIMER_DATA
import com.example.reminds.service.NotificationService
import com.example.reminds.service.ScheduledWorker.Companion.TOPIC_ID_OPEN
import com.example.reminds.ui.sharedviewmodel.MainActivityViewModel
import com.example.reminds.utils.gone
import com.example.reminds.utils.postValue
import com.example.reminds.utils.visible
import com.google.android.gms.ads.*
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.google.android.gms.ads.rewarded.RewardItem
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback
import com.google.android.play.core.review.ReviewManagerFactory
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.AndroidEntryPoint
import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEvent
import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEventListener


@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    /** Flag indicating whether we have called bind on the service.  */
    private var bound: Boolean = false
    private var mService: Messenger? = null

    private var TAG = "logMain"
    private var mInterstitialAd: InterstitialAd? = null

    private var mRewardedAd: RewardedAd? = null

//    private lateinit var mBannerAd: AdView

    lateinit var intentService: Intent

    private lateinit var firebaseAnalytics: FirebaseAnalytics

    /** Messenger for communicating with the service.  */
    private var mMessenger: Messenger? = null
    val viewModel: MainActivityViewModel by viewModels()
    private lateinit var navController: NavController
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        firebaseAnalytics = Firebase.analytics

        setSupportActionBar(findViewById(R.id.toolbar))
        navController = findNavController(R.id.nav_host_fragment)
        NavigationUI.setupActionBarWithNavController(this, navController)

        onStartService()
//        createNotificationChannel()
        catchEventKeyboard()
        setObserver()
        createAdsMode()
        setOnListener()
//      showRatingApp()
    }

    override fun onResume() {
        super.onResume()
        onNewIntent(intent)
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        intent?.extras?.apply {
            Log.d("tasktask", "getTopic:${getLong(TOPIC_ID_OPEN)}")
            if (containsKey(TOPIC_ID_OPEN)) {
                viewModel.getTopic(getLong(TOPIC_ID_OPEN))
            }
        }
    }

    private fun setObserver() {
        viewModel.apply {
            notifyDataInsert.observe(this@MainActivity, {
                sendActionInsertAlert(it)
            })
            showAdsMobile.observe(this@MainActivity, {
                showAdsMobile()
            })
            navigateToFragmentFromIntent.observe(this@MainActivity, {
                val bundle = Bundle().apply {
                    putLong("idGroup", it.id)
                    putString("titleGroup", it.name)
                }
                navController.navigate(R.id.SecondFragment, bundle)
            })
        }
    }

    private fun onStartService() {
        super.onStart()
        intentService = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Intent(this, NotificationService::class.java).also { intent ->
                startForegroundService(intent)
                bindService(intent, mConnection, Context.BIND_AUTO_CREATE)
            }

        } else {
            Intent(this, NotificationService::class.java).also { intent ->
                startService(intent)
                bindService(intent, mConnection, Context.BIND_AUTO_CREATE)
            }

        }
    }

    override fun onDestroy() {
        super.onDestroy()
        stopService(intentService)
    }

    /**
     * Class for interacting with the main interface of the service.
     */
    private val mConnection = object : ServiceConnection {

        override fun onServiceConnected(className: ComponentName, service: IBinder) {
            mService = Messenger(service)
            bound = true
        }

        override fun onServiceDisconnected(className: ComponentName) {
            mService = null
            bound = false
        }
    }

    private fun sendActionInsertAlert(data: AlarmNotificationEntity) {
        if (!bound) return
        val msg: Message = Message.obtain(null, INSERT_OBJECT_TIMER_DATA, data)
        try {
            mService?.send(msg)
        } catch (e: RemoteException) {
            e.printStackTrace()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        return onNavigateUp()
    }

  /*  private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name: CharSequence = getString(R.string.notify_name_channel)
            val description = getString(R.string.content_delete_topic_title)
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel("CHANNEL_ID", name, importance)
            channel.description = description
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }*/

    private fun createAdsMode() {
        MobileAds.initialize(this)
        MobileAds.setRequestConfiguration(
            RequestConfiguration.Builder()
                .setTestDeviceIds(
                    listOf(
                        "B3EEABB8EE11C2BE770B684D95219ECB", "6EEE3EBFBB63E518F69A46CA2B4676D5"
                    )
                )
                .build()
        )
        createBannerAds()
        createRewardAds()
        setFullScreenRewardAds()
        eventBannerAds()
    }

    private fun createBannerAds() {
        /*       mBannerAd = findViewById(R.id.adBanner)
               val adRequest = AdRequest.Builder().build()
               mBannerAd.loadAd(adRequest)*/
    }

    private fun createRewardAds() {
        val adRequest = AdRequest.Builder().build()

        RewardedAd.load(this, "ca-app-pub-5558775664447893/4511965582", adRequest, object : RewardedAdLoadCallback() {
            override fun onAdFailedToLoad(adError: LoadAdError) {
                Log.d(TAG, adError.message)
                mRewardedAd = null
            }

            override fun onAdLoaded(rewardedAd: RewardedAd) {
                Log.d(TAG, "Ad was loaded.")
                mRewardedAd = rewardedAd
            }
        })
    }

    private fun setFullScreenRewardAds() {
        mRewardedAd?.fullScreenContentCallback = object : FullScreenContentCallback() {
            override fun onAdDismissedFullScreenContent() {
                Log.d(TAG, "Ad was dismissed.")
            }

            override fun onAdFailedToShowFullScreenContent(adError: AdError?) {
                Log.d(TAG, "Ad failed to show.")
            }

            override fun onAdShowedFullScreenContent() {
                Log.d(TAG, "Ad showed fullscreen content.")
                // Called when ad is dismissed.
                // Don't set the ad reference to null to avoid showing the ad a second time.
                mRewardedAd = null
            }
        }
    }

    private fun eventBannerAds() {
        /* mBannerAd.adListener = object : AdListener() {
             override fun onAdLoaded() {
                 // Code to be executed when an ad finishes loading.
             }

             override fun onAdFailedToLoad(adError: LoadAdError) {
                 // Code to be executed when an ad request fails.
             }

             override fun onAdOpened() {
                 // Code to be executed when an ad opens an overlay that
                 // covers the screen.
             }

             override fun onAdClicked() {
                 // Code to be executed when the user clicks on an ad.
             }

             override fun onAdLeftApplication() {
                 // Code to be executed when the user has left the app.
             }

             override fun onAdClosed() {
                 // Code to be executed when the user is about to return
                 // to the app after tapping on an ad.
             }
         }*/
    }

    private fun createInterstitialAd() {
        val adRequest = AdRequest.Builder().build()
        InterstitialAd.load(this, "ca-app-pub-9829869928534139/4215290997", adRequest, object : InterstitialAdLoadCallback() {
            override fun onAdFailedToLoad(adError: LoadAdError) {
                Log.d(TAG, adError.message)
                mInterstitialAd = null
            }

            override fun onAdLoaded(interstitialAd: InterstitialAd) {
                Log.d(TAG, "Ad was loaded. release")
                mInterstitialAd = interstitialAd
            }
        })
    }

    private fun showAdsMobile() {
        showRewardAds()
    }

    private fun showRewardAds() {
        if (mRewardedAd != null) {
            mRewardedAd?.show(this) {
                fun onUserEarnedReward(rewardItem: RewardItem) {
//                    var rewardAmount = rewardItem.amount()
                    var rewardType = rewardItem.type
                    Log.d("TAG", "User earned the reward.")
                }
            }
        } else {
            Log.d("TAG", "The rewarded ad wasn't ready yet.")
        }
    }

    private fun catchEventKeyboard() {
        KeyboardVisibilityEvent.setEventListener(
            this,
            object : KeyboardVisibilityEventListener {
                override fun onVisibilityChanged(isOpen: Boolean) {
                    viewModel.isKeyboardShow.postValue(isOpen)
                }
            })
    }

    private fun showRatingApp() {
        val manager = ReviewManagerFactory.create(this)
        val request = manager.requestReviewFlow()
        request.addOnCompleteListener { request ->
            if (request.isSuccessful) {
                // We got the ReviewInfo object
                val reviewInfo = request.result
                val flow = manager.launchReviewFlow(this, reviewInfo)
                flow.addOnCompleteListener { _ ->
                    // The flow has finished. The API does not indicate whether the user
                    // reviewed or not, or even whether the review dialog was shown. Thus, no
                    // matter the result, we continue our app flow.
                }
            } else {
                // There was some problem, continue regardless of the result.
            }
        }
    }

    private fun setOnListener() {
        binding.contentMain.bottomNavigation.setOnNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.pageOne -> {
                    navController.navigate(R.id.FirstFragment)
                    true
                }
                R.id.pageSecond -> {
                    true
                }
                R.id.pageThird -> {
                    true
                }
                R.id.pageFour -> {
                    true
                }
                else -> {
                    false
                }
            }
        }

        binding.contentMain.bottomNavigation.setOnNavigationItemReselectedListener { item ->
            when (item.itemId) {
                R.id.pageOne -> {
                }
                R.id.pageSecond -> {
                }
                R.id.pageThird -> {
                }
                R.id.pageFour -> {
                }
            }
        }
    }

    fun hideOrShowBottomAppBar(isShow: Boolean) {
        if (isShow) {
            if (binding.contentMain.bottomNavigation.visibility != View.VISIBLE) {
                binding.contentMain.bottomNavigation.visible()
            }
        } else {
            if (binding.contentMain.bottomNavigation.visibility == View.VISIBLE) {
                binding.contentMain.bottomNavigation.gone()
            }
        }
    }
}