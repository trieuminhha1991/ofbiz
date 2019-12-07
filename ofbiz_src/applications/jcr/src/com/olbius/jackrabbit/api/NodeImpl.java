package com.olbius.jackrabbit.api;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.jcr.AccessDeniedException;
import javax.jcr.PathNotFoundException;
import javax.jcr.Property;
import javax.jcr.PropertyIterator;
import javax.jcr.PropertyType;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.Workspace;
import javax.jcr.nodetype.NodeType;
import javax.jcr.version.Version;
import javax.jcr.version.VersionHistory;
import javax.jcr.version.VersionIterator;
import javax.jcr.version.VersionManager;

import org.apache.jackrabbit.core.SessionImpl;

import com.olbius.jackrabbit.core.Constant;
import com.olbius.jackrabbit.core.OlbiusNode;
import com.olbius.jackrabbit.core.OlbiusRepository;
import com.olbius.jackrabbit.core.OlbiusSession;

public class NodeImpl implements Node {

	private Session session;

	public NodeImpl(Session session) {
		this.session = session;
	}

	@Override
	public javax.jcr.Node createFolder(String parent, String name) throws RepositoryException {
		OlbiusNode olbiusNode = ((OlbiusSession) session).getOlbiusNode();
		olbiusNode.setNode(session.getNode(parent));
		return olbiusNode.addFolder(name);
	}

	@Override
	public Session getSession() {
		return session;
	}

	@Override
	public void remove(String path) throws RepositoryException {
		try {
			OlbiusNode node = ((OlbiusSession) session).getOlbiusNode();
			node.setNode(session.getNode(path));
			node.remove();
		} catch (PathNotFoundException e) {

		}
	}

	@Override
	public List<String> getChildNode(String parent) throws RepositoryException {
		OlbiusNode olbiusNode = ((OlbiusSession) session).getOlbiusNode();
		olbiusNode.setNode(session.getNode(parent));
		List<javax.jcr.Node> nodes = olbiusNode.getNodes();
		List<String> strings = new ArrayList<String>();
		for (javax.jcr.Node x : nodes) {
			strings.add(x.getName());
		}
		return strings;
	}

	@Override
	public void rename(String path, String newName) throws RepositoryException {
		OlbiusNode node = ((OlbiusSession) session).getOlbiusNode();
		node.setNode(session.getNode(path));
		String s = "";
		int i = 2;
		while (true) {
			try {
				session.getNode(node.getNode().getParent().getPath() + "/" + newName + s);
				s = "(" + Integer.toString(i) + ")";
				i++;
			} catch (PathNotFoundException e) {
				break;
			}
		}
		node.rename(newName + s);
	}

	@Override
	public Map<String, String> createFile(String path, String fileName, String mimeType, InputStream data, boolean version)
			throws RepositoryException, IOException {
		String[] tmp = null;

		OlbiusNode olbiusNode = ((OlbiusSession) session).getOlbiusNode();
		javax.jcr.Node node = null;
		try {
			node = session.getNode(path);
		} catch (PathNotFoundException e) {
			if (path.startsWith("/")) {
				tmp = path.substring(1).split("/");
			} else {
				tmp = path.split("/");
			}
			node = session.getRootNode();
			for (String x : tmp) {
				try {
					node = node.getNode(x);
				} catch (PathNotFoundException e1) {
					olbiusNode.setNode(node);
					node = olbiusNode.addFolder(x);
				}
			}
		}
		olbiusNode.setNode(node);
		javax.jcr.Node file = olbiusNode.addFile(fileName, mimeType, data, version);
		Map<String, String> map = new HashMap<String, String>();
		map.put(JCR_PATH, file.getPath());
		if (session.getWorkspace().getName().equals(OlbiusRepository.WSP_DEFAULT)) {
			map.put(WEBDAV_PATH, Constant.getUrl() + session.getWorkspace().getName() + file.getPath());
		}
		if (session.getWorkspace().getName().equals(OlbiusRepository.WSP_SECURITY)) {
			map.put(WEBDAV_PATH, WEBDAV_URL + session.getWorkspace().getName() + file.getPath());
		}
		return map;
	}

