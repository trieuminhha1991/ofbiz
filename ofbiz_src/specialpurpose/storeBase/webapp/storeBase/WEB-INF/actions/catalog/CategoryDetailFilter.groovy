import org.ofbiz.base.util.*;
import org.ofbiz.product.catalog.*;
import org.ofbiz.product.feature.*;
import org.ofbiz.storebase.product.*;

searchCategoryId = request.getAttribute("productCategoryId");


parameters.SEARCH_CATEGORY_ID = searchCategoryId;

if (!parameters.sortAscending){
	parameters.sortAscending = "Y";
}

// note: this can be run multiple times in the same request without causing problems, will check to see on its own if it has run again
ProductSearchSession.processSearchParameters(parameters, request);

prodCatalogId = CatalogWorker.getCurrentCatalogId(request);
result = ProductSearchSession.getProductSearchResult(request, delegator, prodCatalogId);

context.productIds = result.productIds;
context.viewIndex = result.viewIndex;
context.viewSize = result.viewSize;
context.listSize = result.listSize;
context.lowIndex = result.lowIndex;
context.highIndex = result.highIndex;
context.paging = result.paging;
context.previousViewSize = result.previousViewSize;
context.searchCategory = result.searchCategory;
context.searchConstraintStrings = result.searchConstraintStrings;
context.searchSortOrderString = result.searchSortOrderString;

if (request.getParameter("sortOrder")){
	context.sortOrder = (String) request.getParameter("sortOrder");
}

if (request.getParameter("sortAscending")){
	context.sortAscending = (String) request.getParameter("sortAscending");
} else {
	context.sortAscending = parameters.sortAscending;
}

if (request.getParameter("viewSize")){
	context.viewSize = Integer.valueOf( request.getParameter("viewSize"));
}