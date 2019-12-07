package com.olbius.jackrabbit.client.services.product.image;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Locale;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.jcr.RepositoryException;
import javolution.util.FastMap;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.common.image.ImageTransform;

import com.olbius.jackrabbit.client.api.ClientSession;

public class JackrabbitImageTransform {

	public static final String module = JackrabbitImageTransform.class.getName();

	public static Map<String, Object> getBufferedImage(String fileLocation, Locale locale, ClientSession session) throws Exception {

		/* VARIABLES */
		BufferedImage bufImg;
		Map<String, Object> result = FastMap.newInstance();

		/* BUFFERED IMAGE */
		try {
			bufImg = ImageIO
					.read(session.getJcrSession("server").getNode(fileLocation).getNode("jcr:content").getProperty("jcr:data").getBinary().getStream());
		} catch (IllegalArgumentException e) {
			String errMsg = UtilProperties.getMessage(ImageTransform.resource, "ImageTransform.input_is_null", locale) + " : " + fileLocation + " ; "
					+ e.toString();
			Debug.logError(errMsg, module);
			result.put("errorMessage", errMsg);
			return result;
		} catch (IOException e) {
			String errMsg = UtilProperties.getMessage(ImageTransform.resource, "ImageTransform.error_occurs_during_reading", locale) + " : "
					+ fileLocation + " ; " + e.toString();
			Debug.logError(errMsg, module);
			result.put("errorMessage", errMsg);
			return result;
		} catch (RepositoryException e) {
			String errMsg = e.toString();
			Debug.logError(errMsg, module);
			result.put("errorMessage", errMsg);
			return result;
		}

		result.put("responseMessage", "success");
		result.put("bufferedImage", bufImg);
		try {
			result.put("mimeType", session.getJcrSession("server").getNode(fileLocation).getNode("jcr:content").getProperty("jcr:mimeType").getString());
		} catch (RepositoryException e) {
		}
		return result;

	}

}
