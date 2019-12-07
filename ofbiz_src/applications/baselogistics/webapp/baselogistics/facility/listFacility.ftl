<#include "script/facilityScript.ftl"/>
<div id="contentNotificationUpdateSuccess" style="width:100%"></div>
<#assign dataField="[{ name: 'facilityId', type: 'string'},
					 { name: 'facilityCode', type: 'string'},
					 { name: 'facilityName', type: 'string'},
					 { name: 'facilitySize', type: 'number'},
					 { name: 'facilitySizeUomId', type: 'string'},
					 { name: 'descriptionType', type: 'string'},
					 { name: 'description', type: 'string'},
					 { name: 'facilityNameP', type: 'string'},
					 { name: 'facilityGroupName', type: 'string'},
					 { name: 'facilitySizeUomId', type: 'string'},
					 { name: 'partyManagerId', type: 'string'},
					 { name: 'partySKId', type: 'string'},
					 { name: 'uomDesc', type: 'string'},
					 { name: 'fromDate', type: 'date', other: 'Timestamp'},
					 { name: 'thruDate', type: 'date', other: 'Timestamp'},
					 { name: 'openedDate', type: 'date', other: 'Timestamp'},
					 { name: 'closedDate', type: 'date', other: 'Timestamp'},
					 { name: 'fromDateManager', type: 'date', other: 'Timestamp'},
					 { name: 'thruDateManager', type: 'date', other: 'Timestamp'},
					 { name: 'ownerPartyId', type: 'string'},
					 { name: 'trackingProducts', type: 'string'},
					 { name: 'shipment', type: 'string'},
					 { name: 'productInventory', type: 'string'},
					 { name: 'facilityTypeId', type: 'string'},
					 { name: 'firstName', type: 'string'},
					 { name: 'middleName', type: 'string'},
					 { name: 'lastName', type: 'string'},
					 { name: 'groupName', type: 'string'},
					 { name: 'ownerPartyName', type: 'string'},
					 { name: 'rowDetail', type: 'string'},
				   ]"/>
