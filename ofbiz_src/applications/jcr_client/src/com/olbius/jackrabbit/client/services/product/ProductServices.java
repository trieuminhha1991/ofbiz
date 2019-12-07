package com.olbius.jackrabbit.client.services.product;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.jdom.JDOMException;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.product.image.ScaleImage;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ServiceUtil;

import com.olbius.jackrabbit.client.OlbiusProvider;

public class ProductServices {
	public static final String module = ProductServices.class.getName();

	public static Map<String, Object> addAdditionalViewForProduct(DispatchContext dctx, Map<String, ?> context)
			throws GenericServiceException {

		LocalDispatcher dispatcher = dctx.getDispatcher();
		Delegator delegator = dctx.getDelegator();
		String productContentTypeId = (String) context.get("productContentTypeId");
		String fileName = (String) context.get("_uploadedFile_fileName");
		String viewNumber = String.valueOf(productContentTypeId.charAt(productContentTypeId.length() - 1));
		try {
			Integer.parseInt(viewNumber);
		} catch (NumberFormatException e) {
			viewNumber = null;
		}
		String isPublic = (String) context.get("isPublic");
		if (isPublic == null) {
			isPublic = "Y";
		}
		if (UtilValidate.isNotEmpty(fileName)) {

			Map<String, Object> dataResourceCtx = UtilMisc.toMap("userLogin", context.get("userLogin"),
					"_uploadedFile_fileName", context.get("_uploadedFile_fileName"), "_uploadedFile_contentType",
					context.get("_uploadedFile_contentType"), "uploadedFile", context.get("uploadedFile"), "public",
					isPublic);

			Map<String, Object> result = null;

			try {
				result = dispatcher.runSync("jackrabbitUploadFile", dataResourceCtx);
			} catch (GenericServiceException e) {
				throw new GenericServiceException(e);
			}
			String path = (String) result.get("path");

			result = addImageResource(dispatcher, delegator, context, path, productContentTypeId);

			if (ServiceUtil.isError(result)) {
				return result;
			}

			if (viewNumber == null) {
				return ServiceUtil.returnSuccess();
			}

			dataResourceCtx = UtilMisc.toMap("userLogin", context.get("userLogin"), "curPath", OlbiusProvider.getJcrPath(path), "public",
					context.get("isPublic"));
			try {
				result = dispatcher.runSync("jackrabbitScaleImageService", dataResourceCtx);
			} catch (GenericServiceException e) {
				throw new GenericServiceException(e);
			}

			Map<String, String> imageUrlMap = UtilMisc.toMap(result.get("imageUrl"));

			for (String sizeType : ScaleImage.sizeTypeList) {
				String imageUrl = imageUrlMap.get(sizeType);
				if (UtilValidate.isNotEmpty(imageUrl)) {
					result = addImageResource(dispatcher, delegator, context, imageUrl,
							"XTRA_IMG_" + viewNumber + "_" + sizeType.toUpperCase());
					if (ServiceUtil.isError(result)) {
						return result;
					}
				}
			}
		}
		return ServiceUtil.returnSuccess();
	}

