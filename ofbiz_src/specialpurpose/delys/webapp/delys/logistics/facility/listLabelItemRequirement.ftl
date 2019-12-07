<@jqGridMinimumLib/>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxeditor.js"></script>
<script type="text/javascript" src="/delys/images/js/util/DateUtil.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxvalidator.js"></script> 
<script type="text/javascript" src="/delys/images/js/bootbox.min.js"></script>
<script>
	<#assign facilities = delegator.findList("Facility", null, null, null, null, false)>
	var faciData = new Array();
	<#list facilities as item>
		var row = {};
		<#assign description = StringUtil.wrapString(item.facilityName?if_exists)/>
		row['facilityId'] = '${item.facilityId?if_exists}';
		row['description'] = '${description?if_exists}';
		faciData[${item_index}] = row;
	</#list>
	
	function getDescriptionByFacilityId(facilityId) {
		for ( var x in faciData) {
			if (facilityId == faciData[x].facilityId) {
				return faciData[x].description;
			}
		}
	}
	
	<#assign packingUoms = delegator.findList("Uom", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("uomTypeId", "PRODUCT_LABEL_ITEM"), null, null, null, false)>
	var packingData = new Array();
	<#list packingUoms as item>
		var row = {};
		row['uomId'] = '${item.uomId?if_exists}';
		row['description'] = '${item.description?if_exists}';
		packingData[${item_index}] = row;
	</#list>
	
	function getDescriptionByUomId(uomId) {
		for ( var x in packingData) {
			if (uomId == packingData[x].uomId) {
				return packingData[x].description;
			}
		}
	}
	
	<#assign statuses = delegator.findList("StatusItem", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("statusTypeId", "LABEL_ITEM_STATUS"), null, null, null, false)>
	var statusData = new Array();
	<#list statuses as item>
		var row = {};
		<#assign description = StringUtil.wrapString(item.get("description", locale))/>
		row['statusId'] = '${item.statusId?if_exists}';
		row['description'] = '${description?if_exists}';
		statusData[${item_index}] = row;
	</#list>
	
	function getDescriptionByStatusId(statusId) {
		for ( var x in statusId) {
			if (statusId == statusData[x].statusId) {
				return statusData[x].description;
			}
		}
	}
</script>
	<div id="contentNotificationSendRequestSuccess">
	</div>
	<#assign dataField="[
					{ name: 'facilityId', type: 'string'},
					{ name: 'requirementId', type: 'string'},
					{ name: 'createdDate', type: 'date', other: 'Timestamp' },
					{ name: 'requiredByDate', type: 'date', other: 'Timestamp' },
					{ name: 'requirementStartDate', type: 'date', other: 'Timestamp' },
					{ name: 'description', type: 'string'},
					{ name: 'requirementTypeId', type: 'string'},
					{ name: 'statusId', type: 'string'},
				]"/>
	<#assign columnlist="
					{
					    text: '${uiLabelMap.SequenceId}', sortable: false, filterable: false, editable: false, pinned: true,
					    groupable: false, draggable: false, resizable: false,
					    datafield: '', columntype: 'number', width: 50,
					    cellsrenderer: function (row, column, value) {
					        return '<div style=margin:4px;>' + (value + 1) + '</div>';
					    }
					},
					{ text: '${uiLabelMap.LogRequiremtId}', datafield: 'requirementId', align: 'center',
						cellsrenderer: function (row, column, value){
							return '<span><a href=\"javascript:void(0);\" onclick=\"showPopupDetail('+value+')\">'+value+'</a></span>';
						}
					},
					{ text: '${uiLabelMap.LogRequirePurchaseForFacility}', datafield: 'facilityId', align: 'center',
						cellsrenderer: function (row, column, value){
							if(value){
								return '<span>' + getDescriptionByFacilityId(value) + '<span>';
							}
						}
						
					},
					{ text: '${StringUtil.wrapString(uiLabelMap.LogRequirePurchaseCreatedDate)}', datafield: 'createdDate', align: 'left', columntype: 'datetimeinput', editable: false, cellsformat: 'dd/MM/yyyy', filtertype:'range'},
					{ text: '${uiLabelMap.Status}', dataField: 'statusId',
						cellsrenderer: function (row, column, value){
							if(value){
								return '<span>' + getDescriptionByStatusId(value) + '<span>';
							}
						}
					},
				"/>
	
	<@jqGrid filtersimplemode="true" filterable="true" dataField=dataField columnlist=columnlist editable="false" showtoolbar="true"
		id="jqxgirdLableItem" addrow="true" alternativeAddPopup="alterpopupWindow"  addType="popup" addrefresh="true" addrow="true" filterable="true"
		url="jqxGeneralServicer?sname=JQGetListRequirementPurchaseLable"
		createUrl="jqxGeneralServicer?sname=createRequirementPurchaseLabelItemForFacility&jqaction=C"
		mouseRightMenu="true" contextMenuId="menuSendRequest" selectionmode= "checkbox" rowselectfunction="rowselectfunction2(event);"
		rowunselectfunction="rowunselectfunction2(event);"
		customcontrol1="fa-envelope open-sans@${uiLabelMap.LogSendRequestPurchaseTotalTitle}@javascript:sendRequestPurchaseTotal()"
		addColumns="requirementTypeId;facilityId;requirementByDate(java.sql.Timestamp);requirementStartDate(java.sql.Timestamp);description;listProducts(java.util.List);"
	/>		
					
