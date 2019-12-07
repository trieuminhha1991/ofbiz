package com.olbius.bi.olap;

import java.util.HashMap;
import java.util.Map;

import com.olbius.bi.olap.query.OlapQuery;

/**
 * Abstract class sử dụng để xây dựng các class xử lý dữ liệu trả về của truy
 * vấn dữ liệu olap
 * 
 * @author Nguyen Ha
 */
public abstract class AbstractOlapResult implements OlapResultInterface {

	protected OlapInterface olap;
	protected OlapResultQueryInterface query;
	protected Map<String, Object> parameters = new HashMap<String, Object>();

	/**
	 * Constructor
	 * 
	 * @param olap
	 *            object truy vấn dữ liệu
	 * @param query
	 *            object xử lý dữ liệu truy vấn
	 */
	public AbstractOlapResult(OlapInterface olap, OlapResultQueryInterface query) {
		this.olap = olap;
		this.query = query;
	}

	@Override
	public Object getParameter(String key) {
		return parameters.get(key);
	}

	@Override
	public Map<String, Object> getParameters() {
		return parameters;
	}

	@Override
	public OlapResultQueryInterface getResultQuery() {
		return query;
	}

	/**
	 * Khởi tạo object trả về client
	 * 
	 * @return object trả về client
	 */
	protected abstract Map<String, Object> putMap();

	@Override
	public void putParameter(String key, Object value) {
		parameters.put(key, value);
	}

	/**
	 * Get dữ liệu đã được xử lý
	 * 
	 * @param object
	 *            dữ liệu đã xử lý
	 */
	protected abstract void result(Object object);

	@Override
	public Map<String, Object> returnResult(OlapQuery object) {
		if (olap == null) {
			return null;
		}
		Object tmp;
		if (query != null) {
			tmp = query.resultQuery(object);
		} else {
			tmp = object;
		}

		result(tmp);

		return putMap();
	}

	@Override
	public void setParameters(Map<String, Object> map) {
		this.parameters = map;
	}
}
