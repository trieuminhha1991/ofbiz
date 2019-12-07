roleTypeId = parameters.roleTypeId ?: parameters.roleTypeId;


	if (roleTypeId.equals("DELYS_ADMIN")){
		tabButtonItem = "NewCSM";
	} else {
		if (roleTypeId.equals("DELYS_CSM")) {
			tabButtonItem = "NewRSM";
		} else {
			if (roleTypeId.equals("DELYS_RSM")) {
				tabButtonItem = "NewASM";
			} else {
				if (roleTypeId.equals("DELYS_ASM")) {
					tabButtonItem = "NewROUTE";
				} else {
					if (roleTypeId.equals("DELYS_CUSTOMER")) {
						tabButtonItem = "NewDelysCUSTOMER";
					} else {
						tabButtonItem = "null";
					}
				}
			}		
		}
	}

context.tabButtonItem = tabButtonItem;
context.roleTypeId = roleTypeId;

if (security.hasPermission("DELYS_CSM_CREATE", session)) {
	hasCreatedCSM = true;
	context.hasCreatedCSM = hasCreatedCSM;
} else {
	hasCreatedCSM = false;
	context.hasCreatedCSM = hasCreatedCSM;
}
if (security.hasPermission("DELYS_RSM_CREATE", session)) {
	hasCreatedRSM = true;
	context.hasCreatedRSM = hasCreatedRSM;
} else {
	hasCreatedRSM = false;
	context.hasCreatedRSM = hasCreatedRSM;
}
if (security.hasPermission("DELYS_ASM_CREATE", session)) {
	hasCreatedASM = true;
	context.hasCreatedASM = hasCreatedASM;
} else {
	hasCreatedASM = false;
	context.hasCreatedASM = hasCreatedASM;
}
if (security.hasPermission("DELYS_ROUTE_CREATE", session)) {
	hasCreatedROUTE = true;
	context.hasCreatedROUTE = hasCreatedROUTE;
} else {
	hasCreatedROUTE = false;
	context.hasCreatedROUTE = hasCreatedROUTE;
}
if (security.hasPermission("DELYS_CUSTOMER_CREATE", session)) {
	hasCreatedCUSTOMER = true;
	context.hasCreatedCUSTOMER = hasCreatedCUSTOMER;
} else {
	hasCreatedCUSTOMER = false;
	context.hasCreatedCUSTOMER = hasCreatedCUSTOMER;
}

if (security.hasPermission("DELYS_CSM_DELETE", session)) {
	hasDeletedCSM = true;
	context.hasDeletedCSM = hasDeletedCSM;
} else {
	hasDeletedCSM = false;
	context.hasDeletedCSM = hasDeletedCSM;
}
if (security.hasPermission("DELYS_RSM_DELETE", session)) {
	hasDeletedRSM = true;
	context.hasDeletedRSM = hasDeletedRSM;
} else {
	hasDeletedRSM = false;
	context.hasDeletedRSM = hasDeletedRSM;
}
if (security.hasPermission("DELYS_ASM_DELETE", session)) {
	hasDeletedASM = true;
	context.hasDeletedASM = hasDeletedASM;
} else {
	hasDeletedASM = false;
	context.hasDeletedASM = hasDeletedASM;
}
if (security.hasPermission("DELYS_ROUTE_DELETE", session)) {
	hasDeletedROUTE = true;
	context.hasDeletedROUTE = hasDeletedROUTE;
} else {
	hasDeletedROUTE = false;
	context.hasDeletedROUTE = hasDeletedROUTE;
}
if (security.hasPermission("DELYS_CUSTOMER_DELETE", session)) {
	hasDeletedCUSTOMER = true;
	context.hasDeletedCUSTOMER = hasDeletedCUSTOMER;
} else {
	hasDeletedCUSTOMER = false;
	context.hasDeletedCUSTOMER = hasDeletedCUSTOMER;
}