<#assign columnlist="
	{ text: '${uiLabelMap.SequenceId}', sortable: false, filterable: false, editable: false, pinned: true,
	    groupable: false, draggable: false, resizable: false,
	    datafield: '', columntype: 'number', width: 50,
	    cellsrenderer: function (row, column, value) {
	        return '<div style=margin:4px;>' + (value + 1) + '</div>';
	    }
	},
	{ text: '${StringUtil.wrapString(uiLabelMap.FacilityId)}', datafield: 'facilityCode', pinned:true, width:150, cellsrenderer:
       function(row, colum, value){
	        var data = $('#jqxgrid').jqxGrid('getrowdata', row);
	        if (isDistributor && isDistributor == true) {
	        	return '<span><a href=\"' + 'FacilityDetail?facilityId=' + data.facilityId + '\">' + data.facilityCode + '</a></span>';
	        } else {
    			return '<span><a href=\"' + 'detailFacility?facilityId=' + data.facilityId + '\">' + data.facilityCode + '</a></span>';
    		}
     }},
	 { text: '${StringUtil.wrapString(uiLabelMap.FacilityName)}', datafield: 'facilityName', minwidth:200,},
	 { text: '${StringUtil.wrapString(uiLabelMap.Owner)}', datafield: 'ownerPartyName', width:200,
	 	cellsrenderer: function (row, colum, value){
	 	} 
	 },
	 { text: '${StringUtil.wrapString(uiLabelMap.Inventory)}', datafield: 'productInventory', width: 150, filterable: false, sortable: false, cellsrenderer:
	       function(row, colum, value){
		        var data = $('#jqxgrid').jqxGrid('getrowdata', row);
		        if (isDistributor && isDistributor == true) {
        			return '<span><a class=\"fa-archive open-sans\" href=\"' + 'editFacilityInventoryItemsDis?facilityId=' + data.facilityId + '\">' + '${uiLabelMap.ProductInventory}' + '</a></span>';
        		} else {
        			return '<span><a class=\"fa-archive open-sans\" href=\"' + 'getInventory?facilityId=' + data.facilityId + '\">' + '${uiLabelMap.ProductInventory}' + '</a></span>';
        		}
           }
	 },
	 { text: '${StringUtil.wrapString(uiLabelMap.SquareFootage)}', datafield: 'facilitySize', width: 150, filtertype: 'number', cellsrenderer: function (row, columnfield, value, defaulthtml, columnproperties) {
	 		var data = $('#jqxgrid').jqxGrid('getrowdata', row);
	 		var tmpSize = data.facilitySize;
	 		var tmpUom = data.facilitySizeUomId;
	 		if(tmpSize==null){
	 			return '<span></span>';
	 		}else if(tmpUom==null){
	 			return '<span>' + data.facilitySize + '</span>';
	 		}
    		if (tmpSize > 0) {
				return '<span>' + formatnumber(tmpSize) + '&nbsp;' + data.uomDesc + '</span>';
			} else {
				return '<span></span>';
			}
	 	}
	 },
	 { text: '${StringUtil.wrapString(uiLabelMap.DirectlyUnder)}', datafield: 'facilityNameP', width: 200, hidden: hiddenParent},
	 { text: '${StringUtil.wrapString(uiLabelMap.OpenedDate)}', dataField: 'openedDate', align: 'left', width: 130, columntype: 'datetimeinput', filtertype: 'range', cellsformat: 'dd/MM/yyyy', cellsalign: 'right',
		cellsrenderer: function(row, colum, value){
			if(!value){
				return '<span style=\"text-align: right;\"></span>';
			}
	    },
	},
	{ text: '${StringUtil.wrapString(uiLabelMap.ClosedDate)}', dataField: 'closedDate', align: 'left', width: 170, columntype: 'datetimeinput', filtertype: 'range', cellsformat: 'dd/MM/yyyy', cellsalign: 'right',
		cellsrenderer: function(row, colum, value){
			if(!value){
				return '<span style=\"text-align: right;\"></span>';
			}
	    },
	},
	"/>

 <#assign initrowdetailsDetail = "function (index, parentElement, gridElement, datarecord) {
		var grid = $($(parentElement).children()[0]);
		$(grid).attr('id','jqxgridDetail'+index);
		var id = '#jqxgridDetail'+index;
		reponsiveRowDetails(grid);
		if(datarecord.rowDetail){
			var sourceGridDetail =
	        {
	            localdata: datarecord.rowDetail,
	            datatype: 'local',
	            datafields:
	            [
					{ name: 'facilityId', type: 'string'},
					{ name: 'facilityName', type: 'string'},
					{ name: 'facilitySize', type: 'string'},
					{ name: 'facilitySizeUomId', type: 'string'},
					{ name: 'descriptionType', type: 'string'},
					{ name: 'description', type: 'string'},
					{ name: 'facilityNameP', type: 'string'},
					{ name: 'facilityGroupName', type: 'string'},
					{ name: 'facilitySizeUomId', type: 'string'},
					{ name: 'partyManagerId', type: 'string'},
					{ name: 'partySKId', type: 'string'},
					{ name: 'uomDesc', type: 'string'},
					{ name: 'fromDate', type: 'date', other: 'Timestamp'},
					{ name: 'thruDate', type: 'date', other: 'Timestamp'},
					{ name: 'fromDateManager', type: 'date', other: 'Timestamp'},
					{ name: 'thruDateManager', type: 'date', other: 'Timestamp'},
					{ name: 'ownerPartyId', type: 'string'},
					{ name: 'trackingProducts', type: 'string'},
					{ name: 'ownerPartyName', type: 'string'},
					{ name: 'shipment', type: 'string'},
					{ name: 'productInventory', type: 'string'},
					{ name: 'facilityTypeId', type: 'string'},
					{ name: 'firstName', type: 'string'},
					{ name: 'middleName', type: 'string'},
					{ name: 'lastName', type: 'string'},
					{ name: 'groupName', type: 'string'},
	            ]
	        };
	        var dataAdapterGridDetail = new $.jqx.dataAdapter(sourceGridDetail);
	        grid.jqxGrid({
	            width: '98%',
	            height: '90%',
	            theme: 'olbius',
	            localization: getLocalization(),
	            source: dataAdapterGridDetail,
	            sortable: true,
	            pagesize: 5,
		 		pageable: true,
	            selectionmode: 'singlerow',
	            columns: [
						{ text: '${uiLabelMap.SequenceId}', sortable: false, filterable: false, editable: false, pinned: true,
						    groupable: false, draggable: false, resizable: false,
						    datafield: '', columntype: 'number', width: 50,
						    cellsrenderer: function (row, column, value) {
						        return '<div style=margin:4px;>' + (value + 1) + '</div>';
						    }
						},
						{ text: '${StringUtil.wrapString(uiLabelMap.FacilityId)}', datafield: 'facilityId', pinned:true, width:150, cellsrenderer:
						   function(row, colum, value){
								return '<span><a href=\"' + 'detailFacility?facilityId=' + value + '\">' + value + '</a></span>';
						 }},
						 { text: '${StringUtil.wrapString(uiLabelMap.FacilityName)}', datafield: 'facilityName', minwidth:200,},
						 { text: '${StringUtil.wrapString(uiLabelMap.Owner)}', datafield: 'ownerPartyName',
						 	cellsrenderer: function (row, colum, value){
						 		var data = $(id).jqxGrid('getrowdata', row);
						 		var des = '';
						 		if (data.firstName != null || data.middleName != null || data.lastName != null){
						 			if (data.firstName){
						 				des = des + data.firstName;
						 			}
						 			if (data.middleName){
						 				des = des + ' '+ data.middleName;
						 			}
						 			if (data.firstName){
						 				des = des + ' '+ data.lastName;
						 			}
						 			return '<span>' + des + '</span>';
						 		} else {
						 			if (data.groupName != null){
						 				return '<span>' + data.groupName + '</span>';
						 			} else {
						 				return '<span>' + value + '</span>';
						 			}
						 		}
						 	} 
						 },
						 { text: '${StringUtil.wrapString(uiLabelMap.Inventory)}', datafield: 'productInventory', width: 150, cellsrenderer:
						       function(row, colum, value){
							        var data = $(id).jqxGrid('getrowdata', row);
						    		return '<span class=\"open-sans\"><a href=\"' + 'getInventory?facilityId=' + data.facilityId + '\"><i class=\"fa-archive\"></i>' + '${uiLabelMap.ProductInventory}' + '</a></span>';
						       }
						 },
						 { text: '${StringUtil.wrapString(uiLabelMap.SquareFootage)}', datafield: 'facilitySizeUomId', width: 150, cellsrenderer: function (row, columnfield, value, defaulthtml, columnproperties) {
						 		var data = $(id).jqxGrid('getrowdata', row);
						 		var tmpSize = data.facilitySize;
						 		var tmpUom = data.facilitySizeUomId;
						 		if(tmpSize==null){
						 			return '<span></span>';
						 		}else if(tmpUom==null){
						 			return '<span>' + data.facilitySize + '</span>';
						 		}
						 		if (tmpSize > 0) {
									return '<span>' + formatnumber(tmpSize) + '&nbsp;' + data.uomDesc + '</span>';
								} else {
									return '<span></span>';
								}
						 	}
						 },
						 { text: '${StringUtil.wrapString(uiLabelMap.DirectlyUnder)}', datafield: 'facilityNameP', width: 200, hidden: true},
                     ]
	        });
		}else {
			grid.jqxGrid({
	            width: '98%',
	            height: '90%',
	            theme: 'olbius',
	            localization: getLocalization(),
	            source: [],
	            sortable: true,
	            pagesize: 5,
		 		pageable: true,
	            selectionmode: 'singlerow',
	            columns: [
						{ text: '${uiLabelMap.SequenceId}', sortable: false, filterable: false, editable: false, pinned: true,
						    groupable: false, draggable: false, resizable: false,
						    datafield: '', columntype: 'number', width: 50,
						    cellsrenderer: function (row, column, value) {
						        return '<div style=margin:4px;>' + (value + 1) + '</div>';
						    }
						},
						{ text: '${uiLabelMap.FacilityId}', datafield: 'facilityId', align: 'left', width: 150},
						{ text: '${uiLabelMap.FacilityName}', datafield: 'facilityName', align: 'left', minwidth: 150},
						{ text: '${StringUtil.wrapString(uiLabelMap.SquareFootage)}', datafield: 'facilitySizeUomId', width: 150,},
						{ text: '${StringUtil.wrapString(uiLabelMap.DirectlyUnder)}', datafield: 'facilityNameP', width: 200},
                  ]
	        });
		}
	 }"/>	 
	 
