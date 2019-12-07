<#include "script/modernTradeEditSalesmanCustomersScript.ftl"/>
<@jqGridMinimumLib/>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxcombobox.js"></script>
<@jqOlbCoreLib hasGrid=true hasDropDownButton=true hasDropDownList=true hasComboBox=true hasValidator=true hasComboBoxSearchRemote=true hasCore=true/>
<script type="text/javascript">
    $(function () {
        OlbMTCustomersObj.init();
    });
    if (typeof (InternalUtil) == "undefined") {
        var InternalUtil = (function() {
            var initComboboxGeo = function(geoId, geoTypeId, element) {
                var url = "";
                if(geoTypeId != "COUNTRY" && geoId){
                    url =  "autoCompleteGeoAjax?geoTypeId=" + geoTypeId + "&geoId=" + geoId;
                }else if(geoTypeId == "COUNTRY"){
                    url =  "autoCompleteGeoAjax?geoTypeId=" + geoTypeId;
                }
                var source = { datatype: "json",
                    datafields: [{ name: "geoId" },
                        { name: "geoName" }],
                    url: url,
                    cache: true };
                var dataAdapter = new $.jqx.dataAdapter(source);
                $("#" + element).jqxComboBox({ source: dataAdapter, theme: "olbius", displayMember: "geoName", valueMember: "geoId",
                    width: 218, height: 30, dropDownHeight: 150});
            };
            var initEventComboboxGeo = function (geoTypeId, element, elementAffected, elementParents, thisGeoTypeId) {
                $("#" + element).on("change", function (event) {
                    var args = event.args;
                    if (args) {
                        var index = args.index;
                        var item = args.item;
                        if (item) {
                            var label = item.label;
                            var value = item.value;
                            if (elementAffected) {
                                initComboboxGeo(value, geoTypeId, elementAffected);
                            }
                        }
                    }
                });
            };
            return {
                initComboboxGeo: initComboboxGeo,
                initEventComboboxGeo: initEventComboboxGeo
            };
        })();
    }
    var OlbMTCustomersObj = (function () {
        var customerGroupDDB;
        var countryGeoId="VNM", stateProvinceGeoId, districtGeoId, wardGeoId;
        var groupId="";
        var init = function () {
            initElementComplex();
            initEvents();
        };
        var initElementComplex = function () {
            var configCustomerGroup = {
                useUrl: true,
                root: 'results',
                widthButton: '86%',
                width: 500,
                showdefaultloadelement: false,
                autoshowloadelement: false,
                datafields: [{name: 'partyId', type: 'string'}, {
                    name: 'partyCode',
                    type: 'string'
                }, {name: 'description', type: 'string'}],
                columns: [
                    {text: '${uiLabelMap.BSCustomerGroupId}', datafield: 'partyCode', width: '30%'},
                    {text: '${uiLabelMap.BSCustomerGroup}', datafield: 'description'},
                ],
                url: 'JQGetListMTGroupCustomer',
                useUtilFunc: true,
                key: 'partyId',
                keyCode: 'partyCode',
                description: ['description'],
                autoCloseDropDown: true,
                filterable: true,
                sortable: true,
                dropDownHorizontalAlignment: 'right',
            };
            customerGroupDDB = new OlbDropDownButton($("#customerGroup"), $("#jqxGridCustomerGroup"), null, configCustomerGroup, []);
            InternalUtil.initComboboxGeo("", "WARD", "txtWard");
            InternalUtil.initComboboxGeo("", "DISTRICT", "txtCounty");
            InternalUtil.initComboboxGeo("", "PROVINCE", "txtProvince");
            InternalUtil.initComboboxGeo("", "COUNTRY", "txtCountry");
        };
        var initEvents = function () {
            InternalUtil.initEventComboboxGeo("PROVINCE", "txtCountry", "txtProvince", "", "COUNTRY");
            InternalUtil.initEventComboboxGeo("DISTRICT", "txtProvince", "txtCounty", "txtCountry", "PROVINCE");
            InternalUtil.initEventComboboxGeo("WARD", "txtCounty", "txtWard", "txtProvince", "DISTRICT");
            InternalUtil.initEventComboboxGeo("", "txtWard", null, "txtCounty", "WARD");
            $("#txtCountry").on("bindingComplete", function (event) {
                if (countryGeoId) {
                    $("#txtCountry").jqxComboBox("val", countryGeoId);
                    countryGeoId = "VNM";
                }
            });
            $("#txtProvince").on("bindingComplete", function (event) {
                if (stateProvinceGeoId) {
                    $("#txtProvince").jqxComboBox("val", stateProvinceGeoId);
                    if (!$("#txtProvince").jqxComboBox("getSelectedItem")) {
                        $("#txtProvince").jqxComboBox("clearSelection");
                        stateProvinceGeoId = null;
                    }
                }
            });
            $("#txtCounty").on("bindingComplete", function (event) {
                if (districtGeoId) {
                    $("#txtCounty").jqxComboBox("val", districtGeoId);
                    if (!$("#txtCounty").jqxComboBox("getSelectedItem")) {
                        $("#txtCounty").jqxComboBox("clearSelection");
                        districtGeoId = null;
                    }
                }
            });
            $("#txtWard").on("bindingComplete", function (event) {
                if (wardGeoId) {
                    $("#txtWard").jqxComboBox("val", wardGeoId);
                    if (!$("#txtWard").jqxComboBox("getSelectedItem")) {
                        $("#txtWard").jqxComboBox("clearSelection");
                        wardGeoId = null;
                    }
                }
            });
            customerGroupDDB.getGrid().rowSelectListener(function (rowData) {
                if (OlbCore.isNotEmpty(rowData.partyId)) {
                    groupId = rowData.partyId;
                } else {
                    groupId = ""
                }
            });
            $("#btnSearch").on("click", function(){
                countryGeoId = $("#txtCountry").jqxComboBox("val");
                stateProvinceGeoId = $("#txtProvince").jqxComboBox("val");
                districtGeoId = $("#txtCounty").jqxComboBox("val");
                wardGeoId = $("#txtWard").jqxComboBox("val");
                updateSourceGrid(true);
            });
            $("#btnRemoveFilter").on("click", function(){
                removeFilter();
            });
        };
        var removeFilter = function () {
            $("#txtProvince").jqxComboBox("clearSelection");
            $("#txtCounty").jqxComboBox("clearSelection");
            $("#txtWard").jqxComboBox("clearSelection");
            $("#jqxGridCustomerGroup").jqxGrid("clearselection");
            updateSourceGrid(null);
        };

        var updateSourceGrid = function (flagFilter) {
            OlbMTEditSalesmanCustomersObj.clearCustomersSelected();
            if (OlbCore.isNotEmpty(flagFilter) && flagFilter) {
                var adapter = $("#jqxgridCustomers").jqxGrid("source");
                if (adapter) {
                    adapter.url = "jqxGeneralServicer?sname=JQGetListMTCustomersByGroup&groupId="+ groupId;
                    if (OlbCore.isNotEmpty(countryGeoId)) {
                        adapter.url += "&countryGeoId=" + countryGeoId;
                    }
                    if (OlbCore.isNotEmpty(stateProvinceGeoId)) {
                        adapter.url += "&stateProvinceGeoId=" + stateProvinceGeoId;
                    }
                    if (OlbCore.isNotEmpty(districtGeoId)) {
                        adapter.url += "&districtGeoId=" + districtGeoId;
                    }
                    if (OlbCore.isNotEmpty(wardGeoId)) {
                        adapter.url += "&wardGeoId=" + wardGeoId;
                    }
                    adapter._source.url = adapter.url;
                    $("#jqxgridCustomers").jqxGrid("source", adapter);
                    $("#jqxgridCustomers").jqxGrid("clearselection");
                }
            } else {
                customerGroupDDB.clearAll();
                var adapter = $("#jqxgridCustomers").jqxGrid("source");
                if (adapter) {
                    adapter.url = "jqxGeneralServicer?sname=JQGetListMTCustomersByGroup";
                    adapter._source.url = adapter.url;
                    $("#jqxgridCustomers").jqxGrid("source", adapter);
                    $("#jqxgridCustomers").jqxGrid("clearselection");
                }
            }
        };

        return {
            init: init,
            removeFilter: removeFilter,
            updateSourceGrid: updateSourceGrid,
        }
    }());
