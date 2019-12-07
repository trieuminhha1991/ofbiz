<@jqGridMinimumLib/>
<#include "component://widget/templates/jqwLocalization.ftl"/>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxexpander.js"></script>

<style>
#myImg { 
	position : fixed;
	left: 654px;
	top: 266px;
	visibility: hidden;
}
.bootbox{
    z-index: 990009 !important;
}
.modal-backdrop{
    z-index: 890009 !important;
}
.loading-container{
	z-index: 999999 !important;
}
</style>
<script>
	function getLocalizationTmp() {
	    var localizationobj = {};
	    localizationobj.pagergotopagestring = "${StringUtil.wrapString(uiLabelMap.wgpagergotopagestring)}:";
         localizationobj.pagershowrowsstring = "${StringUtil.wrapString(uiLabelMap.wgpagershowrowsstring)}:";
         localizationobj.pagerrangestring = " ${StringUtil.wrapString(uiLabelMap.wgpagerrangestring)} ";
         localizationobj.pagernextbuttonstring = "${StringUtil.wrapString(uiLabelMap.wgpagernextbuttonstring)}";
         localizationobj.pagerpreviousbuttonstring = "${StringUtil.wrapString(uiLabelMap.wgpagerpreviousbuttonstring)}";
         localizationobj.sortascendingstring = "${StringUtil.wrapString(uiLabelMap.wgsortascendingstring)}";
         localizationobj.sortdescendingstring = "${StringUtil.wrapString(uiLabelMap.wgsortdescendingstring)}";
         localizationobj.sortremovestring = "${StringUtil.wrapString(uiLabelMap.wgsortremovestring)}";
         localizationobj.emptydatastring = "${StringUtil.wrapString(uiLabelMap.wgemptydatastring)}";
         localizationobj.filterselectstring = "${StringUtil.wrapString(uiLabelMap.wgfilterselectstring)}";
         localizationobj.filterselectallstring = "${StringUtil.wrapString(uiLabelMap.wgfilterselectallstring)}";
         localizationobj.filterchoosestring = "${StringUtil.wrapString(uiLabelMap.filterchoosestring)}";
         localizationobj.groupsheaderstring = "${StringUtil.wrapString(uiLabelMap.wgdragDropToGroupColumn)}";
         localizationobj.notificationfiltershortkey = "${StringUtil.wrapString(uiLabelMap.filterDropDownGridDescription)}"
         localizationobj.todaystring = "${StringUtil.wrapString(uiLabelMap.wgtodaystring)}";
         localizationobj.clearstring = "${StringUtil.wrapString(uiLabelMap.wgclearstring)}";
	    return localizationobj;
		};
	
	var facilityId = '${parameters.facilityId}';
	var contactMechTypeData = new Array();
	<#list listContactMechType as contactMechType>
			var row = {};
			row['contactMechTypeId'] = "${contactMechType.contactMechTypeId}";
			row['description'] = "${contactMechType.get('description', locale)?if_exists}";
			contactMechTypeData[${contactMechType_index}] = row;
	</#list>
	
	<#assign geoList = delegator.findList("Geo", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("geoTypeId", "COUNTRY"), null, null, null, false) />
	var geoData = new Array();
	<#list geoList as geo>
		var row = {};
		row['geoId'] = "${geo.geoId}";
		row['geoName'] = "${geo.geoName?if_exists}";
		geoData[${geo_index}] = row;
	</#list>
	
	function getGeoId(geoId) {
		for ( var x in geoData) {
			if (geoId == geoData[x].geoId) {
				return geoData[x].geoName;
			}
		}
	}
	
	<#assign geoDataList = delegator.findList("Geo", null, null, null, null, false) />
	var geoDataByData = new Array();
	<#list geoDataList as geo>
		var row = {};
		row['geoId'] = "${geo.geoId}";
		row['geoName'] = "${geo.geoName?if_exists}";
		geoDataByData[${geo_index}] = row;
	</#list>
	
	function getGeoIdByGeoDistrict(geoId) {
		for ( var x in geoDataByData) {
			if (geoId == geoDataByData[x].geoId) {
				return geoDataByData[x].geoName;
			}
		}
	}
	
	<#assign contactMechPurposeTypeList = delegator.findList("ContactMechPurposeType", null, null, null, null, false) />
	var contactMechPurposeTypeData = new Array();
	<#list contactMechPurposeTypeList as contactMechPurposeType>
		var row = {};
		row['contactMechPurposeTypeId'] = "${contactMechPurposeType.contactMechPurposeTypeId}";
		row['description'] = "${StringUtil.wrapString(contactMechPurposeType.get('description', locale)?if_exists)}";
		contactMechPurposeTypeData[${contactMechPurposeType_index}] = row;
	</#list>
	function getContactMechPurposeTypeId(contactMechPurposeTypeId) {
		for ( var x in contactMechPurposeTypeData) {
			if (contactMechPurposeTypeId == contactMechPurposeTypeData[x].contactMechPurposeTypeId) {
				var value = contactMechPurposeTypeData[x].description;
				return value;
			}
		}
	}
	function getContactMechTypeId(contactMechTypeId) {
		for ( var x in contactMechTypeData) {
			if (contactMechTypeId == contactMechTypeData[x].contactMechTypeId) {
				return contactMechTypeData[x].description;
			}
		}
	}
	
