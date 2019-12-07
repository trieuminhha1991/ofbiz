(function (window, olbius) {

    var _readConfig = {};

    var _popupLabel = {
        configuration: 'Configuration',
        button: 'OK'
    };

    olbius.obiusPopupConfig = function (name, func) {
        if (typeof func == 'function') {
            _readConfig[name] = func;
        }
    };

    olbius.callObiusPopupConfig = function (name, object, config, popup) {
        if (typeof _readConfig[name] == 'function') {
            _readConfig[name].call(popup, object, config);
        }
    };

    olbius.obiusPopupConfig('event', function (object, config) {
        this._event[object.id] = config;
    });

    olbius.obiusPopupConfig('hide', function (object, config) {
        object._hide = config;
    });

    olbius.popupLabel = function (config) {
        _popupLabel = $.extend(_popupLabel, config);
    };

    function OlbiusPopup(config) {

        this.uuid = olbius.random();

        this._event = {};

        var _group = {};

        var $this = this;

        var _widthMax = 0;

        var elements = {};

        var _func;

        for (var element in config) {
            var $g = this[config[element].group];
            if ($g) {
                var tmp = new PopupGroupElement(config[element].id, $g, this, config[element].params);

                _group[config[element].id] = tmp;

                var tmpElms = tmp.getConfig();

                for (var e in tmpElms) {
                    var obj = popupObject(tmpElms[e]);
                    obj.group(config[element].id);
                    elements[obj.id] = obj;
                }

            } else {
                var object = popupObject(config[element]);
                elements[object.id] = object;
            }
        }

        function popupObject(element) {

            var $f = $this[element.action];
            var object = {};
            if (typeof $f == 'function') {
                object = $f.call($this, element.params);
            }
            for (var i in element) {
                if (_readConfig[i]) {
                    _readConfig[i].call($this, object, element[i]);
                }
            }
            return object;
        }

        function initWindow() {
            $this.jqxWindow.jqxWindow({
                showCollapseButton: false,
                isModal: true,
                theme: olbius.getVariable('theme'),
                resizable: false, draggable: false,
                minWidth: 620,
                maxWidth: $(window).width(),
                maxHeight: $(window).height(),
                initContent: function () {
                    $this.jqxWindow.jqxWindow('focus');
                    $this.jqxWindow.jqxWindow('close');
                }
            });

            var body;

            $this.jqxWindow.on('close', function () {
                $('body').css('overflow', body);
                var tmp;
                for (var i in elements) {
                    tmp = elements[i];
                    if (tmp && !tmp._hide) {
                        tmp.clean();
                    }
                }
                $(window).resize();
            });
            $this.jqxWindow.on('open', function () {
                body = $('body').css('overflow');
                $('body').css('overflow', 'hidden');
            });
            $this.jqxWindow.on('keyup', function (event) {
                var code = event.keyCode || event.which;
                if (code == 13) {
                    apply();
                }
            });
        }

        function apply() {
            if (typeof _func == 'function') {
                _func.call($this, $this);
            }
            $this.close();
        }

        this.init = function () {
            if (!$this._init) {

                var text = '<div id="?" style="display: none;position: fixed;"><div id="?"><span>' + _popupLabel.configuration + '</span></div><div style="overflow: hidden;padding: 10px 20px 20px 20px" id="?"><div class="row-fluid"><div id="?" class="span12"></div><div id="?" class="span12" style="text-align: center;margin: 10px auto auto;"><button id="?" type="button" class="btn btn-primary form-action-button pull-right" style="margin:0 auto;"><i class="icon-ok"></i>' + _popupLabel.button + '</button></div></div></div></div>';

                text = olbius.replaceString(text, ['window', 'windowHeader', 'windowContent', 'windowElement', 'span-btn-ok', 'btn-ok'], $this.uuid);

                $('body').append(text);

                function getId(id) {
                    return '#'.concat(id).concat('-').concat($this.uuid);
                }

                $this.jqxWindow = $(getId('window'));
                $this.jqxWindowHeader = $(getId('windowHeader'));
                $this.jqxWindowContent = $(getId('windowContent'));
                $this.jqxWindowElement = $(getId('windowElement'));

                initWindow();

                $(getId('btn-ok')).click(apply);

                for (var i in elements) {
                    text = '<div id="?" class="span6" style="float:left; margin-bottom: 10px;"></div>';
                    var tmp = olbius.random();
                    text = olbius.replaceString(text, ['span'], tmp);
                    $this.jqxWindowElement.append(text);
                    elements[i].spanElement($('#span-' + tmp));
                    elements[i].append();

                    (function (element, config) {
                        for (var j in config) {
                            var event = config[j];
                            if (event && event.name && event.action && typeof event.action == 'function') {
                                element.on(event.name, function () {
                                    event.action($this, element);
                                });
                            }
                        }
                    }(elements[i], $this._event[elements[i].id]));

                    _widthMax = Math.max(_widthMax, elements[i].width());
                }

                $this._append = true;
                $this._init = true;
            }
        };

        this.val = function (id, value) {
            if (!elements[id] || elements[id]._hide) {
                return null;
            }
            return elements[id].val(value);
        };

        this.group = function (id) {
            return _group[id];
        };

        this.element = function (id) {
            return elements[id];
        };

        this.resize = function () {
            if ($this._append) {
                var _windowWidth = 40;
                var _windowHeight = 105;

                var _elementHeightOne = 0;
                var _elementHeightTwo = 0;

                var _num = 0;
                var _tmpElement = [];
                var flag = false;

                for (var i in elements) {
                    var tmp = elements[i];
                    if (!tmp._hide) {
                        if (_num % 2 == 0) {
                            tmp.spanElement().attr('class', 'span6 no-left-margin');
                            _elementHeightOne += tmp.height() + 15;
                            flag = true;
                        } else {
                            tmp.spanElement().attr('class', 'span6');
                            _elementHeightTwo += tmp.height() + 15;
                            if (_elementHeightTwo >= _elementHeightOne) {
                                flag = true;
                                _elementHeightOne = _elementHeightTwo;
                            }
                        }
                        tmp.spanElement().show();
                        _tmpElement[_num] = tmp;
                        if (flag) {
                            _num++;
                            flag = false;
                        }
                    } else {
                        tmp.spanElement().hide();
                    }
                }

                _windowHeight += Math.max(_elementHeightOne, _elementHeightTwo);

                if (_tmpElement.length > 1) {
                    _windowWidth += 2 * (_widthMax + 20) + 15;
                } else if (_tmpElement.length > 0) {
                    _windowWidth += _widthMax + 10;
                    _tmpElement[0].spanElement().attr('class', 'span12 no-left-margin');
                }

                $this.jqxWindow.jqxWindow('resizable', true);
                $this.jqxWindow.jqxWindow('resize', _windowWidth, _windowHeight);
                $this.jqxWindow.jqxWindow('resize', _windowWidth, _windowHeight);
                $this.jqxWindow.jqxWindow('resizable', false);
                $this._append = false;
                $this.move();
            }
        };

        this.apply = function (func) {
            if (typeof func == 'function') {
                _func = func;
            }
        }

    }

    OlbiusPopup.prototype = {
        open: function () {
            olbius.popupLoading().open();
            var $this = this;
            setTimeout(function() {
                if (!$this._init) {
                    $this.init();
                }
                $this.resize();
                olbius.popupLoading().close();
                $this.jqxWindow.jqxWindow('open');
                $this.jqxWindow.jqxWindow('focus');
            }, 0);
        },
        close: function () {
            if (this._init) {
                this.jqxWindow.jqxWindow('close');
            }
        },
        move: function () {
            var width = ($(window).width() - this.jqxWindow.jqxWindow('width')) / 2;
            var height = ($(window).height() - this.jqxWindow.jqxWindow('height')) / 2;
            this.jqxWindow.jqxWindow('move', width, height);
        }
    };

    function PopupElement(popup, config) {

        this.popup = popup;

        this.id = config.id;

        var _element;

        var _group;

        this.spanElement = function (object) {
            if (!object) {
                return _element;
            } else {
                _element = object;
            }
        };
        this.group = function (id) {
            if (id) {
                _group = id;
            } else {
                return _group;
            }
        }
    }

    PopupElement.prototype = {
        val: function (value) {
            if (!value) {
                return this._value;
            } else {
                this._value = value;
            }
        },
        hide: function () {
            if(!this._hide) {
                this._hide = true;
                this.popup._append = true;
                this.popup.resize();
            }
        },
        show: function () {
            if(this._hide) {
                this._hide = false;
                this.popup._append = true;
                this.popup.resize();
            }
        },
        height: function () {
            return 0;
        },
        width: function () {
            return 0;
        },
        append: function () {

        },
        clean: function () {

        },
        on: function (event, func) {

        }
    };

    olbius.addPopupConfig = function (name, func) {
        if (typeof func == 'function') {
            OlbiusPopup.prototype[name] = func;
        }
    };

    olbius.PopupElement = PopupElement;

    function PopupGroupElement(id, config, popup, params) {

        var $this = this;

        this._popup = popup;

        this._config = config.elements(params);

        this._id = id;

        for (var i in this._config) {
            this._config[i].params.id = this._id + '-' + this._config[i].params.id
        }

        this.val = function (id) {
            if (id) {
                return $this._popup.val(this._id + '-' + id);
            } else {
                var tmp = config.val;
                if (typeof tmp == 'function') {
                    return tmp(this._id, popup);
                }
                return null;
            }
        };

        this.element = function (id) {
            return $this._popup.element($this._id + '-' + id);
        };

        this.getConfig = function () {
            return $this._config;
        }

    }

    olbius.addPopupConfigGroup = function (name, config) {
        OlbiusPopup.prototype[name] = config;
    };

    olbius.popup = function (config) {
        var p = new OlbiusPopup(config);
        $(window).bind('resize', function () {
            if(p._init) {
                p.move();
            }
         });
        return p;
    }

}(window, OlbiusUtil));
