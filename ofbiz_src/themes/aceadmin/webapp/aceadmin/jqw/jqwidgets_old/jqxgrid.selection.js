/*
 jQWidgets v3.5.0 (2014-Sep-15)
 Copyright (c) 2011-2014 jQWidgets.
 License: http://jqwidgets.com/license/
 */

(function(a) {
	a.extend(a.jqx._jqxGrid.prototype, {
		selectallrows : function() {
			this._trigger = false;
			var d = this.virtualmode ? this.dataview.totalrecords : this.dataview.loadedrecords.length;
			this.selectedrowindexes = new Array();
			var e = this.dataview.loadedrecords;
			for (var c = 0; c < d; c++) {
				var f = e[c];
				if (!f) {
					this.selectedrowindexes[c] = c;
					continue
				}
				var b = this.getboundindex(f);
				if (b != undefined) {
					this.selectedrowindexes[c] = b
				}
			}
			if (this.selectionmode == "checkbox" && !this._checkboxcolumnupdating) {
				if (this._checkboxcolumn) {
					this._checkboxcolumn.checkboxelement.jqxCheckBox({
						checked : true
					})
				}
			}
			this._renderrows(this.virtualsizeinfo);
			this._trigger = true;
			if (this.selectionmode == "checkbox") {
				this._raiseEvent(2, {
					rowindex : this.selectedrowindexes
				})
			}
		},
		unselectallrows : function() {
			this._trigger = false;
			var c = this.virtualmode ? this.dataview.totalrecords : this.dataview.loadedrecords.length;
			this.selectedrowindexes = new Array();
			var d = this.dataview.loadedrecords;
			for (var b = 0; b < c; b++) {
				var e = d[b];
				if (!e) {
					this.selectedrowindexes[b] = b;
					continue
				}
				delete this.selectedrowindexes[b]
			}
			if (this.selectionmode == "checkbox" && !this._checkboxcolumnupdating) {
				if (this._checkboxcolumn) {
					this._checkboxcolumn.checkboxelement.jqxCheckBox({
						checked : false
					})
				}
			}
			this._renderrows(this.virtualsizeinfo);
			this._trigger = true;
			if (this.selectionmode == "checkbox") {
				this._raiseEvent(2, {
					rowindex : this.selectedrowindexes
				})
				//olbius fix unselect all
				this._raiseEvent(3, {
					rowindex : -1
				})
			}
		},
		selectrow : function(b, c) {
			this._applyrowselection(b, true, c);
			if (c !== false) {
				this._updatecheckboxselection()
			}
		},
		_updatecheckboxselection : function() {
			if (this.selectionmode == "checkbox") {
				var d = this.getrows();
				if (d && this._checkboxcolumn) {
					if (d.length === 0) {
						this._checkboxcolumn.checkboxelement.jqxCheckBox({
							checked : false
						});
						return
					}
					var c = d.length;
					if (this.groupable) {
						c = this.dataview.loadedrecords.length
					}
					if (this.virtualmode) {
						c = this.source._source.totalrecords
					}
					var b = this.selectedrowindexes.length;
					if (b === c) {
						this._checkboxcolumn.checkboxelement.jqxCheckBox({
							checked : true
						})
					} else {
						if (b === 0) {
							this._checkboxcolumn.checkboxelement.jqxCheckBox({
								checked : false
							})
						} else {
							this._checkboxcolumn.checkboxelement.jqxCheckBox({
								checked : null
							})
						}
					}
				}
			}
		},
		unselectrow : function(b, c) {
			this._applyrowselection(b, false, c);
			if (c !== false) {
				this._updatecheckboxselection()
			}
		},
		selectcell : function(c, b) {
			this._applycellselection(c, b, true)
		},
		unselectcell : function(c, b) {
			this._applycellselection(c, b, false)
		},
		clearselection : function(c, d) {
			this._trigger = false;
			this.selectedrowindex = -1;
			this._oldselectedcell = null;
			if (d !== false) {
				for (var b = 0; b < this.selectedrowindexes.length; b++) {
					this._raiseEvent(3, {
						rowindex : this.selectedrowindexes[b]
					})
				}
			}
			this.selectedrowindexes = new Array();
			this.selectedcells = new Array();
			if (this.selectionmode == "checkbox" && !this._checkboxcolumnupdating) {
				this._checkboxcolumn.checkboxelement.jqxCheckBox({
					checked : false
				})
			}
			if (false === c) {
				this._trigger = true;
				return
			}
			this._renderrows(this.virtualsizeinfo);
			this._trigger = true;
			if (this.selectionmode == "checkbox") {
				this._raiseEvent(3, {
					rowindex : this.selectedrowindexes
				})
			}
		},
		getselectedrowindex : function() {
			if (this.selectedrowindex == -1) {
				for (var b = 0; b < this.selectedrowindexes.length; b++) {
					return this.selectedrowindexes[b]
				}
			}
			return this.selectedrowindex
		},
		getselectedrowindexes : function() {
			return this.selectedrowindexes
		},
		getselectedcell : function() {
			if (!this.selectedcell) {
				return null
			}
			var b = this.selectedcell;
			b.row = this.selectedcell.rowindex;
			b.column = this.selectedcell.datafield;
			b.value = this.getcellvalue(b.row, b.column);
			return b
		},
		getselectedcells : function() {
			var b = new Array();
			for (obj in this.selectedcells) {
				b[b.length] = this.selectedcells[obj]
			}
			return b
		},
		_getcellsforcopypaste : function() {
			var e = new Array();
			if (this.selectionmode.indexOf("cell") == -1) {
				var h = this.selectedrowindexes;
				for (var d = 0; d < h.length; d++) {
					var c = h[d];
					for (var f = 0; f < this.columns.records.length; f++) {
						var g = c + "_" + this.columns.records[f].datafield;
						var b = {
							rowindex : c,
							datafield : this.columns.records[f].datafield
						};
						e.push(b)
					}
				}
			}
			return e
		},
		deleteselection : function() {
			var d = this;
			var f = d.getselectedcells();
			if (this.selectionmode.indexOf("cell") == -1) {
				f = this._getcellsforcopypaste()
			}
			if (f != null && f.length > 0) {
				for (var e = 0; e < f.length; e++) {
					var b = f[e];
					var g = d.getcolumn(b.datafield);
					var h = d.getcellvalue(b.rowindex, b.datafield);
					if (!g) {
						continue
					}
					if (h !== "") {
						var c = null;
						if (g.columntype == "checkbox") {
							if (!g.threestatecheckbox) {
								c = false
							}
						}
						d._raiseEvent(17, {
							rowindex : b.rowindex,
							datafield : b.datafield,
							value : h
						});
						if (e == f.length - 1) {
							d.setcellvalue(b.rowindex, b.datafield, c, true);
							if (g.displayfield != g.datafield) {
								d.setcellvalue(b.rowindex, g.displayfield, c, true)
							}
						} else {
							d.setcellvalue(b.rowindex, b.datafield, c, false);
							if (g.displayfield != g.datafield) {
								d.setcellvalue(b.rowindex, g.displayfield, c, true)
							}
						}
						d._raiseEvent(18, {
							rowindex : b.rowindex,
							datafield : b.datafield,
							oldvalue : h,
							value : c
						})
					}
				}
				this.dataview.updateview();
				this._renderrows(this.virtualsizeinfo)
			}
		},
		copyselection : function() {
			var f = "";
			var l = this;
			this.clipboardselection = {};
			this._clipboardselection = [];
			var k = l.getselectedcells();
			if (this.selectionmode.indexOf("cell") == -1) {
				k = this._getcellsforcopypaste()
			}
			if (k != null && k.length > 0) {
				var m = 999999999999999;
				var j = -1;
				for (var d = 0; d < k.length; d++) {
					var g = k[d];
					var b = l.getcolumn(g.datafield);
					if (b != null) {
						var h = l.getcelltext(g.rowindex, g.datafield);
						if (!this.clipboardselection[g.rowindex]) {
							this.clipboardselection[g.rowindex] = {}
						}
						this.clipboardselection[g.rowindex][g.datafield] = h;
						m = Math.min(m, g.rowindex);
						j = Math.max(j, g.rowindex)
					}
				}
				for (var c = m; c <= j; c++) {
					var e = 0;
					this._clipboardselection[this._clipboardselection.length] = new Array();
					if (this.clipboardselection[c] != undefined) {
						a.each(this.clipboardselection[c], function(i, n) {
							if (e > 0) {
								f += "\t"
							}
							var o = n;
							if (n == null) {
								o = ""
							}
							l._clipboardselection[l._clipboardselection.length-1][e] = o;
							e++;
							f += o
						})
					}
					if (c < j) {
						f += "\r\n"
					}
				}
			}
			this.clipboardselectedtext = f;
			return f
		},
		pasteselection : function() {
			var o = this.getselectedcells();
			if (this.selectionmode.indexOf("cell") == -1) {
				o = this._getcellsforcopypaste()
			}
			if (o != null && o.length > 0) {
				var n = o[0].rowindex;
				var f = o[0].datafield;
				var k = this._getcolumnindex(f);
				var j = 0;
				this.selectedrowindexes = new Array();
				this.selectedcells = new Array();
				if (!this._clipboardselection) {
					return
				}
				for (var p = 0; p < this._clipboardselection.length; p++) {
					for (var i = 0; i < this._clipboardselection[p].length; i++) {
						var g = this.getcolumnat(k + i);
						if (!g) {
							continue
						}
						var l = this.getcell(n + p, g.datafield);
						var d = null;
						d = this._clipboardselection[p][i];
						if (d != null) {
							if (g.cellsformat) {
								if (g.cellsformat.indexOf("p") != -1 || g.cellsformat.indexOf("c") != -1 || g.cellsformat.indexOf("n") != -1 || g.cellsformat.indexOf("f") != -1) {
									if (d.indexOf(this.gridlocalization.currencysymbol) > -1) {
										d = d.replace(this.gridlocalization.currencysymbol, "")
									}
									var h = function(t, r, s) {
										var c = t;
										if (r == s) {
											return t
										}
										var q = c.indexOf(r);
										while (q != -1) {
											c = c.replace(r, s);
											q = c.indexOf(r)
										}
										return c
									};
									d = h(d, this.gridlocalization.thousandsseparator, "");
									d = d.replace(this.gridlocalization.decimalseparator, ".");
									if (d.indexOf(this.gridlocalization.percentsymbol) > -1) {
										d = d.replace(this.gridlocalization.percentsymbol, "")
									}
									var e = "";
									for (var m = 0; m < d.length; m++) {
										var b = d.substring(m, m + 1);
										if (b === "-") {
											e += "-"
										}
										if (b === ".") {
											e += "."
										}
										if (b.match(/^[0-9]+$/) != null) {
											e += b
										}
									}
									d = e;
									d = d.replace(/ /g, "");
									d = new Number(d);
									if (isNaN(d)) {
										d = ""
									}
								}
							}
							this._raiseEvent(17, {
								rowindex : n + p,
								datafield : l.datafield,
								value : d
							});
							this.setcellvalue(l.row, l.column, d, false);
							this._raiseEvent(18, {
								rowindex : n + p,
								datafield : l.datafield,
								oldvalue : this.getcellvalue(l.rowindex, l.datafield),
								value : d
							});
							this._applycellselection(n + p, l.datafield, true, false)
						}
					}
				}
				this.dataview.updateview();
				this._renderrows(this.virtualsizeinfo)
			}
		},
		_applyrowselection : function(e, i, f, h, b) {
			if (e == null) {
				return false
			}
			var j = this.selectedrowindex;
			if (this.selectionmode == "singlerow") {
				if (i) {
					this._raiseEvent(2, {
						rowindex : e,
						row : this.getrowdata(e)
					})
				} else {
					this._raiseEvent(3, {
						rowindex : e,
						row : this.getrowdata(e)
					})
				}
				this._raiseEvent(3, {
					rowindex : j
				});
				this.selectedrowindexes = new Array();
				this.selectedcells = new Array()
			}
			if (h == true) {
				this.selectedrowindexes = new Array()
			}
			if (this.dataview.filters.length > 0) {
				var c = this.getrowdata(e);
				if (c && c.dataindex !== undefined) {
					e = c.dataindex
				} else {
					if (c && c.dataindex === undefined) {
						if (c.uid != undefined) {
							e = this.getrowboundindexbyid(c.uid)
						}
					}
				}
			}
			var d = this.selectedrowindexes.indexOf(e);
			if (i) {
				this.selectedrowindex = e;
				if (d == -1) {
					this.selectedrowindexes.push(e);
					if (this.selectionmode != "singlerow") {
						this._raiseEvent(2, {
							rowindex : e,
							row : this.getrowdata(e)
						})
					}
				} else {
					if (this.selectionmode == "multiplerows") {
						this.selectedrowindexes.splice(d, 1);
						this._raiseEvent(3, {
							rowindex : this.selectedrowindex,
							row : this.getrowdata(e)
						});
						this.selectedrowindex = this.selectedrowindexes.length > 0 ? this.selectedrowindexes[this.selectedrowindexes.length - 1] : -1
					}
				}
			} else {
				if (d >= 0 || this.selectionmode == "singlerow" || this.selectionmode == "multiplerowsextended" || this.selectionmode == "multiplerowsadvanced") {
					var g = this.selectedrowindexes[d];
					this.selectedrowindexes.splice(d, 1);
					this._raiseEvent(3, {
						rowindex : g,
						row : this.getrowdata(e)
					});
					this.selectedrowindex = -1
				}
			}
			if (f == undefined || f) {
				this._rendervisualrows()
			}
			return true
		},
		_applycellselection : function(e, b, h, f) {
			if (e == null) {
				return false
			}
			if (b == null) {
				return false
			}
			var j = this.selectedrowindex;
			if (this.selectionmode == "singlecell") {
				var d = this.selectedcell;
				if (d != null) {
					this._raiseEvent(16, {
						rowindex : d.rowindex,
						datafield : d.datafield
					})
				}
				this.selectedcells = new Array()
			}
			if (this.selectionmode == "multiplecellsextended" || this.selectionmode == "multiplecellsadvanced") {
				var d = this.selectedcell;
				if (d != null) {
					this._raiseEvent(16, {
						rowindex : d.rowindex,
						datafield : d.datafield
					})
				}
			}
			var g = e + "_" + b;
			if (this.dataview.filters.length > 0) {
				var c = this.getrowdata(e);
				if (c && c.dataindex !== undefined) {
					e = c.dataindex;
					var g = e + "_" + b
				} else {
					if (c && c.dataindex === undefined) {
						if (c.uid) {
							e = this.getrowboundindexbyid(c.uid);
							var g = e + "_" + b
						}
					}
				}
			}
			var i = {
				rowindex : e,
				datafield : b
			};
			if (h) {
				this.selectedcell = i;
				if (!this.selectedcells[g]) {
					this.selectedcells[g] = i;
					this.selectedcells.length++;
					this._raiseEvent(15, i)
				} else {
					if (this.selectionmode == "multiplecells" || this.selectionmode == "multiplecellsextended" || this.selectionmode == "multiplecellsadvanced") {
						delete this.selectedcells[g];
						if (this.selectedcells.length > 0) {
							this.selectedcells.length--
						}
						this._raiseEvent(16, i)
					}
				}
			} else {
				delete this.selectedcells[g];
				if (this.selectedcells.length > 0) {
					this.selectedcells.length--
				}
				this._raiseEvent(16, i)
			}
			if (f == undefined || f) {
				this._rendervisualrows()
			}
			return true
		},
		_getcellindex : function(b) {
			var c = -1;
			a.each(this.selectedcells, function() {
				c++;
				if (this[b]) {
					return false
				}
			});
			return c
		},
		_clearhoverstyle : function() {
			if (undefined == this.hoveredrow || this.hoveredrow == -1) {
				return
			}
			if (this.vScrollInstance.isScrolling()) {
				return
			}
			if (this.hScrollInstance.isScrolling()) {
				return
			}
			var b = this.table.find(".jqx-grid-cell-hover");
			if (b.length > 0) {
				b.removeClass(this.toTP("jqx-grid-cell-hover"));
				b.removeClass(this.toTP("jqx-fill-state-hover"))
			}
			this.hoveredrow = -1
		},
		_clearselectstyle : function() {
			var k = this.table[0].rows.length;
			var p = this.table[0].rows;
			var l = this.toTP("jqx-grid-cell-selected");
			var c = this.toTP("jqx-fill-state-pressed");
			var m = this.toTP("jqx-grid-cell-hover");
			var h = this.toTP("jqx-fill-state-hover");
			for (var g = 0; g < k; g++) {
				var b = p[g];
				var f = b.cells.length;
				var o = b.cells;
				for (var e = 0; e < f; e++) {
					var d = o[e];
					var n = a(d);
					if (d.className.indexOf("jqx-grid-cell-selected") != -1) {
						n.removeClass(l);
						n.removeClass(c)
					}
					if (d.className.indexOf("jqx-grid-cell-hover") != -1) {
						n.removeClass(m);
						n.removeClass(h)
					}
				}
			}
		},
		_selectpath : function(n, e) {
			var l = this;
			var i = this._lastClickedCell ? Math.min(this._lastClickedCell.row, n) : 0;
			var k = this._lastClickedCell ? Math.max(this._lastClickedCell.row, n) : 0;
			if (i <= k) {
				var h = this._getcolumnindex(this._lastClickedCell.column);
				var g = this._getcolumnindex(e);
				var f = Math.min(h, g);
				var d = Math.max(h, g);
				this.selectedcells = new Array();
				var m = this.dataview.loadedrecords;
				for (var b = i; b <= k; b++) {
					for (var j = f; j <= d; j++) {
						var n = m[b];
						this._applycellselection(l.getboundindex(n), l._getcolumnat(j).datafield, true, false)
					}
				}
				this._rendervisualrows()
			}
		},
		_selectrowpath : function(g) {
			if (this.selectionmode == "multiplerowsextended") {
				var c = this;
				var b = this._lastClickedCell ? Math.min(this._lastClickedCell.row, g) : 0;
				var h = this._lastClickedCell ? Math.max(this._lastClickedCell.row, g) : 0;
				var f = this.dataview.loadedrecords;
				if (b <= h) {
					this.selectedrowindexes = new Array();
					for (var e = b; e <= h; e++) {
						var g = f[e];
						var d = this.getrowboundindex(e);
						this._applyrowselection(d, true, false)
					}
					this._rendervisualrows()
				}
			}
		},
		_selectrowwithmouse : function(p, b, c, f, d, s) {
			var j = b.row;
			if (j == undefined) {
				return
			}
			var k = b.index;
			if (this.hittestinfo[k] == undefined) {
				return
			}
			var t = this.hittestinfo[k].visualrow;
			if (this.hittestinfo[k].details) {
				return
			}
			var m = t.cells[0].className;
			if (j.group) {
				return
			}
			if (this.selectionmode == "multiplerows" || this.selectionmode == "multiplecells" || this.selectionmode == "checkbox" || (this.selectionmode.indexOf("multiple") != -1 && (s == true || d == true))) {
				var l = this.getboundindex(j);
				if (this.dataview.filters.length > 0) {
					var v = this.getrowdata(l);
					if (v) {
						l = v.dataindex;
						if (l == undefined) {
							var l = this.getboundindex(j)
						}
					}
				}
				var q = c.indexOf(l) != -1;
				var w = this.getboundindex(j) + "_" + f;
				if (this.selectionmode.indexOf("cell") != -1) {
					var h = this.selectedcells[w] != undefined;
					if (this.selectedcells[w] != undefined && h) {
						this._selectcellwithstyle(p, false, k, f, t)
					} else {
						this._selectcellwithstyle(p, true, k, f, t)
					}
					if (s && this._lastClickedCell == undefined) {
						var g = this.getselectedcells();
						if (g && g.length > 0) {
							this._lastClickedCell = {
								row : g[0].rowindex,
								column : g[0].datafield
							}
						}
					}
					if (s && this._lastClickedCell) {
						this._selectpath(j.visibleindex, f);
						this.mousecaptured = false;
						if (this.selectionarea.css("visibility") == "visible") {
							this.selectionarea.css("visibility", "hidden")
						}
					}
				} else {
					if (q) {
						if (d) {
							this._applyrowselection(this.getboundindex(j), false)
						} else {
							this._selectrowwithstyle(p, t, false, f)
						}
					} else {
						this._selectrowwithstyle(p, t, true, f)
					}
					if (s && this._lastClickedCell == undefined) {
						var i = this.getselectedrowindexes();
						if (i && i.length > 0) {
							this._lastClickedCell = {
								row : i[0],
								column : f
							}
						}
					}
					if (s && this._lastClickedCell) {
						this.selectedrowindexes = new Array();
						var e = this._lastClickedCell ? Math.min(this._lastClickedCell.row, j.visibleindex) : 0;
						var u = this._lastClickedCell ? Math.max(this._lastClickedCell.row, j.visibleindex) : 0;
						var n = this.dataview.loadedrecords;
						for (var o = e; o <= u; o++) {
							var j = n[o];
							if (j) {
								this._applyrowselection(this.getboundindex(j), true, false, false)
							}
						}
						this._rendervisualrows()
					}
				}
			} else {
				this._clearselectstyle();
				this._selectrowwithstyle(p, t, true, f);
				if (this.selectionmode.indexOf("cell") != -1) {
					this._selectcellwithstyle(p, true, k, f, t)
				}
			}
			if (!s) {
				this._lastClickedCell = {
					row : j.visibleindex,
					column : f
				}
			}
		},
		_selectcellwithstyle : function(d, c, g, f, e) {
			var b = a(e.cells[d._getcolumnindex(f)]);
			b.removeClass(this.toTP("jqx-grid-cell-hover"));
			b.removeClass(this.toTP("jqx-fill-state-hover"));
			if (c) {
				b.addClass(this.toTP("jqx-grid-cell-selected"));
				b.addClass(this.toTP("jqx-fill-state-pressed"))
			} else {
				b.removeClass(this.toTP("jqx-grid-cell-selected"));
				b.removeClass(this.toTP("jqx-fill-state-pressed"))
			}
		},
		_selectrowwithstyle : function(e, h, b, j) {
			var c = h.cells.length;
			var f = 0;
			if (e.rowdetails && e.showrowdetailscolumn) {
				if (!this.rtl) {
					f = 1 + this.groups.length
				} else {
					c -= 1;
					c -= this.groups.length
				}
			} else {
				if (this.groupable) {
					if (!this.rtl) {
						f = this.groups.length
					} else {
						c -= this.groups.length
					}
				}
			}
			for (var g = f; g < c; g++) {
				var d = h.cells[g];
				if (b) {
					a(d).removeClass(this.toTP("jqx-grid-cell-hover"));
					a(d).removeClass(this.toTP("jqx-fill-state-hover"));
					if (e.selectionmode.indexOf("cell") == -1) {
						a(d).addClass(this.toTP("jqx-grid-cell-selected"));
						a(d).addClass(this.toTP("jqx-fill-state-pressed"))
					}
				} else {
					a(d).removeClass(this.toTP("jqx-grid-cell-hover"));
					a(d).removeClass(this.toTP("jqx-grid-cell-selected"));
					a(d).removeClass(this.toTP("jqx-fill-state-hover"));
					a(d).removeClass(this.toTP("jqx-fill-state-pressed"))
				}
			}
		},
		_handlemousemoveselection : function(ab, o) {
			if ((o.selectionmode == "multiplerowsextended" || o.selectionmode == "multiplecellsextended" || o.selectionmode == "multiplecellsadvanced") && o.mousecaptured) {
				if (o.multipleselectionbegins) {
					var b = o.multipleselectionbegins(ab);
					if (b === false) {
						return true
					}
				}
				var aa = this.showheader ? this.columnsheader.height() + 2 : 0;
				var I = this._groupsheader() ? this.groupsheader.height() : 0;
				var K = this.showtoolbar ? this.toolbarheight : 0;
				I += K;
				var Z = this.host.coord();
				if (this.hasTransform) {
					Z = a.jqx.utilities.getOffset(this.host);
					var ad = this._getBodyOffset();
					Z.left -= ad.left;
					Z.top -= ad.top
				}
				var M = ab.pageX;
				var L = ab.pageY - I;
				if (Math.abs(this.mousecaptureposition.left - M) > 3 || Math.abs(this.mousecaptureposition.top - L) > 3) {
					var f = parseInt(this.columnsheader.coord().top);
					if (this.hasTransform) {
						f = a.jqx.utilities.getOffset(this.columnsheader).top
					}
					if (M < Z.left) {
						M = Z.left
					}
					if (M > Z.left + this.host.width()) {
						M = Z.left + this.host.width()
					}
					var X = Z.top + aa;
					if (L < X) {
						L = X + 5
					}
					var J = parseInt(Math.min(o.mousecaptureposition.left, M));
					var g = -5 + parseInt(Math.min(o.mousecaptureposition.top, L));
					var H = parseInt(Math.abs(o.mousecaptureposition.left - M));
					var P = parseInt(Math.abs(o.mousecaptureposition.top - L));
					J -= Z.left;
					g -= Z.top;
					this.selectionarea.css("visibility", "visible");
					if (o.selectionmode == "multiplecellsadvanced") {
						var M = J;
						var t = M + H;
						var G = M;
						var n = o.hScrollInstance;
						var v = n.value;
						if (this.rtl) {
							if (this.hScrollBar.css("visibility") != "hidden") {
								v = n.max - n.value
							}
							if (this.vScrollBar[0].style.visibility != "hidden") {
							}
						}
						var h = o.table[0].rows[0];
						var T = 0;
						var B = o.mousecaptureposition.clickedcell;
						var A = B;
						var m = false;
						var r = 0;
						var ac = h.cells.length;
						if (o.mousecaptureposition.left <= ab.pageX) {
							r = B
						}
						for (var W = r; W < ac; W++) {
							var Y = parseInt(a(this.columnsrow[0].cells[W]).css("left"));
							var j = Y - v;
							if (o.columns.records[W].pinned) {
								j = Y;
								continue
							}
							var O = this._getcolumnat(W);
							if (O != null && O.hidden) {
								continue
							}
							if (o.groupable && o.groups.length > 0) {
								if (W < o.groups.length) {
									continue
								}
							}
							var S = j + a(this.columnsrow[0].cells[W]).width();
							if (o.mousecaptureposition.left > ab.pageX) {
								if (S >= M && M >= j) {
									A = W;
									m = true;
									break
								}
							} else {
								if (S >= t && t >= j) {
									A = W;
									m = true;
									break
								}
							}
						}
						if (!m) {
							if (o.mousecaptureposition.left > ab.pageX) {
								a.each(this.columns.records, function(i, k) {
									if (o.groupable && o.groups.length > 0) {
										if (i < o.groups.length) {
											return true
										}
									}
									if (!this.pinned && !this.hidden) {
										A = i;
										return false
									}
								})
							} else {
								if (!o.groupable || (o.groupable && !o.groups.length > 0)) {
									A = h.cells.length - 1
								}
							}
						}
						var N = B;
						B = Math.min(B, A);
						A = Math.max(N, A);
						g += 5;
						g += I;
						var R = o.table[0].rows.indexOf(o.mousecaptureposition.clickedrow);
						var w = 0;
						var e = -1;
						var u = -1;
						var d = 0;
						for (var W = 0; W < o.table[0].rows.length; W++) {
							var s = a(o.table[0].rows[W]);
							if (W == 0) {
								d = s.coord().top
							}
							var F = s.height();
							var z = d - Z.top;
							if (e == -1 && z + F >= g) {
								var c = false;
								for (var Q = 0; Q < o.groups.length; Q++) {
									var V = s[0].cells[Q].className;
									if (V.indexOf("jqx-grid-group-collapse") != -1 || V.indexOf("jqx-grid-group-expand") != -1) {
										c = true;
										break
									}
								}
								if (c) {
									continue
								}
								e = W
							}
							d += F;
							if (o.groupable && o.groups.length > 0) {
								var c = false;
								for (var Q = 0; Q < o.groups.length; Q++) {
									var V = s[0].cells[Q].className;
									if (V.indexOf("jqx-grid-group-collapse") != -1 || V.indexOf("jqx-grid-group-expand") != -1) {
										c = true;
										break
									}
								}
								if (c) {
									continue
								}
								var T = 0;
								for (var U = o.groups.length; U < s[0].cells.length; U++) {
									var E = s[0].cells[U];
									if (a(E).html() == "") {
										T++
									}
								}
								if (T == s[0].cells.length - o.groups.length) {
									continue
								}
							}
							if (e != -1) {
								w += F
							}
							if (z + F > g + P) {
								u = W;
								break
							}
						}
						if (e != -1) {
							g = a(o.table[0].rows[e]).coord().top - Z.top - I - 2;
							var D = 0;
							if (this.filterable && this.showfilterrow) {
								D = this.filterrowheight
							}
							if (parseInt(o.table[0].style.top) < 0 && g < this.rowsheight + D) {
								g -= parseInt(o.table[0].style.top);
								w += parseInt(o.table[0].style.top)
							}
							P = w;
							var l = a(this.columnsrow[0].cells[B]);
							var C = a(this.columnsrow[0].cells[A]);
							J = parseInt(l.css("left"));
							H = parseInt(C.css("left")) - parseInt(J) + C.width() - 2;
							J -= v;
							if (o.editcell && o.editable && o.endcelledit && (B != A || e != u)) {
								if (o.editcell.validated == false) {
									return
								}
								o.endcelledit(o.editcell.row, o.editcell.column, true, true)
							}
						}
					}
					this.selectionarea.width(H);
					this.selectionarea.height(P);
					this.selectionarea.css("left", J);
					this.selectionarea.css("top", g)
				}
			}
		},
		_handlemouseupselection : function(u, o) {
			if (!this.selectionarea) {
				return
			}
			if (this.selectionarea.css("visibility") != "visible") {
				o.mousecaptured = false;
				return true
			}
			if (o.mousecaptured && (o.selectionmode == "multiplerowsextended" || o.selectionmode == "multiplerowsadvanced" || o.selectionmode == "multiplecellsextended" || o.selectionmode == "multiplecellsadvanced")) {
				o.mousecaptured = false;
				if (this.selectionarea.css("visibility") == "visible") {
					this.selectionarea.css("visibility", "hidden");
					var w = this.showheader ? this.columnsheader.height() + 2 : 0;
					var p = this._groupsheader() ? this.groupsheader.height() : 0;
					var B = this.showtoolbar ? this.toolbarheight : 0;
					p += B;
					var C = this.selectionarea.coord();
					var c = this.host.coord();
					if (this.hasTransform) {
						c = a.jqx.utilities.getOffset(this.host);
						C = a.jqx.utilities.getOffset(this.selectionarea)
					}
					var n = C.left - c.left;
					var k = C.top - w - c.top - p;
					var s = k;
					var g = n + this.selectionarea.width();
					var D = n;
					var l = new Array();
					var e = new Array();
					if (o.selectionmode == "multiplerowsextended") {
						while (k < s + this.selectionarea.height()) {
							var b = this._hittestrow(n, k);
							var f = b.row;
							var h = b.index;
							if (h != -1) {
								if (!e[h]) {
									e[h] = true;
									l[l.length] = b
								}
							}
							k += 20
						}
						var s = 0;
						a.each(l, function() {
							var i = this;
							var m = this.row;
							if (o.selectionmode != "none" && o._selectrowwithmouse) {
								if (u.ctrlKey) {
									o._applyrowselection(o.getboundindex(m), true, false, false)
								} else {
									if (s == 0) {
										o._applyrowselection(o.getboundindex(m), true, false, true)
									} else {
										o._applyrowselection(o.getboundindex(m), true, false, false)
									}
								}
								s++
							}
						})
					} else {
						if (o.selectionmode == "multiplecellsadvanced") {
							k += 2
						}
						var r = o.hScrollInstance;
						var t = r.value;
						if (this.rtl) {
							if (this.hScrollBar.css("visibility") != "hidden") {
								t = r.max - r.value
							}
							if (this.vScrollBar[0].style.visibility != "hidden") {
								t -= this.scrollbarsize + 4
							}
						}
						var q = o.table[0].rows[0];
						var j = o.selectionarea.height();
						if (!u.ctrlKey && j > 0) {
							o.selectedcells = new Array()
						}
						var A = j;
						while (k < s + A) {
							var b = o._hittestrow(n, k);
							if (!b) {
								k += 5;
								continue
							}
							var f = b.row;
							var h = b.index;
							if (h != -1) {
								if (!e[h]) {
									e[h] = true;
									for (var v = 0; v < q.cells.length; v++) {
										var d = parseInt(a(o.columnsrow[0].cells[v]).css("left")) - t;
										var z = d + a(o.columnsrow[0].cells[v]).width();
										if ((D >= d && D <= z) || (g >= d && g <= z) || (d >= D && d <= g)) {
											o._applycellselection(o.getboundindex(f), o._getcolumnat(v).datafield, true, false)
										}
									}
								}
							}
							k += 5
						}
					}
					if (o.autosavestate) {
						if (o.savestate) {
							o.savestate()
						}
					}
					o._renderrows(o.virtualsizeinfo)
				}
			}
		},
		selectprevcell : function(e, c) {
			var f = this._getcolumnindex(c);
			var b = this.columns.records.length;
			var d = this._getprevvisiblecolumn(f);
			if (d != null) {
				this.clearselection();
				this.selectcell(e, d.datafield)
			}
		},
		selectnextcell : function(e, d) {
			var f = this._getcolumnindex(d);
			var c = this.columns.records.length;
			var b = this._getnextvisiblecolumn(f);
			if (b != null) {
				this.clearselection();
				this.selectcell(e, b.datafield)
			}
		},
		_getfirstvisiblecolumn : function() {
			var b = this;
			var e = this.columns.records.length;
			for (var c = 0; c < e; c++) {
				var d = this.columns.records[c];
				if (!d.hidden && d.datafield != null) {
					return d
				}
			}
			return null
		},
		_getlastvisiblecolumn : function() {
			var b = this;
			var e = this.columns.records.length;
			for (var c = e - 1; c >= 0; c--) {
				var d = this.columns.records[c];
				if (!d.hidden && d.datafield != null) {
					return d
				}
			}
			return null
		},
		_handlekeydown : function(x, q) {
			if (q.groupable && q.groups.length > 0) {
				return true
			}
			if (q.disabled) {
				return false
			}
			var D = x.charCode ? x.charCode : x.keyCode ? x.keyCode : 0;
			if (q.editcell && q.selectionmode != "multiplecellsadvanced") {
				return true
			} else {
				if (q.editcell && q.selectionmode == "multiplecellsadvanced") {
					if (D >= 33 && D <= 40) {
						if (!x.altKey) {
							if (q._cancelkeydown == undefined || q._cancelkeydown == false) {
								if (q.editmode !== "selectedrow") {
									q.endcelledit(q.editcell.row, q.editcell.column, false, true);
									q._cancelkeydown = false;
									if (q.editcell && !q.editcell.validated) {
										q._rendervisualrows();
										q.endcelledit(q.editcell.row, q.editcell.column, false, true);
										return false
									}
								} else {
									return true
								}
							} else {
								q._cancelkeydown = false;
								return true
							}
						} else {
							q._cancelkeydown = false;
							return true
						}
					} else {
						return true
					}
				}
			}
			if (q.selectionmode == "none") {
				return true
			}
			if (q.showfilterrow && q.filterable) {
				if (this.filterrow) {
					if (a(x.target).ischildof(this.filterrow)) {
						return true
					}
				}
			}
			if (q.pageable) {
				if (a(x.target).ischildof(this.pager)) {
					return true
				}
			}
			if (this.showtoolbar) {
				if (a(x.target).ischildof(this.toolbar)) {
					return true
				}
			}
			if (this.showstatusbar) {
				if (a(x.target).ischildof(this.statusbar)) {
					return true
				}
			}
			var p = false;
			if (x.altKey) {
				return true
			}
			if (x.ctrlKey) {
				if (this.clipboard) {
					var b = String.fromCharCode(D).toLowerCase();
					if (b == "c" || b == "x") {
						var o = this.copyselection();
						if (window.clipboardData) {
							window.clipboardData.setData("Text", o)
						} else {
							var g = a('<textarea style="position: absolute; left: -1000px; top: -1000px;"/>');
							g.val(o);
							a("body").append(g);
							g.select();
							setTimeout(function() {
								document.designMode = "off";
								g.select();
								g.remove();
								q.focus()
							}, 100)
						}
					} else {
						if (b == "v") {
							var C = a('<textarea style="position: absolute; left: -1000px; top: -1000px;"/>');
							a("body").append(C);
							C.select();
							var j = this;
							setTimeout(function() {
								j._clipboardselection = new Array();
								var I = C.val();
								var H = I.split("\n");
								for (var G = 0; G < H.length; G++) {
									if (H[G].split("\t").length > 0) {
										var F = H[G].split("\t");
										if (F.length == 1 && G == H.length - 1 && F[0] == "") {
											continue
										}
										if (F.length > 0) {
											j._clipboardselection.push(F)
										}
									}
								}
								j.pasteselection();
								C.remove();
								j.focus()
							}, 100)
						}
					}
					if (b == "x") {
						this.deleteselection();
						this.host.focus()
					}
				}
			}
			var l = Math.round(q._gettableheight());
			var v = Math.round(l / q.rowsheight);
			var f = q.getdatainformation();
			switch(q.selectionmode) {
			case"singlecell":
			case"multiplecells":
			case"multiplecellsextended":
			case"multiplecellsadvanced":
				var E = q.getselectedcell();
				if (E != null) {
					var e = this.getrowvisibleindex(E.rowindex);
					var i = e;
					var n = E.datafield;
					var t = q._getcolumnindex(n);
					var c = q.columns.records.length;
					var s = function(K, G, J) {
						var F = function(O, L) {
							var N = q.dataview.loadedrecords[O];
							if (N != undefined && L != null) {
								if (J || J == undefined) {
									q.clearselection()
								}
								var M = q.getboundindex(N);
								q.selectcell(M, L);
								q._oldselectedcell = q.selectedcell;
								p = true;
								q.ensurecellvisible(O, L);
								return true
							}
							return false
						};
						if (!F(K, G)) {
							q.ensurecellvisible(K, G);
							F(K, G);
							if (q.virtualmode) {
								q.host.focus()
							}
						}
						if (x.shiftKey && D != 9) {
							if (q.selectionmode == "multiplecellsextended" || q.selectionmode == "multiplecellsadvanced") {
								if (q._lastClickedCell) {
									q._selectpath(K, G);
									var I = q.dataview.loadedrecords[K];
									var H = q.getboundindex(I);
									q.selectedcell = {
										rowindex : H,
										datafield : G
									};
									return
								}
							}
						} else {
							if (!x.shiftKey) {
								q._lastClickedCell = {
									row : K,
									column : G
								}
							}
						}
					};
					var y = x.shiftKey && q.selectionmode != "singlecell" && q.selectionmode != "multiplecells";
					var z = function() {
						s(0, n, !y)
					};
					var h = function() {
						var F = f.rowscount - 1;
						s(F, n, !y)
					};
					var d = D == 9 && !x.shiftKey;
					var k = D == 9 && x.shiftKey;
					if (d || k) {
						y = false
					}
					var m = x.ctrlKey;
					if (m && D == 37) {
						var B = q._getfirstvisiblecolumn(t);
						if (B != null) {
							s(i, B.datafield)
						}
					} else {
						if (m && D == 39) {
							var r = q._getlastvisiblecolumn(t);
							if (r != null) {
								s(i, r.datafield)
							}
						} else {
							if (D == 39 || d) {
								var u = q._getnextvisiblecolumn(t);
								if (u != null) {
									s(i, u.datafield, !y)
								} else {
									if (!d) {
										p = true
									}
								}
							} else {
								if (D == 37 || k) {
									var B = q._getprevvisiblecolumn(t);
									if (B != null) {
										s(i, B.datafield, !y)
									} else {
										if (!k) {
											p = true
										}
									}
								} else {
									if (D == 36) {
										z()
									} else {
										if (D == 35) {
											h()
										} else {
											if (D == 33) {
												if (i - v >= 0) {
													var A = i - v;
													s(A, n, !y)
												} else {
													z()
												}
											} else {
												if (D == 34) {
													if (f.rowscount > i + v) {
														var A = i + v;
														s(A, n, !y)
													} else {
														h()
													}
												} else {
													if (D == 38) {
														if (m) {
															z()
														} else {
															if (i > 0) {
																s(i - 1, n, !y)
															} else {
																p = true
															}
														}
													} else {
														if (D == 40) {
															if (m) {
																h()
															} else {
																if (f.rowscount > i + 1) {
																	s(i + 1, n, !y)
																} else {
																	p = true
																}
															}
														}
													}
												}
											}
										}
									}
								}
							}
						}
					}
				}
				break;
			case"singlerow":
			case"multiplerows":
			case"multiplerowsextended":
			case"multiplerowsadvanced":
				var i = q.getselectedrowindex();
				if (i == null || i == -1) {
					return true
				}
				i = this.getrowvisibleindex(i);
				var w = function(G, H) {
					var F = function(K) {
						var M = q.dataview.loadedrecords[K];
						if (M != undefined) {
							var L = q.getboundindex(M);
							var J = q.selectedrowindex;
							if (H || H == undefined) {
								q.clearselection()
							}
							q.selectedrowindex = J;
							q.selectrow(L, false);
							var I = q.ensurerowvisible(K);
							if (!I || q.autoheight || q.groupable) {
								q._rendervisualrows()
							}
							p = true;
							return true
						}
						return false
					};
					if (!F(G)) {
						q.ensurerowvisible(G);
						F(G, H);
						if (q.virtualmode) {
							setTimeout(function() {
								F(G, H)
							}, 25)
						}
						if (q.virtualmode) {
							q.host.focus()
						}
					}
					if (x.shiftKey && D != 9) {
						if (q.selectionmode == "multiplerowsextended") {
							if (q._lastClickedCell) {
								q._selectrowpath(G);
								q.selectedrowindex = q.getrowboundindex(G);
								return
							}
						}
					} else {
						if (!x.shiftKey) {
							q._lastClickedCell = {
								row : G
							};
							q.selectedrowindex = q.getrowboundindex(G)
						}
					}
				};
				var y = x.shiftKey && q.selectionmode != "singlerow" && q.selectionmode != "multiplerows";
				var z = function() {
					w(0, !y)
				};
				var h = function() {
					var F = f.rowscount - 1;
					w(F, !y)
				};
				var m = x.ctrlKey;
				if (D == 36 || (m && D == 38)) {
					z()
				} else {
					if (D == 35 || (m && D == 40)) {
						h()
					} else {
						if (D == 33) {
							if (i - v >= 0) {
								var A = i - v;
								w(A, !y)
							} else {
								z()
							}
						} else {
							if (D == 34) {
								if (f.rowscount > i + v) {
									var A = i + v;
									w(A, !y)
								} else {
									h()
								}
							} else {
								if (D == 38) {
									if (i > 0) {
										w(i - 1, !y)
									} else {
										p = true
									}
								} else {
									if (D == 40) {
										if (f.rowscount > i + 1) {
											w(i + 1, !y)
										} else {
											p = true
										}
									}
								}
							}
						}
					}
				}
				break
			}
			if (p) {
				if (q.autosavestate) {
					if (q.savestate) {
						q.savestate()
					}
				}
				return false
			}
			return true
		},
		_handlemousemove : function(u, p) {
			if (p.vScrollInstance.isScrolling()) {
				return
			}
			if (p.hScrollInstance.isScrolling()) {
				return
			}
			var w;
			var q;
			var f;
			var n;
			var m;
			if (p.enablehover || p.selectionmode == "multiplerows") {
				w = this.showheader ? this.columnsheader.height() + 2 : 0;
				q = this._groupsheader() ? this.groupsheader.height() : 0;
				var A = this.showtoolbar ? this.toolbarheight : 0;
				q += A;
				f = this.host.coord();
				if (this.hasTransform) {
					f = a.jqx.utilities.getOffset(this.host);
					var k = this._getBodyOffset();
					f.left -= k.left;
					f.top -= k.top
				}
				n = u.pageX - f.left;
				m = u.pageY - w - f.top - q
			}
			if (p.selectionmode == "multiplerowsextended" || p.selectionmode == "multiplecellsextended" || p.selectionmode == "multiplecellsadvanced") {
				if (p.mousecaptured == true) {
					return
				}
			}
			if (p.enablehover) {
				if (p.disabled) {
					return
				}
				if (this.vScrollInstance.isScrolling() || this.hScrollInstance.isScrolling()) {
					return
				}
				var c = this._hittestrow(n, m);
				if (!c) {
					return
				}
				var h = c.row;
				var j = c.index;
				if (this.hoveredrow != -1 && j != -1 && this.hoveredrow == j && this.selectionmode.indexOf("cell") == -1 && this.selectionmode != "checkbox") {
					return
				}
				this._clearhoverstyle();
				if (j == -1 || h == undefined) {
					return
				}
				var r = this.hittestinfo[j].visualrow;
				if (r == null) {
					return
				}
				if (this.hittestinfo[j].details) {
					return
				}
				if (u.clientX > a(r).width() + a(r).coord().left) {
					return
				}
				var B = 0;
				var o = r.cells.length;
				if (p.rowdetails && p.showrowdetailscolumn) {
					if (!this.rtl) {
						B = 1 + this.groups.length
					} else {
						o -= 1;
						o -= this.groups.length
					}
				} else {
					if (this.groupable) {
						if (!this.rtl) {
							B = this.groups.length
						} else {
							o -= this.groups.length
						}
					}
				}
				if (r.cells.length == 0) {
					return
				}
				var l = r.cells[B].className;
				if (h.group || (this.selectionmode.indexOf("row") >= 0 && l.indexOf("jqx-grid-cell-selected") != -1)) {
					return
				}
				this.hoveredrow = j;
				if (this.selectionmode.indexOf("cell") != -1 || this.selectionmode == "checkbox") {
					var e = -1;
					var s = this.hScrollInstance;
					var t = s.value;
					if (this.rtl) {
						if (this.hScrollBar.css("visibility") != "hidden") {
							t = s.max - s.value
						}
					}
					for (var v = B; v < o; v++) {
						var g = parseInt(a(this.columnsrow[0].cells[v]).css("left")) - t;
						var z = g + a(this.columnsrow[0].cells[v]).width();
						if (z >= n && n >= g) {
							e = v;
							break
						}
					}
					if (e != -1) {
						var b = r.cells[e];
						if (b.className.indexOf("jqx-grid-cell-selected") == -1) {
							if (this.editcell) {
								var d = this._getcolumnat(e);
								if (d) {
									if (this.editcell.row == j && this.editcell.column == d.datafield) {
										return
									}
								}
							}
							a(b).addClass(this.toTP("jqx-grid-cell-hover"));
							a(b).addClass(this.toTP("jqx-fill-state-hover"));
							if (this.cellhover) {
								this.cellhover(b, u.pageX, u.pageY)
							}
						}
					}
					return
				}
				for (var v = B; v < o; v++) {
					var b = r.cells[v];
					a(b).addClass(this.toTP("jqx-grid-cell-hover"));
					a(b).addClass(this.toTP("jqx-fill-state-hover"));
					if (this.cellhover) {
						this.cellhover(b, u.pageX, u.pageY)
					}
				}
			} else {
				return true
			}
		}
	})
})(jqxBaseFramework);