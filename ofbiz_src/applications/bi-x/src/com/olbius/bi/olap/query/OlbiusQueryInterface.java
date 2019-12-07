package com.olbius.bi.olap.query;

import com.olbius.bi.olap.query.condition.Condition;
import com.olbius.bi.olap.query.join.Join;

/**
 * Extend {@link OlapQuery}
 * 
 * @author Nguyen Ha
 */
public interface OlbiusQueryInterface extends OlapQuery {

	/**
	 * Thêm đối tượng from vào lệnh sql
	 * <p>
	 * Ví dụ: SELECT * FROM table với name = "table"
	 * <p>
	 * Chú ý: mỗi một object OlbiusQueryInterface chỉ tồn tại duy nhất 1 đối
	 * tượng from, nếu thêm nhiều lần thì chỉ nhận lệnh thêm cuối cùng
	 * 
	 * @param name
	 *            tên table muốn truy vấn dữ liệu
	 * @return {@link OlbiusQueryInterface}
	 */
	OlbiusQueryInterface from(String name);

	/**
	 * Thêm đối tượng from và gán tên cho nó trong lệnh sql
	 * <p>
	 * Ví dụ: SELECT * FROM table AS olbius với name = "table" và as = "olbius"
	 * 
	 * @param name
	 *            tên table muốn truy vấn dữ liệu
	 * @param as
	 *            tên được gán
	 * @return {@link OlbiusQueryInterface}
	 * @see OlbiusQueryInterface from(String name)
	 */
	OlbiusQueryInterface from(String name, String as);

	/**
	 * Sử dựng trong các lệnh sql lồng
	 * <p>
	 * Ví dụ: SELECT * FROM (SELECT * FROM table) AS olbius với query = (SELECT
	 * * FROM table) và as = "olbius"
	 * 
	 * @param query
	 *            object OlapQuery
	 * @param as
	 *            tên được gán
	 * @return {@link OlbiusQueryInterface}
	 */
	OlbiusQueryInterface from(OlapQuery query, String as);

	/**
	 * Thêm đối tương distinct on vào lệnh sql
	 * <p>
	 * Ví dụ: SELECT DISTINCT ON (a) a, b, c FROM table với name = "a"
	 * <p>
	 * Chú ý: có thể truyền vào nhiều tên cột 1 lúc, ví dụ distinctOn("a", "b",
	 * "c")
	 * 
	 * @param name
	 *            tên cột muốn distinct
	 * @return {@link OlbiusQueryInterface}
	 */
	OlbiusQueryInterface distinctOn(String... name);

	/**
	 * Thêm đối tương distinct on vào lệnh sql sử dụng cờ
	 * <p>
	 * Nếu cờ bằng false đối tượng sẽ không được thêm vào lệnh sql
	 * 
	 * @param name
	 *            tên cột muốn distinct
	 * @param flag
	 *            cờ
	 * @return {@link OlbiusQueryInterface}
	 * @see OlbiusQueryInterface distinctOn(String... name)
	 */
	OlbiusQueryInterface distinctOn(String name, boolean flag);

	/**
	 * Thêm đối tương distinct vào lệnh sql
	 * <p>
	 * Ví dụ: SELECT DISTINCT a, b, c FROM table
	 * 
	 * @return {@link OlbiusQueryInterface}
	 */
	OlbiusQueryInterface distinct();

	/**
	 * Thêm đối tương distinct vào lệnh sql sử dụng cờ
	 * <p>
	 * Nếu cờ bằng false đối tượng sẽ không được thêm vào lệnh sql
	 * 
	 * @param flag
	 *            cờ
	 * @return {@link OlbiusQueryInterface}
	 * @see OlbiusQueryInterface distinct()
	 */
	OlbiusQueryInterface distinct(boolean flag);

