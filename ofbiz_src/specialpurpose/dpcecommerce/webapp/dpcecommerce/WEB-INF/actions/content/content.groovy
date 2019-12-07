import java.util.*;
import java.lang.*;
import org.ofbiz.entity.*;
import org.ofbiz.entity.condition.*;
import org.ofbiz.base.util.*;
import org.ofbiz.base.util.UtilMisc;

contentId = parameters.pid;
if(UtilValidate.isNotEmpty(contentId)){
	ct = ContentUtils.getContent(delegator, request, contentId);
	context.content = ct;
}
