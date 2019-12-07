import java.util.*;
import java.lang.*;
import org.ofbiz.entity.*;
import org.ofbiz.entity.condition.*;
import org.ofbiz.base.util.*;
import org.ofbiz.base.util.UtilMisc;
import com.olbius.obb.*;
import com.olbius.baseecommerce.backend.*;
import javolution.util.*;

categories = delegator.findList("ContentCategory", EntityCondition.makeCondition([contentCategoryTypeId : "ARTICLE"]), null, null, null, false);

pagesize = ConfigUtils.CATEGORY_INTRO_CONTENT_PAGE_SIZE;
viewIndex = 0;
List<Map<String, Object>> res = FastList.newInstance();
for(GenericValue e : categories){
	Map<String, Object> o = FastMap.newInstance();
	o.put("contentCategoryId", e.getString("contentCategoryId"));
	o.put("categoryName", e.getString("categoryName"));
	o.put("icon", e.getString("icon"));
	catContentId = e.getString("contentCategoryId");
	contentMap = ContentUtils.getContentDataByCategory(delegator, context.webSiteId, catContentId, viewIndex, pagesize);
	contentList = contentMap.contents;
	o.put("contents", contentList);
	res.add(o);
}
context.contentCategories = res;
