<<<<<<< HEAD
<#--
Licensed to the Apache Software Foundation (ASF) under one
or more contributor license agreements.  See the NOTICE file
distributed with this work for additional information
regarding copyright ownership.  The ASF licenses this file
to you under the Apache License, Version 2.0 (the
"License"); you may not use this file except in compliance
with the License.  You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing,
software distributed under the License is distributed on an
"AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
KIND, either express or implied.  See the License for the
specific language governing permissions and limitations
under the License.
-->
<script>
    function escapeHTML(a) {
        return a.replace(/&/g,'&amp;').replace(/</g,'&lt;').replace(/>/g,'&gt;');
    }

</script>
<#--
    jq renderer
-->
<#macro jqMinimumLib>
    <!-- add the jQWidgets framework -->
    <#--<script type="text/javascript">
        if(jqxCoreLoaded == undefined && !jqxCoreLoaded){
            $.getScript("/aceadmin/jqw/jqwidgets/jqxcore.js");
        }
    </script>-->
</#macro>
<#macro jqDataMinimumLib>
    <@jqMinimumLib />
    <script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxbuttons.js"></script>
    <script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxscrollbar.js"></script>
    <script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxdatatable.js"></script>
    <script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxdata.js"></script>
</#macro>
<#macro jqTable url entityName columnlist dataField pageable="true" viewSize="20" columnsResize="true" width="1000" dataType="json"
        sortable="true" filterable="true">
    <@jqDataMinimumLib/>
    <script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxdropdownlist.js"></script>
    <script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxlistbox.js"></script>
    <script type="text/javascript">
        $(document).ready(function () {
            // prepare the data
            var source =
            {
                dataType: '${dataType}',
                dataFields: [
                    ${dataField}
                ],
                id: 'id',
                type: 'POST',
                data: {
                    noConditionFind: "Y"
                },
                sortcolumn: 'glAccountId',
                entityName: '${entityName}',
                sortdirection: 'asc',
                contentType: 'application/x-www-form-urlencoded',
                url: '${url}'
            };
            var filterChanged = false;
            var dataAdapter = new $.jqx.dataAdapter(source,
                {
                    formatData: function (data) {
                        <#if sortable == "true">
                            if (source.totalRecords) {
                                // update the $skip and $top params of the OData service.
                                // data.pagenum - page number starting from 0.
                                // data.pagesize - page size
                                // data.sortdatafield - the column's datafield value(ShipCountry, ShipCity, etc.).
                                // data.sortorder - the sort order(asc or desc).
                                if (data.sortdatafield && data.sortorder) {
                                    data.$orderby = data.sortdatafield + " " + data.sortorder;
                                }
                            }
                        </#if>
                        <#if pageable == "true">
                            // update the $skip and $top params of the OData service.
                            // data.pagenum - page number starting from 0.
                            // data.pagesize - page size
                             data.$skip = data.pagenum * data.pagesize;
                             data.$top = data.pagesize;
                             data.$inlinecount = "allpages";
                        </#if>
                        <#if filterable == "true">
                            if (data.filterslength) {
                                filterChanged = true;
                                var filterParam = "";
                                for (var i = 0; i < data.filterslength; i++) {
                                    // filter's value.
                                    var filterValue = data["filtervalue" + i];
                                    // filter's condition. For the filterMode="simple" it is "CONTAINS".
                                    var filterCondition = data["filtercondition" + i];
                                    // filter's data field - the filter column's datafield value.
                                    var filterDataField = data["filterdatafield" + i];
                                    // "and" or "or" depending on the filter expressions. When the filterMode="simple", the value is "or".
                                    var filterOperator = data[filterDataField + "operator"];
                                    var startIndex = 0;
                                    if (filterValue.indexOf('-') == -1) {
                                        if (filterCondition == "CONTAINS") {
                                            filterParam += "substringof('" + filterValue + "', " + filterDataField + ") eq true";
                                            filterParam += " " + filterOperator + " ";
                                        }
                                    }
                                    else {
                                        if (filterDataField == "ShippedDate") {
                                            var dateGroups = new Array();
                                            var startIndex = 0;
                                            var item = filterValue.substring(startIndex).indexOf('-');
                                            while (item > -1) {
                                                dateGroups.push(filterValue.substring(startIndex, item + startIndex));
                                                startIndex += item + 1;
                                                item = filterValue.substring(startIndex).indexOf('-');
                                                if (item == -1) {
                                                    dateGroups.push(filterValue.substring(startIndex));
                                                }
                                            }
                                            if (dateGroups.length == 3) {
                                                filterParam += "year(ShippedDate) eq " + parseInt(dateGroups[0]) + " and month(ShippedDate) eq " + parseInt(dateGroups[1]) + " and day(ShippedDate) eq " + parseInt(dateGroups[2]);
                                            }
                                            filterParam += " " + filterOperator + " ";
                                        }
                                    }
                                }
                                // remove last filter operator.
                                filterParam = filterParam.substring(0, filterParam.length - filterOperator.length - 2);
                                data.$filter = filterParam;
                                source.totalRecords = 0;
                            }
                            else {
                                if (filterChanged) {
                                    source.totalRecords = 0;
                                    filterChanged = false;
                                }
                            }
                        </#if>
                        data.entityName = '${entityName}';
                        return data;
                    },
                    downloadComplete: function (data, status, xhr) {
                        if (!source.totalRecords) {
                            source.totalRecords = parseInt(data["odata.count"]);
                        }
                    }
                }
            );
            $("#dataTable").jqxDataTable(
            {
                width: ${width},
                pageable: ${pageable},
                pagerButtonsCount: ${viewSize},
                serverProcessing: true,
                source: dataAdapter,
                filterable: ${filterable},
                filterMode: 'simple',
                sortable: ${sortable},
                columnsReorder: true,
                columnsResize: ${columnsResize},
                columns: [
                  ${columnlist}
              ]
            });
        });
    </script>
    <div id="dataTable"></div>
</#macro>

<#macro jqGridMinimumLib>
    <@jqMinimumLib/>
    <script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxdata.js"></script>
    <script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxbuttons.js"></script>
    <script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxscrollbar.js"></script>
    <script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxlistbox.js"></script>
    <script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxdropdownlist.js"></script>
    <script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxpanel.js"></script>
    <script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxcalendar.js"></script>
    <script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxdatetimeinput.js"></script>
    <script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxdata.export.js"></script>
    <script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxcheckbox.js"></script>
    <script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxnotification2.js"></script>
    <script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxdropdownbutton.js"></script>
    <script type="text/javascript" src="/aceadmin/jqw/jqwidgets/globalization/globalize.js"></script>
    <script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxwindow.js"></script>
    <script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxnumberinput.js"></script>
    <script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxinput.js"></script>
    <script type="text/javascript" src="/aceadmin/jqw/demos/jqxgrid/localization.js"></script>
    <script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxgrid2.full.js"></script>
    <script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxgrid.filter2.full.js"></script>
    <script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxgrid.sort.js"></script>
    <script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxgrid.selection.js"></script>
    <script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxgrid.export.js"></script>
    <script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxgrid.aggregates.js"></script>
    <script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxgrid.columnsreorder.js"></script>
    <script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxgrid.columnsresize.js"></script>
    <script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxgrid.grouping.js"></script>
    <script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxgrid.pager.js"></script>
    <script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxgrid.edit2.full.js"></script>
    <script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxmenu.js"></script>
    <script type="text/javascript" src="/aceadmin/assets/js/bootbox.min.js"></script>
</#macro>
<#macro useLocalizationNumberFunction>
<script type="text/javascript">
    var convertLocalNumber = function(num){
        if(num == null){
            return "";
        }
        decimalseparator = ".";
        thousandsseparator = ",";
        if("${locale}" == "vi"){
            decimalseparator = ",";
            thousandsseparator = ".";
        }
        var str = num.toString(), parts = false, output = [], i = 1, formatted = null;
        if(str.indexOf(".") > 0) {
            parts = str.split(".");
            str = parts[0];
        }
        str = str.split("").reverse();
        for(var j = 0, len = str.length; j < len; j++) {
            if(str[j] != ",") {
                output.push(str[j]);
                if(i%3 == 0 && j < (len - 1)) {
                    output.push(thousandsseparator);
                }
                i++;
            }
        }
        formatted = output.reverse().join("");
        return(formatted + ((parts) ? decimalseparator + parts[1].substr(0, 6) : ""));
    }
</script>
</#macro>

