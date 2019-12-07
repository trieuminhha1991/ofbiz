package org.ofbiz.salesman;

import java.util.List;
import java.util.Map;

import org.ofbiz.customer.Customer;

public class Salesman{
	public String fullName;
	public String[] _roleType;
	public String[] _route ; 
	private String partyId;
	private List<Map<String,Customer>> listCustomer;
	
	
	public Salesman(){
		return ;
	}
	
	//get and setter
	public String getName(){
		return this.fullName;
	}
	
	public void setName(String name){
		this.fullName = name;
	}
	
	public String[] getRole(){
		return this._roleType;
	}
	
	public void setRole(String[] role){
		this._roleType = role;
	}
	
	public String[] getRoute(){
		return this._route;
	}
	
	public void setRoute(String[] route){
		this._route = route;
	}

	public String getPartyId() {
		return partyId;
	}

	public void setPartyId(String partyId) {
		this.partyId = partyId;
	}

	
}