package cn.wj.android.cashbook.core.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import cn.wj.android.cashbook.core.database.table.TagTable

/**
 * 记录类型数据库操作类
 *
 * > [王杰](mailto:15555650921@163.com) 创建于 2023/2/20
 */
@Dao
interface TagDao {

    @Insert
    suspend fun insert(tag: TagTable): Long

    @Update
    suspend fun update(tag: TagTable)

    @Delete
    suspend fun delete(tag: TagTable)

    @Query("SELECT * FROM db_tag")
    suspend fun queryAll(): List<TagTable>

    @Query("SELECT * FROM db_tag WHERE id IN (SELECT tag_id FROM db_tag_with_record WHERE record_id=:recordId)")
    suspend fun queryByRecordId(recordId: Long): List<TagTable>

    @Query(value = """
        DELETE FROM db_tag_with_record
        WHERE record_id IN (SELECT id FROM db_record WHERE asset_id=:assetId OR into_asset_id=:assetId)
    """)
    suspend fun deleteRelatedWithAsset(assetId: Long)

    @Query(value="""
        SELECT COUNT(*) FROM db_tag WHERE name=:name
    """)
    suspend fun countByName(name:String):Int
}