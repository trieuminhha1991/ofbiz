package com.olbius.jackrabbit.services.content;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;
import java.util.Locale;
import java.util.Map;

import javolution.util.FastMap;

import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ServiceUtil;


public class JcrDataServices {
	public static final String module = JcrDataServices.class.getName();
    public static final String resource = "JcrWrapperContentUiLabels";


    /**
     * A service wrapper for the createElectronicTextMethod method. Forces permissions to be checked.
     */
    public static Map<String, Object> createElectronicText(DispatchContext dctx, Map<String, ? extends Object> context) {
        Map<String, Object> result = createElectronicTextMethod(dctx, context);
        return result;
    }

    public static Map<String, Object> createElectronicTextMethod(DispatchContext dctx, Map<String, ? extends Object> context) {
    	LocalDispatcher dispatcher = dctx.getDispatcher();
    	Map<String, Object> result = FastMap.newInstance();
        Delegator delegator = dctx.getDelegator();
        String dataResourceId = (String) context.get("dataResourceId");
        String textData = (String) context.get("textData");

        Map<String, Object> uploadContext = FastMap.newInstance();


//		String folder = pathFile.substring(0, pathFile.lastIndexOf('/'));
//		String fileName = pathFile.substring(pathFile.lastIndexOf('/') + 1);
        String fileName = "message";

		uploadContext.put("userLogin", context.get("userLogin"));
		uploadContext.put("_uploadedFile_fileName", fileName);
//		uploadContext.put("folder", folder);
		uploadContext.put("public", context.get("isPublic"));
		uploadContext.put("textData", textData);

		Map<String, Object> response;
		try {
			response = dispatcher.runSync("jackrabbitUploadText", uploadContext);
		} catch (GenericServiceException e) {
			return ServiceUtil.returnError(e.getMessage());
		}

		//update objectInfo DataResource
        GenericValue dataResource = null;
        try {
			dataResource = delegator.findOne("DataResource", UtilMisc.toMap("dataResourceId", dataResourceId), false);
			dataResource.put("objectInfo", response.get("path"));
			dataResource.store();
		} catch (GenericEntityException e) {
			return ServiceUtil.returnError(e.getMessage());
		}

        return result;
    }

    /**
     * A service wrapper for the createFileMethod method. Forces permissions to be checked.
     */
    public static Map<String, Object> createFile(DispatchContext dctx, Map<String, ? extends Object> context) {
        return createFileMethod(dctx, context);
    }

    public static Map<String, Object> createFileNoPerm(DispatchContext dctx, Map<String, ? extends Object> rcontext) {
        Map<String, Object> context = UtilMisc.makeMapWritable(rcontext);
        context.put("skipPermissionCheck", "true");
        return createFileMethod(dctx, context);
    }

