package com.olbius.bi.olap;

/**
 * @author Nguyen Ha
 *
 * @param <T>
 */
public interface ReturnResultCallback<T> {

	T get(Object object);
	
}
