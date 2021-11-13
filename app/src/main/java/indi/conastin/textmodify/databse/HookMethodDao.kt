package indi.conastin.textmodify.databse


import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface HookMethodDao {

    // 获取所有方法
    @Query("select * from app_method order by id desc")
    fun getAllMethod(): Flow<List<HookMethodEntity>>

    // 获取全局方法
    @Query("select * from app_method where packageName = 'global' order by id desc")
    fun getGlobalMethod(): Flow<List<HookMethodEntity>>

    // 获取包名的所有方法
    @Query("select * from app_method where packageName = :packageName order by id desc")
    fun getPackageMethod(packageName: String): Flow<List<HookMethodEntity>>

    // 新增方法
    @Insert
    suspend fun addMethod(method: HookMethodEntity)

    // 删除方法
    @Delete
    suspend fun deleteMethod(method: HookMethodEntity)

    // 更新方法
    @Update
    suspend fun updateMethod(method: HookMethodEntity)
}