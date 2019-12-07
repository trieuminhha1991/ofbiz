import java.util.*;
import java.lang.*;

import org.ofbiz.entity.*;
import org.ofbiz.entity.condition.*;
import org.ofbiz.base.util.*;
import org.ofbiz.entity.util.EntityListIterator;
import org.ofbiz.entity.util.EntityFindOptions;

import com.olbius.baseecommerce.backend.ContentUtils;
import com.olbius.ecommerce.ConfigUtils;

context.latestNews = ContentUtils.processListContent(delegator, ContentUtils.getLatestNews(delegator, 0, ConfigUtils.LATEST_ARTICLE_CATEGORY_PAGE_SIZE), locale);
