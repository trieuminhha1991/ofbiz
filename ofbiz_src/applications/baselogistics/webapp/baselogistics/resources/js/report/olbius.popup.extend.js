(function (window, olbius) {

    function JqxGridMultipleElement(popup, config) {
    	if (_.isEmpty(config.grid.url)) {
			throw "init popup: grid.url required";
		}
    	if (_.isEmpty(config.grid.datafields)) {
    		throw "init popup: grid.datafields required";
    	}
    	if (_.isEmpty(config.grid.id)) {
    		throw "init popup: grid.id required";
    	}
    	
        olbius.PopupElement.call(this, popup, config);

        var $this = this;

        var _append = false;

        var rowindexes = [];
        
        var tmp = $.extend({
            theme: olbius.getVariable('theme')
        }, _.omit(config, 'grid') );
        
        var configGrid = $.extend({
        	selectionmode: 'checkbox',
        	autoheight: true,
        	pagesizeoptions: ['5', '10'],
        	handlekeyboardnavigation: function (event) {
                var key = event.charCode ? event.charCode : event.keyCode ? event.keyCode : 0;
                if (key == 70 && event.ctrlKey) {
                	$('#' + id + '-jqxgrid-' + $this.popup.uuid).jqxGrid('clearfilters');
					return true;
                }
			},
			source: {pagesize: 5, id: config.grid.id}
        }, _.omit(config.grid, 'datafields', 'id', "columns") );
        
        var id = olbius.get(tmp, 'id');

        var label = olbius.get(tmp, 'label');

        var text = '<div id="?" style="float: right"><div style="border-color: transparent;" id="?"></div></div><div id="?" style="margin-top: 5px;margin-right: 7px;float: right;" class="align-right">' +
            label +
            '</div>';

        text = olbius.replaceString(text, [id, id.concat('-jqxgrid'), id.concat('-text')], $this.popup.uuid);

        this.append = function () {
            $this.spanElement().append(text);
            $this.element = $('#' + id + '-' + $this.popup.uuid);
            $this.element.jqxDropDownButton(tmp);
            
            $this.Gridelement = $('#' + id + '-jqxgrid-' + $this.popup.uuid);
            
            Grid.initGrid(configGrid, config.grid.datafields, config.grid.columns, null, $this.Gridelement);
            
            $this.Gridelement.on('rowselect', function (event) {
                var args = event.args;
                var rowselected = $this.Gridelement.jqxGrid('getselectedrowindexes');
                setContentDropDownButton(rowselected.length==0?"":rowselected.length + " item(s)");
            });
            $this.Gridelement.on('rowunselect', function (event) {
            	var args = event.args;
            	var rowselected = $this.Gridelement.jqxGrid('getselectedrowindexes');
            	setContentDropDownButton(rowselected.length==0?"":rowselected.length + " item(s)");
            });
            _append = true;
        }
        var setContentDropDownButton = function(content) {
        	var dropDownContent = '<div style="position: relative; margin-left: 3px; margin-top: 5px;">' + content + '</div>';
            $this.element.jqxDropDownButton('setContent', dropDownContent);
		}
        this.val = function () {
            if (_append) {
            	var value = new Array();
            	rowindexes = $this.Gridelement.jqxGrid('getselectedrowindexes');
            	for ( var x in rowindexes) {
            		value.push($this.Gridelement.jqxGrid('getrowid', rowindexes[x]));
				}
                return value;
            } else {
            	return null;
            }
        }

        this.clean = function () {
            if (_append) {
            	$this.Gridelement.jqxGrid('clearselection');
                if (rowindexes) {
					for ( var x in rowindexes) {
						$this.Gridelement.jqxGrid('selectrow', rowindexes[x]);
					}
				}
            }
        }

        this.width = function () {
            return $('#' + id + '-text-' + $this.popup.uuid).actual('width') + 10 + $this.element.width();
        }

        this.height = function () {
            return $this.element.height();
        }

        this.on = function (event, func) {
            $this.element.on(event, func);
        }
    }

    JqxGridMultipleElement.prototype = $.extend({}, olbius.PopupElement.prototype, JqxGridMultipleElement.prototype);

    olbius.addPopupConfig('jqxGridMultipleUrl', function (config) {
        return new JqxGridMultipleElement(this, config);
    });
    
}(window, OlbiusUtil));