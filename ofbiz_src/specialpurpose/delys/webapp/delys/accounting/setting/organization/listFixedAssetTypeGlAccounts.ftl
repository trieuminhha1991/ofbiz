<script type="text/javascript">
var dataLFLAT = new Array();
dataLFLAT = [
		<#list listFixedAssetType as acc>
			{
				'fixedAssetTypeId' : '${acc.fixedAssetTypeId?if_exists}',
				'description' : "<span class='custom-style-word'> [ ${acc.fixedAssetTypeId?if_exists} ] " + " - " +  "${StringUtil.wrapString(acc.get('description',locale)?default(''))}</span>"
			},
		</#list>	
		]
var assetTypeRenderer = function (row, column, value) {
	var data = $('#jqxgrid').jqxGrid('getrowdata', row);
    for(i=0;i < dataLFLAT.length; i++){
    	if(dataLFLAT[i].fixedAssetTypeId == data.fixedAssetTypeId){
    		return "<span>" + dataLFLAT[i].description + "</span>";
    	}
    }
    return data.fixedAssetTypeId;
}

var dataLFA = new Array();
dataLFA = [
		<#list listFixedAsset as acc>
			{
				'fixedAssetId' : '${acc.fixedAssetId?if_exists}',
				'description' : "<span class='custom-style-word'>[ ${acc.fixedAssetId?if_exists} ]" + " - " +  "${StringUtil.wrapString(acc.fixedAssetName?default(''))}</span>"
			},
		</#list>	
		]
var fixedAssetRenderer = function (row, column, value) {
	var data = $('#jqxgrid').jqxGrid('getrowdata', row);
    for(i=0;i < dataLFA.length; i++){
    	if(dataLFA[i].fixedAssetId == data.fixedAssetId){
    		return "<span>" + dataLFA[i].description + "</span>";
    	}
    }
    return "";
}

var dataLLGLA = new Array();
dataLLGLA = [
		<#list listLossGlAccount as acc>
			{
				'glAccountId' : '${acc.glAccountId?if_exists}',
				'description' : "<span class='custom-style-word'> [ ${acc.glAccountId?if_exists} ]" + " - " +  "${StringUtil.wrapString(acc.accountName?default(''))}</span>"
			},
		</#list>	
		]
var listLossGlAccountRender = function (row, column, value) {
	var data = $('#jqxgrid').jqxGrid('getrowdata', row);
    for(i=0;i < dataLLGLA.length; i++){
    	if(dataLLGLA[i].glAccountId == data.lossGlAccountId){
    		return "<span>" + dataLLGLA[i].description + "</span>";
    	}
    }
    return "";
}


var dataLPGLA = new Array();
dataLPGLA = [
		<#list listProfitGlAccount as acc>
			{
				'glAccountId' : '${acc.glAccountId?if_exists}',
				'description' : "<span class='custom-style-word'>[ ${acc.glAccountId?if_exists} ]" + " - " +  "${StringUtil.wrapString(acc.accountName?default(''))}</span>"
			},
		</#list>	
		]
var listProfitGlAccountRender = function (row, column, value) {
	var data = $('#jqxgrid').jqxGrid('getrowdata', row);
    for(i=0;i < dataLPGLA.length; i++){
    	if(dataLPGLA[i].glAccountId == data.profitGlAccountId){
    		return "<span>" + dataLPGLA[i].description + "</span>";
    	}
    }
    return "";
}

var dataLAGLA = new Array();
dataLAGLA = [
		<#list listAssetGlAccount as acc>
			{
				'glAccountId' : '${acc.glAccountId?if_exists}',
				'description' : "<span class='custom-style-word'> [ ${acc.glAccountId?if_exists} ]" + " - " +  "${StringUtil.wrapString(acc.accountName?default(''))}</span>"
			},
		</#list>	
		]
