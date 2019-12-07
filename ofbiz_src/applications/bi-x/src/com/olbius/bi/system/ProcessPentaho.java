package com.olbius.bi.system;

import java.sql.Timestamp;
import java.util.Calendar;

import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.config.EntityConfigUtil;
import org.ofbiz.entity.config.model.Datasource;
import org.ofbiz.entity.config.model.InlineJdbc;
import org.ofbiz.entity.datasource.GenericHelperInfo;
import org.ofbiz.entity.util.EntityUtil;

public class ProcessPentaho {
	
	private Process process;
	
	private boolean log;
	
	private String file;
	
	private final static String OLBIUS = "OLBIUS";
	private final static String OLBIUS_HOST = "OLBIUS_HOST";
	private final static String OLBIUS_PORT = "OLBIUS_PORT";
	private final static String OLBIUS_USER = "OLBIUS_USER";
	private final static String OLBIUS_PWD = "OLBIUS_PWD";
	private final static String OLAP = "OLAP";
	private final static String OLAP_HOST = "OLAP_HOST";
	private final static String OLAP_PORT = "OLAP_PORT";
	private final static String OLAP_USER = "OLAP_USER";
	private final static String OLAP_PWD = "OLAP_PWD";
	
	private GenericHelperInfo ofbiz;
	
	private GenericHelperInfo olap;
	
	private Delegator delegator;
	
	private boolean update = true;
	
	public ProcessPentaho(String file) {
		this.file = file;
		
		process = new Process();
		
		process.setDir("applications/bi-x/integration");
		
		process.addCommand("/file", file);
		
		process.addCommand("/level", "Error");
	}
	
	public ProcessPentaho(String dir, String file, boolean use) {
		
		this.file = file;
		
		process = new Process();
		
		process.setDir(dir);
		
		if(use) {
			process.addCommand("/file", file);
		}
		
		process.addCommand("/level", "Error");
	}
	
	public void setDelegator(Delegator delegator) {
		this.delegator = delegator;
		setInfo(((GenericDelegator) delegator).getGroupHelperInfo("org.ofbiz"), ((GenericDelegator) delegator).getGroupHelperInfo("org.ofbiz.olap"));
	}
	
	public void addParam(String name, String value) {
		
		process.addCommand("/param:".concat(name).concat("=").concat(value), true);
		
	}
	
	public void addLogFile(String file) {
		
		if(!log) {
			process.addCommand("/logfile", file);
			log = true;
		}
		
	}
	
	public void setInfo(GenericHelperInfo ofbiz, GenericHelperInfo olap) {
		this.ofbiz = ofbiz;
		this.olap = olap;
	}
	
	public Timestamp getLastUpdate(Delegator delegator, String job) throws GenericEntityException {
		GenericValue value = null;
		
		value = EntityUtil.getFirst(delegator.findByAnd("SchedulePentaho", UtilMisc.toMap("job", job), UtilMisc.toList("-scheduleId"), false));
	
		Timestamp timestamp = null;
		if(value != null) {
			timestamp = value.getTimestamp("lastUpdated");
		} else {
			Calendar calendar = Calendar.getInstance();
			calendar.set(1990, 0, 1);
			timestamp = new Timestamp(calendar.getTimeInMillis());
		}
		return timestamp;
	}
	
	public Timestamp setDate(Delegator delegator, String name) throws GenericEntityException {
		GenericValue value = null;
		
		Timestamp timestamp = getLastUpdate(delegator, file);

		addParam(name, timestamp.toString());
		
		value = delegator.makeValue("SchedulePentaho");
		String id = delegator.getNextSeqId("SchedulePentaho");
		value.set("scheduleId", id);
		value.set("job", file);
		timestamp = new Timestamp(System.currentTimeMillis());
		value.set("lastUpdated", timestamp);
		value.create();
		return timestamp;
	}
	
	public void setDate(Delegator delegator, String name, long time) throws GenericEntityException {
		GenericValue value = delegator.makeValue("SchedulePentaho");
		String id = delegator.getNextSeqId("SchedulePentaho");
		value.set("scheduleId", id);
		value.set("job", file);
		Timestamp timestamp = new Timestamp(time);
		value.set("lastUpdated", timestamp);
		value.create();
		addParam(name, timestamp.toString());
	}

	public void start(String name) throws Exception {
		
		Timestamp timestamp = getLastUpdate(delegator, file);

		addParam(name, timestamp.toString());
		
		start();
		
		int i = process.waitFor();
		
		if(i == 0) {
			upadteSchedulePentaho(new Timestamp(System.currentTimeMillis()));
		}
		
	}
	
	private void upadteSchedulePentaho(Timestamp timestamp) throws GenericEntityException {
		GenericValue value = delegator.makeValue("SchedulePentaho");
		String id = delegator.getNextSeqId("SchedulePentaho");
		value.set("scheduleId", id);
		value.set("job", file);
		value.set("lastUpdated", timestamp);
		value.create();
	}
	
