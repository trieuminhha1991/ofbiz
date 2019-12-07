(function (window) {

    var variable = {};

    var olbius = {
        putVariable: function (name, value) {
            variable[name] = value;
        },
        getVariable: function (name) {
            return variable[name];
        },
        random: function () {
            var d = new Date().getTime();
            var uuid = 'xxxxxxxx-xxxx-xxxx-yxxx-xxxxxx9xxxxx'.replace(/[xy]/g, function (c) {
                var r = (d + Math.random() * 16) % 16 | 0;
                d = Math.floor(d / 16);
                return (c == 'x' ? r : (r & 0x3 | 0x8)).toString(16);
            });
            return uuid;
        },
        replaceString: function (text, attr, uuid) {
            var tmp = text;
            for (var i in attr) {
                tmp = tmp.replace('?', attr[i] + ((uuid) ? '-'.concat(uuid) : ''));
            }
            return tmp;
        }
    };

    olbius.getFields = function (columns) {
        var tmp = {
            data : {},
            datafields : [],
            columns : [],
            olbiusColumns: {}
        };
        if (columns) {
            for (var i in columns) {
                var element = columns[i];
                var datafield = element.datafield;
                tmp.data[datafield.name] = {
                    datafield: datafield,
                    column: element
                };
                tmp.datafields.push(datafield);
                delete element.datafield;
                element.datafield = datafield.name;
                tmp.columns.push(element);
                if(typeof element.hidden == 'undefined') {
                    tmp.olbiusColumns[datafield.name] = datafield.name;
                }
            }
        }
        return tmp;
    };

    olbius.get = function(config, name) {
        var tmp = config[name];
        delete config[name];
        return tmp;
    };

    olbius.dateToString = function (date) {
        var day = date.getDate();
        if (day < 10) {
            day = '0' + day;
        }
        var month = date.getMonth() + 1;
        if (month < 10) {
            month = '0' + month;
        }
        return date.getFullYear() + '-' + month + '-' + day;
    };

    olbius.putVariable('theme', 'olbius');
    olbius.putVariable('dateTimeFullFormat', 'yyyy-MM-dd HH:mm:ss');
    olbius.putVariable('dateTimeFormat', 'yyyy-MM-dd');
    olbius.putVariable('monthFormat', 'yyyy-MM');

    var _popupLoading = (function () {

        var uuid = 'wait-spinner-' + olbius.random();

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
        }

        function _close() {
            if (isOpen) {
                $('body').css('overflow', body);
                $loading.hide();
                isOpen = false;
            }
        }

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

    olbius.popupLoading = function () {
        return _popupLoading;
    };

    window.OlbiusUtil = olbius;

}(window));