</script>

<hr/>
<div class="row-fluid form-horizontal form-window-content-custom" style="margin-bottom: -10px">
    <div class="span5">
        <div class="row-fluid" hidden>
            <div class="span5"><label class="text-right">${uiLabelMap.DmsCountry}</label></div>
            <div class="span7"><div id="txtCountry"></div></div>
        </div>
        <div class="row-fluid">
            <div class="span5"><label class="text-right">${uiLabelMap.DmsProvince}</label></div>
            <div class="span7"><div id="txtProvince" tabindex="7"></div></div>
        </div>
        <div class="row-fluid">
            <div class='row-fluid'>
                <div class='span5'>
                    <span class="">${uiLabelMap.BSFilterByCustomerGroup}</span>
                </div>
                <div class="span7">
                    <div id="customerGroup">
                        <div id="jqxGridCustomerGroup"></div>
                    </div>
                </div>
            </div>
        </div>
    </div>
    <div class="span5">
        <div class="row-fluid">
            <div class="span5"><label class="text-right">${uiLabelMap.DmsCounty}&nbsp;&nbsp;&nbsp;</label></div>
            <div class="span7"><div id="txtCounty" tabindex="8"></div></div>
        </div>
        <div class="row-fluid">
            <div class="span5"><label class="text-right">${uiLabelMap.DmsWard}&nbsp;&nbsp;&nbsp;</label></div>
            <div class="span7"><div id="txtWard" tabindex="9"></div></div>
        </div>
    </div>
    <div class="span2">
        <div class="row-fluid">
            <button class="btn btn-small btn-primary btn-next" id="btnSearch">
                ${uiLabelMap.CommonSearch}
                <i class="icon-search"></i>
            </button>
        </div>
        <div class="row-fluid">
            <button class="btn btn-small btn-default btn-next" id="btnRemoveFilter">
                ${uiLabelMap.BSRemoveFilter}
                <i class="icon-filter"></i>
            </button>
        </div>
    </div>
