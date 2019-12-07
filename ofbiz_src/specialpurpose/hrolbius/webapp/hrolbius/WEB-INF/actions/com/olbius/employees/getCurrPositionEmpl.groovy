import java.util.List;

import javolution.util.FastList;

import org.apache.commons.lang.StringUtils;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.util.EntityUtil;

import com.olbius.util.PartyUtil;

//println ("parameters.partyId: " + parameters.partyId);
if(emplId){
	//partyId = parameters.partyId; 
	//List<GenericValue> currPositionTypeIds = PartyUtil.getCurrPositionTypeOfEmpl(delegator, partyId);
	partyId = emplId;
	List<GenericValue> emplPosition = PartyUtil.getCurrPositionTypeOfEmpl(delegator, partyId);
	context.currPositionTypeIds = emplPosition;
	
	//println ("emplPosition: " + emplPosition);
	List<String> emplPos = FastList.newInstance();
	for(GenericValue tempPos: emplPosition){
		GenericValue emplType = delegator.findOne("EmplPositionType", UtilMisc.toMap("emplPositionTypeId", tempPos.getString("emplPositionTypeId")), false);
		emplPos.add(emplType.getString("description"));
	}
	//println ("emplPos: " + emplPos);
	context.currPositionsStr = StringUtils.join(emplPos, ", ");
}

