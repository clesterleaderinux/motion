package demo.tfmf.ui.activity

import android.os.Bundle
import android.view.Menu
import android.view.View
import android.view.Window
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.transition.Fade
import com.google.android.material.navigation.NavigationView
import demo.tfmf.ui.fragment.xmlsample.CardViewFragment
import demo.tfmf.ui.fragment.xmlsample.CascadeFragment
import demo.tfmf.ui.fragment.xmlsample.CurvesFragment
import demo.tfmf.ui.fragment.xmlsample.DurationsFragment
import demo.tfmf.ui.fragment.xmlsample.LottieFragment
import demo.tfmf.ui.fragment.xmlsample.ResizeFragment
import demo.tfmf.ui.fragment.xmlsample.ScaleFragment
import tfmf.ui.demo.fragment.xmlsample.ASyncChainsFragment
import tfmf.ui.demo.fragment.xmlsample.AlphaFragment
import tfmf.ui.demo.fragment.xmlsample.SharedTransitionExitFragment
import tfmf.ui.demo.fragment.xmlsample.ShimmerFragment
import tfmf.ui.demo.fragment.xmlsample.SyncChainsFragment
import tfmf.ui.demo.fragment.xmlsample.TranslationFragment

class DemoActivity : AppCompatActivity() {
    lateinit var drawerLayout: DrawerLayout
    private lateinit var navigationView: NavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_CONTENT_TRANSITIONS)

        setContentView(R.layout.activity_xmlsamples)

        drawerLayout = findViewById(R.id.drawer_layout)
        val openDrawerButton = findViewById<ImageButton>(R.id.open_nav)
        openDrawerButton.setOnClickListener { drawerLayout.openDrawer(GravityCompat.START) }
        navigationView = findViewById(R.id.nav_view)
        val title = findViewById<TextView>(R.id.title)
        navigationView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {`
                R.id.shimmer -> {
                switchFragments("Shimmer")
                title.text = "Shimmer"
            }
                R.id.search_fab -> {
                    switchFragments("SearchFab")
                    title.text = "Search Fab"
                }

                R.id.cascade -> {
                    switchFragments("Cascade")
                    title.text = "Cascade"
                }

                R.id.card_view -> {
                    switchFragments("CardView")
                    title.text = "CardView"
                }

                R.id.navslider -> {
                    switchFragments("NavSlider")
                    title.text = "Nav Slider"
                }

                R.id.lottie_button -> {
                    switchFragments("LottieButton")
                    title.text = "Lottie Button"
                }

                R.id.lottieanimation -> {
                    switchFragments("Lottie")
                    title.text = "Lottie Animation"
                }

                R.id.curves -> {
                    switchFragments("Curves")
                    title.text = "Curves"
                }

                R.id.durations -> {
                    switchFragments("Durations")
                    title.text = "Durations"
                }

                R.id.alpha -> {
                    switchFragments("Alpha")
                    title.text = "Alpha"
                }

                R.id.scale -> {
                    switchFragments("Scale")
                    title.text = "Scale"
                }

                R.id.translation -> {
                    switchFragments("Translation")
                    title.text = "Translation"
                }

                R.id.resize -> {
                    switchFragments("Resize")
                    title.text = "Resize"
                }

                R.id.syncchains -> {
                    switchFragments("SyncChains")
                    title.text = "Sync Chains"
                }

                R.id.asyncchains -> {
                    switchFragments("ASyncChains")
                    title.text = "Async Chains"
                }

                R.id.shimmer -> {
                    switchFragments("Shimmer")
                    title.text = "Shimmer"
                }
            }
            drawerLayout.closeDrawer(GravityCompat.START)
            true
        }
        navigationView.inflateMenu(R.menu.drawer_menu)
        switchFragments("TabSelector")
        title.text = "TabSelector"
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.drawer_menu, menu)
        return true
    }

    private fun switchFragments(key: String) {
        var fragment: Fragment =
            when (key) {
                "SearchFab" -> MotionViewSearchFabFragment()
                "Cascade" -> CascadeFragment()
                "CardView" -> CardViewFragment()
                "NavSlider" -> NavSliderFragment()
                "LottieButton" -> LottieButtonFragment()
                "Lottie" -> LottieFragment()
                "Curves" -> CurvesFragment()
                "Durations" -> DurationsFragment()
                "Alpha" -> AlphaFragment()
                "Scale" -> ScaleFragment()
                "Translation" -> TranslationFragment()
                "Resize" -> ResizeFragment()
                "SyncChains" -> SyncChainsFragment()
                "ASyncChains" -> ASyncChainsFragment()
                "Shimmer" -> ShimmerFragment()
                else -> Fragment()
            }

        // Get the FragmentManager and start a transaction
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()

        // Add the YourFragment
        fragment.exitTransition = Fade()
        fragmentTransaction.replace(R.id.fragment_container, fragment)
        fragmentTransaction.commit()
    }

    fun switchToContinuosMotion(view: View, transitionName: String) {
        val secondFragment = SharedTransitionExitFragment()
        val transaction = supportFragmentManager.beginTransaction()

        secondFragment.enterTransition = Fade()
        transaction.addSharedElement(view, transitionName)
        transaction.replace(R.id.fragment_container, secondFragment)
        transaction.commit()
    }
}