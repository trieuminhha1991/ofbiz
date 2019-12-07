package com.olbius.jackrabbit.client.core;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.jcr.AccessDeniedException;
import javax.jcr.Binary;
import javax.jcr.InvalidItemStateException;
import javax.jcr.ItemExistsException;
import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.PathNotFoundException;
import javax.jcr.Property;
import javax.jcr.ReferentialIntegrityException;
import javax.jcr.RepositoryException;
import javax.jcr.lock.LockException;
import javax.jcr.nodetype.ConstraintViolationException;
import javax.jcr.nodetype.NoSuchNodeTypeException;
import javax.jcr.nodetype.NodeType;
import javax.jcr.security.Privilege;
import javax.jcr.version.VersionException;

import com.olbius.jackrabbit.client.api.ClientNode;
import com.olbius.jackrabbit.client.api.ClientNodePermission;
import com.olbius.jackrabbit.client.api.ClientSession;
import com.olbius.jcr.security.principal.EveryonePrincipal;

public class OlbiusNode implements ClientNode {

	private ClientSession session;
	private ClientSession system;

	public OlbiusNode(ClientSession session) {
		this.session = session;
	}

	@Override
	public void setSession(ClientSession session) {
		this.session = session;
	}

	@Override
	public boolean delete(Node node) throws Exception {
		node.remove();
		session.save("server");
		return true;
	}

	@Override
	public Node createFolder(String path, boolean isCreate) throws Exception {

		Node node = null;

		if (path == null || path.isEmpty()) {
			throw new Exception("Path is null");
		}

		if (path.startsWith("/")) {
			path = path.substring(1);
			node = session.getNode("/");
		}

		if (!isCreate && path.indexOf("/") != -1) {
			return null;
		}

		if (path.indexOf("/") == -1) {
			node = session.getNode().addNode(path, NodeType.NT_FOLDER);
		} else {
			try {
				node = session.getNode(path);
			} catch (PathNotFoundException e) {
				String[] tmp = path.split("/");

				node = session.getNode();
				for (String x : tmp) {
					try {
						node = node.getNode(x);
					} catch (PathNotFoundException e1) {
						node = createNode(node, x, NodeType.NT_FOLDER);
					}
				}
			}
		}

		session.save("server");

		return node;
	}

	private Node createNode(Node node, String name, String type) throws ItemExistsException, PathNotFoundException, NoSuchNodeTypeException,
			LockException, VersionException, ConstraintViolationException, RepositoryException {

		name = getName(node, name);

		return node.addNode(name, type);
	}

	private String getName(Node node, String name) throws RepositoryException {

		String s = "";
		int i = 2;
		while (true) {
			try {
				node.getNode(name.concat(s));
				s = "(".concat(Integer.toString(i)).concat(")");
				i++;
			} catch (PathNotFoundException e) {
				break;
			}
		}

		return name.concat(s);
	}

	@Override
	public Node createFolder(String path) throws Exception {
		return createFolder(path, false);
	}

	@Override
	public Node rename(Node node, String name)
			throws AccessDeniedException, ItemExistsException, ReferentialIntegrityException, ConstraintViolationException, InvalidItemStateException,
			VersionException, LockException, NoSuchNodeTypeException, RepositoryException, Exception {

		String _path = node.getParent().getPath() + "/" + getName(node.getParent(), name);

		session.getJcrSession("server").move(node.getPath(), _path);
		session.save("server");

		return session.getJcrSession("server").getNode(_path);
	}

	@Override
	public List<Node> getChilds(Node node) throws Exception {

		NodeIterator iterator = node.getNodes();

		List<Node> list = new ArrayList<Node>();

		while (iterator.hasNext()) {
			Node n = iterator.nextNode();
			if (n.isNodeType(NodeType.NT_FILE) || n.isNodeType(NodeType.NT_FOLDER)) {
				list.add(n);
			}
		}

		return list;

	}

	@Override
	public Map<String, List<String>> getChildItems(Node node) throws Exception {

		List<Node> nodes = getChilds(node);

		Map<String, List<String>> strings = new HashMap<String, List<String>>();

		List<String> folder = new ArrayList<String>();

		List<String> file = new ArrayList<String>();

		for (javax.jcr.Node x : nodes) {
			if (x.isNodeType(NodeType.NT_FILE)) {
				file.add(x.getName());
			} else if (x.isNodeType(NodeType.NT_FOLDER)) {
				folder.add(x.getName());
			}
		}
		Collections.sort(file);
		Collections.sort(folder);
		strings.put("folder", folder);
		strings.put("file", file);
		return strings;
	}

