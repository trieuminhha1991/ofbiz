import org.ofbiz.security.Security;
import com.olbius.services.DelysServices;

if (security.hasPermission("DELYS_CSM_CREATE", session) || security.hasPermission("DELYS_CSM_UPDATE", session) || security.hasPermission("DELYS_CSM_DELETE", session)){
	context.role = "DELYS_ADMIN";
	context.roleTypeId = "DELYS_ADMIN";
} else {
	if (security.hasPermission("DELYS_RSM_CREATE", session) || security.hasPermission("DELYS_RSM_UPDATE", session) || security.hasPermission("DELYS_RSM_DELETE", session)){
		context.role = "DELYS_CSM";
		context.roleTypeId = "DELYS_CSM";
	} else {
		if (security.hasPermission("DELYS_ASM_CREATE", session) || security.hasPermission("DELYS_ASM_UPDATE", session) || security.hasPermission("DELYS_ASM_DELETE", session)) {
			context.role = "DELYS_RSM";
			context.roleTypeId = "DELYS_RSM";
		} else {
			if (security.hasPermission("DELYS_ROUTE_CREATE", session) || security.hasPermission("DELYS_ROUTE_UPDATE", session) || security.hasPermission("DELYS_ROUTE_DELETE", session)){
				context.role = "DELYS_ASM";
				context.roleTypeId = "DELYS_ASM";	
			} else {
				context.role = "DELYS_CUSTOMER";
				context.roleTypeId = "DELYS_CUSTOMER";	
			}
		}
	}
}

if (security.hasPermission("DELYS_CSM_UPDATE", session) || security.hasPermission("DELYS_CSM_DELETE", session) 
|| security.hasPermission("DELYS_CSM_VIEW", session) || security.hasPermission("DELYS_CSM_CREATE", session)) { 
	hasCSMPermission = true;
	context.hasCSMPermission = hasCSMPermission;
} else {
	hasCSMPermission = false;
	context.hasCSMPermission = hasCSMPermission;
}


if (security.hasPermission("DELYS_CUSTOMER_UPDATE", session) || security.hasPermission("DELYS_CUSTOMER_DELETE", session) 
|| security.hasPermission("DELYS_CUSTOMER_VIEW", session) || security.hasPermission("DELYS_CUSTOMER_CREATE", session)) { 
	hasCUSTOMERPermission = true;
	context.hasCUSTOMERPermission = hasCUSTOMERPermission;
} else {
	hasCUSTOMERPermission = false;
	context.hasCUSTOMERPermission = hasCUSTOMERPermission;
}


if (security.hasPermission("DELYS_ASM_UPDATE", session) || security.hasPermission("DELYS_ASM_DELETE", session) 
|| security.hasPermission("DELYS_ASM_VIEW", session) || security.hasPermission("DELYS_ASM_CREATE", session)) { 
	hasASMPermission = true;
	context.hasASMPermission = hasASMPermission;
} else {
	hasASMPermission = false;
	context.hasASMPermission = hasASMPermission;
}


if (security.hasPermission("DELYS_RSM_UPDATE", session) || security.hasPermission("DELYS_RSM_DELETE", session) 
|| security.hasPermission("DELYS_RSM_VIEW", session) || security.hasPermission("DELYS_RSM_CREATE", session)) { 
	hasRSMPermission = true;
	context.hasRSMPermission = hasRSMPermission;
} else {
	hasRSMPermission = false;
	context.hasRSMPermission = hasRSMPermission;
}

if (security.hasPermission("DELYS_ROUTE_UPDATE", session) || security.hasPermission("DELYS_ROUTE_DELETE", session) 
|| security.hasPermission("DELYS_ROUTE_VIEW", session) || security.hasPermission("DELYS_ROUTE_CREATE", session)) { 
	hasROUTEPermission = true;
	context.hasROUTEPermission = hasROUTEPermission;
} else {
	hasROUTEPermission = false;
	context.hasROUTEPermission = hasROUTEPermission;
}

