<#assign initrowdetailsDetail = "function (index, parentElement, gridElement, datarecord) {
	var container = $($(parentElement).children()[0]);
	$(container).attr('id', 'container'+index);
	
	var jqxtabs = $(\"<div id=jqxtabs\" + index + \"></div>\");
	var ul = $(\"<ul></ul>\");
	var li1 = $(\"<li style='margin-left: 30px;'>\" + multiLang.ADUser + \"</li>\");
	var li2 = $(\"<li>\" + multiLang.ADUserGroup + \"</li>\");
	ul.append(li1);
	ul.append(li2);
	var jqxgrid1 = $(\"<div id=jqxgridActionUser\" + index + \"></div>\");
	var jqxgrid2 = $(\"<div id=jqxgridActionGroup\" + index + \"></div>\");
	jqxtabs.append(jqxgrid1);
	jqxtabs.append(jqxgrid2);
	jqxtabs.append(ul);
	container.append(jqxtabs);
	
	$('#containerjqxgridActionInModule').append('<div id=\"containerjqxgridActionUser' + index + '\"></div>');
	$('#containerjqxgridActionInModule').append('<div id=\"containerjqxgridActionGroup' + index + '\"></div>');
	
	var datafield =  [
		{ name: 'applicationId', type: 'string'},
		{ name: 'userLoginId', type: 'string'},
		{ name: 'partyId', type: 'string'},
		{ name: 'partyCode', type: 'string'},
		{ name: 'partyName', type: 'string'},
		{ name: 'permissionId', type: 'string'},
		{ name: 'allow', type: 'bool'},
		{ name: 'fromDate', type: 'date', other: 'Timestamp'},
		{ name: 'thruDate', type: 'date', other: 'Timestamp'}
	];
	var columnlist = [
		{text: '${uiLabelMap.DmsSequenceId}', datafield: '', sortable: false, filterable: false, editable: false, pinned: true, groupable: false, draggable: false, resizable: false, width: 50,
			cellsrenderer: function (row, column, value) {
				return '<div style=margin:4px;>' + (row + 1) + '</div>';
			}
		},
		{ text: '${StringUtil.wrapString(uiLabelMap.ADApplicationId)}', dataField: 'applicationId', width: 200, editable: false },
		{ text: '${StringUtil.wrapString(uiLabelMap.userLoginId)}', dataField: 'userLoginId', width: 200, editable: false },
		{ text: '${StringUtil.wrapString(uiLabelMap.ADPermission)}', datafield: 'permissionId', minwidth: 100, editable: false,
			cellsrenderer: function (row, columns, value, defaulthtml, columnproperties, rowdata) {
				return '<div style=margin:4px;>' + JSON.stringify(rowdata).viewOverridePermission() + '</div>';
			}
		},
		{ text: '${StringUtil.wrapString(uiLabelMap.DmsFromDate)}', datafield: 'fromDate', width: 170, filtertype: 'range', cellsformat: 'dd/MM/yyyy', editable: false },
		{ text: '${StringUtil.wrapString(uiLabelMap.DmsThruDate)}', datafield: 'thruDate', width: 170, filtertype: 'range', columntype: 'datetimeinput', cellsformat: 'dd/MM/yyyy',
			cellbeginedit: function (row, datafield, columntype, value) {
				if (jqxgrid1.jqxGrid('getcellvalue', row, 'applicationId') == datarecord.applicationId) {
					return true;
				} else {
					return false;
				}
			}
		},
		{ text: '${StringUtil.wrapString(uiLabelMap.ADAllow)}', datafield: 'allow', width: 100, columntype: 'checkbox',
			cellbeginedit: function (row, datafield, columntype, value) {
				if (jqxgrid1.jqxGrid('getcellvalue', row, 'applicationId') == datarecord.applicationId) {
					return true;
				} else {
					return false;
				}
			}
		}
  	];
  	var config = {
		showfilterrow: true,
		filterable: true,
		editable: true,
		width: '95%',
		height: 250,
		pageable: true,
		sortable: true,
		virtualmode : true,
		enabletooltips: true,
		autoheight: true,
		selectionmode: 'singlerow',
		editmode: 'dblclick',
		url: 'JQGetListOlbiusPermissionOfAction&type=USER&applicationId=' + datarecord.applicationId,
		source: {
			pagesize: 5,
			updateUrl: 'updateOlbiusPartyPermission',
			editColumns: 'applicationId;partyId;permissionId;fromDate(java.sql.Timestamp);thruDate(java.sql.Timestamp);allow(java.lang.Boolean)',
			createUrl: 'createOlbiusPartyPermission',
			addColumns: 'applicationId;partyId;permissionId;fromDate(java.sql.Timestamp);thruDate(java.sql.Timestamp);allow(java.lang.Boolean)'
		},
		showtoolbar: true,
		toolbarheight: 27,
		rendertoolbar: rendertoolbar
	};
	Grid.initGrid(config, datafield, columnlist, null, jqxgrid1);

	function rendertoolbar(toolbar) {
		var container = $(\"<div style='margin: 10px 4px 0px 0px;' class='pull-right'></div>\");
		var aTag = $(\"<a style='cursor: pointer;'><i class='fa-plus open-sans'></i>${StringUtil.wrapString(uiLabelMap.CommonAddNew)}</a>\");
		toolbar.append(container);
		container.append(aTag);
		aTag.click(function() {
			AddUserToAction.open(jqxgrid1, datarecord.applicationId);
		});
  	}
	function rendertoolbar2(toolbar) {
		var container = $(\"<div style='margin: 10px 4px 0px 0px;' class='pull-right'></div>\");
		var aTag = $(\"<a style='cursor: pointer;'><i class='fa-plus open-sans'></i>${StringUtil.wrapString(uiLabelMap.CommonAddNew)}</a>\");
		toolbar.append(container);
		container.append(aTag);
		aTag.click(function() {
			AddGroupToAction.open(jqxgrid2, datarecord.applicationId);
		});
	}
	
	jqxtabs.on('tabclick', function (event) {
		var clickedItem = event.args.item;
		if (clickedItem == 1) {
			if (jqxgrid2.attr('role') != 'grid') {
				setTimeout(function() {
					initGrid2();
				}, 50);
			}
		}
	});
	var initGrid2 = function() {
		config.url = 'JQGetListOlbiusPermissionOfAction&type=GROUP&applicationId=' + datarecord.applicationId;
		config.rendertoolbar = rendertoolbar2;
		columnlist = [
					{text: '${uiLabelMap.DmsSequenceId}', datafield: '', sortable: false, filterable: false, editable: false, pinned: true, groupable: false, draggable: false, resizable: false, width: 50,
						cellsrenderer: function (row, column, value) {
							return '<div style=margin:4px;>' + (row + 1) + '</div>';
						}
					},
					{ text: '${StringUtil.wrapString(uiLabelMap.ADApplicationId)}', dataField: 'applicationId', width: 200, editable: false },
					{ text: '${StringUtil.wrapString(uiLabelMap.ADUserGroupId)}', dataField: 'partyCode', width: 200, editable: false },
					{ text: '${StringUtil.wrapString(uiLabelMap.ADPermission)}', datafield: 'permissionId', minwidth: 100, editable: false,
						cellsrenderer: function (row, columns, value, defaulthtml, columnproperties, rowdata) {
							return '<div style=margin:4px;>' + JSON.stringify(rowdata).viewOverridePermission() + '</div>';
						}
					},
					{ text: '${StringUtil.wrapString(uiLabelMap.DmsFromDate)}', datafield: 'fromDate', width: 170, filtertype: 'range', cellsformat: 'dd/MM/yyyy', editable: false },
					{ text: '${StringUtil.wrapString(uiLabelMap.DmsThruDate)}', datafield: 'thruDate', width: 170, filtertype: 'range', columntype: 'datetimeinput', cellsformat: 'dd/MM/yyyy',
						cellbeginedit: function (row, datafield, columntype, value) {
							if (jqxgrid1.jqxGrid('getcellvalue', row, 'applicationId') == datarecord.applicationId) {
								return true;
							} else {
								return false;
							}
						}
					},
					{ text: '${StringUtil.wrapString(uiLabelMap.ADAllow)}', datafield: 'allow', width: 100, columntype: 'checkbox',
						cellbeginedit: function (row, datafield, columntype, value) {
							if (jqxgrid1.jqxGrid('getcellvalue', row, 'applicationId') == datarecord.applicationId) {
								return true;
							} else {
								return false;
							}
						}
					}
		];
		Grid.initGrid(config, datafield, columnlist, null, jqxgrid2);
	};
	
	jqxtabs.jqxTabs({ width: '100%', height: 250, position: 'top'});
}"/>

<#include "popup/addPartyToAction.ftl"/>
<#include "popup/addGroupToAction.ftl"/>