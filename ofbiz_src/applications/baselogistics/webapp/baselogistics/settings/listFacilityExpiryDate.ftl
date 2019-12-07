<#include "component://basecrm/webapp/basecrm/callcenter/ftLlibraryVariable.ftl"/>
<link rel="stylesheet" type="text/css" href="/imexresources/css/bl-css.1.0.0.css">
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxcombobox.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxtextarea.js"></script>

<@jqGridMinimumLib/>
<#assign listStatus = delegator.findList("StatusItem", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("statusTypeId", "PARTY_STATUS"), null, null, null, true) />
<script>

	if (uiLabelMap == undefined) var uiLabelMap = {};
	uiLabelMap.BLFacilityId = "${StringUtil.wrapString(uiLabelMap.BLFacilityId)}";
	uiLabelMap.BLFacilityName = "${StringUtil.wrapString(uiLabelMap.BLFacilityName)}";
	uiLabelMap.BLFacilityRequireDate = "${StringUtil.wrapString(uiLabelMap.BLFacilityRequireDate)}";
	uiLabelMap.Yes = "${StringUtil.wrapString(uiLabelMap.Yes)}";
	uiLabelMap.No = "${StringUtil.wrapString(uiLabelMap.No)}";
	
	var Theme ='olbius'
	var facilityEdittingId;
	var mapRequireDateItem = {
			"Y": uiLabelMap.Yes,
			"N": uiLabelMap.No,
		};
	var RequireDateData = [{value :'Y', description:uiLabelMap.Yes}, {value: 'N', description:uiLabelMap.No } ];
</script>

<div id ="grid"> </div>

<div id="contextMenu" style="display:none;">
	<ul>
	    <li action="edit"><i class="fa fa-pencil-square-o" ></i>${StringUtil.wrapString(uiLabelMap.Edit)}</li>
	    <li action="refresh"><i class="fa fa-refresh"></i>${StringUtil.wrapString(uiLabelMap.BSRefresh)}</li>
	</ul>
</div>
<div id="jqxNotification" >
	<div id="notificationContent"> 
	</div>
</div>

<script>
var initGridFacility = function(grid){
		var url = "jqGetFacilities&facilityGroupId=FACILITY_INTERNAL";
		var datafield =  [
			{name: 'facilityId', type: 'string'},
			{name: 'facilityCode', type: 'string'},
			{name: 'facilityName', type: 'string'},
			{name: 'requireDate', type: 'string'},
      	];
      	var columnlist = [
				{text: uiLabelMap.BLFacilityId, datafield: 'facilityCode', width: '25%', pinned: true, classes: 'pointer',
					cellsrenderer: function (row, column, value) {
						if (!value) {
							var data = grid.jqxGrid('getrowdata', row);
							value = data.facilityId;
						}
				        return '<div style="cursor:pointer;">' + (value) + '</div>';
				    }
				},
				{text: uiLabelMap.BLFacilityName, datafield: 'facilityName', width: '50%',
					cellsrenderer: function (row, column, value) {
				        return '<div style="cursor:pointer;">' + (value) + '</div>';
				    }
				},
				{text: uiLabelMap.BLFacilityRequireDate, datafield: 'requireDate', width: '25%', filtertype: 'checkedlist',
					cellsrenderer: function(row, colum, value){
							value?value=mapRequireDateItem[value]:value;
					        return '<span>' + value + '</span>';
					},
					createfilterwidget: function (column, columnElement, widget) {
					 		if (RequireDateData.length > 0) {
								var filterDataAdapter = new $.jqx.dataAdapter(RequireDateData, {
									autoBind: true
								});
								var records = filterDataAdapter.records;
								widget.jqxDropDownList({source: records, displayMember: 'description', valueMember: 'value',
									renderer: function(index, label, value){
										if (RequireDateData.length > 0) {
											for(var i = 0; i < RequireDateData.length; i++){
												if(RequireDateData[i].value == value){
													return '<span>' + RequireDateData[i].description + '</span>';
												}
											}
										}
										return value;
									}
								});
								widget.jqxDropDownList('checkAll');
							}
			   		},
			   	},
					
      	];
      	
      	var rendertoolbar = function (toolbar){
			var container = $("<div id='toolbarcontainer' class='widget-header'></div>");
            toolbar.append(container);
            container.append('<h4>${uiLabelMap.ConfigFacilityExpiryDate}</h4>');
	   	}
      	
      	var config = {
  			width: '100%', 
	   		virtualmode: true,
	   		showtoolbar: true,
	   		selectionmode: 'singlerow',
	   		pageable: true,
	   		sortable: true,
	        filterable: true,	        
	        editable: false,
	        rowsheight: 26,
	        useUrl: true,
	        url: url,                
	        source: {pagesize: 10},
	        rendertoolbar: rendertoolbar,
      	};
      	Grid.initGrid(config, datafield, columnlist, null, grid);
      	Grid.createContextMenu(grid, $("#contextMenu"), true);
	};
initGridFacility($("#grid"));
</script>

<script type="text/javascript" src="/logresources/js/config/contextMenuFacilityExpiryDate.js"></script>

<#include "editFacilityExpiryDatePopup.ftl"/>