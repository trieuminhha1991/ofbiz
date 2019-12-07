package com.olbius.jackrabbit.client.services;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.List;
import java.util.Map;

import javax.jcr.Node;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.GenericServiceException;
import com.olbius.jackrabbit.client.api.ClientSession;
import com.olbius.jackrabbit.client.services.product.image.JackrabbitScaleImage;

public class JackrabbitOlbiusDataServices {

	public final static String module = JackrabbitOlbiusDataServices.class.getName();

	public static Map<String, Object> jackrabbitCopyNode(DispatchContext ctx, Map<String, ?> context) throws GenericServiceException {

		return JackrabbitServices.serviceBuilder(context, new JackrabbitServices.Callback() {
			@Override
			public void execute(Map<String, ?> context, ClientSession session, Map<String, Object> result) throws Exception {
				String curPath = (String) context.get("curPath");
				String parentPath = (String) context.get("parentPath");
				Node node = session.getClientNode().copy(session.getNode(curPath), session.getNode(parentPath));
				result.put("path", session.getFullPath(node));
			}
		});

	}

	public static Map<String, Object> jackrabbitCreateFolder(DispatchContext ctx, Map<String, ?> context) throws GenericServiceException {

		return JackrabbitServices.serviceBuilder(context, new JackrabbitServices.Callback() {
			@Override
			public void execute(Map<String, ?> context, ClientSession session, Map<String, Object> result) throws Exception {
				String curPath = (String) context.get("curPath");
				String folderName = (String) context.get("folderName");
				Node node = session.getClientNode().createFolder(curPath + "/" + folderName);
				result.put("path", session.getFullPath(node));
			}
		});

	}

	public static Map<String, Object> jackrabbitDeleteNode(DispatchContext ctx, Map<String, ?> context) throws GenericServiceException {
		return JackrabbitServices.serviceBuilder(context, new JackrabbitServices.Callback() {
			@Override
			public void execute(Map<String, ?> context, ClientSession session, Map<String, Object> result) throws Exception {
				String curPath = (String) context.get("curPath");
				session.getClientNode().delete(session.getNode(curPath));
			}
		});
	}

	public static Map<String, Object> jackrabbitGetChildNode(DispatchContext ctx, Map<String, ?> context) throws GenericServiceException {
		throw new GenericServiceException("No supported");
	}

	public static Map<String, Object> jackrabbitMoveNode(DispatchContext ctx, Map<String, ?> context) throws GenericServiceException {
		return JackrabbitServices.serviceBuilder(context, new JackrabbitServices.Callback() {
			@Override
			public void execute(Map<String, ?> context, ClientSession session, Map<String, Object> result) throws Exception {
				String curPath = (String) context.get("curPath");
				String newPath = (String) context.get("newPath");
				Node node = session.getClientNode().move(session.getNode(curPath), newPath);
				result.put("path", session.getFullPath(node));
			}
		});
	}

	public static Map<String, Object> jackrabbitRenameNode(DispatchContext ctx, Map<String, ?> context) throws GenericServiceException {
		return JackrabbitServices.serviceBuilder(context, new JackrabbitServices.Callback() {
			@Override
			public void execute(Map<String, ?> context, ClientSession session, Map<String, Object> result) throws Exception {
				String curPath = (String) context.get("curPath");
				String newName = (String) context.get("newName");
				Node node = session.getClientNode().rename(session.getNode(curPath), newName);
				result.put("path", session.getFullPath(node));
			}
		});
	}

	public static Map<String, Object> jackrabbitUploadFile(DispatchContext ctx, Map<String, ?> context) throws GenericServiceException {
		return JackrabbitServices.serviceBuilder(context, new JackrabbitServices.Callback() {
			@Override
			public void execute(Map<String, ?> context, ClientSession session, Map<String, Object> result) throws Exception {
				ByteBuffer fileBytes = (ByteBuffer) context.get("uploadedFile");
				String fileName = (String) context.get("_uploadedFile_fileName");
				String mimeType = (String) context.get("_uploadedFile_contentType");
				String folder = (String) context.get("folder");
				InputStream stream = new ByteArrayInputStream(fileBytes.array());
				Node node = session.getClientNode().createFileRandomName(folder, fileName, mimeType, stream);
				result.put("path", session.getFullPath(node));
			}
		});
	}

	public static Map<String, Object> jackrabbitUploadFileProperties(DispatchContext ctx, Map<String, ?> context) throws GenericServiceException {
		throw new GenericServiceException("No supported");
	}

	public static Map<String, Object> jackrabbitScaleImageService(DispatchContext ctx, Map<String, ?> context) throws GenericServiceException {
		return JackrabbitServices.serviceBuilder(context, new JackrabbitServices.Callback() {
			@Override
			public void execute(Map<String, ?> context, ClientSession session, Map<String, Object> result) throws Exception {
				Map<String, Object> map = null;
				String curPath = (String) context.get("curPath");
				map = JackrabbitScaleImage.scaleImageInAllSize(context, curPath, session);
				result.put("imageUrl", map.get("imageUrlMap"));
			}
		});
	}

	public static Map<String, Object> jackrabbitGetNodeProperties(DispatchContext ctx, Map<String, ?> context) throws GenericServiceException {
		throw new GenericServiceException("No supported");
	}

	public static Map<String, Object> jackrabbitGetChildItem(DispatchContext ctx, Map<String, ?> context) throws GenericServiceException {
		return JackrabbitServices.serviceBuilder(context, new JackrabbitServices.Callback() {
			@Override
			public void execute(Map<String, ?> context, ClientSession session, Map<String, Object> result) throws Exception {
				String curPath = (String) context.get("curPath");
				Map<String, List<String>> nodes = session.getClientNode().getChildItems(session.getNode(curPath));
				result.put("childNodes", nodes);
			}
		});
	}
}
