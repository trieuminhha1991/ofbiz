(function (window, olbius) {

    var _readConfig = {};

    olbius.OlbiusChartConfig = function (name, func) {
        if (typeof func == 'function') {
            _readConfig[name] = func;
        }
    };

    function appendContainer(id, width, height) {

        if ($(id).is('div')) {
            var uuid = 'container-' + olbius.random();

            $(id).append('<div id="' + uuid + '" class="olbiusChartContainer" style="width: ' + width + '; margin: 0 auto;position: relative">');

            containerSize('#' + uuid, height);

            return uuid;
        } else {
            return appendContainer($(id).parent(), width, height);
        }

    }

    function containerSize(id, height) {
        var obj = $(id);
        if (obj.parents('.grid-stack-item-content').length == 0) {
            if (height && typeof height == 'number') {
                $(id).height($(id).width() * height);
            } else {
                $(id).height($(id).width() * 0.4);
            }
        } else {
            var fh = obj.parents('.grid-stack-item-content').height();
            obj.height(fh - 2);
        }
    }

    function appendLoading(id, offset) {
        var uuid = 'wait-spinner-' + olbius.random();
        var height = $(id).height() / 2 - 20;
        var text = '<div id="' + uuid + '" style="display: none;z-index: 99999;position: absolute;">' +
            '<div style="background: url(/images/spinner.gif) no-repeat;height: 36px;width: 36px;"></div></div>';

        var $id = $(id);
        $id.append(text);
        $id = $('#' + uuid);

        if (!offset) {
            $id.css('top', '48%');
            $id.css('left', '48%');
        } else {
            $id.show();
        }
        return uuid;
    }

    olbius.OlbiusChartConfig('apply', function (config) {
        var $this = this;
        var apply = olbius.get(config, 'apply');

        if(typeof apply == 'function') {
        	$this._data = apply($this, $this._popup);
        	$this.update = function () {
        		$this._data = apply($this, $this._popup);
        	}
            if($this._popup) {
                $this._popup.apply(function (popup) {
                    $this.update();
                });
            }
        }
    });
    
    olbius.OlbiusChartConfig('height', function (config) {
    	this._height = olbius.get(config, 'height');
    });

    olbius.OlbiusChartConfig('url', function (config) {
        var url = olbius.get(config, 'url');
        if(!url) {
            url = 'olbiusOlapService';
        }
        this._url = url;
    });

    olbius.OlbiusChartConfig('olap', function (config) {
        this._serviceName = olbius.get(config, 'olap');
    });

    olbius.OlbiusChartConfig('chartRender', function (config) {
        this._render = olbius.get(config, 'chartRender');

        if(typeof this._render != 'function') {
            this._render = function (data, obj) {
                while (obj._chart.series.length > 0) {
                    obj._chart.series[0].remove(true);
                }

                return true;
            }
        }
    });

    function OlbiusChart(config) {

        var $this = this;

        this._data = {};

        this._config = config;

        this._configPopup = olbius.get(config, 'popup');

        if (this._configPopup) {
            this._popup = olbius.popup(this._configPopup);
            this._config = $.extend(this._config, {
                exporting: {
                    buttons: {
                        /*printButton: {
                        	symbol: "url(/aceadmin/assets/images/download-icon.png)",
                        	onclick: function () {
	                        },
	                         _titleKey:"download"
                         },*/
                        'config': {
                            align: 'left',
                            x: 5,
                            onclick: function () {
                                $this._popup.open();
                            },
                            symbol: 'url(/aceadmin/assets/images/cogs_icon.png)',
                            _titleKey:"config"
                        },
                        contextButton: {
                            enabled: false
                        }
                    }
                }
            });
        } else {
            this._config = $.extend(this._config, {
                exporting: {
                    buttons: {
                        contextButton: {
                            enabled: false
                        }
                    }
                }
            });
        }

        var _service;

        var _serviceTimestamp;

        _service = olbius.get(this._config, 'service');

        for (var i in _readConfig) {
            _readConfig[i].call(this, this._config);
        }

        this._config = $.extend(this._config, {
            loading: {
                hideDuration: 100,
                showDuration: 100
            }
        });

        var containerUuid = appendContainer($('#' + olbius.get(this._config, 'id')), '100%', this._height);

        var $containerUuid= $('#'+containerUuid);

        $containerUuid.highcharts(this._config);

        this._chart = $containerUuid.highcharts();

        this.reszie = function() {
            setTimeout(function () {
                containerSize($containerUuid, $this._height);
                $this._chart.reflow();
            }, 200);
        };
        
        $(window).bind('resize', function () {
            $this.reszie();
        });
        if(typeof Dashboard != 'undefined') {
            if(!Dashboard.gridStack) {
                Dashboard.init();
            }
            Dashboard.gridStack.on('resizestop', function() {
                $this.reszie();
            });
        }

        var idLoading = appendLoading($containerUuid);

        var _loading = $('#' + idLoading);

        var flag;

        function run() {

            jQuery.ajax({
                url: $this._url,
                async: true,
                type: 'POST',
                beforeSend: function () {
                    flag = false;
                    $this._chart.hideLoading();
                    _loading.show();
                },
                data: $.extend({
                    serviceName : $this._serviceName
                }, $this._data, {
                    service: _service,
                    serviceTimestamp: _serviceTimestamp
                }),
                success: function (data) {
                    if(typeof $this._render == 'function') {
                        flag = $this._render(data, $this);
                    }
                },
                complete: function () {
                    _loading.hide();
                    if (flag) {
                        $this._chart.showLoading();
                    }
                }
            });
        }

        var fn = this.update;
        
        this.update = function() {
        	if (typeof fn == "function") fn();
            run();
        };

        run();
    }

    olbius.chart = function (config) {
        return new OlbiusChart(config);
    };

    var chartRender = {};

    olbius.getChartRender = function (name) {
        return chartRender[name];
    };

    olbius.putChartRender = function (name, func) {
        if(typeof func == 'function') {
            chartRender[name] = func;
        }
    };

    olbius.putVariable('tickInterval', 10);

    olbius.getTickIntervalSize = function (length) {
        var tmp = length / olbius.getVariable('tickInterval');
        if (tmp < 1) {
            return 1;
        } else {
            return Math.ceil(tmp);
        }
    };

    olbius.getFormaterAxisLabel = function (dateType) {
        if (dateType == 'DAY') {
            return function () {
                return Highcharts.dateFormat('%a, %b %e, %Y', new Date(this.value));
            }
        }
        if (dateType == 'WEEK') {
            return function () {
                var tmp = (this.value + '').split('-');
                return 'Week ' + tmp[1] + ', ' + tmp[0];
            }
        }
        if (dateType == 'MONTH') {
            return function () {
                var tmp = this.value + '-01';
                return Highcharts.dateFormat('%b %Y', new Date(tmp));
            }
        }
        if (dateType == 'QUARTER') {
            return function () {
                var tmp = (this.value + '').split('-');
                return 'Quarter ' + tmp[1] + ', ' + tmp[0];
            }
        }

        return function () {
            return this.value;
        }
    };

    olbius.putChartRender('defaultLineFunc', function(data, obj) {
        var tmp = {
            labels: {
                enabled: true
            },
            categories: data.xAxis,
            tickInterval : olbius.getTickIntervalSize(data.xAxis.length)
        };

        if (data.dateType) {
            tmp['labels']['formatter'] = olbius.getFormaterAxisLabel(data.dateType);
        }

        obj._chart.xAxis[0].update(tmp, false);

        while (obj._chart.series.length > 0) {
            obj._chart.series[0].remove(false);
        }

        var color = 0;
        var marker = 0;
        for (var i in data.yAxis) {
            obj._chart.addSeries({
                name: i,
                data: data.yAxis[i],
                color: Highcharts.getOptions().colors[color++],
                marker: {
                    symbol: Highcharts.getOptions().symbols[marker++]
                }
            }, false);
        }

        obj._chart.redraw();

        return !!(data.xAxis && data.xAxis.length == 0);
    });

    olbius.putChartRender('defaultColumnFunc', function(data, obj) {
        var tmp = {
            labels: {
                enabled: true
            },
            categories: data.xAxis
        };

        obj._chart.xAxis[0].update(tmp, false);

        while (obj._chart.series.length > 0) {
            obj._chart.series[0].remove(false);
        }

        var color = 0;
        for (var i in data.yAxis) {
            obj._chart.addSeries({
                name: i,
                data: data.yAxis[i],
                color: Highcharts.getOptions().colors[color++]
            }, false);
        }

        obj._chart.redraw();

        return !!(data.xAxis && data.xAxis.length == 0);
    });

    olbius.putChartRender('defaultBarFunc', function(data, obj) {

        var tickSize = olbius.getTickIntervalSize(data.xAxis.length);

        obj._chart.xAxis[0].update({
            categories: data.xAxis,
            tickInterval: tickSize
        }, false);

        obj._chart.xAxis[1].update({
            categories: data.xAxis,
            tickInterval: tickSize

        }, false);

        while (chart.series.length > 0) {
            chart.series[0].remove(false);
        }

        var _max = [];
        var color = 0;
        for (var i in data.yAxis) {
            obj._chart.addSeries({name: i, data: data.yAxis[i], color: Highcharts.getOptions().colors[color++]}, false);
            var max = Math.max.apply(Math, data.yAxis[i]);
            if (max <= 0) {
                max = Math.min.apply(Math, data.yAxis[i]);
                max = -max;
            }
            _max.push(max);
        }
        var max = Math.max.apply(Math, _max) + 1;

        obj._chart.yAxis[0].update({min: -max, max: max}, false);

        obj._chart.redraw();

        return !!(data.xAxis && data.xAxis.length == 0);
    });

    olbius.putChartRender('defaultPieFunc', function(data, obj) {

        var array = [];

        for (var i in data.yAxis) {
            var tmp = [];
            tmp.push(i);
            tmp.push(data.yAxis[i][0]);
            array.push(tmp);
        }
        obj._chart.series[0].setData(array);

        return !!(data.xAxis && data.xAxis.length == 0);
    });

}(window, OlbiusUtil));
