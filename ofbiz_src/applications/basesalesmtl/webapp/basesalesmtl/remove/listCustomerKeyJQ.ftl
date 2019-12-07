<#include "macroTree.ftl"/>

<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxvalidator.js"></script>
<div id="notifyId" style="display: none;">
	<div>
		${uiLabelMap.createSuccessfully}
	</div>
</div>
<div class="row-fluid" id="container"></div>
<script type="text/javascript">
	<#assign localData = []>
	<#if listCustomTimePeriodRoot?exists>
		<#list listCustomTimePeriodRoot as item>
			<#assign localData = localData + [{"customTimePeriodId": "${item.customTimePeriodId}","periodName": "${item.periodName?if_exists}","parentId": "${item.parentPeriodId?default(-1)}","fromDate": "${item.fromDate?if_exists}","thruDate": "${item.thruDate?if_exists}","hasItem": "true"}]/>
		</#list>
	</#if>
</script>

<#assign dataField = "[{name: 'partyIdFrom', type: 'string'}, 
					{name: 'partyId', type: 'string'},
					{name: 'fullName', type: 'string'},
					{name: 'relStatusId', type:'string'},
					{name: 'fromDate', type: 'date', other: 'Date'},
					{name: 'thruDate', type: 'date', other: 'Date'}]"/>
<#assign columnlist = "{text: '${StringUtil.wrapString(uiLabelMap.partyIdFrom)}', dataField: 'partyIdFrom', width: '25%', editable: false,hidden: true}, 
					{text: '${StringUtil.wrapString(uiLabelMap.idCustomer)}', dataField: 'partyId', width: '20%', cellclassname: cellclass},
					{text: '${StringUtil.wrapString(uiLabelMap.customerName)}', dataField: 'fullName', width: '20%', cellclassname: cellclass},
					{text: '${StringUtil.wrapString(uiLabelMap.statusId)}', dataField: 'relStatusId', width: '20%', cellclassname: cellclass},
					{text: '${StringUtil.wrapString(uiLabelMap.CommonFromDate)}', dataField: 'fromDate', cellsformat: 'dd/MM/yyyy', width: '20%', filtertype:'range', columntype: 'datetimeinput', cellclassname: cellclass,
						createeditor: function (row, cellvalue, editor, celltext, cellwidth, cellheight) {
							editor.jqxDateTimeInput({ });
						}
					},
					{text: '${StringUtil.wrapString(uiLabelMap.CommonThruDate)}', dataField: 'thruDate', cellsformat: 'dd/MM/yyyy', width: '20%', filtertype:'range', columntype: 'datetimeinput',cellclassname: cellclass,
						createeditor: function (row, cellvalue, editor, celltext, cellwidth, cellheight) {
							editor.jqxDateTimeInput({ });
						}
					}"/>

<@jqGrid id="jqxgrid" addrow="false" clearfilteringbutton="true" editable="false" alternativeAddPopup="alterpopupWindow1" columnlist=columnlist dataField=dataField
viewSize="25" showtoolbar="true" filtersimplemode="true" showstatusbar="false" isShowTitleProperty="true" addType="popup" customControlAdvance="<div id='dropDownButton3'><div id='jqxTreeYear'></div></div>"
url="jqxGeneralServicer?sname=JQGetListCustomerKey" mouseRightMenu="false" customcontrol1="icon-plus open-sans@${uiLabelMap.DANew}@javascript:openWindowPopupNew();" 
/> 
<style>
	.line-height-25{ line-height: 25px; }
	#jqxTreeYear {
		z-index:100000;
	}
	[id^=panelContentpaneljqxTreeYear]{
	  	visibility: inherit !important;
	}
</style>



