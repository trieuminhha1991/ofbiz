package com.olbius.jackrabbit.core;

import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.jcr.AccessDeniedException;
import javax.jcr.Binary;
import javax.jcr.ItemExistsException;
import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.PathNotFoundException;
import javax.jcr.Property;
import javax.jcr.PropertyType;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.Workspace;
import javax.jcr.lock.LockException;
import javax.jcr.nodetype.ConstraintViolationException;
import javax.jcr.nodetype.NoSuchNodeTypeException;
import javax.jcr.nodetype.NodeType;
import javax.jcr.security.Privilege;
import javax.jcr.version.VersionException;
import javax.jcr.version.VersionManager;

import org.apache.jackrabbit.api.JackrabbitSession;
import org.apache.jackrabbit.api.security.user.UserManager;
import org.apache.jackrabbit.core.NodeImpl;

import com.olbius.jackrabbit.api.NodeTypeImpl;
import com.olbius.jackrabbit.loader.OlbiusContainer;
import com.olbius.jackrabbit.security.OlbiusAccessControlManager;
import com.olbius.jackrabbit.security.OlbiusAccessControlManagerImpl;

public class OlbiusNodeImpl implements OlbiusNode {

	private Node node;

	protected OlbiusNodeImpl(Node node) throws RepositoryException {
		this.node = node;
	}

	@Override
	public void rename(String newName) throws RepositoryException {
		// ((NodeImpl) node).rename(newName);
		node.getSession().move(node.getPath(), node.getParent().getPath() + "/" + newName);
		node.getSession().save();
	}

	@Override
	public void setMixins(String[] mixinNames) throws NoSuchNodeTypeException, VersionException, ConstraintViolationException, LockException,
			RepositoryException {
		((NodeImpl) node).setMixins(mixinNames);
		node.getSession().save();
	}

	@Override
	public Node getNode() {
		return node;
	}

	@Override
	public Session getSession() throws RepositoryException {
		return node.getSession();
	}

	@Override
	public Node addFolder(String name) throws ItemExistsException, PathNotFoundException, NoSuchNodeTypeException, LockException, VersionException,
			ConstraintViolationException, RepositoryException {
		Node tmp = addNode(name, NodeType.NT_FOLDER);
		node.getSession().save();
		return tmp;
	}

	private Node addNode(String name, String type) throws ItemExistsException, PathNotFoundException, NoSuchNodeTypeException, LockException,
			VersionException, ConstraintViolationException, RepositoryException {
		int i = 2;
		String s = "";
		while (true) {
			try {
				node.getNode(name + s);
				s = "(" + Integer.toString(i) + ")";
				i++;
			} catch (PathNotFoundException e) {
				break;
			}
		}
		return node.addNode(name + s, type);
	}

	@Override
	public Node addFile(String name, String mimeType, InputStream data, boolean version) throws ItemExistsException, PathNotFoundException,
			NoSuchNodeTypeException, LockException, VersionException, ConstraintViolationException, RepositoryException, IOException {
		Node file = addNode(name, NodeType.NT_FILE);
		Node resNode = file.addNode(Node.JCR_CONTENT, NodeType.NT_RESOURCE);
		resNode.setProperty(Property.JCR_MIMETYPE, mimeType);
		Binary b = node.getSession().getValueFactory().createBinary(data);
		resNode.setProperty(Property.JCR_DATA, b);
		node.getSession().save();

		file.addMixin(NodeType.MIX_VERSIONABLE);
		node.getSession().save();

		data.close();

		OlbiusAccessControlManager acm = new OlbiusAccessControlManagerImpl(node.getSession());

		UserManager manager = ((JackrabbitSession) node.getSession()).getUserManager();

		Privilege[] privileges = acm.privilege(OlbiusContainer.getMapPermission().get("ADMIN"));

		acm.addEntry(file, manager.getAuthorizable(node.getSession().getUserID()).getPrincipal(), privileges, true);
		if (version) {
			Workspace workspace = node.getSession().getWorkspace();
			VersionManager versionManager = workspace.getVersionManager();
			versionManager.checkin(file.getPath());
		}
		node.getSession().save();
		return file;
	}

