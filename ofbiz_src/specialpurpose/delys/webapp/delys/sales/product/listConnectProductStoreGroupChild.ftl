<#assign listproductGroupStore = delegator.findList("ProductStoreGroup", null, null, null, null, false)/>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxcombobox.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxdata.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxvalidator.js"></script>
<script>
 	<#if listproductGroupStore?exists>
 		 var productGroupStore = [
             <#list listproductGroupStore as lpGS>
             {
             	producStoreGroupName : "${lpGS.productStoreGroupName?if_exists}",
             	productStoreGroupId : "${lpGS.productStoreGroupId?if_exists}",
             },
             </#list>
         ]
 		<#else>
 			var productGroupStore = [];
 	</#if>
 	<#if listproductGroupStore?exists>
		 var productGroupStoreAndId = [
	        <#list listproductGroupStore as lpGS>
	        {
	        	producStoreGroupName : "${lpGS.productStoreGroupName?if_exists}" + "[" + "${lpGS.productStoreGroupId?if_exists}" + "]",
	        	productStoreGroupId : "${lpGS.productStoreGroupId?if_exists}",
	        },
	        </#list>
	    ]
		<#else>
			var productGroupStoreAndId = [];
	</#if>
	var srcchild = (function(){
		for(var i=0;i<productGroupStoreAndId.length;i++){
			if(productGroupStoreAndId[i].productStoreGroupId == '${parameters.productStoreGroupId?if_exists}'){
				productGroupStoreAndId.splice(i,1);
			}
		}
		return productGroupStoreAndId;
	}())
 	var sourceproductGroupStore = {
 			localdata : srcchild,
 			datatype : "array",
 			dataField : [
             	{name:'productStoreGroupId'}
         ]
 	};
 	var dataAdapterproductGroupStore = new $.jqx.dataAdapter(sourceproductGroupStore, {
 		formatData : function(data){
 			if($('#productStoreGroupIdChildAdd').jqxComboBox('searchString') !== undefined){
				data.searchKey = $('#productStoreGroupIdChildAdd').jqxComboBox('searchString');
				return data;
			}
 		}
 	});
