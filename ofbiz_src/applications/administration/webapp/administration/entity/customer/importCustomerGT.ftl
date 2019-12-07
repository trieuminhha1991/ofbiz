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
	BSCheckAddress: "${StringUtil.wrapString(uiLabelMap.BSCheckAddress)}",
	BSCheckFullName: "${StringUtil.wrapString(uiLabelMap.BSCheckFullName)}",
	BSCheckSpecialCharacter: "${StringUtil.wrapString(uiLabelMap.BSCheckSpecialCharacter)}",
	BSCheckPostalCode: "${StringUtil.wrapString(uiLabelMap.BSCheckPostalCode)}",
	BSCheckTaxCode: "${StringUtil.wrapString(uiLabelMap.BSCheckTaxCode)}",
	BSCheckWebsite: "${StringUtil.wrapString(uiLabelMap.BSCheckWebsite)}",
	BSCheckEmail: "${StringUtil.wrapString(uiLabelMap.BSCheckEmail)}",
	BSCheckPhone: "${StringUtil.wrapString(uiLabelMap.BSCheckPhone)}",

};

<#assign genders = delegator.findList("Gender", null, null, null, null, false)/>
<#assign countries = delegator.findList("Geo", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("geoTypeId", "COUNTRY"), null, null, null, false)/>
<#assign provinces = delegator.findList("Geo", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("geoTypeId", "PROVINCE"), null, null, null, false)/>
<#assign uoms = delegator.findList("Uom", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("uomTypeId", "CURRENCY_MEASURE"), null, null, null, false)/>
var globalVar = {};
globalVar.uoms = [
    <#if uoms?has_content>
            <#list uoms as uom>{
            uomId: '${uom.uomId}',
    		abbreviation: '${StringUtil.wrapString(uom.abbreviation)}'
    		},</#list>
    </#if>
]
globalVar.genders = [
    <#if genders?has_content>
            <#list genders as gender>{
            genderId: '${gender.genderId}',
    		description: '${StringUtil.wrapString(gender.description)}'
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

    function isUom(uomName){
        if (OlbCore.isEmpty(uomName)){
            return "";
        }else{
            for(var item in globalVar.uoms){
                if(uomName == globalVar.uoms[item].abbreviation) return globalVar.uoms[item].uomId;
            }
            return '_NA_';
        }
    }
    function isGender(genderName){
        if (OlbCore.isEmpty(genderName)){
            return "";
        }else{
            for(var item in globalVar.genders){
                if(genderName == globalVar.genders[item].description) return globalVar.genders[item].genderId;
            }
            return '_NA_';
        }
    }
    function isCountry(name){
        for(var item in globalVar.countries){
            if(name == globalVar.countries[item].geoName) return globalVar.countries[item].geoId;
        }
        return null;
    }
    function isProvince(nameCountry, nameProvince){
        if(isCountry(nameCountry)){
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
        }
        return null;
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

    function isNullString(str){
        return str != null? str : '';
    }

    function checkValidPartyCode(name){
        if (OlbCore.isEmpty(name)){
            return "";
        }else{
            if (!name.containSpecialChars() && !hasWhiteSpace(name)) return name;
            return '_NA_';
        }
    }

    function checkValidPartyName(name){
        if (OlbCore.isEmpty(name)) return "";
        return name;
    }

    function checkValidFullName(name){
        if (OlbCore.isEmpty(name)){
            return "";
        }else{
            if(checkRex(name, multiLang.BSCheckFullName)) return name;
            return '_NA_';
        }
    }

    function checkValidSpecialCharacter(name){
            if (OlbCore.isEmpty(name)){
                return "";
            }else{
                if(checkRex(name, multiLang.BSCheckSpecialCharacter)) return name;
                return '_NA_';
            }
        }

    function checkValidAddress(name){
        if (OlbCore.isEmpty(name)){
            return "";
        }else{
            if(checkRex(name, multiLang.BSCheckAddress)) return name;
            return '_NA_';
        }
    }

    function checkValidWebsite(name){
        if (OlbCore.isEmpty(name)){
            return "";
        }else{
            if(checkRex(name, multiLang.BSCheckWebsite)) return name;
            return '_NA_';
        }
    }

    function checkValidEmail(name){
        if (OlbCore.isEmpty(name)){
            return "";
        }else{
            if(checkRex(name, multiLang.BSCheckEmail)) return name;
            return '_NA_';
        }
    }

    function checkValidPhone(name){
        if (OlbCore.isEmpty(name)){
            return "";
        }else{
            if(checkRex(name, multiLang.BSCheckPhone)) return name;
            return '_NA_';
        }
    }

    function checkValidPostalCode(name){
        if (OlbCore.isEmpty(name)){
            return "";
        }else{
            if(checkRex(name, multiLang.BSCheckPostalCode)) return name;
            return '_NA_';
        }
    }

    function checkValidTaxCode(name){
        if (OlbCore.isEmpty(name)){
            return "";
        }else{
            if(checkRex(name, multiLang.BSCheckTaxCode)) return name;
            return '_NA_';
        }
    }

</script>

<!--html-->
<div class="top-menu-import-data">
    <button class="btn btn-primary" onclick="downloadFileCustomer()"><i class="fa fa-download"></i>${uiLabelMap.DownloadFileTemplateSalesForecast}</button>
    <button class="btn btn-primary" onclick="uploadFileCustomer()"><i class="fa fa-upload"></i>${uiLabelMap.UploadFileAttachment}</button>
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
    <button id="excuteImportData" class="btn btn-primary" onclick="importDataCustomerGT()"><i class="fa fa-upload"></i>${uiLabelMap.BSImportData}</button>
</div>
<!--popup-->
<div id="alterpopupWindow" class='hide'>
	<div>${uiLabelMap.UploadFileAttachment}</div>
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

    function downloadFileCustomer(){
        window.location.href = "exportExcelDataSampleCustomer";
    }

    function uploadFileCustomer(){
        $('#alterpopupWindow').jqxWindow('open');
    }

    function toTimestamp(strDate){
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

    function importDataCustomerGT(){
        var source = $("#jqGridDataUpload").jqxGrid('source');
    	var dataSource = source._source.localdata;
    	if (dataSource.length > 0) {
        	var dataSubmit = [];
        	for (var i=0;i<dataSource.length;i++){
                var representative = {
                    "partyFullName":checkValidFullName(dataSource[i].datafield_18),
                    "birthDate":toTimestamp(""+dataSource[i].datafield_19) != "_NA_" ? toTimestamp(""+dataSource[i].datafield_19): null,
                    "isValidBirthDate": toTimestamp(""+dataSource[i].datafield_19),
                    "gender":isGender(dataSource[i].datafield_20),
                    "contactNumber":checkValidPhone(dataSource[i].datafield_21),
                    "infoString":checkValidEmail(dataSource[i].datafield_22)
                };
        		var customer = {};
                    customer["sequence"] = dataSource[i].datafield_0;
                    customer["partyCode"] = checkValidPartyCode(dataSource[i].datafield_1);
                    customer["groupName"] = checkValidPartyName(dataSource[i].datafield_2);
                    customer["groupNameLocal"] = checkValidPartyName(dataSource[i].datafield_2);
                    customer["contactNumber"] = checkValidPhone(dataSource[i].datafield_3);
                    customer["infoString"] = checkValidEmail(dataSource[i].datafield_4);
                    customer["taxAuthInfos"] = checkValidTaxCode(dataSource[i].datafield_5);
                    customer["postalCode"] = checkValidPostalCode(dataSource[i].datafield_6);
                    customer["distributorId"] = dataSource[i].datafield_7;
                    customer["salesmanId"] = dataSource[i].datafield_8;
                    customer["routeId"] = dataSource[i].datafield_9;
                    customer["toName"] = checkValidFullName(dataSource[i].datafield_10);
                    customer["attnName"] = checkValidSpecialCharacter(dataSource[i].datafield_11);
                    customer["address1"] = checkValidAddress(dataSource[i].datafield_12);
                    customer["wardGeoId"] = dataSource[i].datafield_13;
                    customer["districtGeoId"] = dataSource[i].datafield_14;
                    customer["stateProvinceGeoId"] = isProvince(dataSource[i].datafield_16, dataSource[i].datafield_15);
                    customer["countryGeoId"] = isCountry(dataSource[i].datafield_16);
                    customer["currencyUomId"] = isUom(dataSource[i].datafield_17);
                    customer["visitFrequencyTypeId"] = "F0";
                    customer["representative"] = JSON.stringify(representative);
        		dataSubmit.push(customer);
        	}
        	$.ajax({
        		type: 'POST',
        		url: 'jqxGeneralServicer?sname=createDataCustomerGTImport&pagesize=0',
        		data: {
        			"customers": dataSubmit,
        			"sizeCustomers": dataSubmit.length
        		},
        		dataType: 'json',
        		beforeSend: function(){
        			$("#loader_page_common").show();
        		},
        		success: function(dataSucc){
                    //console.log(JSON.stringify(dataSucc.results));
                    //console.log(encodeURIComponent(JSON.stringify(dataSucc.results)));
                    var form = document.createElement("form");
                    form.setAttribute("method", "POST");
                    form.setAttribute("action", "customerGT?importdata");
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
        			console.log(data);
        		},
        		complete: function(data){
        			$("#loader_page_common").hide();
        		},
        	});
    	}else{
    	    OlbCore.alert.info("${uiLabelMap.BSNoDataEntered}");
    	}
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
            console.log(localdata);
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
                    tempData.push(rowData[j]);
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

    var displayMessageError = function(row){
        var data = $('#jqGridImportDataError').jqxGrid('getrowdata', row);
        var messages = data.message;
        var messageArr = messages.trim().split("\n");
        $("#alterpopupMessage").jqxWindow('open');
        var table = document.getElementById('displayMessageErrorId');
        for(var i in messageArr){
            console.log(i);
            var row = table.insertRow(i);
            var cell1 = row.insertCell(0);
            cell1.innerHTML = "- "+ messageArr[i];
        }
    }

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
            console.log(dataImport);
            var dataImportSucc = [];
            var dataImportError = [];
            for (var i=0;i<dataImport.length; i++)  {
                dataImport[i].statusImport === "success"?dataImportSucc.push(dataImport[i]):dataImportError.push(dataImport[i]);
            }
            console.log(dataImportError);
            console.log(dataImportSucc);
            $('#dataSuccessId').html("" + dataImportSucc.length + " ${uiLabelMap.BSCustomerImportSuccess}");
            var config = {
                datafields: [
                    {name: 'sequence', type: 'string'},
                    {name: 'partyCode', type: 'string'},
                    {name: 'groupName', type: 'string'},
                    {name: 'groupNameLocal', type: 'string'},
                    {name: 'contactNumber', type: 'string'},
                    {name: 'infoString', type: 'string'},
                    {name: 'taxAuthInfos', type: 'string'},
                    {name: 'postalCode', type: 'string'},
                    {name: 'distributorId', type: 'string'},
                    {name: 'salesmanId', type: 'string'},
                    {name: 'routeId', type: 'string'},
                    {name: 'toName', type: 'string'},
                    {name: 'attnName', type: 'string'},
                    {name: 'address1', type: 'string'},
                    {name: 'wardGeoId', type: 'string'},
                    {name: 'districtGeoId', type: 'string'},
                    {name: 'stateProvinceGeoId', type: 'string'},
                    {name: 'countryGeoId', type: 'string'},
                    {name: 'currencyUomId', type: 'string'},
                    {name: 'visitFrequencyTypeId', type: 'string'},
                    {name: 'message', type: 'string'}
                ],
                columns: [
                    {text: '${StringUtil.wrapString(uiLabelMap.BSNo2)}', dataField: 'sequence', editable: false, pinned: true, width: '5%'},
                    {text: '${StringUtil.wrapString(uiLabelMap.BSCustomerId)}', dataField: 'partyCode', width: '15%'},
                    {text: '${StringUtil.wrapString(uiLabelMap.BSCustomerName)}', dataField: 'groupName', width: '40%'},
                    {text: '${StringUtil.wrapString(uiLabelMap.BSPhoneNumber)}', dataField: 'contactNumber', width: '10%'},
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