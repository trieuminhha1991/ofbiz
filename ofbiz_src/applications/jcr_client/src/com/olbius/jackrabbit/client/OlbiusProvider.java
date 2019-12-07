package com.olbius.jackrabbit.client;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.jcr.Repository;
import javax.jcr.RepositoryException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.jackrabbit.commons.JcrUtils;
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
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentType;
import org.w3c.dom.Element;

public class OlbiusProvider {

	public static final String module = OlbiusProvider.class.getName();

	private static final boolean dev = UtilProperties.getPropertyAsBoolean("jcr_client", "jcr.dev", false);

	private String remoteJcrUrl;
	private final Map<String, Repository> repositories = new HashMap<String, Repository>();
	private static OlbiusProvider instance;
	private static boolean init;

	public static final String WSP_SECURITY = "security";
	public static final String WSP_DEFAULT = "default";

	public static final String WEB_DAV_URI = "/storage/repository/";

	private static String jcrUrl;

	public static Process process;

	private OlbiusProvider() {
		this.remoteJcrUrl = jcrUrl;
	}

	public static boolean checkConnect(String url) {

		try {
			JcrUtils.getRepository(url);
			return true;
		} catch (RepositoryException e) {
			return false;
		}
	}

	public static String getJcrPath(String path) {
		String s = path;

		s = s.substring(WEB_DAV_URI.length());

		if (s.startsWith(WSP_DEFAULT)) {
			s = s.substring(WSP_DEFAULT.length());
		}
		if (s.startsWith(WSP_SECURITY)) {
			s = s.substring(WSP_SECURITY.length());
		}

		return s;
	}

	public static void destroy() {
		if (process != null) {
			process.destroy();

			if (dev) {
				File f = new File("applications/jcr_client/dev/jackrabbit/tmp/lib");
				if (f.exists() && f.isDirectory()) {
					String files[] = f.list();
					for (String temp : files) {
						File fileDelete = new File(f, temp);
						fileDelete.delete();
					}
				}
			}

		}
	}

