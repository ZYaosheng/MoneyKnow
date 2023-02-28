package cn.wj.android.cashbook.core.data.repository

import cn.wj.android.cashbook.core.common.SWITCH_INT_ON
import cn.wj.android.cashbook.core.common.tools.dateFormat
import cn.wj.android.cashbook.core.data.helper.AssetHelper
import cn.wj.android.cashbook.core.database.dao.AssetDao
import cn.wj.android.cashbook.core.database.table.AssetTable
import cn.wj.android.cashbook.core.model.enums.AssetClassificationEnum
import cn.wj.android.cashbook.core.model.enums.ClassificationTypeEnum
import cn.wj.android.cashbook.core.model.model.AssetModel
import javax.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * 资产类型数据仓库
 *
 * > [王杰](mailto:15555650921@163.com) 创建于 2023/2/20
 */
class AssetRepository @Inject constructor(
    private val assetDao: AssetDao
) {

    suspend fun getAssetById(assetId: Long): AssetModel? = withContext(Dispatchers.IO) {
        assetDao.queryAssetById(assetId)?.asModel()
    }

    suspend fun getVisibleAssetsByBookId(bookId: Long): List<AssetModel> {
        return assetDao.queryVisibleAssetByBookId(bookId)
            .map { it.asModel() }
    }

    private fun AssetTable.asModel(): AssetModel {
        val classification = AssetClassificationEnum.valueOf(this.classification)
        return AssetModel(
            id = this.id ?: -1L,
            booksId = this.booksId,
            name = this.name,
            iconResId = AssetHelper.getIconResIdByType(classification),
            totalAmount = this.totalAmount.toString(),
            billingDate = this.billingDate,
            repaymentDate = this.repaymentDate,
            type = ClassificationTypeEnum.valueOf(this.type),
            classification = classification,
            invisible = this.invisible == SWITCH_INT_ON,
            openBank = this.openBank,
            cardNo = this.cardNo,
            remark = this.remark,
            sort = this.sort,
            modifyTime = this.modifyTime.dateFormat(),
            balance = this.balance.toString(),
        )
    }

}