package com.olbius.jackrabbit.client.services.product.image;

import java.awt.image.BufferedImage;
import java.awt.image.ImagingOpException;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.jcr.Node;
import javolution.util.FastMap;

import org.jdom.JDOMException;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilGenerics;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.base.util.string.FlexibleStringExpander;
import org.ofbiz.common.image.ImageTransform;
import org.ofbiz.product.image.ScaleImage;
import org.ofbiz.service.ServiceUtil;

import com.olbius.jackrabbit.client.OlbiusProvider;
import com.olbius.jackrabbit.client.api.ClientSession;
import com.olbius.jackrabbit.client.services.product.image.JackrabbitImageTransform;

public class JackrabbitScaleImage {

	public static final String module = JackrabbitScaleImage.class.getName();

	public static Map<String, Object> scaleImageInAllSize(Map<String, ? extends Object> context, String path,
			ClientSession session) throws Exception {

		Node node = session.getJcrSession("server").getNode(path).getParent();

		Locale locale = (Locale) context.get("locale");

		int index;
		Map<String, Map<String, String>> imgPropertyMap = FastMap.newInstance();
		BufferedImage bufImg, bufNewImg;
		double imgHeight, imgWidth;
		Map<String, String> imgUrlMap = FastMap.newInstance();
		Map<String, Object> resultXMLMap = FastMap.newInstance();
		Map<String, Object> resultBufImgMap = FastMap.newInstance();
		Map<String, Object> resultScaleImgMap = FastMap.newInstance();
		Map<String, Object> result = FastMap.newInstance();

		/* ImageProperties.xml */
		String imgPropertyFullPath = System.getProperty("ofbiz.home")
				+ "/applications/product/config/ImageProperties.xml";
		resultXMLMap.putAll(ImageTransform.getXMLValue(imgPropertyFullPath, locale));
		if (resultXMLMap.containsKey("responseMessage") && resultXMLMap.get("responseMessage").equals("success")) {
			imgPropertyMap.putAll(UtilGenerics.<Map<String, Map<String, String>>> cast(resultXMLMap.get("xml")));
		} else {
			String errMsg = UtilProperties.getMessage(ScaleImage.resource, "ScaleImage.unable_to_parse", locale)
					+ " : ImageProperties.xml";
			Debug.logError(errMsg, module);
			result.put("errorMessage", errMsg);
			return result;
		}

		/* IMAGE */
		// get Name and Extension
		index = path.lastIndexOf(".");
		int index2 = path.lastIndexOf("(");
		String imgExtension = null;
		if (index2 > index) {
			imgExtension = path.substring(index + 1, index2);
		} else {
			imgExtension = path.substring(index + 1);
		}

		/* get original BUFFERED IMAGE */
		resultBufImgMap.putAll(JackrabbitImageTransform.getBufferedImage(path, locale, session));
		String imageUrl = "";

		if (resultBufImgMap.containsKey("responseMessage")
				&& resultBufImgMap.get("responseMessage").equals("success")) {
			bufImg = (BufferedImage) resultBufImgMap.get("bufferedImage");

			// get Dimensions
			imgHeight = bufImg.getHeight();
			imgWidth = bufImg.getWidth();
			if (imgHeight == 0.0 || imgWidth == 0.0) {
				String errMsg = UtilProperties.getMessage(ScaleImage.resource,
						"ScaleImage.one_current_image_dimension_is_null", locale) + " : imgHeight = " + imgHeight
						+ " ; imgWidth = " + imgWidth;
				Debug.logError(errMsg, module);
				result.put("errorMessage", errMsg);
				return result;
			}

			/* Scale image for each size from ImageProperties.xml */

			for (Map.Entry<String, Map<String, String>> entry : imgPropertyMap.entrySet()) {
				String sizeType = entry.getKey();

				// Scale
				resultScaleImgMap.putAll(
						ImageTransform.scaleImage(bufImg, imgHeight, imgWidth, imgPropertyMap, sizeType, locale));

				/* Write the new image file */
				if (resultScaleImgMap.containsKey("responseMessage")
						&& resultScaleImgMap.get("responseMessage").equals("success")) {
					bufNewImg = (BufferedImage) resultScaleImgMap.get("bufferedImage");

					// write new image
					try {
						ByteArrayOutputStream os = new ByteArrayOutputStream();
						ImageIO.write(bufNewImg, imgExtension, os);
						InputStream is = new ByteArrayInputStream(os.toByteArray());
						imageUrl = session.getClientNode().createFile(node, sizeType + "." + imgExtension, (String) resultBufImgMap.get("mimeType"), is).getPath();
					} catch (IllegalArgumentException e) {
						String errMsg = UtilProperties.getMessage(ScaleImage.resource,
								"ScaleImage.one_parameter_is_null", locale) + e.toString();
						Debug.logError(errMsg, module);
						result.put("errorMessage", errMsg);
						return result;
					} catch (IOException e) {
						String errMsg = UtilProperties.getMessage(ScaleImage.resource,
								"ScaleImage.error_occurs_during_writing", locale) + e.toString();
						Debug.logError(errMsg, module);
						result.put("errorMessage", errMsg);
						return result;
					}

					if (ScaleImage.sizeTypeList.contains(sizeType)) {
						imgUrlMap.put(sizeType,
								OlbiusProvider.WEB_DAV_URI.concat(session.getWorkspace()).concat(imageUrl));
					}
				}
			}

			result.put("responseMessage", "success");
			result.put("imageUrlMap", imgUrlMap);
			result.put("original", resultBufImgMap);
			return result;

		} else {
			String errMsg = UtilProperties.getMessage(ScaleImage.resource, "ScaleImage.unable_to_scale_original_image",
					locale) + " : " + node.getPath();
			Debug.logError(errMsg, module);
			result.put("errorMessage", errMsg);
			return ServiceUtil.returnError(errMsg);
		}
	}

