<#ftl encoding='UTF-8'>
<@jqGridMinimumLib/>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxvalidator.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxwindow.js"></script>
<script src="/hrresources/js/jszip.js" type="text/javascript"></script>
<script src="/hrresources/js/xlsx.js" type="text/javascript"></script>
<script type="text/javascript" src="/crmresources/js/miscUtil.js"></script>
<@jqOlbCoreLib hasDropDownButton=true hasValidator=true hasGrid=true/>

<#include "component://administration/webapp/administration/entity/popupMessageError.ftl"/>

<script>
var multiLang = {
    HRCheckId: "${StringUtil.wrapString(uiLabelMap.HRCheckId)}",
    HRCheckName: "${StringUtil.wrapString(uiLabelMap.HRCheckName)}",
    HRCheckFullName: "${StringUtil.wrapString(uiLabelMap.HRCheckFullName)}",
    HRCheckAddress: "${StringUtil.wrapString(uiLabelMap.HRCheckAddress)}",
    HRCheckIdCard: "${StringUtil.wrapString(uiLabelMap.HRCheckIdCard)}",
    HRCheckAddress: "${StringUtil.wrapString(uiLabelMap.HRCheckAddress)}",
};

var globalVar = {};
<#assign genders = delegator.findList("Gender", null, null, null, null, false)/>
<#assign ethnicOrigins = delegator.findList("EthnicOrigin", null, null, null, null, false)/>
<#assign religions = delegator.findList("Religion", null, null, null, null, false)/>
<#assign condIssuePlace1 =  Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("geoId", Static["org.ofbiz.entity.condition.EntityJoinOperator"].LIKE, "VNM%")/>
<#assign condIssuePlace2 =  Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("geoTypeId", "PROVINCE")/>
<#assign issuePlaces = delegator.findList("Geo", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition(condIssuePlace1, Static["org.ofbiz.entity.condition.EntityJoinOperator"].AND, condIssuePlace2), null, null, null, false)/>
<#assign maritalStatuss = delegator.findList("StatusItem", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("statusTypeId", "MARITAL_STATUS"), null, null, null, false)/>
<#assign nationalitys = delegator.findList("Nationality", null, null, null, null, false)/>
<#assign emplPositionTypes = delegator.findList("EmplPositionType",  Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("emplPositionTypeId", Static["org.ofbiz.entity.condition.EntityJoinOperator"].IN, ["SALES_EXECUTIVE_MT", "SALES_EXECUTIVE_GT"]), null, null, null, false)/>
<#assign periodTypes = delegator.findList("PeriodType", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("periodTypeId", Static["org.ofbiz.entity.condition.EntityJoinOperator"].IN, ["MONTHLY", "YEARLY", "DAILY"]), null, null, null, false)/>
<#assign countries = delegator.findList("Geo", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("geoTypeId", "COUNTRY"), null, null, null, false)/>
<#assign provinces = delegator.findList("Geo", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("geoTypeId", "PROVINCE"), null, null, null, false)/>
<#assign districts = delegator.findList("Geo", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("geoTypeId", "DISTRICT"), null, null, null, false)/>
<#assign condWard1 =  Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("geoName", Static["org.ofbiz.entity.condition.EntityJoinOperator"].NOT_EQUAL, null)/>
<#assign condWard2 =  Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("geoTypeId", "WARD")/>
<#assign wards = delegator.findList("Geo", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition(condWard1, Static["org.ofbiz.entity.condition.EntityJoinOperator"].AND, condWard2), null, null, null, false)/>
<#assign partys = delegator.findList("PartyRoleNameDetail", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("roleTypeId", Static["org.ofbiz.entity.condition.EntityJoinOperator"].IN, ["SALESSUP_DEPT_MT", "SALESSUP_DEPT_GT"]), null, null, null, false)/>
globalVar.genders = [
    <#if genders?has_content>
            <#list genders as gender>{
            genderId: '${gender.genderId}',
    		description: '${StringUtil.wrapString(gender.description)}'
    		},</#list>
    </#if>
]
globalVar.issuePlaces = [
    <#if issuePlaces?has_content>
            <#list issuePlaces as issuePlace>{
            geoId: '${issuePlace.geoId}',
    		geoName: '${StringUtil.wrapString(issuePlace.geoName)}'
    		},</#list>
    </#if>
]
globalVar.ethnicOrigins = [
    <#if ethnicOrigins?has_content>
            <#list ethnicOrigins as ethnicOrigin>{
            ethnicOriginId: '${ethnicOrigin.ethnicOriginId}',
    		description: '${StringUtil.wrapString(ethnicOrigin.description)}'
    		},</#list>
    </#if>
]
globalVar.religions = [
    <#if religions?has_content>
            <#list religions as religion>{
            religionId: '${religion.religionId}',
    		description: '${StringUtil.wrapString(religion.description)}'
    		},</#list>
    </#if>
]
globalVar.maritalStatuss = [
    <#if maritalStatuss?has_content>
            <#list maritalStatuss as maritalStatus>{
            statusId: '${maritalStatus.statusId}',
    		description: '${StringUtil.wrapString(maritalStatus.description)}'
    		},</#list>
    </#if>
]
globalVar.nationalitys = [
    <#if nationalitys?has_content>
            <#list nationalitys as nationality>{
            nationalityId: '${nationality.nationalityId}',
    		description: '${StringUtil.wrapString(nationality.description)}'
    		},</#list>
    </#if>
]
globalVar.emplPositionTypes = [
    <#if emplPositionTypes?has_content>
            <#list emplPositionTypes as emplPositionType>{
            emplPositionTypeId: '${emplPositionType.emplPositionTypeId}',
    		<#if emplPositionType.description?has_content>
    		    description: '${StringUtil.wrapString(emplPositionType.description)}'
    		<#else>
    		    description: ''
    		</#if>
    		},</#list>
    </#if>
]
globalVar.periodTypes = [
    <#if periodTypes?has_content>
            <#list periodTypes as periodType>{
            periodTypeId: '${periodType.periodTypeId}',
    		description: '${StringUtil.wrapString(periodType.description)}'
    		},</#list>
    </#if>
]
globalVar.countries = [
    <#if countries?has_content>
            <#list countries as country>{
            geoId: '${country.geoId}',
    		geoName: "${StringUtil.wrapString(country.geoName)}"
    		},</#list>
    </#if>
]
globalVar.provinces = [
    <#if provinces?has_content>
            <#list provinces as province>{
            geoId: '${province.geoId}',
    		geoName: "${StringUtil.wrapString(province.geoName)}"
    		},</#list>
    </#if>
]
globalVar.districts = [
    <#if districts?has_content>
            <#list districts as district>{
            geoId: '${district.geoId}',
    		geoName: "${StringUtil.wrapString(district.geoName)}"
    		},</#list>
    </#if>
]
globalVar.wards = [
    <#if wards?has_content>
            <#list wards as ward>{
            geoId: '${ward.geoId}',
    		geoName: "${StringUtil.wrapString(ward.geoName)}"
    		},</#list>
    </#if>
]
globalVar.partys = [
    <#if partys?has_content>
            <#list partys as party>{
            partyId: '${party.partyId}',
    		description: "${StringUtil.wrapString(party.groupName)}"
    		},</#list>
    </#if>
]
    function isGender(name){
        if (OlbCore.isEmpty(name)){
            return "";
        }else{
            for(var item in globalVar.genders){
                if(name == globalVar.genders[item].description) return globalVar.genders[item].genderId;
            }
            return '_NA_';
        }
    }
    function isIssuePlace(name){
        if (OlbCore.isEmpty(name)){
            return "";
        }else{
            for(var item in globalVar.issuePlaces){
                if(name == globalVar.issuePlaces[item].geoName) return globalVar.issuePlaces[item].geoId;
            }
            return '_NA_';
        }
    }
    function isEthnicOrigin(name){
        if (OlbCore.isEmpty(name)){
            return "";
        }else{
            for(var item in globalVar.ethnicOrigins){
                if(name == globalVar.ethnicOrigins[item].description) return globalVar.ethnicOrigins[item].ethnicOriginId;
            }
            return '_NA_';
        }
    }
    function isReligion(name){
        if (OlbCore.isEmpty(name)){
            return "";
        }else{
            for(var item in globalVar.religions){
                if(name == globalVar.religions[item].description) return globalVar.religions[item].religionId;
            }
            return '_NA';
        }
    }
    function isMaritalStatus(name){
        if (OlbCore.isEmpty(name)){
            return "";
        }else{
            for(var item in globalVar.maritalStatuss){
                if(name == globalVar.maritalStatuss[item].description) return globalVar.maritalStatuss[item].statusId;
            }
            return '_NA_';
        }
    }
    function isNationality(name){
        if (OlbCore.isEmpty(name)){
            return "";
        }else{
            for(var item in globalVar.nationalitys){
                if(name == globalVar.nationalitys[item].description) return globalVar.nationalitys[item].nationalityId;
            }
            return '_NA_';
        }
    }
    function isPartyIdFrom(name){
        if (OlbCore.isEmpty(name)){
            return "";
        }else{
            for(var item in globalVar.partys){
                if(name == globalVar.partys[item].description) return globalVar.partys[item].partyId;
            }
            return '_NA_';
        }
    }
    function isEmplPositionType(name){
        if (OlbCore.isEmpty(name)){
            return "";
        }else{
            for(var item in globalVar.emplPositionTypes){
                if(name == globalVar.emplPositionTypes[item].description) return globalVar.emplPositionTypes[item].emplPositionTypeId;
            }
            return '_NA_';
        }
    }
    function isPeriodType(name){
        if (OlbCore.isEmpty(name)){
            return "";
        }else{
            for(var item in globalVar.periodTypes){
                if(name == globalVar.periodTypes[item].description) return globalVar.periodTypes[item].periodTypeId;
            }
            return '_NA_';
        }
    }
    function isNullString(str){
        return str != null? str : '';
    }

    function isExistGeoPoint(county, province, district, ward){
        return county && province && district && ward ? true : false;
    }

    var checkRex = function(value,uilabel){
        if(OlbCore.isNotEmpty(uilabel) && OlbCore.isNotEmpty(value)){
            var regexCheck = new RegExp(uilabel);
            if(regexCheck.test(value)){
                return true;
            }
        }
        return false;
    };

    function hasWhiteSpace(s) {
    	return /\s/g.test(s);
    }

    function checkValidPartyCode(name){
        if (OlbCore.isEmpty(name)){
            return "";
        }else{
            if (!name.containSpecialChars() && !hasWhiteSpace(name) && checkRex(name, multiLang.HRCheckId)) return name;
            return '_NA_';
        }
    }

    function checkValidLastOrFirstName(name){
        if (OlbCore.isEmpty(name)){
            return "";
        }else{
            if (checkRex(name, multiLang.HRCheckName)) return name;
            return '_NA_';
        }
    }

    function checkValidMiddleName(name){
        if (OlbCore.isEmpty(name)){
            return "";
        }else{
            if (checkRex(name, multiLang.HRCheckFullName)) return name;
            return '_NA_';
        }
    }

    function checkValidNativeLand(name){
        if (OlbCore.isEmpty(name)){
            return "";
        }else{
            if (checkRex(name, multiLang.HRCheckAddress)) return name;
            return '_NA_';
        }
    }

    function checkValidIdNumber(name){
        if (OlbCore.isEmpty(name)){
            return "";
        }else{
            if (checkRex(name, multiLang.HRCheckIdCard)) return name;
            return '_NA_';
        }
    }

    function checkValidUserLoginId(name){
        if (OlbCore.isEmpty(name)){
            return "";
        }else{
            if (!hasWhiteSpace(name)) return name;
            return '_NA_';
        }
    }

    function isCountry(name){
        if(OlbCore.isEmpty(name)){
            return "";
        }else{
            for(var item in globalVar.countries){
                if(name == globalVar.countries[item].geoName) return globalVar.countries[item].geoId;
            }
            return "_NA_";
        }
    }
    function isProvince(nameCountry, nameProvince){
        if(!OlbCore.isEmpty(nameCountry) && OlbCore.isEmpty(nameProvince)){
            return "";
        }else{
            var country = isCountry(nameCountry);
            var provinces = [];
            for(var item in globalVar.provinces){
                var province = globalVar.provinces[item].geoId;
                if(province.indexOf(country) !== -1){
                    provinces.push(globalVar.provinces[item]);
                }
            }
            if(provinces.length !== 0){
                for(var item in provinces){
                    if(nameProvince == provinces[item].geoName) return provinces[item].geoId;
                }
            }
            return "_NA_";
        }
    }

    function isDistrict(nameCountry, nameProvince, nameDistrict){
        if(!OlbCore.isEmpty(nameCountry) && !OlbCore.isEmpty(nameProvince) && OlbCore.isEmpty(nameDistrict)){
            return "";
        }else{
            if(isCountry(nameCountry) && isProvince(nameCountry,nameProvince)){
                var province = isProvince(nameCountry, nameProvince);
                var districts = [];
                for(var item in globalVar.districts){
                    var district = globalVar.districts[item].geoId;
                    if(district.indexOf(province) !== -1){
                        districts.push(globalVar.districts[item]);
                    }
                }
                if(districts.length !== 0){
                    for(var item in districts){
                        if(nameDistrict == districts[item].geoName) return districts[item].geoId;
                    }
                }
            }
            return "_NA_";
        }
    }

    function isWard(nameCountry, nameProvince, nameDistrict, nameWard){
        if(!OlbCore.isEmpty(nameCountry) && !OlbCore.isEmpty(nameProvince) && !OlbCore.isEmpty(nameDistrict) && OlbCore.isEmpty(nameWard)){
            return "";
        }else{
            if(isCountry(nameCountry) && isProvince(nameCountry,nameProvince) && isDistrict(nameCountry,nameProvince, nameDistrict)){
                var district = isDistrict(nameCountry, nameProvince, nameDistrict);
                var wards = [];
                for(var item in globalVar.wards){
                    var ward = globalVar.wards[item].geoId;
                    if(ward.indexOf(district) !== -1){
                        wards.push(globalVar.wards[item]);
                    }
                }
                if(wards.length !== 0){
                    for(var item in wards){
                        if(nameWard == wards[item].geoName) return wards[item].geoId;
                    }
                }
            }
            return "_NA_";
        }
    }

    function checkValidAddress(name){
        if (OlbCore.isEmpty(name)){
            return "";
        }else{
            if(checkRex(name, multiLang.HRCheckAddress)) return name;
            return '_NA_';
        }
    }

    function checkValidBigDecimal(name){
        if (OlbCore.isEmpty(name)){
            return "";
        }else{
            if (checkRex(name, new RegExp(/^[1-9]\d*([.]\d{1,2})?$/))) return name;
            return '_NA_';
        }
    }

    function decodeEntities(encodedString) {
        var textArea = document.createElement('textarea');
        textArea.innerHTML = encodedString;
        return textArea.value;
    };
