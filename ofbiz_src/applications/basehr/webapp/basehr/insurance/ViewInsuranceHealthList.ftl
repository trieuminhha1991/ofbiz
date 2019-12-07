<#include "script/ViewInsuranceHealthListScript.ftl"/>
<#assign datafield = "[{name: 'partyHealthInsId', type: 'string'},
					   {name: 'partyId', type: 'string'},
					   {name: 'partyCode', type: 'string'},
					   {name: 'fullName', type: 'string'},
					   {name: 'insHealthCard', type: 'string'},
					   {name: 'fromDate', type: 'date', other : 'Timestamp'},
					   {name: 'thruDate', type: 'date', other : 'Timestamp'},
					   {name: 'stateProvinceGeoId', type: 'string'},
					   {name: 'hospitalId', type: 'string'},
					   {name: 'hospitalName', type: 'string'},
					   {name: 'hospitalCode', type: 'string'},
					  ]"/>

<script type="text/javascript">

<#assign columnlist = "{text: '${StringUtil.wrapString(uiLabelMap.EmployeeId)}', datafield: 'partyCode', width: '10%'},
					   {text: '${StringUtil.wrapString(uiLabelMap.EmployeeName)}', datafield: 'fullName', width: '15%'},
					   {text: '${StringUtil.wrapString(uiLabelMap.HealthInsuranceNbr)}', datafield: 'insHealthCard', width: '14%'},
					   {text: '${StringUtil.wrapString(uiLabelMap.CommonFromDate)}', datafield: 'fromDate', cellsformat: 'dd/MM/yyyy', columntype: 'datetimeinput', width: '12%', filterType : 'range'},
					   {text: '${StringUtil.wrapString(uiLabelMap.CommonThruDate)}', datafield: 'thruDate', cellsformat: 'dd/MM/yyyy', columntype: 'datetimeinput', width: '12%', filterType : 'range'},
					   {text: '${StringUtil.wrapString(uiLabelMap.InsuranceStateProvinceGeoHospital)}', datafield: 'stateProvinceGeoId', width: '15%', columngroup: 'hospitalRegisters', columntype: 'dropdownlist',filterType : 'checkedlist',
						   cellsrenderer: function (row, column, value) {
								for(var i = 0; i < stateProvinceGeoArr.length; i++){
									if(stateProvinceGeoArr[i].geoId == value){
										var desc = '';
										if(stateProvinceGeoArr[i].codeNumber){
											desc += stateProvinceGeoArr[i].codeNumber;
										}
										desc += \" (\" + stateProvinceGeoArr[i].geoName + \")\";
										return '<span>' + desc + '</span>'; 
									}
								}
								return '<span>' + value + '</span>';
							},
							createfilterwidget : function(column, columnElement, widget){
								var source = {
										localdata : stateProvinceGeoArr,
										datatype : 'array'
								};
								var filterBoxAdapter = new $.jqx.dataAdapter(source, {autoBind : true});
								var dataFilter = filterBoxAdapter.records;
								//dataFilter.splice(0,0, '(${StringUtil.wrapString(uiLabelMap.filterselectallstring)})');
								widget.jqxDropDownList({source : dataFilter, valueMember : 'geoId', displayMember : 'geoName'});
								if(dataFilter.length <= 8){
									widget.jqxDropDownList({autoDropDownHeight : true});
								}else{
									widget.jqxDropDownList({autoDropDownHeight : false});
								}
							},
					   },
					   {text: '${StringUtil.wrapString(uiLabelMap.InsuranceHospital)}', datafield: 'hospitalName', columngroup: 'hospitalRegisters',
						   cellsrenderer: function (row, column, value) {
							   var data = $('#jqxgrid').jqxGrid('getrowdata', row);
							   return '<span>' + data.hospitalCode + ' (' + data.hospitalName + ')</span>';
						   },
					   },
					   "/>

