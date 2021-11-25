package moe.gkd.bangumi.ui.main

import android.view.MenuItem
import androidx.core.view.GravityCompat
import androidx.navigation.Navigation
import com.google.android.material.navigation.NavigationView
import moe.gkd.bangumi.R
import moe.gkd.bangumi.databinding.ActivityMainBinding
import moe.gkd.bangumi.ui.BaseActivity

class MainActivity : BaseActivity<ActivityMainBinding>(R.layout.activity_main),
    NavigationView.OnNavigationItemSelectedListener {
    val navController by lazy {
        Navigation.findNavController(this, R.id.nav_host_fragment)
    }

    override fun initViews() {
        binding.navigation.also { navigation ->
            navigation.setNavigationItemSelectedListener(this)
            navigation.setCheckedItem(R.id.bangumi)
        }
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_baseline_menu_24)
    }

    override fun initViewModel() {

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
                binding.drawerLayout.closeDrawer(GravityCompat.START)
            } else {
                binding.drawerLayout.openDrawer(GravityCompat.START)
            }
            return false
        }
        return super.onOptionsItemSelected(item)
    }

    private fun switchFragment(id: Int) {
        val current = navController.currentDestination!!.id
        if (current != id) {
            navController.popBackStack()
            navController.navigate(id)
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        when (id) {
            R.id.bangumi -> {
                switchFragment(R.id.nav_bangumi)
            }
            R.id.settings -> {
                switchFragment(R.id.nav_settings)
            }
        }
        binding.drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }
}