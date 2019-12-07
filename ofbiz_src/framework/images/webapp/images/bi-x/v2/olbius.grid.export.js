/*
 jQWidgets v4.0.0 (2016-Jan)
 Copyright (c) 2011-2016 jQWidgets.
 License: http://jqwidgets.com/license/
 */

(function (a) {
    a.extend(a.jqx._jqxGrid.prototype, {
        exportdata: function (q, z, y, o, r, t, f, data) {
            if (!a.jqx.dataAdapter.ArrayExporter) {
                throw"jqxGrid: Missing reference to jqxdata.export.js!"
            }
            if (y == undefined) {
                y = true
            }
            var H = this;
            if (o == undefined) {
                var o = this.getrows();
                if (o.length == 0) {
                	alert("No data to export");
                    throw"No data to export."
                }
            }
            this.exporting = true;
            if (!this.pageable) {
                this.loadondemand = true
            }
            if (this.altrows) {
                this._renderrows(this.virtualsizeinfo)
            }
            var F = r != undefined ? r : false;
            var E = {};
            var n = {};
            var v = [];
            var l = this.host.find(".jqx-grid-cell:first");
            var w = this.host.find(".jqx-grid-cell-alt:first");
            l.removeClass(this.toThemeProperty("jqx-grid-cell-selected"));
            l.removeClass(this.toThemeProperty("jqx-fill-state-pressed"));
            w.removeClass(this.toThemeProperty("jqx-grid-cell-selected"));
            w.removeClass(this.toThemeProperty("jqx-fill-state-pressed"));
            l.removeClass(this.toThemeProperty("jqx-grid-cell-hover"));
            l.removeClass(this.toThemeProperty("jqx-fill-state-hover"));
            w.removeClass(this.toThemeProperty("jqx-grid-cell-hover"));
            w.removeClass(this.toThemeProperty("jqx-fill-state-hover"));
            var g = "cell";
            var e = 1;
            var G = "column";
            var c = 1;
            var h = [];
            for (var B = 0; B < this.columns.records.length; B++) {
                var d = this.columns.records[B];
                if (d.cellclassname != "") {
                    d.customCellStyles = new Array();
                    if (typeof d.cellclassname == "string") {
                        for (var C = 0; C < o.length; C++) {
                            d.customCellStyles[C] = d.cellclassname
                        }
                    } else {
                        for (var C = 0; C < o.length; C++) {
                            var u = this.getrowboundindex(C);
                            var b = d.cellclassname(u, d.displayfield, o[C][d.displayfield], o[C]);
                            if (b) {
                                d.customCellStyles[C] = b
                            }
                        }
                    }
                }
            }
            var x = new Array();
            a.each(this.columns.records, function (K) {
                var N = a(H.table[0].rows[0].cells[K]);
                if (H.table[0].rows.length > 1) {
                    var j = a(H.table[0].rows[1].cells[K])
                }
                var J = this;
                var L = function (P) {
                    P.removeClass(H.toThemeProperty("jqx-grid-cell-selected"));
                    P.removeClass(H.toThemeProperty("jqx-fill-state-pressed"));
                    P.removeClass(H.toThemeProperty("jqx-grid-cell-hover"));
                    P.removeClass(H.toThemeProperty("jqx-fill-state-hover"));
                    if (J.customCellStyles) {
                        for (var Q in J.customCellStyles) {
                            P.removeClass(J.customCellStyles[Q])
                        }
                    }
                };
                L(N);
                if (j) {
                    L(j)
                }
                if (this.displayfield == null) {
                    return true
                }
                if (H.showaggregates) {
                    if (H.getcolumnaggregateddata) {
                        h.push(H.getcolumnaggregateddata(this.displayfield, this.aggregates, true, o))
                    }
                }
                var M = H._getexportcolumntype(this);
                if (this.exportable && (!this.hidden || F)) {
                    E[this.displayfield] = {};
                    E[this.displayfield].text = this.text;
                    E[this.displayfield].width = parseInt(this.width);
                    if (isNaN(E[this.displayfield].width)) {
                        E[this.displayfield].width = 60
                    }
                    E[this.displayfield].formatString = this.cellsformat;
                    E[this.displayfield].localization = H.gridlocalization;
                    E[this.displayfield].type = M;
                    E[this.displayfield].cellsAlign = this.cellsalign;
                    E[this.displayfield].hidden = !y;
                    E[this.displayfield].displayfield = this.displayfield;
                    x.push(E[this.displayfield])
                }
                g = "cell" + e;
                var O = a(this.element);
                if (this.element == undefined) {
                    O = a(this.uielement)
                }
                G = "column" + c;
                if (q == "html" || q == "xls" || q == "pdf") {
                    var i = function (P, X, W, Q, V, S, R, T, U) {
                        n[P] = {};
                        if (X == undefined) {
                            return
                        }
                        n[P]["font-size"] = X.css("font-size");
                        n[P]["font-weight"] = X.css("font-weight");
                        n[P]["font-style"] = X.css("font-style");
                        n[P]["background-color"] = S._getexportcolor(X.css("background-color"));
                        n[P]["color"] = S._getexportcolor(X.css("color"));
                        n[P]["border-color"] = S._getexportcolor(X.css("border-top-color"));
                        if (W) {
                            n[P]["text-align"] = V.align
                        } else {
                            n[P]["text-align"] = V.cellsalign;
                            n[P]["formatString"] = V.cellsformat;
                            n[P]["dataType"] = M
                        }
                        if (q == "html" || q == "pdf") {
                            n[P]["border-top-width"] = X.css("border-top-width");
                            n[P]["border-left-width"] = X.css("border-left-width");
                            n[P]["border-right-width"] = X.css("border-right-width");
                            n[P]["border-bottom-width"] = X.css("border-bottom-width");
                            n[P]["border-top-style"] = X.css("border-top-style");
                            n[P]["border-left-style"] = X.css("border-left-style");
                            n[P]["border-right-style"] = X.css("border-right-style");
                            n[P]["border-bottom-style"] = X.css("border-bottom-style");
                            if (W) {
                                if (R == 0) {
                                    n[P]["border-left-width"] = X.css("border-right-width")
                                }
                                n[P]["border-top-width"] = X.css("border-right-width");
                                n[P]["border-bottom-width"] = X.css("border-bottom-width")
                            } else {
                                if (R == 0) {
                                    n[P]["border-left-width"] = X.css("border-right-width")
                                }
                            }
                            n[P]["height"] = X.css("height")
                        }
                        if (V.exportable && (!V.hidden || F)) {
                            if (T == true) {
                                if (!E[V.displayfield].customCellStyles) {
                                    E[V.displayfield].customCellStyles = new Array()
                                }
                                E[V.displayfield].customCellStyles[U] = P
                            } else {
                                if (W) {
                                    E[V.displayfield].style = P
                                } else {
                                    if (!Q) {
                                        E[V.displayfield].cellStyle = P
                                    } else {
                                        E[V.displayfield].cellAltStyle = P
                                    }
                                }
                            }
                        }
                    };
                    i(G, O, true, false, this, H, K);
                    c++;
                    i(g, N, false, false, this, H, K);
                    if (H.altrows) {
                        g = "cellalt" + e;
                        i(g, j, false, true, this, H, K)
                    }
                    if (this.customCellStyles) {
                        for (var I in J.customCellStyles) {
                            N.removeClass(J.customCellStyles[I])
                        }
                        for (var I in J.customCellStyles) {
                            N.addClass(J.customCellStyles[I]);
                            i(g + J.customCellStyles[I], N, false, false, this, H, K, true, I);
                            N.removeClass(J.customCellStyles[I])
                        }
                    }
                    e++
                }
            });
            a.each(this.columns.records, function (i) {
                if (E[this.displayfield]) {
                    E[this.displayfield].columnsDataFields = x
                }
            });
            if (this.showaggregates) {
                var D = [];
                var A = q == "xls" ? "_AG" : "";
                var k = this.groupable ? this.groups.length : 0;
                if (this.rowdetails) {
                    k++
                }
                if (h.length > 0) {
                    a.each(this.columns.records, function (j) {
                        if (this.aggregates) {
                            for (var J = 0; J < this.aggregates.length; J++) {
                                if (!D[J]) {
                                    D[J] = {}
                                }
                                if (D[J]) {
                                    var K = H._getaggregatename(this.aggregates[J]);
                                    var L = H._getaggregatetype(this.aggregates[J]);
                                    var I = h[j - k];
                                    if (I) {
                                        D[J][this.displayfield] = A + K + ": " + I[L]
                                    }
                                }
                            }
                        }
                    });
                    a.each(this.columns.records, function (j) {
                        for (var I = 0; I < D.length; I++) {
                            if (D[I][this.displayfield] == undefined) {
                                D[I][this.displayfield] = A
                            }
                        }
                    })
                }
                a.each(D, function () {
                    o.push(this)
                })
            }
            var m = this;
            var s = a.jqx.dataAdapter.ArrayExporter(o, E, n);
            if (z == undefined) {
                this._renderrows(this.virtualsizeinfo);
                var p = s.exportTo(q);
                if (this.showaggregates) {
                    a.each(D, function () {
                        o.pop(this)
                    })
                }
                setTimeout(function () {
                    m.exporting = false
                }, 50);
                return p
            } else {
                s.exportToFile(q, z, t, f, undefined, data)
            }
            if (this.showaggregates) {
                a.each(D, function () {
                    o.pop(this)
                })
            }
            this._renderrows(this.virtualsizeinfo);
            setTimeout(function () {
                m.exporting = false
            }, 50)
        }
    })
})(jqxBaseFramework);