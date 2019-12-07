(function(window, olbius) {

	function DropDownListElement(popup, config) {
		olbius.PopupElement.call(this, popup, config);

		var $this = this;

		var _append = false;

		var tmp = $.extend({
			theme : olbius.getVariable('theme'),
			displayMember : 'text',
			valueMember : 'value'
		}, config);

		var id = olbius.get(tmp, 'id');

		var label = olbius.get(tmp, 'label');

		var text = '<div id="?" style="float: right"></div><div id="?" style="margin-top: 5px;margin-right: 7px;float: right;" class="align-right">'
				+ label + '</div>';

		text = olbius.replaceString(text, [ id, id.concat('-text') ],
				$this.popup.uuid);

		var index = tmp.selectedIndex;

		this.append = function() {
			$this.spanElement().append(text);
			$this.element = $('#' + id + '-' + $this.popup.uuid);
			$this.element.jqxDropDownList(tmp);
			_append = true;
		};

		this.val = function() {
			if (_append) {
				var item = $this.element.jqxDropDownList('getSelectedItem');
				index = item.index;
				return item.value;
			} else {
				if (typeof tmp.selectedIndex == 'undefined'
						|| typeof tmp.source == 'undefined') {
					return null;
				} else {
					if (tmp.source[tmp.selectedIndex][tmp.valueMember]) {
						return tmp.source[tmp.selectedIndex][tmp.valueMember];
					} else {
						return tmp.source[tmp.selectedIndex];
					}
				}
			}
		};

		this.clean = function() {
			if (_append) {
				$this.element.jqxDropDownList('selectIndex', index);
			}
		};

		this.width = function() {
			return $('#' + id + '-text-' + $this.popup.uuid).actual('width')
					+ 10 + $this.element.width();
		};

		this.height = function() {
			return $this.element.height();
		};

		this.on = function(event, func) {
			$this.element.on(event, func);
		}
	}

	DropDownListElement.prototype = $.extend({}, olbius.PopupElement.prototype,
			DropDownListElement.prototype);

	olbius.addPopupConfig('jqxDropDownList', function(config) {
		return new DropDownListElement(this, config);
	});

}(window, OlbiusUtil));

(function(window, olbius) {

	function DateTimeInputElement(popup, config) {
		olbius.PopupElement.call(this, popup, config);

		var $this = this;

		var _append = false;

		var tmp = $.extend({
			theme : olbius.getVariable('theme'),
			formatString : olbius.getVariable('dateTimeFormat')
		}, config);

		var id = olbius.get(tmp, 'id');

		var label = olbius.get(tmp, 'label');

		var text = '<div id="?" style="display:inline-block; vertical-align:top;float: right;margin-left:0 !important;"></div><div id="?" style="margin-top: 5px;margin-right: 7px;float: right;" class="align-right">'
				+ label + '</div>';

		text = olbius.replaceString(text, [ id, id.concat('-text') ],
				$this.popup.uuid);

		var value = tmp.value;

		this.append = function() {
			$this.spanElement().append(text);
			$this.element = $('#' + id + '-' + $this.popup.uuid);
			$this.element.jqxDateTimeInput(tmp);
			_append = true;
		};

		this.val = function() {
			if (_append) {
				value = $this.element.val('date');
				return olbius.dateToString(value);
			} else {
				if (typeof tmp.value == 'undefined') {
					return null;
				} else {
					return olbius.dateToString(tmp.value);
				}
			}
		};

		this.clean = function() {
			if (_append) {
				$this.element.jqxDateTimeInput('val', value);
			}
		};

		this.width = function() {
			return $('#' + id + '-text-' + $this.popup.uuid).actual('width')
					+ 10 + $this.element.width();
		};

		this.height = function() {
			return $this.element.height();
		};

		this.on = function(event, func) {
			$this.element.on(event, func);
		}
	}

	DateTimeInputElement.prototype = $.extend({},
			olbius.PopupElement.prototype, DateTimeInputElement.prototype);

	olbius.addPopupConfig('jqxDateTimeInput', function(config) {
		return new DateTimeInputElement(this, config);
	});

}(window, OlbiusUtil));

