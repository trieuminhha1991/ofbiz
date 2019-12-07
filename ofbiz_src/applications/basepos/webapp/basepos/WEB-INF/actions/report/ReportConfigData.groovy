import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.GenericValue
import org.ofbiz.entity.condition.EntityCondition
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.condition.EntityJoinOperator;
import javolution.util.FastList;
import com.olbius.basehr.util.MultiOrganizationUtil;

List<GenericValue> listProductCategory = FastList.newInstance();
List<GenericValue> facilityList = FastList.newInstance();
List<GenericValue> productStoreList = FastList.newInstance();
List<GenericValue> productList = FastList.newInstance();
List<GenericValue> customerList = FastList.newInstance();
List<GenericValue> employeeList = FastList.newInstance();

organization = MultiOrganizationUtil.getCurrentOrganization(delegator, userLogin.getString("userLoginId"));

EntityCondition categoryTypeCond = EntityCondition.makeCondition("productCategoryTypeId", EntityOperator.EQUALS, "CATALOG_CATEGORY");
EntityCondition catalogCond = EntityCondition.makeCondition("prodCatalogId", EntityOperator.EQUALS, "MainCatalog");
listProductCategory = delegator.findList("ProdCatalogCategoryAndProductCategory", EntityCondition.makeCondition(EntityJoinOperator.AND, categoryTypeCond, catalogCond), null, null, null, false);
context.listProductCategory = listProductCategory;

EntityCondition channelCond = EntityCondition.makeCondition("salesMethodChannelEnumId", EntityOperator.EQUALS, "SMCHANNEL_POS");
EntityCondition orgCond = EntityCondition.makeCondition("payToPartyId", EntityOperator.EQUALS, organization);
productStoreList = delegator.findList("ProductStore", EntityCondition.makeCondition(orgCond), null, null, null, false);
context.productStoreList = productStoreList;

EntityCondition typeCond = EntityCondition.makeCondition("facilityTypeId", EntityOperator.EQUALS, "WAREHOUSE");
EntityCondition orgFaCond = EntityCondition.makeCondition("ownerPartyId", EntityOperator.EQUALS, organization);
facilityList = delegator.findList("Facility", EntityCondition.makeCondition(EntityJoinOperator.AND, typeCond, orgFaCond), null, null, null, false);
context.facilityList = facilityList;

EntityCondition delCondition = EntityCondition.makeCondition("salesDiscontinuationDate", EntityOperator.EQUALS, null);
EntityCondition productTypeCondition = EntityCondition.makeCondition("productTypeId", EntityOperator.EQUALS, "FINISHED_GOOD");
productList = delegator.findList("ProductAndPriceAndGoodIdentificationSimple", EntityCondition.makeCondition(EntityJoinOperator.AND, delCondition, productTypeCondition), null, null, null, false);
context.productList = productList;

customerList = delegator.findList("CustomerAndStore", EntityCondition.makeCondition(EntityJoinOperator.AND, channelCond, orgCond), null, null, null, false);
context.customerList = customerList;

employeeList = delegator.findList("EmployeePOSAndRole", EntityCondition.makeCondition("parentTypeId", EntityOperator.EQUALS, "POS_ROLETYPE"), null, null, null, false);
context.employeeList = employeeList;
