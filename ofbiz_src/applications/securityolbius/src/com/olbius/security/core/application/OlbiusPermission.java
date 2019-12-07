package com.olbius.security.core.application;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import com.olbius.security.api.Permission;

public class OlbiusPermission implements Permission {

	private String name;
	private List<String> permissionInclude;
	private Timestamp fromDate;
	private Timestamp thruDate;
	private boolean allow;
	
	public OlbiusPermission() {
	}
	
	public OlbiusPermission(String name, Timestamp fromDate, Timestamp thruDate, boolean allow) {
		this.name = name;
		this.fromDate = fromDate;
		this.thruDate = thruDate;
		this.allow = allow;
	}
	
	@Override
	public Timestamp getFromDate() {
		return fromDate;
	}
	
	@Override
	public void setFromDate(Timestamp fromDate) {
		this.fromDate = fromDate;
	}
	
	@Override
	public Timestamp getThruDate() {
		return thruDate;
	}
	
	@Override
	public void setThruDate(Timestamp thruDate) {
		this.thruDate = thruDate;
	}
	
	@Override
	public boolean isAllow() {
		return allow;
	}
	
	@Override
	public void setAllow(boolean allow) {
		this.allow = allow;
	}
	
	@Override
	public String getName() {
		return name;
	}

	@Override
	public void setName(String name) {
		this.name = name;
	}

	@Override
	public List<String> getPermissionInclude() {
		if(permissionInclude == null) {
			permissionInclude = new ArrayList<String>();
		}
		return permissionInclude;
	}

	@Override
	public void setPermissionInclude(List<String> permissionInclude) {
		this.permissionInclude = permissionInclude;
	}

	@Override
	public boolean isTime(Timestamp timestamp) {
		return timestamp.after(fromDate) && (thruDate == null || timestamp.before(thruDate));
	}

	@Override
	public boolean isInclude(String perm) {
		if(name.equals(perm) || permissionInclude.contains(perm)) {
			return true;
		}
		return false;
	}

}
