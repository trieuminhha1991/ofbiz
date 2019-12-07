package com.olbius.bi.olap.query;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.ofbiz.entity.GenericDataSourceException;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.jdbc.SQLProcessor;

/**
 * Olap Query Interface
 * Hỗ trợ tạo câu lệnh sql truy vấn dữ liệu olap
 * 
 * @author Nguyen Ha
 */
public interface OlapQuery {

	/**
	 * Thực thi câu lệnh sql
	 * 
	 * @throws GenericDataSourceException
	 * @throws GenericEntityException
	 * @throws SQLException
	 */
	void execute() throws GenericDataSourceException, GenericEntityException, SQLException;

	/**
	 * Trả về dữ liệu sau khi truy vấn
	 * 
	 * @return {@link ResultSet}
	 * @throws GenericDataSourceException
	 * @throws GenericEntityException
	 * @throws SQLException
	 */
	public ResultSet getResultSet() throws GenericDataSourceException, GenericEntityException, SQLException;

	/**
	 * Trả về danh sách các biến sử dụng trong truy vấn
	 * 
	 * @return {@link List}
	 */
	List<Object> getConditionValues();

	/**
	 * Thêm đối tương limit với vào lệnh sql
	 * <p>
	 * Ví dụ: SELECT * FROM LIMIT 10 với limit = 10
	 * 
	 * @param limit số lượng bảng ghi dược truy vấn
	 * @return {@link OlapQuery}
	 */
	OlapQuery limit(long limit);

	/**
	 * Thêm đối tương offset với vào lệnh sql
	 * <p>
	 * Ví dụ: SELECT * FROM OFFSET 0 với offset = 0
	 * 
	 * @param offset vị trí bảng ghi bắt đầu truy vấn, bắt đầu từ 0
	 * @return {@link OlapQuery}
	 */
	OlapQuery offset(long offset);

	OlapQuery limit(int limit);

	OlapQuery offset(int offset);

	/**
	 * Đóng kết nối với database
	 * 
	 * @throws GenericDataSourceException
	 */
	void close() throws GenericDataSourceException;

	/**
	 * Trả về object kết nối với database
	 * 
	 * @return {@link SQLProcessor}
	 */
	SQLProcessor getSQLProcessor();
}
