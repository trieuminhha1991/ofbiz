<#include "script/ViewPaymentTypeListScript.ftl"/>
<#assign datafield = "[{name: 'paymentTypeId', type: 'string'},
                       {name: 'description', type: 'string'},
					   {name: 'parentTypeId', type: 'string'},
					   {name: 'parentTypeDesc', type: 'string'},
					   {name: 'glAccountTypeId', type: 'string'},
					   {name: 'glAccountTypeDesc', type: 'string'},
					   {name: 'isAppliedInvoice', type: 'bool'},
					  ]"/>
					  
<script type="text/javascript">
<#assign columnlist = "{text: '${StringUtil.wrapString(uiLabelMap.CommonId)}', datafield: 'paymentTypeId', width: '18%'},
					   {text: '${StringUtil.wrapString(uiLabelMap.CommonDescription)}', datafield: 'description', width: '19%'},
					   {text: '${StringUtil.wrapString(uiLabelMap.BACCChildOfType)}', datafield: 'parentTypeDesc', width: '19%'},
					   {text: '${StringUtil.wrapString(uiLabelMap.BACCGlAccountType)}', datafield: 'glAccountTypeId', width: '29%',
						   cellsrenderer: function (row, columnfield, value, defaulthtml, columnproperties) {
							   var rowData = $('#jqxgrid').jqxGrid('getrowdata', row);
							   if(rowData && value){
								   return '<span>' + value + ' - ' + rowData.glAccountTypeDesc + '</span>'
							   }
						   }
					   },
					   {text: '${StringUtil.wrapString(uiLabelMap.BACCRequiredAppliedInvoice)}', columntype: 'checkbox', 
						   datafield: 'isAppliedInvoice', width: '15%', filtertype: 'bool', filterable: false},
						"/>
</script>		
<@jqGrid url="jqxGeneralServicer?sname=JQGetListPaymentType" dataField=datafield columnlist=columnlist 
		 id="jqxgrid" filtersimplemode="true" showtoolbar="true" clearfilteringbutton="true" 
		 addrow="true" addType="popup" alternativeAddPopup="editPaymentTypeWindow"
		 mouseRightMenu="true" contextMenuId="contextMenu"
		 jqGridMinimumLibEnable="false"
		 />
		 
<div id='contextMenu' class="hide">
	<ul>
		<li action="edit">
			<i class="icon-edit"></i>${uiLabelMap.CommonEdit}
        </li>
	</ul>
</div>	

<div id="editPaymentTypeWindow" class="hide">
	<div>${uiLabelMap.BACCCreateNew}</div>
	<div class='form-window-container'>
		<div class='form-window-content'>
			<form class="form-horizontal form-window-content-custom">
				<div class='row-fluid'>
					<div class="span4">
						<label class='asterisk'>${uiLabelMap.CommonId}</label>
					</div>
					<div class="span8">
						<input type="text" id="editPaymentTypeId"> 
					</div>
				</div>
				<div class='row-fluid'>
					<div class="span4">
						<label class='asterisk'>${uiLabelMap.CommonDescription}</label>
					</div>
					<div class="span8">
						<input type="text" id="editPaymentTypeDesc"> 
					</div>
				</div>
				<div class='row-fluid' style="position: relative;">
					<div class="span4">
						<label class=''>${uiLabelMap.BACCChildOfType}</label>
					</div>
					<div class="span8">
						<div id="parentTypeDropDown">
							<div id="parentTypeGrid"></div>
						</div>
						<a id="clearParentType" tabindex="-1" href="javascript:void(0);" class="clear-value-jqx" title="clear" style="right: 0px;">
							<i class="fa fa-eraser"></i>
						</a>
					</div>
				</div>
				<div class='row-fluid' style="position: relative;">
					<div class="span4">
						<label class=''>${uiLabelMap.BACCGlAccountType}</label>
					</div>
					<div class="span8">
						<div id="glAccountTypeDropDown">
							<div id="glAccountTypeGrid"></div>
						</div>
						<a id="clearGlAccountType" tabindex="-1" href="javascript:void(0);" class="clear-value-jqx" title="clear" style="right: 0px;">
							<i class="fa fa-eraser"></i>
						</a>
					</div>
				</div>
				<div class='row-fluid' style="position: relative;">
					<div class="span4">
						<label class=''>${uiLabelMap.BACCRequiredAppliedInvoiceShort}</label>
					</div>
					<div class="span8">
						<div id="isAppliedInvoice" style="margin-left: -2px !important; margin-top: 6px"></div>
					</div>
				</div>
			</form>
		</div>
		<div class="form-action">
			<button type="button" class='btn btn-danger form-action-button pull-right'id="cancelEditPaymentType">
				<i class='icon-remove'></i>&nbsp;${uiLabelMap.CommonCancel}</button>	
			<button type="button" class='btn btn-primary form-action-button pull-right' id="saveEditPaymentType">
				<i class='fa fa-check'></i>&nbsp;${uiLabelMap.CommonSave}</button>
		</div>
	</div>		
</div>

<script type="text/javascript" src="/accresources/js/setting/paymentType/ViewPaymentTypeList.js"></script>	 					  
<script type="text/javascript" src="/accresources/js/setting/paymentType/editPaymentType.js"></script>	 					  