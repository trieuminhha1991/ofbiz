package com.olbius.jackrabbit.client.services;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.GenericServiceException;
import com.olbius.jackrabbit.client.api.ClientSession;
import com.olbius.jcr.security.principal.OlbiusGroupPrincipal;
import com.olbius.jcr.security.principal.OlbiusUserPrincipal;

public class JackrabbitOlbiusSecurityServices {

	public final static String module = JackrabbitOlbiusSecurityServices.class.getName();

	public static Map<String, Object> jackrabbitSetPermission(DispatchContext ctx, Map<String, ?> context) throws GenericServiceException {

		return JackrabbitServices.serviceBuilder(context, new JackrabbitServices.Callback() {
			@Override
			public void execute(Map<String, ?> context, ClientSession session, Map<String, Object> result) throws Exception {

				String curPath = (String) context.get("curPath");

				String userId = (String) context.get("userId");

				String groupId = (String) context.get("groupId");

				String permission = (String) context.get("permission");

				Boolean allow = (Boolean) context.get("allow");

				String[] privileges = session.getNodeSecurity().getNodePermission().permission(permission);

				if (privileges == null) {
					throw new Exception("Permission not found");
				}

				if (userId != null && !userId.isEmpty()) {
					session.getNodeSecurity().addEntry(session.getNode(curPath), new OlbiusUserPrincipal(userId, session.getTenantId()), privileges,
							allow);
				} else if (groupId != null && !groupId.isEmpty()) {
					session.getNodeSecurity().addEntry(session.getNode(curPath), new OlbiusGroupPrincipal(groupId, session.getTenantId()), privileges,
							allow);
				} else {
					throw new Exception("GroupID or UserID not found");
				}

			}
		});

	}

	public static Map<String, Object> jackrabbitRemovePermission(DispatchContext ctx, Map<String, ?> context) throws GenericServiceException {

		return JackrabbitServices.serviceBuilder(context, new JackrabbitServices.Callback() {
			@Override
			public void execute(Map<String, ?> context, ClientSession session, Map<String, Object> result) throws Exception {

				String curPath = (String) context.get("curPath");

				String userId = (String) context.get("userId");

				String groupId = (String) context.get("groupId");

				String permission = (String) context.get("permission");

				String[] privileges = session.getNodeSecurity().getNodePermission().permission(permission);

				if (privileges == null) {
					throw new Exception("Permission not found");
				}

				if (userId != null && !userId.isEmpty()) {
					session.getNodeSecurity().removeEntry(session.getNode(curPath), new OlbiusUserPrincipal(userId, session.getTenantId()),
							privileges);
				} else if (groupId != null && !groupId.isEmpty()) {
					session.getNodeSecurity().removeEntry(session.getNode(curPath), new OlbiusUserPrincipal(userId, session.getTenantId()),
							privileges);
				} else {
					throw new Exception("GroupID or UserID not found");
				}

			}
		});
	}

	public static Map<String, Object> jackrabbitGetPermission(DispatchContext ctx, Map<String, ?> context) throws GenericServiceException {

		return JackrabbitServices.serviceBuilder(context, new JackrabbitServices.Callback() {
			@Override
			public void execute(Map<String, ?> context, ClientSession session, Map<String, Object> result) throws Exception {

				String curPath = (String) context.get("curPath");

				String userId = (String) context.get("userId");

				String groupId = (String) context.get("groupId");

				String[] permission;

				if (userId != null && !userId.isEmpty()) {

					permission = session.getNodeSecurity().getPermissionName(session.getNode(curPath),
							new OlbiusUserPrincipal(userId, session.getTenantId()));

				} else if (groupId != null && !groupId.isEmpty()) {
					permission = session.getNodeSecurity().getPermissionName(session.getNode(curPath),
							new OlbiusGroupPrincipal(groupId, session.getTenantId()));
				} else {
					throw new Exception("GroupID or UserID not found");
				}

				List<String> tmp = Arrays.asList(permission);

				result.put("permission", tmp != null ? tmp : new ArrayList<String>());

			}
		});

	}
}
