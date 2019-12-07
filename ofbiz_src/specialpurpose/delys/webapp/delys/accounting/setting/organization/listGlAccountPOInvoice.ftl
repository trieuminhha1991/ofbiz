<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxvalidator.js"></script>
<script src="../images/js/generalUtils.js"></script>
<script type="text/javascript">
    
	var dataITT = new Array();
	dataITT = [
		<#list listInvoiceItemType as acc>
			{
				'invoiceItemTypeId' : '${acc.invoiceItemTypeId?if_exists}',
				'description' : "<span class='custom-style-word'> ${StringUtil.wrapString(acc.get('description',locale)?default(''))}</span>"
			},
		</#list>	
		]
</script>
<#assign dataField="[
					{ name: 'description', type: 'string' },
					 { name: 'defaultGlAccountId', type: 'string' },
					 { name: 'glAccountId', type: 'string' },
					 { name: 'accountName', type: 'string' },
					 { name: 'accountCode', type: 'string' },
					 { name: 'accountCodeOver', type: 'string' },
					 { name: 'invoiceItemTypeId', type: 'string' }
					 ]
					"/>
<#assign columnlist="{ text: '${uiLabelMap.description}', datafield: 'description', width:250,cellsrenderer : function(row){
							var data = $('#jqxgrid').jqxGrid('getrowdata',row);
							return '<span class=\"custom-style-word\">' + data.description + '</span>';
						}},
					 { text: '${uiLabelMap.FormFieldTitle_defaultGlAccountId}', datafield: 'accountCode', width:200},
					 { text: '${uiLabelMap.FormFieldTitle_overrideGlAccountId}', datafield: 'accountCodeOver', width:250},
					 { text: '${uiLabelMap.FormFieldTitle_activeGlDescription}', datafield: 'accountName'}
					"/>	
<@jqGrid url="jqxGeneralServicer?invItemTypePrefix=${parameters.invItemTypePrefix?default('PINV')}&sname=JQGetListGLAccountItemTypeSale" filtersimplemode="true" dataField=dataField columnlist=columnlist showtoolbar="true" editable="false"
		 height="640"  addrefresh="true" sortable="false" alternativeAddPopup="alterpopupWindow" addrow="true" addType="popup" addrefresh="true"
		 id="jqxgrid" addColumns="invoiceItemTypeId;glAccountId;organizationPartyId" createUrl="jqxGeneralServicer?jqaction=C&sname=addInvoiceItemTypeGlAssignment"
		 removeUrl="jqxGeneralServicer?sname=removeInvoiceItemTypeGlAssignment&jqaction=D" deleterow="true" clearfilteringbutton="true"
		 deleteColumn="invoiceItemTypeId;organizationPartyId[${parameters.organizationPartyId?if_exists}]"
		 />

<div id="alterpopupWindow" class ='hide'>
    <div>${uiLabelMap.accCreateNew}</div>
    <div style="overflow: hidden;">
    	<div class='row-fluid form-window-content'>
			<form id="formAdd">
    			<div class='row-fluid margin-bottom10'>
    				<div class='span5 align-right asterisk'>
    					${uiLabelMap.FormFieldTitle_invoiceItemTypeId}
    				</div>
    				<div class='span7'>
						<div id="invoiceItemTypeId">
 						</div>
    				</div>
				</div>
				<div class='row-fluid margin-bottom10'>
    				<div class='span5 align-right asterisk'>
    					${uiLabelMap.FormFieldTitle_overrideGlAccountId}
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
<script type="text/javascript">
	$.jqx.theme = 'olbius';  
	theme = $.jqx.theme;
	var action = (function(){
		var initElement = function(){
			 //$('#glAccountId').jqxDropDownList({filterable : true,source: dataGAOAC, displayMember: "description", valueMember: "glAccountId",placeHolder : '${StringUtil.wrapString(uiLabelMap.PleaseChooseAcc?default(''))}'});
		    $('#invoiceItemTypeId').jqxDropDownList({filterable : true,width  :'250',dropDownHorizontalAlignment: 'right',dropDownWidth  : '250' ,source: dataITT, displayMember: "description", valueMember: "invoiceItemTypeId",placeHolder : '${StringUtil.wrapString(uiLabelMap.PleaseChooseAcc?default(''))}'});
		    initDropDown($('#glAccountId'),$('#jqxgridGlAccount'));
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
		
		var initRules = function(){
			$('#formAdd').jqxValidator({
				rules : [
					{input : '#invoiceItemTypeId',message : '${StringUtil.wrapString(uiLabelMap.FieldRequiredAccounting?default(''))}',action : 'change,close',rule : function(input){
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
		          row = { 
		        		glAccountId:$('#glAccountId').val(),
		        		invoiceItemTypeId: $('#invoiceItemTypeId').val(),
		        		organizationPartyId:'${parameters.organizationPartyId}'              
		        	  };
			   $("#jqxgrid").jqxGrid('addRow', null, row, "first");
		        $("#alterpopupWindow").jqxWindow('close');
		    });
		    $('#alterpopupWindow').on('close',function(){
		    	 $('#glAccountId').jqxDropDownButton('val','');
		    	  $('#invoiceItemTypeId').jqxDropDownList('clearSelection');
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