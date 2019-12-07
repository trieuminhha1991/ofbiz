<style type="text/css">
	#toolbarjqxSalesForecast {
		height: 33px !important;
		visibility: visible !important;
		width:100% !important;
	}
	#contentjqxSalesForecast {
		top: 33px !important;
	}
</style>

<div id="container" style="background-color: transparent; overflow: auto;"></div>
<div id="jqxNotification" style="margin-bottom:5px">
    <div id="notificationContent">
    </div>
</div>
<div id="jqxSalesForecast"></div>

<div id='contextMenu' style="display:none">
	<ul>
	    <li><i class="fa fa-search"></i>${StringUtil.wrapString(uiLabelMap.DAViewDetail)}</li>
	    <li><i class="fa fa-search"></i>${StringUtil.wrapString(uiLabelMap.DAViewDetailInNewTab)}</li>
	</ul>
</div>

<#assign addType = "popup"/>
<#assign alternativeAddPopup="alterpopupWindow"/>

<@jqGridMinimumLib />
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxcore.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxdata.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxbuttons.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxscrollbar.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxdatatable.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxtreegrid.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxlistbox.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxdropdownlist.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxcheckbox.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxcolorpicker.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxinput.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxdropdownbutton.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxnotification2.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxcombobox.js"></script>
<@jqOlbCoreLib hasTreeGrid=true/>
<script type="text/javascript">
	$.jqx.theme = 'olbius';
	var theme = $.jqx.theme;
	$(function(){
		pageCommonSalesForecast.init();
	});
	var pageCommonSalesForecast = (function(){
		var init = function(){
			initElement();
			initElementComplex();
			initEvent();
		};
		var initElement = function(){
			$("#contextMenu").jqxMenu({ width: 230, height: 60, autoOpenPopup: false, mode: 'popup', theme: theme});
			
			$("#container").width('100%');
            $("#jqxNotification").jqxNotification({ 
            	icon: { width: 25, height: 25, url: '/aceadmin/assets/images/info.jpg'}, 
            	width: '100%', 
            	appendContainer: "#container", 
            	opacity: 1, autoClose: true, template: "success" 
            });
		};
		var initElementComplex = function(){
			var configSalesForecast = {
				width: '100%',
				height: 600,
				filterable: false,
				pageable: true,
				showfilterrow: false,
				key: 'salesForecastId',
				parentKeyId: 'parentSalesForecastId',
				localization: getLocalization(),
				datafields: [
					{name: 'salesForecastId', type: 'string'},
					{name: 'parentSalesForecastId', type: 'string'},
					{name: 'organizationPartyId', type: 'string'},
					{name: 'internalPartyId', type: 'string'},
					{name: 'customTimePeriodId', type: 'string'},
					{name: 'periodName', type: 'string'},
					{name: 'fromDate', type: 'date'},
					{name: 'thruDate', type: 'date'},
				],
				columns: [
					{text: '${StringUtil.wrapString(uiLabelMap.DASalesForecastId)}', datafield: 'salesForecastId', width: '16%',
						cellsrenderer: function(row, colum, value) {
							return '<span><a href=\"viewSalesForecastDetailByPO?salesForecastId=' + value + '\">' + value + '</a></span>';
						}
					},
					{text: '${StringUtil.wrapString(uiLabelMap.DAOrganizationId)}', datafield: 'organizationPartyId', width: '16%'},
					{text: '${StringUtil.wrapString(uiLabelMap.DAInternalPartyId)}', datafield: 'internalPartyId', width: '16%'},
					{text: '${StringUtil.wrapString(uiLabelMap.DASalesCustomTimePeriodId)}', datafield: 'customTimePeriodId'},
					{text: '${StringUtil.wrapString(uiLabelMap.DASalesPeriodName)}', datafield: 'periodName', width: '12%'},
					{text: '${StringUtil.wrapString(uiLabelMap.DAFromDate)}', datafield: 'fromDate', width: '12%', cellsformat: 'dd/MM/yyyy',
						cellsrenderer: function(row, colum, value) {
							if (OlbCore.isNotEmpty(value)) {
								var dateLong = new Date(value);
								if (OlbCore.isNotEmpty(dateLong)) return dateLong.toString("dd/MM/yyyy");
							}
							return "";
						},
					},
					{text: '${StringUtil.wrapString(uiLabelMap.DAThruDate)}', datafield: 'thruDate', width: '12%', cellsformat: 'dd/MM/yyyy',
						cellsrenderer: function(row, colum, value) {
							if (OlbCore.isNotEmpty(value)) {
								var dateLong = new Date(value);
								if (OlbCore.isNotEmpty(dateLong)) return dateLong.toString("dd/MM/yyyy");
							}
							return "";
						},
					},
				],
				useUrl: true,
				root: 'results',
				url: 'jqxGeneralServicer?sname=JQListSalesForecast&pagesize=0',
				useUtilFunc: true,
				showToolbar: false,
				<#--
				rendertoolbar: function (toolbar) {
					toolbar.html("");
					<#assign id = "jqxSalesForecast"/>
                	var grid = $('#${id}');
                    var me = this;
                    <#if titleProperty?has_content && (!customTitleProperties?exists || customTitleProperties == "")>
                        <@renderJqxTitle titlePropertyTmp=titleProperty id=id/>
                    <#elseif customTitleProperties?exists && customTitleProperties != "">
                        <@renderJqxTitle titlePropertyTmp=customTitleProperties id=id/>
                    <#else>
                        var jqxheader = $("<div id='toolbarcontainer${id}' class='widget-header'><h4>" + "</h4><div id='toolbarButtonContainer${id}' class='pull-right'></div></div>");
                    </#if>
                    toolbar.append(jqxheader);
                    var container = $('#toolbarButtonContainer${id}');
                    var maincontainer = $("#toolbarcontainer${id}");
                    Grid.createAddRowButton(
                    	grid, container, "${uiLabelMap.accAddNewRow}", {
                    		type: "${addType}",
                    		container: $("#${alternativeAddPopup}"),
                    		<#if addType != "popup">
                    			<#if addinitvalue !="">
		                            data: {${primaryColumn}: '${addinitvalue}'}
		                        <#else>
		                        	data: ${primaryColumn}
		                        </#if>  
	                		</#if>
                    	}
                    );
                },
				-->
			};
			new OlbTreeGrid($("#jqxSalesForecast"), null, configSalesForecast, []);
		};
		var initEvent = function(){
			var heightTreeGrid = $("#jqxSalesForecast").css("height");
			var heightContainerTreeGrid = $("#contentjqxSalesForecast").css("height");
			if ("25px" == heightTreeGrid) {
				$("#jqxSalesForecast").css("height", '100px');
				if ("26px" == heightContainerTreeGrid) {
					$("#contentjqxSalesForecast").css("height", '101px');
				}
			}
			/*$('#jqxSalesForecast').on('bindingComplete', function(event){
				var heightTreeGrid = $("#jqxSalesForecast").css("height");
				var heightContainerTreeGrid = $("#contentjqxSalesForecast").css("height");
			});*/
			
	        $("#jqxSalesForecast").on('contextmenu', function () {
	            return false;
	        });
	        $("#jqxSalesForecast").on('rowClick', function (event) {
	            var args = event.args;
	            if (args.originalEvent.button == 2) {
	                var scrollTop = $(window).scrollTop();
	                var scrollLeft = $(window).scrollLeft();
	                $("#contextMenu").jqxMenu('open', parseInt(event.args.originalEvent.clientX) + 5 + scrollLeft, parseInt(event.args.originalEvent.clientY) + 5 + scrollTop);
	                return false;
	            }
	        });
	        $("#contextMenu").on('itemclick', function (event) {
	            var args = event.args;
		        var tmpKey = $.trim($(args).text());
		        if (tmpKey == "${StringUtil.wrapString(uiLabelMap.DAViewDetail)}") {
		        	var rowData;
		        	var selection = $("#jqxSalesForecast").jqxTreeGrid('getSelection');
		        	if (selection.length > 0) {
		        		rowData = selection[0];
		        	}
					if (rowData != undefined && rowData != null) {
						var salesForecastId = rowData.salesForecastId;
						var url = 'viewSalesForecastDetailByPO?salesForecastId=' + salesForecastId;
						var win = window.open(url, '_self');
						win.focus();
					}
		        } else if (tmpKey == "${StringUtil.wrapString(uiLabelMap.DAViewDetailInNewTab)}") {
		        	var rowData;
		        	var selection = $("#jqxSalesForecast").jqxTreeGrid('getSelection');
		        	if (selection.length > 0) {
		        		rowData = selection[0];
		        	}
					if (rowData != undefined && rowData != null) {
						var salesForecastId = rowData.salesForecastId;
						var url = 'viewSalesForecastDetailByPO?salesForecastId=' + salesForecastId;
						var win = window.open(url, '_blank');
						win.focus();
					}
		        }
	        });
		};
		return {
			init: init
		}
	}());
</script>