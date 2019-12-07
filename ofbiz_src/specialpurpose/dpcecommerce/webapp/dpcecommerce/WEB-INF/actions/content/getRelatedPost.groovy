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

pagesize = ConfigUtils.RELATE_ARTICLE_CATEGORY_PAGE_SIZE;
contentId = parameters.pid;
if(UtilValidate.isNotEmpty(contentId)){
	categories = delegator.findList("Content", EntityCondition.makeCondition(UtilMisc.toList(
										EntityCondition.makeCondition([contentId : contentId]),
										EntityCondition.makeCondition([statusId : "CTNT_PUBLISHED"]))
									), null, null, null, false);
	List<EntityCondition> lcond = FastList.newInstance();
	for(GenericValue e : categories){
		String contentTypeId = e.getString("contentTypeId");
		lcond.add(EntityCondition.makeCondition([contentTypeId : contentTypeId]));
	}
	cond = EntityCondition.makeCondition(UtilMisc.toList(EntityCondition.makeCondition([statusId : "CTNT_PUBLISHED"]),
			EntityCondition.makeCondition(lcond)));
	findOptions = new EntityFindOptions(true, EntityFindOptions.TYPE_SCROLL_INSENSITIVE, EntityFindOptions.CONCUR_READ_ONLY, true);
	lstIte = delegator.find("Content", cond, null, null, UtilMisc.toList("-createdDate"), findOptions);
	arraySize = lstIte.getResultsSizeAfterPartialList();
	List<GenericValue> contents = FastList.newInstance();
	if(arraySize < pagesize){
		contents = lstIte.getPartialList(0,arraySize);
	}else{
		contents = lstIte.getPartialList(0,pagesize);
	}
	lstIte.close();

	context.relatedPost = ContentUtils.processListContent(delegator, contents, locale);
}