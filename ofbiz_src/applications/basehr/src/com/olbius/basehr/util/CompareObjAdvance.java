package com.olbius.basehr.util;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import org.ofbiz.base.util.UtilValidate;

@SuppressWarnings("rawtypes")
class CompareObjAdvance implements Comparator {
	private String sortField;
	private List<String> sortFields;
	private List<String> alterSortFields;
	private boolean isReverse;
	public boolean isReverse() {
		return isReverse;
	}
	public void setReverse(boolean isReverse) {
		this.isReverse = isReverse;
	}
	public String getSortField() {
		return sortField;
	}
	public void setSortField(String sortField) {
		this.sortField = sortField;
		this.sortFields = new ArrayList<String>();
		this.alterSortFields = new ArrayList<String>();
		String[] sortFieldTmp = sortField.split(",");
		if (UtilValidate.isNotEmpty(sortFieldTmp)) {
			for (String item : sortFieldTmp) {
				String itemAfter = item.trim();
				sortFields.add(itemAfter);
				if (itemAfter.contains("-")) {
					alterSortFields.add(itemAfter.replace("-", ""));
				} else {
					alterSortFields.add(itemAfter);
				}
			}
		}
	}
	@SuppressWarnings("unchecked")
	@Override
	public int compare(Object test1, Object test2) {
		if (test1 == null || test2 == null) return 0;
		Map<String, Object> o1 = (Map<String, Object>) test1;
		Map<String, Object> o2 = (Map<String, Object>) test2;
		
		int returnValue = 0;
		for (int i = 0; i < alterSortFields.size(); i++) {
			String alterSortFieldItem = alterSortFields.get(i);
			if (o1.get(alterSortFieldItem) instanceof String) {
				if (sortFields.get(i).contains("-")) {
					if (o1.get(alterSortFieldItem) != null && o2.get(alterSortFieldItem) != null) {
						returnValue = ((String)o2.get(alterSortFieldItem)).compareTo((String)o1.get(alterSortFieldItem));
					} else if (o1.get(alterSortFieldItem) == null && o2.get(alterSortFieldItem) == null) {
						returnValue = 0;
					} else if (o1.get(alterSortFieldItem) == null) {
						if (isReverse) returnValue = 1;
						else returnValue = -1;
					} else if (o2.get(alterSortFieldItem) == null) {
						if (isReverse) returnValue = -1;
						else returnValue = 1;
					}
				} else {
					if (o1.get(alterSortFieldItem) != null && o2.get(alterSortFieldItem) != null) {
						returnValue = ((String)o1.get(alterSortFieldItem)).compareTo((String)o2.get(alterSortFieldItem));
					} else if (o1.get(alterSortFieldItem) == null && o2.get(alterSortFieldItem) == null) {
						returnValue = 0;
					} else if (o1.get(alterSortFieldItem) == null) {
						if (isReverse) returnValue = -1;
						else returnValue = 1;
					} else if (o2.get(alterSortFieldItem) == null) {
						if (isReverse) returnValue = 1;
						else returnValue = -1;
					}
				}
			} else if(o1.get(alterSortFieldItem) instanceof BigDecimal) {
				if (sortFields.get(i).contains("-")) {
					if (o1.get(alterSortFieldItem) != null && o2.get(alterSortFieldItem) != null) {
						returnValue = ((BigDecimal)o2.get(alterSortFieldItem)).compareTo((BigDecimal)o1.get(alterSortFieldItem));
					} else if (o1.get(alterSortFieldItem) == null && o2.get(alterSortFieldItem) == null) {
						returnValue = 0;
					} else if (o1.get(alterSortFieldItem) == null) {
						if (isReverse) returnValue = 1;
						else returnValue = -1;
					} else if (o2.get(alterSortFieldItem) == null) {
						if (isReverse) returnValue = -1;
						else returnValue = 1;
					}
				} else {
					if (o1.get(alterSortFieldItem) != null && o2.get(alterSortFieldItem) != null) {
						returnValue = ((BigDecimal)o1.get(alterSortFieldItem)).compareTo((BigDecimal)o2.get(alterSortFieldItem));
					} else if (o1.get(alterSortFieldItem) == null && o2.get(alterSortFieldItem) == null) {
						returnValue = 0;
					} else if (o1.get(alterSortFieldItem) == null) {
						if (isReverse) returnValue = -1;
						else returnValue = 1;
					} else if (o2.get(alterSortFieldItem) == null) {
						if (isReverse) returnValue = 1;
						else returnValue = -1;
					}
				}
			} else {
				if (sortFields.get(i).contains("-")) {
					if (o1.get(alterSortFieldItem) != null && o2.get(alterSortFieldItem) != null) {
						if (o2.get(alterSortFieldItem) instanceof Timestamp) {
							returnValue = ((Timestamp)o2.get(alterSortFieldItem)).compareTo((Timestamp)o1.get(alterSortFieldItem));
						}else if (o2.get(alterSortFieldItem) instanceof Date) {
							returnValue = ((Date)o2.get(alterSortFieldItem)).compareTo((Date)o1.get(alterSortFieldItem));
						}
					} else if (o1.get(alterSortFieldItem) == null && o2.get(alterSortFieldItem) == null) {
						returnValue = 0;
					} else if (o1.get(alterSortFieldItem) == null) {
						if (isReverse) returnValue = 1;
						else returnValue = -1;
					} else if (o2.get(alterSortFieldItem) == null) {
						if (isReverse) returnValue = -1;
						else returnValue = 1;
					}
				} else {
					if (o1.get(alterSortFieldItem) != null && o2.get(alterSortFieldItem) != null) {
						if (o1.get(alterSortFieldItem) instanceof Timestamp) {
							returnValue = ((Timestamp)o1.get(alterSortFieldItem)).compareTo((Timestamp)o2.get(alterSortFieldItem));
						}else if (o1.get(alterSortFieldItem) instanceof Date) {
							returnValue = ((Date)o1.get(alterSortFieldItem)).compareTo((Date)o2.get(alterSortFieldItem));
						}
					} else if (o1.get(alterSortFieldItem) == null && o2.get(alterSortFieldItem) == null) {
						returnValue = 0;
					} else if (o1.get(alterSortFieldItem) == null) {
						if (isReverse) returnValue = -1;
						else returnValue = 1;
					} else if (o2.get(alterSortFieldItem) == null) {
						if (isReverse) returnValue = 1;
						else returnValue = -1;
					}
				}
			}
			if (returnValue != 0) {
				return returnValue;
			}
		}
		return returnValue;
	}
}