	private static Map<String, Object> addImageResource(LocalDispatcher dispatcher, Delegator delegator,
			Map<String, ? extends Object> context, String imageUrl, String productContentTypeId) {
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		String productId = (String) context.get("productId");

		if (UtilValidate.isNotEmpty(imageUrl) && imageUrl.length() > 0) {
			String contentId = (String) context.get("contentId");

			Map<String, Object> dataResourceCtx = new HashMap<String, Object>();
			dataResourceCtx.put("objectInfo", imageUrl);
			dataResourceCtx.put("dataResourceName", context.get("_uploadedFile_fileName"));
			dataResourceCtx.put("userLogin", userLogin);

			Map<String, Object> productContentCtx = new HashMap<String, Object>();
			productContentCtx.put("productId", productId);
			productContentCtx.put("productContentTypeId", productContentTypeId);
			productContentCtx.put("fromDate", context.get("fromDate"));
			productContentCtx.put("thruDate", context.get("thruDate"));
			productContentCtx.put("userLogin", userLogin);

			if (UtilValidate.isNotEmpty(contentId)) {
				GenericValue content = null;
				try {
					content = delegator.findOne("Content", UtilMisc.toMap("contentId", contentId), false);
				} catch (GenericEntityException e) {
					Debug.logError(e, module);
					return ServiceUtil.returnError(e.getMessage());
				}

				if (content != null) {
					GenericValue dataResource = null;
					try {
						dataResource = content.getRelatedOne("DataResource", false);
					} catch (GenericEntityException e) {
						Debug.logError(e, module);
						return ServiceUtil.returnError(e.getMessage());
					}

					if (dataResource != null) {
						dataResourceCtx.put("dataResourceId", dataResource.getString("dataResourceId"));
						try {
							dispatcher.runSync("updateDataResource", dataResourceCtx);
						} catch (GenericServiceException e) {
							Debug.logError(e, module);
							return ServiceUtil.returnError(e.getMessage());
						}
					} else {
						dataResourceCtx.put("dataResourceTypeId", "SHORT_TEXT");
						dataResourceCtx.put("mimeTypeId", "text/html");
						Map<String, Object> dataResourceResult = new HashMap<String, Object>();
						try {
							dataResourceResult = dispatcher.runSync("createDataResource", dataResourceCtx);
						} catch (GenericServiceException e) {
							Debug.logError(e, module);
							return ServiceUtil.returnError(e.getMessage());
						}

						Map<String, Object> contentCtx = new HashMap<String, Object>();
						contentCtx.put("contentId", contentId);
						contentCtx.put("dataResourceId", dataResourceResult.get("dataResourceId"));
						contentCtx.put("userLogin", userLogin);
						try {
							dispatcher.runSync("updateContent", contentCtx);
						} catch (GenericServiceException e) {
							Debug.logError(e, module);
							return ServiceUtil.returnError(e.getMessage());
						}
					}

					productContentCtx.put("contentId", contentId);
				}
			} else {
				dataResourceCtx.put("dataResourceTypeId", "SHORT_TEXT");
				dataResourceCtx.put("mimeTypeId", "text/html");
				Map<String, Object> dataResourceResult = new HashMap<String, Object>();
				try {
					dataResourceResult = dispatcher.runSync("createDataResource", dataResourceCtx);
				} catch (GenericServiceException e) {
					Debug.logError(e, module);
					return ServiceUtil.returnError(e.getMessage());
				}

				Map<String, Object> contentCtx = new HashMap<String, Object>();
				contentCtx.put("contentTypeId", "DOCUMENT");
				contentCtx.put("dataResourceId", dataResourceResult.get("dataResourceId"));
				contentCtx.put("userLogin", userLogin);
				Map<String, Object> contentResult = new HashMap<String, Object>();
				try {
					contentResult = dispatcher.runSync("createContent", contentCtx);
				} catch (GenericServiceException e) {
					Debug.logError(e, module);
					return ServiceUtil.returnError(e.getMessage());
				}

				productContentCtx.put("contentId", contentResult.get("contentId"));
				
			}
			try {
				GenericValue productContent = delegator.findOne("ProductContent", UtilMisc.toMap("contentId", contentId, "productId", productId,
						"productContentTypeId", productContentTypeId, "fromDate", context.get("fromDate")), false);
				if(productContent != null) {
					try {
						dispatcher.runSync("updateProductContent", productContentCtx);
					} catch (GenericServiceException e) {
						Debug.logError(e, module);
						return ServiceUtil.returnError(e.getMessage());
					}
				} else {
					try {
						dispatcher.runSync("createProductContent", productContentCtx);
					} catch (GenericServiceException e) {
						Debug.logError(e, module);
						return ServiceUtil.returnError(e.getMessage());
					}
				}
			} catch (GenericEntityException e) {
				Debug.logError(e, module);
				return ServiceUtil.returnError(e.getMessage());
			}
			
		}
		return ServiceUtil.returnSuccess();
	}

