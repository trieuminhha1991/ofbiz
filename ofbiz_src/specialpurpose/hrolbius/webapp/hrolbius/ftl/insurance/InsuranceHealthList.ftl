<@jqGridMinimumLib/>
<script src="/aceadmin/assets/js/bootbox.min.js" type="text/javascript"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxtree.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxsplitter.js"></script>	
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxvalidator.js"></script>
<script src="/delys/images/js/generalUtils.js"></script>
<#assign datafield = "[{name: 'partyId', type: 'string'},
					   {name: 'fullName', type: 'string'},
					   {name: 'insHealthCard', type: 'string'},
					   {name: 'fromDate', type: 'date'},
					   {name: 'thruDate', type: 'date'},
					   {name: 'stateProvinceGeoId', type: 'string'},
					   {name: 'hospitalId', type: 'string'},
					   {name: 'hospitalName', type: 'string'},
					   {name: 'hospitalCode', type: 'string'},
					  ]"/>

<#assign nowTimestamp = Static["org.ofbiz.base.util.UtilDateTime"].nowTimestamp()/>
<#assign startYear = Static["org.ofbiz.base.util.UtilDateTime"].getYearStart(nowTimestamp)/>
<#assign endYear = Static["org.ofbiz.base.util.UtilDateTime"].getYearEnd(nowTimestamp, timeZone, locale)/>
					  
<script type="text/javascript">
var stateProvinceGeoArr = [
   	<#if listStateProvinceGeoVN?has_content>
   		<#list listStateProvinceGeoVN as geo>
   			{
   				geoId: '${geo.geoId}',
   				geoName: '${StringUtil.wrapString(geo.geoName)}',
   				codeNumber: '${geo.codeNumber?if_exists}'
   			},
   		</#list>
   	</#if>
];
<#assign columnlist = "{text: '${StringUtil.wrapString(uiLabelMap.EmployeeId)}', datafield: 'partyId', width: 110},
					   {text: '${StringUtil.wrapString(uiLabelMap.EmployeeName)}', datafield: 'fullName', width: 150},
					   {text: '${StringUtil.wrapString(uiLabelMap.HealthInsuranceNbr)}', datafield: 'insHealthCard', width: 140},
					   {text: '${StringUtil.wrapString(uiLabelMap.CommonFromDate)}', datafield: 'fromDate', cellsformat: 'dd/MM/yyyy', columntype: 'datetimeinput', width: 130},
					   {text: '${StringUtil.wrapString(uiLabelMap.CommonThruDate)}', datafield: 'thruDate', cellsformat: 'dd/MM/yyyy', columntype: 'datetimeinput', width: 130},
					   {text: '${StringUtil.wrapString(uiLabelMap.InsuranceStateProvinceGeoHospital)}', datafield: 'stateProvinceGeoId', width: 160, columngroup: 'hospitalRegisters', columntype: 'dropdownlist',
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
							}   
					   },
					   {text: '${StringUtil.wrapString(uiLabelMap.InsuranceHospital)}', datafield: 'hospitalName', columngroup: 'hospitalRegisters', columntype: 'dropdownlist',
						   cellsrenderer: function (row, column, value) {
							   var data = $('#jqxgrid').jqxGrid('getrowdata', row);
							   return '<span>' + data.hospitalCode + ' (' + data.hospitalName + ')</span>';
						   }
					   },
					   {datafield: 'hospitalId', hidden: true},
					   {datafield: 'hospitalCode', hidden: true}
					   "/>

<#assign columngroup = "{text: '${StringUtil.wrapString(uiLabelMap.HospitalOriginalRegistration)}', name: 'hospitalRegisters', align: 'center'}"/>

</script>
<@jqGrid filtersimplemode="true" dataField=datafield columnlist=columnlist  showtoolbar="true" columngrouplist=columngroup
					 filterable="false" alternativeAddPopup="popupAddrow" deleterow="true" editable="false" addrow="true"
					 url="" id="jqxgrid" jqGridMinimumLibEnable="false"
					 removeUrl="" deleteColumn="" addType="popup"
					 updateUrl="" 
					 editColumns=""
					 createUrl="jqxGeneralServicer?jqaction=C&sname=createPartyInsuranceHealth"
					 addColumns="partyId;insHealthCard;hospitalId;fromDate(java.sql.Timestamp);thruDate(java.sql.Timestamp)" addrefresh="true"
					 removeUrl="jqxGeneralServicer?sname=deletePartyInsuranceHealth&jqaction=D" deleteColumn="partyId;insHealthCard;fromDate(java.sql.Timestamp)"
					 selectionmode="singlerow" 
					 customControlAdvance="<div id='jqxDatimeInput'></div>" />	
					 