</script>
<#assign dataField = 
	"[
		{name : 'productStoreGroupId', type: 'String'},
		{name : 'fromDate', type : 'date', other : 'Timestamp'},
		{name : 'thruDate', type : 'date:nowTimestamp()', other : 'Timestamp'},
		{name : 'parentGroupId', type : 'String'}
]"/>
<#assign columnlist = "
			{text: '${uiLabelMap.DANo}',cellsrenderer : function(row,column,value){
				var data = $(\"#jqxgrid1\").jqxGrid(\'getrowdata\',row);
				var index = data.uid +1;
				return '<span>' + index + '</span>';
			}},
			{text: '${uiLabelMap.DAProductStoreGroupId}',dataField:'productStoreGroupId',cellsrenderer: function(row,column,value){
				var data = $(\"#jqxgrid1\").jqxGrid(\'getrowdata\',row);
				for(var i=0;i<productGroupStore.length;i++){
					if(productGroupStore[i].productStoreGroupId == data.productStoreGroupId){
						return \"<span><a href= '/delys/control/editProductStoreGroup?productStoreGroupId=\" + data.productStoreGroupId +\"'>\" + productGroupStore[i].producStoreGroupName + \"[\" + data.productStoreGroupId + \"]\" + \"</a></span>\";
					}
				}
			}},
			{text:'${uiLabelMap.DACommonFromDate}', dataField:'fromDate',cellsformat : 'dd/MM/yyyy hh:MM:ss'},
			{text:'${uiLabelMap.DAToDate}', dataField:'thruDate'}
	
	
"/>
<@jqGrid id="jqxgrid1" filterable ="true" editable = "false" clearfilteringbutton="true" alternativeAddPopup="alterpopupWindowCreate" columnlist=columnlist dataField=dataField addrow="true" addType="popup" addrefresh="true"
	url="jqxGeneralServicer?sname=JQGetListConnectProductStoreGroup&parentGroupId=${parameters.productStoreGroupId?if_exists}"
	createUrl="jqxGeneralServicer?sname=createProductStoreGroupRollup&jqaction=C" addColumns="parentGroupId;productStoreGroupId;fromDate(java.sql.Timestamp)"
	removeUrl="jqxGeneralServicer?sname=updateProductStoreGroupRollup&jqaction=D" deleteColumn="productStoreGroupId;fromDate(java.sql.Timestamp);thruDate(java.sql.Timestamp);parentGroupId" jqGridMinimumLibEnable="true" deleterow="true"
/>
<div id="alterpopupWindowCreate" style="display:none">
	<div>${uiLabelMap.DAAddToProductStoreGroupChild}</div>
		<div style="overflow: hidden;">
		<form id="alterpopupWindowCreateform" class="form-horizontal">
			<div class="row-fluid  form-window-content">
				<div class="span12">
					<div class="row-fluid margin-bottom10">
					
						<div class='span5 align-right asterisk'>
				        	${uiLabelMap.DAProductStoreGroup}
				        </div>
						<div class="span7">
							<div id="productStoreGroupIdChildAdd"></div>
						</div>
					</div>
					<div class="row-fluid margin-bottom10">
						
						<div class='span5 align-right asterisk'>
			            	${uiLabelMap.DACommonFromDate}
			            </div>
						<div class="span7 span7edit">
							<div id="fromDateChildAdd" name="fromDateChildAdd"></div>
						</div>
					</div>
				</div>
			</div>
		</form>
		<div class="form-action">
			<div class='row-fluid'>
				<div class="span12 margin-top10">
					<button id="alterCancel" class='btn btn-danger form-action-button pull-right'><i class='fa-remove'></i> ${uiLabelMap.Cancel}</button>
					<button id="alterSave" class='btn btn-primary form-action-button pull-right'><i class='fa-check'></i> ${uiLabelMap.Save}</button>
				</div>
			</div>
		</div>
	</div>
</div>
<style>
	#fromDateChildAdd > div > input{
		width : 221px !important;
	}
</style>
<script>
	$.jqx.theme = 'olbius';
	var theme = $.jqx.theme; 
	$("#alterpopupWindowCreate").jqxWindow({width :480,height :235,resizable: false,isModal: true,autoOpen: false,cancelButton: $("#alterCancel"),modalOpacity : 0.8,theme : theme});
	$("#productStoreGroupIdChildAdd").jqxComboBox({width : 248,height : 23,source: dataAdapterproductGroupStore,displayMember: 'producStoreGroupName',valueMember: 'productStoreGroupId',selectedIndex: null,dropDownHeight:100});
	$("#fromDateChildAdd").jqxDateTimeInput({width: 248, height: 23, theme: theme ,allowNullDate: true, value: null,formatString : 'dd/MM/yyyy HH:mm:ss'});
	$("#alterpopupWindowCreateform").jqxValidator({
		rules : [
		         {
		        	 input : '#productStoreGroupIdChildAdd',
		        	 message : '${uiLabelMap.DAchooseGroupChild}',
		        	 action : 'change',
		        	 rule : function(input,commit){
		        		 if(!$('#productStoreGroupIdChildAdd').jqxComboBox('getSelectedItem')){
		        			 return false;
	        			 }
		        		 return true;
	        		 }
		        },
//		        {
//		        	input : '#fromDateChildAdd',
//		        	message : '${uiLabelMap.DAwrongDate}',
//		        	action : 'change,close,keyup',
//		        	rule : function(input,commit){
//		        		var nowtime = new Date('${nowTimestamp}');
//		        		var timecurrent =  $('#fromDateChildAdd').jqxDateTimeInput('val');
//		        		var timecurrentsplit = timecurrent.split("/");
//		        		var nowtimeTmp = new Date(1900 + nowtime.getYear(),nowtime.getMonth(),nowtime.getDate());
//		        		var temp = (1900 + nowtime.getYear()).toString() + "/" +( nowtime.getMonth() +1 ).toString() + "/"+ nowtime.getDate().toString();
//		        		var temsplit = temp.split("/");
//		        		console.log(timecurrentsplit + "||" + temsplit);
//		        		if((timecurrentsplit[0]==temsplit[2])&&(timecurrentsplit[1]==temsplit[1])&&(timecurrentsplit[2]==temsplit[0])){
//		        			return true;
//		        		}
//		        		return false;
//	        		}
//		        },
		        {input: '#fromDateChildAdd', message: '${StringUtil.wrapString(uiLabelMap.DARequiredValueGreatherOrEqualToDay)}', action: 'blur', rule: 
					function (input, commit) {
						var now = new Date();
						//now.setHours(0,0,0,0);
		        		if($(input).jqxDateTimeInput('getDate') < now){
		        			return false;
		        		}
		        		return true;
		    		}
				},
		        {
		        	input : '#fromDateChildAdd',
		        	message : '${uiLabelMap.DAsetFromDate}',
		        	action : 'change,close,keyup',
		        	rule : function(input,commit){
		        		if(!$('#fromDateChildAdd').val()){
		        			return false;
		        		}
		        		return true;
		        	}
		        }
         ]
	});
	
	$("#alterSave").click(function(){
		$("#alterpopupWindowCreateform").jqxValidator('validate');
	});
	$("#alterpopupWindowCreateform").on('validationSuccess', function(event){
		var row ={};
		row = {
				productStoreGroupId :$('#productStoreGroupIdChildAdd').val(),
				parentGroupId : '${parameters.productStoreGroupId?if_exists}',
				fromDate : $('#fromDateChildAdd').jqxDateTimeInput('getDate')
		};
		$("#jqxgrid1").jqxGrid('addRow', null, row, "first");
        // select the first row and clear the selection.
        $("#jqxgrid1").jqxGrid('clearSelection');                        
        $("#jqxgrid1").jqxGrid('selectRow', 0);  
        $("#alterpopupWindowCreate").jqxWindow('close');
	});
	$("#alterpopupWindowCreate").on('close', function(){
		$("#productStoreGroupIdChildAdd").jqxComboBox('val', null);
	});
</script>