var listAssetGlAccountRender = function (row, column, value) {
	var data = $('#jqxgrid').jqxGrid('getrowdata', row);
    for(i=0;i < dataLAGLA.length; i++){
    	if(dataLAGLA[i].glAccountId == data.assetGlAccountId){
    		return "<span>" + dataLAGLA[i].description + "</span>";
    	}
    }
    return "";
}

var dataLDGLA = new Array();
dataLDGLA = [
		<#list listDepGlAccount as acc>
			{
				'glAccountId' : '${acc.glAccountId?if_exists}',
				'description' : "<span class='custom-style-word'> [ ${acc.glAccountId?if_exists} ]" + " - " +  "${StringUtil.wrapString(acc.accountName?default(''))}</span>"
			},
		</#list>	
		]
		
var listDeptGlAccountRender = function (row, column, value) {
	var data = $('#jqxgrid').jqxGrid('getrowdata', row);
    for(i=0;i < dataLDGLA.length; i++){
    	if(dataLDGLA[i].glAccountId == data.depGlAccountId){
    		return "<span>" + dataLDGLA[i].description + "</span>";
    	}
    }
    return "";
}


var dataLADGLA = new Array();
dataLADGLA = [
		<#list listAccDepGlAccount as acc>
			{
				'glAccountId' : '${acc.glAccountId?if_exists}',
				'description' : "<span class='custom-style-word'> [ ${acc.glAccountId?if_exists} ]" + " - " +  "${StringUtil.wrapString(acc.accountName?default(''))}</span>"
			},
		</#list>	
		]
var listAccDeptGlAccountRender = function (row, column, value) {
	var data = $('#jqxgrid').jqxGrid('getrowdata', row);
    for(i=0;i < dataLADGLA.length; i++){
    	if(dataLADGLA[i].glAccountId == data.accDepGlAccountId){
    		return "<span>" + dataLADGLA[i].description + "</span>";
    	}
    }
    return "";
}

</script>

<#assign dataField="[{ name: 'fixedAssetTypeId', type: 'string'},
					 { name: 'fixedAssetId', type: 'string'},
					 { name: 'assetGlAccountId', type: 'string'},
					 { name: 'accDepGlAccountId', type: 'string'},
					 { name: 'depGlAccountId', type: 'string'},
					 { name: 'profitGlAccountId', type: 'string'},
					 { name: 'lossGlAccountId', type: 'string'},
					]"/>