<#macro jqGrid url columnlist dataField initrowdetailsDetail="" isShowTitleProperty="true" customTitleProperties="" columngrouplist="" updaterow="" addColumns="" deleterow="" addrow="" updateUrl="" createUrl=""
               removeUrl="" showtoolbar="true" addmultiplerows="false" updateMulUrl="" updatemultiplerows="" updateoffline="false" offlinerefreshbutton="true" deleteConditionFunction=""
               excelExport="false" toPrint="false" filterbutton="" clearfilteringbutton="false" noConditionFind="N" otherCondition="" conditionsFind="N" doubleClick="false" dictionaryColumns="" groupable="false"
               primaryColumn="ID" editable="false" editColumns="" id="jqxgrid" dataType="json" filterable="true" filtersimplemode="true" viewSize="15" viewIndex="0" width="500" height="" autorowheight="false"
               pageable="true" columnsresize="true" columnsreorder="true" sortable="true" defaultSortColumn="" autoheight="true" currencySymbol="d" selectionmode="singlerow" addrefresh="false"
               showstatusbar="false" editpopup="false" initrowdetails="false" keyvalue="" deleteColumn="" addinitvalue="" addType="direct" entityName="" groups="" alternativeAddPopup=""
               editmode="selectedrow" jqGridMinimumLibEnable="true" otherParams="" sortdirection="asc" altrows="false" sourceId="Id" rowselectfunction="" bindingcompletefunction="" rowunselectfunction="" idExisted="false"
               ulistname="" updatelist="false"  editrefresh="false" groupsexpanded="false" pagesizeoptions="['5', '10', '15', '20', '25', '30', '50', '100']" contextMenuId="contextMenuId" mouseRightMenu="false"
               usecurrencyfunction="true" deletesuccessfunction="" autoload="true" statusbarheight="50" dropdownlist="false" ddlSource="" displayMember="" valueMember="" sendEmail="false" changeState="false"
               bindresize="true" customcontrol1="" customcontrol2="" customcontrol3=""  customtoolbaraction=""  showlist="true" extraUrl="" offmode="false" allGridMenu="false" rowsheight="30" functionAfterUpdate="" functionAfterAddRow=""
               customLoadFunction="false" rowdetailsheight="200" readyFunction="" rowdetailstemplateAdvance="" deletelocal="false" enablemousewheel="true" customCss="" functionAfterRowComplete=""
               exceptFieldToCompare="" confirmEditFunction="" scrollmode="deferred" updateRowFunction="" afterinitfunction="" deleteConditionMessage="" autoshowloadelement="true" showdefaultloadelement="true"
               beforeprocessing="" customControlAdvance="" autoMeasureHeight='false'>
   <#if jqGridMinimumLibEnable=="true">
    <@jqGridMinimumLib/>
   </#if>
   <#include "jqwLocalization.ftl"/>
    <script type="text/javascript">
        var wgdeletesuccess = "${StringUtil.wrapString(uiLabelMap.wgdeletesuccess)}";
        var wgupdatesuccess = "${StringUtil.wrapString(uiLabelMap.wgupdatesuccess)}";
        var wgaddsuccess = "${StringUtil.wrapString(uiLabelMap.wgaddsuccess)}";
        function updateGridMessage(id, template, message){
            $('#container' + id).empty();
            $('#jqxNotification' + id).jqxNotification({ template: template});
            $("#notificationContent" + id).text(message);
            $("#jqxNotification" + id).jqxNotification("open");
        }
        function displayEditSuccessMessage(id){
            $('#container' + id).empty();
            $('#jqxNotification' + id).jqxNotification({ template: 'success'});
            $("#notificationContent" + id).text(wgupdatesuccess);
            $("#jqxNotification" + id).jqxNotification("open");
        }
        function displayDeleteSuccessMessage(id){
            $('#container' + id).empty();
            $('#jqxNotification' + id).jqxNotification({ template: 'success'});
            $("#notificationContent" + id).text(wgdeletesuccess);
            $("#jqxNotification" + id).jqxNotification("open");
        }
        function CallbackFocusFilter(){
		var previousId = localStorage.getItem('previousInputFilter');
		$("#" + previousId).find('input').focus();
        }
        function AutoMeasureGridHeight(grid){
		var x = Math.abs($('#page-content').innerHeight() - $('#page-content').height());
		var tmpheight = $(window).height() - $('#nav').height() - $('.breadcrumb-inner').height() - x - 10;
		grid.jqxGrid({ height: tmpheight });
        }
    </script>
    <script type="text/javascript">
        var tmpEditable = false;
        var editPending = false;
        $.jqx.theme = 'olbius';
        theme = $.jqx.theme;
        <#if usecurrencyfunction=="true">
            var formatcurrency = function(num, uom){
                if(num == null){
                    return "";
                }
                decimalseparator = ",";
                thousandsseparator = ".";
                currencysymbol = "d";
                if(typeof(uom) == "undefined" || uom == null){
                    uom = "${defaultOrganizationPartyCurrencyUomId?if_exists}";
                }
                if(uom == "USD"){
                    currencysymbol = "$";
                    decimalseparator = ".";
                    thousandsseparator = ",";
                }else if(uom == "EUR"){
                    currencysymbol = "€";
                    decimalseparator = ".";
                    thousandsseparator = ",";
                }
                var str = num.toString().replace(currencysymbol, ""), parts = false, output = [], i = 1, formatted = null;
                if(str.indexOf(".") > 0) {
                    parts = str.split(".");
                    str = parts[0];
                }
                str = str.split("").reverse();
                for(var j = 0, len = str.length; j < len; j++) {
                    if(str[j] != ",") {
                        output.push(str[j]);
                        if(i%3 == 0 && j < (len - 1)) {
                            output.push(thousandsseparator);
                        }
                        i++;
                    }
                }
                formatted = output.reverse().join("");
                return(formatted + ((parts) ? decimalseparator + parts[1].substr(0, 2) : "") + "&nbsp;" + currencysymbol);
            };
        </#if>
        var deleteRow${id} = function(url, data, commit){
            $('#${id}').jqxGrid('showloadelement');
            $.ajax({
                type: "POST",
                url: url,
                data:  data,
                <#if updateoffline == "true">
                async: false,
                </#if>
                success: function (data, status, xhr) {
                    if(data.responseMessage == "error"){
                        commit(false);
                        $('#container${id}').empty();
                        $('#jqxNotification${id}').jqxNotification({ template: 'error'});
                        $("#notificationContent${id}").text(data.errorMessage);
                        $("#jqxNotification${id}").jqxNotification("open");
                    }else{
                        if(commit){
                            commit(true);
                        }
                        $('#container${id}').empty();
                        $('#${id}').jqxGrid('updatebounddata');
                        $('#jqxNotification${id}').jqxNotification({ template: 'success'});
                        $("#notificationContent${id}").text(wgdeletesuccess);
                        $("#jqxNotification${id}").jqxNotification({icon: { width: 25, height: 25, url: '/aceadmin/assets/images/info.jpg'}});
                        $("#jqxNotification${id}").jqxNotification("open");
                        <#if deletesuccessfunction?has_content>
                            ${deletesuccessfunction}
                        </#if>
                    }
                }, error: function () {
                    if(commit){
                        commit(false);
                    }
                },complete: function(){
                    $('#${id}').jqxGrid('hideloadelement');
                }
            });
        };
        var updateRow${id} = function(url, data, commit){
            $('#${id}').jqxGrid('showloadelement');
            $.ajax({
                type: "POST",
                url: url,
                data: data,
                <#if updateoffline == "true">
                async: false,
                </#if>
                success: function (data, status, xhr) {
                    // update command is executed.
                    if(data.responseMessage == "error"){
                        if(commit){commit(false)}
                        <#if updateoffline != "true">
                            $('#${id}').jqxGrid('updatebounddata');
                        </#if>
                        $('#container${id}').empty();
                        $('#jqxNotification${id}').jqxNotification({ template: 'error'});
                        $("#notificationContent${id}").text(data.errorMessage);
                        $("#jqxNotification${id}").jqxNotification("open");
                    }else{
                        if(commit){commit(true)}
                        localStorage.removeItem("localGridUpdate${id}");
                        $('#container${id}').empty();
                        $('#jqxNotification${id}').jqxNotification({ template: 'success'});
                        $("#notificationContent${id}").text(wgupdatesuccess);
                        $("#jqxNotification${id}").jqxNotification("open");
                        <#if editrefresh=="true" && updateoffline != "true">
                            $('#${id}').jqxGrid('updatebounddata');
                        </#if>
                    }
                    <#if functionAfterUpdate != "">
                        ${functionAfterUpdate}
                    </#if>
                },
                error: function () {
                    if(commit){commit(false)}
                },complete: function(){
                    $('#${id}').jqxGrid('hideloadelement');
                }
            });
        };
        var checkDataUpdate = function(data1, data2){
            var arr = JSON.parse(JSON.stringify(data1));
            for(var x in data1){
                var obj = data1[x];
                for(var y in data2){
                    var obj2 = data2[y];
                    var i = 0;
                    var j = 0;
                    for(var z in obj2){
                        if(obj[z].value && obj[z].value == obj2[z].value){
                            i++;
                        }
                        j++;
                    }
                    if(i == j){
                        arr.splice(x, 1);
                    }
                }
            }
            return arr;
        };
        var clearData = function(){
		localStorage.removeItem('previousInputFilter');
            localStorage.removeItem("localGridUpdate${id}");
            localStorage.removeItem("localGridDelete${id}");
            localStorage.removeItem("localGridCreate${id}");
        };

        function getFieldType(fName){
            for (i=0;i < ${dataField}.length;i++) {
               if(${dataField}[i]['name'] == fName){
                    if(!(typeof ${dataField}[i]['other'] === 'undefined' || ${dataField}[i]['other'] =="")){
                        return  ${dataField}[i]['other'];
                    }else{
                        return  ${dataField}[i]['type'];
                    }

               }
            }
        }
        var combobox = [];
        var dropdownGrid = [];
        <#if customLoadFunction == "true">
            function initGrid${id}(){
                if($("#${id}").is('*[class^="jqx"]')){
                    return;
                }
        <#else>
            $(document).ready(function () {
        </#if>
            var culture="en";
            var oldEditingValue;
            var source${id} =
            {
                dataType: '${dataType}',
                dataFields:
                    ${dataField}
                ,
                id: '${sourceId}',
                type: 'POST',
                cache: false,
                data: {
                    noConditionFind: '${noConditionFind}',
                    conditionsFind: '${conditionsFind}',
                    dictionaryColumns: '${dictionaryColumns}',
                    otherCondition: '${otherCondition}'
                },
                contentType: 'application/x-www-form-urlencoded',
                url: '${url}'<#if extraUrl != ""> + ${extraUrl}</#if>,
                beforeprocessing: function (data) {
                    source${id}.totalrecords = data.TotalRows;
                    <#if beforeprocessing == "">
                    if(parseInt(data.TotalRows) <= 0){
			$("#${id}").jqxGrid({
				height: "",
				autoheight: "true"
			});
                    }
                    <#else>
			var x = ${beforeprocessing};
			if(typeof(x) == 'function'){
				x();
			}
                    </#if>
                },
                <#if filterable=="true">
                filter: function () {
                    // update the grid and send a request to the server.
                    $("#${id}").jqxGrid('updatebounddata');
                },
                </#if>
                <#if sortable=="true">
                sort: function () {
                    // update the grid and send a request to the server.
                    $("#${id}").jqxGrid('updatebounddata');
                },
                sortcolumn: '${defaultSortColumn}',
                sortdirection: '${sortdirection}',
                </#if>
                <#if pageable=="true">
                pagenum: ${viewIndex},
                pagesize: ${viewSize},
                entityName: '${entityName}',
                pager: function (pagenum, pagesize, oldpagenum) {
                    // callback called when a page or page size is changed.
                },
                </#if>

                <#if (editable=="true" || updaterow=="true" || doubleClick=="true" || updatemultiplerows=="true" || editColumns!="") && (updateRowFunction == "") >
                updaterow: function (rowid, rowdata, commit) {
                    $("#${id}").jqxGrid({ disabled: true});
                    beginEdit = true;
                    for(var n in rowdata){
                        var tmpExisted = $('#${id}').jqxGrid('getcolumnindex', n);
                        if(tmpExisted != -1){
				var column = $('#${id}').jqxGrid('getcolumnproperty', n, 'columntype');
	                    if(column == 'combobox' && combobox.length){
				rowdata[n] = JSON.stringify(combobox);
				break;
	                    }
	                    var infoCl;
	                    if(localStorage.getItem('infoColumnDetail')){
				infoCl = $.parseJSON(localStorage.getItem('infoColumnDetail'));
	                    }
	                    if(infoCl){
				if(column == 'custom' && dropdownGrid && infoCl.gridname == '${id?if_exists}' && infoCl.field == 'paymentId' && infoCl.columntype == 'custom' && infoCl.type == 'dropdownGrid'){
					rowdata[n] = dropdownGrid.paymentId ? dropdownGrid.paymentId : null;
					break;
				}
	                    }
                        }
                    }
                    if(tmpEditable){
                        commit(false);
                        $("#${id}").jqxGrid({ disabled: false});
                        return;
                    }
                    var tmpOlFlag = true;
                    var strExceptFieldToCompare = '';
                    <#if exceptFieldToCompare != "">
                        strExceptFieldToCompare = ";${exceptFieldToCompare};";
                    </#if>
                    if(oldEditingValue != undefined && oldEditingValue != null){
			for (var key in rowdata) {
			    if(strExceptFieldToCompare != '' && strExceptFieldToCompare.indexOf(';' + key + ';') > -1){
			        continue;
			    }
                            if((rowdata[key] == undefined && oldEditingValue[key] != undefined) || (rowdata[key] != undefined && oldEditingValue[key] == undefined)){
                                tmpOlFlag = false;
                                break;
                            }
                            if(rowdata[key] != oldEditingValue[key]){
                                tmpOlFlag = false;
                                break;
                            }
                        }
                    }else{
			tmpOlFlag = false;
                    }
                    if(tmpOlFlag){
                        commit(false);
                        $("#${id}").jqxGrid({ disabled: false});
                        return false;
                    }
                    <#if confirmEditFunction != "">
                        if(!editPending){
                            ${confirmEditFunction}
                            commit(false);
                            $("#${id}").jqxGrid({ disabled: false});
                            editPending = true;
                            return false;
                        }
                        editPending = false;
                    </#if>
                    // Split data to submit to server
                    data = "";
                    urlStatus = "";
                    var keysData = Object.keys(rowdata).toString();
                    data = "rl=1&";
                    <#if updatelist!="false">
                        data = "rl=" + rowid.length + "&"; // record length
                        data += "updatelist=true&";
                        <#if ulistname!="">
                            data += "ulistname=${ulistname}&";
                        </#if>
                    </#if>
                    <#if updateoffline=="true">
                        var local = {};
                    </#if>
                    if (keysData.indexOf("oldValue") < 0)
                    {
                        var columnList = '${editColumns}';

                        var arrKeysData = "";
                        if(typeof(columnList) == 'function'){
				var tmp = columnList();
				arrKeysData = tmp.split(";")
                        }else{
				arrKeysData = columnList.split(";")
                        }
                        var rowidlength = 1;
                        if(!(rowid instanceof Array)){
                            rowid = [rowid];
                        }
                        if(!(rowdata instanceof Array)){
                            rowdata = [rowdata];
                        }
                        <#if updateoffline!="true">
                        for(j=0; j < rowid.length;j++){
                            var columnValues ="";
                            for(i = 0; i < arrKeysData.length;i++){
                                if(arrKeysData[i].indexOf("(") > -1){
                                    var tmpStr = arrKeysData[i].substring(0,arrKeysData[i].indexOf("("));
                                    if(rowdata[j][tmpStr]==null){
                                        if(arrKeysData[i].indexOf("[") > -1){
                                            if(arrKeysData[i].indexOf(".Timestamp)") > -1){
                                                var tmstr = arrKeysData[i].substring(arrKeysData[i].indexOf('[') + 1,arrKeysData[i].length - 1);
                                                if(tmstr){
                                                    var tmpdate = new Date(tmstr);
                                                    var tmps = isNaN(tmpdate.getTime()) ? "" : tmpdate.getTime() ;
                                                    columnValues += tmps + "#;";
                                                }else{
                                                    columnValues += "#;";
                                                }

                                            }else{
                                                columnValues += arrKeysData[i].substring(arrKeysData[i].indexOf('[') + 1,arrKeysData[i].length - 1) + "#;";
                                            }
                                        }else{
                                            columnValues += "undefined#;";
                                        }
                                    }else{
                                        if(arrKeysData[i].indexOf(".Timestamp)") > -1){
                                            var tmstr = rowdata[j][tmpStr];
                                            if(tmstr){
                                                var tmpdate = new Date(tmstr);
                                                var tmps = isNaN(tmpdate.getTime()) ? "" : tmpdate.getTime() ;
                                                columnValues += tmps + "#;";
                                            }else{
                                                columnValues += "#;";
                                            }
                                        }else if(arrKeysData[i].indexOf(".Date)") > -1){
						var tmstr = rowdata[j][tmpStr];
                                            if(tmstr){
                                                var tmpdate = new Date(tmstr);
                                                var tmps = isNaN(tmpdate.getTime()) ? "" : tmpdate.getTime() ;
                                                columnValues += tmps + "#;";
                                            }else{
                                                columnValues += "#;";
                                            }
                                            // columnValues += rowdata[j][tmpStr] + "#;";
                                        }else if(arrKeysData[i].indexOf(".Time)") > -1){
						var tmstr = rowdata[j][tmpStr];
                                            if(tmstr){
                                                var tmpdate = new Date(tmstr);
                                                var lo = !isNaN(tmpdate.getTime()) ? tmpdate.getTime() : "";
                                                columnValues += lo + "#;";
                                            }else{
                                                columnValues += "#;";
                                            }
                                            // columnValues += rowdata[j][tmpStr] + "#;";
                                        }else{
                                            columnValues += rowdata[j][tmpStr] + "#;";
                                        }
                                    }
                                }else{
                                    var tmpStr = arrKeysData[i];
                                    if(tmpStr.indexOf("[") > -1){
                                        var tmpStr = tmpStr.substring(0,tmpStr.indexOf("["));
                                    }
                                    if(rowdata[j][tmpStr]==null){
                                        if(arrKeysData[i].indexOf("[") > -1){
                                            columnValues += arrKeysData[i].substring(arrKeysData[i].indexOf('[') + 1,arrKeysData[i].length - 1) + "#;";
                                        }else{
                                            columnValues += "undefined#;";
                                        }
                                    }else{
                                        columnValues += rowdata[j][tmpStr] + "#;";
                                    }
                                }
                            };
                            if(j==0){
                                data += "columnList" + j + "=";
                            }else{
                                data += "&columnList" + j + "=";
                            }
                            var arrColumnData = columnList.split(";");
                            var ilength = arrColumnData.length;

                            for(i = 0; i < ilength;i++){
                                var strTmp;
                                if(arrColumnData[i].indexOf("[") > -1){
                                    strTmp = arrColumnData[i].substring(0,arrColumnData[i].indexOf("["));
                                }else{
                                    strTmp = arrColumnData[i];
                                }
                                data += strTmp + ";";
                            }
                            data += "&" + "columnValues" + j + "=" +  columnValues;
                        }
                        data +=  "&" + "primaryColumn" + "=" + '${primaryColumn}';
                        data +=  "&" +  "entityName" + "=" + '${entityName}';
                        urlStatus = '${updateUrl}';
                        <#else>
                        for(j=0; j < rowid.length;j++){
                            for(i = 0; i < arrKeysData.length;i++){
                                if(arrKeysData[i].indexOf("(") > -1){
                                    var tmpStr = arrKeysData[i].substring(0,arrKeysData[i].indexOf("("));
                                    if(arrKeysData[i].indexOf(".Timestamp)") > -1){
                                        var tmstr = rowdata[j][tmpStr];
                                        if(tmstr){
                                            var tmpdate = new Date(tmstr);
                                            var tmps = isNaN(tmpdate.getTime()) ? "" : tmpdate.getTime() ;
                                            local[arrKeysData[i]] = {
                                                    value : tmps,
                                                    type: arrKeysData[i]
                                                };
                                        }else{
                                            local[arrKeysData[i]] = {
                                                    value : "",
                                                    type: arrKeysData[i]
                                                };
                                        }
                                    }else if(arrKeysData[i].indexOf(".Date)") > -1){
					var tmstr = rowdata[j][tmpStr];
                                        if(tmstr){
                                            var tmpdate = new Date(tmstr);
                                            var tmps = isNaN(tmpdate.getTime()) ? "" : tmpdate.getTime() ;
                                            local[arrKeysData[i]] = {
                                                    value : tmps,
                                                    type: arrKeysData[i]
                                                };
                                        }else{
                                            local[arrKeysData[i]] = {
                                                    value : "",
                                                    type: arrKeysData[i]
                                                };
                                        }
                                    }else{
                                        local[tmpStr] = {
                                            value : rowdata[j][tmpStr],
                                            type: arrKeysData[i]
                                        };
                                    }
                                }else{
                                    var tmpStr = arrKeysData[i];
                                    if(rowdata[j][tmpStr]==null){
                                        if(arrKeysData[i].indexOf("[") > -1){
                                            local[tmpStr] = {
                                                value : arrKeysData[i].substring(arrKeysData[i].indexOf('[') + 1,arrKeysData[i].length - 1)
                                            };
                                        }
                                    }else{
                                        local[tmpStr] = {
                                            value : rowdata[j][tmpStr]
                                        };
                                    }
                                }
                            };
                        }
                        </#if>
                    }
                    else
                    {
                        var arrKeysData = keysData.split(",");
                        if(arrKeysData.length > 0)
                        {
                            data = arrKeysData[0] + "=" + rowdata[arrKeysData[0]];
                        }
                        for(i = 1; i < arrKeysData.length ;i++){
                            if(rowdata[arrKeysData[i]]){
                                data  = data + "&" + arrKeysData[i] + "=" + rowdata[arrKeysData[i]] ;
                            }
                        };
                        data = data +  "&" + "primaryColumn" + "=" + '${primaryColumn}';
                        data = data +  "&" +  "entityName" + "=" + '${entityName}';
                        urlStatus = '${updateMulUrl}';
                    }
                    // End of spliting data
                    <#if updateoffline != "true">
                    if (urlStatus && !(/^\s*$/.test(urlStatus))) {
                        updateRow${id}(urlStatus, data, commit);
                    }
                    <#else>
                        var localData = $.parseJSON(localStorage.getItem('localGridUpdate${id}'));
                        if(localData){
                            localData.push(local);
                            localStorage.setItem("localGridUpdate${id}", JSON.stringify(localData));
                        }else{
                            localData = new Array();
                            localData.push(local);
                            localStorage.setItem("localGridUpdate${id}", JSON.stringify(localData));
                        }
                        commit(true);
                    </#if>
                    $("#${id}").jqxGrid({ disabled: false});
                },
                <#elseif updateRowFunction != "" && editable=="true">
			updateRow: ${updateRowFunction},
                </#if>
                <#if addrow=="true">
                addrow: function (rowid, rowdata, position, commit) {
                    $("#${id}").jqxGrid({ disabled: true});
                    data = "";
                    var keysData = Object.keys(rowdata).toString();
                    var arrKeysData = keysData.split(",");
                    var addColumns = "${addColumns}".split(";");
                    var tmpAddclm = "";
                    var spl = "";
                    var splCol = "";
                    data = "columnValues=";
                    for(i=0;i<addColumns.length;i++){
			if(i != 0){
				spl = "#;";
				splCol = ";";
			}
                        tmpKey = addColumns[i];
                        if(addColumns[i].indexOf('(') > -1){
                            tmpKey = addColumns[i].substring(0,addColumns[i].indexOf('('));
                        }else if(addColumns[i].indexOf('[') > -1){
                            tmpKey = addColumns[i].substring(0,addColumns[i].indexOf('['));
                        }
                        if(addColumns[i].indexOf('[') > -1){
                            if(addColumns[i].indexOf(".Timestamp)") > -1 || addColumns[i].indexOf(".Date)") > -1){
                                var tmstr = addColumns[i].substring(addColumns[i].indexOf('[') + 1,addColumns[i].length - 1);
                                if(tmstr){
                                    var tmpdate = new Date(tmstr);
                                    var tmps = isNaN(tmpdate.getTime()) ? "" : tmpdate.getTime() ;
                                    data += spl + tmps;
                                }else{
                                    data += spl;
                                }
                            }else{
                                data += spl + addColumns[i].substring(addColumns[i].indexOf('[') + 1,addColumns[i].length - 1);
                            }
                        }else{
                            if(addColumns[i].indexOf(".Timestamp)") > -1 || addColumns[i].indexOf(".Date)") > -1){
                                var tmstr = rowdata[tmpKey];
                                if(tmstr){
                                    var tmpdate = new Date(tmstr);
                                    var tmps = isNaN(tmpdate.getTime()) ? "" :  tmpdate.getTime();
                                    data += spl + tmps;
                                }else {
                                    data += spl;
                                }
                            }else{
                                data += spl + rowdata[tmpKey];
                            }
                        }
                        if(addColumns[i].indexOf('[') > -1){
                            tmpAddclm += splCol + addColumns[i].substring(0,addColumns[i].indexOf('['));
                        }else{
                            tmpAddclm += splCol + addColumns[i];
                        }
                    }
                    data += "&" +  "columnList" + "=" + tmpAddclm;
                    // End of spliting data
                    $('#${id}').jqxGrid('showloadelement');
                    $.ajax({
                        type: "POST",
                        url: '${createUrl}',
                        data: data,
                        success: function (data, status, xhr) {
                            if(data.responseMessage == 'error'){
                                commit(false);
                                $('#container${id}').empty();
                                $('#jqxNotification${id}').jqxNotification({ template: 'error'});
                                $("#notificationContent${id}").text(data.errorMessage);
                                $("#jqxNotification${id}").jqxNotification("open");
                            }else{
                            $('#${id}').trigger('myEvent',JSON.stringify(data));
                                // update command is executed.
                                var keysDataTmp = Object.keys(data.results);
                                for(i=0; i < keysDataTmp.length;i++){
                                    rowdata[keysDataTmp[i]] = data.results[keysDataTmp[i]];
                                }
                                commit(true);
                                $('#container${id}').empty();
                                $('#jqxNotification${id}').jqxNotification({ template: 'success'});
                                $("#notificationContent${id}").text(wgaddsuccess);
                                $("#jqxNotification${id}").jqxNotification("open");
                                <#if addrefresh=="true">
                                    $('#${id}').jqxGrid('updatebounddata');
                                </#if>
                                <#if functionAfterAddRow != "">
                                    ${functionAfterAddRow}
                                </#if>
                            }
                        },
                        error: function () {
                            commit(false);
                        },
                        complete: function(){
                            $('#${id}').jqxGrid('hideloadelement');
				<#if functionAfterRowComplete != "">
					${functionAfterRowComplete}
				</#if>
                        }
                    });
                    $("#${id}").jqxGrid({ disabled: false});
                },
                </#if>
                <#if deleterow=="true">
                deleterow: function (rowid, commit) {
                    $("#${id}").jqxGrid({ disabled: false});
                    var dataRecord = $('#${id}').jqxGrid('getrowdatabyid', rowid);
                    var dcl = "${deleteColumn}".split(";");
                    <#if deletelocal!="true">
                    var data = "";
                    var tmpValue = "&columnValues=";
                    if(dcl[0].indexOf('[') > -1){
                        tmpStr = dcl[0].substring(0, dcl[0].indexOf('['));
                        tmpValue += dcl[0].substring(dcl[0].indexOf('[') + 1,dcl[0].length - 1);
                        data = "columnList" + "=" + tmpStr;
                    }else{
                        tmpStr = dcl[0];
                        if(dcl[0].indexOf("(") > -1){
                            tmpStr = dcl[0].substring(0, dcl[0].indexOf('('));
                            var tmpD = new Date(dataRecord[tmpStr]);
                            tmpStr = tmpD.getTime() + "";
                            tmpValue += tmpStr.substring(0, tmpStr.length - 3);
                            data = "columnList" + "=" + dcl[0];
                        }else{
                            tmpValue += dataRecord[tmpStr];
                            data = "columnList" + "=" + dcl[0];
                        }
                    }
                    for(i=1; i < dcl.length;i++){
                        if(dcl[i].indexOf('[') > -1){
                            tmpStr = dcl[i].substring(0, dcl[i].indexOf('['));
                            data += ";" + tmpStr;
                            if(tmpStr.indexOf("(") > -1){
                                tmpStr = dcl[i].substring(0, dcl[i].indexOf('('));
                            }
                            tmpValue += "#;" + dcl[i].substring(dcl[i].indexOf('[') + 1,dcl[i].length - 1);
                        }else{
                            tmpStr = dcl[i];
                            if(dcl[i].indexOf("(") > -1){
                                tmpStr = dcl[i].substring(0, dcl[i].indexOf('('));
                                data += ";" + dcl[i];
                                var tmpD = new Date(dataRecord[tmpStr]);
                                tmpStr = tmpD.getTime() + "";
                                tmpValue += "#;" + tmpStr;
                            }else{
                                data += ";" + dcl[i];
                                tmpValue += "#;" + dataRecord[tmpStr];
                            }
                        }
                    }
                    data += tmpValue;
                    deleteRow${id}("${removeUrl}", data, commit);
                    <#else>
                        var local = {};
                        for(var i = 0; i < dcl.length;i++){
                            if(dcl[i].indexOf('[') > -1){
                                tmpStr = dcl[i].substring(0, dcl[i].indexOf('['));
                                if(tmpStr.indexOf("(") > -1){
                                    tmpStr = dcl[i].substring(0, dcl[i].indexOf('('));
                                }
                                local[tmpStr] = {
                                    value : dcl[i].substring(dcl[i].indexOf('[') + 1,dcl[i].length - 1),
                                    type: dcl[i]
                                };
                            }else{
                                tmpStr = dcl[i];
                                if(dcl[i].indexOf(".Timestamp") > -1){
                                    tmpStr = dcl[i].substring(0, dcl[i].indexOf('('));
                                    var tmpD = new Date(dataRecord[tmpStr]);
                                    local[tmpStr] = {
                                        value : tmpD.getTime(),
                                        type: dcl[i]
                                    };
                                }else{
                                    local[tmpStr] = {
                                        value : dataRecord[tmpStr]
                                    };
                                }
                            }
                        }
                        var localData = $.parseJSON(localStorage.getItem("localGridDelete${id}"));
                        if(localData){
                            localData.push(local);
                            localStorage.setItem("localGridDelete${id}", JSON.stringify(localData));
                        }else{
                            localData = new Array();
                            localData.push(local);
                            localStorage.setItem("localGridDelete${id}", JSON.stringify(localData));
                        }
                        commit(true);
                    </#if>
                    $("#${id}").jqxGrid({ disabled: false});
                },
                </#if>
                root: 'results'
            };
            <#--
            <#if (editable=="true" && editpopup=="true") || doubleClick=="true" || updaterow=="true">

            <#list "${editColumns}"?split(";") as eColumn>
                    $("#${eColumn}").jqxInput({ theme: theme });
                    $("#${eColumn}").width(250);
                    $("#${eColumn}").height(23);
            </#list>

            </#if> -->

             <#if updatemultiplerows=="true">
                $("#newValueUpdate").jqxInput({ theme: theme });
                $("#newValueUpdate").width(250);
                $("#newValueUpdate").height(23);
            </#if>
             var dataadapter${id} = new $.jqx.dataAdapter(source${id}, {
                formatData: function (data) {
                    if((typeof outFilterCondition === 'undefined' || outFilterCondition =="") && (typeof alterData === 'undefined' || alterData =="" || $.isEmptyObject(alterData)))
                    {
                        <#if filterable=="true">
                            if (data.filterscount) {
                                var filterListFields = "";
                                var tmpFieldName = "";
                                for (var i = 0; i < data.filterscount; i++) {
                                    var filterValue = data["filtervalue" + i];
                                    if(!filterValue){continue;}
                                    var filterCondition = data["filtercondition" + i];
                                    var filterDataField = data["filterdatafield" + i];
                                    var filterOperator = data["filteroperator" + i];
                                    if(getFieldType(filterDataField)=='number'){
                                        filterListFields += "|OLBIUS|" + filterDataField + "(BigDecimal)";
                                    }else if(getFieldType(filterDataField)=='date'){
                                        filterListFields += "|OLBIUS|" + filterDataField + "(Date)";
                                    }else if(getFieldType(filterDataField)=='Timestamp'){
                                        filterListFields += "|OLBIUS|" + filterDataField + "(Timestamp)[dd/MM/yyyy hh:mm:ss aa]";
                                    }
                                    else{
                                        filterListFields += "|OLBIUS|" + filterDataField;
                                    }
                                    if(getFieldType(filterDataField)=='Timestamp'){
                                        if(tmpFieldName != filterDataField){
                                            filterListFields += "|SUIBLO|" + filterValue + " 00:00:00 am";
                                        }else{
                                            filterListFields += "|SUIBLO|" + filterValue + " 11:59:59 pm";
                                        }
                                    }else{
                                        filterListFields += "|SUIBLO|" + filterValue;
                                    }
                                    filterListFields += "|SUIBLO|" + filterCondition;
                                    filterListFields += "|SUIBLO|" + filterOperator;
                                    tmpFieldName = filterDataField;
                                }
                                data.filterListFields = filterListFields;
                            }
                        </#if>
                    }else if(!(typeof alterData === 'undefined' || alterData =="")){
                        var tmppn = data.pagenum;
                        data = alterData;
                        data.pagenum = tmppn;
                    }else{
                        data.filterListFields = outFilterCondition;
                        outFilterCondition = "";
                    }
                    <#if otherParams!="">
                        data.otherParams = "${otherParams}";
                    <#else>
                        data.otherParams = null;
                    </#if>
                    data.$skip = data.pagenum * data.pagesize;
                    data.$top = data.pagesize;
                    data.$inlinecount = "allpages";
                    return data;
                },
                loadError: function (xhr, status, error) {
                    // FIXME Consider to remove this action
                    // alert(error);
                    console.log(error);
                },
                downloadComplete: function (data, status, xhr) {
                    /*if (!source${id}.totalRecords) {
                        source${id}.totalRecords = parseInt(data["odata.count"]);
                    };   */
                },
                beforeLoadComplete: function (records) {
                    for (var i = 0; i < records.length; i++) {
                        if(typeof(records[i])=="object"){
                            for(var key in records[i]) {
                                var value = records[i][key];
                                if(value != null && typeof(value) == "object" && typeof(value) != null){
                                    //var date = new Date(records[i][key]["time"]);
                                    //records[i][key] = date;
                                }

                            }
                        }
                    }
                }
            });
            //var editrow = -1;
            clearData();
            /*init jqxgrid */
            $("#${id}").jqxGrid(
            {
                source: dataadapter${id},
                columnsheight: 30,
                filterable: ${filterable},
                autoshowfiltericon: true,
                showdefaultloadelement:${showdefaultloadelement},
                autoshowloadelement:${autoshowloadelement},
                pagesizeoptions: ${pagesizeoptions},
                autoheight:${autoheight},
                editable: ${editable},
                rowsheight: ${rowsheight},
                autorowheight: ${autorowheight},
                localization: getLocalization(),
                altrows: ${altrows},
                groupable: ${groupable},
                <#if groupable == "true" && groups != "">
                    groups:['${groups}'],
                </#if>
                <#if editable=="true">
                    editmode:'${editmode}',
                </#if>
                selectionmode: '${selectionmode}',
                ready: function(){
                    <#if autoload=="false">
                        $("#${id}").jqxGrid("clear");
                    </#if>
                    <#if readyFunction!="">
                        ${readyFunction}
                    </#if>
                },
                width: '${width}',

                handlekeyboardnavigation: function (event) {
                    var key = event.charCode ? event.charCode : event.keyCode ? event.keyCode : 0;
                    // F2
                    if ("${updaterow}"=="true")
                    if (key == 113) {
                        $("#updaterowbutton").click();
                        return true;
                    };
                    // F8
                    if ("${deleterow}" == "true")
                    if (key == 119) {
                        $("#deleterowbutton").click();
                        return true;
                    };
                    // F5
                    if ("${filterbutton}" == "true")
                    if (key == 116) {
                        $("#filterbutton").click();
                        return true;
                    };
                    // Ctrl + F
                    if ("${clearfilteringbutton}" == "true")
                    if (key == 70 && event.ctrlKey) {
                        $('#clearfilteringbutton${id}').click();
                        return true;
                    }

                    // Ctrl + I
                    if ("${addrow}" == "true")
                    if (key == 73 && event.ctrlKey) {
                        $('#addrowbutton${id}').click();
                        return true;
                    }
                    // F9
                    if ("${updatemultiplerows}" == "true")
                    if (key == 120) {
                        $('#updatemultiplerows').click();
                        return true;
                    }
                   // F7
                   if ("${toPrint}"=="true")
                    if (key == 118) {
                        $('#print').click();
                        return true;
                    }
                   // Ctrl + E
                    if ("${excelExport}"=="true")
                    if (key == 69 && event.ctrlKey) {
                        $('#excelExport').click();
                        return true;
                    }
                },

                <#if showtoolbar=="true">
                showtoolbar: true,
                rendertoolbar: function (toolbar) {
                    var me = this;
                    <#if titleProperty?has_content && isShowTitleProperty=="true" && customTitleProperties == "">
                        <@renderJqxTitle titlePropertyTmp=titleProperty id=id/>
                    <#elseif isShowTitleProperty=="true" && customTitleProperties != "">
                        <@renderJqxTitle titlePropertyTmp=customTitleProperties id=id/>
                    <#else>
                        var jqxheader = $("<div id='toolbarcontainer${id}' class='widget-header'><h4>" + "</h4><div id='toolbarButtonContainer${id}' class='pull-right'></div></div>");
                    </#if>
                    toolbar.append(jqxheader);
                    var container = $('#toolbarButtonContainer${id}');
                    var maincontainer = $("#toolbarcontainer${id}");
                    <#if showlist != "false">
                        (function(){
                            <#if showlist == "true">
				var columns = $("#${id}").jqxGrid('columns');
				if(typeof(columns) == 'undefined'){
					return;
				}
                                var allFields = columns.records;
                                var strSList = [];
                                for(i = 0; allFields != undefined && i < allFields.length;i++){
                                    strSList[i] = allFields[i].datafield;
                                }
                            <#else>
                                var strSList = "${StringUtil.wrapString(showlist)}".split(";");
                            </#if>
                            var strNList = [];
                            for(i=0; i < strSList.length;i++){
                                strNList[i] = $('<textarea />').html($('#${id}').jqxGrid('getcolumn', strSList[i]).text).text();
                            }
                            var listSource = [];
                            for(i=0; i < strSList.length;i++){
                                var tmpVL = {label: strNList[i], value: strSList[i], checked: true};
                                listSource[i] = tmpVL;
                            }
                            $("#showSL${id}").jqxDropDownList({ checkboxes: true, source: listSource,autoDropDownHeight : true ,displayMember: "label", valueMember: "value"});
                            $("#frozenSL${id}").jqxDropDownList({ checkboxes: true, source: listSource,autoDropDownHeight : true, displayMember: "label", valueMember: "value"});
                            $("#frozenSL${id}").jqxDropDownList('uncheckAll');
                            container.append('<button id="btngridsetting${id}" class="btn btn-mini btngridsetting" onclick="openJqxConfigWindow${id}();"><i class="fa-cogs"></i></button>');
                            // init window
                            $("#jqxconfig${id}").jqxWindow({
                                width: 400, height: 180, resizable: false, isModal: true, autoOpen: false, cancelButton: $("#configCancel${id}"), modalOpacity: 0.7, modalZIndex: 10000
                            });

                            $("#configSave${id}").click(function () {
                                var i = 0;
                                var dislayList = $("#showSL${id}").jqxDropDownList('getCheckedItems');
                                var frozenList = $("#frozenSL${id}").jqxDropDownList('getCheckedItems');
                                var dislayListValue = [];
                                var frozenListValue = [];
                                for(i = 0; i < dislayList.length; i++){
                                    dislayListValue[i] = dislayList[i].value;
                                }
                                for(i = 0; i < strSList.length; i++){
                                    if(dislayListValue.indexOf(strSList[i]) < 0){
                                        $("#${id}").jqxGrid('hidecolumn', strSList[i]);
                                    }else{
                                        if(!$('#${id}').jqxGrid('iscolumnvisible', strSList[i])){
                                            $("#${id}").jqxGrid('showcolumn', strSList[i]);
                                        }
                                    }
                                }
                                for(i = 0; i < frozenList.length; i++){
                                    frozenListValue[i] = frozenList[i].value;
                                }
                                for(i = 0; i < strSList.length; i++){
                                    if(frozenListValue.indexOf(strSList[i]) > -1){
                                        $("#${id}").jqxGrid('pincolumn', strSList[i]);
                                    }else{
                                        if($('#${id}').jqxGrid('iscolumnpinned', strSList[i])){
                                            $("#${id}").jqxGrid('unpincolumn', strSList[i]);
                                        }
                                    }
                                }
                                $("#jqxconfig${id}").jqxWindow('close');
                            });
                        })();
                    </#if>
                    <#if customControlAdvance != "">
			(function(){
                            container.append("<div style='float:right;margin-left:20px;margin-top: 4px; font-size: 14px; font-weight: normal;'>${customControlAdvance}</div>");
				$('#${id}').trigger('loadCustomControlAdvance');
                        })();
                    </#if>
                    <#if customcontrol1 != "">
                        (function(){
                            var tmpStr = "${StringUtil.wrapString(customcontrol1)}".split("@");
                            if(tmpStr.length == 4){
                                container.append('<div style="float:right;margin-left:20px;margin-top: 10px; font-size: 14px; font-weight: normal;"><a id="customcontrol1${id}" style="color:#438eb9" href="' + tmpStr[2] +'" onclick="' + tmpStr[3] + '"><i class="' + tmpStr[0] +'"></i>&nbsp;' + tmpStr[1] +'</a></div>');
                            }else{
                                container.append('<div style="float:right;margin-left:20px;margin-top: 10px; font-size: 14px; font-weight: normal;"><a id="customcontrol1${id}" style="color:#438eb9" href="' + tmpStr[2] +'"><i class="' + tmpStr[0] +'"></i>&nbsp;' + tmpStr[1] +'</a></div>');
                            }
                        })();
                    </#if>
                    <#if customcontrol2 != "">
                        (function(){
                            var tmpStr = "${StringUtil.wrapString(customcontrol2)}".split("@");
                            if(tmpStr.length == 4){
                                container.append('<div style="float:right;margin-left:20px;margin-top: 10px; font-size: 14px; font-weight: normal;"><a id="customcontrol2${id}" style="color:#438eb9;" href="' + tmpStr[2] +'" onclick="' + tmpStr[3] + '"><i class="' + tmpStr[0] +'"></i>&nbsp;' + tmpStr[1] +'</a></div>');
                            }else{
                                container.append('<div style="float:right;margin-left:20px;margin-top: 10px; font-size: 14px; font-weight: normal;"><a id="customcontrol2${id}" style="color:#438eb9;" href="' + tmpStr[2] +'"><i class="' + tmpStr[0] +'"></i>&nbsp;' + tmpStr[1] +'</a></div>');
                            }
                        })();
                    </#if>
                    <#if customcontrol3 != "">
                    (function(){
                        var tmpStr = "${StringUtil.wrapString(customcontrol3)}".split("@");
                        if(tmpStr.length == 4){
                            container.append('<div style="float:right;margin-left:20px;margin-top: 10px; font-size: 14px; font-weight: normal;"><a id="customcontrol3${id}" style="color:#438eb9;" href="' + tmpStr[2] +'" onclick="' + tmpStr[3] + '"><i class="' + tmpStr[0] +'"></i>&nbsp;' + tmpStr[1] +'</a></div>');
                        }else{
                            container.append('<div style="float:right;margin-left:20px;margin-top: 10px; font-size: 14px; font-weight: normal;"><a id="customcontrol3${id}" style="color:#438eb9;" href="' + tmpStr[2] +'"><i class="' + tmpStr[0] +'"></i>&nbsp;' + tmpStr[1] +'</a></div>');
                        }
                    })();
                    </#if>
                    <#if filterbutton=="true">
                        (function(){
                            container.append('<button id="filterbutton${id}" style="margin-left:20px;"><i class="icon-filter"></i>${uiLabelMap.accFilter}</button>');
                            var obj = $('#filterbutton${id}');
                            obj.jqxButton();
                            obj.click(function () {
                            var columname = $("#${id}").jqxGrid('getselectedcell').column;
                            var selectedrow = $("#${id}").jqxGrid('getselectedcell');
                            var filtergroup = new $.jqx.filter();
                            var filter_or_operator = 1;
                            var filtervalue = $("#${id}").jqxGrid('getselectedcell').value;
                            var filtercondition = 'equal';
                            var filter1 = filtergroup.createfilter('stringfilter', filtervalue, filtercondition);
                            filtergroup.addfilter(filter_or_operator, filter1);
                            $("#${id}").jqxGrid('addfilter', columname, filtergroup);
                                  $("#${id}").jqxGrid('applyfilters');
                            });
                        })();
                    </#if>
                    <#if clearfilteringbutton=="true">
                        (function(){
                            container.append('<button id="clearfilteringbutton${id}" style="margin-left:20px;" title="(Ctrl+F)"><span style="color:red;font-size:80%;left:5px;position:relative;">x</span><i class="fa-filter"></i></span> ${uiLabelMap.accRemoveFilter}</button>');
                            var obj = $('#clearfilteringbutton${id}');
                            obj.jqxButton();
                            obj.click(function () {
                                $("#${id}").jqxGrid('clearfilters');
                            });
                        })();
                    </#if>

                    <#if addrow=="true">
                        (function(){
                            container.append('<button id="addrowbutton${id}" style="margin-left:20px;" title="(Ctrl+I)"><i class="icon-plus-sign"></i>${uiLabelMap.accAddNewRow}</button>');
                            var obj = $("#addrowbutton${id}");
                            obj.jqxButton();
                            // create new row.
                            obj.on('click', function () {
                                var selectedrowindex;
                                if($("#${id}").jqxGrid('getselectedrowindex') == null){
                                    selectedrowindex = 0;
                                }else{
                                    selectedrowindex = $("#${id}").jqxGrid('getselectedrowindex').rowindex;
                                }
                                var dataRecord = $("#${id}").jqxGrid('getrowdata', selectedrowindex);
                                var row;
                                if(dataRecord==null){
                                    row = {
                                        <#if addinitvalue !="">
                                            ${primaryColumn}: '${addinitvalue}'
                                        </#if>
                                    };
                                }else{
                                    var primaryKey = dataRecord.${primaryColumn};
                                    row = {
                                        ${primaryColumn}: primaryKey
                                    };
                                }
                                <#if addType=="popup">
                                    // edit the new row.
                                    var wtmp = window;
                                    <#if alternativeAddPopup=="">
                                        var tmpwidth = $('#popupWindow').jqxWindow('width');
                                        $("#popupWindow").jqxWindow({ position: { x: (wtmp.outerWidth - tmpwidth)/2, y: pageYOffset + 30 } });
                                        $("#popupWindow").jqxWindow('open');
                                        /*$('#popupWindow').on('close', function (event) {
                                            $('#popupWindow').jqxValidator('hide');
                                        });*/
                                    <#else>
                                        var tmpwidth = $('#${alternativeAddPopup}').jqxWindow('width');
                                        $("#${alternativeAddPopup}").jqxWindow({ position: { x: (wtmp.outerWidth - tmpwidth)/2, y: pageYOffset + 30 } });
                                        $("#${alternativeAddPopup}").jqxWindow('open');
                                        /*$('#${alternativeAddPopup}').on('close', function (event) {
                                            $('#${alternativeAddPopup}').jqxValidator('hide');
                                        });*/
                                    </#if>
                                <#else>
                                    $("#${id}").jqxGrid('addRow', null, row, "first");
                                    // select the first row and clear the selection.
                                    $("#${id}").jqxGrid('clearSelection');
                                    $("#${id}").jqxGrid('selectRow', 0);
                                    //$("#jqxgrid").jqxGrid('beginRowEdit', 0);
                                </#if>
                            });
                        })();
                    </#if>
                    <#if addmultiplerows=="true">
                        (function(){
                            container.append('<input style="margin-left: 20px;" id="addmultiplerowsbutton${id}" type="button" value="Add Multiple New Rows" />');
                            var obj = $("#addmultiplerowsbutton${id}");
                            obj.jqxButton();
                            // create new rows.
                            obj.on('click', function () {
                                $("#${id}").jqxGrid('beginupdate');
                                for (var i = 0; i < 10; i++) {
                                    var datarow = generaterow();
                                    var commit = $("#${id}").jqxGrid('addrow', null, datarow);
                                }
                                $("#${id}").jqxGrid('endupdate');
                            });
                        })();
                    </#if>
                    <#if deleterow=="true">
                        (function(){
                            container.append('<button style="margin-left: 20px;" id="deleterowbutton${id}"><i class="icon-trash"></i>${uiLabelMap.accDeleteSelectedRow}</button>');
                            var obj = $("#deleterowbutton${id}");
                            obj.jqxButton();
                             // delete row.
                            obj.on('click', function () {
				<#if deleteConditionFunction != "">
				if(typeof(${deleteConditionFunction}) == "function"){
					var dcf = ${deleteConditionFunction};
					var res = dcf();
					if(typeof(res) == "boolean" && !res){
						var message = "${deleteConditionMessage}";
						if(!message){
							message = "${StringUtil.wrapString(uiLabelMap.CannotDeleteRow)}"
						}
						bootbox.alert(message);
						return;
					}else if(typeof(res) == "string"){
						bootbox.alert(res);
						return;
					}
				}
				</#if>
                                var selectedrowindex = $("#${id}").jqxGrid('getselectedrowindex');
                                var rowscount = $("#${id}").jqxGrid('getdatainformation').rowscount;
                                if (selectedrowindex >= 0 && selectedrowindex < rowscount) {
                                    var dataRecord = $("#${id}").jqxGrid('getrowdata', selectedrowindex);
                                    var id = $("#${id}").jqxGrid('getrowid', selectedrowindex);
                                    $("#dialog-delete${id}").text("${StringUtil.wrapString(uiLabelMap.wgdeleteconfirm)}?" );
                                    $("#dialog-delete${id}").dialog({
                                      resizable: false,
                                      height:180,
                                      modal: true,
                                      buttons: {
                                        "${StringUtil.wrapString(uiLabelMap.wgok)}": function() {
                                          $( this ).dialog( "close" );
                                        var offset = $("#${id}").offset();
                                         //$("#popupModifyWindow").jqxWindow({ position: { x: parseInt(offset.left) + 60, y: parseInt(offset.top) + 60 } });
                                        var commit = $("#${id}").jqxGrid('deleterow', id);
                                        },
                                        "${StringUtil.wrapString(uiLabelMap.wgcancel)}": function() {
                                          $( this ).dialog( "close" );
                                        }
                                      }
                                    });
                                    $("#dialog-delete${id}").parent().css('zIndex',19000);
                                }
                            });
                        })();
                    </#if>
                    <#if updaterow=="true">
                        (function(){
                            container.append('<input style="margin-left: 20px;" id="updaterowbutton${id}" type="button" value="${uiLabelMap.accUpdateSelectedRow}" />');
                            var obj = $("#updaterowbutton${id}");
                            obj.jqxButton();
                            obj.on('click', function () {
                                var selectedrowindex = $("#${id}").jqxGrid('getselectedcell').rowindex;
                                var rowscount = $("#${id}").jqxGrid('getdatainformation').rowscount;
                                if (selectedrowindex >= 0 && selectedrowindex < rowscount) {
                                 //open the popup window when the user clicks a button.
                                 editrow = selectedrowindex;
                                 var offset = $("#${id}").offset();
                                 $("#popupWindow").jqxWindow({ position: { x: parseInt(offset.left) + 60, y: parseInt(offset.top) + 20 } });
                                 // get the clicked row's data and initialize the input fields.
                                 var dataRecord = $("#${id}").jqxGrid('getrowdata', selectedrowindex);
                                <#list "${editColumns}"?split(";") as eColumn>
                                    $("#${eColumn}").val(dataRecord.${eColumn});
                                </#list>
                                 // show the popup window.
                                 $("#popupWindow").jqxWindow('open');
                                }
                               });
                        });
                    </#if>
                    <#if updatemultiplerows=="true">
                        (function(){
                            container.append('<button style="margin-left: 20px;" id="updatemultiplerows${id}"><i class="fa fa-check"></i>${uiLabelMap.accUpdateMultipleRow}</button>');
                            var obj = $("#updatemultiplerows${id}");
                            obj.jqxButton();
                            obj.on('click', function () {
                                var rowscount = $("#${id}").jqxGrid('getdatainformation').rowscount;
                                var status = "true";
                                var selectedrowindex = $("#${id}").jqxGrid('getselectedcell').rowindex;
                                var column =  $("#${id}").jqxGrid('getselectedcell').column;
                                var value = $("#${id}").jqxGrid('getselectedcell').value;
                                var dataRecord = $("#${id}").jqxGrid('getrowdata', selectedrowindex);
                                var columnNames = $('#${id}').jqxGrid('getcolumn',  column).text;
                                editrow = selectedrowindex;
                                $("#dialog-message").text("These all items in Column: " + columnNames + " := " + value +  " will be modify. Are you sure?" );
                                    $("#dialog-message" ).dialog({
                                      resizable: false,
                                      height:180,
                                      modal: true,
                                      buttons: {
                                        "Save": function() {
                                          $( this ).dialog( "close" );
                                        var offset = $("#${id}").offset();
                                         $("#popupModifyWindow").jqxWindow({ position: { x: parseInt(offset.left) + 60, y: parseInt(offset.top) + 20 } });
                                         // get the clicked row's data and initialize the input fields.
                                         $("#newValueUpdate").val(value);
                                         // show the popup window.
                                         $("#popupModifyWindow").jqxWindow('open');
                                        },
                                        Cancel: function() {
                                          $( this ).dialog( "close" );
                                        }
                                      }
                                    });
                            });
                        })();
                    </#if>
                    <#if updateoffline=="true">
                        <#-- FIXME use this feature when data has changed -->
                        (function(){
                            /*$(window).bind("beforeunload", function(e){
                                return "${StringUtil.wrapString(uiLabelMap.confirmRefresh?default(''))}";
                            }); (disable for demo purpose)*/
                            localStorage.removeItem("localGridUpdate${id}");
                            <#if offlinerefreshbutton == "true">
                                container.append('<button style="margin-left: 20px;" id="updateoffline${id}"><i class="fa fa-check"></i>${uiLabelMap.accUpdateGrid}</button>');
                                var obj = $("#updateoffline${id}");
                                var urlStatus = "${updateUrl}";
                                obj.jqxButton();
                                obj.on('click', function () {
                                    var dataupdate = localStorage.getItem("localGridUpdate${id}");
                                    var datadelete = localStorage.getItem("localGridDelete${id}");
                                    if(!dataupdate && !datadelete){
                                        return;
                                    }
                                    var remainupdate = checkDataUpdate(JSON.parse(dataupdate), JSON.parse(datadelete));
                                   if(remainupdate){
					 updateRow${id}(urlStatus, {data:remainupdate});
                                   }
                                   if(datadelete){
					deleteRow${id}("${removeUrl}",{data:datadelete});
                                    }
                                    $('#${id}').jqxGrid('updatebounddata');
                                });
                            </#if>
                        })();
                    </#if>
                    <#if excelExport=="true">
                        (function(){
                            container.append('<input style="margin-left: 20px;" id="excelExport${id}" type="button" value="${uiLabelMap.accExcel}" />');
                            var obj = $("#excelExport${id}");
                            obj.jqxButton();
                            $("#excelExport").click(function () {
                                $("#${id}").jqxGrid('exportdata', 'xls', 'jqxGrid');
                            });
                        })();
                    </#if>
                    <#if toPrint=="true">
                        (function(){
                            container.append('<input style="margin-left: 20px;" id="print${id}" type="button" value="${uiLabelMap.accPrint}" />');
                            var obj = $("#print${id}");
                            obj.jqxButton();
                            obj.click(function () {
                                var gridContent = $("#${id}").jqxGrid('exportdata', 'html');
                                var newWindow = window.open('', '', 'width=800, height=500'),
                                document = newWindow.document.open(),
                                pageContent =
                                    '<!DOCTYPE html>\n' +
                                    '<html>\n' +
                                    '<head>\n' +
                                    '<meta charset="utf-8" />\n' +
                                    '<title>jQWidgets Grid</title>\n' +
                                    '</head>\n' +
                                    '<body>\n' + gridContent + '\n</body>\n</html>';
                                document.write(pageContent);
                                document.close();
                                newWindow.print();
                            });
                        })();
                    </#if>

                    <#if dropdownlist=="true" >
                        (function(){
                            container.append('<div style="margin-left: 20px; position: absolute; right: 4px; top: 6px;" id="dropdownlist${id}" />');
                            $("#dropdownlist${id}").jqxDropDownList({width: 150, selectedIndex: 0, source: ${ddlSource}, displayMember: '${displayMember}', valueMember: '${valueMember}'});
                        })();
                    </#if>
                    <#if sendEmail == "true" >
                        (function(){
                            container.append('<button id="sendemail${id}" style="margin-left: 20px;"><i class="icon-envelope"></i>${uiLabelMap.sendEmail}</button>');
                            jqxid = "${id}";
                            var emailInput = $("#sendemail${id}");
                            var emailModal = $("#emailForm");
                            var outputs = $("#outputEmails");
                            var key = "currentEmail-${id}";
                            localStorage.removeItem(key);
                            emailInput.jqxButton();
                            emailInput.click(function(){
                                outputs.html("");
                                var rows = $('#${id}').jqxGrid('getboundrows');
                                for(var x in rows){
                                    var email = rows[x];
                                    if(email.infoString){
                                        var res = email.infoString;
                                        if(res.length > 1){
                                            for(var y in res){
                                                outputs.append("<option>"+res[y]+"</option>");
                                            }
                                        }else{
                                            outputs.append("<option>"+res[0]+"</option>");
                                        }
                                    }
                                }
                                var currentEmail = $.parseJSON(localStorage.getItem(key));
                                if(currentEmail){
                                    outputs.val(currentEmail);
                                }
                                outputs.trigger("liszt:updated");
                                emailModal.modal("show");
                            });
                        })();
                    </#if>
                    <#if changeState == "true" >
                        (function(){
                            jqxid = "${id}";
                            container.append('<button id="changeState${id}" style="margin-left:20px;"><i class="icon-exchange"></i>${uiLabelMap.changeState}</button>');
                            var stateModal = $("#changeStateForm");
                            var stateBt = $("#changeState${id}");
                            var outputsCustomer = $("#stateCustomerId");
                            var key = "currentCustomer-${id}";
                            localStorage.removeItem(key);
                            var initName = function(customer){
                                if(customer){
                                    var party = customer.partyId;
                                    var name = "";
                                    if(customer.lastName){
                                        name += $.trim(customer.lastName);
                                    }else if(customer.groupName){
                                        name += customer.groupName;
                                    }
                                    if(name && party){
                                        var res = name + "(" + party + ")";
                                        return res;
                                    }else if(party){
                                        var res = "(" + party + ")";
                                        return res;
                                    }
                                }
                            };
                            stateBt.jqxButton();
                            stateBt.click(function(){
                                outputsCustomer.html("");
                                outputsCustomer.val([]);
                                outputsCustomer.trigger("liszt:updated");
                                stateModal.modal("show");
                                var rows = $('#${id}').jqxGrid('getboundrows');
                                for(var x in rows){
                                    var customer = rows[x];
                                    var res = initName(customer);
                                    if(res){
                                        outputsCustomer.append("<option>"+res+"</option>");
                                    }
                                }
                                var customers = $.parseJSON(localStorage.getItem(key));
                                var tmp = [];
                                if(customers){
                                    for(var x in customers){
                                        var customer = customers[x];
                                        var res = initName(customer);
                                        if(res){
                                            tmp.push(res);
                                        }
                                    }
                                    outputsCustomer.val(tmp);
                                }
                                outputsCustomer.trigger("liszt:updated");
                            });
                        })();
                    </#if>
                    <#if customtoolbaraction != "">
                    (function customToolbarAction(container){
			if(typeof(${customtoolbaraction}) == "function"){
				${customtoolbaraction}(container);
			}
                    })(maincontainer);
                    </#if>
                },
                </#if>

                <#if showstatusbar=="true">
                    showstatusbar: true,
                    statusbarheight: ${statusbarheight},
                    showaggregates: true,
                <#else>
                    showstatusbar: false,
                    statusbarheight: 0,
                    showaggregates: false,
                </#if>
                <#if height!="">
                    height:'${height}',
                </#if>
                pageable: ${pageable},
                <#if filterable=="true" && filtersimplemode=="true">
                    showfilterrow: true,
                </#if>
                columnsresize: ${columnsresize},
                columnsreorder: ${columnsreorder},
                sortable: ${sortable},
                scrollmode: '${scrollmode}',
                virtualmode: true,
                enablemousewheel: ${enablemousewheel},
                rendergridrows: function () {
                    return dataadapter${id}.records;
                },
                <#if initrowdetails=="true">
                    rowdetails: true,
                    rowdetailstemplate: { rowdetails: "<div style='margin: 10px;'>${rowdetailstemplateAdvance}</div>", rowdetailsheight: ${rowdetailsheight} },
                    initrowdetails: ${initrowdetailsDetail},
                </#if>
                columns: [
                  ${columnlist}
                  <#if editpopup=="true">
                  ,{ text: 'Edit', datafield: 'Edit', columntype: 'button', cellsrenderer: function () {
                         return "Edit";
                      }, buttonclick: function (row) {
                         // open the popup window when the user clicks a button.
                         editrow = row;
                         var offset = $("#${id}").offset();
                         $("#popupWindow").jqxWindow({ position: { x: parseInt(offset.left) + 60, y: parseInt(offset.top) + 20 } });
                         // get the clicked row's data and initialize the input fields.
                         var dataRecord = $("#${id}").jqxGrid('getrowdata', editrow);
                         <#list "${editColumns}"?split(";") as eColumn>
                            $("#${eColumn}").val(dataRecord.${eColumn});
                         </#list>
                         // show the popup window.
                         $("#popupWindow").jqxWindow('open');
                  }}
                  </#if>
                ],

                <#if '${columngrouplist}' != ''>
                    columngroups: [
                         ${columngrouplist} ]
                 </#if>
            });

            <#if mouseRightMenu != "false">
                $("#${id}").on('contextmenu', function () {
                    return false;
                });
                <#if allGridMenu == "true">
                    $("#${id}").on('mousedown', function (event) {
                        if (event.which == 3) {
                            var scrollTop = $(window).scrollTop();
                            var scrollLeft = $(window).scrollLeft();
                            $("#${contextMenuId}").jqxMenu('open', parseInt(event.clientX) + 5 + scrollLeft, parseInt(event.clientY) + 5 + scrollTop);
                            return false;
                        }
                    });
                <#else>
                    $("#${id}").on('rowClick', function (event) {
                        if (event.args.rightclick) {
                            $("#${id}").jqxGrid('selectrow', event.args.rowindex);
                            var scrollTop = $(window).scrollTop();
                            var scrollLeft = $(window).scrollLeft();
//                            console.log('contextmenu',${contextMenuId});
                            $("#${contextMenuId}").jqxMenu('open', parseInt(event.args.originalEvent.clientX) + 5 + scrollLeft, parseInt(event.args.originalEvent.clientY) + 5 + scrollTop);
                            return false;
                        }
                    });
                </#if>
            </#if>

            <#if '${groupsexpanded}' == "true">
                    $('#${id}').jqxGrid({ groupsexpandedbydefault: true});
            </#if>
            <#if rowselectfunction != "">
             $("#${id}").on('rowSelect', function (event) {
                ${rowselectfunction}
             });
            </#if>
            <#if url != "">
             $("#${id}").on('bindingComplete', function (event) {
		$("#${id}Container").height('auto');
		$('#${id}').jqxGrid('scrolloffset',0,1);
				CallbackFocusFilter();
             });
            <#else>
             $("#${id}Container").height('auto');
            </#if>
            <#if bindingcompletefunction != "">
                $("#${id}").on('bindingComplete', function (event) {
                    ${bindingcompletefunction}
                 });
            </#if>
            <#if afterinitfunction != "">
                ${afterinitfunction}
            </#if>
            $("#${id}").on('cellBeginEdit', function (event)
             {
                oldEditingValue = $('#${id}').jqxGrid('getrowdata', args.rowindex);
                oldEditingValue = jQuery.extend({}, oldEditingValue);
             });
             $('#${id}').on('filter', function(){
		CallbackFocusFilter();
             })
            <#if rowunselectfunction != "">
             $("#${id}").on('rowUnselect', function (event) {
                ${rowunselectfunction}
             });
            </#if>

            <#if bindresize=="true">
                // Responsive for Grid widget
                var tmpWidth = $(window).width() - 40;
                $(window).bind('resize', function() {
                    var sibar = $('#sidebar');
                    var grid = $('#${id}');
                    if($('#sidebar').css("display") != "none"){
                        // NEED UPDATE THE FOLLOWING CODE
                        if($('#sidebar').hasClass("menu-min") != null){
                            grid.jqxGrid({ width: $(window).width() - $('#sidebar').width() - 40 });
                        }else{
                            grid.jqxGrid({ width: $(window).width() - $('#sidebar').width() - 40 });
                        }
                        // END COMMENT
                    }else{
                        grid.jqxGrid({ width: tmpWidth });
                    }
                    tmpWidth = grid.jqxGrid('width');
                    $("#container${id}").width(tmpWidth);
                    $("#container").width(tmpWidth);
                    $("#jqxNotification${id}").jqxNotification({ width: tmpWidth, appendContainer: "#container${id}", opacity: 1, autoClose: false, template: "success" });
                    $("#jqxNotification").jqxNotification({ width: tmpWidth, appendContainer: "#container", opacity: 1, autoClose: false, template: "success" });
                    $("#jqxNotification${id}").jqxNotification({ icon: { width: 25, height: 25, url: '/aceadmin/assets/images/info.jpg'},width: tmpWidth, appendContainer: "#container${id}", opacity: 1, autoClose: false, template: "success" });
                    $("#jqxNotification").jqxNotification({ icon: { width: 25, height: 25, url: '/aceadmin/assets/images/info.jpg'},width: tmpWidth, appendContainer: "#container", opacity: 1, autoClose: false, template: "success" });
                    AutoMeasureGridHeight(grid);
                }).trigger('resize');
                $('#sidebar').bind('resize', function() {
                    grid.jqxGrid({ width: tmpWidth });
                    tmpWidth = grid.jqxGrid('width');
                    $("#container${id}").width(tmpWidth);
                    $("#container").width(tmpWidth);
                    $("#jqxNotification${id}").jqxNotification({ width: tmpWidth, appendContainer: "#container${id}", opacity: 1, autoClose: false, template: "success" });
                    $("#jqxNotification").jqxNotification({ width: tmpWidth, appendContainer: "#container", opacity: 1, autoClose: false, template: "success" });
                    $("#jqxNotification${id}").jqxNotification({ icon: { width: 25, height: 25, url: '/aceadmin/assets/images/info.jpg'},width: tmpWidth, appendContainer: "#container${id}", opacity: 1, autoClose: false, template: "success" });
                    $("#jqxNotification").jqxNotification({ icon: { width: 25, height: 25, url: '/aceadmin/assets/images/info.jpg'},width: tmpWidth, appendContainer: "#container", opacity: 1, autoClose: false, template: "success" });
                });//.trigger('resize');
            <#else>
                var tmpWidth = $('#${id}').jqxGrid('width');
                $("#container").width(tmpWidth);
                $("#jqxNotification").jqxNotification({ icon: { width: 25, height: 25, url: '/aceadmin/assets/images/info.jpg'},width: tmpWidth, appendContainer: "#container", opacity: 1, autoClose: false, template: "success" });
                $("#container${id}").width(tmpWidth);
                $("#jqxNotification${id}").jqxNotification({ icon: { width: 25, height: 25, url: '/aceadmin/assets/images/info.jpg'},width: tmpWidth, appendContainer: "#container${id}", opacity: 1, autoClose: false, template: "success" });
            </#if>
            <#if editpopup=="true" || doubleClick=="true" || updaterow=="true" || addType=="popup">
                // initialize the popup window and buttons.
                $("#popupWindow").jqxWindow({
                    width: 450, resizable: false,  isModal: true, autoOpen: false, cancelButton: $("#Cancel"), modalOpacity: 0.7, modalZIndex: 1000
                });

                $("#Cancel").jqxButton({ theme: theme });
                $("#Save").jqxButton({ theme: theme });

                // update the edited row when the user clicks the 'Save' button.
                $("#Save").click(function () {
                    if (editrow >= 0) {
                        var row = {
                            <#list "${editColumns}"?split(";") as eColumn>
                                //${eColumn} : $("#${eColumn}").val(),
                            </#list>
                        };
                        var rowID = $('#${id}').jqxGrid('getrowid', editrow);
                        $('#${id}').jqxGrid('updaterow', rowID, row);
                        $("#popupWindow").jqxWindow('hide');
                    }
                });
            </#if>
            <#if updatemultiplerows=="true">
                $("#popupModifyWindow").jqxWindow({
                    width: 450, resizable: false,  isModal: true, autoOpen: false, cancelButton: $("#CancelModify"), modalOpacity: 0.7, modalZIndex: 1000
                });

                $("#popupModifyWindow").on('open', function () {
                    $("#newValueUpdate").jqxInput('selectAll');
                });

                $("#CancelModify").jqxButton({ theme: theme });
                $("#SaveModify").jqxButton({ theme: theme });

                // update the edited row when the user clicks the 'Save' button.
                $("#SaveModify").click(function () {
                    if (editrow >= 0) {
                           var selectedrowindex = $("#${id}").jqxGrid('getselectedcell').rowindex;
                            var columnvalue =  $("#${id}").jqxGrid('getselectedcell').column;
                            var value = $("#${id}").jqxGrid('getselectedcell').value;
                            var dataRecord = $("#${id}").jqxGrid('getrowdata', selectedrowindex);
                            var columnNames = $('#${id}').jqxGrid('getcolumn',  columnvalue).text;
                        var row = {
                            columnName: columnvalue,
                            newValue: $("#newValueUpdate").val(),
                            oldValue: value
                        };
                        var rowID = $('#${id}').jqxGrid('getrowid', editrow);
                        $('#${id}').jqxGrid('updaterow', rowID, row);
                        $("#popupModifyWindow").jqxWindow('hide');
                    }
                });
            </#if>

            <#if doubleClick=="true">
                $("#${id}").on('rowDoubleClick', function (event) {
                var args = event.args;
                var row = args.rowindex;

                // open the popup window when the user clicks a button.
                 editrow = row;
                 var offset = $("#${id}").offset();
                 $("#popupWindow").jqxWindow({ position: { x: parseInt(offset.left) + 60, y: parseInt(offset.top) + 20 } });
                 // get the clicked row's data and initialize the input fields.
                 var dataRecord = $("#${id}").jqxGrid('getrowdata', editrow);

                 <#list "${editColumns}"?split(";") as eColumn>
                    $("#${eColumn}").val(dataRecord.${eColumn});
                 </#list>
                /* $("#glAccountId").val(dataRecord.glAccountId);
                 $("#parentGlAccountId").val(dataRecord.parentGlAccountId);
                 $("#accountCode").val(dataRecord.accountCode);
                 $("#accountName").val(dataRecord.accountName);
                 $("#description").val(dataRecord.description); */
                 // show the popup window.
                 $("#popupWindow").jqxWindow('open');
            });
            </#if>
            <#if autoMeasureHeight=="true" && autoheight='false'>
		(function MesureHeight(){
			console.log($("#${id}"));
			AutoMeasureGridHeight($("#${id}"));
		})();
            </#if>
        <#if customLoadFunction == "true">
            };
        <#else>
            });
        </#if>
    </script>
    <#if showlist != "">
        <div id="jqxconfig${id}" style="display:none;">
            <div><i class="fa-cogs btn-mini" style="padding-right:5px;margin:0px;"></i>${uiLabelMap.clconfiguration}</div>
            <div class='form-window-container' id="windowContent${id}">
		<div class='form-window-content'>
			<div class='row-fluid margin-bottom10'>
						<div class='span5 text-algin-right'>
							<label class="asterisk">${uiLabelMap.cldisplay}</label>
						</div>
						<div class="span7">
							<div id="showSL${id}"></div>
						</div>
					</div>
					<div class='row-fluid margin-bottom10'>
						<div class='span5 text-algin-right'>
							<label class="asterisk">${uiLabelMap.clpin}</label>
						</div>
						<div class="span7">
							<div id="frozenSL${id}"></div>
						</div>
					</div>
		</div>
		<div class="form-action">
					<button id="configCancel${id}" class='btn btn-danger form-action-button pull-right'><i class='fa-remove'></i> ${uiLabelMap.CommonCancel}</button>
					<button id="configSave${id}" class='btn btn-primary form-action-button pull-right'><i class='fa-check'></i> ${uiLabelMap.CommonSave}</button>
				</div>
            </div>
        </div>
        <script type="text/javascript">
            function openJqxConfigWindow${id}(){
                var wtmp = window;
                var tmpwidth = $('#jqxconfig${id}').jqxWindow('width');
                $("#jqxconfig${id}").jqxWindow({ position: { x: (wtmp.outerWidth - tmpwidth)/2, y: pageYOffset + 80 } });
                $('#jqxconfig${id}').jqxWindow('open');
            };
        </script>
    </#if>
    <div id="container${id}" style="background-color: transparent; overflow: auto;">
    </div>
    <div id="container" style="background-color: transparent; overflow: auto;">
    </div>
    <div id="jqxNotification${id}">
        <div id="notificationContent${id}">
        </div>
    </div>
    <div id="jqxNotification">
        <div id="notificationContent">
        </div>
    </div>
    <#if idExisted=="false">
        <#if customCss != ""><div class="${customCss}"></#if>
            <div id="${id}Container" style="height:0px">
			<div id="${id}">
	            </div>
            </div>
        <#if customCss != ""></div></#if>
    </#if>
    <#if editpopup=="true" || doubleClick=="true" || updaterow=="true" || addType=="popup">
    <div id="popupWindow" style="display:none;">
        <div>Edit</div>
        <div style="overflow: hidden;">
            <table>
                 <#list "${editColumns}"?split(";") as eColumn>
                    <tr>
                        <td align="right">${eColumn}:</td>
                        <#if '${eColumn}' == '${primaryColumn}' >
                            <td align="left"><input id="${eColumn}" readonly/></td>
                        <#else>
                            <td align="left"><input id="${eColumn}" /></td>
                        </#if>
                    </tr>
                 </#list>
                <tr>
                    <td align="right"></td>
                    <td style="padding-top: 10px;" align="right"><input style="margin-right: 5px;" type="button" id="Save" value="Save" /><input id="Cancel" type="button" value="Cancel" /></td>
                </tr>
            </table>
        </div>
    </div>
    <div id="dialog-confirm" title="Modify data?">
        <div style="overflow: hidden;">
            <div style="margin-top: 10px;" id="dialog-confirm"></div>
        </div>
    </div>
    <div id="dialog-message" title="Modify data?">
        <div style="margin-top: 10px;" id="dialog-message"></div>
    </div>

    </#if>
     <#if updatemultiplerows=="true">
        <div id="popupModifyWindow">
            <div>Edit</div>
            <div style="overflow: hidden;">
                <table>
                    <tr>
                        <td align="right">New values:</td>
                        <td align="left"><input id="newValueUpdate" /></td>
                        <tr>
                        <td align="right"></td>
                        <td style="padding-top: 10px;" align="right"><input style="margin-right: 5px;" type="button" id="SaveModify" value="Save" /><input id="CancelModify" type="button" value="Cancel" /></td>
                    </tr>
                    </tr>
                </table>
            </div>
    </div>
     </#if>
     <div id="dialog-delete${id}" title="${StringUtil.wrapString(uiLabelMap.wgdeletedata)}?">
    </div>
