<#assign listproductGroupStoreParent = delegator.findList("ProductStoreGroup", null, null, null, null, false)/>

<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxvalidator.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxdata.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxcombobox.js"></script>
<script>
	<#if listproductGroupStoreParent?exists>
		 var productGroupStoreParent = [
		    <#list listproductGroupStoreParent as lpGSP>
		    {
		    	producStoreGroupName : "${lpGSP.productStoreGroupName?if_exists}",
		    	productStoreGroupId : "${lpGSP.productStoreGroupId?if_exists}",
		    },
		    </#list>
		]
		<#else>
			var productGroupStore = [];
	</#if>
	<#if listproductGroupStoreParent?exists>
		 var productGroupStoreAndIdParent = [
	       <#list listproductGroupStoreParent as lpGSP>
	       {
	       	producStoreGroupName : "${lpGSP.productStoreGroupName?if_exists}" + "[" + "${lpGSP.productStoreGroupId?if_exists}" + "]",
	       	productStoreGroupId : "${lpGSP.productStoreGroupId?if_exists}",
	       },
	       </#list>
	   ]
		<#else>
			var productGroupStoreAndIdParent = [];
	</#if>
	var srcparent = (function(){
		for(var i=0;i<productGroupStoreAndIdParent.length;i++){
			if(productGroupStoreAndIdParent[i].productStoreGroupId == '${parameters.productStoreGroupId?if_exists}'){
				productGroupStoreAndIdParent.splice(i,1);
			}
		}
		return productGroupStoreAndIdParent;
	}())
	var sourceproductGroupStoreAndIdParent = {
		localdata : srcparent,
		datatype : "array",
		datafield : [
             {name : "productStoreGroupId"}
         ]
	};
	var dataAdapterproductGroupStoreAndIdParent = new $.jqx.dataAdapter(sourceproductGroupStoreAndIdParent, {
		formatData : function(data){
			if($('#productStoreGroupIdParentAdd').jqxComboBox('searchString') != undefined){
				data.searchKey = $('#productStoreGroupIdParentAdd').jqxComboBox('searchString');
				return data;
			}
		}
	});
</script>
<#assign dataField = "[
      {name :'parentGroupId', type :'String'},
      {name :'fromDate', type :'date', other:'Timestamp'},
      {name:'thruDate',type :'date:nowTimestamp()', other:'Timestamp'},
      {name:'productStoreGroupId', type:'String'}
  ]"/>
