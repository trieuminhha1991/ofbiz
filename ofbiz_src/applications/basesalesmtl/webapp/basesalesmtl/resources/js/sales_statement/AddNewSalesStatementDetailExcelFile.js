var addSalesStatementSheetDetailObj = (function(){
    var _workBook = null;
    var _optFS = "|OLBIUS|";
    var _optRS = "\n";
    var _csv;
    var _totalCols = 0;
    var listProducts = [];
    var init = function(){
        initWindow();
        initSimpleInput();
        initDropDown();
        initJqxNumberInput();
        initSourceFileGrid();
        initEvent();
        initJoinColumnDataGrid();
        initJqxValidator();
    };
    var initWindow = function(){
        createJqxWindow($("#UploadFileSalesStatementEdited"), 700, 550);
    };
    var openWindow = function(){
        openJqxWindow($("#UploadFileSalesStatementEdited"));
        $("#numberLineTitle").val(6);
        listProducts = OlbCRUDSalesStatement.getListProducts();
        //sort listProduct by productCode
        var tempProducts = listProducts;
        var tempProductMap = {};
        var tempProductCodes = [];
        $.each(tempProducts, function (index, value) {
            tempProductMap[value.productCode] = value;
            tempProductCodes.push(value.productCode);
        });
        tempProductCodes.sort();
        listProducts = [];
        $.each(tempProductCodes, function (i, v) {
            listProducts.push(tempProductMap[v]);
        });

    };
    var initSourceFileGrid = function(){
        var datafield = [];
        var columns = [];
        var grid = $("#sourceFileSalesStatementGrid");
        var config = {
            url: '',
            showtoolbar : false,
            autoheight: true,
            //height: 250,
            width : '100%',
            virtualmode: false,
            editable: false,
            localization: getLocalization(),
            source: {
                pagesize: 10
            }
        };
        Grid.initGrid(config, datafield, columns, null, grid);
    };
    var initJoinColumnDataGrid = function(){
        var datafield = [{name: 'fieldValueInSys', type: 'string'},
            {name: 'fieldNameInSys', type: 'string'},
            {name: 'fieldValueInExcel', type: 'string'},
            {name: 'fieldNameInExcel', type: 'string'},
            {name: 'fileType', type: 'string'},
        ];
        var columns = [{datafield: 'fieldValueInSys', hidden: true},
            {datafield: 'fileType', hidden: true},
            {text: uiLabelMap.ColumnDataInSystem, datafield: 'fieldNameInSys', editable:false, width: '50%'},
            {text: uiLabelMap.ColumnDataInImportFile, datafield: 'fieldValueInExcel', editable: true, width: '50%', columntype: 'dropdownlist',
                cellsrenderer: function (row, columnfield, value, defaulthtml, columnproperties) {
                    var rowData = $("#joinColumnGrid").jqxGrid('getrowdata', row);
                    if(rowData.fieldNameInExcel){
                        return '<span>' + rowData.fieldNameInExcel + '</span>';
                    }
                }
            }
        ];
        var grid = $("#joinColumnGrid");
        var customcontrol = "fa fa-columns open-sans@" + uiLabelMap.ColumnMapAuto + "@javascript: void(0);@addSalesStatementSheetDetailObj.autoMapColumn()";
        var customcontrol2 = "fa fa-refresh open-sans@" + uiLabelMap.BSCommonReset + "@javascript: void(0);@addSalesStatementSheetDetailObj.resetJoinColumnGridData()";
        var rendertoolbar = function(toolbar){
            toolbar.html("");
            var id = "joinColumnGrid";
            var jqxheader = $("<div id='toolbarcontainer" + id +"' class='widget-header'><h5>" + uiLabelMap.JoinColumnDataExcel + "</h5><div id='toolbarButtonContainer" + id + "' class='pull-right'></div></div>");
            toolbar.append(jqxheader);
            var container = $('#toolbarButtonContainer' + id);
            var maincontainer = $("#toolbarcontainer" + id);
            Grid.createCustomControlButton(grid, container, customcontrol);
            Grid.createCustomControlButton(grid, container, customcontrol2);
        };

        var config = {
            url: '',
            showtoolbar : true,
            rendertoolbar: rendertoolbar,
            autoheight: false,
            height: 340,
            width : '100%',
            virtualmode: false,
            editable: true,
            selectionmode: 'singlecell',
            //pageable: false,
            localization: getLocalization(),
            source: {
                pagesize: 10,
                localdata: []
            }
        };
        Grid.initGrid(config, datafield, columns, null, grid);
    };
    var getJoinColumnGridLocaldata = function(){
        var localdata = [];
        var columnLabel;
        var prodCode_productId;
        localdata.push({fieldValueInSys: 'partyIdFrom', fieldNameInSys: uiLabelMap.OrgUnitName, fileType: "string"});
        localdata.push({fieldValueInSys: 'partyCode', fieldNameInSys: uiLabelMapPartyId, fileType: "string"});
        for(var i = 0; i < listProducts.length; i ++){
            columnLabel =  listProducts[i].productCode + " (" + listProducts[i].quantityUomIdDesc + ")";
            prodCode_productId = 'prodCode_' + listProducts[i].productId;
            var row = {fieldValueInSys: prodCode_productId, fieldNameInSys: columnLabel, fileType: "string"};
            localdata.push(row);
        }
        return localdata;
    };

    var initJqxNumberInput = function(){
        $("#numberLineTitle").jqxNumberInput({ width: 100, height: '25px', spinButtons: true, decimalDigits: 0, inputMode: 'simple', min: 1});
    };
    var initSimpleInput = function(){
        $('#sourceFileSalesStatementImport').ace_file_input({
            no_file:'No File ...',
            btn_choose: uiLabelMap.CommonChooseFile,
            droppable:false,
            onchange:null,
            thumbnail:false,
            width: '100%',
            //whitelist:'gif|png|jpg|jpeg',
            preview_error : function(filename, error_code) {
            }
        });
    };

    var initDropDown = function(){
        createJqxDropDownList([], $("#sheetImportDropDown"), "sheetName", "sheetName", 25, '99%');
    };
    var initEvent = function(){
        var xlf = document.getElementById('sourceFileSalesStatementImport');
        xlf.addEventListener('change', handleFile, false);

        $("#sheetImportDropDown").on('select', function(event){
            var args = event.args;
            if (args) {
                var item = args.item;
                var value = item.value;
                _csv = to_csv(_workBook, value);
            }
        });
        $("#UploadFileSalesStatementEdited").on('close', function(event){
            resetData();
            hideValidate();
        });
    };

    var handleFile = function (e) {
        var files = e.target.files;
        var f = files[0];
        {
            var reader = new FileReader();
            //var name = f.name;
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
        for(var i = 0; i < sheetNames.length; i++){
            source.push({sheetName: sheetNames[i], sheetName: sheetNames[i]});
        }
        updateSourceDropdownlist($("#sheetImportDropDown"), source);
    }
    var to_csv = function (workbook, sheetName) {
        var csv = XLSX.utils.sheet_to_csv(workbook.Sheets[sheetName], {FS: _optFS, RS: _optRS});
        var safeDecodeRangeObj = XLSX.utils.safe_decode_range(workbook.Sheets[sheetName]['!ref']);
        //_totalCols = safeDecodeRangeObj.e.c;
        if(csv.length > 0){
            return csv;
        }
        return null;
    };
    var prepareSourceFileData = function(){
        if(_csv && _csv.length > 0){
            var allData = _csv.split(_optRS);
            var numberLineTitle = $("#numberLineTitle").val();
            if(numberLineTitle <= 1){
                numberLineTitle = 1;
            }
            var headerData = allData[numberLineTitle - 1];
            var headerArr = getHeaderSourceFile(headerData.split(_optFS));
            var columnData = getRowSourceFile(allData.slice(numberLineTitle));
            updateSourceFileGrid(headerArr, columnData);
        }else{
            updateSourceFileGrid([], []);
        }
    };

    var prepareJoinColumnData = function(){
        updateJoinColumnGrid();
    };

    var updateSourceFileGrid = function(dataFieldData, columnRowData){
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
        var source = $("#sourceFileSalesStatementGrid").jqxGrid('source');
        source._source.datafields = datafield;
        source._source.localdata = localdata;
        $("#sourceFileSalesStatementGrid").jqxGrid('columns', columns);
        $("#sourceFileSalesStatementGrid").jqxGrid('source', source);
    }
    var updateJoinColumnGrid = function(){
        var editorColumnArr = getEditorColumnArr();
        var columns = [{datafield: 'fieldValueInSys', hidden: true},
            {datafield: 'fileType', hidden: true},
            {text: uiLabelMap.ColumnDataInSystem, datafield: 'fieldNameInSys', editable:false, width: '50%'},
            {text: uiLabelMap.ColumnDataInImportFile, datafield: 'fieldValueInExcel', editable: true, width: '50%', columntype: 'dropdownlist',
                cellsrenderer: function (row, columnfield, value, defaulthtml, columnproperties) {
                    for(var i = 0; i < editorColumnArr.length; i++){
                        if(editorColumnArr[i].fieldValueInExcel == value){
                            return '<div style="margin: 4px">' + editorColumnArr[i].fieldNameInExcel + '</div>';
                        }
                    }
                    return '<div style="margin: 4px">' + value + '</div>';
                },
                createeditor: function (row, cellvalue, editor, celltext, cellwidth, cellheight) {
                    createJqxDropDownList(editorColumnArr, editor, "fieldValueInExcel", "fieldNameInExcel", cellheight, cellwidth);
                },
                geteditorvalue: function (row, cellvalue, editor) {
                    // return the editor's value.
                    return editor.val();
                },
                initeditor: function (row, cellvalue, editor, celltext, pressedChar) {
                    editor.jqxDropDownList('val', cellvalue);
                }
            }
        ];
        $("#joinColumnGrid").jqxGrid("columns", columns);
        var localdata = getJoinColumnGridLocaldata();
        var source = $("#joinColumnGrid").jqxGrid("source");
        source._source.localdata = localdata;
        $("#joinColumnGrid").jqxGrid("source", source);
        $("#joinColumnGrid").jqxGrid({pagesizeoptions: [localdata.length]});
        $("#joinColumnGrid").jqxGrid({pagesize: localdata.length});
    };
    var getEditorColumnArr = function(){
        var textData = [];
        var cols = $("#sourceFileSalesStatementGrid").jqxGrid("columns");
        var length = cols.records.length;
        for (var i = 1; i < length; i++) {
            textData.push({fieldValueInExcel: cols.records[i].datafield, fieldNameInExcel: cols.records[i].text});
        }
        return textData;
    };
    var autoMapColumn = function(){
        $("#joinColumnGrid").jqxGrid('showloadelement');
        var rows = $("#joinColumnGrid").jqxGrid("getrows");
        var length = rows.length;
        var startColumnInSysIndex = 0;
        var startColumnInExcelIndex = 0;
        var columnInExcelData = getEditorColumnArr();
        columnInExcelData.splice(2,1);
        var columnInExcelDataLengh = columnInExcelData.length;
        for(var i = length - 1; i >= 0; i--){
            var rowData = $("#joinColumnGrid").jqxGrid('getrowdata', i);
            var fieldValueInExcel = rowData.fieldValueInExcel;
            if(fieldValueInExcel && fieldValueInExcel.length > 0){
                startColumnInSysIndex = i + 1;
                for(var j = 0; j < columnInExcelDataLengh; j++){
                    if(fieldValueInExcel == columnInExcelData[j].fieldValueInExcel){
                        startColumnInExcelIndex = j + 1;
                        break;
                    }
                }
                break;
            }
        }
        for(var i = startColumnInSysIndex; i < length; i++){
            if(startColumnInExcelIndex >= columnInExcelDataLengh){
                startColumnInExcelIndex = 0;
            }
            var columnValue = columnInExcelData[startColumnInExcelIndex].fieldValueInExcel;
            $("#joinColumnGrid").jqxGrid('setcellvalue', i, "fieldValueInExcel", columnValue);
            startColumnInExcelIndex++;
        }



        $("#joinColumnGrid").jqxGrid('hideloadelement');
    };
    var resetJoinColumnGridData = function(){
        var rows = $("#joinColumnGrid").jqxGrid("getrows");
        var length = rows.length;
        for(var i = 0; i < length; i++){
            $("#joinColumnGrid").jqxGrid('setcellvalue', i, "fieldValueInExcel", null);
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
    var validate = function(){
        return $("#fileSalesStatementExcelUpload").jqxValidator('validate');
    };
    var hideValidate = function(){
        $("#fileSalesStatementExcelUpload").jqxValidator('hide');
    };
    var initJqxValidator = function(){
        $("#fileSalesStatementExcelUpload").jqxValidator({
            rules: [
                {input : '#sheetImportDropDown', message : uiLabelMap.FieldRequired, action: 'blur',
                    rule : function(input, commit){
                        if(!input.val()){
                            return false;
                        }
                        return true;
                    }
                },
                {input : '#sourceFileSalesStatementImportContainer', message : uiLabelMap.FieldRequired, action: 'blur',
                    rule : function(input, commit){
                        var value = $("#sourceFileSalesStatementImport").val();
                        if(!value){
                            return false;
                        }
                        return true;
                    }
                },
            ]
        });
    };
    var resetData = function(){
        _workBook = null;
        _csv = null;
        _totalCols = 0;
        clearAceInputFile($("#sourceFileSalesStatementImport"));
        updateSourceDropdownlist($("#sheetImportDropDown"), []);
        $("#numberLineTitle").val(1);
        refreshSourceFileGrid();
        refreshJoinColumnGrid();
    };
    var refreshSourceFileGrid = function(){
        var source =  $("#sourceFileSalesStatementGrid").jqxGrid('source');
        $("#sourceFileSalesStatementGrid").jqxGrid('columns', []);
        source._source.datafield = [];
        source._source.localdata = [];
        $("#sourceFileSalesStatementGrid").jqxGrid('source', source)
    };
    var refreshJoinColumnGrid = function(){
        var source =  $("#joinColumnGrid").jqxGrid('source');
        source._source.localdata = [];
        $("#joinColumnGrid").jqxGrid('source', source);
        $("#joinColumnGrid").jqxGrid('columns', []);
    };
    var getData = function(){
        var columnMapGrid = $("#joinColumnGrid").jqxGrid('getrows');
        var data = {};
        var columnMapSubmit = [];
        var fieldValueExcelMapped = [];
        for(var i = 0; i < columnMapGrid.length; i++){
            var tempRowData = columnMapGrid[i];
            columnMapSubmit.push({fieldValueInSys: tempRowData.fieldValueInSys, fieldValueInExcel: tempRowData.fieldValueInExcel, fileType: tempRowData.fileType});
            fieldValueExcelMapped.push(tempRowData.fieldValueInExcel);
            //console.log(tempRowData.fieldValueInExcel);
        }

        data.sheetIndex = $("#sheetImportDropDown").jqxDropDownList('getSelectedIndex');
        data.startLine = $("#numberLineTitle").val();
        data.columnMap = JSON.stringify(columnMapSubmit);
        return data;
    };
    return{
        init: init,
        resetData: resetData,
        prepareJoinColumnData: prepareJoinColumnData,
        prepareSourceFileData: prepareSourceFileData,
        //onWindowOpen: onWindowOpen,
        openWindow:openWindow,
        getData: getData,
        validate: validate,
        hideValidate: hideValidate,
        autoMapColumn: autoMapColumn,
        resetJoinColumnGridData: resetJoinColumnGridData
    }
}());