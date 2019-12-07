import org.ofbiz.entity.util.*;

viewSize = parameters.VIEW_SIZE ?:8;
viewIndex = parameters.VIEW_INDEX ?:1;
context.viewIndex = String.valueOf(viewIndex);
context.viewSize =  viewSize;
// parameters.orderByFields

// set the default view size
defaultViewSize = request.getAttribute("defaultViewSize") ?: 8;
context.defaultViewSize = defaultViewSize;

// set the limit view
limitView = request.getAttribute("limitView") ?: true;
context.limitView = limitView;
tmpParam = new HashMap();
tmpParam.partyId = parameters.supId;
andMap = ["inputFields" : tmpParam,
			"viewIndex" : new Integer(viewIndex),
			"viewSize" : new Integer(viewSize),
			"entityName":"ProductSellerFriend"];
tmpList = dispatcher.runSync("performFind", andMap);
if(tmpList != null && tmpList.listIt != null){
	context.listProduct = EntityUtil.filterByDate(tmpList.listIt.getCompleteList());
}
context.hoz = "Y";