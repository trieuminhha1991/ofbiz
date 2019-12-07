 /*
jQWidgets v3.5.0 (2014-Sep-15)
Copyright (c) 2011-2014 jQWidgets.
License: http://jqwidgets.com/license/
*/
(function(a) {
    a.extend(a.jqx._jqxGrid.prototype, {_updatefilterrowui: function(f) {
            var m = this.columns.records.length;
            var e = 0;
            var l = this;
            for (var i = 0; i < m; i++) {
                var g = this.columns.records[i];
                var c = g.width;
                if (c < g.minwidth) {
                    c = g.minwidth
                }
                if (c > g.maxwidth) {
                    c = g.maxwidth
                }
                var k = a(this.filterrow[0].cells[i]);
                k.css("left", e);
                var h = true;
                if (k.width() == c) {
                    h = false
                }
                if (f) {
                    h = true
                }
                k.width(c);
                k[0].left = e;
                if (!(g.hidden && g.hideable)) {
                    e += c
                } else {
                    k.css("display", "none")
                }
                if (!h) {
                    continue
                }
                if (g.createfilterwidget && g.filtertype == "custom") {
                    g.createfilterwidget(g, k)
                } else {
                    if (g.filterable) {
                        var d = function(n, o) {
                            var j = a(o.children()[0]);
                            j.width(c - 10);
                            j.attr("disabled", n.disabled)
                        };
                        switch (g.filtertype) {
                            case "number":
                            case "input":
                                a(k.children()[0]).width(c);
                                k.find("input").width(c - 30);
                                k.find("input").attr("disabled", l.disabled);
                                a(k.find(".jqx-dropdownlist-state-normal")).jqxDropDownList({disabled: l.disabled});
                                break;
                            case "date":
                            case "range":
                                if (this.host.jqxDateTimeInput) {
                                    a(k.children()[0]).jqxDateTimeInput({disabled: l.disabled,width: c - 10})
                                } else {
                                    d(this, k)
                                }
                                break;
                            case "textbox":
                            case "default":
                                d(this, k);
                                break;
                            case "list":
                            case "checkedlist":
                                if (this.host.jqxDropDownList) {
                                    a(k.children()[0]).jqxDropDownList({disabled: l.disabled,width: c - 10})
                                } else {
                                    d(this, k)
                                }
                                break;
                            case "olbiusdropgrid":
                                if (this.host.jqxDropDownButton) {
                                    a(k.children()[0]).jqxInput({disabled: l.disabled,width: c - 10})
                                } else {
                                    d(this, k)
                                }
                                break;
                            case "bool":
                            case "boolean":
                                if (!this.host.jqxCheckBox) {
                                    d(this, k)
                                } else {
                                    a(k.children()[0]).jqxCheckBox({disabled: l.disabled})
                                }
                                break
                        }
                    }
                }
            }
            var b = a(this.filterrow.children()[0]);
            b.width(parseInt(e) + 2);
            b.height(this.filterrowheight)
        },clearfilterrow: function(e) {
            this._disablefilterrow = true;
            if (!this.columns.records) {
                return
            }
            var b = this.columns.records.length;
            var g = 0;
            for (var d = 0; d < b; d++) {
                var c = this.columns.records[d];
                var h = a(this.filterrow[0].cells[d]);
                if (typeof e == "string") {
                    if (c.displayfield != e) {
                        continue
                    }
                }
                if (c.filterable) {
                    var f = function(j, k) {
                        var i = a(k.children()[0]);
                        i.val("");
                        if (i[0]) {
                            j["_oldWriteText" + i[0].id] = ""
                        }
                    };
                    switch (c.filtertype) {
                        case "number":
                        case "input":
                            h.find("input").val("");
                            break;
                        case "date":
                        case "range":
                            if (this.host.jqxDateTimeInput) {
                                a(h.children()[0]).jqxDateTimeInput("setDate", null)
                            } else {
                                f(this, h)
                            }
                            break;
                        case "textbox":
                        case "default":
                            f(this, h);
                            break;
                        case "list":
                            if (this.host.jqxDropDownList) {
                                a(h.children()[0]).jqxDropDownList("clearSelection")
                            } else {
                                f(this, h)
                            }
                            break;
                        case "checkedlist":
                            if (this.host.jqxDropDownList) {
                                a(h.children()[0]).jqxDropDownList("checkAll", false)
                            } else {
                                f(this, h)
                            }
                            break;
                        case "olbiusdropgrid":
                            if (this.host.jqxDropDownButton) {
                                a(h.children()[0]).jqxInput("val", '')
                            } else {
                                f(this, h)
                            }
                            break;
                        case "bool":
                        case "boolean":
                            if (!this.host.jqxCheckBox) {
                                f(this, h)
                            } else {
                                a(h.children()[0]).jqxCheckBox({checked: null})
                            }
                            break
                    }
                }
            }
            this._disablefilterrow = false
        },_applyfilterfromfilterrow: function() {
            if (this._disablefilterrow == true) {
                return
            }
            if (this.disabled) {
                return
            }
            var z = this.columns.records.length;
            var C = this.that;
            for (var t = 0; t < z; t++) {
                var k = new a.jqx.filter();
                var u = this.columns.records[t];
                if (!u.filterable) {
                    continue
                }
                if (u.datafield === null) {
                    continue
                }
                var f = C._getcolumntypebydatafield(u);
                var d = C._getfiltertype(f);
                var l = 1;
                var D = true;
                var e = u.filtertype;
                var A = function(j, K, H) {
                    var i = true;
                    if (j._filterwidget) {
                        var F = j._filterwidget.val();
                        if (F != "") {
                            var I = "equal";
                            if (K == "stringfilter") {
                                var I = "contains"
                            }
                            if (K == "numericfilter") {
                                if (C.gridlocalization.decimalseparator == ",") {
                                    if (F.indexOf(C.gridlocalization.decimalseparator) >= 0) {
                                        F = F.replace(C.gridlocalization.decimalseparator, ".")
                                    }
                                }
                            }
                            if (K != "stringfilter") {
                                var J = 0;
                                if (F.indexOf(">") != -1) {
                                    I = "greater_than";
                                    J = 1
                                }
                                if (F.indexOf("<") != -1) {
                                    I = "less_than";
                                    J = 1
                                }
                                if (F.indexOf("=") != -1) {
                                    if (I == "greater_than") {
                                        I = "greater_than_or_equal";
                                        J = 2
                                    } else {
                                        if (I == "less_than") {
                                            I = "less_than_or_equal";
                                            J = 2
                                        } else {
                                            I = "equal";
                                            J = 1
                                        }
                                    }
                                }
                                if (J != 0) {
                                    F = F.substring(J);
                                    if (F.length < 1) {
                                        return false
                                    }
                                }
                            }
                            if (j.filtercondition != undefined) {
                                I = j.filtercondition
                            }
                            if (K == "datefilter") {
                                var G = H.createfilter(K, F, I, null, j.cellsformat, C.gridlocalization)
                            } else {
                                var G = H.createfilter(K, F, I)
                            }
                            H.addfilter(l, G)
                        } else {
                            i = false
                        }
                    }
                    return i
                };
                switch (u.filtertype) {
                    case "range":
                    case "date":
                        if (u._filterwidget.jqxDateTimeInput) {
                            if (u.filtertype == "range") {
                                var p = u._filterwidget.jqxDateTimeInput("getRange");
                                if (p != null && p.from != null && p.to != null) {
                                    var o = "GREATER_THAN_OR_EQUAL";
                                    var r = new Date(0);
                                    r.setHours(0);
                                    r.setMinutes(0);
                                    r.setFullYear(p.from.getFullYear(), p.from.getMonth(), p.from.getDate());
                                    var q = new Date(0);
                                    q.setHours(0);
                                    q.setMinutes(0);
                                    q.setFullYear(p.to.getFullYear(), p.to.getMonth(), p.to.getDate());
                                    q.setHours(p.to.getHours());
                                    q.setMinutes(p.to.getMinutes());
                                    q.setSeconds(p.to.getSeconds());
                                    var y = k.createfilter(d, r, o);
                                    k.addfilter(0, y);
                                    var c = "LESS_THAN_OR_EQUAL";
                                    var x = k.createfilter(d, q, c);
                                    k.addfilter(0, x);
                                } else {
                                    D = false
                                }
                            } else {
                                var p = u._filterwidget.jqxDateTimeInput("getDate");
                                if (p != null) {
                                    var r = new Date(0);
                                    r.setHours(0);
                                    r.setMinutes(0);
                                    r.setFullYear(p.getFullYear(), p.getMonth(), p.getDate());
                                    var y = k.createfilter(d, r, "EQUAL");
                                    k.addfilter(0, y)
                                } else {
                                    D = false
                                }
                            }
                        } else {
                            D = A(u, d, k)
                        }
                        break;
                    case "input":
                        if (u._filterwidget) {
                            var p = u._filterwidget.find("input").val();
                            var h = u._filterwidget.find(".filter").jqxDropDownList("selectedIndex");
                            var w = k.getoperatorsbyfiltertype(d)[h];
                            if (C.updatefilterconditions) {
                                var E = C.updatefilterconditions(d, k.getoperatorsbyfiltertype(d));
                                if (E != undefined) {
                                    k.setoperatorsbyfiltertype(d, E)
                                }
                                var w = k.getoperatorsbyfiltertype(d)[h]
                            }
                            var n = w == "NULL" || w == "NOT_NULL";
                            var s = w == "EMPTY" || w == "NOT_EMPTY";
                            if (p != undefined && p.length > 0 || n || s) {
                                y = k.createfilter(d, p, w, null, u.cellsformat, C.gridlocalization);
                                k.addfilter(0, y)
                            } else {
                                D = false
                            }
                        } else {
                            D = false
                        }
                        break;
                    case "number":
                        if (u._filterwidget) {
                            var p = u._filterwidget.find("input").val();
                            if (C.gridlocalization.decimalseparator == ",") {
                                if (p.indexOf(C.gridlocalization.decimalseparator) >= 0) {
                                    p = p.replace(C.gridlocalization.decimalseparator, ".")
                                }
                            }
                            var h = u._filterwidget.find(".filter").jqxDropDownList("selectedIndex");
                            var w = k.getoperatorsbyfiltertype(d)[h];
                            if (C.updatefilterconditions) {
                                var E = C.updatefilterconditions(d, k.getoperatorsbyfiltertype(d));
                                if (E != undefined) {
                                    k.setoperatorsbyfiltertype(d, E)
                                }
                                var w = k.getoperatorsbyfiltertype(d)[h]
                            }
                            var n = w == "NULL" || w == "NOT_NULL";
                            var s = w == "EMPTY" || w == "NOT_EMPTY";
                            if (p != undefined && p.length > 0 || n || s) {
                                y = k.createfilter(d, new Number(p), w, null, u.cellsformat, C.gridlocalization);
                                k.addfilter(0, y)
                            } else {
                                D = false
                            }
                        } else {
                            D = false
                        }
                        break;
                    case "textbox":
                    case "default":
                        D = A(u, d, k);
                        break;
                    case "bool":
                    case "boolean":
                        if (u._filterwidget.jqxCheckBox) {
                            var p = u._filterwidget.jqxCheckBox("checked");
                            if (p != null) {
                                var o = "equal";
                                var m = k.createfilter(d, p, o);
                                k.addfilter(l, m)
                            } else {
                                D = false
                            }
                        } else {
                            D = A(u, d, k)
                        }
                        break;
                    case "list":
                        var g = u._filterwidget.jqxDropDownList("listBox");
                        if (g.selectedIndex > 0) {
                            var b = g.selectedValue;//g.getItem(g.selectedIndex);
                            var p = b;
                            var o = "equal";
                            if (p === "") {
                                o = "NULL"
                            }
                            var m = k.createfilter(d, p, o);
                            k.addfilter(l, m)
                        } else {
                            D = false
                        }
                        break;
                    case "olbiusdropgrid":
			if (u._filterwidget.jqxDropDownButton) {
				var g = u._filterwidget.jqxInput("val");
				if(g === ""){
					break
				}
				var p = g;
                            var o = "CONTAINS";
                            if (p === "") {
                                o = "NULL"
                            }
                            var m = k.createfilter(d, p, o);
                            k.addfilter(0, m);
                            D= true
			} else {
                            D = A(u, d, k)
                        }
                        break
                    case "checkedlist":
                        if (u._filterwidget.jqxDropDownList) {
                            var g = u._filterwidget.jqxDropDownList("listBox");
                            var B = g.getCheckedItems();
                            if (B.length == 0) {
                                /*for (var v = 1; v < g.items.length; v++) {
                                    var p = g.items[v].label;
                                    var o = "not_equal";
                                    if (p === "") {
                                        o = "NULL"
                                    }
                                    var m = k.createfilter(d, p, o);
                                    k.addfilter(0, m)
                                }*/
                                D = true
                            } else {
                                if (B.length != (g.items.length)) {
                                    for (var v = 0; v < B.length; v++) {
                                        var p = B[v].value;
                                        var o = "equal";
                                        if (p === "") {
                                            o = "NULL"
                                        }
                                        var m = k.createfilter(d, p, o);
                                        k.addfilter(l, m)
                                    }
                                } else {
                                    D = false
                                }
                            }
                        } else {
                            D = A(u, d, k)
                        }
                        break
                }
                if (!this._loading) {
                    if (D) {
                        this.addfilter(u.displayfield, k, false)
                    } else {
                        this.removefilter(u.displayfield, false)
                    }
                }
            }
            if (!this._loading) {
                this.applyfilters("filterrow")
            }
        },_updatefilterrow: function() {
		//olbius - after add filter it call to update filter row. this function every running in filter
            var b = a('<div style="position: relative;" id="row00' + this.element.id + '"></div>');
            var f = 0;
            var o = this.columns.records.length;
            var m = this.toThemeProperty("jqx-grid-cell");
            m += " " + this.toThemeProperty("jqx-grid-cell-pinned");
            m += " " + this.toThemeProperty("jqx-grid-cell-filter-row");
            var q = o + 10;
            var r = new Array();
            var n = this.that;
            this.filterrow[0].cells = r;
            b.height(this.filterrowheight);
            this.filterrow.children().detach();
            this.filterrow.append(b);
            if (!this._filterrowcache) {
                this._filterrowcache = new Array()
            }
            this._initcolumntypes();
            var g = false;
            var d = new Array();
            for (var h = 0; h < o; h++) {
                var e = this.columns.records[h];
                var c = e.width;
                if (c < e.minwidth) {
                    c = e.minwidth
                }
                if (c > e.maxwidth) {
                    c = e.maxwidth
                }
                var l = a('<div style="overflow: hidden; position: absolute; height: 100%;" class="' + m + '" id="'+ e.datafield+'filterwidget"></div>');
                b.append(l);
                l.css("left", f);
                if (this.rtl) {
                    l.css("z-index", q++);
                    l.css("border-left-width", "1px")
                } else {
                    l.css("z-index", q--)
                }
                if (c == "auto") {
                    c = 0
                }
                l[0].style.width = parseFloat(c) + "px";
                l[0].left = f;
                if (!(e.hidden && e.hideable)) {
                    f += c
                } else {
                    l.css("display", "none")
                }
                r[r.length] = l[0];
                var k = true;

                if (!this.rtl) {
                    if (this.groupable) {
                        var p = (this.showrowdetailscolumn && this.rowdetails) ? 1 : 0;
                        if (this.groups.length + p > h) {
                            k = false
                        }
                    }
                    if (this.showrowdetailscolumn && this.rowdetails && h == 0) {
                        k = false
                    }
                } else {
                    if (this.groupable) {
                        var p = (this.showrowdetailscolumn && this.rowdetails) ? 1 : 0;
                        if (this.groups.length + p + h > o - 1) {
                            k = false
                        }
                    }
                    if (this.showrowdetailscolumn && this.rowdetails && h == o - 1) {
                        k = false
                    }
                }
                if (k) {
                    if (e.filtertype == "custom" && e.createfilterwidget) {
                        var i = function() {
                            n._applyfilterfromfilterrow()
                        };
                        e.createfilterwidget(e, l, i)
                    } else {
                        if (e.filterable) {
				if((e.datatype == "date" || e.datatype == "string" || e.datatype == "number") && this._filterrowcache[e.datafield]){
					g = true;
                                l.append(this._filterrowcache[e.datafield]);
                                e._filterwidget = this._filterrowcache[e.datafield]
				}else{
					this._addfilterwidget(e, l, c);
                                d[e.datafield] = e._filterwidget
				}
                            /*if (this._filterrowcache[e.datafield]) {
                                g = true;
                                l.append(this._filterrowcache[e.datafield]);
                                e._filterwidget = this._filterrowcache[e.datafield]
                            } else {
                                this._addfilterwidget(e, l, c);
                                d[e.datafield] = e._filterwidget
                            }*/
                        }
                    }
                }
            }
            this._filterrowcache = d;
            if (a.jqx.browser.msie && a.jqx.browser.version < 8) {
                b.css("z-index", q--)
            }
            b.width(parseFloat(f) + 2);
            this.filterrow.addClass(m);
            this.filterrow.css("border-top-width", "1px");
            this.filterrow.css("border-right-width", "0px");
            if (g) {
                this._updatefilterrowui(true)
            }
        },_addfilterwidget: function(C, d, A) {
            var G = this.that;
            var field = C.datafield;
            var key = '';
            if(field){
		var key = field + 'filterwidget';
		d.attr('id', key);
            }
            var z = "";
            for (var E = 0; E < G.dataview.filters.length; E++) {
                var x = G.dataview.filters[E];
                if (x.datafield && x.datafield == C.datafield) {
			if(x.filter.getfilters().length < 1)
				break;
                    z = x.filter.getfilters()[0].value;
                    break
                }
            }
            var g = function(H, I) {
                var f = a('<input autocomplete="off" type="textarea"/>');
                f[0].id = a.jqx.utilities.createId();
                f.addClass(H.toThemeProperty("jqx-widget"));
                f.addClass(H.toThemeProperty("jqx-input"));
                f.addClass(H.toThemeProperty("jqx-rc-all"));
                f.addClass(H.toThemeProperty("jqx-widget-content"));
                if (H.rtl) {
                    f.css("direction", "rtl")
                }
                if (H.disabled) {
                    f.attr("disabled", true)
                }
                f.attr("disabled", false);
                f.appendTo(I);
                f.width(A - 10);
                f.height(H.filterrowheight - 10);
                f.css("margin", "4px");
                if (C.createfilterwidget) {
                    C.createfilterwidget(C, I, f)
                }
                C._filterwidget = f;
                f.focus(function() {
                    H.content[0].scrollLeft = 0;
                    setTimeout(function() {
                        H.content[0].scrollLeft = 0
                    }, 10);
                    H.focusedfilter = f;
                    f.addClass(H.toThemeProperty("jqx-fill-state-focus"));
                });
                f.blur(function() {
                    f.removeClass(H.toThemeProperty("jqx-fill-state-focus"))
                });
                f.keydown(function(J) {
			//olbius fix lost focus in virtual mode
			localStorage.setItem('previousInputFilter', key);
                    if (J.keyCode == "13") {
                        H._applyfilterfromfilterrow()
                    }
                    if (f[0]._writeTimer) {
                        clearTimeout(f[0]._writeTimer)
                    }
                    f[0]._writeTimer = setTimeout(function() {
                        if (!H._loading) {
                            if (H["_oldWriteText" + f[0].id] != f.val()) {
                                H._applyfilterfromfilterrow();
                                H["_oldWriteText" + f[0].id] = f.val()
                            }
                        }
                    }, C.filterdelay);
                    H.focusedfilter = f
                });
                H.host.removeClass("jqx-disableselect");
                H.content.removeClass("jqx-disableselect");
                f.val(z)
            };
            if (C.datatype != null) {
                if (C.filtertype == "number") {
                    if (C.datatype == "string" || C.datatype == "date" || C.datatype == "bool") {
                        C.filtertype = "textbox"
                    }
                }else if (C.filtertype == "date") {
                    if (C.datatype == "string" || C.datatype == "number" || C.datatype == "bool") {
                        C.filtertype = "textbox"
                    }
                }else if (C.filtertype == "bool") {
                    if (C.datatype == "string" || C.datatype == "number" || C.datatype == "date") {
                        C.filtertype = "textbox"
                    }
                }
            }
            // FIXBUG filter disappear in some cases
            var tmpWidth = d.width();
            if(d.width() < 0){
                tmpWidth = parseInt(d[0].style.width.substring(0, d[0].style.width.length - 2));
            }
            switch (C.filtertype) {
                case "number":
                case "input":
                    var m = a("<div></div>");
                    m.width(tmpWidth);
                    m.height(this.filterrowheight);
                    d.append(m);
                    var A = tmpWidth - 20;
                    var s = function(I, J, f) {
                        var H = a('<input style="float: left;" autocomplete="off" type="textarea"/>');
                        if (G.rtl) {
                            H.css("float", "right");
                            H.css("direction", "rtl")
                        }
                        H[0].id = a.jqx.utilities.createId();
                        H.addClass(G.toThemeProperty("jqx-widget"));
                        H.addClass(G.toThemeProperty("jqx-input"));
                        H.addClass(G.toThemeProperty("jqx-rc-all"));
                        H.addClass(G.toThemeProperty("jqx-widget-content"));
                        H.appendTo(I);
                        H.width(J - 10);
                        if (G.disabled) {
                            H.attr("disabled", true)
                        }
                        H.attr("disabled", false);
                        H.height(G.filterrowheight - 10);
                        H.css("margin", "4px");
                        H.css("margin-right", "2px");
                        H.focus(function() {
                            G.focusedfilter = H;
                            H.addClass(G.toThemeProperty("jqx-fill-state-focus"))
                        });
                        H.blur(function() {
                            H.removeClass(G.toThemeProperty("jqx-fill-state-focus"))
                        });
                        H.keydown(function(K) {
				//olbius - add number input to filter widget
				if(C.filtertype == "number"){
					var x = K.keyCode == 190 || K.keyCode == 189 || K.metaKey || K.which <= 0 || K.which == 8 || /[0-9]/.test(String.fromCharCode(K.which));
					if (!x){
							        K.preventDefault();
							    }
				}
				//olbius fix lost focus in virtual mode
				localStorage.setItem('previousInputFilter', key);
                            if (K.keyCode == "13") {
                                G._applyfilterfromfilterrow()
                            }
                            if (H[0]._writeTimer) {
                                clearTimeout(H[0]._writeTimer)
                            }
                            H[0]._writeTimer = setTimeout(function() {
                                if (!G._loading) {
                                    if (G["_oldWriteText" + H[0].id] != H.val()) {
                                        G._applyfilterfromfilterrow();
                                        G["_oldWriteText" + H[0].id] = H.val()
                                    }
                                }
                            }, C.filterdelay);
                            G.focusedfilter = H
                        });
                        H.val(z);
                        return H
                    };
                    s(m, A);
                    var B = G._getfiltersbytype(C.filtertype == "number" ? "number" : "string");
                    var t = a("<div class='filter' style='float: left;'></div>");
                    t.css("margin-top", "4px");
                    t.appendTo(m);
                    if (G.rtl) {
                        t.css("float", "right")
                    }
                    var h = 0;
                    //olbius fix focus dropdown condition
                    if(!C.previousConditionIndex){
			if (C.filtercondition != null) {
	                        var E = new a.jqx.filter();
	                        var r = E.getoperatorsbyfiltertype(C.filtertype == "number" ? "numericfilter" : "stringfilter");
	                        var e = r.indexOf(C.filtercondition.toUpperCase());
	                        if (e != -1) {
	                            h = e
	                        }
	                    }
	                    var D = 170;
	                    if (C.filtertype == "input") {
	                        D = 240;
	                        if (h == 0) {
	                            var e = B.indexOf("contains");
	                            if (e != -1 && C.filtercondition == null) {
	                                h = e
	                            }
	                        }
	                    }
                    }else{
			h = C.previousConditionIndex;
                    }

                    t.jqxDropDownList({disabled: G.disabled,touchMode: G.touchmode,rtl: G.rtl,dropDownHorizontalAlignment: "right",enableBrowserBoundsDetection: true,selectedIndex: h,width: 18,height: 21,dropDownHeight: 150,dropDownWidth: D,source: B,theme: G.theme});
                    t.jqxDropDownList({selectionRenderer: function(f) {
                            return ""
                        }});
                    t.jqxDropDownList("setContent", "");
                    t.find(".jqx-dropdownlist-content").hide();
                    if (C.createfilterwidget) {
                        C.createfilterwidget(C, d, m)
                    }
                    C._filterwidget = m;
                    var j = null;
                    this.addHandler(t, "select", function() {
			C.previousConditionIndex = t.jqxDropDownList("getSelectedIndex");
                        var f = t.jqxDropDownList("getSelectedItem").label;
                        if (C._filterwidget.find("input").val().length > 0 && !G.refreshingfilter) {
                            G._applyfilterfromfilterrow()
                        }
                        if (C.filtertype == "input" && !G.refreshingfilter) {
                            G._applyfilterfromfilterrow()
                        } else {
                            if (C._filterwidget.find("input").val().length == 0 && !G.refreshingfilter) {
                                if (j == "null" || j == "not null" || f == "null" || f == "not null") {
                                    G._applyfilterfromfilterrow()
                                }
                            }
                        }
                        j = f
                    });
                    break;
                case "textbox":
                case "default":
                default:
                    g(this, d);
                    break;
                case "none":
                    break;
                case "date":
                case "range":
                    if (this.host.jqxDateTimeInput) {
			z = "";
			for (var E = 0; E < G.dataview.filters.length; E++) {
                            var x = G.dataview.filters[E];
                            if (x.datafield && x.datafield == C.datafield) {
				if (x.filter.getfilters()[1]) {
					z = x.filter.getfilters()[0].value + " - " + x.filter.getfilters()[1].value;
								}
                            }
                        }
                        var b = a("<div></div>");
                        b.val(z);
                        b.css("margin", "4px");
                        b.appendTo(d);
                        var n = {calendar: this.gridlocalization,todayString: this.gridlocalization.todaystring,clearString: this.gridlocalization.clearstring};
                        b.jqxDateTimeInput({readonly: true,disabled: G.disabled,localization: n,rtl: G.rtl,showFooter: true,formatString: C.cellsformat,selectionMode: C.filtertype,value: null,theme: this.theme,width: A - 10,height: this.filterrowheight - 10});
                        if (C.createfilterwidget) {
                            C.createfilterwidget(C, d, b)
                        }
                        C._filterwidget = b;
                        this.addHandler(b, "valueChanged", function(f) {
                            if (!G.refreshingfilter) {
                                G._applyfilterfromfilterrow();
                                G.focusedfilter = null
                            }
                        })
                    } else {
                        g(this, d)
                    }
                    break;
                case "olbiusdropgrid":
			if (this.host.jqxDropDownButton) {
				 var q = this._getfilterdataadapter(C);
                         var l = false;
                         var tmpColName = C.datafield;
                         var t = a("<input placeHolder='" + this.gridlocalization.notificationfiltershortkey + "'  id='input" + tmpColName + "' style='height:21px;'/>"); //<div id='jqxgrid" + C.datafield + "'></div>
                         t.addClass(this.toThemeProperty("jqx-input"));
                         t.css("margin", "4px");
                         var u = C.datafield;
                         var v = C.filtertype == "olbiusdropgrid" ? true : false;
                         var D = A < 150 ? 220 : "auto";
                         q.dataBind();
                         var p = q.records;
                         var k = p.length < 8 ? true : false;
                         l = k;
                         t.appendTo(d);
                         t.on('click',function(){
							 if(cntrlIsPressed){
								var stmp = '#jqxwindow' + tmpColName + 'key';
					$(stmp).val("input" + tmpColName);
								$("#jqxwindow" + tmpColName).jqxWindow('open');
								cntrlIsPressed = false;
							 }
						});
						t.keydown(function(K) {
				 K.preventDefault();
				});
                         t.jqxInput();
                         if (v) {

                         }
                         if (C.createfilterwidget) {
                             C.createfilterwidget(C, d, t)
                         }
                         t.focus(function() {
                             G.focusedfilter = t;
                             t.addClass(G.toThemeProperty("jqx-fill-state-focus"))
                         });
                         t.blur(function() {
                             t.removeClass(G.toThemeProperty("jqx-fill-state-focus"))
                         });
                         C._filterwidget = t;
                         //var o = t.jqxDropDownList("dropdownlistWrapper");
                         if (C.filtertype == "olbiusdropgrid") {
                              this.addHandler(t, 'change', function(f) {
                                 if (!G.refreshingfilter) {
                                     if ((f.args && f.args.type != "none") || f.type != "none") {
                                         G._applyfilterfromfilterrow();
                                         G.focusedfilter = null
                                     }
                                 }
                             });
                         } else {
                             this.addHandler(t, "close", function(f) {
                                 if (t[0]._selectionChanged) {
                                     G._applyfilterfromfilterrow();
                                     G.focusedfilter = null;
                                     t[0]._selectionChanged = false
                                 }
                             })
                         }
                         t.css("width", (tmpWidth - 10) + "px");
			} else {
                        g(this, d)
                    }
			break
                case "list":
                case "checkedlist":
                    if (this.host.jqxDropDownList) {
                        var q = this._getfilterdataadapter(C);
                        var l = false;
                        var t = a("<div></div>");
                        t.css("margin", "4px");
                        var u = C.datafield;
                        //fix dropdown height in filter checkedlist namdn
                        var v = C.filtertype == "checkedlist" ? true : false;
                        var D = A < 150 ? 220 : "auto";
                        q.dataBind();
                        var p = q.records;
                        setTimeout(function(){
				var dd = $(C._filterwidget[0]).jqxDropDownList('source');
				if(typeof(dd) != 'undefined' && dd != null){
							var k = dd.length  < 10 ? true : false;
							if(!k) var ddHeight = 220;
						}else {
							var k = p.length < 8 ? true : false;
						}
                        },2000);
                        l = k;
                        t.appendTo(d);
                        if(typeof(ddHeight) != 'undefined' && ddHeight  > 0 && !k ){
				t.jqxDropDownList({disabled: G.disabled,touchMode: G.touchmode,rtl: G.rtl,checkboxes: v,dropDownWidth: D,source: q.records,autoDropDownHeight: k,dropDownHeight : ddHeight,theme: this.theme,width: A - 10,height: this.filterrowheight - 10,displayMember: C.displayfield,valueMember: u});
                        }else {
				t.jqxDropDownList({disabled: G.disabled,touchMode: G.touchmode,rtl: G.rtl,checkboxes: v,dropDownWidth: D,source: q.records,autoDropDownHeight: k,theme: this.theme,width: A - 10,height: this.filterrowheight - 10,displayMember: C.displayfield,valueMember: u});
                        }
                        var c = t.jqxDropDownList("listBox");
                        if (v) {
                            t.jqxDropDownList({selectionRenderer: function() {
                                    var f = '<span class="' + G.toThemeProperty("jqx-item") + '" style="top: 2px; position: relative; color: inherit; border: none; background-color: transparent;">' + G.gridlocalization.filterselectstring + "</span>";
                                    return f
                                }});
                            var y = a('<span style="top: 2px; position: relative; color: inherit; border: none; background-color: transparent;">' + G.gridlocalization.filterselectstring + "</span>");
                            y.addClass(this.toThemeProperty("jqx-item"));
                            if (c != undefined) {
                                if (!l) {
                                    c.host.height(200)
                                }
                                //c.insertAt(G.gridlocalization.filterselectallstring, 0);
                                //console.log(G.gridlocalization.filterselectallstring);
                                t.jqxDropDownList("setContent", y);
                                var i = true;
                                var F = new Array();
                                c.checkAll(false);
                                G.addHandler(c.host, "checkChange", function(I) {
                                    t[0]._selectionChanged = true;
                                    if (!i) {
                                        return
                                    }
                                    if (I.args.label != G.gridlocalization.filterselectallstring) {
//                                        i = false;
//                                        c.host.jqxListBox("checkIndex", 0, true, false);
//                                        var f = c.host.jqxListBox("getCheckedItems");
//                                        var H = c.host.jqxListBox("getItems");
//                                        if (f.length == 1) {
//                                            c.host.jqxListBox("uncheckIndex", 0, true, false)
//                                        } else {
//                                            if (H.length != (f.length)) {
//                                                c.host.jqxListBox("indeterminateIndex", 0, true, false)
//                                            }
//                                        }
//                                        i = true
                                    } else {
                                        i = false;
                                        if (I.args.checked) {
                                            c.host.jqxListBox("checkAll", false)
                                        } else {
                                            c.host.jqxListBox("uncheckAll", false)
                                        }
                                        i = true
                                    }
                                })
                            }
                        } else {
                            c.insertAt({label: this.gridlocalization.filterchoosestring,value: ""}, 0);
                            t.jqxDropDownList({selectedIndex: 0})
                        }
                        if (C.createfilterwidget) {
                            C.createfilterwidget(C, d, t)
                        }
                        C._filterwidget = t;
                        var o = t.jqxDropDownList("dropdownlistWrapper");
                        if (C.filtertype == "list") {
                            this.addHandler(t, "select", function(f) {
                                if (!G.refreshingfilter) {
                                    if (f.args && f.args.type != "none") {
                                        G._applyfilterfromfilterrow();
                                        G.focusedfilter = null
                                    }
                                }
                            })
                        } else {
                            this.addHandler(t, "close", function(f) {
                                if (t[0]._selectionChanged) {
                                    G._applyfilterfromfilterrow();
                                    G.focusedfilter = null;
                                    t[0]._selectionChanged = false
                                }
                            })
                        }
                    } else {
                        g(this, d)
                    }
                    break;
                case "bool":
                case "boolean":
                    if (this.host.jqxCheckBox) {
                        var w = a('<div tabIndex=0 style="opacity: 0.99; position: absolute; top: 50%; left: 50%; margin-top: -7px; margin-left: -10px;"></div>');
                        w.appendTo(d);
                        w.jqxCheckBox({disabled: G.disabled,enableContainerClick: false,animationShowDelay: 0,animationHideDelay: 0,hasThreeStates: true,theme: this.theme,checked: null});
                        if (C.createfilterwidget) {
                            C.createfilterwidget(C, d, w)
                        }
                        if (z === true || z == "true") {
                            w.jqxCheckBox({checked: true})
                        } else {
                            if (z === false || z == "false") {
                                w.jqxCheckBox({checked: false})
                            }
                        }
                        C._filterwidget = w;
                        this.addHandler(w, "change", function(f) {
                            if (!G.refreshingfilter) {
                                if (f.args) {
                                    G.focusedfilter = null;
                                    G._applyfilterfromfilterrow()
                                }
                            }
                        })
                    } else {
                        g(this, d)
                    }
                    break
            }
        },_getfilterdataadapter: function(b) {
            var c = this.source._source ? true : false;
            if (!c) {
                dataadapter = new a.jqx.dataAdapter(this.source, {autoBind: false,uniqueDataFields: [b.displayfield],autoSort: true,autoSortField: b.displayfield,async: false})
            } else {
                var e = {localdata: this.source.records,datatype: this.source.datatype,async: false};
                var d = this;
                dataadapter = new a.jqx.dataAdapter(e, {autoBind: false,autoSort: true,autoSortField: b.displayfield,async: false,uniqueDataFields: [b.displayfield],beforeLoadComplete: function(f) {
                        var k = new Array();
                        if (b.cellsformat) {
                            var j = d._getcolumntypebydatafield(b);
                            for (var g = 0; g < f.length; g++) {
                                k.push(f[g]);
                                var h = f[g][b.displayfield];
                                f[g][b.displayfield + "JQValue"] = h;
                                if (j === "date") {
                                    f[g][b.displayfield] = dataadapter.formatDate(h, b.cellsformat, d.gridlocalization)
                                } else {
                                    if (j === "number" || j === "float" || j === "int") {
                                        f[g][b.displayfield] = dataadapter.formatNumber(h, b.cellsformat, d.gridlocalization)
                                    }
                                }
                            }
                            return k
                        } else {
                            return f
                        }
                    }})
            }
            if (b.filteritems && b.filteritems.length > 0) {
                var e = {localdata: b.filteritems,datatype: this.source.datatype,async: false};
                dataadapter = new a.jqx.dataAdapter(e, {autoBind: false,async: false})
            } else {
                if (b.filteritems) {
                    if (b.filteritems._source) {
                        b.filteritems._options.autoBind = false;
                        b.filteritems._options.async = false;
                        return b.filteritems
                    } else {
                        if (a.isFunction(b.filteritems)) {
                            return b.filteritems()
                        }
                    }
                }
            }
            return dataadapter
        },refreshfilterrow: function() {
            if (!this.showfilterrow) {
                return
            }
            this.refreshingfilter = true;
            this._updatefilterrowui();
            this._updatelistfilters(true, true);
            var h = this.that;
            var l = this.columns.records.length;
            for (var d = 0; d < l; d++) {
                var c = this.columns.records[d];
                if (c.filterable) {
                    if (c.filter) {
                        var b = c.filter.getfilters();
                        if (b.length > 0) {
                            var k = b[0].value;
                            var e = c._filterwidget;
                            var f = c._filterwidget.parent();
                            if (e != null) {
                                switch (c.filtertype) {
					case "olbiusdropgrid":
						break;
                                    case "number":
                                        f.find("input").val(k);
                                        if (this.host.jqxDropDownList) {
                                            var i = c.filter.getoperatorsbyfiltertype("numericfilter");
                                            e.find(".filter").jqxDropDownList("selectIndex", i.indexOf(b[0].condition))
                                        }
                                        break;
                                    case "input":
                                        f.find("input").val(k);
                                        if (this.host.jqxDropDownList) {
                                            var i = c.filter.getoperatorsbyfiltertype("stringfilter");
                                            e.find(".filter").jqxDropDownList("selectIndex", i.indexOf(b[0].condition))
                                        }
                                        break;
                                    case "date":
                                    case "range":
                                        if (this.host.jqxDateTimeInput) {
                                            var k = c.filter.getfilterat(0).filtervalue;
                                            if (k != undefined) {
                                                if (c.filter.getfilterat(1)) {
                                                    var g = c.filter.getfilterat(1).filtervalue
                                                } else {
                                                    g = k
                                                }
                                                if (c.filtertype == "range") {
                                                    a(f.children()[0]).jqxDateTimeInput("setRange", new Date(k), new Date(g))
                                                } else {
                                                    a(f.children()[0]).jqxDateTimeInput("setDate", new Date(k))
                                                }
                                            }
                                        } else {
                                            e.val(k)
                                        }
                                        break;
                                    case "textbox":
                                    case "default":
                                        e.val(k);
                                        h["_oldWriteText" + e[0].id] = k;
                                        break;
                                    case "bool":
                                    case "boolean":
                                        if (!this.host.jqxCheckBox) {
                                            e.val(k)
                                        } else {
                                            a(f.children()[0]).jqxCheckBox({checked: k})
                                        }
                                        break
                                }
                            }
                        }
                    }
                }
            }
            this.refreshingfilter = false
        },_destroyedfilters: function() {
            var g = this.that;
            var b = this.columns.records.length;
            for (var f = 0; f < b; f++) {
                var c = this.columns.records[f];
                if (c.filterable) {
                    var h = c._filterwidget;
                    if (c.filtertype == "list" || c.filtertype == "checkedlist") {
                        this.removeHandler(h, "select");
                        this.removeHandler(h, "close");
                        h.jqxDropDownList("destroy")
                    } else {
                        if (c.filtertype == "date") {
                            this.removeHandler(h, "valueChanged");
                            h.jqxDateTimeInput("destroy")
                        } else {
                            if (c.filtertype == "bool") {
                                this.removeHandler(h, "change");
                                h.jqxCheckBox("destroy")
                            } else {
                                if (c.filtertype == "number") {
                                    var d = h.find(".jqx-input");
                                    this.removeHandler(d, "keydown");
                                    var e = a(h.children()[1]);
                                    e.jqxDropDownList("destroy")
                                } else {
                                    this.removeHandler(h, "keydown")
                                }
                            }
                        }
                    }
                    h.remove()
                }
            }
        },_updatelistfilters: function(l, k) {
            var u = this.that;
            var s = this.columns.records.length;
            for (var o = 0; o < s; o++) {
                var p = this.columns.records[o];
                if (p.filterable) {
                    if (p.filtertype == "list" || p.filtertype == "checkedlist") {
                        var h = p._filterwidget;
                        if (!l) {
                            if (p.filter == undefined) {
                                h.jqxDropDownList("renderSelection");
                                continue
                            }
                        } else {
                            var e = this._getfilterdataadapter(p);
                            h.jqxDropDownList({source: e});
                            var d = h.jqxDropDownList("getItems");
                            var n = true;
                            if (d.length != e.records.length + 1) {
                                n = false
                            }
                            if (n) {
                                for (var r = 1; r < d.length; r++) {
                                    if (d[r].label != e.records[r - 1][p.displayfield]) {
                                        n = false;
                                        break
                                    }
                                }
                            }
                            if (n && !k) {
                                continue
                            }
                        }
                        var m = p.filtertype == "checkedlist" ? true : false;
                        var d = h.jqxDropDownList("getItems");
                        var b = h.jqxDropDownList("listBox");
                        h.jqxDropDownList("dataBind");
                        if (m) {
                            h.jqxDropDownList({selectionRenderer: function() {
                                    return u.gridlocalization.filterselectstring
                                }});
                            //b.insertAt(this.gridlocalization.filterselectallstring, 0);
                            h.jqxDropDownList("setContent", this.gridlocalization.filterselectstring);
                            b.checkAll(false);
                            if (p.filter) {
                                var g = p.filter.getfilters();
                                for (var r = 0; r < b.items.length; r++) {
                                    var f = b.items[r].label;
                                    var q = undefined;
                                    a.each(g, function() {
                                        var i;
                                        if (this.condition == "NOT_EQUAL") {
                                            if (f == this.value) {
                                                i = false
                                            } else {
                                                i = true
                                            }
                                        } else {
                                            if (this.condition == "EQUAL") {
                                                if (f == this.value) {
                                                    i = true
                                                } else {
                                                    i = false
                                                }
                                            }
                                        }
                                        if (q == undefined && i !== undefined) {
                                            q = i
                                        } else {
                                            if (this.condition == "EQUAL") {
                                                q = q || i
                                            } else {
                                                q = q && i
                                            }
                                        }
                                    });
                                    if (q) {
                                        b.checkIndex(r, false, false)
                                    } else {
                                        b.uncheckIndex(r, false, false)
                                    }
                                }
                                b._updateCheckedItems();
                                var t = b.getCheckedItems().length;
                                if (b.items.length != t && t > 0) {
                                    b.host.jqxListBox("indeterminateIndex", 0, true, false)
                                }
                            }
                        } else {
                            b.insertAt({label: this.gridlocalization.filterchoosestring,value: ""}, 0);
                            h.jqxDropDownList({selectedIndex: 0});
                            if (p.filter) {
                                var g = p.filter.getfilters();
                                var c = -1;
                                for (var r = 0; r < b.items.length; r++) {
                                    var f = b.items[r].label;
                                    a.each(g, function() {
                                        if (this.condition == "NOT_EQUAL") {
                                            return true
                                        }
                                        if (f == this.value) {
                                            c = r;
                                            return false
                                        }
                                    })
                                }
                                if (c != -1) {
                                    b.selectIndex(c)
                                }
                            }
                        }
                        if (d.length < 8) {
                            h.jqxDropDownList("autoDropDownHeight", true)
                        } else {
                            h.jqxDropDownList("autoDropDownHeight", false)
                        }
                    }
                    if (p.filtertype == "olbiusdropgrid") {
			var h = p._filterwidget;
                        if (!l) {
                            if (p.filter == undefined) {
                                h.jqxInput('val', '');
                                continue
                            }
                        } else {
                            var e = this._getfilterdataadapter(p);
                            h.jqxDropDownList({source: e});
                            var d = h.jqxInput("val");
                            var n = true;
                            if (d.length != e.records.length + 1) {
                                n = false
                            }
                            if (n) {
                                for (var r = 1; r < d.length; r++) {
                                    if (d[r].label != e.records[r - 1][p.displayfield]) {
                                        n = false;
                                        break
                                    }
                                }
                            }
                            if (n && !k) {
                                continue
                            }
                        }
                    }
                }
            }
        },_renderfiltercolumn: function() {
            var b = this.that;
            if (this.filterable) {
                if (!this.columns.records) {
                    return
                }
                a.each(this.columns.records, function(d, e) {
                    var c = false;
                    if (b.autoshowfiltericon) {
                        if (this.filter) {
                            a(this.filtericon).show();
                            c = true
                        } else {
                            a(this.filtericon).hide()
                        }
                    } else {
                        if (this.filterable) {
                            a(this.filtericon).show();
                            c = true
                        }
                    }
                    if (this.align == "right" && !this.renderer) {
                        if (this.element) {
                            if (!c) {
                                this.element.firstChild.firstChild.style.marginRight = "2px"
                            } else {
                                this.element.firstChild.firstChild.style.marginRight = "18px"
                            }
                        }
                    }
                })
            }
        },_initcolumntypes: function() {
            if (this.columns && this.columns.records) {
                var b = this.source._source.datafields;
                if (b) {
                    for (var c = 0; c < this.columns.records.length; c++) {
                        var d = this.columns.records[c];
                        if (d.datatype) {
                            continue
                        }
                        var e = "";
                        a.each(b, function() {
                            if (this.name == d.displayfield) {
                                if (this.type) {
                                    e = this.type
                                }
                                return false
                            }
                        });
                        if (e != "") {
                            d.datatype = e
                        } else {
                            d.datatype = ""
                        }
                    }
                }
            }
        },_getcolumntypebydatafield: function(f) {
            var g = this.that;
            var e = "string";
            var d = g.source.datafields || ((g.source._source) ? g.source._source.datafields : null);
            if (d) {
                var i = "";
                a.each(d, function() {
                    if (this.name == f.displayfield) {
                        if (this.type) {
                            i = this.type
                        }
                        return false
                    }
                });
                if (i) {
                    return i
                }
            }
            if (f != null) {
                if (this.dataview.cachedrecords == undefined) {
                    return e
                }
                var b = null;
                if (!this.virtualmode) {
                    if (this.dataview.cachedrecords.length == 0) {
                        return e
                    }
                    b = this.dataview.cachedrecords[0][f.displayfield];
                    if (b != null && b.toString() == "") {
                        return "string"
                    }
                } else {
                    a.each(this.dataview.cachedrecords, function() {
                        b = this[f.displayfield];
                        return false
                    })
                }
                if (b != null) {
                    if (typeof b == "boolean") {
                        e = "boolean"
                    } else {
                        if (a.jqx.dataFormat.isNumber(b)) {
                            e = "number"
                        } else {
                            var h = new Date(b);
                            if (h.toString() == "NaN" || h.toString() == "Invalid Date") {
                                if (a.jqx.dataFormat) {
                                    h = a.jqx.dataFormat.tryparsedate(b);
                                    if (h != null) {
                                        if (h && h.getFullYear()) {
                                            if (h.getFullYear() == 1970 && h.getMonth() == 0 && h.getDate() == 1) {
                                                var c = new Number(b);
                                                if (!isNaN(c)) {
                                                    return "number"
                                                }
                                                return "string"
                                            }
                                        }
                                        return "date"
                                    } else {
                                        e = "string"
                                    }
                                } else {
                                    e = "string"
                                }
                            } else {
                                e = "date"
                            }
                        }
                    }
                }
            }
            return e
        },_getfiltersbytype: function(b) {
            var c = this.that;
            var d = "";
            switch (b) {
                case "number":
                case "float":
                case "int":
                    d = c.gridlocalization.filternumericcomparisonoperators;
                    break;
                case "date":
                    d = c.gridlocalization.filterdatecomparisonoperators;
                    break;
                case "boolean":
                case "bool":
                    d = c.gridlocalization.filterbooleancomparisonoperators;
                    break;
                case "string":
                default:
                    d = c.gridlocalization.filterstringcomparisonoperators;
                    break
            }
            return d
        },_getfiltertype: function(b) {
            var c = "stringfilter";
            switch (b) {
                case "number":
                case "int":
                case "float":
                case "decimal":
                    c = "numericfilter";
                    break;
                case "boolean":
                case "bool":
                    c = "booleanfilter";
                    break;
                case "date":
                case "time":
                case "range":
                    c = "datefilter";
                    break;
                case "string":
                case "input":
                    c = "stringfilter";
                    break
            }
            return c
        },_buildfilter: function(r, l, F) {
            var f = a(l).find("#filter1" + r.element.id);
            var G = a(l).find("#filter2" + r.element.id);
            var J = a(l).find("#filter3" + r.element.id);
            var k = a(l).find(".filtertext1" + r.element.id);
            var j = a(l).find(".filtertext2" + r.element.id);
            var A = k.val();
            var y = j.val();
            var L = r._getcolumntypebydatafield(F);
            var t = r._getfiltersbytype(L);
            var I = new a.jqx.filter();
            var w = r._getfiltertype(L);
            if (r.filtermode === "default") {
                var E = f.jqxDropDownList("selectedIndex");
                var c = G.jqxDropDownList("selectedIndex");
                var D = J.jqxDropDownList("selectedIndex");
                var e = null;
                var d = null;
                if (r.updatefilterconditions) {
                    var p = r.updatefilterconditions(w, I.getoperatorsbyfiltertype(w));
                    if (p != undefined) {
                        I.setoperatorsbyfiltertype(w, p)
                    }
                }
                var q = false;
                var K = I.getoperatorsbyfiltertype(w)[E];
                var J = I.getoperatorsbyfiltertype(w)[D];
                var v = K == "NULL" || K == "NOT_NULL";
                var h = K == "EMPTY" || K == "NOT_EMPTY";
                if (K == undefined) {
                    K = I.getoperatorsbyfiltertype(w)[0]
                }
                if (J == undefined) {
                    J = I.getoperatorsbyfiltertype(w)[0]
                }
                if (A.length > 0 || v || h) {
                    e = I.createfilter(w, A, K, null, F.cellsformat, r.gridlocalization);
                    I.addfilter(c, e);
                    q = true
                }
                var u = J == "NULL" || J == "NOT_NULL";
                var g = J == "EMPTY" || J == "NOT_EMPTY";
                if (y.length > 0 || u || g) {
                    d = I.createfilter(w, y, J, null, F.cellsformat, r.gridlocalization);
                    I.addfilter(c, d);
                    q = true
                }
                if (q) {
                    var C = F.displayfield;
                    this.addfilter(C, I, true)
                } else {
                    this._clearfilter(r, l, F)
                }
            } else {
                var B = this;
                var n = false;
                var x = f.data().jqxListBox.instance;
                var o = x.getCheckedItems();
                if (o.length == 0) {
                    for (var H = 1; H < x.items.length; H++) {
                        var m = x.items[H].value;
                        var b = "not_equal";
                        if (m.indexOf("|") >= 0 || m.indexOf(" AND ") >= 0 || m.indexOf(" OR ") >= 0 || m.indexOf(" and ") >= 0 || m.indexOf(" or ") >= 0) {
                            m = m.replace("|", "");
                            m = m.replace("AND", "");
                            m = m.replace("OR", "");
                            m = m.replace("and", "");
                            m = m.replace("or", "");
                            var b = "equal"
                        }
                        var z = I.createfilter(w, m, b, null);
                        I.addfilter(0, z)
                    }
                    n = true
                } else {
                    if (o.length != x.items.length) {
                        n = true;
                        for (var H = 0; H < o.length; H++) {
                            if (r.gridlocalization.filterselectallstring === o[H].value) {
                                continue
                            }
                            var m = o[H].value;
                            var b = "equal";
                            var z = I.createfilter(w, m, b, null);
                            var s = 1;
                            I.addfilter(s, z)
                        }
                    } else {
                        n = false
                    }
                }
                if (n) {
                    var C = F.displayfield;
                    this.addfilter(C, I, true)
                } else {
                    var C = F.displayfield;
                    this.removefilter(C, true)
                }
            }
        },_clearfilter: function(e, c, d) {
            var b = d.displayfield;
            this.removefilter(b, true)
        },addfilter: function(d, e, c) {
            if (this._loading) {
                throw new Error("jqxGrid: " + this.loadingerrormessage);
                return false
            }
            var f = this.getcolumn(d);
            var b = this._getcolumn(d);
            if (f == undefined || f == null) {
                return
            }
            f.filter = e;
            b.filter = e;
            this.dataview.addfilter(d, e);
            if (c == true && c != undefined) {
                this.applyfilters("add")
            }
        },removefilter: function(d, c) {
            if (this._loading) {
                throw new Error("jqxGrid: " + this.loadingerrormessage);
                return false
            }
            var e = this.getcolumn(d);
            var b = this._getcolumn(d);
            if (e == undefined || e == null) {
                return
            }
            if (e.filter == null) {
                return
            }
            this.dataview.removefilter(d, e.filter);
            e.filter = null;
            b.filter = null;
            if (this.showfilterrow) {
                this.clearfilterrow(d)
            }
            if (c == true || c !== false) {
                this.applyfilters("remove")
            }
        },applyfilters: function(f) {
            var c = false;
            if (this.dataview.filters.length >= 0 && (this.virtualmode || !this.source.localdata)) {
                if (this.source != null && this.source.filter) {
                    var g = -1;
                    if (this.pageable) {
                        g = this.dataview.pagenum;
                        this.dataview.pagenum = 0
                    } else {
                        this.vScrollInstance.setPosition(0);
                        this.loadondemand = true;
                        this._renderrows(this.virtualsizeinfo)
                    }
                    if (this.pageable && this.virtualmode) {
                        this.dataview.pagenum = 0
                    }
                    this.source.filter(this.dataview.filters, this.dataview.records, this.dataview.records.length);
                    if (this.pageable && !this.virtualmode) {
                        this.dataview.pagenum = g
                    }
                }
            }
            if (this.dataview.clearsortdata) {
                this.dataview.clearsortdata()
            }
            if (!this.virtualmode) {
                var b = this.selectedrowindexes;
                var d = this.that;
                this.dataview.refresh();
                if (this.dataview.clearsortdata) {
                    if (this.sortcolumn && this.sortdirection) {
                        var e = this.sortdirection.ascending ? "asc" : "desc";
                        if (!this._loading) {
                            this.sortby(this.sortcolumn, e, null, false)
                        } else {
                            this.sortby(this.sortcolumn, e, null, false, false)
                        }
                    }
                }
            } else {
                if (this.pageable) {
                    this.dataview.updateview();
                    if (this.gotopage) {
                        this.gotopage(0)
                    }
                }
                this.rendergridcontent(false, false);
                if (this.showfilterrow) {
                    if (typeof f != "string" && a.isEmptyObject(f)) {
                        this.refreshfilterrow()
                    }
                }
                this._raiseEvent(13, {filters: this.dataview.filters});
                return
            }
            if (this.pageable) {
                this.dataview.updateview();
                if (this.gotopage) {
                    this.gotopage(0);
                    this.updatepagerdetails()
                }
            }
            this._updaterowsproperties();
            if (!this.groupable || (this.groupable && this.groups.length == 0)) {
                this._rowdetailscache = new Array();
                this.virtualsizeinfo = null;
                this._pagescache = new Array();
                if (this.columns && this.columns.records && this.columns.records.length > 0 && !this.columns.records[0].filtericon) {
                    this.prerenderrequired = true
                }
                this.rendergridcontent(true, false);
                this._updatecolumnwidths();
                this._updatecellwidths();
                this._renderrows(this.virtualsizeinfo);
                if (this.showaggregates && this._updatecolumnsaggregates) {
                    this._updatecolumnsaggregates()
                }
            } else {
                this._rowdetailscache = new Array();
                this._render(true, true, false, false, false);
                if (this.showfilterrow) {
                    this._updatefocusedfilter()
                }
                this._updatecolumnwidths();
                this._updatecellwidths();
                this._renderrows(this.virtualsizeinfo)
            }
            if (this.showfilterrow) {
                if (typeof f != "string" && a.isEmptyObject(f)) {
                    this.refreshfilterrow()
                }
            }
            this._raiseEvent(13, {filters: this.dataview.filters})
        },getfilterinformation: function() {
            var d = new Array();
            for (var b = 0; b < this.dataview.filters.length; b++) {
                var c = this.getcolumn(this.dataview.filters[b].datafield);
                d[b] = {filter: this.dataview.filters[b].filter,filtercolumn: c.datafield,filtercolumntext: c.text}
            }
            return d
        },clearfilters: function(b) {
            var d = this.that;
            if (this.showfilterrow) {
                this.clearfilterrow()
            }
            if (this.columns.records) {
                var c = b == true || b !== false;
                a.each(this.columns.records, function() {
                    d.removefilter(this.displayfield, !c)
                })
            }
            if (b === false) {
                return
            }
            if (b == true || b !== false) {
                this.applyfilters("clear")
            }
        },_destroyfilterpanel: function() {
            var e = a(a.find("#filterclearbutton" + this.element.id));
            var d = a(a.find("#filterbutton" + this.element.id));
            var h = a(a.find("#filter1" + this.element.id));
            var c = a(a.find("#filter2" + this.element.id));
            var g = a(a.find("#filter3" + this.element.id));
            var f = a(a.find(".filtertext1" + this.element.id));
            var b = a(a.find(".filtertext2" + this.element.id));
            if (f.length > 0 && b.length > 0) {
                f.removeClass();
                b.removeClass();
                f.remove();
                b.remove()
            }
            if (e.length > 0) {
                e.jqxButton("destroy");
                d.jqxButton("destroy");
                this.removeHandler(e, "click");
                this.removeHandler(d, "click")
            }
            if (h.length > 0) {
                h.jqxDropDownList("destroy")
            }
            if (c.length > 0) {
                c.jqxDropDownList("destroy")
            }
            if (g.length > 0) {
                g.jqxDropDownList("destroy")
            }
        },_updatefilterpanel: function(r, o, D) {
            if (r == null || r == undefined) {
                r = this
            }
            var N = r._getcolumntypebydatafield(D);
            var t = r._getfiltersbytype(N);
            if (!r.host.jqxDropDownList) {
                throw new Error("jqxGrid: Missing reference to jqxdropdownlist.js.");
                return
            }
            var g = a(o);
            var K = g.find("#filterclearbutton" + r.element.id);
            var k = g.find("#filterbutton" + r.element.id);
            var f = g.find("#filter1" + r.element.id);
            var l = g.find("#filter2" + r.element.id);
            var M = g.find("#filter3" + r.element.id);
            var j = g.find(".filtertext1" + r.element.id);
            var h = g.find(".filtertext2" + r.element.id);
            if (this._hasdatefilter) {
                var e = j.parent();
                var d = h.parent();
                e.children().remove();
                d.children().remove();
                if (D.filtertype == "date") {
                    r._showwhere.text(r.gridlocalization.filtershowrowdatestring);
                    var b = a("<div class='filtertext1" + r.element.id + "' style='margin-top: 3px; margin-bottom: 3px;'></div>");
                    e.append(b);
                    var m = function(c) {
                        var i = {calendar: r.gridlocalization,todayString: r.gridlocalization.todaystring,clearString: r.gridlocalization.clearstring};
                        c.jqxDateTimeInput({disabled: r.disabled,localization: i,rtl: r.rtl,width: r._filterpanelwidth - 15,height: 20,value: null,formatString: D.cellsformat,theme: r.theme})
                    };
                    m(b);
                    var b = a("<div class='filtertext2" + r.element.id + "' style='margin-top: 3px; margin-bottom: 3px;'></div>");
                    d.append(b);
                    m(b)
                } else {
                    r._showwhere.text(r.gridlocalization.filtershowrowstring);
                    var b = a("<input class='filtertext1" + r.element.id + "' style='height: 20px; margin-top: 3px; margin-bottom: 3px;' type='text'></input>");
                    e.append(b);
                    var m = function(c) {
                        c.addClass(r.toThemeProperty("jqx-input"));
                        c.addClass(r.toThemeProperty("jqx-widget-content"));
                        c.addClass(r.toThemeProperty("jqx-rc-all"));
                        c.width(r._filterpanelwidth - 15)
                    };
                    m(b);
                    var b = a("<input class='filtertext2" + r.element.id + "' style='height: 20px; margin-top: 3px; margin-bottom: 3px;' type='text'></input>");
                    d.append(b);
                    m(b)
                }
                var j = g.find(".filtertext1" + r.element.id);
                var h = g.find(".filtertext2" + r.element.id)
            }
            j.val("");
            h.val("");
            this.removeHandler(k, "click");
            this.addHandler(k, "click", function() {
                r._buildfilter(r, o, D);
                r._closemenu()
            });
            this.removeHandler(K, "click");
            this.addHandler(K, "click", function() {
                r._clearfilter(r, o, D);
                r._closemenu()
            });
            if (this.filtermode === "default") {
                if (f.jqxDropDownList("source") != t) {
                    f.jqxDropDownList({enableBrowserBoundsDetection: false,source: t});
                    M.jqxDropDownList({enableBrowserBoundsDetection: false,source: t})
                }
                if (N == "boolean" || N == "bool") {
                    f.jqxDropDownList({autoDropDownHeight: true,selectedIndex: 0});
                    M.jqxDropDownList({autoDropDownHeight: true,selectedIndex: 0})
                } else {
                    var G = false;
                    if (t && t.length) {
                        if (t.length < 5) {
                            G = true
                        }
                    }
                    f.jqxDropDownList({autoDropDownHeight: G,selectedIndex: 2});
                    M.jqxDropDownList({autoDropDownHeight: G,selectedIndex: 2})
                }
                l.jqxDropDownList({selectedIndex: 0});
                var y = D.filter;
                var J = new a.jqx.filter();
                var w = "";
                switch (N) {
                    case "number":
                    case "int":
                    case "float":
                    case "decimal":
                        w = "numericfilter";
                        n = J.getoperatorsbyfiltertype("numericfilter");
                        break;
                    case "boolean":
                    case "bool":
                        w = "booleanfilter";
                        n = J.getoperatorsbyfiltertype("booleanfilter");
                        break;
                    case "date":
                    case "time":
                        w = "datefilter";
                        n = J.getoperatorsbyfiltertype("datefilter");
                        break;
                    case "string":
                        w = "stringfilter";
                        n = J.getoperatorsbyfiltertype("stringfilter");
                        break
                }
                if (y != null) {
                    var e = y.getfilterat(0);
                    var d = y.getfilterat(1);
                    var H = y.getoperatorat(0);
                    if (r.updatefilterconditions) {
                        var n = [];
                        var q = r.updatefilterconditions(w, n);
                        if (q != undefined) {
                            for (var L = 0; L < q.length; L++) {
                                q[L] = q[L].toUpperCase()
                            }
                            y.setoperatorsbyfiltertype(w, q);
                            n = q
                        }
                    }
                    var v = "default";
                    if (e != null) {
                        var C = n.indexOf(e.comparisonoperator);
                        var z = e.filtervalue;
                        j.val(z);
                        f.jqxDropDownList({selectedIndex: C,animationType: v})
                    }
                    if (d != null) {
                        var B = n.indexOf(d.comparisonoperator);
                        var x = d.filtervalue;
                        h.val(x);
                        M.jqxDropDownList({selectedIndex: B,animationType: v})
                    }
                    if (y.getoperatorat(0) == undefined) {
                        l.jqxDropDownList({selectedIndex: 0,animationType: v})
                    } else {
                        if (y.getoperatorat(0) == "and" || y.getoperatorat(0) == 0) {
                            l.jqxDropDownList({selectedIndex: 0})
                        } else {
                            l.jqxDropDownList({selectedIndex: 1})
                        }
                    }
                }
                if (r.updatefilterpanel) {
                    r.updatefilterpanel(f, M, l, j, h, k, K, y, w, n)
                }
                if (!this._hasdatefilter || (this._hasdatefilter && D.filtertype != "date")) {
                    j.focus();
                    setTimeout(function() {
                        j.focus()
                    }, 10)
                }
            } else {
                var u = r._getfilterdataadapter(D);
                var w = r._getfiltertype(N);
                if (D.cellsformat) {
                    f.jqxListBox({displayMember: D.displayfield,valueMember: D.displayfield + "JQValue",source: u})
                } else {
                    f.jqxListBox({displayMember: D.displayfield,valueMember: D.displayfield,source: u})
                }
                f.jqxListBox("insertAt", r.gridlocalization.filterselectallstring, 0);
                var E = f.data().jqxListBox.instance;
                E.checkAll(false);
                var A = this;
                if (D.filter) {
                    E.uncheckAll(false);
                    var s = D.filter.getfilters();
                    for (var I = 0; I < E.items.length; I++) {
                        var F = E.items[I].value;
                        a.each(s, function() {
                            if (this.condition == "NOT_EQUAL") {
                                if (F != this.value) {
                                    E.uncheckIndex(I, false, false);
                                    return false
                                }
                            } else {
                                if (this.condition == "EQUAL") {
                                    if (F == this.value) {
                                        E.checkIndex(I, false, false);
                                        return false
                                    }
                                }
                            }
                        })
                    }
                    E._updateCheckedItems();
                    var p = E.getCheckedItems().length;
                    if (E.items.length != p && p > 0) {
                        E.host.jqxListBox("indeterminateIndex", 0, true, false)
                    }
                    if (p === E.items.length - 1) {
                        E.host.jqxListBox("checkIndex", 0, true, false)
                    }
                }
            }
        },_initfilterpanel: function(x, b, c, o) {
            if (x == null || x == undefined) {
                x = this
            }
            b[0].innerHTML = "";
            var s = a("<div class='filter' style='margin-left: 7px;'></div>");
            b.append(s);
            var n = a("<div class='filter' style='margin-top: 3px; margin-bottom: 3px;'></div>");
            n.text(x.gridlocalization.filtershowrowstring);
            this._showwhere = n;
            var u = a("<div class='filter' id='filter1" + x.element.id + "'></div>");
            var h = a("<div class='filter' id='filter2" + x.element.id + "' style='margin-bottom: 3px;'></div>");
            var r = a("<div class='filter' id='filter3" + x.element.id + "'></div>");
            var e = x._getcolumntypebydatafield(c);
            if (!u.jqxDropDownList) {
                throw new Error("jqxGrid: jqxdropdownlist.js is not loaded.");
                return
            }
            var p = x._getfiltersbytype(e);
            this._hasdatefilter = false;
            this._filterpanelwidth = o;
            if (this.columns && this.columns.records) {
                for (var t = 0; t < this.columns.records.length; t++) {
                    if (this.columns.records[t].filtertype == "date") {
                        this._hasdatefilter = true;
                        break
                    }
                }
            }
            var k = a("<div class='filter'><input class='filtertext1" + x.element.id + "' style='height: 20px; margin-top: 3px; margin-bottom: 3px;' type='text'></input></div>");
            var m = k.find("input");
            m.addClass(this.toThemeProperty("jqx-input"));
            m.addClass(this.toThemeProperty("jqx-widget-content"));
            m.addClass(this.toThemeProperty("jqx-rc-all"));
            m.width(o - 15);
            var l = a("<div class='filter'><input class='filtertext2" + x.element.id + "' style='height: 20px; margin-top: 3px;' type='text'></input></div>");
            var j = l.find("input");
            j.addClass(this.toThemeProperty("jqx-input"));
            j.addClass(this.toThemeProperty("jqx-widget-content"));
            j.addClass(this.toThemeProperty("jqx-rc-all"));
            j.width(o - 15);
            if (x.rtl) {
                m.css("direction", "rtl");
                j.css("direction", "rtl")
            }
            var g = a("<div class='filter' style='height: 25px; margin-left: 20px; margin-top: 7px;'></div>");
            var f = a('<span tabIndex=0 id="filterbutton' + x.element.id + '" class="filterbutton" style="padding: 4px 12px; margin-left: 2px;">' + x.gridlocalization.filterstring + "</span>");
            g.append(f);
            var v = a('<span tabIndex=0 id="filterclearbutton' + x.element.id + '" class="filterclearbutton" style="padding: 4px 12px; margin-left: 5px;">' + x.gridlocalization.filterclearstring + "</span>");
            g.append(v);
            f.jqxButton({height: 20,theme: x.theme});
            v.jqxButton({height: 20,theme: x.theme});
            var w = function(y) {
                if (y) {
                    if (y.text().indexOf("case sensitive") != -1) {
                        var i = y.text();
                        i = i.replace("case sensitive", "match case");
                        y.text(i)
                    }
                    y.css("font-family", x.host.css("font-family"));
                    y.css("font-size", x.host.css("font-size"));
                    y.css("top", "1px");
                    y.css("position", "relative");
                    return y
                }
                return ""
            };
            if (this.filtermode === "default") {
                s.append(n);
                s.append(u);
                u.jqxDropDownList({_checkForHiddenParent: false,rtl: x.rtl,enableBrowserBoundsDetection: false,selectedIndex: 2,width: o - 15,height: 20,dropDownHeight: 150,dropDownWidth: o - 15,selectionRenderer: w,source: p,theme: x.theme});
                s.append(k);
                var q = new Array();
                q[0] = x.gridlocalization.filterandconditionstring;
                q[1] = x.gridlocalization.filterorconditionstring;
                h.jqxDropDownList({_checkForHiddenParent: false,rtl: x.rtl,enableBrowserBoundsDetection: false,autoDropDownHeight: true,selectedIndex: 0,width: 60,height: 20,source: q,selectionRenderer: w,theme: x.theme});
                s.append(h);
                r.jqxDropDownList({_checkForHiddenParent: false,rtl: x.rtl,enableBrowserBoundsDetection: false,selectedIndex: 2,width: o - 15,height: 20,dropDownHeight: 150,dropDownWidth: o - 15,selectionRenderer: w,source: p,theme: x.theme});
                s.append(r);
                s.append(l)
            } else {
                s.append(n);
                s.append(u);
                u.jqxListBox({rtl: x.rtl,_checkForHiddenParent: false,checkboxes: true,selectedIndex: 2,width: o - 15,height: 120,theme: x.theme});
                var d = true;
                x.addHandler(u, "checkChange", function(z) {
                    if (!d) {
                        return
                    }
                    if (z.args.label != x.gridlocalization.filterselectallstring) {
                        d = false;
                        u.jqxListBox("checkIndex", 0, true, false);
                        var i = u.jqxListBox("getCheckedItems");
                        var y = u.jqxListBox("getItems");
                        if (i.length == 1) {
                            u.jqxListBox("uncheckIndex", 0, true, false)
                        } else {
                            if (y.length != i.length) {
                                u.jqxListBox("indeterminateIndex", 0, true, false)
                            }
                        }
                        d = true
                    } else {
                        d = false;
                        if (z.args.checked) {
                            u.jqxListBox("checkAll", false)
                        } else {
                            u.jqxListBox("uncheckAll", false)
                        }
                        d = true
                    }
                })
            }
            s.append(g);
            if (x.updatefilterpanel) {
                x.updatefilterpanel(u, r, h, k, l, f, v, null, null, p)
            }
        }})
})(jqxBaseFramework);