</script>

<@jqGridMinimumLib/>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxvalidator.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxwindow.js"></script>
<script src="/hrresources/js/jszip.js" type="text/javascript"></script>
<script src="/hrresources/js/xlsx.js" type="text/javascript"></script>
<@jqOlbCoreLib hasDropDownButton=true hasValidator=true hasGrid=true/>

<!--html-->
<div class="top-menu-import-data">
    <button class="btn btn-primary" onclick="downloadFileSalesman()"><i class="fa fa-download"></i>${uiLabelMap.DownloadFileTemplateSalesForecast}</button>
    <button class="btn btn-primary" onclick="uploadFileSalesman()"><i class="fa fa-upload"></i>${uiLabelMap.UploadFileAttachment}</button>
</div>
<hr>
<div id="divGridDataUpload" class='hide'>
    <div><p>${uiLabelMap.BSDataImport}</p></div>
    <div id="divGrid" class="hide">
        <div id="jqGridDataUpload"></div>
    </div>
    <div id="dataSampleError" class="text-warning"></div>
</div>
<div id="divNotificationStatusImport" class='hide'>
    <div>
        <div><p style="font-weight: 900;">${uiLabelMap.BSImportDataSuccess}</p></div>
        <div id="dataSuccessId"></div>
    </div>
    <br>
    <div>
        <div><p style="font-weight: 900;">${uiLabelMap.BSImportDataError}</p></div>
        <div id="divGridError">
            <div  id="jqGridImportDataError"></div>
        </div>
    </div>
