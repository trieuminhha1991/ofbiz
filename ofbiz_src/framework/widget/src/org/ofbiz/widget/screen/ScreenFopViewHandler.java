/*******************************************************************************
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 *******************************************************************************/
package org.ofbiz.widget.screen;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.nio.ByteBuffer;
import java.sql.Timestamp;
import java.util.Locale;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.transform.stream.StreamSource;

import org.apache.fop.apps.Fop;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilCodec;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilHttp;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ServiceUtil;
import org.ofbiz.webapp.view.AbstractViewHandler;
import org.ofbiz.webapp.view.ApacheFopWorker;
import org.ofbiz.webapp.view.ViewHandlerException;
import org.ofbiz.widget.form.FormStringRenderer;
import org.ofbiz.widget.form.MacroFormRenderer;
import org.ofbiz.widget.html.HtmlScreenRenderer;

import javolution.util.FastMap;

/**
 * Uses XSL-FO formatted templates to generate PDF, PCL, POSTSCRIPT etc.  views
 * This handler will use JPublish to generate the XSL-FO
 */
public class ScreenFopViewHandler extends AbstractViewHandler {
    public static final String module = ScreenFopViewHandler.class.getName();
    protected static final String DEFAULT_ERROR_TEMPLATE = "component://common/widget/CommonScreens.xml#FoError";

    protected ServletContext servletContext = null;

    /**
     * @see org.ofbiz.webapp.view.ViewHandler#init(javax.servlet.ServletContext)
     */
    @Override
	public void init(ServletContext context) throws ViewHandlerException {
        this.servletContext = context;
    }