	/**
	 * Thêm 1 cột dữ liệu muốn truy vấn vào lệnh sql
	 * <p>
	 * Ví dụ: SELECT a FROM table với name = "a"
	 * <p>
	 * Chú ý: có thể truyền vào nhiều tên cột 1 lúc, ví dụ select("a", "b", "c")
	 * 
	 * @param name
	 *            tên cột muốn truy vấn
	 * @return {@link OlbiusQueryInterface}
	 */
	OlbiusQueryInterface select(String... name);

	/**
	 * Thêm 1 cột dữ liệu muốn truy vấn và gán tên vào lệnh sql
	 * <p>
	 * Ví dụ: SELECT a AS b FROM table với name = "a" và as = "b"
	 * 
	 * @param name
	 *            tên cột muốn truy vấn
	 * @param as
	 *            tên được gán
	 * @return {@link OlbiusQueryInterface}
	 */
	OlbiusQueryInterface select(String name, String as);

	/**
	 * Thêm 1 cột dữ liệu muốn truy vấn và gán tên vào lệnh sql sử dụng cờ
	 * <p>
	 * Nếu cờ bằng false cột dữ liệu sẽ không được truy vấn trong lệnh sql
	 * 
	 * @param name
	 *            tên cột muốn truy vấn
	 * @param as
	 *            tên được gán
	 * @param flag
	 *            cờ
	 * @return {@link OlbiusQueryInterface}
	 * @see OlbiusQueryInterface select(String name, String as)
	 */
	OlbiusQueryInterface select(String name, String as, boolean flag);

	/**
	 * Call select(name, null, flag)
	 * 
	 * @param name
	 *            tên cột muốn truy vấn
	 * @param flag
	 *            cờ
	 * @return {@link OlbiusQueryInterface}
	 * @see OlbiusQueryInterface select(String name, String as, boolean flag)
	 */
	OlbiusQueryInterface select(String name, boolean flag);

	/**
	 * Call select(function, String as, true)
	 * 
	 * @param function
	 *            {@link Function}
	 * @param as
	 *            tên được gán
	 * @return {@link OlbiusQueryInterface}
	 * @see OlbiusQueryInterface select(Function function, String as, boolean
	 *      flag)
	 */
	OlbiusQueryInterface select(Function function, String as);

	/**
	 * Truy vấn dữ liệu bằng function sql và gán tên cho côt dữ liệu truy vấn
	 * được sử dụng cờ
	 * <p>
	 * Nếu cờ bằng false function không được sử dụng trong lệnh sql
	 * <p>
	 * Ví dụ: SELECT SUM(a) AS b FROM table với function = SUM(a) và as = "b"
	 * 
	 * @param function
	 *            {@link Function}
	 * @param as
	 *            tên được gán
	 * @param flag
	 *            cờ
	 * @return {@link OlbiusQueryInterface}
	 */
	OlbiusQueryInterface select(Function function, String as, boolean flag);

	OlbiusQueryInterface select(Condition condition, String as);

	OlbiusQueryInterface select(Condition condition, String as, boolean flag);

	/**
	 * Thêm điều kiện truy vấn dữ liệu vào lệnh sql
	 * <p>
	 * Ví dụ: SELECT * FROM table WHERE a = 10 với condition = (a = 10)
	 * 
	 * @param condition
	 *            {@link Condition}
	 * @return {@link OlbiusQueryInterface}
	 */
	OlbiusQueryInterface where(Condition condition);

	/**
	 * Trả về object mô tả điều kiện truy vấn
	 * 
	 * @return {@link Condition}
	 */
	Condition where();

	OlbiusQueryInterface join(Join join);

	OlbiusQueryInterface join(Join join, boolean flag);

	OlbiusQueryInterface join(String type, String table, String condition);

	OlbiusQueryInterface join(String type, String table, String condition, boolean flag);

	OlbiusQueryInterface join(String type, String table, Condition condition);

	OlbiusQueryInterface join(String type, String table, Condition condition, boolean flag);

	OlbiusQueryInterface join(String type, String table, String as, String condition);

	OlbiusQueryInterface join(String type, String table, String as, String condition, boolean flag);

