package com.olbius.service.annotations.type;

public enum BooleanType implements IType<Boolean> {
	TRUE(true), FALSE(false), NULL(null);

	private final Boolean b;

	private BooleanType(final Boolean b) {
		this.b = b;
	}

	@Override
	public Boolean value() {
		return this.b;
	}

}
