package org.ofbiz.customer;


public class Customer{
	
	private String name;
	private String _roleType;
	private String _partyType;
	private String channel;
	
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String get_roleType() {
		return _roleType;
	}
	public void set_roleType(String _roleType) {
		this._roleType = _roleType;
	}
	public String get_partyType() {
		return _partyType;
	}
	public void set_partyType(String _partyType) {
		this._partyType = _partyType;
	}
	public String getChannel() {
		return channel;
	}
	public void setChannel(String channel) {
		this.channel = channel;
	}
	
}