package com.olbius.acc.utils.accounts;

import java.util.List;

public class AccountLeaf extends Account{

	@Override
	public List<Account> getListChild() {
		return null;
	}

	@Override
	public void addAccount(Account acc) {
	}

	@Override
	public void removeAccount(Account acc) {
	}

	@Override
	public void addListAccount(List<Account> accs) {
	}

	@Override
	public void addDirectedAccount(Account acc) {
		// TODO Auto-generated method stub
	}

	@Override
	public List<Account> getListDirectedChild() {
		// TODO Auto-generated method stub
		return null;
	}
}