<div id="alterpopupWindow1" style="display : none;">
	<div>${uiLabelMap.CommonAdd}</div>
	<div style="overflow: hidden;">
		<form id="CustomKeyForm" class="form-horizontal">
			<div class="row-fluid">
				<div class="row-fluid">
					<div class="span6">
						<div class="row-fluid no-left-margin">
							<label class="span4 align-right line-height-25">${uiLabelMap.CommonFromDate}</label>
							<div class="span8" style="margin-bottom: 10px;">
								<div id="fromDateNewAdd"> </div>
							</div>
						</div>
					</div>
					
					<div class="span6">
						<div class="row-fluid no-left-margin">
							<label class="span4 align-right line-height-25">${uiLabelMap.CommonThruDate}</label>
							<div class="span8" style="margin-bottom: 10px;">
								<div id="thruDateNewAdd"> </div>
							</div>
						</div>
					</div>
				</div>
				
				<div class="row-fluid span12">
					<div style="overflow:hidden;overflow-y:visible; max-height:300px !important; width:930px">
						<div id="jqxgridCustomer"> </div>
					</div>
				</div>
			</div>
			
			<div class="form-action">
				<div class='row-fluid'>
					<div class="span12 margin-top10">
						<button type="button" id="alterCancel1" class='btn btn-danger form-action-button pull-right'><i class='fa-remove'> </i> ${uiLabelMap.Cancel}</button>
						<button type="button" id="alterSave1" class='btn btn-primary form-action-button pull-right'><i class='fa-check'> </i> ${uiLabelMap.Save}</button>
					</div>
				</div>
			</div>
		</form>
	</div>
</div>
<div id="notifyMissInfoId" style="display: none;">
	<div>
		${uiLabelMap.EnterMissInformation}
	</div>
</div>
<style>
.expire {
    color: black;
    background-color: #f0f0f0 !important;
}

</style>

<div id="errorList" style="display: none;">
</div>
<#assign dataFieldsTree = "[{ name: 'customTimePeriodId' },
			                { name: 'periodName' },
			                { name: 'parentId' },
			                { name: 'items'}]">
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxcore.js"></script>
    <script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxdata.js"></script>
    <script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxbuttons.js"></script>
    <script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxscrollbar.js"></script>
    <script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxpanel.js"></script>
    <script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxtree.js"></script>
    <script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxexpander.js"></script>
    <script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxcheckbox.js"></script>
    <script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxlistbox.js"></script>
<script type="text/javascript">