	@Override
	public void move(String oldPath, String newPerant) throws RepositoryException {
		javax.jcr.Node oldNode = session.getNode(oldPath);
		javax.jcr.Node newPerantNode = session.getNode(newPerant);

		String s = "";
		int i = 2;
		while (true) {
			try {
				session.getNode(newPerantNode.getPath() + "/" + oldNode.getName() + s);
				s = "(" + Integer.toString(i) + ")";
				i++;
			} catch (PathNotFoundException e) {
				break;
			}
		}
		session.move(oldNode.getPath(), newPerantNode.getPath() + "/" + oldNode.getName() + s);
		session.save();
	}

	@Override
	public void copy(String curPath, String path) throws RepositoryException {
		javax.jcr.Node curNode = session.getNode(curPath);
		javax.jcr.Node node = session.getNode(path);
		;
		String s = "";
		int i = 2;
		while (true) {
			try {
				session.getNode(node.getPath() + "/" + curNode.getName() + s);
				s = "(" + Integer.toString(i) + ")";
				i++;
			} catch (PathNotFoundException e) {
				break;
			}
		}
		session.getWorkspace().copy(curNode.getPath(), node.getPath() + "/" + curNode.getName() + s);
		session.save();
	}

	@Override
	public void updateFile(String path, InputStream data) throws RepositoryException, IOException {
		// OlbiusNode file = ((OlbiusSession)
		// session).getOlbiusTenantNode(path);
		javax.jcr.Node file = session.getNode(path);
		Workspace workspace = session.getWorkspace();
		VersionManager versionManager = workspace.getVersionManager();
		boolean flag = false;
		if (!versionManager.isCheckedOut(file.getPath())) {
			versionManager.checkout(file.getPath());
			session.save();
			flag = true;
		}

		javax.jcr.Node content = file.getNode(javax.jcr.Node.JCR_CONTENT);
		content.setProperty(Property.JCR_DATA, session.getValueFactory().createBinary(data));
		session.save();
		data.close();
		if (flag) {
			versionManager.checkin(file.getPath());
			session.save();
		}
	}

	@Override
	public List<String> getAllVersion(String path) throws RepositoryException {
		// OlbiusNode file = ((OlbiusSession)
		// session).getOlbiusTenantNode(path);
		javax.jcr.Node file = session.getNode(path);
		Workspace workspace = session.getWorkspace();
		VersionManager versionManager = workspace.getVersionManager();
		VersionHistory history = versionManager.getVersionHistory(file.getPath());
		VersionIterator versions = history.getAllVersions();
		List<String> list = new ArrayList<String>();
		while (versions.hasNext()) {
			Version version = versions.nextVersion();
			if (!version.getName().startsWith("jcr:")) {
				list.add(version.getName());
			}
		}
		return list;
	}

	@Override
	public String getVersion(String path, String label) throws RepositoryException {
		// OlbiusNode file = ((OlbiusSession)
		// session).getOlbiusTenantNode(path);
		javax.jcr.Node file = session.getNode(path);
		Workspace workspace = session.getWorkspace();
		VersionManager versionManager = workspace.getVersionManager();
		VersionHistory history = versionManager.getVersionHistory(file.getPath());
		Version version = history.getVersion(label);
		return version.getPath();
	}

