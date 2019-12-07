package com.olbius.baseecommerce.resources;

import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;

import javolution.util.FastList;


public class VisualThemeUtils {

    public final static String module = VisualThemeUtils.class.getName();
    public static final String resource = "CommonUiLabels";

    public static Map<String, Object> getVisualThemeResources(Delegator delegator, String visualThemeId, Map<String, Object> themeResources, Locale locale){
	try {
		List<String> filters = UtilMisc.toList("resourceTypeEnumId", "sequenceId");
			List<GenericValue> resources = delegator.findList("VisualThemeResource", EntityCondition.makeCondition("visualThemeId", visualThemeId), null, filters, null, true);
			if(UtilValidate.isEmpty(resources)){
				Debug.log("Could not find the '${visualThemeId}' theme, reverting back to the good old OFBiz theme...");
				resources = delegator.findList("VisualThemeResource", EntityCondition.makeCondition("visualThemeId", "FLAT_GREY"), null, filters, null, true);
			}
			if(UtilValidate.isEmpty(resources)){
				Debug.log(UtilProperties.getMessage(resource, "CommonVisualThemeResourcesNotFound", locale));
			}else{
				String cur = "";
				List<String> tmp = null;
				for(GenericValue e : resources){
					String resourceTypeEnumId = e.getString("resourceTypeEnumId");
					String resourceValue = e.getString("resourceValue");
					if(UtilValidate.isEmpty(cur) || !resourceTypeEnumId.equals(cur)){
						cur = resourceTypeEnumId;
						tmp = (List<String>) themeResources.get(cur);
						if(tmp == null){
							tmp = FastList.newInstance();
						}
						if(UtilValidate.isEmpty(resourceValue)){
							Debug.logWarning(UtilProperties.getMessage(resource, "CommonVisualThemeInvalidRecord", locale), module);
						}else{
							tmp.add(resourceValue);
							themeResources.put(resourceTypeEnumId, tmp);
						}
					}
				}
			}
		} catch (GenericEntityException e) {
			Debug.log(e.getMessage());
		}
	return themeResources;
    }
}
