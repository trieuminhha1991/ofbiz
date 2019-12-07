package com.olbius.jackrabbit.client.services;

import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import javax.jcr.Node;
import javax.jcr.PathNotFoundException;
import javax.jcr.nodetype.NodeType;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.ServiceUtil;

import com.olbius.entity.tenant.OlbiusTenant;
import com.olbius.jackrabbit.client.api.ClientNodePermission;
import com.olbius.jackrabbit.client.api.ClientSession;
import com.olbius.jackrabbit.client.core.OlbiusSession;
import com.olbius.jackrabbit.client.loader.JackrabbitClientContainer;
import com.olbius.jcr.security.principal.EveryonePrincipal;
import com.olbius.jcr.security.principal.OlbiusGroupPrincipal;
import com.olbius.jcr.security.principal.OlbiusTenantPrincipal;
import com.olbius.service.OlbiusService;

public class JackrabbitRegistryFolder implements OlbiusService {

	public final static String module = JackrabbitRegistryFolder.class.getName();

	@Override
	public Map<String, Object> run(DispatchContext dctx, Map<String, Object> context) throws Exception {

		String folder = (String) context.get("folder");
		String permissions = (String) context.get("permissions");

		String tenantId = OlbiusTenant.getTenantId(dctx.getDelegator());

		ClientSession session = JackrabbitClientContainer.SESSION_FACTORY
				.newSystemInstance(OlbiusSession.WORKSPACE_SECURITY);

		String perm = permissions.trim();
		String[] perms = perm.split(",");

		Set<String> tmp = new TreeSet<String>();

		for (String y : perms) {
			tmp.add(y);
		}
		try {
			createFolder(session, tenantId, folder, tmp);
		} finally {
			session.logout();
		}
		return ServiceUtil.returnSuccess();
	}

	private void createFolder(ClientSession session, String tenantId, String folder, Set<String> permissions)
			throws Exception {

		Node node = session.getNode();

		if (tenantId != null) {
			try {
				node = node.getNode(tenantId);
			} catch (PathNotFoundException e) {
				node = node.addNode(tenantId, NodeType.NT_FOLDER);
				node.getSession().save();
				session.getNodeSecurity().addEntry(node, EveryonePrincipal.getInstance(),
						session.getNodeSecurity().getNodePermission().permission(ClientNodePermission.VIEW), false);
				session.getNodeSecurity().addEntry(node, new OlbiusTenantPrincipal(tenantId),
						session.getNodeSecurity().getNodePermission().permission(ClientNodePermission.VIEW), true);
			}
		}

		Node node2 = null;
		try {
			node2 = node.getNode(folder);
		} catch (PathNotFoundException e) {
			node2 = node.addNode(folder, NodeType.NT_FOLDER);
			node2.getSession().save();
		}

		String[] permission = new String[] { ClientNodePermission.ADMIN, ClientNodePermission.CREATE,
				ClientNodePermission.DELETE, ClientNodePermission.UPDATE, ClientNodePermission.VIEW };

		for (String y : permissions) {
			for (String z : permission) {

				session.getNodeSecurity().removeEntry(node2,
						new OlbiusGroupPrincipal(y.concat("_").concat(z), tenantId));

				session.getNodeSecurity().addEntry(node2, new OlbiusGroupPrincipal(y.concat("_").concat(z), tenantId),
						session.getNodeSecurity().getNodePermission().permission(z), true);
			}
		}
	}

}