(function(window, olbius) {

	function JqxGridMultipleElement(popup, config) {
		if (!config.grid.source) {
			throw "init popup: grid.source required";
		}
		if (_.isEmpty(config.grid.id)) {
			throw "init popup: grid.id required";
		}

		olbius.PopupElement.call(this, popup, config);

		var $this = this;

		var _append = false;

		var rowindexes = [];

		var tmp = $.extend({
			theme : olbius.getVariable('theme')
		}, _.omit(config, 'grid'));

		var configGrid = $.extend({
			localization : getLocalization(),
			theme : olbius.getVariable('theme'),
			selectionmode : 'checkbox',
			autoheight : true,
			pagesizeoptions : [ '5', '10' ],
			handlekeyboardnavigation : function(event) {
				var key = event.charCode ? event.charCode
						: event.keyCode ? event.keyCode : 0;
				if (key == 70 && event.ctrlKey) {
					$('#' + id + '-jqxgrid-' + $this.popup.uuid).jqxGrid(
							'clearfilters');
					return true;
				}
			}
		}, _.omit(config.grid, 'source', 'datafields', 'id'));

		var source = {};
		if (_.isEmpty(config.grid.datafields)) {
			source = {
				localdata : config.grid.source
			};
			source.datafields = [];
			var keys = _.keys(_.first(source.localdata));
			for ( var x in keys) {
				source.datafields.push({
					name : keys[x],
					type : 'string'
				});
			}
		} else {
			source = {
				localdata : config.grid.source,
				datafields : config.grid.datafields
			};
		}
		source.id = config.grid.id;
		configGrid.source = new $.jqx.dataAdapter(source);

		var id = olbius.get(tmp, 'id');

		var label = olbius.get(tmp, 'label');

		var text = '<div id="?" style="float: right"><div style="border-color: transparent;" id="?"></div></div><div id="?" style="margin-top: 5px;margin-right: 7px;float: right;" class="align-right">'
				+ label + '</div>';

		text = olbius.replaceString(text, [ id, id.concat('-jqxgrid'),
				id.concat('-text') ], $this.popup.uuid);

		this.append = function() {
			$this.spanElement().append(text);
			$this.element = $('#' + id + '-' + $this.popup.uuid);
			$this.element.jqxDropDownButton(tmp);
			$this.Gridelement = $('#' + id + '-jqxgrid-' + $this.popup.uuid);
			$this.Gridelement.jqxGrid(configGrid);

			$this.Gridelement.on('rowselect', function() {
				var rowselected = $this.Gridelement
						.jqxGrid('getselectedrowindexes');
				setContentDropDownButton(rowselected.length == 0 ? ""
						: rowselected.length + " item(s)");
			});
			$this.Gridelement.on('rowunselect', function() {
				var rowselected = $this.Gridelement
						.jqxGrid('getselectedrowindexes');
				setContentDropDownButton(rowselected.length == 0 ? ""
						: rowselected.length + " item(s)");
			});
			_append = true;
		};
		var setContentDropDownButton = function(content) {
			var dropDownContent = '<div style="position: relative; margin-left: 3px; margin-top: 5px;">'
					+ content + '</div>';
			$this.element.jqxDropDownButton('setContent', dropDownContent);
		};
		this.val = function() {
			if (_append) {
				var value = [];
				rowindexes = $this.Gridelement.jqxGrid('getselectedrowindexes');
				for ( var x in rowindexes) {
					value.push($this.Gridelement.jqxGrid('getrowid',
							rowindexes[x]));
				}
				return value;
			} else {
				return null;
			}
		};

		this.clean = function() {
			if (_append) {
				$this.Gridelement.jqxGrid('clearselection');
				if (rowindexes) {
					for ( var x in rowindexes) {
						$this.Gridelement.jqxGrid('selectrow', rowindexes[x]);
					}
				}
			}
		};

		this.width = function() {
			return $('#' + id + '-text-' + $this.popup.uuid).actual('width')
					+ 10 + $this.element.width();
		};

		this.height = function() {
			return $this.element.height();
		};

		this.on = function(event, func) {
			$this.element.on(event, func);
		}
	}

	JqxGridMultipleElement.prototype = $.extend({},
			olbius.PopupElement.prototype, JqxGridMultipleElement.prototype);

	olbius.addPopupConfig('jqxGridMultiple', function(config) {
		return new JqxGridMultipleElement(this, config);
	});

}(window, OlbiusUtil));

