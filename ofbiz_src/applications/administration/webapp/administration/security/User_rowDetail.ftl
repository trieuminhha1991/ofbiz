<#assign initrowdetailsDetail = "function (index, parentElement, gridElement, datarecord) {
	var container = $($(parentElement).children()[0]);
	$(container).attr('id', 'container'+index);
	
	var jqxtabs = $(\"<div id=jqxtabs\" + index + \"></div>\");
	var ul = $(\"<ul></ul>\");
	var li1 = $(\"<li style='margin-left: 30px;'>\" + multiLang.ADModule + \"</li>\");
	var li2 = $(\"<li>\" + multiLang.ADAction + \"</li>\");
	ul.append(li1);
	ul.append(li2);
	var jqxgrid1 = $(\"<div id=jqxgridModule\" + index + \"></div>\");
	var jqxgrid2 = $(\"<div id=jqxgridAction\" + index + \"></div>\");
	jqxtabs.append(jqxgrid1);
	jqxtabs.append(jqxgrid2);
	jqxtabs.append(ul);
	container.append(jqxtabs);
	
	var datafield = [
		{ name: 'applicationId', type: 'string' },
		{ name: 'applicationType', type: 'string' },
		{ name: 'application', type: 'string' },
		{ name: 'name', type: 'string' },
		{ name: 'permissionId', type: 'string' },
		{ name: 'moduleId', type: 'string' }
	];
	var columnlist = [
		{text: '${uiLabelMap.DmsSequenceId}', datafield: '', sortable: false, filterable: false, pinned: true, groupable: false, draggable: false, resizable: false, width: 50,
			cellsrenderer: function (row, column, value) {
				return '<div style=margin:4px;>' + (row + 1) + '</div>';
			}
		},
		{ text: '${StringUtil.wrapString(uiLabelMap.ADApplicationId)}', datafield: 'applicationId', width: 250,
			cellsrenderer: function(row, column, value, a, b, data){
				var link = 'ModuleDetail?moduleId=' + value;
				return '<a href=\"' + link + '\">' + value + '</a>';
			}
		},
		{ text: '${StringUtil.wrapString(uiLabelMap.ADApplication)}', dataField: 'application', width: 250 },
		{ text: '${StringUtil.wrapString(uiLabelMap.ADApplicationName)}', dataField: 'name', minwidth: 300 },
		{ text: '${StringUtil.wrapString(uiLabelMap.ADPermissionDefault)}', dataField: 'permissionId', width: 150,
			cellsrenderer: function (row, columns, value, defaulthtml, columnproperties, rowdata) {
				return '<div style=margin:4px;>' + JSON.stringify(rowdata).viewOverridePermission() + '</div>';
			}
		}
	];
	var config = {
		showfilterrow: true,
		filterable: true,
		editable: false,
		width: '95%',
		height: 250,
		pageable: true,
		sortable: true,
		virtualmode : true,
		autoheight: true,
		selectionmode: 'singlerow',
		url: 'JQGetListApplicationOfParty&applicationType=MODULE&partyId=' + datarecord.partyId,
		source: {
			pagesize: 5
		}
	};
	Grid.initGrid(config, datafield, columnlist, null, jqxgrid1);
	
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
		config.url = 'JQGetListApplicationOfParty&applicationType=ENTITY&partyId=' + datarecord.partyId;
		columnlist = [
			{text: '${uiLabelMap.DmsSequenceId}', datafield: '', sortable: false, filterable: false, pinned: true, groupable: false, draggable: false, resizable: false, width: 50,
				cellsrenderer: function (row, column, value) {
					return '<div style=margin:4px;>' + (row + 1) + '</div>';
				}
			},
			{ text: '${StringUtil.wrapString(uiLabelMap.ADActionId)}', datafield: 'applicationId', width: 250 },
			{ text: '${StringUtil.wrapString(uiLabelMap.ADAction)}', dataField: 'application', width: 250 },
			{ text: '${StringUtil.wrapString(uiLabelMap.ADActionName)}', dataField: 'name', minwidth: 300 },
			{ text: '${StringUtil.wrapString(uiLabelMap.ADPermissionDefault)}', dataField: 'permissionId', width: 150 }
		];
		Grid.initGrid(config, datafield, columnlist, null, jqxgrid2);
	}
	jqxtabs.jqxTabs({ width: '100%', height: 250, position: 'top'});
}"/>