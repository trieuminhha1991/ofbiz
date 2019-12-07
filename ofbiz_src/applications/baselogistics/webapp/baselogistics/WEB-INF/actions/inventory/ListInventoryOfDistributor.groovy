import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import javolution.util.FastList;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityJoinOperator
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityUtil;
import com.olbius.basesales.util.SalesUtil;
import com.olbius.basesales.util.SalesPartyUtil;
import com.olbius.basesales.party.PartyWorker;

/*
String orgId = SalesUtil.getCurrentOrganization(delegator, userLogin.getString("userLoginId"));
List<EntityCondition> conds = FastList.newInstance();
conds.add(EntityCondition.makeCondition("payToPartyId", orgId));
List<GenericValue> listProductStoreRoleOwner = delegator.findList("ProductStore", EntityCondition.makeCondition(conds), null, null, null, false);
List<String> productStoreIds = EntityUtil.getFieldListFromEntityList(listProductStoreRoleOwner, "productStoreId", false);

conds.clear();
conds.add(EntityCondition.makeCondition("productStoreId", EntityOperator.IN, productStoreIds));
conds.add(EntityCondition.makeCondition("partyId", userLogin.getString("userLoginId")));
conds.add(EntityCondition.makeCondition("roleTypeId","MANAGER"));
conds.add(EntityUtil.getFilterByDateExpr());
List<GenericValue> listProductStoreRoleManager = delegator.findList("ProductStoreRole", EntityCondition.makeCondition(conds), null, null, null, false);
List<String> productIdsmanager = EntityUtil.getFieldListFromEntityList(listProductStoreRoleManager, "productStoreId", false);

conds.clear();
conds.add(EntityCondition.makeCondition("productStoreId", EntityOperator.IN, productIdsmanager));
conds.add(EntityCondition.makeCondition("roleTypeId", "CUSTOMER"));
conds.add(EntityUtil.getFilterByDateExpr());
List<GenericValue> listCustomerOfProdStore = delegator.findList("ProductStoreRole", EntityCondition.makeCondition(conds), null, null, null, false);
List<String> distributorIds = EntityUtil.getFieldListFromEntityList(listCustomerOfProdStore, "partyId", false);

conds.clear();
conds.add(EntityCondition.makeCondition("partyId", EntityJoinOperator.IN, distributorIds));
conds.add(EntityCondition.makeCondition("statusId", "PARTY_ENABLED"));
List<GenericValue> listDitributor = delegator.findList("PartyDistributor", EntityCondition.makeCondition(conds), null, null, null, false);
//List<GenericValue> listDitributor = delegator.findList("ProductStoreRole", EntityCondition.makeCondition(conds), null, null, null, false);
*/
List<EntityCondition> conditions = FastList.newInstance();
try{
    boolean  isSearch = true;
    String userLoginPartyId = userLogin.getString("userLoginId");
    if (SalesPartyUtil.isSalesman(delegator, userLoginPartyId)) {
        List<String> listDistIds = PartyWorker.getDistributorBySalesman(delegator, userLoginPartyId);
        if (UtilValidate.isEmpty(listDistIds)) {
            isSearch = false;
        } else if (listDistIds.size() == 1) {
            conditions.add(EntityCondition.makeCondition("partyId", listDistIds.get(0)));
        } else {
            conditions.add(EntityCondition.makeCondition("partyId", EntityOperator.IN, listDistIds));
        }
    } else if (SalesPartyUtil.isSalessup(delegator, userLoginPartyId)) {
        conditions.add(EntityCondition.makeCondition("supervisorId", userLoginPartyId));
    } else if (SalesPartyUtil.isSalesASM(delegator, userLoginPartyId)) {
        List<String> listSupIds = PartyWorker.getSupervisorByASM(delegator, userLoginPartyId);
        if (UtilValidate.isEmpty(listSupIds)) {
            isSearch = false;
        } else if (listSupIds.size() == 1) {
            conditions.add(EntityCondition.makeCondition("supervisorId", listSupIds.get(0)));
        } else {
            conditions.add(EntityCondition.makeCondition("supervisorId", EntityOperator.IN, listSupIds));
        }
    } else if (SalesPartyUtil.isSalesRSM(delegator, userLoginPartyId)) {
        List<String> listSupIds = PartyWorker.getSupervisorByRSM(delegator, userLoginPartyId);
        if (UtilValidate.isEmpty(listSupIds)) {
            isSearch = false;
        } else if (listSupIds.size() == 1) {
            conditions.add(EntityCondition.makeCondition("supervisorId", listSupIds.get(0)));
        } else {
            conditions.add(EntityCondition.makeCondition("supervisorId", EntityOperator.IN, listSupIds));
        }
    } else if (SalesPartyUtil.isSalesCSM(delegator, userLoginPartyId)) {
        List<String> listSupIds = PartyWorker.getSupervisorByCSM(delegator, userLoginPartyId);
        if (UtilValidate.isEmpty(listSupIds)) {
            isSearch = false;
        } else if (listSupIds.size() == 1) {
            conditions.add(EntityCondition.makeCondition("supervisorId", listSupIds.get(0)));
        } else {
            conditions.add(EntityCondition.makeCondition("supervisorId", EntityOperator.IN, listSupIds));
        }
    } else if (SalesPartyUtil.isSalesAdmin(delegator, userLoginPartyId)) {
        List<String> listDistIds = PartyWorker.getDistributorBySalesadmin(delegator, userLoginPartyId);
        if (UtilValidate.isEmpty(listDistIds)) {
            isSearch = false;
        } else if (listDistIds.size() == 1) {
            conditions.add(EntityCondition.makeCondition("partyId", listDistIds.get(0)));
        } else {
            conditions.add(EntityCondition.makeCondition("partyId", EntityOperator.IN, listDistIds));
        }
    } else if (SalesPartyUtil.isSalesManager(delegator, userLoginPartyId) || SalesPartyUtil.isSalesAdminManager(delegator, userLoginPartyId)) {
        String currentOrgId = SalesUtil.getCurrentOrganization(delegator, userLogin);
        List<String> listDistIds = PartyWorker.getDistributorByOrg(delegator, currentOrgId , Boolean.FALSE);
        if (UtilValidate.isEmpty(listDistIds)) {
            isSearch = false;
        } else if (listDistIds.size() == 1) {
            conditions.add(EntityCondition.makeCondition("partyId", listDistIds.get(0)));
        } else {
            conditions.add(EntityCondition.makeCondition("partyId", EntityOperator.IN, listDistIds));
        }
    } else {
        isSearch = false;
    }
    List<GenericValue> listDitributor = delegator.findList("PartyDistributor", EntityCondition.makeCondition(conditions), null, null, null, false);

    if(UtilValidate.isNotEmpty(listDitributor)) {
        List<String> owners = EntityUtil.getFieldListFromEntityList(listDitributor, "partyId", false);
        List<EntityCondition> conds = FastList.newInstance();
        conds.add(EntityCondition.makeCondition("ownerPartyId", EntityJoinOperator.IN, owners))
        def dummy = delegator.findList("Facility", EntityCondition.makeCondition(conds),
                null, null, null, false);
        List<String> facilities = EntityUtil.getFieldListFromEntityList(dummy, "facilityId", false);
        System.out.println("facilities" + facilities)
        context.facilities = facilities
        def facilityJson = "[";
        flag = false;
        for (value in dummy) {
            if (flag) {
                facilityJson += ",";
            }
            facilityJson += "{facilityId:" + value.get("facilityId") + "}"
            flag = true;
        }
        facilityJson += "]"
        context.facilityJson = facilityJson
    }
}catch (Exception e){
    e.printStackTrace();
}