/** add grid with remote data by kieuanhvu **/
(function(window, olbius) {
	function JqxGridElement(popup, config){
		if (_.isEmpty(config.grid.id)) {
			throw "init popup: grid.id required";
		}
		if (_.isEmpty(config.grid.url)) {
			throw "init popup: grid.url is required";
		}
		olbius.PopupElement.call(this, popup, config);
		var $this = this;
		
		var _append = false;
		var rowSelectedIndex = -1;
		
		var url = config.grid.url;
		var showtoolbar = typeof(config.grid.showtoolbar) != "undefined"? config.grid.showtoolbar: true; 
		var tmp = $.extend({theme : olbius.getVariable('theme')}, _.omit(config, 'grid'));
		
		var configGrid = $.extend({
							localization : getLocalization(),
							theme : olbius.getVariable('theme'),
							selectionmode : 'singlerow',
							virtualmode: true,
							pagesizeoptions : [ '5', '10' ],
							source:{
								id: config.grid.id,
								pagesize: config.grid.pagesize
							}
						}, 
						_.omit(config.grid, 'source', 'datafields', 'id', 'columns', 'pagesize'));
		
		configGrid.showtoolbar = showtoolbar;
		var datafields = config.grid.datafields;
		var columns = config.grid.columns;
		
		var id = olbius.get(tmp, 'id');

		var label = olbius.get(tmp, 'label');
		
		var displayField = config.grid.displayField;
		if(typeof(displayField) == undefined){
			displayField = config.grid.id;
		}
		var displayAdditionField = config.grid.displayAdditionField;
		
		var text = '<div id="?" style="float: right"><div style="border-color: transparent;" id="?"></div></div><div id="?" style="margin-top: 5px;margin-right: 7px;float: right;" class="align-right">'
				+ label + '</div>';
		
		text = olbius.replaceString(text, [ id, id.concat('-jqxgrid'), id.concat('-text') ], $this.popup.uuid);
		
		this.append = function() {
			$this.spanElement().append(text);
			$this.element = $('#' + id + '-' + $this.popup.uuid);
			$this.element.jqxDropDownButton(tmp);
			$this.Gridelement = $('#' + id + '-jqxgrid-' + $this.popup.uuid);
			if(showtoolbar){
				var rendertoolbar = function(toolbar){
					toolbar.html("");
					var jqxheader = $("<div id='toolbarcontainer" + id +"' class='widget-header'><h5>" + config.grid.gridTitle + "</h5><div id='toolbarButtonContainer" + id + "' class='pull-right'></div></div>");
					toolbar.append(jqxheader);
			     	var container = $('#toolbarButtonContainer' + id);
			        var maincontainer = $("#toolbarcontainer" + id);
			        var buttonEle = '<div class="custom-control-toolbar">'
    					+ '<a id="customcontrol' + id + '" style="color:#438eb9;" href="javascript:void(0)">'
    					+ '<i class="icon-remove open-sans"></i>&nbsp;<span>' + config.grid.clearSelectionBtnText +'</span></a></div>';
			        container.append(buttonEle);
			        $("#customcontrol" + id).click(function(){
			        	$this.clearSelection();
			        });
				}
				configGrid.rendertoolbar = rendertoolbar;
			}
			
			Grid.initGrid(configGrid, datafields, columns, null, $this.Gridelement);
			_append = true;
			if (config.grid.placeHolder) {
				setContentDropDownButton(config.grid.placeHolder);
			} else {
				setContentDropDownButton("------------------------");
			}
			$this.Gridelement.on('rowclick', function(event) {
				var boundIndex = event.args.rowindex;
				var rowData = $this.Gridelement.jqxGrid('getrowdata', boundIndex);
				var content = rowData[displayField];
				if(typeof(displayAdditionField) != undefined){
					content += ' [' + rowData[displayAdditionField] + ']';
				}
				setContentDropDownButton(content);
			});
		};
		var setContentDropDownButton = function(content) {
			var dropDownContent = '<div style="position: relative; margin-left: 3px; margin-top: 5px;">' + content + '</div>';
			$this.element.jqxDropDownButton('setContent', dropDownContent);
			$this.element.jqxDropDownButton('close');
		};
		this.val = function() {
			if (_append) {
				rowSelectedIndex = $this.Gridelement.jqxGrid('getselectedrowindex');
				if(rowSelectedIndex > -1){
					var rowData = $this.Gridelement.jqxGrid('getrowdata', rowSelectedIndex);
					return rowData[config.grid.id];
				}
				return "";
			} else {
				return null;
			}
		};
		
		this.clearSelection = function(){
			$this.Gridelement.jqxGrid('clearselection');
			if (config.grid.placeHolder) {
				setContentDropDownButton(config.grid.placeHolder);
			} else {
				setContentDropDownButton("------------------------");
			}
		};
		
		this.clean = function() {
			if (_append) {
				$this.Gridelement.jqxGrid('clearselection');
				if (rowSelectedIndex > -1) {
					$this.Gridelement.jqxGrid('selectrow', rowSelectedIndex);
					var rowData = $this.Gridelement.jqxGrid('getrowdata', rowSelectedIndex);
					var content = rowData[displayField];
					if(typeof(displayAdditionField) != undefined){
						content += ' [' + rowData[displayAdditionField] + ']';
					}
					setContentDropDownButton(content);
				}else{
					if (config.grid.placeHolder) {
						setContentDropDownButton(config.grid.placeHolder);
					} else {
						setContentDropDownButton("------------------------");
					}
				}
			}
		};

		this.width = function() {
			return $('#' + id + '-text-' + $this.popup.uuid).actual('width') + 10 + $this.element.width();
		};

		this.height = function() {
			return $this.element.height();
		};

		this.on = function(event, func) {
			$this.element.on(event, func);
		}
	}
	JqxGridElement.prototype = $.extend({},
			olbius.PopupElement.prototype, JqxGridElement.prototype);

	olbius.addPopupConfig('jqxGridElement', function(config) {
		return new JqxGridElement(this, config);
	});
}(window, OlbiusUtil));


