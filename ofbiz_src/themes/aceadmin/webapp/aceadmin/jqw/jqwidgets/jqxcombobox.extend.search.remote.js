/*
jQWidgets v4.0.0 (2016-Jan)
Copyright (c) 2011-2016 jQWidgets.
License: http://jqwidgets.com/license/
*/
(function(a) {
    a.extend(a.jqx._jqxComboBox.prototype, {
    	defineInstance: function() {
            var b = {
                disabled: false,
                width: 200,
                height: 25,
                items: new Array(),
                selectedIndex: -1,
                selectedItems: new Array(),
                _selectedItems: new Array(),
                source: null,
                scrollBarSize: a.jqx.utilities.scrollBarSize,
                arrowSize: 18,
                enableHover: true,
                enableSelection: true,
                visualItems: new Array(),
                groups: new Array(),
                equalItemsWidth: true,
                itemHeight: -1,
                visibleItems: new Array(),
                emptyGroupText: "Group",
                emptyString: "",
                openDelay: 250,
                closeDelay: 300,
                animationType: "default",
                dropDownWidth: "auto",
                dropDownHeight: "200px",
                autoDropDownHeight: false,
                enableBrowserBoundsDetection: false,
                dropDownHorizontalAlignment: "left",
                searchMode: "startswithignorecase",
                autoComplete: false,
                remoteAutoComplete: false,
                remoteAutoCompleteDelay: 500,
                selectionMode: "default",
                minLength: 2,
                displayMember: "",
                valueMember: "",
                groupMember: "",
                searchMember: "",
                keyboardSelection: true,
                renderer: null,
                autoOpen: false,
                checkboxes: false,
                promptText: "",
                placeHolder: "",
                rtl: false,
                listBox: null,
                validateSelection: null,
                showCloseButtons: true,
                renderSelectedItem: null,
                search: null,
                popupZIndex: 100000,
                searchString: null,
                multiSelect: false,
                showArrow: true,
                _disabledItems: new Array(),
                touchMode: "auto",
                autoBind: true,
                aria: {
                    "aria-disabled": {
                        name: "disabled",
                        type: "boolean"
                    }
                },
                events: ["open", "close", "select", "unselect", "change", "checkChange", "bindingComplete"],
                autoShowListBox: true // extend for olb.combobox.search.remote
            };
            a.extend(true, this, b);
            return b
        },
        showListBox: function(l) {
        	if (!this.autoShowListBox) {
                return;
            }
            if (this.listBox.items && this.listBox.items.length == 0) {
                return
            }
            if (l == "search" && !this.autoComplete && !this.remoteAutoComplete) {
                if (this.autoDropDownHeight) {
                    this.container.height(this.listBoxContainer.height() + 25)
                }
            }
            if (this.autoComplete || this.multiSelect && !this.remoteAutoComplete) {
                if (l != "search") {
                    this._updateItemsVisibility("");
                    if (this.multiSelect) {
                        var p = this.getVisibleItems();
                        for (var t = 0; t < p.length; t++) {
                            if (!p[t].disabled) {
                                this.ensureVisible(t);
                                break
                            }
                        }
                    }
                }
            }
            if (this.remoteAutoComplete) {
                this.listBox.clearSelection()
            }
            if (l != "search") {
                this._oldvalue = this.listBox.selectedValue
            }
            a.jqx.aria(this, "aria-expanded", true);
            if (this.dropDownWidth == "auto" && this.width != null && this.width.indexOf && this.width.indexOf("%") != -1) {
                if (this.listBox.host.width() != this.host.width()) {
                    var r = this.host.width();
                    this.listBoxContainer.jqxListBox({
                        width: r
                    });
                    this.container.width(parseInt(r) + 25)
                }
            }
            var o = this;
            var h = this.listBoxContainer;
            var v = this.listBox;
            var e = a(window).scrollTop();
            var f = a(window).scrollLeft();
            var m = parseInt(this._findPos(this.host[0])[1]) + parseInt(this.host.outerHeight()) - 1 + "px";
            var d, q = parseInt(Math.round(this.host.coord(true).left));
            d = q + "px";
            var u = a.jqx.mobile.isSafariMobileBrowser() || a.jqx.mobile.isWindowsPhone();
            this.ishiding = false;
            var g = a.jqx.utilities.hasTransform(this.host);
            if (g || (u != null && u)) {
                d = a.jqx.mobile.getLeftPos(this.element);
                m = a.jqx.mobile.getTopPos(this.element) + parseInt(this.host.outerHeight());
                if (a("body").css("border-top-width") != "0px") {
                    m = parseInt(m) - this._getBodyOffset().top + "px"
                }
                if (a("body").css("border-left-width") != "0px") {
                    d = parseInt(d) - this._getBodyOffset().left + "px"
                }
            }
            this.host.addClass(this.toThemeProperty("jqx-combobox-state-selected"));
            this.dropdownlistArrowIcon.addClass(this.toThemeProperty("jqx-icon-arrow-down-selected"));
            this.dropdownlistArrow.addClass(this.toThemeProperty("jqx-combobox-arrow-selected"));
            this.dropdownlistArrow.addClass(this.toThemeProperty("jqx-fill-state-pressed"));
            this.host.addClass(this.toThemeProperty("jqx-combobox-state-focus"));
            this.host.addClass(this.toThemeProperty("jqx-fill-state-focus"));
            this.dropdownlistContent.addClass(this.toThemeProperty("jqx-combobox-content-focus"));
            this.container.css("left", d);
            this.container.css("top", m);
            v._arrange();
            var c = true;
            var b = false;
            if (this.dropDownHorizontalAlignment == "right" || this.rtl) {
                var k = this.container.outerWidth();
                var s = Math.abs(k - this.host.width());
                if (k > this.host.width()) {
                    this.container.css("left", 25 + parseInt(Math.round(q)) - s + "px")
                } else {
                    this.container.css("left", 25 + parseInt(Math.round(q)) + s + "px")
                }
            }
            if (this.enableBrowserBoundsDetection) {
                var j = this.testOffset(h, {
                    left: parseInt(this.container.css("left")),
                    top: parseInt(m)
                }, parseInt(this.host.outerHeight()));
                if (parseInt(this.container.css("top")) != j.top) {
                    b = true;
                    h.css("top", 23);
                    h.addClass(this.toThemeProperty("jqx-popup-up"))
                } else {
                    h.css("top", 0)
                }
                this.container.css("top", j.top);
                this.container.css("top", j.top);
                if (parseInt(this.container.css("left")) != j.left) {
                    this.container.css("left", j.left)
                }
            }
            if (this.animationType == "none") {
                this.container.css("display", "block");
                a.data(document.body, "openedCombojqxListBoxParent", o);
                a.data(document.body, "openedCombojqxListBox" + o.element.id, h);
                h.css("margin-top", 0);
                h.css("opacity", 1)
            } else {
                this.container.css("display", "block");
                var n = h.outerHeight();
                h.stop();
                if (this.animationType == "fade") {
                    h.css("margin-top", 0);
                    h.css("opacity", 0);
                    h.animate({
                        opacity: 1
                    }, this.openDelay, function() {
                        o.isanimating = false;
                        o.opening = false;
                        a.data(document.body, "openedCombojqxListBoxParent", o);
                        a.data(document.body, "openedCombojqxListBox" + o.element.id, h)
                    })
                } else {
                    h.css("opacity", 1);
                    if (b) {
                        h.css("margin-top", n)
                    } else {
                        h.css("margin-top", -n)
                    }
                    this.isanimating = true;
                    this.opening = true;
                    h.animate({
                        "margin-top": 0
                    }, this.openDelay, function() {
                        o.isanimating = false;
                        o.opening = false;
                        a.data(document.body, "openedCombojqxListBoxParent", o);
                        a.data(document.body, "openedCombojqxListBox" + o.element.id, h)
                    })
                }
            }
            v._renderItems();
            if (!b) {
                this.host.addClass(this.toThemeProperty("jqx-rc-b-expanded"));
                h.addClass(this.toThemeProperty("jqx-rc-t-expanded"));
                this.dropdownlistArrow.addClass(this.toThemeProperty("jqx-rc-b-expanded"))
            } else {
                this.host.addClass(this.toThemeProperty("jqx-rc-t-expanded"));
                h.addClass(this.toThemeProperty("jqx-rc-b-expanded"));
                this.dropdownlistArrow.addClass(this.toThemeProperty("jqx-rc-t-expanded"))
            }
            h.addClass(this.toThemeProperty("jqx-fill-state-focus"));
            this._raiseEvent("0", v)
        },
    })
})(jqxBaseFramework);