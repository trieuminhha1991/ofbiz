<script type="text/javascript">

var dataLLGLA = new Array();
dataLLGLA = [
		{
			'glAccountId' : '',
			'description' : ''	
		},
	<#list listLossGlAccount as loss>
		{
			'glAccountId' : '${loss.glAccountId?if_exists}',
			'description' : '${StringUtil.wrapString(loss.accountName?default(''))}'
		},
	</#list>
];

var listLossGlAccountRender = function (row, column, value) {
	var data = $('#jqxgridFatgla').jqxGrid('getrowdata', row);
    for(i=0;i < dataLLGLA.length; i++){
    	if(dataLLGLA[i].glAccountId == data.lossGlAccountId){
    		return "<span>" + dataLLGLA[i].description + "</span>";
    	}
    }
    return "";
}


var dataLPGLA = new Array();
dataLPGLA = [
		{
			'glAccountId' : '',
			'description' : ''	
		},
		<#list listProfitGlAccount as loss>
			{
				'glAccountId' : '${loss.glAccountId?if_exists}',
				'description' : '${StringUtil.wrapString(loss.accountName?default(''))}'
			},
		</#list>
];
var listProfitGlAccountRender = function (row, column, value) {
	var data = $('#jqxgridFatgla').jqxGrid('getrowdata', row);
    for(i=0;i < dataLPGLA.length; i++){
    	if(dataLPGLA[i].glAccountId == data.profitGlAccountId){
    		return "<span>" + dataLPGLA[i].description + "</span>";
    	}
    }
    return "";
}

var dataLAGLA = new Array();
dataLAGLA = [
		{
			'glAccountId' : '',
			'description' : ''	
		},
		<#list listAssetGlAccount as loss>
			{
				'glAccountId' : '${loss.glAccountId?if_exists}',
				'description' : '${StringUtil.wrapString(loss.accountName?default(''))}'
			},
		</#list>
];
var listAssetGlAccountRender = function (row, column, value) {
	var data = $('#jqxgridFatgla').jqxGrid('getrowdata', row);
    for(i=0;i < dataLAGLA.length; i++){
    	if(dataLAGLA[i].glAccountId == data.assetGlAccountId){
    		return "<span>" + dataLAGLA[i].description + "</span>";
    	}
    }
    return "";
}

var dataLDGLA = new Array();
	dataLDGLA = [
				{
					'glAccountId' : '',
					'description' : ''	
				},
				<#list listDepGlAccount as loss>
					{
						'glAccountId' : '${loss.glAccountId?if_exists}',
						'description' : '${StringUtil.wrapString(loss.accountName?default(''))}'
					},
				</#list>
		];

var listDeptGlAccountRender = function (row, column, value) {
	var data = $('#jqxgridFatgla').jqxGrid('getrowdata', row);
    for(i=0;i < dataLDGLA.length; i++){
    	if(dataLDGLA[i].glAccountId == data.depGlAccountId){
    		return "<span>" + dataLDGLA[i].description + "</span>";
    	}
    }
    return "";
}


var dataLADGLA = new Array();
		dataLADGLA = [
				{
					'glAccountId' : '',
					'description' : ''	
				},
				<#list listAccDepGlAccount as loss>
					{
						'glAccountId' : '${loss.glAccountId?if_exists}',
						'description' : '${StringUtil.wrapString(loss.accountName?default(''))}'
					},
				</#list>
			];