	@Override
	public String publicNode(String path) throws RepositoryException {
		if (session.getWorkspace().getName().equals(OlbiusRepository.WSP_SECURITY)) {
			Session sessionPublic = ((SessionImpl) session).createSession(OlbiusRepository.WSP_DEFAULT);
			javax.jcr.Node node = null;
			node = session.getNode(path);

			try {
				session.checkPermission(node.getPath(), Session.ACTION_REMOVE);
			} catch (AccessDeniedException e) {
				throw new RepositoryException(e);
			}

			try {
				sessionPublic.getNode(node.getParent().getPath());
			} catch (PathNotFoundException e) {
				String[] tmp = node.getParent().getPath().substring(1).split("/");
				javax.jcr.Node tmpNode = sessionPublic.getRootNode();
				for (String x : tmp) {
					try {
						tmpNode = tmpNode.getNode(x);
					} catch (PathNotFoundException e1) {
						// try{
						// sessionPublic.checkPermission(tmpNode.getPath(),
						// Session.ACTION_ADD_NODE);
						// } catch (AccessDeniedException e2) {
						// throw new RepositoryException(e2);
						// }
						tmpNode = tmpNode.addNode(x, NodeType.NT_FOLDER);
					}
				}
				sessionPublic.save();
			}
			// try{
			// sessionPublic.checkPermission(parent.getPath(),
			// Session.ACTION_ADD_NODE);
			// } catch (AccessDeniedException e) {
			// throw new RepositoryException(e);
			// }
			String s = "";
			int i = 2;
			while (true) {
				try {
					sessionPublic.getNode(node.getPath() + s);
					s = "(" + i + ")";
					i++;
				} catch (PathNotFoundException e) {
					break;
				}
			}
			sessionPublic.getWorkspace().copy(OlbiusRepository.WSP_SECURITY, node.getPath(), node.getPath() + s);
			s = node.getPath() + s;
			node.remove();
			session.save();
			return s;
		} else {
			throw new RepositoryException("WorkSpace private: " + session.getWorkspace().getName());
		}
	}

	@Override
	public String privateNode(String path) throws RepositoryException {
		if (session.getWorkspace().getName().equals(OlbiusRepository.WSP_DEFAULT)) {
			Session sessionPrivate = ((SessionImpl) session).createSession(OlbiusRepository.WSP_SECURITY);
			javax.jcr.Node node = null;
			node = session.getNode(path);

			try {
				session.checkPermission(node.getPath(), Session.ACTION_REMOVE);
			} catch (AccessDeniedException e) {
				throw new RepositoryException(e);
			}

			try {
				sessionPrivate.getNode(node.getParent().getPath());
			} catch (PathNotFoundException e) {
				String[] tmp = node.getParent().getPath().substring(1).split("/");
				javax.jcr.Node tmpNode = sessionPrivate.getRootNode();
				for (String x : tmp) {
					try {
						tmpNode = tmpNode.getNode(x);
					} catch (PathNotFoundException e1) {
						// try{
						// sessionPrivate.checkPermission(tmpNode.getPath(),
						// Session.ACTION_ADD_NODE);
						// } catch (AccessDeniedException e2) {
						// throw new RepositoryException(e2);
						// }
						tmpNode = tmpNode.addNode(x, NodeType.NT_FOLDER);
					}
				}
				sessionPrivate.save();
			}
			// try{
			// sessionPrivate.checkPermission(parent.getPath(),
			// Session.ACTION_ADD_NODE);
			// } catch (AccessDeniedException e) {
			// throw new RepositoryException(e);
			// }
			String s = "";
			int i = 2;
			while (true) {
				try {
					sessionPrivate.getNode(node.getPath() + s);
					s = "(" + i + ")";
					i++;
				} catch (PathNotFoundException e) {
					break;
				}
			}
			sessionPrivate.getWorkspace().copy(OlbiusRepository.WSP_DEFAULT, node.getPath(), node.getPath() + s);
			s = node.getPath() + s;
			node.remove();
			session.save();
			return s;
		} else {
			throw new RepositoryException("WorkSpace public: " + session.getWorkspace().getName());
		}
	}

	@Override
	public String createFile(javax.jcr.Node node, String fileName, String mimeType, InputStream data, boolean version) throws RepositoryException,
			IOException {
		String s = "";
		int i = 2;
		while (true) {
			try {
				node.getNode(fileName + s);
				s = "(" + i + ")";
				i++;
			} catch (PathNotFoundException e) {
				break;
			}
		}
		OlbiusNode olbiusNode = ((OlbiusSession) session).getOlbiusNode();
		olbiusNode.setNode(node);
		return olbiusNode.addFile(fileName + s, mimeType, data, version).getPath();
	}

