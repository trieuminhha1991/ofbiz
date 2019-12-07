<script type="text/javascript" language="Javascript">
	var dataGLAT = new Array();
	dataGLAT = [
		<#list listGlAccountType as type>
			{
				'glAccountTypeId' : '${type.glAccountTypeId?if_exists}',
				'description' : "${StringUtil.wrapString(type.get("description",locale)?if_exists)}" 	
			},
		</#list>
	];

	var dataLC = new Array();
	dataLC = [
		<#list listCurrency as type>
			{
				'uomId' : '${type.uomId?if_exists}',
				'description' :  "${StringUtil.wrapString(type.get("description",locale)?if_exists)}" 	
			},
		</#list>
	];
    
	var dataCustomMethod = new Array();
	dataCustomMethod = [
		<#list listCustomMethod as type>
			{
				'customMethodId' : '${type.customMethodId?if_exists}',
				'description' :  "${StringUtil.wrapString(type.get("description",locale)?if_exists)}" 	
			},
		</#list>
	];
    
</script>
<#assign dataField="[{ name: 'costComponentCalcId', type: 'string' },
						 { name: 'description', type: 'string' },
						 { name: 'costGlAccountTypeId', type: 'string' },
						 { name: 'offsettingGlAccountTypeId', type: 'string' },
						 { name: 'fixedCost', type: 'number'},
						 { name: 'variableCost', type: 'number'},
						 { name: 'perMilliSecond', type: 'number' },
						 { name: 'currencyUomId', type: 'string' },
						 { name: 'costCustomMethodId', type: 'string' },
						 { name: 'customName', type: 'string' },
						 { name: 'desUom', type: 'string' },
						 { name: 'costDes', type: 'string' },
						 { name: 'offDes', type: 'string' }
						 ]
						 "/>
