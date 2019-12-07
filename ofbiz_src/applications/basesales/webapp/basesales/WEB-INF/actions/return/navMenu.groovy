import com.olbius.security.util.SecurityUtil;

if(com.olbius.basehr.util.SecurityUtil.hasRole("ACC_EMPLOYEE", userLogin.getString("partyId"), delegator)) {
    context.selectedMenuItem = "returnOrder";
    context.selectedSubMenuItem = "returnOrderList";
}
else if(com.olbius.basehr.util.SecurityUtil.hasRole("LOG_EMPLOYEE", userLogin.getString("partyId"), delegator)) {
    if ("VENDOR_RETURN".equals(returnHeader.getString("returnHeaderTypeId"))){
        context.selectedSubMenuItem = "ListSupReturn";
    } else {
        context.selectedSubMenuItem = "ListCusReturn";
    }
    context.selectedMenuItem = "Return";
}

else {
    context.selectedMenuItem = "returnOrder";
    context.selectedSubMenuItem = "returnOrderList";
}