$(function(){
	$("#jqxgrid").on("loadCustomControlAdvance", function(){
		$("#dropDownButton3").jqxDropDownButton({ width: 200, height: 25});
		// Create jqxTree
        <#if localData?exists>
            var data_tree = [<#list localData as item>{<#if item?exists><#assign hasItem = false><#list item?keys as key>
			        		"${key}": "${item[key]}",
			        		<#if key == "hasItem" && item[key] == "true"><#assign hasItem = true></#if>
		                </#list><#if hasItem>"items": [{"label": "Loading..."}]</#if></#if>},</#list>];
        <#else>
        	var data_tree = [];
        </#if>
        // prepare the data
        var source_tree = {
            datatype: "json",
            datafields: ${dataFieldsTree},
            id: "customTimePeriodId",
            localdata: data_tree
        };
        
        var dataAdapter_tree = new $.jqx.dataAdapter(source_tree);
        dataAdapter_tree.dataBind();
        var records_tree = dataAdapter_tree.getRecordsHierarchy('customTimePeriodId', 'parentId', 'items', [{ name: 'periodName', map: 'label'}, { name: 'customTimePeriodId', map: 'value'}]);
    	
		$('#jqxTreeYear').jqxTree({ 
        	source: records_tree, 
        	theme:'energyblue',
        	width: '220px',
        	height: 'auto',
        });
        $('#jqxTreeYear').jqxTree('selectItem', null);
		
		$('#jqxTreeYear').on('expand', function (event) {
	    	var _item = $('#jqxTreeYear').jqxTree('getItem', event.args.element);
	        var label = _item.label;
	        var value = _item.value;
	        var $element = $(event.args.element);
	        var loader = false;
	        var loaderItem = null;
	        var children = $element.find('ul:first').children();
	        $.each(children, function () {
	            var item = $('#jqxTreeYear').jqxTree('getItem', this);
	            if (item && item.label == 'Loading...') {
	                loaderItem = item;
	                loader = true;
	                return false;
	            }
	        });
	        if (loader) {
	            jQuery.ajax({
	                url: 'getCustomTimePeriodJson',
	                type: 'POST',
	                data: {"parentPeriodId": value},
	                success: function (data) {
	                    var items = [];
	                    var dataList = data.listCustomTimePeriod;
	                    for (var i in dataList) {
	                    	var itemData = dataList[i];
	                        var _value = itemData.customTimePeriodId;
	                        var _label = itemData.periodName;
	                        var _id = itemData.customTimePeriodId;
	                        items.push({
	                            label: _label,
	                            id: _id,
	                            value: _value,
	                            "items": [{"label": "Loading..."}]
	                        });
	                    }
	                    $('#jqxTreeYear').jqxTree('addTo', items, $element[0]);
	                    $('#jqxTreeYear').jqxTree('removeItem', loaderItem.element);
	                }
	            });
	        }
	    });
	    
	    $('#jqxTreeYear').on('select', function (event) {
			refreshGridData();
			var args = event.args;
            var item = $('#jqxTreeYear').jqxTree('getItem', args.element);
 			var dropDownContent = '<div style="position: relative; margin-left: 3px; margin-top: 5px;">' + item.label + '</div>';
            $("#dropDownButton3").jqxDropDownButton('setContent', dropDownContent);
        });
	});
});
</script>
<script type="text/javascript">
	$.jqx.theme = 'olbius';
	var theme = theme;
	var cellclass = function (row, columnfield, value) {
		var rowData = $('#jqxgrid').jqxGrid('getrowdata', row);
		var now = new Date();
		now.setHours(0,0,0,0);
		var thruDate = rowData.thruDate;
        if (thruDate != undefined && thruDate < now) {
            return 'expire';
        }
        return 'valid';
	};
	$('#alterpopupWindow1').jqxWindow({ width: 750, height : 405,resizable: true,  isModal: true, autoOpen: false, cancelButton: $("#alterCancel1"), modalOpacity: 0.7 });
	$("#fromDateNewAdd").jqxDateTimeInput({width: '198px', height: '25px', allowNullDate: true, value: null});
	$("#thruDateNewAdd").jqxDateTimeInput({width: '198px', height: '25px', allowNullDate: true, value: null});
	function openWindowPopupNew(){
		$('#alterpopupWindow1').jqxWindow('open');
	}
    function refreshGridData(){
    	var item = $('#jqxTreeYear').jqxTree('getSelectedItem');
    	var valu = item.value;
		var tmpS = $("#jqxgrid").jqxGrid('source');
		tmpS._source.url = "jqxGeneralServicer?sname=JQGetListCustomerKeyItem&hasrequest=Y&customTimePeriodId=" + valu;
		$("#jqxgrid").jqxGrid('source', tmpS);
	}
</script>

<#assign dataFieldCustomers = "[{name: 'partyIdTo', type: 'string'},
								{name: 'fullName', type: 'string'}, 
								{name: 'partyIdFrom', type: 'string'}, 
								{name: 'roleTypeIdFrom', type: 'string'},
								{name: 'roleTypeIdTo', type: 'string'},
								{name: 'partyId', type: 'string'},
								{name: 'fromDate', type: 'date', other: 'Timestamp'},
								{name: 'thruDate', type: 'date', other: 'Timestamp'}]"/>
