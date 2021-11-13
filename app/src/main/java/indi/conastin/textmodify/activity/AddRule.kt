package indi.conastin.textmodify.activity

import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import indi.conastin.textmodify.databinding.ActivityAddRuleBinding
import indi.conastin.textmodify.databse.HookMethodDatabase
import indi.conastin.textmodify.databse.RuleInfo
import indi.conastin.textmodify.viewmodel.InventoryViewModel
import java.io.BufferedReader
import java.io.InputStreamReader

class AddRule : AppCompatActivity() {

    private lateinit var binding: ActivityAddRuleBinding
    private lateinit var viewModel: InventoryViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // binding创建layout
        binding = ActivityAddRuleBinding.inflate(layoutInflater)
        setContentView(binding.root)
        // 接收Intent参数
        val bundle = this.intent.extras
        val id = bundle?.get("id")
        val packageName = bundle?.get("packageName")
        val originText = bundle?.get("originText")
        val newText = bundle?.get("newText")
//        Log.d("TextModify", "【AddRule】 | id: $id | packageName: $packageName | originText: $originText | newText: $newText")
        // 配置SharedPreference对象
        val sps = getSharedPreferences("${packageName}_TextModify", MODE_PRIVATE)
        // 初始化存储数量变量
        var num = 0
        // 获取SharedPreference的所有键值对
        val all = readSharedPreference(sps)
//        Log.d("TextModify", "【AddRule】 | all: $all")
        // map按id存储originText和newText
        val map = mutableMapOf<String, RuleInfo>()
        // map赋值
        if (all != null) {
            for ((k, v) in all) {
                if (k == "num") {
                    // 获取总共的originText和newText数量
                    num = v as Int
//                    Log.d("TextModify", "【AddRule】 | num: $num")
                } else {
                    // 获取originText和newText给map
//                    Log.d("TextModify", "【AddRule】 | k.drop(1): ${k.drop(1)}")
                    if ("o" in k) {
                        // k是原文字
                        map[k.drop(1)] = RuleInfo(v as String, map[k.drop(1)]?.newText ?: "")
                    } else {
                        // k是新文字
                        map[k.drop(1)] = RuleInfo(map[k.drop(1)]?.originText ?: "", v as String)
                    }
                }
            }
        }
//        Log.d("TextModify", "【AddRule】 | map: $map")
        // room对象
        val database = HookMethodDatabase.getDatabase(this)
        // 初始化id对象
        var maxId = 0
        // viewModel对象
        viewModel = InventoryViewModel(database.HookMethodDao())
        // 获取最大id
        viewModel.allMethod.observe(this) {
            it.let {
                maxId = it.count()
            }
        }
//        Log.d("TextModify", "【AddRule】 | maxId: $maxId | packageName: $packageName | originText: $originText | newText: $newText")
        // 更改时将原值显示
        if (id != null) {
            if (originText != null) {
                binding.originText.setText(originText.toString())
            }
            if (newText != null) {
                binding.newText.setText(newText.toString())
            }
        }

        // 保存按钮监听
        binding.save.setOnClickListener {
            if (id == null) {
                // 新建
                viewModel.addPackageMethod(
                    maxId,
                    packageName.toString(),
                    binding.originText.text.toString(),
                    binding.newText.text.toString()
                )
                addRuleToSharedPreference(
                    sps,
                    num,
                    maxId,
                    binding.originText.text.toString(),
                    binding.newText.text.toString()
                )
                Toast.makeText(
                    this,
                    "添加规则成功\n原文字：" + binding.originText.text.toString() + "\n替换文字：" + binding.newText.text.toString(),
                    Toast.LENGTH_SHORT
                ).show()
                this.finish()
            } else {
                // 更新
                viewModel.updatePackageMethod(
                    id as Int,
                    packageName.toString(),
                    binding.originText.text.toString(),
                    binding.newText.text.toString()
                )
                updateRuleToSharedPreference(
                    sps,
                    id.toInt(),
                    binding.originText.text.toString(),
                    binding.newText.text.toString()
                )
                Toast.makeText(
                    this,
                    "更新规则成功\n原文字：" + binding.originText.text.toString() + "\n替换文字：" + binding.newText.text.toString(),
                    Toast.LENGTH_SHORT
                ).show()
                this.finish()
            }
        }

