package com.olbius.bi.olap.grid;

import com.olbius.bi.olap.ReturnResultCallback;

public interface OlapResultQueryEx {

	void addDataField(String name, String col);

	void addDataField(String name, String col, ReturnResultCallback<?> callBack);

	String toDataField(String col);

	String toColumn(String name);

	void addDataField(String name, ReturnResultCallback<?> callBack);

}