	@Override
	public boolean isFile() throws RepositoryException {
		return node.isNodeType(NodeType.NT_FILE);
	}

	@Override
	public void remove() throws AccessDeniedException, VersionException, LockException, ConstraintViolationException, RepositoryException {
		node.remove();
		node.getSession().save();
	}

	@Override
	public List<Node> getNodes() throws RepositoryException {
		return getNodes(false);
	}

	@Override
	public List<Node> getNodes(boolean isSystem) throws RepositoryException {
		NodeIterator iterator = node.getNodes();
		List<Node> list = new ArrayList<Node>();
		while (iterator.hasNext()) {
			Node n = iterator.nextNode();
			if (isSystem) {
				list.add(n);
			} else if (n.isNodeType(NodeType.NT_RESOURCE)) {
				list.add(n);
			} else if (n.getName().contains("jcr:") || n.getName().contains("rep:")) {

			} else {
				list.add(n);
			}
		}
		return list;
	}

	@Override
	public void setNode(Node node) {
		this.node = node;
	}

	@Override
	public Node addFile(String name, String mimeType, InputStream data, String nodeType, Map<String, String> properties, boolean version)
			throws ItemExistsException, PathNotFoundException, NoSuchNodeTypeException, LockException, VersionException,
			ConstraintViolationException, RepositoryException, IOException, ParseException {

		Node file = addNode(name, NodeType.NT_FILE);
		Node resNode = file.addNode(Node.JCR_CONTENT, nodeType);
		resNode.setProperty(Property.JCR_MIMETYPE, mimeType);
		Binary b = node.getSession().getValueFactory().createBinary(data);
		resNode.setProperty(Property.JCR_DATA, b);

		com.olbius.jackrabbit.api.NodeType type = new NodeTypeImpl(node.getSession());

		Map<String, String> map = type.getProperties(nodeType);

		for (String x : properties.keySet()) {
			if (map.get(x).equals(PropertyType.TYPENAME_BOOLEAN)) {
				resNode.setProperty(x, Boolean.parseBoolean(properties.get(x)));
			} else if (map.get(x).equals(PropertyType.TYPENAME_DATE)) {
				SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy hh:mm");
				Date date = format.parse(properties.get(x));
				Calendar calendar = Calendar.getInstance();
				calendar.setTime(date);
				resNode.setProperty(x, calendar);
			} else if (map.get(x).equals(PropertyType.TYPENAME_STRING)) {
				resNode.setProperty(x, properties.get(x));
			} else if (map.get(x).equals(PropertyType.TYPENAME_DOUBLE)) {
				resNode.setProperty(x, Double.parseDouble(properties.get(x)));
			} else if (map.get(x).equals(PropertyType.TYPENAME_LONG)) {
				resNode.setProperty(x, Long.parseLong(properties.get(x)));
			} else if (map.get(x).equals(PropertyType.TYPENAME_NAME)) {
				resNode.setProperty(x, properties.get(x));
			}
			// resNode.setProperty(x, properties.get(x));
		}

		node.getSession().save();

		file.addMixin(NodeType.MIX_VERSIONABLE);
		node.getSession().save();

		data.close();

		OlbiusAccessControlManager acm = new OlbiusAccessControlManagerImpl(node.getSession());

		UserManager manager = ((JackrabbitSession) node.getSession()).getUserManager();

		Privilege[] privileges = acm.privilege(OlbiusContainer.getMapPermission().get("ADMIN"));

		acm.addEntry(file, manager.getAuthorizable(node.getSession().getUserID()).getPrincipal(), privileges, true);

		if (version) {
			Workspace workspace = node.getSession().getWorkspace();
			VersionManager versionManager = workspace.getVersionManager();
			versionManager.checkin(file.getPath());
		}
		node.getSession().save();
		return file;
	}
}
