<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxvalidator.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxtabs.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxmaskedinput.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxeditor.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxradiobutton.js"></script>
<script type="text/javascript" src="/delys/images/js/util/DateUtil.js" ></script>
<script type="text/javascript" src="/aceadmin/assets/js/bootbox.min.js"></script>
<#--=================================================Prepare Data==================================================================-->
<script>
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
	<#assign listStatus = delegator.findList("StatusItem", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("statusTypeId", Static["org.ofbiz.entity.condition.EntityOperator"].IN , ["APPLICANT_STATUS", "AVAI_APPL_STATUS"]), null, null, null, false)>
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
	
	//Prepare for teamWorkData
	<#assign listTeamWorks = delegator.findList("TeamWork", null, null, null, null, false)>
	var teamWorkData = new Array();
	<#list listTeamWorks as item>
		var row = {};
		<#assign description = StringUtil.wrapString(item.description)>
		row['teamWorkId'] = '${item.teamWorkId?if_exists}';
		row['description'] = '${description}';
		teamWorkData[${item_index}] = row;
	</#list>
	
	//Prepare for AloneWork
	<#assign listAloneWorks = delegator.findList("AloneWork", null, null, null, null, false)>
	var aloneWorkData = new Array();
	<#list listAloneWorks as item>
		var row = {};
		<#assign description = StringUtil.wrapString(item.description)>
		row['aloneWorkId'] = '${item.aloneWorkId?if_exists}';
		row['description'] = '${description}';
		aloneWorkData[${item_index}] = row;
	</#list>
	
	//Prepare ResultItem	
	<#assign resultlList = delegator.findByAnd("ResultItem", Static["org.ofbiz.base.util.UtilMisc"].toMap("resultTypeId", "RECR_TEST_RESULT"), null, false)>
	var resultData = new Array();
	<#list resultlList as item>
		var row = {};
		<#assign description = StringUtil.wrapString(item.description?if_exists) />
		row['resultId'] = '${item.resultId?if_exists}';
		row['description'] = "${description}";
		resultData[${item_index}] = row;
	</#list>
</script>
<#--=================================================/Prepare Data==================================================================-->

<#--=================================================Initial Grid==================================================================-->
<#assign dataField="[{ name: 'partyId', type: 'string'},
					 { name: 'firstName', type: 'string'},
					 { name: 'gender', type: 'string'},
					 { name: 'birthDate', type: 'date'},
					 { name: 'middleName', type: 'string'},
					 { name: 'lastName', type: 'string'},
					 { name: 'comments', type: 'string'},
					 { name: 'assignmentStatusId', type: 'string' },
					 { name: 'workEffortName', type: 'string' },
					 { name: 'availabilityStatusId', type: 'string' },
					 { name: 'workEffortId', type: 'string' }
					 ]"/>
