package com.olbius.bi.olap.query;

import java.util.Map;

/**
 * @author Nguyen Ha
 */
public interface OptionQueryExtend extends OptionQuery {

	void addOption(Map<String, Object> map);
	
}
