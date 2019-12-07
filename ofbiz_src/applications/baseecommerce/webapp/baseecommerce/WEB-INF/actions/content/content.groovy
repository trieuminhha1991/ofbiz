import java.util.*;
import java.lang.*;
import org.ofbiz.entity.*;
import org.ofbiz.entity.condition.*;
import org.ofbiz.base.util.*;
import org.ofbiz.base.util.UtilMisc;

contentId = parameters.cId;
if(UtilValidate.isNotEmpty(contentId)){
	ct = delegator.findOne("Content", [contentId : contentId], false);
	if(ct != null && ct.getString("statusId").equals("CTNT_PUBLISHED")){
		String categoryId = ct.getString("contentTypeId");
		cat = delegator.findOne("ContentType", [contentTypeId : categoryId], false);
		context.content = ct;
		context.cat = cat;
		context.checkContent = true;
	}
}
