<#include "script/ViewListGlAccountTypeListScript.ftl"/>
<#assign datafield = "[{name: 'glAccountTypeId', type: 'string'},
                       {name: 'description', type: 'string'},
                       {name: 'parentTypeId', type: 'string'},
                       {name: 'parentTypeDesc', type: 'string'},
                       {name: 'glAccountId', type: 'string'},
                       {name: 'glAccountCode', type: 'string'},
                       {name: 'glAccountName', type: 'string'}
					  ]"/>
					  
<script type="text/javascript">
<#assign columnlist = "{text: '${StringUtil.wrapString(uiLabelMap.CommonId)}', datafield: 'glAccountTypeId', width: '18%'},
						{text: '${StringUtil.wrapString(uiLabelMap.CommonDescription)}', datafield: 'description', width: '22%'},
						{text: '${StringUtil.wrapString(uiLabelMap.BACCChildOfType)}', datafield: 'parentTypeDesc', width: '22%'},
						{text: '${StringUtil.wrapString(uiLabelMap.BACCDefaultGlAccountId)}', datafield: 'glAccountCode', width: '38%',
							   cellsrenderer: function (row, columnfield, value, defaulthtml, columnproperties) {
								   var rowData = $('#jqxgrid').jqxGrid('getrowdata', row);
								   if(rowData && value){
									   return '<span>' + value + ' - ' + rowData.glAccountName + '</span>'
								   }
							   }
						}
	"/>	
</script>	
<@jqGrid url="jqxGeneralServicer?sname=JQGetListGlAccountType" dataField=datafield columnlist=columnlist 
		 id="jqxgrid" filtersimplemode="true" showtoolbar="true" clearfilteringbutton="true" 
		 addrow="true" addType="popup" alternativeAddPopup="editGlAccountTypeWindow"
		 mouseRightMenu="true" contextMenuId="contextMenu"
		 />
		 
<div id='contextMenu' class="hide">
	<ul>
		<li action="edit">
			<i class="icon-edit"></i>${uiLabelMap.CommonEdit}
        </li>
	</ul>
</div>

<div id="editGlAccountTypeWindow" class="hide">
	<div>${uiLabelMap.BACCCreateNew}</div>
	<div class='form-window-container'>
		<div class='form-window-content'>
			<form class="form-horizontal form-window-content-custom">
				<div class='row-fluid'>
					<div class="span4">
						<label class='asterisk'>${uiLabelMap.CommonId}</label>
					</div>
					<div class="span8">
						<input type="text" id="editGlAccountTypeId"> 
					</div>
				</div>
				<div class='row-fluid'>
					<div class="span4">
						<label class='asterisk'>${uiLabelMap.CommonDescription}</label>
					</div>
					<div class="span8">
						<input type="text" id="editGlAccountTypeDesc"> 
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
						<label class=''>${uiLabelMap.BACCDefaultGlAccountId}</label>
					</div>
					<div class="span8">
						<div id="glAccountDropDown">
							<div id="glAccountGrid"></div>
						</div>
						<a id="clearGlAccount" tabindex="-1" href="javascript:void(0);" class="clear-value-jqx" title="clear" style="right: 0px;">
							<i class="fa fa-eraser"></i>
						</a>
					</div>
				</div>
			</form>
		</div>
		<div class="form-action">
			<button type="button" class='btn btn-danger form-action-button pull-right'id="cancelEditGlAccountType">
				<i class='icon-remove'></i>&nbsp;${uiLabelMap.CommonCancel}</button>	
			<button type="button" class='btn btn-primary form-action-button pull-right' id="saveEditGlAccountType">
				<i class='fa fa-check'></i>&nbsp;${uiLabelMap.CommonSave}</button>
		</div>
	</div>		
</div>

<script type="text/javascript" src="/accresources/js/setting/glAccountType/ViewGlAccountTypeList.js"></script>	 					  
<script type="text/javascript" src="/accresources/js/setting/glAccountType/editGlAccountType.js"></script>	 					  