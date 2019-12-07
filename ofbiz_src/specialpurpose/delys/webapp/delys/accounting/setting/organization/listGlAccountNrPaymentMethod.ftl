<script type="text/javascript" language="Javascript">
	<#assign listPM = delegator.findByAnd("PaymentMethodType",null,null,false)/>
	
	var listPM = [
		<#list listPM as pm>
		{
			paymentMethodTypeId : '${pm.paymentMethodTypeId?if_exists}' ,
			description : "<span class='custom-style-word'>${StringUtil.wrapString(pm.get("description",locale)?if_exists)}</span>"
		},
		</#list>
	];	
	
	var dataLPMT = new Array();
	dataLPMT = [
		<#list listPaymentMethodType as acc>
			{
				'paymentMethodTypeId' : '${acc.paymentMethodTypeId?if_exists}',
				'description' : "<span class='custom-style-word'>${StringUtil.wrapString(acc.get('description',locale)?default(''))}</span>"
			},
		</#list>	
		]
	var dataGAOAC = new Array();
	dataGAOAC = [
		<#list listGlAccountOrganizationAndClass as acc>
			{
				'glAccountId' : '${acc.glAccountId?if_exists}',
				'description' : "<span class='custom-style-word'>${StringUtil.wrapString(acc.accountName?default(''))}" + "[" + '${acc.glAccountId?if_exists}' + "]</span>"
			},
		</#list>	
		]
		
    var listGlAccountOrganizationAndClassRender = function (row, column, value) {
    	var data = $('#jqxgrid').jqxGrid('getrowdata', row);
    	if(typeof(data) != 'undefined' && data.accountCode != null){
    		return "<span class='custom-style-word'>" + data.accountName + '[' + data.accountCode + ']' + "</span>";
    	}
        return "";
    }
    
    
    var listDefaultGlAccountRender = function (row, column, value) {
    	var data = $('#jqxgrid').jqxGrid('getrowdata', row);
    	if(typeof(data) != 'undefined' && data.accountCodeDef != null){
    		return "<span class='custom-style-word'>" + data.accountNameDef + '[' + data.accountCodeDef + ']' + "</span>";
		}
        return "";
    }
</script>

<#assign dataField="[{ name: 'paymentMethodTypeId', type: 'string' },
					 { name: 'accountCode', type: 'string' },
					 { name: 'accountName', type: 'string' },
					 { name: 'accountCodeDef', type: 'string' },
					 { name: 'accountNameDef', type: 'string' }
					 ]
					"/>
<#assign columnlist="{ text: '${uiLabelMap.PaymentMethodType}', filtertype  : 'checkedlist',datafield: 'paymentMethodTypeId',cellsrenderer : function(row){
						var data = $('#jqxgrid').jqxGrid('getrowdata',row);
						if(typeof(data) != 'undefined'){
							for(var i = 0 ;i <listPM.length;i++){
								if(listPM[i].paymentMethodTypeId == data.paymentMethodTypeId){
									return '<span  class=\"custom-style-word\">' + listPM[i].description + '</span>';
								}
							}	
						}
						return '<span></span>';
					},createfilterwidget : function(column,columnElement,widget){
				    	var source = {
				    		localdata : listPM,
				    		datatype : 'array'
				    	};
				    	var filterBoxAdapter = new $.jqx.dataAdapter(source,{autoBind : true});
				    	var uniRecords = filterBoxAdapter.records;
				    	uniRecords.splice(0, 0, '(${StringUtil.wrapString(uiLabelMap.filterselectallstring)})');
				    	widget.jqxDropDownList({source: uniRecords, displayMember: 'description', valueMember: 'paymentMethodTypeId',placeHolder : '${StringUtil.wrapString(uiLabelMap.PleaseChooseAcc?default(''))}',renderer: function (index, label, value) 
											{
												for(i=0;i < listPM.length; i++){
													if(listPM[i].paymentMethodTypeId == value){
														return listPM[i].description;
													}
												}
											    return value;
											}});
				    }},
					 { text: '${uiLabelMap.GLAccountId}', datafield: 'accountCode', cellsrenderer:listGlAccountOrganizationAndClassRender,filterable : true},
					 { text: '${uiLabelMap.accDefaultGlAccountId}', cellsrenderer:listDefaultGlAccountRender, datafield: 'accountCodeDef', filterable: true}
					 
					"/>
