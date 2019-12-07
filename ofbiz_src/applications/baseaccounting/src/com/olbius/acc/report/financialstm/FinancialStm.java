package com.olbius.acc.report.financialstm;

import java.util.ArrayList;
import java.util.List;

public abstract class FinancialStm {
	protected List<FinancialStm> children;

	public FinancialStm() {
		super();
		this.children = new ArrayList<FinancialStm>();
	}

	public void addChild(FinancialStm child) {
		children.add(child);
	}

	public void removeChild(FinancialStm child) {
		children.remove(child);
	}

	public void addAllChild(List<FinancialStm> listStms) {
		if (listStms != null) {
			children.addAll(listStms);
		}
	}

	public List<FinancialStm> getChildren() {
		return children;
	}

	public void setChildren(List<FinancialStm> children) {
		this.children = children;
	}
}