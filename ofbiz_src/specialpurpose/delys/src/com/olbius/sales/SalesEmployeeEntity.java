package com.olbius.sales;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

public class SalesEmployeeEntity {
	private String partyId;
	private String firstName;
	private String lastName;
	private String middleName;
	private String groupName;
	private String fullName;
	private Date birthday;
	private String statusId;
	private String description;
	private List<SalesEmployeeEntity> listChild;
	private SalesEmployeeEntity manager;

	public SalesEmployeeEntity() {
		this.listChild = new ArrayList<SalesEmployeeEntity>();
	}

	public SalesEmployeeEntity(SalesEmployeeEntity tmp) {
		super();
		this.partyId = tmp.partyId;
		this.firstName = tmp.firstName;
		this.lastName = tmp.lastName;
		this.middleName = tmp.middleName;
		this.groupName = tmp.groupName;
		this.fullName = tmp.fullName;
		this.birthday = tmp.birthday;
		this.statusId = tmp.statusId;
		this.description = tmp.description;
		this.listChild = tmp.listChild;
		this.manager = tmp.manager;
	}

	public void addChild(SalesEmployeeEntity child) {
		if (child != null) {
			this.listChild.add(child);
		}
	}

	public void addChild(List<SalesEmployeeEntity> children) {
		if (children != null) {
			this.listChild.addAll(children);
		}
	}

	public String getPartyId() {
		return partyId;
	}

	public void setPartyId(String partyId) {
		this.partyId = partyId;
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

	public String getMiddleName() {
		return middleName;
	}

	public void setMiddleName(String middleName) {
		this.middleName = middleName;
	}

	public String getFullName() {
		return fullName;
	}

	public void setFullName(String fullName) {
		this.fullName = fullName;
	}

	public String getGroupName() {
		return groupName;
	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}

	public Date getBirthday() {
		return birthday;
	}

	public void setBirthday(Date birthday) {
		this.birthday = birthday;
	}

	public String getStatusId() {
		return statusId;
	}

	public void setStatusId(String statusId) {
		this.statusId = statusId;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public List<SalesEmployeeEntity> getListChild() {
		return listChild;
	}

	public void setListChild(List<SalesEmployeeEntity> listChild) {
		this.listChild = listChild;
	}

	public SalesEmployeeEntity getManager() {
		return manager;
	}

	public void setManager(SalesEmployeeEntity manager) {
		this.manager = manager;
	}
}
