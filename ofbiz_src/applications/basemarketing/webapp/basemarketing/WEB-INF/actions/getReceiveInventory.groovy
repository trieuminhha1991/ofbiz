import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.util.*
import org.ofbiz.entity.condition.*
import org.ofbiz.service.ServiceUtil

import java.util.Map;
import java.sql.Date;
import java.text.SimpleDateFormat;

import org.ofbiz.base.util.*;
import org.ofbiz.entity.*;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityConditionList;
import org.ofbiz.entity.condition.EntityExpr;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.model.*;
import org.ofbiz.entity.util.EntityListIterator;
import org.ofbiz.entity.util.EntityUtil;

inventoryItemTypes = delegator.findList("InventoryItemType", null, null, null, null, false);
// reject reasons
rejectReasons = delegator.findList("RejectionReason", null, null, null, null, false);
context.inventoryItemTypes = inventoryItemTypes;
context.rejectReasons = rejectReasons;