<div id="alterpopupWindow" class="hide">
	<div class="row-fluid">
		${uiLabelMap.LogAddRequestPurchaseLabelProductTitle}
	</div>
	<div class='form-window-container'>
    	<div class='form-window-content'>
        	<h4 class="row header smaller lighter blue" style="margin: 5px 25px 20px 20px !important;font-weight:500;line-height:20px;font-size:18px;">
                ${uiLabelMap.GeneralInfo}
            </h4>
        	<div class="row-fluid">
        		<input type="hidden" id="facilityId"></input>
        		<div class="span6">
					<div class="row-fluid margin-bottom10">	
	    				<div class="span5" style="text-align: right">
	    					<label class="asterisk"> ${uiLabelMap.LogRequirePurchaseForFacility}: </label>
						</div>
						<div class="span7">
							<div id="originFacilityId" style="width: 100%" class="green-label">
								<div id="jqxgridListFacilityId">
					            </div>
							</div>
						</div>
					</div>
					<div class="row-fluid margin-bottom10">	
	    				<div class="span5" style="text-align: right">
	    					<div> ${uiLabelMap.LogRequirePurchaseByDate}: </div>
						</div>
						<div class="span7">
							<div id="requirementByDate" style="width: 100%"></div>
						</div>
					</div>		
        		</div>
        		<div class="span6 no-left-margin">
	    			<div class="row-fluid margin-bottom10">	
	    				<div class="span5" style="text-align: right">
	    					<div>${uiLabelMap.description}:</div>
						</div>
						<div class="span7">
							<div style="width: 195px; display: inline-block; margin-bottom: 3px;"><input id="description"></input></div><a onclick="showEditor()" style="display: inline-block"><i style="padding-left: 24px" class="icon-edit"></i></a>
						</div>
					</div>
					<div class="row-fluid margin-bottom10">	
	    				<div class="span5" style="text-align: right">
		    				<div> ${uiLabelMap.LogRequirePurchaseBeforeDate}: </div>
						</div>
							<div class="span7"> 
								<div id="requirementStartDate" style="width: 100%"></div>
							</div>
						</div>
		    		</div>
        		<div>
        		    <div style="margin: 20px 0px 10px 20px !important;"><div id="jqxgridProduct"></div></div>
        	    </div>
    	    </div>
    	</div>
    	<div class="form-action">
            <div class='row-fluid'>
                <div class="span12 margin-top20" style="margin-bottom:10px;">
                    <button id="addButtonCancel" class='btn btn-danger form-action-button pull-right' style="margin-right:20px !important;"><i class='fa-remove'></i> ${uiLabelMap.CommonCancel}</button>
                    <button id="addButtonSave" class='btn btn-primary form-action-button pull-right'><i class='fa-check'></i> ${uiLabelMap.CommonSave}</button>
                </div>
            </div>
        </div>
	</div>
</div>

<div id="jqxEditorWindow" style="display: none">
	<div id="windowHeader">
		<span>
		    ${uiLabelMap.Description}
		</span>
	</div>
	<div style="overflow: hidden;" id="windowContent">
		<textarea id="editor">
		</textarea>
		<div class="form-action">
			<div class='row-fluid'>
				<div class="span12 margin-top10">
					<button id="cancelButton" class='btn btn-danger form-action-button pull-right'><i class='fa-remove'></i> ${uiLabelMap.CommonCancel}</button>
					<button id="okButton" class='btn btn-primary form-action-button pull-right'><i class='fa-check'></i> ${uiLabelMap.CommonSave}</button>
				</div>
			</div>
		</div>
	</div>
</div>

<div id='menuSendRequest' style="display:none;">
	<ul>
	    <li><i class="fa-envelope"></i>&nbsp;&nbsp;${uiLabelMap.LogTitleSendRequest}</li>
	    <li><i class="fa-trash"></i>&nbsp;&nbsp;${uiLabelMap.DSDeleteRowGird}</li>
	</ul>
</div>

<div id="jqxNotificationSendRequestSuccess" >
	<div id="notificationSendRequestSuccess">
	</div>
</div>

