<script type="text/javascript" src="/aceadmin/assets/js/bootbox.min.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxvalidator.js"></script>
<#include "../item/uiLabelMap.ftl"/>
<#assign statuses = delegator.findList("StatusItem", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition(Static["org.ofbiz.base.util.UtilMisc"].toMap("statusTypeId", "PURCHASING_PLAN")), null, null, null, false) />
<script>
$.jqx.theme = 'olbius';  
var theme = $.jqx.theme;
var dataLU = [<#list listUom as uom>{uomId:"${uom.uomId}", description:"${StringUtil.wrapString(uom.description?default(""))}"},</#list>]
var dataLC = [<#list listCurrency as item>{uomId : "${item.uomId}",description : "${StringUtil.wrapString(item.uomId + ' : '+ item.description?default(""))}"},</#list>];
var statusData = [<#list statuses as item>{statusId : "${item.statusId}",description : "${item.description}"},</#list>];
</script>
<#include "initRowDetailPlanning.ftl"/>
<#assign id="jqxgridsupplier"/>
<#assign department = Static["com.olbius.util.PartyUtil"].getDepartmentOfEmployee(delegator, userLogin.getString("partyId"))?default("") />
<#if department != "">
	<#assign departmentId = department.getString("partyIdFrom")/>
	<#assign departmentName = Static["com.olbius.util.PartyUtil"].getPartyName(delegator, departmentId)?default(departmentId)/>
</#if>
<#if planTypeId?exists && planTypeId == "ORGANIZATION_PLAN">
	<#assign serviceGetName = "JQGetListPOPlanOrg"/>
<#else>
	<#assign serviceGetName = "JQGetListPOPlan"/>
</#if>
<#assign company = Static["com.olbius.util.PartyUtil"].getParentOrgOfDepartmentCurr(delegator, departmentId)/>
<#if company?exists && company.partyIdFrom?exists>
	<#assign companyId = company.partyIdFrom/>
	<#assign companyName = Static["com.olbius.util.PartyUtil"].getPartyName(delegator, companyId)/>
	<#assign partyAcctg = delegator.findOne("PartyAcctgPreference", Static["org.ofbiz.base.util.UtilMisc"].toMap("partyId", companyId), false) />
	<#assign baseUomId = partyAcctg.baseCurrencyUomId?if_exists/>
</#if>

<#assign dataField="[{ name: 'partyId', type: 'string' },
					 { name: 'groupName', type: 'string' },
					 { name: 'year', type: 'string' }, 
					 { name: 'statusId', type: 'string'},
					 { name: 'estimatedCost', type: 'string'},
					 { name: 'currencyUomId', type: 'string'},
					 { name: 'reason', type: 'string'}]
					 "/>
<#assign columnlist = "{ text: '${uiLabelMap.groupName}', width:250, datafield: 'groupName', editable: false},
					  { text: '${uiLabelMap.Year}', width:100, datafield: 'year', editable: false},
					  { text: '${uiLabelMap.statusId}', width:200, datafield: 'statusId', 
						  cellsrenderer: function(row, column, value){
							  var str = \"<div style='overflow: hidden; text-overflow: ellipsis; padding-bottom: 2px; text-align: left; margin-right: 2px; margin-left: 4px; margin-top: 4px;'>\";
							  for(var x in statusData){
								  statusData[x].statusId == value;
								  str += statusData[x].description;
								  str += \"</div>\"; 
								  return str;
							  }
							  return str + value + \"</div>\";
						  }
					  },
					  { text: '${uiLabelMap.estimatedCost}', datafield: 'estimatedCost', width: 150, editable: false},
					  { text: '${uiLabelMap.DACurrency}', datafield: 'currencyUomId', width: 150, editable: false,
						 cellsrenderer: function(row, column, value){
					  		var str = '<div style=\"margin-top:4px;margin-left:4px\">';
					  		for(var x in dataLC){
					  			if(dataLC[x].uomId == value){
					  				str += dataLC[x].description + '</div>';
					  				return str;
					  			}
					  		}
					  		str += value + '</div>';
					  		return str;
					  	}
					  },
					  { text: '${uiLabelMap.Reason}', datafield: 'reason'},
					 	"/>		

<@jqGrid url="jqxGeneralServicer?sname=${serviceGetName}" dataField=dataField columnlist=columnlist filterable="true" clearfilteringbutton="true"
		 showtoolbar="true" alternativeAddPopup="alterpopupWindow" filtersimplemode="true" addrow="true" addType="popup" addrow="true" addType="popup"
	     editable="true" editrefresh="true" id=id autorowheight="true" initrowdetails="true" initrowdetailsDetail=initrowdetailsDetail rowdetailsheight="400"
		 createUrl="jqxGeneralServicer?sname=createPOPlan&jqaction=C" addColumns="year;partyId;estimatedCost(java.math.BigDecimal);reason;products(java.util.List)"
	 />

<#assign year = Static["com.olbius.util.CommonUtil"].getCurrentYear(context.timeZone, context.locale)?default("")/>
<#include "popupPlanningPO.ftl"/>
<script>
	/* grid to show product chosen */
	var productChosenGrid = $("#jqxgridProductChosen");
	CKEDITOR.replace( 'reason', {enableTabKeyToolsv: true, skin: 'office2013', height: "130px"});
	var sourceLC =
	{
	    localdata: dataLC,
	    datatype: "array"
	};
	var dataAdapterLC = new $.jqx.dataAdapter(sourceLC);
	$('#currencyUomContainer').jqxDropDownList({theme:theme, source: dataAdapterLC,  width: '265px', displayMember: "description", valueMember: "uomId", filterable: true});
	for(var x in dataLC){
		if(dataLC[x].uomId == "VND"){
			$('#currencyUomContainer').jqxDropDownList("selectIndex", parseInt(x));
		}
	}
	$("#estimatedBudget").jqxNumberInput({ width: '265px', disabled:true, height: '25px', max : 999999999999, digits: 12 });
	$("#alterpopupWindow").jqxWindow({
		theme: 'olbius', width: 864, maxWidth: 864, height:576, isModal: true, autoOpen: false, modalOpacity: 0.7, modalZIndex: 1000,
    });

	$("#alterpopupWindow").jqxValidator({
	   	rules: []
	});
	$("#alterpopupWindow").on("close", function(){
		$(this).jqxValidator("hide");
	});
	
    // update the edited row when the user clicks the 'Save' button.
    $("#alterSave").click(function () {
    	if(!$("#alterpopupWindow").jqxValidator("validate")){
    		return;
    	}
    	var i = $("#currencyUomContainer").jqxDropDownList("getSelectedItem");
    	var currency = i && i.value ? i.value : "";
    	var products = JSON.stringify($("#jqxgridProductChosen").jqxGrid("getboundrows"));
        var row = { 
    		year: $("#yearId").val(),
    		partyId: $("#departmentId").val(),
    		currencyUomId: currency,
    		estimatedCost: $("#estimatedBudget").jqxNumberInput("val"),
    		products: products,
    		reason : $("#reason").val()
	   };
       var grid = $("#${id}");
	   grid.jqxGrid('addRow', null, row, "first");
        // select the first row and clear the selection.
	   grid.jqxGrid('clearSelection');                        
	   grid.jqxGrid('selectRow', 0);  
       $("#alterpopupWindow").jqxWindow('close');
    });
    function processTotalProduct(){
    	$.ajax({
    		url: "calculatePlanItem",
    		type: "POST",
    		success: function(res){
    			if(res && res.results){
    				var obj, db;
    				for(var x in res.results){
    					db = res.results[x];
    					obj = {
							productId: db.productId,
					    	productName: db.productName,
					   		quantity: db.quantity,
					   		quantityUomId : db.quantityUomId
    					};
    					renderGridProduct(productChosenGrid, obj);
    				}
    			}
    		}
    	});
    }
</script>
<#include "../item/popupProduct.ftl"/>
<script src="/delys/images/js/procurement/gridAction.js"></script> 