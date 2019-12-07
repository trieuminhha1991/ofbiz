package com.olbius.service.annotations.type;

public enum HtmlType implements IType<String> {
	NONE("none"), ANY("any"), SAFE("safe"), NULL("");

	private final String s;

	private HtmlType(final String s) {
		this.s = s;
	}

	@Override
	public String value() {
		return this.s;
	}

}
