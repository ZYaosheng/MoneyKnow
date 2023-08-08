package cn.wj.android.cashbook.feature.records.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cn.wj.android.cashbook.core.common.ext.decimalFormat
import cn.wj.android.cashbook.core.common.ext.logger
import cn.wj.android.cashbook.core.common.ext.toBigDecimalOrZero
import cn.wj.android.cashbook.core.model.entity.RecordDayEntity
import cn.wj.android.cashbook.core.model.entity.RecordViewsEntity
import cn.wj.android.cashbook.core.model.model.ResultModel
import cn.wj.android.cashbook.core.ui.DialogState
import cn.wj.android.cashbook.domain.usecase.DeleteRecordUseCase
import cn.wj.android.cashbook.domain.usecase.GetCurrentMonthRecordViewsMapUseCase
import cn.wj.android.cashbook.domain.usecase.GetCurrentMonthRecordViewsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import java.math.BigDecimal
import java.time.YearMonth
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@HiltViewModel
class LauncherContentViewModel @Inject constructor(
    getCurrentMonthRecordViewsUseCase: GetCurrentMonthRecordViewsUseCase,
    getCurrentMonthRecordViewsMapUseCase: GetCurrentMonthRecordViewsMapUseCase,
    private val deleteRecordUseCase: DeleteRecordUseCase,
) : ViewModel() {

    /** 删除记录失败错误信息 */
    var shouldDisplayDeleteFailedBookmark by mutableStateOf(0)
        private set

    /** 弹窗状态 */
    var dialogState by mutableStateOf<DialogState>(DialogState.Dismiss)
        private set

    /** 记录详情数据 */
    var viewRecord by mutableStateOf<RecordViewsEntity?>(null)
        private set

    /** 当前选择时间 */
    private val _dateData = MutableStateFlow(YearMonth.now())
    val dateData: StateFlow<YearMonth> = _dateData

    /** 当前月记录数据 */
    private val currentMonthRecordListData = _dateData.flatMapLatest { date ->
        getCurrentMonthRecordViewsUseCase(date.year.toString(), date.monthValue.toString())
    }

    val uiState = currentMonthRecordListData
        .mapLatest { list ->
            val recordMap = getCurrentMonthRecordViewsMapUseCase(list)
            var totalIncome = BigDecimal.ZERO
            var totalExpenditure = BigDecimal.ZERO
            recordMap.keys.forEach {
                totalIncome += it.dayIncome.toBigDecimalOrZero()
                totalExpenditure += it.dayExpand.toBigDecimalOrZero()
            }
            LauncherContentUiState.Success(
                monthIncome = totalIncome.decimalFormat(),
                monthExpand = totalExpenditure.decimalFormat(),
                monthBalance = (totalIncome - totalExpenditure).decimalFormat(),
                recordMap = recordMap,
            )
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = LauncherContentUiState.Loading,
        )

    fun onBookmarkDismiss() {
        shouldDisplayDeleteFailedBookmark = 0
    }

    fun onSheetDismiss() {
        viewRecord = null
    }

    fun onRecordItemClick(record: RecordViewsEntity) {
        viewRecord = record
    }

    fun onRecordItemDeleteClick(recordId: Long) {
        onSheetDismiss()
        dialogState = DialogState.Shown(DialogType.DeleteRecord(recordId))
    }

    fun onDeleteRecordConfirm(recordId: Long) {
        viewModelScope.launch {
            try {
                deleteRecordUseCase(recordId)
                // 删除成功，隐藏弹窗
                onDialogDismiss()
            } catch (throwable: Throwable) {
                this@LauncherContentViewModel.logger()
                    .e(throwable, "tryDeleteRecord(recordId = <$recordId>) failed")
                // 提示
                shouldDisplayDeleteFailedBookmark = ResultModel.Failure.FAILURE_THROWABLE
            }
        }
    }

    fun showDateSelectDialog() {
        viewModelScope.launch {
            dialogState = DialogState.Shown(DialogType.SelectDate(_dateData.first()))
        }
    }

    fun onDateSelected(date: YearMonth) {
        onDialogDismiss()
        _dateData.tryEmit(date)
    }

    fun onDialogDismiss() {
        dialogState = DialogState.Dismiss
    }

}

sealed interface LauncherContentUiState {
    object Loading : LauncherContentUiState
    data class Success(
        val monthIncome: String,
        val monthExpand: String,
        val monthBalance: String,
        val recordMap: Map<RecordDayEntity, List<RecordViewsEntity>>,
    ) : LauncherContentUiState
}

sealed interface DialogType {
    data class DeleteRecord(val recordId: Long) : DialogType
    data class SelectDate(val date: YearMonth) : DialogType
}