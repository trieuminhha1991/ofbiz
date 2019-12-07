package com.olbius.acc.utils.accounts;

import java.util.ArrayList;
import java.util.List;

public class AccountComposite extends Account{
	//Properties
	private List<Account> listChild;
	private List<Account> listDirectedChild;
	
	public AccountComposite() {
		listChild = new ArrayList<Account>();
		listDirectedChild = new ArrayList<Account>();
	}

	//Get All Child
	public List<Account> getListChild() {
		return listChild;
	}
	
	public void addAccount(Account acc) {
		listChild.add(acc);
	}
	
	@Override
	public void removeAccount(Account acc) {
		listChild.remove(acc);
	}

	@Override
	public void addListAccount(List<Account> accs) {
		listChild.addAll(accs);
	}

	public List<Account> getListDirectedChild() {
		return listDirectedChild;
	}

	@Override
	public void addDirectedAccount(Account acc) {
		// TODO Auto-generated method stub
		listDirectedChild.add(acc);
	}
}