	public static boolean dev(String admin) throws ParserConfigurationException, TransformerException, IOException {

		if (dev) {
			String port = UtilProperties.getPropertyValue("jcr_client", "jcr.port", "8888");

			jcrUrl = "http://localhost:" + port + "/";

			if (!checkConnect(jcrUrl+"server")) {
				File f = new File("applications/jcr_client/dev/repository.xml");

				if (f.exists()) {
					f.delete();
				}

				Delegator delegator = DelegatorFactory.getDelegator("default");

				GenericHelperInfo ofbiz = ((GenericDelegator) delegator).getGroupHelperInfo("org.ofbiz");

				GenericHelperInfo tenant = ((GenericDelegator) delegator).getGroupHelperInfo("org.ofbiz.tenant");

				Datasource datasourceInfo = EntityConfigUtil.getDatasource(ofbiz.getHelperBaseName());

				InlineJdbc jdbcElement = datasourceInfo.getInlineJdbc();

				String _uri_ofbiz = UtilValidate.isNotEmpty(ofbiz.getOverrideJdbcUri()) ? ofbiz.getOverrideJdbcUri()
						: jdbcElement.getJdbcUri();
				String _user_ofbiz = UtilValidate.isNotEmpty(ofbiz.getOverrideUsername()) ? ofbiz.getOverrideUsername()
						: jdbcElement.getJdbcUsername();
				String _pwd_ofbiz = UtilValidate.isNotEmpty(ofbiz.getOverridePassword()) ? ofbiz.getOverridePassword()
						: EntityConfigUtil.getJdbcPassword(jdbcElement);

				datasourceInfo = EntityConfigUtil.getDatasource(tenant.getHelperBaseName());

				jdbcElement = datasourceInfo.getInlineJdbc();

				String _uri_tenant = UtilValidate.isNotEmpty(tenant.getOverrideJdbcUri()) ? tenant.getOverrideJdbcUri()
						: jdbcElement.getJdbcUri();
				String _user_tenant = UtilValidate.isNotEmpty(tenant.getOverrideUsername())
						? tenant.getOverrideUsername() : jdbcElement.getJdbcUsername();
				String _pwd_tenant = UtilValidate.isNotEmpty(tenant.getOverridePassword())
						? tenant.getOverridePassword() : EntityConfigUtil.getJdbcPassword(jdbcElement);

				DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
				DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

				Document doc = docBuilder.newDocument();

				DOMImplementation domImpl = doc.getImplementation();
				DocumentType doctype = domImpl.createDocumentType("Repository",
						"-//The Apache Software Foundation//DTD Jackrabbit 2.0//EN",
						"http://jackrabbit.apache.org/dtd/repository-2.0.dtd");
				doc.appendChild(doctype);
				Element param;

				Element rootElement = doc.createElement("Repository");
				doc.appendChild(rootElement);

				Element dataStore = doc.createElement("DataStore");
				dataStore.setAttribute("class", "org.apache.jackrabbit.core.data.FileDataStore");
				rootElement.appendChild(dataStore);

				Element fileSystem = doc.createElement("FileSystem");
				fileSystem.setAttribute("class", "org.apache.jackrabbit.core.fs.local.LocalFileSystem");
				param = doc.createElement("param");
				param.setAttribute("name", "path");
				param.setAttribute("value", "${rep.home}/repository");
				fileSystem.appendChild(param);
				rootElement.appendChild(fileSystem);

				Element security = doc.createElement("Security");
				security.setAttribute("appName", "Jackrabbit");
				Element securityManager = doc.createElement("SecurityManager");
				securityManager.setAttribute("class", "com.olbius.jcr.OlbiusSecurityManager");
				securityManager.setAttribute("workspaceName", "security");
				security.appendChild(securityManager);
				Element accessManager = doc.createElement("AccessManager");
				accessManager.setAttribute("class", "org.apache.jackrabbit.core.security.DefaultAccessManager");
				security.appendChild(accessManager);
				Element loginModule = doc.createElement("LoginModule");
				loginModule.setAttribute("class", "com.olbius.jcr.security.authentication.OlbiusLoginModule");
				param = doc.createElement("param");
				param.setAttribute("name", "anonymousId");
				param.setAttribute("value", "jcr.anonymous");
				loginModule.appendChild(param);
				param = doc.createElement("param");
				param.setAttribute("name", "adminId");
				param.setAttribute("value", admin);
				loginModule.appendChild(param);
				param = doc.createElement("param");
				param.setAttribute("name", "principalProvider");
				param.setAttribute("value", "com.olbius.jcr.security.OlbiusPrincipalProvider");
				loginModule.appendChild(param);
				param = doc.createElement("param");
				/*param.setAttribute("name", "url.default");
				param.setAttribute("value", _uri_ofbiz);
				loginModule.appendChild(param);
				param = doc.createElement("param");
				param.setAttribute("name", "user.default");
				param.setAttribute("value", _user_ofbiz);
				loginModule.appendChild(param);
				param = doc.createElement("param");
				param.setAttribute("name", "password.default");
				param.setAttribute("value", _pwd_ofbiz);
				loginModule.appendChild(param);
				param = doc.createElement("param");

				String useMultitenant = UtilProperties.getPropertyValue("general.properties", "multitenant");
				if ("Y".equals(useMultitenant)) {
					param.setAttribute("name", "url.tenant");
					param.setAttribute("value", _uri_tenant);
					loginModule.appendChild(param);
					param = doc.createElement("param");
					param.setAttribute("name", "user.tenant");
					param.setAttribute("value", _user_tenant);
					loginModule.appendChild(param);
					param = doc.createElement("param");
					param.setAttribute("name", "password.tenant");
					param.setAttribute("value", _pwd_tenant);
					loginModule.appendChild(param);
				}*/
				
				param.setAttribute("name", "url");
				param.setAttribute("value", _uri_tenant);
				loginModule.appendChild(param);
				param = doc.createElement("param");
				param.setAttribute("name", "user");
				param.setAttribute("value", _user_tenant);
				loginModule.appendChild(param);
				param = doc.createElement("param");
				param.setAttribute("name", "password");
				param.setAttribute("value", _pwd_tenant);
				loginModule.appendChild(param);

				param = doc.createElement("param");
				param.setAttribute("name", "driver");
				param.setAttribute("value", "com.mysql.jdbc.Driver");
				loginModule.appendChild(param);
				security.appendChild(loginModule);
				rootElement.appendChild(security);

				Element workspaces = doc.createElement("Workspaces");
				workspaces.setAttribute("rootPath", "${rep.home}/workspaces");
				workspaces.setAttribute("defaultWorkspace", "default");
				rootElement.appendChild(workspaces);

				Element workspace = doc.createElement("Workspace");
				workspace.setAttribute("name", "${wsp.name}");
				Element _fileSystem = doc.createElement("FileSystem");
				_fileSystem.setAttribute("class", "org.apache.jackrabbit.core.fs.local.LocalFileSystem");
				param = doc.createElement("param");
				param.setAttribute("name", "path");
				param.setAttribute("value", "${wsp.home}");
				_fileSystem.appendChild(param);
				workspace.appendChild(_fileSystem);
				Element _persistenceManager = doc.createElement("PersistenceManager");
				_persistenceManager.setAttribute("class",
						"org.apache.jackrabbit.core.persistence.pool.DerbyPersistenceManager");
				param = doc.createElement("param");
				param.setAttribute("name", "url");
				param.setAttribute("value", "jdbc:derby:${wsp.home}/db;create=true");
				_persistenceManager.appendChild(param);
				param = doc.createElement("param");
				param.setAttribute("name", "schemaObjectPrefix");
				param.setAttribute("value", "${wsp.name}_");
				_persistenceManager.appendChild(param);
				workspace.appendChild(_persistenceManager);
				Element _searchIndex = doc.createElement("SearchIndex");
				_searchIndex.setAttribute("class", "org.apache.jackrabbit.core.query.lucene.SearchIndex");
				param = doc.createElement("param");
				param.setAttribute("name", "path");
				param.setAttribute("value", "${wsp.home}/index");
				_searchIndex.appendChild(param);
				param = doc.createElement("param");
				param.setAttribute("name", "supportHighlighting");
				param.setAttribute("value", "true");
				_searchIndex.appendChild(param);
				workspace.appendChild(_searchIndex);
				Element _workspaceSecurity = doc.createElement("WorkspaceSecurity");
				Element _accessControlProvider = doc.createElement("AccessControlProvider");
				_accessControlProvider.setAttribute("class",
						"org.apache.jackrabbit.core.security.authorization.acl.OlbiusACLProvider");
				param = doc.createElement("param");
				param.setAttribute("name", "allow-unknown-principals");
				param.setAttribute("value", "true");
				_accessControlProvider.appendChild(param);
				_workspaceSecurity.appendChild(_accessControlProvider);
				workspace.appendChild(_workspaceSecurity);
				rootElement.appendChild(workspace);

				Element versioning = doc.createElement("Versioning");
				versioning.setAttribute("rootPath", "${rep.home}/version");
				_fileSystem = doc.createElement("FileSystem");
				_fileSystem.setAttribute("class", "org.apache.jackrabbit.core.fs.local.LocalFileSystem");
				param = doc.createElement("param");
				param.setAttribute("name", "path");
				param.setAttribute("value", "${rep.home}/version");
				_fileSystem.appendChild(param);
				versioning.appendChild(_fileSystem);
				_persistenceManager = doc.createElement("PersistenceManager");
				_persistenceManager.setAttribute("class",
						"org.apache.jackrabbit.core.persistence.pool.DerbyPersistenceManager");
				param = doc.createElement("param");
				param.setAttribute("name", "url");
				param.setAttribute("value", "jdbc:derby:${rep.home}/version/db;create=true");
				_persistenceManager.appendChild(param);
				param = doc.createElement("param");
				param.setAttribute("name", "schemaObjectPrefix");
				param.setAttribute("value", "version_");
				_persistenceManager.appendChild(param);
				versioning.appendChild(_persistenceManager);
				rootElement.appendChild(versioning);

				Element searchIndex = doc.createElement("SearchIndex");
				searchIndex.setAttribute("class", "org.apache.jackrabbit.core.query.lucene.SearchIndex");
				param = doc.createElement("param");
				param.setAttribute("name", "path");
				param.setAttribute("value", "${rep.home}/repository/index");
				searchIndex.appendChild(param);
				param = doc.createElement("param");
				param.setAttribute("name", "supportHighlighting");
				param.setAttribute("value", "true");
				searchIndex.appendChild(param);
				rootElement.appendChild(searchIndex);

				Element cluster = doc.createElement("Cluster");
				cluster.setAttribute("id", "node1");
				Element _journal = doc.createElement("Journal");
				_journal.setAttribute("class", "org.apache.jackrabbit.core.journal.MemoryJournal");
				cluster.appendChild(_journal);
				rootElement.appendChild(cluster);

				TransformerFactory transformerFactory = TransformerFactory.newInstance();
				Transformer transformer = transformerFactory.newTransformer();
				DOMSource source = new DOMSource(doc);
				StreamResult result = new StreamResult(new File("applications/jcr_client/dev/repository.xml"));

				transformer.setOutputProperty(OutputKeys.DOCTYPE_PUBLIC, doctype.getPublicId());
				transformer.setOutputProperty(OutputKeys.DOCTYPE_SYSTEM, doctype.getSystemId());

				transformer.transform(source, result);

				ProcessBuilder processBuilder = new ProcessBuilder();
				List<String> command = new ArrayList<String>();
				command.add("java");
				command.add("-jar");
				command.add("jackrabbit-standalone-2.12.0.jar");
				command.add("-c");
				command.add("repository.xml");
				command.add("-p");
				command.add(port);

				processBuilder.directory(new File("applications/jcr_client/dev"));

				processBuilder.command(command);

				process = processBuilder.inheritIO().start();

				while (!checkConnect(jcrUrl+"server")) {
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				
				try {
					GenericValue value = delegator.findOne("Tenant", UtilMisc.toMap("tenantId", "default"), false);
					if(value == null) {
						value = delegator.makeValue("Tenant");
						value.set("tenantId", "default");
						value.create();
					}
					value = delegator.findOne("TenantDataSource", UtilMisc.toMap("tenantId", "default", "entityGroupName", "org.ofbiz"), false);
					if(value == null) {
						value = delegator.makeValue("TenantDataSource");
						value.set("tenantId", "default");
						value.set("entityGroupName", "org.ofbiz");
						value.set("jdbcUri", _uri_ofbiz);
						value.set("jdbcUsername", _user_ofbiz);
						value.set("jdbcPassword", _pwd_ofbiz);
						value.create();
					}
				} catch (GenericEntityException e) {
					Debug.logError(e.getMessage(), module);
				}
			}

		} else {
			jcrUrl = UtilProperties.getPropertyValue("jcr_client", "jcr.remote.url");
			if(!jcrUrl.endsWith("/")) {
				jcrUrl += "/";
			}
		}

		init = true;

		return dev;

	}

	public static OlbiusProvider getInstance() {
		if (instance == null) {
			instance = new OlbiusProvider();
		}
		return instance;
	}

	/*public Session getSession(String userName, String password, String tenantId, String workspace, boolean security)
			throws LoginException, RepositoryException {
		if (tenantId != null && tenantId.isEmpty()) {
			tenantId = null;
		}
		if (security) {
			return getRepository().login(new OlbiusCredentials(userName, password, tenantId), workspace);
		} else {
			return getRepository().login(new OlbiusCredentials(userName, password, tenantId), workspace);
		}
	}*/

	public Repository getRepository(String remote) throws RepositoryException {
		if (repositories.get(remote) == null) {
			if (!init) {
				return null;
			}
			try {
				repositories.put(remote, JcrUtils.getRepository(remoteJcrUrl+remote));
			} catch (RepositoryException e) {
				Debug.logError(e, e.getMessage(), module);
				throw new RepositoryException(e);
			}
		}
		return repositories.get(remote);
	}

	public void resetRepository(String remote) {
		repositories.remove(remote);
	}

}
