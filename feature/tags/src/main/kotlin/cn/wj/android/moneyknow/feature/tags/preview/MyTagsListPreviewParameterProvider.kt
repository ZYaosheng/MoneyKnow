/*
 * Copyright 2021 The Cashbook Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package cn.wj.android.moneyknow.feature.tags.preview

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import cn.wj.android.moneyknow.core.model.model.TagModel

/**
 * 标签列表预览参数提供者
 *
 * > [王杰](mailto:15555650921@163.com) 创建于 2024/3/28
 */
class MyTagsListPreviewParameterProvider : PreviewParameterProvider<List<TagModel>> {
    override val values: Sequence<List<TagModel>>
        get() = MyTagsData.tagListData.asSequence()
}

object MyTagsData {
    val tagListData = listOf(
        emptyList(),
        listOf(
            TagModel(id = 1L, name = "测试1", invisible = false),
            TagModel(id = 2L, name = "测试标签2", invisible = false),
            TagModel(id = 3L, name = "测试标签3333", invisible = true),
            TagModel(id = 4L, name = "测试444444", invisible = true),
            TagModel(id = 5L, name = "标签5", invisible = false),
            TagModel(id = 6L, name = "测试标签66666666", invisible = true),
            TagModel(id = 7L, name = "测试7", invisible = false),
            TagModel(id = 8L, name = "测试标签88888", invisible = true),
            TagModel(id = 9L, name = "测9", invisible = true),
            TagModel(id = 0L, name = "0", invisible = true),
        ),
        listOf(
            TagModel(id = 1L, name = "测试1", invisible = false),
            TagModel(id = 2L, name = "测试标签2", invisible = false),
            TagModel(id = 3L, name = "测试标签3333", invisible = false),
            TagModel(id = 4L, name = "测试444444", invisible = false),
            TagModel(id = 5L, name = "标签5", invisible = false),
            TagModel(id = 6L, name = "测试标签66666666", invisible = false),
            TagModel(id = 7L, name = "测试7", invisible = false),
            TagModel(id = 8L, name = "测试标签88888", invisible = false),
            TagModel(id = 9L, name = "测9", invisible = false),
            TagModel(id = 0L, name = "0", invisible = false),
        ),
        listOf(
            TagModel(id = 1L, name = "测试1", invisible = true),
            TagModel(id = 2L, name = "测试标签2", invisible = true),
            TagModel(id = 3L, name = "测试标签3333", invisible = true),
            TagModel(id = 4L, name = "测试444444", invisible = true),
            TagModel(id = 5L, name = "标签5", invisible = true),
            TagModel(id = 6L, name = "测试标签66666666", invisible = true),
            TagModel(id = 7L, name = "测试7", invisible = true),
            TagModel(id = 8L, name = "测试标签88888", invisible = true),
            TagModel(id = 9L, name = "测9", invisible = true),
            TagModel(id = 0L, name = "0", invisible = true),
        ),
    )
}
