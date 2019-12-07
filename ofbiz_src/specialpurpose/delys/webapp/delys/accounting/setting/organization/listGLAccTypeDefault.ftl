<script type="text/javascript">
	var dataGATP = new Array();
		dataGATP = [
		<#list glAccountTypeDefaults as acc>
			{
				'glAccountTypeId' : '${acc.glAccountTypeId?if_exists}',
				'description' : "${StringUtil.wrapString(acc.get('description',locale)?default(''))}"
			},
		</#list>	
		]
		
		
</script>
<#assign dataField="[{ name: 'glAccountId', type: 'string' },
					 { name: 'accountCode', type: 'string' },					 
					 { name: 'glAccountTypeId', type: 'string' },
					 { name: 'accountName', type: 'string' }
					 ]
					"/>
<#assign columnlist="
	{ text: '${uiLabelMap.FormFieldTitle_glAccountType}',filtertype : 'checkedlist', datafield: 'glAccountTypeId', cellsrenderer:function (row, column, value) {
		var data = $('#jqxgrid').jqxGrid('getrowdata', row);
        for(i=0;i < dataGATP.length; i++){
			if(typeof(data) != 'undefined' && dataGATP[i].glAccountTypeId == data.glAccountTypeId){
				return '<span>' + dataGATP[i].description + '</span>';
			}
        }
        return '';
    },createfilterwidget : function(column,columnElement,widget){
    	var source = {
    		localdata : dataGATP,
    		datatype : 'array'
    	};
    	var filterBoxAdapter = new $.jqx.dataAdapter(source,{autoBind : true});
    	var uniRecords = filterBoxAdapter.records;
    	uniRecords.splice(0, 0, '(${StringUtil.wrapString(uiLabelMap.filterselectallstring)})');
    	widget.jqxDropDownList({source: uniRecords, displayMember: 'description', valueMember: 'glAccountTypeId',placeHolder : '${StringUtil.wrapString(uiLabelMap.PleaseChooseAcc?default(''))}',renderer: function (index, label, value) 
							{
								for(i=0;i < dataGATP.length; i++){
									if(dataGATP[i].glAccountTypeId == value){
										return dataGATP[i].description;
									}
								}
							    return value;
							}});
    }},
    { text: '${uiLabelMap.accountCode}', datafield: 'accountCode',width : '10%',cellsrenderer : function(row){
			var data = $('#jqxgrid').jqxGrid('getrowdata',row);
			if(typeof(data) != 'undefined' && data.accountCode != null){
				return '<span class=\"custom-style-word\">' + data.accountCode + '</span>';
			}
			return '';
	 	}
	 } ,   
     { text: '${uiLabelMap.accountName}', datafield: 'accountName',cellsrenderer : function(row){
     			var data = $('#jqxgrid').jqxGrid('getrowdata',row);
     			if(typeof(data) != 'undefined' && data.accountName != null){
     				return '<span class=\"custom-style-word\">' + data.accountName + '</span>';
     			}
     			return '';
    		 }
   		 }
					"/>		
<div id="alterpopupWindow" class ='hide'>
    <div>${uiLabelMap.accCreateNew}</div>
    <div style="overflow: hidden;">
    	<div class='row-fluid form-window-content'>
			<form id="formAdd">
    			<div class='row-fluid margin-bottom10'>
    				<div class='span5 align-right asterisk'>
    					${uiLabelMap.AccountingGlAccountId}
    					
    				</div>
    				<div class='span7'>
						<div id="glAccountId">
							<div id="jqxgridGlAccount"></div>
 						</div>
    				</div>
				</div>
				<div class='row-fluid margin-bottom10'>
    				<div class='span5 align-right asterisk'>
    					${uiLabelMap.FormFieldTitle_glAccountType}
    				</div>
    				<div class='span7'>
						<div id="glAccountTypeId">
							<div id="jqxgridGlAccountType"></div>
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
							
<script src="../images/js/generalUtils.js"></script>

