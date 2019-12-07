package com.olbius.jackrabbit.client.core;

import java.util.HashMap;
import java.util.Map;

import javax.jcr.AccessDeniedException;
import javax.jcr.InvalidItemStateException;
import javax.jcr.ItemExistsException;
import javax.jcr.LoginException;
import javax.jcr.Node;
import javax.jcr.ReferentialIntegrityException;
import javax.jcr.Repository;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.SimpleCredentials;
import javax.jcr.lock.LockException;
import javax.jcr.nodetype.ConstraintViolationException;
import javax.jcr.nodetype.NoSuchNodeTypeException;
import javax.jcr.version.VersionException;


import org.apache.jackrabbit.rmi.client.RemoteRepositoryException;
import org.ofbiz.base.util.Debug;

import com.olbius.jackrabbit.client.OlbiusProvider;
import com.olbius.jackrabbit.client.api.ClientNode;
import com.olbius.jackrabbit.client.api.ClientNodeFactory;
import com.olbius.jackrabbit.client.api.ClientNodeSecurity;
import com.olbius.jackrabbit.client.api.ClientNodeSecurityFactory;
import com.olbius.jackrabbit.client.api.ClientSession;
import com.olbius.jackrabbit.client.api.ClientSessionFactory;
import com.olbius.jcr.OlbiusCredentials;
import com.olbius.jcr.security.principal.OlbiusPrincipal;
import com.olbius.jcr.security.principal.OlbiusUserPrincipal;

public class OlbiusSession implements ClientSession {

	public static final String WORKSPACE_DEFAULT = "default";
	public static final String WORKSPACE_SECURITY = "security";

	private static final ClientNodeFactory NODE_FACTORY = new OlbiusNodeFactory();
	private static final ClientNodeSecurityFactory NODE_FACTORY_SECURITY = new OlbiusNodeSecurityFactory();

	private String userLoginId;
	private String pwd;
	private String workspace;
	private String tenantId;
	private OlbiusProvider provider;
	private final Map<String, Session> jcrSessions = new HashMap<String, Session>();
	private Node node;
	private boolean system;
	private ClientNode clientNode;
	private ClientNodeSecurity clientNodeSecurity;
	private boolean root;
	private boolean save;
	private OlbiusPrincipal principal;
	private ClientSessionFactory factory;

	public OlbiusSession(String userLoginId, String pwd, String tenantId, String workspace) {
		this.userLoginId = userLoginId;
		this.pwd = pwd;
		this.tenantId = tenantId;
		this.workspace = workspace;
	}

	@Override
	public void setUserLoginId(String userLoginId) {
		this.userLoginId = userLoginId;
	}

	@Override
	public void setPassword(String pwd) {
		this.pwd = pwd;
	}

	@Override
	public void setWorkspace(String workspace) {
		this.workspace = workspace;
	}

	@Override
	public String getUserLoginId() {
		return this.userLoginId;
	}

	@Override
	public String getPassword() {
		return this.pwd;
	}

	@Override
	public String getWorkspace() {
		if (workspace == null) {
			return WORKSPACE_DEFAULT;
		} else {
			return this.workspace;
		}
	}

	@Override
	public Node getNode() throws Exception {
		if (node == null) {
			node = getJcrSession("server").getRootNode();
			if (!isSystem() && getTenantId() != null && WORKSPACE_SECURITY.equals(getWorkspace())) {
				node = node.getNode(getTenantId());
			}
			root = true;
		}
		return node;
	}

	@Override
	public Node getNode(String path) throws Exception {
		Node n;
		if (path == null || path.isEmpty()) {
			throw new Exception("Path is null");
		}
		if (path.startsWith("/")) {
			Node tmp = getNode();
			boolean flag = root;
			node = null;
			n = getNode();
			if (!"/".equals(path)) {
				n = n.getNode(path.substring(1));
			}
			node = tmp;
			root = flag;
		} else {
			n = getNode().getNode(path);
		}
		return n;
	}

