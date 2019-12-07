package com.olbius.entity.tenant;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.DelegatorFactory;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.config.EntityConfigUtil;
import org.ofbiz.entity.config.model.Datasource;
import org.ofbiz.entity.config.model.InlineJdbc;
import org.ofbiz.entity.datasource.GenericHelperInfo;
import org.ofbiz.entity.util.EntityUtil;

public class OlbiusTenant {

	public final static String module = OlbiusTenant.class.getName();
	
	private static final Tenant TENANT;

	static {
		String useMultitenant = UtilProperties.getPropertyValue("general.properties", "multitenant");
		if("Y".equals(useMultitenant)) {
			TENANT = new MultiTenant();
		} else {
			TENANT = new SingleTenant();
		}
	}
	
	public static String getTenantId(Delegator delegator) {
		return TENANT.getTenantId(delegator);
	}
	
	private static interface Tenant {
		
		String getTenantId(Delegator delegator);
		
	}
	
	private static class SingleTenant implements Tenant{

		private String tenantId;
		
		@Override
		public String getTenantId(Delegator delegator) {
			
			if (tenantId != null) {
				return tenantId;
			}
			
			tenantId = UtilProperties.getPropertyValue("general.properties", "tenantid");
			
			if (tenantId != null) {
				return tenantId;
			}
			
			Delegator tmp = DelegatorFactory.getDelegator("default");

			GenericHelperInfo ofbiz = ((GenericDelegator) tmp).getGroupHelperInfo("org.ofbiz");

			Datasource datasourceInfo = EntityConfigUtil.getDatasource(ofbiz.getHelperBaseName());

			InlineJdbc jdbcElement = datasourceInfo.getInlineJdbc();

			String _uri_ofbiz = UtilValidate.isNotEmpty(ofbiz.getOverrideJdbcUri()) ? ofbiz.getOverrideJdbcUri()
					: jdbcElement.getJdbcUri();

			try {
				GenericValue value = EntityUtil.getFirst(tmp.findByAnd("TenantDataSource",
						UtilMisc.toMap("entityGroupName", "org.ofbiz", "jdbcUri", _uri_ofbiz), null, false));
				if(value != null) {
					tenantId = value.getString("tenantId");
				}
			} catch (GenericEntityException e) {
				Debug.logError(e, e.getMessage(), module);
			}

			return tenantId;
		}
	}
	
	private static class MultiTenant implements Tenant {

		@Override
		public String getTenantId(Delegator delegator) {
			return delegator.getDelegatorTenantId();
		}
		
	}
	
}
