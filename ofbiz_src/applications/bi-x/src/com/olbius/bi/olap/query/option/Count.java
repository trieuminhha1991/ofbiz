package com.olbius.bi.olap.query.option;

import java.sql.ResultSet;
import java.util.Map;

import org.ofbiz.base.util.Debug;
import org.ofbiz.entity.GenericDataSourceException;

import com.olbius.bi.olap.OlapInterface;
import com.olbius.bi.olap.query.OlbiusQuery;
import com.olbius.bi.olap.query.OlbiusQueryInterface;
import com.olbius.bi.olap.query.OptionQueryExtend;

public class Count extends AbstractOptionQuery implements OptionQueryExtend {

	public Count() {
		setParam(OlapInterface.INIT);
	}

	@Override
	public void addOption(Map<String, Object> map) {

		if(checkParam()) {
			
			int tmp = size();
			
			if(tmp != 0) {
				map.put("totalsize", tmp);
			}
			
		}
		
	}

	@Override
	public boolean checkParam() {
		return parameters.get(this.param) != null;
	}

	private int size() {
		try {
			OlbiusQueryInterface query = OlbiusQuery.make(this.query.getSQLProcessor());
			query.select("COUNT(*)", "_count").from(this.query, "TMP");
			ResultSet resultSet = query.getResultSet();
			if (resultSet.next()) {
				return resultSet.getInt("_count");
			}
		} catch (Exception e) {
			Debug.logError(e, Count.class.getName());
		} finally {
			try {
				query.close();;
			} catch (GenericDataSourceException e) {
				Debug.logError(e, Count.class.getName());
			}
		}
		return 0;
	}
	
}
