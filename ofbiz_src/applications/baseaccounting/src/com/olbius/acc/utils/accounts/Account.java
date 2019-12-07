package com.olbius.acc.utils.accounts;

import java.util.List;

public abstract class Account {
	//Properties
	protected AccountEntity acc;
	
	//Getters And Setters
	public AccountEntity getAcc() {
		return acc;
	}
	public void setAcc(AccountEntity acc) {
		this.acc = acc;
	}
	
	//Abstract method
	public abstract List<Account> getListChild();
	public abstract List<Account> getListDirectedChild();
	public abstract void addAccount(Account acc);
	public abstract void addListAccount(List<Account> accs);
	public abstract void addDirectedAccount(Account acc);
	public abstract void removeAccount(Account acc);
}
