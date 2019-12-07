<#assign initrowdetailsDetail = "function (index, parentElement, gridElement, datarecord) {
	var grid = $($(parentElement).children()[0]);
	$(grid).attr('id','jqx'+index);
	
	$('#containerjqxgridUserInModule').append('<div id=\"containerjqx' + index + '\"></div>');
	
	var datafield = [
  		{ name: 'applicationId', type: 'string'},
		{ name: 'partyId', type: 'string'},
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
		{ text: '${StringUtil.wrapString(uiLabelMap.ADPermission)}', datafield: 'permissionId', minwidth: 250, editable: false,
			cellsrenderer: function (row, columns, value, defaulthtml, columnproperties, rowdata) {
				return '<div style=margin:4px;>' + JSON.stringify(rowdata).viewOverridePermission() + '</div>';
			}
		},
		{ text: '${StringUtil.wrapString(uiLabelMap.DmsFromDate)}', datafield: 'fromDate', width: 200, filtertype: 'range', cellsformat: 'dd/MM/yyyy', editable: false },
		{ text: '${StringUtil.wrapString(uiLabelMap.DmsThruDate)}', datafield: 'thruDate', width: 200, filtertype: 'range', columntype: 'datetimeinput', cellsformat: 'dd/MM/yyyy' },
		{ text: '${StringUtil.wrapString(uiLabelMap.ADAllow)}', datafield: 'allow', width: 150, columntype: 'checkbox' },
  	];
  	var config = {
		showfilterrow: true,
		filterable: true,
		editable: datarecord.applicationId == moduleId,
		width: '95%',
		height: 250,
		pageable: true,
		sortable: true,
		virtualmode : true,
		autoheight: true,
		selectionmode: 'singlerow',
		editmode: 'dblclick',
		url: 'JQGetListOlbiusPermissionOfParty&partyId=' + datarecord.partyId + '&applicationId=' + datarecord.applicationId + '&moduleId=' + moduleId,
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
  	Grid.initGrid(config, datafield, columnlist, null, grid);
  	
  	function rendertoolbar(toolbar) {
  		if (datarecord.applicationId == moduleId) {
  			var container = $(\"<div style='margin: 10px 4px 0px 0px;' class='pull-right'></div>\");
			var aTag = $(\"<a style='cursor: pointer;'><i class='fa-plus open-sans'></i>${StringUtil.wrapString(uiLabelMap.CommonAddNew)}</a>\");
			toolbar.append(container);
			container.append(aTag);
			aTag.click(function() {
				AddPermission.open(grid, datarecord.applicationId, datarecord.partyId, true);
			});
		}
  	}
}"/>