(function(window, olbius) {

	function NumberInputElement(popup, config) {
		olbius.PopupElement.call(this, popup, config);

		var $this = this;

		var _append = false;

		var tmp = $.extend({
			theme : olbius.getVariable('theme'),
		}, config);

		var id = olbius.get(tmp, 'id');

		var label = olbius.get(tmp, 'label');

		var text = '<div id="?" style="display:inline-block; vertical-align:top;float: right;margin-left:0 !important;"></div><div id="?" style="margin-top: 5px;margin-right: 7px;float: right;" class="align-right">'
				+ label + '</div>';

		text = olbius.replaceString(text, [ id, id.concat('-text') ],
				$this.popup.uuid);

		var value = tmp.value;

		this.append = function() {
			$this.spanElement().append(text);
			$this.element = $('#' + id + '-' + $this.popup.uuid);
			$this.element.jqxNumberInput(tmp);
			_append = true;
		};

		this.val = function() {
			if (_append) {
				value = $this.element.val();
				return value;
			} else {
				if (typeof tmp.value == 'undefined') {
					return null;
				} else {
					return tmp.value;
				}
			}
		};

		this.clean = function() {
			if (_append) {
				var date = new Date();
				$this.element.jqxNumberInput('val', date.getFullYear());
			}
		};

		this.width = function() {
			return $('#' + id + '-text-' + $this.popup.uuid).actual('width')
					+ 10 + $this.element.width();
		};

		this.height = function() {
			return $this.element.height();
		};

		this.on = function(event, func) {
			$this.element.on(event, func);
		}
	}

	NumberInputElement.prototype = $.extend({},
			olbius.PopupElement.prototype, NumberInputElement.prototype);

	olbius.addPopupConfig('jqxNumberInput', function(config) {
		return new NumberInputElement(this, config);
	});

}(window, OlbiusUtil));
/** ./end **/