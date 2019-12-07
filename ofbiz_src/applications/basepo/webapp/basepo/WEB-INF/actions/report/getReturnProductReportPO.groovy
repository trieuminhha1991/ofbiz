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
import com.olbius.basepo.report.ReportEvens;

List<GenericValue> listProduct = delegator.findList("Product", null, null, null, null, false);
List<GenericValue>  listFacility = delegator.findList("Facility", null, null, null, null, false);
List<GenericValue>  listProductCategory = delegator.findList("ProductCategory", null, null, null, null, false);
List<GenericValue>  listEnumeration = delegator.findList("Enumeration", null, null, null, null, false);
List<GenericValue>  listReturnReason = delegator.findList("ReturnReason", null, null, null, null, false);
List<GenericValue>  listProductStore = delegator.findList("ProductStore", null, null, null, null, false);
listReturnProductReport = ReportEvens.exportReturnProductOlapPOToPdf(request, null);
List<GenericValue> listUom = delegator.findList("Uom", EntityCondition.makeCondition(UtilMisc.toMap("uomTypeId", "PRODUCT_PACKING")), null, null, null, false);
context.listProduct = listProduct;
context.listFacility = listFacility;
context.listProductCategory = listProductCategory;
context.listReturnProductReport = listReturnProductReport;
context.listReturnReason = listReturnReason;
context.listUom = listUom;
context.listProductStore = listProductStore;


