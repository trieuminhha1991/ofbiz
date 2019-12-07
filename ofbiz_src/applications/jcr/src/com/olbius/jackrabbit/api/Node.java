package com.olbius.jackrabbit.api;

import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.util.List;
import java.util.Map;

import javax.jcr.RepositoryException;
import javax.jcr.Session;

public interface Node {

	public final static String JCR_PATH = "jcr_path";
	public final static String WEBDAV_PATH = "webdav_path";
	public final static String WEBDAV_URL = "/webdav/repository/";

	public Session getSession();

	public javax.jcr.Node createFolder(String parent, String name) throws RepositoryException;

	public void remove(String path) throws RepositoryException;

	public List<String> getChildNode(String parent) throws RepositoryException;

	public Map<String, List<String>> getChildItem(String parent) throws RepositoryException;

	public void rename(String path, String newName) throws RepositoryException;

	public Map<String, String> createFile(String path, String fileName, String mimeType, InputStream data, boolean version) throws RepositoryException, IOException;

	public void move(String oldPath, String newPerant) throws RepositoryException;

	public void copy(String curPath, String path) throws RepositoryException;

	public void updateFile(String path, InputStream data) throws RepositoryException, IOException;

	public void updateFile(String path, InputStream data, Map<String, Object> properties) throws RepositoryException, IOException;

	public List<String> getAllVersion(String path) throws RepositoryException;

	public String getVersion(String path, String label) throws RepositoryException;

	public String publicNode(String path) throws RepositoryException;

	public String privateNode(String path) throws RepositoryException;

	public String createFile(javax.jcr.Node node, String fileName, String mimeType, InputStream data, boolean version) throws RepositoryException, IOException;

	public String createFile(javax.jcr.Node node, String fileName, String mimeType, InputStream data, String nodeType, Map<String, String> properties, boolean version)
			throws RepositoryException, IOException, ParseException;

	public Map<String, Map<String, String>> getFileProperties(javax.jcr.Node node, String namespace) throws RepositoryException;
}
