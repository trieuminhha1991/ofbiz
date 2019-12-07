package com.olbius.basepos.util;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Timestamp;
import java.util.Comparator;
import java.util.Map;

public class POUtil implements Comparator<Object> {
	private String sortField;
	
	public String getSortField() {
		return sortField;
	}

	public void setSortField(String sortField) {
		this.sortField = sortField;
	}

	public int compare(Object test1, Object test2) {
		if (test1 == null || test2 == null) return 0;
		Map<String, Object> o1 = (Map<String, Object>) test1;
		Map<String, Object> o2 = (Map<String, Object>) test2;
		int returnValue = 0;
		String alterSortField = this.sortField;
		if(alterSortField.contains("-")){
			alterSortField = alterSortField.replace("-", "");
		}
		if (o1.get(alterSortField) instanceof String) {
			if(sortField.contains("-")){
				if (o1.get(alterSortField) != null && o2.get(alterSortField) != null) {
					returnValue = ((String)o2.get(alterSortField)).compareTo((String)o1.get(alterSortField));
				} else if (o1.get(alterSortField) == null && o2.get(alterSortField) == null) {
					returnValue = 0;
				} else if (o1.get(alterSortField) == null) {
					returnValue = -1;
				} else if (o2.get(alterSortField) == null) {
					returnValue = 1;
				}
			} else {
				if (o1.get(alterSortField) != null && o2.get(alterSortField) != null) {
					returnValue = ((String)o1.get(alterSortField)).compareTo((String)o2.get(alterSortField));
				} else if (o1.get(alterSortField) == null && o2.get(alterSortField) == null) {
					returnValue = 0;
				} else if (o1.get(alterSortField) == null) {
					returnValue = 1;
				} else if (o2.get(alterSortField) == null) {
					returnValue = -1;
				}
			}
		} else if(o1.get(alterSortField) instanceof BigDecimal) {
			if (sortField.contains("-")) {
				if (o1.get(alterSortField) != null && o2.get(alterSortField) != null) {
					returnValue = ((BigDecimal)o2.get(alterSortField)).compareTo((BigDecimal)o1.get(alterSortField));
				} else if (o1.get(alterSortField) == null && o2.get(alterSortField) == null) {
					returnValue = 0;
				} else if (o1.get(alterSortField) == null) {
					returnValue = -1;
				} else if (o2.get(alterSortField) == null) {
					returnValue = 1;
				}
			} else {
				if (o1.get(alterSortField) != null && o2.get(alterSortField) != null) {
					returnValue = ((BigDecimal)o1.get(alterSortField)).compareTo((BigDecimal)o2.get(alterSortField));
				} else if (o1.get(alterSortField) == null && o2.get(alterSortField) == null) {
					returnValue = 0;
				} else if (o1.get(alterSortField) == null) {
					returnValue = 1;
				} else if (o2.get(alterSortField) == null) {
					returnValue = -1;
				}
			}
		} else {
			if (sortField.contains("-")) {
				if (o1.get(alterSortField) != null && o2.get(alterSortField) != null) {
					if (o2.get(alterSortField) instanceof Timestamp) {
						returnValue = ((Timestamp)o2.get(alterSortField)).compareTo((Timestamp)o1.get(alterSortField));
					}else if (o2.get(alterSortField) instanceof Date) {
						returnValue = ((Date)o2.get(alterSortField)).compareTo((Date)o1.get(alterSortField));
					}
				} else if (o1.get(alterSortField) == null && o2.get(alterSortField) == null) {
					returnValue = 0;
				} else if (o1.get(alterSortField) == null) {
					returnValue = -1;
				} else if (o2.get(alterSortField) == null) {
					returnValue = 1;
				}
			} else {
				if (o1.get(alterSortField) != null && o2.get(alterSortField) != null) {
					if (o1.get(alterSortField) instanceof Timestamp) {
						returnValue = ((Timestamp)o1.get(alterSortField)).compareTo((Timestamp)o2.get(alterSortField));
					}else if (o1.get(alterSortField) instanceof Date) {
						returnValue = ((Date)o1.get(alterSortField)).compareTo((Date)o2.get(alterSortField));
					}
				} else if (o1.get(alterSortField) == null && o2.get(alterSortField) == null) {
					returnValue = 0;
				} else if (o1.get(alterSortField) == null) {
					returnValue = 1;
				} else if (o2.get(alterSortField) == null) {
					returnValue = -1;
				}
			}
		}
		if (returnValue != 0) {
			return returnValue;
		}
		return returnValue;
	}
}
