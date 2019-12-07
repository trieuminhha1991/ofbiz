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

pagesize = ConfigUtils.TOP_COMMENT_ARTICLE_CATEGORY_PAGE_SIZE;
contents = ContentUtils.getPostTopComment(delegator, 0, pagesize);
res = FastList.newInstance();
for(GenericValue e : contents){
	String contentId = e.getString("contentIdTo");
	Map<String, Object> content = ContentUtils.getContent(delegator, contentId, locale);
	res.add(content);
}
context.posttopcomment = res;