<div id='menuForFacility' style="display:none;">
	<ul>
		<li><i class="fa fa-folder-open-o"></i>${uiLabelMap.ViewDetailInNewPage}</li>
	    <li><i class="fa fa-eye"></i>${StringUtil.wrapString(uiLabelMap.BSViewDetail)}</li>
	    <li><i class="fa fa-database"></i>${uiLabelMap.Inventory}</li>
	    <#if hasOlbPermission("MODULE", "LOG_FACILITY_NEW", "CREATE")>
	    	<li><i class="fa fa-edit"></i>${uiLabelMap.Edit}</li>
	    </#if>
	    <#if hasOlbPermission("MODULE", "LOG_FACILITY_NEW", "CREATE")>
	    	<li><i class="fa red fa-trash"></i>${StringUtil.wrapString(uiLabelMap.CommonDelete)}</li>
	    </#if>
	    <li><i class="fa fa-refresh"></i>${StringUtil.wrapString(uiLabelMap.BSRefresh)}</li>
	</ul>
</div>

<div id="jqxNotificationUpdateSuccess" >
	<div id="notificationContentUpdateSuccess">
	</div>
</div>
<#if hasOlbPermission("MODULE", "LOG_FACILITY_NEW", "CREATE") && facility?has_content && facility.primaryFacilityGroupId?has_content && facility.primaryFacilityGroupId == "FACILITY_INTERNAL" >
	<@jqGrid filtersimplemode="true" id="jqxgrid" addType="popup" dataField=dataField columnlist=columnlist clearfilteringbutton="true"
		showtoolbar="true" addrow=addPerm deleterow="false" alternativeAddPopup="alterpopupWindow" editable="false" addrefresh="true"
		initrowdetails = "true" rowdetailsheight="240" initrowdetailsDetail=initrowdetailsDetail 
		url=params addColumns="listProductStoreId(java.util.List);facilityId;primaryFacilityGroupId;facilityName;facilityTypeId;managerPartyId;ownerPartyId;fromDate;thruDate;fromDateManager;thruDateManager;openedDate;facilitySize(java.math.BigDecimal);facilitySizeUomId;parentFacilityId;address;countryGeoId;districtGeoId;provinceGeoId;wardGeoId;description;imagesPath;phoneNumber"
		createUrl="jqxGeneralServicer?sname=updateFacility&jqaction=C" mouseRightMenu="true" contextMenuId="menuForFacility" jqGridMinimumLibEnable="false"
		showlist="true" customTitleProperties=title addrow="false" selectionmode="singlerow"
		customcontrol1="icon-plus open-sans@${uiLabelMap.AddNew}@javascript:FacilityObj.prepareCreateFacility('"+parentFacilityId?if_exists+"');"
	/>	
