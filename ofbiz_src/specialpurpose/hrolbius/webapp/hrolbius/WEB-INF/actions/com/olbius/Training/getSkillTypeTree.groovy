import javolution.util.FastList;

import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityUtil;

import com.olbius.training.JqxTreeJson;


public static List<JqxTreeJson> getSkillTypeTree(List<GenericValue> skillTypeList){
	List<JqxTreeJson> retList = FastList.newInstance();
	for(GenericValue tempGv: skillTypeList){
		String skillTypeId = tempGv.getString("skillTypeId");
		String description = tempGv.getString("description");
		if(description == null){
			description = skillTypeId;
		} 
		String parentTypeId = tempGv.getString("parentTypeId");
		if(parentTypeId == null){
			parentTypeId = "-1";
		}
		//println ("parentTypeId:" +parentTypeId);
		retList.add(new JqxTreeJson(skillTypeId, description, parentTypeId))
	}
	return retList;																 
																 
}


context.listSkillTypeTree = getSkillTypeTree(skillTypeList);