</div>
<div style="margin-top: -10px">
    <#assign dataField="[
				{name: 'partyId', type: 'string'},
				{name: 'partyCode', type: 'string'},
				{name: 'statusId', type: 'string'},
				{name: 'fullName', type: 'string'},
				{name: 'distributorName', type: 'string'},
				{name: 'distributorId', type: 'string'},
				{name: 'distributorCode', type: 'string'},
				{name: 'fullName', type: 'string'},
				{name: 'salesmanName', type: 'string'},
				{name: 'salesmanId', type: 'string'},
				{name: 'supervisorId', type: 'string'},
				{name: 'supervisorName', type: 'string'},
				{name: 'postalAddressName', type: 'string'},
				{name: 'contactNumber', type: 'string'},
				{name: 'emailAddress', type: 'string'},
				{name: 'officeSiteName', type: 'string'},
				{name: 'latitude', type: 'number'},
				{name: 'longitude', type: 'number'},
				{name: 'geoPointId', type: 'string'},
				{name: 'preferredCurrencyUomId', type: 'string'}]"/>
    <#assign columnlist = "
				{text: '${uiLabelMap.DmsSequenceId}', datafield: '', sortable: false, filterable: false, editable: false, pinned: true,
					groupable: false, draggable: false, resizable: false, width: '5%',
				    cellsrenderer: function (row, column, value) {
				        return '<div>' + (row + 1) + '</div>';
				    }
				},
				{text: '${StringUtil.wrapString(uiLabelMap.BSCustomerId)}', datafield: 'partyCode', width: '12%'},
				{text: '${StringUtil.wrapString(uiLabelMap.BSCustomerName)}', datafield: 'fullName'},
				{text: '${StringUtil.wrapString(uiLabelMap.DmsAddress)}', datafield: 'postalAddressName', width: '15%', sortable: false},
                {text: '${StringUtil.wrapString(uiLabelMap.BSSupervisor)}', datafield: 'supervisorName', width: 150, sortable: false},
                {text: '${StringUtil.wrapString(uiLabelMap.BSSalesman)}', datafield: 'salesmanName', width: 150, sortable: false},
				{text: '${StringUtil.wrapString(uiLabelMap.DmsStatus)}', datafield: 'statusId', filtertype: 'checkedlist', width: '15%', editable: true,
					cellsrenderer: function(row, colum, value){
						value?value=mapStatusItem[value]:value;
				        return '<span>' + value + '</span>';
					},
					createfilterwidget: function (column, htmlElement, editor) {
    		        	editor.jqxDropDownList({ autoDropDownHeight: true, source: listStatusItem, displayMember: 'description', valueMember: 'statusId' ,
                            renderer: function (index, label, value) {
                            	if (index == 0) {
                            		return value;
								}
							    return mapStatusItem[value];
			                }
    		        	});
					}
				},
			"/>
    <@jqGrid filtersimplemode="true" id="jqxgridCustomers" filterable="true" dataField=dataField columnlist=columnlist editable="false" showtoolbar="false" clearfilteringbutton="false"
    url="jqxGeneralServicer?sname=JQGetListMTCustomersByGroup" initrowdetails = "false" selectionmode="checkbox" rowdetailsheight="200" viewSize="10"
    />
</div>