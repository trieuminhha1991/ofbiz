package com.olbius.service.annotations.type;

public enum SemaphoreType implements IType<String> {
	NONE("none"), FAIL("fail"), WAIT("wait"), NULL("");

	private final String s;

	private SemaphoreType(final String s) {
		this.s = s;
	}

	@Override
	public String value() {
		return this.s;
	}
}
