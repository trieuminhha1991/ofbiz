package com.olbius.bi.olap;

import java.util.Map;

import com.olbius.bi.olap.query.OlapQuery;

/**
 * Interface sử dụng để xử lý dữ liệu trả về của truy vấn dữ liệu olap
 * 
 * @author Nguyen Ha
 */
public interface OlapResultInterface {

	/**
	 * Set parameters sử dụng để xử lý dữ liệu
	 * 
	 * @param map
	 *            parameters
	 */
	void setParameters(Map<String, Object> map);

	/**
	 * Thêm parameter sử dụng để xử lý dữ liệu
	 * 
	 * @param key
	 *            tên parameter
	 * @param value
	 *            giá trị parameter
	 */
	void putParameter(String key, Object value);

	/**
	 * Get parameters sử dụng để xử lý dữ liệu
	 * 
	 * @return parameters
	 */
	Map<String, Object> getParameters();

	/**
	 * Get gía trị của parameter
	 * 
	 * @param key
	 *            tên parameter
	 * @return giá trị parameter
	 */
	Object getParameter(String key);

	/**
	 * Xử lý dữ liệu trả về của truy vấn olap
	 * 
	 * @param object
	 *            lệnh truy vấn
	 * @return dữ liệu đã xử lý
	 */
	Map<String, Object> returnResult(OlapQuery object);

	/**
	 * Dữ liệu trả về được sử dụng cho highcharts hay không
	 * 
	 * @return true nếu dữ liệu trả về được sử dụng cho highcharts, false nếu
	 *         không
	 */
	boolean isChart();

	/**
	 * Get object xử lý dữ liệu truy vấn
	 * 
	 * @return object xử lý dữ liệu truy vấn
	 */
	OlapResultQueryInterface getResultQuery();

}
