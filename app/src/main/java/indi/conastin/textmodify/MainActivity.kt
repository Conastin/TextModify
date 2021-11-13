package indi.conastin.textmodify

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Process
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import indi.conastin.textmodify.activity.AboutActivity
import indi.conastin.textmodify.activity.AppListActivity
import indi.conastin.textmodify.activity.GlobalMethodActivity
import indi.conastin.textmodify.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    @SuppressLint("UseCompatLoadingForDrawables")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // binding创建layout
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        // 检查root权限
//        Runtime.getRuntime().exec("su")
        // 检查xposed激活状态
        if (checkModelActive()) {
            binding.mainCheckXposedActiveBackground.setBackgroundResource(R.drawable.bg_green_solid)
            binding.mainCheckXposedActiveText.setText(R.string.main_xposed_is_active)
            binding.mainCheckXposedActiveIcon.setImageDrawable(
                resources.getDrawable(R.drawable.ic_is_active, null)
            )
        }
        // 重启按钮监听
        binding.mainCheckXposedActiveBackground.setOnClickListener {
            // 重启
            val intent = Intent(this, MainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            this.startActivity(intent)
            Process.killProcess(Process.myPid())
        }
        // 应用列表按钮监听
        binding.appMethod.setOnClickListener {
            startActivity(Intent(view.context, AppListActivity::class.java))
        }
        // 全局替换按钮监听
        binding.globalMethod.setOnClickListener {
            startActivity(Intent(view.context, GlobalMethodActivity::class.java))
        }

        binding.setting.setOnClickListener {
            Toast.makeText(this, "憋催了，没写呢！", Toast.LENGTH_SHORT).show()
            // 隐藏图标
//            val componentName = ComponentName(this, MainActivity::class.java)
//            val res = packageManager.getComponentEnabledSetting(componentName);
//            Log.d("TextModify", (res == PackageManager.COMPONENT_ENABLED_STATE_DEFAULT).toString() + (res == PackageManager.COMPONENT_ENABLED_STATE_ENABLED));
//            if (res == PackageManager.COMPONENT_ENABLED_STATE_DEFAULT
//                || res == PackageManager.COMPONENT_ENABLED_STATE_ENABLED
//            ) {
//                // 隐藏应用图标
//                packageManager.setComponentEnabledSetting(
//                    componentName, PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
//                    PackageManager.DONT_KILL_APP
//                );
//            } else {
//                // 显示应用图标
//                packageManager.setComponentEnabledSetting(
//                    componentName, PackageManager.COMPONENT_ENABLED_STATE_DEFAULT,
//                    PackageManager.DONT_KILL_APP
//                );
//            }
        }
        // 关于按钮监听
        binding.info.setOnClickListener {
            startActivity(Intent(view.context, AboutActivity::class.java))
        }
    }

    private fun checkModelActive(): Boolean {
        return false
    }

}