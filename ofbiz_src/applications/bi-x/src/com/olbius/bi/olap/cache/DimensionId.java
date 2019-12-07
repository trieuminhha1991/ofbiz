package com.olbius.bi.olap.cache;

import com.olbius.bi.olap.OlbiusBuilder;

public class DimensionId {

	private String dimension;

	private Object id;

	public String getDimension() {
		return dimension;
	}

	public void setDimension(String dimension) {
		this.dimension = dimension;
	}

	public Object getId() {
		return id;
	}

	public void setId(Object id) {
		this.id = id;
	}

	@Override
	public String toString() {
		return dimension.concat("_").concat(id.toString()).concat("_")
				.concat(Long.toString(OlbiusBuilder.getTime()));
	}

	@Override
	public int hashCode() {
		return toString().hashCode();
	}

	public static DimensionId newInstance(String dimension, long id) {
		DimensionId dimensionId = new DimensionId();
		dimensionId.setDimension(dimension);
		dimensionId.setId(id);
		return dimensionId;
	}
}
