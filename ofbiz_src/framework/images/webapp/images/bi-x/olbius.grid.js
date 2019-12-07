(function (window, OLBIUS) {

    function appendContainer(id) {

        if($(id).is('div')) {
            var uuid = 'grid-' + OLBIUS.generateUUID();

            $(id).append('<div id="' + uuid + '" class="olbiusGridContainer">');

            return uuid;
        } else {
            return appendContainer($(id).parent());
        }
    }

    function olbiusOLapGrid(container, config, configPopup, url, async) {
        var $object;

        var $popup;

        var $grid;

        var $fn_update;

        var _defaultVal = OLBIUS.getDefaultValue(configPopup);

        if(typeof container == 'object') {
            $grid = container;
        } else {
            var containerUuid = appendContainer($('#' + container));
            $grid = $('#' + containerUuid);
        }

        var _datafields = {};

        var _title = config.title;

        delete config.title;

        var _init = false;

        var _initPage = true;

        var _dataDefault;

        var _id;

        var _data;

        var _async = async;

        var _url = url;

        var _pageSize = 20;

        if (config.pagesize) {
            _pageSize = config.pagesize;
            delete config.pagesize;
        }

        var _datetype;

        var $datetype;

        var $fn_print;

        var $fn_excel;

        var $lastupdated;

        var $update;

        var $status;

        var $funEdit;

        var $endupdate = false;

        var $updating = false;

        var $service = config.service;

        var $serviceTimestamp;

        delete config.service;

        var _columns = [];

        var _status = {};

        var $fn_popupOK;

        var _hideColumns = {};

        var _sort = {};

        var _filter = {};

        var _showtoolbar = config.showtoolbar;
        if(_showtoolbar != false) {
            _showtoolbar = true;
        }

        var _configGrid = {
            width: '100%',
            columnsresize: true,
            pageable: true, virtualmode: true,
            rendergridrows: rendergridrows,
            pagesizeoptions: [_pageSize],
            pagesize: _pageSize,
            localization: getLocalization(),
            theme: OLBIUS.getTheme()
        };

        if(_showtoolbar) {
            _configGrid.rendertoolbar = rendertoolbar;
            _configGrid.showtoolbar = true;
        }

        if(!config.height) {
            _configGrid.autoheight=true;
        }

        for (var i in config) {
            if (i != 'columns') {
                _configGrid[i] = config[i];
            }
        }

        var tmpData;

        for (var i in config.columns) {
            var element = config.columns[i];
            var field = element.datafield;
            var type = element.type;

            var olapcellrender = element.olapcellrenderer;

            var olapaggregates = element.olapaggregatesrenderer;

            element = $.extend(element, {
                renderer: columnsrenderer
            });

            if (type === 'olapdate') {
                element.cellsrenderer = rederdatecolumn;
            }

            if (olapcellrender) {
                element.cellsrenderer = olapcellrender($grid);
            }

            if (olapaggregates) {
                element.aggregatesrenderer = olapaggregates($grid);
            }

            if (element.hidden) {
                _hideColumns[field] = true;
            }

            element.hidden = true;

            _datafields[field] = {
                datafield: {
                    name: field,
                    type: type
                },
                column: element,
                status: false
            }

            _columns.push(element);

            _status[field] = true;
        }

        function load(data) {

            var localdata = data.data;

            if (!_init) {
                _id = data.id;
                var source = {
                    localdata: localdata,
                    id: _id,
                    totalrecords: data.totalsize,
                    datatype: 'json',
                    datafields: loadDatafields(data.datafields)
                };
                var dataAdapter = new $.jqx.dataAdapter(source);
                if (_initPage) {
                    _configGrid.source = dataAdapter;
                    _configGrid.columns = _columns;
                } else {
                    $grid.jqxGrid('source', dataAdapter);
                }
                loadColumns(data.datafields);
            }

            return localdata;
        }

        function loadDatafields(datafields) {
            var tmp = [];
            for (var i in datafields) {
            	if(datafields[i] != null && datafields[i] != undefined){
            		if (_hideColumns[datafields[i]]) {
                        continue;
                    }
            		if(_datafields[datafields[i]] != null && datafields[i] != undefined){
            			tmp.push(_datafields[datafields[i]].datafield);
            			_datafields[datafields[i]].column.hidden = false;
            		}
            	}
            }
            return tmp;
        }

        function loadColumns(columns) {
            clear();
            for (var i in columns) {
            	if(_datafields[columns[i]] != null && _datafields[columns[i]] != undefined){
            		_datafields[columns[i]].status = true;
            	}
            }
        }

        function clear() {
            for (var i in _datafields) {
                _datafields[i].status = false;
            }
        }

        function showColumns() {
            for (var i in _datafields) {
                if (_hideColumns[i]) {
                    continue;
                }
                if (_datafields[i].status && !_status[i]) {
                    $grid.jqxGrid('showcolumn', i);
                    _status[i] = true;
                }
                if (!_datafields[i].status && _status[i]) {
                    $grid.jqxGrid('hidecolumn', i);
                    _status[i] = false;
                }
            }
        }

        function rendertoolbar(toolbar) {

            if (!_title && !configPopup && !$fn_print && $fn_excel && $service) {
                return;
            }

            var container = $("<div style='margin: 3px;'></div>");
            var span = $("<h4 style='float: left; margin-top: 5px; margin-right: 4px; color: #4383b4'>" + _title + " </h4>");
            toolbar.append(container);
            container.append(span);
            var uuid = OLBIUS.generateUUID();
            var id = 'button-' + uuid;
            var pid = 'pbutton-' + uuid;
            var eid = 'ebutton-' + uuid;

            if (configPopup) {
                container.append('<button id="' + id + '" class="btn btn-mini btngridsetting"><i class="fa-cogs"></i></button>');
                $('#' + id).on('click', function () {
                    if (!$popup) {
                        initPopup();
                    } else {
                        $popup.open();
                    }
                });
            }

            if ($fn_print) {
                container.append('<button id="' + pid + '" class="btn btn-mini btngridsetting"><i class="fa-print"></i></button>');
                $('#' + pid).on('click', function () {
                    $fn_print($object);
                });
            }

            if ($fn_excel) {
                container.append('<button id="' + eid + '" class="btn btn-mini btngridsetting"><i class="fa-file-excel-o"></i></button>');
                $('#' + eid).on('click', function () {
                    $fn_excel($object);
                });
            }

            if ($service) {
                var uid = 'update-' + uuid;

                container.append('<button id="' + uid + '" class="btn btn-mini btngridsetting"><i class="fa-play"></i></button>');

                $update = $('#' + uid);

                $update.on('click', function () {
                    bootbox.dialog(OLBIUS.getWarnGridLable(),
                        [{
                            "label": OLBIUS.getOkGridLable(),
                            "class": "btn-primary btn-small icon-ok",
                            "callback": function () {
                                jQuery.ajax({
                                    url: 'runOlapService',
                                    async: true,
                                    type: 'POST',
                                    data: {'service': $service},
                                    success: function (data) {
                                        getUpdating();
                                        setTimeout(checkStatus, 10000);
                                    }
                                });
                            }
                        },
                            {
                                "label": OLBIUS.getCancelGridLable(),
                                "class": "btn-danger btn-small icon-remove",
                                "callback": function () {
                                    checkStatus();
                                }
                            }]
                    );

                });

                var sid = 'status-' + uuid;

                container.append('<span id="' + sid + '" style="float: right; margin-top: 9px; margin-right: 4px; color: #4383b4"><image src="/images/ajax-loader.gif"></span>');

                $status = $('#' + sid);

                var lid = 'lastupdate-' + uuid;

                container.append('<h5 id="' + lid + '" style="float: right; margin-top: 9px; margin-right: 4px; color: #4383b4"></h5>');

                $lastupdated = $('#' + lid);

                checkStatus();
            }

        }

        function checkStatus() {
            jQuery.ajax({
                url: 'getStatusOlapServices',
                async: true,
                type: 'POST',
                data: {'service': $service},
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
            $lastupdated.hide();
            $update.hide();
            $updating = true;
        }

        function getLastUpdated() {
            jQuery.ajax({
                url: 'getLastupdatedOlapServices',
                async: true,
                type: 'POST',
                data: {'service': $service},
                success: function (data) {
                    if (data.lastupdated > 0) {
                        var tmp = $.jqx.dataFormat.formatdate(new Date(data.lastupdated), OLBIUS.getDateTimeFullFormat());
                        $serviceTimestamp = data.lastupdated;
                        $lastupdated.html(OLBIUS.getLastupdatedGridLable() + ': ' + tmp);
                        $status.hide();
                        $update.show();
                        $lastupdated.show();
                    } else {
                        $status.hide();
                        $lastupdated.hide();
                        $update.show();
                    }
                }
            });
            if ($updating) {
                $endupdate = true;
                $updating = false;
                run();
            }
        }

        function rederdatecolumn(row, column, value) {
            if ($datetype == 'DAY') {
                return '<div>' + $.jqx.dataFormat.formatdate(new Date(value), OLBIUS.getDateTimeFormat()) + '</div>';
            }
            if ($datetype == 'WEEK') {
                var tmp = (value + '').split('-');
                return '<div>' + OLBIUS.getWeekLable() + tmp[1] + ', ' + tmp[0] + '</div>';
            }
            if ($datetype == 'MONTH') {
                var tmp = value + '-01';
                return '<div>' + $.jqx.dataFormat.formatdate(new Date(tmp), OLBIUS.getMonthFormat()) + '</div>';
            }
            if ($datetype == 'QUARTER') {
                var tmp = (value + '').split('-');
                return '<div>' + OLBIUS.getQuarterLable() + tmp[1] + ', ' + tmp[0] + '</div>';
            }

            return '<div>' + value + '</div>';
        }

        function columnsrenderer(value) {
            return '<div style="text-align: left; margin-top: 7px; margin-left: 5px;">' + '<b>' + value + '</b>' + '</div>';
        }

        function rendergridrows(params) {
            var paginginformation = $grid.jqxGrid('getpaginginformation');
            var pagenum = paginginformation.pagenum;
            var pagesize = paginginformation.pagesize;
            if (pagenum == 0 && !$endupdate) {
                return _dataDefault;
            } else {
                var tmp;
                jQuery.ajax({
                    url: _url,
                    async: false,
                    type: 'POST',
                    beforeSend: function () {
                    },
                    data: $.extend(_data, {
                        limit: pagesize,
                        offset: params.startindex,
                        service: $service,
                        serviceTimestamp: $serviceTimestamp
                    }),
                    success: function (data) {
                        tmp = load(data);
                        if (pagenum == 0) {
                            $endupdate = false;
                            _dataDefault = tmp;
                        }
                    },
                    complete: function () {
                    }
                });

                return tmp;
            }
        }

        function initGrid() {

        }

        function sort(event) {
            var sortInfo = event.args.sortinformation;
            var sortdirection = sortInfo.sortdirection.ascending ? "ASC" : "DESC";
            var sortColumnDataField = sortInfo.sortcolumn;
            _sort.sort = sortColumnDataField;
            _sort.sorttype = sortdirection;
            run();
        }

        function filter(event) {

            var filterinfo = $grid.jqxGrid('getfilterinformation');

            _filter = {};

            for (var i = 0; i < filterinfo.length; i++) {
                var tmp = filterinfo[i].filter.getfilters();
                _filter[filterinfo[i].filtercolumn] = tmp;
            }

        }

        function run(a) {
            $fn_update($object);
            _data = $.extend(_data, _sort);
            if (!jQuery.isEmptyObject(_filter)) {
                var tmp = [];
                var i = 0;
                for (var key in _filter) {
                    if(_filter[key]) {
                        for(var x in _filter[key]) {
                            if (_filter[key][x] != '') {
                                tmp [i++] = key;
                                tmp [i++] = _filter[key][x].condition;
                                if(_filter[key][x].operator == 0) {
                                    tmp[i++] = 'AND';
                                } else {
                                    tmp[i++] = 'OR';
                                }
                                tmp [i++] = _filter[key][x].type;
                                var val;
                                if(typeof _filter[key][x].value === 'Date') {
                                    val = OLBIUS.dateToString(_filter[key][x].value);
                                } else if(_filter[key][x].type === 'numericfilter') {
                                    val = _filter[key][x].value.toString();
                                } else {
                                    val = _filter[key][x].value;
                                }
                                tmp [i++] = val;
                            }
                        }
                    }
                }
                _data = $.extend(_data, {filter: tmp});
            }
            var tmp;
            if (a) {
                tmp = false;
            } else {
                tmp = _async;
            }
            jQuery.ajax({
                url: _url,
                async: tmp,
                type: 'POST',
                beforeSend: function () {
                    _init = false;
                    if (_initPage) {
                        OLBIUS.popupLoading().open();
                    } else {
                        $endupdate = false;
                        $grid.jqxGrid('gotopage', 0);
                        $grid.jqxGrid('showloadelement');
                    }
                },
                data: $.extend(_data, {
                    limit: _pageSize,
                    offset: 0,
                    init: true,
                    service: $service,
                    serviceTimestamp: $serviceTimestamp
                }),
                success: function (data) {
                    tmpData = data.data;
                    _dataDefault = load(data);
                    $datetype = _datetype;
                    if ($funEdit) {
                        $funEdit(data);
                    }
                    if (_initPage) {
                        $grid.jqxGrid(_configGrid);
                        $grid.on('bindingcomplete', function () {
                            if (!_init) {
                                showColumns();
                            }
                        });
                        $grid.on("sort", sort);
                        $grid.on("filter", filter);
                        $grid.on("keypress", function (event) {
                            if (event.keyCode == 13) {
                                run();
                            }
                        });
                    } else {
                        $grid.jqxGrid('updatebounddata');
                    }

                    delete  _configGrid.rendertoolbar;
                },
                complete: function () {
                    _init = true;
                    if (_initPage) {
                        OLBIUS.popupLoading().close();
                        _initPage = false;
                    } else {
                        $grid.jqxGrid('hideloadelement');
                    }
                    $(window).resize();
                }
            });
        }

        function initPopup() {
            $popup = OLBIUS.initPopup(configPopup, $fn_popupOK);
        }

        function grid() {
        }

        grid.prototype = {
            popupOK: function (funcOK) {
                $fn_popupOK = funcOK;
            },
            runAjax: function (a) {
                $datetype = _datetype;
                run(a);
            },
            val: function (id) {
                if (!$popup) {
                    return _defaultVal[id];
                } else {
                    return $popup.val(id);
                }
            },
            update: function (data, datetype) {
                _data = data;
                if (datetype) {
                    _datetype = datetype;
                } else {
                    _datetype = 'DAY';
                }
            },
            funcUpdate: function (func) {
                if (func) {
                    $fn_update = func;
                } else {
                    $fn_update($object);
                }
            },
            init: function (funcOK, funPrint, funExcel) {
                $fn_popupOK = funcOK;
                $fn_print = funPrint;
                $fn_excel = funExcel;
                initGrid();
                run();
            },
            getAllData: function () {
                var tmpdata;
                jQuery.ajax({
                    url: _url,
                    async: false,
                    type: 'POST',
                    data: _data,
                    success: function (data) {
                        tmpdata = data.data;
                    },
                });
                return tmpdata;
            },
            getGrid: function () {
                return $grid;
            },
            getRunData: function (fn) {
                $funEdit = fn;
            }
        }
        $object = new grid;
        return $object;

    }

    var okGridLable = null;
    var cancelGridLable = null;
    var warnGridLable = null;
    var lastupdatedGridLable = null;
    var weekLable = null;
    var quarterLable = null;

    OLBIUS.olbius().setWeekLable = function (lable) {
        weekLable = lable;
    }

    OLBIUS.olbius().getWeekLable = function () {
        return weekLable;
    }

    OLBIUS.olbius().setQuarterLable = function (lable) {
        quarterLable = lable;
    }

    OLBIUS.olbius().getQuarterLable = function () {
        return quarterLable;
    }

    OLBIUS.olbius().setOkGridLable = function (lable) {
        okGridLable = lable;
    }

    OLBIUS.olbius().getOkGridLable = function () {
        return okGridLable;
    }

    OLBIUS.olbius().setCancelGridLable = function (lable) {
        cancelGridLable = lable;
    }

    OLBIUS.olbius().getCancelGridLable = function () {
        return cancelGridLable;
    }

    OLBIUS.olbius().setWarnGridLable = function (lable) {
        warnGridLable = lable;
    }

    OLBIUS.olbius().getWarnGridLable = function () {
        return warnGridLable;
    }

    OLBIUS.olbius().setLastupdatedGridLable = function (lable) {
        lastupdatedGridLable = lable;
    }

    OLBIUS.olbius().getLastupdatedGridLable = function () {
        return lastupdatedGridLable;
    }

    OLBIUS.olbius().oLapGrid = function (container, config, configPopup, url, async) {
        return olbiusOLapGrid(container, config, configPopup, url, async);
    };
}(window, OLBIUS));