	@Override
	public void setPath(String path) throws Exception {
		if (path == null || path.isEmpty()) {
			throw new Exception("Path is null");
		} else {
			if (path.startsWith("/")) {
				node = null;
				getNode();
				path = path.substring(1);
				root = true;
			}
			if (!path.isEmpty()) {
				node = getNode().getNode(path);
				root = false;
			}
		}
	}

	@Override
	public Session getJcrSession(String remote) throws Exception {

		if (jcrSessions.get(remote) != null) {
			return jcrSessions.get(remote);
		}

		try {
			if (getUserLoginId() != null) {
				Debug.log("[PQD] OlbiusSession::getJcrSession, remote = " + remote + ", isSystem = " + isSystem());
				if (isSystem()) {
					jcrSessions.put(remote, getRepository(remote).login(new SimpleCredentials(getUserLoginId(), getPassword().toCharArray()), getWorkspace()));
				} else {
					Debug.log("[PQD] OlbiusSession::getJcrSession, userLogin = " + getUserLoginId() + 
							", password = " + getPassword() + ", workspace = " + getWorkspace() + ", tenantId = " + getTenantId());
					jcrSessions.put(remote, getRepository(remote).login(new OlbiusCredentials(getUserLoginId(), getPassword(), getTenantId()), getWorkspace()));
					Debug.log("[PQD] OlbiusSession::getJcrSession, login OK");
					principal = new OlbiusUserPrincipal(getUserLoginId(), getTenantId());
					Debug.log("[PQD] OlbiusSession::getJcrSession, set principal OK");
				}
			} else {
				jcrSessions.put(remote, getRepository(remote).login(getWorkspace()));
			}
			return jcrSessions.get(remote);
		} catch (RemoteRepositoryException e) {
			provider.resetRepository(remote);
			throw e;
		} catch (LoginException e) {
			throw e;
		} catch (Exception e) {
			Debug.logError(e, OlbiusSession.class.getName());
			throw new Exception("JCR_SESSION not found", e);
		}
	}

	@Override
	public void logout() {
		for(String s: jcrSessions.keySet()) {
			jcrSessions.get(s).logout();
		}
		jcrSessions.clear();
		principal = null;
		clientNode = null;
		clientNodeSecurity = null;
	}

	@Override
	public void setTenantId(String tenantId) {
		this.tenantId = tenantId;
	}

	@Override
	public String getTenantId() {
		if (tenantId != null && tenantId.isEmpty()) {
			return null;
		}
		return this.tenantId;
	}

	@Override
	public void setSystem(boolean b) {
		this.system = b;
	}

	@Override
	public boolean isSystem() {
		return this.system;
	}

	@Override
	public ClientNode getClientNode() {

		if (clientNode == null) {
			clientNode = NODE_FACTORY.newInstance(this);
		}

		return clientNode;
	}

	@Override
	public ClientNodeSecurity getNodeSecurity() {

		if (clientNodeSecurity == null) {
			clientNodeSecurity = NODE_FACTORY_SECURITY.newInstance(this);
		}

		return clientNodeSecurity;
	}

	@Override
	public boolean isRoot() {
		return root;
	}

	@Override
	public boolean save(String remote) throws AccessDeniedException, ItemExistsException, ReferentialIntegrityException, ConstraintViolationException,
			InvalidItemStateException, VersionException, LockException, NoSuchNodeTypeException, RepositoryException {

		if (save) {
			jcrSessions.get(remote).save();
			return true;
		}

		return false;
	}

	@Override
	public void setSave(boolean b) {
		this.save = b;
	}

	@Override
	public OlbiusPrincipal getPrincipal() {
		return principal;
	}

	@Override
	public void setSessionFactory(ClientSessionFactory factory) {
		this.factory = factory;
	}

	@Override
	public ClientSessionFactory getSessionFactory() {
		return factory;
	}

	@Override
	public String getFullPath(Node node) throws RepositoryException {
		return OlbiusProvider.WEB_DAV_URI + getWorkspace() + node.getPath();
	}

	@Override
	public Repository getRepository(String remote) throws RepositoryException {
		return provider.getRepository(remote);
	}

	@Override
	public void setOlbiusProvider(OlbiusProvider provider) {
		this.provider = provider;
	}

}