<#assign columnlistCustomers = "{text: '${StringUtil.wrapString(uiLabelMap.idCustomer)}', dataField: 'partyId', editable: false, width: '17%'},
		{text: '${StringUtil.wrapString(uiLabelMap.customerName)}', dataField: 'fullName', editable: false}, 
		{text: '${StringUtil.wrapString(uiLabelMap.DADistributor)}', dataField: 'partyIdFrom', width: '17%', editable: false},
		{text: '${StringUtil.wrapString(uiLabelMap.roleTypeIdFrom)}', dataField: 'roleTypeIdFrom', hidden : true},
		{text: '${StringUtil.wrapString(uiLabelMap.roleTypeIdTo)}', dataField: 'roleTypeIdTo', hidden : true},
		{text: '${StringUtil.wrapString(uiLabelMap.CommonFromDate)}', dataField: 'fromDate', cellsformat: 'dd/MM/yyyy', filtertype:'range',width: '15%', columntype: 'datetimeinput',
			createeditor: function (row, cellvalue, editor, celltext, cellwidth, cellheight) {
				editor.jqxDateTimeInput({ });
			}
		},
		{text: '${StringUtil.wrapString(uiLabelMap.CommonThruDate)}', dataField: 'thruDate', cellsformat: 'dd/MM/yyyy', filtertype:'range',width: '15%', columntype: 'datetimeinput',
			createeditor: function (row, cellvalue, editor, celltext, cellwidth, cellheight) {
				editor.jqxDateTimeInput({ });
			}
		}"/>
<#assign nowTimestamp = Static["org.ofbiz.base.util.UtilDateTime"].nowTimestamp()/>

<@jqGrid id="jqxgridCustomer" idExisted="true" filtersimplemode="true" viewSize="5" pagesizeoptions="['5', '10', '15', '20', '25', '30', '50', '100']" 
	dataField=dataFieldCustomers columnlist=columnlistCustomers clearfilteringbutton="true" showtoolbar="true" addrow="false" editable="true" 
	url="jqxGeneralServicer?sname=JQGetListCustomerBySupp&partyId=${parameters.userLogin.partyId?if_exists}" filterable="true" width="700" isShowTitleProperty="false" bindresize="false" alternativeAddPopup="alterpopupWindow" addType="popup" 
	deleterow="false" offmode="true" editmode="click" selectionmode="checkbox"/>