</#macro>

<#macro renderFilterType arrayName="dataStringFilterType">
    var ${arrayName} = new Array();
    // begin init String filter type
    var row = {};
    /*row["stringFilterType"] = "EMPTY";
    row["description"] = "R?ng";
    ${arrayName}[0] = row;
    row = {};
    row["stringFilterType"] = "NOT_EMPTY";
    row["description"] = "Không r?ng";
    ${arrayName}[1] = row;*/
    row = {};
    row["stringFilterType"] = "CONTAINS";
    row["description"] = "Ch?a";
    ${arrayName}[2] = row;
    /*row = {};
    row["stringFilterType"] = "CONTAINS_CASE_SENSITIVE";
    row["description"] = "Ch?a(Phân bi?t hoa thu?ng)";
    ${arrayName}[3] = row;*/
    row = {};
    row["stringFilterType"] = "DOES_NOT_CONTAIN";
    row["description"] = "Không ch?a";
    ${arrayName}[4] = row;
    /*row = {};
    row["stringFilterType"] = "DOES_NOT_CONTAIN_CASE_SENSITIVE";
    row["description"] = "Không ch?a(Phân bi?t hoa thu?ng)";
    ${arrayName}[5] = row;*/
    row = {};
    row["stringFilterType"] = "STARTS_WITH";
    row["description"] = "B?t d?u b?ng";
    ${arrayName}[6] = row;
    /*row = {};
    row["stringFilterType"] = "STARTS_WITH_CASE_SENSITIVE";
    row["description"] = "B?t d?u b?ng(Phân bi?t hoa thu?ng)";
    ${arrayName}[7] = row;*/
    row = {};
    row["stringFilterType"] = "ENDS_WITH";
    row["description"] = "K?t thúc b?ng";
    ${arrayName}[8] = row;
    /*row = {};
    row["stringFilterType"] = "ENDS_WITH_CASE_SENSITIVE";
    row["description"] = "K?t thúc b?ng(Phân bi?t hoa thu?ng)";
    ${arrayName}[9] = row;*/
    row = {};
    row["stringFilterType"] = "EQUAL";
    row["description"] = "B?ng";
    ${arrayName}[10] = row;
    row = {};
    row["stringFilterType"] = "NOT_EQUAL";
    row["description"] = "Không b?ng";
    ${arrayName}[11] = row;
    /*row = {};
    row["stringFilterType"] = "EQUAL_CASE_SENSITIVE";
    row["description"] = "B?ng(PhÃ¢n biáº¿t hoa thÆ°á»?ng)";
    ${arrayName}[11] = row;*/
    row = {};
    row["stringFilterType"] = "NULL";
    row["description"] = "Null";
    ${arrayName}[12] = row;
    row = {};
    row["stringFilterType"] = "NOT_NULL";
    row["description"] = "Không null";
    ${arrayName}[13] = row;
</#macro>
<#macro renderDateTimeFilterType arrayName="dataDateTimeFilterType">
    var ${arrayName} = new Array();
    // begin init String filter type
    var row = {};
    row = {};
    row["datetimeFilterType"] = "EQUAL";
    row["description"] = "B?ng";
    ${arrayName}[0] = row;
    row = {};
    row["datetimeFilterType"] = "NOT_EQUAL";
    row["description"] = "Không b?ng";
    ${arrayName}[1] = row;
    row = {};
    row["datetimeFilterType"] = "NULL";
    row["description"] = "Null";
    ${arrayName}[2] = row;
    row = {};
    row["datetimeFilterType"] = "NOT_NULL";
    row["description"] = "Không null";
    ${arrayName}[3] = row;
    row = {};
    row["datetimeFilterType"] = "LESS_THAN";
    row["description"] = "Nh? hon";
    ${arrayName}[4] = row;
    row = {};
    row["datetimeFilterType"] = "LESS_THAN_OR_EQUAL";
    row["description"] = "Nh? hon ho?c b?ng";
    ${arrayName}[5] = row;
    row = {};
    row["datetimeFilterType"] = "GREATER_THAN";
    row["description"] = "L?n hon";
    ${arrayName}[6] = row;
    row = {};
    row["datetimeFilterType"] = "GREATER_THAN_OR_EQUAL";
    row["description"] = "L?n hon ho?c b?ng";
    ${arrayName}[7] = row;
</#macro>
<#macro renderJqxTitle titlePropertyTmp id>
    <#if titlePropertyTmp?contains("-")>
        var jqxheader = $("<div id='toolbarcontainer${id}' class='widget-header'><h4>"
            <#list titlePropertyTmp?split("-") as str>
                <#if str?contains("[")>
                <#assign strTmp = str?substring(2, str.length - 1)/>
                    + "${strTmp}" + "&nbsp;"
                <#else>
                    + "${StringUtil.wrapString(uiLabelMap[str])}&nbsp;"
                </#if>
            </#list>
            + "</h4><div id='toolbarButtonContainer${id}' class='pull-right'></div></div>");
    <#else>
        var jqxheader = $("<div id='toolbarcontainer${id}' class='widget-header'><h4>" + "${StringUtil.wrapString(uiLabelMap[titlePropertyTmp])}" + "</h4><div id='toolbarButtonContainer${id}' class='pull-right'></div></div>");
    </#if>
</#macro>
<#global jqTable=jqTable />
<#global jqGrid=jqGrid />
<#global useLocalizationNumberFunction=useLocalizationNumberFunction />
<#global jqGridMinimumLib=jqGridMinimumLib />
<#global jqMinimumLib=jqMinimumLib />
<#global renderFilterType=renderFilterType />
<#global renderDateTimeFilterType=renderDateTimeFilterType />
<#--
    End of jq renderer
-->

<#macro renderField text>
  <#if text?exists>
    ${text}<#lt/>
  </#if>
</#macro>

<#macro renderDisplayField type imageLocation idName description title class alert inPlaceEditorUrl="" inPlaceEditorParams="">
  <#if type?has_content && type=="image">
    <img src="${imageLocation}" alt=""><#lt/>
  <#else>
    <#if inPlaceEditorUrl?has_content || class?has_content || alert=="true" || title?has_content>
      <span <#if idName?has_content>id="cc_${idName}"</#if> <#if title?has_content>title="${title}"</#if> <@renderClass class alert />><#t/>
    </#if>

    <#if description?has_content>
      ${description?replace("\n", "<br />")}<#t/>
    <#else>
      &nbsp;<#t/>
    </#if>
    <#if inPlaceEditorUrl?has_content || class?has_content || alert=="true">
      </span><#lt/>
    </#if>
    <#if inPlaceEditorUrl?has_content && idName?has_content>
      <script language="JavaScript" type="text/javascript"><#lt/>
        ajaxInPlaceEditDisplayField('cc_${idName}', '${inPlaceEditorUrl}', ${inPlaceEditorParams});<#lt/>
      </script><#lt/>
    </#if>
  </#if>
</#macro>
<#macro renderHyperlinkField></#macro>

<#macro renderTextField name className alert value textSize maxlength id event action disabled clientAutocomplete ajaxUrl ajaxEnabled mask placeholder="">
  <#if mask?has_content>
    <script type="text/javascript">
      jQuery(function($){jQuery("#${id}").mask("${mask}");});
    </script>
  </#if>
  <input type="text" name="${name?default("")?html}"<#t/>
    <@renderClass className alert />
    <#if value?has_content> value="${value}"</#if><#rt/>
    <#if textSize?has_content> size="${textSize}"</#if><#rt/>
    <#if maxlength?has_content> maxlength="${maxlength}"</#if><#rt/>
    <#if disabled?has_content && disabled> disabled="disabled"</#if><#rt/>
    <#if id?has_content> id="${id}"</#if><#rt/>
    <#if event?has_content && action?has_content> ${event}="${action}"</#if><#rt/>
    <#if clientAutocomplete?has_content && clientAutocomplete=="false"> autocomplete="off"</#if><#rt/>
    <#if placeholder?has_content> placeholder="${placeholder}"</#if><#rt/>
  /><#t/>
  <#if ajaxEnabled?has_content && ajaxEnabled>
    <#assign defaultMinLength = Static["org.ofbiz.base.util.UtilProperties"].getPropertyValue("widget.properties", "widget.autocompleter.defaultMinLength")>
    <#assign defaultDelay = Static["org.ofbiz.base.util.UtilProperties"].getPropertyValue("widget.properties", "widget.autocompleter.defaultDelay")>
    <script language="JavaScript" type="text/javascript">ajaxAutoCompleter('${ajaxUrl}', false, ${defaultMinLength!2}, ${defaultDelay!300});</script><#lt/>
  </#if>
</#macro>

<#macro renderTextareaField name className alert cols rows id readonly value visualEditorEnable buttons language="">
  <textarea name="${name}"<#t/><@renderClass className alert />
    <#if cols?has_content> cols="${cols}"</#if><#rt/>
    <#if rows?has_content> rows="${rows}"</#if><#rt/>
    <#if id?has_content> id="${id}"</#if><#rt/>
    <#if readonly?has_content && readonly=='readonly'> readonly="readonly"</#if><#rt/>
    <#if maxlength?has_content> maxlength="${maxlength}"</#if><#rt/>><#t/>
    <#if value?has_content>${value}</#if><#t/>
  </textarea><#lt/>
  <#if visualEditorEnable?has_content>
    <script language="javascript" src="/images/jquery/plugins/elrte-1.3/js/elrte.min.js" type="text/javascript"></script><#rt/>
    <#if language?has_content && language != "en">
      <script language="javascript" src="/images/jquery/plugins/elrte-1.3/js/i18n/elrte.${language!"en"}.js" type="text/javascript"></script><#rt/>
    </#if>
    <link href="/images/jquery/plugins/elrte-1.3/css/elrte.min.css" rel="stylesheet" type="text/css">
    <script language="javascript" type="text/javascript">
      var opts = {
         cssClass : 'el-rte',
         lang     : '${language!"en"}',
         toolbar  : '${buttons?default("maxi")}',
         doctype  : '<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">', //'<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN">',
         cssfiles : ['/images/jquery/plugins/elrte-1.3/css/elrte-inner.css']
      }
      jQuery('#${id?default("")}').elrte(opts);
    </script>
  </#if>
</#macro>

