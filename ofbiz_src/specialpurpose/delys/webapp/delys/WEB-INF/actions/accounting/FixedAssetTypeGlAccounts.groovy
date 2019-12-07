import javolution.util.FastList;

import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;

List<EntityCondition> listAssetGlAccountCond = FastList.newInstance();
listAssetGlAccountCond.add(EntityCondition.makeCondition("organizationPartyId", EntityOperator.EQUALS, organizationPartyId));
listAssetGlAccountCond.add(EntityCondition.makeCondition("glAccountClassId", EntityOperator.EQUALS, "LONGTERM_ASSET"));
EntityCondition assetGlAccountCond = EntityCondition.makeCondition(listAssetGlAccountCond, EntityOperator.AND);
List<GenericValue> listAssetGlAccount = delegator.findList("GlAccountOrganizationAndClass", assetGlAccountCond, null, UtilMisc.toList("accountCode DESC"), null, false);


List<EntityCondition> listAccDepGlAccountCond = FastList.newInstance();
listAccDepGlAccountCond.add(EntityCondition.makeCondition("organizationPartyId", EntityOperator.EQUALS, organizationPartyId));
List<EntityCondition> listAccDepGlAccountOr = FastList.newInstance();
listAccDepGlAccountOr.add(EntityCondition.makeCondition("glAccountClassId", EntityOperator.EQUALS, "ACCUM_DEPRECIATION"));
listAccDepGlAccountOr.add(EntityCondition.makeCondition("glAccountClassId", EntityOperator.EQUALS, "ACCUM_AMORTIZATION"));
EntityCondition accDepGlAccountOr = EntityCondition.makeCondition(listAccDepGlAccountOr, EntityOperator.OR);
listAccDepGlAccountCond.add(accDepGlAccountOr);
EntityCondition accDepGlAccountCond = EntityCondition.makeCondition(listAccDepGlAccountCond, EntityOperator.AND);
List<GenericValue> listAccDepGlAccount = delegator.findList("GlAccountOrganizationAndClass", accDepGlAccountCond, null, UtilMisc.toList("accountCode DESC"), null, false);


List<EntityCondition> listDepGlAccountCond = FastList.newInstance();
listDepGlAccountCond.add(EntityCondition.makeCondition("organizationPartyId", EntityOperator.EQUALS, organizationPartyId));
List<EntityCondition> listDepGlAccountOr = FastList.newInstance();
listDepGlAccountOr.add(EntityCondition.makeCondition("glAccountClassId", EntityOperator.EQUALS, "DEPRECIATION"));
listDepGlAccountOr.add(EntityCondition.makeCondition("glAccountClassId", EntityOperator.EQUALS, "AMORTIZATION"));
EntityCondition depGlAccountOr = EntityCondition.makeCondition(listDepGlAccountOr, EntityOperator.OR);
listDepGlAccountCond.add(depGlAccountOr);
EntityCondition depGlAccountCond = EntityCondition.makeCondition(listDepGlAccountCond, EntityOperator.AND);
List<GenericValue> listDepGlAccount = delegator.findList("GlAccountOrganizationAndClass", depGlAccountCond, null, UtilMisc.toList("accountCode DESC"), null, false);

context.listAssetGlAccount  = listAssetGlAccount;
context.listAccDepGlAccount  = listAccDepGlAccount;
context.listDepGlAccount  = listDepGlAccount;