<@jqGrid url="jqxGeneralServicer?organizationPartyId=${parameters.organizationPartyId}&sname=JQListGLAccountTypeDedault" columnlist=columnlist dataField=dataField
		  filterable="true" clearfilteringbutton="true" showtoolbar="true" addrow="true" alternativeAddPopup="alterpopupWindow" deleterow="true" deletesuccessfunction="updateData" functionAfterAddRow="updateData"
		 createUrl="jqxGeneralServicer?organizationPartyId=${parameters.organizationPartyId}&jqaction=C&sname=createGlAccountTypeDefault"
		 removeUrl="jqxGeneralServicer?organizationPartyId=${parameters.organizationPartyId}&jqaction=D&sname=removeGlAccountTypeDefault"
		 deleteColumn="glAccountId;glAccountTypeId;organizationPartyId[${parameters.organizationPartyId}]"
		 addColumns="glAccountId;glAccountTypeId;organizationPartyId[${parameters.organizationPartyId}]" addType="popup" id="jqxgrid"/>

<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxvalidator.js"></script>
<script type="text/javascript">
	$.jqx.theme = 'olbius';  
	theme = $.jqx.theme;
	var updateData = function(){
		$('#jqxgridGlAccountType').jqxGrid('updatebounddata');
	};
	
	var action = (function(){
		var initElement = function(){
			 //$('#glAccountId').jqxDropDownList({ filterable : true,source: dataGAOAC, displayMember: "description", valueMember: "glAccountId",placeHolder : '${StringUtil.wrapString(uiLabelMap.PleaseChooseAcc?default(''))}'});
		    //$('#glAccountTypeId').jqxDropDownList({ filterable : true,dropDownWidth : '250',width : '250',source: dataGATP, displayMember: "description", valueMember: "glAccountTypeId",placeHolder : '${StringUtil.wrapString(uiLabelMap.PleaseChooseAcc?default(''))}'});
		    initDropDown($('#glAccountId'),$('#jqxgridGlAccount'));
		     initDropDownGlTypeNotDf($('#glAccountTypeId'),$('#jqxgridGlAccountType'));
		    $("#alterpopupWindow").jqxWindow({
		        width: 500,height : 180, resizable: false,  isModal: true, autoOpen: false, cancelButton: $("#cancel"), modalOpacity: 0.7, theme:theme           
		    });
		}
		
		var initDropDown = function(dropdown,grid){
			GridUtils.initDropDownButton({url : 'getListGLAccountOACsData',autoshowloadelement : true,width : 400,filterable : true,source : {pagesize : 5,cache  : false},dropdown : {width : 250, dropDownHorizontalAlignment: true}},
			[
				{name : 'glAccountId',type : 'string'},
				{name : 'accountCode',type : 'string'},
				{name : 'accountName',type : 'string'}
			], 
			[
				{text : '${uiLabelMap.accountCode}',datafield : 'accountCode',width : '30%'},
				{text : '${uiLabelMap.accountName}',datafield : 'accountName'}
			]
			, null, grid,dropdown,'glAccountId');
		}
		
		var initDropDownGlTypeNotDf = function(dropdown,grid){
			GridUtils.initDropDownButton({url : 'getGLAccountTypeNotDedault&organizationPartyId=${parameters.organizationPartyId?if_exists}',autoshowloadelement : true,width : 400,filterable : true,source : {pagesize : 5,cache  : false},dropdown : {width : 250, dropDownHorizontalAlignment: true}},
			[
				{name : 'glAccountTypeId',type : 'string'},
				{name : 'description',type : 'string'}
			], 
			[
				{text : '${uiLabelMap.glAccountTypeId}',datafield : 'glAccountTypeId',width : '30%'},
				{text : '${uiLabelMap.CommonDescription}',datafield : 'description'}
			]
			, null, grid,dropdown,'glAccountTypeId');
		}
		
		
		var initRules = function(){
			$('#formAdd').jqxValidator({
				rules : [
					{input : '#glAccountId',message : '${StringUtil.wrapString(uiLabelMap.FieldRequiredAccounting?default(''))}',action : 'change,close',rule : function(){
						var val = $('#glAccountId').val();
						if(!val) return false;
						return true;
					}},
					{input : '#glAccountTypeId',message : '${StringUtil.wrapString(uiLabelMap.FieldRequiredAccounting?default(''))}',action : 'change,close',rule : function(input){
						var val = input.jqxDropDownButton('val');
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
		        row = { 
		        		glAccountId:$('#glAccountId').val(),
		        		glAccountTypeId:$('#glAccountTypeId').val()           
		        	  };
			   $("#jqxgrid").jqxGrid('addRow', null, row, "first");
		        $("#alterpopupWindow").jqxWindow('close');
		    });
		    $('#alterpopupWindow').on('close',function(){
		    	 $('#glAccountId').jqxDropDownButton('val','');
		    	  $('#glAccountTypeId').jqxDropDownButton('val','');
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