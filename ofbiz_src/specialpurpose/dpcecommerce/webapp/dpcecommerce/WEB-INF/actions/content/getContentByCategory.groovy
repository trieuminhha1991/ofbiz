import java.util.*;
import java.lang.*;
import org.ofbiz.entity.*;
import org.ofbiz.entity.condition.*;
import org.ofbiz.base.util.*;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.util.EntityListIterator;
import org.ofbiz.entity.util.EntityFindOptions;
import javolution.util.FastList;
import javolution.util.FastMap;
import org.ofbiz.base.util.UtilValidate;
import com.olbius.baseecommerce.backend.ContentUtils;
import com.olbius.ecommerce.ConfigUtils;

pagesize = ConfigUtils.ARTICLE_CATEGORY_PAGE_SIZE;
viewIndex = parameters.VIEW_INDEX != null ? Integer.parseInt(parameters.VIEW_INDEX) : 0;
contentTypeId = parameters.id;
contentData = null;

if(UtilValidate.isNotEmpty(contentTypeId)){
	context.link = "categorycontent?id=" + contentTypeId;
	contentData = ContentUtils.getContentDataByCategory(delegator, contentTypeId, viewIndex, pagesize);
}else{
	context.link = "newcontent?";
	contentData = ContentUtils.getNewestContent(delegator, viewIndex, pagesize);
}
if(contentData != null){
	List<Map<String, Object>> contents = (List<Map<String, Object>>) contentData.get("contents");
	int totalrows = (int) contentData.get("totalrows");
	context.contentbycategory = ContentUtils.processListContent(delegator, contents, locale);
	context.listSize = totalrows;
	context.viewIndex = viewIndex;
	context.viewSize = pagesize;
	context.paginationSize = ConfigUtils.CATEGORY_DETAIL_PAGINATION_SIZE;
}
