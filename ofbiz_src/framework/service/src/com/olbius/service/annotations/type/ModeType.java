package com.olbius.service.annotations.type;

public enum ModeType implements IType<String> {
	IN("IN"), OUT("OUT"), INOUT("INOUT"), NULL("");

	private final String s;

	private ModeType(final String s) {
		this.s = s;
	}

	@Override
	public String value() {
		return this.s;
	}

}
