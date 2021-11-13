package indi.conastin.textmodify.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import indi.conastin.textmodify.databse.HookMethodDao
import indi.conastin.textmodify.databse.HookMethodEntity
import kotlinx.coroutines.launch

class InventoryViewModel(private val hookMethodDao: HookMethodDao) : ViewModel() {

    var allMethod: LiveData<List<HookMethodEntity>> = hookMethodDao.getAllMethod().asLiveData()
    val globalMethod: LiveData<List<HookMethodEntity>> =
        hookMethodDao.getGlobalMethod().asLiveData()
    lateinit var packageMethod: LiveData<List<HookMethodEntity>>

    fun getPackageMethod(packageName: String) {
        viewModelScope.launch {
            packageMethod = hookMethodDao.getPackageMethod(packageName).asLiveData()
        }
    }

//    private fun getMethodEntry(
//        packageName: String,
//        originText: String,
//        newText: String,
//    ): HookMethodEntity {
//        return HookMethodEntity(
//            packageName = packageName,
//            originText = originText,
//            newText = newText
//        )
//    }

    // 新增规则
    fun addPackageMethod(
        id: Int,
        packageName: String,
        originText: String,
        newText: String
    ) {
//        val packageMethod = getMethodEntry(packageName, originText, newText)
        viewModelScope.launch {
            hookMethodDao.addMethod(HookMethodEntity(id, packageName, originText, newText))
        }
    }

    // 删除规则
    fun deletePackageMethod(
        id: Int,
        packageName: String,
        originText: String,
        newText: String
    ) {
//        val packageMethod = getMethodEntry(packageName, originText, newText)
        viewModelScope.launch {
            hookMethodDao.deleteMethod(HookMethodEntity(id, packageName, originText, newText))
        }
    }

    // 更新规则
    fun updatePackageMethod(
        id: Int,
        packageName: String,
        originText: String,
        newText: String
    ) {
        viewModelScope.launch {
            hookMethodDao.updateMethod(HookMethodEntity(id, packageName, originText, newText))
        }
    }
}