package com.olbius.bi.olap.cache;

import com.olbius.bi.olap.OlbiusBuilder;

public class DimensionKey {

	private String dimension;

	private String key;

	private Object value;

	public String getDimension() {
		return dimension;
	}

	public void setDimension(String dimension) {
		this.dimension = dimension;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public Object getValue() {
		return value;
	}

	public void setValue(Object value) {
		this.value = value;
	}

	@Override
	public String toString() {
		return dimension.concat("_").concat(key).concat("_").concat(value.toString()).concat("_")
				.concat(Long.toString(OlbiusBuilder.getTime()));
	}

	@Override
	public int hashCode() {
		return toString().hashCode();
	}

	public static DimensionKey newInstance(String dimension, String key, String value) {
		DimensionKey dimensionKey = new DimensionKey();
		dimensionKey.setDimension(dimension);
		dimensionKey.setValue(value);
		dimensionKey.setKey(key);
		return dimensionKey;
	}

}
