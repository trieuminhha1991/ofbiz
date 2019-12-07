(function (window, OLBIUS) {

    var theme = OLBIUS.getTheme();

    var width_default = 200;
    var height_default = 25;

    function appendContainer(id, width, height) {

        if($(id).is('div')) {
            var uuid = 'container-' + OLBIUS.generateUUID();

            $(id).append('<div id="' + uuid + '" class="olbiusChartContainer" style="width: ' + width + '; margin: 0 auto;position: relative">');

            containerSize('#' + uuid, height);

            return uuid;
        } else {
            return appendContainer($(id).parent(), width, height);
        }

    }

    function containerSize(id, height) {
		var obj = $(id);
		if(obj.parents('.grid-stack-item-content').length == 0) {
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


    function olbiusOLapChart(container, config, configPopup, url, async, removeSeries, func, height) {

        var _chart;
        var _popup;
        var _chartConfig = config;
        var _popupConfig = configPopup;
        var _url = url;
        var _async = async;
        var _removeFlag = removeSeries;
        var defaultFunc;
        var $loading;
        var flag;

        var $fn_update;
        var $fn_popupOK;
        var $object;
        var _init;

        var _data;
        var _datetype;

        var $service = config.service;
        delete config.service;
        var $serviceTimestamp;

        var _defaultVal = OLBIUS.getDefaultValue(_popupConfig);

        if (func) {
            defaultFunc = func;
        } else {
            defaultFunc = function (data, chart, datetype, removeSeries, flagFunc, oLap) {

                while (chart.series.length > 0) {
                    chart.series[0].remove(true);
                }

                flagFunc();
            }
        }

        var containerUuid = appendContainer($('#' + container), '100%', height);
        var $containerUuid = $('#' + containerUuid);

        function initPopup() {
            _popup = OLBIUS.initPopup(_popupConfig, $fn_popupOK);
        }

        function popupAddOK() {
            _popup.addOK(function () {
                $fn_popupOK();
                _popup.close();
            });
        }

        function initChart() {
            if (_popupConfig) {
                _chartConfig['exporting'] = {
                    buttons: {
                        /*printButton: {
                            symbol: "url(/aceadmin/assets/images/print_icon.png)",
                            onclick: function () {
                                _reszie();
                                this.print()
                            }
                        },*/
                        'config': {
                            align: 'left',
                            x: 5,
                            onclick: function () {
                                if (!_popup) {
                                    initPopup();
                                } else {
                                    _popup.open();
                                }
                            },
                            symbol: 'url(/aceadmin/assets/images/cogs_icon.png)',
                            _titleKey: "config"
                        },
                        contextButton: {
                            enabled: false
                        }
                    }
                };
            } else {
                _chartConfig['exporting'] = {
                    buttons: {
                        contextButton: {
                            enabled: false
                        }
                    }
                }
            }
            _chartConfig['loading'] = {
                hideDuration: 100,
                showDuration: 100
            };

            $containerUuid.highcharts(_chartConfig);

            _chart = $containerUuid.highcharts();

            /*$('#sidebar-collapse,#sidebar-collapse-bottom').click(function() {
             containerSize($containerUuid);
             setTimeout(function(){
             _chart.reflow();
             }, 200);
             });*/
            $(window).bind('resize', function () {
            	_reszie();
            });
        	if(typeof Dashboard != 'undefined') {
                if(!Dashboard.gridStack) {
                    Dashboard.init();
                }
            	Dashboard.gridStack.on('resizestop', function() {
        			_reszie(true);
            	});
            }

            var idLoading = OLBIUS.appendLoading($containerUuid);

            $loading = $('#' + idLoading);

            _init = true;
        }

        function _reszie(flag) {
    		setTimeout(function () {
    			containerSize($containerUuid, height);
                _chart.reflow();
            }, 200);
        }
        
        function run(a) {

            $fn_update($object);

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
                    flag = false;

                    _chart.hideLoading();

                    if (OLBIUS.popupLoading().isOpen()) {
                        OLBIUS.popupLoading().open();
                    } else {
                        $loading.show();
                    }

                },
                data: $.extend(_data, {
                    service: $service,
                    serviceTimestamp: $serviceTimestamp
                }),
                success: function (data) {
                    defaultFunc(data, _chart, _datetype, _removeFlag, function () {
                        flag = true;
                    }, $object);
                },
                complete: function () {
                    if (OLBIUS.popupLoading().isOpen()) {
                        OLBIUS.popupLoading().close();
                    } else {
                        $loading.hide();
                    }

                    if (flag) {
                        _chart.showLoading();
                    }
                }
            });
        }

        function chart() {
            $object = this;
        }

        chart.prototype = {
            popupOK: function (funcOK) {
                $fn_popupOK = funcOK;
            },
            runAjax: function (a) {
                if (_init) {
                    run(a);
                }
            },
            val: function (id) {
                if (!_popup) {
                    return _defaultVal[id];
                } else {
                    return _popup.val(id);
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
                    $fn_update($fn_update);
                }
            },
            resetTitle: function (_newTitle) {
                _chart.setTitle({text: _newTitle});
            },
            resize:function() {
            	 _reszie();
            },
            init: function (funcOK) {
                $fn_popupOK = funcOK;
                //run();
                if ($(window).scrollTop() + $(window).height() >= $containerUuid.offset().top) {
                    initChart();
                    run();
                }
                $(window).scroll(function () {
                    if (!_init) {
                        if ($(window).scrollTop() + $(window).height() >= $containerUuid.offset().top) {
                            initChart();
                            run();
                        }
                    }

                });
            },
            getChart: function () {
                return _chart;
            }
        }

        return new chart;
    }

    OLBIUS.olbius().getDefaultValue = function (config) {
        var _defaultVal = {};
        if (config) {
            for (var element in config) {
                var params = config[element].params;
                var val = config[element].val;
                if (val) {
                    _defaultVal[params[0].id] = val;
                } else {
                    var data = params[0].data;
                    var index = params[0].index;
                    var value = params[0].value;
                    if (value) {
                        _defaultVal[params[0].id] = value;
                    } else {
                        if (!index) {
                            index = 0;
                        }
                        if (data) {
                        	if(typeof index == 'object') {
                        		_defaultVal[params[0].id] = [];
                        		for(var i in index) {
                        			if (data[i].text) {
                        				_defaultVal[params[0].id].push(data[i].value);
                                    } else {
                                    	_defaultVal[params[0].id].push(data[i]);
                                    }
                        			
                        		}
                        	} else if (data.length > 0 && data[index].text) {
                                _defaultVal[params[0].id] = data[index].value;
                            } else {
                                _defaultVal[params[0].id] = data[index];
                            }
                        }
                    }
                }
            }
        }
        return _defaultVal;
    }

    OLBIUS.olbius().oLapChart = function (container, config, configPopup, url, async, removeSeries, func, height) {
        return olbiusOLapChart(container, config, configPopup, url, async, removeSeries, func, height);
    };

}(window, OLBIUS));