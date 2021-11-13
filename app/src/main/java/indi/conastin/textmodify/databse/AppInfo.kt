package indi.conastin.textmodify.databse

import android.graphics.drawable.Drawable

data class AppInfo(
    // 数据类用以生成应用列表
    val appName: String,
    val packageName: String,
    val icon: Drawable,
    val isSystem: Boolean
)