<#assign columnlist="{ text: '${uiLabelMap.FormFieldTitle_costComponentCalcId}', datafield: 'costComponentCalcId', width: '20%',pinned : true},
					 { text: '${uiLabelMap.description}', datafield: 'description', width: 190},
					 { text: '${uiLabelMap.FormFieldTitle_costGlAccountTypeId}', datafield: 'costDes', width: '20%',sortable : false
					 	},
                     { text: '${uiLabelMap.FormFieldTitle_offsettingGlAccountTypeId}', datafield: 'offDes', width: '20%',sortable : false
                     },
                     { text: '${uiLabelMap.FormFieldTitle_fixedCost}', datafield: 'fixedCost', width: '20%',filtertype : 'number',cellsformat : 'd'},
                     { text: '${uiLabelMap.FormFieldTitle_variableCost}', datafield: 'variableCost', width: '20%',filtertype : 'number',cellsformat : 'd'},
                     { text: '${uiLabelMap.FormFieldTitle_perMilliSecond}', datafield: 'perMilliSecond', width: '20%',filtertype : 'number' ,cellsformat : 'd'},
					 { text: '${uiLabelMap.currency}', datafield: 'desUom', width: '20%',sortable : false},
					 { text: '${uiLabelMap.FormFieldTitle_costCustomMethodId}', datafield: 'customName', width: '20%',sortable : false}
					 "/>
 <div id="alterpopupWindow" style="display:none;">
	    <div id="header"></div>
	    <div style="overflow: hidden;">
			<div class='row-fluid form-window-content'>
				<form id="formAdd">
	    			<div class='row-fluid margin-bottom10'>
	    				<div class='span5 align-right'>
	    					${uiLabelMap.FormFieldTitle_costGlAccountTypeId}
	    				</div>
	    				<div class='span7'>
	    					<div id="costGlAccountTypeIdAdd"></div>
	    				</div>
					</div>
					<div class='row-fluid margin-bottom10'>
	    				<div class='span5 align-right'>
	    					${uiLabelMap.FormFieldTitle_offsettingGlAccountTypeId}
	    				</div>
	    				<div class='span7'>
		 					<div id="offsettingGlAccountTypeIdAdd"></div>
	    				</div>
					</div>
					<div class='row-fluid margin-bottom10'>
	    				<div class='span5 align-right'>
	    					${uiLabelMap.description}
	    				</div>
	    				<div class='span7'>
	    					<input id="descriptionAdd"></input>
	    				</div>
					</div>
					<div class='row-fluid margin-bottom10'>
	    				<div class='span5 align-right'>
	    					${uiLabelMap.FormFieldTitle_fixedCost}
	    				</div>
	    				<div class='span7'>
	    					<div id="fixedCostAdd"></div>
	    				</div>
					</div>
					<div class='row-fluid margin-bottom10'>
	    				<div class='span5 align-right'>
	    					${uiLabelMap.FormFieldTitle_variableCost}
	    				</div>
	    				<div class='span7'>
	    					<div id="variableCostAdd"></div>
	    				</div>
					</div>
					<div class='row-fluid margin-bottom10'>
	    				<div class='span5 align-right'>
	    					${uiLabelMap.FormFieldTitle_perMilliSecond}
	    				</div>
	    				<div class='span7'>
	    					<div id="perMilliSecondAdd"></div>
	    				</div>
					</div>
					<div class='row-fluid margin-bottom10'>
	    				<div class='span5 align-right'>
	    					${uiLabelMap.currency}
	    				</div>
	    				<div class='span7'>
	    					<div id="currencyUomIdAdd"></div>
	    				</div>
					</div>
					<div class='row-fluid margin-bottom10'>
	    				<div class='span5 align-right'>
	    					${uiLabelMap.FormFieldTitle_costCustomMethodId}
	    				</div>
	    				<div class='span7'>
	    					<div id="costCustomMethodIdAdd"></div>
	    				</div>
					</div>
				</form>
			</div>
		<div class="form-action">
			<div class='row-fluid'>
				<div class="span12 margin-top10">
					<button id="cancel" class='btn btn-danger form-action-button pull-right'><i class='fa-remove'></i> ${uiLabelMap.Cancel}</button>
					<button id="saveAndContinue" class='btn btn-success form-action-button pull-right'><i class='fa-plus'></i> ${uiLabelMap.SaveAndContinue}</button>
					<button id="save" class='btn btn-primary form-action-button pull-right'><i class='fa-check'></i> ${uiLabelMap.Save}</button>
				</div>
			</div>
		</div>	
	</div>
</div>	
<#assign customcontrol1="icon-save open-sans@${uiLabelMap.CommonUpdate}@javascript: void(0);@action.openGrid()"/>			
<@jqGrid url="jqxGeneralServicer?sname=JQListCosts" dataField=dataField columnlist=columnlist 
		 id="jqxgrid" filtersimplemode="true" showtoolbar="true" customcontrol1=customcontrol1
		 editable="false" deleterow="true" editrefresh="true"
		 addrow="true" addType="popup" alternativeAddPopup="alterpopupWindow" createUrl="jqxGeneralServicer?jqaction=C&sname=createCostComponentCalc"
		 addColumns="description;fixedCost(java.lang.Float);costGlAccountTypeId;offsettingGlAccountTypeId;fixedCost(java.lang.Float);variableCost(java.lang.Long);perMilliSecond(java.lang.Float);currencyUomId;costCustomMethodId;organizationPartyId]" clearfilteringbutton="true"
		 updateUrl="jqxGeneralServicer?jqaction=U&sname=updateCostComponentCalc" 
		 editColumns="costComponentCalcId;description;costGlAccountTypeId;offsettingGlAccountTypeId;fixedCost;variableCost;perMilliSecond;currencyUomId;costCustomMethodId"
		 removeUrl="jqxGeneralServicer?sname=removeCostComponentCalc&jqaction=D" deleteColumn="costComponentCalcId" 
 />