</div>
<hr>
<div class="bottom-menu-import-data">
    <button id="excuteImportData" class="btn btn-primary" onclick="importDataSalesman()"><i class="fa fa-upload"></i>${uiLabelMap.BSImportData}</button>
</div>
<!--popup-->
<div id="alterpopupWindow" class='hide'>
	<div>${uiLabelMap.AddNewPackingUom}</div>
	<div class='form-window-container'>
		<div class='row-fluid'>
			<div class="form-action">
				<div class="span12" class="margin-bottom10" style="margin-left:10px;">
					<div class="span3" style="text-align: right;">
						<label>Select file</label>
					</div>
					<div class="span9" style="width:60%;">
						<input type="file" id="id-input-file" />
					</div>
				</div>
				<div class="span12" class="margin-bottom10"  style="margin-left:10px;">
					<div class="span3" style="text-align: right;" >
						<label>Select sheet</label>
					</div>
					<div class="span9" style="width:60%;">
						<div id="selectSheetFile">
						</div>
					</div>
				</div>
				<div class="span12 margin-top20" style="margin-bottom:10px;">
					<button id="alterCancel" class='btn btn-danger form-action-button pull-right' style="margin-right:20px !important;"><i class='fa-remove'></i> ${uiLabelMap.CommonCancel}</button>
					<button id="alterSave" class='btn btn-primary form-action-button pull-right'><i class='fa-check'></i> ${uiLabelMap.CommonSave}</button>
				</div>
			</div>
		</div>
	</div>