	public void start(String name, long time) throws GenericEntityException {
		
		Timestamp timestamp = new Timestamp(time);

		addParam(name, timestamp.toString());
		
		try {
			start();
		} catch (Exception e) {
			return;
		}
		
		int i = process.waitFor();
		
		if(i == 0) {
			upadteSchedulePentaho(timestamp);
		}
		
	}
	
	public void start(String last, String past, String cur, long time) throws GenericEntityException {
		Timestamp timestamp = getLastUpdate(delegator, file);

		addParam(last, timestamp.toString());
		
		timestamp = new Timestamp(time);

		addParam(cur, timestamp.toString());
		
		long past_date = UtilProperties.getPropertyAsInteger("pentaho", "past_date", 365);
		
		timestamp = new Timestamp(time - past_date*86400000);
		
		addParam(past, timestamp.toString());
		
		try {
			start();
		} catch (Exception e) {
			return;
		}
		
		int i = process.waitFor();
		
		if(i == 0) {
			upadteSchedulePentaho(new Timestamp(time));
		} else {
			throw new GenericEntityException("Pentaho error code: " + i);
		}
	}
	
	public void start(String... bash) throws Exception {
		
		if(ofbiz != null && olap != null) {
			Datasource datasourceInfo = EntityConfigUtil.getDatasource(ofbiz.getHelperBaseName());
        	
        	InlineJdbc jdbcElement = (InlineJdbc)datasourceInfo.getInlineJdbc();
        	
        	String _uri_ofbiz = UtilValidate.isNotEmpty(ofbiz.getOverrideJdbcUri()) ? ofbiz.getOverrideJdbcUri() : jdbcElement.getJdbcUri();
        	String _user_ofbiz = UtilValidate.isNotEmpty(ofbiz.getOverrideUsername()) ? ofbiz.getOverrideUsername() : jdbcElement.getJdbcUsername();
        	String _pwd_ofbiz = UtilValidate.isNotEmpty(ofbiz.getOverridePassword()) ? ofbiz.getOverridePassword() : EntityConfigUtil.getJdbcPassword(jdbcElement);
        	

        	String _s = _uri_ofbiz.substring(_uri_ofbiz.indexOf("://")+3);
        	String[] tmp = _s.split("/");
        	
        	String _dbn_ofbiz = tmp[1];
        	
        	tmp = tmp[0].split(":");
        	
        	String _host_ofbiz = tmp[0];
        	String _port_ofbiz = "";
        	if(tmp.length < 2) {
        		if(jdbcElement.getJdbcDriver().equals("org.postgresql.Driver")) {
        			_port_ofbiz = "5432";
        		}
        	} else {
        		_port_ofbiz = tmp[1];
        	}
        	
        	addParam(OLBIUS, _dbn_ofbiz);
        	addParam(OLBIUS_HOST, _host_ofbiz);
        	if(!_port_ofbiz.isEmpty()) {
        		addParam(OLBIUS_PORT, _port_ofbiz);
        	}
        	addParam(OLBIUS_USER, _user_ofbiz);
        	addParam(OLBIUS_PWD, _pwd_ofbiz);
        	
        	datasourceInfo = EntityConfigUtil.getDatasource(olap.getHelperBaseName());
        	
        	jdbcElement = (InlineJdbc)datasourceInfo.getInlineJdbc();
        	
        	String _uri_olap = UtilValidate.isNotEmpty(olap.getOverrideJdbcUri()) ? olap.getOverrideJdbcUri() : jdbcElement.getJdbcUri();
        	String _user_olap = UtilValidate.isNotEmpty(olap.getOverrideUsername()) ? olap.getOverrideUsername() : jdbcElement.getJdbcUsername();
        	String _pwd_olap = UtilValidate.isNotEmpty(olap.getOverridePassword()) ? olap.getOverridePassword() : EntityConfigUtil.getJdbcPassword(jdbcElement);
        	
        	_s = _uri_olap.substring(_uri_olap.indexOf("://")+3);
        	tmp = _s.split("/");
        	
        	String _dbn_olap = tmp[1];
        	
        	tmp = tmp[0].split(":");
        	
        	String _host_olap = tmp[0];
        	String _port_olap = "";
        	if(tmp.length < 2) {
        		if(jdbcElement.getJdbcDriver().equals("org.postgresql.Driver")) {
        			_port_olap = "5432";
        		}
        	} else {
        		_port_olap = tmp[1];
        	}
        	
        	addParam(OLAP, _dbn_olap);
        	addParam(OLAP_HOST, _host_olap);
        	if(!_port_olap.isEmpty()) {
        		addParam(OLAP_PORT, _port_olap);
        	}
        	addParam(OLAP_USER, _user_olap);
        	addParam(OLAP_PWD, _pwd_olap);
		}
		
		process.start(bash);
	}
	
	public boolean isUpdate() {
		return update;
	}

	public void setUpdate(boolean update) {
		this.update = update;
	}

	public Process getProcess() {
		return process;
	}
	
}
