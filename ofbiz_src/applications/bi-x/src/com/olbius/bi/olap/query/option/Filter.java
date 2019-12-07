package com.olbius.bi.olap.query.option;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.olbius.bi.olap.OlapInterface;
import com.olbius.bi.olap.grid.OlapResultQueryEx;
import com.olbius.bi.olap.query.OlbiusQueryInterface;
import com.olbius.bi.olap.query.condition.Condition;

public class Filter extends AbstractOptionResult {

	private List<Object> objects;

	private Map<String, FilterCondition> filterConditions = new HashMap<String, FilterCondition>();

	{
		new ContainsFilter().registry(filterConditions);
		new GreaterEqualFilter().registry(filterConditions);
		new EqualFilter().registry(filterConditions);
		new NotEqualFilter().registry(filterConditions);
		new GreaterFilter().registry(filterConditions);
		new LessEqualFilter().registry(filterConditions);
		new LessFilter().registry(filterConditions);
	}

	public Filter() {
		setParam(OlapInterface.FILTER);
	}

	@Override
	public void addOption() {
		if (checkParam()) {
			if (query instanceof OlbiusQueryInterface && result != null && result instanceof OlapResultQueryEx) {

				OlbiusQueryInterface tmp = (OlbiusQueryInterface) query;

				Map<String, List<FilterInfo>> map = new HashMap<String, List<FilterInfo>>();

				for (int i = 0; i < objects.size(); i = i + 5) {

					if (objects.get(i) instanceof String && objects.get(i + 1) instanceof String && objects.get(i + 2) instanceof String
							&& objects.get(i + 3) instanceof String) {

						String name = ((OlapResultQueryEx) result).toColumn((String) objects.get(i));

						if (name == null) {
							return;
						}

						if (map.get(name) == null) {
							map.put(name, new ArrayList<FilterInfo>());
						}

						map.get(name).add(new FilterInfo(name, (String) objects.get(i + 1), (String) objects.get(i + 2), (String) objects.get(i + 3),
								objects.get(i + 4)));

					}

				}

				for (String name : map.keySet()) {

					Condition condition = null;

					for (FilterInfo info : map.get(name)) {
						FilterCondition filterCondition = filterConditions.get(info.condition);
						if (filterCondition != null) {
							if(condition == null) {
								condition = new Condition();
							}
							Object val;
							if("numericfilter".equals(info.type)) {
								val = Double.parseDouble(info.value.toString());
							} else {
								val = info.value;
							}
							if (Condition.AND.equals(info.operator)) {
								condition.and(filterCondition.makeCondition(info.name, val));
							} else {
								condition.or(filterCondition.makeCondition(info.name, val));
							}
						}
					}

					if(condition != null) {
						tmp.where().and(condition);
					}
					
				}

			}

		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean checkParam() {
		Object filter = this.parameters.get(this.param);
		if (filter != null && filter instanceof List) {
			this.objects = (List<Object>) filter;
			return true;
		}
		return false;
	}

	private static class FilterInfo {

		final String name;
		final String condition;
		final String operator;
		final String type;
		final Object value;

		public FilterInfo(String name, String condition, String operator, String type, Object value) {
			this.name = name;
			this.condition = condition;
			this.operator = operator;
			this.type = type;
			this.value = value;
		}
	}

}