<#assign columnlist="{ text: '${uiLabelMap.fixedAssetTypeId}', datafield: 'fixedAssetTypeId',cellsrenderer:assetTypeRenderer,width : '20%',filtertype : 'checkedlist',createfilterwidget : function(column,columnElement,widget){
				    	var source = {
				    		localdata : dataLFLAT,
				    		datatype : 'array'
				    	};
				    	var filterBoxAdapter = new $.jqx.dataAdapter(source,{autoBind : true});
				    	var uniRecords = filterBoxAdapter.records;
				    	uniRecords.splice(0, 0, '(${StringUtil.wrapString(uiLabelMap.filterselectallstring)})');
				    	widget.jqxDropDownList({source: uniRecords, displayMember: 'description', valueMember: 'fixedAssetTypeId',placeHolder : '${StringUtil.wrapString(uiLabelMap.PleaseChooseAcc?default(''))}',renderer: function (index, label, value) 
											{
												for(i=0;i < dataLFLAT.length; i++){
													if(dataLFLAT[i].fixedAssetTypeId == value){
														return dataLFLAT[i].description;
													}
												}
											    return value;
											}});
				    }},
					 { text: '${uiLabelMap.fixedAssetId}', datafield: 'fixedAssetId',cellsrenderer:fixedAssetRenderer,width : '20%',filtertype : 'checkedlist',createfilterwidget : function(column,columnElement,widget){
				    	var source = {
				    		localdata : dataLFA,
				    		datatype : 'array'
				    	};
				    	var filterBoxAdapter = new $.jqx.dataAdapter(source,{autoBind : true});
				    	var uniRecords = filterBoxAdapter.records;
				    	uniRecords.splice(0, 0, '(${StringUtil.wrapString(uiLabelMap.filterselectallstring)})');
				    	widget.jqxDropDownList({source: uniRecords, displayMember: 'description', valueMember: 'fixedAssetId',placeHolder : '${StringUtil.wrapString(uiLabelMap.PleaseChooseAcc?default(''))}',renderer: function (index, label, value) 
								{
									for(i=0;i < dataLFA.length; i++){
										if(dataLFA[i].fixedAssetId == value){
											return dataLFA[i].description;
										}
									}
								    return value;
								}});
				    }},
					 { text: '${uiLabelMap.assetGlAccountId}', datafield: 'assetGlAccountId',width : '20%', cellsrenderer:listAssetGlAccountRender,filtertype : 'checkedlist',createfilterwidget : function(column,columnElement,widget){
				    	var source = {
				    		localdata : dataLAGLA,
				    		datatype : 'array'
				    	};
				    	var filterBoxAdapter = new $.jqx.dataAdapter(source,{autoBind : true});
				    	var uniRecords = filterBoxAdapter.records;
				    	uniRecords.splice(0, 0, '(${StringUtil.wrapString(uiLabelMap.filterselectallstring)})');
				    	widget.jqxDropDownList({source: uniRecords,dropDownHeight : 200, displayMember: 'description', valueMember: 'glAccountId',placeHolder : '${StringUtil.wrapString(uiLabelMap.PleaseChooseAcc?default(''))}',renderer: function (index, label, value) 
								{
									for(i=0;i < dataLAGLA.length; i++){
										if(dataLAGLA[i].glAccountId == value){
											return dataLAGLA[i].description;
										}
									}
								    return value;
								}});
				    }},
					 { text: '${uiLabelMap.accDepGlAccountId}', datafield: 'accDepGlAccountId',width : '20%', cellsrenderer:listAccDeptGlAccountRender,filtertype : 'checkedlist',createfilterwidget : function(column,columnElement,widget){
				    	var source = {
				    		localdata : dataLADGLA,
				    		datatype : 'array'
				    	};
				    	var filterBoxAdapter = new $.jqx.dataAdapter(source,{autoBind : true});
				    	var uniRecords = filterBoxAdapter.records;
				    	uniRecords.splice(0, 0, '(${StringUtil.wrapString(uiLabelMap.filterselectallstring)})');
				    	widget.jqxDropDownList({source: uniRecords, displayMember: 'description', valueMember: 'glAccountId',placeHolder : '${StringUtil.wrapString(uiLabelMap.PleaseChooseAcc?default(''))}',renderer: function (index, label, value) 
								{
									for(i=0;i < dataLADGLA.length; i++){
										if(dataLADGLA[i].glAccountId == value){
											return dataLADGLA[i].description;
										}
									}
								    return value;
								}});
				    }},
					 { text: '${uiLabelMap.depGlAccountId}', datafield: 'depGlAccountId',width : '20%', cellsrenderer:listDeptGlAccountRender,filtertype : 'checkedlist',createfilterwidget : function(column,columnElement,widget){
				    	var source = {
				    		localdata : dataLDGLA,
				    		datatype : 'array'
				    	};
				    	var filterBoxAdapter = new $.jqx.dataAdapter(source,{autoBind : true});
				    	var uniRecords = filterBoxAdapter.records;
				    	uniRecords.splice(0, 0, '(${StringUtil.wrapString(uiLabelMap.filterselectallstring)})');
				    	widget.jqxDropDownList({source: uniRecords, displayMember: 'description', valueMember: 'glAccountId',placeHolder : '${StringUtil.wrapString(uiLabelMap.PleaseChooseAcc?default(''))}',renderer: function (index, label, value) 
								{
									for(i=0;i < dataLDGLA.length; i++){
										if(dataLDGLA[i].glAccountId == value){
											return dataLDGLA[i].description;
										}
									}
								    return value;
								}});
				    }},
					 { text: '${uiLabelMap.profitGlAccountId}', datafield: 'profitGlAccountId',width : '20%', cellsrenderer:listProfitGlAccountRender,filtertype : 'checkedlist',createfilterwidget : function(column,columnElement,widget){
				    	var source = {
				    		localdata : dataLPGLA,
				    		datatype : 'array'
				    	};
				    	var filterBoxAdapter = new $.jqx.dataAdapter(source,{autoBind : true});
				    	var uniRecords = filterBoxAdapter.records;
				    	uniRecords.splice(0, 0, '(${StringUtil.wrapString(uiLabelMap.filterselectallstring)})');
				    	widget.jqxDropDownList({source: uniRecords, displayMember: 'description', valueMember: 'glAccountId',placeHolder : '${StringUtil.wrapString(uiLabelMap.PleaseChooseAcc?default(''))}',renderer: function (index, label, value) 
								{
									for(i=0;i < dataLPGLA.length; i++){
										if(dataLPGLA[i].glAccountId == value){
											return dataLPGLA[i].description;
										}
									}
								    return value;
								}});
				    }},
					 { text: '${uiLabelMap.lossGlAccountId}', datafield: 'lossGlAccountId',width : '20%', cellsrenderer:listLossGlAccountRender,filtertype : 'checkedlist',createfilterwidget : function(column,columnElement,widget){
				    	var source = {
				    		localdata : dataLLGLA,
				    		datatype : 'array'
				    	};
				    	var filterBoxAdapter = new $.jqx.dataAdapter(source,{autoBind : true});
				    	var uniRecords = filterBoxAdapter.records;
				    	uniRecords.splice(0, 0, '(${StringUtil.wrapString(uiLabelMap.filterselectallstring)})');
				    	widget.jqxDropDownList({source: uniRecords, displayMember: 'description', valueMember: 'glAccountId',placeHolder : '${StringUtil.wrapString(uiLabelMap.PleaseChooseAcc?default(''))}',renderer: function (index, label, value) 
								{
									for(i=0;i < dataLLGLA.length; i++){
										if(dataLLGLA[i].glAccountId == value){
											return dataLLGLA[i].description;
										}
									}
								    return value;
								}});
				    }}
					"/>
	