</div>
<!--end popup-->
<!--end html-->

<script type="text/javascript">
    $(document).ready(function(){
        <#if parameters.importdata?exists>
            $("#divNotificationStatusImport").show();
             OlbResultImportData.init();
        <#else>
            $("#divGridDataUpload").show();
        </#if>

        OlbImportDataProduct.init();
        OlbPopupSelectFile.init();
    });

    function downloadFileSalesman(){
        window.location.href = "exportExcelDataSampleSalesman";
    }

    function uploadFileSalesman(){
        $('#alterpopupWindow').jqxWindow('open');
    }

    function convertDate(strDate){
       var regex = new RegExp(/^(0[1-9]|[12][0-9]|3[01])[/](0[1-9]|1[012])[/](19|20)\d\d$/);
       if(!OlbCore.isEmpty(strDate)){
           if (!regex.test(strDate)){
                return "_NA_";
           }else{
                var dateStr = strDate.replace(/[/]|:/g, " ");
                var arrDate = dateStr.split(" ");
                var strDateN = ""+arrDate[2]+"/"+arrDate[1]+"/"+arrDate[0];
                return new Date(strDateN).getTime();
           }
       }
       return null;
    }

    var displayMessageError = function(row){
        var data = $('#jqGridImportDataError').jqxGrid('getrowdata', row);
        var messages = data.message;
        var messageArr = messages.trim().split("\n");
        $("#alterpopupMessage").jqxWindow('open');
        var table = document.getElementById('displayMessageErrorId');
        for(var i in messageArr){
            var row = table.insertRow(i);
            var cell1 = row.insertCell(0);
            cell1.innerHTML = "- "+ messageArr[i];
        }
    }

    function importDataSalesman(){
        var source = $("#jqGridDataUpload").jqxGrid('source');
    	var dataSource = source._source.localdata;
    	if (dataSource.length > 0) {
    	    var dataSubmit = [];
    	    for (var i=0;i<dataSource.length;i++){
            	var permanentRes = {}
            	if(isExistGeoPoint(dataSource[i].datafield_19, dataSource[i].datafield_18, dataSource[i].datafield_17, dataSource[i].datafield_16, dataSource[i].datafield_15 )){
            	    permanentRes.address1 = checkValidAddress(dataSource[i].datafield_15);
            	    permanentRes.wardGeoId = isWard(dataSource[i].datafield_19, dataSource[i].datafield_18, dataSource[i].datafield_17, dataSource[i].datafield_16);
            	    permanentRes.districtGeoId = isDistrict(dataSource[i].datafield_19, dataSource[i].datafield_18, dataSource[i].datafield_17);
            	    permanentRes.stateProvinceGeoId = isProvince(dataSource[i].datafield_19, dataSource[i].datafield_18);
            	    permanentRes.countryGeoId = isCountry(dataSource[i].datafield_19);
            	}
            	var currRes = {}
            	if(isExistGeoPoint(dataSource[i].datafield_24, dataSource[i].datafield_23, dataSource[i].datafield_22, dataSource[i].datafield_21, dataSource[i].datafield_20 )){
                    currRes.address1 = checkValidAddress(dataSource[i].datafield_20);
                    currRes.wardGeoId = isWard(dataSource[i].datafield_24, dataSource[i].datafield_23, dataSource[i].datafield_22, dataSource[i].datafield_21);
                    currRes.districtGeoId = isDistrict(dataSource[i].datafield_24, dataSource[i].datafield_23, dataSource[i].datafield_22);
                    currRes.stateProvinceGeoId = isProvince(dataSource[i].datafield_24, dataSource[i].datafield_23);
                    currRes.countryGeoId = isCountry(dataSource[i].datafield_24);
                }
                var defaultPassword = "123456";
                var salesman = {};
                    salesman["sequence"] = isNullString(dataSource[i].datafield_0);
                    salesman["partyCode"] = checkValidPartyCode(dataSource[i].datafield_1);
                    salesman["lastName"] = checkValidLastOrFirstName(dataSource[i].datafield_2);
                    salesman["middleName"] = checkValidMiddleName(dataSource[i].datafield_3);
                    salesman["firstName"] = checkValidLastOrFirstName(dataSource[i].datafield_4);
                    salesman["gender"] = isGender(dataSource[i].datafield_5);
                    salesman["birthdate"] = convertDate(dataSource[i].datafield_6);
                    salesman["idNumber"] = checkValidIdNumber(dataSource[i].datafield_7);
                    salesman["religion"] = isReligion(dataSource[i].datafield_8);
                    salesman["idIssueDate"] = convertDate(dataSource[i].datafield_9);
                    salesman["idIssuePlace"] = isIssuePlace(dataSource[i].datafield_10);
                    salesman["maritalStatusId"] = isMaritalStatus(dataSource[i].datafield_11);
                    salesman["ethnicOrigin"] = isEthnicOrigin(dataSource[i].datafield_12);
                    salesman["nationality"] = isNationality(dataSource[i].datafield_13);
                    salesman["nativeLand"] = checkValidNativeLand(dataSource[i].datafield_14);
                    salesman["partyIdFrom"] = isPartyIdFrom(dataSource[i].datafield_25);
                    salesman["emplPositionTypeId"] = isEmplPositionType(dataSource[i].datafield_26);
                    salesman["dateJoinCompany"] = convertDate(dataSource[i].datafield_27)
                    salesman["salaryBaseFlat"] = checkValidBigDecimal(dataSource[i].datafield_28);
                    salesman["periodTypeId"] = isPeriodType(dataSource[i].datafield_29);
                    salesman["userLoginId"] = checkValidUserLoginId(dataSource[i].datafield_30);
                    salesman["password"] = defaultPassword;
                    salesman["isParticipateIns"] = "N";
                    salesman["permanentRes"] = JSON.stringify(permanentRes);
                    salesman["currRes"] = JSON.stringify(currRes);
                 dataSubmit.push(salesman);
    	    }
    	    $.ajax({
        		type: 'POST',
        		url: 'jqxGeneralServicer?sname=createNewSalesmanImports&pagesize=0',
        		data: {
        			"salesman": dataSubmit,
        			"sizeSalesman": dataSubmit.length
        		},
        		dataType: 'json',
        		beforeSend: function(){
        			$("#loader_page_common").show();
        		},
        		success: function(dataSucc){
                    var form = document.createElement("form");
                    form.setAttribute("method", "POST");
                    form.setAttribute("action", "salesman?importdata");
                    var hiddenField0 = document.createElement("input");
                    hiddenField0.setAttribute("type", "hidden");
                    hiddenField0.setAttribute("id", "myjsoninput");
                    hiddenField0.setAttribute("name", "json");
                    hiddenField0.setAttribute("value", JSON.stringify(dataSucc.results));
                    form.appendChild(hiddenField0);
                    document.body.appendChild(form);
                    form.submit();
        		},
        		error: function(data){
        			alert("Send request is error");
        		},
        		complete: function(data){
        			$("#loader_page_common").hide();
        		},
        	});
    	}else{
    	    OlbCore.alert.info("${uiLabelMap.BSNoDataEntered}");
    	}
    }

    function submitDataResult(data){
        var form = document.createElement("form");
        form.setAttribute("method", "POST");
        form.setAttribute("action", "salesman?importdata");
        var hiddenField0 = document.createElement("input");
        hiddenField0.setAttribute("type", "hidden");
        hiddenField0.setAttribute("id", "myjsoninput");
        hiddenField0.setAttribute("name", "json");
        hiddenField0.setAttribute("value", JSON.stringify(data));
        form.appendChild(hiddenField0);
        document.body.appendChild(form);
        form.submit()
    }

    var OlbImportDataProduct = (function(){
        var dataUploadDDB;
        var init = function(){
            initInputs();
            initElementComplex();
            initEvents();
            initValidateForm();
        };
        var initInputs = function(){
            var config = {
                datafields: [
                ],
                columns: [
                ],
                width: '100%',
                height: 'auto',
                sortable: true,
                filterable: false,
                pageable: true,
                pagesize: 10,
                useUrl: false,
                url: '',
                groupable: true,
                virtualmode: true,
            };
            dataUploadDDB = new OlbGrid($("#jqGridDataUpload"), null, config, []);;
        };

        var initElementComplex = function(){

        };

        var initEvents = function(){

        };

        var initValidateForm = function(){

        };
        var getValues = function(){
        };

        var clearGrid = function(){
            dataUploadDDB.updateSource(null, []);
        };
        var updateDataGrid = function(dataFieldData, columnRowData){
            var localdata = [];
            var datafield = [];
            var columns = [];
            if(dataFieldData.length > 0){
                var width = 100 / dataFieldData.length;
                if(width < 15){
                    width = 15;
                }
                for(var i = 0; i < dataFieldData.length; i++){
                    datafield.push({name: 'datafield_' + i, type: 'string'});
                    columns.push({text: dataFieldData[i], datafield: 'datafield_' + i, width: width + '%'});
                }
                var localdata = [];
                for(var i = 0; i < columnRowData.length; i++){
                    var rowData = columnRowData[i];
                    var newRow = {};
                    for(var j = 0; j < dataFieldData.length; j++){
                        var value = null;
                        if(rowData[j]){
                            value = rowData[j];
                        }
                        newRow['datafield_' + j] = value;
                    }
                    localdata.push(newRow);
                }
            }
            var source = $("#jqGridDataUpload").jqxGrid('source');
            source._source.datafields = datafield;
            source._source.localdata = localdata;
            $("#jqGridDataUpload").jqxGrid('columns', columns);
            $("#jqGridDataUpload").jqxGrid('source', source);
        };

        return {
            init: init,
            updateDataGrid: updateDataGrid,
            getValues: getValues,
            clearGrid: clearGrid
        };
    }());

    var OlbPopupSelectFile = (function(){
        var _workBook = null;
        var _optFS = "|OLBIUS|";
        var _optRS = "\n";
        var _csv;
        var _totalCols = 0;

        var init = function(){
            var validatorForm;
            initInputs();
            initElementComplex();
            initEvents();
            initValidateForm();
        };

        var initInputs = function(){
            $("#alterpopupWindow").jqxWindow({
                width: 650, maxWidth: 1000, theme: "olbius", height: 200, resizable: false,  isModal: true, autoOpen: false, cancelButton: $("#alterCancel"), modalOpacity: 0.7
            });
            $('#id-input-file').ace_file_input({
                no_file:'No File ...',
                btn_choose:'Choose',
                btn_change:'Change',
                droppable:false,
                onchange:null,
                thumbnail:false
            });

            $("#selectSheetFile").jqxDropDownList({placeHolder: uiLabelMap.PleaseSelectTitle, width: '100%', theme: theme, height: '24px'});
        };

        var initElementComplex = function(){
        };

        var initEvents = function(){
            var xlf = document.getElementById('id-input-file');
            xlf.addEventListener('change', handleFile, false);

            $("#selectSheetFile").on('select', function(event){
                var args = event.args;
                if (args) {
                    var item = args.item;
                    var value = item.value;
                    _csv = to_csv(_workBook, value);
                }
            });

            $("#alterSave").click(function (){
                var validate = $('#alterpopupWindow').jqxValidator('validate');
                if(!validate) return false;
                $('#alterpopupWindow').jqxWindow('close');
                $("#dataSampleError").empty();
                OlbImportDataProduct.clearGrid();
                $("#divNotificationStatusImport").hide();
                $("#divGridDataUpload").show();
                prepareSourceFileData();
                document.getElementById("excuteImportData").disabled = false;

            });

            $('#alterpopupWindow').on('close', function (event) {
                resetPopup();
            });
        };

        var initValidateForm = function(){
            var extendRules = [
            {
                input : "#id-input-file",
                message : uiLabelMap.BSFieldRequired,
                action : "onchange",
                rule : function(input, commit) {
                    var value = input.val();
                    if (value == '')
                        return false;
                    return true;
                }
            }
            ];
            var mapRules = [
            {input: '#selectSheetFile', type: 'validInputNotNull'},
            ];
            validatorForm = new OlbValidator($('#alterpopupWindow'), mapRules, extendRules, {position: 'right'});
        };

        var handleFile = function (e) {
            var files = e.target.files;
            var f = files[0];
            {
                var reader = new FileReader();
                reader.onload = function(e) {
                    var data = e.target.result;
                    wb = XLSX.read(data, {type: 'binary'});
                    process_wb(wb);
                };
                reader.readAsBinaryString(f);
            }
        };

        var process_wb = function(wb) {
            _workBook = wb;
            var sheetNames = wb.SheetNames;
            var source = [];
            var listSheets = [];
            for(var i = 0; i < sheetNames.length; i++){
                source.push({sheetName: sheetNames[i], sheetName: sheetNames[i]});
            }
            $("#selectSheetFile").jqxDropDownList({source: sheetNames});
        }

        var to_csv = function (workbook, sheetName) {
            var csv = XLSX.utils.sheet_to_csv(workbook.Sheets[sheetName], {FS: _optFS, RS: _optRS});
            if(csv.length > 0){
                return csv;
            }
            return null;
        };

        function clearAceInputFile(divAce){
            divAce.parent().find('a.remove').trigger('click');
        }

        var resetPopup = function(){
            $("#alterpopupWindow").jqxValidator('hide');
            clearAceInputFile($("#id-input-file"));
            $("#selectSheetFile").jqxDropDownList('clear');
        }

        var prepareSourceFileData = function(){
            if(_csv && _csv.length > 0){
                var allData = _csv.split(_optRS);
                var numberLineTitle = 5;
                var headerData = allData[4];
                var headerArr = getHeaderSourceFile(headerData.split(_optFS));
                if (headerArr.length > 0){
                    $("#divGrid").show();
                    var columnData = getRowSourceFile(allData.slice(numberLineTitle));
                    OlbImportDataProduct.updateDataGrid(headerArr, columnData);
                }else{
                    $("#divGrid").hide();
                    $("#dataSampleError").html("${uiLabelMap.BSNeedEnterTemplateFile}");
                }
            }else{
                updateDataGrid([], []);
            }
        };

        var getHeaderSourceFile = function(headerRawArr){
            var headerArr = [];
            for(var i = 0; i < headerRawArr.length; i++){
                if(headerRawArr[i] && headerRawArr[i].trim().length > 0){
                    headerArr.push(headerRawArr[i]);
                }
            }
            return headerArr;
        };
        var getRowSourceFile = function(rowRawDataArr){
            var data = [];
            for(var i = 0; i < rowRawDataArr.length; i++){
                var rowData = rowRawDataArr[i].split(_optFS);
                var tempData = [];
                var isInsert = false;
                for(var j = 0; j < rowData.length; j++){
                    if(!isInsert && rowData[j] && rowData[j].trim().length > 0){
                        isInsert = true;
                    }
                    tempData.push(rowData[j]?decodeEntities(rowData[j]):'');
                }
                if(isInsert){
                    data.push(tempData);
                }
            }
            return data;
        };

        return {
            init: init
        };

    }());
    <#if parameters.importdata?exists>
    function decodeEntities(encodedString) {
        var textArea = document.createElement('textarea');
        textArea.innerHTML = encodedString;
        return textArea.value;
    };

    var OlbResultImportData = (function(){
        var productErrorGRID;

        var init = function(){
            initInputs();
            initElementComplex();
            initEvents();
            initValidateForm();
        };

        var initInputs = function(){
            var dataImport = JSON.parse(decodeEntities('${parameters.json}'));
            var dataImportSucc = [];
            var dataImportError = [];
            for (var i=0;i<dataImport.length; i++)  {
                dataImport[i].statusImport === "success"?dataImportSucc.push(dataImport[i]):dataImportError.push(dataImport[i]);
            }
            dataImportError.sort(function(a, b) {
                return a.sequence - b.sequence;
            });
            $('#dataSuccessId').html("" + dataImportSucc.length + " ${uiLabelMap.BSCustomerImportSuccess}");
            var config = {
                datafields: [
                    {name: 'sequence', type: 'string'},
                    {name: 'partyCode', type: 'string'},
                    {name: 'lastName', type: 'string'},
                    {name: 'middleName', type: 'string'},
                    {name: 'firstName', type: 'string'},
                    {name: 'gender', type: 'string'},
                    {name: 'birthday', type: 'string'},
                    {name: 'userLoginId', type: 'string'},
                    {name: 'message', type: 'string'}
                ],
                columns: [
                    {text: '${StringUtil.wrapString(uiLabelMap.BSNo2)}', dataField: 'sequence', editable: false, pinned: true, width: '5%'},
                    {text: '${StringUtil.wrapString(uiLabelMap.BSEmployeeId)}', dataField: 'partyCode', width: '20%'},
                    {text: '${StringUtil.wrapString(uiLabelMap.BSLastName)}', dataField: 'lastName', width: '15%'},
                    {text: '${StringUtil.wrapString(uiLabelMap.BSMiddleName)}', dataField: 'middleName', width: '15%'},
                    {text: '${StringUtil.wrapString(uiLabelMap.BSFirstName)}', dataField: 'firstName', width: '15%'},
                    {text: '${StringUtil.wrapString(uiLabelMap.BSMessage)}', dataField: 'message', width : '30%',
                        cellsrenderer: function(row, column, value){
                            return '<a href="#" onclick="displayMessageError('+row+')">'+'${StringUtil.wrapString(uiLabelMap.BSDisplayErrorMessage)}'+'</a>';
                        }
                    }
                ],
                width: '100%',
                height: 'auto',
                sortable: true,
                filterable: false,
                pageable: true,
                pagesize: 10,
                useUrl: false,
                url: '',
                groupable: true,
                virtualmode: true
            };
            productErrorGRID = new OlbGrid($("#jqGridImportDataError"), dataImportError, config, []);
        }

        var initElementComplex = function(){

        }

        var initEvents = function(){

        }

        var initValidateForm = function(){

        }

        return {
            init: init,
        };

    }());
    </#if>


</script>