if (security.hasPermission("DELYS_CSM_UPDATE", session)) {
	hasUpdatedCSM = true;
	context.hasUpdatedCSM = hasUpdatedCSM;
} else {
	hasUpdatedCSM = false;
	context.hasUpdatedCSM = hasUpdatedCSM;
}
if (security.hasPermission("DELYS_RSM_UPDATE", session)) {
	hasUpdatedRSM = true;
	context.hasUpdatedRSM = hasUpdatedRSM;
} else {
	hasUpdatedRSM = false;
	context.hasUpdatedRSM = hasUpdatedRSM;
}
if (security.hasPermission("DELYS_ASM_UPDATE", session)) {
	hasUpdatedASM = true;
	context.hasUpdatedASM = hasUpdatedASM;
} else {
	hasUpdatedASM = false;
	context.hasUpdatedASM = hasUpdatedASM;
}
if (security.hasPermission("DELYS_ROUTE_UPDATE", session)) {
	hasUpdatedROUTE = true;
	context.hasUpdatedROUTE = hasUpdatedROUTE;
} else {
	hasUpdatedROUTE = false;
	context.hasUpdatedROUTE = hasUpdatedROUTE;
}
if (security.hasPermission("DELYS_CUSTOMER_UPDATE", session)) {
	hasUpdatedCUSTOMER = true;
	context.hasUpdatedCUSTOMER = hasUpdatedCUSTOMER;
} else {
	hasUpdatedCUSTOMER = false;
	context.hasUpdatedCUSTOMER = hasUpdatedCUSTOMER;
}

if (security.hasPermission("DELYS_CSM_VIEW", session)) {
	hasViewedCSM = true;
	context.hasViewedCSM = hasViewedCSM;
} else {
	hasViewedCSM = false;
	context.hasViewedCSM = hasViewedCSM;
}
if (security.hasPermission("DELYS_RSM_VIEW", session)) {
	hasViewedRSM = true;
	context.hasViewedRSM = hasViewedRSM;
} else {
	hasViewedRSM = false;
	context.hasViewedRSM = hasViewedRSM;
}
if (security.hasPermission("DELYS_ASM_VIEW", session)) {
	hasViewedASM = true;
	context.hasViewedASM = hasViewedASM;
} else {
	hasViewedASM = false;
	context.hasViewedASM = hasViewedASM;
}
if (security.hasPermission("DELYS_ROUTE_VIEW", session)) {
	hasViewedROUTE = true;
	context.hasViewedROUTE = hasViewedROUTE;
} else {
	hasViewedROUTE = false;
	context.hasViewedROUTE = hasViewedROUTE;
}
if (security.hasPermission("DELYS_CUSTOMER_VIEW", session)) {
	hasViewedCUSTOMER = true;
	context.hasViewedCUSTOMER = hasViewedCUSTOMER;
} else {
	hasViewedCUSTOMER = false;
	context.hasViewedCUSTOMER = hasViewedCUSTOMER;
}


if (security.hasPermission("DELYS_CSM_CREATE", session) || security.hasPermission("DELYS_RSM_CREATE", session) 
|| security.hasPermission("DELYS_ASM_CREATE", session) || security.hasPermission("DELYS_ROUTE_CREATE", session) ||
security.hasPermission("DELYS_CUSTOMER_CREATE", session) ) {
	hasCreatedPermission = true;
	context.hasCreatedPermission = hasCreatedPermission;
} else {
	hasCreatedPermission = false;
	context.hasCreatedPermission = hasCreatedPermission;
}



if (security.hasPermission("DELYS_CSM_VIEW", session) || security.hasPermission("DELYS_RSM_VIEW", session) 
|| security.hasPermission("DELYS_ASM_VIEW", session) || security.hasPermission("DELYS_ROUTE_VIEW", session) ||
security.hasPermission("DELYS_CUSTOMER_VIEW", session) ) {
	hasViewPermission = true;
	context.hasViewPermission = hasViewPermission;
} else {
	hasViewPermission = false;
	context.hasViewPermission = hasViewPermission;
}
if (security.hasPermission("DELYS_CSM_DELETE", session) || security.hasPermission("DELYS_RSM_DELETE", session) 
|| security.hasPermission("DELYS_ASM_DELETE", session) || security.hasPermission("DELYS_ROUTE_DELETE", session) ||
security.hasPermission("DELYS_CUSTOMER_DELETE", session) ) {
	hasDeletedPermission = true;
	context.hasDeletedPermission = hasDeletedPermission;
} else {
	hasDeletedPermission = false;
	context.hasDeletedPermission = hasDeletedPermission;
}
if (security.hasPermission("DELYS_CSM_UPDATE", session) || security.hasPermission("DELYS_RSM_UPDATE", session) 
|| security.hasPermission("DELYS_ASM_UPDATE", session) || security.hasPermission("DELYS_ROUTE_UPDATE", session) ||
security.hasPermission("DELYS_CUSTOMER_UPDATE", session) ) { 
	hasUpdatedPermission = true;
	context.hasUPDATEdPermission = hasUpdatedPermission;
} else {
	hasUpdatedPermission = false;
	context.hasUPDATEdPermission = hasUpdatedPermission;
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