<#assign columnlist = "
		{text:'${uiLabelMap.DANo}', cellsrenderer : function(row,column,value){
			var data = $(\"#jqxgrid2\").jqxGrid(\'getrowdata\',row);
			index = data.uid +1;
			return '<span>' + index + '</span>';
			}
		},
		{text :'${uiLabelMap.DAparentgroup}',dataField : 'parentGroupId',cellsrenderer: function(row,column,value){
			var data = $(\"#jqxgrid2\").jqxGrid(\'getrowdata\',row);
			for(var i=0;i<productGroupStore.length;i++){
				if(productGroupStoreParent[i].productStoreGroupId == data.parentGroupId){
					return \"<span><a href= '/delys/control/editProductStoreGroup?productStoreGroupId=\" + data.parentGroupId +\"'>\" + productGroupStoreParent[i].producStoreGroupName + \"[\" + data.parentGroupId + \"]\" + \"</a></span>\";
				}
			}
		}},
		{text: '${uiLabelMap.DACommonFromDate}',dataField: 'fromDate',cellsformat : 'dd/MM/yyyy hh:MM:ss'},
		{text: '${uiLabelMap.DAToDate}',dataField: 'thruDate',cellsformat : 'dd/MM/yyyy hh:MM:ss'}
	"/>
<@jqGrid id="jqxgrid2" filterable ="true" editable = "false" clearfilteringbutton="true" alternativeAddPopup="alterpopupWindowCreate1" columnlist=columnlist dataField=dataField
addrow="true" addType="popup" addrefresh="true" 
	url="jqxGeneralServicer?sname=JQGetListConnectProductStoreGroupParent&productStoreGroupId=${parameters.productStoreGroupId?if_exists}"
	createUrl="jqxGeneralServicer?sname=createProductStoreGroupRollup&jqaction=C" addColumns="parentGroupId;productStoreGroupId;fromDate(java.sql.Timestamp)"
	removeUrl="jqxGeneralServicer?sname=updateProductStoreGroupRollup&jqaction=D" deleteColumn="productStoreGroupId;fromDate(java.sql.Timestamp);thruDate(java.sql.Timestamp);parentGroupId" jqGridMinimumLibEnable="true" deleterow="true"
/>
<div id="alterpopupWindowCreate1" style="display:none">
<div>${uiLabelMap.DAAddToProductStoreGroupChild}</div>
	<div style="overflow: hidden;">
	<form id="alterpopupWindowCreateform1" class="form-horizontal">
		<div class="row-fluid  form-window-content">
			<div class="span12">
				<div class="row-fluid margin-bottom10">
				
					<div class='span5 align-right asterisk'>
			        	${uiLabelMap.DAProductStoreGroup}
			        </div>
					<div class="span7">
						<div id="productStoreGroupIdParentAdd"></div>
					</div>
				</div>
				<div class="row-fluid margin-bottom10">
					
					<div class='span5 align-right asterisk'>
		            	${uiLabelMap.DACommonFromDate}
		            </div>
					<div class="span7 span7edit">
						<div id="fromDateParentAdd" name="fromDateParentAdd"></div>
					</div>
				</div>
			</div>
		</div>
	</form>
	<div class="form-action">
		<div class='row-fluid'>
			<div class="span12 margin-top10">
				<button id="alterCancel1" class='btn btn-danger form-action-button pull-right'><i class='fa-remove'></i> ${uiLabelMap.Cancel}</button>
				<button id="alterSave1" class='btn btn-primary form-action-button pull-right'><i class='fa-check'></i> ${uiLabelMap.Save}</button>
			</div>
		</div>
	</div>
</div>
</div>
<style>
	#fromDateParentAdd > div > input {
		width : 221px !important;
	}
</style>
<script	>
	$("#alterpopupWindowCreate1").jqxWindow({width :480,height :235,resizable: false,isModal: true,autoOpen: false,cancelButton: $("#alterCancel1"),modalOpacity : 0.8,theme : theme});
	$("#productStoreGroupIdParentAdd").jqxComboBox({width : 248,height : 23,source: dataAdapterproductGroupStoreAndIdParent, displayMember: 'producStoreGroupName',valueMember: 'productStoreGroupId',selectedIndex: null,dropDownHeight:100});
	$("#fromDateParentAdd").jqxDateTimeInput({width: 248, height: 23, theme: theme,allowNullDate: true, value: null,formatString : 'dd/MM/yyyy hh:mm:ss'});
	$("#alterpopupWindowCreateform1").jqxValidator({
		rules: [
		        {
		        	input : '#productStoreGroupIdParentAdd',
		        	message : '${uiLabelMap.DAchooseGroupParent}',
		        	action : 'change',
		        	rule : function(){
		        		if(!$('#productStoreGroupIdParentAdd').jqxComboBox('getSelectedItem')){
		        			return false;
		        		}
		        		return true;
		        	}
		        },
		        {
		        	input : '#fromDateParentAdd',
		        	message : '${uiLabelMap.DASetFromDate}',
		        	action : 'change',
		        	rule : function(){
		        		if(!$('#fromDateParentAdd').val()){
		        			return false;
		        		}
		        		return true;
		        	}
		        },
		        {input: '#fromDateParentAdd', message: '${StringUtil.wrapString(uiLabelMap.DARequiredValueGreatherOrEqualToDay)}', action: 'blur', rule: 
					function (input, commit) {
						var now = new Date();
						//now.setHours(0,0,0,0);
		        		if($(input).jqxDateTimeInput('getDate') < now){
		        			return false;
		        		}
		        		return true;
		    		}
				},
		]
	});
	$("#alterSave1").click(function(){
		$("#alterpopupWindowCreateform1").jqxValidator('validate');
	});
	$("#alterpopupWindowCreateform1").on('validationSuccess', function(event){
		var row ={};
		row = {
				parentGroupId : $('#productStoreGroupIdParentAdd').val(),
				productStoreGroupId : '${parameters.productStoreGroupId?if_exists}',
				fromDate : $('#fromDateParentAdd').jqxDateTimeInput('getDate')
		};
		$("#jqxgrid2").jqxGrid('addRow', null, row, "first");
        // select the first row and clear the selection.
        $("#jqxgrid2").jqxGrid('clearSelection');                        
        $("#jqxgrid2").jqxGrid('selectRow', 0);  
        $("#alterpopupWindowCreate1").jqxWindow('close');
	});
	$("#alterpopupWindowCreate1").on('close', function(){
		$("#productStoreGroupIdParentAdd").jqxComboBox('val',null);
		$("#fromDateParentAdd").jqxDateTimeInput('val',null);
	});
</script>
