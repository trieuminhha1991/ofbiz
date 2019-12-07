<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxvalidator2.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxnumberinput.js"></script>
<#assign hasPerm = true>
<#assign addPerm = "false">
<#assign params="jqxGeneralServicer?sname=listFacilityJqx&facilityTypeId=WAREHOUSE">
<#if security.hasPermission("LOGISTICS_ADMIN", session) || security.hasPermission("FACILITY_ADMIN", session)>
	<#assign addPerm = "true">
<#elseif !security.hasPermission("LOGISTICS_VIEW", session) && !security.hasPermission("FACILITY_VIEW", session) && !security.hasPermission("FACILITY_ROLE_VIEW", session)>
	<#assign hasPerm = false>
</#if>
<#if security.hasPermission("LOGISTICS_CREATE", session) || security.hasPermission("FACILITY_CREATE", session)>
	<#assign addPerm = "true">
</#if>

<#if hasPerm = false>
	<div class="alert alert-danger">
		<strong>
			<i class="ace-icon fa fa-times"></i>
			${uiLabelMap.noViewPerm}
		</strong>
	</div>
<#else>
<div id="contentNotificationUpdateSuccess" style="width:100%">
</div>
	<#assign dataField="[{ name: 'facilityId', type: 'string'},
						 { name: 'facilityName', type: 'string'},
						 { name: 'groupName', type: 'string'},
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
						 { name: 'shipment', type: 'string'},
						 { name: 'productInventory', type: 'string'},
						 { name: 'facilityTypeId', type: 'string'},
					   ]"/>
	<#assign columnlist="{
						    text: '${uiLabelMap.SequenceId}', sortable: false, filterable: false, editable: false, pinned: true,
						    groupable: false, draggable: false, resizable: false,
						    datafield: '', columntype: 'number', width: 50,
						    cellsrenderer: function (row, column, value) {
						        return '<div style=margin:4px;>' + (value + 1) + '</div>';
						    }
						},
						{ text: '${StringUtil.wrapString(uiLabelMap.facilityId)}', datafield: 'facilityId', pinned:true ,maxwidth:150,cellsrenderer:
					       function(row, colum, value){
						        var data = $('#jqxgrid').jqxGrid('getrowdata', row);
				        		return '<span><a href=\"' + 'editFacilityInfo?facilityId=' + data.facilityId + '\">' + data.facilityId + '</a></span>';
				         }},
						 { text: '${StringUtil.wrapString(uiLabelMap.FormFieldTitle_facilityName)}', datafield: 'facilityName'},
						 { text: '${StringUtil.wrapString(uiLabelMap.facilityOwner)}', datafield: 'groupName', minwidth:250},
						 { text: '${StringUtil.wrapString(uiLabelMap.ChildOfFacility)}', datafield: 'facilityNameP', minwidth: 150},
						 { text: '${StringUtil.wrapString(uiLabelMap.FacilitySize)}', datafield: 'facilitySizeUomId', width: 150, cellsrenderer: function (row, columnfield, value, defaulthtml, columnproperties) {
						 		var data = $('#jqxgrid').jqxGrid('getrowdata', row);
						 		var tmpSize = data.facilitySize;
						 		var tmpUom = data.facilitySizeUomId;
						 		if(tmpSize==null){
						 			return '<span></span>';
						 		}else if(tmpUom==null){
						 			return '<span>' + data.facilitySize + '</span>';
						 		}
				        		return '<span>' + data.facilitySize + '&nbsp;' + data.uomDesc + '</span>';
						 }},
						 { text: '${StringUtil.wrapString(uiLabelMap.LogTitleManagerLocation)}', datafield: 'trackingProducts', width: 110, cellsrenderer:
						       function(row, colum, value){
							        var data = $('#jqxgrid').jqxGrid('getrowdata', row);
					        		return '<span><a href=\"' + 'FindLocationFacility?facilityId=' + data.facilityId + '\"><i class=\"icon-book\"></i>' + 'QLVT' + '</a></span>';
					           }
						 },
						 { text: '${StringUtil.wrapString(uiLabelMap.Shipment)}', datafield: 'shipment', width: 90, cellsrenderer:
						       function(row, colum, value){
							        var data = $('#jqxgrid').jqxGrid('getrowdata', row);
					        		return '<span><a href=\"' + 'getShipmentByFacility?facilityId=' + data.facilityId + '\"><i class=\"fa-truck\"></i>' + '${uiLabelMap.Shipment}' + '</a></span>';
					           }
						 },
						 { text: '${StringUtil.wrapString(uiLabelMap.ProductInventory)}', datafield: 'productInventory', width: 90, cellsrenderer:
						       function(row, colum, value){
							        var data = $('#jqxgrid').jqxGrid('getrowdata', row);
					        		return '<span><a href=\"' + 'getInventoryItemList?facilityId=' + data.facilityId + '\">' + '${uiLabelMap.ProductInventory}' + '</a></span>';
					           }
						 },
						"/>
	<div id='menuForFacility' style="display:none;">
		<ul>
		    <li><i class="icon-book"></i>&nbsp;&nbsp;${uiLabelMap.LogTitleManagerLocation}</li>
		    <li><i class="fa-truck"></i>&nbsp;&nbsp;${uiLabelMap.Shipment}</li>
		    <li><i class="icon-edit"></i>&nbsp;&nbsp;${uiLabelMap.DSEditRowGird}</li>
		</ul>
	</div>
	
	<div id="jqxNotificationUpdateSuccess" >
		<div id="notificationContentUpdateSuccess">
		</div>
	</div>
	
	<#include "editFacilityByFacilityId.ftl" />
	<@jqGridMinimumLib/>
	<script type="text/javascript">
		$.jqx.theme = "olbius";
		theme = $.jqx.theme;
		$("#jqxNotificationUpdateSuccess").jqxNotification({ width: "100%", appendContainer: "#contentNotificationUpdateSuccess", opacity: 0.9, autoClose: true, template: "success" });
		$("#menuForFacility").jqxMenu({ width: 170, autoOpenPopup: false, mode: 'popup', theme: theme});
		$("#menuForFacility").on('itemclick', function (event) {
			var data = $('#jqxgrid').jqxGrid('getRowData', $("#jqxgrid").jqxGrid('selectedrowindexes'));
			var tmpStr = $.trim($(args).text());
			if(tmpStr == '${StringUtil.wrapString(uiLabelMap.LogTitleManagerLocation)}'){
//				window.location.href = "FindPhysicalInventory?facilityId=" + data.facilityId;
				window.location.href = "FindLocationFacility?facilityId=" + data.facilityId;
			}else if(tmpStr == '${StringUtil.wrapString(uiLabelMap.Shipment)}'){
				window.location.href = "getShipmentByFacility?facilityId=" + data.facilityId;
			}
			if(tmpStr == '${StringUtil.wrapString(uiLabelMap.DSEditRowGird)}'){
				var checkUpdateFacilityBylistFacility = 0;
				loadDataEditByFacility(data, checkUpdateFacilityBylistFacility);
			}
		});
	</script>
	
	<@jqGrid filtersimplemode="true" id="jqxgrid" addType="popup" dataField=dataField columnlist=columnlist clearfilteringbutton="true"
		showtoolbar="true" addrow=addPerm deleterow="false" alternativeAddPopup="alterpopupWindow" editable="false" addrefresh="true"
		otherParams="partyIdFromName:M-org.ofbiz.party.party.PartyHelper(getPartyName)<partyIdFrom>"
		url=params addColumns="facilityId;primaryFacilityGroupId;facilityName;facilityTypeId;managerPartyId;ownerPartyId;fromDate(java.sql.Timestamp);thruDate(java.sql.Timestamp);fromDateManager(java.sql.Timestamp);thruDateManager(java.sql.Timestamp);facilitySize(java.math.BigDecimal);facilitySizeUomId"
		createUrl="jqxGeneralServicer?sname=createFacilityJqx&jqaction=C" mouseRightMenu="true" contextMenuId="menuForFacility" jqGridMinimumLibEnable="false"
		showlist="true" entityName="facility"
	/>	
	<style type="text/css">
		.span6{
		    overflow:hidden;display:inline;
			margin-top:8px;
		}
		.span6 label, .span6 input, .span6 .jqxcpn {
		display:inline-block;
		}
		.span6 input div{
		    width:40%;
			margin: 0px !important;
		}
		.span6 .lblfjqx{
			margin-top:-15px;
		}
		.span6 label{
			margin-right:5px;
			text-align:right;
			width:118px;
		}
		.bordertop{
			border-top:solid 1px #CCC;
			margin-top:8px;
		}
	</style>
	

	<div id="alterpopupWindow" class='hide'>
		<div>${uiLabelMap.ProductNewFacility}</div>
		<div>
			<div class='row-fluid margin-bottom8 padding-top8'>
				<div class='span6'>
					<div class='span5 text-algin-right'>
						<label class="asterisk">${StringUtil.wrapString(uiLabelMap.FacilityName)}</label>
					</div>  
					<div class="span7">
						<input id="facilityName">
						</input>
					</div>
				</div>
				<div class='span6'>
					<div class='span5 text-algin-right'>
						<label class="asterisk">${StringUtil.wrapString(uiLabelMap.facilityId)}</label>
					</div>  
					<div class="span7">
						<input id="facilityId"></input>
			   		</div>
				</div>
		   	</div>
		   	<div class='row-fluid margin-bottom8 padding-top8'>
				<div class='span6'>
					<div class='span5 text-algin-right'>
						<label>${StringUtil.wrapString(uiLabelMap.Owner)}</label>
					</div>  
					<div class="span7">
						<div id="ownerPartyId">
						</div>
					</div>
				</div>
				<div class='span6'>
					<div class='span5 text-algin-right'>
						<label>${StringUtil.wrapString(uiLabelMap.Manager)}</label>
					</div>  
					<div class="span7">
						<div id="managerPartyId"></div>
			   		</div>
				</div>
		   	</div>
		   	<div class='row-fluid margin-bottom8 padding-top8'>
				<div class='span6'>
					<div class='span5 text-algin-right'>
						<label>${StringUtil.wrapString(uiLabelMap.fromDate)}</label>
					</div>  
					<div class="span7">
						<div id="fromDate">
						</div>
					</div>
				</div>
				<div class='span6'>
					<div class='span5 text-algin-right'>
						<label>${StringUtil.wrapString(uiLabelMap.fromDate)}</label>
					</div>  
					<div class="span7">
						<div id="fromDateManager">
						</div>
					</div>
				</div>
		   	</div>
		   	<div class='row-fluid margin-bottom8 padding-top8'>
			   	<div class='span6'>
					<div class='span5 text-algin-right'>
						<label>${StringUtil.wrapString(uiLabelMap.thruDate)}</label>
					</div>  
					<div class="span7">
						<div id="thruDate"></div>
			   		</div>
				</div>
				<div class='span6'>
					<div class='span5 text-algin-right'>
						<label>${StringUtil.wrapString(uiLabelMap.thruDate)}</label>
					</div>  
					<div class="span7">
						<div id="thruDateManager">
						</div>
					</div>
				</div>
		   	</div>
		   	<div class='row-fluid margin-bottom8 padding-top8'>
				<div class='span6'>
					<div class='span5 text-algin-right'>
						<label>${StringUtil.wrapString(uiLabelMap.SquareFootage)}</label>
					</div>  
					<div class="span7">
						<div id="facilitySize">
						</div>
					</div>
				</div>
				<div class='span6'>
					<div class='span5 text-algin-right'>
						<label>${StringUtil.wrapString(uiLabelMap.facilitySizeUomId)}</label>
					</div>  
					<div class="span7">
						<div id="facilitySizeUomId">
						</div>
					</div>
				</div>
		   	</div>
		   	<div class="form-action">
				<button id="alterCancel" class='btn btn-danger form-action-button pull-right' style='height: 30px'><i class='fa-remove'></i> ${uiLabelMap.CommonCancel}</button>
				<button id="alterSave" class='btn btn-primary form-action-button pull-right' style='height: 30px'><i class='fa-check'></i> ${uiLabelMap.CommonSave}</button> 
			</div>
		</div>
	</div>
	
	<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxcombobox2.js"></script>
	<script type="text/javascript">
		$(document).ready(function () {
			var source =
	            {
	                datatype: "json",
	                datafields: [
	                    { name: 'partyId' },
	                    { name: 'groupName' }
	                ],
	                type: "POST",
	                root: "listParties",
	                contentType: 'application/x-www-form-urlencoded',
	                url: "facilityOwnerableList"
	            };
	            var dataAdapter = new $.jqx.dataAdapter(source,
	                {
	                    formatData: function (data) {
	                        if ($("#ownerPartyId").jqxComboBox('searchString') != undefined) {
	                            data.searchKey = $("#ownerPartyId").jqxComboBox('searchString');
	                            return data;
	                        }
	                    }
	                }
	            );
	            $("#ownerPartyId").jqxComboBox(
	            {
	                width: 208,
	                placeHolder: "${StringUtil.wrapString(uiLabelMap.wmparty)}",
	                dropDownWidth: 500,
	                height: 25,
	                source: dataAdapter,
	                remoteAutoComplete: true,
	                autoDropDownHeight: true,               
	                selectedIndex: 0,
	                displayMember: "groupName",
	                valueMember: "partyId",
	                renderer: function (index, label, value) {
	                	
	                    var item = dataAdapter.records[index];
	                    if (item != null) {
	                        var label = item.groupName + " (" + item.partyId + ")";
	                        return label;
	                    }
	                    return "";
	                },
	                renderSelectedItem: function(index, item)
	                {
	                    var item = dataAdapter.records[index];
	                    if (item != null) {
	                        var label = item.groupName;
	                        return label;
	                    }
	                    return "";   
	                },
	                search: function (searchString) {
	                    dataAdapter.dataBind();
	                }
	            });
			var sourceMNG =
	            {
	                datatype: "json",
	                datafields: [
	                    { name: 'partyId' },
	                    { name: 'firstName' },
	                    { name: 'middleName' },
	                    { name: 'lastName' },
	                    { name: 'fullName' }
	                ],
	                type: "POST",
	                root: "listParties",
	                contentType: 'application/x-www-form-urlencoded',
	                url: "facilityManagerableList"
	            };
	            var dataAdapterMNG = new $.jqx.dataAdapter(sourceMNG,
	                {
	                    formatData: function (data) {
	                        if ($("#managerPartyId").jqxComboBox('searchString') != undefined) {
	                            data.searchKey = $("#managerPartyId").jqxComboBox('searchString');
	                            return data;
	                        }
	                    }
	                }
	            );
	            $("#managerPartyId").jqxComboBox(
	            {
	                width: 208,
	                placeHolder: "${StringUtil.wrapString(uiLabelMap.wmparty)}",
	                dropDownWidth: 500,
	                height: 25,
	                source: dataAdapterMNG,
	                remoteAutoComplete: true,
	                autoDropDownHeight: true,               
	                selectedIndex: 0,
	                displayMember: "fullName",
	                valueMember: "partyId",
	                renderer: function (index, label, value) {
	                    var item = dataAdapterMNG.records[index];
	                    if (item != null) {
	                    	var label;
	                    	if(item.fullName != null){
	                    		label = item.firstName + "&nbsp;" + item.middleName + "&nbsp;" + item.lastName;
	                    	}else if(item.firstLast != null){
	                    		label = item.firstName + "&nbsp;" + lastName;
	                    	}else if(item.firstMiddle != null){
	                    		label = item.firstName + "&nbsp;" + item.middleName;
	                    	}else if(item.middleLast != null){
	                    		label = item.middleName + "&nbsp;" + item.lastName;
	                    	}
	                        label += " (" + item.partyId + ")";
	                        return label;
	                    }
	                    return "";
	                },
	                renderSelectedItem: function(index, item)
	                {
	                    var item = dataAdapterMNG.records[index];
	                    if (item != null) {
	                        var label;
	                    	if(item.fullName != null){
	                    		label = item.firstName + " " + item.middleName + " " + item.lastName;
	                    	}else if(item.firstLast != null){
	                    		label = item.firstName + " " + lastName;
	                    	}else if(item.firstMiddle != null){
	                    		label = item.firstName + " " + item.middleName;
	                    	}else if(item.middleLast != null){
	                    		label = item.middleName + " " + item.lastName;
	                    	}
	                        return label;
	                    }
	                    return "";   
	                },
	                search: function (searchString) {
	                    dataAdapterMNG.dataBind();
	                }
	            });
            });
	</script>
	<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/globalization/globalize.culture.vi-VN.js"></script>
	<script type="text/javascript">
		$.jqx.theme = 'olbius';  
		theme = $.jqx.theme;
