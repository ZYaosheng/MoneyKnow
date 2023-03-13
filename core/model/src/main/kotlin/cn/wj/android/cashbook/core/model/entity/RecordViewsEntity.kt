package cn.wj.android.cashbook.core.model.entity


/**
 * 记录数据实体类
 *
 * @param id 主键自增长
 *  @param booksId 关联账本 id
 * @param type 记录类型
 * @param asset 关联资产
 * @param relatedAsset 转账转入资产
 * @param amount 记录金额
 * @param charges 转账手续费
 * @param concessions 优惠
 * @param remark 备注
 * @param reimbursable 能否报销
 * @param recordTime 修改时间
 *
 * > [王杰](mailto:15555650921@163.com) 创建于 2021/6/10
 */
data class RecordViewsEntity(
    val id: Long,
    val booksId: Long,
    val type: RecordTypeEntity,
    val asset: AssetEntity?,
    val relatedAsset: AssetEntity?,
    val amount: String,
    val charges: String,
    val concessions: String,
    val remark: String,
    val reimbursable: Boolean,
    val relatedTags: List<TagEntity>,
    val relatedRecord: List<RecordViewsEntity>,
    val recordTime: String,
)