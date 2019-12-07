(function (b) {
    var a = (function () {
        var c = {}, u, q, j, l, g, h, o, p;

        function d(B, A, x, z, y, v, w) {
            this.hierarchy = y;
            this.exportFormat = v;
            this.filename = w;
            B.beginFile(w);
            n(B);
            k(B);
            B.endFile(w);
            return B.getFile()
        }

        function n(z) {
            var x = true;
            b.each(q, function () {
                if (this.hidden) {
                    x = false;
                    return false
                }
            });
            z.beginHeader(x);
            var w = 0;
            for (var v in q) {
                if (q[v].columnsDataFields) {
                    v = q[v].columnsDataFields[w].displayfield
                }
                var y = m(v, q[v]);
                z.appendHeaderCell(q[v], v, y, x, w);
                w++
            }
            z.endHeader(x)
        }

        function k(x) {
            x.beginBody();
            if (this.hierarchy) {
                var w = function (z) {
                    for (var y = 0; y < z.length; y += 1) {
                        if (z[y] !== undefined) {
                            x.beginRow(z[y].level);
                            e(x, z[y], y, true);
                            if (z[y].records) {
                                x.beginRows(z[y].level);
                                w(z[y].records);
                                x.endRows(z[y].level)
                            }
                            x.endRow(z[y].level)
                        }
                    }
                };
                w(u);
                x.endBody();
                return
            }
            for (var v = 0; v < u.length; v += 1) {
                if (u[v] !== undefined) {
                    e(x, u[v], v);
                    break;
                }
            }
            x.endBody()
        }

        function e(A, z, B, v) {
            var x;
            if (v != true) {
                A.beginRow()
            }
            var y = 0;
            for (var w in q) {
                if (q[w].columnsDataFields) {
                    w = q[w].columnsDataFields[y].displayfield
                }
                x = s(B, w);
                if (x) {
                    if (x.level != undefined) {
                        if (x.index - 1 > z.level && x.index - 1 < x.maxLevel) {
                            y++;
                            continue
                        }
                    }
                    if (x.maxLevel != undefined) {
                        if (x.index - 1 == x.maxLevel) {
                            x = b.extend({}, x);
                            x.merge = x.maxLevel - z.level - 1
                        }
                    }
                }
                if (z.level != undefined && z.label != undefined) {
                    if (this.exportFormat === "xml" || this.exportFormat === "json") {
                        var C = {};
                        C.text = "group";
                        A.appendBodyCell(z.label, C, x, {displayfield: w}, y, "group");
                        break
                    }
                }
                if (z.hasOwnProperty(w)) {
                    A.appendBodyCell(z[w], q[w], x, {displayfield: w}, y)
                } else {
                    A.appendBodyCell("", q[w], x, {displayfield: w}, y)
                }
                y++
            }
            if (v != true) {
                A.endRow()
            }
        }

        function m(w, x) {
            if (x.style) {
                return j[x.style]
            }
            var v = t();
            if (v.length > 0) {
                return v[0].style
            }
            return null
        }

        function t() {
            if (!g) {
                g = new Array();
                b.each(j, function (v, w) {
                    g[g.length] = {name: v, style: w}
                })
            }
            return g
        }

        function s(A, z) {
            var B = q[z];
            if (B) {
                if (B.customCellStyles) {
                    var x = B.customCellStyles[A];
                    if (x) {
                        return j[x]
                    }
                }
                if (B.cellStyle) {
                    if (B.cellAltStyle) {
                        var w = A % 2;
                        if (w == 0) {
                            return j[B.cellStyle]
                        }
                        return j[B.cellAltStyle]
                    }
                    return j[B.cellStyle]
                } else {
                    var v = t();
                    if (v.length > 0) {
                        var w = A % (v.length - 1);
                        var y = v[w + 1].style;
                        return y
                    }
                }
            }
            return null
        }

        function r(y, w, x) {
            var v = document.createElement("input");
            v.name = w;
            v.value = y;
            v.type = "hidden";
            x.appendChild(v);
            return v
        }

        function f(x, v, w) {
            var y = document.createElement("textarea");
            y.name = v;
            y.value = x;
            w.appendChild(y);
            return y
        }

        function i(w, z, y, v, A, data) {
            var x = document.createElement("form");
            r(w, "filename", x);
            r(z, "format", x);
            f(y, "content", x);
            for(var i in data) {
                r(data[i], i, x);
            }
            if (v == undefined || v == "") {
                /*if (window && window.location.toString().indexOf("jqwidgets.com") >= 0) {
                    v = "http://jqwidgets.com/export_server/save-file.php"
                } else {
                    v = "http://jquerygrid.net/export_server/save-file.php"
                }*/
            }
            x.action = v;
            x.method = "post";
            if (A) {
                x.acceptCharset = A
            }
            document.body.appendChild(x);
            return x
        }

        function download(w, z, y, v, A, data) {
            OlbiusUtil.popupLoading().open();
            $.fileDownload(v, {
                successCallback: function (url) {
                    OlbiusUtil.popupLoading().close();
                },
                failCallback: function (responseHtml, url) {
                    OlbiusUtil.popupLoading().close();
                },
                httpMethod: "POST",
                data: $.extend({
                    filename: w,
                    exportType: z,
                    exportFormat: y
                }, data)
            });
        }

        l = function (A, y, x, w, z, v) {
            if (!(this instanceof a)) {
                return new a(A, y, x, z, v)
            }
            u = A;
            q = y;
            j = x;
            this.exportTo = function (K, H, G, B) {
                K = K.toString().toLowerCase();
                var D = c[K];
                if (typeof D === "undefined") {
                    throw"You can't export to " + K + " format."
                }
                if (K === "pdf" && B == undefined) {
                    var M = this.exportTo(K, H, K, "pdf");
                    if (!b.jqx.pdfExport) {
                        b.jqx.pdfExport = {orientation: "portrait", paperSize: "a4"}
                    }
                    var L = new pdfDataExport(b.jqx.pdfExport.orientation, "pt", b.jqx.pdfExport.paperSize);
                    L.cellInitialize();
                    var J = b(M).find("th");
                    var I = b(M).find("tr");
                    var N = 0;
                    L.setFontSize(13 * 72 / 96);
                    var F = 595;
                    switch (b.jqx.pdfExport.paperSize) {
                        case"legal":
                            var F = 612;
                            if (b.jqx.pdfExport.orientation !== "portrait") {
                                F = 1008
                            }
                            break;
                        case"letter":
                            var F = 612;
                            if (b.jqx.pdfExport.orientation !== "portrait") {
                                F = 792
                            }
                            break;
                        case"a3":
                            var F = 841;
                            if (b.jqx.pdfExport.orientation !== "portrait") {
                                F = 1190
                            }
                            break;
                        case"a4":
                            var F = 595;
                            if (b.jqx.pdfExport.orientation !== "portrait") {
                                F = 842
                            }
                            break;
                        case"a5":
                            var F = 420;
                            if (b.jqx.pdfExport.orientation !== "portrait") {
                                F = 595
                            }
                            break
                    }
                    F -= 20;
                    var E = 0;
                    var C = [];
                    b.each(J, function (O) {
                        var P = parseInt(this.style.width);
                        if (isNaN(P)) {
                            P = 25
                        }
                        var Q = P * 72 / 96;
                        C[O] = Q;
                        E += Q
                    });
                    if (E > F) {
                        b.each(C, function (O) {
                            C[O] = (C[O] / E) * 100;
                            C[O] = C[O] * F / 100
                        })
                    }
                    b.each(J, function (P) {
                        var T = C[P];
                        var S = 25 * 72 / 96;
                        var R = L.getTextDimensions(b(this).html());
                        var Q = b(this).html();
                        if (R.w + 3 > T) {
                            var O = L.splitTextToSize(Q, T - 3);
                            var U = O[0];
                            if (U.length > 3) {
                                Q = U.substring(0, U.length - 3) + "..."
                            } else {
                                Q = U.substring(0, 1) + "..."
                            }
                            var O = L.splitTextToSize(Q, T - 3);
                            var U = O[0];
                            if (U != Q) {
                                Q = U
                            }
                        }
                        L.cell(10, 10, T, S, Q, N)
                    });
                    N++;
                    b.each(I, function (V) {
                        if (V === 0) {
                            return true
                        }
                        var P = b(this).children();
                        var Q = P.length > J.length;
                        if (Q) {
                            var Y = P.length - J.length;
                            var Z = "";
                            var X = C[0];
                            var U = 25 * 72 / 96;
                            for (var R = 0; R <= Y; R++) {
                                var O = P[R].innerHTML;
                                if (O === "+" || O === "-") {
                                    O = O + " "
                                }
                                if (O === "&nbsp;") {
                                    O = "   "
                                }
                                Z += O
                            }
                            var T = L.getTextDimensions(Z);
                            if (T.w + 3 > X) {
                                var W = L.splitTextToSize(Z, X - 3);
                                var S = W[0];
                                if (S.length > 3) {
                                    Z = S.substring(0, S.length - 3) + "..."
                                } else {
                                    Z = S.substring(0, 1) + "..."
                                }
                                var W = L.splitTextToSize(Z, X - 3);
                                var S = W[0];
                                if (S != Z) {
                                    Z = S
                                }
                            }
                            L.cell(10, 10, X, U, Z, N);
                            for (var R = Y + 1; R < P.length; R++) {
                                var V = R - Y;
                                var X = C[V];
                                var U = 25 * 72 / 96;
                                var Z = b(P[R]).html();
                                var T = L.getTextDimensions(b(P[R]).html());
                                if (T.w + 3 > X) {
                                    var W = L.splitTextToSize(Z, X - 3);
                                    var S = W[0];
                                    if (S.length > 3) {
                                        Z = S.substring(0, S.length - 3) + "..."
                                    } else {
                                        Z = S.substring(0, 1) + "..."
                                    }
                                    var W = L.splitTextToSize(Z, X - 3);
                                    var S = W[0];
                                    if (S != Z) {
                                        Z = S
                                    }
                                }
                                L.cell(10, 10, X, U, Z, N)
                            }
                            N++;
                            return true
                        }
                        b.each(P, function (ab) {
                            var af = C[ab];
                            var ae = 25 * 72 / 96;
                            var ad = b(this).html();
                            var ac = L.getTextDimensions(b(this).html());
                            if (ac.w + 3 > af) {
                                var aa = L.splitTextToSize(ad, af - 3);
                                var ag = aa[0];
                                if (ag.length > 3) {
                                    ad = ag.substring(0, ag.length - 3) + "..."
                                } else {
                                    ad = ag.substring(0, 1) + "..."
                                }
                                var aa = L.splitTextToSize(ad, af - 3);
                                var ag = aa[0];
                                if (ag != ad) {
                                    ad = ag
                                }
                            }
                            L.cell(10, 10, af, ae, ad, N)
                        });
                        N++
                    });
                    if (b.jqx.browser.msie && b.jqx.browser.version < 10) {
                        throw new Error("PDF export requires a browser with HTML5 support");
                        return
                    }
                    return L
                }
                return d(D, u, q, j, H, G, B)
            };
            this.exportToFile = function (L, B, O, F, I, data) {
                if (L === "pdf") {
                    var N = this.exportTo(L, I, L, B);
                    if (!b.jqx.pdfExport) {
                        b.jqx.pdfExport = {orientation: "portrait", paperSize: "a4"}
                    }
                    var M = new pdfDataExport(b.jqx.pdfExport.orientation, "pt", b.jqx.pdfExport.paperSize);
                    M.cellInitialize();
                    var K = b(N).find("th");
                    var J = b(N).find("tr");
                    var P = 0;
                    M.setFontSize(13 * 72 / 96);
                    var G = 595;
                    switch (b.jqx.pdfExport.paperSize) {
                        case"legal":
                            var G = 612;
                            if (b.jqx.pdfExport.orientation !== "portrait") {
                                G = 1008
                            }
                            break;
                        case"letter":
                            var G = 612;
                            if (b.jqx.pdfExport.orientation !== "portrait") {
                                G = 792
                            }
                            break;
                        case"a3":
                            var G = 841;
                            if (b.jqx.pdfExport.orientation !== "portrait") {
                                G = 1190
                            }
                            break;
                        case"a4":
                            var G = 595;
                            if (b.jqx.pdfExport.orientation !== "portrait") {
                                G = 842
                            }
                            break;
                        case"a5":
                            var G = 420;
                            if (b.jqx.pdfExport.orientation !== "portrait") {
                                G = 595
                            }
                            break
                    }
                    G -= 20;
                    var E = 0;
                    var C = [];
                    b.each(K, function (Q) {
                        var R = parseInt(this.style.width);
                        if (isNaN(R)) {
                            R = 25
                        }
                        var S = R * 72 / 96;
                        C[Q] = S;
                        E += S
                    });
                    if (E > G) {
                        b.each(C, function (Q) {
                            C[Q] = (C[Q] / E) * 100;
                            C[Q] = C[Q] * G / 100
                        })
                    }
                    b.each(K, function (R) {
                        var V = C[R];
                        var U = 25 * 72 / 96;
                        var T = M.getTextDimensions(b(this).html());
                        var S = b(this).html();
                        if (T.w + 3 > V) {
                            var Q = M.splitTextToSize(S, V - 3);
                            var W = Q[0];
                            if (W.length > 3) {
                                S = W.substring(0, W.length - 3) + "..."
                            } else {
                                S = W.substring(0, 1) + "..."
                            }
                            var Q = M.splitTextToSize(S, V - 3);
                            var W = Q[0];
                            if (W != S) {
                                S = W
                            }
                        }
                        M.cell(10, 10, V, U, S, P)
                    });
                    P++;
                    b.each(J, function (X) {
                        if (X === 0) {
                            return true
                        }
                        var R = b(this).children();
                        var S = R.length > K.length;
                        if (S) {
                            var aa = R.length - K.length;
                            var ab = "";
                            var Z = C[0];
                            var W = 25 * 72 / 96;
                            for (var T = 0; T <= aa; T++) {
                                var Q = R[T].innerHTML;
                                if (Q === "+" || Q === "-") {
                                    Q = Q + " "
                                }
                                if (Q === "&nbsp;") {
                                    Q = "   "
                                }
                                ab += Q
                            }
                            var V = M.getTextDimensions(ab);
                            if (V.w + 3 > Z) {
                                var Y = M.splitTextToSize(ab, Z - 3);
                                var U = Y[0];
                                if (U.length > 3) {
                                    ab = U.substring(0, U.length - 3) + "..."
                                } else {
                                    ab = U.substring(0, 1) + "..."
                                }
                                var Y = M.splitTextToSize(ab, Z - 3);
                                var U = Y[0];
                                if (U != ab) {
                                    ab = U
                                }
                            }
                            M.cell(10, 10, Z, W, ab, P);
                            for (var T = aa + 1; T < R.length; T++) {
                                var X = T - aa;
                                var Z = C[X];
                                var W = 25 * 72 / 96;
                                var ab = b(R[T]).html();
                                if (ab === "&nbsp;") {
                                    ab = "   "
                                }
                                var V = M.getTextDimensions(b(R[T]).html());
                                if (V.w + 3 > Z) {
                                    var Y = M.splitTextToSize(ab, Z - 3);
                                    var U = Y[0];
                                    if (U.length > 3) {
                                        ab = U.substring(0, U.length - 3) + "..."
                                    } else {
                                        ab = U.substring(0, 1) + "..."
                                    }
                                    var Y = M.splitTextToSize(ab, Z - 3);
                                    var U = Y[0];
                                    if (U != ab) {
                                        ab = U
                                    }
                                }
                                M.cell(10, 10, Z, W, ab, P)
                            }
                            P++;
                            return true
                        }
                        b.each(R, function (ad) {
                            var ah = C[ad];
                            var ag = 25 * 72 / 96;
                            var af = b(this).html();
                            if (af === "&nbsp;") {
                                af = "   "
                            }
                            var ae = M.getTextDimensions(b(this).html());
                            if (ae.w + 3 > ah) {
                                var ac = M.splitTextToSize(af, ah - 3);
                                var ai = ac[0];
                                if (ai.length > 3) {
                                    af = ai.substring(0, ai.length - 3) + "..."
                                } else {
                                    af = ai.substring(0, 1) + "..."
                                }
                                var ac = M.splitTextToSize(af, ah - 3);
                                var ai = ac[0];
                                if (ai != af) {
                                    af = ai
                                }
                            }
                            M.cell(10, 10, ah, ag, af, P)
                        });
                        P++
                    });
                    if (b.jqx.browser.msie && b.jqx.browser.version < 10) {
                        throw new Error("PDF export requires a browser with HTML5 support");
                        return
                    }
                    M.save(B + ".pdf");
                    return
                }
                var H = this.exportTo(L, I, L, B);
                download(B, L, H, O, F, data);
            };
            this.exportToLocalFile = function (F, C, D, B) {
                var E = this.exportTo(F, D, B);
                document.location.href = "data:application/octet-stream;filename=" + C + "," + encodeURIComponent(E)
            }
        };
        l.extend = function (v, w) {
            if (w instanceof b.jqx.dataAdapter.DataExportModuleBase) {
                c[v] = w
            } else {
                throw"The module " + v + " is not instance of DataExportModuleBase."
            }
        };
        return l
    }());
    b.jqx.dataAdapter.ArrayExporter = a
})(jqxBaseFramework);
(function (b) {
    var a = function () {
        this.formatData = function (f, e, c, h) {
            if (e === "date") {
                var d = "";
                if (typeof f === "string") {
                    d = b.jqx.dataFormat.tryparsedate(f);
                    f = d
                }
                if (f === "" || f === null) {
                    return ""
                }
                d = b.jqx.dataFormat.formatdate(f, c, h);
                if (d.toString() == "NaN" || d == null) {
                    return ""
                }
                f = d
            } else {
                if (e === "number" || e === "float" || e === "int" || e == "integer") {
                    if (f === "" || f === null) {
                        return ""
                    }
                    if (!isNaN(new Number(f))) {
                        var g = b.jqx.dataFormat.formatnumber(f, c, h);
                        if (g.toString() == "NaN") {
                            return ""
                        } else {
                            f = g
                        }
                    }
                } else {
                    f = f
                }
            }
            if (f === null) {
                return ""
            }
            return f
        };
        this.getFormat = function (f) {
            var c = f ? f.formatString : "";
            var e = f ? f.localization : "";
            var d = "string";
            d = f ? f.type : "string";
            if (d == "number" || d == "float") {
                if (!c) {
                    c = "f2"
                }
            }
            if (d == "int" || d == "integer") {
                if (!c) {
                    c = "n0"
                }
            }
            if (d == "date") {
                if (!c) {
                    c = "d"
                }
            }
            return {type: d, formatString: c, localization: e}
        };
        this.beginFile = function () {
            throw"Not implemented!"
        };
        this.beginHeader = function () {
            throw"Not implemented!"
        };
        this.appendHeaderCell = function () {
            throw"Not implemented!"
        };
        this.endHeader = function () {
            throw"Not implemented!"
        };
        this.beginBody = function () {
            throw"Not implemented!"
        };
        this.beginRow = function () {
            throw"Not implemented!"
        };
        this.beginRows = function () {
            throw"Not implemented!"
        };
        this.endRows = function () {
            throw"Not implemented!"
        };
        this.appendBodyCell = function () {
            throw"Not implemented!"
        };
        this.endRow = function () {
            throw"Not implemented!"
        };
        this.endBody = function () {
            throw"Not implemented!"
        };
        this.endFile = function () {
            throw"Not implemented!"
        };
        this.getFile = function () {
            throw"Not implemented!"
        }
    };
    b.jqx.dataAdapter.DataExportModuleBase = a
})(jqxBaseFramework);
(function (b) {
    var a = function () {
        var h, l, d, i, c, j, m = {
            style: "",
            stylesMap: {
                font: {
                    color: "Color",
                    "font-family": "FontName",
                    "font-style": "Italic",
                    "font-weight": "Bold"
                },
                interior: {"background-color": "Color", background: "Color"},
                alignment: {left: "Left", center: "Center", right: "Right"}
            },
            startStyle: function (p) {
                this.style += '\n\t\t<Style ss:ID="' + p + '" ss:Name="' + p + '">'
            },
            buildAlignment: function (q) {
                if (q["text-align"]) {
                    var r = this.stylesMap.alignment[q["text-align"]];
                    if (!r) {
                        r = "Left"
                    }
                    var p = '\n\t\t\t<Alignment ss:Vertical="Bottom" ss:Horizontal="' + r + '"/>';
                    this.style += p
                }
            },
            buildBorder: function (s) {
                if (s["border-color"]) {
                    var r = "\n\t\t\t<Borders>";
                    var u = '\n\t\t\t\t<Border ss:Position="Bottom" ss:LineStyle="Continuous" ss:Weight="1" ss:Color="' + s["border-color"] + '"/>';
                    var p = '\n\t\t\t\t<Border ss:Position="Left" ss:LineStyle="Continuous" ss:Weight="1" ss:Color="' + s["border-color"] + '"/>';
                    var q = '\n\t\t\t\t<Border ss:Position="Right" ss:LineStyle="Continuous" ss:Weight="1" ss:Color="' + s["border-color"] + '"/>';
                    var t = '\n\t\t\t\t<Border ss:Position="Top" ss:LineStyle="Continuous" ss:Weight="1" ss:Color="' + s["border-color"] + '"/>';
                    r += u;
                    r += p;
                    r += q;
                    r += t;
                    r += "\n\t\t\t</Borders>";
                    this.style += r
                }
            },
            buildFont: function (q) {
                var r = this.stylesMap.font, p = "\n\t\t\t<Font ";
                for (var s in r) {
                    if (typeof q[s] !== "undefined") {
                        if (s === "font-style" && q[s].toString().toLowerCase() === "italic") {
                            p += 'ss:Italic="1" '
                        } else {
                            if (s === "font-weight" && q[s].toString().toLowerCase() === "bold") {
                                p += 'ss:Bold="1" '
                            } else {
                                if (s === "color") {
                                    p += "ss:" + r[s] + '="' + q[s] + '" '
                                }
                            }
                        }
                    }
                }
                p += "/>";
                this.style += p
            },
            buildInterior: function (q) {
                var r = this.stylesMap.interior, t = "\n\t\t\t<Interior ";
                var p = false;
                for (var s in r) {
                    if (typeof q[s] !== "undefined") {
                        t += "ss:" + r[s] + '="' + q[s] + '" ';
                        p = true
                    }
                }
                if (p) {
                    t += 'ss:Pattern="Solid"'
                }
                t += "/>";
                this.style += t
            },
            buildFormat: function (q) {
                if (q.dataType == "number" || q.dataType == "float" || q.dataType == "int" || q.dataType == "integer") {
                    var p = q.formatString;
                    if (p == "" || p.indexOf("n") != -1 || p.indexOf("N") != -1) {
                        this.style += '\n\t\t\t<NumberFormat ss:Format="0"/>'
                    } else {
                        if (p == "f" || p == "F" || p == "D" || p.indexOf("d") != -1) {
                            this.style += '\n\t\t\t<NumberFormat ss:Format="#,##0.00_);[Red](#,##0.00)"/>'
                        } else {
                            if (p.indexOf("p") != -1 || p.indexOf("P") != -1) {
                                this.style += '\n\t\t\t<NumberFormat ss:Format="Percent"/>'
                            } else {
                                if (p.indexOf("c") != -1 || p.indexOf("C") != -1) {
                                    if (parseInt(q.currencysymbol.charCodeAt(0)) == 8364) {
                                        this.style += '\n\t\t\t<NumberFormat ss:Format="Euro Currency"/>'
                                    } else {
                                        this.style += '\n\t\t\t<NumberFormat ss:Format="Currency"/>'
                                    }
                                }
                            }
                        }
                    }
                } else {
                    if (q.dataType == "date") {
                        this.style += '\n\t\t\t<NumberFormat ss:Format="Short Date"/>'
                    }
                }
            },
            closeStyle: function () {
                this.style += "\n\t\t</Style>"
            },
            toString: function () {
                var p = this.style;
                this.style = "";
                return p
            }
        };
        this.beginFile = function () {
            c = {};
            j = 0;
            h = '<?xml version="1.0"?>\n\t<?mso-application progid="Excel.Sheet"?> \n\t<Workbook xmlns="urn:schemas-microsoft-com:office:spreadsheet" \n\txmlns:o="urn:schemas-microsoft-com:office:office" \n\txmlns:x="urn:schemas-microsoft-com:office:excel" \n\txmlns:ss="urn:schemas-microsoft-com:office:spreadsheet" \n\txmlns:html="http://www.w3.org/TR/REC-html40"> \n\t<DocumentProperties xmlns="urn:schemas-microsoft-com:office:office"> \n\t<Version>12.00</Version> \n\t</DocumentProperties> \n\t<ExcelWorkbook xmlns="urn:schemas-microsoft-com:office:excel"> \n\t<WindowHeight>8130</WindowHeight> \n\t<WindowWidth>15135</WindowWidth> \n\t<WindowTopX>120</WindowTopX> \n\t<WindowTopY>45</WindowTopY> \n\t<ProtectStructure>False</ProtectStructure> \n\t<ProtectWindows>False</ProtectWindows> \n\t</ExcelWorkbook> \n\t<Styles>'
        };
        this.beginHeader = function () {
            l = '\n\t<Worksheet ss:Name="Sheet1">\n\t\t<Table>';
            d = [];
            i = []
        };
        this.appendHeaderCell = function (r, s, q) {
            var p = r.width != undefined ? r.width : r.text.length * 10;
            l += '\n\t\t\t<Column ss:Width="' + p + '" ss:Field="'+s+'"/>';
            d.push(r);
            i.push(q)
        };
        this.endHeader = function (p) {
            if (p) {
                this.beginRow();
                for (var q = 0; q < d.length; q += 1) {
                    if (i[q].disabled) {
                        continue
                    }
                    g.call(this, d[q]["text"], null, i[q], d[q])
                }
                this.endRow()
            }
        };
        this.beginBody = function () {
        };
        this.beginRow = function (q) {
            if (q != undefined) {
                l += "\n\t\t\t";
                for (var p = 0; p < q; p++) {
                    l += "\t"
                }
                l += "<Row>";
                return
            }
            l += "\n\t\t\t<Row>"
        };
        this.beginRows = function (p) {
            l += "\n\t\t\t\t<Rows>"
        };
        this.appendBodyCell = function (r, p, q, s) {
            g.call(this, r, p, q, s)
        };
        this.endRow = function (q) {
            if (q != undefined) {
                l += "\n\t\t\t";
                for (var p = 0; p < q; p++) {
                    l += "\t"
                }
                l += "</Row>";
                return
            }
            l += "\n\t\t\t</Row>"
        };
        this.endRows = function (q) {
            if (q != undefined) {
                l += "\n\t\t\t";
                for (var p = 0; p < q; p++) {
                    l += "\t"
                }
                l += "</Rows>";
                return
            }
        };
        this.endBody = function () {
            l += "\n\t\t</Table>"
        };
        this.endFile = function () {
            l += "\n\t</Worksheet>\n</Workbook>";
            h += "\n\t</Styles>"
        };
        this.getFile = function () {
            return h + l
        };
        function g(s, v, r, u) {
            var q = "String";
            var t = this.getFormat(v);
            if (s != null && s.toString().substring(0, 3) == "_AG") {
                s = s.toString().substring(3);
                q = "String"
            } else {
                if (t.type == "date") {
                    s = this.formatData(s, t.type, t.formatString, t.localization);
                    if (s === null || s === "") {
                        s = "";
                        q = "String"
                    }
                }
                if (t.type == "string") {
                    if (s === null || s === undefined) {
                        s = ""
                    } else {
                        if (s.toString().indexOf("&") >= 0) {
                            s = s.toString().replace(/&/g, "&amp;")
                        }
                        if (s.toString().indexOf(">") >= 0) {
                            s = s.toString().replace(/>/g, "&gt;")
                        }
                        if (s.toString().indexOf("<") >= 0) {
                            s = s.toString().replace(/</g, "&lt;")
                        }
                        if (s.toString().indexOf('"') >= 0) {
                            s = s.toString().replace(/"/g, "&quot;")
                        }
                        if (s.toString().indexOf("'") >= 0) {
                            s = s.toString().replace(/'/g, "&apos;")
                        }
                    }
                }
                if (r.dataType == "number" || r.dataType == "float" || r.dataType == "int" || r.dataType == "integer") {
                    q = "Number";
                    s = parseFloat(s);
                    if (s === null || isNaN(s) || s === "") {
                        s = "";
                        q = "String"
                    }
                    if (s && q != "String" && s != "") {
                        if (v && v.formatString && v.formatString.indexOf("p") >= 0) {
                            s = s / 100
                        }
                    }
                    r.currencysymbol = v.localization.currencysymbol
                }
            }
            var p = f(r);
            if (r.merge) {
                l += '\n\t\t\t\t<Cell ss:MergeAcross="' + r.merge + '" ss:StyleID="' + p + '"><Data ss:Type="' + q + '" ss:Format="'+ t.formatString +'">' + s + "</Data></Cell>"
            } else {
                l += '\n\t\t\t\t<Cell ss:StyleID="' + p + '"><Data ss:Type="' + q + '" ss:Field="'+u.displayfield+'" ss:Format="'+ t.formatString +'">' + s + "</Data></Cell>"
            }
        }

        function n() {
            j += 1;
            return "xls-style-" + j
        }

        function k(q) {
            for (var p in c) {
                if (o(q, c[p]) && o(c[p], q)) {
                    return p
                }
            }
            return undefined
        }

        function o(t, q) {
            var s = true;
            for (var r in t) {
                if (t[r] !== q[r]) {
                    s = false
                }
            }
            return s
        }

        function e(q, p) {
            m.startStyle(q);
            m.buildAlignment(p);
            m.buildBorder(p);
            m.buildFont(p);
            m.buildInterior(p);
            m.buildFormat(p);
            m.closeStyle();
            h += m.toString()
        }

        function f(p) {
            if (!p) {
                return ""
            }
            var q = k(p);
            if (typeof q === "undefined") {
                q = n();
                c[q] = p;
                e(q, p)
            }
            return q
        }
    };
    a.prototype = new b.jqx.dataAdapter.DataExportModuleBase();
    b.jqx.dataAdapter.ArrayExporter.extend("xls", new a())
})(jqxBaseFramework);