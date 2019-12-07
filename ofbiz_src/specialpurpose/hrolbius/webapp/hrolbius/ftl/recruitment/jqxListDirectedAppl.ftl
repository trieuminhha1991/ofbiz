<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxpasswordinput.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxtooltip.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxvalidator.js"></script>
<script type="text/javascript" src="/aceadmin/assets/js/bootbox.min.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxmaskedinput.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxtabs.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxeditor.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxradiobutton.js"></script>
<@jqGridMinimumLib />
<script>
	//Prepare for status data
	<#assign listStatus = delegator.findList("StatusItem", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("statusTypeId", Static["org.ofbiz.entity.condition.EntityOperator"].IN , ["PROB_STATUS"]), null, null, null, false)>
	var statusData = new Array();
	<#list listStatus as item>
		var row = {};
		<#assign description = StringUtil.wrapString(item.description)>
		row['statusId'] = '${item.statusId}';
		row['description'] = '${description}';
		statusData[${item_index}] = row;
	</#list>
	
	var genderData = new Array();
	var row = {};
	row['gender'] = 'M';
	row['description'] = "${uiLabelMap.Male}";
	genderData[0] = row;
	
	var row = {};
	row['gender'] = 'F';
	row['description'] = "${uiLabelMap.Female}";
	genderData[1] = row;
	
	//Prepare data for nationality 
	<#assign listNationality = delegator.findList("Nationality", null, null, null, null, false) >
	var nationalitypeData = new Array();
	<#list listNationality as item>
		var row = {};
		<#assign description = StringUtil.wrapString(item.description?if_exists) />
		row['nationalityId'] = '${item.nationalityId?if_exists}';
		row['description'] = '${description}';
		nationalitypeData[${item_index}] = row;
	</#list>
	
	//Prepare data for maritalStatus 
	<#assign listMaritalStatus = delegator.findList("MaritalStatus", null, null, null, null, false) >
	var maritalStatusData = new Array();
	<#list listMaritalStatus as item>
		var row = {};
		<#assign description = StringUtil.wrapString(item.description?if_exists) />
		row['maritalStatusId'] = '${item.maritalStatusId?if_exists}';
		row['description'] = '${description}';
		maritalStatusData[${item_index}] = row;
	</#list>
	
	//Prepare data for ethnicOrigin 
	<#assign listEthnicOrigin = delegator.findList("EthnicOrigin", null, null, null, null, false) >
	var ethnicOriginData = new Array();
	<#list listEthnicOrigin as item>
		var row = {};
		<#assign description = StringUtil.wrapString(item.description?if_exists) />
		row['maritalStatusId'] = '${item.ethnicOriginId?if_exists}';
		row['description'] = '${description}';
		ethnicOriginData[${item_index}] = row;
	</#list>
	
	//Prepare data for religion 
	<#assign listReligion = delegator.findList("Religion", null, null, null, null, false) >
	var religionData = new Array();
	<#list listReligion as item>
		var row = {};
		<#assign description = StringUtil.wrapString(item.description?if_exists) />
		row['religionId'] = '${item.religionId?if_exists}';
		row['description'] = '${description}';
		religionData[${item_index}] = row;
	</#list>
	
	//Prepare data for employment app source type 
	<#assign listEmplAppSourceTypes = delegator.findList("EmploymentAppSourceType", null, null, null, null, false) >
	var emplAppSourceTypeData = new Array();
	<#list listEmplAppSourceTypes as item>
		var row = {};
		<#assign description = StringUtil.wrapString(item.description?if_exists) />
		row['employmentAppSourceTypeId'] = '${item.employmentAppSourceTypeId?if_exists}';
		row['description'] = '${description}';
		emplAppSourceTypeData[${item_index}] = row;
	</#list>
	
	//Prepare data for partyRelationshipType
	<#assign listPartyRelationshipTypes = delegator.findList("PartyRelationshipType", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("parentTypeId", Static["org.ofbiz.entity.condition.EntityOperator"].IN , ["FAMILY"]), null, null, null, false)>
	var partyRelaTypeData = new Array();
	<#list listPartyRelationshipTypes as item>
		var row = {};
		<#assign description = StringUtil.wrapString(item.partyRelationshipName?if_exists) />
		row['partyRelationshipTypeId'] = '${item.partyRelationshipTypeId?if_exists}';
		row['description'] = '${description}';
		partyRelaTypeData[${item_index}] = row;
	</#list>
	
	//Prepare data for friendType
	<#assign listFriendTypes = delegator.findList("PartyRelationshipType", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("parentTypeId", Static["org.ofbiz.entity.condition.EntityOperator"].IN , ["FRIEND"]), null, null, null, false)>
	var friendTypeData = new Array();
	<#list listFriendTypes as item>
		var row = {};
		<#assign description = StringUtil.wrapString(item.partyRelationshipName?if_exists) />
		row['partyRelationshipTypeId'] = '${item.partyRelationshipTypeId?if_exists}';
		row['description'] = '${description}';
		friendTypeData[${item_index}] = row;
	</#list>
	
	//Prepare data for school
	<#assign schoolList = delegator.findByAnd("EducationSchool", null, null, false)>
	var schoolData = new Array();
	<#list schoolList as item>
		var row = {};
		<#assign description = StringUtil.wrapString(item.schoolName?if_exists) />
		row['schoolId'] = '${item.schoolId?if_exists}';
		row['description'] = '${description}';
		schoolData[${item_index}] = row;
	</#list>
	
	//Prepare data for Major
	<#assign majorList = delegator.findList("Major", null , null, orderBy,null, false)>
	var majorData = new Array();
	<#list majorList as item>
		var row = {};
		<#assign description = StringUtil.wrapString(item.description?if_exists) />
		row['majorId'] = '${item.majorId?if_exists}';
		row['description'] = '${description}';
		majorData[${item_index}] = row;
	</#list>
	
	//Prepare StudyModeType
	<#assign studyModeTypeList = delegator.findByAnd("StudyModeType", null ,null , false)>
	var studyModeTypeData = new Array();
	<#list studyModeTypeList as item>
		var row = {};
		<#assign description = StringUtil.wrapString(item.description?if_exists) />
		row['studyModeTypeId'] = '${item.studyModeTypeId?if_exists}';
		row['description'] = '${description}';
		studyModeTypeData[${item_index}] = row;
	</#list>
	
	//Prepare classificationType
	<#assign degreeClassificationTypeList = delegator.findByAnd("DegreeClassificationType", null, null, false)>
	var classificationTypeData = new Array();
	<#list degreeClassificationTypeList as item>
		var row = {};
		<#assign description = StringUtil.wrapString(item.description?if_exists) />
		row['classificationTypeId'] = '${item.classificationTypeId?if_exists}';
		row['description'] = '${description}';
		classificationTypeData[${item_index}] = row;
	</#list>
	
	//Prepare EducationSystemType
	<#assign educationSystemTypeList = delegator.findByAnd("EducationSystemType", null, null, false)>
	var educationSystemTypeData = new Array();
	<#list educationSystemTypeList as item>
		var row = {};
		<#assign description = StringUtil.wrapString(item.description?if_exists) />
		row['educationSystemTypeId'] = '${item.educationSystemTypeId?if_exists}';
		row['description'] = '${description}';
		educationSystemTypeData[${item_index}] = row;
	</#list>
	
	//Prepare Skill Type
	<#assign skillTypeList = delegator.findByAnd("SkillType", null, null, false)>
	var skillTypeData = new Array();
	<#list skillTypeList as item>
		var row = {};
		<#assign description = StringUtil.wrapString(item.description?if_exists) />
		row['skillTypeId'] = '${item.skillTypeId?if_exists}';
		row['description'] = '${description}';
		skillTypeData[${item_index}] = row;
	</#list>
	
	//Prepare Skill Level
	<#assign skillLevelList = delegator.findByAnd("SkillLevel", null, null, false)>
	var skillLevelData = new Array();
	<#list skillLevelList as item>
		var row = {};
		<#assign description = StringUtil.wrapString(item.description?if_exists) />
		row['skillLevelId'] = '${item.skillLevelId?if_exists}';
		row['description'] = '${description}';
		skillLevelData[${item_index}] = row;
	</#list>
	
	//Prepare Country Geo
	<#assign countrylList = delegator.findByAnd("Geo", Static["org.ofbiz.base.util.UtilMisc"].toMap("geoTypeId", "COUNTRY"), null, false)>
	var countryData = new Array();
	<#list countrylList as item>
		var row = {};
		<#assign description = StringUtil.wrapString(item.geoName?if_exists) />
		row['geoId'] = '${item.geoId?if_exists}';
		row['description'] = "${description}";
		countryData[${item_index}] = row;
	</#list>
	
	//Prepare Province/City Geo
	<#assign provincelList = delegator.findList("GeoAssocAndGeoToDetail", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("geoTypeId", Static["org.ofbiz.entity.condition.EntityOperator"].IN , ["CITY", "PROVINCE"]), null, null, null, false)>
	var provinceData = new Array();
	<#list provincelList as item>
		var row = {};
		<#assign description = StringUtil.wrapString(item.geoName?if_exists) />
		row['geoId'] = '${item.geoId?if_exists}';
		row['geoIdFrom'] = '${item.geoIdFrom}';
		row['description'] = "${description}";
		provinceData[${item_index}] = row;
	</#list>
	
	//Prepare District Geo
	<#assign districtlList = delegator.findByAnd("GeoAssocAndGeoToDetail", Static["org.ofbiz.base.util.UtilMisc"].toMap("geoTypeId", "DISTRICT"), null, false)>
	var districtData = new Array();
	<#list districtlList as item>
		var row = {};
		<#assign description = StringUtil.wrapString(item.geoName?if_exists) />
		row['geoId'] = '${item.geoId?if_exists}';
		row['geoIdFrom'] = '${item.geoIdFrom}';
		row['description'] = "${description}";
		districtData[${item_index}] = row;
	</#list>
	
	//Prepare Ward Geo
	<#assign wardList = delegator.findByAnd("GeoAssocAndGeoToDetail", Static["org.ofbiz.base.util.UtilMisc"].toMap("geoTypeId", "WARD"), null, false)>
	var wardData = new Array();
	<#list wardList as item>
		var row = {};
		<#assign description = StringUtil.wrapString(item.geoName?if_exists) />
		row['geoId'] = '${item.geoId?if_exists}';
		row['geoIdFrom'] = '${item.geoIdFrom}';
		row['description'] = "${description}";
		wardData[${item_index}] = row;
	</#list>
	
	//Prepare for status data
	<#assign listStatus = delegator.findList("StatusItem", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("statusTypeId", Static["org.ofbiz.entity.condition.EntityOperator"].IN , ["APPLICANT_STATUS", "AVAI_APPL_STATUS", "PROB_STATUS"]), null, null, null, false)>
	var statusData = new Array();
	<#list listStatus as item>
		var row = {};
		<#assign description = StringUtil.wrapString(item.description)>
		row['statusId'] = '${item.statusId?if_exists}';
		row['description'] = '${description}';
		statusData[${item_index}] = row;
	</#list>
	
	//Prepare for ChildBackground data
	<#assign listChildBackgrounds = delegator.findList("ChildBackground", null, null, null, null, false)>
	var childBackgroundData = new Array();
	<#list listChildBackgrounds as item>
		var row = {};
		<#assign description = StringUtil.wrapString(item.description)>
		row['childBackgroundId'] = '${item.childBackgroundId?if_exists}';
		row['description'] = '${description}';
		childBackgroundData[${item_index}] = row;
	</#list>
	
	//Prepare for SpousesBackground data
	<#assign listSpousesBackgrounds = delegator.findList("SpousesBackground", null, null, null, null, false)>
	var spousesBackgroundData = new Array();
	<#list listSpousesBackgrounds as item>
		var row = {};
		<#assign description = StringUtil.wrapString(item.description)>
		row['spousesBackgroundId'] = '${item.spousesBackgroundId?if_exists}';
		row['description'] = '${description}';
		spousesBackgroundData[${item_index}] = row;
	</#list>
	
	//Prepare for ParentBackground data
	<#assign listParentBackgrounds = delegator.findList("ParentBackground", null, null, null, null, false)>
	var parentBackgroundData = new Array();
	<#list listParentBackgrounds as item>
		var row = {};
		<#assign description = StringUtil.wrapString(item.description)>
		row['parentBackgroundId'] = '${item.parentBackgroundId?if_exists}';
		row['description'] = '${description}';
		parentBackgroundData[${item_index}] = row;
	</#list>
	
	//Prepare for SiblingBackground data
	<#assign listSiblingBackgrounds = delegator.findList("SiblingBackground", null, null, null, null, false)>
	var siblingBackgroundData = new Array();
	<#list listSiblingBackgrounds as item>
		var row = {};
		<#assign description = StringUtil.wrapString(item.description)>
		row['siblingBackgroundId'] = '${item.siblingBackgroundId?if_exists}';
		row['description'] = '${description}';
		siblingBackgroundData[${item_index}] = row;
	</#list>
	
	//Prepare for SiblingBackground data
	<#assign listWorkChanges = delegator.findList("WorkChange", null, null, null, null, false)>
	var workChangeData = new Array();
	<#list listWorkChanges as item>
		var row = {};
		<#assign description = StringUtil.wrapString(item.description)>
		row['workChangeId'] = '${item.workChangeId?if_exists}';
		row['description'] = '${description}';
		workChangeData[${item_index}] = row;
	</#list>
	
	//Prepare for UniCertificate data
	<#assign listUniCertificates = delegator.findList("UniCertificate", null, null, null, null, false)>
	var uniCertificateData = new Array();
	<#list listUniCertificates as item>
		var row = {};
		<#assign description = StringUtil.wrapString(item.description)>
		row['uniCertificateId'] = '${item.uniCertificateId?if_exists}';
		row['description'] = '${description}';
		uniCertificateData[${item_index}] = row;
	</#list>
	
	//Prepare for itCertificateData
	<#assign listItCertificates = delegator.findList("ItCertificate", null, null, null, null, false)>
	var itCertificateData = new Array();
	<#list listItCertificates as item>
		var row = {};
		<#assign description = StringUtil.wrapString(item.description)>
		row['itCertificateId'] = '${item.itCertificateId?if_exists}';
		row['description'] = '${description}';
		itCertificateData[${item_index}] = row;
	</#list>
	
	//Prepare for engCertificateData
	<#assign listEngCertificates = delegator.findList("EngCertificate", null, null, null, null, false)>
	var engCertificateData = new Array();
	<#list listEngCertificates as item>
		var row = {};
		<#assign description = StringUtil.wrapString(item.description)>
		row['engCertificateId'] = '${item.engCertificateId?if_exists}';
		row['description'] = '${description}';
		engCertificateData[${item_index}] = row;
	</#list>
	
	//Prepare data for Employee Position Type 
	<#assign listAllEmplPositionTypes = delegator.findList("EmplPositionType", null, null, null, null, false) >
	var allPositionTypeData = new Array();
	<#list listAllEmplPositionTypes as item>
		var row = {};
		<#assign description = StringUtil.wrapString(item.description?if_exists) />
		row['emplPositionTypeId'] = '${item.emplPositionTypeId}';
		row['description'] = '${description}';
		allPositionTypeData[${item_index}] = row;
	</#list>
	
	//Prepare data for Role Type 
	<#assign roleTypeList = delegator.findList("RoleType", null, null, null, null, false) >
	var roleTypeData = new Array();
	<#list roleTypeList as item>
		var row = {};
		<#assign description = StringUtil.wrapString(item.description?if_exists) />
		row['roleTypeId'] = '${item.roleTypeId}';
		row['description'] = '${description}';
		roleTypeData[${item_index}] = row;
	</#list>
	
	var partyColumnFilter = function () {
        var filtergroup = new $.jqx.filter();
        var filter_or_operator = 1;
        var filtervalue = '${parameters.partyId?if_exists}';
        var filtercondition = 'contains';
        var filter = filtergroup.createfilter('stringfilter', filtervalue, filtercondition);
        filtergroup.addfilter(filter_or_operator, filter);
        return filtergroup;
    }();
