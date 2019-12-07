import java.util.*;
import java.lang.*;
import org.ofbiz.entity.*;
import org.ofbiz.entity.condition.*;
import org.ofbiz.base.util.*;
import org.ofbiz.base.util.UtilMisc;

categories = delegator.findList("ContentType", EntityCondition.makeCondition([contentTypeId : "NEWS_ARTICLE"]), null, null, null, false);
context.categories = categories;
