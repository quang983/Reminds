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
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.NavigationUI
import com.example.common.base.model.AlarmNotificationEntity
import com.example.framework.local.cache.CacheImpl.Companion.KEY_FIRST_LOGIN
import com.example.framework.local.cache.CacheImpl.Companion.SHARED_NAME
import com.example.reminds.R
import com.example.reminds.service.INSERT_OBJECT_TIMER_DATA
import com.example.reminds.service.NotificationService
import com.example.reminds.ui.sharedviewmodel.MainActivityViewModel
import com.example.reminds.utils.postValue
import com.google.android.gms.ads.*
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.google.android.gms.ads.rewarded.RewardItem
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback
import com.google.android.play.core.review.ReviewManagerFactory
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.content_main.*
import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEvent
import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEventListener


@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    /** Flag indicating whether we have called bind on the service.  */
    private var bound: Boolean = false
    private var mService: Messenger? = null

    //    private var mRewardedAd: RewardedAd? = null
    private var TAG = "logMain"
    private var mInterstitialAd: InterstitialAd? = null

    private var mRewardedAd: RewardedAd? = null

    private lateinit var mBannerAd: AdView

    lateinit var intentService: Intent

    /** Messenger for communicating with the service.  */
    private var mMessenger: Messenger? = null
    val viewModel: MainActivityViewModel by viewModels()
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(findViewById(R.id.toolbar))
        onStartService()
        createNotificationChannel()
        /*show back press button*/
        navController = findNavController(R.id.nav_host_fragment)
        NavigationUI.setupActionBarWithNavController(this, navController)
        checkAddFirstTopic()
        catchEventKeyboard()
        setObserver()
        createAdsMode()
//        showRatingApp()
    }

    private fun setObserver() {
        viewModel.apply {
            notifyDataInsert.observe(this@MainActivity, {
                sendActionInsertAlert(it)
            })
            showAdsMobile.observe(this@MainActivity, {
                showAdsMobile()
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

    private fun checkAddFirstTopic() {
        val shared = this.applicationContext.getSharedPreferences(SHARED_NAME, Context.MODE_PRIVATE)
        if (shared.getBoolean(KEY_FIRST_LOGIN, true)) {
            viewModel.addFirstTopic(resources.getString(R.string.topic_title))
            shared.edit().putBoolean(KEY_FIRST_LOGIN, false).apply()
        }
    }

/*
    override fun onStop() {
        super.onStop()
        // Unbind from the service
        if (bound) {
            unbindService(mConnection)
            bound = false
        }
    }
*/

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
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        return onNavigateUp()
    }

    private fun createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name: CharSequence = getString(R.string.notify_name_channel)
            val description = getString(R.string.content_delete_topic_title)
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel("CHANNEL_ID", name, importance)
            channel.description = description
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun createAdsMode() {
//        Log.d(TAG, "createAdsMode: ${AdRequest.DEVICE_ID_EMULATOR}")
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
        mBannerAd = findViewById(R.id.adBanner)
        val adRequest = AdRequest.Builder().build()
        mBannerAd.loadAd(adRequest)
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
        mBannerAd.adListener = object : AdListener() {
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
        }
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

    private fun showAdsInterstitial() {
        if (mInterstitialAd != null) {
            mInterstitialAd?.show(this)
        } else {
            Log.d("TAG", "The interstitial ad wasn't ready yet.")
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
}