    public static Map<String, Object> createFileMethod(DispatchContext dctx, Map<String, ? extends Object> context) {
    	LocalDispatcher dispatcher = dctx.getDispatcher();

    	//GenericValue dataResource = (GenericValue) context.get("dataResource");
        String dataResourceTypeId = (String) context.get("dataResourceTypeId");
        String objectInfo = (String) context.get("objectInfo");
        ByteBuffer binData = (ByteBuffer) context.get("binData");
        String textData = (String) context.get("textData");
        Locale locale = (Locale) context.get("locale");


        // a few place holders
        String prefix = "";
        String sep = "";

        // extended validation for binary/character data
        if (UtilValidate.isNotEmpty(textData) && binData != null) {
            return ServiceUtil.returnError(UtilProperties.getMessage(resource, "ContentCannotProcessBothCharacterAndBinaryFile", locale));
        }

        // Cac buoc thuc hien de chuyen viec luu du lieu vao file sang luu du lieu vao JCR
        // Step 1: Chuyen tu ham tao file thanh ham tao duong dan (path).
        // Step 2: Thuc hien luu vao JCR theo path.
        // Step 3: Update objectInfo.

        String pathFile = null;

        if (UtilValidate.isEmpty(objectInfo)) {
            return ServiceUtil.returnError(UtilProperties.getMessage(resource, "ContentUnableObtainReferenceToFile", UtilMisc.toMap("objectInfo", ""), locale));
        }
        if (UtilValidate.isEmpty(dataResourceTypeId) || dataResourceTypeId.equals("LOCAL_FILE") || dataResourceTypeId.equals("LOCAL_FILE_BIN")) {
        	pathFile = objectInfo;

        } else if (dataResourceTypeId.equals("OFBIZ_FILE") || dataResourceTypeId.equals("OFBIZ_FILE_BIN")) {
            prefix = System.getProperty("ofbiz.home");
            if (objectInfo.indexOf("/") != 0 && prefix.lastIndexOf("/") != (prefix.length() - 1)) {
                sep = "/";
            }
            pathFile = prefix + sep + objectInfo;
        } else if (dataResourceTypeId.equals("CONTEXT_FILE") || dataResourceTypeId.equals("CONTEXT_FILE_BIN")) {
            prefix = (String) context.get("rootDir");
            if (UtilValidate.isEmpty(prefix)) {
                return ServiceUtil.returnError(UtilProperties.getMessage(resource, "ContentCannotFindContextFileWithEmptyContextRoot", locale));
            }
            if (objectInfo.indexOf("/") != 0 && prefix.lastIndexOf("/") != (prefix.length() - 1)) {
                sep = "/";
            }
            pathFile = prefix + sep + objectInfo;
        }

        if (pathFile == null) {
            return ServiceUtil.returnError(UtilProperties.getMessage(resource, "ContentUnableObtainReferenceToFile", UtilMisc.toMap("objectInfo", objectInfo), locale));
        }


		Map<String, Object> uploadContext = FastMap.newInstance();

		String folder = pathFile.substring(0, pathFile.lastIndexOf('/'));
		String fileName = pathFile.substring(pathFile.lastIndexOf('/') + 1);

		uploadContext.put("userLogin", context.get("userLogin"));
		uploadContext.put("_uploadedFile_fileName", fileName);
		uploadContext.put("folder", folder);
		uploadContext.put("public", context.get("isPublic"));


        // write the data to the file
        if (UtilValidate.isNotEmpty(textData)) {
        	uploadContext.put("uploadedFile", str_to_bb(textData));
        } else if (binData != null) {
        	uploadContext.put("uploadedFile", binData);
        } else {
            return ServiceUtil.returnError(UtilProperties.getMessage(resource, "ContentNoContentFilePassed", UtilMisc.toMap("fileName", pathFile), locale));
        }

		Map<String, Object> response;
		try {
			response = dispatcher.runSync("jackrabbitUploadFile", uploadContext);
		} catch (GenericServiceException e) {
			return ServiceUtil.returnError(UtilProperties.getMessage(resource, "ContentNoContentFilePassed", UtilMisc.toMap("fileName", pathFile), locale));
		}

		//update objectInfo DataResource
		Delegator delegator = dctx.getDelegator();
        String dataResourceId = (String) context.get("dataResourceId");

        GenericValue dataResource = null;
        try {
			dataResource = delegator.findOne("DataResource", UtilMisc.toMap("dataResourceId", dataResourceId), false);
			dataResource.put("objectInfo", response.get("path"));
			dataResource.store();
		} catch (GenericEntityException e) {
			return ServiceUtil.returnError(e.getMessage());
		}

        Map<String, Object> result = ServiceUtil.returnSuccess();
        return result;
    }


    public static Charset charset = Charset.forName("UTF-8");
    public static CharsetEncoder encoder = charset.newEncoder();
    public static CharsetDecoder decoder = charset.newDecoder();

    public static ByteBuffer str_to_bb(String msg){
      try{
        return encoder.encode(CharBuffer.wrap(msg));
      } catch(Exception e){e.printStackTrace();}
      return null;
    }

    public static String bb_to_str(ByteBuffer buffer){
      String data = "";
      try {
        int old_position = buffer.position();
        data = decoder.decode(buffer).toString();
        // reset buffer's position to its original so it is not altered:
        buffer.position(old_position);
      } catch (Exception e){
        e.printStackTrace();
        return "";
      }

      return data;
    }
}