<script type="text/javascript">
	$('#fromDateNewAdd').on('change', function (event) {
	    var rowindexes = $('#jqxgridCustomer').jqxGrid('getselectedrowindexes');
	    var fromDateValue = $('#fromDateNewAdd').jqxDateTimeInput('getDate');
	    for(var i=0; i<= rowindexes.length;i++){
	    	$("#jqxgridCustomer").jqxGrid('setcellvalue', rowindexes[i], "fromDate", fromDateValue);
	    }
	});
	
	$('#thruDateNewAdd').on('change', function (event) {
	    var rowindexes1 = $('#jqxgridCustomer').jqxGrid('getselectedrowindexes');
	    var thruDateValue = $('#thruDateNewAdd').jqxDateTimeInput('getDate');
	    for(var j=0; j< rowindexes1.length;j++){
	    	$("#jqxgridCustomer").jqxGrid('setcellvalue', rowindexes1[j], "thruDate", thruDateValue);
	    }
	});
	
	$('#CustomKeyForm').jqxValidator({
		rules : [
			{input: '#thruDateNewAdd', message: '${uiLabelMap.DAThruDateMustNotBeEmpty}', action: 'changed', rule: 
				function (input, commit) {
					if(input.jqxDateTimeInput('getDate')){
						return true;
					}
					return false;
				}
			},
			{input: '#fromDateNewAdd', message: '${uiLabelMap.DAFromDateNotBeEmpty}', action: 'changed', rule: 
				function (input, commit) {
					if(input.jqxDateTimeInput('getDate')){
						return true;
					}
					return false;
				}
			}]
	});
	
	$("#notifyId").jqxNotification({ width: "100%", opacity: 0.9, appendContainer: "#container", autoOpen: false, animationOpenDelay: 800, autoClose: true, autoCloseDelay: 5000 }); 
	$("#errorList").jqxNotification({width: "100%", opacity: 0.9, appendContainer: "#container", autoOpen: false, animationOpenDelay: 800, autoClose: true, autoCloseDelay: 5000 });
	
	$('#alterSave1').click(function(){
		if ($('#CustomKeyForm').jqxValidator('validate')) {
			createCustomerKey();
		}
	});
	
	$('#alterCancel1').click(function(){
		$('#fromDateNewAdd').val('');
		$('#thruDateNewAdd').val('');
		$('#jqxgridCustomer').jqxGrid('refresh');
	});
	function addZero(i) {
	    if (i < 10) {i = "0" + i;}
	    return i;
	}
	function formatFullDate(value) {
		if (value != undefined && value != null && !(/^\s*$/.test(value))) {
			var dateStr = "";
			dateStr += addZero(value.getDate()) + '/';
			dateStr += addZero(value.getMonth()+1) + '/';
			dateStr += addZero(value.getFullYear());
			return dateStr;
		} else {
			return "";
		}
	}
	
	function createCustomerKey() {
		var rows = $('#jqxgridCustomer').jqxGrid('getselectedrowindexes');
		var listCustomerKeys = new Array();
		for(var i = 0; i < rows.length; i++){
			var data = $('#jqxgridCustomer').jqxGrid('getrowdata', rows[i]);
			var map = {};
			map['roleTypeIdFrom'] = 'SUPPLIER';
			map['roleTypeIdTo'] = 'CUSTOMER_GT';
			if(!data.fromDateNew){
				map['fromDate'] = $('#fromDateNewAdd').jqxDateTimeInput('val', 'date').getTime();
			}else{
				map['fromDate'] = data.fromDateNew.getTime();
			}
			if(!data.thruDateNew){
				map['thruDate'] = $('#thruDateNewAdd').jqxDateTimeInput('val', 'date').getTime();
			}else if(map['thruDate'] < $('#fromDateNewAdd').jqxDateTimeInput('val', 'date').getTime()){
				map['thruDate'] = $('#thruDateNewAdd').jqxDateTimeInput('val', 'date').getTime();
			}else{
				map['thruDate'] = data.thruDateNew.getTime();
			}
			map['partyIdTo'] = data.partyId;
			var now = new Date();
			now.setHours(0,0,0,0);
			var thruDate = data.thruDateNew;
	        if (thruDate < now) {
	        	map['relStatusId'] = 'KEYPERRE_CANCELLED';
	        }else{
	        	map['relStatusId'] = 'KEYPERRE_CREATED';
	        }
			listCustomerKeys[i] = map;
		}
		if (listCustomerKeys.length <= 0){
			$("#notifyMissInfoId").jqxNotification("open");
			return false;
		} else {
			listCustomerKeys = JSON.stringify(listCustomerKeys);
			jQuery.ajax({
		        url: "createCustomerKeyss",
		        type: "POST",
		        async: true,
		        data: {listCustomerKeys: listCustomerKeys},
		        success: function(res) {
		        	if(res.ErrorList){
		        		var error = "<div>${uiLabelMap.CommonId} " + res.ErrorList + " ${uiLabelMap.wasCustomerKey}</div>";
		        		$("#errorList").html(error);
		        		$("#errorList").jqxNotification("open");
		        	}else{
			        	$("#notifyId").jqxNotification("open");
		        	}
		        	$('#jqxgrid').jqxGrid('updatebounddata');
		        	$('#jqxgrid').jqxGrid('clearselection');
		        	$('#jqxgridCustomer').jqxGrid('updatebounddata');
		        	$('#jqxgridCustomer').jqxGrid('clearselection');
		        },
		        error: function(e){
		        	console.log(e);
		        }
		    });
		}
		$("#alterpopupWindow1").jqxWindow('close');
	}
	$('#alterpopupWindow1').on('close',function(){
		$('#CustomKeyForm').jqxValidator('hide');
		$('#jqxgridCustomer').jqxGrid('refresh');
	});
</script>