	public static Map<String, Object> scaleImageManageInAllSize(Map<String, ? extends Object> context,
			String filenameToUse, String viewType, String viewNumber, String imageType)
					throws IllegalArgumentException, ImagingOpException, IOException, JDOMException {

		/* VARIABLES */
		Locale locale = (Locale) context.get("locale");
		List<String> sizeTypeList = null;
		if (UtilValidate.isNotEmpty(imageType)) {
			sizeTypeList = UtilMisc.toList(imageType);
		} else {
			sizeTypeList = UtilMisc.toList("small", "medium", "large", "detail");
		}

		int index;
		Map<String, Map<String, String>> imgPropertyMap = FastMap.newInstance();
		BufferedImage bufImg, bufNewImg;
		double imgHeight, imgWidth;
		Map<String, String> imgUrlMap = FastMap.newInstance();
		Map<String, Object> resultXMLMap = FastMap.newInstance();
		Map<String, Object> resultBufImgMap = FastMap.newInstance();
		Map<String, Object> resultScaleImgMap = FastMap.newInstance();
		Map<String, Object> result = FastMap.newInstance();

		/* ImageProperties.xml */
		String imgPropertyFullPath = System.getProperty("ofbiz.home")
				+ "/applications/product/config/ImageProperties.xml";
		resultXMLMap.putAll(ImageTransform.getXMLValue(imgPropertyFullPath, locale));
		if (resultXMLMap.containsKey("responseMessage") && resultXMLMap.get("responseMessage").equals("success")) {
			imgPropertyMap.putAll(UtilGenerics.<Map<String, Map<String, String>>> cast(resultXMLMap.get("xml")));
		} else {
			String errMsg = UtilProperties.getMessage(ScaleImage.resource, "ScaleImage.unable_to_parse", locale)
					+ " : ImageProperties.xml";
			Debug.logError(errMsg, module);
			result.put("errorMessage", errMsg);
			return result;
		}

		/* IMAGE */
		// get Name and Extension
		index = filenameToUse.lastIndexOf(".");
		String imgName = filenameToUse.substring(0, index - 1);
		String imgExtension = filenameToUse.substring(index + 1);
		// paths
		String mainFilenameFormat = UtilProperties.getPropertyValue("catalog", "image.filename.format");
		String imageServerPath = FlexibleStringExpander
				.expandString(UtilProperties.getPropertyValue("catalog", "image.server.path"), context);
		String imageUrlPrefix = UtilProperties.getPropertyValue("catalog", "image.url.prefix");

		String id = null;
		String type = null;
		if (viewType.toLowerCase().contains("main")) {
			type = "original";
			id = imgName;
		} else if (viewType.toLowerCase().contains("additional") && viewNumber != null && !viewNumber.equals("0")) {
			type = "additional";
			id = imgName + "_View_" + viewNumber;
		} else {
			return ServiceUtil.returnError(UtilProperties.getMessage(ScaleImage.resource, "ProductImageViewType",
					UtilMisc.toMap("viewType", type), locale));
		}
		FlexibleStringExpander mainFilenameExpander = FlexibleStringExpander.getInstance(mainFilenameFormat);
		String fileLocation = mainFilenameExpander
				.expandString(UtilMisc.toMap("location", "products", "id", context.get("productId"), "type", type));
		String filePathPrefix = "";
		if (fileLocation.lastIndexOf("/") != -1) {
			filePathPrefix = fileLocation.substring(0, fileLocation.lastIndexOf("/") + 1);

		}

		if (context.get("contentId") != null) {
			resultBufImgMap.putAll(ImageTransform.getBufferedImage(
					imageServerPath + "/" + context.get("productId") + "/" + context.get("clientFileName"), locale));
		} else {
			/* get original BUFFERED IMAGE */
			resultBufImgMap.putAll(
					ImageTransform.getBufferedImage(imageServerPath + "/" + filePathPrefix + filenameToUse, locale));
		}

		if (resultBufImgMap.containsKey("responseMessage")
				&& resultBufImgMap.get("responseMessage").equals("success")) {
			bufImg = (BufferedImage) resultBufImgMap.get("bufferedImage");

			// get Dimensions
			imgHeight = bufImg.getHeight();
			imgWidth = bufImg.getWidth();
			if (imgHeight == 0.0 || imgWidth == 0.0) {
				String errMsg = UtilProperties.getMessage(ScaleImage.resource,
						"ScaleImage.one_current_image_dimension_is_null", locale) + " : imgHeight = " + imgHeight
						+ " ; imgWidth = " + imgWidth;
				Debug.logError(errMsg, module);
				result.put("errorMessage", errMsg);
				return result;
			}

			// new Filename Format
			FlexibleStringExpander addFilenameExpander = mainFilenameExpander;
			if (viewType.toLowerCase().contains("additional")) {
				String addFilenameFormat = UtilProperties.getPropertyValue("catalog",
						"image.filename.additionalviewsize.format");
				addFilenameExpander = FlexibleStringExpander.getInstance(addFilenameFormat);
			}

			/* scale Image for each Size Type */
			for (String sizeType : sizeTypeList) {
				resultScaleImgMap.putAll(
						ImageTransform.scaleImage(bufImg, imgHeight, imgWidth, imgPropertyMap, sizeType, locale));

				if (resultScaleImgMap.containsKey("responseMessage")
						&& resultScaleImgMap.get("responseMessage").equals("success")) {
					bufNewImg = (BufferedImage) resultScaleImgMap.get("bufferedImage");

					// write the New Scaled Image
					String newFileLocation = null;
					if (viewType.toLowerCase().contains("main")) {
						newFileLocation = mainFilenameExpander
								.expandString(UtilMisc.toMap("location", "products", "id", id, "type", sizeType));
					} else if (viewType.toLowerCase().contains("additional")) {
						newFileLocation = addFilenameExpander.expandString(UtilMisc.toMap("location", "products", "id",
								id, "viewtype", viewType, "sizetype", sizeType));
					}
					String newFilePathPrefix = "";
					if (newFileLocation.lastIndexOf("/") != -1) {
						newFilePathPrefix = newFileLocation.substring(0, newFileLocation.lastIndexOf("/") + 1);
					}

					String targetDirectory = imageServerPath + "/" + newFilePathPrefix;
					File targetDir = new File(targetDirectory);
					if (!targetDir.exists()) {
						boolean created = targetDir.mkdirs();
						if (!created) {
							String errMsg = UtilProperties.getMessage(ScaleImage.resource,
									"ScaleImage.unable_to_create_target_directory", locale) + " - " + targetDirectory;
							Debug.logFatal(errMsg, module);
							return ServiceUtil.returnError(errMsg);
						}
					}

					// write new image
					try {
						ImageIO.write(bufNewImg, imgExtension,
								new File(imageServerPath + "/" + newFilePathPrefix + filenameToUse));
					} catch (IllegalArgumentException e) {
						String errMsg = UtilProperties.getMessage(ScaleImage.resource,
								"ScaleImage.one_parameter_is_null", locale) + e.toString();
						Debug.logError(errMsg, module);
						result.put("errorMessage", errMsg);
						return result;
					} catch (IOException e) {
						String errMsg = UtilProperties.getMessage(ScaleImage.resource,
								"ScaleImage.error_occurs_during_writing", locale) + e.toString();
						Debug.logError(errMsg, module);
						result.put("errorMessage", errMsg);
						return result;
					}

					/* write Return Result */
					String imageUrl = imageUrlPrefix + "/" + newFilePathPrefix + filenameToUse;
					imgUrlMap.put(sizeType, imageUrl);

				} // scaleImgMap
			} // sizeIter

			result.put("responseMessage", "success");
			result.put("imageUrlMap", imgUrlMap);
			result.put("original", resultBufImgMap);
			return result;

		} else {
			String errMsg = UtilProperties.getMessage(ScaleImage.resource, "ScaleImage.unable_to_scale_original_image",
					locale) + " : " + filenameToUse;
			Debug.logError(errMsg, module);
			result.put("errorMessage", errMsg);
			return ServiceUtil.returnError(errMsg);
		}
	}

}
