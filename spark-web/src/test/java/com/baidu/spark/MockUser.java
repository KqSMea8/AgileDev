package com.baidu.spark;

public class MockUser{
	private int userId;
	private int groupId;
	private String firstName;
	private String lastName;
	
	public MockUser(int userId, int groupId, String firstName, String lastName) {
		super();
		this.userId = userId;
		this.groupId = groupId;
		this.firstName = firstName;
		this.lastName = lastName;
	}
	public MockUser(){
		
	}
	public int getUserId() {
		return userId;
	}
	public void setUserId(int userId) {
		this.userId = userId;
	}
	public int getGroupId() {
		return groupId;
	}
	public void setGroupId(int groupId) {
		this.groupId = groupId;
	}
	public String getFirstName() {
		return firstName;
	}
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
	public String getLastName() {
		return lastName;
	}
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
}
	