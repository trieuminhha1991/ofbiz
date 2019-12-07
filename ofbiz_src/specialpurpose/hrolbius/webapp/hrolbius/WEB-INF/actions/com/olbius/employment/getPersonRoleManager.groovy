import org.ofbiz.base.util.UtilMisc;

parameters.roleTypeIdFrom = 'MANAGER';

Map<String, Object> results = dispatcher.runSync("performFind", UtilMisc.toMap("entityName", "PartyRelationshipFromAndPerson", 
																				"inputFields", parameters, "orderBy", "partyId",
																				"filterByDate", "Y",
																				"noConditionFind", "Y"));
context.listIt = results.listIt;																			