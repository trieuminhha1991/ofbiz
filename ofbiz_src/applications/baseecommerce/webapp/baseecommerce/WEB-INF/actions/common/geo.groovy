import java.util.*;
import java.lang.*;
import org.ofbiz.entity.*;
import org.ofbiz.entity.condition.*;
import org.ofbiz.base.util.*;
import org.ofbiz.base.util.UtilMisc;

country = delegator.findList("Geo", EntityCondition.makeCondition("geoTypeId", "COUNTRY"), null, UtilMisc.toList("+geoName"), null, false);

context.country = country;
province = delegator.findList("GeoAssocAndGeoTo", EntityCondition.makeCondition(UtilMisc.toList(
								EntityCondition.makeCondition([geoIdFrom : "VNM"]),
								EntityCondition.makeCondition([geoTypeId : "PROVINCE"])
							)), null, null, null, false);
def defaultCityId = "";
if(context.stateProvinceGeoId) {
	defaultCityId = stateProvinceGeoId;
}
if(!defaultCityId) {
	defaultCityId = "VNM-HN2";
	stateProvinceGeoId = "VNM-HN2";
	context.stateProvinceGeoId = stateProvinceGeoId;
}

defaultDistrict = delegator.findList("GeoAssocAndGeoTo", EntityCondition.makeCondition([geoIdFrom : defaultCityId]), null, null, null, false);

context.province = province;
context.defaultDistrict = defaultDistrict;
context.defaultCityId = defaultCityId;