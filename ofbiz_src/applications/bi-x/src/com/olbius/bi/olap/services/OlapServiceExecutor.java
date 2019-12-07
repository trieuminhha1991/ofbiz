package com.olbius.bi.olap.services;

import java.io.File;
import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.ServiceUtil;

import com.olbius.bi.loader.BiContairner;
import com.olbius.bi.loader.HttpSendRequest;
import com.olbius.bi.system.ProcessPentaho;
import com.olbius.bi.system.ProcessServices;
import com.olbius.entity.tenant.OlbiusTenant;
import com.olbius.service.OlbiusService;

public class OlapServiceExecutor implements OlbiusService {

	public final static String module = OlapServiceExecutor.class.getName();
	protected final static String absPath = new File("applications/bi-x").getAbsolutePath();
	protected final static String pathJob = "applications/bi-x/job/";

	private final static OlapExecutor EXECUTOR;
	
	static {
		if(BiContairner.DEV) {
			EXECUTOR = new DevModeExecutor();
		} else {
			EXECUTOR = new RemoteExecutor();
		}
	}

	@Override
	public Map<String, Object> run(DispatchContext ctx, Map<String, Object> context) throws Exception {

		final String service = (String) context.get("service");
		
		final boolean important = (Boolean) context.get("important");

		final Delegator delegator = ctx.getDelegator();

		if(!important) {
			boolean flag = false;
			try {
				flag = ProcessServices.checkRun(delegator, service);
			} catch (GenericEntityException e) {
				Debug.logError(e.getMessage(), module);
			}
			if (flag) {
				return ServiceUtil.returnSuccess();
			}
		}

		File job = new File(pathJob + service + ".kjb");

		if (!job.exists() || job.isDirectory()) {
			return ServiceUtil.returnSuccess();
		}

		ProcessServices.updateStatus(delegator, service, ProcessServices.PROCESSING, null);

		EXECUTOR.execute(delegator, service);

		return ServiceUtil.returnSuccess();
	}

	private static interface OlapExecutor {

		void execute(Delegator delegator, String service);

	}

	private static class DevModeExecutor implements OlapExecutor {

		private final ThreadPoolExecutor executor = new ThreadPoolExecutor(0, 1, 120000, TimeUnit.MILLISECONDS,
				new LinkedBlockingQueue<Runnable>(100));

		@Override
		public void execute(final Delegator delegator, final String service) {

			executor.execute(new Runnable() {

				@Override
				public void run() {

					ProcessPentaho pentaho = new ProcessPentaho("../job/" + service + ".kjb");

					long commitSize = UtilProperties.getPropertyAsLong("pentaho", "commit_size", 1000);

					long pastDate = UtilProperties.getPropertyAsLong("pentaho", "past_date", 365);

					pentaho.setDelegator(delegator);
					pentaho.setUpdate(false);

					if (UtilProperties.getPropertyAsBoolean("pentaho", "logfile", false)) {
						pentaho.addLogFile(service + ".log");
					}

					try {
						Debug.logInfo("Run: " + service, module);
						pentaho.addParam("path", absPath);
						pentaho.addParam("job", service);
						pentaho.addParam("commitSize", Long.toString(commitSize));
						pentaho.addParam("past", Long.toString(pastDate));
						pentaho.start("Kitchen.bat", "kitchen.sh");
						int j = pentaho.getProcess().waitFor();
						if (j != 0) {
							throw new GenericEntityException("Pentaho error code: " + j);
						}
					} catch (Exception e) {
						Debug.logError(e, module);
					}
				}
			});
		}
	}

	private static class RemoteExecutor implements OlapExecutor {

		@Override
		public void execute(Delegator delegator, String service) {
			HttpSendRequest.send(BiContairner.URL, "run",
					new String[] { "tenant=" + OlbiusTenant.getTenantId(delegator), "job=" + service });
		}

	}

}
