package cn.wj.android.cashbook.data.entity

import android.os.Parcelable
import androidx.databinding.ObservableBoolean
import cn.wj.android.cashbook.data.enums.RecordTypeEnum
import cn.wj.android.cashbook.data.enums.TypeEnum
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize

/**
 * 类型数据实体类
 *
 * @param id 主键自增长
 * @param parentId 子类时父类id
 * @param name 类型名称
 * @param iconResName 图标资源名称
 * @param type 类型类别
 * @param recordType 记录类型
 * @param childEnable 是否允许子类型
 * @param refund 是否是退款
 * @param system 是否是系统类型
 * @param sort 排序
 * @param childList 子类型列表
 *
 * > [王杰](mailto:15555650921@163.com) 创建于 2021/6/8
 */
@Parcelize
data class TypeEntity(
    val id: Long,
    val parentId: Long,
    val name: String,
    val iconResName: String,
    val type: TypeEnum,
    val recordType: RecordTypeEnum,
    val childEnable: Boolean,
    val refund: Boolean,
    val system: Boolean,
    val sort: Int,
    val childList: List<TypeEntity>
) : Parcelable {

    /** 是否显示更多图标 */
    @IgnoredOnParcel
    val showMore: Boolean
        get() = type == TypeEnum.FIRST && childEnable && childList.isNotEmpty()

    /** 标记 - 是否选中 */
    @IgnoredOnParcel
    val selected: ObservableBoolean by lazy {
        ObservableBoolean(false)
    }

    /** 标记 - 是否展开 */
    @IgnoredOnParcel
    val expand: ObservableBoolean by lazy {
        ObservableBoolean(false)
    }

    companion object {

        fun empty(): TypeEntity {
            return TypeEntity(
                id = -1L,
                parentId = -1L,
                name = "",
                iconResName = "",
                type = TypeEnum.FIRST,
                recordType = RecordTypeEnum.EXPENDITURE,
                childEnable = false,
                refund = false,
                system = false,
                sort = -1,
                childList = arrayListOf()
            )
        }

        fun newFirstExpenditure(name: String, iconResName: String, sort: Int, childEnable: Boolean = true): TypeEntity {
            return TypeEntity(
                id = -1L,
                parentId = -1L,
                name = name,
                iconResName = iconResName,
                type = TypeEnum.FIRST,
                recordType = RecordTypeEnum.EXPENDITURE,
                childEnable = childEnable,
                refund = false,
                system = true,
                sort = sort,
                childList = arrayListOf()
            )
        }

        fun newSecondExpenditure(parentId: Long, name: String, iconResName: String, sort: Int): TypeEntity {
            return TypeEntity(
                id = -1L,
                parentId = parentId,
                name = name,
                iconResName = iconResName,
                type = TypeEnum.SECOND,
                recordType = RecordTypeEnum.EXPENDITURE,
                childEnable = false,
                refund = false,
                system = true,
                sort = sort,
                childList = arrayListOf()
            )
        }

        fun newFirstIncome(name: String, iconResName: String, sort: Int, childEnable: Boolean = true, refund: Boolean = false): TypeEntity {
            return TypeEntity(
                id = -1L,
                parentId = -1L,
                name = name,
                iconResName = iconResName,
                type = TypeEnum.FIRST,
                recordType = RecordTypeEnum.INCOME,
                childEnable = childEnable,
                refund = refund,
                system = true,
                sort = sort,
                childList = arrayListOf()
            )
        }

        fun newSecondIncome(parentId: Long, name: String, iconResName: String, sort: Int): TypeEntity {
            return TypeEntity(
                id = -1L,
                parentId = parentId,
                name = name,
                iconResName = iconResName,
                type = TypeEnum.SECOND,
                recordType = RecordTypeEnum.INCOME,
                childEnable = false,
                refund = false,
                system = true,
                sort = sort,
                childList = arrayListOf()
            )
        }

        fun newFirstTransfer(name: String, iconResName: String, sort: Int, childEnable: Boolean = true): TypeEntity {
            return TypeEntity(
                id = -1L,
                parentId = -1L,
                name = name,
                iconResName = iconResName,
                type = TypeEnum.FIRST,
                recordType = RecordTypeEnum.TRANSFER,
                childEnable = childEnable,
                refund = false,
                system = true,
                sort = sort,
                childList = arrayListOf()
            )
        }

        fun newSecondTransfer(parentId: Long, name: String, iconResName: String, sort: Int): TypeEntity {
            return TypeEntity(
                id = -1L,
                parentId = parentId,
                name = name,
                iconResName = iconResName,
                type = TypeEnum.SECOND,
                recordType = RecordTypeEnum.TRANSFER,
                childEnable = false,
                refund = false,
                system = true,
                sort = sort,
                childList = arrayListOf()
            )
        }
    }
}