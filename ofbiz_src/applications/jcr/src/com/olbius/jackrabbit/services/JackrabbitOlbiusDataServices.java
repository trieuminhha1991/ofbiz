package com.olbius.jackrabbit.services;

import java.awt.image.ImagingOpException;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.text.ParseException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.jcr.RepositoryException;
import javax.jcr.Session;

import javolution.util.FastMap;

import org.jdom.JDOMException;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.ModelService;

import com.olbius.jackrabbit.api.Namespace;
import com.olbius.jackrabbit.api.NamespaceImpl;
import com.olbius.jackrabbit.api.Node;
import com.olbius.jackrabbit.api.NodeImpl;
import com.olbius.jackrabbit.api.NodeType;
import com.olbius.jackrabbit.api.NodeTypeImpl;
import com.olbius.jackrabbit.api.image.JackrabbitScaleImage;
import com.olbius.jackrabbit.core.Constant;
import com.olbius.jackrabbit.core.OlbiusSession;
import com.olbius.jackrabbit.loader.JackrabbitOlbiusContainer;
import com.olbius.jackrabbit.security.OlbiusUserManager;

public class JackrabbitOlbiusDataServices {
	public final static String module = JackrabbitOlbiusDataServices.class.getName();

	public static Map<String, Object> jackrabbitCopyNode(DispatchContext ctx, Map<String, ?> context) throws GenericServiceException {
		Map<String, Object> result = FastMap.newInstance();
		String path = (String) context.get("nodePath");
		String parentPath = (String) context.get("parentNodePath");

		Session jcrSession = (Session) context.get(JackrabbitOlbiusSessionServices.JCR_SESSION);

		if (jcrSession != null) {
			try {
				Node node = new NodeImpl(jcrSession);
				node.copy(path, parentPath);
			} catch (RepositoryException e) {
				throw new GenericServiceException(e.getMessage());
			}
		} else {
			throw new GenericServiceException("JCR_SESSION not found");
		}

		result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_SUCCESS);
		return result;
	}

	public static Map<String, Object> jackrabbitCreateFolder(DispatchContext ctx, Map<String, ?> context) throws GenericServiceException {
		Map<String, Object> result = FastMap.newInstance();
		String parentNode = (String) context.get("parentNodePath");
		String nodeName = (String) context.get("nodeName");

		Session jcrSession = (Session) context.get(JackrabbitOlbiusSessionServices.JCR_SESSION);

		if (jcrSession != null) {
			try {
				Node node = new NodeImpl(jcrSession);
				
				if (parentNode == null) {
					node.createFolder(((OlbiusSession) jcrSession).getOlbiusTenantRootNode().getPath(), nodeName);
				} else if (parentNode.startsWith("/")) {
					node.createFolder(((OlbiusSession) jcrSession).getOlbiusTenantRootNode().getPath() + parentNode, nodeName);
				} else {
					node.createFolder(((OlbiusSession) jcrSession).getOlbiusTenantRootNode().getPath() + "/" + parentNode, nodeName);
				}
//				node.createFolder(parentNode, nodeName);
			} catch (RepositoryException e) {
				throw new GenericServiceException(e.getMessage());
			}
		} else {
			throw new GenericServiceException("JCR_SESSION not found");
		}

		result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_SUCCESS);
		return result;
	}

	public static Map<String, Object> jackrabbitDeleteNode(DispatchContext ctx, Map<String, ?> context) throws GenericServiceException {
		Map<String, Object> result = FastMap.newInstance();
		String nodePath = (String) context.get("nodePath");

		Session jcrSession = (Session) context.get(JackrabbitOlbiusSessionServices.JCR_SESSION);

		if (jcrSession != null) {
			try {
				Node node = new NodeImpl(jcrSession);
				node.remove(nodePath);
			} catch (RepositoryException e) {
				throw new GenericServiceException(e.getMessage());
			}
		} else {
			throw new GenericServiceException("JCR_SESSION not found");
		}

		result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_SUCCESS);
		return result;
	}

	public static Map<String, Object> jackrabbitGetChildNode(DispatchContext ctx, Map<String, ?> context) throws GenericServiceException {
		Map<String, Object> result = FastMap.newInstance();
		String nodePath = (String) context.get("nodePath");

		Session jcrSession = (Session) context.get(JackrabbitOlbiusSessionServices.JCR_SESSION);

		if (jcrSession != null) {
			try {
				Node node = new NodeImpl(jcrSession);
				List<String> nodes = node.getChildNode(nodePath);
				result.put("childNodes", nodes);
				result.put("nodePath", nodePath);
			} catch (RepositoryException e) {
				throw new GenericServiceException(e);
			}
		} else {
			throw new GenericServiceException("JCR_SESSION not found");
		}

		result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_SUCCESS);
		return result;
	}

	public static Map<String, Object> jackrabbitMoveNode(DispatchContext ctx, Map<String, ?> context) throws GenericServiceException {
		Map<String, Object> result = FastMap.newInstance();
		String nodePath = (String) context.get("nodePath");
		String parentPath = (String) context.get("parentNodePath");
		Session jcrSession = (Session) context.get(JackrabbitOlbiusSessionServices.JCR_SESSION);

		if (jcrSession != null) {
			try {
				Node node = new NodeImpl(jcrSession);
				node.move(nodePath, parentPath);
			} catch (RepositoryException e) {
				throw new GenericServiceException(e.getMessage());
			}
		} else {
			throw new GenericServiceException("JCR_SESSION not found");
		}

		result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_SUCCESS);
		return result;
	}

	public static Map<String, Object> jackrabbitRenameNode(DispatchContext ctx, Map<String, ?> context) throws GenericServiceException {
		Map<String, Object> result = FastMap.newInstance();
		String nodePath = (String) context.get("nodePath");
		String newNodeName = (String) context.get("newNodeName");

		Session jcrSession = (Session) context.get(JackrabbitOlbiusSessionServices.JCR_SESSION);

		if (jcrSession != null) {
			try {
				Node node = new NodeImpl(jcrSession);
				node.rename(nodePath, newNodeName);
			} catch (RepositoryException e) {
				jcrSession.logout();
				throw new GenericServiceException(e.getMessage());
			}
		} else {
			throw new GenericServiceException("JCR_SESSION not found");
		}

		result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_SUCCESS);
		return result;
	}

	public static Map<String, Object> jackrabbitUploadFile(DispatchContext ctx, Map<String, ?> context) throws GenericServiceException {
		Map<String, Object> result = FastMap.newInstance();

		ByteBuffer fileBytes = (ByteBuffer) context.get("uploadedFile");
		String fileName = (String) context.get("_uploadedFile_fileName");
		String mimeType = (String) context.get("_uploadedFile_contentType");
//		String component = ctx.getName();
		String folder = (String) context.get("folder");
		String pathType = (String) context.get("pathType");

		if (pathType == null) {
			pathType = Node.WEBDAV_PATH;
		}

		InputStream stream = new ByteArrayInputStream(fileBytes.array());

		Session jcrSession = (Session) context.get(JackrabbitOlbiusSessionServices.JCR_SESSION);

		if (jcrSession != null) {
			try {
				Node node = new NodeImpl(jcrSession);
				Map<String, String> path;
				if (folder == null) {
					path = node.createFile(((OlbiusSession) jcrSession).getOlbiusTenantRootNode().getPath(), fileName, mimeType,
							stream, false);
				} else if (folder.startsWith("/")) {
					path = node.createFile(((OlbiusSession) jcrSession).getOlbiusTenantRootNode().getPath() + folder, fileName,
							mimeType, stream, false);
				} else {
					path = node.createFile(((OlbiusSession) jcrSession).getOlbiusTenantRootNode().getPath() + "/" + folder,
							fileName, mimeType, stream, false);
				}
				result.put("path", path.get(pathType));
				result.put("name", fileName);
				result.put("mimeType", mimeType);
			} catch (RepositoryException e) {
				jcrSession.logout();
				throw new GenericServiceException(e.getMessage());
			} catch (IOException e) {
				jcrSession.logout();
				throw new GenericServiceException(e.getMessage());
			}
		} else {
			throw new GenericServiceException("JCR_SESSION not found");
		}
		result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_SUCCESS);
		return result;
	}

	public static Map<String, Object> jackrabbitUploadFileProperties(DispatchContext ctx, Map<String, ?> context) throws GenericServiceException {
		Map<String, Object> result = FastMap.newInstance();

		ByteBuffer fileBytes = (ByteBuffer) context.get("uploadedFile");
		String fileName = (String) context.get("_uploadedFile_fileName");
		String mimeType = (String) context.get("_uploadedFile_contentType");
		String component = ctx.getName();
		String folder = (String) context.get("folder");
		Map<?, ?> map = (Map<?, ?>) context.get("properties");

		String nodeType = (String) context.get("nodeType");

		Map<String, String> properties = new HashMap<String, String>();

		for (Object x : map.keySet()) {
			properties.put((String) x, (String) map.get(x));
		}

		InputStream stream = new ByteArrayInputStream(fileBytes.array());

		Session jcrSession = (Session) context.get(JackrabbitOlbiusSessionServices.JCR_SESSION);

		if (jcrSession != null) {
			try {
				Node node = new NodeImpl(jcrSession);
				String path;
				if (folder == null || folder.equals("/")) {
					path = node.createFile(((OlbiusSession) jcrSession).getOlbiusTenantRootNode().getNode(component), fileName, mimeType, stream,
							nodeType, properties, false);
				} else if (folder.startsWith("/")) {
					path = node.createFile(((OlbiusSession) jcrSession).getOlbiusTenantRootNode().getNode(folder.substring(1)),
							fileName, mimeType, stream, nodeType, properties, false);
				} else {
					path = node.createFile(((OlbiusSession) jcrSession).getOlbiusTenantRootNode().getNode(folder), fileName,
							mimeType, stream, nodeType, properties, false);
				}
				result.put("path", path);
				result.put("name", fileName);
				result.put("mimeType", mimeType);
			} catch (RepositoryException e) {
				jcrSession.logout();
				throw new GenericServiceException(e.getMessage());
			} catch (IOException e) {
				jcrSession.logout();
				throw new GenericServiceException(e.getMessage());
			} catch (ParseException e) {
				jcrSession.logout();
				throw new GenericServiceException(e.getMessage());
			}
		} else {
			throw new GenericServiceException("JCR_SESSION not found");
		}
		result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_SUCCESS);
		return result;
	}

	public static Map<String, Object> jackrabbitUploadText(DispatchContext ctx, Map<String, ?> context) throws GenericServiceException {
		Map<String, Object> result = FastMap.newInstance();

		String textData = (String) context.get("textData");
		String fileName = (String) context.get("_uploadedFile_fileName");
		String mimeType = (String) context.get("_uploadedFile_contentType");
		String component = ctx.getName();
		String folder = (String) context.get("folder");
		String pathType = (String) context.get("pathType");
		if (pathType == null) {
			pathType = Node.WEBDAV_PATH;
		}

		InputStream stream;
		try {
			stream = new ByteArrayInputStream(textData.getBytes("UTF-8"));
		} catch (UnsupportedEncodingException e) {
			throw new GenericServiceException(e.getMessage());
		}

		Session jcrSession = (Session) context.get(JackrabbitOlbiusSessionServices.JCR_SESSION);
		if (jcrSession != null) {
			try {
				Node node = new NodeImpl(jcrSession);
				Map<String, String> path;
				if (folder == null) {
					path = node.createFile("/" + component, fileName, mimeType, stream, false);
				} else if (folder.startsWith("/")) {
					path = node.createFile("/" + component + folder, fileName, mimeType, stream, false);
				} else {
					path = node.createFile("/" + component + "/" + folder, fileName, mimeType, stream, false);
				}
				result.put("path", path.get(pathType));
				result.put("name", fileName);
				result.put("mimeType", mimeType);
			} catch (RepositoryException e) {
				jcrSession.logout();
				throw new GenericServiceException(e.getMessage());
			} catch (IOException e) {
				jcrSession.logout();
				throw new GenericServiceException(e.getMessage());
			}
		} else {
			throw new GenericServiceException("JCR_SESSION not found");
		}
		result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_SUCCESS);
		return result;
	}

	public static Map<String, Object> jackrabbitUpdateVersionFile(DispatchContext ctx, Map<String, ?> context) throws GenericServiceException {
		Map<String, Object> result = FastMap.newInstance();
		String nodePath = (String) context.get("nodePath");
		ByteBuffer fileBytes = (ByteBuffer) context.get("uploadedFile");
		InputStream stream = new ByteArrayInputStream(fileBytes.array());
		Session jcrSession = (Session) context.get(JackrabbitOlbiusSessionServices.JCR_SESSION);
		if (jcrSession != null) {
			try {
				Node node = new NodeImpl(jcrSession);
				node.updateFile(nodePath, stream);
			} catch (RepositoryException e) {
				jcrSession.logout();
				throw new GenericServiceException(e.getMessage());
			} catch (IOException e) {
				jcrSession.logout();
				throw new GenericServiceException(e.getMessage());
			}
		} else {
			throw new GenericServiceException("JCR_SESSION not found");
		}
		result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_SUCCESS);
		return result;
	}

	public static Map<String, Object> jackrabbitUpdateVersionFileProperties(DispatchContext ctx, Map<String, ?> context)
			throws GenericServiceException {
		Map<String, Object> result = FastMap.newInstance();

		String nodePath = (String) context.get("nodePath");

		ByteBuffer fileBytes = (ByteBuffer) context.get("uploadedFile");

		InputStream stream = null;

		if (fileBytes != null) {
			stream = new ByteArrayInputStream(fileBytes.array());
		}

		Map<?, ?> map = (Map<?, ?>) context.get("properties");

		Map<String, Object> properties = new HashMap<String, Object>();

		for (Object x : map.keySet()) {
			properties.put((String) x, map.get(x));
		}

		Session jcrSession = (Session) context.get(JackrabbitOlbiusSessionServices.JCR_SESSION);
		if (jcrSession != null) {
			try {
				Node node = new NodeImpl(jcrSession);
				node.updateFile(nodePath, stream, properties);
			} catch (RepositoryException e) {
				jcrSession.logout();
				throw new GenericServiceException(e.getMessage());
			} catch (IOException e) {
				jcrSession.logout();
				throw new GenericServiceException(e.getMessage());
			}
		} else {
			throw new GenericServiceException("JCR_SESSION not found");
		}
		result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_SUCCESS);
		return result;
	}

	public static Map<String, Object> jackrabbitGetAllVersion(DispatchContext ctx, Map<String, ?> context) throws GenericServiceException {
		Map<String, Object> result = FastMap.newInstance();
		Session jcrSession = (Session) context.get(JackrabbitOlbiusSessionServices.JCR_SESSION);
		String nodePath = (String) context.get("nodePath");
		if (jcrSession != null) {
			Node node = new NodeImpl(jcrSession);
			List<String> versions = null;
			try {
				versions = node.getAllVersion(nodePath);
			} catch (RepositoryException e) {
				jcrSession.logout();
				throw new GenericServiceException(e);
			}

			result.put("versions", versions);
		} else {
			throw new GenericServiceException("JCR_SESSION not found");
		}
		result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_SUCCESS);
		return result;
	}

	public static Map<String, Object> jackrabbitGetVersion(DispatchContext ctx, Map<String, ?> context) throws GenericServiceException {
		Map<String, Object> result = FastMap.newInstance();
		Session jcrSession = (Session) context.get(JackrabbitOlbiusSessionServices.JCR_SESSION);
		String nodePath = (String) context.get("nodePath");
		String label = (String) context.get("label");
		if (jcrSession != null) {
			Node node = new NodeImpl(jcrSession);
			String path = "";
			try {
				path = node.getVersion(nodePath, label);
			} catch (RepositoryException e) {
				jcrSession.logout();
				throw new GenericServiceException(e);
			}
			result.put("nodePath", path);
		} else {
			throw new GenericServiceException("JCR_SESSION not found");
		}
		result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_SUCCESS);
		return result;
	}

	public static Map<String, Object> jackrabbitPublicNode(DispatchContext ctx, Map<String, ?> context) throws GenericServiceException {
		Map<String, Object> result = FastMap.newInstance();
		Session jcrSession = (Session) context.get(JackrabbitOlbiusSessionServices.JCR_SESSION);
		String nodePath = (String) context.get("nodePath");
		if (jcrSession != null) {
			// result.put("nodePath", "");
			Node node = new NodeImpl(jcrSession);
			try {
				node.publicNode(nodePath);
			} catch (RepositoryException e) {
				jcrSession.logout();
				throw new GenericServiceException(e);
			}
		} else {
			throw new GenericServiceException("JCR_SESSION not found");
		}
		result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_SUCCESS);
		return result;
	}

	public static Map<String, Object> jackrabbitPrivateNode(DispatchContext ctx, Map<String, ?> context) throws GenericServiceException {
		Map<String, Object> result = FastMap.newInstance();
		Session jcrSession = (Session) context.get(JackrabbitOlbiusSessionServices.JCR_SESSION);
		String nodePath = (String) context.get("nodePath");
		if (jcrSession != null) {
			// result.put("nodePath", "");
			Node node = new NodeImpl(jcrSession);
			try {
				node.privateNode(nodePath);
			} catch (RepositoryException e) {
				jcrSession.logout();
				throw new GenericServiceException(e);
			}
		} else {
			throw new GenericServiceException("JCR_SESSION not found");
		}
		result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_SUCCESS);
		return result;
	}

	public static Map<String, Object> jackrabbitScaleImageService(DispatchContext ctx, Map<String, ?> context) throws GenericServiceException {
		Map<String, Object> result = FastMap.newInstance();
		Map<String, Object> map = null;

		Session jcrSession = (Session) context.get(JackrabbitOlbiusSessionServices.JCR_SESSION);
		String nodePath = (String) context.get("nodePath");
		String pathType = (String) context.get("pathType");

		if (pathType == null) {
			pathType = Node.JCR_PATH;
		}
		if (jcrSession != null) {
			try {
				String path = null;
				if (pathType.equals(Node.JCR_PATH)) {
					javax.jcr.Node node = jcrSession.getNode(nodePath);
					path = node.getPath();
				} else {
					jcrSession.logout();
					throw new GenericServiceException("Error: Path format");
				}
				map = JackrabbitScaleImage.scaleImageInAllSize(context, path, jcrSession);
				result.put("imageUrl", map.get("imageUrlMap"));
			} catch (RepositoryException e) {
				jcrSession.logout();
				throw new GenericServiceException(e);
			} catch (IllegalArgumentException e) {
				jcrSession.logout();
				throw new GenericServiceException(e);
			} catch (ImagingOpException e) {
				jcrSession.logout();
				throw new GenericServiceException(e);
			} catch (IOException e) {
				jcrSession.logout();
				throw new GenericServiceException(e);
			} catch (JDOMException e) {
				jcrSession.logout();
				throw new GenericServiceException(e);
			}
		} else {
			throw new GenericServiceException("JCR_SESSION not found");
		}

		result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_SUCCESS);
		return result;
	}

	public static Map<String, Object> jackrabbitGetNodeTypeProperties(DispatchContext ctx, Map<String, ?> context) throws GenericServiceException {
		Map<String, Object> result = FastMap.newInstance();

		Session jcrSession = (Session) context.get(JackrabbitOlbiusSessionServices.JCR_SESSION);

		String name = (String) context.get("nodetype");

		String namespace = (String) context.get("namespace");

		if (jcrSession != null) {
			NodeType nodeType = new NodeTypeImpl(jcrSession);
			try {
				if (namespace == null || namespace.isEmpty()) {
					result.put("properties", nodeType.getProperties(name));
				} else {
					result.put("properties", nodeType.getProperties(name, namespace));
				}

			} catch (RepositoryException e) {
				jcrSession.logout();
				throw new GenericServiceException(e);
			}
		} else {
			throw new GenericServiceException("JCR_SESSION not found");
		}

		result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_SUCCESS);
		return result;
	}

	public static Map<String, Object> jackrabbitGetNodeType(DispatchContext ctx, Map<String, ?> context) throws GenericServiceException {
		Map<String, Object> result = FastMap.newInstance();
		Session jcrSession = (Session) context.get(JackrabbitOlbiusSessionServices.JCR_SESSION);

		String namespace = (String) context.get("namespace");

		if (jcrSession != null) {
			NodeType nodeType = new NodeTypeImpl(jcrSession);
			try {
				if (namespace == null || namespace.isEmpty()) {
					result.put("nodetypes", nodeType.getNodeTypes());
				} else {
					result.put("nodetypes", nodeType.getNodeTypes(namespace));
				}
			} catch (RepositoryException e) {
				jcrSession.logout();
				throw new GenericServiceException(e);
			}
		} else {
			throw new GenericServiceException("JCR_SESSION not found");
		}

		result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_SUCCESS);
		return result;
	}

	public static Map<String, Object> jackrabbitRegisterNodeType(DispatchContext ctx, Map<String, ?> context) throws GenericServiceException {
		Map<String, Object> result = FastMap.newInstance();

		Session jcrSession = (Session) context.get(JackrabbitOlbiusSessionServices.JCR_SESSION);

		String nodetype = (String) context.get("nodetype");

		List<?> heritates = (List<?>) context.get("heritates");

		Map<?, ?> properties = (Map<?, ?>) context.get("properties");

		String[] strings = new String[heritates.size()];

		if (heritates != null) {
			for (int i = 0; i < heritates.size(); i++) {
				strings[i] = (String) heritates.get(i);
			}
		}

		Map<String, String> map = new HashMap<String, String>();
		if (properties != null) {
			for (Object x : properties.keySet()) {
				map.put((String) x, (String) properties.get(x));
			}
		}

		String namespace = null;

		String[] tmp = nodetype.split(":");
		if (tmp.length == 2) {
			namespace = tmp[0];
		}

		if (jcrSession != null) {
			if (namespace != null) {
				Namespace nsp = null;
				try {
					nsp = new NamespaceImpl(JackrabbitOlbiusContainer.getSession());
					if (!nsp.isExist(namespace)) {
						if (namespace.startsWith(Namespace.OLBIUS_PREFIX)) {
							nsp.registry(namespace, Namespace.OLBIUS_URI + "/" + namespace.replaceFirst(Namespace.OLBIUS_PREFIX, ""));
						} else {
							nsp.registry(namespace, Namespace.OLBIUS_URI + "/" + namespace);
						}
					}
				} catch (RepositoryException e) {
					jcrSession.logout();
					throw new GenericServiceException(e);
				} finally {
					if (nsp != null) {
						nsp.logoutSession();
					}
				}
			}
			NodeType nodeType = null;
			try {
				nodeType = new NodeTypeImpl(JackrabbitOlbiusContainer.getSession());
				nodeType.registerNodeType(nodetype, strings, map);
			} catch (RepositoryException e) {
				jcrSession.logout();
				throw new GenericServiceException(e);
			} finally {
				if (nodeType != null) {
					nodeType.logoutSession();
				}
			}
		} else {
			throw new GenericServiceException("JCR_SESSION not found");
		}

		result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_SUCCESS);
		return result;
	}

	public static Map<String, Object> jackrabbitAddPropertyNodeType(DispatchContext ctx, Map<String, ?> context) throws GenericServiceException {
		Map<String, Object> result = FastMap.newInstance();
		Session jcrSession = (Session) context.get(JackrabbitOlbiusSessionServices.JCR_SESSION);
		String nodetype = (String) context.get("nodetype");
		String propertyName = (String) context.get("propertyName");
		String propertyType = (String) context.get("propertyType");

		if (jcrSession != null) {
			NodeType type = null;
			try {
				type = new NodeTypeImpl(JackrabbitOlbiusContainer.getSession());
				type.addPropertyNodeType(nodetype, propertyName, propertyType);
			} catch (RepositoryException e) {
				jcrSession.logout();
				throw new GenericServiceException(e);
			} finally {
				if (type != null) {
					type.logoutSession();
				}
			}
		} else {
			throw new GenericServiceException("JCR_SESSION not found");
		}
		result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_SUCCESS);
		return result;
	}

	public static Map<String, Object> jackrabbitRemovePropertyNodeType(DispatchContext ctx, Map<String, ?> context) throws GenericServiceException {
		Map<String, Object> result = FastMap.newInstance();
		Session jcrSession = (Session) context.get(JackrabbitOlbiusSessionServices.JCR_SESSION);
		String nodetype = (String) context.get("nodetype");
		String propertyName = (String) context.get("propertyName");

		if (jcrSession != null) {
			NodeType type = null;
			try {
				type = new NodeTypeImpl(JackrabbitOlbiusContainer.getSession());
				type.removePropertyNodeType(nodetype, propertyName);
			} catch (RepositoryException e) {
				jcrSession.logout();
				throw new GenericServiceException(e);
			} finally {
				if (type != null) {
					type.logoutSession();
				}
			}
		} else {
			throw new GenericServiceException("JCR_SESSION not found");
		}
		result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_SUCCESS);
		return result;
	}

	public static Map<String, Object> jackrabbitRegisterNameSpace(DispatchContext ctx, Map<String, ?> context) throws GenericServiceException {
		Map<String, Object> result = FastMap.newInstance();

		Session jcrSession = (Session) context.get(JackrabbitOlbiusSessionServices.JCR_SESSION);

		String name = (String) context.get("name");

		String uri = (String) context.get("uri");

		if (jcrSession != null) {
			Namespace namespace = null;
			try {
				namespace = new NamespaceImpl(JackrabbitOlbiusContainer.getSession());
				namespace.registry(name, uri);
			} catch (RepositoryException e) {
				jcrSession.logout();
				throw new GenericServiceException(e);
			} finally {
				if (namespace != null) {
					namespace.logoutSession();
				}
			}
		} else {
			throw new GenericServiceException("JCR_SESSION not found");
		}

		result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_SUCCESS);
		return result;
	}

	public static Map<String, Object> jackrabbitGetNodeProperties(DispatchContext ctx, Map<String, ?> context) throws GenericServiceException {
		Map<String, Object> result = FastMap.newInstance();

		Map<String, Map<String, String>> map = null;

		Session jcrSession = (Session) context.get(JackrabbitOlbiusSessionServices.JCR_SESSION);

		String path = (String) context.get("path");

		String namespace = (String) context.get("namespace");
		
		if (jcrSession != null) {
			Node node = new NodeImpl(jcrSession);
			try {
				map = node.getFileProperties(jcrSession.getNode(path), namespace);
			} catch (RepositoryException e) {
				jcrSession.logout();
				throw new GenericServiceException(e);
			}
			String user = map.get("fileProperties").get("createdBy");
			String tenant = ctx.getDelegator().getDelegatorTenantId();
			if(tenant == null) {
				tenant = Constant.getTenantDefault();
			}
			if(user.startsWith(tenant+OlbiusUserManager.USER)) {
				map.get("fileProperties").put("createdBy", user.replaceFirst(tenant+OlbiusUserManager.USER, ""));
			}
			user = map.get("fileProperties").get("lastModifiedBy");
			if(user.startsWith(tenant+OlbiusUserManager.USER)) {
				map.get("fileProperties").put("lastModifiedBy", user.replaceFirst(tenant+OlbiusUserManager.USER, ""));
			}
		} else {
			throw new GenericServiceException("JCR_SESSION not found");
		}

		result.put("properties", map);

		result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_SUCCESS);
		return result;
	}

	public static Map<String, Object> jackrabbitGetChildItem(DispatchContext ctx, Map<String, ?> context) throws GenericServiceException {
		Map<String, Object> result = FastMap.newInstance();
		String nodePath = (String) context.get("nodePath");

		Session jcrSession = (Session) context.get(JackrabbitOlbiusSessionServices.JCR_SESSION);

		if (jcrSession != null) {
			try {
				Node node = new NodeImpl(jcrSession);
				Map<String, List<String>> nodes = node.getChildItem(nodePath);
				result.put("childNodes", nodes);
				result.put("nodePath", nodePath);
			} catch (RepositoryException e) {
				jcrSession.logout();
				throw new GenericServiceException(e);
			}
		} else {
			throw new GenericServiceException("JCR_SESSION not found");
		}

		result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_SUCCESS);
		return result;
	}
}
