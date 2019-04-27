package com.baidu.spark.service.impl.helper.copyspace.metadata.impl;

import java.util.ArrayList;
import java.util.List;

import com.baidu.spark.model.Group;
import com.baidu.spark.service.impl.helper.copyspace.metadata.Metadata;
import com.baidu.spark.service.impl.helper.copyspace.option.CheckboxImportOption;
import com.baidu.spark.service.impl.helper.copyspace.option.ImportOption;
/**
 * 用户组的空间定义的元数据对象
 * @author zhangjing_pe
 *
 */
public class GroupMetadata extends Metadata {
	
	public static final String IMPORT_GROUP_INFO_KEY = "importGroupInfo";
	
	public static final String IMPORT_GROUP_USER_KEY = "importGroupUser";

	private List<Group> groups = new ArrayList<Group>();
	
	private List<Integer> permission = new ArrayList<Integer>();
	
	public GroupMetadata(){
		importOptions = new ArrayList<ImportOption<?>>();
		//添加导入选项：是否导入用户组信息
		ImportOption<GroupMetadata> option = new CheckboxImportOption<GroupMetadata>(GroupMetadata.class, IMPORT_GROUP_INFO_KEY, "space.copyFromExist.importGroupInfo","1");
		importOptions.add(option);
		//添加导入选项：是否导入用户组成员
		option = new CheckboxImportOption<GroupMetadata>(GroupMetadata.class, IMPORT_GROUP_USER_KEY, "space.copyFromExist.importGroupUser","1");
		importOptions.add(option);
	}

	public List<Group> getGroups() {
		return groups;
	}

	public void setGroups(List<Group> groups) {
		this.groups = groups;
	}

	public List<Integer> getPermission() {
		return permission;
	}

	public void setPermission(List<Integer> permission) {
		this.permission = permission;
	}
	
	
}