	@Override
	public Node getParent() throws Exception {
		if (session.isRoot()) {
			return null;
		}
		return session.getNode().getParent();
	}

	@Override
	public Node createFile(String fileName, String mimeType, InputStream data) throws Exception {
		return createFile(session.getNode(), fileName, mimeType, data);
	}

	@Override
	public Node createFile(Node node, String fileName, String mimeType, InputStream data) throws Exception {

		boolean flag = false;

		try {

			if (!getSystem().getNodeSecurity().hasPrivilege(node, session.getPrincipal(), Privilege.JCR_MODIFY_PROPERTIES)) {
				getSystem().getNodeSecurity().addEntry(getSystem().getJcrSession("rmi").getNode(node.getPath()), session.getPrincipal(),
						new String[] { Privilege.JCR_MODIFY_PROPERTIES }, true);
				flag = true;
			}

		} finally {
			getSystem().logout();
		}

		Node file = null;

		try {
			file = createNode(node, getName(node, fileName), NodeType.NT_FILE);

			Node resNode = file.addNode(Node.JCR_CONTENT, NodeType.NT_RESOURCE);
			resNode.setProperty(Property.JCR_MIMETYPE, mimeType);

			Binary b = session.getJcrSession("server").getValueFactory().createBinary(data);
			resNode.setProperty(Property.JCR_DATA, b);

			session.save("server");

			data.close();
		} finally {
			if (flag) {
				try {
					getSystem().getNodeSecurity().removeEntry(getSystem().getJcrSession("rmi").getNode(node.getPath()), session.getPrincipal(),
							Privilege.JCR_MODIFY_PROPERTIES);
				} finally {
					getSystem().logout();
				}
			}

		}

		try {

			Node tmp = getSystem().getJcrSession("server").getNode(file.getPath());

			tmp.addMixin(NodeType.MIX_VERSIONABLE);

			getSystem().save("server");

			getSystem().getNodeSecurity().addEntry(tmp, EveryonePrincipal.getInstance(), new String[] { Privilege.JCR_ADD_CHILD_NODES }, flag);
			
			getSystem().getNodeSecurity().addEntry(node, session.getPrincipal(), new String[] { Privilege.JCR_REMOVE_CHILD_NODES }, true);
			
			getSystem().getNodeSecurity().addEntry(tmp, session.getPrincipal(),
					session.getNodeSecurity().getNodePermission().permission(ClientNodePermission.ADMIN), true);

		} finally {
			getSystem().logout();
		}

		return file;
	}

	@Override
	public Node createFile(String path, String fileName, String mimeType, InputStream data, boolean isCreate) throws Exception {

		Node folder = null;

		if (isCreate) {
			folder = createFolder(path, isCreate);
		} else {
			folder = session.getNode(path);
		}

		return createFile(folder, fileName, mimeType, data);
	}

	protected ClientSession getSystem() {
		if (system == null) {
			system = session.getSessionFactory().newSystemInstance(session.getWorkspace());
		}
		return system;
	}

	@Override
	public Node createFileRandomName(String path, String fileName, String mimeType, InputStream data) throws Exception {

		String _path = "";

		int index = fileName.lastIndexOf(".");
		int index2 = fileName.lastIndexOf("(");
		String imgExtension = null;
		if (index2 > index) {
			imgExtension = fileName.substring(index + 1, index2);
		} else {
			imgExtension = fileName.substring(index + 1);
		}

		String tmp = UUID.randomUUID().toString().replaceAll("-", "");

		for (int i = 0; i < tmp.length(); i = i + 2) {
			_path = _path + "/" + tmp.substring(i, i + 2);
		}

		if (!OlbiusSession.WORKSPACE_DEFAULT.equals(session.getWorkspace())) {
			_path = path + _path;
		}

		return createFile(_path, tmp + "." + imgExtension, mimeType, data, true);
	}

	@Override
	public Node move(Node node, String newPath) throws Exception {
		Node newPerantNode = createFolder(newPath, true);
		String _path = newPerantNode.getPath() + "/" + getName(newPerantNode, node.getName());
		session.getJcrSession("server").move(node.getPath(), _path);
		session.save("server");
		return session.getJcrSession("server").getNode(_path);
	}

	@Override
	public Node copy(Node node, Node parent) throws Exception {
		String _path = parent.getPath() + "/" + getName(parent, node.getName());
		session.getJcrSession("server").getWorkspace().copy(node.getPath(), _path);
		session.save("server");
		return session.getJcrSession("server").getNode(_path);
	}

}
