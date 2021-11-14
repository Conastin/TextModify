package indi.conastin.textmodify.log

import android.util.Log
import de.robv.android.xposed.XposedBridge

class NewLog {

    fun systemLog(string: String) {
        Log.d("TextModify", string)
    }

    fun xposedLog(string: String) {
        XposedBridge.log(string.replace("【", "【TextModify|"))
    }
}