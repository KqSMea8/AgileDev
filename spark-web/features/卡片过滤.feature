# language: zh-CN
功能: 卡片过滤
	#205 卡片过滤，如果人员字段填写中文，则查询出所有该属性值为空的卡片
	
	场景: 如果人员字段填写中文，则查询出所有该属性值为空的卡片
		假如 用户是tianyusong
		当 打开列表视图
		而且 输入创建人等于中文名
		那么 显示当前没有符合条件的卡片
	
	