<@jqGrid  filtersimplemode="true" addType="popup" dataField=dataField columnlist=columnlist clearfilteringbutton="true" showtoolbar="true"
		 addrow="true" deleterow="true" alternativeAddPopup="alterpopupWindow"
		 url="jqxGeneralServicer?sname=JQListFixedAssetTypeyGLAccounts&organizationPartyId=${parameters.organizationPartyId}"
		 removeUrl="jqxGeneralServicer?sname=deleteFixedAssetTypeGlAccount&jqaction=D&organizationPartyId=${parameters.organizationPartyId}"
		 createUrl="jqxGeneralServicer?sname=createFixedAssetTypeGlAccount&jqaction=C&organizationPartyId=${parameters.organizationPartyId}"
		 deleteColumn="fixedAssetTypeId;fixedAssetId;organizationPartyId[${parameters.organizationPartyId}]"
		 addColumns="fixedAssetTypeId;fixedAssetId;assetGlAccountId;accDepGlAccountId;depGlAccountId;profitGlAccountId;lossGlAccountId;organizationPartyId[${parameters.organizationPartyId}]" 
		 />

<div id="alterpopupWindow" class ='hide'>
    <div>${uiLabelMap.accCreateNew}</div>
    <div style="overflow: hidden;">
    	<div class='row-fluid form-window-content'>
			<form id="formAdd">
    			<div class='row-fluid margin-bottom10'>
    				<div class='span5 align-right asterisk'>
    					${uiLabelMap.assetGlAccountId}
    				</div>
    				<div class='span7'>
						<div id="assetGlAccountId">
 						</div>
    				</div>
				</div>
				<div class='row-fluid margin-bottom10'>
    				<div class='span5 align-right'>
    					${uiLabelMap.accDepGlAccountId}
    				</div>
    				<div class='span7'>
						<div id="accDepGlAccountId">
 						</div>
    				</div>
				</div>
				<div class='row-fluid margin-bottom10'>
    				<div class='span5 align-right asterisk'>
    					${uiLabelMap.depGlAccountId}
    				</div>
    				<div class='span7'>
						<div id="depGlAccountId">
 						</div>
    				</div>
				</div>
				<div class='row-fluid margin-bottom10'>
    				<div class='span5 align-right asterisk'>
    					${uiLabelMap.profitGlAccountId}
    				</div>
    				<div class='span7'>
						<div id="profitGlAccountId">
 						</div>
    				</div>
				</div>
				<div class='row-fluid margin-bottom10'>
    				<div class='span5 align-right'>
    					${uiLabelMap.lossGlAccountId}
    				</div>
    				<div class='span7'>
						<div id="lossGlAccountId">
 						</div>
    				</div>
				</div>
				<div class='row-fluid margin-bottom10'>
    				<div class='span5 align-right'>
    					${uiLabelMap.fixedAssetTypeId}
    				</div>
    				<div class='span7'>
						<div id="fixedAssetTypeId">
 						</div>
    				</div>
				</div>
				<div class='row-fluid margin-bottom10'>
    				<div class='span5 align-right'>
    					${uiLabelMap.fixedAssetId}
    				</div>
    				<div class='span7'>
						<div id="fixedAssetId">
 						</div>
    				</div>
				</div>
			</form>
		</div>
		<div class="form-action">
			<div class='row-fluid'>
				<div class="span12 margin-top10">
					<button id="cancel" class='btn btn-danger form-action-button pull-right'><i class='fa-remove'></i> ${uiLabelMap.Cancel}</button>
					<button id="save" class='btn btn-primary form-action-button pull-right'><i class='fa-check'></i> ${uiLabelMap.Save}</button>
				</div>
			</div>
		</div>
    </div>
