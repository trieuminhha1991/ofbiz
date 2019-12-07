(function (window) {

    var _tickInterval;

    var _dateTimeFormat;

    var _dateTimeFullFormat;

    var _monthFormat;

    var _company = 'company';

    var _product = [];

    var _category = [];

    var _childParty = {};

    var _facility = [];

    var _geoType = [];

    var _geo = {};

    var _okText = 'OK';

    var _localizationobj = {};

    var _theme = 'olbius';

    var configuration = 'Configuration';

    function olbius() {
        _dateTimeFormat = 'yyyy-MM-dd';
        _dateTimeFullFormat = 'yyyy-MM-dd HH:mm:ss';
        _monthFormat = 'yyyy-MM';
        _tickInterval = 10;
    }

    function _appendHtmlUUID(id, html, uuid, attr) {
        var tmp = html;
        for (var i in attr) {
            tmp = tmp.replace('?', attr[i].concat('-').concat(uuid));
        }
        $(id).append(tmp);
    }

    function _generateUUID() {
        var d = new Date().getTime();
        var uuid = 'xxxxxxxx-xxxx-xxxx-yxxx-xxxxxx9xxxxx'.replace(/[xy]/g, function (c) {
            var r = (d + Math.random() * 16) % 16 | 0;
            d = Math.floor(d / 16);
            return (c == 'x' ? r : (r & 0x3 | 0x8)).toString(16);
        });
        return uuid;
    }

    olbius.prototype = {
        putLocalizationObj: function (key, vaule) {
            _localizationobj[key] = vaule;
        },
        getLocalizationObj: function (key) {
            if (key) {
                return _localizationobj[key];
            } else {
                return _localizationobj;
            }
        },
        setTheme: function (theme) {
            _theme = theme;
        },
        getTheme: function () {
            return _theme;
        },
        getProduct: function () {
            if (_product.length == 0) {
                jQuery.ajax({
                    url: 'getProduct',
                    async: false,
                    type: 'POST',
                    success: function (data) {
                        _product = data.product;
                    }
                });
            }
            return _product;
        },
        getCategory: function () {
            if (_category.length == 0) {
                jQuery.ajax({
                    url: 'getCategory',
                    async: false,
                    type: 'POST',
                    success: function (data) {
                        _category = data.category;
                    }
                });
            }
            return _category;
        },
        getChildParty: function (party) {
            if (!_childParty[party]) {
                _childParty[party] = [];
                jQuery.ajax({
                    url: 'getChildParty',
                    async: false,
                    type: 'POST',
                    data: {'parent': party},
                    success: function (data) {
                        _childParty[party] = data.child;
                    }
                });
            }
            return _childParty[party];
        },
        getChildGroups: function (party) {
            if (!party) return null;
            var items = this.getChildParty(party);
            return items;
        },
        getFacility: function () {
            if (_facility.length == 0) {
                jQuery.ajax({
                    url: 'getFacility',
                    async: false,
                    type: 'POST',
                    success: function (data) {
                        _facility = data.facility;
                    }
                });
            }
            return _facility;
        },
        getGeoType: function () {
            if (_geoType.length == 0) {
                jQuery.ajax({
                    url: 'getGeoType',
                    async: false,
                    type: 'POST',
                    success: function (data) {
                        _geoType = data.geoType;
                    }
                });
            }
            return _geoType;
        },
        getGeo: function (type) {
            if (!_geo[type]) {
                jQuery.ajax({
                    url: 'getGeo',
                    async: false,
                    type: 'POST',
                    data: {'geoType': type},
                    success: function (data) {
                        _geo[type] = data.geo;
                    }
                });
            }
            return _geo[type];
        },
        setDateTimeFormat: function (dateTimeFormat) {
            _dateTimeFormat = dateTimeFormat;
        },
        getDateTimeFormat: function () {
            return _dateTimeFormat;
        },
        setDateTimeFullFormat: function (format) {
            _dateTimeFullFormat = format;
        },
        getDateTimeFullFormat: function () {
            return _dateTimeFullFormat;
        },
        setMonthFormat: function (format) {
            _monthFormat = format;
        },
        getMonthFormat: function () {
            return _monthFormat;
        },
        setCompany: function (company) {
            _company = company;
        },
        getCompany: function () {
            return _company;
        },
        setTickInterval: function (tickInterval) {
            _tickInterval = tickInterval;
        },
        getTickInterval: function () {
            return _tickInterval;
        },
        setConfiguration: function (cf) {
            configuration = cf;
        },
        getConfiguration: function () {
            return configuration;
        },
        dateToString: function (date) {
            var day = date.getDate();
            if (day < 10) {
                day = '0' + day;
            }
            var month = date.getMonth() + 1;
            if (month < 10) {
                month = '0' + month;
            }
            return date.getFullYear() + '-' + month + '-' + day;
        },
        getFormaterAxisLabel: function (dateType) {
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
        },
        getTickIntervalSize: function (length, tickInterval) {
            var tmp = length / tickInterval;
            if (tmp < 1) {
                return 1;
            } else {
                return Math.ceil(tmp);
            }
        },
        setOKText: function (ok) {
            _okText = ok;
        },
        getOKText: function () {
            return _okText;
        },
        defaultLineFunc: function (data, chart, datetype, removeSeries, flagFunc, olap) {
            var tmp = {
                labels: {
                    enabled: true
                },
                categories: data.xAxis
            };

            if (datetype) {
                tmp['labels']['formatter'] = OLBIUS.getFormaterAxisLabel(datetype);
            }
            if(data.xAxis)
                tmp['tickInterval'] = OLBIUS.getTickIntervalSize(data.xAxis.length, _tickInterval);

            chart.xAxis[0].update(tmp, false);

            if (removeSeries) {
                while (chart.series.length > 0) {
                    chart.series[0].remove(false);
                }
            }
            var color = 0;
            var marker = 0;
            for (var i in data.yAxis) {
                chart.addSeries({
                    name: i,
                    data: data.yAxis[i],
                    color: Highcharts.getOptions().colors[color++],
                    marker: {
                        symbol: Highcharts.getOptions().symbols[marker++]
                    }
                }, false);
            }

            chart.redraw();

            if (data.xAxis != undefined && data.xAxis && data.xAxis.length == 0) {
                flagFunc();
            }
        },
        defaultColumnFunc: function (data, chart, datetype, removeSeries, flagFunc, olap) {
            var tmp = {
                labels: {
                    enabled: true
                },
                categories: data.xAxis
            };

            chart.xAxis[0].update(tmp, false);

            if (removeSeries) {
                while (chart.series.length > 0) {
                    chart.series[0].remove(false);
                }
            }
            var color = 0;
            for (var i in data.yAxis) {
                chart.addSeries({
                    name: i,
                    data: data.yAxis[i],
                    color: Highcharts.getOptions().colors[color++],
                }, false);
            }

            chart.redraw();

            if (data.xAxis != undefined && data.xAxis && data.xAxis.length == 0) {
                flagFunc();
            }
        },
        defaultBarFunc: function (data, chart, datetype, removeSeries, flagFunc, olap) {
            var tickSize = OLBIUS.getTickIntervalSize(data.xAxis.length, _tickInterval);

            chart.xAxis[0].update({
                categories: data.xAxis,
                tickInterval: tickSize
            }, false);

            chart.xAxis[1].update({
                categories: data.xAxis,
                tickInterval: tickSize

            }, false);

            if (removeSeries) {
                while (chart.series.length > 0) {
                    chart.series[0].remove(false);
                }
            }

            var _max = [];
            var color = 0;
            for (var i in data.yAxis) {
                chart.addSeries({name: i, data: data.yAxis[i], color: Highcharts.getOptions().colors[color++]}, false);
                var max = Math.max.apply(Math, data.yAxis[i]);
                if (max <= 0) {
                    max = Math.min.apply(Math, data.yAxis[i]);
                    max = -max;
                }
                _max.push(max);
            }
            var max = Math.max.apply(Math, _max) + 1;

            chart.yAxis[0].update({min: -max, max: max}, false);

            chart.redraw();

            if (data.xAxis != undefined && data.xAxis && data.xAxis.length == 0) {
                flagFunc();
            }
        },
        defaultPieFunc: function (data, chart, datetype, removeSeries, flagFunc, olap) {
            var array = [];

            for (var i in data.yAxis) {
                var tmp = [];
                tmp.push(i);
                tmp.push(data.yAxis[i][0]);
                array.push(tmp);
            }
            chart.series[0].setData(array);

            if (data.xAxis != undefined && data.xAxis && data.xAxis.length == 0) {
                flagFunc();
            }

        },
        olbius: function () {
            return this;
        },
        generateUUID: function () {
            return _generateUUID();
        },
        appendHtmlUUID: function (id, html, uuid, attr) {
            _appendHtmlUUID(id, html, uuid, attr);
        }
    }

    function _appendLoading(id, offset) {
        var uuid = 'wait-spinner-' + _generateUUID();
        var height = $(id).height() / 2 - 20;
        var text = '<div id="' + uuid + '" style="display: none;z-index: 99999;position: absolute;">' +
            '<div style="background-image:url(/images/spinner.gif);background-repeat: no-repeat;height: 36px;width: 36px;"></div></div>';

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

    var _popupLoading = (function () {

        var uuid = 'wait-spinner-' + _generateUUID();

        var item = [];

        item.push('<div class="sk-double-bounce loading-item"><div class="sk-child sk-double-bounce1"></div><div class="sk-child sk-double-bounce2"></div></div>');
        item.push('<div class="sk-wave loading-item"><div class="sk-rect sk-rect1"></div><div class="sk-rect sk-rect2"></div>' +
            '<div class="sk-rect sk-rect3"></div><div class="sk-rect sk-rect4"></div><div class="sk-rect sk-rect5"></div></div>');
        item.push('<div class="sk-wandering-cubes loading-item"><div class="sk-cube sk-cube1"></div><div class="sk-cube sk-cube2"></div></div>');
        item.push('<div class="sk-spinner sk-spinner-pulse loading-item"></div>');
        item.push('<div class="sk-chasing-dots loading-item"><div class="sk-child sk-dot1"></div><div class="sk-child sk-dot2"></div></div>');
        item.push('<div class="sk-three-bounce loading-item"><div class="sk-child sk-bounce1"></div><div class="sk-child sk-bounce2"></div>' +
            '<div class="sk-child sk-bounce3"></div></div>');
        item.push('<div class="sk-circle loading-item"><div class="sk-circle1 sk-child"></div><div class="sk-circle2 sk-child"></div>' +
            '<div class="sk-circle3 sk-child"></div><div class="sk-circle4 sk-child"></div><div class="sk-circle5 sk-child"></div>' +
            '<div class="sk-circle6 sk-child"></div><div class="sk-circle7 sk-child"></div><div class="sk-circle8 sk-child"></div>' +
            '<div class="sk-circle9 sk-child"></div><div class="sk-circle10 sk-child"></div><div class="sk-circle11 sk-child"></div>' +
            '<div class="sk-circle12 sk-child"></div></div>');
        item.push('<div class="sk-cube-grid loading-item"><div class="sk-cube sk-cube1"></div><div class="sk-cube sk-cube2"></div>' +
            '<div class="sk-cube sk-cube3"></div><div class="sk-cube sk-cube4"></div><div class="sk-cube sk-cube5"></div>' +
            '<div class="sk-cube sk-cube6"></div><div class="sk-cube sk-cube7"></div><div class="sk-cube sk-cube8"></div>' +
            '<div class="sk-cube sk-cube9"></div></div>');
        item.push('<div class="sk-fading-circle loading-item"><div class="sk-circle1 sk-circle"></div><div class="sk-circle2 sk-circle"></div>' +
            '<div class="sk-circle3 sk-circle"></div><div class="sk-circle4 sk-circle"></div><div class="sk-circle5 sk-circle"></div>' +
            '<div class="sk-circle6 sk-circle"></div><div class="sk-circle7 sk-circle"></div><div class="sk-circle8 sk-circle"></div>' +
            '<div class="sk-circle9 sk-circle"></div><div class="sk-circle10 sk-circle"></div><div class="sk-circle11 sk-circle"></div>' +
            '<div class="sk-circle12 sk-circle"></div></div>');
        item.push('<div class="sk-folding-cube loading-item"><div class="sk-cube1 sk-cube"></div><div class="sk-cube2 sk-cube"></div>' +
            '<div class="sk-cube4 sk-cube"></div><div class="sk-cube3 sk-cube"></div></div>');

        var tmp = new Date();
        tmp = tmp.getTime() % item.length;
        var text = '<div id="' + uuid + '" style="position: fixed; top: 0; z-index: 99999; background: rgba(0, 0, 0, 0.3); width: 100%;height:100%;">' +
            '<div class="loading-content" style="position: absolute;top: 40%;left: 50%;margin-left:-20px">' +
            item[tmp] +
            '</div></div>';

        var $loading = $('body');
        $loading.append(text);
        $loading = $('#' + uuid);
        $loading.hide();

        var body;

        var isOpen;

        var count = 0;

        function _open() {
            if (!isOpen) {
                body = $('body').css('overflow');
                $loading.show();
                isOpen = true;
            }
        };

        function _close() {
            if (isOpen) {
                $('body').css('overflow', body);
                $loading.hide();
                isOpen = false;
            }
        };

        return {
            open: function () {
                _open();
                count++;
            },
            close: function () {
                count--;
                if (count <= 0) {
                    _close();
                }
            },
            isOpen: function () {
                return isOpen;
            }
        };
    }());

    olbius.prototype.popupLoading = function () {
        return _popupLoading;
    };

    olbius.prototype.appendLoading = function (id, offset) {
        return _appendLoading(id, offset);
    };

    window.OLBIUS = new olbius;

}(window));