	public static Map<String, Object> addImageOverrideFields(DispatchContext dctx,
			Map<String, ? extends Object> context) throws GenericServiceException {

		Map<String, String> map = new HashMap<String, String>();
		map.put("small", "smallImageUrl");
		map.put("medium", "mediumImageUrl");
		map.put("large", "largeImageUrl");
		map.put("detail", "detailImageUrl");
		map.put("original", "originalImageUrl");

		String productId = (String) context.get("productId");
		String fileType = (String) context.get("upload_file_type");
		LocalDispatcher dispatcher = dctx.getDispatcher();
		Delegator delegator = dctx.getDelegator();
		GenericValue product = null;
		try {
			product = delegator.findOne("Product", false, "productId", productId);
		} catch (GenericEntityException e) {
			throw new GenericServiceException(e);
		}
		String fileName = (String) context.get("_uploadedFile_fileName");
		fileName = fileType + "." + fileName.substring(fileName.lastIndexOf(".") + 1);
		String isPublic = (String) context.get("isPublic");
		if (isPublic == null) {
			isPublic = "Y";
		}
		if (product != null) {
			Map<String, Object> dataResourceCtx = UtilMisc.toMap("userLogin", context.get("userLogin"),
					"_uploadedFile_fileName", fileName, "_uploadedFile_contentType",
					context.get("_uploadedFile_contentType"), "uploadedFile", context.get("uploadedFile"), "public",
					isPublic);

			Map<String, Object> result = new HashMap<String, Object>();

			try {
				result = dispatcher.runSync("jackrabbitUploadFile", dataResourceCtx);
			} catch (GenericServiceException e) {
				throw new GenericServiceException(e);
			}
			String path = (String) result.get("path");

			product.put(map.get(fileType), path);

			if (fileType.equals("original")) {
				dataResourceCtx = UtilMisc.toMap("userLogin", context.get("userLogin"), "curPath",
						OlbiusProvider.getJcrPath(path), "public", context.get("isPublic"));
				try {
					result = dispatcher.runSync("jackrabbitScaleImageService", dataResourceCtx);
				} catch (GenericServiceException e) {
					throw new GenericServiceException(e);
				}
				Map<String, String> imageUrl = UtilMisc.toMap(result.get("imageUrl"));
				for (String x : imageUrl.keySet()) {
					product.put(map.get(x), imageUrl.get(x));
				}
			}
			try {
				product.store();
			} catch (GenericEntityException e) {
				throw new GenericServiceException(e);
			}
		} else {
			throw new GenericServiceException(productId + "not found");
		}

		return ServiceUtil.returnSuccess();
	}