</div>	

<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxvalidator.js"></script>
<script type="text/javascript">
	$.jqx.theme = 'olbius';  
	theme = $.jqx.theme;
	var action = (function(){
		var initElement = function(){
			$('#assetGlAccountId').jqxDropDownList({dropDownWidth : 250,width  :250,theme: theme, source: dataLAGLA, displayMember: "description", valueMember: "glAccountId",placeHolder : '${StringUtil.wrapString(uiLabelMap.PleaseChooseAcc?default(''))}'});
		    $('#accDepGlAccountId').jqxDropDownList({dropDownWidth : 250,width  :250,theme: theme, source: dataLADGLA, displayMember: "description", valueMember: "glAccountId",placeHolder : '${StringUtil.wrapString(uiLabelMap.PleaseChooseAcc?default(''))}'});
		    $('#depGlAccountId').jqxDropDownList({dropDownWidth : 250,width  :250,theme: theme, source: dataLDGLA, displayMember: "description", valueMember: "glAccountId",placeHolder : '${StringUtil.wrapString(uiLabelMap.PleaseChooseAcc?default(''))}'});
		    $('#profitGlAccountId').jqxDropDownList({dropDownWidth : 250,width  :250,theme: theme,autoDropDownHeight : true, source: dataLPGLA, displayMember: "description", valueMember: "glAccountId",placeHolder : '${StringUtil.wrapString(uiLabelMap.PleaseChooseAcc?default(''))}'});
		    $('#lossGlAccountId').jqxDropDownList({ dropDownWidth : 250,width  :250,theme: theme,source: dataLLGLA, displayMember: "description", valueMember: "glAccountId",placeHolder : '${StringUtil.wrapString(uiLabelMap.PleaseChooseAcc?default(''))}'});
		    $('#fixedAssetTypeId').jqxDropDownList({dropDownWidth : 250,width  :250,theme: theme, source: dataLFLAT, displayMember: "description", valueMember: "fixedAssetTypeId",placeHolder : '${StringUtil.wrapString(uiLabelMap.PleaseChooseAcc?default(''))}'});
		    $('#fixedAssetId').jqxDropDownList({autoDropDownHeight : true,dropDownWidth : 250,width  :250, theme: theme,source: dataLFA, displayMember: "description", valueMember: "fixedAssetId",placeHolder : '${StringUtil.wrapString(uiLabelMap.PleaseChooseAcc?default(''))}'});
		    
		    $("#alterpopupWindow").jqxWindow({
		        width: 500,height : 400, resizable: false,  isModal: true, autoOpen: false, cancelButton: $("#cancel"), modalOpacity: 0.7, theme:theme           
		    });
		}
		
		
		
		var initRules = function(){
			$('#formAdd').jqxValidator({
				rules : [
					{input : '#assetGlAccountId',message : '${StringUtil.wrapString(uiLabelMap.FieldRequiredAccounting?default(''))}',action : 'change,close',rule : function(input){
						var val = input.jqxDropDownList('val');
						if(!val) return false;
						return true;
					}},
					{input : '#depGlAccountId',message : '${StringUtil.wrapString(uiLabelMap.FieldRequiredAccounting?default(''))}',action : 'change,close',rule : function(input){
						var val = input.jqxDropDownList('val');
						if(!val) return false;
						return true;
					}},
					{input : '#profitGlAccountId',message : '${StringUtil.wrapString(uiLabelMap.FieldRequiredAccounting?default(''))}',action : 'change,close',rule : function(input){
						var val = input.jqxDropDownList('val');
						if(!val) return false;
						return true;
					}}
				]
			})
			
		}
		
		var bindEvent = function(){
			// update the edited row when the user clicks the 'Save' button.
		    $("#save").click(function () {
		    	if(!$('#formAdd').jqxValidator('validate')){return;}
		    	var row;
				var fixedAssetId = $('#fixedAssetId').val(); 
				if(fixedAssetId == ""){
					fixedAssetId = "_NA_";
				}
				var fixedAssetTypeId = $('#fixedAssetTypeId').val();
				if(fixedAssetTypeId == ""){
					fixedAssetTypeId = "_NA_";
				}
		        row = {
		        		assetGlAccountId: $('#assetGlAccountId').val(),
		        		accDepGlAccountId: $('#accDepGlAccountId').val(),
		        		depGlAccountId: $('#depGlAccountId').val(),
		        		profitGlAccountId: $('#profitGlAccountId').val(),
		        		lossGlAccountId: $('#lossGlAccountId').val(),
		        		fixedAssetTypeId: fixedAssetTypeId,
		        		fixedAssetId: fixedAssetId        		        		
		        	  };
			   $("#jqxgrid").jqxGrid('addRow', null, row, "first");
			   $("#jqxgrid").jqxGrid('clearSelection');                        
      			$("#jqxgrid").jqxGrid('selectRow', 0);  
		        $("#alterpopupWindow").jqxWindow('close');
		    });
		    
		    $('#alterpopupWindow').on('close',function(){
		    	 $('#assetGlAccountId').jqxDropDownList('clearSelection');
		    	  $('#accDepGlAccountId').jqxDropDownList('clearSelection');
		    	  $('#depGlAccountId').jqxDropDownList('clearSelection');
		    	  $('#profitGlAccountId').jqxDropDownList('clearSelection');
		    	  $('#lossGlAccountId').jqxDropDownList('clearSelection');
		    	  $('#fixedAssetTypeId').jqxDropDownList('clearSelection');
		    	  $('#fixedAssetId').jqxDropDownList('clearSelection');
		    });
		}
		return {
			init : function(){
				initElement();
				bindEvent();
				initRules();
			}
		}
	}());
	
	$(document).ready(function(){
		action.init();
	});
</script>	