<div class="row-fluid">
	<div id="popupAddrow" class="hide">
		<div id="windowHeader">
			${StringUtil.wrapString(uiLabelMap.AddEmplInsuranceHealth)}
		</div>
		<div class='form-window-container'>
			<div class="form-window-content">
				<div class='row-fluid margin-bottom10'>
					<div class="span5 text-algin-right">
						<label class="control-label asterisk">${uiLabelMap.CommonEmployee}</label>
					</div>
					<div class="span7">
						<input type="text" id="partyId">
						<img alt="search" id="searchEmpl" width="16" height="16" src="/aceadmin/assets/images/search_lg.png" 
						style="
						   border: #d5d5d5 1px solid;
						   padding: 4.5px;
						   border-bottom-right-radius: 3px;
						   border-top-right-radius: 3px;
						   margin-left: -4px;
						   background-color: #f0f0f0;
						   border-left: 0px;
						   cursor: pointer;
						"/>
					</div>
				</div>
				<div class='row-fluid margin-bottom10'>
					<div class="span5 text-algin-right">
						<label class="control-label asterisk">${uiLabelMap.HealthInsuranceNbr}</label>
					</div>
					<div class="span7">
						<input type="text" id="healthInsuranceNbrNew">
					</div>
				</div>
				<div class='row-fluid margin-bottom10'>
					<div class="span5 text-algin-right">
						<label class="control-label asterisk">${uiLabelMap.CommonFromDate}</label>
					</div>
					<div class="span7">
						<div id="fromDateNew"></div>
					</div>
				</div>
				<div class='row-fluid margin-bottom10'>
					<div class="span5 text-algin-right">
						<label class="control-label asterisk">${uiLabelMap.CommonThruDate}</label>
					</div>
					<div class="span7">
						<div id="thruDateNew"></div>
					</div>
				</div>
				<div class='row-fluid margin-bottom10'>
					<div class="span5 text-algin-right">
						<label class="control-label asterisk">${uiLabelMap.StateProviceHospitalGeoCode}</label>
					</div>
					<div class="span7">
						<div id="hospitalStateProvinceGeoId"></div>
					</div>
				</div>
				<div class='row-fluid margin-bottom10'>
					<div class="span5 text-algin-right">
						<label class="control-label asterisk">${uiLabelMap.HospitalOriginalRegistration}</label>
					</div>
					<div class="span7">
						<div id="hospitalOriginalRegisNew"></div>
					</div>
				</div>
			</div>	
			<div class="form-action">
				<button id="btnCancel" class='btn btn-danger form-action-button pull-right'>
					<i class='fa-remove'></i> ${uiLabelMap.Cancel}</button>
				<button type="button" class='btn btn-primary form-action-button pull-right' id="btnSave">
					<i class='fa fa-check'></i>${uiLabelMap.CommonSave}</button>
			</div>
		</div>
	</div>
</div>				
<div class="row-fluid">
	<div id="popupWindowEmplList" class='hide'>
		<div>
			${uiLabelMap.HREmplList}
		</div>
		<div class="">
			<div id="splitterEmplList" style="border: none;">
				<div style="overflow: hidden !important" class="jqx-hideborder jqx-hidescrollbars">
					<div id="jqxTreeEmplList"></div>
				</div>
				<div id="ContentPanel" style="overflow: hidden !important;">
	               <div class="jqx-hideborder jqx-hidescrollbars" >
	                   <div id="EmplListInOrg">
	                   </div>
	               </div>
	        	</div>
			</div>
		</div>
	</div>
</div>
<script type="text/javascript">
<#assign nowTimestamp = Static["org.ofbiz.base.util.UtilDateTime"].nowTimestamp()/>
<#assign startYear = Static["org.ofbiz.base.util.UtilDateTime"].getYearStart(nowTimestamp)/>
<#assign endYear = Static["org.ofbiz.base.util.UtilDateTime"].getYearEnd(nowTimestamp, timeZone, locale)/>
$(document).ready(function () {
	$("#jqxgrid").on('loadCustomControlAdvance', function(){
		$("#jqxDatimeInput").jqxDateTimeInput({ width: 220, height: 25,  selectionMode: 'range', theme: 'olbius'});
		$("#jqxDatimeInput").on('valueChanged', function (event){
			var selection = $("#jqxDatimeInput").jqxDateTimeInput('getRange');
			refreshGridData(selection.from, selection.to);
		});
		$("#jqxDatimeInput").jqxDateTimeInput('setRange', new Date(${startYear.getTime()}), new Date(${endYear.getTime()}));
	});
	
	initJqxDropdownlist();
	initJqxDropdownlistEvent();
	initJqxDateTimeInput();
	initJqxInput();
	initJqxWindow();
	initBtnEvent();
	initJqxValidator();
	createJqxGridSearchEmpl();
});

