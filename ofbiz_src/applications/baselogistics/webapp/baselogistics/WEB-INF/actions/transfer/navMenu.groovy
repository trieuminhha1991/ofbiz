import com.olbius.basehr.util.SecurityUtil;

Boolean accEmpl = SecurityUtil.hasRole("ACC_EMPLOYEE", userLogin.getString("partyId"), delegator);

if (accEmpl) { 
	parameters.selectedMenuItem = "accApprovement";
	parameters.selectedSubMenuItem = "accTransfer";
} else { 
	parameters.selectedMenuItem = "Transfer";
	parameters.selectedSubMenuItem = "ListTransfer";
}