//		var source = [<#list listFG as item>{key: '${item.facilityGroupId}', value: '${item.facilityGroupName}'}<#if item_index!=(listFG?size)>,</#if></#list>];
		var sourceUom = [<#list listUoms as item>{key: '${item.uomId}', value: '${item.description}'}<#if item_index!=(listUoms?size)>,</#if></#list>];
//		var sourceFT = [<#list listFT as item>{key: '${item.facilityTypeId}', value: '${item.description}'}<#if item_index!=(listFT?size)>,</#if></#list>];
		var tmpLcl = '${locale}';
		if(tmpLcl=='vi'){
			tmpLcl = 'vi-VN';
		}else{
			tmpLcl = 'en-EN';
		}
		$("#facilitySize").jqxNumberInput({ width: '208px', height: '25px', inputMode: 'simple', spinButtons: true });
		$("#fromDate").jqxDateTimeInput({width: '208px', height: '25px', culture: tmpLcl});
		$("#thruDate").jqxDateTimeInput({width: '208px', height: '25px', culture: tmpLcl});
		$("#thruDate").jqxDateTimeInput("val",null);
		$("#fromDateManager").jqxDateTimeInput({width: '208px', height: '25px', culture: tmpLcl});
		$("#thruDateManager").jqxDateTimeInput({width: '208px', height: '25px', culture: tmpLcl});
		$("#thruDateManager").jqxDateTimeInput("val",null);
//		$("#primaryFacilityGroupId").jqxDropDownList({ source: source, displayMember: 'value', valueMember: 'key', theme: theme, selectedIndex: 1, width: '208', height: '25'});
//		$("#facilityTypeId").jqxDropDownList({ source: sourceFT, displayMember: 'value', valueMember: 'key', theme: theme, selectedIndex: 1, width: '208', height: '25'});
		$("#facilitySizeUomId").jqxDropDownList({ source: sourceUom, displayMember: 'value', valueMember: 'key', theme: theme, selectedIndex: 1, width: '208', height: '25'});
		$("#alterpopupWindow").jqxWindow({
			width: 1000, height: 360, resizable: false,  isModal: true, autoOpen: false, cancelButton: $("#alterCancel"), modalOpacity: 0.7, theme:theme         
	    });
	
	    $('#alterpopupWindow').jqxValidator({
            rules: [
	                   {
	                       input: '#thruDateManager', message: '${StringUtil.wrapString(uiLabelMap.faFromDateLTThruDate)}', action: 'valueChanged', rule: function (input, commit) {
	                           var fromDate = $('#fromDateManager').jqxDateTimeInput('value');
	                           var thruDate = $('#thruDateManager').jqxDateTimeInput('value');
	                           if(thruDate == null || thruDate== undefined || fromDate == null || fromDate== undefined){
	                        	   return true;
	                           }
	                           if(fromDate > thruDate){
	                        	   return false;
	                           }
	                           return true;
	                       }
	                   },
	                   {
	                       input: '#thruDate', message: '${StringUtil.wrapString(uiLabelMap.faFromDateLTThruDate)}', action: 'valueChanged', rule: function (input, commit) {
	                           var fromDate = $('#fromDate').jqxDateTimeInput('value');
	                           var thruDate = $('#thruDate').jqxDateTimeInput('value');
	                           if(thruDate == null || thruDate== undefined || fromDate == null || fromDate== undefined){
	                        	   return true;
	                           }
	                           if(fromDate > thruDate){
	                        	   return false;
	                           }
	                           return true;
	                       }
	                   }
                   ]
        });
	    // update the edited row when the user clicks the 'Save' button.
	    $("#alterSave").click(function () {
	    	var row;
	        row = { 
	        		fromDate:$('#fromDate').jqxDateTimeInput('getDate'),
	        		fromDateManager:$('#fromDateManager').jqxDateTimeInput('getDate'),
	        		ownerPartyId:$('#ownerPartyId').val(),
	        		primaryFacilityGroupId:'_NA_',
	        		managerPartyId:$('#managerPartyId').val(),
	        		facilityName:$('#facilityName').val(),
	        		facilityId:$('#facilityId').val(),
	        		facilitySize:$('#facilitySize').val(),
	        		facilitySizeUomId:$('#facilitySizeUomId').val(),
	        		facilityTypeId: 'WAREHOUSE',
	        		periodTypeId:$('#periodTypeId').val(),
	        		thruDate: $('#thruDate').jqxDateTimeInput('getDate'),            
	        		thruDateManager: $('#thruDateManager').jqxDateTimeInput('getDate')            
	        	  };
	        if($('#alterpopupWindow').jqxValidator('validate')){
			    $("#jqxgrid").jqxGrid('addRow', null, row, "first");
		        // select the first row and clear the selection.
		        $("#jqxgrid").jqxGrid('clearSelection');                        
		        $("#jqxgrid").jqxGrid('selectRow', 0);  
		        $("#alterpopupWindow").jqxWindow('close');
	        }
	    });
	    $('#alterpopupWindow').on('close', function (event) {
			$('#alterpopupWindow').jqxValidator('hide');
		}); 
	</script>
</#if>