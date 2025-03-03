#MoneyKnow (原 cashbook 修改版)

⚠️ 注意

本项目为个人自用修改版本，基于 cashbook Fork 并重命名，非官方版本。
当前仍处于开发调试阶段，功能可能不稳定，不建议他人直接使用。
后续将用cursor辅助开发。
原始代码遵循 [MIT 许可证](LICENSE)，修改后的代码仍遵守该协议。


## 原仓库[Cashbook]ToDo 列表
- [x] 基本记账功能；
- [x] 资产管理功能；
- [x] 信用卡支持；
- [x] 多账本功能；
- [x] 检查更新功能；
- [x] 账单标签功能；
- [x] 自定义账单分类功能；
- [x] 账单搜索功能；
- [x] 备份与恢复；
- [x] WebDAV 备份支持；
- [x] 统计图表；
- [x] 离线版本；
- [x] 资产详情添加新建记录入口；
- [x] 标签可隐藏（特定时间使用之后不会再使用的标签隐藏，防止选择标签列表太过繁琐）；
- [x] 快捷进入菜单；
- [x] 使用本地图片作为账单背景；
- [x] 记录详情添加跳转资产功能；
- [x] 新增记账可关联图片功能；
- [ ] 导入导出为 csv；
- [ ] 多主题支持；
- [ ] proto 拆分，减少设置项修改导致的无意义刷新数据；
- [ ] 数据加载速度慢优化；
- [ ] 选择账单类型后，资产选择优化；
- [ ] markdown中链接点击事件；
- [ ] 记录金额显示优化，只显示最终金额；
- [ ] 信用卡账单日提醒及最后还款日提醒；
- [ ] 可报销记录未报销提醒；
- [ ] 修复切换账本后添加记录默认关联其它账本资产；


🎯 项目目标
在保留原 cashbook 核心记账功能的基础上，针对个人需求进行以下改造：

UI/UX 优化：调整界面布局和操作流程，适配个人使用习惯。

自动化增强：与 Tasker 联动，实现场景化记账（如定位到超市自动弹出食品分类）。

功能扩展：添加自定义报表、快捷记账小组件等实用功能。


🛠️ 当前状态
✅ 已完成修改
项目重命名为 MoneyKnow，替换代码中所有 cashbook 引用。

优化主界面布局。

🚧 开发中功能
Tasker 联动

通过 Tasker 监听短信/通知，自动解析消费金额和分类（如银行扣款短信 → 自动记账）。

📅 未来计划
添加「语音记账」快捷入口。

支持导出数据并生成可视化报表。

待定。


📌 免责声明
本项目为个人实验性修改，可能存在数据丢失风险，建议定期备份账本数据。

与 Tasker 联动功能需自行承担自动化脚本风险。


🤝 协作说明
暂不开放 PR：当前为个人开发阶段，代码结构可能大幅调整。

## 使用说明
运行项目前请在项目 /gradle 路径下按如下格式添加文件 signing.versions.toml

```toml
[versions]
# 密钥别名
keyAlias = ""
# 别名密码
keyPassword = ""
# 密钥文件路径
storeFile = ""
# 密钥密码
storePassword = ""
```
