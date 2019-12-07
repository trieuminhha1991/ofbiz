package com.olbius.jackrabbit.loader;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import javax.jcr.LoginException;
import javax.jcr.NoSuchWorkspaceException;
import javax.jcr.Repository;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.SimpleCredentials;

import org.apache.jackrabbit.core.RepositoryImpl;
import org.apache.jackrabbit.core.config.ConfigurationException;
import org.apache.jackrabbit.core.config.RepositoryConfig;
import org.ofbiz.base.container.Container;
import org.ofbiz.base.container.ContainerConfig;
import org.ofbiz.base.container.ContainerException;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.DelegatorFactory;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;

import com.olbius.jackrabbit.core.Constant;
import com.olbius.jackrabbit.core.OlbiusRepository;
import com.olbius.jackrabbit.tenant.OlbiusTenant;
import com.olbius.jackrabbit.tenant.OlbiusTenantImpl;

public class JackrabbitOlbiusContainer extends OlbiusContainer implements Container {

	public static final String module = JackrabbitOlbiusContainer.class.getName();

	private static final String REPO_DIR = "repository.dir";
	private static final String REPO_CONFIG = "repository.config";
	private static final String REPO_USER = "repository.user";
	private static final String REPO_PWD = "repository.password";
	private static final String REPO_MODE = "tenant.mode";
	private static final String TENANT_DEFAULT = "tenant.default";
	private static final String SINGLE_TENANT = "SINGLE";
	private static final String MULTI_TENANT = "MULTI";
	private static final String JCR_URL = "jcr.url";

	private static Repository repository;

	private RepositoryConfig config;
	private ContainerConfig.Container cfg;
	private String CONTAINER_NAME;

	@Override
	public void init(String[] args, String name, String configFile) throws ContainerException {
		cfg = ContainerConfig.getContainer(name, configFile);
		CONTAINER_NAME = name;
		Debug.logInfo("Initializing " + CONTAINER_NAME, module);
		addComponent();
		try {
			this.config = RepositoryConfig.create(cfg.getProperty(REPO_CONFIG).value, cfg.getProperty(REPO_DIR).value);
		} catch (ConfigurationException e) {
			throw new ContainerException(e);
		}
	}

	private void initTenant() throws ContainerException, RepositoryException {
		String admin = cfg.getProperty(REPO_USER).value;
		String pwd = cfg.getProperty(REPO_PWD).value;
		String mode = null;
		if (cfg.getProperty(TENANT_DEFAULT) != null) {
			Constant.setTenantDefault(cfg.getProperty(TENANT_DEFAULT).value);
		}
		if (cfg.getProperty(REPO_MODE) != null) {
			mode = cfg.getProperty(REPO_MODE).value;
		} else {
			mode = SINGLE_TENANT;
		}
		if (cfg.getProperty(JCR_URL) != null) {
			Constant.setUrl(cfg.getProperty(JCR_URL).value);
		}
		Session session;
		try {
			session = repository.login(new SimpleCredentials(admin, pwd.toCharArray()), OlbiusRepository.WSP_SECURITY);
			OlbiusContainer.setAdmin(admin);
			OlbiusContainer.setPwd(pwd);
		} catch (RepositoryException e) {
			throw new ContainerException(e);
		}

		Delegator delegator = createDelegator();
		initTenant(delegator, session);
		if (mode.equals(MULTI_TENANT)) {
			List<GenericValue> tenants = null;
			try {
				tenants = delegator.findList("Tenant", null, null, UtilMisc.toList("tenantId"), null, false);
			} catch (GenericEntityException e) {
				throw new ContainerException(e);
			}
			Delegator d = null;

			for (GenericValue value : tenants) {
				d = DelegatorFactory.getDelegator(delegator.getDelegatorBaseName() + "#" + value.getString("tenantId"));
				try {
					initTenant(d, session);
				} catch (ContainerException e) {
					throw new ContainerException(e);
				}
			}
		}
		session.logout();
	}

