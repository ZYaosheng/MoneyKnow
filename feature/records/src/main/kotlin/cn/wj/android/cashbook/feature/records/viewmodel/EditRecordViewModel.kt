@file:OptIn(ExperimentalCoroutinesApi::class)

package cn.wj.android.cashbook.feature.records.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cn.wj.android.cashbook.core.common.Symbol
import cn.wj.android.cashbook.core.common.ext.decimalFormat
import cn.wj.android.cashbook.core.common.ext.toBigDecimalOrZero
import cn.wj.android.cashbook.core.data.repository.AssetRepository
import cn.wj.android.cashbook.core.data.repository.TypeRepository
import cn.wj.android.cashbook.core.model.entity.AssetEntity
import cn.wj.android.cashbook.core.model.entity.RecordEntity
import cn.wj.android.cashbook.core.model.entity.RecordTypeEntity
import cn.wj.android.cashbook.core.model.entity.TagEntity
import cn.wj.android.cashbook.core.model.enums.ClassificationTypeEnum
import cn.wj.android.cashbook.core.model.enums.RecordTypeCategoryEnum
import cn.wj.android.cashbook.core.model.transfer.asEntity
import cn.wj.android.cashbook.domain.usecase.GetDefaultRecordUseCase
import cn.wj.android.cashbook.domain.usecase.GetDefaultTagListUseCase
import cn.wj.android.cashbook.domain.usecase.asEntity
import cn.wj.android.cashbook.feature.records.enums.EditRecordBottomSheetEnum
import dagger.hilt.android.lifecycle.HiltViewModel
import java.math.BigDecimal
import javax.inject.Inject
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@HiltViewModel
class EditRecordViewModel @Inject constructor(
    assetRepository: AssetRepository,
    typeRepository: TypeRepository,
    getDefaultRecordUseCase: GetDefaultRecordUseCase,
    getDefaultTagListUseCase: GetDefaultTagListUseCase,
) : ViewModel() {

    /** 显示底部弹窗数据 */
    val bottomSheetData: MutableStateFlow<EditRecordBottomSheetEnum> =
        MutableStateFlow(EditRecordBottomSheetEnum.NONE)

    /** 记录 id 数据 */
    val recordIdData: MutableStateFlow<Long> = MutableStateFlow(-1L)

    /** 默认记录数据 */
    private val defaultRecordData: Flow<RecordEntity> = recordIdData.mapLatest {
        getDefaultRecordUseCase(it)
    }

    /** 经过修改的记录数据 */
    private val mutableRecordData: MutableStateFlow<RecordEntity?> = MutableStateFlow(null)

    /** 实际显示数据源 */
    private val recordData: Flow<RecordEntity> =
        combine(defaultRecordData, mutableRecordData) { default, modified ->
            modified ?: default
        }

    /** 金额 */
    val amountData: StateFlow<String> = recordData
        .mapLatest { it.amount }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(),
            initialValue = ""
        )

    /** 选中类型数据 */
    val selectedTypeData: StateFlow<RecordTypeEntity?> = recordData
        .mapLatest { typeRepository.getNoNullRecordTypeById(it.type).asEntity() }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(),
            initialValue = null
        )

    private val defaultTypeCategory: Flow<RecordTypeCategoryEnum> = selectedTypeData
        .mapLatest {
            it?.typeCategory ?: RecordTypeCategoryEnum.EXPENDITURE
        }

    private val mutableTypeCategory: MutableStateFlow<RecordTypeCategoryEnum?> =
        MutableStateFlow(null)

    /** 类型标签数据 */
    val typeCategory: StateFlow<RecordTypeCategoryEnum> =
        combine(defaultTypeCategory, mutableTypeCategory) { default, mutable ->
            mutable ?: default
        }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(),
                initialValue = RecordTypeCategoryEnum.EXPENDITURE
            )

    /** 备注文本 */
    val remarkData: StateFlow<String> = recordData
        .mapLatest { it.remark }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(),
            initialValue = ""
        )

    /** 资产文本 */
    val assetData: StateFlow<String> = recordData
        .mapLatest {
            val asset = assetRepository.getAssetById(it.assetId)?.asEntity()
            if (null == asset) {
                ""
            } else {
                "${asset.name}(${Symbol.rmb} ${asset.displayBalance})"
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(),
            initialValue = ""
        )

    /** 关联资产文本 */
    val relatedAssetData: StateFlow<String> = recordData
        .mapLatest {
            val asset = assetRepository.getAssetById(it.relatedAssetId)?.asEntity()
            if (null == asset) {
                ""
            } else {
                "${asset.name}(${Symbol.rmb} ${asset.displayBalance})"
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(),
            initialValue = ""
        )

    /** 时间文本 */
    val dateTimeData: StateFlow<String> = recordData
        .mapLatest {
            it.modifyTime
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(),
            initialValue = ""
        )

    /** TODO 默认标签数据 */
    private val defaultTagsData: Flow<List<TagEntity>> = recordIdData.mapLatest {
        getDefaultTagListUseCase(it)
    }

    /** 可修改的标签数据 */
    private val mutableTagsData: MutableStateFlow<List<TagEntity>?> = MutableStateFlow(listOf())

    /** 最终标签数据 */
    private val tagsData: StateFlow<List<TagEntity>> =
        combine(defaultTagsData, mutableTagsData) { default, mutable ->
            mutable ?: default
        }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(),
                initialValue = listOf()
            )

    /** 标签 id 列表 */
    val tagsIdData: StateFlow<List<Long>> = tagsData
        .mapLatest { list ->
            list.map { it.id }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(),
            initialValue = listOf()
        )

    /** 标签文本 */
    val tagsTextData: StateFlow<String> = tagsData
        .mapLatest { list ->
            StringBuilder().run {
                list.forEach { tag ->
                    if (!isBlank()) {
                        append(",")
                    }
                    append(tag.name)
                }
                toString()
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(),
            initialValue = ""
        )

    /** 手续费文本 */
    val chargesData: StateFlow<String> = recordData
        .mapLatest {
            if (it.charges.toBigDecimalOrZero() == BigDecimal.ZERO) {
                ""
            } else {
                "${Symbol.rmb}${it.charges}"
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(),
            initialValue = ""
        )

    /** 优惠文本 */
    val concessionsData: StateFlow<String> = recordData
        .mapLatest {
            if (it.concessions.toBigDecimalOrZero() == BigDecimal.ZERO) {
                ""
            } else {
                "${Symbol.rmb}${it.concessions}"
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(),
            initialValue = ""
        )

    /** 是否可报销 */
    val reimbursableData: StateFlow<Boolean> = recordData
        .mapLatest { it.reimbursable }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(),
            initialValue = false
        )

    /** 类型分类点击切换为 [typeCategory] */
    fun onTypeCategoryTabSelected(typeCategory: RecordTypeCategoryEnum) {
        viewModelScope.launch {
            mutableTypeCategory.value = typeCategory
        }
    }

    /** 类型点击切换为 [type] */
    fun onTypeClick(type: RecordTypeEntity?) {
        viewModelScope.launch {
            type?.let {
                mutableRecordData.value = recordData.first().copy(type = it.id)
            }
        }
    }

    /** 备注文本变化为 [remark] */
    fun onRemarkTextChanged(remark: String) {
        viewModelScope.launch {
            mutableRecordData.value = recordData.first().copy(remark = remark)
        }
    }

    fun onBottomSheetAction(action: EditRecordBottomSheetEnum) {
        bottomSheetData.value = action
    }

    fun onAssetItemClick(item: AssetEntity?) {
        viewModelScope.launch {
            mutableRecordData.value = recordData.first().copy(assetId = item?.id ?: -1L)
        }
    }

    fun onRelatedAssetItemClick(item: AssetEntity?) {
        viewModelScope.launch {
            mutableRecordData.value = recordData.first().copy(relatedAssetId = item?.id ?: -1L)
        }
    }

    fun onTagItemClick(item: TagEntity) {
        viewModelScope.launch {
            val list = arrayListOf<TagEntity>()
            var needAdd = true
            tagsData.first().forEach {
                if (it.id != item.id) {
                    list.add(it)
                } else {
                    needAdd = false
                }
            }
            if (needAdd) {
                list.add(item)
            }
            mutableTagsData.value = list
        }
    }

    /** TODO 尝试保存记录 */
    fun trySaveRecord() {

    }
}

val AssetEntity.displayBalance: String
    get() = if (type == ClassificationTypeEnum.CREDIT_CARD_ACCOUNT) {
        (totalAmount.toBigDecimalOrZero() - balance.toBigDecimalOrZero()).decimalFormat()
    } else {
        balance
    }