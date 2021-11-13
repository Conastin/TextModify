package indi.conastin.textmodify.activity

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import indi.conastin.textmodify.adapter.AppMethodAdapter
import indi.conastin.textmodify.databinding.ActivityAppMethodInfoBinding
import indi.conastin.textmodify.databse.HookMethodDatabase
import indi.conastin.textmodify.viewmodel.InventoryViewModel
import java.io.BufferedReader
import java.io.InputStreamReader


class AppMethodActivity : AppCompatActivity() {

    //    private lateinit var application: InventoryApplication
    private lateinit var viewModel: InventoryViewModel
    private lateinit var binding: ActivityAppMethodInfoBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAppMethodInfoBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val bundle = this.intent.extras
        binding.topBar.title = bundle?.get("appName").toString()
        val database = HookMethodDatabase.getDatabase(this)
//        application = InventoryApplication()
//        viewModel = InventoryViewModel((application as InventoryApplication).database.HookMethodDao())
        viewModel = InventoryViewModel(database.HookMethodDao())
        viewModel.getPackageMethod(bundle?.get("packageName").toString())
        val adapter = AppMethodAdapter {
            val intent = Intent(binding.root.context, AddRule::class.java)
            intent.putExtra("id", it.id)
            intent.putExtra("packageName", it.packageName)
            intent.putExtra("originText", it.originText)
            intent.putExtra("newText", it.newText)
            startActivity(intent)
        }
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = adapter
        viewModel.packageMethod.observe(this) {
            it.let {
                adapter.submitList(it)
            }
        }

        // 添加规则
        binding.addRule.setOnClickListener {
            val intent = Intent(binding.root.context, AddRule::class.java)
            intent.putExtra("packageName", bundle?.get("packageName").toString())
            startActivity(intent)
        }

        // 重启应用
        binding.rebootApp.setOnClickListener {
            // 关闭应用
            // 获取root权限
            val process = Runtime.getRuntime().exec("su")
            val out = process.outputStream
            // 移动配置文件至目标应用私有目录
            var cmd =
                "cp -f /data/data/indi.conastin.textmodify/shared_prefs/global_TextModify.xml /data/data/indi.conastin.textmodify/shared_prefs/${
                    bundle?.get("packageName").toString()
                }_TextModify.xml /data/data/${
                    bundle?.get("packageName").toString()
                }/shared_prefs/\n"
            out.write(cmd.toByteArray())
            out.flush()
            // 因为移动过去的文件归属用户还是归TextModify 目标应用无法读取 所以需要提权使目标应用可以正常访问
            cmd = "chmod 777 /data/data/${
                bundle?.get("packageName").toString()
            }/shared_prefs/global_TextModify.xml /data/data/${
                bundle?.get("packageName").toString()
            }/shared_prefs/${bundle?.get("packageName").toString()}_TextModify.xml\n"
            out.write(cmd.toByteArray())
            out.flush()
            // 强行关闭目标应用
            cmd = "am force-stop ${bundle?.get("packageName").toString()}\n"
            out.write(cmd.toByteArray())
            out.flush()
            out.close()
            // 等待完成
            val fis = process.inputStream
            val isr = InputStreamReader(fis)
            val br = BufferedReader(isr)
            var line: String? = br.readLine()
            while (line != null) {
                line = br.readLine()
            }
            // 开启应用
            startActivity(
                Intent(
                    packageManager.getLaunchIntentForPackage(
                        bundle?.get("packageName").toString()
                    )
                )
            )
        }
    }
}