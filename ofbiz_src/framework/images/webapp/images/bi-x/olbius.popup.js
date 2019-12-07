(function (window, OLBIUS) {

    var _windowElementType = {};
    var _windowElementEvent = {};

    function initJqxWindow() {

        var width_default = 200;

        var height_default = 25;

        var uuid = OLBIUS.generateUUID();

        var text = '<div id="?" style="display: none;position: fixed;"><div id="?"><span>' + OLBIUS.getConfiguration() + '</span></div><div style="overflow: hidden;padding: 10px 20px 20px 20px" id="?"><div class="row-fluid"><div id="?" class="span12"></div><div id="?" class="span12" style="text-align: center;margin: auto;margin-top: 10px"><button id="?" type="button" class="btn btn-primary form-action-button pull-right" style="margin:0px auto;"><i class="icon-ok"></i>' + OLBIUS.getOKText() + '</button></div></div></div></div>';

        OLBIUS.appendHtmlUUID('body', text, uuid, ['window', 'windowHeader', 'windowContent', 'windowElement', 'span-btn-ok', 'btn-ok']);

        function getId(id) {
            return '#'.concat(id).concat('-').concat(uuid);
        }

        var $jqxWindow = $(getId('window'));
        var jqxWindowHeader = $(getId('windowHeader'));
        var jqxWindowContent = $(getId('windowContent'));
        var jqxWindowElement = $(getId('windowElement'));

        var _windowElementMap = {};
        var _windowElementSize = 0;

        var _widthMax = 0;

        var _append = false;

        var curValue = {};

        var $func_ok;

        function _returnObject(id, label, type) {
            return {
                id: id,
                span: $(getId(id.concat('-span'))),
                text: $(getId(id.concat('-text'))),
                element: $(getId(id)),
                label: label,
                type: type
            }
        }

        function getLength(text) {
            var length = text.length;
            var tmp = 0;
            for (var i = 0; i < length; i++) {
                if (text[i].toLowerCase() === text[i]) {
                    tmp += 1;
                } else {
                    tmp += 2;
                }
            }
            return tmp;
        }

        function _pushElement(object, hide) {
            var fontSize = parseInt(object.text.css('font-size'), 10);
            var tmp = getLength(object.label);
            object['textSize'] = tmp * Math.ceil(fontSize / 2);
            object['elementHeight'] = object.element.height();
            object['elementWidth'] = object.element.width();
            object['spanHeight'] = object.elementHeight + parseInt(object.span.css('margin-bottom'), 0);

            if (_widthMax < object.textSize + object.elementWidth) {
                _widthMax = object.textSize + object.elementWidth;
            }

            _windowElementMap[object.id] = {
                index: _windowElementSize,
                object: object
            };
            if (hide) {
                _windowElementMap[object.id].hide = true;
            }
            _windowElementSize++;
        }

        function _resize() {
            if (_append) {
                var _windowWidth = 40;
                var _windowHeight = 105;

                var _elementHeightOne = 0;
                var _elementHeightTwo = 0;

                var _num = 0;
                var _tmpElement = [];
                var flag = false;

                for (var i in _windowElementMap) {
                    var tmp = _windowElementMap[i];
                    if (!tmp.hide) {
                        if (_num % 2 == 0) {
                            tmp.object.span.attr('class', 'span6 no-left-margin');
                            _elementHeightOne += tmp.object.spanHeight + 2;
                            flag = true;
                        } else {
                            tmp.object.span.attr('class', 'span6');
                            _elementHeightTwo += tmp.object.spanHeight + 2;
                            if (_elementHeightTwo >= _elementHeightOne) {
                                flag = true;
                                _elementHeightOne = _elementHeightTwo;
                            }
                        }
                        tmp.object.text.width(_widthMax - tmp.object.elementWidth);
                        tmp.object.span.show();
                        _tmpElement[_num] = tmp.object;
                        if (flag) {
                            _num++;
                            flag = false;
                        }
                    } else {
                        tmp.object.span.hide();
                    }
                }
                _windowHeight += Math.max(_elementHeightOne, _elementHeightTwo);

                if (_tmpElement.length > 1) {
                    _windowWidth += 2 * (_widthMax + 20) + 15;
                } else {
                    _windowWidth += _widthMax + 10;
                    _tmpElement[0].span.attr('class', 'span12 no-left-margin');
                }

                $jqxWindow.jqxWindow('resizable', true);
                $jqxWindow.jqxWindow('resize', _windowWidth, _windowHeight);
                $jqxWindow.jqxWindow('resize', _windowWidth, _windowHeight);
                $jqxWindow.jqxWindow('resizable', false);
                _append = false;
            }
        }

        return {
            getJqxWindow: function () {
                return $jqxWindow;
            },
            getJqxWindowElement: function () {
                return jqxWindowElement;
            },
            getWindowElementMap: function (key) {
                if (key) {
                    return _windowElementMap[key];
                } else {
                    return _windowElementMap;
                }
            },
            putWindowElementMap: function (key, object) {
                _windowElementMap[key] = object;
            },
            getCurValue: function (key) {
                if (key) {
                    return curValue[key];
                } else {
                    return curValue;
                }
            },
            putCurValue: function (key, object) {
                curValue[key] = object;
            },
            append: function () {
                _append = true;
            },
            setFuncOk: function (func) {
                $func_ok = func;
            },
            getFuncOk: function () {
                return $func_ok;
            },
            returnObject: function (id, label, type) {
                return _returnObject(id, label, type);
            },
            resize: function () {
                _resize();
            },
            pushElement: function (object, hide) {
                _pushElement(object, hide)
            },
            getUuid: function () {
                return uuid;
            },
            id: function (id) {
                return getId(id);
            },
            validate: function (config) {
                if (!config['width']) {
                    config['width'] = width_default;
                }
                if (!config['height']) {
                    config['height'] = height_default;
                }
            }
        }
    }

    function popup() {
        this.$jqxWindowElement = initJqxWindow();
    }

    popup.prototype = {
        init: function () {
            var jqxWindowElement = this.$jqxWindowElement;
            jqxWindowElement.getJqxWindow().jqxWindow({
                showCollapseButton: false,
                isModal: true,
                theme: OLBIUS.getTheme(),
                resizable: false, draggable: false,
                initContent: function () {
                    jqxWindowElement.getJqxWindow().jqxWindow('focus');
                    jqxWindowElement.getJqxWindow().jqxWindow('close');
                }
            });
            var body;
            jqxWindowElement.getJqxWindow().on('close', function (event) {
                $('body').css('overflow', body);
                var tmp;
                for (var id in jqxWindowElement.getCurValue()) {
                    tmp = jqxWindowElement.getWindowElementMap(id);
                    if (tmp) {
                        if (typeof _windowElementType[tmp.object.type].clear === 'function') {
                            _windowElementType[tmp.object.type].clear(tmp, jqxWindowElement.getCurValue());
                        }
                    }
                }
                $(window).resize();
            });
            jqxWindowElement.getJqxWindow().on('open', function (event) {
                body = $('body').css('overflow');
                $('body').css('overflow', 'hidden');
            });
            jqxWindowElement.getJqxWindow().on('keyup', function (event) {
                var code = event.keyCode || event.which;
                if (code == 13) {
                    jqxWindowElement.getFuncOk().apply();
                }
            });
        },
        jqxElement: function () {
            return this.$jqxWindowElement.getJqxWindowElement();
        },
        elementMap: function (id) {
            return this.$jqxWindowElement.getWindowElementMap(id);
        },
        id: function (id) {
            if (id) {
                return this.$jqxWindowElement.id(id);
            }
            return this.$jqxWindowElement.getUuid();
        },
        validate: function (config) {
            this.$jqxWindowElement.validate(config);
        },
        returnObject: function (id, label, type) {
            return this.$jqxWindowElement.returnObject(id, label, type)
        },
        putCurValue: function (id, object) {
            this.$jqxWindowElement.putCurValue(id, object);
        },
        getCurValue: function (id) {
            return this.$jqxWindowElement.getCurValue(id);
        },
        pushElement: function (object, hide) {
            this.$jqxWindowElement.pushElement(object, hide);
        },
        append: function () {
            this.$jqxWindowElement.append();
        },
        setPrototype: function (name, func) {
            this[name] = func;
        },
        addOK: function (click) {
            this.$jqxWindowElement.setFuncOk(click);
            $(this.$jqxWindowElement.id('btn-ok')).click(this.$jqxWindowElement.getFuncOk());
        },
        hide: function (id) {
            if (!this.$jqxWindowElement.getWindowElementMap(id).hide) {
                this.$jqxWindowElement.getWindowElementMap(id).hide = true;
                this.append();
            }
        },
        show: function (id) {
            if (this.$jqxWindowElement.getWindowElementMap(id).hide) {
                this.$jqxWindowElement.getWindowElementMap(id).hide = false;
                this.append();
            }
        },
        onEvent: function (id, event, func) {
            var tmp = this.$jqxWindowElement.getWindowElementMap(id);
            if (tmp) {
                tmp.object.element.on(event, func);
            }
        },
        resize: function () {
            this.$jqxWindowElement.resize();
        },
        open: function () {

            this.$jqxWindowElement.resize();

            var width = ($(window).width() - this.$jqxWindowElement.getJqxWindow().jqxWindow('width')) / 2;
            var height = ($(window).height() - this.$jqxWindowElement.getJqxWindow().jqxWindow('height')) / 2;
            this.$jqxWindowElement.getJqxWindow().jqxWindow('move', width, height);
            this.$jqxWindowElement.getJqxWindow().jqxWindow('open');
            this.$jqxWindowElement.getJqxWindow().jqxWindow('focus');
        },
        close: function () {
            this.$jqxWindowElement.getJqxWindow().jqxWindow('close');
        },
        val: function (id, value) {
            var tmp = this.$jqxWindowElement.getWindowElementMap(id);
            if (!tmp) {
                return null;
            }
            if (value) {
                tmp.object.element.val(value);
            } else if (!tmp.hide) {
                if (typeof _windowElementType[tmp.object.type].val === 'function') {
                    return _windowElementType[tmp.object.type].val(tmp, this.$jqxWindowElement.getCurValue());
                }
            }

            return null;
        },
        clear: function (id) {
            var tmp = this.$jqxWindowElement.getWindowElementMap(id);
            if (tmp) {
                if (typeof _windowElementType[tmp.object.type].clear === 'function') {
                    _windowElementType[tmp.object.type].clear(tmp, this.$jqxWindowElement.getCurValue());
                }
            }
        }
    }


    OLBIUS.olbius().popup = (function () {

        function _instant(config, fnOk) {

            OLBIUS.popupLoading().open();

            var _popup = new popup;

            _popup.init();

            var timeout = 100;
            var i = 0;
            for (var element in config) {
                (function (element, i) {
                    setTimeout(function () {
                        var $fn = _popup[config[element].action];
                        if (typeof $fn === 'function') {
                            var params = config[element].params;
                            $fn.apply(_popup, params);
                            var val = config[element].val;
                            if (val) {
                                _popup.val(params[0].id, val);
                            }
                        }
                        var tmpFunc = _windowElementEvent[config[element].action];
                        if (typeof tmpFunc === 'function') {
                            config[element]['event'] = tmpFunc(config[element]);
                        }
                        $fn = config[element].event;
                        if (typeof $fn === 'function') {
                            $fn.apply(_popup, [_popup]);
                        }
                    }, i * timeout);
                }(element, i));
                i++;
            }

            setTimeout(function () {

                if (fnOk) {
                    _popup.addOK(function () {
                        fnOk();
                        _popup.close();
                    });
                }

                OLBIUS.popupLoading().close();

                _popup.open();

            }, i * timeout);

            return _popup;
        }

        return {
            instantPopup: function (config, fnOk) {
                return _instant(config, fnOk);
            },
            addConfig: function (name, func, funCur) {

                if (typeof func === 'function') {
                    popup.prototype[name] = function (config) {
                        popup.prototype.append.call(this);
                        popup.prototype.validate.call(this, config);
                        var tmp = func.call(this, config);
                        popup.prototype.pushElement.call(this, tmp, config.hide);
                        if (typeof funCur === 'function') {
                            popup.prototype.putCurValue.call(this, config.id, funCur(config));
                        }
                    }
                }
            },
            addType: function (name, type) {
                if (typeof type.val === 'function' && typeof type.clear === 'function') {
                    _windowElementType[name] = type;
                }
            },
            addPrototype: function (name, func) {
                if (typeof func === 'function') {
                    popup.prototype[name] = func;
                }
            },
            addEvent: function (name, event) {
                if (typeof event === 'function') {
                    _windowElementEvent[name] = event;
                }
            }
        }
    })();

    OLBIUS.olbius().initPopup = function (config, fnOk) {
        return OLBIUS.olbius().popup.instantPopup(config, fnOk);
    }

    OLBIUS.olbius().popup.addPrototype('item', function (id, index) {
        var tmp = this.elementMap(id);
        if (!tmp) {
            return null;
        }
        if (tmp.object.type === 'dropDown') {
            return tmp.object.element.jqxDropDownList('getItem', index);
        }
    });

    OLBIUS.olbius().popup.addPrototype('getDate', function (id) {
        var tmp = this.elementMap(id);
        if (!tmp) {
            return null;
        }
        if (tmp.object.type === 'dateTime') {
            return tmp.object.element.jqxDateTimeInput('getDate');
        }
    });

    OLBIUS.olbius().popup.addType('dropDown', {
        val: function (object, cur) {
            var item = object.object.element.jqxDropDownList('getSelectedItem');
            cur[object.object.id] = {index: item.index, hide: object.hide};
            return item.value;
        },
        clear: function (object, cur) {
            object.object.element.jqxDropDownList('selectIndex', cur[object.object.id].index);
        }
    });

    OLBIUS.olbius().popup.addType('dateTime', {
        val: function (object, cur) {
            var date = object.object.element.val('date');
            var dateString = OLBIUS.dateToString(date);
            cur[object.object.id] = {value: dateString, hide: object.hide};
            return dateString;
        },
        clear: function (object, cur) {
            object.object.element.val(cur[object.object.id].value);
        }
    });

    OLBIUS.olbius().popup.addType('tree', {
        val: function (object, cur) {
            var item = object.object.element.jqxTree('getSelectedItem');
            if (item) {
                return item.value;
            } else {
                return null;
            }
        },
        clear: function (object, cur) {
        }
    });

    OLBIUS.olbius().popup.addEvent('addDateTimeInput', function (element) {
        if (element.before) {
            return function (popup) {
                popup.onEvent(element.params[0].id, 'valueChanged', function (event) {
                    var fromDate = event.args.date;
                    var thruDate = popup.getDate(element.before);
                    if (thruDate < fromDate) {
                        popup.val(element.before, fromDate);
                    }
                });
            }
        } else if (element.after) {
            return function (popup) {
                popup.onEvent(element.params[0].id, 'valueChanged', function (event) {
                    var thruDate = event.args.date;
                    var fromDate = popup.getDate(element.after);
                    if (thruDate < fromDate) {
                        popup.val(element.after, thruDate);
                    }
                });
            }
        }
    });

    function appendDropDownList(id, label, object) {
        var text = '<div id="?" class="span6" style="float:left; margin-bottom: 10px;"><div id="?" style="margin-top: 5px;float: left;" class="align-right">' +
            label +
            '</div><div id="?" style="float: right"></div></div>';
        var element = object.jqxElement();
        var _id = object.id();
        OLBIUS.appendHtmlUUID(element, text, _id, [id.concat('-span'), id.concat('-text'), id]);

        return object.returnObject(id, label, 'dropDown');
    }

    function appendDateTimeInput(id, label, object) {
        var text = '<div id="?" class="span6" style="float:left; margin-bottom: 10px;"><div id="?" style="margin-top: 5px;float: left" class="align-right">' +
            label +
            '</div><div id="?" style="display:inline-block; vertical-align:top;float: right;margin-left:0px !important;"></div></div>';
        var element = object.jqxElement();
        var _id = object.id();
        OLBIUS.appendHtmlUUID(element, text, _id, [id.concat('-span'), id.concat('-text'), id]);

        return object.returnObject(id, label, 'dateTime');
    }

    function appendJQXTreeParty(id, label, width, height, source, object) {
        var text = '<div id="?" class="span6" style="float:left; margin-bottom: 10px;"><div id="?" style="margin-top: 5px;float: left" class="align-right">' +
            label +
            '</div><div id="?" style="visibility: hidden; float: right"></div></div>';
        var element = object.jqxElement();
        var _id = object.id();

        OLBIUS.appendHtmlUUID(element, text, _id, [id.concat('-span'), id.concat('-text'), id]);

        var $id = $(object.id(id));

        $id.jqxTree({source: source, width: width, height: height});

        $id.css('visibility', 'visible');

        $id.on('expand', function (event) {
            var _item = $id.jqxTree('getItem', event.args.element);
            var label = _item.label;
            var value = _item.value;
            var $element = $(event.args.element);
            var loader = false;
            var loaderItem = null;
            var children = $element.find('ul:first').children();
            $.each(children, function () {
                var item = $id.jqxTree('getItem', this);
                if (item && item.label == 'Loading...') {
                    loaderItem = item;
                    loader = true;
                    return false
                }
            });
            if (loader) {
                jQuery.ajax({
                    url: 'getChildParty',
                    async: true,
                    type: 'POST',
                    data: {"parent": label},
                    success: function (data) {
                        var items = [];
                        for (var i in data.child) {
                            var _value = data.child[i];
                            items.push({
                                label: data.child[i],
                                id: data.child[i] + '-' + _id,
                                value: _value,
                                "items": [{"label": "Loading..."}]
                            });
                        }
                        $id.jqxTree('addTo', items, $element[0]);
                        $id.jqxTree('removeItem', loaderItem.element);
                    }
                });
            }
        });

        return object.returnObject(id, label, 'tree');

    }

    OLBIUS.olbius().popup.addConfig('addDropDownList', function (config) {

        var tmp = appendDropDownList(config['id'], config['label'], this);

        $(tmp.element).jqxDropDownList({
            source: config['data'],
            selectedIndex: config['index'],
            theme: OLBIUS.getTheme(),
            width: config['width'],
            height: config['height'],
            displayMember: 'text',
            valueMember: 'value'
        });
        tmp.span.css('min-height', config['height'] + 'px');

        return tmp;

    }, function (config) {
        return {index: config.index, hide: config.hide};
    });

    OLBIUS.olbius().popup.addConfig('addJqxDropDownList', function (config) {
        var tmp = appendDropDownList(config['id'], config['label'], this);

        config.theme = OLBIUS.getTheme();
        config.displayMember = 'text';
        config.valueMember = 'value';

        delete config.id;

        delete config.label;

        $(tmp.element).jqxDropDownList(config);
        tmp.span.css('min-height', config['height'] + 'px');

        return tmp;

    }, function (config) {
        return {index: config.index, hide: config.hide};
    });

    OLBIUS.olbius().popup.addConfig('addDateTimeInput', function (config) {

        var tmp = appendDateTimeInput(config['id'], config['label'], this);

        if (!config['format']) {
            config['format'] = OLBIUS.getDateTimeFormat();
        }

        $(tmp.element).jqxDateTimeInput({
            width: config['width'],
            height: config['height'],
            formatString: config['format'],
            max: new Date(),
            theme: OLBIUS.getTheme(), 
            disabled: config['disabled'],
        });
        if (config['value']) {
            $(tmp.element).val(new Date(config.value));
        }

        tmp.span.css('min-height', config['height'] + 'px');

        return tmp;

    }, function (config) {
        return {value: new Date(config.value), hide: config.hide};
    });

    OLBIUS.olbius().popup.addConfig('addJQXTree', function (config) {

        var source = [];

        source.push({
            label: OLBIUS.getCompany(),
            id: OLBIUS.getCompany() + '-' + this.id(),
            value: OLBIUS.getCompany(),
            "items": [{"label": "Loading..."}]
        });

        var tmp = appendJQXTreeParty(config['id'], config['label'], config['width'], config['width'], source, this);

        return tmp;

    });

}(window, OLBIUS));