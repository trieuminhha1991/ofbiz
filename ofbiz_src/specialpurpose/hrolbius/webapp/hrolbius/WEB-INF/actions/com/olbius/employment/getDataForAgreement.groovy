import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.base.util.*;
import org.ofbiz.product.catalog.*;
import org.ofbiz.product.category.*;

import javolution.util.FastMap;
import javolution.util.FastList;
import javolution.util.FastList.*;

import org.ofbiz.entity.*;

import com.olbius.util.*;

import java.util.List;
import java.util.Calendar;

agreementId = parameters.agreementId;
agreement = delegator.findOne("Agreement", UtilMisc.toMap("agreementId", agreementId), false);

Calendar cal = Calendar.getInstance();

partyIdFrom = agreement.getString("partyIdFrom");
partyIdTo = agreement.getString("partyIdTo");
fromDate = agreement.getTimestamp("fromDate");
if(fromDate != null){
	cal.setTime(fromDate);
	fromDateStr = cal.get(Calendar.DATE) + "/" + (cal.get(Calendar.MONTH) + 1) + "/" + cal.get(Calendar.YEAR);
}

thruDate = agreement.getTimestamp("thruDate");
if(thruDate != null){
	cal.setTime(thruDate);
	thruDateStr = cal.get(Calendar.DATE) + "/" + (cal.get(Calendar.MONTH) + 1) + "/" + cal.get(Calendar.YEAR);
}
roleTypeIdFrom = agreement.getString("roleTypeIdFrom");

agreementAttr = delegator.findOne("AgreementAttribute", UtilMisc.toMap("agreementId", agreementId, "attrName", "representFor"), false);
agreementTypeId = agreement.getString("agreementTypeId");
agreementType = delegator.findOne("AgreementType", UtilMisc.toMap("agreementTypeId", agreementTypeId), false);

partyNameFrom = PartyHelper.getPartyName(delegator, partyIdFrom, true, true);
partyNameTo = PartyHelper.getPartyName(delegator, partyIdTo, true, true);
partyNameRep = PartyHelper.getPartyName(delegator, agreementAttr.getString("attrValue"), true, true);
partyNationalityFrom = PersonHelper.getNationality(partyIdFrom, delegator);
partyNationalityTo = PersonHelper.getNationality(partyIdTo, delegator);
birthDate = PersonHelper.getBirthDate(partyIdTo, delegator);
idIssuePlace = PersonHelper.getIDIssuePlace(partyIdTo, delegator);
idIssueDate = PersonHelper.getIDIssueDate(partyIdTo, delegator);
idNumber = PersonHelper.getIDNumber(partyIdTo, delegator);
addressRep = PartyHelper.getPartyPostalAddress(agreementAttr.getString("attrValue"), "PRIMARY_LOCATION", delegator);
partyAddressTo = PartyHelper.getPartyPostalAddress(partyIdTo, "PERMANENT_RESIDENCE", delegator);
telephoneRep = PartyHelper.getPartyPostalAddress(agreementAttr.getString("attrValue"), "PRIMARY_PHONE", delegator);
roleTypeFrom = delegator.findOne("RoleType", UtilMisc.toMap("roleTypeId", roleTypeIdFrom), true);

agreementPosTermList = delegator.findByAnd("AgreementTerm", [agreementId : agreementId, termTypeId: "JOB_POSITION_TERM"],null,false);
emplPositionType = delegator.findOne("EmplPositionType", UtilMisc.toMap("emplPositionTypeId", agreementPosTermList.get(0).getString("textValue")), false);

agreementWorkTermList = delegator.findByAnd("AgreementTerm", [agreementId : agreementId, termTypeId: "WORK_TERM"],null,false);
partyWork = PartyHelper.getPartyName(delegator, agreementWorkTermList.get(0).getString("textValue"), true, true);

agreementSalaryTermList = delegator.findByAnd("AgreementTerm", [agreementId : agreementId, termTypeId: "SALARY_TERM"],null,false);

context.partyNameFrom = partyNameFrom;
context.roleTypeNameFrom = roleTypeFrom.getString("description");
context.nationality = partyNationalityFrom;
context.partyNameRep = partyNameRep;
context.addressRep = addressRep;
context.telephoneRep = telephoneRep;
context.partyNameTo = partyNameTo;
context.partyAddressTo = partyAddressTo;
context.partyNationalityTo = partyNationalityTo;
context.birthDate = birthDate;
context.idNumber = idNumber;
context.idIssueDate = idIssueDate;
context.idIssuePlace = idIssuePlace;
context.agreementType = agreementType.getString("description");
context.fromDate = fromDateStr;
context.thruDate = thruDateStr;
context.emplPositionType = emplPositionType.getString("description");
context.partyWork = partyWork;
context.salary = agreementSalaryTermList.get(0).getBigDecimal("termValue");