<@jqGrid url="jqxGeneralServicer?organizationPartyId=${parameters.organizationPartyId}&sname=JQListGlAccountNrPaymentMethod" dataField=dataField columnlist=columnlist
		 deletesuccessfunction="updateData" functionAfterAddRow="updateData"
		 id="jqxgrid" filtersimplemode="true" showtoolbar="true" clearfilteringbutton="true" addrefresh="true"
		 addrow="true" addType="popup" alternativeAddPopup="alterpopupWindow"
		 createUrl="jqxGeneralServicer?organizationPartyId=${parameters.organizationPartyId}&jqaction=C&sname=addPaymentMethodTypeGlAssignment"
		 addColumns="paymentMethodTypeId;glAccountId;organizationPartyId[${parameters.organizationPartyId}]"		 
		 deleterow="true" removeUrl="jqxGeneralServicer?sname=removePaymentMethodTypeGlAssignment&jqaction=D" 
		 deleteColumn="paymentMethodTypeId;organizationPartyId[${parameters.organizationPartyId}]"
 />				
<div id="alterpopupWindow" class ='hide'>
    <div>${uiLabelMap.accCreateNew}</div>
    <div style="overflow: hidden;">
    	<div class='row-fluid form-window-content'>
			<form id="formAdd">
    			<div class='row-fluid margin-bottom10'>
    				<div class='span5 align-right asterisk'>
    					${uiLabelMap.PaymentMethodType}
    				</div>
    				<div class='span7'>
						<div id="paymentMethodTypeId">
							<div id="jqxgridPaymentType"></div>
 						</div>
    				</div>
				</div>
				<div class='row-fluid margin-bottom10'>
    				<div class='span5 align-right asterisk'>
    					${uiLabelMap.GLAccountId}
    				</div>
    				<div class='span7'>
						<div id="glAccountId">
							<div id="jqxgridGlAccount"></div>
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
<script src="/delys/images/js/generalUtils.js"></script>
<script type="text/javascript">
	$.jqx.theme = 'olbius';  
	theme = $.jqx.theme;
	var updateData = function(){
		$('#jqxgridPaymentType').jqxGrid('updatebounddata');
	}
	var action = (function(){
		var initElement = function(){
			 //$('#glAccountId').jqxDropDownList({ filterable : true,source: dataGAOAC,width : 250,dropDownWidth : 250, displayMember: "description", valueMember: "glAccountId",placeHolder : '${StringUtil.wrapString(uiLabelMap.PleaseChooseAcc?default(''))}'});
		    //$('#paymentMethodTypeId').jqxDropDownList({autoDropDownHeight : true, filterable : true,source: dataLPMT,width : 250,dropDownWidth : 250, displayMember: "description", valueMember: "paymentMethodTypeId",placeHolder : '${StringUtil.wrapString(uiLabelMap.PleaseChooseAcc?default(''))}'});
		    initDropDown($('#glAccountId'),$('#jqxgridGlAccount'));
		    initDropDownPM($('#paymentMethodTypeId'),$('#jqxgridPaymentType'));
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
		
		var initDropDownPM = function(dropdown,grid){
			GridUtils.initDropDownButton({url : 'getPaymentMethodTypeNotDedault&organizationPartyId=${parameters.organizationPartyId}',autoshowloadelement : true,width : 400,filterable : true,source : {pagesize : 5,cache  : false},dropdown : {width : 250, dropDownHorizontalAlignment: true}},
			[
				{name : 'paymentMethodTypeId',type : 'string'},
				{name : 'description',type : 'string'}
			], 
			[
				{text : '${uiLabelMap.paymentMethodTypeId}',datafield : 'paymentMethodTypeId',width : '50%'},
				{text : '${uiLabelMap.CommonDescription}',datafield : 'description'}
			]
			, null, grid,dropdown,'paymentMethodTypeId');
		}
		
		var initRules = function(){
			$('#formAdd').jqxValidator({
				rules : [
					{input : '#paymentMethodTypeId',message : '${StringUtil.wrapString(uiLabelMap.FieldRequiredAccounting?default(''))}',action : 'change,close',rule : function(input){
						var val = input.val();
						if(!val) return false;
						return true;
					}},
					{input : '#glAccountId',message : '${StringUtil.wrapString(uiLabelMap.FieldRequiredAccounting?default(''))}',action : 'change,close',rule : function(input){
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
		        		paymentMethodTypeId: $('#paymentMethodTypeId').val(),
    					glAccountId: $('#glAccountId').val()            
		        	  };
			   $("#jqxgrid").jqxGrid('addRow', null, row, "first");
		        $("#alterpopupWindow").jqxWindow('close');
		    });
		    $('#alterpopupWindow').on('close',function(){
		    	 $('#glAccountId').jqxDropDownButton('val','');
		    	  $('#paymentMethodTypeId').jqxDropDownButton('val','');
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