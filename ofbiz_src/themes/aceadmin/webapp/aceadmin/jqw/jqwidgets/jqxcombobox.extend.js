/*
jQWidgets v4.0.0 (2016-Jan)
Copyright (c) 2011-2016 jQWidgets.
License: http://jqwidgets.com/license/
*/
(function(a) {
    a.extend(a.jqx._jqxComboBox.prototype, {
    	_search: function(m) {
            var d = this;
            if (m.keyCode == 9) {
                return
            }
            if (d.searchMode == "none" || d.searchMode == null || d.searchMode == "undefined") {
                return
            }
            if (m.keyCode == 16 || m.keyCode == 17 || m.keyCode == 20) {
                return
            }
            if (d.checkboxes) {
                return
            }
            if (d.multiSelect) {
                var n = a("<span style='visibility: hidden; white-space: nowrap;'>" + d.input.val() + "</span>");
                n.addClass(d.toThemeProperty("jqx-widget"));
                a(document.body).append(n);
                var j = n.width() + 15;
                n.remove();
                if (j > d.host.width()) {
                    j = d.host.width()
                }
                if (j < 25) {
                    j = 25
                }
                d.input.css("width", j + "px");
                if (d.selectedItems.length == 0) {
                    d.input.css("width", "100%");
                    d.input.attr("placeholder", d.placeHolder)
                } else {
                    d.input.attr("placeholder", "")
                }
                var f = parseInt(this._findPos(d.host[0])[1]) + parseInt(d.host.outerHeight()) - 1 + "px";
                var o = a.jqx.mobile.isSafariMobileBrowser() || a.jqx.mobile.isWindowsPhone();
                if ((o != null && o) ) {
                    f = a.jqx.mobile.getTopPos(this.element) + parseInt(d.host.outerHeight());
                    if (a("body").css("border-top-width") != "0px") {
                        f = parseInt(f) - this._getBodyOffset().top + "px"
                    }
                }
                d.container.css("top", f);
                var i = parseInt(d.host.height());
                d.dropdownlistArrow.height(i)
            }
            if (!d.isanimating) {
                if (m.altKey && m.keyCode == 38) {
                    d.hideListBox("altKey");
                    return false
                }
                if (m.altKey && m.keyCode == 40) {
                    if (!d.isOpened()) {
                        d.showListBox("altKey")
                    }
                    return false
                }
            }
            if (m.keyCode == 37 || m.keyCode == 39) {
                return false
            }
            if (m.altKey || m.keyCode == 18) {
                return
            }
            if (m.keyCode >= 33 && m.keyCode <= 40) {
                return
            }
            if (m.ctrlKey || d.ctrlKey) {
                if (m.keyCode != 88 && m.keyCode != 86) {
                    return
                }
            }
            var l = d.input.val();
            if (l.length == 0 && !d.autoComplete) {
                d.listBox.searchString = d.input.val();
                d.listBox.clearSelection();
                d.hideListBox("search");
                d.searchString = d.input.val();
                return
            }
            if (d.remoteAutoComplete) {
                var s = this;
                var p = function() {
                    s.listBox.vScrollInstance.value = 0
                }
                ;
                if (l.length >= s.minLength) {
                    if (!m.ctrlKey && !m.altKey) {
                        if (s.searchString != l) {
                            var k = s.listBoxContainer.jqxListBox("source");
                            if (k == null ) {
                                s.listBoxContainer.jqxListBox({
                                    source: s.source
                                })
                            }
                            if (d._searchTimer) {
                                clearTimeout(d._searchTimer)
                            }
                            if (m.keyCode != 13 && m.keyCode != 27) {
                                d._searchTimer = setTimeout(function() {
                                    p();
                                    if (s.autoDropDownHeight) {
                                        s.listBox.autoHeight = true
                                    }
                                    s.searchString = s.input.val();
                                    if (s.search != null ) {
                                        s.search(s.input.val())
                                    } else {
                                        throw "'search' function is not defined"
                                    }
                                }, d.remoteAutoCompleteDelay)
                            }
                        }
                        s.searchString = l
                    }
                } else {
                    if (d._searchTimer) {
                        clearTimeout(d._searchTimer)
                    }
                    p();
                    s.searchString = "";
                    s.listBoxContainer.jqxListBox({
                        source: null
                    })
                }
                return
            }
            var s = this;
            if (l === s.searchString) {
                return
            }
            if (!(m.keyCode == "27" || m.keyCode == "13")) {
                var b = d._updateItemsVisibility(l);
                var e = b.matchItems;
                var c = b.index;
                if (!d.autoComplete && !d.removeAutoComplete) {
                    if (!d.multiSelect || (d.multiSelect && c >= 0)) {
                        d.listBox.selectIndex(c);
                        var r = d.listBox.isIndexInView(c);
                        if (!r) {
                            d.listBox.ensureVisible(c)
                        } else {
                            d.listBox._renderItems()
                        }
                    }
                }
                if (d.autoComplete && e.length === 0) {
                    d.hideListBox("search")
                }
            }
            if (m.keyCode == "13") {
                var g = d.container.css("display") == "block";
                if (g && !d.isanimating) {
                    d.hideListBox("keyboard");
                    d._oldvalue = d.listBox.selectedValue;
                    return
                }
            } else {
                if (m.keyCode == "27") {
                    var g = d.container.css("display") == "block";
                    if (g && !d.isanimating) {
                        if (!h.multiSelect) {
                            var q = d.listBox.getVisibleItem(d._oldvalue);
                            if (q) {
                                var h = this;
                                setTimeout(function() {
                                    if (h.autoComplete) {
                                        h._updateItemsVisibility("")
                                    }
                                    h.listBox.selectIndex(q.index);
                                    h.renderSelection("api")
                                }, h.closeDelay)
                            } else {
                                d.clearSelection()
                            }
                        } else {
                            h.input.val("");
                            h.listBox.selectedValue = null
                        }
                        d.hideListBox("keyboard");
                        d.renderSelection("api");
                        m.preventDefault();
                        return false
                    }
                } else {
                    if (!d.isOpened() && !d.opening && !m.ctrlKey) {
                        if (d.listBox.visibleItems && d.listBox.visibleItems.length > 0) {
                            if (d.input.val() != d.searchString && d.searchString != undefined && c != -1) {
                                d.showListBox("search")
                            }
                        }
                    }
                    d.searchString = d.input.val();
                    if (d.searchString == "") {
                        if (!d.listBox.itemsByValue[""]) {
                            c = -1;
                            if (!d.multiSelect) {
                                d.clearSelection()
                            }
                        }
                    }
                    var q = d.listBox.getVisibleItem(c);
                    if (q != undefined) {
                        d._updateInputSelection()
                    }
                }
            }
        },
        val: function(c) {
            if (!this.input) {
                return ""
            }
            var d = function(f) {
                for (var e in f) {
                    if (f.hasOwnProperty(e)) {
                        return false
                    }
                }
                if (typeof c == "number") {
                    return false
                }
                if (typeof c == "date") {
                    return false
                }
                if (typeof c == "boolean") {
                    return false
                }
                if (typeof c == "string") {
                    return false
                }
                return true
            }
            ;
            if (d(c) || arguments.length == 0) {
                var b = this.getSelectedItem();
                if (b) {
                    return b.value
                }
                return this.input.val()
            } else {
                var b = this.getItemByValue(c);
                if (b != null ) {
                    this.selectItem(b)
                } else {
                    this.input.val(c)
                }
                return this.input.val()
            }
        },
    })
})(jqxBaseFramework);