function refreshGridData(fromDate, thruDate){
	var source = $("#jqxgrid").jqxGrid('source');
	source._source.url = "jqxGeneralServicer?hasrequest=Y&sname=JQInsuranceHealthList&fromDate=" + fromDate.getTime() + "&thruDate=" + thruDate.getTime();
	$("#jqxgrid").jqxGrid('source', source);
}

function initJqxDateTimeInput(){
	$("#fromDateNew").jqxDateTimeInput({width: '98%', height: '25px', formatString: 'dd/MM/yyyy', theme: 'olbius'});
	$("#thruDateNew").jqxDateTimeInput({width: '98%', height: '25px', formatString: 'dd/MM/yyyy', theme: 'olbius'});
}

function initJqxValidator(){
	$("#popupAddrow").jqxValidator({
		rules: [
		{
			input: "#partyId",
			message: "${StringUtil.wrapString(uiLabelMap.FieldRequired?default(''))}",
			rule: 'required'
		},
		{
			input: "#healthInsuranceNbrNew",
			message: "${StringUtil.wrapString(uiLabelMap.FieldRequired?default(''))}",
			rule: 'required'
		},
		{
			input: "#fromDateNew",
			message: "${StringUtil.wrapString(uiLabelMap.FieldRequired?default(''))}",
			rule: 'required'
		},
		{
			input: "#hospitalOriginalRegisNew",
			message: "${StringUtil.wrapString(uiLabelMap.FieldRequired?default(''))}",
			rule: 'required'
		},
		{
			input: "#hospitalStateProvinceGeoId",
			message: "${StringUtil.wrapString(uiLabelMap.FieldRequired?default(''))}",
			rule: 'required'
		},
		{
			input: "#thruDateNew",
			message: "${StringUtil.wrapString(uiLabelMap.ThruDateMustGreaterThanFromDate)}",
			rule: function (input, commit){
				var fromDate = $("#fromDateNew").jqxDateTimeInput('val', 'date');
				var thruDate = input.jqxDateTimeInput('val', 'date');
				if(fromDate.getTime() > thruDate.getTime()){
					return false;
				}
				return true
			}
		},
		]
	});
}

function createJqxGridSearchEmpl(){
	var datafield =  [
   		{name: 'partyId', type: 'string'},
   		{name: 'partyName', type: 'string'},
   		{name: 'emplPositionType', type: 'string'},
   		{name: 'department', type: 'string'},
   		{name: 'dateJoinCompnay', type: 'date'},
   	];
   	var columnlist = [
      {text: '${uiLabelMap.EmployeeId}', datafield: 'partyId' , editable: false, cellsalign: 'left', width: 90, filterable: false},
   	  {text: '${uiLabelMap.EmployeeName}', datafield: 'partyName', editable: false, cellsalign: 'left', width: 150, filterable: false},
   	  {text: '${uiLabelMap.Position}', datafield: 'emplPositionType', editable: false, cellsalign: 'left', width: 130, filterable: false},
   	  {text: '${uiLabelMap.CommonDepartment}', datafield: 'department', editable: false, cellsalign: 'left', filterable: false},
   	  {text: '${uiLabelMap.DateJoinCompany}', hidden: true, datafield: 'dateJoinCompnay', cellsformat: 'dd/MM/yyyy', editable: false, cellsalign: 'left', filterable: true,
   		  filtertype: 'range'  
   	  },
   	];
   	var config = {
   		width: '100%', 
   		height: 467,
   		autoheight: false,
   		virtualmode: true,
   		showfilterrow: true,
   		showtoolbar: false,
   		selectionmode: 'singlerow',
   		pageable: true,
   		sortable: false,
        filterable: true,
        editable: false,
        selectionmode: 'singlerow',
        url: 'JQGetEmplListInOrg&hasrequest=Y',
        source: {pagesize: 15}
   	};
   	GridUtils.initGrid(config, datafield, columnlist, null, $("#EmplListInOrg"));
   	
   	$("#EmplListInOrg").on('rowdoubleclick', function(event){
   		var args = event.args;
   	    var boundIndex = args.rowindex;
   	    var data = $("#EmplListInOrg").jqxGrid('getrowdata', boundIndex);
   	    $('#popupWindowEmplList').jqxWindow('close');
   	    $("#partyId").jqxInput('val', {value: data.partyId, label: data.partyName + ' [' + data.partyId + ']'});  	
   	});
}

