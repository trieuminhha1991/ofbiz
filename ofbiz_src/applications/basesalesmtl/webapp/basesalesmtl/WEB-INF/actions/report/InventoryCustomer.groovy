import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.Delegator;
import javolution.util.FastList;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityJoinOperator;
import com.olbius.basesales.util.SalesPartyUtil;
import com.olbius.basehr.util.MultiOrganizationUtil;

def partyId = userLogin.getString("partyId");

def dummy = delegator.findByAnd("Product", UtilMisc.toMap("productTypeId", "FINISHED_GOOD"), null, false);
def products = "[";
def flag = false;
for (value in dummy) {
	if (flag) {
		products += ",";
	}
	products += "{ productId: " + "\'" + value.get("productId") + "\'" + ", productCode: " + "\'" + value.get("productCode") + "\'" + ", productName: " + "\'" + value.get("productName") + "\'" + " }";
	flag = true;
}
products += "]";
context.products = products;


def conditions = FastList.newInstance();
conditions.add(EntityCondition.makeCondition(UtilMisc.toMap("productCategoryTypeId", "CATALOG_CATEGORY")));
conditions.add(EntityCondition.makeCondition("productCategoryId", EntityJoinOperator.NOT_EQUAL, "BROWSE_ROOT"));
dummy = delegator.findList("ProductCategory",
		EntityCondition.makeCondition(conditions), null, null, null, false);
def categories = "[";
flag = false;
for(value in dummy) {
	if (flag) {
		categories += ",";
	}
	categories += "{ productCategoryId: " + "\'" + value.get("productCategoryId") + "\'" + ", categoryName: " + "\'" + value.get("categoryName") + "\'" + " }";
	flag = true;
}
categories += "]";
context.categories = categories;

