package indi.conastin.textmodify.activity

import android.content.Intent
import android.content.pm.ApplicationInfo
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.inputmethod.EditorInfo
import android.widget.SearchView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import indi.conastin.textmodify.R
import indi.conastin.textmodify.adapter.AppListAdapter
import indi.conastin.textmodify.databinding.ActivityAppMethodBinding
import indi.conastin.textmodify.databse.AppInfo

class AppListActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAppMethodBinding
    lateinit var appsInfo: List<AppInfo>
    lateinit var searchInfo: List<AppInfo>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val adapter = AppListAdapter {
            var intent = Intent(binding.root.context, AppMethodActivity::class.java)
            intent.putExtra("appName", it.appName)
            intent.putExtra("packageName", it.packageName)
            startActivity(intent)
        }
        binding = ActivityAppMethodBinding.inflate(layoutInflater)
        setContentView(binding.root)
        appsInfo = getPackageList()
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = adapter
        adapter.submitList(appsInfo)
        binding.searchView.imeOptions = EditorInfo.IME_ACTION_DONE
        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(p0: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(p0: String?): Boolean {
                searchInfo = arrayListOf()
                for (appInfo in appsInfo) {
                    if (p0.toString() in appInfo.appName || p0.toString() in appInfo.packageName) {
                        searchInfo += appInfo
                    }
                }
                adapter.submitList(searchInfo)
                return false
            }

        })

        // 刷新功能
        binding.topBar.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.menu_refresh -> {
                    appsInfo = getPackageList()
                }
            }
            true
        }

    }

    private fun getAppInfo(
        packageName: String,
        name: String,
        icon: Drawable,
        isSystem: Boolean
    ): AppInfo {
        return AppInfo(
            packageName = packageName,
            appName = name,
            icon = icon,
            isSystem = isSystem
        )
    }

    private fun getPackageList(): List<AppInfo> {
        var list: MutableList<AppInfo> = mutableListOf()
        var packagesInfo = packageManager.getInstalledPackages(0)
        for (packageInfo in packagesInfo) {
            var isSystem: Boolean = false
            var packageName: String = packageInfo.packageName
            var name: String = packageInfo.applicationInfo.loadLabel(packageManager).toString()
            var icon: Drawable = packageInfo.applicationInfo.loadIcon(packageManager)
            // check if system app
            if ((packageInfo.applicationInfo.flags and ApplicationInfo.FLAG_SYSTEM) != 0) {
                // if system app
                isSystem = true
            }
            list.add(getAppInfo(packageName, name, icon, isSystem))
        }
        return list
    }

}