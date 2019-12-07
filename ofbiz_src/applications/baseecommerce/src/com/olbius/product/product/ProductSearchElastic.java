package com.olbius.product.product;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.product.store.ProductStoreWorker;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;

public class ProductSearchElastic {
	public static final String module = ProductSearchElastic.class.getName();

	public static final String searchKeyword(HttpServletRequest request, HttpServletResponse response) {

		String productStoreId = ProductStoreWorker.getProductStoreId(request);
		if (productStoreId != null) {
			String catalogId = request.getParameter("SEARCH_CATALOG_ID");
			String keyword = request.getParameter("name");
			String viewSize = request.getParameter("VIEW_SIZE");
			String viewIndex = request.getParameter("VIEW_INDEX");

			if(viewIndex == null) {
				viewIndex = "0";
			}

			if(viewSize == null) {
				viewSize = "8";
			}

			Integer row;

			try{
				row = Integer.parseInt(viewSize);
			} catch (NumberFormatException e) {
				row = 8;
			}

			Integer index;

			try{
				index = Integer.parseInt(viewIndex);
			} catch (NumberFormatException e) {
				index = 0;
			}

			index = index*row;

			LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
			Map<String, Object> result = null;
			try {
				if (catalogId == null || catalogId.equals("#")) {
					result = dispatcher.runSync("searchProducts",
							UtilMisc.toMap("keyword", keyword, "productStoreId", productStoreId, "rows", row, "start", index));
				} else {
					result = dispatcher.runSync("searchProducts",
							UtilMisc.toMap(new String("keyword"), keyword, "prodCatalog", catalogId, "productStoreId", productStoreId, "rows", row, "start", index));
				}
			} catch (GenericServiceException e) {
				request.setAttribute("_ERROR_MESSAGE_", e.getMessage());
				return "error";
			}
			request.setAttribute("result", result.get("result"));
			request.setAttribute("countResult", result.get("count"));
		}
		return "success";
	}
}
