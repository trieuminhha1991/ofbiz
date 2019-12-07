<#assign initrowdetailsDetail = "function (index, parentElement, gridElement, datarecord) {
			var tabsDiv = $($(parentElement).children()[0]);
		    if (tabsDiv != null) {
		        var loadingStr = '<div id=\"info_loader_' + index + '\" style=\"overflow: hidden; position: absolute; display: none; left: 45%; top: 25%;\" class=\"jqx-rc-all jqx-rc-all-olbius\">';
		        loadingStr += '<div style=\"z-index: 99999; position: relative; width: auto; height: 33px; padding: 5px; font-family: verdana; font-size: 12px; color: #767676; border-color: #898989; border-width: 1px; border-style: solid; background: #f6f6f6; border-collapse: collapse;\" ';
		        loadingStr += ' class=\"jqx-rc-all jqx-rc-all-olbius jqx-fill-state-normal jqx-fill-state-normal-olbius\">';
		        loadingStr += '<div style=\"float: left;\"><div style=\"float: left; overflow: hidden; width: 32px; height: 32px;\" class=\"jqx-grid-load\"></div>';
		        loadingStr += '<span style=\"margin-top: 10px; float: left; display: block; margin-left: 5px;\">${uiLabelMap.DALoading}...</span></div></div></div>';
		        var notescontainer = $(loadingStr);
		        $(tabsDiv).append(notescontainer);
		        
		        var routerId = datarecord.routeId;
		        
		        var loadPage = function (url, tabClass, data, index) {
		            $.ajax({
					  	type: 'POST',
					  	url: url,
					  	data: data,
					  	beforeSend: function () {
							$(\"#info_loader_\" + index).show();
						}, 
						success: function(data){
							var tabActive = tabsDiv.find('.' + tabClass);
							var container2 = $('<div style=\"margin: 5px;\">' + data + '</div>');
					        container2.appendTo($(tabActive));
						},
						error: function(e){
						}, 
			            complete: function() {
					        $(\"#info_loader_\" + index).hide();
					    }
					});
		        }
		        loadPage('getDetailListOutletByRouteAjax', 'contentTab1', {'routerId' : routerId}, index);
		    }
	 }"/>
<#assign rowdetailstemplateAdvance = "<div class='contentTab1'></div>"/>


<#assign dataField="[{ name: 'executorId', type: 'string'},
					 { name: 'routeId', type: 'string'},
					 { name: 'routeCode', type: 'string'},
					 { name: 'routeName', type: 'string'},
					 { name: 'description', type: 'string'}]"/>
<#assign columnlist = "{text: '${uiLabelMap.DmsSequenceId}', datafield: '', sortable: false, filterable: false, editable: false, pinned: true, groupable: false, draggable: false, resizable: false, width: 40,
						    cellsrenderer: function (row, column, value) {
						        return '<div style=margin:4px;>' + (row + 1) + '</div>';
						    }
						},
					 	{ text: '${StringUtil.wrapString(uiLabelMap.EmployeeId)}', datafield: 'executorId', width: 150},
					 	{ text: '${StringUtil.wrapString(uiLabelMap.BsRouteId)}', datafield: 'routeCode', width: 200},
						{ text: '${StringUtil.wrapString(uiLabelMap.BSRouteName)}', datafield: 'routeName', width: 200},
						{ text: '${StringUtil.wrapString(uiLabelMap.BSDescription)}', datafield: 'description', minwidth: 200}"/>
<#assign showtoolbar = "false"/>
<#if !urlRoutes?exists>
	<#assign urlRoutes = "jqxGeneralServicer?sname=JQGetListRoutes&partyId=${parameters.partyId?if_exists}"/>
	<#assign showtoolbar = "true"/>
	<#assign customLoadFunction = "false"/>
</#if>

<@jqGrid url=urlRoutes dataField=dataField columnlist=columnlist filterable="true" clearfilteringbutton="true"
	showtoolbar=showtoolbar alternativeAddPopup="alterpopupWindow" filtersimplemode="true" addType="popup" viewSize="10"
	height="300" id="jqxgridRoutes" addrow="false" customLoadFunction=customLoadFunction 
	initrowdetailsDetail=initrowdetailsDetail initrowdetails="true"  rowdetailstemplateAdvance=rowdetailstemplateAdvance rowdetailsheight="255"/>

