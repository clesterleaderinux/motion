package tfmf.ui.demo

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
import com.microsoft.fluentxml.demo.fragment.xmlsample.ASyncChainsFragment
import com.microsoft.fluentxml.demo.fragment.xmlsample.AlphaFragment
import com.microsoft.fluentxml.demo.fragment.xmlsample.BannerIntroFragment
import com.microsoft.fluentxml.demo.fragment.xmlsample.ButtonFragment
import com.microsoft.fluentxml.demo.fragment.xmlsample.CardFragment
import com.microsoft.fluentxml.demo.fragment.xmlsample.CardViewFragment
import com.microsoft.fluentxml.demo.fragment.xmlsample.CascadeFragment
import com.microsoft.fluentxml.demo.fragment.xmlsample.CheckboxFragment
import com.microsoft.fluentxml.demo.fragment.xmlsample.CurvesFragment
import com.microsoft.fluentxml.demo.fragment.xmlsample.DurationsFragment
import com.microsoft.fluentxml.demo.fragment.xmlsample.EmptyStateFragment
import com.microsoft.fluentxml.demo.fragment.xmlsample.FirstRunExperienceViewFragment
import com.microsoft.fluentxml.demo.fragment.xmlsample.FabButtonFragment
import com.microsoft.fluentxml.demo.fragment.xmlsample.FullPageIntroFragment
import com.microsoft.fluentxml.demo.fragment.xmlsample.GridCardFragment
import com.microsoft.fluentxml.demo.fragment.xmlsample.ListItemViewFragment
import com.microsoft.fluentxml.demo.fragment.xmlsample.LottieButtonFragment
import com.microsoft.fluentxml.demo.fragment.xmlsample.LottieFragment
import com.microsoft.fluentxml.demo.fragment.xmlsample.MotionViewSearchFabFragment
import com.microsoft.fluentxml.demo.fragment.xmlsample.NavSliderFragment
import com.microsoft.fluentxml.demo.fragment.xmlsample.PillFragment
import com.microsoft.fluentxml.demo.fragment.xmlsample.RadioButtonFragment
import com.microsoft.fluentxml.demo.fragment.xmlsample.ResizeFragment
import com.microsoft.fluentxml.demo.fragment.xmlsample.ScaleFragment
import com.microsoft.fluentxml.demo.fragment.xmlsample.SectionHeaderFragment
import com.microsoft.fluentxml.demo.fragment.xmlsample.SharedTransitionEnterFragment
import com.microsoft.fluentxml.demo.fragment.xmlsample.SharedTransitionExitFragment
import com.microsoft.fluentxml.demo.fragment.xmlsample.ShimmerFragment
import com.microsoft.fluentxml.demo.fragment.xmlsample.SnackbarFragment
import com.microsoft.fluentxml.demo.fragment.xmlsample.SpotlightFragment
import com.microsoft.fluentxml.demo.fragment.xmlsample.SwitchFragment
import com.microsoft.fluentxml.demo.fragment.xmlsample.SyncChainsFragment
import com.microsoft.fluentxml.demo.fragment.xmlsample.TabSelectorFragment
import com.microsoft.fluentxml.demo.fragment.xmlsample.TranslationFragment

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
                R.id.section_header -> {
                    switchFragments("SectionHeader")
                    title.text = "Section Header"
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

                R.id.sharedviews -> {
                    switchFragments("SharedViews")
                    title.text = "Shared views"
                }

                R.id.grid_card -> {
                    switchFragments("GridCard")
                    title.text = "Grid card"
                }

                R.id.buttons -> {
                    switchFragments("Buttons")
                    title.text = "Buttons"
                }

                R.id.switches -> {
                    switchFragments("Switch")
                    title.text = "Switch"
                }

                R.id.fab_create_button -> {
                    switchFragments("FabButton")
                    title.text = "FAB Create Button"
                }

                R.id.radio_button -> {
                    switchFragments("RadioButton")
                    title.text = "Radio Button"
                }

                R.id.checkbox -> {
                    switchFragments("Checkbox")
                    title.text = "Checkbox"
                }

                R.id.shimmer -> {
                    switchFragments("Shimmer")
                    title.text = "Shimmer"
                }

                R.id.empty_state -> {
                    switchFragments("EmptyState")
                    title.text = "Empty State"
                }

                R.id.snackbar -> {
                    switchFragments("Snackbar")
                    title.text = "Snackbar"
                }

                R.id.spotlight -> {
                    switchFragments("Spotlight")
                    title.text = "Spotlight"
                }

                R.id.full_page_intro -> {
                    switchFragments("FullPageIntro")
                    title.text = "Full Page Intro"
                }

                R.id.banner_control -> {
                    switchFragments("BannerControl")
                    title.text = "Banner Control"
                }

                R.id.fre -> {
                    switchFragments("FirstRunExperienceView")
                    title.text = "First Run Experience View"
                }

                R.id.pill -> {
                    switchFragments("Pill")
                    title.text = "Pill"
                }
                R.id.list_item_view -> {
                    switchFragments("ListItemView")
                    title.text = "ListItemView"
                }
                R.id.card -> {
                    switchFragments("Card")
                    title.text = "Card"
                }
                R.id.tab_selector -> {
                    switchFragments("TabSelector")
                    title.text = "TabSelector"
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
                "SectionHeader" -> SectionHeaderFragment()
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
                "SharedViews" -> SharedTransitionEnterFragment()
                "GridCard" -> GridCardFragment()
                "Buttons" -> ButtonFragment()
                "Switch" -> SwitchFragment()
                "FabButton" -> FabButtonFragment()
                "RadioButton" -> RadioButtonFragment()
                "Checkbox" -> CheckboxFragment()
                "Shimmer" -> ShimmerFragment()
                "EmptyState" -> EmptyStateFragment()
                "Snackbar" -> SnackbarFragment()
                "Spotlight" -> SpotlightFragment()
                "FullPageIntro" -> FullPageIntroFragment()
                "FirstRunExperienceView" -> FirstRunExperienceViewFragment()
                "BannerControl" -> BannerIntroFragment()
                "Pill" -> PillFragment()
                "ListItemView" -> ListItemViewFragment()
                "Card" -> CardFragment()
                "TabSelector" -> TabSelectorFragment()
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