	public static Map<String, Object> addImageForProductPromo(DispatchContext dctx,
			Map<String, ? extends Object> context) throws IOException, JDOMException {
		LocalDispatcher dispatcher = dctx.getDispatcher();
		Delegator delegator = dctx.getDelegator();
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		String productPromoId = (String) context.get("productPromoId");
		String productPromoContentTypeId = (String) context.get("productPromoContentTypeId");
		String contentId = (String) context.get("contentId");
		
		if (UtilValidate.isNotEmpty(context.get("_uploadedFile_fileName"))) {

			Map<String, Object> dataResourceCtx = new HashMap<String, Object>();

			// add context file upload to jackrabbitUpdateDataResource service
			dataResourceCtx.put("uploadedFile", context.get("uploadedFile"));
			dataResourceCtx.put("_uploadedFile_fileName", context.get("_uploadedFile_fileName"));
			dataResourceCtx.put("_uploadedFile_contentType", context.get("_uploadedFile_contentType"));

			dataResourceCtx.put("dataResourceName", context.get("_uploadedFile_fileName"));
			dataResourceCtx.put("userLogin", userLogin);

			Map<String, Object> productPromoContentCtx = new HashMap<String, Object>();
			productPromoContentCtx.put("productPromoId", productPromoId);
			productPromoContentCtx.put("productPromoContentTypeId", productPromoContentTypeId);
			productPromoContentCtx.put("fromDate", context.get("fromDate"));
			productPromoContentCtx.put("thruDate", context.get("thruDate"));
			productPromoContentCtx.put("userLogin", userLogin);

			if (UtilValidate.isNotEmpty(contentId)) {
				GenericValue content = null;
				try {
					content = delegator.findOne("Content", UtilMisc.toMap("contentId", contentId), false);
				} catch (GenericEntityException e) {
					Debug.logError(e, module);
					return ServiceUtil.returnError(e.getMessage());
				}

				if (UtilValidate.isNotEmpty(content)) {
					GenericValue dataResource = null;
					try {
						dataResource = content.getRelatedOne("DataResource", false);
					} catch (GenericEntityException e) {
						Debug.logError(e, module);
						return ServiceUtil.returnError(e.getMessage());
					}

					if (UtilValidate.isNotEmpty(dataResource)) {
						dataResourceCtx.put("dataResourceId", dataResource.getString("dataResourceId"));
						try {
							dispatcher.runSync("jackrabbitUpdateDataResource", dataResourceCtx);
						} catch (GenericServiceException e) {
							Debug.logError(e, module);
							return ServiceUtil.returnError(e.getMessage());
						}
					} else {
						dataResourceCtx.put("dataResourceTypeId", "SHORT_TEXT");
						dataResourceCtx.put("mimeTypeId", "text/html");
						Map<String, Object> dataResourceResult = new HashMap<String, Object>();
						try {
							dataResourceResult = dispatcher.runSync("jackrabbitCreateDataResource", dataResourceCtx);
						} catch (GenericServiceException e) {
							Debug.logError(e, module);
							return ServiceUtil.returnError(e.getMessage());
						}

						Map<String, Object> contentCtx = new HashMap<String, Object>();
						contentCtx.put("contentId", contentId);
						contentCtx.put("dataResourceId", dataResourceResult.get("dataResourceId"));
						contentCtx.put("userLogin", userLogin);
						try {
							dispatcher.runSync("updateContent", contentCtx);
						} catch (GenericServiceException e) {
							Debug.logError(e, module);
							return ServiceUtil.returnError(e.getMessage());
						}
					}

					productPromoContentCtx.put("contentId", contentId);
					try {
						dispatcher.runSync("updateProductPromoContent", productPromoContentCtx);
					} catch (GenericServiceException e) {
						Debug.logError(e, module);
						return ServiceUtil.returnError(e.getMessage());
					}
				}
			} else {
				dataResourceCtx.put("dataResourceTypeId", "SHORT_TEXT");
				dataResourceCtx.put("mimeTypeId", "text/html");
				Map<String, Object> dataResourceResult = new HashMap<String, Object>();
				try {
					dataResourceResult = dispatcher.runSync("jackrabbitCreateDataResource", dataResourceCtx);
				} catch (GenericServiceException e) {
					Debug.logError(e, module);
					return ServiceUtil.returnError(e.getMessage());
				}

				Map<String, Object> contentCtx = new HashMap<String, Object>();
				contentCtx.put("contentTypeId", "DOCUMENT");
				contentCtx.put("dataResourceId", dataResourceResult.get("dataResourceId"));
				contentCtx.put("userLogin", userLogin);
				Map<String, Object> contentResult = new HashMap<String, Object>();
				try {
					contentResult = dispatcher.runSync("createContent", contentCtx);
				} catch (GenericServiceException e) {
					Debug.logError(e, module);
					return ServiceUtil.returnError(e.getMessage());
				}

				productPromoContentCtx.put("contentId", contentResult.get("contentId"));
				try {
					dispatcher.runSync("createProductPromoContent", productPromoContentCtx);
				} catch (GenericServiceException e) {
					Debug.logError(e, module);
					return ServiceUtil.returnError(e.getMessage());
				}
			}
		} else {
			Map<String, Object> productPromoContentCtx = new HashMap<String, Object>();
			productPromoContentCtx.put("productPromoId", productPromoId);
			productPromoContentCtx.put("productPromoContentTypeId", productPromoContentTypeId);
			productPromoContentCtx.put("contentId", contentId);
			productPromoContentCtx.put("fromDate", context.get("fromDate"));
			productPromoContentCtx.put("thruDate", context.get("thruDate"));
			productPromoContentCtx.put("userLogin", userLogin);
			try {
				dispatcher.runSync("updateProductPromoContent", productPromoContentCtx);
			} catch (GenericServiceException e) {
				Debug.logError(e, module);
				return ServiceUtil.returnError(e.getMessage());
			}
		}
		return ServiceUtil.returnSuccess();
	}

