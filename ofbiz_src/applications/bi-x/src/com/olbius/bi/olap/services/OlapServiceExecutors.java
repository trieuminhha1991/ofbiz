package com.olbius.bi.olap.services;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.ServiceUtil;

import com.olbius.service.OlbiusService;

public class OlapServiceExecutors implements OlbiusService {

	public final static String module = OlapServiceExecutors.class.getName();

	@Override
	public Map<String, Object> run(DispatchContext ctx, Map<String, Object> context) throws Exception {

		File dir = new File(OlapServiceExecutor.pathJob);

		String[] files = null;

		if (dir.isDirectory()) {
			files = dir.list();
		}
		
		for (int i = 0; i < files.length; i++) {
			if (files[i].endsWith(".kjb") && !files[i].startsWith("$") && !files[i].equals("executeJob.kjb")
					&& !files[i].equals("aggregateJob.kjb")) {
				String service = files[i].substring(0, files[i].length() - 4);
				Map<String, Object> map = new HashMap<>();
				map.put("userLogin", context.get("userLogin"));
				map.put("service", service);
				map.put("important", true);
				ctx.getDispatcher().runAsync("olapService", map);
			}
		}
		
		return ServiceUtil.returnSuccess();
	}

}
