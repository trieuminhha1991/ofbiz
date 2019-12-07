(function (window, olbius) {

    var _readConfig = {};

    var _gridLabel = {
        warnGridLabel: 'Warn',
        okGridLabel: 'OK',
        cancelGridLabel: 'Cancel',
        lastUpdatedGridLabel: 'Last updated',
        aggregateLabel: 'Aggregate',
        pdfLabel: 'pdf',
        excelLabel: 'excel',
        configLabel: 'config'
    };

    olbius.ObiusGridConfig = function (name, func) {
        if (typeof func == 'function') {
            _readConfig[name] = func;
        }
    };

    olbius.gridLabel = function (config) {
        _gridLabel = $.extend(_gridLabel, config);
    };

    function appendContainer(id) {

        if ($(id).is('div')) {
            var uuid = olbius.random();
            var txt = olbius.replaceString('<div id="?"></div>', ['olbiusGrid'], uuid);
            $(id).append(txt);
            return $('#olbiusGrid-' + uuid);
        } else {
            return appendContainer($(id).parent());
        }
    }

    function initApdater(object) {
        var apdater = new $.jqx.dataAdapter(object._source, {
            formatData: function () {
                var tmp = $.extend({
                    'serviceName': object._serviceName
                }, object._data, object.getConfig());
                tmp = $.extend(tmp, {
                    olapType: 'GRID'
                });
                return tmp;
            },
            downloadComplete: function (edata) {
                if (edata.totalsize) {
                    object._source.totalrecords = edata.totalsize;
                    object.init();
                }
            },
            loadComplete: function (edata) {
                var tmp = {};
                var i;
                for (i in edata.datafields) {
                    tmp[edata.datafields[i]] = true;
                }

                for (i in object._columns) {
                    if (tmp[i]) {
                        if (object._datafields[i].column.hidden == false) {
                            object._grid.jqxGrid('showcolumn', i);
                        }
                    } else {
                        if (typeof object._datafields[i].column.hidden == 'undefined' || object._datafields[i].column.hidden) {
                            object._grid.jqxGrid('hidecolumn', i);
                        }
                    }
                }
            }
        });
        return apdater;
    }

    function OlbiusGrid(config) {

        var $this = this;

        this._data = {};

        this._source = {
            datatype: 'json',
            type: 'POST',
            root: 'data'
        };

        this._config = config;

        this._configPopup = olbius.get(config, 'popup');

        if (this._configPopup) {
            this._popup = olbius.popup(this._configPopup);
        }

        for (var i in _readConfig) {
            _readConfig[i].call(this, this._config);
        }

        var _service;

        var _serviceTimestamp;

        _service = olbius.get(this._config, 'service');

        var $update;
        var $status;
        var $lastupdated;
        var _render = false;
        var _updating = false;
        var _updated = false;
        var _init = true;

        function checkStatus() {
            jQuery.ajax({
                url: 'getStatusOlapServices',
                async: true,
                type: 'POST',
                data: {'service': _service},
                success: function (data) {
                    if (data.status && data.status === 'COMPLETED') {
                        getLastUpdated();
                    } else {
                        getUpdating();
                        setTimeout(checkStatus, 10000);
                    }
                }
            });
        }

        function getUpdating() {
            $status.show();
            //$lastupdated.hide();
            if ($update) {
                $update.hide();
            }
            _updating = true;
        }

        function getLastUpdated() {
            jQuery.ajax({
                url: 'getLastupdatedOlapServices',
                async: true,
                type: 'POST',
                data: {'service': _service},
                success: function (data) {
                    if (data.lastupdated > 0) {
                        var tmp = $.jqx.dataFormat.formatdate(new Date(data.lastupdated), olbius.getVariable('dateTimeFullFormat'));
                        _serviceTimestamp = data.lastupdated;
                        $lastupdated.html(_gridLabel.lastUpdatedGridLabel + ': ' + tmp);
                        $status.hide();
                        if ($update) {
                            $update.show();
                        }
                        //$lastupdated.show();
                    } else {
                        $status.hide();
                        //$lastupdated.hide();
                        if ($update) {
                            $update.show();
                        }
                    }
                },
                complete: function(){
                	if (_updating) {
                        _updated = true;
                        _updating = false;
                        _init = true;
                        $this._grid.jqxGrid('updatebounddata');
                        $('body').trigger('runolapservicedone');
                    }
                }
            });
        }

        function rendertoolbar(toolbar) {

            if (!$this._title && !$this._popup && !$this._pdf && !$this._excel && !_service) {
                return;
            }

            if (_render) {
                return;
            }

            var container = $("<div style='margin: 3px;'></div>");
            var span = $("<h4 style='float: left; margin-top: 5px; margin-right: 4px; color: #4383b4'>" + $this._title + " </h4>");
            toolbar.append(container);
            container.append(span);
            var uuid = olbius.random();
            var id = 'button-' + uuid;
            var pid = 'pbutton-' + uuid;
            var eid = 'ebutton-' + uuid;

            if ($this._popup) {
                container.append('<button id="' + id + '" title="' + _gridLabel.configLabel + '" class="btn btn-mini btngridsetting"><i class="fa-cogs"></i></button>');
                $('#' + id).on('click', function () {
                    $this._popup.open();
                });
            }

            if (typeof $this._pdf == 'function') {
                container.append('<button id="' + pid + '" title="' + _gridLabel.pdfLabel + '" class="btn btn-mini btngridsetting"><i class="fa-print"></i></button>');
                $('#' + pid).on('click', function () {
                    $this._pdf($this);
                });
            }

            if (typeof $this._excel == 'function') {
                container.append('<button id="' + eid + '" title="' + _gridLabel.excelLabel + '" class="btn btn-mini btngridsetting"><i class="fa-file-excel-o"></i></button>');
                $('#' + eid).on('click', function () {
                    $this._excel($this);
                });
            }

            if (_service) {
                if ($this._button) {
                    var uid = 'update-' + uuid;

                    container.append('<button id="' + uid + '" title="' + _gridLabel.aggregateLabel + '" class="btn btn-mini btngridsetting"><i class="fa-play"></i></button>');

                    $update = $('#' + uid);

                    $update.on('click', function () {
                        bootbox.dialog(_gridLabel.warnGridLabel,
                            [{
                                "label": _gridLabel.okGridLabel,
                                "class": "btn-primary btn-small icon-ok",
                                "callback": function () {
                                    jQuery.ajax({
                                        url: 'runOlapService',
                                        async: true,
                                        type: 'POST',
                                        data: {'service': _service},
                                        success: function () {
                                            getUpdating();
                                            setTimeout(checkStatus, 10000);
                                        }
                                    });
                                }
                            },
                                {
                                    "label": _gridLabel.cancelGridLabel,
                                    "class": "btn-danger btn-small icon-remove",
                                    "callback": function () {
                                        checkStatus();
                                    }
                                }]
                        );

                    });
                }

                var sid = 'status-' + uuid;

                container.append('<span id="' + sid + '" style="float: right; margin-top: 9px; margin-right: 4px; color: #4383b4"><image src="/images/ajax-loader.gif"></span>');

                $status = $('#' + sid);

                var lid = 'lastupdate-' + uuid;

                container.append('<h5 id="' + lid + '" style="float: right; margin-top: 9px; margin-right: 4px; color: #4383b4"></h5>');

                $lastupdated = $('#' + lid);

                getLastUpdated();
                checkStatus();
            }

        }


        var _sort = {};
        var _filter = {};

        this.getFilter = function () {
        	if (!jQuery.isEmptyObject(_filter)) {
                var tmp = [];
                var i = 0;
                for (var key in _filter) {
                    if (_filter[key]) {
                        for (var x in _filter[key]) {
                            if (_filter[key][x] != '') {
                                tmp [i++] = key;
                                tmp [i++] = _filter[key][x].condition;
                                if (_filter[key][x].operator == 0) {
                                    tmp[i++] = 'AND';
                                } else {
                                    tmp[i++] = 'OR';
                                }
                                tmp [i++] = _filter[key][x].type;
                                var val;
                                if (typeof _filter[key][x].value === 'Date') {
                                    val = olbius.dateToString(_filter[key][x].value);
                                } else if (_filter[key][x].type === 'numericfilter') {
                                    val = _filter[key][x].value.toString();
                                } else {
                                    val = _filter[key][x].value;
                                }
                                tmp [i++] = val;
                            }
                        }
                    }
                }
                return {filter: tmp};
            }
        	return {};
        }
        
        this.getConfig = function () {

            var paginginformation = $this._grid.jqxGrid('getpaginginformation');
            var pagenum;
            var pagesize;
            if (paginginformation) {
                pagenum = paginginformation.pagenum;
                pagesize = paginginformation.pagesize;
            } else {
                pagenum = 0;
                pagesize = $this._config.pagesize;
            }
            var data = {
                init: _init,
                limit: pagesize,
                offset: pagenum * pagesize,
                service: _service,
                serviceTimestamp: _serviceTimestamp
            };

            data = $.extend(data, _sort);

            data = $.extend(data, $this.getFilter());

            return data;
        };

        this.init = function (b) {
            _init = !!b;
        };

        var adapter = initApdater(this);

        this._config = $.extend({
            width: '100%',
            columnsresize: true,
            pageable: true,
            pagesizeoptions: [20, 30, 50, 100],
            pagesize: 20,
            localization: olbius.getVariable('jqxLocalization'),
            theme: olbius.getVariable('theme'),
            autoheight: true,
            showtoolbar: true
        }, this._config);

        this._config = $.extend(this._config, {
            source: adapter,
            virtualmode: true,
            rendertoolbar: rendertoolbar,
            rendergridrows: function () {
                return adapter.records;
            }
        });

        this._grid.jqxGrid(this._config);

        function sort(event) {
            var sortInfo = event.args.sortinformation;
            var sortdirection = sortInfo.sortdirection.ascending ? "ASC" : "DESC";
            var sortColumnDataField = sortInfo.sortcolumn;
            _sort.sort = sortColumnDataField;
            _sort.sorttype = sortdirection;
            $this._grid.jqxGrid('updatebounddata');
        }

        function filter() {

            var filterinfo = $this._grid.jqxGrid('getfilterinformation');

            _filter = {};

            for (var i = 0; i < filterinfo.length; i++) {
                var tmp = filterinfo[i].filter.getfilters();
                _filter[filterinfo[i].filtercolumn] = tmp;
            }

        }

        this._grid.on("sort", sort);
        this._grid.on("filter", filter);
        this._grid.on("keypress", function (event) {
            if (event.keyCode == 13) {
            	_init = true;
                $this._grid.jqxGrid('updatebounddata');
            }
        });
    }

    olbius.ObiusGridConfig('columns', function (config) {
        var tmp = olbius.getFields(config.columns);
        this._source.datafields = tmp.datafields;
        this._datafields = tmp.data;
        this._columns = tmp.olbiusColumns;
    });

    olbius.ObiusGridConfig('id', function (config) {
        this._grid = appendContainer('#' + olbius.get(config, 'id'));
    });

    olbius.ObiusGridConfig('url', function (config) {
        var url = olbius.get(config, 'url');
        if (!url) {
            url = 'olbiusOlapService';
        }
        this._source.url = url;
    });

    olbius.ObiusGridConfig('olap', function (config) {
        this._serviceName = olbius.get(config, 'olap');
    });

    olbius.ObiusGridConfig('apply', function (config) {
        var $this = this;
        var apply = olbius.get(config, 'apply');

        if (typeof apply == 'function') {
            $this._data = apply($this, $this._popup);
            if ($this._popup) {
                $this._popup.apply(function (popup) {
                    $this._data = apply($this, popup);
                    $this.init(true);
                    $this._grid.jqxGrid('updatebounddata');
                });
            }
        }
    });

    olbius.ObiusGridConfig('title', function (config) {
        this._title = olbius.get(config, 'title');
    });

    olbius.ObiusGridConfig('pdf', function (config) {
        this._pdf = olbius.get(config, 'pdf');
    });

    olbius.ObiusGridConfig('exportFileName', function (config) {
        this._exportFileName = olbius.get(config, 'exportFileName');
    });

    olbius.ObiusGridConfig('excel', function (config) {
        this._excel = olbius.get(config, 'excel');
        if(this._excel == true) {
            this._excel = function (obj) {
                obj._grid.jqxGrid('exportdata', 'xls', obj._exportFileName, true, null, false, 'olbiusOlapExport', null, $.extend({
                    serviceName: obj._serviceName,
                    olapType: 'GRID',
                    olapTitle: obj._title
                }, obj._data, obj.getFilter()));
            }
        }
    });

    olbius.ObiusGridConfig('button', function (config) {
        this._button = olbius.get(config, 'button');
    });

    olbius.OlbiusGrid = OlbiusGrid.prototype;

    olbius.grid = function (config) {
        return new OlbiusGrid(config);
    }

}(window, OlbiusUtil));