<@jqGridMinimumLib/>
<#include "component://basesalesmtl/webapp/basesalesmtl/employee/ftLibraryVariable.ftl"/>

<script src="/salesmtlresources/js/employee/employeeDSA.js"></script>

<@jqGridMinimumLib/>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxtree.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxdropdownbutton.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxtabs.js"></script>
<#assign datafield = "[{name: 'partyId', type: 'string'},
					   {name: 'partyCode', type: 'string'},
					   {name: 'fullName', type: 'string'},
					   {name: 'gender', type: 'string'},
					   {name: 'birthDate', type: 'date'},
					   {name: 'emplPositionType', type: 'string'},
					   {name: 'directManager', type: 'string'},
					   {name: 'directManagerName', type: 'string'},
					   {name: 'nextMgrId', type: 'string'},
					   {name: 'nextManagerName', type: 'string'},
					   {name: 'partyGroupId', type: 'string'},
					   {name: 'partyGroupName', type: 'string'},
					   {name: 'channel', type: 'string'},
					   {name: 'departmentAddress', type: 'string'}]"/>
<script type="text/javascript">
	var genderArr = [
		{genderId: 'M', description: '${StringUtil.wrapString(uiLabelMap.Male)}'},
		{genderId: 'F', description: '${StringUtil.wrapString(uiLabelMap.Female)}'},
	];
	
	<#assign ethnicOriginList = delegator.findByAnd("EthnicOrigin", null, null, false)/>
	var ethnicOriginArr = [
		<#list ethnicOriginList as ethnicOrigin>
			{ethnicOriginId: '${ethnicOrigin.ethnicOriginId}', description: '${StringUtil.wrapString(ethnicOrigin.description)}?default("")'}
			<#if ethnicOrigin_has_next>
			,
			</#if>
		</#list>
	];
	<#assign columnlist = "{text: '${uiLabelMap.EmployeeName}', datafield: 'fullName', width: 130, pinned: true},
						   {text: '${uiLabelMap.EmployeeId}', datafield: 'partyCode', width: 100, pinned: true},
						   {text: '${uiLabelMap.PartyBirthDate}', datafield: 'birthDate', width: 90, cellsformat: 'dd/MM/yyyy'},
						   {text: '${uiLabelMap.PartyGender}', datafield: 'gender', width: 80,
							   cellsrenderer: function (row, columnfield, value, defaulthtml, columnproperties){
								   for(var i = 0; i < genderArr.length; i++){
									   if(genderArr[i].genderId == value){
										   return '<div style=\"margin-top: 4px; margin-left: 2px\">' + genderArr[i].description + '</div>'; 
									   }
								   }
								   return '<div style=\"margin-top: 4px; margin-left: 2px\">' +  value + '</div>';
							   }
						   },
						   {text: '${uiLabelMap.HREmplFromPositionType}', datafield: 'emplPositionType', width: 200},
						   {datafield: 'partyGroupId', hidden: true},
						   {text: '${uiLabelMap.PartyIdWork}', datafield: 'partyGroupName', width: 200},
						   {text: '${uiLabelMap.HRCommonChannel}', datafield: 'channel', width: 130},
						   {text: '${uiLabelMap.DirectManager}', datafield: 'directManagerName', width: 130}, 
						   {datafield: 'nextMgrId', hidden: true}, 
						   {text: '${uiLabelMap.NextManager}', datafield: 'nextManagerName', width: 130,
							   cellsrenderer: function (row, columnfield, value, defaulthtml, columnproperties){
								   var data = $('#jqxgrid').jqxGrid('getrowdata', row);
								   if(value){
									   return '<span>' + value + '</span>';   
								   }
							   }
						   },
						   {datafield: 'directManager', hidden: true},
						   {text: '${uiLabelMap.HRPlaceWork}', datafield: 'departmentAddress', width: 100}"/>
    
	<#assign rowDetails = "function (index, parentElement, gridElement, datarecord){
		 var partyId = datarecord.partyId;
		 var id = datarecord.uid.toString();
		 var tabsdiv = $($(parentElement).children()[0]);
		 if(tabsdiv != null){
			 var divContainer = tabsdiv.find('.informationDetails');
			 var container = $('<div style=\"margin: 5px;\"></div>');
			 container.appendTo(divContainer);
			 var photocolumn = $('<div style=\"float: left; width: 15%;\"></div>');
             var leftcolumn = $('<div style=\"float: left; width: 25%;\"></div>');
             var rightcolumn = $('<div style=\"float: left; width: 60%;\"></div>');
             container.append(photocolumn);
             container.append(leftcolumn);
             container.append(rightcolumn);
             var photo = $(\"<div class='jqx-rc-all'></div>\");
             var image = $(\"<div style='margin-top: 10px;'></div>\");
             var imgurl = '/aceadmin/assets/avatars/no-avatar.png';
             var img = $('<img height=\"130\" style=\"height: 130px\" src=\"' + imgurl + '\"/>');
             image.append(img);
             image.appendTo(photo);
             photocolumn.append(photo);
             var idNumber = \"<div style='margin: 10px;'><b>${StringUtil.wrapString(uiLabelMap.IDNumber)}: </b><span id='idNumber_\" + id + \"'></span></div>\";
             var idIssueDate = \"<div style='margin: 10px;'><b>${StringUtil.wrapString(uiLabelMap.HrolbiusidIssueDate)}: </b><span id='idIssueDate_\" + id + \"'></span></div>\";
             var idIssuePlace = \"<div style='margin: 10px;'><b>${StringUtil.wrapString(uiLabelMap.HrolbiusidIssuePlace)}: </b><span id='idIssuePlace_\" + id + \"'></span></div>\";
             var nativeLand = \"<div style='margin: 10px;'><b>${StringUtil.wrapString(uiLabelMap.NativeLand)}: </b><span id='nativeLand_\" + id + \"'></span></div>\";
             var ethnic = \"<div style='margin: 10px;'><b>${StringUtil.wrapString(uiLabelMap.EthnicOrigin)}: </b><span id='ethnic_\" + id + \"'></span></div>\";
             $(leftcolumn).append(idNumber);
             $(leftcolumn).append(idIssueDate);
             $(leftcolumn).append(idIssuePlace);
             $(leftcolumn).append(nativeLand);
             $(leftcolumn).append(ethnic);
             
             var permanentResidence = \"<div style='margin: 10px;'><b>${StringUtil.wrapString(uiLabelMap.PermanentResidence)}: </b><span id='permanentResidence_\" + id + \"'></span></div>\";
             var currentResidence = \"<div style='margin: 10px;'><b>${StringUtil.wrapString(uiLabelMap.CurrentResidence)}: </b><span id='currentResidence_\" + id + \"'></span></div>\";
             var educationSystemTypeId = \"<div style='margin: 10px;'><b>${StringUtil.wrapString(uiLabelMap.Level)}: </b><span id='educationSystemTypeId_\" + id + \"'></span></div>\";
             var schoolId = \"<div style='margin: 10px;'><b>${StringUtil.wrapString(uiLabelMap.HRCommonSchool)}: </b><span  id='schoolId_\" + id + \"'></span></div>\";
             var majorId = \"<div style='margin: 10px;'><b>${StringUtil.wrapString(uiLabelMap.HRSpecialization)}: </b><span id='majorId_\" + id + \"'></span></div>\";
             $(rightcolumn).append(permanentResidence);
             $(rightcolumn).append(currentResidence);
             $(rightcolumn).append(educationSystemTypeId);
             $(rightcolumn).append(schoolId);
             $(rightcolumn).append(majorId);
             $(tabsdiv).jqxTabs({ width: '950px', height: 220});
             $.ajax({
            	url: 'getPartyInformation',
            	data: {partyId: partyId},
            	type: 'POST',
            	success: function(data){
            		if(data.partyInfo){
            			var partyInfo = data.partyInfo;
            			if(partyInfo.ethnicOrigin){
            				$('#ethnic_' + id).text(partyInfo.ethnicOrigin);
            			}
            			if(partyInfo.idIssueDate){
            				var idIssueDate = new Date(partyInfo.idIssueDate);
            				$('#idIssueDate_' + id).text(idIssueDate.getDate() + '/' + (idIssueDate.getMonth() + 1) + '/' + idIssueDate.getFullYear());
            			}
            			if(partyInfo.idIssuePlace){
            				$('#idIssuePlace_' + id).text(partyInfo.idIssuePlace);
            			}
            			if(partyInfo.idNumber){
            				$('#idNumber_' + id).text(partyInfo.idNumber);
            			}
            			if(partyInfo.nativeLand){
            				$('#nativeLand_' + id).text(partyInfo.nativeLand);
            			}
            			if(partyInfo.permanentResidence){
            				$('#permanentResidence_' + id).text(partyInfo.permanentResidence);
            			}
            			if(partyInfo.currentResidence){
            				$('#currentResidence_' + id).text(partyInfo.currentResidence);
            			}
            			if(partyInfo.schoolId){
            				$('#schoolId_' + id).text(partyInfo.schoolId);
            			}
            			if(partyInfo.majorId){
            				$('#majorId_' + id).text(partyInfo.majorId);
            			}
            			if(partyInfo.educationSystemTypeId){
            				$('#educationSystemTypeId_' + id).text(partyInfo.educationSystemTypeId);
            			}
            		}
            	}
             });
		 }
	}"/>		
	
	<#assign rowdetailstemplateAdvance = "<ul style='margin-left: 30px;'><li class='title'>${StringUtil.wrapString(uiLabelMap.profile)}</li></ul><div class='informationDetails'></div>"/>
	<#if expandedList?has_content>
		<#assign expandTreeId=expandedList[0]>
	<#else>
		<#assign expandTreeId="">
	</#if>
			
				
</script>		
<#assign nowTimestamp = Static["org.ofbiz.base.util.UtilDateTime"].nowTimestamp()/>
<#assign monthStart = Static["org.ofbiz.base.util.UtilDateTime"].getMonthStart(nowTimestamp)/>
<#assign monthEnd = Static["org.ofbiz.base.util.UtilDateTime"].getMonthEnd(prevMonthStart, timeZone, locale)/>

<script type="text/javascript">
$(document).ready(function () {
	initJqxDateTime();
});

function initJqxDateTime(){
	$("#dateTimeInput").jqxDateTimeInput({ width: 250, height: 25,  selectionMode: 'range', theme: 'olbius'});
	var fromDate = new Date(${monthStart.getTime()});
	var thruDate = new Date(${monthEnd.getTime()});
	$("#dateTimeInput").jqxDateTimeInput('setRange', fromDate, thruDate);
	$("#dateTimeInput").on('change', function(event){
		var selection = $("#dateTimeInput").jqxDateTimeInput('getRange');
		var fromDate = selection.from.getTime();
	    var thruDate = selection.to.getTime();
	    var item = $("#jqxTree").jqxTree('getSelectedItem');
	    var partyId = item.value;
	    refreshGridData(partyId, fromDate, thruDate);
	});
}

function setDropdownContent(element){
	 var item = $("#jqxTree").jqxTree('getItem', element);
	 var dropDownContent = '<div style="position: relative; margin-left: 3px; margin-top: 5px;">' + item.label + '</div>';
     $("#jqxDropDownButton").jqxDropDownButton('setContent', dropDownContent);
}
function jqxTreeSelectFunc(event){
		var dataField = event.args.datafield;
		var rowBoundIndex = event.args.rowindex;
		var id = event.args.element.id;
	    var item = $('#jqxTree').jqxTree('getItem', event.args.element);
	    setDropdownContent(event.args.element);
	    var tmpS = $("#jqxgrid").jqxGrid('source');
        var partyId = item.value;
        var selection = $("#dateTimeInput").jqxDateTimeInput('getRange');
    	var fromDate = selection.from.getTime();
    	var thruDate = selection.to.getTime();
    	refreshGridData(partyId, fromDate, thruDate);
}

function refreshGridData(partyGroupId, fromDate, thruDate){
	var tmpS = $("#jqxgrid").jqxGrid('source');
	tmpS._source.url = "jqxGeneralServicer?sname=getEmployeeListDetailInfo&hasrequest=Y&partyGroupId=" + partyGroupId + "&fromDate=" + fromDate + "&thruDate=" + thruDate;
	$("#jqxgrid").jqxGrid('source', tmpS);
}
</script>
<div class="widget-box transparent no-bottom-border">
	<div class="widget-header">
		<h4>${uiLabelMap.HREmplList}</h4>
		<div class="widget-toolbar none-content">
		</div>
	</div>
	<div class="widget-body">
		<div class="row-fluid">
			<div class="span12">
				<div class="span6">
					<div class='row-fluid margin-bottom10'>
						<div class='span2' style="text-align: center;">
							<b>${uiLabelMap.Time}</b>
						</div>
						<div class="span7">
							<div id="dateTimeInput"></div>						
						</div>
					</div>
				</div>
				<div class="span6">
					<div class='row-fluid margin-bottom10'>						
						<div class="span12" style="margin-right: 15px">
							<div id="jqxDropDownButton" class="pull-right">
								<div style="border: none;" id="jqxTree">
								</div>
							</div>
						</div>
					</div>
				</div>
			</div>
		</div>
		<div class="row-fluid">
			<@jqGrid url="" dataField=datafield columnlist=columnlist
				clearfilteringbutton="true" id="jqxgrid" 
				editable="false" width="100%" filterable="false" sortable="false"
				initrowdetails="true" initrowdetailsDetail=rowDetails rowdetailsheight="250" rowdetailstemplateAdvance=rowdetailstemplateAdvance
				showtoolbar="false" deleterow="false" jqGridMinimumLibEnable="false"
			/>			
		</div>
	</div>
</div>
<@htmlTemplate.renderJqxTreeDropDownBtn treeDropDownSource=treePartyGroup expandTreeId=expandTreeId id="jqxTree" dropdownBtnId="jqxDropDownButton"/>