<#include "listLabelItemProduct.ftl" />
<script>
	$('#document').ready(function(){
		loadFacility();
		$("#jqxEditorWindow").jqxWindow({
			maxWidth: 640, minWidth: 640, minHeight: 360, maxHeight: 360, resizable: true,  isModal: true, autoOpen: false, initContent : function(){
				$('#editor').jqxEditor({
		            height: '85%',
		            width: '100%',
		            theme: theme,
		        });
			},
		});
		$("#description").jqxInput({placeHolder: ". . .", height: 20, width: '195'});
	});
	$("#jqxNotificationSendRequestSuccess").jqxNotification({ width: "100%", appendContainer: "#contentNotificationSendRequestSuccess", opacity: 0.9, autoClose: true, template: "success" });
	$("#menuSendRequest").jqxMenu({ width: 170, autoOpenPopup: false, mode: 'popup', theme: theme});
	$("#alterpopupWindow").jqxWindow({
		maxWidth: 1500, minWidth: 950, height:520 ,minHeight: 300, maxHeight: 1200, resizable: true,  isModal: true, autoOpen: false, cancelButton: $("#addButtonCancel"), modalOpacity: 0.7, theme:theme           
	});
	
	$("#okButton").click(function () {
		var des = $('#editor').val();
		var tmp = des.substring(5, des.length - 6);
		$("#description").val(tmp);
		$("#jqxEditorWindow").jqxWindow('close');
	});
	$("#cancelButton").click(function () {
		$("#jqxEditorWindow").jqxWindow('close');
	});
	function showEditor(){
		$("#jqxEditorWindow").jqxWindow('open');
	}
	
	$("#facilityId").jqxInput();
	$("#originFacilityId").jqxDropDownButton({width: 200});
	$('#originFacilityId').jqxDropDownButton('setContent', '${StringUtil.wrapString(uiLabelMap.LogPleaseSelect)}'); 
	$("#requirementByDate").jqxDateTimeInput({height: '25px', formatString: 'dd/MM/yyyy', showFooter:true,
	     clearString:'Clear'});
	$("#requirementStartDate").jqxDateTimeInput({height: '25px', formatString: 'dd/MM/yyyy', showFooter:true,
	     clearString:'Clear'});
	function loadFacility(){
    	var listFacility;
    	$.ajax({
			url: "loadListFacility",
			type: "POST",
			data: {},
			dataType: "json",
			success: function(data) {
			}
		}).done(function(data) {
			listFacility = data["listFacility"];
			bindingDataToJqxGirdFacilityList(listFacility);
		});
    }
    
    function bindingDataToJqxGirdFacilityList(listFacility){
 	    var sourceP2 =
 	    {
 	        datafields:[{name: 'facilityId', type: 'string'},
 	            		{name: 'facilityName', type: 'string'},
         				],
 	        localdata: listFacility,
 	        datatype: "array",
 	    };
 	    var dataAdapterP2 = new $.jqx.dataAdapter(sourceP2);
 	    $("#jqxgridListFacilityId").jqxGrid({
 	        source: dataAdapterP2,
 	        filterable: true,
	        showfilterrow: true,
	        theme: theme,
	        autoheight:true,
	        pageable: true,
 	        columns: [{text: '${uiLabelMap.FacilityId}', datafield: 'facilityId'},
 	          			{text: '${uiLabelMap.DAFacilityName}', datafield: 'facilityName'},
 	        		]
 	    });
    }
    
    $("#jqxgridListFacilityId").on('rowselect', function (event) {
        var args = event.args;
        var row = $("#jqxgridListFacilityId").jqxGrid('getrowdata', args.rowindex);
        var dropDownContent = '<div style=\"position: relative; margin-left: 3px; margin-top: 5px;\">'+ getDescriptionByFacilityId(row['facilityId']) +'</div>';
        $('#originFacilityId').jqxDropDownButton('setContent', dropDownContent);
        $('#facilityId').val(row['facilityId']);
    });
	
	function addRequirementPurchaseLabelItem(){
		$("#alterpopupWindow").jqxWindow('open');
	}
	
	$('#alterpopupWindow').on('open', function (event) {
//		loadUomIdByLabelItemProduct();
	}); 
	
	var listUomIdByLabelItemProduct = [];
	function loadUomIdByLabelItemProduct(){
		listUomIdByLabelItemProduct = [];
		$.ajax({
			url: "loadUomIdByLabelItemProduct",
			type: "POST",
			data: {},
			dataType: "json",
			success: function(data) {
			}
		}).done(function(data) {
			listUomIdByLabelItemProduct = data["listUomIdByLabelItemProduct"];
		});
	}
	
	 $("#menuSendRequest").on('itemclick', function (event) {
        var args = event.args;
        var rowindex = $("#jqxgirdLableItem").jqxGrid('getselectedrowindex');
        if ($.trim($(args).text()) == "${StringUtil.wrapString(uiLabelMap.LogTitleSendRequest)}") {
            bootbox.confirm("${uiLabelMap.AreYouSureSent}",function(result){ 
    			if(result){	
    				var dataRecord = $("#jqxgirdLableItem").jqxGrid('getrowdata', rowindex);
    	            var reqId = dataRecord.requirementId;
    				sendRequirement({
    	            	requirementId: reqId,
    	            	roleTypeId: 'QA_QUALITY_MANAGER',
    					sendMessage: '${uiLabelMap.NewPurchaseRequirementMustBeApprove}',
    					action: "getListPurchaseRequiremetLabelItemToQA",
    				}, 'sendPurchaseRequirement', 'jqxgrid');
    			}
    		});
        }
    });
	 
	function sendRequirement(jsonObject, url, jqxgrid) {
	    jQuery.ajax({
	        url: url,
	        type: "POST",
	        data: jsonObject,
	        success: function(res) {
	        	$("#jqxgirdLableItem").jqxGrid('updatebounddata');
	        	$("#notificationSendRequestSuccess").text('${StringUtil.wrapString(uiLabelMap.sentSuccessfully)}');
				$("#jqxNotificationSendRequestSuccess").jqxNotification('open');
	        }
	    });
	}
	 
	var requirementIdTotal = [];
	function sendRequestPurchaseTotal(){
		if(requirementIdTotal.length == 0){
			bootbox.alert("${StringUtil.wrapString(uiLabelMap.LogNotSelectedRequirementAppore)}");
		}else{
			bootbox.confirm("${uiLabelMap.AreYouSureSent}",function(result){ 
    			if(result){	
    				var requirementData = [];
    				for(var i in requirementIdTotal){
    					requirementData.push(requirementIdTotal[i].requirementId);
    				}
    				
    				sendRequirementTotal({
    					requirementData: requirementData,
    	            	roleTypeId: 'QA_QUALITY_MANAGER',
    					sendMessage: '${uiLabelMap.NewPurchaseRequirementMustBeApprove}',
    					action: "getListPurchaseRequiremetLabelItemToQA",
    				}, 'sendPurchaseRequirementByRequirementData', 'jqxgrid');
    			}
    		});
		}
	} 
	
	function sendRequirementTotal(jsonObject, url, jqxgrid) {
	    jQuery.ajax({
	        url: url,
	        type: "POST",
	        data: jsonObject,
	        success: function(res) {
        		$("#jqxgirdLableItem").jqxGrid('updatebounddata');
	        	$("#notificationSendRequestSuccess").text('${StringUtil.wrapString(uiLabelMap.sentSuccessfully)}');
				$("#jqxNotificationSendRequestSuccess").jqxNotification('open');
				requirementIdTotal = [];
	        }
	    });
	}
	
	function rowselectfunction2(event){
		var args = event.args;
		if(typeof event.args.rowindex != 'number'){
            var rowBoundIndex = args.rowindex;
	    	if(rowBoundIndex.length == 0){
	    		requirementIdTotal = [];
	    	}else{
	    		for ( var x in rowBoundIndex) {
		    		var rowID = $('#jqxgirdLableItem').jqxGrid('getRowId', rowBoundIndex[x]);
    		        var data = $('#jqxgirdLableItem').jqxGrid('getrowdatabyid', rowID);
    		        requirementIdTotal.push(data);
				}
	    	}
        }else{
        	var tmpArray = event.args.rowindex;
	        var rowID = $('#jqxgirdLableItem').jqxGrid('getRowId', tmpArray);
	        var data = $('#jqxgirdLableItem').jqxGrid('getrowdatabyid', rowID);
	        requirementIdTotal.push(data);
        }
    }
	
	function rowunselectfunction2(event){
		var args = event.args;
	    if(typeof event.args.rowindex != 'number'){
	    	var rowBoundIndex = args.rowindex;
	    	for ( var x in rowBoundIndex) {
	    		var rowID = $('#jqxgirdLableItem').jqxGrid('getRowId', rowBoundIndex[x]);
	    		var data = $('#jqxgirdLableItem').jqxGrid('getrowdatabyid', rowID);
	    		var ii = requirementIdTotal.indexOf(data);
	    		requirementIdTotal.splice(ii, 1);
			}
	    }else{
	    	var tmpArray = event.args.rowindex;
	        var rowID = $('#jqxgirdLableItem').jqxGrid('getRowId', tmpArray);
	        var data = $('#jqxgirdLableItem').jqxGrid('getrowdatabyid', rowID);
	        var ii = requirementIdTotal.indexOf(data);
    		requirementIdTotal.splice(ii, 1);
	    }
    }
	
</script>