</script>

<#assign dataField="[{ name: 'partyId', type: 'string'},
					 { name: 'firstName', type: 'string'},
					 { name: 'offerProbationId', type: 'string'},
					 { name: 'roleTypeId', type: 'string'},
					 { name: 'basicSalary', type: 'number'},
					 { name: 'trafficAllowance', type: 'number'},
					 { name: 'phoneAllowance', type: 'number'},
					 { name: 'otherAllowance', type: 'number'},
					 { name: 'inductedStartDate', type: 'date'},
					 { name: 'inductedCompletionDate', type: 'date'},
					 { name: 'percentBasicSalary', type: 'number'},
					 { name: 'lastName', type: 'string'},
					 { name: 'middleName', type: 'string'},
					 { name: 'comment', type: 'string'},
					 { name: 'statusId', type: 'string' },
					 { name: 'approverPartyId', type: 'string' },
					 { name: 'approverRoleTypeId', type: 'string' },
					 { name: 'workEffortId', type: 'string' },
					 { name: 'emplPositionTypeId', type: 'string' },
					 { name: 'partyIdWork', type: 'string' }
					 ]"/>

<#assign columnlist="{ text: '${uiLabelMap.CommonId}', datafield: 'partyId', width: 80, editable: false,pinned : true,filter: partyColumnFilter,
						cellsrenderer: function (row, columnfield, value, defaulthtml, columnproperties) {
							return '<span><a href=EmployeeProfile?partyId=' + value + '>' + value + '</a></span>';
					 	}
					 },
                     { text: '${uiLabelMap.fullName}', width: 150, editable:false, pinned : true,
						cellsrenderer: function (row, columnfield, value, defaulthtml, columnproperties) {
                    	 	var rowData = $('#jqxgrid').jqxGrid('getrowdata', row);
                    	 	return '<span>' + rowData['lastName'] + ' ' + (rowData['middleName'] ? rowData['middleName'] : '') + ' ' + rowData['firstName'] + '</span>'
                     	}
                     },
                     { text: '${uiLabelMap.comment}', datafield: 'comment', width: 150, pinned: false},
                     { text: '${uiLabelMap.statusId}', datafield: 'statusId', columntype:'dropdownlist',width: 100,pinned : true,
                    	 cellsrenderer: function (row, columnfield, value, defaulthtml, columnproperties) {
 							for(var i = 0; i < statusData.length; i++){
 								if(value == statusData[i].statusId){
 									return '<span title=' + value + '>' + statusData[i].description + '</span>';
 								}
 							}
 							return '<span> ' + value + '</span>';
 						}
                     },
                     { text: '${uiLabelMap.approverPartyId}', datafield: 'approverPartyId', width: 150, pinned : false,
                    	 cellsrenderer: function(row, column, value){
   						  var partyName = value;
                     		  $.ajax({
                     				url: 'getPartyName',
                     				type: 'POST',
                     				data: {partyId: value},
                     				dataType: 'json',
                     				async: false,
                     				success : function(data) {
                     					if(!data._ERROR_MESSAGE_){
                     						partyName = data.partyName;
                     					}
                     		        }
                     			});
                     		  return '<span title' + value + '>' + partyName + '</span>';
   						}
                     },
                     { text: '${uiLabelMap.approverRoleTypeId}', datafield: 'approverRoleTypeId', width: 150, pinned : false,
                    	 cellsrenderer: function (row, columnfield, value, defaulthtml, columnproperties) {
  							for(var i = 0; i < roleTypeData.length; i++){
  								if(value == roleTypeData[i].roleTypeId){
  									return '<span title=' + value + '>' + roleTypeData[i].description + '</span>';
  								}
  							}
  							return '<span> ' + value + '</span>';
  						}
                     },
                     { text: '${uiLabelMap.basicSalary}', datafield: 'basicSalary', width: 100},
                     { text: '${uiLabelMap.percentBasicSalary}', datafield: 'percentBasicSalary', width: 100},
                     { text: '${uiLabelMap.trafficAllowance}', datafield: 'trafficAllowance', width: 100},
                     { text: '${uiLabelMap.phoneAllowance}', datafield: 'phoneAllowance', width: 100},
                     { text: '${uiLabelMap.otherAllowance}', datafield: 'otherAllowance', width: 100},
                     { text: '${uiLabelMap.inductedStartDate}', datafield: 'inductedStartDate', width: 150,cellsformat: 'dd/MM/yyyy',columntype:'datetimeinput',
                    	 createeditor: function (row, cellvalue, editor, celltext, cellwidth, cellheight) {
 							editor.jqxDateTimeInput({width: '150', formatString:'dd/MM/yyyy'});
 					    }
                     },
                     { text: '${uiLabelMap.inductedCompletionDate}', datafield: 'inductedCompletionDate', width: 150,cellsformat: 'dd/MM/yyyy',columntype:'datetimeinput',
                    	 createeditor: function (row, cellvalue, editor, celltext, cellwidth, cellheight) {
  							editor.jqxDateTimeInput({width: '150', formatString:'dd/MM/yyyy'});
  					    }
                     }
					 "/>

