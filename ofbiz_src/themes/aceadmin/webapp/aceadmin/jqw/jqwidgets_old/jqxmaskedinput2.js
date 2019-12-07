! function(t) {
    t.jqx.jqxWidget("jqxMaskedInput", "", {}), t.extend(t.jqx._jqxMaskedInput.prototype, {
        defineInstance: function() {
            var e = {
                value: null,
                mask: "99999",
                width: null,
                height: 25,
                textAlign: "left",
                readOnly: !1,
                cookies: !1,
                promptChar: "_",
                inputMode: "advanced",
                rtl: !1,
                disabled: !1,
                events: ["valueChanged", "textchanged", "mousedown", "mouseup", "keydown", "keyup", "keypress", "change"],
                aria: {
                    "aria-valuenow": {
                        name: "value",
                        type: "string"
                    },
                    "aria-disabled": {
                        name: "disabled",
                        type: "boolean"
                    }
                }
            };
            t.extend(!0, this, e)
        },
        createInstance: function() {
            this.render()
        },
        render: function() {
            var e = this;
            e.host.attr({
                role: "textbox"
            }), e.host.attr("data-role", "input");
            var i = e.host.attr("value");
            void 0 != i && "" != i && (e.value = i), t.jqx.aria(this), t.jqx.aria(this, "aria-multiline", !1), t.jqx.aria(this, "aria-readonly", e.readOnly), e.host.addClass(e.toThemeProperty("jqx-input jqx-maskedinput")), e.host.addClass(e.toThemeProperty("jqx-rc-all")), e.host.addClass(e.toThemeProperty("jqx-widget")), e.host.addClass(e.toThemeProperty("jqx-widget-content")), maskEditor = this, "div" == e.element.nodeName.toLowerCase() ? (e.element.innerHTML = "", e.maskbox = t("<input autocomplete='off' autocorrect='off' autocapitalize='off' spellcheck='false' type='textarea'/>").appendTo(e.host)) : (e.maskbox = e.host, e.maskbox.attr("autocomplete", "off"), e.maskbox.attr("autocorrect", "off"), e.maskbox.attr("autocapitalize", "off"), e.maskbox.attr("spellcheck", !1)), e.maskbox.addClass(e.toThemeProperty("jqx-reset")), e.maskbox.addClass(e.toThemeProperty("jqx-input-content")), e.maskbox.addClass(e.toThemeProperty("jqx-widget-content"));
            var s = e.host.attr("name");
            s && e.maskbox.attr("name", s), e.rtl && e.maskbox.addClass(e.toThemeProperty("jqx-rtl"));
            var a = this;
            e.propertyChangeMap.disabled = function(t, e, i, s) {
                s ? t.maskbox.addClass(a.toThemeProperty("jqx-input-disabled")) : t.maskbox.removeClass(a.toThemeProperty("jqx-input-disabled"))
            }, e.disabled && (e.maskbox.addClass(e.toThemeProperty("jqx-input-disabled")), e.maskbox.attr("disabled", !0), e.host.addClass(e.toThemeProperty("jqx-fill-state-disabled"))), e.selectedText = "", e.self = this, e.oldValue = e._value(), e.items = new Array, e._initializeLiterals(), e._render(), null != e.value && e.inputValue(e.value.toString());
            var a = this;
            if (e.host.parents("form").length > 0 && e.host.parents("form").on("reset", function() {
                    setTimeout(function() {
                        a.clearValue()
                    }, 10)
                }), e.addHandlers(), e.cookies) {
                var r = t.jqx.cookie.cookie("maskedInput." + e.element.id);
                r && e.val(r)
            }
        },
        addHandlers: function() {
            var e = this;
            t.jqx.mobile.isTouchDevice() && (this.inputMode = "simple");
            var i = "",
                s = function(t, e) {
                    var i = String.fromCharCode(e),
                        s = parseInt(i),
                        a = !0;
                    if (!isNaN(s)) {
                        a = !0;
                        var r = this.maskbox.val().toString().length;
                        r >= this._getEditStringLength() && 0 == this._selection().length && (a = !1)
                    }
                    return t.ctrlKey || t.shiftKey || e >= 65 && 90 >= e && (a = !1), a
                };
            this.addHandler(this.maskbox, "blur", function(s) {
                return "simple" == e.inputMode ? (e._exitSimpleInputMode(s, e, !1, i), !1) : (e.rtl && e.maskbox.css("direction", "ltr"), e.host.removeClass(e.toThemeProperty("jqx-fill-state-focus")), void(e.maskbox.val() != i && (e._raiseEvent(7, s), e.cookies && t.jqx.cookie.cookie("maskedInput." + e.element.id, e.maskbox.val()))))
            }), this.addHandler(this.maskbox, "focus", function() {
                return i = e.maskbox.val(), "simple" == e.inputMode ? (e.maskbox[0].value = e._getEditValue(), t.data(e.maskbox, "simpleInputMode", !0), !1) : (e.rtl && e.maskbox.css("direction", "rtl"), void e.host.addClass(e.toThemeProperty("jqx-fill-state-focus")))
            }), this.addHandler(this.host, "keydown", function(t) {
                var i = e.readOnly,
                    a = t.charCode ? t.charCode : t.keyCode ? t.keyCode : 0;
                if (i || e.disabled) return !1;
                if ("simple" != e.inputMode) {
                    var r = e._handleKeyDown(t, a);
                    return r || (t.preventDefault && t.preventDefault(), t.stopPropagation && t.stopPropagation()), r
                }
                return s.call(e, t, a)
            }), this.addHandler(this.host, "keyup", function(t) {
                var i = e.readOnly,
                    a = t.charCode ? t.charCode : t.keyCode ? t.keyCode : 0;
                return i || e.disabled ? !0 : "simple" == e.inputMode ? s.call(e, t, a) : (t.preventDefault && t.preventDefault(), t.stopPropagation && t.stopPropagation(), !1)
            }), this.addHandler(this.host, "keypress", function(t) {
                var i = e.readOnly,
                    a = t.charCode ? t.charCode : t.keyCode ? t.keyCode : 0;
                if (i || e.disabled) return !0;
                if ("simple" == e.inputMode) return s.call(e, t, a);
                var r = e._handleKeyPress(t, a);
                return r || (t.preventDefault && t.preventDefault(), t.stopPropagation && t.stopPropagation()), r
            })
        },
        focus: function() {
            try {
                this.maskbox.focus()
            } catch (t) {}
        },
        _exitSimpleInputMode: function(e, i, s, a) {
            if (void 0 == i && (i = e.data), null != i) {
                if (void 0 == s) {
                    if (null != e.target && null != i.element && (void 0 != e.target.id && e.target.id.toString().length > 0 && i.host.find("#" + e.target.id).length > 0 || e.target == i.element)) return;
                    var r = i.host.offset(),
                        n = r.left,
                        o = r.top,
                        h = i.host.width(),
                        l = i.host.height(),
                        d = t(e.target).offset();
                    if (d.left >= n && d.left <= n + h && d.top >= o && d.top <= o + l) return
                }
                if (!i.disabled && !i.readOnly) {
                    var c = t.data(i.maskbox, "simpleInputMode");
                    if (null != c) {
                        for (var m = i.maskbox.val(), u = m.toString(), f = 0, p = 0; p < this.items.length; p++)
                            if (this.items[p].canEdit) {
                                if (!this._match(u.substring(f, f + 1), this.items[p].regex) && "" !== u.substring(f, f + 1)) return i.maskbox[0].value = a, t.data(i.maskbox, "simpleInputMode", null), !1;
                                f++
                            }
                        return i.inputValue(m, !0), t.data(i.maskbox, "simpleInputMode", null), !1
                    }
                }
            }
        },
        _getString: function() {
            for (var t = "", e = 0; e < this.items.length; e++) {
                var i = this.items[e].character;
                t += this.items[e].character == this.promptChar && this.promptChar != this.items[e].defaultCharacter ? this.items[e].defaultCharacter : i
            }
            return t
        },
        _initializeLiterals: function() {
            if (void 0 == this.mask || null == this.mask) return void(this.items = new Array);
            this.mask = this.mask.toString();
            for (var t = this.mask.length, e = 0; t > e; e++) {
                var i = this.mask.substring(e, e + 1),
                    s = "",
                    a = !1;
                if ("[" == i) {
                    for (var r = e; t > r; r++) {
                        var n = this.mask.substring(r, r + 1);
                        if ("]" == n) break
                    }
                    s = "(" + this.mask.substring(e, r + 1) + ")", e = r, a = !0
                }
                "#" == i ? (s = "(\\d|[+]|[-])", a = !0) : "9" == i || "0" == i ? (s = "\\d", a = !0) : "$" == i ? a = !1 : "/" == i || ":" == i ? a = !1 : "A" == i || "a" == i ? (s = "\\w", a = !0) : "c" == i || "C" == i ? (s = ".", a = !0) : ("L" == i || "l" == i) && (s = "([a-zA-Z])", a = !0);
                var o = this,
                    h = function(t, e, i) {
                        h.character = t, h.regex = e, h.canEdit = i, h.defaultCharacter = o.promptChar
                    };
                a ? h(this.promptChar, s, a) : h(i, s, a), this.items.push(h)
            }
        },
        setRegex: function(t, e, i, s) {
            null != t && void 0 != t && null != e && void 0 != e && t < this.items.length && (this.items[t].regex = e, null != i && void 0 != i && (this.items[t].canEdit = i), null != s && void 0 != s && (this.items[t].defaultCharacter = s))
        },
        _match: function(t, e) {
            var i = new RegExp(e, "i");
            return i.test(t)
        },
        _raiseEvent: function(e, i) {
            var s = this.events[e],
                a = {};
            a.owner = this;
            var r = (i.charCode ? i.charCode : i.keyCode ? i.keyCode : 0, !0),
                n = (this.readOnly, new t.Event(s));
            return n.owner = this, a.value = this.inputValue(), a.text = this.maskedValue(), n.args = a, (2 > e || e > 6) && (r = this.host.trigger(n)), r
        },
        _handleKeyPress: function(t, e) {
            var i = this._isSpecialKey(e, t);
            return i
        },
        _insertKey: function(e) {
            var i = this._selection(),
                s = this;
            if (i.start >= 0 && i.start < this.items.length) {
                var a = String.fromCharCode(e),
                    r = !1;
                t.each(this.items, function(t) {
                    if (!(t < i.start)) {
                        var e = s.items[t];
                        if (e.canEdit) {
                            if (s._match(a, e.regex)) {
                                if (!r && i.length > 0) {
                                    for (var n = i.start; n < i.end; n++) s.items[n].canEdit && (s.items[n].character = s.promptChar);
                                    var o = s._getString();
                                    s.maskedValue(o), r = !0
                                }
                                e.character = a;
                                var o = s._getString();
                                return s.maskedValue(o), i.start < s.items.length && s._setSelectionStart(t + 1), !1
                            }
                            return !1
                        }
                    }
                })
            }
        },
        _deleteSelectedText: function() {
            var t = this._selection(),
                e = !1;
            if (t.start > 0 || t.length > 0) {
                for (i = t.start; i < t.end; i++) i < this.items.length && this.items[i].canEdit && this.items[i].character != this.promptChar && (this.items[i].character = this.promptChar, e = !0);
                var s = this._getString();
                return this.maskedValue(s), e
            }
        },
        _saveSelectedText: function() {
            var e = this._selection(),
                s = "";
            if (e.start > 0 || e.length > 0)
                for (i = e.start; i < e.end; i++) this.items[i].canEdit && (s += this.items[i].character);
            if (window.clipboardData) window.clipboardData.setData("Text", s);
            else {
                var a = t('<textarea style="position: absolute; left: -1000px; top: -1000px;"/>');
                a.val(s), t("body").append(a), a.select(), setTimeout(function() {
                    document.designMode = "off", a.select(), a.remove()
                }, 100)
            }
            return s
        },
        _pasteSelectedText: function() {
            var e = this._selection(),
                s = 0,
                a = e.start,
                r = "",
                n = this,
                o = function(t) {
                    if (!(t != n.selectedText && t.length > 0 && (n.selectedText = t, null == n.selectedText || void 0 == n.selectedText))) {
                        if (e.start >= 0 || e.length > 0)
                            for (i = e.start; i < n.items.length; i++) n.items[i].canEdit && s < n.selectedText.length && (n.items[i].character = n.selectedText[s], s++, a = 1 + i);
                        var r = n._getString();
                        n.maskedValue(r), n._setSelectionStart(a < n.items.length ? a : n.items.length)
                    }
                };
            if (window.clipboardData) r = window.clipboardData.getData("Text"), o(r);
            else {
                var h = t('<textarea style="position: absolute; left: -1000px; top: -1000px;"/>');
                t("body").append(h), h.select();
                setTimeout(function() {
                    var t = h.val();
                    o(t), h.remove()
                }, 100)
            }
        },
        _handleKeyDown: function(e, i) {
            var s = this._selection();
            if (i >= 96 && 105 >= i && (i -= 48), e.ctrlKey && 97 == i || e.ctrlKey && 65 == i) return !0;
            if (e.ctrlKey && 120 == i || e.ctrlKey && 88 == i) return this.selectedText = this._saveSelectedText(e), this._deleteSelectedText(e), t.jqx.browser.msie ? !1 : !0;
            if (e.ctrlKey && 99 == i || e.ctrlKey && 67 == i) return this.selectedText = this._saveSelectedText(e), t.jqx.browser.msie ? !1 : !0;
            if (e.ctrlKey && 122 == i || e.ctrlKey && 90 == i) return !1;
            if (e.ctrlKey && 118 == i || e.ctrlKey && 86 == i || e.shiftKey && 45 == i) return this._pasteSelectedText(), t.jqx.browser.msie ? !1 : !0;
            if (s.start >= 0 && s.start < this.items.length) {
                String.fromCharCode(i), this.items[s.start]
            }
            if (8 == i) {
                if (0 == s.length)
                    for (n = this.items.length - 1; n >= 0; n--)
                        if (this.items[n].canEdit && n < s.end && this.items[n].character != this.promptChar) {
                            this._setSelection(n, n + 1);
                            break
                        }
                s = this._selection();
                var a = this._deleteSelectedText();
                return (s.start > 0 || s.length > 0) && s.start <= this.items.length && this._setSelectionStart(a ? s.start : s.start - 1), !1
            }
            if (190 == i)
                for (var r = s.start, n = r; n < this.items.length; n++)
                    if ("." == this.items[n].character) {
                        this._setSelectionStart(n + 1);
                        break
                    }
            if (191 == i)
                for (var r = s.start, n = r; n < this.items.length; n++)
                    if ("/" == this.items[n].character) {
                        this._setSelectionStart(n + 1);
                        break
                    }
            if (189 == i)
                for (var r = s.start, n = r; n < this.items.length; n++)
                    if ("-" == this.items[n].character) {
                        this._setSelectionStart(n + 1);
                        break
                    }
            if (46 == i) {
                if (0 == s.length)
                    for (var n = 0; n < this.items.length; n++)
                        if (this.items[n].canEdit && n >= s.start && this.items[n].character != this.promptChar) {
                            this._setSelection(n, n + 1);
                            break
                        }
                var o = s;
                s = this._selection(); {
                    this._deleteSelectedText()
                }
                return (s.start >= 0 || s.length >= 0) && s.start < this.items.length && this._setSelectionStart(s.length <= 1 ? o.end != s.end ? s.end : s.end + 1 : s.start), !1
            }
            this._insertKey(i);
            var h = this._isSpecialKey(i, e);
            return h
        },
        _isSpecialKey: function(t, e) {
            return 189 == t || 9 == t || 13 == t || 35 == t || 36 == t || 37 == t || 39 == t || 46 == t ? !0 : 16 === t && e.shiftKey || e.ctrlKey ? !0 : !1
        },
        _selection: function() {
            if ("selectionStart" in this.maskbox[0]) {
                var t = this.maskbox[0],
                    e = t.selectionEnd - t.selectionStart;
                return {
                    start: t.selectionStart,
                    end: t.selectionEnd,
                    length: e,
                    text: t.value
                }
            }
            var i = document.selection.createRange();
            if (null == i) return {
                start: 0,
                end: t.value.length,
                length: 0
            };
            var s = this.maskbox[0].createTextRange(),
                a = s.duplicate();
            s.moveToBookmark(i.getBookmark()), a.setEndPoint("EndToStart", s);
            var e = i.text.length;
            return {
                start: a.text.length,
                end: a.text.length + i.text.length,
                length: e,
                text: i.text
            }
        },
        _setSelection: function(t, e) {
            if ("selectionStart" in this.maskbox[0]) this.maskbox[0].focus(), this.maskbox[0].setSelectionRange(t, e);
            else {
                var i = this.maskbox[0].createTextRange();
                i.collapse(!0), i.moveEnd("character", e), i.moveStart("character", t), i.select()
            }
        },
        _setSelectionStart: function(t) {
            this._setSelection(t, t)
        },
        refresh: function(t) {
            t || this._render()
        },
        resize: function(t, e) {
            this.width = t, this.height = e, this.refresh()
        },
        _render: function() {
            var e = parseInt(this.host.css("border-left-width")),
                i = parseInt(this.host.css("border-left-width")),
                s = parseInt(this.host.css("border-left-width")),
                a = parseInt(this.host.css("border-left-width")),
                r = parseInt(this.host.css("height")) - s - a,
                n = parseInt(this.host.css("width")) - e - i;
            null != this.width && -1 != this.width.toString().indexOf("px") ? n = this.width : void 0 == this.width || isNaN(this.width) || (n = this.width), null != this.height && -1 != this.height.toString().indexOf("px") ? r = this.height : void 0 == this.height || isNaN(this.height) || (r = this.height), n = parseInt(n), r = parseInt(r), this.maskbox[0] != this.element && this.maskbox.css({
                "border-left-width": 0,
                "border-right-width": 0,
                "border-bottom-width": 0,
                "border-top-width": 0
            }), this.maskbox.css("text-align", this.textAlign);
            var o = this.maskbox.css("font-size");
            isNaN(r) || this.maskbox.css("height", parseInt(o) + 4 + "px"), isNaN(n) || this.maskbox.css("width", n - 2);
            var h = parseInt(r) - 2 * parseInt(s) - 2 * parseInt(a) - parseInt(o);
            if (isNaN(h) && (h = 0), isNaN(r) || this.host.height(r), isNaN(n) || this.host.width(n), this.maskbox[0] != this.element) {
                var l = h / 2;
                t.jqx.browser.msie && t.jqx.browser.version < 8 && (l = h / 4), this.maskbox.css("padding-right", "0px"), this.maskbox.css("padding-left", "0px"), this.maskbox.css("padding-top", l), this.maskbox.css("padding-bottom", h / 2)
            }
            this.maskbox[0].value = this._getString(), this.width && (this.width.toString().indexOf("%") >= 0 && (this.element.style.width = this.width), this.height.toString().indexOf("%") >= 0 && (this.element.style.height = this.height))
        },
        destroy: function() {
            this.host.remove()
        },
        maskedValue: function(t) {
            return void 0 === t ? this._value() : (this.value = t, this._refreshValue(), this.oldValue !== t && (this._raiseEvent(1, t), this.oldValue = t, this._raiseEvent(0, t)), this)
        },
        _value: function() {
            var t = this.maskbox.val();
            return t
        },
        propertyChangedHandler: function(e, s, a, r) {
            if (void 0 != this.isInitialized && 0 != this.isInitialized) {
                if ("rtl" == s && (e.rtl ? e.maskbox.addClass(e.toThemeProperty("jqx-rtl")) : e.maskbox.removeClass(e.toThemeProperty("jqx-rtl"))), "value" === s && ((void 0 == r || null == r) && (r = ""), "" === r ? this.clear() : (r = r.toString(), this.inputValue(r)), e._raiseEvent(7, event)), "theme" === s && t.jqx.utilities.setTheme(a, r, this.host), "disabled" == s && (r ? (e.maskbox.addClass(e.toThemeProperty("jqx-input-disabled")), e.host.addClass(e.toThemeProperty("jqx-fill-state-disabled")), e.maskbox.attr("disabled", !0)) : (e.host.removeClass(this.toThemeProperty("jqx-fill-state-disabled")), e.host.removeClass(this.toThemeProperty("jqx-input-disabled")), e.maskbox.attr("disabled", !1)), t.jqx.aria(e, "aria-disabled", r)), "readOnly" == s && (this.readOnly = r), "promptChar" == s) {
                    for (i = 0; i < e.items.length; i++) e.items[i].character == e.promptChar && (e.items[i].character = r, e.items[i].defaultCharacter = r);
                    e.promptChar = r
                }
                "textAlign" == s && (e.maskbox.css("text-align", r), e.textAlign = r), "mask" == s && (e.mask = r, e.items = new Array, e._initializeLiterals(), e.value = e._getString(), e._refreshValue()), "width" == s ? (e.width = r, e._render()) : "height" == s && (e.height = r, e._render())
            }
        },
        _value: function() {
            var t = this.value;
            return t
        },
        _getEditStringLength: function() {
            var t = "";
            for (i = 0; i < this.items.length; i++) this.items[i].canEdit && (t += this.items[i].character);
            return t.length
        },
        _getEditValue: function() {
            var t = "";
            for (i = 0; i < this.items.length; i++) this.items[i].canEdit && this.items[i].character != this.promptChar && (t += this.items[i].character);
            return t
        },
        parseValue: function(t) {
            if (void 0 == t || null == t) return null;
            var e = t.toString(),
                s = "",
                a = 0;
            for (m = 0; m < e.length; m++) {
                var r = e.substring(m, m + 1);
                for (i = a; i < this.items.length; i++)
                    if (this.items[i].canEdit && this._match(r, this.items[i].regex)) {
                        s += r, a = i;
                        break
                    }
            }
            return s
        },
        clear: function() {
            this.clearValue()
        },
        clearValue: function() {
            this.inputValue("", !0)
        },
        valNoPrompt: function(t) {
            void 0 != t && "object" != typeof t && ("number" == typeof t && isFinite(t) && (t = t.toString()), this.maskedValue(t));
            var e = this.maskbox[0].value;
            e = e.split('_').join('');
            return e = e.replace(/[^\w\s]/gi, "")
        },
        val: function(t) {
            return void 0 != t && "object" != typeof t && ("number" == typeof t && isFinite(t) && (t = t.toString()), this.maskedValue(t)), this.maskbox[0].value
        },
        inputValue: function(t, e) {
            if (void 0 == t || null == t) {
                for (var i = "", s = 0; s < this.items.length; s++) this.items[s].canEdit && (i += this.items[s].character);
                return i
            }
            var a = 0;
            t = t.toString();
            for (var s = 0; s < this.items.length; s++) this.items[s].canEdit && (this._match(t.substring(a, a + 1), this.items[s].regex) ? (this.items[s].character = t.substring(a, a + 1), a++) : e && (this.items[s].character = this.promptChar, a++));
            var r = this._getString();
            return this.maskedValue(r), this.inputValue()
        },
        _refreshValue: function() {
            for (var e = this.maskedValue(), i = 0, s = 0; s < this.items.length; s++) e.length > i && (this.items[s].canEdit && this.items[s].character != e[i] && (!this._match(e[i], this.items[s].regex) && e[i] != this.promptChar || 1 != e[i].length || (this.items[s].character = e[i])), i++);
            this.value = this._getString(), e = this.value, this.maskbox[0].value = e, t.jqx.aria(this, "aria-valuenow", e)
        }
    })
}(jqxBaseFramework);