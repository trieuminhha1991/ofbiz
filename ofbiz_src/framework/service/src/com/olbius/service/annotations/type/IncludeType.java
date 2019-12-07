package com.olbius.service.annotations.type;

public enum IncludeType implements IType<String> {
	ALL("all"), PK("pk"), NONPK("nonpk"), NULL("");

	private final String s;

	private IncludeType(final String s) {
		this.s = s;
	}

	@Override
	public String value() {
		return this.s;
	}

}