<#else>
	<@jqGrid filtersimplemode="true" id="jqxgrid" addType="popup" dataField=dataField columnlist=columnlist clearfilteringbutton="true"
		showtoolbar="true" addrow=addPerm deleterow="false" alternativeAddPopup="alterpopupWindow" editable="false" addrefresh="true"
		initrowdetails = "true" rowdetailsheight="240" initrowdetailsDetail=initrowdetailsDetail 
		url=params addColumns="listProductStoreId(java.util.List);facilityId;primaryFacilityGroupId;facilityName;facilityTypeId;managerPartyId;ownerPartyId;fromDate;thruDate;fromDateManager;thruDateManager;openedDate;facilitySize(java.math.BigDecimal);facilitySizeUomId;parentFacilityId;address;countryGeoId;districtGeoId;provinceGeoId;wardGeoId;description;imagesPath;phoneNumber"
		createUrl="jqxGeneralServicer?sname=updateFacility&jqaction=C" mouseRightMenu="true" contextMenuId="menuForFacility" jqGridMinimumLibEnable="false"
		showlist="true" customTitleProperties=title addrow="false" selectionmode="singlerow"
	/>	
</#if>
<div id="alterpopupWindow" class='hide popup-bound'>
	<div>${uiLabelMap.FacilityInformation}</div>
	<div class='form-window-container' id="content">
			<div class="row-fluid">
				<div class="span6">
					<div class="row-fluid hide">	
						<div class="span5" style="text-align: right">
							<div> ${uiLabelMap.FacilityId} </div>
						</div>
						<div class="span7">	
							<input id="facilityId"></input>
						</div>
					</div>
					<div class="row-fluid margin-bottom10">	
						<div class="span5" style="text-align: right">
							<div class="asterisk"> ${uiLabelMap.FacilityId} </div>
						</div>
						<div class="span7">	
							<input id="facilityCode"></input>
						</div>
					</div>
					<div class="row-fluid margin-bottom10">	
						<div class="span5" style="text-align: right">
							<div class="asterisk"> ${uiLabelMap.FacilityName} </div>
						</div>
						<div class="span7">	
							<input id="facilityName"></input>
						</div>
					</div>
					<div class="row-fluid margin-bottom10">	
						<div class="span5" style="text-align: right">
							<div> ${uiLabelMap.FacilityDirectlyUnder} </div>
						</div>
						<div class="span7">	
							<div id="parentFacilityId" class="green-label"></div>
						</div>
					</div>
					<div class="row-fluid margin-bottom10">	
						<div class="span5" style="text-align: right">
							<div class="asterisk"> ${uiLabelMap.BSPSSalesChannel} </div>
						</div>
						<div class="span7">	
							<div id="productStoreId" style="width: 100%;" class="green-label"></div>
						</div>
					</div>
					<div class="row-fluid margin-bottom10">	
						<div class="span5" style="text-align: right">
							<div> ${uiLabelMap.OpenedDate} </div>
						</div>
						<div class="span7">	
							<div id="openedDate" style="width: 100%;" class="green-label"></div>
						</div>
					</div>
					<div class="row-fluid margin-bottom10">	
						<div class="span5" style="text-align: right">
							<div> ${uiLabelMap.Country} </div>
						</div>
						<div class="span7">	
							<div id="countryGeoId" style="width: 100%;" class="green-label"></div>
						</div>
					</div>
					<div class="row-fluid margin-bottom10">	
						<div class="span5" style="text-align: right">
							<div> ${uiLabelMap.Provinces} </div>
						</div>
						<div class="span7">	
							<div id="provinceGeoId" style="width: 100%;" class="green-label"></div>
						</div>
					</div>
					<div class="row-fluid margin-bottom10">	
						<div class="span5" style="text-align: right">
							<div> ${uiLabelMap.County} </div>
						</div>
						<div class="span7">	
							<div id="districtGeoId" style="width: 100%;" class="green-label"></div>
						</div>
					</div>
					<div class="row-fluid margin-bottom10">	
						<div class="span5" style="text-align: right">
							<div> ${uiLabelMap.Ward} </div>
						</div>
						<div class="span7">	
							<div id="wardGeoId" style="width: 100%;" class="green-label"></div>
						</div>
					</div>
					<div class="row-fluid margin-bottom10">	
						<div class="span5" style="text-align: right">
							<div class="asterisk"> ${uiLabelMap.Address} </div>
						</div>
						<div class="span7">	
							<input id="address"></input>
						</div>
					</div>
					<div class="row-fluid margin-bottom10">	
						<div class="span5" style="text-align: right">
							<div> ${uiLabelMap.PhoneNumber} </div>
						</div>
						<div class="span7">	
							<input id="phoneNumber"></input>
						</div>
					</div>
				</div>
				<div class="span6">
					<div class="row-fluid margin-bottom10">	
						<div class="span5" style="text-align: right">
							<div class="asterisk"> ${uiLabelMap.Owner} </div>
						</div>
						<div class="span7">	
							<div id="ownerPartyId" style="width: 100%; margin-left: 0px !important" class="green-label asterisk"></div>
						</div>
					</div>
					<div class="row-fluid margin-bottom10">	
						<div class="span5" style="text-align: right">
							<div class="asterisk"> ${uiLabelMap.OwnFromDate} </div>
						</div>
						<div class="span7">	
							<div id="fromDate" style="width: 100%;" class="green-label asterisk"></div>
						</div>
					</div>
					<div class="row-fluid margin-bottom10">	
						<div class="span5" style="text-align: right">
							<div> ${uiLabelMap.OwnThruDate} </div>
						</div>
						<div class="span7">	
							<div id="thruDate" style="width: 100%;" class="green-label"></div>
						</div>
					</div>
					<div class="row-fluid margin-bottom10">	
						<div class="span5" style="text-align: right">
							<div> ${uiLabelMap.Storekeeper} </div>
						</div>
						<div class="span7">	
							<div id="storekeeperId" class="green-label">
								<div id="managerPartyId"></div>
								<div id="inputManagerPartyId" type="hidden"></div>
							</div>
						</div>
					</div>
					<div class="row-fluid margin-bottom10">	
						<div class="span5" style="text-align: right">
							<div> ${uiLabelMap.ManageFromDate} </div>
						</div>
						<div class="span7">	
							<div id="fromDateManager" style="width: 100%;" class="green-label asterisk"></div>
						</div>
					</div>
					<div class="row-fluid margin-bottom10">	
						<div class="span5" style="text-align: right">
							<div> ${uiLabelMap.ManageThruDate} </div>
						</div>
						<div class="span7">	
							<div id="thruDateManager" style="width: 100%;" class="green-label"></div>
						</div>
					</div>
					<div class="row-fluid margin-bottom10">	
	    				<div class="span5" style="text-align: right">
	    					<div>${uiLabelMap.SquareFootage}</div>
						</div>
						<div class="span7">
							<div id="facilitySizeAdd"></div>
						</div>
					</div>
					<div class="row-fluid margin-bottom10">	
	    				<div class="span5" style="text-align: right">
	    					<div>${uiLabelMap.Unit}</div>
						</div>
						<div class="span7">
							<div id="facilitySizeUomId" class="green-label"></div>
						</div>
					</div>
					<div class="row-fluid margin-bottom10">	
	    				<div class="span5" style="text-align: right">
	    					<div>${uiLabelMap.Avatar}</div>
						</div>
						<div class="span7" style="height: 20px !important">
							<div id="idImagesPath">
								<input type="file" id="imagesPath" name="uploadedFile" class="green-label" accept="image/*"/>
							</div>
						</div>
					</div>
					<div class="row-fluid margin-bottom10">	
	    				<div class="span5" style="text-align: right">
	    					<div>${uiLabelMap.Description}</div>
						</div>
						<div class="span7">
							<div style="width: 195px; display: inline-block; margin-bottom: 3px;"><input id="description"></input></div>
						</div>
					</div>
					<div class="row-fluid margin-bottom10">	
						<div class="span5" style="text-align: right">
							<div> ${uiLabelMap.BLUsingLocation} </div>
						</div>
						<div class="span7">	
							<div id="requireLocation"></div>
						</div>
					</div>
					<div class="row-fluid margin-bottom10">	
						<div class="span5" style="text-align: right">
							<div> ${uiLabelMap.BLDateManagement} </div>
						</div>
						<div class="span7">	
							<div id="requireDate"></div>
						</div>
					</div>
				</div>
		   	<div class="form-action popup-footer">
				<button id="alterCancel" class='btn btn-danger form-action-button pull-right'><i class='fa-remove'></i> ${uiLabelMap.CommonClose}</button>
				<button id="alterSave" class='btn btn-primary form-action-button pull-right'><i class='fa-check'></i> ${uiLabelMap.CommonSave}</button> 
			</div>
		</div>
	</div>