        // 删除按钮监听
        binding.delete.setOnClickListener {
            if (id == null) {
//                Log.d("TextModify", "【AddRule】 | maxId: $maxId")
                Toast.makeText(this, "添加规则你都还没添加\n删个屁的", Toast.LENGTH_SHORT).show()
            } else {
                // 删除
                viewModel.deletePackageMethod(
                    id as Int,
                    packageName.toString(),
                    binding.originText.text.toString(),
                    binding.newText.text.toString()
                )
                deleteRuleToSharedPreference(sps, num, id.toInt())
                Toast.makeText(
                    this,
                    "删了就删了，世上没有后悔药\n但我可以给你瞅一眼让你记下来\n原文字：" + binding.originText.text.toString() + "\n替换文字：" + binding.newText.text.toString(),
                    Toast.LENGTH_SHORT
                ).show()
                this.finish()
            }
        }
    }

    private fun addRuleToSharedPreference(
        sps: SharedPreferences,
        num: Int,
        id: Int,
        originText: String,
        newText: String
    ) {
        val edit = sps.edit()
        edit.putInt("num", num + 1)
        edit.putString("o$id", originText)
        edit.putString("n$id", newText)
        edit.apply()
    }

    private fun updateRuleToSharedPreference(
        sps: SharedPreferences,
        id: Int,
        originText: String,
        newText: String
    ) {
        val edit = sps.edit()
        edit.putString("o$id", originText)
        edit.putString("n$id", newText)
        edit.apply()
    }

    private fun deleteRuleToSharedPreference(sps: SharedPreferences, num: Int, id: Int) {
        val edit = sps.edit()
        edit.remove("o$id")
        edit.remove("n$id")
        edit.putInt("num", num - 1)
        edit.apply()
    }

    private fun getCountShared(sps: SharedPreferences): Int {
        return sps.getInt("num", 0)
    }

    private fun readSharedPreference(sps: SharedPreferences): MutableMap<String, *>? {
        return sps.all
    }

    private fun cpSharedPreference(packageName: String) {
        if (packageName != "global") {
            // 获取root
            val process = Runtime.getRuntime().exec("su")
            val out = process.outputStream
            var cmd =
                "cp -f /data/data/indi.conastin.textmodify/shared_prefs/global_TextModify.xml /data/data/indi.conastin.textmodify/shared_prefs/${packageName}_TextModify.xml /data/data/$packageName/shared_prefs/ \n"
            out.write(cmd.toByteArray())
            out.flush()
            cmd =
                "chmod 777 /data/data/$packageName/shared_prefs/global_TextModify.xml /data/data/$packageName/shared_prefs/${packageName}_TextModify.xml \n"
            out.write(cmd.toByteArray())
            out.flush()
            out.write("exit \n".toByteArray())
            out.close()
            val fis = process.inputStream
            val isr = InputStreamReader(fis)
            val br = BufferedReader(isr)
            var line: String? = br.readLine()
            while (line != null) {
                line = br.readLine()
            }
        }
    }

//    private fun rubbish() {
//private fun initFile(file: File) {
//    if (!file.parentFile.exists()) {
//        file.parentFile.mkdir()
//    }
//    if (!file.exists()) {
//        file.createNewFile()
//        val writer = JsonWriter(OutputStreamWriter(FileOutputStream(file), "utf-8"))
//        writer.setIndent("    ")
//        writer.beginArray()
//        writer.endArray()
//        writer.close()
//    }
//}
////        private fun readJson(file: File): MutableMap<Int, MethodInfo> {
////            var reader = JsonReader(InputStreamReader(FileInputStream(file), "utf-8"))
////            val appsMethod: MutableMap<Int, MethodInfo> = mutableMapOf()
////            reader.beginArray()
////            while (reader.hasNext()) {
////                var id = ""
////                var packageName = ""
////                var originText = ""
////                var newText = ""
////                reader.beginObject()
////                while (reader.hasNext()) {
////                    var field = reader.nextName()
////                    if (field.equals("id")) {
////                        id = reader.nextString()
////                    } else if (field.equals("packageName")) {
////                        packageName = reader.nextString()
////                    } else if (field.equals("originText")) {
////                        originText = reader.nextString()
////                    } else if (field.equals("newText")) {
////                        newText = reader.nextString()
////                    } else {
////                        reader.skipValue()
////                    }
////                }
////                reader.endObject()
////                appsMethod.put(id.toInt(), MethodInfo(packageName, originText, newText))
////            }
////            reader.endArray()
////            reader.close()
////            return appsMethod
////        }
////
////        private fun writeJson(file: File, map: MutableMap<Int, MethodInfo>) {
////            val writer = JsonWriter(OutputStreamWriter(FileOutputStream(file), "utf-8"))
////            writer.setIndent("    ")
////            writer.beginArray()
////            for ((m, k) in map) {
////                writer.beginObject()
////                writer.name("id").value(m)
////                writer.name("packageName").value(k.packageName)
////                writer.name("originText").value(k.originText)
////                writer.name("newText").value(k.newText)
////                writer.endObject()
////            }
////            writer.endArray()
////            writer.close()
////        }
//
//
//    }
}