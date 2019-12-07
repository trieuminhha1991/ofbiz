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

pagesize = 5;
cond = EntityCondition.makeCondition(UtilMisc.toList(EntityCondition.makeCondition([statusId : "CTNT_PUBLISHED"]),
		EntityCondition.makeCondition([contentTypeId : "COMMENT"])));
findOptions = new EntityFindOptions(true, EntityFindOptions.TYPE_SCROLL_INSENSITIVE, EntityFindOptions.CONCUR_READ_ONLY, true);
lstIte = delegator.find("Content", cond, null, null, UtilMisc.toList("-createdDate"), findOptions);
arraySize = lstIte.getResultsSizeAfterPartialList();
if(arraySize < pagesize){
	contents = lstIte.getPartialList(0,arraySize);
}else{
	contents = lstIte.getPartialList(0,pagesize);
}
lstIte.close();
context.bestcomment = contents;