</script>	



<div>
	<div id="contentNotificationContentCreatePostalAddressSuccess">
	</div>

	<#assign initrowdetailsDetail = "function (index, parentElement, gridElement, datarecord) {
		if(datarecord.rowDetail == null || datarecord.rowDetail.length < 1){
			return 'Not Data';
		}
		var ordersDataAdapter = new $.jqx.dataAdapter(datarecord.rowDetail, { autoBind: true });
	    var orders = ordersDataAdapter.records;
	    
			var nestedGrids = new Array();
	        var id = datarecord.uid.toString();
	        
	         var grid = $($(parentElement).children()[0]);
	         $(grid).attr(\"id\",\"jqxgridDetail\");
	         nestedGrids[index] = grid;
	       
	         var ordersbyid = [];
	        
	         for (var ii = 0; ii < orders.length; ii++) {
	                 ordersbyid.push(orders[ii]);
	         }
	         var contactMechTypeId = datarecord.contactMechTypeId;
	         var orderssource = { 
	        		 datafields: [	
     		         	 { name: \'contactMechId\', type:\'string\' },
     		         	 { name: \'contactMechPurpuseTypeId\', type:\'string\' },
     		             { name: \'infoString\', type:\'string\' }, 
     		         	 { name: \'address1\', type:\'string\' },
     		         	 { name: \'toName\', type:\'string\' },
     		         	 { name: \'countryCode\', type:\'string\' },
        		         { name: \'areaCode\', type:\'string\' },
     		         	 { name: \'contactNumber\', type:\'string\' },
     		         	 { name: \'attnName\', type:\'string\' },
        		         { name: \'address2\', type:\'string\' },
        		         { name: \'postalCode\', type:\'string\' },
        		         { name: \'countryGeoId\', type:\'string\' },
        		         { name: \'stateProvinceGeoId\', type:\'string\' },
        		         { name: \'extendsion\', type:\'string\' },
     	         	 ],
     	             localdata: ordersbyid
 	    	 };
    	 	    	     
	         var nestedGridAdapter = new $.jqx.dataAdapter(orderssource);
             if(contactMechTypeId == 'POSTAL_ADDRESS'){
            	 if (grid != null) {
    	             grid.jqxGrid({
    	                 source: nestedGridAdapter, 
    	                 width: '98%',
    	                 height: 210,
    	                 showtoolbar:true,
    			 		 editable:false,
    			 		 editmode:\"click\",
    			 		 theme: 'olbius',
    			 		 showheader: true,
    			 		 pagesize: 5,
    			 		 localization: getLocalizationTmp(),
    			 		 rendertoolbar: function (toolbar) {
    			 			var container = $(\"<div style='overflow: hidden; position: relative; margin: 5px;'></div>\");
    	                    var editButton = $(\"<div style='float: right;' class='btn btn-mini btn-primary icon-edit open-sans'>${uiLabelMap.Edit}</span></div>\");
    	                    var deleteButton = $(\"<div style='float: right; margin-right: 10px;' class='btn btn-mini btn-danger icon-trash open-sans'>${uiLabelMap.Delete}</span></div>\");
    	                    container.append(editButton);
    	                    container.append(deleteButton);
    	                    toolbar.append(container);
    	                    editButton.jqxButton();
    	                    deleteButton.jqxButton();
    	                    // edit row.
    	                    editButton.click(function (event) {
    	                    	var selectedrowindex = grid.jqxGrid('getselectedrowindex');
    	                        var rowscount = grid.jqxGrid('getdatainformation').rowscount;
    	                        var id = grid.jqxGrid('getrowid', selectedrowindex);
    	                        var data = grid.jqxGrid('getrowdatabyid', id);
    	                        if(id == null){
    	                        	bootbox.dialog(\"${uiLabelMap.SelectItemYouWantToEdit}!\", [{
    	                                \"label\" : \"OK\",
    	                                \"class\" : \"btn btn-primary standard-bootbox-bt\",
    	                                \"icon\" : \"fa fa-check\",
    	                                }]
    	                            );
    	                            return false;
    	                        }else{
    	                        	var valueContactMechId = data.contactMechId;
                                	var contactMechPurpuseTypeId = data.contactMechPurpuseTypeId;
    		                       	loadContactMechDetailByEdit(valueContactMechId, contactMechPurpuseTypeId);
        	                        grid.jqxGrid('clearselection');
    	                        }
    	                    });
    	                    // delete selected row.
    	                    deleteButton.click(function (event) {
    	                        var selectedrowindex = grid.jqxGrid('getselectedrowindex');
    	                        var rowscount = grid.jqxGrid('getdatainformation').rowscount;
    	                        var id = grid.jqxGrid('getrowid', selectedrowindex);
    	                        if(id == null){
    	                        	bootbox.dialog(\"${uiLabelMap.SelectItemYouWantToDelete}!\", [{
    	                                \"label\" : \"OK\",
    	                                \"class\" : \"btn btn-primary standard-bootbox-bt\",
    	                                \"icon\" : \"fa fa-check\",
    	                                }]
    	                            );
    	                            return false;
    	                        }else{
    	                        	var data = grid.jqxGrid('getrowdatabyid', id);
        	                        var valueContactMechId = data.contactMechId;
        	                        deleteContactMechInFacility(valueContactMechId);
        	                        grid.jqxGrid('clearselection');
    	                        }
    	                    });
    			 		 },
    			 		 selectionmode:\"singlerow\",
    			 		 pageable: true,
    			 		 columns: 
    			 	     [
    			 	      	{ text: \'${uiLabelMap.ContactPurpose}\', datafield: \'contactMechPurpuseTypeId\',editable: true,
    	                	   cellsrenderer: function(row, colum, value){
    	                		   var data = grid.jqxGrid('getrowdata', row);
    	       					   var value = data.contactMechPurpuseTypeId;
    	       					   var contactMechPurpuseTypeId = getContactMechPurposeTypeId(value);
    	       					   return '<span>' + contactMechPurpuseTypeId + '</span>';
    	       				   }, 
    	                    },
    	                    { text: \'${uiLabelMap.Address}\', datafield: \'address1\',editable: true},
    	                    { text: \'${uiLabelMap.National}\', datafield: \'countyGeoId\',editable: true,
    	                    	cellsrenderer: function(row, colum, value){
     	                		   var data = grid.jqxGrid('getrowdata', row);
     	       					   var countryGeoIdValue = data.countryGeoId;
     	       					   if(countryGeoIdValue != null){
     	       						   var countyGeoId = getGeoId(countryGeoIdValue);
     	       						   return '<span>' + countyGeoId + '</span>';
     	       					   }
     	       				   }, 
    	                    },
    	                    { text: \'${uiLabelMap.Provinces}\', datafield: \'stateProvinceGeoId\',editable: true,
    	                    	cellsrenderer: function(row, colum, value){
     	                		   var data = grid.jqxGrid('getrowdata', row);
     	       					   var stateProvinceGeoIdValue = data.stateProvinceGeoId;
     	       					   if(stateProvinceGeoIdValue != null){
     	       						   var stateProvinceGeoId = getGeoIdByGeoDistrict(stateProvinceGeoIdValue);
     	       						   return '<span>' + stateProvinceGeoId + '</span>';
     	       					   }
     	       				    }, 
    	                    },
    	                 ]
    	             });
    	         }
  	         }
     		 if(contactMechTypeId == 'EMAIL_ADDRESS'){
     			if (grid != null) {
		             grid.jqxGrid({
		                 source: nestedGridAdapter,
		                 width: '98%',
		                 height: 180,
		                 showtoolbar:true,
				 		 editable:false,
				 		 editmode:\"click\",
				 		 theme: 'olbius',
				 		 showheader: true,
				 		 localization: getLocalizationTmp(),
				 		 rendertoolbar: function (toolbar) {
				 			var container = $(\"<div style='overflow: hidden; position: relative; margin: 5px;'></div>\");
		                    var editButton = $(\"<div style='float: right;' class='btn btn-mini btn-primary icon-edit open-sans'>${uiLabelMap.Edit}</span></div>\");
		                    var deleteButton = $(\"<div style='float: right; margin-right: 10px;' class='btn btn-mini btn-danger icon-trash open-sans'>${uiLabelMap.Delete}</span></div>\");
		                    container.append(editButton);
		                    container.append(deleteButton);
		                    toolbar.append(container);
		                    editButton.jqxButton();
		                    deleteButton.jqxButton();
		                    // edit row.
		                    editButton.click(function (event) {
		                    	var selectedrowindex = grid.jqxGrid('getselectedrowindex');
		                        var rowscount = grid.jqxGrid('getdatainformation').rowscount;
		                        var id = grid.jqxGrid('getrowid', selectedrowindex);
		                        if(id == null){
    	                        	bootbox.dialog(\"${uiLabelMap.SelectItemYouWantToEdit}!\", [{
    	                                \"label\" : \"OK\",
    	                                \"class\" : \"btn btn-primary standard-bootbox-bt\",
    	                                \"icon\" : \"fa fa-check\",
    	                                }]
    	                            );
    	                            return false;
    	                        }else{
    	                        	var data = grid.jqxGrid('getrowdatabyid', id);
    		                        var valueContactMechId = data.contactMechId; 
    		                        var contactMechPurpuseTypeId = data.contactMechPurpuseTypeId;
    		                       	loadContactMechDetailByEdit(valueContactMechId, contactMechPurpuseTypeId);
    		                        grid.jqxGrid('clearselection');
    	                        }
		                    });
		                    // delete selected row.
		                    deleteButton.click(function (event) {
		                        var selectedrowindex = grid.jqxGrid('getselectedrowindex');
		                        var rowscount = grid.jqxGrid('getdatainformation').rowscount;
		                        var id = grid.jqxGrid('getrowid', selectedrowindex);
		                        if(id == null){
    	                        	bootbox.dialog(\"${uiLabelMap.SelectItemYouWantToDelete}!\", [{
    	                                \"label\" : \"OK\",
    	                                \"class\" : \"btn btn-primary standard-bootbox-bt\",
    	                                \"icon\" : \"fa fa-check\",
    	                                }]
    	                            );
    	                            return false;
    	                        }else{
    	                        	var data = grid.jqxGrid('getrowdatabyid', id);
    		                        var valueContactMechId = data.contactMechId;
    		                        deleteContactMechInFacility(valueContactMechId);
    		                        grid.jqxGrid('clearselection');
    	                        }
		                    });
				 		 },
				 		 selectionmode:\"singlerow\",
				 		 pageable: true,
				 		 columns: 
				 	     [
				 	      	
				 	      	{ text: \'${uiLabelMap.ContactPurpose}\', datafield: \'contactMechPurpuseTypeId\',editable: true,
		                	   cellsrenderer: function(row, colum, value){
		                		   var data = grid.jqxGrid('getrowdata', row);
		       					   var value = data.contactMechPurpuseTypeId;
		       					   var contactMechPurpuseTypeId = getContactMechPurposeTypeId(value);
		       					   return '<span>' + contactMechPurpuseTypeId + '</span>';
		       				   }, 
		                    },
		                    { text: \'${uiLabelMap.Email}\', datafield: \'infoString\',editable: true},
		                 ]
		             });
   	             }
     		 }
     		 if(contactMechTypeId == 'TELECOM_NUMBER'){
     			if (grid != null) {
		             grid.jqxGrid({
		                 source: nestedGridAdapter, 
		                 width: '98%',
		                 height: 180,
		                 showtoolbar:true,
				 		 editable:false,
				 		 editmode:\"click\",
				 		 theme: 'olbius',
				 		 showheader: true,
				 		 localization: getLocalizationTmp(),
				 		 rendertoolbar: function (toolbar) {
				 			var container = $(\"<div style='overflow: hidden; position: relative; margin: 5px;'></div>\");
		                    var editButton = $(\"<div style='float: right;' class='btn btn-mini btn-primary icon-edit open-sans'>${uiLabelMap.Edit}</span></div>\");
		                    var deleteButton = $(\"<div style='float: right; margin-right: 10px;' class='btn btn-mini btn-danger icon-trash open-sans'>${uiLabelMap.Delete}</span></div>\");
		                    container.append(editButton);
		                    container.append(deleteButton);
		                    toolbar.append(container);
		                    editButton.jqxButton();
		                    deleteButton.jqxButton();
		                    // edit row.
		                    editButton.click(function (event) {
		                    	var selectedrowindex = grid.jqxGrid('getselectedrowindex');
		                        var rowscount = grid.jqxGrid('getdatainformation').rowscount;
		                        var id = grid.jqxGrid('getrowid', selectedrowindex);
		                        if(id == null){
    	                        	bootbox.dialog(\"${uiLabelMap.SelectItemYouWantToEdit}!\", [{
    	                                \"label\" : \"OK\",
    	                                \"class\" : \"btn btn-primary standard-bootbox-bt\",
    	                                \"icon\" : \"fa fa-check\",
    	                                }]
    	                            );
    	                            return false;
    	                        }else{
    	                        	var data = grid.jqxGrid('getrowdatabyid', id);
    		                        var valueContactMechId = data.contactMechId;
    		                        var contactMechPurpuseTypeId = data.contactMechPurpuseTypeId;
    		                       	loadContactMechDetailByEdit(valueContactMechId, contactMechPurpuseTypeId);
    		                        grid.jqxGrid('clearselection');
    	                        }
		                    });
		                    // delete selected row.
		                    deleteButton.click(function (event) {
		                        var selectedrowindex = grid.jqxGrid('getselectedrowindex');
		                        var rowscount = grid.jqxGrid('getdatainformation').rowscount;
		                        var id = grid.jqxGrid('getrowid', selectedrowindex);
		                        if(id == null){
    	                        	bootbox.dialog(\"${uiLabelMap.SelectItemYouWantToDelete}!\", [{
    	                                \"label\" : \"OK\",
    	                                \"class\" : \"btn btn-primary standard-bootbox-bt\",
    	                                \"icon\" : \"fa fa-check\",
    	                                }]
    	                            );
    	                            return false;
    	                        }else{
    	                        	var data = grid.jqxGrid('getrowdatabyid', id);
    		                        var valueContactMechId = data.contactMechId;
    		                        deleteContactMechInFacility(valueContactMechId);
    		                        grid.jqxGrid('clearselection');
    	                        }
		                    });
				 		 },
				 		 selectionmode:\"singlerow\",
				 		 pageable: true,
				 		 columns: 
				 	     [
				 	      	
				 	      	{ text: \'${uiLabelMap.ContactPurpose}\', datafield: \'contactMechPurpuseTypeId\',editable: true, width: 300,
		                	   cellsrenderer: function(row, colum, value){
		                		   var data = grid.jqxGrid('getrowdata', row);
		       					   var value = data.contactMechPurpuseTypeId;
		       					   var contactMechPurpuseTypeId = getContactMechPurposeTypeId(value);
		       					   return '<span>' + contactMechPurpuseTypeId + '</span>';
		       				   }, 
		                    },
		                    { text: \'${uiLabelMap.CountryCode}\', datafield: \'countryCode\',editable: true, width: 300},
	                    	{ text: \'${uiLabelMap.AreaCode}\', datafield: \'areaCode\',editable: true, width: 300},
	                    	{ text: \'${uiLabelMap.PhoneNumber}\', datafield: \'contactNumber\',editable: true},
		                 ]
		             });
  	             }
     		 }
	 }"/>


		<#assign dataField="[
			{ name: 'contactMechTypeId', type: 'string'},
			{ name: 'rowDetail', type: 'string' }
		]"/>
		<#assign columnlist="
		{ text: '${uiLabelMap.ContactMechType}', datafield: 'contactMechTypeId', filtertype: 'checkedlist', editable:false,
				cellsrenderer: function(row, colum, value){
					var data = $('#jqxgrid').jqxGrid('getrowdata', row);
					var contactMechTypeId = data.contactMechTypeId;
					var contactMechTypeId = getContactMechTypeId(contactMechTypeId);
					return '<span>' + contactMechTypeId + '</span>';
				}, 
				createfilterwidget: function (column, columnElement, widget) {
	        		widget.jqxDropDownList({ selectedIndex: 0,  source: contactMechTypeData, displayMember: 'contactMechTypeId', valueMember: 'contactMechTypeId',
	                	renderer: function (index, label, value) {
		                    var datarecord = contactMechTypeData[index];
		                    return datarecord.description;
		                }
	                });
	        	},
		}
		
		"/>
		
		<@jqGrid filtersimplemode="true" id="jqxgrid" filterable="false" addType="popup" dataField=dataField columnlist=columnlist addrow="true"  editable="true" alternativeAddPopup="alterpopupWindow"  showtoolbar="true" editmode="click"
			url="jqxGeneralServicer?sname=getFacilityContactMech&facilityId=${parameters.facilityId}"
			usecurrencyfunction="true" rowdetailsheight="230"
			initrowdetailsDetail=initrowdetailsDetail initrowdetails="true"
		/>
		
<div id="popupWindownDetail"  class="hide">
	<div>${uiLabelMap.FacilityContactMechInfo}</div>
	<div id="jqxGirdDetail"></div>
</div>


<div id="alterpopupWindow" class='hide popup-bound'>
	<div>${uiLabelMap.CreateNewFacilityContactMech}</div>
	<div>
		<div id="contentNotificationContentCreatePostalAddressError" class="popup-notification">
		</div>
		<div id="contentNotificationContentCreatePhoneNumbersError" class="popup-notification">
		</div>
		<div id="contentNotificationContentCreateInternetIPAddressError" class="popup-notification">
		</div>
		<div id="contentNotificationContentCreateEmailAddressError" class="popup-notification">
		</div>
		<div id="contentNotificationContentCreateWebURLAddressError" class="popup-notification">
		</div>
		<div id="contentNotificationContentCreateInternalNoteviaPartyIdError" class="popup-notification">
		</div>
		<div id="contentNotificationContentCreateElectronicAddressError" class="popup-notification">
		</div>
		<div id="contentNotificationContentCreateInternetDomainNameError" class="popup-notification">
		</div>
		<div class='row-fluid margin-bottom8 padding-top8'>
	   		<div class='span5 text-algin-right'>
				<label class="asterisk">${StringUtil.wrapString(uiLabelMap.PartySelectContactType)}</label>
			</div>  
			<div class="span7">
				<div id="contactMechTypeId">
				</div>
	   		</div>
	   	</div>
	   	<div>
		</div>
		<div id="alterpopupContent">
		</div>
	</div>
</div>

<#include "editPostalAddress.ftl" />

<div id="jqxNotificationCreatePostalAddressSuccess" >
	<div id="notificationCreatePostalAddressSuccess">
	</div>
</div>

<script type="text/javascript">


		
		
		//Create theme
		$.jqx.theme = 'olbius';  
		theme = $.jqx.theme;
		
		$("#alterpopupWindow").jqxExpander({ toggleMode: 'none', showArrow: false });
		var myVar;
		function onLoadData() {
			clearInterval(myVar);
			$("body").css("opacity", 0.2);
			$("#myImg").css({"visibility": "visible", "opacity": 1});
		}
		function onLoadDone() {
			myVar = setInterval(function(){ 
			$("body").css("opacity", 1);
			$("#myImg").css({"visibility": "hidden"});
			 }, 1000);
		}
		$("#jqxNotificationCreatePostalAddressSuccess").jqxNotification({ width: "100%", appendContainer: "#contentNotificationContentCreatePostalAddressSuccess", opacity: 0.9, autoClose: true, template: "success" });
		$("#alterpopupWindow").jqxWindow({
			maxWidth: 600, minWidth: 400, height: 500 ,width:1110, resizable: true,  isModal: true, autoOpen: false, cancelButton: $("#alterCancel"), modalOpacity: 0.7, theme:theme           
	    });
		
		function deleteContactMechInFacility(contactMechId){
			if(contactMechId != undefined){
				bootbox.dialog("${uiLabelMap.AreYouSureDelete}", 
				[{"label": "${StringUtil.wrapString(uiLabelMap.CommonCancel)}", 
					"icon": 'fa fa-remove', "class": 'btn  btn-danger form-action-button pull-right',
		            "callback": function() {bootbox.hideAll();}
		        }, 
		        {"label": "${StringUtil.wrapString(uiLabelMap.OK)}",
		            "icon": 'fa-check', "class": 'btn btn-primary form-action-button pull-right',
		            "callback": function() {
		            	$.ajax({
							  url: "deleteFacilityContactMech",
							  type: "POST",
							  data: {contactMechId : contactMechId, facilityId: facilityId},
							  dataType: "json",
							  success: function(data) {
							  }
						}).done(function(data) {
							var value = data["value"];
							if(value == "success"){
								$("#notificationCreatePostalAddressSuccess").text('${StringUtil.wrapString(uiLabelMap.wgdeletesuccess)}');
								$("#jqxNotificationCreatePostalAddressSuccess").jqxNotification('open');
								$('#jqxgrid').jqxGrid('updatebounddata'); 
							}
							if(value == "exits"){
								bootbox.dialog("${uiLabelMap.LogNotifiCannotDelele}!", [{
		                                "label" : "OK",
		                                "class" : "btn btn-primary standard-bootbox-bt",
		                                "icon" : "fa fa-check",
		                                }]
		                        );
							}
						});
		            }
				}]);
			}
		}
		
		
        $("#popupWindownDetail").jqxWindow({ width: '100%', height: '100%' , resizable: true,  isModal: true, autoOpen: false, cancelButton: $("#alterCancel"), modalOpacity: 0.7, theme:theme });     
		function loadDetailTest(soureDeatilData){
			var sourceData =
		     {
				 localdata: soureDeatilData,
		         datatype: "array",
		         datafields: [
					{ name: 'address1', type: 'string' },
					{ name: 'descriptionContactMechPurpuseType', type: 'string' },
					{ name: 'infoString', type: 'string' },
					{ name: 'toName', type: 'string' },
		         ],
		     };
			var dataAdapter = new $.jqx.dataAdapter(sourceData);
			$("#jqxGirdDetail").jqxGrid(
		    {
		        source: dataAdapter,
		        height: '100%',
		        width: '100%',
		        keyboardnavigation: false,
		        columns: [
		            { text: '${uiLabelMap.ContactPurpose}', datafield: 'descriptionContactMechPurpuseType'},
		            { text: '${uiLabelMap.Address} 1', datafield: 'address1'},
		            { text: '${uiLabelMap.ElectronicAddress}', datafield: 'infoString'},
		            { text: '${uiLabelMap.FormFieldTitle_toName}', datafield: 'toName'},
		        ]
		    });
			$("#popupWindownDetail").jqxWindow('show');
			
		}
		
		$("#contactMechTypeId").jqxDropDownList({dropDownHeight: 80, selectedIndex: 0,source: contactMechTypeData, placeHolder: "${uiLabelMap.PleaseSelectTitle}", displayMember: "description" ,valueMember: "contactMechTypeId"});
		var val = $("#contactMechTypeId").jqxDropDownList('val');
		updateContentPopupAdd(val, facilityId);
		
		$("#contactMechTypeId").on('change', function (event) {
			$('#alterpopupWindow').jqxValidator('hide');
			Loading.show('loadingMacro');
			setTimeout(function(){
				if (event.args) {
		            var item = event.args.item;
		        }
		        var contactMechTypeId = item.value;
		    	var request = $.ajax({
					  url: "viewContactMechPostalAddressInfo",
					  type: "POST",
					  async: false,
					  data: {contactMechTypeId : contactMechTypeId, facilityId: facilityId},
					  dataType: "html",
					  success: function(data) {
						  $("#alterpopupContent").html(data);
					  }
				});
				request.done(function(data) {
				});
			Loading.hide('loadingMacro');
			}, 300);
		});
		
		function updateContentPopupAdd(contactMechTypeId, facilityId){
			$.ajax({
				  url: "viewContactMechPostalAddressInfo",
				  type: "POST",
				  data: {contactMechTypeId : contactMechTypeId, facilityId: facilityId},
				  dataType: "html",
				  success: function(data) {
					  $("#alterpopupContent").html(data);
				  }
			});
		}
		
</script>