<#macro renderDateTimeField name className alert title value size maxlength id dateType shortDateInput timeDropdownParamName defaultDateTimeString localizedIconTitle timeDropdown timeHourName classString hour1 hour2 timeMinutesName minutes isTwelveHour ampmName amSelected pmSelected compositeType formName mask="" event="" action="" step="" timeValues="">
  <span class="view-calendar">
    <#if dateType!="time" >
      <input type="text" name="${name}_i18n" <@renderClass className alert /><#rt/>
        <#if title?has_content> title="${title}"</#if>
        <#if value?has_content> value="${value}"</#if>
        <#if size?has_content> size="${size}"</#if><#rt/>
        <#if maxlength?has_content>  maxlength="${maxlength}"</#if>
        <#if id?has_content> id="${id}_i18n"</#if>/><#rt/>
    </#if>
    <#-- the style attribute is a little bit messy but when using disply:none the timepicker is shown on a wrong place -->
    <input type="text" name="${name}" style="height:1px;width:1px;border:none;background-color:transparent;display:none;" <#if event?has_content && action?has_content> ${event}="${action}"</#if> <@renderClass className alert /><#rt/>
      <#if title?has_content> title="${title}"</#if>
      <#if value?has_content> value="${value}"</#if>
      <#if size?has_content> size="${size}"</#if><#rt/>
      <#if maxlength?has_content>  maxlength="${maxlength}"</#if>
      <#if id?has_content> id="${id}"</#if>/><#rt/>
    <#if dateType!="time" >
      <script type="text/javascript">
        <#-- If language specific lib is found, use date / time converter else just copy the value fields -->
        if (Date.CultureInfo != undefined) {
          var initDate = <#if value?has_content>jQuery("#${id}_i18n").val()<#else>""</#if>;
          if (initDate != "") {
            var dateFormat = "dd/MM/yyyy"<#if shortDateInput?exists && !shortDateInput> + " " + "HH:mm:ss"</#if>;
            <#-- bad hack because the JS date parser doesn't understand dots in the date / time string -->
            if (initDate.indexOf('.') != -1) {
              initDate = initDate.substring(0, initDate.indexOf('.'));
            }
            var ofbizTime = "<#if shortDateInput?exists && shortDateInput>yyyy-MM-dd<#else>yyyy-MM-dd HH:mm:ss</#if>";
            var dateObj = Date.parseExact(initDate, ofbizTime);
            var formatedObj = dateObj.toString(dateFormat);
            jQuery("#${id}_i18n").val(formatedObj);
          }

          jQuery("#${id}").change(function() {
            var ofbizTime = "<#if shortDateInput?exists && shortDateInput>yyyy-MM-dd<#else>yyyy-MM-dd HH:mm:ss</#if>";
            var newValue = ""
            if (this.value != "") {
              var dateObj = Date.parseExact(this.value, ofbizTime);
              var dateFormat = "dd/MM/yyyy"<#if shortDateInput?exists && !shortDateInput> + " " + "HH:mm:ss"</#if>;
              newValue = dateObj.toString(dateFormat);
            }
            jQuery("#${id}_i18n").val(newValue);
          });
          jQuery("#${id}_i18n").change(function() {
            var dateFormat = "dd/MM/yyyy"<#if shortDateInput?exists && !shortDateInput> + " " + "HH:mm:ss"</#if>,
            newValue = "",
            dateObj = Date.parseExact(this.value, dateFormat),
            ofbizTime;
            if (this.value != "" && dateObj !== null) {
              ofbizTime = "<#if shortDateInput?exists && shortDateInput>yyyy-MM-dd<#else>yyyy-MM-dd HH:mm:ss</#if>";
              newValue = dateObj.toString(ofbizTime);
            }
            else { // invalid input
              jQuery("#${id}_i18n").val("");
            }
            jQuery("#${id}").val(newValue);
          });
        } else {
          <#-- fallback if no language specific js date file is found -->
          jQuery("#${id}").change(function() {
          jQuery("#${id}_i18n").val(this.value);
        });
        jQuery("#${id}_i18n").change(function() {
          jQuery("#${id}").val(this.value);
        });
      }

      <#if shortDateInput?exists && shortDateInput>
        jQuery("#${id}").datepicker({
      <#else>
        jQuery("#${id}").datetimepicker({
          showSecond: true,
          <#-- showMillisec: true, -->
          timeFormat: 'hh:mm:ss',
          stepHour: 1,
          stepMinute: 1,
          stepSecond: 1,
      </#if>
          showOn: 'button',
          buttonText: '',
          buttonImageOnly: false,
          dateFormat: 'yy-mm-dd'
        })
        <#if mask?has_content>.mask("${mask}")</#if>
        ;
      </script>
    </#if>
    <#if timeDropdown?has_content && timeDropdown=="time-dropdown">
      <select name="${timeHourName}" <#if classString?has_content>class="${classString}"</#if>><#rt/>
        <#if isTwelveHour>
          <#assign x=11>
          <#list 0..x as i>
            <option value="${i}"<#if hour1?has_content><#if i=hour1> selected="selected"</#if></#if>>${i}</option><#rt/>
          </#list>
        <#else>
          <#assign x=23>
          <#list 0..x as i>
            <option value="${i}"<#if hour2?has_content><#if i=hour2> selected="selected"</#if></#if>>${i}</option><#rt/>
          </#list>
        </#if>
        </select>:<select name="${timeMinutesName}" <#if classString?has_content>class="${classString}"</#if>><#rt/>
          <#assign values = Static["org.ofbiz.base.util.StringUtil"].toList(timeValues)>
          <#list values as i>
            <option value="${i}"<#if minutes?has_content><#if i?number== minutes ||((i?number==(60 -step?number)) && (minutes &gt; 60 - (step?number/2))) || ((minutes &gt; i?number )&& (minutes &lt; i?number+(step?number/2))) || ((minutes &lt; i?number )&& (minutes &gt; i?number-(step?number/2)))> selected="selected"</#if></#if>>${i}</option><#rt/>
          </#list>
        </select>
        <#rt/>
        <#if isTwelveHour>
          <select name="${ampmName}" <#if classString?has_content>class="${classString}"</#if>><#rt/>
            <option value="AM" <#if amSelected == "selected">selected="selected"</#if> >AM</option><#rt/>
            <option value="PM" <#if pmSelected == "selected">selected="selected"</#if>>PM</option><#rt/>
          </select>
        <#rt/>
      </#if>
    </#if>
    <input type="hidden" name="${compositeType}" value="Timestamp"/>
  </span>
</#macro>

<#macro renderDropDownField name className alert id multiple formName otherFieldName event action size firstInList currentValue explicitDescription allowEmpty options fieldName otherFieldName otherValue otherFieldSize dDFCurrent ajaxEnabled noCurrentSelectedKey ajaxOptions frequency minChars choices autoSelect partialSearch partialChars ignoreCase fullSearch>
  <span class="ui-widget">
    <select name="${name?default("")}<#rt/>" <@renderClass className alert /><#if id?has_content> id="${id}"</#if><#if multiple?has_content> multiple="multiple"</#if><#if otherFieldSize gt 0> onchange="process_choice(this,document.${formName}.${otherFieldName})"</#if><#if event?has_content> ${event}="${action}"</#if><#if size?has_content> size="${size}"</#if>>
      <#if firstInList?has_content && currentValue?has_content && !multiple?has_content>
        <option selected="selected" value="${currentValue}">${explicitDescription}</option><#rt/>
        <option value="${currentValue}">---</option><#rt/>
      </#if>
      <#if allowEmpty?has_content || !options?has_content>
        <option value="">&nbsp;</option>
      </#if>
      <#list options as item>
        <#if multiple?has_content>
          <option<#if currentValue?has_content && item.selected?has_content> selected="${item.selected}" <#elseif !currentValue?has_content && noCurrentSelectedKey?has_content && noCurrentSelectedKey == item.key> selected="selected" </#if> value="${item.key}">${item.description}</option><#rt/>
        <#else>
          <option<#if currentValue?has_content && currentValue == item.key && dDFCurrent?has_content && "selected" == dDFCurrent> selected="selected"<#elseif !currentValue?has_content && noCurrentSelectedKey?has_content && noCurrentSelectedKey == item.key> selected="selected"</#if> value="${item.key}">${item.description}</option><#rt/>
        </#if>
      </#list>
    </select>
  </span>
  <#if otherFieldName?has_content>
    <noscript><input type='text' name='${otherFieldName}' /></noscript>
    <script type='text/javascript' language='JavaScript'><!--
      disa = ' disabled';
      if(other_choice(document.${formName}.${fieldName}))
        disa = '';
      document.write("<input type='text' name='${otherFieldName}' value='${otherValue?js_string}' size='${otherFieldSize}'"+disa+" onfocus='check_choice(document.${formName}.${fieldName})' />");
      if(disa && document.styleSheets)
      document.${formName}.${otherFieldName}.style.visibility  = 'hidden';
    //--></script>
  </#if>

  <#if ajaxEnabled>
    <script language="JavaScript" type="text/javascript">
      ajaxAutoCompleteDropDown();
      jQuery(function() {
        jQuery("#${id}").combobox();
      });
    </script>
  </#if>
</#macro>

<#macro renderDropDownFieldSelectedAll name className alert id multiple formName otherFieldName event action size firstInList currentValue explicitDescription allowEmpty options fieldName otherFieldName otherValue otherFieldSize dDFCurrent ajaxEnabled noCurrentSelectedKey ajaxOptions frequency minChars choices autoSelect partialSearch partialChars ignoreCase fullSearch selectedAll>
  <span class="ui-widget">
    <select name="${name?default("")}<#rt/>" <@renderClass className alert /><#if id?has_content> id="${id}"</#if><#if multiple?has_content> multiple="multiple"</#if><#if otherFieldSize gt 0> onchange="process_choice(this,document.${formName}.${otherFieldName})"</#if><#if event?has_content> ${event}="${action}"</#if><#if size?has_content> size="${size}"</#if>>
      <#if firstInList?has_content && currentValue?has_content && !multiple?has_content>
        <option selected="selected" value="${currentValue}">${explicitDescription}</option><#rt/>
        <option value="${currentValue}">---</option><#rt/>
      </#if>
      <#if allowEmpty?has_content || !options?has_content>
        <option value="">&nbsp;</option>
      </#if>

      <#list options as item>
        <#if multiple?has_content>
            <#if (currentValue?exists && currentValue?has_content) || (currentValue?has_content && item.selected?has_content) || (!currentValue?has_content && noCurrentSelectedKey?has_content && noCurrentSelectedKey == item.key)>
                <option<#if currentValue?has_content && item.selected?has_content> selected="${item.selected}" <#elseif !currentValue?has_content && noCurrentSelectedKey?has_content && noCurrentSelectedKey == item.key> selected="selected" </#if> value="${item.key}">${item.description}</option><#rt/>
            <#else>
                <#if selectedAll?has_content>
                    <option selected="selected" value="${item.key}">${item.description}</option><#rt/>
                <#else>
                    <option value="${item.key}">${item.description}</option><#rt/>
                </#if>
            </#if>
        <#else>
            <option<#if currentValue?has_content && currentValue == item.key && dDFCurrent?has_content && "selected" == dDFCurrent> selected="selected"<#elseif !currentValue?has_content && noCurrentSelectedKey?has_content && noCurrentSelectedKey == item.key> selected="selected"</#if> value="${item.key}">${item.description}</option><#rt/>
        </#if>
      </#list>
    </select>
  </span>
  <#if otherFieldName?has_content>
    <noscript><input type='text' name='${otherFieldName}' /></noscript>
    <script type='text/javascript' language='JavaScript'><!--
      disa = ' disabled';
      if(other_choice(document.${formName}.${fieldName}))
        disa = '';
      document.write("<input type='text' name='${otherFieldName}' value='${otherValue?js_string}' size='${otherFieldSize}'"+disa+" onfocus='check_choice(document.${formName}.${fieldName})' />");
      if(disa && document.styleSheets)
      document.${formName}.${otherFieldName}.style.visibility  = 'hidden';
    //--></script>
  </#if>

  <#if ajaxEnabled>
    <script language="JavaScript" type="text/javascript">
      ajaxAutoCompleteDropDown();
      jQuery(function() {
        jQuery("#${id}").combobox();
      });
    </script>
  </#if>
</#macro>

<#macro renderCheckField items className alert id allChecked currentValue name event action>
  <#list items as item>
    <label style="display:inline;" <@renderClass className alert />><#rt/>
      <input type="checkbox"<#if (item_index == 0)> id="${id}"</#if><#rt/>
        <#if allChecked?has_content && allChecked> checked="checked" <#elseif allChecked?has_content && !allChecked>
          <#elseif currentValue?has_content && currentValue==item.value> checked="checked"</#if>
          name="${name?default("")?html}" value="${item.value?default("")?html}"<#if event?has_content> ${event}="${action}"</#if>/><#rt/>
        ${item.description?default("")}
        <span class="lbl"></span>
    </label>
  </#list>
</#macro>

<#macro renderRadioField items className alert currentValue noCurrentSelectedKey name event action>
  <#list items as item>
    <span <@renderClass className alert />><#rt/>
      <input type="radio"<#if currentValue?has_content><#if currentValue==item.key> checked="checked"</#if>
        <#elseif noCurrentSelectedKey?has_content && noCurrentSelectedKey == item.key> checked="checked"</#if>
        name="${name?default("")?html}" value="${item.key?default("")?html}"<#if event?has_content> ${event}="${action}"</#if>/><#rt/>
      ${item.description}
    </span>
  </#list>
</#macro>

<#macro renderSubmitField buttonType className alert formName title name event action imgSrc confirmation containerId ajaxUrl>
  <#if buttonType=="text-link">
    <a <@renderClass className alert /> href="javascript:document.${formName}.submit()" <#if confirmation?has_content>onclick="return confirm('${confirmation?js_string}');"</#if>><#if title?has_content>${title}</#if> </a>
  <#elseif buttonType=="image">
    <input type="image" src="${imgSrc}" <@renderClass className alert /><#if name?has_content> name="${name}"</#if>
    <#if title?has_content> alt="${title}"</#if><#if event?has_content> ${event}="${action}"</#if>
    <#if confirmation?has_content>onclick="return confirm('${confirmation?js_string}');"</#if>/>
  <#else>
    <button type="<#if containerId?has_content>button<#else>submit</#if>" <@renderClass className alert />
    <#if name?exists> name="${name}"</#if><#if title?has_content> </#if><#if event?has_content> ${event}="${action}"</#if>
    <#if containerId?has_content> onclick="<#if confirmation?has_content>if (confirm('${confirmation?js_string}')) </#if>ajaxSubmitFormUpdateAreas('${containerId}', '${ajaxUrl}')"
      <#else><#if confirmation?has_content> onclick="return confirm('${confirmation?js_string}');"</#if>
    </#if>><i class="${imgSrc}"></i>${title}</button>
  </#if>
</#macro>

<#macro renderSubmitFieldBU buttonType className alert formName title name event action imgSrc confirmation containerId ajaxUrl>
  <#if buttonType=="text-link">
    <a <@renderClass className alert /> href="javascript:document.${formName}.submit()" <#if confirmation?has_content>onclick="return confirm('${confirmation?js_string}');"</#if>><#if title?has_content>${title}</#if> </a>
  <#elseif buttonType=="image">
    <input type="image" src="${imgSrc}" <@renderClass className alert /><#if name?has_content> name="${name}"</#if>
    <#if title?has_content> alt="${title}"</#if><#if event?has_content> ${event}="${action}"</#if>
    <#if confirmation?has_content>onclick="return confirm('${confirmation?js_string}');"</#if>/>
  <#else>
    <input type="<#if containerId?has_content>button<#else>submit</#if>" <@renderClass className alert />
    <#if name?exists> name="${name}"</#if><#if title?has_content> value="${title}"</#if><#if event?has_content> ${event}="${action}"</#if>
    <#if containerId?has_content> onclick="<#if confirmation?has_content>if (confirm('${confirmation?js_string}')) </#if>ajaxSubmitFormUpdateAreas('${containerId}', '${ajaxUrl}')"
      <#else><#if confirmation?has_content> onclick="return confirm('${confirmation?js_string}');"</#if>
    </#if>/>
  </#if>
</#macro>

<#macro renderResetField className alert name title>
  <input type="reset" <@renderClass className alert /> name="${name}"<#if title?has_content> value="${title}"</#if>/>
</#macro>

<#macro renderHiddenField name value id event action>
  <input type="hidden" name="${name}"<#if value?has_content> value="${value}"</#if><#if id?has_content> id="${id}"</#if><#if event?has_content && action?has_content> ${event}="${action}"</#if>/>
</#macro>

<#macro renderIgnoredField></#macro>

<#macro renderFieldTitle style title id fieldHelpText="" for="">
  <label <#if for?has_content>for="${for}"</#if> <#if fieldHelpText?has_content> title="${fieldHelpText}"</#if><#if style?has_content> class="${style}"</#if><#if id?has_content> id="${id}"</#if>><#t/>
    ${title}<#t/>
  </label><#t/>
</#macro>

<#macro renderSingleFormFieldTitle></#macro>

<#macro renderFormOpen linkUrl formType targetWindow containerId containerStyle autocomplete name viewIndexField viewSizeField viewIndex viewSize useRowSubmit>
  <form method="post" action="${linkUrl}"<#if formType=="upload"> enctype="multipart/form-data"</#if><#if targetWindow?has_content> target="${targetWindow}"</#if><#if containerId?has_content> id="${containerId}"</#if> class=<#if containerStyle?has_content>"${containerStyle} form-horizontal"<#else>"basic-form form-horizontal"</#if> onsubmit="javascript:submitFormDisableSubmits(this)"<#if autocomplete?has_content> autocomplete="${autocomplete}"</#if> name="${name}"><#lt/>
    <#if useRowSubmit?has_content && useRowSubmit>
      <input type="hidden" name="_useRowSubmit" value="Y"/>
      <#if linkUrl?index_of("VIEW_INDEX") &lt;= 0 && linkUrl?index_of(viewIndexField) &lt;= 0>
        <input type="hidden" name="${viewIndexField}" value="${viewIndex}"/>
      </#if>
      <#if linkUrl?index_of("VIEW_SIZE") &lt;= 0 && linkUrl?index_of(viewSizeField) &lt;= 0>
        <input type="hidden" name="${viewSizeField}" value="${viewSize}"/>
      </#if>
    </#if>
    <div class="row-fluid">
</#macro>
<#macro renderFormClose focusFieldName formName containerId hasRequiredField>
  </div></form><#lt/>
  <#if focusFieldName?has_content>
    <script language="JavaScript" type="text/javascript">
      var form = document.${formName};
      form.${focusFieldName}.focus();
      <#-- enable the validation plugin for all generated forms
      only enable the validation if min one field is marked as 'required' -->
      if (jQuery(form).find(".required").size() > 0) {
          jQuery(form).validate();
      }
    </script><#lt/>
  </#if>
  <#if containerId?has_content && hasRequiredField?has_content>
    <script type="text/javascript">
      jQuery("#${containerId}").validate({
        errorElement: 'div',
        errorClass: "invalid",
        errorPlacement: function(error, element) {
            element.addClass("border-error");
            if (element.parent() != null ){
                element.parent().find("button").addClass("button-border");
                error.appendTo(element.parent());
            }
          },
        unhighlight: function(element, errorClass) {
            $(element).removeClass("border-error");
            $(element).parent().find("button").removeClass("button-border");
        },
        submitHandler:
          function(form) {
            form.submit();
          }
      });
    </script>
  </#if>
</#macro>
<#macro renderMultiFormClose>
  </form><#lt/>
</#macro>

<#macro renderFormatListWrapperOpen formName style columnStyles>
  <table cellspacing="0" class="<#if style?has_content>${style}<#else>basic-table form-widget-table dark-grid</#if>"><#lt/>
</#macro>

<#macro renderFormatListWrapperClose formName>
  </table><#lt/>
</#macro>

<#macro renderFormatHeaderRowOpen style>
  <thead><tr role="row" class="<#if style?has_content>${style}<#else>header-row</#if>">
</#macro>
<#macro renderFormatHeaderRowClose>
  </tr></thead>
</#macro>
<#macro renderFormatHeaderRowCellOpen style positionSpan>
  <th class="hidden-phone"><i class="${style}"></i>${style}
</#macro>
<#macro renderFormatHeaderRowCellClose>
  </th>
</#macro>

<#macro renderFormatHeaderRowFormCellOpen style>
  <td <#if style?has_content>class="${style}"</#if>>
</#macro>
<#macro renderFormatHeaderRowFormCellClose>
  </td>
</#macro>
<#macro renderFormatHeaderRowFormCellTitleSeparator style isLast>
  <#if style?has_content><span class="${style}"></#if> - <#if style?has_content></span></#if>
</#macro>

<#macro renderFormatItemRowOpen formName itemIndex altRowStyles evenRowStyle oddRowStyle>
  <tr <#if itemIndex?has_content><#if itemIndex%2==0><#if evenRowStyle?has_content>class="${evenRowStyle}<#if altRowStyles?has_content> ${altRowStyles}</#if>"<#elseif altRowStyles?has_content>class="${altRowStyles}"</#if><#else><#if oddRowStyle?has_content>class="${oddRowStyle}<#if altRowStyles?has_content> ${altRowStyles}</#if>"<#elseif altRowStyles?has_content>class="${altRowStyles}"</#if></#if></#if> >
</#macro>
<#macro renderFormatItemRowClose formName>
  </tr>
</#macro>
<#macro renderFormatItemRowCellOpen fieldName style positionSpan>
  <td <#if positionSpan?has_content && positionSpan gt 1>colspan="${positionSpan}"</#if><#if style?has_content>class="${style}"</#if>>
</#macro>
<#macro renderFormatItemRowCellClose fieldName>
  </td>
</#macro>
<#macro renderFormatItemRowFormCellOpen style>
  <td<#if style?has_content> class="${style}"</#if>>
</#macro>
<#macro renderFormatItemRowFormCellClose>
  </td>
</#macro>

<#macro renderFormatSingleWrapperOpen formName style>
  <#--
  <table cellspacing="0" <#if style?has_content>class="${style}"</#if>>
  -->
</#macro>
<#macro renderFormatSingleWrapperClose formName>
  <#--
  </table>
  -->
</#macro>

<#macro renderFormatFieldRowOpen>
  <#--
  <tr>
  -->
  <div class="control-group no-left-margin">
</#macro>
<#macro renderFormatFieldRowOpenRow style>
  <#--
  <tr>
  -->
  <div class="control-group no-left-margin ${style}">
</#macro>
<#--
<#macro renderFormatFieldRowOpenRow widgetStyleRow>
  <div class="control-group no-left-margin ${widgetStyleRow}">
</#macro>
-->
<#macro renderFormatFieldRowClose>
  <#--
  </tr>
  -->
  </div>
</#macro>
<#macro renderFormatFieldRowTitleCellOpen style>
  <#--
  <td class="<#if style?has_content>${style}<#else>label</#if>">
  -->
  <label class="${style}">
</#macro>
<#macro renderFormatFieldRowTitleCellClose>
  </label>
</#macro>
<#macro renderFormatFieldRowSpacerCell></#macro>
<#macro renderFormatFieldRowWidgetCellOpen positionSpan style>
  <#--
  <td<#if positionSpan?has_content && positionSpan gt 0> colspan="${1+positionSpan*3}"</#if><#if style?has_content> class="${style}"</#if>>
  -->
  <div class="controls">
</#macro>
<#macro renderFormatFieldRowWidgetCellClose>
  <#--
  </td>
  -->
  </div>
</#macro>

<#--
    Initial work to convert table based layout for "single" form to divs.
<#macro renderFormatSingleWrapperOpen style> <div <#if style?has_content>class="${style}"</#if> ></#macro>
<#macro renderFormatSingleWrapperClose> </div></#macro>

<#macro renderFormatFieldRowOpen>  <div></#macro>
<#macro renderFormatFieldRowClose>  </div></#macro>
<#macro renderFormatFieldRowTitleCellOpen style>   <div class="<#if style?has_content>${style}<#else>label</#if>"></#macro>
<#macro renderFormatFieldRowTitleCellClose></div></#macro>
<#macro renderFormatFieldRowSpacerCell></#macro>
<#macro renderFormatFieldRowWidgetCellOpen positionSpan style>   <div<#if positionSpan?has_content && positionSpan gt 0> colspan="${1+positionSpan*3}"</#if><#if style?has_content> class="${style}"</#if>></#macro>
<#macro renderFormatFieldRowWidgetCellClose></div></#macro>

-->


<#macro renderFormatEmptySpace>&nbsp;</#macro>

<#macro renderTextFindField name value defaultOption opEquals opBeginsWith opContains opIsEmpty opNotEqual className alert size maxlength autocomplete titleStyle hideIgnoreCase ignCase ignoreCase>
  <#if opEquals?has_content>
    <select <#if name?has_content>name="${name}_op"</#if>    class="selectBox"><#rt/>
      <option value="equals"<#if defaultOption=="equals"> selected="selected"</#if>>${opEquals}</option><#rt/>
      <option value="like"<#if defaultOption=="like"> selected="selected"</#if>>${opBeginsWith}</option><#rt/>
      <option value="contains"<#if defaultOption=="contains"> selected="selected"</#if>>${opContains}</option><#rt/>
      <option value="empty"<#rt/><#if defaultOption=="empty"> selected="selected"</#if>>${opIsEmpty}</option><#rt/>
      <option value="notEqual"<#if defaultOption=="notEqual"> selected="selected"</#if>>${opNotEqual}</option><#rt/>
    </select>
  <#else>
    <input type="hidden" name=<#if name?has_content> "${name}_op"</#if>    value="${defaultOption}"/><#rt/>
  </#if>
    <input type="text" <@renderClass className alert /> name="${name}"<#if value?has_content> value="${value}"</#if><#if size?has_content> size="${size}"</#if><#if maxlength?has_content> maxlength="${maxlength}"</#if><#if autocomplete?has_content> autocomplete="off"</#if>/><#rt/>
    <#if titleStyle?has_content><span class="${titleStyle}" ><#rt/></#if>
    <label style="display:inline;">
        <#if hideIgnoreCase>
          <input type="hidden" name="${name}_ic" value=<#if ignCase>"Y"<#else> ""</#if>/><#rt/>
        <#else>
          <input style="height:20px;" type="checkbox" name="${name}_ic" value="Y" <#if ignCase> checked="checked"</#if> /> <span class="lbl">${ignoreCase}</span><#rt/>
        </#if>
    </label>
    <#if titleStyle?has_content>
  </#if>
  <input type="checkbox" />
</#macro>

<#macro renderDateFindField className alert name localizedInputTitle value size maxlength dateType formName defaultDateTimeString imgSrc localizedIconTitle titleStyle defaultOptionFrom defaultOptionThru opEquals opSameDay opGreaterThanFromDayStart opGreaterThan opGreaterThan opLessThan opUpToDay opUpThruDay opIsEmpty>
  <span class="view-calendar">
    <input id="${name?html}_fld0_value" type="text" <@renderClass className alert /><#if name?has_content> name="${name?html}_fld0_value"</#if><#if localizedInputTitle?has_content> title="${localizedInputTitle}"</#if><#if value?has_content> value="${value}"</#if><#if size?has_content> size="${size}"</#if><#if maxlength?has_content> maxlength="${maxlength}"</#if>/><#rt/>
    <#if dateType != "time">
      <script type="text/javascript">
        <#if dateType == "date">
          jQuery("#${name?html}_fld0_value").datepicker({
        <#else>
          jQuery("#${name?html}_fld0_value").datetimepicker({
            showSecond: true,
            <#-- showMillisec: true, -->
            timeFormat: 'hh:mm:ss',
            stepHour: 1,
            stepMinute: 5,
            stepSecond: 10,
        </#if>
            showOn: 'button',
            buttonImage: '',
            buttonText: '',
            buttonImageOnly: false,
            dateFormat: 'yy-mm-dd'
          });
      </script>
      <#rt/>
    </#if>
    <#if titleStyle?has_content>
      <span class="${titleStyle}"><#rt/>
    </#if>
    <select<#if name?has_content> name="${name}_fld0_op"</#if> class="selectBox"><#rt/>
      <option value="equals"<#if defaultOptionFrom=="equals"> selected="selected"</#if>>${opEquals}</option><#rt/>
      <option value="sameDay"<#if defaultOptionFrom=="sameDay"> selected="selected"</#if>>${opSameDay}</option><#rt/>
      <option value="greaterThanFromDayStart"<#if defaultOptionFrom=="greaterThanFromDayStart"> selected="selected"</#if>>${opGreaterThanFromDayStart}</option><#rt/>
      <option value="greaterThan"<#if defaultOptionFrom=="greaterThan"> selected="selected"</#if>>${opGreaterThan}</option><#rt/>
    </select><#rt/>
    <#if titleStyle?has_content>
      </span><#rt/>
    </#if>
    <#rt/>
    <input id="${name?html}_fld1_value" type="text" <@renderClass className alert /><#if name?has_content> name="${name}_fld1_value"</#if><#if localizedInputTitle?exists> title="${localizedInputTitle?html}"</#if><#if value2?has_content> value="${value2}"</#if><#if size?has_content> size="${size}"</#if><#if maxlength?has_content> maxlength="${maxlength}"</#if>/><#rt/>
    <#if dateType != "time">
      <script type="text/javascript">
        <#if dateType == "date">
          jQuery("#${name?html}_fld1_value").datepicker({
        <#else>
          jQuery("#${name?html}_fld1_value").datetimepicker({
            showSecond: true,
            <#-- showMillisec: true, -->
            timeFormat: 'hh:mm:ss',
            stepHour: 1,
            stepMinute: 5,
            stepSecond: 10,
        </#if>
            showOn: 'button',
            buttonImage: '',
            buttonText: '',
            buttonImageOnly: false,
            dateFormat: 'yy-mm-dd'
          });
      </script>
      <#rt/>
    </#if>
    <#if titleStyle?has_content>
      <span class="${titleStyle}"><#rt/>
    </#if>
    <select name=<#if name?has_content>"${name}_fld1_op"</#if> class="selectBox"><#rt/>
      <option value="opLessThan"<#if defaultOptionThru=="opLessThan"> selected="selected"</#if>>${opLessThan}</option><#rt/>
      <option value="upToDay"<#if defaultOptionThru=="upToDay"> selected="selected"</#if>>${opUpToDay}</option><#rt/>
      <option value="upThruDay"<#if defaultOptionThru=="upThruDay"> selected="selected"</#if>>${opUpThruDay}</option><#rt/>
      <option value="empty"<#if defaultOptionFrom=="empty"> selected="selected"</#if>>${opIsEmpty}</option><#rt/>
    </select><#rt/>
    <#if titleStyle?has_content>
      </span>
    </#if>
  </span>
</#macro>

<#macro renderRangeFindField className alert name value size maxlength autocomplete titleStyle defaultOptionFrom opEquals opGreaterThan opGreaterThanEquals opLessThan opLessThanEquals value2 defaultOptionThru>
  <input type="text" <@renderClass className alert /> <#if name?has_content>name="${name}_fld0_value"</#if><#if value?has_content> value="${value}"</#if><#if size?has_content> size="${size}"</#if><#if maxlength?has_content> maxlength="${maxlength}"</#if><#if autocomplete?has_content> autocomplete="off"</#if>/><#rt/>
  <#if titleStyle?has_content>
    <span class="${titleStyle}" ><#rt/>
  </#if>
  <select <#if name?has_content>name="${name}_fld0_op"</#if> class="selectBox"><#rt/>
    <option value="equals"<#if defaultOptionFrom=="equals"> selected="selected"</#if>>${opEquals}</option><#rt/>
    <option value="greaterThan"<#if defaultOptionFrom=="greaterThan"> selected="selected"</#if>>${opGreaterThan}</option><#rt/>
    <option value="greaterThanEqualTo"<#if defaultOptionFrom=="greaterThanEqualTo"> selected="selected"</#if>>${opGreaterThanEquals}</option><#rt/>
  </select><#rt/>
  <#if titleStyle?has_content>
    </span><#rt/>
  </#if>
  <br /><#rt/>
  <input type="text" <@renderClass className alert /><#if name?has_content> name="${name}_fld1_value"</#if><#if value2?has_content> value="${value2}"</#if><#if size?has_content> size="${size}"</#if><#if maxlength?has_content> maxlength="${maxlength}"</#if><#if autocomplete?has_content> autocomplete="off"</#if>/><#rt/>
  <#if titleStyle?has_content>
    <span class="${titleStyle}" ><#rt/>
  </#if>
  <select name=<#if name?has_content>"${name}_fld1_op"</#if> class="selectBox"><#rt/>
    <option value="lessThan"<#if defaultOptionThru=="lessThan"> selected="selected"</#if>>${opLessThan?html}</option><#rt/>
    <option value="lessThanEqualTo"<#if defaultOptionThru=="lessThanEqualTo"> selected="selected"</#if>>${opLessThanEquals?html}</option><#rt/>
  </select><#rt/>
  <#if titleStyle?has_content>
    </span>
  </#if>
</#macro>

<#--
@renderLookupField

Description: Renders a text input field as a lookup field.

Parameter: name, String, required - The name of the lookup field.
Parameter: formName, String, required - The name of the form that contains the lookup field.
Parameter: fieldFormName, String, required - Contains the lookup window form name.
Parameter: className, String, optional - The CSS class name for the lookup field.
Parameter: alert, String, optional - If "true" then the "alert" CSS class will be added to the lookup field.
Parameter: value, Object, optional - The value of the lookup field.
Parameter: size, String, optional - The size of the lookup field.
Parameter: maxlength, String or Integer, optional - The max length of the lookup field.
Parameter: id, String, optional - The ID of the lookup field.
Parameter: event, String, optional - The lookup field event that invokes "action". If the event parameter is not empty, then the action parameter must be specified as well.
Parameter: action, String, optional - The action that is invoked on "event". If action parameter is not empty, then the event parameter must be specified as well.
Parameter: readonly, boolean, optional - If true, the lookup field is made read-only.
Parameter: autocomplete, String, optional - If not empty, autocomplete is turned off for the lookup field.
Parameter: descriptionFieldName, String, optional - If not empty and the presentation parameter contains "window", specifies an alternate input field for updating.
Parameter: targetParameterIter, List, optional - Contains a list of form field names whose values will be passed to the lookup window.
Parameter: imgSrc, Not used.
Parameter: ajaxUrl, String, optional - Contains the Ajax URL, used only when the ajaxEnabled parameter contains true.
Parameter: ajaxEnabled, boolean, optional - If true, invokes the Ajax auto-completer.
Parameter: presentation, String, optional - Contains the lookup window type, either "layer" or "window".
Parameter: width, String or Integer, optional - The width of the lookup field.
Parameter: height, String or Integer, optional - The height of the lookup field.
Parameter: position, String, optional - The position style of the lookup field.
Parameter: fadeBackground, ?
Parameter: clearText, String, optional - If the readonly parameter is true, clearText contains the text to be displayed in the field, default is CommonClear label.
Parameter: showDescription, String, optional - If the showDescription parameter is true, a special span with css class "tooltip" will be created at right of the lookup button and a description will fill in (see setLookDescription in selectall.js). For now not when the lookup is read only.
Parameter: initiallyCollapsed, Not used.
Parameter: lastViewName, String, optional - If the ajaxEnabled parameter is true, the contents of lastViewName will be appended to the Ajax URL.
Parameter: zIndex, String, optional - set z-index for dialog
-->
<#macro renderLookupField name formName fieldFormName className="" alert="false" value="" size="" maxlength="" id="" event=""  action="" readonly=false autocomplete="" descriptionFieldName="" targetParameterIter="" imgSrc="" ajaxUrl="" ajaxEnabled=false javaScriptEnabled=false presentation="layer" width="" height="" position="" fadeBackground="true" clearText="" showDescription="" initiallyCollapsed="" title="" zIndex="" lastViewName="main">
  <#if Static["org.ofbiz.widget.ModelWidget"].widgetBoundaryCommentsEnabled(context)>
  <!-- @renderLookupField -->
  </#if>
  <#if (!ajaxUrl?has_content) && ajaxEnabled?has_content && ajaxEnabled>
    <#local ajaxUrl = requestAttributes._REQUEST_HANDLER_.makeLink(request, response, fieldFormName)/>
    <#local ajaxUrl = id + "," + ajaxUrl + ",ajaxLookup=Y" />
  </#if>
  <#if (!showDescription?has_content)>
    <#local showDescriptionProp = Static["org.ofbiz.base.util.UtilProperties"].getPropertyValue("widget.properties", "widget.lookup.showDescription", "N")>
    <#if "Y" == showDescriptionProp>
      <#local showDescription = "true" />
    <#else>
      <#local showDescription = "false" />
    </#if>
  </#if>
  <#if ajaxEnabled?has_content && ajaxEnabled>
    <script type="text/javascript">
      jQuery(document).ready(function(){
        if (!jQuery('form[name="${formName}"]').length) {
          alert("Developer: for lookups to work you must provide a form name!")
        }
      });
    </script>
    <style type="text/css">
        .ui-dialog{
            padding: 0px !important;
            <!--position : absolute !important;-->
        }
        .ui-widget-header{
            border: 1px solid #FFF !important;
            opacity:1 !important;
        }
        .ui-corner-all{
            border-bottom-right-radius: 0px !important;
            border-bottom-left-radius: 0px !important;
            border-top-right-radius: 0px !important;
            border-top-left-radius: 0px !important;
        }
        .ui-widget-overlay{
            opacity: 0.9 !important;
            background-color: black !important;
        }
      </style>
  </#if>
  <span class="field-lookup">
    <#if size?has_content && size=="0">
      <input type="hidden" <#if name?has_content> name="${name}"/></#if>
    <#else>
      <input type="text" <@renderClass className alert /><#if name?has_content> name="${name}"</#if><#if value?has_content> value="${value}"</#if>
        <#if size?has_content> size="${size}"</#if><#if maxlength?has_content> maxlength="${maxlength}"</#if><#if id?has_content> id="${id}"</#if><#rt/>
        <#if readonly?has_content && readonly> readonly="readonly"</#if><#rt/><#if event?has_content && action?has_content> ${event}="${action}"</#if><#rt/>
        <#if autocomplete?has_content> autocomplete="off"</#if>/><#rt/></#if>
    <#if presentation?has_content && descriptionFieldName?has_content && presentation == "window">
      <a href="javascript:call_fieldlookup3(document.${formName?html}.${name?html},document.${formName?html}.${descriptionFieldName},'${fieldFormName}', '${presentation}'<#rt/>
      <#if targetParameterIter?has_content>
        <#list targetParameterIter as item>
          ,document.${formName}.${item}.value<#rt>
        </#list>
      </#if>
      );"></a><#rt>
    <#elseif presentation?has_content && presentation == "window">
      <a href="javascript:call_fieldlookup2(document.${formName?html}.${name?html},'${fieldFormName}', '${presentation}'<#rt/>
      <#if targetParameterIter?has_content>
        <#list targetParameterIter as item>
          ,document.${formName}.${item}.value<#rt>
        </#list>
      </#if>
      );"></a><#rt>
    <#else>
      <#if ajaxEnabled?has_content && ajaxEnabled>
        <#assign defaultMinLength = Static["org.ofbiz.base.util.UtilProperties"].getPropertyValue("widget.properties", "widget.autocompleter.defaultMinLength")>
        <#assign defaultDelay = Static["org.ofbiz.base.util.UtilProperties"].getPropertyValue("widget.properties", "widget.autocompleter.defaultDelay")>
        <#local ajaxUrl = ajaxUrl + "&amp;_LAST_VIEW_NAME_=" + lastViewName />
        <#if !ajaxUrl?contains("searchValueFieldName=")>
          <#if descriptionFieldName?has_content && showDescription == "true">
            <#local ajaxUrl = ajaxUrl + "&amp;searchValueFieldName=" + descriptionFieldName />
          <#else>
            <#local ajaxUrl = ajaxUrl + "&amp;searchValueFieldName=" + name />
          </#if>
        </#if>
      </#if>
      <script type="text/javascript">
        jQuery(document).ready(function(){
          var options = {
            requestUrl : "${fieldFormName}",
            inputFieldId : "${id}",
            dialogTarget : document.${formName?html}.${name?html},
            dialogOptionalTarget : <#if descriptionFieldName?has_content>document.${formName?html}.${descriptionFieldName}<#else>null</#if>,
            formName : "${formName?html}",
            width : <#if width?has_content>"${width}"<#else>"1000"</#if>,
            height : <#if height?has_content>"${height}"<#else>"600"</#if>,
            position : "center",
            modal : "${fadeBackground}",
            ajaxUrl : <#if ajaxEnabled?has_content && ajaxEnabled>"${ajaxUrl}"<#else>""</#if>,
            showDescription : <#if ajaxEnabled?has_content && ajaxEnabled>"${showDescription}"<#else>false</#if>,
            presentation : "${presentation!}",
            defaultMinLength : "${defaultMinLength!2}",
            defaultDelay : "${defaultDelay!300}",
            show : { effect: 'slide-up',duration :400 },
            hide: { effect: 'slide-up',duration : 100 },
            title : <#if title?has_content>"${title}"<#else>""</#if>,
            zIndex: "${zIndex}",
            args :
              <#rt/>
                <#if targetParameterIter?has_content>
                  <#assign isFirst = true>
                  <#lt/>[<#rt/>
                  <#list targetParameterIter as item>
                    <#if isFirst>
                      <#lt/>document.${formName}.${item}<#rt/>
                      <#assign isFirst = false>
                    <#else>
                      <#lt/> ,document.${formName}.${item}<#rt/>
                    </#if>
                  </#list>
                  <#lt/>]<#rt/>
                <#else>[]
                </#if>
                <#lt/>
          };
          new Lookup(options).init();
        });
      </script>
    </#if>
    <#if readonly?has_content && readonly>
      <a id="${id}_clear"
        style="background:none;margin-left:5px;margin-right:15px;"
        class="clearField"
        href="javascript:void(0);"
        onclick="javascript:document.${formName}.${name}.value='';
          jQuery('#' + jQuery('#${id}_clear').next().attr('id').replace('_button','') + '_${id}_lookupDescription').html('');
          <#if descriptionFieldName?has_content>document.${formName}.${descriptionFieldName}.value='';</#if>">
          <#if clearText?has_content>${clearText}<#else>${uiLabelMap.CommonClear}</#if>
      </a>
    </#if>
  </span>
  <#if ajaxEnabled?has_content && ajaxEnabled && (presentation?has_content && presentation == "window")>
    <#if ajaxUrl?index_of("_LAST_VIEW_NAME_") < 0>
      <#local ajaxUrl = ajaxUrl + "&amp;_LAST_VIEW_NAME_=" + lastViewName />
    </#if>
    <script language="JavaScript" type="text/javascript">ajaxAutoCompleter('${ajaxUrl}', ${showDescription}, ${defaultMinLength!2}, ${defaultDelay!300});</script><#t/>
  </#if>
</#macro>

<#macro renderNextPrev paginateStyle paginateFirstStyle viewIndex highIndex listSize viewSize ajaxEnabled javaScriptEnabled ajaxFirstUrl firstUrl paginateFirstLabel paginatePreviousStyle ajaxPreviousUrl previousUrl paginatePreviousLabel pageLabel ajaxSelectUrl selectUrl ajaxSelectSizeUrl selectSizeUrl commonDisplaying paginateNextStyle ajaxNextUrl nextUrl paginateNextLabel paginateLastStyle ajaxLastUrl lastUrl paginateLastLabel paginateViewSizeLabel renderBottom=true>
  <#if listSize gt 0>
  <#if listSize gt viewSize>
    <div class="${paginateStyle} row-fluid dataTables_paginate paging_bootstrap pagination">&nbsp;
      <div style="float:left">
      <ul>
        <li class="nav-displaying">${commonDisplaying}</li>
        <#if javaScriptEnabled><li class="nav-pagesize">,
        <select name="pageSize" size="1" onchange="<#if ajaxEnabled>javascript:ajaxUpdateAreas('${ajaxSelectSizeUrl}')<#else>location.href='${selectSizeUrl}';</#if>">
        <#rt/>
          <#assign availPageSizes = [10, 20, 30, 50, 100, 200]>
          <#list availPageSizes as ps>
            <option<#if viewSize == ps> selected="selected" </#if> value="${ps}">${ps}</option>
          </#list>
          </select> ${paginateViewSizeLabel}</li>
        </#if>
      </ul>
      </div>
      <div style="float:right;padding-right:15px">
      <ul>
        <li class="${paginateFirstStyle}<#if viewIndex gt 0>"><a href="<#if ajaxEnabled>javascript:ajaxUpdateAreas('${ajaxFirstUrl}')<#else>${firstUrl}</#if>"><i class="icon-double-angle-left"></i></a><#else>-disabled"><span><i class="icon-double-angle-left"></i></span></#if></li>
        <li class="${paginatePreviousStyle}<#if viewIndex gt 0>"><a href="<#if ajaxEnabled>javascript:ajaxUpdateAreas('${ajaxPreviousUrl}')<#else>${previousUrl}</#if>"><i class="icon-angle-left"></i></a><#else>-disabled"><span><i class="icon-angle-left"></i></span></#if></li>
        <#if listSize gt 0 && javaScriptEnabled><li class="nav-page-select">
            <select name="page" size="1" style="display:none;"></select>
            <script type="text/javascript">
                function pagenvg(inputvalue){
                    var div = document.createElement('div');
                    div.innerHTML = "${selectUrl}";
                    var decoded = div.firstChild.nodeValue;
                    return decoded + inputvalue;
                }
            </script>
            <#rt/>
          <#assign x=(listSize/viewSize)?ceiling>
            <#if (x>5)>
                <#if (viewIndex < 3)>
                    <#list 1..4 as i>
                      <#if i == (viewIndex+1)>
                        <li class="active"><a
                      <#else><li><a href="<#if ajaxEnabled>javascript:ajaxUpdateAreas('${ajaxSelectUrl}')<#else>${selectUrl}${i-1}</#if>"
                      </#if> >${i}</a>
                        </li>
                    </#list>
                    <li><a>-></a></li>
                    <li><a href="<#if ajaxEnabled>javascript:ajaxUpdateAreas('${ajaxSelectUrl}')<#else>${selectUrl}${x-1}</#if>">${x}</a>
                <#else>
                    <#if (x-viewIndex <4)>
                        <li><a href="<#if ajaxEnabled>javascript:ajaxUpdateAreas('${ajaxSelectUrl}')<#else>${selectUrl}0</#if>">1</a>
                        <li><a><-</a></li>
                        <#list 3..0 as i>
                          <#if (x-i) == (viewIndex+1)>
                            <li class="active"><a
                          <#else><li><a href="<#if ajaxEnabled>javascript:ajaxUpdateAreas('${ajaxSelectUrl}')<#else>${selectUrl}${x-i-1}</#if>"
                          </#if> >${x-i}</a>
                            </li>
                        </#list>
                    <#else>
                        <li><a href="<#if ajaxEnabled>javascript:ajaxUpdateAreas('${ajaxSelectUrl}')<#else>${selectUrl}0</#if>">1</a>
                        <li><a><-</a></li>
                        <li><a href="<#if ajaxEnabled>javascript:ajaxUpdateAreas('${ajaxSelectUrl}')<#else>${selectUrl}${viewIndex-1}</#if>">${viewIndex}</a></li>
                        <li class="active">
                            <a href="<#if ajaxEnabled>javascript:ajaxUpdateAreas('${ajaxSelectUrl}')<#else>${selectUrl}${viewIndex}</#if>">${viewIndex+1}</a>
                        </li>
                        <li><a href="<#if ajaxEnabled>javascript:ajaxUpdateAreas('${ajaxSelectUrl}')<#else>${selectUrl}${viewIndex+1}</#if>">${viewIndex+2}</a></li>
                        <li><a>-></a></li>
                        <li><a href="<#if ajaxEnabled>javascript:ajaxUpdateAreas('${ajaxSelectUrl}')<#else>${selectUrl}${x-1}</#if>">${x}</a>
                    </#if>
                </#if>
            <#else>
                <#list 1..x as i>
                  <#if i == (viewIndex+1)>
                    <li class="active"><a
                  <#else><li><a href="<#if ajaxEnabled>javascript:ajaxUpdateAreas('${ajaxSelectUrl}')<#else>${selectUrl}${i-1}</#if>"
                  </#if> >${i}</a>
                    </li>
                </#list>
            </#if>
        </#if>
        <li class="${paginateNextStyle}<#if highIndex lt listSize>"><a href="<#if ajaxEnabled>javascript:ajaxUpdateAreas('${ajaxNextUrl}')<#else>${nextUrl}</#if>"><i class="icon-angle-right"></i></a><#else>-disabled"><span><i class="icon-angle-right"></i></span></#if></li>
        <li class="${paginateLastStyle}<#if highIndex lt listSize>"><a href="<#if ajaxEnabled>javascript:ajaxUpdateAreas('${ajaxLastUrl}')<#else>${lastUrl}</#if>"><i class="icon-double-angle-right"></i></i></a><#else>-disabled"><span><i class="icon-double-angle-right"></i></span></#if></li>
      </ul>
      </div>
    </div>
  <#else>
    <div  class="${paginateStyle} row-fluid dataTables_paginate paging_bootstrap pagination">
      <div style="float:left">
      <ul style="float:inherit;width:100%;" >
        <li class="nav-displaying">${commonDisplaying}</li>
        <#if javaScriptEnabled><li class="nav-pagesize">, <select name="pageSize" size="1"  onchange="<#if ajaxEnabled>javascript:ajaxUpdateAreas('${ajaxSelectSizeUrl}')<#else>location.href='${selectSizeUrl}';</#if>"><#rt/>
          <#assign availPageSizes = [10, 20, 30, 50, 100, 200]>
          <#list availPageSizes as ps>
            <option<#if viewSize == ps> selected="selected" </#if> value="${ps}">${ps}</option>
          </#list>
          </select> ${paginateViewSizeLabel}</li>
        </#if>
      </ul>
      </div>
    </div>
  </#if>
  </#if>
</#macro>

<#macro renderFileField className alert name value size maxlength autocomplete>
  <input type="file" id="${name}" <@renderClass className alert /><#if name?has_content> name="${name}"</#if><#if value?has_content> value="${value}"</#if><#if size?has_content> size="${size}"</#if><#if maxlength?has_content> maxlength="${maxlength}"</#if><#if autocomplete?has_content> autocomplete="off"</#if>/><#rt/>
    <style type="text/css">
        .ace-file-input{
            width: 220px !important;
        }
    </style>
    <script type="text/javascript">
    function addadditionImages(){
        $('#${name}').ace_file_input({
                no_file:'No File ...',
                btn_choose:'Choose',
                btn_change:'Change',
                droppable:false,
                onchange:null,
                thumbnail:false //| true | large
                //whitelist:'gif|png|jpg|jpeg'
                //blacklist:'exe|php'
                //onchange:''
                //
            });
            }
  </script>
</#macro>
<#macro renderPasswordField className alert name value size maxlength id autocomplete>
  <input type="password" <@renderClass className alert /><#if name?has_content> name="${name}"</#if><#if value?has_content> value="${value}"</#if><#if size?has_content> size="${size}"</#if><#if maxlength?has_content> maxlength="${maxlength}"</#if><#if id?has_content> id="${id}"</#if><#if autocomplete?has_content> autocomplete="off"</#if>/>
</#macro>
<#macro renderImageField value description alternate style event action><img<#if value?has_content> src="${value}"</#if><#if description?has_content> title="${description}"</#if> alt="<#if alternate?has_content>${alternate}"</#if><#if style?has_content> class="${style}"</#if><#if event?has_content> ${event?html}="${action}" </#if>/></#macro>

<#macro renderBanner style leftStyle rightStyle leftText text rightText>
  <table width="100%">
    <tr><#rt/>
      <#if leftText?has_content><td align="left"><#if leftStyle?has_content><div class="${leftStyle}"></#if>${leftText}<#if leftStyle?has_content></div></#if></td><#rt/></#if>
      <#if text?has_content><td align="center"><#if style?has_content><div class="${style}"></#if>${text}<#if style?has_content></div></#if></td><#rt/></#if>
      <#if rightText?has_content><td align="right"><#if rightStyle?has_content><div class="${rightStyle}"></#if>${rightText}<#if rightStyle?has_content></div></#if></td><#rt/></#if>
    </tr>
  </table>
</#macro>

<#macro renderContainerField id className><div id="${id}" class="${className}"/></#macro>

<#macro renderFieldGroupOpen style id title collapsed collapsibleAreaId collapsible expandToolTip collapseToolTip>
  <#if style?has_content || id?has_content || title?has_content><#if style?contains("begin-group-group")><span class="span12 no-left-margin"></#if><div class="<#if style?has_content> ${style}</#if>">
    <#if !style?contains("no-widget-header")>
    <div class="widget-box <#if collapsed && collapsible>collapsed<#else></#if>">
    <div class="widget-header widget-header-small header-color-blue2">
      <#if collapsible>
        <#--
        <ul>
          <li class="<#if collapsed>collapsed">
                      <a onclick="javascript:toggleCollapsiblePanel(this, '${collapsibleAreaId}', '${expandToolTip}', '${collapseToolTip}');">
                    <#else>expanded">
                      <a onclick="javascript:toggleCollapsiblePanel(this, '${collapsibleAreaId}', '${expandToolTip}', '${collapseToolTip}');">
                    </#if>
            &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<#if title?has_content>${title}</#if></a>
          </li>
        </ul>
        -->
        <h6><#if title?has_content>${title}</#if></h6>
        <div class="widget-toolbar">
                <a href="#" data-action="collapse"><i class="icon-chevron-down" onclick="javascript:changeIconChev($(this));toggleScreenlet(this, '${collapsibleAreaId}', 'true', '${expandToolTip}', '${collapseToolTip}');"<#if expandToolTip?has_content> title="${expandToolTip}"</#if>></i></a>
        </div>
      <#else>
        <#if title?has_content>${title}</#if>
      </#if><#rt/>
    </div>
    <div class="widget-body" id="${collapsibleAreaId}">
    <div class="widget-body-inner" style="display: block;">
    <div class="widget-main row-fluid span12">
    </#if>
  </#if>
</#macro>

<#macro renderFieldGroupClose style id title>
<#if style?has_content || id?has_content || title?has_content>
    <#if !style?contains("no-widget-header")>
    </div></div></div></div></div>
    <#else>
    </div>
    </#if>
    <#if style?contains("end-group-group")></span></#if>
</#if>
</#macro>

<#macro renderHyperlinkTitle name title showSelectAll="N">
  <#if title?has_content>${title}<br /></#if>
  <#if showSelectAll="Y"><input type="checkbox" name="selectAll" value="Y" onclick="javascript:toggleAll(this, '${name}');"/></#if>
</#macro>

<#macro renderSortField style title linkUrl ajaxEnabled tooltip="">
  <a<#if style?has_content> class="${style}"</#if> href="<#if ajaxEnabled?has_content && ajaxEnabled>javascript:ajaxUpdateAreas('${linkUrl}')<#else>${linkUrl}</#if>"<#if tooltip?has_content> title="${tooltip}"</#if>>${title}</a>
</#macro>

<#macro formatBoundaryComment boundaryType widgetType widgetName><!-- ${boundaryType}  ${widgetType}  ${widgetName} --></#macro>

<#macro renderTooltip tooltip tooltipStyle>
  <#if tooltip?has_content><span class="<#if tooltipStyle?has_content>${tooltipStyle}<#else>tooltipob</#if>"><#--${tooltip}--></span><#rt/></#if>
</#macro>

<#macro renderClass className="" alert="">
  <#if className?has_content || (alert?has_content && alert=="true")> class="${className}<#if alert?has_content && alert=="true"> alert</#if>" </#if>
</#macro>

<#macro renderAsterisks requiredField requiredStyle>
  <#if requiredField=="true"><#if !requiredStyle?has_content></#if></#if>
</#macro>

<#macro makeHiddenFormLinkForm actionUrl name parameters targetWindow>
  <form method="post" action="${actionUrl}" <#if targetWindow?has_content>target="${targetWindow}"</#if> onsubmit="javascript:submitFormDisableSubmits(this)" name="${name}">
    <#list parameters as parameter>
      <input name="${parameter.name}" value="${parameter.value}" type="hidden"/>
    </#list>
  </form>
</#macro>
<#macro makeHiddenFormLinkAnchor linkStyle hiddenFormName event action imgSrc description confirmation>
  <a <#if linkStyle?has_content>class="${linkStyle}"</#if> href="javascript:document.${hiddenFormName}.submit()"
    <#if action?has_content && event?has_content> ${event}="${action}"</#if>
    <#if confirmation?has_content> onclick="return confirm('${confirmation?js_string}')"</#if>>
      <#if imgSrc?has_content><img src="${imgSrc}" alt=""/></#if>${description}</a>
</#macro>
<#macro makeHyperlinkString linkStyle hiddenFormName event action imgSrc title alternate linkUrl targetWindow description confirmation>
    <a <#if linkStyle?has_content>class="${linkStyle}"</#if>
      href="${linkUrl}"<#if targetWindow?has_content> target="${targetWindow}"</#if>
      <#if action?has_content && event?has_content> ${event}="${action}"</#if>
      <#if confirmation?has_content> onclick="return confirm('${confirmation?js_string}')"</#if>
      <#if imgSrc?length == 0 && title?has_content> title="${title}"</#if>>
        <#if imgSrc?has_content><img src="${imgSrc}" alt="${alternate}" title="${title}"/></#if>${description}</a>
  </#macro>
<#macro test></#macro>
||||||| merged common ancestors
=======
<#--
Licensed to the Apache Software Foundation (ASF) under one
or more contributor license agreements.  See the NOTICE file
distributed with this work for additional information
regarding copyright ownership.  The ASF licenses this file
to you under the Apache License, Version 2.0 (the
"License"); you may not use this file except in compliance
with the License.  You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing,
software distributed under the License is distributed on an
"AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
KIND, either express or implied.  See the License for the
specific language governing permissions and limitations
under the License.
-->
<script>
    function escapeHTML(a) {
        return a.replace(/&/g,'&amp;').replace(/</g,'&lt;').replace(/>/g,'&gt;');
    }
    
</script>
<#--
    jq renderer
-->
<#macro jqMinimumLib>
    <!-- add the jQWidgets framework -->
    <#--<script type="text/javascript">
        if(jqxCoreLoaded == undefined && !jqxCoreLoaded){
            $.getScript("/aceadmin/jqw/jqwidgets/jqxcore.js");
        }
    </script>-->
</#macro>
<#macro jqDataMinimumLib>
    <@jqMinimumLib />
    <script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxbuttons.js"></script>
    <script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxscrollbar.js"></script>
    <script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxdatatable.js"></script>
    <script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxdata.js"></script>
</#macro>
<#macro jqTable url entityName columnlist dataField pageable="true" viewSize="20" columnsResize="true" width="1000" dataType="json" 
        sortable="true" filterable="true">
    <@jqDataMinimumLib/>
    <script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxdropdownlist.js"></script>
    <script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxlistbox.js"></script>
    <script type="text/javascript">
        $(document).ready(function () {
            // prepare the data
            var source =
            {
                dataType: '${dataType}',
                dataFields: [
                    ${dataField}
                ],
                id: 'id',
                type: 'POST',
                data: {
                    noConditionFind: "Y"
                },
                sortcolumn: 'glAccountId',
                entityName: '${entityName}',
                sortdirection: 'asc',
                contentType: 'application/x-www-form-urlencoded',
                url: '${url}'
            };
            var filterChanged = false;
            var dataAdapter = new $.jqx.dataAdapter(source,
                {
                    formatData: function (data) {
                        <#if sortable == "true">
                            if (source.totalRecords) {
                                // update the $skip and $top params of the OData service.
                                // data.pagenum - page number starting from 0.
                                // data.pagesize - page size
                                // data.sortdatafield - the column's datafield value(ShipCountry, ShipCity, etc.).
                                // data.sortorder - the sort order(asc or desc).
                                if (data.sortdatafield && data.sortorder) {
                                    data.$orderby = data.sortdatafield + " " + data.sortorder;
                                }
                            }
                        </#if>
                        <#if pageable == "true">
                            // update the $skip and $top params of the OData service.
                            // data.pagenum - page number starting from 0.
                            // data.pagesize - page size
                             data.$skip = data.pagenum * data.pagesize;
                             data.$top = data.pagesize;
                             data.$inlinecount = "allpages";
                        </#if>
                        <#if filterable == "true">
                            if (data.filterslength) {
                                filterChanged = true;
                                var filterParam = "";
                                for (var i = 0; i < data.filterslength; i++) {
                                    // filter's value.
                                    var filterValue = data["filtervalue" + i];
                                    // filter's condition. For the filterMode="simple" it is "CONTAINS".
                                    var filterCondition = data["filtercondition" + i];
                                    // filter's data field - the filter column's datafield value.
                                    var filterDataField = data["filterdatafield" + i];
                                    // "and" or "or" depending on the filter expressions. When the filterMode="simple", the value is "or".
                                    var filterOperator = data[filterDataField + "operator"];
                                    var startIndex = 0;
                                    if (filterValue.indexOf('-') == -1) {
                                        if (filterCondition == "CONTAINS") {
                                            filterParam += "substringof('" + filterValue + "', " + filterDataField + ") eq true";
                                            filterParam += " " + filterOperator + " ";
                                        }
                                    }
                                    else {
                                        if (filterDataField == "ShippedDate") {
                                            var dateGroups = new Array();
                                            var startIndex = 0;
                                            var item = filterValue.substring(startIndex).indexOf('-');
                                            while (item > -1) {
                                                dateGroups.push(filterValue.substring(startIndex, item + startIndex));
                                                startIndex += item + 1;
                                                item = filterValue.substring(startIndex).indexOf('-');
                                                if (item == -1) {
                                                    dateGroups.push(filterValue.substring(startIndex));
                                                }
                                            }
                                            if (dateGroups.length == 3) {
                                                filterParam += "year(ShippedDate) eq " + parseInt(dateGroups[0]) + " and month(ShippedDate) eq " + parseInt(dateGroups[1]) + " and day(ShippedDate) eq " + parseInt(dateGroups[2]);
                                            }
                                            filterParam += " " + filterOperator + " ";
                                        }
                                    }
                                }
                                // remove last filter operator.
                                filterParam = filterParam.substring(0, filterParam.length - filterOperator.length - 2);
                                data.$filter = filterParam;
                                source.totalRecords = 0;
                            }
                            else {
                                if (filterChanged) {
                                    source.totalRecords = 0;
                                    filterChanged = false;
                                }
                            }
                        </#if>
                        data.entityName = '${entityName}';
                        return data;
                    },
                    downloadComplete: function (data, status, xhr) {
                        if (!source.totalRecords) {
                            source.totalRecords = parseInt(data["odata.count"]);
                        }
                    }
                }
            );
            $("#dataTable").jqxDataTable(
            {
                width: ${width},
                pageable: ${pageable},
                pagerButtonsCount: ${viewSize},
                serverProcessing: true,
                source: dataAdapter,
                filterable: ${filterable},
                filterMode: 'simple',
                sortable: ${sortable},
                columnsReorder: true,
                columnsResize: ${columnsResize},
                columns: [
                  ${columnlist}
              ]
            });
        });
    </script>
    <div id="dataTable"></div>
</#macro>

<#macro jqGridMinimumLib>
    <@jqMinimumLib/>
    <script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxdata.js"></script>
    <script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxbuttons.js"></script>
    <script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxscrollbar.js"></script>
    <script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxlistbox.js"></script>
    <script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxdropdownlist.js"></script>
    <script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxpanel.js"></script>
    <script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxcalendar.js"></script>
    <script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxdatetimeinput.js"></script>
    <script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxdata.export.js"></script>
    <script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxcheckbox.js"></script>
    <script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxnotification2.js"></script>
    <script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxdropdownbutton.js"></script>
    <script type="text/javascript" src="/aceadmin/jqw/jqwidgets/globalization/globalize.js"></script>
    <script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxwindow.js"></script>
    <script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxnumberinput.js"></script>
    <script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxinput.js"></script>
    <script type="text/javascript" src="/aceadmin/jqw/demos/jqxgrid/localization.js"></script>
    <script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxgrid2.full.js"></script>
    <script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxgrid.filter2.full.js"></script>
    <script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxgrid.sort.js"></script>
    <script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxgrid.selection.js"></script>
    <script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxgrid.export.js"></script>
    <script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxgrid.aggregates.js"></script>
    <script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxgrid.columnsreorder.js"></script>
    <script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxgrid.columnsresize.js"></script>
    <script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxgrid.grouping.js"></script>
    <script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxgrid.pager.js"></script> 
    <script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxgrid.edit2.full.js"></script>
    <script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxmenu.js"></script>
    <script type="text/javascript" src="/aceadmin/assets/js/bootbox.min.js"></script>
</#macro>
<#macro useLocalizationNumberFunction>
<script type="text/javascript">
    var convertLocalNumber = function(num){
        if(num == null){
            return "";
        }
        decimalseparator = ".";
        thousandsseparator = ",";
        if("${locale}" == "vi"){
            decimalseparator = ",";
            thousandsseparator = ".";
        }
        var str = num.toString(), parts = false, output = [], i = 1, formatted = null;
        if(str.indexOf(".") > 0) {
            parts = str.split(".");
            str = parts[0];
        }
        str = str.split("").reverse();
        for(var j = 0, len = str.length; j < len; j++) {
            if(str[j] != ",") {
                output.push(str[j]);
                if(i%3 == 0 && j < (len - 1)) {
                    output.push(thousandsseparator);
                }
                i++;
            }
        }
        formatted = output.reverse().join("");
        return(formatted + ((parts) ? decimalseparator + parts[1].substr(0, 6) : ""));
    }
</script>
</#macro>

<#macro jqGrid url columnlist dataField initrowdetailsDetail="" isShowTitleProperty="true" customTitleProperties="" columngrouplist="" updaterow="" addColumns="" deleterow="" addrow="" updateUrl="" createUrl="" 
               removeUrl="" showtoolbar="true" addmultiplerows="false" updateMulUrl="" updatemultiplerows="" updateoffline="false" offlinerefreshbutton="true" deleteConditionFunction=""	
               excelExport="false" toPrint="false" filterbutton="" clearfilteringbutton="false" noConditionFind="N" otherCondition="" conditionsFind="N" doubleClick="false" dictionaryColumns="" groupable="false"
               primaryColumn="ID" editable="false" editColumns="" id="jqxgrid" dataType="json" filterable="true" filtersimplemode="true" viewSize="15" viewIndex="0" width="500" height="" autorowheight="false"
               pageable="true" columnsresize="true" columnsreorder="true" sortable="true" defaultSortColumn="" autoheight="true" currencySymbol="d" selectionmode="singlerow" addrefresh="false" 
               showstatusbar="false" editpopup="false" initrowdetails="false" keyvalue="" deleteColumn="" addinitvalue="" addType="direct" entityName="" groups="" alternativeAddPopup=""
               editmode="selectedrow" jqGridMinimumLibEnable="true" otherParams="" sortdirection="asc" altrows="false" sourceId="Id" rowselectfunction="" bindingcompletefunction="" rowunselectfunction="" idExisted="false"
               ulistname="" updatelist="false"  editrefresh="false" groupsexpanded="false" pagesizeoptions="['5', '10', '15', '20', '25', '30', '50', '100']" contextMenuId="contextMenuId" mouseRightMenu="false"
               usecurrencyfunction="true" deletesuccessfunction="" autoload="true" statusbarheight="50" dropdownlist="false" ddlSource="" displayMember="" valueMember="" sendEmail="false" changeState="false" 
               bindresize="true" customcontrol1="" customcontrol2="" customcontrol3=""  customtoolbaraction=""  showlist="true" extraUrl="" offmode="false" allGridMenu="false" rowsheight="30" functionAfterUpdate="" functionAfterAddRow="" 
               customLoadFunction="false" rowdetailsheight="200" readyFunction="" rowdetailstemplateAdvance="" deletelocal="false" enablemousewheel="true" customCss="" functionAfterRowComplete=""
               exceptFieldToCompare="" confirmEditFunction="" scrollmode="deferred" updateRowFunction="" afterinitfunction="" deleteConditionMessage="" autoshowloadelement="true" showdefaultloadelement="true"
               beforeprocessing="" customControlAdvance="" autoMeasureHeight='false'>
   <#if jqGridMinimumLibEnable=="true">
    <@jqGridMinimumLib/>
   </#if>
   <#include "jqwLocalization.ftl"/>
    <script type="text/javascript">
        var wgdeletesuccess = "${StringUtil.wrapString(uiLabelMap.wgdeletesuccess)}";
        var wgupdatesuccess = "${StringUtil.wrapString(uiLabelMap.wgupdatesuccess)}";
        var wgaddsuccess = "${StringUtil.wrapString(uiLabelMap.wgaddsuccess)}";
        function updateGridMessage(id, template, message){
            $('#container' + id).empty();
            $('#jqxNotification' + id).jqxNotification({ template: template});
            $("#notificationContent" + id).text(message);
            $("#jqxNotification" + id).jqxNotification("open");
        }
        function displayEditSuccessMessage(id){
            $('#container' + id).empty();
            $('#jqxNotification' + id).jqxNotification({ template: 'success'});
            $("#notificationContent" + id).text(wgupdatesuccess);
            $("#jqxNotification" + id).jqxNotification("open");
        }
        function displayDeleteSuccessMessage(id){
            $('#container' + id).empty();
            $('#jqxNotification' + id).jqxNotification({ template: 'success'});
            $("#notificationContent" + id).text(wgdeletesuccess);
            $("#jqxNotification" + id).jqxNotification("open");
        }
        function CallbackFocusFilter(){
        	var previousId = localStorage.getItem('previousInputFilter');
        	$("#" + previousId).find('input').focus();
        }
        function AutoMeasureGridHeight(grid){
        	var x = Math.abs($('#page-content').innerHeight() - $('#page-content').height());
    		var tmpheight = $(window).height() - $('#nav').height() - $('.breadcrumb-inner').height() - x - 10;
    		grid.jqxGrid({ height: tmpheight });
        }
    </script>
    <script type="text/javascript">
        var tmpEditable = false;
        var editPending = false;
        $.jqx.theme = 'olbius';  
        theme = $.jqx.theme;     
        <#if usecurrencyfunction=="true">
            var formatcurrency = function(num, uom){
                if(num == null){
                    return "";
                }
                decimalseparator = ",";
                thousandsseparator = ".";
                currencysymbol = "d";
                if(typeof(uom) == "undefined" || uom == null){
                    uom = "${defaultOrganizationPartyCurrencyUomId?if_exists}";
                }
                if(uom == "USD"){
                    currencysymbol = "$";
                    decimalseparator = ".";
                    thousandsseparator = ",";
                }else if(uom == "EUR"){
                    currencysymbol = "€";
                    decimalseparator = ".";
                    thousandsseparator = ",";
                }
                var str = num.toString().replace(currencysymbol, ""), parts = false, output = [], i = 1, formatted = null;
                if(str.indexOf(".") > 0) {
                    parts = str.split(".");
                    str = parts[0];
                }
                str = str.split("").reverse();
                for(var j = 0, len = str.length; j < len; j++) {
                    if(str[j] != ",") {
                        output.push(str[j]);
                        if(i%3 == 0 && j < (len - 1)) {
                            output.push(thousandsseparator);
                        }
                        i++;
                    }
                }
                formatted = output.reverse().join("");
                return(formatted + ((parts) ? decimalseparator + parts[1].substr(0, 2) : "") + "&nbsp;" + currencysymbol);
            };
        </#if>
        var deleteRow${id} = function(url, data, commit){
            $('#${id}').jqxGrid('showloadelement');
            $.ajax({
                type: "POST",
                url: url,
                data:  data,
                <#if updateoffline == "true">
                async: false,
                </#if>
                success: function (data, status, xhr) {
                    if(data.responseMessage == "error"){
                        commit(false);
                        $('#container${id}').empty();
                        $('#jqxNotification${id}').jqxNotification({ template: 'error'});
                        $("#notificationContent${id}").text(data.errorMessage);
                        $("#jqxNotification${id}").jqxNotification("open");
                    }else{
                        if(commit){
                            commit(true);   
                        }
                        $('#container${id}').empty();
                        $('#${id}').jqxGrid('updatebounddata');
                        $('#jqxNotification${id}').jqxNotification({ template: 'success'});
                        $("#notificationContent${id}").text(wgdeletesuccess);
                        $("#jqxNotification${id}").jqxNotification({icon: { width: 25, height: 25, url: '/aceadmin/assets/images/info.jpg'}});
                        $("#jqxNotification${id}").jqxNotification("open");
                        <#if deletesuccessfunction?has_content>
                            ${deletesuccessfunction}
                        </#if>
                    }
                }, error: function () {
                    if(commit){
                        commit(false);  
                    }
                },complete: function(){
                    $('#${id}').jqxGrid('hideloadelement');
                }
            });   
        };
        var updateRow${id} = function(url, data, commit){
            $('#${id}').jqxGrid('showloadelement');
            $.ajax({
                type: "POST",                        
                url: url,
                data: data,
                <#if updateoffline == "true">
                async: false,
                </#if>
                success: function (data, status, xhr) {
                    // update command is executed.
                    if(data.responseMessage == "error"){
                        if(commit){commit(false)}
                        <#if updateoffline != "true">
                            $('#${id}').jqxGrid('updatebounddata');
                        </#if>
                        $('#container${id}').empty();
                        $('#jqxNotification${id}').jqxNotification({ template: 'error'});
                        $("#notificationContent${id}").text(data.errorMessage);
                        $("#jqxNotification${id}").jqxNotification("open");
                    }else{
                        if(commit){commit(true)}
                        localStorage.removeItem("localGridUpdate${id}");
                        $('#container${id}').empty();
                        $('#jqxNotification${id}').jqxNotification({ template: 'success'});
                        $("#notificationContent${id}").text(wgupdatesuccess);
                        $("#jqxNotification${id}").jqxNotification("open");
                        <#if editrefresh=="true" && updateoffline != "true">
                            $('#${id}').jqxGrid('updatebounddata');
                        </#if>
                    }
                    <#if functionAfterUpdate != "">
                        ${functionAfterUpdate}
                    </#if>
                },
                error: function () {
                    if(commit){commit(false)}
                },complete: function(){
                    $('#${id}').jqxGrid('hideloadelement');
                }
            });
        };
        var checkDataUpdate = function(data1, data2){
            var arr = JSON.parse(JSON.stringify(data1));
            for(var x in data1){
                var obj = data1[x];
                for(var y in data2){
                    var obj2 = data2[y];
                    var i = 0;
                    var j = 0;
                    for(var z in obj2){
                        if(obj[z].value && obj[z].value == obj2[z].value){
                            i++;
                        }
                        j++;
                    }
                    if(i == j){
                        arr.splice(x, 1);
                    }
                }
            }
            return arr;
        };
        var clearData = function(){
        	localStorage.removeItem('previousInputFilter');
            localStorage.removeItem("localGridUpdate${id}");
            localStorage.removeItem("localGridDelete${id}");
            localStorage.removeItem("localGridCreate${id}");
        };
        
        function getFieldType(fName){
            for (i=0;i < ${dataField}.length;i++) {
               if(${dataField}[i]['name'] == fName){
                    if(!(typeof ${dataField}[i]['other'] === 'undefined' || ${dataField}[i]['other'] =="")){
                        return  ${dataField}[i]['other'];
                    }else{
                        return  ${dataField}[i]['type'];
                    }
                    
               }
            }
        }
        var combobox = [];
        var dropdownGrid = [];
        <#if customLoadFunction == "true">
            function initGrid${id}(){
                if($("#${id}").is('*[class^="jqx"]')){
                    return;
                }
        <#else>
            $(document).ready(function () {
        </#if>
            var culture="en";
            var oldEditingValue;
            var source${id} =
            {
                dataType: '${dataType}',
                dataFields: 
                    ${dataField}
                ,
                id: '${sourceId}',
                type: 'POST',
                cache: false,
                data: {
                    noConditionFind: '${noConditionFind}',
                    conditionsFind: '${conditionsFind}',
                    dictionaryColumns: '${dictionaryColumns}',
                    otherCondition: '${otherCondition}'
                },
                contentType: 'application/x-www-form-urlencoded',
                url: '${url}'<#if extraUrl != ""> + ${extraUrl}</#if>,
                beforeprocessing: function (data) {
                    source${id}.totalrecords = data.TotalRows;
                    <#if beforeprocessing == "">
                    if(parseInt(data.TotalRows) <= 0){
                    	$("#${id}").jqxGrid({
                    		height: "",
                    		autoheight: "true"
                    	});
                    }
                    <#else>
                    	var x = ${beforeprocessing};
                    	if(typeof(x) == 'function'){
                    		x();
                    	}
                    </#if>
                },
                <#if filterable=="true">
                filter: function () {
                    // update the grid and send a request to the server.
                    $("#${id}").jqxGrid('updatebounddata');
                },
                </#if>
                <#if sortable=="true">
                sort: function () {
                    // update the grid and send a request to the server.
                    $("#${id}").jqxGrid('updatebounddata');
                },
                sortcolumn: '${defaultSortColumn}',
                sortdirection: '${sortdirection}',
                </#if>
                <#if pageable=="true">
                pagenum: ${viewIndex},
                pagesize: ${viewSize},
                entityName: '${entityName}',
                pager: function (pagenum, pagesize, oldpagenum) {
                    // callback called when a page or page size is changed.
                },
                </#if>
                                                    
                <#if (editable=="true" || updaterow=="true" || doubleClick=="true" || updatemultiplerows=="true" || editColumns!="") && (updateRowFunction == "") >
                updaterow: function (rowid, rowdata, commit) {
                    $("#${id}").jqxGrid({ disabled: true});
                    beginEdit = true;
                    for(var n in rowdata){
                        var tmpExisted = $('#${id}').jqxGrid('getcolumnindex', n);
                        if(tmpExisted != -1){
                        	var column = $('#${id}').jqxGrid('getcolumnproperty', n, 'columntype');
    	                    if(column == 'combobox' && combobox.length){
    	                    	rowdata[n] = JSON.stringify(combobox);
    	                    	break;
    	                    }
    	                    var infoCl;
    	                    if(localStorage.getItem('infoColumnDetail')){
    	                    	infoCl = $.parseJSON(localStorage.getItem('infoColumnDetail'));
    	                    }
    	                    if(infoCl){
    	                    	if(column == 'custom' && dropdownGrid && infoCl.gridname == '${id?if_exists}' && infoCl.field == 'paymentId' && infoCl.columntype == 'custom' && infoCl.type == 'dropdownGrid'){
    	                    		rowdata[n] = dropdownGrid.paymentId ? dropdownGrid.paymentId : null;
    	                    		break;
    	                    	}
    	                    }
                        }
                    }
                    if(tmpEditable){
                        commit(false);
                        $("#${id}").jqxGrid({ disabled: false});
                        return;
                    }
                    var tmpOlFlag = true;
                    var strExceptFieldToCompare = '';
                    <#if exceptFieldToCompare != "">
                        strExceptFieldToCompare = ";${exceptFieldToCompare};";
                    </#if>
                    if(oldEditingValue != undefined && oldEditingValue != null){
                    	for (var key in rowdata) {
                    	    if(strExceptFieldToCompare != '' && strExceptFieldToCompare.indexOf(';' + key + ';') > -1){
                    	        continue;
                    	    }
                            if((rowdata[key] == undefined && oldEditingValue[key] != undefined) || (rowdata[key] != undefined && oldEditingValue[key] == undefined)){
                                tmpOlFlag = false;
                                break;
                            }
                            if(rowdata[key] != oldEditingValue[key]){
                                tmpOlFlag = false;
                                break;
                            }
                        }	
                    }else{
                    	tmpOlFlag = false;
                    }
                    if(tmpOlFlag){
                        commit(false);
                        $("#${id}").jqxGrid({ disabled: false});
                        return false;
                    }
                    <#if confirmEditFunction != "">
                        if(!editPending){
                            ${confirmEditFunction}
                            commit(false);
                            $("#${id}").jqxGrid({ disabled: false});
                            editPending = true;
                            return false;
                        }
                        editPending = false;
                    </#if>
                    // Split data to submit to server
                    data = "";
                    urlStatus = "";
                    var keysData = Object.keys(rowdata).toString();
                    data = "rl=1&";
                    <#if updatelist!="false">
                        data = "rl=" + rowid.length + "&"; // record length
                        data += "updatelist=true&";
                        <#if ulistname!="">
                            data += "ulistname=${ulistname}&";
                        </#if>
                    </#if>
                    <#if updateoffline=="true">
                        var local = {};
                    </#if>
                    if (keysData.indexOf("oldValue") < 0)
                    {
                        var columnList = '${editColumns}';
                        
                        var arrKeysData = "";
                        if(typeof(columnList) == 'function'){
                        	var tmp = columnList();
                        	arrKeysData = tmp.split(";")
                        }else{
                        	arrKeysData = columnList.split(";")
                        }
                        var rowidlength = 1;
                        if(!(rowid instanceof Array)){
                            rowid = [rowid];
                        }
                        if(!(rowdata instanceof Array)){
                            rowdata = [rowdata]; 
                        }
                        <#if updateoffline!="true">
                        for(j=0; j < rowid.length;j++){
                            var columnValues ="";
                            for(i = 0; i < arrKeysData.length;i++){
                                if(arrKeysData[i].indexOf("(") > -1){
                                    var tmpStr = arrKeysData[i].substring(0,arrKeysData[i].indexOf("("));
                                    if(rowdata[j][tmpStr]==null){                                   
                                        if(arrKeysData[i].indexOf("[") > -1){
                                            if(arrKeysData[i].indexOf(".Timestamp)") > -1){                                             
                                                var tmstr = arrKeysData[i].substring(arrKeysData[i].indexOf('[') + 1,arrKeysData[i].length - 1);
                                                if(tmstr){
                                                    var tmpdate = new Date(tmstr);
                                                    var tmps = isNaN(tmpdate.getTime()) ? "" : tmpdate.getTime() ;
                                                    columnValues += tmps + "#;";
                                                }else{
                                                    columnValues += "#;";
                                                }

                                            }else{
                                                columnValues += arrKeysData[i].substring(arrKeysData[i].indexOf('[') + 1,arrKeysData[i].length - 1) + "#;"; 
                                            }
                                        }else{
                                            columnValues += "undefined#;";
                                        }
                                    }else{
                                        if(arrKeysData[i].indexOf(".Timestamp)") > -1){
                                            var tmstr = rowdata[j][tmpStr];
                                            if(tmstr){
                                                var tmpdate = new Date(tmstr);
                                                var tmps = isNaN(tmpdate.getTime()) ? "" : tmpdate.getTime() ;
                                                columnValues += tmps + "#;";
                                            }else{
                                                columnValues += "#;";
                                            }                                           
                                        }else if(arrKeysData[i].indexOf(".Date)") > -1){
                                        	var tmstr = rowdata[j][tmpStr];
                                            if(tmstr){
                                                var tmpdate = new Date(tmstr);
                                                var tmps = isNaN(tmpdate.getTime()) ? "" : tmpdate.getTime() ;
                                                columnValues += tmps + "#;";
                                            }else{
                                                columnValues += "#;";
                                            } 
                                            // columnValues += rowdata[j][tmpStr] + "#;";
                                        }else if(arrKeysData[i].indexOf(".Time)") > -1){
                                        	var tmstr = rowdata[j][tmpStr];
                                            if(tmstr){
                                                var tmpdate = new Date(tmstr);
                                                var lo = !isNaN(tmpdate.getTime()) ? tmpdate.getTime() : "";
                                                columnValues += lo + "#;";
                                            }else{
                                                columnValues += "#;";
                                            } 
                                            // columnValues += rowdata[j][tmpStr] + "#;";
                                        }else{
                                            columnValues += rowdata[j][tmpStr] + "#;";
                                        }
                                    }
                                }else{
                                    var tmpStr = arrKeysData[i];
                                    if(tmpStr.indexOf("[") > -1){
                                        var tmpStr = tmpStr.substring(0,tmpStr.indexOf("["));
                                    }     
                                    if(rowdata[j][tmpStr]==null){
                                        if(arrKeysData[i].indexOf("[") > -1){
                                            columnValues += arrKeysData[i].substring(arrKeysData[i].indexOf('[') + 1,arrKeysData[i].length - 1) + "#;"; 
                                        }else{
                                            columnValues += "undefined#;";
                                        }
                                    }else{
                                        columnValues += rowdata[j][tmpStr] + "#;";
                                    }
                                }
                            };     
                            if(j==0){
                                data += "columnList" + j + "=";
                            }else{
                                data += "&columnList" + j + "=";
                            } 
                            var arrColumnData = columnList.split(";");
                            var ilength = arrColumnData.length;
                            
                            for(i = 0; i < ilength;i++){
                                var strTmp;
                                if(arrColumnData[i].indexOf("[") > -1){
                                    strTmp = arrColumnData[i].substring(0,arrColumnData[i].indexOf("["));
                                }else{
                                    strTmp = arrColumnData[i];
                                }
                                data += strTmp + ";";
                            }
                            data += "&" + "columnValues" + j + "=" +  columnValues;
                        }
                        data +=  "&" + "primaryColumn" + "=" + '${primaryColumn}';
                        data +=  "&" +  "entityName" + "=" + '${entityName}'; 
                        urlStatus = '${updateUrl}';
                        <#else>
                        for(j=0; j < rowid.length;j++){
                            for(i = 0; i < arrKeysData.length;i++){
                                if(arrKeysData[i].indexOf("(") > -1){
                                    var tmpStr = arrKeysData[i].substring(0,arrKeysData[i].indexOf("("));
                                    if(arrKeysData[i].indexOf(".Timestamp)") > -1){
                                        var tmstr = rowdata[j][tmpStr];
                                        if(tmstr){
                                            var tmpdate = new Date(tmstr);
                                            var tmps = isNaN(tmpdate.getTime()) ? "" : tmpdate.getTime() ;
                                            local[arrKeysData[i]] = {
                                                    value : tmps,
                                                    type: arrKeysData[i]
                                                };                                          
                                        }else{
                                            local[arrKeysData[i]] = {
                                                    value : "",
                                                    type: arrKeysData[i]
                                                };
                                        }                                           
                                    }else if(arrKeysData[i].indexOf(".Date)") > -1){
                                    	var tmstr = rowdata[j][tmpStr];
                                        if(tmstr){
                                            var tmpdate = new Date(tmstr);
                                            var tmps = isNaN(tmpdate.getTime()) ? "" : tmpdate.getTime() ;
                                            local[arrKeysData[i]] = {
                                                    value : tmps,
                                                    type: arrKeysData[i]
                                                };                                          
                                        }else{
                                            local[arrKeysData[i]] = {
                                                    value : "",
                                                    type: arrKeysData[i]
                                                };
                                        } 
                                    }else{
                                        local[tmpStr] = {
                                            value : rowdata[j][tmpStr],
                                            type: arrKeysData[i]
                                        };
                                    }
                                }else{
                                    var tmpStr = arrKeysData[i];
                                    if(rowdata[j][tmpStr]==null){
                                        if(arrKeysData[i].indexOf("[") > -1){
                                            local[tmpStr] = {
                                                value : arrKeysData[i].substring(arrKeysData[i].indexOf('[') + 1,arrKeysData[i].length - 1)
                                            };
                                        }
                                    }else{
                                        local[tmpStr] = {
                                            value : rowdata[j][tmpStr]
                                        };
                                    }
                                }
                            };     
                        }
                        </#if>
                    }
                    else
                    {   
                        var arrKeysData = keysData.split(",");                  
                        if(arrKeysData.length > 0)
                        {
                            data = arrKeysData[0] + "=" + rowdata[arrKeysData[0]];
                        }
                        for(i = 1; i < arrKeysData.length ;i++){
                            if(rowdata[arrKeysData[i]]){
                                data  = data + "&" + arrKeysData[i] + "=" + rowdata[arrKeysData[i]] ;
                            }
                        }; 
                        data = data +  "&" + "primaryColumn" + "=" + '${primaryColumn}';
                        data = data +  "&" +  "entityName" + "=" + '${entityName}'; 
                        urlStatus = '${updateMulUrl}';  
                    }
                    // End of spliting data
                    <#if updateoffline != "true">
                    if (urlStatus && !(/^\s*$/.test(urlStatus))) {
                        updateRow${id}(urlStatus, data, commit);
                    }
                    <#else>
                        var localData = $.parseJSON(localStorage.getItem('localGridUpdate${id}'));
                        if(localData){
                            localData.push(local);
                            localStorage.setItem("localGridUpdate${id}", JSON.stringify(localData));
                        }else{
                            localData = new Array();
                            localData.push(local);
                            localStorage.setItem("localGridUpdate${id}", JSON.stringify(localData));
                        }
                        commit(true);
                    </#if>
                    $("#${id}").jqxGrid({ disabled: false});
                },
                <#elseif updateRowFunction != "" && editable=="true">
                	updateRow: ${updateRowFunction},
                </#if>
                <#if addrow=="true">
                addrow: function (rowid, rowdata, position, commit) {   
                    $("#${id}").jqxGrid({ disabled: true});
                    data = "";
                    var keysData = Object.keys(rowdata).toString();
                    var arrKeysData = keysData.split(",");
                    var addColumns = "${addColumns}".split(";");
                    var tmpAddclm = "";
                    var spl = "";
                    var splCol = "";
                    data = "columnValues=";
                    for(i=0;i<addColumns.length;i++){
                    	if(i != 0){
                    		spl = "#;";
                    		splCol = ";";
                    	}
                        tmpKey = addColumns[i];
                        if(addColumns[i].indexOf('(') > -1){
                            tmpKey = addColumns[i].substring(0,addColumns[i].indexOf('('));
                        }else if(addColumns[i].indexOf('[') > -1){
                            tmpKey = addColumns[i].substring(0,addColumns[i].indexOf('['));
                        }
                        if(addColumns[i].indexOf('[') > -1){
                            if(addColumns[i].indexOf(".Timestamp)") > -1 || addColumns[i].indexOf(".Date)") > -1){
                                var tmstr = addColumns[i].substring(addColumns[i].indexOf('[') + 1,addColumns[i].length - 1);
                                if(tmstr){
                                    var tmpdate = new Date(tmstr);
                                    var tmps = isNaN(tmpdate.getTime()) ? "" : tmpdate.getTime() ;
                                    data += spl + tmps;
                                }else{
                                    data += spl; 
                                }
                            }else{
                                data += spl + addColumns[i].substring(addColumns[i].indexOf('[') + 1,addColumns[i].length - 1);
                            }
                        }else{
                            if(addColumns[i].indexOf(".Timestamp)") > -1 || addColumns[i].indexOf(".Date)") > -1){
                                var tmstr = rowdata[tmpKey];
                                if(tmstr){
                                    var tmpdate = new Date(tmstr);
                                    var tmps = isNaN(tmpdate.getTime()) ? "" :  tmpdate.getTime();
                                    data += spl + tmps;
                                }else {
                                    data += spl; 
                                }
                            }else{
                                data += spl + rowdata[tmpKey];
                            }
                        }
                        if(addColumns[i].indexOf('[') > -1){
                            tmpAddclm += splCol + addColumns[i].substring(0,addColumns[i].indexOf('['));
                        }else{
                            tmpAddclm += splCol + addColumns[i];
                        }
                    } 
                    data += "&" +  "columnList" + "=" + tmpAddclm; 
                    // End of spliting data
                    $('#${id}').jqxGrid('showloadelement');
                    $.ajax({
                        type: "POST",
                        url: '${createUrl}',
                        data: data,
                        success: function (data, status, xhr) {
                            if(data.responseMessage == 'error'){
                                commit(false);
                                $('#container${id}').empty();
                                $('#jqxNotification${id}').jqxNotification({ template: 'error'});
                                $("#notificationContent${id}").text(data.errorMessage);
                                $("#jqxNotification${id}").jqxNotification("open");
                            }else{
                            $('#${id}').trigger('myEvent',JSON.stringify(data));
                                // update command is executed.
                                var keysDataTmp = Object.keys(data.results);
                                for(i=0; i < keysDataTmp.length;i++){
                                    rowdata[keysDataTmp[i]] = data.results[keysDataTmp[i]];
                                }
                                commit(true);
                                $('#container${id}').empty();
                                $('#jqxNotification${id}').jqxNotification({ template: 'success'});
                                $("#notificationContent${id}").text(wgaddsuccess);
                                $("#jqxNotification${id}").jqxNotification("open");
                                <#if addrefresh=="true">
                                    $('#${id}').jqxGrid('updatebounddata');
                                </#if>
                                <#if functionAfterAddRow != "">
                                    ${functionAfterAddRow}
                                </#if>
                            }
                        },
                        error: function () {
                            commit(false);
                        },
                        complete: function(){
                            $('#${id}').jqxGrid('hideloadelement');
                        	<#if functionAfterRowComplete != "">
                        		${functionAfterRowComplete}
                        	</#if>
                        }
                    });         
                    $("#${id}").jqxGrid({ disabled: false});
                },
                </#if>
                <#if deleterow=="true">              
                deleterow: function (rowid, commit) {
                    $("#${id}").jqxGrid({ disabled: false});
                    var dataRecord = $('#${id}').jqxGrid('getrowdatabyid', rowid);
                    var dcl = "${deleteColumn}".split(";");
                    <#if deletelocal!="true">
                    var data = "";
                    var tmpValue = "&columnValues=";
                    if(dcl[0].indexOf('[') > -1){
                        tmpStr = dcl[0].substring(0, dcl[0].indexOf('['));
                        tmpValue += dcl[0].substring(dcl[0].indexOf('[') + 1,dcl[0].length - 1);
                        data = "columnList" + "=" + tmpStr;
                    }else{
                        tmpStr = dcl[0];
                        if(dcl[0].indexOf("(") > -1){
                            tmpStr = dcl[0].substring(0, dcl[0].indexOf('('));
                            var tmpD = new Date(dataRecord[tmpStr]);
                            tmpStr = tmpD.getTime() + "";
                            tmpValue += tmpStr.substring(0, tmpStr.length - 3);
                            data = "columnList" + "=" + dcl[0];
                        }else{
                            tmpValue += dataRecord[tmpStr];
                            data = "columnList" + "=" + dcl[0];
                        }
                    }
                    for(i=1; i < dcl.length;i++){
                        if(dcl[i].indexOf('[') > -1){
                            tmpStr = dcl[i].substring(0, dcl[i].indexOf('['));
                            data += ";" + tmpStr;
                            if(tmpStr.indexOf("(") > -1){
                                tmpStr = dcl[i].substring(0, dcl[i].indexOf('('));
                            }
                            tmpValue += "#;" + dcl[i].substring(dcl[i].indexOf('[') + 1,dcl[i].length - 1);
                        }else{
                            tmpStr = dcl[i];
                            if(dcl[i].indexOf("(") > -1){
                                tmpStr = dcl[i].substring(0, dcl[i].indexOf('('));
                                data += ";" + dcl[i];
                                var tmpD = new Date(dataRecord[tmpStr]);
                                tmpStr = tmpD.getTime() + "";
                                tmpValue += "#;" + tmpStr;
                            }else{
                                data += ";" + dcl[i];
                                tmpValue += "#;" + dataRecord[tmpStr];
                            }
                        }
                    }    
                    data += tmpValue;
                    deleteRow${id}("${removeUrl}", data, commit);
                    <#else>
                        var local = {};
                        for(var i = 0; i < dcl.length;i++){
                            if(dcl[i].indexOf('[') > -1){
                                tmpStr = dcl[i].substring(0, dcl[i].indexOf('['));
                                if(tmpStr.indexOf("(") > -1){
                                    tmpStr = dcl[i].substring(0, dcl[i].indexOf('('));
                                }
                                local[tmpStr] = {
                                    value : dcl[i].substring(dcl[i].indexOf('[') + 1,dcl[i].length - 1),
                                    type: dcl[i]
                                };
                            }else{
                                tmpStr = dcl[i];
                                if(dcl[i].indexOf(".Timestamp") > -1){
                                    tmpStr = dcl[i].substring(0, dcl[i].indexOf('('));
                                    var tmpD = new Date(dataRecord[tmpStr]);
                                    local[tmpStr] = {
                                        value : tmpD.getTime(),
                                        type: dcl[i]
                                    };                              
                                }else{
                                    local[tmpStr] = {
                                        value : dataRecord[tmpStr]
                                    };
                                }
                            }
                        }    
                        var localData = $.parseJSON(localStorage.getItem("localGridDelete${id}"));
                        if(localData){
                            localData.push(local);
                            localStorage.setItem("localGridDelete${id}", JSON.stringify(localData));
                        }else{
                            localData = new Array();
                            localData.push(local);
                            localStorage.setItem("localGridDelete${id}", JSON.stringify(localData));
                        }
                        commit(true);
                    </#if>
                    $("#${id}").jqxGrid({ disabled: false});
                }, 
                </#if>                            
                root: 'results'
            };           
            <#--
            <#if (editable=="true" && editpopup=="true") || doubleClick=="true" || updaterow=="true">
            
            <#list "${editColumns}"?split(";") as eColumn>
                    $("#${eColumn}").jqxInput({ theme: theme });
                    $("#${eColumn}").width(250);
                    $("#${eColumn}").height(23);
            </#list>           
                
            </#if> -->
            
             <#if updatemultiplerows=="true">
                $("#newValueUpdate").jqxInput({ theme: theme });
                $("#newValueUpdate").width(250);
                $("#newValueUpdate").height(23);
            </#if>
             var dataadapter${id} = new $.jqx.dataAdapter(source${id}, {
                formatData: function (data) {
                    if((typeof outFilterCondition === 'undefined' || outFilterCondition =="") && (typeof alterData === 'undefined' || alterData =="" || $.isEmptyObject(alterData)))
                    {
                        <#if filterable=="true">
                            if (data.filterscount) {
                                var filterListFields = "";
                                var tmpFieldName = "";
                                for (var i = 0; i < data.filterscount; i++) {
                                    var filterValue = data["filtervalue" + i];
                                    if(!filterValue){continue;}
                                    var filterCondition = data["filtercondition" + i];
                                    var filterDataField = data["filterdatafield" + i];
                                    var filterOperator = data["filteroperator" + i];
                                    if(getFieldType(filterDataField)=='number'){
                                        filterListFields += "|OLBIUS|" + filterDataField + "(BigDecimal)";
                                    }else if(getFieldType(filterDataField)=='date'){
                                        filterListFields += "|OLBIUS|" + filterDataField + "(Date)";
                                    }else if(getFieldType(filterDataField)=='Timestamp'){
                                        filterListFields += "|OLBIUS|" + filterDataField + "(Timestamp)[dd/MM/yyyy hh:mm:ss aa]";
                                    }
                                    else{
                                        filterListFields += "|OLBIUS|" + filterDataField;
                                    }
                                    if(getFieldType(filterDataField)=='Timestamp'){
                                        if(tmpFieldName != filterDataField){
                                            filterListFields += "|SUIBLO|" + filterValue + " 00:00:00 am";
                                        }else{
                                            filterListFields += "|SUIBLO|" + filterValue + " 11:59:59 pm";
                                        }
                                    }else{
                                        filterListFields += "|SUIBLO|" + filterValue;
                                    }
                                    filterListFields += "|SUIBLO|" + filterCondition;
                                    filterListFields += "|SUIBLO|" + filterOperator;
                                    tmpFieldName = filterDataField;
                                }
                                data.filterListFields = filterListFields;
                            }
                        </#if>
                    }else if(!(typeof alterData === 'undefined' || alterData =="")){
                        var tmppn = data.pagenum;
                        data = alterData;
                        data.pagenum = tmppn;
                    }else{
                        data.filterListFields = outFilterCondition;
                        outFilterCondition = "";
                    }
                    <#if otherParams!="">
                        data.otherParams = "${otherParams}";
                    <#else>
                        data.otherParams = null;
                    </#if>
                    data.$skip = data.pagenum * data.pagesize;
                    data.$top = data.pagesize;
                    data.$inlinecount = "allpages";
                    return data;
                },
                loadError: function (xhr, status, error) {
                    // FIXME Consider to remove this action
                    // alert(error);
                    console.log(error);
                },
                downloadComplete: function (data, status, xhr) {
                    /*if (!source${id}.totalRecords) {
                        source${id}.totalRecords = parseInt(data["odata.count"]);
                    };   */   
                }, 
                beforeLoadComplete: function (records) {
                    for (var i = 0; i < records.length; i++) {
                        if(typeof(records[i])=="object"){
                            for(var key in records[i]) {
                                var value = records[i][key];
                                if(value != null && typeof(value) == "object" && typeof(value) != null){
                                    //var date = new Date(records[i][key]["time"]);
                                    //records[i][key] = date;
                                }
                                
                            }
                        }
                    }
                }
            });
            //var editrow = -1;
            clearData();
            /*init jqxgrid */
            $("#${id}").jqxGrid(
            {
                source: dataadapter${id},
                columnsheight: 30,
                filterable: ${filterable},
                autoshowfiltericon: true,
                showdefaultloadelement:${showdefaultloadelement},
                autoshowloadelement:${autoshowloadelement},
                pagesizeoptions: ${pagesizeoptions},
                autoheight:${autoheight},
                editable: ${editable},
                rowsheight: ${rowsheight},
                autorowheight: ${autorowheight},
                localization: getLocalization(),
                altrows: ${altrows},                             
                groupable: ${groupable},                 
                <#if groupable == "true" && groups != "">
                    groups:['${groups}'],
                </#if>
                <#if editable=="true">
                    editmode:'${editmode}',
                </#if>             
                selectionmode: '${selectionmode}',              
                ready: function(){   
                    <#if autoload=="false">
                        $("#${id}").jqxGrid("clear");
                    </#if>
                    <#if readyFunction!="">
                        ${readyFunction}
                    </#if>
                },
                width: '${width}',
                
                handlekeyboardnavigation: function (event) {
                    var key = event.charCode ? event.charCode : event.keyCode ? event.keyCode : 0;
                    // F2    
                    if ("${updaterow}"=="true")              
                    if (key == 113) {
                        $("#updaterowbutton").click();
                        return true;
                    };
                    // F8
                    if ("${deleterow}" == "true")
                    if (key == 119) {
                        $("#deleterowbutton").click();
                        return true;
                    };
                    // F5
                    if ("${filterbutton}" == "true")
                    if (key == 116) {
                        $("#filterbutton").click();
                        return true;
                    };
                    // Ctrl + F
                    if ("${clearfilteringbutton}" == "true")
                    if (key == 70 && event.ctrlKey) {
                        $('#clearfilteringbutton${id}').click();
                        return true;
                    }                                          

                    // Ctrl + I
                    if ("${addrow}" == "true")                 
                    if (key == 73 && event.ctrlKey) {
                        $('#addrowbutton${id}').click();
                        return true;
                    }
                    // F9
                    if ("${updatemultiplerows}" == "true")
                    if (key == 120) {
                        $('#updatemultiplerows').click();
                        return true;
                    }    
                   // F7
                   if ("${toPrint}"=="true")
                    if (key == 118) {
                        $('#print').click();
                        return true;
                    }                      
                   // Ctrl + E
                    if ("${excelExport}"=="true")
                    if (key == 69 && event.ctrlKey) {
                        $('#excelExport').click();
                        return true;
                    }                                                                        
                }, 

                <#if showtoolbar=="true">
                showtoolbar: true,
                rendertoolbar: function (toolbar) {
                    var me = this;
                    <#if titleProperty?has_content && isShowTitleProperty=="true" && customTitleProperties == "">
                        <@renderJqxTitle titlePropertyTmp=titleProperty id=id/>
                    <#elseif isShowTitleProperty=="true" && customTitleProperties != "">
                        <@renderJqxTitle titlePropertyTmp=customTitleProperties id=id/>
                    <#else>
                        var jqxheader = $("<div id='toolbarcontainer${id}' class='widget-header'><h4>" + "</h4><div id='toolbarButtonContainer${id}' class='pull-right'></div></div>");
                    </#if>
                    toolbar.append(jqxheader);
                    var container = $('#toolbarButtonContainer${id}');
                    var maincontainer = $("#toolbarcontainer${id}");
                    <#if showlist != "false">
                        (function(){
                            <#if showlist == "true">
                            	var columns = $("#${id}").jqxGrid('columns');
                            	if(typeof(columns) == 'undefined'){
                            		return;
                            	}
                                var allFields = columns.records;
                                var strSList = [];
                                for(i = 0; allFields != undefined && i < allFields.length;i++){
                                    strSList[i] = allFields[i].datafield;
                                }
                            <#else>
                                var strSList = "${StringUtil.wrapString(showlist)}".split(";");
                            </#if>
                            var strNList = [];
                            for(i=0; i < strSList.length;i++){
                                strNList[i] = $('<textarea />').html($('#${id}').jqxGrid('getcolumn', strSList[i]).text).text();
                            }
                            var listSource = [];
                            for(i=0; i < strSList.length;i++){
                                var tmpVL = {label: strNList[i], value: strSList[i], checked: true};                            
                                listSource[i] = tmpVL;
                            }
                            $("#showSL${id}").jqxDropDownList({ checkboxes: true, source: listSource,autoDropDownHeight : true ,displayMember: "label", valueMember: "value"});
                            $("#frozenSL${id}").jqxDropDownList({ checkboxes: true, source: listSource,autoDropDownHeight : true, displayMember: "label", valueMember: "value"});
                            $("#frozenSL${id}").jqxDropDownList('uncheckAll'); 
                            container.append('<button id="btngridsetting${id}" class="btn btn-mini btngridsetting" onclick="openJqxConfigWindow${id}();"><i class="fa-cogs"></i></button>');
                            // init window
                            $("#jqxconfig${id}").jqxWindow({
                                width: 400, height: 180, resizable: false, isModal: true, autoOpen: false, cancelButton: $("#configCancel${id}"), modalOpacity: 0.7, modalZIndex: 10000          
                            });
                            
                            $("#configSave${id}").click(function () {
                                var i = 0;
                                var dislayList = $("#showSL${id}").jqxDropDownList('getCheckedItems');
                                var frozenList = $("#frozenSL${id}").jqxDropDownList('getCheckedItems');
                                var dislayListValue = [];
                                var frozenListValue = [];
                                for(i = 0; i < dislayList.length; i++){
                                    dislayListValue[i] = dislayList[i].value;
                                }
                                for(i = 0; i < strSList.length; i++){
                                    if(dislayListValue.indexOf(strSList[i]) < 0){
                                        $("#${id}").jqxGrid('hidecolumn', strSList[i]);
                                    }else{
                                        if(!$('#${id}').jqxGrid('iscolumnvisible', strSList[i])){
                                            $("#${id}").jqxGrid('showcolumn', strSList[i]);
                                        }
                                    }
                                }
                                for(i = 0; i < frozenList.length; i++){
                                    frozenListValue[i] = frozenList[i].value;
                                }
                                for(i = 0; i < strSList.length; i++){
                                    if(frozenListValue.indexOf(strSList[i]) > -1){
                                        $("#${id}").jqxGrid('pincolumn', strSList[i]);
                                    }else{
                                        if($('#${id}').jqxGrid('iscolumnpinned', strSList[i])){
                                            $("#${id}").jqxGrid('unpincolumn', strSList[i]);
                                        }
                                    }
                                }
                                $("#jqxconfig${id}").jqxWindow('close');
                            });
                        })();
                    </#if>
                    <#if customControlAdvance != "">
                    	(function(){
                            container.append("<div style='float:right;margin-left:20px;margin-top: 4px; font-size: 14px; font-weight: normal;'>${customControlAdvance}</div>");
                        	$('#${id}').trigger('loadCustomControlAdvance');
                        })();
                    </#if>
                    <#if customcontrol1 != "">
                        (function(){
                            var tmpStr = "${StringUtil.wrapString(customcontrol1)}".split("@");
                            if(tmpStr.length == 4){
                                container.append('<div style="float:right;margin-left:20px;margin-top: 10px; font-size: 14px; font-weight: normal;"><a id="customcontrol1${id}" style="color:#438eb9" href="' + tmpStr[2] +'" onclick="' + tmpStr[3] + '"><i class="' + tmpStr[0] +'"></i>&nbsp;' + tmpStr[1] +'</a></div>');
                            }else{
                                container.append('<div style="float:right;margin-left:20px;margin-top: 10px; font-size: 14px; font-weight: normal;"><a id="customcontrol1${id}" style="color:#438eb9" href="' + tmpStr[2] +'"><i class="' + tmpStr[0] +'"></i>&nbsp;' + tmpStr[1] +'</a></div>');
                            }
                        })();
                    </#if>
                    <#if customcontrol2 != "">
                        (function(){
                            var tmpStr = "${StringUtil.wrapString(customcontrol2)}".split("@");
                            if(tmpStr.length == 4){
                                container.append('<div style="float:right;margin-left:20px;margin-top: 10px; font-size: 14px; font-weight: normal;"><a id="customcontrol2${id}" style="color:#438eb9;" href="' + tmpStr[2] +'" onclick="' + tmpStr[3] + '"><i class="' + tmpStr[0] +'"></i>&nbsp;' + tmpStr[1] +'</a></div>');
                            }else{
                                container.append('<div style="float:right;margin-left:20px;margin-top: 10px; font-size: 14px; font-weight: normal;"><a id="customcontrol2${id}" style="color:#438eb9;" href="' + tmpStr[2] +'"><i class="' + tmpStr[0] +'"></i>&nbsp;' + tmpStr[1] +'</a></div>');
                            }
                        })();
                    </#if>
                    <#if customcontrol3 != "">
                    (function(){
                        var tmpStr = "${StringUtil.wrapString(customcontrol3)}".split("@");
                        if(tmpStr.length == 4){
                            container.append('<div style="float:right;margin-left:20px;margin-top: 10px; font-size: 14px; font-weight: normal;"><a id="customcontrol3${id}" style="color:#438eb9;" href="' + tmpStr[2] +'" onclick="' + tmpStr[3] + '"><i class="' + tmpStr[0] +'"></i>&nbsp;' + tmpStr[1] +'</a></div>');
                        }else{
                            container.append('<div style="float:right;margin-left:20px;margin-top: 10px; font-size: 14px; font-weight: normal;"><a id="customcontrol3${id}" style="color:#438eb9;" href="' + tmpStr[2] +'"><i class="' + tmpStr[0] +'"></i>&nbsp;' + tmpStr[1] +'</a></div>');
                        }
                    })();
                    </#if>
                    <#if filterbutton=="true">
                        (function(){
                            container.append('<button id="filterbutton${id}" style="margin-left:20px;"><i class="icon-filter"></i>${uiLabelMap.accFilter}</button>');
                            var obj = $('#filterbutton${id}'); 
                            obj.jqxButton();
                            obj.click(function () {
                            var columname = $("#${id}").jqxGrid('getselectedcell').column;
                            var selectedrow = $("#${id}").jqxGrid('getselectedcell');                                           
                            var filtergroup = new $.jqx.filter();
                            var filter_or_operator = 1;
                            var filtervalue = $("#${id}").jqxGrid('getselectedcell').value;
                            var filtercondition = 'equal';
                            var filter1 = filtergroup.createfilter('stringfilter', filtervalue, filtercondition);                               
                            filtergroup.addfilter(filter_or_operator, filter1);
                            $("#${id}").jqxGrid('addfilter', columname, filtergroup);     
                                  $("#${id}").jqxGrid('applyfilters');
                            });
                        })();
                    </#if>
                    <#if clearfilteringbutton=="true">
                        (function(){
                            container.append('<button id="clearfilteringbutton${id}" style="margin-left:20px;" title="(Ctrl+F)"><span style="color:red;font-size:80%;left:5px;position:relative;">x</span><i class="fa-filter"></i></span> ${uiLabelMap.accRemoveFilter}</button>');
                            var obj = $('#clearfilteringbutton${id}');
                            obj.jqxButton();
                            obj.click(function () {
                                $("#${id}").jqxGrid('clearfilters'); 
                            }); 
                        })();
                    </#if>          
                    
                    <#if addrow=="true">
                        (function(){
                            container.append('<button id="addrowbutton${id}" style="margin-left:20px;" title="(Ctrl+I)"><i class="icon-plus-sign"></i>${uiLabelMap.accAddNewRow}</button>');
                            var obj = $("#addrowbutton${id}");
                            obj.jqxButton();
                            // create new row.
                            obj.on('click', function () { 
                                var selectedrowindex;
                                if($("#${id}").jqxGrid('getselectedrowindex') == null){
                                    selectedrowindex = 0;
                                }else{
                                    selectedrowindex = $("#${id}").jqxGrid('getselectedrowindex').rowindex;
                                }
                                var dataRecord = $("#${id}").jqxGrid('getrowdata', selectedrowindex);  
                                var row;    
                                if(dataRecord==null){
                                    row = { 
                                        <#if addinitvalue !="">
                                            ${primaryColumn}: '${addinitvalue}'
                                        </#if>                  
                                    };
                                }else{
                                    var primaryKey = dataRecord.${primaryColumn};                                               
                                    row = { 
                                        ${primaryColumn}: primaryKey                  
                                    };
                                }  
                                <#if addType=="popup">
                                    // edit the new row.
                                    var wtmp = window;
                                    <#if alternativeAddPopup=="">
                                        var tmpwidth = $('#popupWindow').jqxWindow('width');
                                        $("#popupWindow").jqxWindow({ position: { x: (wtmp.outerWidth - tmpwidth)/2, y: pageYOffset + 30 } });
                                        $("#popupWindow").jqxWindow('open');
                                        /*$('#popupWindow').on('close', function (event) { 
                                            $('#popupWindow').jqxValidator('hide');
                                        });*/ 
                                    <#else>
                                        var tmpwidth = $('#${alternativeAddPopup}').jqxWindow('width');
                                        $("#${alternativeAddPopup}").jqxWindow({ position: { x: (wtmp.outerWidth - tmpwidth)/2, y: pageYOffset + 30 } });
                                        $("#${alternativeAddPopup}").jqxWindow('open');
                                        /*$('#${alternativeAddPopup}').on('close', function (event) { 
                                            $('#${alternativeAddPopup}').jqxValidator('hide');
                                        });*/ 
                                    </#if>
                                <#else>
                                    $("#${id}").jqxGrid('addRow', null, row, "first");
                                    // select the first row and clear the selection.
                                    $("#${id}").jqxGrid('clearSelection');                        
                                    $("#${id}").jqxGrid('selectRow', 0);    
                                    //$("#jqxgrid").jqxGrid('beginRowEdit', 0);  
                                </#if>            
                            });
                        })();
                    </#if>
                    <#if addmultiplerows=="true">
                        (function(){
                            container.append('<input style="margin-left: 20px;" id="addmultiplerowsbutton${id}" type="button" value="Add Multiple New Rows" />');
                            var obj = $("#addmultiplerowsbutton${id}");
                            obj.jqxButton();
                            // create new rows.
                            obj.on('click', function () {
                                $("#${id}").jqxGrid('beginupdate');
                                for (var i = 0; i < 10; i++) {
                                    var datarow = generaterow();
                                    var commit = $("#${id}").jqxGrid('addrow', null, datarow);
                                }
                                $("#${id}").jqxGrid('endupdate');
                            });
                        })();
                    </#if>
                    <#if deleterow=="true">
                        (function(){
                            container.append('<button style="margin-left: 20px;" id="deleterowbutton${id}"><i class="icon-trash"></i>${uiLabelMap.accDeleteSelectedRow}</button>');
                            var obj = $("#deleterowbutton${id}");
                            obj.jqxButton();
                             // delete row.
                            obj.on('click', function () {   
                            	<#if deleteConditionFunction != "">
                            	if(typeof(${deleteConditionFunction}) == "function"){
                            		var dcf = ${deleteConditionFunction};
                            		var res = dcf();
                            		if(typeof(res) == "boolean" && !res){
                            			var message = "${deleteConditionMessage}";
                            			if(!message){
                            				message = "${StringUtil.wrapString(uiLabelMap.CannotDeleteRow)}"	
                            			}
                            			bootbox.alert(message);
                            			return;
                            		}else if(typeof(res) == "string"){
                            			bootbox.alert(res);
                            			return;
                            		}
                            	}
                            	</#if>
                                var selectedrowindex = $("#${id}").jqxGrid('getselectedrowindex');
                                var rowscount = $("#${id}").jqxGrid('getdatainformation').rowscount;
                                if (selectedrowindex >= 0 && selectedrowindex < rowscount) {                           
                                    var dataRecord = $("#${id}").jqxGrid('getrowdata', selectedrowindex);
                                    var id = $("#${id}").jqxGrid('getrowid', selectedrowindex);
                                    $("#dialog-delete${id}").text("${StringUtil.wrapString(uiLabelMap.wgdeleteconfirm)}?" );
                                    $("#dialog-delete${id}").dialog({
                                      resizable: false,
                                      height:180,
                                      modal: true,
                                      buttons: {
                                        "${StringUtil.wrapString(uiLabelMap.wgok)}": function() {
                                          $( this ).dialog( "close" );
                                        var offset = $("#${id}").offset();
                                         //$("#popupModifyWindow").jqxWindow({ position: { x: parseInt(offset.left) + 60, y: parseInt(offset.top) + 60 } });
                                        var commit = $("#${id}").jqxGrid('deleterow', id);                                                                                                                              
                                        },
                                        "${StringUtil.wrapString(uiLabelMap.wgcancel)}": function() {
                                          $( this ).dialog( "close" );
                                        }
                                      }
                                    });    
                                    $("#dialog-delete${id}").parent().css('zIndex',19000);
                                }
                            });
                        })();
                    </#if>
                    <#if updaterow=="true">
                        (function(){
                            container.append('<input style="margin-left: 20px;" id="updaterowbutton${id}" type="button" value="${uiLabelMap.accUpdateSelectedRow}" />');                                                            
                            var obj = $("#updaterowbutton${id}"); 
                            obj.jqxButton();
                            obj.on('click', function () {   
                                var selectedrowindex = $("#${id}").jqxGrid('getselectedcell').rowindex;                                                                             
                                var rowscount = $("#${id}").jqxGrid('getdatainformation').rowscount;                 
                                if (selectedrowindex >= 0 && selectedrowindex < rowscount) {
                                 //open the popup window when the user clicks a button.
                                 editrow = selectedrowindex;                         
                                 var offset = $("#${id}").offset();
                                 $("#popupWindow").jqxWindow({ position: { x: parseInt(offset.left) + 60, y: parseInt(offset.top) + 20 } });
                                 // get the clicked row's data and initialize the input fields.
                                 var dataRecord = $("#${id}").jqxGrid('getrowdata', selectedrowindex);         
                                <#list "${editColumns}"?split(";") as eColumn>
                                    $("#${eColumn}").val(dataRecord.${eColumn});
                                </#list>                                                                                                                                                         
                                 // show the popup window.
                                 $("#popupWindow").jqxWindow('open');    
                                }                                                
                               }); 
                        });
                    </#if>
                    <#if updatemultiplerows=="true">
                        (function(){
                            container.append('<button style="margin-left: 20px;" id="updatemultiplerows${id}"><i class="fa fa-check"></i>${uiLabelMap.accUpdateMultipleRow}</button>');
                            var obj = $("#updatemultiplerows${id}");
                            obj.jqxButton();                    
                            obj.on('click', function () {
                                var rowscount = $("#${id}").jqxGrid('getdatainformation').rowscount;
                                var status = "true";
                                var selectedrowindex = $("#${id}").jqxGrid('getselectedcell').rowindex;
                                var column =  $("#${id}").jqxGrid('getselectedcell').column;
                                var value = $("#${id}").jqxGrid('getselectedcell').value;
                                var dataRecord = $("#${id}").jqxGrid('getrowdata', selectedrowindex);
                                var columnNames = $('#${id}').jqxGrid('getcolumn',  column).text;
                                editrow = selectedrowindex;
                                $("#dialog-message").text("These all items in Column: " + columnNames + " := " + value +  " will be modify. Are you sure?" );
                                    $("#dialog-message" ).dialog({
                                      resizable: false,
                                      height:180,
                                      modal: true,
                                      buttons: {
                                        "Save": function() {                                  
                                          $( this ).dialog( "close" );
                                        var offset = $("#${id}").offset();
                                         $("#popupModifyWindow").jqxWindow({ position: { x: parseInt(offset.left) + 60, y: parseInt(offset.top) + 20 } });
                                         // get the clicked row's data and initialize the input fields.                              
                                         $("#newValueUpdate").val(value);
                                         // show the popup window.
                                         $("#popupModifyWindow").jqxWindow('open');                                                                   
                                        },
                                        Cancel: function() {
                                          $( this ).dialog( "close" );
                                        }
                                      }
                                    });
                            });                             
                        })();
                    </#if>      
                    <#if updateoffline=="true">
                        <#-- FIXME use this feature when data has changed -->
                        (function(){
                            /*$(window).bind("beforeunload", function(e){
                                return "${StringUtil.wrapString(uiLabelMap.confirmRefresh?default(''))}";
                            }); (disable for demo purpose)*/ 
                            localStorage.removeItem("localGridUpdate${id}");
                            <#if offlinerefreshbutton == "true">
                                container.append('<button style="margin-left: 20px;" id="updateoffline${id}"><i class="fa fa-check"></i>${uiLabelMap.accUpdateGrid}</button>');
                                var obj = $("#updateoffline${id}");
                                var urlStatus = "${updateUrl}";
                                obj.jqxButton();           
                                obj.on('click', function () {
                                    var dataupdate = localStorage.getItem("localGridUpdate${id}");
                                    var datadelete = localStorage.getItem("localGridDelete${id}");
                                    if(!dataupdate && !datadelete){
                                        return;
                                    }
                                    var remainupdate = checkDataUpdate(JSON.parse(dataupdate), JSON.parse(datadelete));
                                   if(remainupdate){
                                  	 updateRow${id}(urlStatus, {data:remainupdate});
                                   }
                                   if(datadelete){
                                    	deleteRow${id}("${removeUrl}",{data:datadelete});
                                    }
                                    $('#${id}').jqxGrid('updatebounddata');
                                });  
                            </#if>
                        })();
                    </#if>      
                    <#if excelExport=="true">
                        (function(){
                            container.append('<input style="margin-left: 20px;" id="excelExport${id}" type="button" value="${uiLabelMap.accExcel}" />');
                            var obj = $("#excelExport${id}");
                            obj.jqxButton();
                            $("#excelExport").click(function () {
                                $("#${id}").jqxGrid('exportdata', 'xls', 'jqxGrid');           
                            }); 
                        })();
                    </#if> 
                    <#if toPrint=="true">
                        (function(){
                            container.append('<input style="margin-left: 20px;" id="print${id}" type="button" value="${uiLabelMap.accPrint}" />');
                            var obj = $("#print${id}");
                            obj.jqxButton();
                            obj.click(function () {
                                var gridContent = $("#${id}").jqxGrid('exportdata', 'html');
                                var newWindow = window.open('', '', 'width=800, height=500'),
                                document = newWindow.document.open(),
                                pageContent =
                                    '<!DOCTYPE html>\n' +
                                    '<html>\n' +
                                    '<head>\n' +
                                    '<meta charset="utf-8" />\n' +
                                    '<title>jQWidgets Grid</title>\n' +
                                    '</head>\n' +
                                    '<body>\n' + gridContent + '\n</body>\n</html>';
                                document.write(pageContent);
                                document.close();
                                newWindow.print();
                            });
                        })();
                    </#if>
                    
                    <#if dropdownlist=="true" >
                        (function(){
                            container.append('<div style="margin-left: 20px; position: absolute; right: 4px; top: 6px;" id="dropdownlist${id}" />');
                            $("#dropdownlist${id}").jqxDropDownList({width: 150, selectedIndex: 0, source: ${ddlSource}, displayMember: '${displayMember}', valueMember: '${valueMember}'});    
                        })();
                    </#if>
                    <#if sendEmail == "true" >
                        (function(){
                            container.append('<button id="sendemail${id}" style="margin-left: 20px;"><i class="icon-envelope"></i>${uiLabelMap.sendEmail}</button>');
                            jqxid = "${id}";
                            var emailInput = $("#sendemail${id}");
                            var emailModal = $("#emailForm"); 
                            var outputs = $("#outputEmails");
                            var key = "currentEmail-${id}";
                            localStorage.removeItem(key);
                            emailInput.jqxButton();
                            emailInput.click(function(){
                                outputs.html("");
                                var rows = $('#${id}').jqxGrid('getboundrows');
                                for(var x in rows){
                                    var email = rows[x];
                                    if(email.infoString){
                                        var res = email.infoString;
                                        if(res.length > 1){
                                            for(var y in res){
                                                outputs.append("<option>"+res[y]+"</option>");
                                            }
                                        }else{
                                            outputs.append("<option>"+res[0]+"</option>");
                                        }
                                    }
                                }
                                var currentEmail = $.parseJSON(localStorage.getItem(key));
                                if(currentEmail){
                                    outputs.val(currentEmail);
                                }
                                outputs.trigger("liszt:updated");
                                emailModal.modal("show");
                            });
                        })();
                    </#if>   
                    <#if changeState == "true" >
                        (function(){
                            jqxid = "${id}";
                            container.append('<button id="changeState${id}" style="margin-left:20px;"><i class="icon-exchange"></i>${uiLabelMap.changeState}</button>');
                            var stateModal = $("#changeStateForm");
                            var stateBt = $("#changeState${id}"); 
                            var outputsCustomer = $("#stateCustomerId");
                            var key = "currentCustomer-${id}";
                            localStorage.removeItem(key);
                            var initName = function(customer){
                                if(customer){
                                    var party = customer.partyId;
                                    var name = "";
                                    if(customer.lastName){
                                        name += $.trim(customer.lastName);  
                                    }else if(customer.groupName){
                                        name += customer.groupName;
                                    }
                                    if(name && party){
                                        var res = name + "(" + party + ")";
                                        return res;
                                    }else if(party){
                                        var res = "(" + party + ")";
                                        return res;
                                    }
                                }
                            };
                            stateBt.jqxButton();
                            stateBt.click(function(){
                                outputsCustomer.html("");
                                outputsCustomer.val([]);
                                outputsCustomer.trigger("liszt:updated");
                                stateModal.modal("show");
                                var rows = $('#${id}').jqxGrid('getboundrows');
                                for(var x in rows){
                                    var customer = rows[x];
                                    var res = initName(customer);
                                    if(res){
                                        outputsCustomer.append("<option>"+res+"</option>");
                                    }
                                }
                                var customers = $.parseJSON(localStorage.getItem(key));
                                var tmp = [];
                                if(customers){
                                    for(var x in customers){
                                        var customer = customers[x];
                                        var res = initName(customer);
                                        if(res){
                                            tmp.push(res);
                                        }
                                    }
                                    outputsCustomer.val(tmp);
                                }   
                                outputsCustomer.trigger("liszt:updated");
                            });
                        })();
                    </#if>
                    <#if customtoolbaraction != "">
                    (function customToolbarAction(container){
                    	if(typeof(${customtoolbaraction}) == "function"){
                    		${customtoolbaraction}(container);
                    	}
                    })(maincontainer);
                    </#if>  
                },
                </#if>
                               
                <#if showstatusbar=="true">
                    showstatusbar: true,
                    statusbarheight: ${statusbarheight},
                    showaggregates: true,
                <#else>
                    showstatusbar: false,
                    statusbarheight: 0,
                    showaggregates: false,
                </#if>
                <#if height!="">
                    height:'${height}',
                </#if>
                pageable: ${pageable},
                <#if filterable=="true" && filtersimplemode=="true">
                    showfilterrow: true,
                </#if>
                columnsresize: ${columnsresize},
                columnsreorder: ${columnsreorder},
                sortable: ${sortable},
                scrollmode: '${scrollmode}',
                virtualmode: true,
                enablemousewheel: ${enablemousewheel},
                rendergridrows: function () {
                    return dataadapter${id}.records;
                },
                <#if initrowdetails=="true">
                    rowdetails: true,
                    rowdetailstemplate: { rowdetails: "<div style='margin: 10px;'>${rowdetailstemplateAdvance}</div>", rowdetailsheight: ${rowdetailsheight} },
                    initrowdetails: ${initrowdetailsDetail},
                </#if>
                columns: [
                  ${columnlist}
                  <#if editpopup=="true">
                  ,{ text: 'Edit', datafield: 'Edit', columntype: 'button', cellsrenderer: function () {
                         return "Edit";
                      }, buttonclick: function (row) {
                         // open the popup window when the user clicks a button.
                         editrow = row;
                         var offset = $("#${id}").offset();
                         $("#popupWindow").jqxWindow({ position: { x: parseInt(offset.left) + 60, y: parseInt(offset.top) + 20 } });
                         // get the clicked row's data and initialize the input fields.
                         var dataRecord = $("#${id}").jqxGrid('getrowdata', editrow);
                         <#list "${editColumns}"?split(";") as eColumn>
                            $("#${eColumn}").val(dataRecord.${eColumn});
                         </#list>    
                         // show the popup window.
                         $("#popupWindow").jqxWindow('open');
                  }}
                  </#if>                  
                ], 
                
                <#if '${columngrouplist}' != ''>
                    columngroups: [
                         ${columngrouplist} ]
                 </#if>                
            }); 
            
            <#if mouseRightMenu != "false">
                $("#${id}").on('contextmenu', function () {
                    return false;
                });
                <#if allGridMenu == "true">
                    $("#${id}").on('mousedown', function (event) {
                        if (event.which == 3) {
                            var scrollTop = $(window).scrollTop();
                            var scrollLeft = $(window).scrollLeft();
                            $("#${contextMenuId}").jqxMenu('open', parseInt(event.clientX) + 5 + scrollLeft, parseInt(event.clientY) + 5 + scrollTop);
                            return false;   
                        }
                    });
                <#else>
                    $("#${id}").on('rowClick', function (event) {
                        if (event.args.rightclick) {                
                            $("#${id}").jqxGrid('selectrow', event.args.rowindex);
                            var scrollTop = $(window).scrollTop();
                            var scrollLeft = $(window).scrollLeft();
//                            console.log('contextmenu',${contextMenuId});
                            $("#${contextMenuId}").jqxMenu('open', parseInt(event.args.originalEvent.clientX) + 5 + scrollLeft, parseInt(event.args.originalEvent.clientY) + 5 + scrollTop);
                            return false;   
                        }
                    }); 
                </#if>
            </#if>

            <#if '${groupsexpanded}' == "true">                             
                    $('#${id}').jqxGrid({ groupsexpandedbydefault: true}); 
            </#if>               
            <#if rowselectfunction != "">
             $("#${id}").on('rowSelect', function (event) {
                ${rowselectfunction}
             }); 
            </#if>
            <#if url != "">
             $("#${id}").on('bindingComplete', function (event) {
            	$("#${id}Container").height('auto');
            	$('#${id}').jqxGrid('scrolloffset',0,1);
				CallbackFocusFilter();
             }); 
            <#else>
             $("#${id}Container").height('auto');
            </#if>
            <#if bindingcompletefunction != "">
                $("#${id}").on('bindingComplete', function (event) {
                    ${bindingcompletefunction}
                 }); 
            </#if>
            <#if afterinitfunction != "">
                ${afterinitfunction}
            </#if>
            $("#${id}").on('cellBeginEdit', function (event) 
             {
                oldEditingValue = $('#${id}').jqxGrid('getrowdata', args.rowindex);
                oldEditingValue = jQuery.extend({}, oldEditingValue);
             });
             $('#${id}').on('filter', function(){
             	CallbackFocusFilter();
             })
            <#if rowunselectfunction != "">
             $("#${id}").on('rowUnselect', function (event) {
                ${rowunselectfunction}
             }); 
            </#if>
            
            <#if bindresize=="true">
                // Responsive for Grid widget
                var tmpWidth = $(window).width() - 40;
                $(window).bind('resize', function() {
                    var sibar = $('#sidebar');
                    var grid = $('#${id}');
                    if($('#sidebar').css("display") != "none"){
                        // NEED UPDATE THE FOLLOWING CODE
                        if($('#sidebar').hasClass("menu-min") != null){
                            grid.jqxGrid({ width: $(window).width() - $('#sidebar').width() - 40 });                     
                        }else{
                            grid.jqxGrid({ width: $(window).width() - $('#sidebar').width() - 40 });
                        }
                        // END COMMENT
                    }else{
                        grid.jqxGrid({ width: tmpWidth });
                    }
                    tmpWidth = grid.jqxGrid('width');
                    $("#container${id}").width(tmpWidth);
                    $("#container").width(tmpWidth);
                    $("#jqxNotification${id}").jqxNotification({ width: tmpWidth, appendContainer: "#container${id}", opacity: 1, autoClose: false, template: "success" });
                    $("#jqxNotification").jqxNotification({ width: tmpWidth, appendContainer: "#container", opacity: 1, autoClose: false, template: "success" });
                    $("#jqxNotification${id}").jqxNotification({ icon: { width: 25, height: 25, url: '/aceadmin/assets/images/info.jpg'},width: tmpWidth, appendContainer: "#container${id}", opacity: 1, autoClose: false, template: "success" });
                    $("#jqxNotification").jqxNotification({ icon: { width: 25, height: 25, url: '/aceadmin/assets/images/info.jpg'},width: tmpWidth, appendContainer: "#container", opacity: 1, autoClose: false, template: "success" });
                    AutoMeasureGridHeight(grid);
                }).trigger('resize');
                $('#sidebar').bind('resize', function() {
                    grid.jqxGrid({ width: tmpWidth });
                    tmpWidth = grid.jqxGrid('width');
                    $("#container${id}").width(tmpWidth);
                    $("#container").width(tmpWidth);
                    $("#jqxNotification${id}").jqxNotification({ width: tmpWidth, appendContainer: "#container${id}", opacity: 1, autoClose: false, template: "success" });
                    $("#jqxNotification").jqxNotification({ width: tmpWidth, appendContainer: "#container", opacity: 1, autoClose: false, template: "success" });
                    $("#jqxNotification${id}").jqxNotification({ icon: { width: 25, height: 25, url: '/aceadmin/assets/images/info.jpg'},width: tmpWidth, appendContainer: "#container${id}", opacity: 1, autoClose: false, template: "success" });
                    $("#jqxNotification").jqxNotification({ icon: { width: 25, height: 25, url: '/aceadmin/assets/images/info.jpg'},width: tmpWidth, appendContainer: "#container", opacity: 1, autoClose: false, template: "success" });
                });//.trigger('resize');
            <#else>
                var tmpWidth = $('#${id}').jqxGrid('width');
                $("#container").width(tmpWidth);
                $("#jqxNotification").jqxNotification({ icon: { width: 25, height: 25, url: '/aceadmin/assets/images/info.jpg'},width: tmpWidth, appendContainer: "#container", opacity: 1, autoClose: false, template: "success" }); 
                $("#container${id}").width(tmpWidth);
                $("#jqxNotification${id}").jqxNotification({ icon: { width: 25, height: 25, url: '/aceadmin/assets/images/info.jpg'},width: tmpWidth, appendContainer: "#container${id}", opacity: 1, autoClose: false, template: "success" }); 
            </#if>
            <#if editpopup=="true" || doubleClick=="true" || updaterow=="true" || addType=="popup">
                // initialize the popup window and buttons.
                $("#popupWindow").jqxWindow({
                    width: 450, resizable: false,  isModal: true, autoOpen: false, cancelButton: $("#Cancel"), modalOpacity: 0.7, modalZIndex: 1000           
                });
    
                $("#Cancel").jqxButton({ theme: theme });
                $("#Save").jqxButton({ theme: theme });
    
                // update the edited row when the user clicks the 'Save' button.
                $("#Save").click(function () {
                    if (editrow >= 0) {
                        var row = { 
                            <#list "${editColumns}"?split(";") as eColumn>
                                //${eColumn} : $("#${eColumn}").val(),                          
                            </#list>    
                        };
                        var rowID = $('#${id}').jqxGrid('getrowid', editrow);
                        $('#${id}').jqxGrid('updaterow', rowID, row);
                        $("#popupWindow").jqxWindow('hide');
                    }
                });
            </#if>
            <#if updatemultiplerows=="true">
                $("#popupModifyWindow").jqxWindow({
                    width: 450, resizable: false,  isModal: true, autoOpen: false, cancelButton: $("#CancelModify"), modalOpacity: 0.7, modalZIndex: 1000           
                });
    
                $("#popupModifyWindow").on('open', function () {
                    $("#newValueUpdate").jqxInput('selectAll');
                });
             
                $("#CancelModify").jqxButton({ theme: theme });
                $("#SaveModify").jqxButton({ theme: theme });
    
                // update the edited row when the user clicks the 'Save' button.
                $("#SaveModify").click(function () {
                    if (editrow >= 0) {
                           var selectedrowindex = $("#${id}").jqxGrid('getselectedcell').rowindex;
                            var columnvalue =  $("#${id}").jqxGrid('getselectedcell').column;
                            var value = $("#${id}").jqxGrid('getselectedcell').value;
                            var dataRecord = $("#${id}").jqxGrid('getrowdata', selectedrowindex);
                            var columnNames = $('#${id}').jqxGrid('getcolumn',  columnvalue).text;                
                        var row = { 
                            columnName: columnvalue,
                            newValue: $("#newValueUpdate").val(),
                            oldValue: value 
                        };                    
                        var rowID = $('#${id}').jqxGrid('getrowid', editrow);
                        $('#${id}').jqxGrid('updaterow', rowID, row);
                        $("#popupModifyWindow").jqxWindow('hide'); 
                    } 
                });
            </#if> 
            
            <#if doubleClick=="true">
                $("#${id}").on('rowDoubleClick', function (event) {
                var args = event.args;
                var row = args.rowindex;
                 
                // open the popup window when the user clicks a button.
                 editrow = row;
                 var offset = $("#${id}").offset();
                 $("#popupWindow").jqxWindow({ position: { x: parseInt(offset.left) + 60, y: parseInt(offset.top) + 20 } });
                 // get the clicked row's data and initialize the input fields.
                 var dataRecord = $("#${id}").jqxGrid('getrowdata', editrow);
                 
                 <#list "${editColumns}"?split(";") as eColumn>
                    $("#${eColumn}").val(dataRecord.${eColumn});
                 </#list>                   
                /* $("#glAccountId").val(dataRecord.glAccountId);
                 $("#parentGlAccountId").val(dataRecord.parentGlAccountId);
                 $("#accountCode").val(dataRecord.accountCode);
                 $("#accountName").val(dataRecord.accountName);
                 $("#description").val(dataRecord.description); */
                 // show the popup window.
                 $("#popupWindow").jqxWindow('open');    
            });        
            </#if>            
            <#if autoMeasureHeight=="true" && autoheight='false'>
            	(function MesureHeight(){
            		console.log($("#${id}"));
            		AutoMeasureGridHeight($("#${id}"));
            	})();
            </#if>
        <#if customLoadFunction == "true">
            };
        <#else>
            });
        </#if>         
    </script>
    <#if showlist != "">
        <div id="jqxconfig${id}" style="display:none;">
            <div><i class="fa-cogs btn-mini" style="padding-right:5px;margin:0px;"></i>${uiLabelMap.clconfiguration}</div>
            <div class='form-window-container' id="windowContent${id}">
            	<div class='form-window-content'>
            		<div class='row-fluid margin-bottom10'>
						<div class='span5 text-algin-right'>
							<label class="asterisk">${uiLabelMap.cldisplay}</label>
						</div>  
						<div class="span7">
							<div id="showSL${id}"></div>
				   		</div>		
					</div>
					<div class='row-fluid margin-bottom10'>
						<div class='span5 text-algin-right'>
							<label class="asterisk">${uiLabelMap.clpin}</label>
						</div>  
						<div class="span7">
							<div id="frozenSL${id}"></div>
				   		</div>		
					</div>
            	</div>
            	<div class="form-action">
					<button id="configCancel${id}" class='btn btn-danger form-action-button pull-right'><i class='fa-remove'></i> ${uiLabelMap.CommonCancel}</button>
					<button id="configSave${id}" class='btn btn-primary form-action-button pull-right'><i class='fa-check'></i> ${uiLabelMap.CommonSave}</button>
				</div>
            </div>
        </div>
        <script type="text/javascript">
            function openJqxConfigWindow${id}(){
                var wtmp = window;
                var tmpwidth = $('#jqxconfig${id}').jqxWindow('width');
                $("#jqxconfig${id}").jqxWindow({ position: { x: (wtmp.outerWidth - tmpwidth)/2, y: pageYOffset + 80 } });
                $('#jqxconfig${id}').jqxWindow('open');
            };
        </script>
    </#if>
    <div id="container${id}" style="background-color: transparent; overflow: auto;">
    </div>
    <div id="container" style="background-color: transparent; overflow: auto;">
    </div>
    <div id="jqxNotification${id}">
        <div id="notificationContent${id}">
        </div>
    </div>
    <div id="jqxNotification">
        <div id="notificationContent">
        </div>
    </div>
    <#if idExisted=="false">
        <#if customCss != ""><div class="${customCss}"></#if>
            <div id="${id}Container" style="height:0px">
	        	<div id="${id}">
	            </div>
            </div>
        <#if customCss != ""></div></#if>
    </#if>
    <#if editpopup=="true" || doubleClick=="true" || updaterow=="true" || addType=="popup">
    <div id="popupWindow" style="display:none;">
        <div>Edit</div>
        <div style="overflow: hidden;">
            <table>
                 <#list "${editColumns}"?split(";") as eColumn>
                    <tr>
                        <td align="right">${eColumn}:</td>
                        <#if '${eColumn}' == '${primaryColumn}' >
                            <td align="left"><input id="${eColumn}" readonly/></td>
                        <#else>
                            <td align="left"><input id="${eColumn}" /></td>
                        </#if>
                    </tr>
                 </#list>                      
                <tr>
                    <td align="right"></td>
                    <td style="padding-top: 10px;" align="right"><input style="margin-right: 5px;" type="button" id="Save" value="Save" /><input id="Cancel" type="button" value="Cancel" /></td>
                </tr>
            </table>
        </div>
    </div>
    <div id="dialog-confirm" title="Modify data?">
        <div style="overflow: hidden;">
            <div style="margin-top: 10px;" id="dialog-confirm"></div>
        </div>
    </div>
    <div id="dialog-message" title="Modify data?">      
        <div style="margin-top: 10px;" id="dialog-message"></div>
    </div>
            
    </#if> 
     <#if updatemultiplerows=="true">
        <div id="popupModifyWindow">
            <div>Edit</div>
            <div style="overflow: hidden;">
                <table>
                    <tr>
                        <td align="right">New values:</td>
                        <td align="left"><input id="newValueUpdate" /></td>
                        <tr>
                        <td align="right"></td>
                        <td style="padding-top: 10px;" align="right"><input style="margin-right: 5px;" type="button" id="SaveModify" value="Save" /><input id="CancelModify" type="button" value="Cancel" /></td>
                    </tr>
                    </tr>                    
                </table>
            </div>
    </div>      
     </#if>   
     <div id="dialog-delete${id}" title="${StringUtil.wrapString(uiLabelMap.wgdeletedata)}?">       
    </div>  
</#macro>

<#macro renderFilterType arrayName="dataStringFilterType">
    var ${arrayName} = new Array();
    // begin init String filter type
    var row = {};
    /*row["stringFilterType"] = "EMPTY";
    row["description"] = "R?ng";
    ${arrayName}[0] = row;
    row = {};
    row["stringFilterType"] = "NOT_EMPTY";
    row["description"] = "Không r?ng";
    ${arrayName}[1] = row;*/
    row = {};
    row["stringFilterType"] = "CONTAINS";
    row["description"] = "Ch?a";
    ${arrayName}[2] = row;
    /*row = {};
    row["stringFilterType"] = "CONTAINS_CASE_SENSITIVE";
    row["description"] = "Ch?a(Phân bi?t hoa thu?ng)";
    ${arrayName}[3] = row;*/
    row = {};
    row["stringFilterType"] = "DOES_NOT_CONTAIN";
    row["description"] = "Không ch?a";
    ${arrayName}[4] = row;
    /*row = {};
    row["stringFilterType"] = "DOES_NOT_CONTAIN_CASE_SENSITIVE";
    row["description"] = "Không ch?a(Phân bi?t hoa thu?ng)";
    ${arrayName}[5] = row;*/
    row = {};
    row["stringFilterType"] = "STARTS_WITH";
    row["description"] = "B?t d?u b?ng";
    ${arrayName}[6] = row;
    /*row = {};
    row["stringFilterType"] = "STARTS_WITH_CASE_SENSITIVE";
    row["description"] = "B?t d?u b?ng(Phân bi?t hoa thu?ng)";
    ${arrayName}[7] = row;*/
    row = {};
    row["stringFilterType"] = "ENDS_WITH";
    row["description"] = "K?t thúc b?ng";
    ${arrayName}[8] = row;
    /*row = {};
    row["stringFilterType"] = "ENDS_WITH_CASE_SENSITIVE";
    row["description"] = "K?t thúc b?ng(Phân bi?t hoa thu?ng)";
    ${arrayName}[9] = row;*/
    row = {};
    row["stringFilterType"] = "EQUAL";
    row["description"] = "B?ng";
    ${arrayName}[10] = row;
    row = {};
    row["stringFilterType"] = "NOT_EQUAL";
    row["description"] = "Không b?ng";
    ${arrayName}[11] = row;
    /*row = {};
    row["stringFilterType"] = "EQUAL_CASE_SENSITIVE";
    row["description"] = "B?ng(PhÃ¢n biáº¿t hoa thÆ°á»?ng)";
    ${arrayName}[11] = row;*/
    row = {};
    row["stringFilterType"] = "NULL";
    row["description"] = "Null";
    ${arrayName}[12] = row;
    row = {};
    row["stringFilterType"] = "NOT_NULL";
    row["description"] = "Không null";
    ${arrayName}[13] = row;
</#macro>
<#macro renderDateTimeFilterType arrayName="dataDateTimeFilterType">
    var ${arrayName} = new Array();
    // begin init String filter type
    var row = {};
    row = {};
    row["datetimeFilterType"] = "EQUAL";
    row["description"] = "B?ng";
    ${arrayName}[0] = row;
    row = {};
    row["datetimeFilterType"] = "NOT_EQUAL";
    row["description"] = "Không b?ng";
    ${arrayName}[1] = row;
    row = {};
    row["datetimeFilterType"] = "NULL";
    row["description"] = "Null";
    ${arrayName}[2] = row;
    row = {};
    row["datetimeFilterType"] = "NOT_NULL";
    row["description"] = "Không null";
    ${arrayName}[3] = row;
    row = {};
    row["datetimeFilterType"] = "LESS_THAN";
    row["description"] = "Nh? hon";
    ${arrayName}[4] = row;
    row = {};
    row["datetimeFilterType"] = "LESS_THAN_OR_EQUAL";
    row["description"] = "Nh? hon ho?c b?ng";
    ${arrayName}[5] = row;
    row = {};
    row["datetimeFilterType"] = "GREATER_THAN";
    row["description"] = "L?n hon";
    ${arrayName}[6] = row;
    row = {};
    row["datetimeFilterType"] = "GREATER_THAN_OR_EQUAL";
    row["description"] = "L?n hon ho?c b?ng";
    ${arrayName}[7] = row;
</#macro>
<#macro renderJqxTitle titlePropertyTmp id>
    <#if titlePropertyTmp?contains("-")>
        var jqxheader = $("<div id='toolbarcontainer${id}' class='widget-header'><h4>"
            <#list titlePropertyTmp?split("-") as str>
                <#if str?contains("[")>
                <#assign strTmp = str?substring(2, str.length - 1)/>
                    + "${strTmp}" + "&nbsp;"
                <#else>
                    + "${StringUtil.wrapString(uiLabelMap[str])}&nbsp;"
                </#if>
            </#list> 
            + "</h4><div id='toolbarButtonContainer${id}' class='pull-right'></div></div>");
    <#else>
        var jqxheader = $("<div id='toolbarcontainer${id}' class='widget-header'><h4>" + "${StringUtil.wrapString(uiLabelMap[titlePropertyTmp])}" + "</h4><div id='toolbarButtonContainer${id}' class='pull-right'></div></div>");
    </#if>
</#macro>
<#global jqTable=jqTable />
<#global jqGrid=jqGrid />
<#global useLocalizationNumberFunction=useLocalizationNumberFunction />
<#global jqGridMinimumLib=jqGridMinimumLib />
<#global jqMinimumLib=jqMinimumLib />
<#global renderFilterType=renderFilterType />
<#global renderDateTimeFilterType=renderDateTimeFilterType />
<#--
    End of jq renderer
-->

<#macro renderField text>
  <#if text?exists>
    ${text}<#lt/>
  </#if>
</#macro>

<#macro renderDisplayField type imageLocation idName description title class alert inPlaceEditorUrl="" inPlaceEditorParams="">
  <#if type?has_content && type=="image">
    <img src="${imageLocation}" alt=""><#lt/>
  <#else>
    <#if inPlaceEditorUrl?has_content || class?has_content || alert=="true" || title?has_content>
      <span <#if idName?has_content>id="cc_${idName}"</#if> <#if title?has_content>title="${title}"</#if> <@renderClass class alert />><#t/>
    </#if>

    <#if description?has_content>
      ${description?replace("\n", "<br />")}<#t/>
    <#else>
      &nbsp;<#t/>
    </#if>
    <#if inPlaceEditorUrl?has_content || class?has_content || alert=="true">
      </span><#lt/>
    </#if>
    <#if inPlaceEditorUrl?has_content && idName?has_content>
      <script language="JavaScript" type="text/javascript"><#lt/>
        ajaxInPlaceEditDisplayField('cc_${idName}', '${inPlaceEditorUrl}', ${inPlaceEditorParams});<#lt/>
      </script><#lt/>
    </#if>
  </#if>
</#macro>
<#macro renderHyperlinkField></#macro>

<#macro renderTextField name className alert value textSize maxlength id event action disabled clientAutocomplete ajaxUrl ajaxEnabled mask placeholder="">
  <#if mask?has_content>
    <script type="text/javascript">
      jQuery(function($){jQuery("#${id}").mask("${mask}");});
    </script>
  </#if>
  <input type="text" name="${name?default("")?html}"<#t/>
    <@renderClass className alert />
    <#if value?has_content> value="${value}"</#if><#rt/>
    <#if textSize?has_content> size="${textSize}"</#if><#rt/>
    <#if maxlength?has_content> maxlength="${maxlength}"</#if><#rt/>
    <#if disabled?has_content && disabled> disabled="disabled"</#if><#rt/>
    <#if id?has_content> id="${id}"</#if><#rt/>
    <#if event?has_content && action?has_content> ${event}="${action}"</#if><#rt/>
    <#if clientAutocomplete?has_content && clientAutocomplete=="false"> autocomplete="off"</#if><#rt/>
    <#if placeholder?has_content> placeholder="${placeholder}"</#if><#rt/>
  /><#t/>
  <#if ajaxEnabled?has_content && ajaxEnabled>
    <#assign defaultMinLength = Static["org.ofbiz.base.util.UtilProperties"].getPropertyValue("widget.properties", "widget.autocompleter.defaultMinLength")>
    <#assign defaultDelay = Static["org.ofbiz.base.util.UtilProperties"].getPropertyValue("widget.properties", "widget.autocompleter.defaultDelay")>
    <script language="JavaScript" type="text/javascript">ajaxAutoCompleter('${ajaxUrl}', false, ${defaultMinLength!2}, ${defaultDelay!300});</script><#lt/>
  </#if>
</#macro>

<#macro renderTextareaField name className alert cols rows id readonly value visualEditorEnable buttons language="">
  <textarea name="${name}"<#t/><@renderClass className alert />
    <#if cols?has_content> cols="${cols}"</#if><#rt/>
    <#if rows?has_content> rows="${rows}"</#if><#rt/>
    <#if id?has_content> id="${id}"</#if><#rt/>
    <#if readonly?has_content && readonly=='readonly'> readonly="readonly"</#if><#rt/>
    <#if maxlength?has_content> maxlength="${maxlength}"</#if><#rt/>><#t/>
    <#if value?has_content>${value}</#if><#t/>
  </textarea><#lt/>
  <#if visualEditorEnable?has_content>
    <script language="javascript" src="/images/jquery/plugins/elrte-1.3/js/elrte.min.js" type="text/javascript"></script><#rt/>
    <#if language?has_content && language != "en">
      <script language="javascript" src="/images/jquery/plugins/elrte-1.3/js/i18n/elrte.${language!"en"}.js" type="text/javascript"></script><#rt/>
    </#if>
    <link href="/images/jquery/plugins/elrte-1.3/css/elrte.min.css" rel="stylesheet" type="text/css">
    <script language="javascript" type="text/javascript">
      var opts = {
         cssClass : 'el-rte',
         lang     : '${language!"en"}',
         toolbar  : '${buttons?default("maxi")}',
         doctype  : '<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">', //'<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN">',
         cssfiles : ['/images/jquery/plugins/elrte-1.3/css/elrte-inner.css']
      }
      jQuery('#${id?default("")}').elrte(opts);
    </script>
  </#if>
</#macro>

<#macro renderDateTimeField name className alert title value size maxlength id dateType shortDateInput timeDropdownParamName defaultDateTimeString localizedIconTitle timeDropdown timeHourName classString hour1 hour2 timeMinutesName minutes isTwelveHour ampmName amSelected pmSelected compositeType formName mask="" event="" action="" step="" timeValues="">
  <span class="view-calendar">
    <#if dateType!="time" >
      <input type="text" name="${name}_i18n" <@renderClass className alert /><#rt/>
        <#if title?has_content> title="${title}"</#if>
        <#if value?has_content> value="${value}"</#if>
        <#if size?has_content> size="${size}"</#if><#rt/>
        <#if maxlength?has_content>  maxlength="${maxlength}"</#if>
        <#if id?has_content> id="${id}_i18n"</#if>/><#rt/>
    </#if>
    <#-- the style attribute is a little bit messy but when using disply:none the timepicker is shown on a wrong place -->
    <input type="text" name="${name}" style="height:1px;width:1px;border:none;background-color:transparent;display:none;" <#if event?has_content && action?has_content> ${event}="${action}"</#if> <@renderClass className alert /><#rt/>
      <#if title?has_content> title="${title}"</#if>
      <#if value?has_content> value="${value}"</#if>
      <#if size?has_content> size="${size}"</#if><#rt/>
      <#if maxlength?has_content>  maxlength="${maxlength}"</#if>
      <#if id?has_content> id="${id}"</#if>/><#rt/>
    <#if dateType!="time" >
      <script type="text/javascript">
        <#-- If language specific lib is found, use date / time converter else just copy the value fields -->
        if (Date.CultureInfo != undefined) {
          var initDate = <#if value?has_content>jQuery("#${id}_i18n").val()<#else>""</#if>;
          if (initDate != "") {
            var dateFormat = "dd/MM/yyyy"<#if shortDateInput?exists && !shortDateInput> + " " + "HH:mm:ss"</#if>;
            <#-- bad hack because the JS date parser doesn't understand dots in the date / time string -->
            if (initDate.indexOf('.') != -1) {
              initDate = initDate.substring(0, initDate.indexOf('.'));
            }
            var ofbizTime = "<#if shortDateInput?exists && shortDateInput>yyyy-MM-dd<#else>yyyy-MM-dd HH:mm:ss</#if>";
            var dateObj = Date.parseExact(initDate, ofbizTime);
            var formatedObj = dateObj.toString(dateFormat);
            jQuery("#${id}_i18n").val(formatedObj);
          }
          
          jQuery("#${id}").change(function() {
            var ofbizTime = "<#if shortDateInput?exists && shortDateInput>yyyy-MM-dd<#else>yyyy-MM-dd HH:mm:ss</#if>";
            var newValue = ""
            if (this.value != "") {
              var dateObj = Date.parseExact(this.value, ofbizTime);
              var dateFormat = "dd/MM/yyyy"<#if shortDateInput?exists && !shortDateInput> + " " + "HH:mm:ss"</#if>;
              newValue = dateObj.toString(dateFormat);
            }
            jQuery("#${id}_i18n").val(newValue);
          });
          jQuery("#${id}_i18n").change(function() {
            var dateFormat = "dd/MM/yyyy"<#if shortDateInput?exists && !shortDateInput> + " " + "HH:mm:ss"</#if>,
            newValue = "",
            dateObj = Date.parseExact(this.value, dateFormat),
            ofbizTime;
            if (this.value != "" && dateObj !== null) {
              ofbizTime = "<#if shortDateInput?exists && shortDateInput>yyyy-MM-dd<#else>yyyy-MM-dd HH:mm:ss</#if>";
              newValue = dateObj.toString(ofbizTime);
            }
            else { // invalid input
              jQuery("#${id}_i18n").val("");
            }
            jQuery("#${id}").val(newValue);
          });
        } else {
          <#-- fallback if no language specific js date file is found -->
          jQuery("#${id}").change(function() {
          jQuery("#${id}_i18n").val(this.value);
        });
        jQuery("#${id}_i18n").change(function() {
          jQuery("#${id}").val(this.value);
        });
      }

      <#if shortDateInput?exists && shortDateInput>
        jQuery("#${id}").datepicker({
      <#else>
        jQuery("#${id}").datetimepicker({
          showSecond: true,
          <#-- showMillisec: true, -->
          timeFormat: 'hh:mm:ss',
          stepHour: 1,
          stepMinute: 1,
          stepSecond: 1,
      </#if>
          showOn: 'button',
          buttonText: '',
          buttonImageOnly: false,
          dateFormat: 'yy-mm-dd'
        })
        <#if mask?has_content>.mask("${mask}")</#if>
        ;
      </script>
    </#if>
    <#if timeDropdown?has_content && timeDropdown=="time-dropdown">
      <select name="${timeHourName}" <#if classString?has_content>class="${classString}"</#if>><#rt/>
        <#if isTwelveHour>
          <#assign x=11>
          <#list 0..x as i>
            <option value="${i}"<#if hour1?has_content><#if i=hour1> selected="selected"</#if></#if>>${i}</option><#rt/>
          </#list>
        <#else>
          <#assign x=23>
          <#list 0..x as i>
            <option value="${i}"<#if hour2?has_content><#if i=hour2> selected="selected"</#if></#if>>${i}</option><#rt/>
          </#list>
        </#if>
        </select>:<select name="${timeMinutesName}" <#if classString?has_content>class="${classString}"</#if>><#rt/>
          <#assign values = Static["org.ofbiz.base.util.StringUtil"].toList(timeValues)>
          <#list values as i>
            <option value="${i}"<#if minutes?has_content><#if i?number== minutes ||((i?number==(60 -step?number)) && (minutes &gt; 60 - (step?number/2))) || ((minutes &gt; i?number )&& (minutes &lt; i?number+(step?number/2))) || ((minutes &lt; i?number )&& (minutes &gt; i?number-(step?number/2)))> selected="selected"</#if></#if>>${i}</option><#rt/>
          </#list>
        </select>
        <#rt/>
        <#if isTwelveHour>
          <select name="${ampmName}" <#if classString?has_content>class="${classString}"</#if>><#rt/>
            <option value="AM" <#if amSelected == "selected">selected="selected"</#if> >AM</option><#rt/>
            <option value="PM" <#if pmSelected == "selected">selected="selected"</#if>>PM</option><#rt/>
          </select>
        <#rt/>
      </#if>
    </#if>
    <input type="hidden" name="${compositeType}" value="Timestamp"/>
  </span>
</#macro>

<#macro renderDropDownField name className alert id multiple formName otherFieldName event action size firstInList currentValue explicitDescription allowEmpty options fieldName otherFieldName otherValue otherFieldSize dDFCurrent ajaxEnabled noCurrentSelectedKey ajaxOptions frequency minChars choices autoSelect partialSearch partialChars ignoreCase fullSearch>
  <span class="ui-widget">
    <select name="${name?default("")}<#rt/>" <@renderClass className alert /><#if id?has_content> id="${id}"</#if><#if multiple?has_content> multiple="multiple"</#if><#if otherFieldSize gt 0> onchange="process_choice(this,document.${formName}.${otherFieldName})"</#if><#if event?has_content> ${event}="${action}"</#if><#if size?has_content> size="${size}"</#if>>
      <#if firstInList?has_content && currentValue?has_content && !multiple?has_content>
        <option selected="selected" value="${currentValue}">${explicitDescription}</option><#rt/>
        <option value="${currentValue}">---</option><#rt/>
      </#if>
      <#if allowEmpty?has_content || !options?has_content>
        <option value="">&nbsp;</option>
      </#if>
      <#list options as item>
        <#if multiple?has_content>
          <option<#if currentValue?has_content && item.selected?has_content> selected="${item.selected}" <#elseif !currentValue?has_content && noCurrentSelectedKey?has_content && noCurrentSelectedKey == item.key> selected="selected" </#if> value="${item.key}">${item.description}</option><#rt/>
        <#else>
          <option<#if currentValue?has_content && currentValue == item.key && dDFCurrent?has_content && "selected" == dDFCurrent> selected="selected"<#elseif !currentValue?has_content && noCurrentSelectedKey?has_content && noCurrentSelectedKey == item.key> selected="selected"</#if> value="${item.key}">${item.description}</option><#rt/>
        </#if>
      </#list>
    </select>
  </span>
  <#if otherFieldName?has_content>
    <noscript><input type='text' name='${otherFieldName}' /></noscript>
    <script type='text/javascript' language='JavaScript'><!--
      disa = ' disabled';
      if(other_choice(document.${formName}.${fieldName}))
        disa = '';
      document.write("<input type='text' name='${otherFieldName}' value='${otherValue?js_string}' size='${otherFieldSize}'"+disa+" onfocus='check_choice(document.${formName}.${fieldName})' />");
      if(disa && document.styleSheets)
      document.${formName}.${otherFieldName}.style.visibility  = 'hidden';
    //--></script>
  </#if>

  <#if ajaxEnabled>
    <script language="JavaScript" type="text/javascript">
      ajaxAutoCompleteDropDown();
      jQuery(function() {
        jQuery("#${id}").combobox();
      });
    </script>
  </#if>
</#macro>

<#macro renderDropDownFieldSelectedAll name className alert id multiple formName otherFieldName event action size firstInList currentValue explicitDescription allowEmpty options fieldName otherFieldName otherValue otherFieldSize dDFCurrent ajaxEnabled noCurrentSelectedKey ajaxOptions frequency minChars choices autoSelect partialSearch partialChars ignoreCase fullSearch selectedAll>
  <span class="ui-widget">
    <select name="${name?default("")}<#rt/>" <@renderClass className alert /><#if id?has_content> id="${id}"</#if><#if multiple?has_content> multiple="multiple"</#if><#if otherFieldSize gt 0> onchange="process_choice(this,document.${formName}.${otherFieldName})"</#if><#if event?has_content> ${event}="${action}"</#if><#if size?has_content> size="${size}"</#if>>
      <#if firstInList?has_content && currentValue?has_content && !multiple?has_content>
        <option selected="selected" value="${currentValue}">${explicitDescription}</option><#rt/>
        <option value="${currentValue}">---</option><#rt/>
      </#if>
      <#if allowEmpty?has_content || !options?has_content>
        <option value="">&nbsp;</option>
      </#if>
      
      <#list options as item>
        <#if multiple?has_content>
            <#if (currentValue?exists && currentValue?has_content) || (currentValue?has_content && item.selected?has_content) || (!currentValue?has_content && noCurrentSelectedKey?has_content && noCurrentSelectedKey == item.key)>
                <option<#if currentValue?has_content && item.selected?has_content> selected="${item.selected}" <#elseif !currentValue?has_content && noCurrentSelectedKey?has_content && noCurrentSelectedKey == item.key> selected="selected" </#if> value="${item.key}">${item.description}</option><#rt/>
            <#else>
                <#if selectedAll?has_content>
                    <option selected="selected" value="${item.key}">${item.description}</option><#rt/>
                <#else>
                    <option value="${item.key}">${item.description}</option><#rt/>
                </#if>
            </#if>
        <#else>
            <option<#if currentValue?has_content && currentValue == item.key && dDFCurrent?has_content && "selected" == dDFCurrent> selected="selected"<#elseif !currentValue?has_content && noCurrentSelectedKey?has_content && noCurrentSelectedKey == item.key> selected="selected"</#if> value="${item.key}">${item.description}</option><#rt/>
        </#if>
      </#list>
    </select>
  </span>
  <#if otherFieldName?has_content>
    <noscript><input type='text' name='${otherFieldName}' /></noscript>
    <script type='text/javascript' language='JavaScript'><!--
      disa = ' disabled';
      if(other_choice(document.${formName}.${fieldName}))
        disa = '';
      document.write("<input type='text' name='${otherFieldName}' value='${otherValue?js_string}' size='${otherFieldSize}'"+disa+" onfocus='check_choice(document.${formName}.${fieldName})' />");
      if(disa && document.styleSheets)
      document.${formName}.${otherFieldName}.style.visibility  = 'hidden';
    //--></script>
  </#if>

  <#if ajaxEnabled>
    <script language="JavaScript" type="text/javascript">
      ajaxAutoCompleteDropDown();
      jQuery(function() {
        jQuery("#${id}").combobox();
      });
    </script>
  </#if>
</#macro>

<#macro renderCheckField items className alert id allChecked currentValue name event action>
  <#list items as item>
    <label style="display:inline;" <@renderClass className alert />><#rt/>
      <input type="checkbox"<#if (item_index == 0)> id="${id}"</#if><#rt/>
        <#if allChecked?has_content && allChecked> checked="checked" <#elseif allChecked?has_content && !allChecked>
          <#elseif currentValue?has_content && currentValue==item.value> checked="checked"</#if> 
          name="${name?default("")?html}" value="${item.value?default("")?html}"<#if event?has_content> ${event}="${action}"</#if>/><#rt/>
        ${item.description?default("")}
        <span class="lbl"></span>
    </label>
  </#list>
</#macro>

<#macro renderRadioField items className alert currentValue noCurrentSelectedKey name event action>
  <#list items as item>
    <span <@renderClass className alert />><#rt/>
      <input type="radio"<#if currentValue?has_content><#if currentValue==item.key> checked="checked"</#if>
        <#elseif noCurrentSelectedKey?has_content && noCurrentSelectedKey == item.key> checked="checked"</#if> 
        name="${name?default("")?html}" value="${item.key?default("")?html}"<#if event?has_content> ${event}="${action}"</#if>/><#rt/>
      ${item.description}
    </span>
  </#list>
</#macro>

<#macro renderSubmitField buttonType className alert formName title name event action imgSrc confirmation containerId ajaxUrl>
  <#if buttonType=="text-link">
    <a <@renderClass className alert /> href="javascript:document.${formName}.submit()" <#if confirmation?has_content>onclick="return confirm('${confirmation?js_string}');"</#if>><#if title?has_content>${title}</#if> </a>
  <#elseif buttonType=="image">
    <input type="image" src="${imgSrc}" <@renderClass className alert /><#if name?has_content> name="${name}"</#if>
    <#if title?has_content> alt="${title}"</#if><#if event?has_content> ${event}="${action}"</#if>
    <#if confirmation?has_content>onclick="return confirm('${confirmation?js_string}');"</#if>/>
  <#else>
    <button type="<#if containerId?has_content>button<#else>submit</#if>" <@renderClass className alert />
    <#if name?exists> name="${name}"</#if><#if title?has_content> </#if><#if event?has_content> ${event}="${action}"</#if>
    <#if containerId?has_content> onclick="<#if confirmation?has_content>if (confirm('${confirmation?js_string}')) </#if>ajaxSubmitFormUpdateAreas('${containerId}', '${ajaxUrl}')"
      <#else><#if confirmation?has_content> onclick="return confirm('${confirmation?js_string}');"</#if>
    </#if>><i class="${imgSrc}"></i>${title}</button>
  </#if>
</#macro>

<#macro renderSubmitFieldBU buttonType className alert formName title name event action imgSrc confirmation containerId ajaxUrl>
  <#if buttonType=="text-link">
    <a <@renderClass className alert /> href="javascript:document.${formName}.submit()" <#if confirmation?has_content>onclick="return confirm('${confirmation?js_string}');"</#if>><#if title?has_content>${title}</#if> </a>
  <#elseif buttonType=="image">
    <input type="image" src="${imgSrc}" <@renderClass className alert /><#if name?has_content> name="${name}"</#if>
    <#if title?has_content> alt="${title}"</#if><#if event?has_content> ${event}="${action}"</#if>
    <#if confirmation?has_content>onclick="return confirm('${confirmation?js_string}');"</#if>/>
  <#else>
    <input type="<#if containerId?has_content>button<#else>submit</#if>" <@renderClass className alert />
    <#if name?exists> name="${name}"</#if><#if title?has_content> value="${title}"</#if><#if event?has_content> ${event}="${action}"</#if>
    <#if containerId?has_content> onclick="<#if confirmation?has_content>if (confirm('${confirmation?js_string}')) </#if>ajaxSubmitFormUpdateAreas('${containerId}', '${ajaxUrl}')"
      <#else><#if confirmation?has_content> onclick="return confirm('${confirmation?js_string}');"</#if>
    </#if>/>
  </#if>
</#macro>

<#macro renderResetField className alert name title>
  <input type="reset" <@renderClass className alert /> name="${name}"<#if title?has_content> value="${title}"</#if>/>
</#macro>

<#macro renderHiddenField name value id event action>
  <input type="hidden" name="${name}"<#if value?has_content> value="${value}"</#if><#if id?has_content> id="${id}"</#if><#if event?has_content && action?has_content> ${event}="${action}"</#if>/>
</#macro>

<#macro renderIgnoredField></#macro>

<#macro renderFieldTitle style title id fieldHelpText="" for="">
  <label <#if for?has_content>for="${for}"</#if> <#if fieldHelpText?has_content> title="${fieldHelpText}"</#if><#if style?has_content> class="${style}"</#if><#if id?has_content> id="${id}"</#if>><#t/>
    ${title}<#t/>
  </label><#t/>
</#macro>

<#macro renderSingleFormFieldTitle></#macro>

<#macro renderFormOpen linkUrl formType targetWindow containerId containerStyle autocomplete name viewIndexField viewSizeField viewIndex viewSize useRowSubmit>
  <form method="post" action="${linkUrl}"<#if formType=="upload"> enctype="multipart/form-data"</#if><#if targetWindow?has_content> target="${targetWindow}"</#if><#if containerId?has_content> id="${containerId}"</#if> class=<#if containerStyle?has_content>"${containerStyle} form-horizontal"<#else>"basic-form form-horizontal"</#if> onsubmit="javascript:submitFormDisableSubmits(this)"<#if autocomplete?has_content> autocomplete="${autocomplete}"</#if> name="${name}"><#lt/>
    <#if useRowSubmit?has_content && useRowSubmit>
      <input type="hidden" name="_useRowSubmit" value="Y"/>
      <#if linkUrl?index_of("VIEW_INDEX") &lt;= 0 && linkUrl?index_of(viewIndexField) &lt;= 0>
        <input type="hidden" name="${viewIndexField}" value="${viewIndex}"/>
      </#if>
      <#if linkUrl?index_of("VIEW_SIZE") &lt;= 0 && linkUrl?index_of(viewSizeField) &lt;= 0>
        <input type="hidden" name="${viewSizeField}" value="${viewSize}"/>
      </#if>
    </#if>
    <div class="row-fluid">
</#macro>
<#macro renderFormClose focusFieldName formName containerId hasRequiredField>
  </div></form><#lt/>
  <#if focusFieldName?has_content>
    <script language="JavaScript" type="text/javascript">
      var form = document.${formName};
      form.${focusFieldName}.focus();
      <#-- enable the validation plugin for all generated forms
      only enable the validation if min one field is marked as 'required' -->
      if (jQuery(form).find(".required").size() > 0) {
          jQuery(form).validate();
      }
    </script><#lt/>
  </#if>
  <#if containerId?has_content && hasRequiredField?has_content>
    <script type="text/javascript">
      jQuery("#${containerId}").validate({
        errorElement: 'div',
        errorClass: "invalid",
        errorPlacement: function(error, element) {
            element.addClass("border-error");
            if (element.parent() != null ){   
                element.parent().find("button").addClass("button-border");              
                error.appendTo(element.parent());
            }
          },
        unhighlight: function(element, errorClass) {
            $(element).removeClass("border-error");
            $(element).parent().find("button").removeClass("button-border");
        },
        submitHandler:
          function(form) {
            form.submit();
          }
      });
    </script>
  </#if>
</#macro>
<#macro renderMultiFormClose>
  </form><#lt/>
</#macro>

<#macro renderFormatListWrapperOpen formName style columnStyles>
  <table cellspacing="0" class="<#if style?has_content>${style}<#else>basic-table form-widget-table dark-grid</#if>"><#lt/>
</#macro>

<#macro renderFormatListWrapperClose formName>
  </table><#lt/>
</#macro>

<#macro renderFormatHeaderRowOpen style>
  <thead><tr role="row" class="<#if style?has_content>${style}<#else>header-row</#if>">
</#macro>
<#macro renderFormatHeaderRowClose>
  </tr></thead>
</#macro>
<#macro renderFormatHeaderRowCellOpen style positionSpan>
  <th class="hidden-phone"><i class="${style}"></i>${style}
</#macro>
<#macro renderFormatHeaderRowCellClose>
  </th>
</#macro>

<#macro renderFormatHeaderRowFormCellOpen style>
  <td <#if style?has_content>class="${style}"</#if>>
</#macro>
<#macro renderFormatHeaderRowFormCellClose>
  </td>
</#macro>
<#macro renderFormatHeaderRowFormCellTitleSeparator style isLast>
  <#if style?has_content><span class="${style}"></#if> - <#if style?has_content></span></#if>
</#macro>

<#macro renderFormatItemRowOpen formName itemIndex altRowStyles evenRowStyle oddRowStyle>
  <tr <#if itemIndex?has_content><#if itemIndex%2==0><#if evenRowStyle?has_content>class="${evenRowStyle}<#if altRowStyles?has_content> ${altRowStyles}</#if>"<#elseif altRowStyles?has_content>class="${altRowStyles}"</#if><#else><#if oddRowStyle?has_content>class="${oddRowStyle}<#if altRowStyles?has_content> ${altRowStyles}</#if>"<#elseif altRowStyles?has_content>class="${altRowStyles}"</#if></#if></#if> >
</#macro>
<#macro renderFormatItemRowClose formName>
  </tr>
</#macro>
<#macro renderFormatItemRowCellOpen fieldName style positionSpan>
  <td <#if positionSpan?has_content && positionSpan gt 1>colspan="${positionSpan}"</#if><#if style?has_content>class="${style}"</#if>>
</#macro>
<#macro renderFormatItemRowCellClose fieldName>
  </td>
</#macro>
<#macro renderFormatItemRowFormCellOpen style>
  <td<#if style?has_content> class="${style}"</#if>>
</#macro>
<#macro renderFormatItemRowFormCellClose>
  </td>
</#macro>

<#macro renderFormatSingleWrapperOpen formName style>
  <#--
  <table cellspacing="0" <#if style?has_content>class="${style}"</#if>>
  -->
</#macro>
<#macro renderFormatSingleWrapperClose formName>
  <#--
  </table>
  -->
</#macro>

<#macro renderFormatFieldRowOpen>
  <#--  
  <tr>
  -->
  <div class="control-group no-left-margin">
</#macro>
<#macro renderFormatFieldRowOpenRow style>
  <#--  
  <tr>
  -->
  <div class="control-group no-left-margin ${style}">
</#macro>
<#--
<#macro renderFormatFieldRowOpenRow widgetStyleRow>
  <div class="control-group no-left-margin ${widgetStyleRow}">
</#macro>
-->
<#macro renderFormatFieldRowClose>
  <#-- 
  </tr>
  -->
  </div>
</#macro>
<#macro renderFormatFieldRowTitleCellOpen style>
  <#--
  <td class="<#if style?has_content>${style}<#else>label</#if>">
  -->
  <label class="${style}">
</#macro>
<#macro renderFormatFieldRowTitleCellClose>
  </label>
</#macro>
<#macro renderFormatFieldRowSpacerCell></#macro>
<#macro renderFormatFieldRowWidgetCellOpen positionSpan style>
  <#--
  <td<#if positionSpan?has_content && positionSpan gt 0> colspan="${1+positionSpan*3}"</#if><#if style?has_content> class="${style}"</#if>>
  -->
  <div class="controls">
</#macro>
<#macro renderFormatFieldRowWidgetCellClose>
  <#--
  </td>
  -->
  </div>
</#macro>

<#--
    Initial work to convert table based layout for "single" form to divs.
<#macro renderFormatSingleWrapperOpen style> <div <#if style?has_content>class="${style}"</#if> ></#macro>
<#macro renderFormatSingleWrapperClose> </div></#macro>

<#macro renderFormatFieldRowOpen>  <div></#macro>
<#macro renderFormatFieldRowClose>  </div></#macro>
<#macro renderFormatFieldRowTitleCellOpen style>   <div class="<#if style?has_content>${style}<#else>label</#if>"></#macro>
<#macro renderFormatFieldRowTitleCellClose></div></#macro>
<#macro renderFormatFieldRowSpacerCell></#macro>
<#macro renderFormatFieldRowWidgetCellOpen positionSpan style>   <div<#if positionSpan?has_content && positionSpan gt 0> colspan="${1+positionSpan*3}"</#if><#if style?has_content> class="${style}"</#if>></#macro>
<#macro renderFormatFieldRowWidgetCellClose></div></#macro>

-->


<#macro renderFormatEmptySpace>&nbsp;</#macro>

<#macro renderTextFindField name value defaultOption opEquals opBeginsWith opContains opIsEmpty opNotEqual className alert size maxlength autocomplete titleStyle hideIgnoreCase ignCase ignoreCase>
  <#if opEquals?has_content>
    <select <#if name?has_content>name="${name}_op"</#if>    class="selectBox"><#rt/>
      <option value="equals"<#if defaultOption=="equals"> selected="selected"</#if>>${opEquals}</option><#rt/>
      <option value="like"<#if defaultOption=="like"> selected="selected"</#if>>${opBeginsWith}</option><#rt/>
      <option value="contains"<#if defaultOption=="contains"> selected="selected"</#if>>${opContains}</option><#rt/>
      <option value="empty"<#rt/><#if defaultOption=="empty"> selected="selected"</#if>>${opIsEmpty}</option><#rt/>
      <option value="notEqual"<#if defaultOption=="notEqual"> selected="selected"</#if>>${opNotEqual}</option><#rt/>
    </select>
  <#else>
    <input type="hidden" name=<#if name?has_content> "${name}_op"</#if>    value="${defaultOption}"/><#rt/>
  </#if>
    <input type="text" <@renderClass className alert /> name="${name}"<#if value?has_content> value="${value}"</#if><#if size?has_content> size="${size}"</#if><#if maxlength?has_content> maxlength="${maxlength}"</#if><#if autocomplete?has_content> autocomplete="off"</#if>/><#rt/>
    <#if titleStyle?has_content><span class="${titleStyle}" ><#rt/></#if>
    <label style="display:inline;">
        <#if hideIgnoreCase>
          <input type="hidden" name="${name}_ic" value=<#if ignCase>"Y"<#else> ""</#if>/><#rt/>
        <#else>
          <input style="height:20px;" type="checkbox" name="${name}_ic" value="Y" <#if ignCase> checked="checked"</#if> /> <span class="lbl">${ignoreCase}</span><#rt/>
        </#if>
    </label>
    <#if titleStyle?has_content>
  </#if>
  <input type="checkbox" />
</#macro>

<#macro renderDateFindField className alert name localizedInputTitle value size maxlength dateType formName defaultDateTimeString imgSrc localizedIconTitle titleStyle defaultOptionFrom defaultOptionThru opEquals opSameDay opGreaterThanFromDayStart opGreaterThan opGreaterThan opLessThan opUpToDay opUpThruDay opIsEmpty>
  <span class="view-calendar">
    <input id="${name?html}_fld0_value" type="text" <@renderClass className alert /><#if name?has_content> name="${name?html}_fld0_value"</#if><#if localizedInputTitle?has_content> title="${localizedInputTitle}"</#if><#if value?has_content> value="${value}"</#if><#if size?has_content> size="${size}"</#if><#if maxlength?has_content> maxlength="${maxlength}"</#if>/><#rt/>
    <#if dateType != "time">
      <script type="text/javascript">
        <#if dateType == "date">
          jQuery("#${name?html}_fld0_value").datepicker({
        <#else>
          jQuery("#${name?html}_fld0_value").datetimepicker({
            showSecond: true,
            <#-- showMillisec: true, -->
            timeFormat: 'hh:mm:ss',
            stepHour: 1,
            stepMinute: 5,
            stepSecond: 10,
        </#if>
            showOn: 'button',
            buttonImage: '',
            buttonText: '',
            buttonImageOnly: false,
            dateFormat: 'yy-mm-dd'
          });
      </script>
      <#rt/>
    </#if>
    <#if titleStyle?has_content>
      <span class="${titleStyle}"><#rt/>
    </#if>
    <select<#if name?has_content> name="${name}_fld0_op"</#if> class="selectBox"><#rt/>
      <option value="equals"<#if defaultOptionFrom=="equals"> selected="selected"</#if>>${opEquals}</option><#rt/>
      <option value="sameDay"<#if defaultOptionFrom=="sameDay"> selected="selected"</#if>>${opSameDay}</option><#rt/>
      <option value="greaterThanFromDayStart"<#if defaultOptionFrom=="greaterThanFromDayStart"> selected="selected"</#if>>${opGreaterThanFromDayStart}</option><#rt/>
      <option value="greaterThan"<#if defaultOptionFrom=="greaterThan"> selected="selected"</#if>>${opGreaterThan}</option><#rt/>
    </select><#rt/>
    <#if titleStyle?has_content>
      </span><#rt/>
    </#if>
    <#rt/>
    <input id="${name?html}_fld1_value" type="text" <@renderClass className alert /><#if name?has_content> name="${name}_fld1_value"</#if><#if localizedInputTitle?exists> title="${localizedInputTitle?html}"</#if><#if value2?has_content> value="${value2}"</#if><#if size?has_content> size="${size}"</#if><#if maxlength?has_content> maxlength="${maxlength}"</#if>/><#rt/>
    <#if dateType != "time">
      <script type="text/javascript">
        <#if dateType == "date">
          jQuery("#${name?html}_fld1_value").datepicker({
        <#else>
          jQuery("#${name?html}_fld1_value").datetimepicker({
            showSecond: true,
            <#-- showMillisec: true, -->
            timeFormat: 'hh:mm:ss',
            stepHour: 1,
            stepMinute: 5,
            stepSecond: 10,
        </#if>
            showOn: 'button',
            buttonImage: '',
            buttonText: '',
            buttonImageOnly: false,
            dateFormat: 'yy-mm-dd'
          });
      </script>
      <#rt/>
    </#if>
    <#if titleStyle?has_content>
      <span class="${titleStyle}"><#rt/>
    </#if>
    <select name=<#if name?has_content>"${name}_fld1_op"</#if> class="selectBox"><#rt/>
      <option value="opLessThan"<#if defaultOptionThru=="opLessThan"> selected="selected"</#if>>${opLessThan}</option><#rt/>
      <option value="upToDay"<#if defaultOptionThru=="upToDay"> selected="selected"</#if>>${opUpToDay}</option><#rt/>
      <option value="upThruDay"<#if defaultOptionThru=="upThruDay"> selected="selected"</#if>>${opUpThruDay}</option><#rt/>
      <option value="empty"<#if defaultOptionFrom=="empty"> selected="selected"</#if>>${opIsEmpty}</option><#rt/>
    </select><#rt/>
    <#if titleStyle?has_content>
      </span>
    </#if>
  </span>
</#macro>

<#macro renderRangeFindField className alert name value size maxlength autocomplete titleStyle defaultOptionFrom opEquals opGreaterThan opGreaterThanEquals opLessThan opLessThanEquals value2 defaultOptionThru>
  <input type="text" <@renderClass className alert /> <#if name?has_content>name="${name}_fld0_value"</#if><#if value?has_content> value="${value}"</#if><#if size?has_content> size="${size}"</#if><#if maxlength?has_content> maxlength="${maxlength}"</#if><#if autocomplete?has_content> autocomplete="off"</#if>/><#rt/>
  <#if titleStyle?has_content>
    <span class="${titleStyle}" ><#rt/>
  </#if>
  <select <#if name?has_content>name="${name}_fld0_op"</#if> class="selectBox"><#rt/>
    <option value="equals"<#if defaultOptionFrom=="equals"> selected="selected"</#if>>${opEquals}</option><#rt/>
    <option value="greaterThan"<#if defaultOptionFrom=="greaterThan"> selected="selected"</#if>>${opGreaterThan}</option><#rt/>
    <option value="greaterThanEqualTo"<#if defaultOptionFrom=="greaterThanEqualTo"> selected="selected"</#if>>${opGreaterThanEquals}</option><#rt/>
  </select><#rt/>
  <#if titleStyle?has_content>
    </span><#rt/>
  </#if>
  <br /><#rt/>
  <input type="text" <@renderClass className alert /><#if name?has_content> name="${name}_fld1_value"</#if><#if value2?has_content> value="${value2}"</#if><#if size?has_content> size="${size}"</#if><#if maxlength?has_content> maxlength="${maxlength}"</#if><#if autocomplete?has_content> autocomplete="off"</#if>/><#rt/>
  <#if titleStyle?has_content>
    <span class="${titleStyle}" ><#rt/>
  </#if>
  <select name=<#if name?has_content>"${name}_fld1_op"</#if> class="selectBox"><#rt/>
    <option value="lessThan"<#if defaultOptionThru=="lessThan"> selected="selected"</#if>>${opLessThan?html}</option><#rt/>
    <option value="lessThanEqualTo"<#if defaultOptionThru=="lessThanEqualTo"> selected="selected"</#if>>${opLessThanEquals?html}</option><#rt/>
  </select><#rt/>
  <#if titleStyle?has_content>
    </span>
  </#if>
</#macro>

<#--
@renderLookupField

Description: Renders a text input field as a lookup field.

Parameter: name, String, required - The name of the lookup field.
Parameter: formName, String, required - The name of the form that contains the lookup field.
Parameter: fieldFormName, String, required - Contains the lookup window form name.
Parameter: className, String, optional - The CSS class name for the lookup field.
Parameter: alert, String, optional - If "true" then the "alert" CSS class will be added to the lookup field.
Parameter: value, Object, optional - The value of the lookup field.
Parameter: size, String, optional - The size of the lookup field.
Parameter: maxlength, String or Integer, optional - The max length of the lookup field.
Parameter: id, String, optional - The ID of the lookup field.
Parameter: event, String, optional - The lookup field event that invokes "action". If the event parameter is not empty, then the action parameter must be specified as well.
Parameter: action, String, optional - The action that is invoked on "event". If action parameter is not empty, then the event parameter must be specified as well.
Parameter: readonly, boolean, optional - If true, the lookup field is made read-only.
Parameter: autocomplete, String, optional - If not empty, autocomplete is turned off for the lookup field.
Parameter: descriptionFieldName, String, optional - If not empty and the presentation parameter contains "window", specifies an alternate input field for updating.
Parameter: targetParameterIter, List, optional - Contains a list of form field names whose values will be passed to the lookup window.
Parameter: imgSrc, Not used.
Parameter: ajaxUrl, String, optional - Contains the Ajax URL, used only when the ajaxEnabled parameter contains true.
Parameter: ajaxEnabled, boolean, optional - If true, invokes the Ajax auto-completer.
Parameter: presentation, String, optional - Contains the lookup window type, either "layer" or "window".
Parameter: width, String or Integer, optional - The width of the lookup field.
Parameter: height, String or Integer, optional - The height of the lookup field.
Parameter: position, String, optional - The position style of the lookup field.
Parameter: fadeBackground, ?
Parameter: clearText, String, optional - If the readonly parameter is true, clearText contains the text to be displayed in the field, default is CommonClear label.
Parameter: showDescription, String, optional - If the showDescription parameter is true, a special span with css class "tooltip" will be created at right of the lookup button and a description will fill in (see setLookDescription in selectall.js). For now not when the lookup is read only.
Parameter: initiallyCollapsed, Not used.
Parameter: lastViewName, String, optional - If the ajaxEnabled parameter is true, the contents of lastViewName will be appended to the Ajax URL.
Parameter: zIndex, String, optional - set z-index for dialog
-->
<#macro renderLookupField name formName fieldFormName className="" alert="false" value="" size="" maxlength="" id="" event=""  action="" readonly=false autocomplete="" descriptionFieldName="" targetParameterIter="" imgSrc="" ajaxUrl="" ajaxEnabled=false javaScriptEnabled=false presentation="layer" width="" height="" position="" fadeBackground="true" clearText="" showDescription="" initiallyCollapsed="" title="" zIndex="" lastViewName="main">
  <#if Static["org.ofbiz.widget.ModelWidget"].widgetBoundaryCommentsEnabled(context)>
  <!-- @renderLookupField -->
  </#if>
  <#if (!ajaxUrl?has_content) && ajaxEnabled?has_content && ajaxEnabled>
    <#local ajaxUrl = requestAttributes._REQUEST_HANDLER_.makeLink(request, response, fieldFormName)/>
    <#local ajaxUrl = id + "," + ajaxUrl + ",ajaxLookup=Y" />
  </#if>
  <#if (!showDescription?has_content)>
    <#local showDescriptionProp = Static["org.ofbiz.base.util.UtilProperties"].getPropertyValue("widget.properties", "widget.lookup.showDescription", "N")>
    <#if "Y" == showDescriptionProp>
      <#local showDescription = "true" />
    <#else>
      <#local showDescription = "false" />
    </#if>
  </#if>
  <#if ajaxEnabled?has_content && ajaxEnabled>
    <script type="text/javascript">
      jQuery(document).ready(function(){
        if (!jQuery('form[name="${formName}"]').length) {
          alert("Developer: for lookups to work you must provide a form name!")
        }
      });
    </script>
    <style type="text/css">
        .ui-dialog{
            padding: 0px !important;
            <!--position : absolute !important;-->
        }
        .ui-widget-header{
            border: 1px solid #FFF !important;
            opacity:1 !important;
        }
        .ui-corner-all{
            border-bottom-right-radius: 0px !important;
            border-bottom-left-radius: 0px !important;
            border-top-right-radius: 0px !important;
            border-top-left-radius: 0px !important;
        }
        .ui-widget-overlay{
            opacity: 0.9 !important;
            background-color: black !important;
        }
      </style>
  </#if>
  <span class="field-lookup">
    <#if size?has_content && size=="0">
      <input type="hidden" <#if name?has_content> name="${name}"/></#if>
    <#else>
      <input type="text" <@renderClass className alert /><#if name?has_content> name="${name}"</#if><#if value?has_content> value="${value}"</#if>
        <#if size?has_content> size="${size}"</#if><#if maxlength?has_content> maxlength="${maxlength}"</#if><#if id?has_content> id="${id}"</#if><#rt/>
        <#if readonly?has_content && readonly> readonly="readonly"</#if><#rt/><#if event?has_content && action?has_content> ${event}="${action}"</#if><#rt/>
        <#if autocomplete?has_content> autocomplete="off"</#if>/><#rt/></#if>
    <#if presentation?has_content && descriptionFieldName?has_content && presentation == "window">
      <a href="javascript:call_fieldlookup3(document.${formName?html}.${name?html},document.${formName?html}.${descriptionFieldName},'${fieldFormName}', '${presentation}'<#rt/>
      <#if targetParameterIter?has_content>
        <#list targetParameterIter as item>
          ,document.${formName}.${item}.value<#rt>
        </#list>
      </#if>
      );"></a><#rt>
    <#elseif presentation?has_content && presentation == "window">
      <a href="javascript:call_fieldlookup2(document.${formName?html}.${name?html},'${fieldFormName}', '${presentation}'<#rt/>
      <#if targetParameterIter?has_content>
        <#list targetParameterIter as item>
          ,document.${formName}.${item}.value<#rt>
        </#list>
      </#if>
      );"></a><#rt>
    <#else>
      <#if ajaxEnabled?has_content && ajaxEnabled>
        <#assign defaultMinLength = Static["org.ofbiz.base.util.UtilProperties"].getPropertyValue("widget.properties", "widget.autocompleter.defaultMinLength")>
        <#assign defaultDelay = Static["org.ofbiz.base.util.UtilProperties"].getPropertyValue("widget.properties", "widget.autocompleter.defaultDelay")>
        <#local ajaxUrl = ajaxUrl + "&amp;_LAST_VIEW_NAME_=" + lastViewName />
        <#if !ajaxUrl?contains("searchValueFieldName=")>
          <#if descriptionFieldName?has_content && showDescription == "true">
            <#local ajaxUrl = ajaxUrl + "&amp;searchValueFieldName=" + descriptionFieldName />
          <#else>
            <#local ajaxUrl = ajaxUrl + "&amp;searchValueFieldName=" + name />
          </#if>
        </#if>
      </#if>
      <script type="text/javascript">
        jQuery(document).ready(function(){
          var options = {
            requestUrl : "${fieldFormName}",
            inputFieldId : "${id}",
            dialogTarget : document.${formName?html}.${name?html},
            dialogOptionalTarget : <#if descriptionFieldName?has_content>document.${formName?html}.${descriptionFieldName}<#else>null</#if>,
            formName : "${formName?html}",
            width : <#if width?has_content>"${width}"<#else>"1000"</#if>,
            height : <#if height?has_content>"${height}"<#else>"600"</#if>,
            position : "center",
            modal : "${fadeBackground}",
            ajaxUrl : <#if ajaxEnabled?has_content && ajaxEnabled>"${ajaxUrl}"<#else>""</#if>,
            showDescription : <#if ajaxEnabled?has_content && ajaxEnabled>"${showDescription}"<#else>false</#if>,
            presentation : "${presentation!}",
            defaultMinLength : "${defaultMinLength!2}",
            defaultDelay : "${defaultDelay!300}",
            show : { effect: 'slide-up',duration :400 },    
            hide: { effect: 'slide-up',duration : 100 },
            title : <#if title?has_content>"${title}"<#else>""</#if>,
            zIndex: "${zIndex}",
            args :
              <#rt/>
                <#if targetParameterIter?has_content>
                  <#assign isFirst = true>
                  <#lt/>[<#rt/>
                  <#list targetParameterIter as item>
                    <#if isFirst>
                      <#lt/>document.${formName}.${item}<#rt/>
                      <#assign isFirst = false>
                    <#else>
                      <#lt/> ,document.${formName}.${item}<#rt/>
                    </#if>
                  </#list>
                  <#lt/>]<#rt/>
                <#else>[]
                </#if>
                <#lt/>
          };
          new Lookup(options).init();
        });
      </script>
    </#if>
    <#if readonly?has_content && readonly>
      <a id="${id}_clear" 
        style="background:none;margin-left:5px;margin-right:15px;" 
        class="clearField" 
        href="javascript:void(0);" 
        onclick="javascript:document.${formName}.${name}.value='';
          jQuery('#' + jQuery('#${id}_clear').next().attr('id').replace('_button','') + '_${id}_lookupDescription').html('');
          <#if descriptionFieldName?has_content>document.${formName}.${descriptionFieldName}.value='';</#if>">
          <#if clearText?has_content>${clearText}<#else>${uiLabelMap.CommonClear}</#if>
      </a>
    </#if>
  </span>
  <#if ajaxEnabled?has_content && ajaxEnabled && (presentation?has_content && presentation == "window")>
    <#if ajaxUrl?index_of("_LAST_VIEW_NAME_") < 0>
      <#local ajaxUrl = ajaxUrl + "&amp;_LAST_VIEW_NAME_=" + lastViewName />
    </#if>
    <script language="JavaScript" type="text/javascript">ajaxAutoCompleter('${ajaxUrl}', ${showDescription}, ${defaultMinLength!2}, ${defaultDelay!300});</script><#t/>
  </#if>
</#macro>

<#macro renderNextPrev paginateStyle paginateFirstStyle viewIndex highIndex listSize viewSize ajaxEnabled javaScriptEnabled ajaxFirstUrl firstUrl paginateFirstLabel paginatePreviousStyle ajaxPreviousUrl previousUrl paginatePreviousLabel pageLabel ajaxSelectUrl selectUrl ajaxSelectSizeUrl selectSizeUrl commonDisplaying paginateNextStyle ajaxNextUrl nextUrl paginateNextLabel paginateLastStyle ajaxLastUrl lastUrl paginateLastLabel paginateViewSizeLabel renderBottom=true>
  <#if listSize gt 0>
  <#if listSize gt viewSize>
    <div class="${paginateStyle} row-fluid dataTables_paginate paging_bootstrap pagination">&nbsp;
      <div style="float:left">
      <ul>
        <li class="nav-displaying">${commonDisplaying}</li>
        <#if javaScriptEnabled><li class="nav-pagesize">, 
        <select name="pageSize" size="1" onchange="<#if ajaxEnabled>javascript:ajaxUpdateAreas('${ajaxSelectSizeUrl}')<#else>location.href='${selectSizeUrl}';</#if>">
        <#rt/>
          <#assign availPageSizes = [10, 20, 30, 50, 100, 200]>
          <#list availPageSizes as ps>
            <option<#if viewSize == ps> selected="selected" </#if> value="${ps}">${ps}</option>
          </#list>
          </select> ${paginateViewSizeLabel}</li>
        </#if>
      </ul>
      </div>
      <div style="float:right;padding-right:15px">
      <ul>
        <li class="${paginateFirstStyle}<#if viewIndex gt 0>"><a href="<#if ajaxEnabled>javascript:ajaxUpdateAreas('${ajaxFirstUrl}')<#else>${firstUrl}</#if>"><i class="icon-double-angle-left"></i></a><#else>-disabled"><span><i class="icon-double-angle-left"></i></span></#if></li>
        <li class="${paginatePreviousStyle}<#if viewIndex gt 0>"><a href="<#if ajaxEnabled>javascript:ajaxUpdateAreas('${ajaxPreviousUrl}')<#else>${previousUrl}</#if>"><i class="icon-angle-left"></i></a><#else>-disabled"><span><i class="icon-angle-left"></i></span></#if></li>
        <#if listSize gt 0 && javaScriptEnabled><li class="nav-page-select">
            <select name="page" size="1" style="display:none;"></select>
            <script type="text/javascript">
                function pagenvg(inputvalue){
                    var div = document.createElement('div');
                    div.innerHTML = "${selectUrl}";
                    var decoded = div.firstChild.nodeValue;
                    return decoded + inputvalue;
                }
            </script>
            <#rt/>
          <#assign x=(listSize/viewSize)?ceiling>
            <#if (x>5)>
                <#if (viewIndex < 3)>
                    <#list 1..4 as i>
                      <#if i == (viewIndex+1)>
                        <li class="active"><a 
                      <#else><li><a href="<#if ajaxEnabled>javascript:ajaxUpdateAreas('${ajaxSelectUrl}')<#else>${selectUrl}${i-1}</#if>" 
                      </#if> >${i}</a>
                        </li>
                    </#list>
                    <li><a>-></a></li>
                    <li><a href="<#if ajaxEnabled>javascript:ajaxUpdateAreas('${ajaxSelectUrl}')<#else>${selectUrl}${x-1}</#if>">${x}</a>
                <#else>
                    <#if (x-viewIndex <4)>
                        <li><a href="<#if ajaxEnabled>javascript:ajaxUpdateAreas('${ajaxSelectUrl}')<#else>${selectUrl}0</#if>">1</a>
                        <li><a><-</a></li>
                        <#list 3..0 as i>
                          <#if (x-i) == (viewIndex+1)>
                            <li class="active"><a 
                          <#else><li><a href="<#if ajaxEnabled>javascript:ajaxUpdateAreas('${ajaxSelectUrl}')<#else>${selectUrl}${x-i-1}</#if>" 
                          </#if> >${x-i}</a>
                            </li>
                        </#list>
                    <#else>
                        <li><a href="<#if ajaxEnabled>javascript:ajaxUpdateAreas('${ajaxSelectUrl}')<#else>${selectUrl}0</#if>">1</a>
                        <li><a><-</a></li>
                        <li><a href="<#if ajaxEnabled>javascript:ajaxUpdateAreas('${ajaxSelectUrl}')<#else>${selectUrl}${viewIndex-1}</#if>">${viewIndex}</a></li>
                        <li class="active">
                            <a href="<#if ajaxEnabled>javascript:ajaxUpdateAreas('${ajaxSelectUrl}')<#else>${selectUrl}${viewIndex}</#if>">${viewIndex+1}</a>
                        </li>
                        <li><a href="<#if ajaxEnabled>javascript:ajaxUpdateAreas('${ajaxSelectUrl}')<#else>${selectUrl}${viewIndex+1}</#if>">${viewIndex+2}</a></li>
                        <li><a>-></a></li>
                        <li><a href="<#if ajaxEnabled>javascript:ajaxUpdateAreas('${ajaxSelectUrl}')<#else>${selectUrl}${x-1}</#if>">${x}</a>
                    </#if>
                </#if>
            <#else>
                <#list 1..x as i>
                  <#if i == (viewIndex+1)>
                    <li class="active"><a 
                  <#else><li><a href="<#if ajaxEnabled>javascript:ajaxUpdateAreas('${ajaxSelectUrl}')<#else>${selectUrl}${i-1}</#if>" 
                  </#if> >${i}</a>
                    </li>
                </#list>
            </#if>
        </#if>
        <li class="${paginateNextStyle}<#if highIndex lt listSize>"><a href="<#if ajaxEnabled>javascript:ajaxUpdateAreas('${ajaxNextUrl}')<#else>${nextUrl}</#if>"><i class="icon-angle-right"></i></a><#else>-disabled"><span><i class="icon-angle-right"></i></span></#if></li>
        <li class="${paginateLastStyle}<#if highIndex lt listSize>"><a href="<#if ajaxEnabled>javascript:ajaxUpdateAreas('${ajaxLastUrl}')<#else>${lastUrl}</#if>"><i class="icon-double-angle-right"></i></i></a><#else>-disabled"><span><i class="icon-double-angle-right"></i></span></#if></li>
      </ul>
      </div>
    </div>
  <#else>
    <div  class="${paginateStyle} row-fluid dataTables_paginate paging_bootstrap pagination">
      <div style="float:left">
      <ul style="float:inherit;width:100%;" >
        <li class="nav-displaying">${commonDisplaying}</li>
        <#if javaScriptEnabled><li class="nav-pagesize">, <select name="pageSize" size="1"  onchange="<#if ajaxEnabled>javascript:ajaxUpdateAreas('${ajaxSelectSizeUrl}')<#else>location.href='${selectSizeUrl}';</#if>"><#rt/>
          <#assign availPageSizes = [10, 20, 30, 50, 100, 200]>
          <#list availPageSizes as ps>
            <option<#if viewSize == ps> selected="selected" </#if> value="${ps}">${ps}</option>
          </#list>
          </select> ${paginateViewSizeLabel}</li>
        </#if>
      </ul>
      </div>    
    </div>
  </#if>
  </#if>
</#macro>

<#macro renderFileField className alert name value size maxlength autocomplete>
  <input type="file" id="${name}" <@renderClass className alert /><#if name?has_content> name="${name}"</#if><#if value?has_content> value="${value}"</#if><#if size?has_content> size="${size}"</#if><#if maxlength?has_content> maxlength="${maxlength}"</#if><#if autocomplete?has_content> autocomplete="off"</#if>/><#rt/>
    <style type="text/css">
        .ace-file-input{
            width: 220px !important;
        }
    </style>
    <script type="text/javascript">
    function addadditionImages(){
        $('#${name}').ace_file_input({
                no_file:'No File ...',
                btn_choose:'Choose',
                btn_change:'Change',
                droppable:false,
                onchange:null,
                thumbnail:false //| true | large
                //whitelist:'gif|png|jpg|jpeg'
                //blacklist:'exe|php'
                //onchange:''
                //
            });
            }
  </script>
</#macro>
<#macro renderPasswordField className alert name value size maxlength id autocomplete>
  <input type="password" <@renderClass className alert /><#if name?has_content> name="${name}"</#if><#if value?has_content> value="${value}"</#if><#if size?has_content> size="${size}"</#if><#if maxlength?has_content> maxlength="${maxlength}"</#if><#if id?has_content> id="${id}"</#if><#if autocomplete?has_content> autocomplete="off"</#if>/>
</#macro>
<#macro renderImageField value description alternate style event action><img<#if value?has_content> src="${value}"</#if><#if description?has_content> title="${description}"</#if> alt="<#if alternate?has_content>${alternate}"</#if><#if style?has_content> class="${style}"</#if><#if event?has_content> ${event?html}="${action}" </#if>/></#macro>

<#macro renderBanner style leftStyle rightStyle leftText text rightText>
  <table width="100%">
    <tr><#rt/>
      <#if leftText?has_content><td align="left"><#if leftStyle?has_content><div class="${leftStyle}"></#if>${leftText}<#if leftStyle?has_content></div></#if></td><#rt/></#if>
      <#if text?has_content><td align="center"><#if style?has_content><div class="${style}"></#if>${text}<#if style?has_content></div></#if></td><#rt/></#if>
      <#if rightText?has_content><td align="right"><#if rightStyle?has_content><div class="${rightStyle}"></#if>${rightText}<#if rightStyle?has_content></div></#if></td><#rt/></#if>
    </tr>
  </table>
</#macro>

<#macro renderContainerField id className><div id="${id}" class="${className}"/></#macro>

<#macro renderFieldGroupOpen style id title collapsed collapsibleAreaId collapsible expandToolTip collapseToolTip>
  <#if style?has_content || id?has_content || title?has_content><#if style?contains("begin-group-group")><span class="span12 no-left-margin"></#if><div class="<#if style?has_content> ${style}</#if>">
    <#if !style?contains("no-widget-header")>
    <div class="widget-box <#if collapsed && collapsible>collapsed<#else></#if>">
    <div class="widget-header widget-header-small header-color-blue2">
      <#if collapsible>
        <#--
        <ul>
          <li class="<#if collapsed>collapsed">
                      <a onclick="javascript:toggleCollapsiblePanel(this, '${collapsibleAreaId}', '${expandToolTip}', '${collapseToolTip}');">
                    <#else>expanded">
                      <a onclick="javascript:toggleCollapsiblePanel(this, '${collapsibleAreaId}', '${expandToolTip}', '${collapseToolTip}');">
                    </#if>
            &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<#if title?has_content>${title}</#if></a>
          </li>
        </ul>
        -->
        <h6><#if title?has_content>${title}</#if></h6>
        <div class="widget-toolbar">
                <a href="#" data-action="collapse"><i class="icon-chevron-down" onclick="javascript:changeIconChev($(this));toggleScreenlet(this, '${collapsibleAreaId}', 'true', '${expandToolTip}', '${collapseToolTip}');"<#if expandToolTip?has_content> title="${expandToolTip}"</#if>></i></a>
        </div>
      <#else>
        <#if title?has_content>${title}</#if>
      </#if><#rt/>
    </div>
    <div class="widget-body" id="${collapsibleAreaId}">
    <div class="widget-body-inner" style="display: block;">
    <div class="widget-main row-fluid span12">
    </#if>
  </#if>
</#macro>

<#macro renderFieldGroupClose style id title>
<#if style?has_content || id?has_content || title?has_content>
    <#if !style?contains("no-widget-header")>
    </div></div></div></div></div>
    <#else>
    </div>
    </#if>
    <#if style?contains("end-group-group")></span></#if>
</#if>
</#macro>

<#macro renderHyperlinkTitle name title showSelectAll="N">
  <#if title?has_content>${title}<br /></#if>
  <#if showSelectAll="Y"><input type="checkbox" name="selectAll" value="Y" onclick="javascript:toggleAll(this, '${name}');"/></#if>
</#macro>

<#macro renderSortField style title linkUrl ajaxEnabled tooltip="">
  <a<#if style?has_content> class="${style}"</#if> href="<#if ajaxEnabled?has_content && ajaxEnabled>javascript:ajaxUpdateAreas('${linkUrl}')<#else>${linkUrl}</#if>"<#if tooltip?has_content> title="${tooltip}"</#if>>${title}</a>
</#macro>

<#macro formatBoundaryComment boundaryType widgetType widgetName><!-- ${boundaryType}  ${widgetType}  ${widgetName} --></#macro>

<#macro renderTooltip tooltip tooltipStyle>
  <#if tooltip?has_content><span class="<#if tooltipStyle?has_content>${tooltipStyle}<#else>tooltipob</#if>"><#--${tooltip}--></span><#rt/></#if>
</#macro>

<#macro renderClass className="" alert="">
  <#if className?has_content || (alert?has_content && alert=="true")> class="${className}<#if alert?has_content && alert=="true"> alert</#if>" </#if>
</#macro>

<#macro renderAsterisks requiredField requiredStyle>
  <#if requiredField=="true"><#if !requiredStyle?has_content></#if></#if>
</#macro>

<#macro makeHiddenFormLinkForm actionUrl name parameters targetWindow>
  <form method="post" action="${actionUrl}" <#if targetWindow?has_content>target="${targetWindow}"</#if> onsubmit="javascript:submitFormDisableSubmits(this)" name="${name}">
    <#list parameters as parameter>
      <input name="${parameter.name}" value="${parameter.value}" type="hidden"/>
    </#list>
  </form>
</#macro>
<#macro makeHiddenFormLinkAnchor linkStyle hiddenFormName event action imgSrc description confirmation>
  <a <#if linkStyle?has_content>class="${linkStyle}"</#if> href="javascript:document.${hiddenFormName}.submit()"
    <#if action?has_content && event?has_content> ${event}="${action}"</#if>
    <#if confirmation?has_content> onclick="return confirm('${confirmation?js_string}')"</#if>>
      <#if imgSrc?has_content><img src="${imgSrc}" alt=""/></#if>${description}</a>
</#macro>
<#macro makeHyperlinkString linkStyle hiddenFormName event action imgSrc title alternate linkUrl targetWindow description confirmation>
    <a <#if linkStyle?has_content>class="${linkStyle}"</#if> 
      href="${linkUrl}"<#if targetWindow?has_content> target="${targetWindow}"</#if>
      <#if action?has_content && event?has_content> ${event}="${action}"</#if>
      <#if confirmation?has_content> onclick="return confirm('${confirmation?js_string}')"</#if>
      <#if imgSrc?length == 0 && title?has_content> title="${title}"</#if>>
        <#if imgSrc?has_content><img src="${imgSrc}" alt="${alternate}" title="${title}"/></#if>${description}</a>
  </#macro>
<#macro test></#macro>
>>>>>>> origin/dev-jqx