	@Override
	public String createFile(javax.jcr.Node node, String fileName, String mimeType, InputStream data, String nodeType,
			Map<String, String> properties, boolean version) throws RepositoryException, IOException, ParseException {
		String s = "";
		int i = 2;
		while (true) {
			try {
				node.getNode(fileName + s);
				s = "(" + i + ")";
				i++;
			} catch (PathNotFoundException e) {
				break;
			}
		}
		OlbiusNode olbiusNode = ((OlbiusSession) session).getOlbiusNode();
		olbiusNode.setNode(node);
		return olbiusNode.addFile(fileName + s, mimeType, data, nodeType, properties, version).getPath();
	}

	@Override
	public Map<String, Map<String, String>> getFileProperties(javax.jcr.Node node, String namespace) throws RepositoryException {

		if (!node.isNodeType(NodeType.NT_FILE)) {
			throw new RepositoryException(node.getName() + " is not file");
		}

		Map<String, Map<String, String>> map = new HashMap<String, Map<String, String>>();

		Map<String, String> fileProperties = new HashMap<String, String>();

		Map<String, String> contentProperties = new HashMap<String, String>();

		String fileName = node.getName();

		fileName = fileName.substring(0, fileName.lastIndexOf("."));

		if (node.getName().endsWith(")")) {
			fileName += node.getName().substring(node.getName().lastIndexOf("("));
		}

		String createdBy = node.getProperty(Property.JCR_CREATED_BY).getString();

		Calendar created = node.getProperty(Property.JCR_CREATED).getDate();

		javax.jcr.Node content = node.getNode(javax.jcr.Node.JCR_CONTENT);

		Calendar lastModified = content.getProperty(Property.JCR_LAST_MODIFIED).getDate();

		String lastModifiedBy = content.getProperty(Property.JCR_LAST_MODIFIED_BY).getString();

		String mimeType = content.getProperty(Property.JCR_MIMETYPE).getString();

		fileProperties.put("fileName", fileName);
		fileProperties.put("createdBy", createdBy);
		fileProperties.put("created", created.getTime().toString());
		fileProperties.put("lastModified", lastModified.getTime().toString());
		fileProperties.put("lastModifiedBy", lastModifiedBy);
		fileProperties.put("mimeType", mimeType);

		com.olbius.jackrabbit.api.NodeType nodeType = new NodeTypeImpl(session);

		if (namespace != null) {

			Map<String, String> tmp = nodeType.getProperties(content.getPrimaryNodeType().getName(), namespace);

			for (String x : tmp.keySet()) {
				try {
					Property property = content.getProperty(x);

					if (property.getType() != PropertyType.BINARY) {
						if (property.getType() == PropertyType.BOOLEAN) {
							contentProperties.put(property.getName(), Boolean.toString(property.getBoolean()));
						} else if (property.getType() == PropertyType.DATE) {
							Calendar calendar = property.getDate();
							contentProperties.put(property.getName(), calendar.getTime().toString());
						} else if (property.getType() == PropertyType.DOUBLE) {
							contentProperties.put(property.getName(), Double.toString(property.getDouble()));
						} else if (property.getType() == PropertyType.LONG) {
							contentProperties.put(property.getName(), Long.toString(property.getLong()));
						} else if (property.getType() == PropertyType.NAME) {
							contentProperties.put(property.getName(), property.getValue().getString());
						} else if (property.getType() == PropertyType.STRING) {
							contentProperties.put(property.getName(), property.getString());
						} else {
							contentProperties.put(x, "");
						}
					}
				} catch (PathNotFoundException e) {
					contentProperties.put(x, "");
				}
			}
		}

		map.put("fileProperties", fileProperties);
		map.put("contentProperties", contentProperties);

		// Map<String, String> map = new HashMap<String, String>();
		// PropertyIterator iterator = node.getProperties();
		// while (iterator.hasNext()) {
		// Property property = iterator.nextProperty();
		// if (property.getType() != PropertyType.BINARY) {
		// if (property.getType() == PropertyType.BOOLEAN) {
		// map.put(property.getName(), Boolean.toString(property.getBoolean()));
		// } else if (property.getType() == PropertyType.DATE) {
		// Calendar calendar = property.getDate();
		// map.put(property.getName(), calendar.getTime().toString());
		// } else if (property.getType() == PropertyType.DOUBLE) {
		// map.put(property.getName(), Double.toString(property.getDouble()));
		// } else if (property.getType() == PropertyType.LONG) {
		// map.put(property.getName(), Long.toString(property.getLong()));
		// } else if (property.getType() == PropertyType.NAME) {
		// map.put(property.getName(), property.getValue().getString());
		// } else if (property.getType() == PropertyType.STRING) {
		// map.put(property.getName(), property.getString());
		// }
		// }
		// }
		return map;
	}

