package com.olbius.jackrabbit.api;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.jcr.PropertyType;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.Workspace;
import javax.jcr.nodetype.NoSuchNodeTypeException;
import javax.jcr.nodetype.NodeTypeIterator;
import javax.jcr.nodetype.NodeTypeManager;
import javax.jcr.nodetype.NodeTypeTemplate;
import javax.jcr.nodetype.PropertyDefinition;
import javax.jcr.nodetype.PropertyDefinitionTemplate;

import org.apache.jackrabbit.core.nodetype.NodeTypeRegistry;

public class NodeTypeImpl implements NodeType {

	private Session session;

	public NodeTypeImpl(Session session) {
		this.session = session;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void registerNodeType(String name, String[] heritates, Map<String, String> properties) throws RepositoryException {
		Workspace workspace = session.getWorkspace();

		NodeTypeManager nodeTypeManager = workspace.getNodeTypeManager();

		NodeTypeTemplate ndt = nodeTypeManager.createNodeTypeTemplate();

		javax.jcr.nodetype.NodeType nodeType = null;

		try {
			nodeType = nodeTypeManager.getNodeType(name);
		} catch (NoSuchNodeTypeException e) {

		}

		// heritates = new String[]{};

		if (nodeType != null) {
			/*heritates = nodeType.getDeclaredSupertypeNames();
			if (heritates == null) {
				heritates = new String[] {};
			}
			for (PropertyDefinition x : nodeType.getPropertyDefinitions()) {
				if (properties.get(x.getName()) != null) {
					PropertyDefinitionTemplate createPropertyDefinitionTemplate = nodeTypeManager.createPropertyDefinitionTemplate();
					createPropertyDefinitionTemplate.setName(x.getName());
					createPropertyDefinitionTemplate.setRequiredType(PropertyType.valueFromName(properties.get(x.getName())));
					ndt.getPropertyDefinitionTemplates().add(createPropertyDefinitionTemplate);
				} else {
					ndt.getPropertyDefinitionTemplates().add(x);
				}
			}
			unRegisterNodeType(name);*/
			throw new RepositoryException(name + "is exist");
		} else {
			for (String x : properties.keySet()) {
				PropertyDefinitionTemplate createPropertyDefinitionTemplate = nodeTypeManager.createPropertyDefinitionTemplate();
				createPropertyDefinitionTemplate.setName(x);
				createPropertyDefinitionTemplate.setRequiredType(PropertyType.valueFromName(properties.get(x)));
				ndt.getPropertyDefinitionTemplates().add(createPropertyDefinitionTemplate);
			}
		}

		ndt.setName(name);
		if (heritates.length > 0) {
			ArrayList<String> tmp = new ArrayList<String>();
			for (String x : heritates) {
				nodeType = nodeTypeManager.getNodeType(x);
				if (x != null) {
					tmp.add(x);
				}
			}
			heritates = new String[tmp.size()];
			tmp.toArray(heritates);
		}
		ndt.setDeclaredSuperTypeNames(heritates);
		nodeTypeManager.registerNodeType(ndt, true);
		session.save();
	}

	@SuppressWarnings("unchecked")
	@Override
	public void addPropertyNodeType(String name, String propertyName, String propertyType) throws RepositoryException {
		Workspace workspace = session.getWorkspace();

		NodeTypeManager nodeTypeManager = workspace.getNodeTypeManager();

		NodeTypeTemplate ndt = nodeTypeManager.createNodeTypeTemplate();

		javax.jcr.nodetype.NodeType nodeType = null;

		try {
			nodeType = nodeTypeManager.getNodeType(name);
		} catch (NoSuchNodeTypeException e) {

		}

		String[] heritates = new String[] {};

		if (nodeType != null) {
			heritates = nodeType.getDeclaredSupertypeNames();
			if (heritates == null) {
				heritates = new String[] {};
			}
			Map<String, String> map = new HashMap<String, String>();
			if (heritates.length > 0) {
				for (String s : heritates) {
					javax.jcr.nodetype.NodeType tmp = nodeTypeManager.getNodeType(s);
					for (PropertyDefinition p : tmp.getPropertyDefinitions()) {
						map.put(p.getName(), s);
					}
				}

			}
			for (PropertyDefinition x : nodeType.getPropertyDefinitions()) {
				if (map.get(x.getName()) == null) {
					if (x.getName().equals(propertyName)) {
						PropertyDefinitionTemplate createPropertyDefinitionTemplate = nodeTypeManager.createPropertyDefinitionTemplate();
						createPropertyDefinitionTemplate.setName(x.getName());
						createPropertyDefinitionTemplate.setRequiredType(PropertyType.valueFromName(propertyType));
						ndt.getPropertyDefinitionTemplates().add(createPropertyDefinitionTemplate);
					} else {
						PropertyDefinitionTemplate createPropertyDefinitionTemplate = nodeTypeManager.createPropertyDefinitionTemplate();
						createPropertyDefinitionTemplate.setName(x.getName());
						createPropertyDefinitionTemplate.setRequiredType(x.getRequiredType());
						ndt.getPropertyDefinitionTemplates().add(createPropertyDefinitionTemplate);
					}
				}
			}
		} else {
			throw new RepositoryException("Node Type " + name + " not found");
		}

		PropertyDefinitionTemplate createPropertyDefinitionTemplate = nodeTypeManager.createPropertyDefinitionTemplate();
		createPropertyDefinitionTemplate.setName(propertyName);
		createPropertyDefinitionTemplate.setRequiredType(PropertyType.valueFromName(propertyType));
		ndt.getPropertyDefinitionTemplates().add(createPropertyDefinitionTemplate);
		ndt.setName(name);
		ndt.setDeclaredSuperTypeNames(heritates);
		
//		unRegisterNodeType(name);
		
		nodeTypeManager.registerNodeType(ndt, true);
		session.save();
	}

	@SuppressWarnings("unchecked")
	@Override
	public void removePropertyNodeType(String name, String propertyName) throws RepositoryException {
		Workspace workspace = session.getWorkspace();

		NodeTypeManager nodeTypeManager = workspace.getNodeTypeManager();

		NodeTypeTemplate ndt = nodeTypeManager.createNodeTypeTemplate();

		javax.jcr.nodetype.NodeType nodeType = null;

		try {
			nodeType = nodeTypeManager.getNodeType(name);
		} catch (NoSuchNodeTypeException e) {

		}

		String[] heritates = new String[] {};

		if (nodeType != null) {
			heritates = nodeType.getDeclaredSupertypeNames();
			if (heritates == null) {
				heritates = new String[] {};
			}
			Map<String, String> map = new HashMap<String, String>();
			if (heritates.length > 0) {
				for (String s : heritates) {
					javax.jcr.nodetype.NodeType tmp = nodeTypeManager.getNodeType(s);
					for (PropertyDefinition p : tmp.getPropertyDefinitions()) {
						map.put(p.getName(), s);
					}
				}

			}
			for (PropertyDefinition x : nodeType.getPropertyDefinitions()) {
				if (map.get(x.getName()) == null) {
					if (!x.getName().equals(propertyName)) {
						PropertyDefinitionTemplate createPropertyDefinitionTemplate = nodeTypeManager.createPropertyDefinitionTemplate();
						createPropertyDefinitionTemplate.setName(x.getName());
						createPropertyDefinitionTemplate.setRequiredType(x.getRequiredType());
						ndt.getPropertyDefinitionTemplates().add(createPropertyDefinitionTemplate);
					}
				}
			}
		} else {
			throw new RepositoryException("Node Type " + name + " not found");
		}
		ndt.setName(name);
		ndt.setDeclaredSuperTypeNames(heritates);
		
		unRegisterNodeType(name);
		
		nodeTypeManager.registerNodeType(ndt, true);
		session.save();
	}

	@Override
	public Map<String, String> getProperties(String name) throws RepositoryException {
		Map<String, String> map = new HashMap<String, String>();

		Workspace workspace = session.getWorkspace();

		NodeTypeManager nodeTypeManager = workspace.getNodeTypeManager();

		javax.jcr.nodetype.NodeType nodeType = null;

		try {
			nodeType = nodeTypeManager.getNodeType(name);
		} catch (NoSuchNodeTypeException e) {

		}

		if (nodeType != null) {
			for (PropertyDefinition x : nodeType.getPropertyDefinitions()) {
				map.put(x.getName(), PropertyType.nameFromValue(x.getRequiredType()));
			}
		} else {
			throw new RepositoryException("Node Type " + name + " not found");
		}
		return map;
	}

	@Override
	public List<String> getNodeTypes(String namespace) throws RepositoryException {

		List<String> strings = new ArrayList<String>();

		Workspace workspace = session.getWorkspace();

		NodeTypeManager nodeTypeManager = workspace.getNodeTypeManager();
		NodeTypeIterator iterator = nodeTypeManager.getAllNodeTypes();
		while (iterator.hasNext()) {
			javax.jcr.nodetype.NodeType nodeType = iterator.nextNodeType();
			if (nodeType.getName().startsWith(namespace + ":")) {
				strings.add(nodeType.getName());
			}
		}
		return strings;
	}

	@Override
	public List<String> getNodeTypes() throws RepositoryException {

		List<String> strings = new ArrayList<String>();

		Workspace workspace = session.getWorkspace();

		NodeTypeManager nodeTypeManager = workspace.getNodeTypeManager();
		NodeTypeIterator iterator = nodeTypeManager.getAllNodeTypes();
		while (iterator.hasNext()) {
			javax.jcr.nodetype.NodeType nodeType = iterator.nextNodeType();
			strings.add(nodeType.getName());
		}
		return strings;
	}

	@Override
	public Map<String, String> getProperties(String name, String namespace) throws RepositoryException {
		Map<String, String> map = new HashMap<String, String>();

		Workspace workspace = session.getWorkspace();

		NodeTypeManager nodeTypeManager = workspace.getNodeTypeManager();

		javax.jcr.nodetype.NodeType nodeType = null;

		try {
			nodeType = nodeTypeManager.getNodeType(name);
		} catch (NoSuchNodeTypeException e) {

		}

		if (nodeType != null) {
			for (PropertyDefinition x : nodeType.getPropertyDefinitions()) {
				if (x.getName().startsWith(namespace + ":")) {
					map.put(x.getName(), PropertyType.nameFromValue(x.getRequiredType()));
				}
			}
		} else {
			throw new RepositoryException("Node Type " + name + " not found");
		}
		return map;
	}

	@Override
	public void unRegisterNodeType(String name) throws RepositoryException {
		Workspace workspace = session.getWorkspace();
		NodeTypeManager nodeTypeManager = workspace.getNodeTypeManager();
		NodeTypeRegistry.disableCheckForReferencesInContentException = true;
		nodeTypeManager.unregisterNodeType(name);
		session.save();
	}

	@Override
	public void logoutSession() {
		if(session!=null) {
			session.logout();
		}
	}

}