</div>
<script type="text/javascript">
	$.jqx.theme = 'olbius';  
	theme = $.jqx.theme;
	var facilityDT;
	var curFacilityId = '${parameters.facilityId?if_exists}';
	pathScanFile = null;
	var provinceData = new Array();
	var countyData = new Array();
	var wardData = new Array();
	var sourceProdStoreData = {
		localdata : prodStoreData,
		datatype : "array",
		dataField : [
             {name : 'description', type :'string'},
             {name : 'productStoreId', type :'string'},
         ]
	};
	var dataAdapterStore = new $.jqx.dataAdapter(sourceProdStoreData);
    // Create a jqxComboBox
    $("#productStoreId").jqxComboBox({placeHolder: '${StringUtil.wrapString(uiLabelMap.PleaseSelectTitle)}', checkboxes: true, source: dataAdapterStore, displayMember: "description", valueMember: "productStoreId", width: 200, height: 25, dropDownHeight : 200});
	$("#alterpopupWindow").jqxWindow({
		maxWidth: 1200, minWidth: 800, width: 950, minHeight: 400, height: 580, maxHeight: 1000, resizable: false, modalZIndex: 10000, isModal: true, autoOpen: false, cancelButton: $("#alterCancel"), modalOpacity: 0.7, theme:theme         
    });
	
	function reponsiveRowDetails(grid, parentElement) {
	    $(window).bind('resize', function() {
	    	$(grid).jqxGrid({ width: "96%"});
	    });
	    $('#sidebar').bind('resize', function() {
	    	$(grid).jqxGrid({ width: "96%"});
	    });
	}
</script>

<style type="text/css">
	.span6 label{
		margin-right:5px;
		text-align:right;
		width:200px;
	} 
	.ace-file-input .remove {
		right: 20px;
		padding-left: 2px;
	}
	.ace-file-input label span {
		margin-right: 110px;
	  	padding-right: 50px;
	}
</style>	
<script>
	$(function() {
		$('#id-input-file-1 , #imagesPath').ace_file_input({
			no_file:'${StringUtil.wrapString(uiLabelMap.NoFile)} ...',
			btn_choose:'${StringUtil.wrapString(uiLabelMap.Choose)}',
			btn_change:'${StringUtil.wrapString(uiLabelMap.Change)}',
			droppable:false,
			onchange:null,
			thumbnail:false
		});
	});
</script>