<@jqGrid id="jqxgrid" filtersimplemode="true" addrefresh="true" editable="false" addType="popup" alternativeAddPopup="alterpopupNewApplicant" addrow="true" showtoolbar="true" clearfilteringbutton="true"
		 url="jqxGeneralServicer?sname=JQGetListInductedApplicant&workEffortId=_NA_" dataField=dataField columnlist=columnlist mouseRightMenu="true" contextMenuId="contextMenu" jqGridMinimumLibEnable="false"
		 updateUrl="jqxGeneralServicer?sname=updateApllStatus&jqaction=U" editColumns="roleTypeId;partyId;workEffortId;assignmentStatusId;basicSalary(java.lang.Long);inductedStartDate(java.sql.Timestamp);inductedCompletionDate(java.sql.Timestamp);percentBasicSalary(java.lang.Long);comment"
/>
<div id='contextMenu' style="display: none;">
	<ul>
	    <li id="approveOfferPro"><i class="fa fa-paper-plane"></i>&nbsp;${uiLabelMap.approveOfferPro}</li>
	    <li id="proposeOfferPro"><i class="fa fa-star"></i>&nbsp;${uiLabelMap.proposeOfferPro}</li>
	    <li id="createProbAgreement"><i class="fa fa-file"></i>&nbsp;${uiLabelMap.createProbAgreement}</li>
    </ul>