function initJqxInput(){
	var source = function(query, response){
		var dataApdapter = new $.jqx.dataAdapter(
			{
				datatype: "json",
				datafields:
                [
                    { name: 'partyId' },
                    { name: 'partyName'},
                ],
                url: "searchPartyId",
                data:{
                	maxRows: 12,
                },
			},
			{
				autoBind: true,
				formatData: function (data) {
                    data.partyId_startsWith = query;
                    return data;
                },
                loadComplete: function (data) {
                    if (data.listParty.length > 0) {
                        response($.map(data.listParty, function (item) {
                            return {
                                label: item.partyName + ' [' + item.partyId + ']',
                                value: item.partyId
                            }
                        }));
                    }
                }
			}
		);
	};
	$("#partyId").jqxInput({ placeHolder: "${StringUtil.wrapString(uiLabelMap.EnterEmployeeId)}", source: source,
		height: 21, width: '86%', minLength: 1, theme: 'olbius', valueMember: 'partyId', displayMember:'partyName', items: 12});
	
	$("#healthInsuranceNbrNew").jqxInput({placeHolder: "${StringUtil.wrapString(uiLabelMap.EnterInsuranceNbr)}", width: '96%', height: 21, theme: 'olbius'});
}

function initJqxWindow(){
	$("#popupAddrow").jqxWindow({showCollapseButton: false,autoOpen: false,
		maxWidth: 520, minWidth: 520, height: 340, width: 520, isModal: true, theme:'olbius',
		initContent: function(){
		}	
	});
	
	$("#popupAddrow").on('close', function(event){
		GridUtils.clearForm($(this));
		$("#popupAddrow").jqxValidator('hide');
	});
	$("#popupAddrow").on('open', function(event){
		<#if startYear?exists>
			$("#fromDateNew").val(new Date(${startYear.getTime()}));
		</#if>
		<#if endYear?exists>
			$("#thruDateNew").val(new Date(${endYear.getTime()}));
		</#if>
	});
}

function initJqxDropdownlist(){
	var sourceStateProvinceGeo = {
			localdata: stateProvinceGeoArr,
            datatype: "array"
	}
	var dataAdapterGeo = new $.jqx.dataAdapter(sourceStateProvinceGeo);
	$("#hospitalStateProvinceGeoId").jqxDropDownList({source: dataAdapterGeo, displayMember: "geoName", valueMember: "geoId", 
		height: 25, width: "98%", theme: 'olbius',
		renderer: function (index, label, value) {
			var data = stateProvinceGeoArr[index];
			return data.geoName + " [ ${StringUtil.wrapString(uiLabelMap.CommonAreaCode)}: " + data.codeNumber + "]"  
		}
	});
	
	$("#hospitalOriginalRegisNew").jqxDropDownList({source: [], displayMember: "hospitalName", valueMember: "hospitalId", 
		height: 25, width: '98%', theme: 'olbius', autoDropDownHeight: true
	});
}

function initJqxDropdownlistEvent(){
	$("#hospitalStateProvinceGeoId").on('select', function(event){
		var args = event.args;
		if(args){
			var provinceGeoId = args.item.value;
			fillHospitalDataByGeo(provinceGeoId, $("#hospitalOriginalRegisNew"));
		}
	});
	
	$('#popupWindowEmplList').jqxWindow({
	    showCollapseButton: true, autoOpen: false, maxWidth: "80%", minWidth: "50%", maxHeight: 500, height: 500, width: "80%", isModal: true, 
	    theme:'olbius', collapsed:false,
	    initContent: function () {  
	    	initJqxSplitter();
	    }
	});
	$('#popupWindowEmplList').on('open', function(event){
		<#if expandTreeId?has_content>
			$("#jqxTreeEmplList").jqxTree('expandItem', $("#${expandTreeId}_jqxTreeEmplList")[0]);
			$('#jqxTreeEmplList').jqxTree('selectItem', $("#${expandTreeId}_jqxTreeEmplList")[0]);
		</#if>
	});
	$('#popupWindowEmplList').on('close', function(event){
		$("#EmplListInOrg").jqxGrid('clearselection');
	});
}