    /**
     * @see org.ofbiz.webapp.view.ViewHandler#render(java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    @Override
	public void render(String name, String page, String info, String contentType, String encoding, HttpServletRequest request, HttpServletResponse response) throws ViewHandlerException {

        // render and obtain the XSL-FO
        Writer writer = new StringWriter();
        try {
            ScreenStringRenderer screenStringRenderer = new MacroScreenRenderer(UtilProperties.getPropertyValue("widget", getName() + ".name"), UtilProperties.getPropertyValue("widget", getName() + ".screenrenderer"));
            FormStringRenderer formStringRenderer = new MacroFormRenderer(UtilProperties.getPropertyValue("widget", getName() + ".formrenderer"), request, response);
            // TODO: uncomment these lines when the renderers are implemented
            //TreeStringRenderer treeStringRenderer = new MacroTreeRenderer(UtilProperties.getPropertyValue("widget", getName() + ".treerenderer"), writer);
            //MenuStringRenderer menuStringRenderer = new MacroMenuRenderer(UtilProperties.getPropertyValue("widget", getName() + ".menurenderer"), writer);
            ScreenRenderer screens = new ScreenRenderer(writer, null, screenStringRenderer);
            screens.populateContextForRequest(request, response, servletContext);

            // this is the object used to render forms from their definitions
            screens.getContext().put("formStringRenderer", formStringRenderer);
            screens.getContext().put("simpleEncoder", UtilCodec.getEncoder(UtilProperties.getPropertyValue("widget", getName() + ".encoder")));
            screens.render(page);
        } catch (Exception e) {
            renderError("Problems with the response writer/output stream", e, "[Not Yet Rendered]", request, response);
            return;
        }

        // set the input source (XSL-FO) and generate the output stream of contentType
        String screenOutString = writer.toString();
        if (!screenOutString.startsWith("<?xml")) {
            screenOutString = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + screenOutString;
        }
        if (Debug.verboseOn()) Debug.logVerbose("XSL:FO Screen Output: " + screenOutString, module);

        if (UtilValidate.isEmpty(contentType)) {
            contentType = UtilProperties.getPropertyValue("widget", getName() + ".default.contenttype");
        }
        Reader reader = new StringReader(screenOutString);
        StreamSource src = new StreamSource(reader);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try {
            Fop fop = ApacheFopWorker.createFopInstance(out, contentType);
            ApacheFopWorker.transform(src, null, fop);
        } catch (Exception e) {
            renderError("Unable to transform FO file", e, screenOutString, request, response);
            return;
        }
        if ("application/pdf".equals(contentType)) {
        	// TODOCHANGE new process, export response is redirect to other view
        	String separateSizeStr = UtilProperties.getPropertyValue("general.properties", "export.pdf.direct.size", "100000");
        	int separateSize = Integer.parseInt(separateSizeStr);
        	if (out.size() < separateSize) {
            	// set the content type and length
                response.setContentType(contentType);
                response.setContentLength(out.size());

                // write to the browser
                try {
                    out.writeTo(response.getOutputStream());
                    response.getOutputStream().flush();
                } catch (IOException e) {
                    renderError("Unable to write to OutputStream", e, screenOutString, request, response);
                }
            } else {
            	// get view-map info
            	String contentDisposition = (String) request.getAttribute("content-disposition");
            	if (contentDisposition == null) contentDisposition = "";
            	
            	GenericValue userLogin = (GenericValue) request.getSession().getAttribute("userLogin");
            	LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
            	Delegator delegator = (Delegator) request.getAttribute("delegator");
            	Locale locale = UtilHttp.getLocale(request);
            	String fileName = contentDisposition.replaceFirst("(?i)^.*filename=\"?([^\"]+)\"?.*$", "$1");
            	if (UtilValidate.isEmpty(fileName)) {
            		fileName = request.getRequestURI();
            		if (UtilValidate.isEmpty(fileName)) fileName = UtilDateTime.nowAsString() + ".pdf";
            		else {
            			fileName = fileName.replaceFirst("(?i)^.*control/\"?([^\"]+)\"?.*$", "$1");
            			if (!fileName.toLowerCase().contains(".pdf")) fileName += ".pdf";
            		}
            	}
            	String isPublic = "N";
            	String fileType = "pdf";
            	String exportName = module;
            	String exportCond = null;
            	String moduleExport = "PDF";
            	String descriptionExport = null;
            	Timestamp exportTime = UtilDateTime.nowTimestamp();
                try {
                	GenericValue fileExportedTmp = delegator.findOne("FileExportedTemp", UtilMisc.toMap("exportName", exportName, "exportTime", exportTime), false);
                	if (fileExportedTmp != null) {
                        request.setAttribute("_ERROR_MESSAGE_", UtilProperties.getMessage("CommonErrorUiLabels", "CommonSystemIsExportingFileWithTheSameCondition", locale));
                        request.setAttribute("response-view-name", "ViewFileList");
                        return;
                	}
                	
                	ByteBuffer uploadedFile = ByteBuffer.wrap(out.toByteArray());
                    Map<String, Object> contentCtx = FastMap.newInstance();
                    contentCtx.put("userLogin", userLogin);
                    contentCtx.put("uploadedFile", uploadedFile);
                    contentCtx.put("_uploadedFile_fileName", fileName);
                    contentCtx.put("_uploadedFile_contentType", fileType);
                    contentCtx.put("public", isPublic);
                    contentCtx.put("folder", "filetmpdir");
                    
                    Map<String, Object> fileResult = dispatcher.runSync("jackrabbitUploadFile", contentCtx);
                    if (ServiceUtil.isError(fileResult)) {
                    	//return ServiceUtil.returnError(ServiceUtil.getErrorMessage(fileResult));
                    	Debug.logError(ServiceUtil.getErrorMessage(fileResult), module);
                    	request.setAttribute("_ERROR_MESSAGE_", ServiceUtil.getErrorMessage(fileResult));
                    	request.setAttribute("response-view-name", "ViewFileList");
                    	return;
                    }
                    String path = (String) fileResult.get("path");
                    String nameStr = (String) fileResult.get("name");
                    String mimeType = (String) fileResult.get("mimeType");
                    
                    // create value in FileExportedTemp entity
                	fileExportedTmp = delegator.makeValue("FileExportedTemp");
                	fileExportedTmp.put("exportName", exportName);
                    fileExportedTmp.put("exportTime", UtilDateTime.nowTimestamp());
                    fileExportedTmp.put("exportCond", exportCond);
                    fileExportedTmp.put("fileName", fileName);
                    fileExportedTmp.put("fileSize", Long.valueOf(out.size()));
                    fileExportedTmp.put("fileType", fileType);
                    fileExportedTmp.put("path", path);
                    fileExportedTmp.put("name", nameStr);
                    fileExportedTmp.put("mimeType", mimeType);
                    fileExportedTmp.put("module", moduleExport);
                    fileExportedTmp.put("statusId", "COMPLETED");
                    fileExportedTmp.put("description", descriptionExport);
                    fileExportedTmp.put("finishTime", UtilDateTime.nowTimestamp());
                    fileExportedTmp.put("createdByUserLogin", userLogin.get("userLoginId"));
                    fileExportedTmp.put("isPublic", isPublic);
                    delegator.create(fileExportedTmp);
                } catch (GenericServiceException | GenericEntityException ex) {
                	/*Map<String, Object> changeStatusCtx = UtilMisc.toMap("exportName", exportName, "exportTime", exportTime, "statusId", "ERROR", "userLogin", userLogin);
                    try {
             			dispatcher.runAsync("changeStatusFileExported", changeStatusCtx, false);
             		} catch (GenericServiceException e) {
             			Debug.logError("Error when invoike service change status record log: " + e, module);
             		}*/
                	
                    Debug.logError(ex, module);
                    request.setAttribute("_ERROR_MESSAGE_", UtilProperties.getMessage("CommonUiLabels", "CommonErrorWhenStoreFile", locale));
                }
            	
            	request.setAttribute("response-view-name", "ViewFileList");
            }
        } else {
        	// set the content type and length
            response.setContentType(contentType);
            response.setContentLength(out.size());

            // write to the browser
            try {
                out.writeTo(response.getOutputStream());
                response.getOutputStream().flush();
            } catch (IOException e) {
                renderError("Unable to write to OutputStream", e, screenOutString, request, response);
            }
        }
    }

    protected void renderError(String msg, Exception e, String screenOutString, HttpServletRequest request, HttpServletResponse response) throws ViewHandlerException {
        Debug.logError(msg + ": " + e + "; Screen XSL:FO text was:\n" + screenOutString, module);
        try {
            Writer writer = new StringWriter();
            ScreenRenderer screens = new ScreenRenderer(writer, null, new HtmlScreenRenderer());
            screens.populateContextForRequest(request, response, servletContext);
            screens.getContext().put("errorMessage", msg + ": " + e);
            screens.render(DEFAULT_ERROR_TEMPLATE);
            response.setContentType("text/html");
            response.getWriter().write(writer.toString());
            writer.close();
        } catch (Exception x) {
            Debug.logError("Multiple errors rendering FOP", module);
            throw new ViewHandlerException("Multiple errors rendering FOP", x);
        }
    }
}