</div>
<#--=====================================================================Create Window popup===============================================================-->
<div class="row-fluid">
	<div class="span12">
		<div id="alterpopupNewApplicant" style="display: none;">
			<div id="windowHeaderNewApplicant">
	            <span>
	               ${uiLabelMap.NewApplicant}
	            </span>
	        </div>
	        <div style="overflow: hidden; padding: 5px; margin-left: 10px" id="windowContentNewApplicant">
			    <div id='jqxTabs' style="position: relative;">
		            <ul>
		                <li>${uiLabelMap.generalInfo}</li>
		                <li>${uiLabelMap.contactInfo}</li>
		                <li>${uiLabelMap.familyMemberInfo}</li>
		                <li>${uiLabelMap.trainingInfo}</li>
		                <li>${uiLabelMap.workProcessInfo}</li>
		                <li>${uiLabelMap.skillInfo}</li>
		                <li>${uiLabelMap.specialSkillInfo}</li>
		                <li>${uiLabelMap.healthInfo}</li>
		                <li>${uiLabelMap.acquaintanceInfo}</li>
		                <li>${uiLabelMap.jobInfo}</li>
		            </ul>
		            <div id="newGeneralInfo">
		                <#include "jqxEditGeneralInfo.ftl" />
			            <div class="row-fluid jqx-tabs-button-olbius">
		                	<div class="span12" style="text-align: right">
		                		<button type="button" class="btn btn-primary next btn-small" >${uiLabelMap.CommonNext} <i class="icon-arrow-right"></i></button>
		                	</div>
	                	</div>
		            </div>
		            <div id="newContactInfo">
		                <#include "jqxEditContact.ftl" />
			            <div class="row-fluid jqx-tabs-button-olbius">
		                	<div class="span12" style="text-align: right">
			                	<button type="button" class="btn btn-primary btn-success back btn-small" ><i class="icon-arrow-left"></i>${uiLabelMap.CommonPrevious}</button>
		                		<button type="button" class="btn btn-primary next btn-small" >${uiLabelMap.CommonNext}&nbsp;<i class="icon-arrow-right"></i></button>
		                	</div>
	                	</div>
		            </div>
		            <div id="newFamilyMember" style="margin:10px">
		            	<div id="jqxgridFamily"></div>
		            	<div class="row-fluid jqx-tabs-button-olbius">
		                	<div class="span12" style="text-align: right">
			                	<button type="button" class="btn btn-primary btn-success back btn-small" ><i class="icon-arrow-left"></i>${uiLabelMap.CommonPrevious}</button>
		                		<button type="button" class="btn btn-primary next btn-small" >${uiLabelMap.CommonNext}&nbsp;<i class="icon-arrow-right"></i></button>
		                	</div>
	                	</div>
		            </div>
		            <div style="margin:10px">
		            	<div id="jqxgridEdu"></div>
		            	<div class="row-fluid jqx-tabs-button-olbius">
		                	<div class="span12" style="text-align: right">
			                	<button type="button" class="btn btn-primary btn-success back back btn-small"><i class="icon-arrow-left"></i>${uiLabelMap.CommonPrevious}</button>
		                		<button type="button" class="btn btn-primary next btn-small">${uiLabelMap.CommonNext}&nbsp;<i class="icon-arrow-right"></i></button>
		                	</div>
	                	</div>
		            </div>
		            <div style="margin:10px">
		            	<div id="jqxgridWP"></div>
		            	<div class="row-fluid jqx-tabs-button-olbius">
		                	<div class="span12" style="text-align: right">
			                	<button type="button" class="btn btn-primary btn-success back back btn-small"><i class="icon-arrow-left"></i>${uiLabelMap.CommonPrevious}</button>
		                		<button type="button" class="btn btn-primary next btn-small">${uiLabelMap.CommonNext}&nbsp;<i class="icon-arrow-right"></i></button>
		                	</div>
	                	</div>
		            </div>
		            <div style="margin:10px">
		            	<div id="jqxgridSkill"></div>
		            	<div class="row-fluid jqx-tabs-button-olbius">
		                	<div class="span12" style="text-align: right">
			                	<button type="button" class="btn btn-primary btn-success back back btn-small"" ><i class="icon-arrow-left"></i>${uiLabelMap.CommonPrevious}</button>
		                		<button type="button" class="btn btn-primary next btn-small">${uiLabelMap.CommonNext}&nbsp;<i class="icon-arrow-right"></i></button>
		                	</div>
	                	</div>
		            </div>
		            <div style="margin:10px">
		            	<div id="jqxSpecialSkillEditor"></div>
		            	<div class="row-fluid jqx-tabs-button-olbius">
		                	<div class="span12" style="text-align: right">
			                	<button type="button" class="btn btn-primary btn-success back back btn-small"><i class="icon-arrow-left"></i>${uiLabelMap.CommonPrevious}</button>
		                		<button type="button" class="btn btn-primary next btn-small">${uiLabelMap.CommonNext}&nbsp;<i class="icon-arrow-right"></i></button>
		                	</div>
	                	</div>
		            </div>
		            <div style="margin:10px">
		            	<div class="basic-form form-horizontal" style="margin-top: 10px">
		        			<form name="createNewHealth" id="createNewHealth">	
					            <div class="row-fluid" >
									<div class="span12">
										<div class="control-group no-left-margin">
											<label class="control-label asterisk" style="width: 750px!important">${uiLabelMap.BadHealth}:</label>
											<div class="controls" style="margin-left: 775px!important">
												<div id="badHealth" style="display: inline-block"></div>
											</div>
										</div>
										<div class="control-group no-left-margin">
											<label class="control-label">${uiLabelMap.BadHealthDetail}:</label>  
											<div class="controls">
												<div id="badHealthDetail"></div>
											</div>
										</div>
										<div class="control-group no-left-margin">
											<label class="control-label" style="width: 750px!important">${uiLabelMap.BadInfo}:</label>
											<div class="controls" style="margin-left: 775px!important">
												<div id="badInfo" style="display: inline-block"></div>
											</div>
										</div>
										<div class="control-group no-left-margin">
											<label class="control-label">${uiLabelMap.BadInfoDetail}:</label>  
											<div class="controls">
												<div id="badInfoDetail"></div>
											</div>
										</div>
									</div>
								</div>
							</form>
						</div>
						<div class="row-fluid jqx-tabs-button-olbius">
		                	<div class="span12" style="text-align: right">
			                	<button type="button" class="btn btn-primary btn-success back back btn-small" ><i class="icon-arrow-left"></i>${uiLabelMap.CommonPrevious}</button>
		                		<button type="button" class="btn btn-primary next btn-small" >${uiLabelMap.CommonNext}&nbsp;<i class="icon-arrow-right"></i></button>
		                	</div>
	                	</div>
		            </div>
					<div id="newAcquaintance" style="margin:10px">
		            	<div id="jqxgridAcq"></div>
		            	<div class="row-fluid jqx-tabs-button-olbius">
		                	<div class="span12" style="text-align: right">
		                	<button type="button" class="btn btn-primary btn-success back back btn-small" ><i class="icon-arrow-left"></i>${uiLabelMap.CommonPrevious}</button>
	                		<button type="button" class="btn btn-primary next btn-small" >${uiLabelMap.CommonNext}&nbsp;<i class="icon-arrow-right"></i></button>
		                	</div>
	                	</div>
		            </div>
		            <div id="newJobInfo" style="margin:10px">
			            <div class="basic-form form-horizontal" style="margin-top: 10px">
		        			<form name="createJobInfo" id="createJobInfo">	
					            <div class="row-fluid" >
									<div class="span12">
										<div class="span6">
											<div class="control-group no-left-margin">
												<label class="control-label asterisk">${uiLabelMap.Department}:</label>
												<div class="controls">
													<div id="jiPartyIdWork">
														<div id="jqxJiGridPartyIdWork"></div>
													</div>
												</div>
											</div>
											<div class="control-group no-left-margin">
												<label class="control-label">${uiLabelMap.Position}:</label>  
												<div class="controls">
													<div id="jiEmplPositionTypeId"></div>
												</div>
											</div>
											<div class="control-group no-left-margin">
												<label class="control-label">${uiLabelMap.inductedStartDate}:</label>
												<div class="controls">
													<div id="jiInductedStartDate"></div>
												</div>
											</div>
											<div class="control-group no-left-margin">
												<label class="control-label">${uiLabelMap.inductedCompletionDate}:</label>  
												<div class="controls">
													<div id="jiInductedCompletionDate"></div>
												</div>
											</div>
										</div>
										<div class="span6">
											<div class="control-group no-left-margin">
												<label class="control-label asterisk">${uiLabelMap.basicSalary}:</label>
												<div class="controls">
													<div id="jiBasicSalary"></div>
												</div>
											</div>
											<div class="control-group no-left-margin">
												<label class="control-label">${uiLabelMap.percentBasicSalary}:</label>  
												<div class="controls">
													<div id="jiPercentBasicSalary"></div>
												</div>
											</div>
											<div class="control-group no-left-margin">
												<label class="control-label asterisk">${uiLabelMap.trafficAllowance}:</label>
												<div class="controls">
													<div id="jiTrafficAllowance"></div>
												</div>
											</div>
											<div class="control-group no-left-margin">
												<label class="control-label asterisk">${uiLabelMap.phoneAllowance}:</label>
												<div class="controls">
													<div id="jiPhoneAllowance"></div>
												</div>
											</div>
											<div class="control-group no-left-margin">
												<label class="control-label asterisk">${uiLabelMap.otherAllowance}:</label>
												<div class="controls">
													<div id="jiOtherAllowance"></div>
												</div>
											</div>
										</div>
									</div>
								</div>
							</form>
						</div>
		            	<div class="row-fluid jqx-tabs-button-olbius">
		                	<div class="span12" style="text-align: right">
			                	<button type="button" class="btn btn-primary btn-success back back btn-small"><i class="icon-arrow-left"></i>${uiLabelMap.CommonPrevious}</button>
		                		<button type="button" class="btn btn-primary btn-small" id="submit"><i class="icon-ok"></i>&nbsp;${uiLabelMap.CommonCreate}</button>
		                	</div>
	                	</div>
		            </div>
		        </div>
	        </div>
		</div>
	</div>
</div>
<#include "jqxEditProbAgreement.ftl" />
<#include "jqxEditFamily.ftl" />
<#include "jqxEditEducation.ftl" />
<#include "jqxEditWorkingProcess.ftl" />
<#include "jqxEditSkill.ftl" />
<#include "jqxEditAqcInfo.ftl" />
<#include "jqxApprove.ftl" />
<#--=====================================================================/Create Window popup===============================================================-->
<script>
	$(document).ready(function () {
		<#include "jsApprove.ftl" />
		<#include "jsEditProbAgreement.ftl" />
		<#include "jsDirectedContextMenu.ftl" />
		<#include "jsInitApplWindow.ftl" />
		<#include "jsEditContact.ftl" />
		<#include "jsEditFamily.ftl" />
		<#include "jsEditEducation.ftl" />
		<#include "jsEditWorkingProcess.ftl" />
		<#include "jsEditSkill.ftl" />
		<#include "jsEditGeneralInfo.ftl" />
		<#include "jsEditAqcInfo.ftl" />
		<#include "jsDirectedApplSubmit.ftl" />
	});
</script>