	private void initTenant(Delegator delegator, Session session) throws ContainerException, RepositoryException {
		List<GenericValue> users = null;
		List<GenericValue> groups = null;
		List<GenericValue> permissions = null;
		List<String> parties = new ArrayList<String>();
		Map<String, Map<String, String>> partyRelationship = new HashMap<String, Map<String, String>>();
		Map<String, Map<String, String>> mapUser = new HashMap<String, Map<String, String>>();
		Map<String, Set<String>> mapGroup = new HashMap<String, Set<String>>();
		Map<String, Set<String>> mapPermission = new HashMap<String, Set<String>>();

		try {

			/*List<GenericValue> list = delegator.findList("Party", null, null, UtilMisc.toList("partyId"), null, false);

			for (GenericValue value : list) {
				parties.add(value.getString("partyId"));
				List<GenericValue> list2 = delegator.findList("PartyRelationship",
						EntityCondition.makeCondition("partyIdFrom", EntityOperator.EQUALS, value.getString("partyId")), null,
						UtilMisc.toList("partyIdFrom"), null, false);
				Map<String, String> map = new HashMap<String, String>();
				for(GenericValue value2: list2) {
					if(value2.getString("partyRelationshipTypeId") != null) {
						map.put(value2.getString("partyIdTo"), value2.getString("partyRelationshipTypeId"));
					}
				}
				if(!map.keySet().isEmpty()) {
					partyRelationship.put(value.getString("partyId"), map);
				}
			}*/

			users = delegator.findList("UserLogin", null, null, UtilMisc.toList("userLoginId"), null, false);
			for (GenericValue value2 : users) {
				Map<String, String> map = new HashMap<String, String>();
				map.put("pwd", value2.getString("currentPassword"));
				map.put("partyId", value2.getString("partyId"));
				mapUser.put(value2.getString("userLoginId"), map);
			}
			groups = delegator.findList("SecurityGroup", null, null, UtilMisc.toList("groupId"), null, false);
			for (GenericValue value2 : groups) {
				Set<String> set = new TreeSet<String>();

				mapGroup.put(value2.getString("groupId"), set);

				List<GenericValue> tmp = delegator.findList("UserLoginAndSecurityGroup",
						EntityCondition.makeCondition("groupId", EntityOperator.EQUALS, value2.getString("groupId")), null,
						UtilMisc.toList("groupId"), null, false);
				for (GenericValue value3 : tmp) {
					set.add(value3.getString("userLoginId"));
				}

			}
			permissions = delegator.findList("SecurityPermission", null, null, UtilMisc.toList("permissionId"), null, false);
			for (GenericValue value2 : permissions) {
				Set<String> set = new TreeSet<String>();
				mapPermission.put(value2.getString("permissionId"), set);
				List<GenericValue> tmp = delegator.findList("SecurityGroupPermission",
						EntityCondition.makeCondition("permissionId", EntityOperator.EQUALS, value2.getString("permissionId")), null,
						UtilMisc.toList("groupId"), null, false);
				for (GenericValue value3 : tmp) {
					set.add(value3.getString("groupId"));
				}
			}
		} catch (GenericEntityException e) {
			throw new ContainerException(e);
		}
		String tenantId = delegator.getDelegatorTenantId();
		if (tenantId == null || tenantId.isEmpty()) {
			tenantId = Constant.getTenantDefault();
		}
		OlbiusTenant tenant = new OlbiusTenantImpl(tenantId, session);
		((OlbiusTenantImpl) tenant).init(OlbiusContainer.getMapComponent(), parties, partyRelationship, mapUser, mapGroup, mapPermission,
				OlbiusContainer.getAdminPermission());
	}

	@Override
	public boolean start() throws ContainerException {
		try {
			repository = OlbiusRepository.create(config);
		} catch (RepositoryException e) {
			throw new ContainerException(e);
		}
		try {
			initTenant();
		} catch (RepositoryException e) {
			throw new ContainerException(e);
		}
		return true;
	}

	@Override
	public void stop() throws ContainerException {
		if (repository != null) {
			((RepositoryImpl) repository).shutdown();
		}
	}

	@Override
	public String getName() {
		return CONTAINER_NAME;
	}

	public static Repository getRepository() {
		return repository;
	}

	private Delegator createDelegator() {
		String delegatorName = ContainerConfig.getPropertyValue(cfg, "delegator-name", "default");
		return DelegatorFactory.getDelegator(delegatorName);
	}

	@Override
	public void addComponent() throws ContainerException {
		String permission = null;
		String[] permissions = null;
		String adminPermission = cfg.getProperty("tenant.permission").value;
		String[] adminPermissions = adminPermission.split(",");
		for (String y : adminPermissions) {
			getAdminPermission().add(y);
		}
		for (String s : cfg.getProperty("webapp").properties.keySet()) {
			permission = cfg.getProperty("webapp").getProperty(s).value.trim();
			permissions = permission.split(",");
			Set<String> tmp = getMapComponent().get(s);
			if (tmp == null) {
				tmp = new TreeSet<String>();
				getMapComponent().put(s, tmp);
			}
			for (String y : permissions) {
				tmp.add(y);
			}
		}
	}

	public static Session getSession() throws LoginException, NoSuchWorkspaceException, RepositoryException {
		return repository.login(new SimpleCredentials(OlbiusContainer.getAdmin(), OlbiusContainer.getPwd().toCharArray()),
				OlbiusRepository.WSP_SECURITY);
	}
}