<script type="text/javascript">
	$.jqx.theme = 'olbius';  
	theme = $.jqx.theme;
	var costId;
    var action  = (function(){
    	var initElement = function(data){
    		if(!data){
				$('#costGlAccountTypeIdAdd').jqxDropDownList({width : 250,theme:theme,  source: dataGLAT,filterable : true, displayMember: "description", valueMember: "glAccountTypeId",placeHolder : '${StringUtil.wrapString(uiLabelMap.PleaseChooseAcc?default(''))}'});
			    $('#offsettingGlAccountTypeIdAdd').jqxDropDownList({width : 250,filterable : true,theme:theme, source: dataGLAT, displayMember: "description", valueMember: "glAccountTypeId",placeHolder : '${StringUtil.wrapString(uiLabelMap.PleaseChooseAcc?default(''))}'});
			    $('#descriptionAdd').jqxInput({width:245,height : 25,theme:theme, placeHolder: "${StringUtil.wrapString(uiLabelMap.description	)}"} );
			    $("#fixedCostAdd").jqxNumberInput({ width: '250',height : 25, inputMode: 'simple', spinButtons: true });
			    $("#variableCostAdd").jqxNumberInput({ width: '250',height : 25, inputMode: 'simple', spinButtons: true });
				$("#perMilliSecondAdd").jqxNumberInput({ width: '250',height : 25, inputMode: 'simple', spinButtons: true });
				$('#currencyUomIdAdd').jqxDropDownList({width : 250,theme:theme,  source: dataLC,filterable : true, displayMember: "description", valueMember: "uomId",placeHolder : '${StringUtil.wrapString(uiLabelMap.PleaseChooseAcc?default(''))}'});
				$('#costCustomMethodIdAdd').jqxDropDownList({width : 250,theme:theme,filterable : true,  source: dataCustomMethod, displayMember: "description", valueMember: "customMethodId",placeHolder : '${StringUtil.wrapString(uiLabelMap.PleaseChooseAcc?default(''))}'});
    			initjqxWindow();
    			$('#header').text('${StringUtil.wrapString(uiLabelMap.accCreateNew)}');
    			$('#header').attr('action','add');
    		}else{
    			$('#header').text('${StringUtil.wrapString(uiLabelMap.CommonUpdate)}');
    			$('#header').attr('action','update');
    			costId = data.costComponentCalcId ? data.costComponentCalcId : '';
    			$('#costGlAccountTypeIdAdd').jqxDropDownList('val',data.costGlAccountTypeId ? data.costGlAccountTypeId : '');
    			$('#offsettingGlAccountTypeIdAdd').jqxDropDownList('val', data.offsettingGlAccountTypeId ? data.offsettingGlAccountTypeId : '');
    			$('#descriptionAdd').jqxInput('val',data.description ? data.description : '');
				$("#fixedCostAdd").jqxNumberInput('val', data.fixedCost ? data.fixedCost : '');
				$("#variableCostAdd").jqxNumberInput('val',data.variableCost ? data.variableCost : '');
				$("#perMilliSecondAdd").jqxNumberInput('val',data.perMilliSecond ? data.perMilliSecond : '');
				$('#currencyUomIdAdd').jqxDropDownList('val',data.currencyUomId ? data.currencyUomId : '');
				$('#costCustomMethodIdAdd').jqxDropDownList('val',data.costCustomMethodId ? data.costCustomMethodId : '');
    		} 
    	}
    	
    	var notificationUpdate = function(status,message){
    		$('#notificationContentjqxgrid').text(message);
    		$('#jqxNotificationjqxgrid').jqxNotification({template : status});
		 	$('#jqxNotificationjqxgrid').jqxNotification('open');
    	}
    	
    	function openGrid(){
    		var index = $('#jqxgrid').jqxGrid('getselectedrowindex');
    		if(index == -1){
	    		notificationUpdate('success','${StringUtil.wrapString(uiLabelMap.NotificationBeforeUpdateRow)}');
    		}else {
    			var data = $('#jqxgrid').jqxGrid('getrowdata',index);
    			initElement(data);
    			$('#alterpopupWindow').jqxWindow('open');
    		}
    	} 
    	
    	var initjqxWindow = function(){
    		 $("#alterpopupWindow").jqxWindow({
			        width: 650,height : 450, resizable: false,  isModal: true, autoOpen: false, cancelButton: $("#cancel"), modalOpacity: 0.7, theme:theme           
		    });
    	}
    	var save = function(url){
    		var row;
		        row = {
		        		costGlAccountTypeId: $('#costGlAccountTypeIdAdd').jqxDropDownList('val'),
		        		offsettingGlAccountTypeId: $('#offsettingGlAccountTypeIdAdd').jqxDropDownList('val'),
		        		description: $('#descriptionAdd').val(),
		        		fixedCost: $('#fixedCostAdd').jqxNumberInput('val'),
		        		variableCost: $('#variableCostAdd').jqxNumberInput('val'),
		        		perMilliSecond: $('#perMilliSecondAdd').jqxNumberInput('val'),
		        		currencyUomId : $('#currencyUomIdAdd').jqxDropDownList('val'),
		        		costCustomMethodId:$('#costCustomMethodIdAdd').jqxDropDownList('val')     
		        	  };
		        if(costId){
		        	row.costComponentCalcId = costId;
		        }	  
		    if(url){
		    	sendRequest(url,row);
		    	return true;
		    }    	  
	        $("#jqxgrid").jqxGrid('addRow', null, row, "first");
	        return true; 	  
    	}
    	
    	var sendRequest = function(url,row){
    		$.ajax({
		    		url : url,
		    		data : row,
		    		async : false,
		    		type : 'POST',
		    		datatype : 'json',
		    		success : function(response){
		    		if(response._ERROR_MESSAGE_LIST_){
		    			notificationUpdate('error','error');
		    		}else {
		    			$('#jqxgrid').jqxGrid('updatebounddata');
		    			notificationUpdate('success','${StringUtil.wrapString(uiLabelMap.wgupdatesuccess)}');
		    		}
		    		},error : function(){
		    			notificationUpdate('error','error');
		    		}
		    	});
    	}
    	
    	var clear = function(){
    		$('#header').text('${StringUtil.wrapString(uiLabelMap.accCreateNew)}');
    		$('#header').attr('action','add');
    		$('#costGlAccountTypeIdAdd').jqxDropDownList('clearSelection');
    		$('#offsettingGlAccountTypeIdAdd').jqxDropDownList('clearSelection');
    		$('#currencyUomIdAdd').jqxDropDownList('clearSelection');
    		$('#costCustomMethodIdAdd').jqxDropDownList('clearSelection');
    		$('#descriptionAdd').jqxInput('val','');
    		$("#fixedCostAdd").jqxNumberInput('clear');
    		$("#variableCostAdd").jqxNumberInput('clear');
    		$("#perMilliSecondAdd").jqxNumberInput('clear');
    	}
    	var bindEvent = function(){
    		$("#save").click(function () {
			  	if(($('#header').attr('action') == 'add' ?  save() : ($('#header').attr('action') == 'update') ? save('updateCostComponentCalcJSON') : false))  $("#alterpopupWindow").jqxWindow('close');
				return ;
			});
	    	$("#saveAndContinue").click(function () {
			  	return ($('#header').attr('action') == 'add' ?  save() : ($('#header').attr('action') == 'update') ? save('updateCostComponentCalcJSON') : false);
			});
			 $("#alterpopupWindow").on('close',function(){
			 	clear();
		 	})
    	}
    	
    	return {
    		init : function(){
    			initElement();
    			bindEvent();
    		},
    		openGrid : openGrid
    	}
    
    }());
    
    $(document).ready(function(){
    	action.init();
    })
    
</script>