function initBtnEvent(){
	$("#searchEmpl").click(function(event){
		openJqxWindow($("#popupWindowEmplList"));
	});
	
	$("#btnCancel").click(function(event){
		$("#popupAddrow").jqxWindow('close');
	});
	$("#btnSave").click(function(event){
		var valid = $("#popupAddrow").jqxValidator('validate');
		if(!valid){
			return;
		}
		$(this).attr("disabled", "disabled");
		bootbox.dialog('${StringUtil.wrapString(uiLabelMap.ConfirmAddEmplInsuranceHealth)}',
				[
				 {
					"label" : "${uiLabelMap.CommonSubmit}",
	    		    "class" : "btn-primary btn-mini icon-ok",
	    		    "callback": function() {
	    		 		createNewInsuranceHealth();
	    		 		$("#btnSave").removeAttr("disabled");
	    		    	$("#popupAddrow").jqxWindow('close');
	    		    }	
				},
				{
					"label" : "${uiLabelMap.CommonClose}",
	    		    "class" : "btn-danger btn-mini icon-remove",
	    		    "callback": function() {
	    		    	$("#btnSave").removeAttr("disabled");
	    		    }
				}
				]	
		);
	});
}

function createNewInsuranceHealth(){
	var row ={
		partyId: $("#partyId").jqxInput('val').value,
		insHealthCard: $("#healthInsuranceNbrNew").val(),
		fromDate: $("#fromDateNew").jqxDateTimeInput('getDate'),
		thruDate: $("#thruDateNew").jqxDateTimeInput('getDate'),
		hospitalId: $("#hospitalOriginalRegisNew").val(),
	};
	$("#jqxgrid").jqxGrid('addrow', null, row, "first");
}

function openJqxWindow(jqxWindowDiv){
	var wtmp = window;
	var tmpwidth = jqxWindowDiv.jqxWindow('width');
	jqxWindowDiv.jqxWindow({ position: { x: (wtmp.outerWidth - tmpwidth)/2, y: pageYOffset + 80 } });
	jqxWindowDiv.jqxWindow('open');
}

function fillHospitalDataByGeo(stateProvinceGeoId, hospitalEle){
	$.ajax({
		url: 'getHospitalInStateProvinceGeo',
		data: {stateProvinceGeoId: stateProvinceGeoId},
		type: 'POST',
		success: function(data){
			if(data._EVENT_MESSAGE_){
				var listHospital = data.listReturn;
				if(listHospital.length > 0){
					var sourceHospitalSource = {
							localdata: listHospital,
			                datatype: "array"
					}
					var dataAdapter = new $.jqx.dataAdapter(sourceHospitalSource);
					hospitalEle.jqxDropDownList({source: dataAdapter,
						renderer: function (index, label, value) {
							var data = listHospital[index];
							return data.hospitalName + " [ ${StringUtil.wrapString(uiLabelMap.CommonId)}: " + data.hospitalCode + "]"  
						}	
					});
					if(listHospital.length < 8){
						hospitalEle.jqxDropDownList({autoDropDownHeight: true});
					}else{
						hospitalEle.jqxDropDownList({autoDropDownHeight: false});
					}
				}else{
					hospitalEle.jqxDropDownList({source: [], autoDropDownHeight: true});
				}
			}
		}
	});
}

function initJqxSplitter(){
	$("#splitterEmplList").jqxSplitter({  width: '100%', height: '100%', panels: [{ size: '25%'}, {size: '75%'}] });
}

</script>	 	
<#if expandedList?has_content>
		<#assign expandTreeId=expandedList[0]>
<#else>
	<#assign expandTreeId="">
</#if>
<@htmlTemplate.renderJqxTreeDropDownBtn treeDropDownSource=treePartyGroup id="jqxTreeEmplList" 
	jqxTreeSelectFunc="jqxTreeEmplListSelect" expandTreeId=expandTreeId isDropDown="false" width="100%" height="100%" expandAll="false"/>
	
<script type="text/javascript">
function jqxTreeEmplListSelect(event){
	var item = $('#jqxTreeEmplList').jqxTree('getItem', event.args.element);
	var partyId = item.value;
	var tmpS = $("#EmplListInOrg").jqxGrid('source');
	tmpS._source.url = 'jqxGeneralServicer?sname=JQGetEmplListInOrg&hasrequest=Y&partyGroupId=' + partyId;
	$("#EmplListInOrg").jqxGrid('source', tmpS);
}
</script>				  