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
import com.olbius.baselogistics.report.ReportEvens;

List<GenericValue> listProduct = delegator.findList("Product", null, null, null, null, false);
List<GenericValue>  listFacility = delegator.findList("Facility", null, null, null, null, false);
List<GenericValue> listUom = delegator.findList("Uom", EntityCondition.makeCondition(UtilMisc.toMap("uomTypeId", "PRODUCT_PACKING")), null, null, null, false);
listTransferItemReport = ReportEvens.exportTransferItemOlapToPdf(request, null);
context.listTransferItemReport = listTransferItemReport;
context.listProduct = listProduct;
context.listFacility = listFacility;
context.listUom = listUom;
