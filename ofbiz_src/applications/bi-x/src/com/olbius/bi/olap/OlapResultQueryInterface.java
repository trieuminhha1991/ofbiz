package com.olbius.bi.olap;

import com.olbius.bi.olap.query.OlapQuery;

/**
 * @author Nguyen Ha
 *
 */
public interface OlapResultQueryInterface {

	Object resultQuery(OlapQuery query);
	
}
