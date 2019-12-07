package com.olbius.bi.olap.grid.export;

import java.io.OutputStream;
import java.util.Map;

public interface OlapExport {

	void addData(Map<String, Object> map);
	
	OutputStream getOutputStream();
}
