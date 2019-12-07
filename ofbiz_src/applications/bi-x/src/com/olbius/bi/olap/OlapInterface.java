package com.olbius.bi.olap;

import java.sql.Timestamp;
import java.util.Date;
import java.util.Map;

import org.ofbiz.entity.GenericDataSourceException;
import org.ofbiz.entity.jdbc.SQLProcessor;

/**
 * Interface sử dựng xây dựng truy vấn dữ liệu olap
 * 
 * @author Nguyen Ha
 */
public interface OlapInterface {

	public final static String DAY = "DAY";
	public final static String MONTH = "MONTH";
	public final static String WEEK = "WEEK";
	public final static String QUARTER = "QUARTER";
	public final static String YEAR = "YEAR";
	public final static String OFFSET = "OFFSET";
	public final static String LIMIT = "LIMIT";
	public final static String SORT = "SORT";
	public final static String SORT_TYPE = "SORT_TYPE";
	public final static String FILTER = "FILTER";
	public final static String INIT = "INIT";
	public final static String SERVICE = "SERVICE";
	public final static String SERVICE_TIMESTAMP = "SERVICE_TIMESTAMP";

	/**
	 * Close connection database olap
	 * 
	 * @throws GenericDataSourceException
	 */
	void close() throws GenericDataSourceException;

	/**
	 * Thực thi truy vấn dữ liệu
	 * 
	 * @return dữ liệu truy vấn được
	 */
	Map<String, Object> execute();

	/**
	 * Chuẩn bị dữ liệu và thực thi truy vấn dữ liệu
	 * <p>
	 * Call execute()
	 * 
	 * @param context
	 *            dữ liệu truyền vào
	 * @return dữ liệu truy vấn được
	 * @see OlapInterface#execute()
	 */
	Map<String, Object> execute(Map<String, ? extends Object> context);

	Timestamp getFromDate();

	/**
	 * Get module sử dụng ghi logs
	 * 
	 * @return class name
	 */
	String getModule();

	/**
	 * Get object sử dụng để xử lý dữ liệu trả về của câu truy vấn
	 * 
	 * @return object xử lý dữ liệu trả về của câu truy vấn
	 */
	OlapResultInterface getOlapResult();

	/**
	 * Get giá trị của parameter
	 * 
	 * @param key
	 *            tên parameter
	 * @return giá trị của parameter
	 */
	Object getParameter(String key);

	/**
	 * Get parameters sử dụng cho việc khởi tạo câu lệnh truy vấn
	 * 
	 * @return parameters
	 */
	Map<String, Object> getParameters();

	/**
	 * Get processor object thực thi truy vấn
	 * 
	 * @return object thực thi truy vấn
	 */
	SQLProcessor getSQLProcessor();

	Timestamp getThruDate();

	/**
	 * Kiểm tra kiểu dữ liệu trả về của truy vấn sử dựng cho highcharts hay
	 * không
	 * 
	 * @return true nếu trả về dữ liệu cho highcharts, false nếu không
	 */
	boolean isChart();

	/**
	 * Method được gọi trước khi thực thi truy vấn
	 * 
	 * @see OlapInterface#execute()
	 */
	void prepareResult();

	/**
	 * Thêm parameter sử dụng cho việc khởi tạo câu lệnh truy vấn
	 * 
	 * @param key
	 *            tên parameter
	 * @param value
	 *            giá trị của parameter
	 */
	void putParameter(String key, Object value);

	/**
	 * Set kiểu dữ liệu trả về của truy vấn sử dựng cho highcharts hay không
	 */
	void setChart(boolean value);

	void setFromDate(Date date);

	/**
	 * Set object sử dụng để xử lý dữ liệu trả về của câu truy vấn
	 * 
	 * @param olap
	 *            object xử lý dữ liệu trả về của câu truy vấn
	 */
	void setOlapResult(OlapResultInterface olap);

	/**
	 * Set parameters sử dụng cho việc khởi tạo câu lệnh truy vấn
	 * 
	 * @param map
	 *            parameters
	 */
	void setParameters(Map<String, Object> map);

	void setThruDate(Date date);

	/**
	 * Set processor object thực thi truy vấn
	 * 
	 * @param processor
	 *            object thực thi truy vấn
	 */
	void SQLProcessor(SQLProcessor processor);

}
