package indi.conastin.textmodify.viewmodel

import android.annotation.SuppressLint
import android.util.JsonReader
import android.util.JsonWriter
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import indi.conastin.textmodify.databse.RuleInfo
import kotlinx.coroutines.launch
import java.io.*

class RulesViewModel : ViewModel() {
    @SuppressLint("SdCardPath")
    val file = File("/data/user/0/indi.conastin.textmodify/files", "rules.json")
    lateinit var methods: HashMap<String, ArrayList<RuleInfo>>

    fun getMethod() {
        viewModelScope.launch {
            initFIle()
            methods = readJson()
        }
    }

    private fun initFIle() {
        if (!file.exists()) {
            // 初始化
            file.createNewFile()
            val writer = JsonWriter(OutputStreamWriter(FileOutputStream(file), "utf-8"))
            writer.setIndent("    ")
            writer.beginArray()
            writer.endArray()
            writer.close()
        }
    }

    private fun readJson(): HashMap<String, ArrayList<RuleInfo>> {
        var reader = JsonReader(InputStreamReader(FileInputStream(file), "utf-8"))
        val appsMethod: HashMap<String, ArrayList<RuleInfo>> = hashMapOf()
        reader.beginArray()
        while (reader.hasNext()) {
            var id = ""
            var packageName = ""
            var originText = ""
            var newText = ""
            reader.beginObject()
            while (reader.hasNext()) {
                var field = reader.nextName()
                if (field.equals("packageName")) {
                    packageName = reader.nextString()
                } else if (field.equals("originText")) {
                    originText = reader.nextString()
                } else if (field.equals("newText")) {
                    newText = reader.nextString()
                } else {
                    reader.skipValue()
                }
            }
            reader.endObject()
            if (appsMethod[packageName] == null) {
                appsMethod[packageName] = arrayListOf(RuleInfo(originText, newText))
            } else {
                appsMethod[packageName] = arrayListOf(
                    appsMethod[packageName],
                    RuleInfo(originText, newText)
                ) as java.util.ArrayList<RuleInfo>
            }
        }
        reader.endArray()
        reader.close()
        return appsMethod
    }
}