	OlbiusQueryInterface join(String type, String table, String as, Condition condition);

	OlbiusQueryInterface join(String type, String table, String as, Condition condition, boolean flag);

	OlbiusQueryInterface join(String type, OlbiusQueryInterface query, String as, String condition);

	OlbiusQueryInterface join(String type, OlbiusQueryInterface query, String as, String condition, boolean flag);

	OlbiusQueryInterface join(String type, OlbiusQueryInterface query, String as, Condition condition);

	OlbiusQueryInterface join(String type, OlbiusQueryInterface query, String as, Condition condition, boolean flag);

	/**
	 * Thêm đối tượng group by vào lệnh sql
	 * <p>
	 * Ví dụ: SELECT * FROM table WHERE GROUP BY a với name = "a"
	 * <p>
	 * Chú ý: có thể truyền vào nhiều tên cột 1 lúc, ví dụ groupBy("a", "b",
	 * "c")
	 * 
	 * @param name
	 *            tên cột muốn group
	 * @return {@link OlbiusQueryInterface}
	 */
	OlbiusQueryInterface groupBy(String... name);

	/**
	 * Thêm đối tượng group by vào lệnh sql sử dụng cờ
	 * <p>
	 * Nếu cờ bằng false đối tượng sẽ không được thêm vào lệnh sql
	 * 
	 * @param name
	 *            tên cột muốn group
	 * @param flag
	 *            cờ
	 * @return {@link OlbiusQueryInterface}
	 * @see OlbiusQueryInterface groupBy(String... name)
	 */
	OlbiusQueryInterface groupBy(String name, boolean flag);

	/**
	 * Thêm đối tượng order by vào lệnh sql
	 * <p>
	 * Ví dụ: SELECT * FROM table WHERE ORDER BY a với name = "a"
	 * <p>
	 * Chú ý: có thể truyền vào nhiều tên cột 1 lúc, ví dụ orderBy("a", "b",
	 * "c")
	 * 
	 * @param name
	 *            tên cột muốn sắp xếp
	 * @return {@link OlbiusQueryInterface}
	 */
	OlbiusQueryInterface orderBy(String... name);

	/**
	 * Thêm đối tượng order by vào lệnh sql sử dụng cờ
	 * <p>
	 * Nếu cờ bằng false đối tượng sẽ không được thêm vào lệnh sql
	 * 
	 * @param name
	 *            tên cột muốn sắp xếp
	 * @param flag
	 *            cờ
	 * @return {@link OlbiusQueryInterface}
	 * @see OlbiusQueryInterface orderBy(String... name)
	 */
	OlbiusQueryInterface orderBy(String name, boolean flag);

	/**
	 * Call orderBy(name, sort, true)
	 * 
	 * @param name
	 *            tên cột muốn sắp xếp
	 * @param sort
	 *            "ASC" or "DESC"
	 * @return {@link OlbiusQueryInterface}
	 * @see OlbiusQueryInterface orderBy(String name, String sort, boolean flag)
	 */
	OlbiusQueryInterface orderBy(String name, String sort);

	/**
	 * Thêm đối tượng order by và kiểu sắp xếp vào lệnh sql sử dụng cờ
	 * <p>
	 * Nếu cờ bằng false đối tượng sẽ không được thêm vào lệnh sql
	 * 
	 * @param name
	 *            tên cột muốn sắp xếp
	 * @param sort
	 *            "ASC" or "DESC"
	 * @param flag
	 *            cờ
	 * @return {@link OlbiusQueryInterface}
	 */
	OlbiusQueryInterface orderBy(String name, String sort, boolean flag);

	@Override
	OlbiusQueryInterface limit(long limit);

	@Override
	OlbiusQueryInterface offset(long offset);

	/**
	 * Trả về tên bảng mới truy vấn
	 * 
	 * @return {@link String}
	 */
	String getFrom();
	
	OlbiusQueryInterface extend(OlapQuery query, String as, boolean flag);
}
