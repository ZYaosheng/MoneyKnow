package cn.wj.android.cashbook.core.data.repository.impl

import cn.wj.android.cashbook.core.common.SWITCH_INT_ON
import cn.wj.android.cashbook.core.common.ext.logger
import cn.wj.android.cashbook.core.common.model.recordDataVersion
import cn.wj.android.cashbook.core.common.model.updateVersion
import cn.wj.android.cashbook.core.common.tools.DATE_FORMAT_NO_SECONDS
import cn.wj.android.cashbook.core.common.tools.DATE_FORMAT_YEAR_MONTH
import cn.wj.android.cashbook.core.common.tools.dateFormat
import cn.wj.android.cashbook.core.common.tools.parseDateLong
import cn.wj.android.cashbook.core.data.helper.AssetHelper
import cn.wj.android.cashbook.core.data.repository.RecordRepository
import cn.wj.android.cashbook.core.data.repository.asModel
import cn.wj.android.cashbook.core.data.repository.asTable
import cn.wj.android.cashbook.core.database.dao.RecordDao
import cn.wj.android.cashbook.core.database.dao.TransactionDao
import cn.wj.android.cashbook.core.datastore.datasource.AppPreferencesDataSource
import cn.wj.android.cashbook.core.model.entity.RecordViewsEntity
import cn.wj.android.cashbook.core.model.enums.AssetClassificationEnum
import cn.wj.android.cashbook.core.model.enums.RecordTypeCategoryEnum
import cn.wj.android.cashbook.core.model.model.RecordModel
import cn.wj.android.cashbook.core.model.model.TagModel
import java.util.Calendar
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext

class RecordRepositoryImpl @Inject constructor(
    private val recordDao: RecordDao,
    private val transactionDao: TransactionDao,
    private val appPreferencesDataSource: AppPreferencesDataSource
) : RecordRepository {

    override val currentMonthRecordListData: Flow<List<RecordModel>> =
        combine(recordDataVersion, appPreferencesDataSource.appData) { _, appData ->
            logger().i("currentMonthRecordListData update")
            queryCurrentMonthRecordByBooksId(appData.currentBookId)
        }

    override suspend fun queryById(
        recordId: Long,
        coroutineContext: CoroutineContext
    ): RecordModel? = withContext(coroutineContext) {
        recordDao.queryById(recordId)?.asModel()
    }

    override suspend fun queryRelatedById(
        recordId: Long,
        coroutineContext: CoroutineContext
    ): List<RecordModel> =
        withContext(coroutineContext) {
            recordDao.queryRelatedById(recordId)
                .map { it.asModel() }
        }

    private suspend fun queryCurrentMonthRecordByBooksId(
        booksId: Long,
        coroutineContext: CoroutineContext = Dispatchers.IO
    ): List<RecordModel> =
        withContext(coroutineContext) {
            val monthFirst =
                "${Calendar.getInstance().timeInMillis.dateFormat(DATE_FORMAT_YEAR_MONTH)}-01 00:00:00"
            val result = recordDao.queryByBooksIdAfterDate(booksId, monthFirst.parseDateLong())
                .map { it.asModel() }
            logger().i("queryCurrentMonthRecordByBooksId(booksId = <$booksId>) monthFirst = <$monthFirst>, result = $result")
            result
        }

    override suspend fun updateRecord(
        record: RecordModel, tags: List<TagModel>,
        coroutineContext: CoroutineContext
    ) =
        withContext(coroutineContext) {
            logger().i("updateRecord(record = <$record>, tags = <$tags>")
            transactionDao.updateRecordTransaction(record.asTable(), tags.map { it.id })
            recordDataVersion.updateVersion()
        }

    override suspend fun deleteRecord(
        recordId: Long,
        coroutineContext: CoroutineContext
    ) = withContext(coroutineContext) {
        transactionDao.deleteRecordTransaction(recordId)
        recordDataVersion.updateVersion()
    }

    override suspend fun queryExpenditureRecordAfterDate(
        reimburse: Boolean,
        dataTime: Long,
        coroutineContext: CoroutineContext
    ): List<RecordModel> = withContext(coroutineContext) {
        val currentBookId = appPreferencesDataSource.appData.first().currentBookId
        if (reimburse) {
            recordDao.queryReimburseByBooksIdAfterDate(currentBookId, dataTime)
        } else {
            recordDao.queryByBooksIdAfterDate(currentBookId, dataTime)
        }
            .map { it.asModel() }
    }

    override suspend fun queryExpenditureRecordByAmountOrRemark(
        keyword: String,
        coroutineContext: CoroutineContext
    ): List<RecordViewsEntity> =
        withContext(coroutineContext) {
            val currentBookId = appPreferencesDataSource.appData.first().currentBookId
            recordDao.query(currentBookId).map {
                RecordViewsEntity(
                    id = it.id,
                    typeCategory = RecordTypeCategoryEnum.valueOf(it.typeCategory),
                    typeName = it.typeName,
                    typeIconResName = it.typeIconResName,
                    assetName = it.assetName,
                    assetIconResId = it.assetClassification?.run {
                        AssetHelper.getIconResIdByType(
                            AssetClassificationEnum.valueOf(this)
                        )
                    },
                    relatedAssetName = it.relatedAssetName,
                    relatedAssetIconResId = it.relatedAssetClassification?.run {
                        AssetHelper.getIconResIdByType(
                            AssetClassificationEnum.valueOf(this)
                        )
                    },
                    amount = it.amount.toString(),
                    charges = it.charges.toString(),
                    concessions = it.concessions.toString(),
                    remark = it.remark,
                    reimbursable = it.reimbursable == SWITCH_INT_ON,
                    recordTime = it.recordTime.dateFormat(DATE_FORMAT_NO_SECONDS),
                    relatedTags = listOf(),
                    relatedRecord = listOf()
                )
            }
        }

    override suspend fun queryPagingRecordListByAssetId(
        assetId: Long,
        page: Int,
        pageSize: Int,
        coroutineContext: CoroutineContext
    ): List<RecordModel> = withContext(coroutineContext) {
        recordDao.queryRecordByAssetId(
            booksId = appPreferencesDataSource.appData.first().currentBookId,
            assetId = assetId,
            pageNum = page * pageSize,
            pageSize = pageSize,
        ).map {
            it.asModel()
        }
    }
}