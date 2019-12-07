(function (window, olbius) {

    var _readConfig = {};

    var _gridLabel = {
        warnGridLabel: 'Warn',
        okGridLabel: 'OK',
        cancelGridLabel: 'Cancel',
        lastUpdatedGridLabel: 'Last updated',
        aggregateLabel: 'Aggregate',
        pdfLabel :'pdf',
        excelLabel : 'excel',
        configLabel : 'config'
    };

    olbius.treeGridLabel = function(config) {
        _gridLabel = $.extend(_gridLabel, config);
    };

    olbius.OlbiusTreeGridConfig = function (name, func) {
        if (typeof func == 'function') {
            _readConfig[name] = func;
        }
    };

    function appendContainer(id) {

        if ($(id).is('div')) {
            var uuid = olbius.random();
            var txt = olbius.replaceString('<div id="?"></div>', ['treeGrid'], uuid);
            $(id).append(txt);
            return $('#treeGrid-' + uuid);
        } else {
            return appendContainer($(id).parent());
        }
    }

    function TreeGrid(config) {

        this._dataFields = {};

        this._config = {};

        this._configPopup = {};

        this._source = {
            datatype: 'json',
            root: 'data',
            type: 'POST',
            timeout: 100000
        };

        this._data = {};

        this.init(config);

        this._config = $.extend(this._config, {
            localization: olbius.getVariable('jqxLocalization')
        });

        this._grid.jqxTreeGrid(this._config);

    }

    TreeGrid.prototype = {
        init: function (config) {

            this._config = config;

            for (var i in _readConfig) {
                _readConfig[i].call(this, this._config);
            }

            var source = this._source;

            var _num = 0;

            var _hierarchy = this._hierarchy;

            var $this = this;

            config.virtualModeCreateRecords = function (expandedRecord, done) {

                var dataField;

                var adapter = new $.jqx.dataAdapter(source, {

                    formatData: function () {
                        var tmp = {
                            init: false,
                            olapType: 'GRID',
                            serviceName : $this._serviceName
                        };
                        if (expandedRecord == null) {
                            dataField = null;
                            $this._adapter = adapter;
                        } else {
                            dataField = _hierarchy.keyDataField.name;
                            dataField = expandedRecord[dataField];
                            tmp[_hierarchy.parentDataField] = dataField;
                            dataField = expandedRecord[source.hierarchy.keyDataField.name];
                        }
                        tmp = $.extend({}, $this._data, $this.getConfig(), tmp);
                        return tmp;
                    },
                    beforeLoadComplete: function (records) {
                        var data = [];
                        for (var i = 0; i < records.length; i++) {
                            var record = records[i];
                            record.uid = _num;
                            record[source.hierarchy.keyDataField.name] = _num++;
                            if(dataField != null) {
                                record[source.hierarchy.parentDataField.name] = dataField;
                            }
                            data.push(record);
                        }
                        return data;
                    },
                    loadComplete: function (edata) {
                        done(adapter.records);

                        var tmp = {};
                        var i;
                        for (i in edata.datafields) {
                            tmp[edata.datafields[i]] = true;
                        }

                        for (i in $this._columns) {
                            if (tmp[i]) {
                                if ($this._datafields[i].column.hidden == false) {
                                    $this._grid.jqxTreeGrid('showColumn', i);
                                }
                            } else {
                                if (typeof $this._datafields[i].column.hidden == 'undefined' || $this._datafields[i].column.hidden) {
                                    $this._grid.jqxTreeGrid('hideColumn', i);
                                }
                            }
                        }
                    },
                    loadError: function (xhr, status, error) {
                        done(false);
                        throw new Error(error.toString());
                    }
                });
                if (expandedRecord == null) {
                    $this._adapter = adapter;
                }
                adapter.dataBind();
            };
            config.virtualModeRecordCreating = function (record) {
            }

        },
        grid: function () {
            return this._grid;
        },
        val: function (id) {
            if (this._popup) {
                return this._popup.val(id);
            } else {
                return null;
            }
        }
    };

    olbius.OlbiusTreeGridConfig('columns', function (config) {
        var tmp = olbius.getFields(config.columns);
        this._source.datafields = tmp.datafields;
        this._datafields = tmp.data;
        this._columns = tmp.olbiusColumns;
    });

    olbius.OlbiusTreeGridConfig('id', function (config) {
        this._grid = appendContainer('#' + olbius.get(config, 'id'));
    });

    olbius.OlbiusTreeGridConfig('url', function (config) {
        var url = olbius.get(config, 'url');
        if(!url) {
            url = 'olbiusOlapService';
        }
        this._source.url = url;
    });

    olbius.OlbiusTreeGridConfig('olap', function (config) {
        this._serviceName = olbius.get(config, 'olap');
    });

    olbius.OlbiusTreeGridConfig('hierarchy', function (config) {
        this._hierarchy = olbius.get(config, 'hierarchy');

        if(!this._hierarchy.parentDataField) {
            this._hierarchy.parentDataField = 'parentDataField';
        }

        this._source.datafields.splice(0, 0, {
            name: 'dataField' , type: 'number'
        });

        this._source.datafields.splice(1, 0, {
            name: 'parentDataField' , type: 'number'
        });

        this._source.hierarchy = {
            keyDataField : {name: 'dataField'},
            parentDataField: {name: 'parentDataField'}
        };

    });
    olbius.OlbiusTreeGridConfig('popup', function (config) {
        this._configPopup = olbius.get(config, 'popup');
        if (this._configPopup) {
            this._popup = olbius.popup(this._configPopup);
        }
    });

    olbius.OlbiusTreeGridConfig('apply', function (config) {
        var $this = this;
        var apply = olbius.get(config, 'apply');

        if(typeof apply == 'function') {
            $this._data = apply($this, $this._popup);
            if($this._popup) {
                $this._popup.apply(function (popup) {
                    $this._data = apply($this, popup);
                    $this._adapter.dataBind();
                });
            }
        }
    });

    olbius.OlbiusTreeGridConfig('button', function (config) {
        this._button = olbius.get(config, 'button');
    });

    olbius.OlbiusTreeGridConfig('rendertoolbar', function (config) {

        var _service;

        var _serviceTimestamp;

        _service = olbius.get(this._config, 'service');

        var $update;
        var $status;
        var $lastupdated;
        var _render = false;
        var _updating = false;
        var _updated = false;

        var title = olbius.get(config, 'title');
        var $this = this;

        function rendertoolbar(toolbar) {

            if (!title && !$this._popup && !$this._pdf && !$this._excel && !_service) {
                return;
            }

            if (_render) {
                return;
            } else {
                _render = true;
            }

            var container = $("<div style='margin: 3px;'></div>");
            var span = $("<h4 style='float: left; margin-top: 5px; margin-right: 4px; color: #4383b4'>" + title + " </h4>");
            toolbar.append(container);
            container.append(span);
            var uuid = olbius.random();
            var id = 'button-' + uuid;
            var pid = 'pbutton-' + uuid;
            var eid = 'ebutton-' + uuid;

            if ($this._popup) {
                container.append('<button id="' + id + '" title="'+_gridLabel.configLabel+'" class="btn btn-mini btngridsetting"><i class="fa-cogs"></i></button>');
                $('#' + id).on('click', function () {
                    $this._popup.open();
                });
            }

            if (typeof $this._pdf == 'function') {
                container.append('<button id="' + pid + '" title="'+_gridLabel.pdfLabel+'" class="btn btn-mini btngridsetting"><i class="fa-print"></i></button>');
                $('#' + pid).on('click', function () {
                    $this._pdf($object);
                });
            }

            if (typeof $this._excel == 'function') {
                container.append('<button id="' + eid + '" title="'+_gridLabel.excelLabel+'" class="btn btn-mini btngridsetting"><i class="fa-file-excel-o"></i></button>');
                $('#' + eid).on('click', function () {
                    $this._excel($object);
                });
            }

            if (_service) {
                if($this._button) {
                    var uid = 'update-' + uuid;

                    container.append('<button id="' + uid + '" title="'+_gridLabel.aggregateLabel+'" class="btn btn-mini btngridsetting"><i class="fa-play"></i></button>');

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
            if($update) {
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
                        if($update) {
                            $update.show();
                        }
                        //$lastupdated.show();
                    } else {
                        $status.hide();
                        //$lastupdated.hide();
                        if($update) {
                            $update.show();
                        }
                    }
                }
            });
            if (_updating) {
                _updated = true;
                _updating = false;
                //$this._grid.jqxTreeGrid('updateBoundData');
                $this._adapter.dataBind();
            }
        }

        config.showToolbar=true;
        config.renderToolbar = rendertoolbar;

        this.getConfig = function() {
            return  {
                service: _service,
                serviceTimestamp: _serviceTimestamp
            }
        }

    });

    olbius.OlbiusTreeGrid = TreeGrid.prototype;

    olbius.treeGrid = function (config) {
        return new TreeGrid(config);
    }

}(window, OlbiusUtil));