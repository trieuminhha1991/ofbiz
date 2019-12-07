 <#assign orderCancelReason = delegator.findByAnd("Enumeration", {"enumTypeId" : "ORDER_CANCEL_CODE"}, null, false)!/>
 <script type="text/javascript">
 <#assign shippingTripId = parameters.shippingTripId?if_exists/>
 <#assign trip = delegator.findOne("ShippingTrip", {"shippingTripId" : shippingTripId?if_exists}, false)/>
 <#assign partyName = delegator.findOne("PartyNameView", {"partyId" : trip.shipperId?if_exists}, false)/>
 var tripStatus = "${trip.statusId?if_exists}";
 var row = {};
 row['lastName'] = "${StringUtil.wrapString(partyName.lastName?if_exists)}";
 row['middleName'] = "${StringUtil.wrapString(partyName.middleName?if_exists)}";
 row['firstName'] = "${StringUtil.wrapString(partyName.firstName?if_exists)}";
 var fullName = null;
 if (row.lastName){
   if (fullName){
     fullName = fullName + ' ' + row.lastName;
   } else {
     fullName = row.lastName;
   }
 }
 if (row.middleName){
   if (fullName){
     fullName = fullName + ' ' + row.middleName;
   } else {
     fullName = row.middleName;
   }
 }
 if (row.firstName){
   if (fullName){
     fullName = fullName + ' ' + row.firstName;
   } else {
     fullName = row.firstName;
   }
 }
 if (uiLabelMap == undefined) var uiLabelMap = {};
 uiLabelMap.AreYouSureApprove = "${uiLabelMap.AreYouSureApprove}";
 uiLabelMap.PleaseSelectTitle = "${StringUtil.wrapString(uiLabelMap.PleaseSelectTitle)}";
 uiLabelMap.AreYouSureCreate = "${StringUtil.wrapString(uiLabelMap.AreYouSureCreate)}";
uiLabelMap.AreYouSureExport = "${StringUtil.wrapString(uiLabelMap.AreYouSureExport)}";
 uiLabelMap.AreYouSureSave = "${StringUtil.wrapString(uiLabelMap.AreYouSureSave)}";
 uiLabelMap.CommonSave = "${StringUtil.wrapString(uiLabelMap.CommonSave)}";
 uiLabelMap.CommonCancel = "${StringUtil.wrapString(uiLabelMap.CommonCancel)}";
 uiLabelMap.BLDErrorExportShippingTrip = "${StringUtil.wrapString(uiLabelMap.BLDErrorExportShippingTrip)}";
 uiLabelMap.OK = "${StringUtil.wrapString(uiLabelMap.OK)}";
 uiLabelMap.AreYouSureCancel = "${StringUtil.wrapString(uiLabelMap.AreYouSureCancel)}";
 uiLabelMap.AreYouSureReject = "${StringUtil.wrapString(uiLabelMap.AreYouSureReject)}";

 </script>
 <script type="text/javascript" src="/logresources/js/trip/tripDetailBegin.js"></script>
 <@jqOlbCoreLib hasCore=false hasValidator=true/>