	public static Map<String, Object> uploadProductAdditionalViewImages(DispatchContext dctx, Map<String, ?> context)
			throws GenericServiceException {

		LocalDispatcher dispatcher = dctx.getDispatcher();
		String productId = (String) context.get("productId");
		Map<String, Object> result = null;

		if (productId == null) {
			ServiceUtil.returnError("product id null");
		}

		String fileName1 = (String) context.get("_additionalImageOne_fileName");
		String fileName2 = (String) context.get("_additionalImageTwo_fileName");
		String fileName3 = (String) context.get("_additionalImageThree_fileName");
		String fileName4 = (String) context.get("_additionalImageFour_fileName");

		if (fileName1 != null) {
			Map<String, Object> dataResourceCtx = UtilMisc.toMap("productId", productId, "userLogin",
					context.get("userLogin"), "_uploadedFile_fileName", context.get("_additionalImageOne_fileName"),
					"_uploadedFile_contentType", context.get("_additionalImageOne_contentType"), "uploadedFile",
					context.get("additionalImageOne"), "public", context.get("isPublic"), "productContentTypeId",
					"ADDITIONAL_IMAGE_1");
			try {
				result = dispatcher.runSync("jackrabbitAddAdditionalViewForProduct", dataResourceCtx);
			} catch (GenericServiceException e) {
				throw new GenericServiceException(e);
			}

			if (ServiceUtil.isError(result)) {
				return result;
			}
		}

		if (fileName2 != null) {
			Map<String, Object> dataResourceCtx = UtilMisc.toMap("productId", productId, "userLogin",
					context.get("userLogin"), "_uploadedFile_fileName", context.get("_additionalImageTwo_fileName"),
					"_uploadedFile_contentType", context.get("_additionalImageTwo_contentType"), "uploadedFile",
					context.get("additionalImageTwo"), "public", context.get("isPublic"), "productContentTypeId",
					"ADDITIONAL_IMAGE_2");
			try {
				result = dispatcher.runSync("jackrabbitAddAdditionalViewForProduct", dataResourceCtx);
			} catch (GenericServiceException e) {
				throw new GenericServiceException(e);
			}

			if (ServiceUtil.isError(result)) {
				return result;
			}
		}

		if (fileName3 != null) {
			Map<String, Object> dataResourceCtx = UtilMisc.toMap("productId", productId, "userLogin",
					context.get("userLogin"), "_uploadedFile_fileName", context.get("_additionalImageThree_fileName"),
					"_uploadedFile_contentType", context.get("_additionalImageThree_contentType"), "uploadedFile",
					context.get("additionalImageThree"), "public", context.get("isPublic"), "productContentTypeId",
					"ADDITIONAL_IMAGE_3");
			try {
				result = dispatcher.runSync("jackrabbitAddAdditionalViewForProduct", dataResourceCtx);
			} catch (GenericServiceException e) {
				throw new GenericServiceException(e);
			}

			if (ServiceUtil.isError(result)) {
				return result;
			}
		}

		if (fileName4 != null) {
			Map<String, Object> dataResourceCtx = UtilMisc.toMap("productId", productId, "userLogin",
					context.get("userLogin"), "_uploadedFile_fileName", context.get("_additionalImageFour_fileName"),
					"_uploadedFile_contentType", context.get("_additionalImageFour_contentType"), "uploadedFile",
					context.get("additionalImageFour"), "public", context.get("isPublic"), "productContentTypeId",
					"ADDITIONAL_IMAGE_4");
			try {
				result = dispatcher.runSync("jackrabbitAddAdditionalViewForProduct", dataResourceCtx);
			} catch (GenericServiceException e) {
				throw new GenericServiceException(e);
			}

			if (ServiceUtil.isError(result)) {
				return result;
			}
		}

		return ServiceUtil.returnSuccess();
	}

	public static Map<String, Object> uploadImageCategory(DispatchContext dctx, Map<String, ? extends Object> context)
			throws GenericServiceException {

		Map<String, String> map = new HashMap<String, String>();
		map.put("category", "categoryImageUrl");
		map.put("linkOne", "linkOneImageUrl");
		map.put("linkTwo", "linkTwoImageUrl");

		String productCategoryId = (String) context.get("productCategoryId");
		String fileType = (String) context.get("upload_file_type");
		LocalDispatcher dispatcher = dctx.getDispatcher();
		Delegator delegator = dctx.getDelegator();

		GenericValue productCategory = null;
		try {
			productCategory = delegator.findOne("ProductCategory", false, "productCategoryId", productCategoryId);
		} catch (GenericEntityException e) {
			throw new GenericServiceException(e);
		}
		String fileName = (String) context.get("_uploadedFile_fileName");
		fileName = fileType + "." + fileName.substring(fileName.lastIndexOf(".") + 1);
		String isPublic = (String) context.get("isPublic");
		if (isPublic == null) {
			isPublic = "Y";
		}
		if (productCategory != null) {
			Map<String, Object> dataResourceCtx = UtilMisc.toMap("userLogin", context.get("userLogin"),
					"_uploadedFile_fileName", fileName, "_uploadedFile_contentType",
					context.get("_uploadedFile_contentType"), "uploadedFile", context.get("uploadedFile"), "public", isPublic);

			Map<String, Object> result = new HashMap<String, Object>();

			try {
				result = dispatcher.runSync("jackrabbitUploadFile", dataResourceCtx);
			} catch (GenericServiceException e) {
				throw new GenericServiceException(e);
			}
			String path = (String) result.get("path");

			productCategory.put(map.get(fileType),path);
			try {
				productCategory.store();
			} catch (GenericEntityException e) {
				throw new GenericServiceException(e);
			}
		} else {
			throw new GenericServiceException(productCategoryId + "not found");
		}

		return ServiceUtil.returnSuccess();
	}
}