<#assign columngroup = "{text: '${StringUtil.wrapString(uiLabelMap.HospitalOriginalRegistration)}', name: 'hospitalRegisters', align: 'center'}"/>
</script>
<@jqGrid filtersimplemode="true" dataField=datafield columnlist=columnlist columngrouplist=columngroup
		 filterable="true" editable="false" clearfilteringbutton="true"
		 url="" id="jqxgrid" jqGridMinimumLibEnable="false"
		 removeUrl="" deleteColumn="" 
		 addrow="false" 
		 deleterow="false" 
		 removeUrl="jqxGeneralServicer?sname=deletePartyInsuranceHealth&jqaction=D" deleteColumn="partyId;insHealthCard;fromDate(java.sql.Timestamp)"
		 selectionmode="singlerow" mouseRightMenu="true" contextMenuId="contextMenu"
		 customControlAdvance="<div id='jqxDatimeInput'></div>"/>	

<div id="contextMenu" class="hide">
	<ul>
		<li action="edit">
			<i class="icon-edit"></i>${uiLabelMap.CommonEdit}
        </li>        
	</ul>
</div>

<div class="row-fluid">
	<div id="EditPartyHealthInsWindow" class="hide">
		<div>${uiLabelMap.EditPartyHealthInsuranceInfo}</div>
		<div class="form-window-container">
			<div class="form-window-content">
				<div class='row-fluid margin-bottom10'>
					<div class="span4 text-algin-right">
						<label class="">${uiLabelMap.CommonEmployee}</label>
					</div>
					<div class="span8">
						<input type="text" id="editHealthInsPartyId">
					</div>
				</div>	
				<div class='row-fluid margin-bottom10'>
					<div class="span4 text-algin-right">
						<label class="">${uiLabelMap.HealthInsuranceNbr}</label>
					</div>
					<div class="span8">
						<input type="text" id="editHealthInsNbr">
					</div>
				</div>	
				<div class='row-fluid margin-bottom10'>
					<div class="span4 text-algin-right">
						<label class="">${uiLabelMap.InsurancePrimaryHCEstablishment}</label>
					</div>
					<div class="span8">
						<input type="text" id="editHealthInsHospitalName">
						<button class="btn btn-mini btn-primary" id="editHealthInsChooseHospitalBtn" style="" title="">
							<i class="icon-list icon-only"></i>
						</button>
					</div>
				</div>
				<div class='row-fluid margin-bottom10'>
					<div class="span4 text-algin-right">
						<label class="">${uiLabelMap.EffectiveFromDate}</label>
					</div>
					<div class="span8">
						<div class="row-fluid">
							<div style="display: inline-block; margin-right: 5px" id="editHealthInsMonthFrom" ></div>						
							<div style="display: inline-block;" id="editHealthInsYearFrom" ></div> 	
						</div>
					</div>
				</div>
				<div class='row-fluid'>
					<div class="span4 text-algin-right">
						<label class="">${uiLabelMap.DateExpire}</label>
					</div>
					<div class="span8">
						<div class="row-fluid">
							<div style="display: inline-block; margin-right: 5px" id="editHealthInsMonthThru" ></div>						
							<div style="display: inline-block;" id="editHealthInsYearThru" ></div> 	
						</div>
					</div>
				</div>	
			</div>
			<div class="form-action">
				<button id="cancelEditHealthIns" class='btn btn-danger form-action-button pull-right'>
					<i class='fa-remove'></i> ${uiLabelMap.CommonCancel}</button>
				<button type="button" class='btn btn-primary form-action-button pull-right' id="saveEditHealthIns">
					<i class='fa fa-check'></i>${uiLabelMap.CommonSave}</button>
			</div>
		</div>	
	</div>
</div>
					 
<script type="text/javascript" src="/hrresources/js/insurance/ViewInsuranceHealthList.js"></script>	 	
	
<#assign includeJs = "false"/>
<#assign defaultSuffix = ""/>
<#include "script/HosipitalListScript.ftl"/>

<#include "ViewListHosipitalList.ftl"/>

<script type="text/javascript" src="/hrresources/js/insurance/HosipitalListScript.js"></script>