<#assign currentWorkEffort = delegator.findOne("WorkEffort", {"workEffortId" : parameters.workEffortId}, false)>
<#if currentWorkEffort.workEffortTypeId == "RECRUITMENT_PROCESS">
	<#assign columnlist="{ text: '${uiLabelMap.CommonId}', datafield: 'partyId', width: 100, editable: false,
		cellsrenderer: function (row, columnfield, value, defaulthtml, columnproperties) {
			return '<span><a href=EmployeeProfile?partyId=' + value + '>' + value + '</a></span>'
		}
	},
	 { text: '${uiLabelMap.fullName}', width: 200, 
		cellsrenderer: function (row, columnfield, value, defaulthtml, columnproperties) {
		 	var rowData = $('#jqxgrid').jqxGrid('getrowdata', row);
		 	return '<span>' + rowData['lastName'] + ' ' + (rowData['middleName'] ? rowData['middleName'] : '')+ ' ' + rowData['firstName'] + '</span>'
	 	}
	 },
	 { text: '${uiLabelMap.statusId}', datafield: 'assignmentStatusId', width: 200,
		 cellsrenderer: function (row, columnfield, value, defaulthtml, columnproperties) {
			for(var i = 0; i < statusData.length; i++){
				if(value == statusData[i].statusId){
					return '<span title=' + value + '>' + statusData[i].description + '</span>';
				}
			}
			return '<span> ' + value + '</span>';
		}
	 },
	 { text: '${uiLabelMap.overviewInfo}', datafield: 'comments', width: 200},
	 { text: '${uiLabelMap.availabilityStatusId}', datafield: 'availabilityStatusId', width: 200,
		 cellsrenderer: function (row, columnfield, value, defaulthtml, columnproperties) {
			for(var i = 0; i < statusData.length; i++){
				if(value == statusData[i].statusId){
					return '<span title=' + value + '>' + statusData[i].description + '</span>';
				}
			}
			return '<span> ' + value + '</span>';
		}
	 },
	 { text: '${uiLabelMap.roundName}', datafield: 'workEffortName'},
	 "/>

	 <@jqGrid id="jqxgrid" selectionmode="checkbox" filtersimplemode="true" addrefresh="true" addType="popup" alternativeAddPopup="alterpopupNewApplicant" addrow="true" showtoolbar="true" clearfilteringbutton="true"
		 url="jqxGeneralServicer?sname=JQGetListApplicant&workEffortId=${parameters.workEffortId}" dataField=dataField columnlist=columnlist
		 mouseRightMenu="true" contextMenuId="contextMenu" otherParams="workEffortName,availabilityStatusId:S-getRecruitmentRound(workEffortId,partyId)<workEffortName,availabilityStatusId>"
		/>
 <#else>
	<#assign columnlist="{ text: '${uiLabelMap.CommonId}', datafield: 'partyId', width: 100, editable: false,
		cellsrenderer: function (row, columnfield, value, defaulthtml, columnproperties) {
			return '<span><a href=EmployeeProfile?partyId=' + value + '>' + value + '</a></span>'
		}
	},
	 { text: '${uiLabelMap.fullName}', width: 200, 
		cellsrenderer: function (row, columnfield, value, defaulthtml, columnproperties) {
		 	var rowData = $('#jqxgrid').jqxGrid('getrowdata', row);
		 	return '<span>' + rowData['lastName'] + ' ' + (rowData['middleName'] ? rowData['middleName'] : '') + ' ' + rowData['firstName'] + '</span>'
	 	}
	 },
	 { text: '${uiLabelMap.statusId}', datafield: 'assignmentStatusId', width: 200,
		 cellsrenderer: function (row, columnfield, value, defaulthtml, columnproperties) {
			for(var i = 0; i < statusData.length; i++){
				if(value == statusData[i].statusId){
					return '<span title=' + value + '>' + statusData[i].description + '</span>';
				}
			}
			return '<span> ' + value + '</span>';
		}
	 },
	 { text: '${uiLabelMap.availabilityStatusId}', datafield: 'availabilityStatusId', width: 200,
		 cellsrenderer: function (row, columnfield, value, defaulthtml, columnproperties) {
			for(var i = 0; i < statusData.length; i++){
				if(value == statusData[i].statusId){
					return '<span title=' + value + '>' + statusData[i].description + '</span>';
				}
			}
			return '<span> ' + value + '</span>';
		}
	 },
	 { text: '${uiLabelMap.roundName}', datafield: 'workEffortName'},
	 "/>

	 <@jqGrid id="jqxgrid" selectionmode="checkbox" filtersimplemode="true" addrefresh="true" addType="popup" alternativeAddPopup="alterpopupNewApplicant" addrow="false" showtoolbar="true" clearfilteringbutton="true"
		 url="jqxGeneralServicer?sname=JQGetListApplicant&workEffortId=${parameters.workEffortId}" dataField=dataField columnlist=columnlist
		 mouseRightMenu="true" contextMenuId="contextMenu" otherParams="workEffortName,availabilityStatusId:S-getRecruitmentRound(workEffortId,partyId)<workEffortName,availabilityStatusId>"
		/>
</#if>

<#--=================================================/Initial Grid==================================================================-->

<div id='contextMenu' style="display: none;">
	<ul>
	    <li id="sendEmail"><i class="fa fa-paper-plane"></i>&nbsp;${uiLabelMap.sendEmail}</li>
	    <li id="agreePreliminary"><i class="fa fa-check-circle"></i>&nbsp;${uiLabelMap.agreePreliminary}</li>
	    <li id="scoreInterview"><i class="fa fa-star"></i>&nbsp;${uiLabelMap.scoreInterview}</li>
	    <li id="scoreExam"><i class="fa fa-star"></i>&nbsp;${uiLabelMap.scoreExam}</li>
	    <li id="proposeProbation"><i class="fa fa-level-up"></i>&nbsp;${uiLabelMap.proposeProbation}</li>
	    <li id="contacted"><i class="fa fa-check"></i>&nbsp;${uiLabelMap.contacted}</li>
	    <li id="confirmed"><i class="fa fa-check-circle-o"></i>&nbsp;${uiLabelMap.confirmed}</li>
	</ul>
</div>	

<#--====================================================Create new popup window==========================================================-->

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
		                <li>${uiLabelMap.overviewInfo}</li>
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
		            <div id="newOverview" style="margin:10px">
		            	<div id="jqxOverviewEditor"></div>
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

<#include "jqxEditFamily.ftl">
<#include "jqxEditEducation.ftl">
<#include "jqxEditWorkingProcess.ftl">
<#include "jqxEditSkill.ftl">
<#include "jqxEditAqcInfo.ftl">
<#include "jqxSendEmail.ftl">
<#include "jqxScoreInterview.ftl">
<#include "jqxScoreExam.ftl">
<#include "jqxProposeProb.ftl">
<#--====================================================/Create new popup window==========================================================-->
	
<script>
$(document).ready(function () {
	<#include "jsContextMenu.ftl" />
	<#include "jsInitApplWindow.ftl" />
	<#include "jsEditContact.ftl" />
	<#include "jsProposeProb.ftl" />
	<#include "jsEditFamily.ftl" />
	<#include "jsEditEducation.ftl" />
	<#include "jsEditWorkingProcess.ftl" />
	<#include "jsEditSkill.ftl" />
	<#include "jsEditGeneralInfo.ftl" />
	<#include "jsEditAqcInfo.ftl" />
	<#include "jsSendEmail.ftl" />
	<#include "jsScoreInterview.ftl" />
	<#include "jsScoreExam.ftl" />
	<#include "jsApplSubmit.ftl" />
});
</script>