	@Override
	public void updateFile(String path, InputStream data, Map<String, Object> properties) throws RepositoryException, IOException {
		javax.jcr.Node file = session.getNode(path);
		Workspace workspace = session.getWorkspace();
		VersionManager versionManager = workspace.getVersionManager();
		boolean flag = false;
		if (!versionManager.isCheckedOut(file.getPath())) {
			versionManager.checkout(file.getPath());
			session.save();
			flag = true;
		}

		javax.jcr.Node content = file.getNode(javax.jcr.Node.JCR_CONTENT);

		if (data != null) {
			content.setProperty(Property.JCR_DATA, session.getValueFactory().createBinary(data));
			session.save();
			data.close();
		}
		if (properties != null) {
			for (String x : properties.keySet()) {
				if (properties.get(x) instanceof String) {
					content.setProperty(x, (String) properties.get(x));
				} else if (properties.get(x) instanceof Boolean) {
					content.setProperty(x, (Boolean) properties.get(x));
				} else if (properties.get(x) instanceof BigDecimal) {
					content.setProperty(x, (BigDecimal) properties.get(x));
				} else if (properties.get(x) instanceof Double) {
					content.setProperty(x, (Double) properties.get(x));
				} else if (properties.get(x) instanceof Calendar) {
					content.setProperty(x, (Calendar) properties.get(x));
				} else if (properties.get(x) instanceof Long) {
					content.setProperty(x, (Long) properties.get(x));
				}
			}
			session.save();
		}

		Date date = new Date();

		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);

		content.setProperty(Property.JCR_LAST_MODIFIED_BY, session.getUserID());
		content.setProperty(Property.JCR_LAST_MODIFIED, calendar);

		session.save();

		if (flag) {
			versionManager.checkin(file.getPath());
			session.save();
		}
	}

	@Override
	public Map<String, List<String>> getChildItem(String parent) throws RepositoryException {
		OlbiusNode olbiusNode = ((OlbiusSession) session).getOlbiusNode();
		olbiusNode.setNode(session.getNode(parent));
		List<javax.jcr.Node> nodes = olbiusNode.getNodes();
		Map<String, List<String>> strings = new HashMap<String, List<String>>();
		List<String> folder = new ArrayList<String>();
		List<String> file = new ArrayList<String>();
		for (javax.jcr.Node x : nodes) {
			// strings.add(x.getName());
			if (x.isNodeType(NodeType.NT_FILE)) {
				// strings.put(x.getName(), "file");
				file.add(x.getName());
			} else if (x.isNodeType(NodeType.NT_FOLDER)) {
				// strings.put(x.getName(), "folder");
				folder.add(x.getName());
			}
		}
		Collections.sort(file);
		Collections.sort(folder);
		strings.put("folder", folder);
		strings.put("file", file);
		return strings;
	}
}
