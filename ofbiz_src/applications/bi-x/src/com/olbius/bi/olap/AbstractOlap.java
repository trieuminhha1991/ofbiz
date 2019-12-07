package com.olbius.bi.olap;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.ofbiz.base.util.Debug;
import org.ofbiz.entity.GenericDataSourceException;
import org.ofbiz.entity.jdbc.SQLProcessor;

import com.olbius.bi.olap.query.OlbiusQuery;
import com.olbius.bi.olap.query.OptionQueryExtend;
import com.olbius.bi.olap.query.OptionQueryResult;
import com.olbius.bi.olap.query.OlapQuery;
import com.olbius.bi.olap.query.option.Count;
import com.olbius.bi.olap.query.option.Filter;
import com.olbius.bi.olap.query.option.Limit;
import com.olbius.bi.olap.query.option.Offset;
import com.olbius.bi.olap.query.option.Sort;

/**
 * Abstract class sử dựng xây dựng truy vấn dữ liệu olap
 * 
 * @author Nguyen Ha
 */
public abstract class AbstractOlap extends TypeOlap implements OlapInterface {

	protected Map<String, Object> parameters = new HashMap<String, Object>();

	protected String module;

	private SQLProcessor processor;

	protected Date fromDate;

	protected Date thruDate;

	private OlapResultInterface result;

	protected boolean isChart;

	protected List<OptionQueryResult> optionQueries = new ArrayList<OptionQueryResult>();

	protected List<OptionQueryExtend> extendQueries = new ArrayList<OptionQueryExtend>();

	protected OptionQueryResult filterQuery;

	public AbstractOlap() {
		initOptionQueries();
		initExtendQueries();
		filterQuery = initFilterQuery();
	}
	
	protected void initOptionQueries() {
		optionQueries.add(new Limit());
		optionQueries.add(new Offset());
		optionQueries.add(new Sort());
	}
	
	protected void initExtendQueries() {
		extendQueries.add(new Count());
	}
	
	protected OptionQueryResult initFilterQuery() {
		return new Filter();
	}
	
	@Override
	public void close() throws GenericDataSourceException {
		processor.close();
	}

	@Override
	public Map<String, Object> execute() {
		Map<String, Object> map = new HashMap<String, Object>();

		try {

			if (getOlapResult() != null) {

				OlapQuery query = OlbiusQuery.make(processor).select("*").from(getQuery(), "olbiusQuery");

				prepareResult();

				if (filterQuery != null) {
					filterQuery.setParameters(getParameters());
					filterQuery.setQuery(query);
					filterQuery.setResult(getOlapResult().getResultQuery());
					filterQuery.addOption();
				}

				for (OptionQueryExtend optionQuery : extendQueries) {
					optionQuery.setParameters(getParameters());
					optionQuery.setQuery(query);
					optionQuery.addOption(map);
				}

				for (OptionQueryResult optionQuery : optionQueries) {
					optionQuery.setParameters(getParameters());
					optionQuery.setQuery(query);
					optionQuery.setResult(getOlapResult().getResultQuery());
					optionQuery.addOption();
				}

				map.putAll(getOlapResult().returnResult(query));
			}
		} catch (Exception e) {
			Debug.logError(e, getModule());
		} finally {
			try {
				close();
			} catch (GenericDataSourceException e) {
				Debug.logError(e, getModule());
			}
		}

		return map;
	}

	@Override
	public Map<String, Object> execute(Map<String, ? extends Object> context) {

		Boolean init = (Boolean) context.get("init");

		long limit = 0;

		long offset = -1;

		if (context.get("limit") != null) {
			limit = (Long) context.get("limit");
		}
		if (context.get("offset") != null) {
			offset = (Long) context.get("offset");
		}

		if (init != null && init) {
			putParameter(OlapInterface.INIT, init);
		}

		if (context.get("sort") != null) {
			putParameter(OlapInterface.SORT, context.get("sort"));
		}

		if (context.get("sorttype") != null) {
			putParameter(OlapInterface.SORT_TYPE, context.get("sorttype"));
		}

		if (context.get("filter[]") != null) {
			putParameter(OlapInterface.FILTER, context.get("filter[]"));
		}

		putParameter(OlapInterface.LIMIT, limit);

		putParameter(OlapInterface.OFFSET, offset);

		return execute();
	}

	@Override
	public Timestamp getFromDate() {
		return getSqlFromDate(this.fromDate);
	}

	@Override
	public String getModule() {
		if (this.module == null) {
			return this.getClass().getName();
		}
		return this.module;
	}

	@Override
	public OlapResultInterface getOlapResult() {
		return this.result;
	}

	@Override
	public Object getParameter(String key) {
		return this.parameters.get(key);
	}

	@Override
	public Map<String, Object> getParameters() {
		return this.parameters;
	}

	/**
	 * Get lệnh truy vấn dữ liệu olap
	 * 
	 * @return lệnh truy vấn dữ liệu
	 */
	protected abstract OlapQuery getQuery();

	@Override
	public SQLProcessor getSQLProcessor() {
		return this.processor;
	}

	@Override
	public Timestamp getThruDate() {
		return getSqlThruDate(this.thruDate);
	}

	@Override
	public boolean isChart() {
		return this.isChart;
	}

	@Override
	public void prepareResult() {

	}

	@Override
	public void putParameter(String key, Object value) {
		this.parameters.put(key, value);
	}

	@Override
	public void setChart(boolean value) {
		this.isChart = value;
	}

	@Override
	public void setFromDate(Date date) {
		this.fromDate = date;
	}

	protected void setModule(String module) {
		this.module = module;
	}

	@Override
	public void setOlapResult(OlapResultInterface olap) {
		this.result = olap;
	}

	@Override
	public void setParameters(Map<String, Object> map) {
		this.parameters = map;
	}

	@Override
	public void setThruDate(Date date) {
		this.thruDate = date;
	}

	@Override
	public void SQLProcessor(SQLProcessor processor) {
		this.processor = processor;
	}
}