var listAccDeptGlAccountRender = function (row, column, value) {
	var data = $('#jqxgridFatgla').jqxGrid('getrowdata', row);
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

<#assign columnlist="
					 { text: '${StringUtil.wrapString(uiLabelMap.assetGlAccountId)}', datafield: 'assetGlAccountId', cellsrenderer:listAssetGlAccountRender},
					 { text: '${StringUtil.wrapString(uiLabelMap.accDepGlAccountId)}', datafield: 'accDepGlAccountId', cellsrenderer:listAccDeptGlAccountRender},
					 { text: '${StringUtil.wrapString(uiLabelMap.depGlAccountId)}', datafield: 'depGlAccountId', cellsrenderer:listDeptGlAccountRender},
					 { text: '${StringUtil.wrapString(uiLabelMap.profitGlAccountId)}', datafield: 'profitGlAccountId', cellsrenderer:listProfitGlAccountRender},
					 { text: '${StringUtil.wrapString(uiLabelMap.lossGlAccountId)}', datafield: 'lossGlAccountId', cellsrenderer:listLossGlAccountRender}
					"/>
	
<@jqGrid  filtersimplemode="false" addType="popup" dataField=dataField columnlist=columnlist clearfilteringbutton="true" showtoolbar="true"
		 addrow="true" deleterow="true" alternativeAddPopup="alterpopupWindow1" id="jqxgridFatgla" customTitleProperties="${StringUtil.wrapString(uiLabelMap.accFixedAssetGLMappings)}"
		 url="jqxGeneralServicer?sname=listFixedAssetTypeyGLAccountJqx&organizationPartyId=${organizationPartyId}&fixedAssetId=${parameters.fixedAssetId}"
		 removeUrl="jqxGeneralServicer?sname=deleteFixedAssetTypeGlAccount&jqaction=D&organizationPartyId=${organizationPartyId}&fixedAssetId=${parameters.fixedAssetId}"
		 createUrl="jqxGeneralServicer?sname=createFixedAssetTypeGlAccount&jqaction=C&organizationPartyId=${organizationPartyId}&fixedAssetId=${parameters.fixedAssetId}"
		 deleteColumn="fixedAssetTypeId[${fixedAssetTypeId}];fixedAssetId[${parameters.fixedAssetId}];organizationPartyId[${organizationPartyId}]"
		 addColumns="fixedAssetTypeId[${fixedAssetTypeId}];fixedAssetId[${parameters.fixedAssetId}];assetGlAccountId;accDepGlAccountId;depGlAccountId;profitGlAccountId;lossGlAccountId;organizationPartyId[${organizationPartyId}]" 
		 />


<div id="alterpopupWindow1" style="display:none;">
    <div>${uiLabelMap.accCreateNew}</div>
    <div style="overflow: hidden;">
		<div class='row-fluid form-window-content' style="overflow: hidden;">
			<form id="formAdd1">
			    <div class="span12">
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
		    				<div class='span5 align-right asterisk'>
		    					${uiLabelMap.accDepGlAccountId}
		    				</div>
		    				<div class='span7'>
		    					<div id="accDepGlAccountId">
				 				</div>
		    				</div>
						</div>
						<div class='row-fluid margin-bottom10'>
		    				<div class='span5 align-right'>
		    					${uiLabelMap.depGlAccountId}
		    				</div>
		    				<div class='span7'>
		    					<div id="depGlAccountId" >
		 						</div>
		    				</div>
						</div>
						<div class='row-fluid margin-bottom10'>
		    				<div class='span5 align-right'>
		    					${uiLabelMap.profitGlAccountId}
		    				</div>
		    				<div class='span7'>
		    					<div id="profitGlAccountId"></div>
		    				</div>
						</div>
						<div class='row-fluid margin-bottom10'>
		    				<div class='span5 align-right '>
		    					${uiLabelMap.lossGlAccountId}
		    				</div>
		    				<div class='span7'>
		    					<div id="lossGlAccountId"></div>
		    				</div>
						</div>
				</div>		
			</form>
		</div>
		<div class="form-action">
			<div class='row-fluid'>
				<div class="span12 margin-top10">
					<button id="cancel1" class='btn btn-danger form-action-button pull-right'><i class='fa-remove'></i> ${uiLabelMap.Cancel}</button>
					<button id="saveAndContinue1" class='btn btn-success form-action-button pull-right'><i class='fa-plus'></i> ${uiLabelMap.SaveAndContinue}</button>
					<button id="save1" class='btn btn-primary form-action-button pull-right'><i class='fa-check'></i> ${uiLabelMap.Save}</button>
				</div>
			</div>
		</div>	
    </div>
</div>



<script type="text/javascript">
	$.jqx.theme = 'olbius';  
	theme = $.jqx.theme;
	
	var action1 = (function(){
		var initElement = function(){
			 $('#assetGlAccountId').jqxDropDownList({theme:theme, source: dataLAGLA, displayMember: "description", valueMember: "glAccountId",placeHolder : '${StringUtil.wrapString(uiLabelMap.PleaseChooseAcc?default(''))}'});
		    $('#accDepGlAccountId').jqxDropDownList({theme:theme,  source: dataLADGLA, displayMember: "description", valueMember: "glAccountId",placeHolder : '${StringUtil.wrapString(uiLabelMap.PleaseChooseAcc?default(''))}'});
		    $('#depGlAccountId').jqxDropDownList({theme:theme,   source: dataLDGLA, displayMember: "description", valueMember: "glAccountId",placeHolder : '${StringUtil.wrapString(uiLabelMap.PleaseChooseAcc?default(''))}'});
		    $('#profitGlAccountId').jqxDropDownList({theme:theme,  source: dataLPGLA, displayMember: "description", valueMember: "glAccountId",placeHolder : '${StringUtil.wrapString(uiLabelMap.PleaseChooseAcc?default(''))}'});
		    $('#lossGlAccountId').jqxDropDownList({theme:theme,  source: dataLLGLA, displayMember: "description", valueMember: "glAccountId",placeHolder : '${StringUtil.wrapString(uiLabelMap.PleaseChooseAcc?default(''))}'});
   			initJqxWindow();
		}
		var initJqxWindow = function(){
			  $("#alterpopupWindow1").jqxWindow({
			        width: 500,height: 350, resizable: false,  isModal: true, autoOpen: false, cancelButton: $("#cancel1"), modalOpacity: 0.7, theme:theme           
			    });
		}
		var save = function(){
			var row;
		        row = {
		        		assetGlAccountId: $('#assetGlAccountId').val(),
		        		accDepGlAccountId: $('#accDepGlAccountId').val(),
		        		depGlAccountId: $('#depGlAccountId').val(),
		        		profitGlAccountId: $('#profitGlAccountId').val(),
		        		lossGlAccountId: $('#lossGlAccountId').val()
		        	  };
			   $("#jqxgridFatgla").jqxGrid('addRow', null, row, "first");
				return true;
			}
		var clear = function(){
			$('#assetGlAccountId').jqxDropDownList('clearSelection');
			$('#accDepGlAccountId').jqxDropDownList('clearSelection');
			$('#depGlAccountId').jqxDropDownList('clearSelection');
			$('#profitGlAccountId').jqxDropDownList('clearSelection');
			$('#lossGlAccountId').jqxDropDownList('clearSelection');
		}
		var bindEvent = function(){
				$("#alterpopupWindow1").on('close',function(){
					clear();
				})
				$("#save1").click(function () {
					if(save()){
						$("#alterpopupWindow1").jqxWindow('close');clear();
					}
				});
				$("#saveAndContinue1").click(function () {
					if(save()){
						clear();
						return;
					}
				});
			}
		return {
			init : function(){
				initElement();
				bindEvent();
			}
		}
	}())
	
    $(document).ready(function(){
    	action1.init();
    })
</script>		 