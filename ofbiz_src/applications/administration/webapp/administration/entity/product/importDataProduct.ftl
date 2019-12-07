<@jqGridMinimumLib/>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxvalidator.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxwindow.js"></script>
<script src="/hrresources/js/jszip.js" type="text/javascript"></script>
<script src="/hrresources/js/xlsx.js" type="text/javascript"></script>
<script type="text/javascript" src="/crmresources/js/miscUtil.js"></script>
<@jqOlbCoreLib hasDropDownButton=true hasValidator=true hasGrid=true/>

<#include "component://administration/webapp/administration/entity/popupMessageError.ftl"/>

<script>
var globalVar = {};
<#assign quantityUomList = delegator.findByAnd("Uom", {"uomTypeId" : "PRODUCT_PACKING"}, null, true)!/>
<#assign listWeightUom = delegator.findByAnd("Uom", {"uomTypeId", "WEIGHT_MEASURE"}, null, false)!/>
<#assign listCurrencyUom = delegator.findByAnd("Uom", {"uomTypeId" : "CURRENCY_MEASURE"}, null, true)!/>

    globalVar.quantityUomDatas = [
    	<#if quantityUomList?has_content>
    		<#list quantityUomList as item>
    		{	uomId: "${item.uomId}",
    			abbreviation: "${item.abbreviation?if_exists}",
    			description: "${StringUtil.wrapString(item.get("description", locale)?if_exists)}"
    		},
    		</#list>
    	</#if>
    ]
	globalVar.weightUomDatas = [
		<#if listWeightUom?has_content>
			<#list listWeightUom as item>
			{	uomId: "${item.uomId}",
				abbreviation: "${item.abbreviation?if_exists}",
				description: "${StringUtil.wrapString(item.get("description", locale)?if_exists)}"
			},
			</#list>
		</#if>
	];
	globalVar.currencyUomDatas = [
		<#if listCurrencyUom?has_content>
			<#list listCurrencyUom as item>
			{	uomId: "${item.uomId}"
			},
			</#list>
		</#if>
	];

    function isQuantityUom(name){
        if (OlbCore.isEmpty(name)){
            return "";
        }else{
            for(var item in globalVar.quantityUomDatas){
                if(name == globalVar.quantityUomDatas[item].description) return globalVar.quantityUomDatas[item].uomId;
            }
            return '_NA_';
        }
    }

    function isCurrencyUom(name){
        if (OlbCore.isEmpty(name)){
            return "";
        }else{
            for(var item in globalVar.currencyUomDatas){
                if(name == globalVar.currencyUomDatas[item].uomId) return globalVar.currencyUomDatas[item].uomId;
            }
            return '_NA_';
        }
    }

    function isWeightUom(name){
        if (OlbCore.isEmpty(name)){
            return "";
        }else{
            for(var item in globalVar.weightUomDatas){
                if(name == globalVar.weightUomDatas[item].abbreviation) return globalVar.weightUomDatas[item].uomId;
            }
            return '_NA_';
        }
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

    function checkValidBigDecimal(name){
        if (OlbCore.isEmpty(name)){
            return "";
        }else{
            if (checkRex(name, new RegExp(/^[1-9]\d*([.]\d{1,2})?$/))) return name;
            return '_NA_';
        }
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
</script>

<!--html-->
<div class="top-menu-import-data">
    <button class="btn btn-primary" onclick="downloadFileProduct()"><i class="fa fa-download"></i>${uiLabelMap.DownloadFileTemplateSalesForecast}</button>
    <button class="btn btn-primary" onclick="uploadFileProduct()"><i class="fa fa-upload"></i>${uiLabelMap.UploadFileAttachment}</button>
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
    <button id="excuteImportData" class="btn btn-primary" onclick="importDataProduct()"><i class="fa fa-upload"></i>${uiLabelMap.BSImportData}</button>
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

    function downloadFileProduct(){
        window.location.href = "exportExcelDataSampleProducts";
    }

    function uploadFileProduct(){
        $('#alterpopupWindow').jqxWindow('open');
    }

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

    function importDataProduct(){
        var source = $("#jqGridDataUpload").jqxGrid('source');
    	var dataSource = source._source.localdata;
    	if (dataSource.length > 0) {
        	var dataSubmit = [];
        	for (var i=0;i<dataSource.length;i++){
        		var product = {};
        		product["sequence"] = isNullString(dataSource[i].datafield_0);
        		product["productCode"] = checkValidPartyCode(dataSource[i].datafield_1);
        		product["productName"] = checkValidPartyName(dataSource[i].datafield_2);
        		product["quantityUomId"] = isQuantityUom(dataSource[i].datafield_3);
        		product["productListPrice"] = checkValidBigDecimal(dataSource[i].datafield_4);
                product["productDefaultPrice"] = checkValidBigDecimal(dataSource[i].datafield_5);
                product["currencyUomId"] = isCurrencyUom(dataSource[i].datafield_6);
                product["productWeight"] = checkValidBigDecimal(dataSource[i].datafield_7);
                product["weight"] = checkValidBigDecimal(dataSource[i].datafield_8);
                product["weightUomId"] = isWeightUom(dataSource[i].datafield_9);
        		dataSubmit.push(product);
        	}
        	$.ajax({
        		type: 'POST',
        		url: 'jqxGeneralServicer?sname=createDataSampleProducts&pagesize=0',
        		data: {
        			"products": dataSubmit,
        			"sizeProducts": dataSubmit.length
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
                    form.setAttribute("action", "importProduct?importdata");
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
                var headerSample = ["${uiLabelMap.BSNo2}", "${uiLabelMap.BSProductCode}", "${uiLabelMap.BSProductName}", "${uiLabelMap.QuantityUomId}", "${uiLabelMap.ProductDefaultPrice}", "${uiLabelMap.BSCurrencyUomId}"];
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
            $('#dataSuccessId').html("" + dataImportSucc.length + " ${uiLabelMap.BSProductImportSuccess}");
            var config = {
                datafields: [
                    {name: 'sequence', type: 'string'},
                    {name: 'productCode', type: 'string'},
                    {name: 'productName', type: 'string'},
                    {name: 'quantityUomId', type: 'string'},
                    {name: 'productDefaultPrice', type: 'string'},
                    {name: 'currencyUomId', type: 'string'},
                    {name: 'message', type: 'string'}
                ],
                columns: [
                    {text: '${StringUtil.wrapString(uiLabelMap.BSNo2)}', dataField: 'sequence', editable: false, pinned: true, width: '10%'},
                    {text: '${StringUtil.wrapString(uiLabelMap.BSProductCode)}', dataField: 'productCode', width: '20%'},
                    {text: '${StringUtil.wrapString(uiLabelMap.BSProductName)}', dataField: 'productName', width: '40%'},
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