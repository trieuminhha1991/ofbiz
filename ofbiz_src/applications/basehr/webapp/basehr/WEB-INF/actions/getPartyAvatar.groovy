import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.GenericValue;


/*<entity-and list="partyContentList" entity-name="PartyContent">
<field-map field-name="partyId" from-field="parameters.partyId" />
<field-map field-name="partyContentTypeId" value="LGOIMGURL" />
<order-by field-name="-fromDate" />
</entity-and>

<set field="partyContentId" from-field="partyContentList[0].contentId" />

<set field="personalImage" from-field="partyContent" />
*/
List<GenericValue> partyContentList = delegator.findByAnd("PartyContent", UtilMisc.toMap("partyId", userLogin.partyId, "partyContentTypeId", "LGOIMGURL"), UtilMisc.toList("-fromDate"), false);
if(UtilValidate.isNotEmpty(partyContentList)){
	GenericValue partyContent = partyContentList.get(0);
	String contentId = partyContent.getString("contentId");
	GenericValue content = delegator.findOne("Content", UtilMisc.toMap("contentId", contentId), false);
	String dataResourceId = content.getString("dataResourceId");
	GenericValue dataResource = delegator.findOne("DataResource", UtilMisc.toMap("dataResourceId", dataResourceId), false);
	String objectInfo = dataResource.getString("objectInfo");
	context.personalImage = objectInfo;
}