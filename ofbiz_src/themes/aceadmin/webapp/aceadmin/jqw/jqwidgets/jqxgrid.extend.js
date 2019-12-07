/*
 jQWidgets v4.0.0 (2016-Jan)
 Copyright (c) 2011-2016 jQWidgets.
 License: http://jqwidgets.com/license/
 */

(function(b) {
	b.extend(b.jqx._jqxGrid.prototype, {
		createInstance : function(i) {
			this.that = this;
			var h = this;
			h.pagesize = parseInt(h.pagesize);
			h.toolbarheight = parseInt(h.toolbarheight);
			h.columnsheight = parseInt(h.columnsheight);
			h.filterrowheight = parseInt(h.filterrowheight);
			h.statusbarheight = parseInt(h.statusbarheight);
			h.groupsheaderheight = parseInt(h.groupsheaderheight);
			h.detailsVisibility = new Array();
			h.savedArgs = i && i.length > 0 ? i[0] : null;
			//olbius add jqx-grid-content class
			var g = "<div class='jqx-clear jqx-border-reset jqx-overflow-hidden jqx-max-size jqx-position-relative'><div tabindex='1' class='jqx-clear jqx-max-size jqx-position-relative jqx-overflow-hidden jqx-background-reset' id='wrapper" + h.element.id + "'><div class='jqx-clear jqx-position-absolute' id='toolbar' style='visibility: hidden;'></div><div class='jqx-clear jqx-position-absolute' id='groupsheader' style='visibility: hidden;'></div><div class='jqx-clear jqx-overflow-hidden jqx-position-absolute jqx-border-reset jqx-background-reset jqx-grid-content' id='content" + h.element.id + "'></div><div class='jqx-clear jqx-position-absolute' id='verticalScrollBar" + h.element.id + "'></div><div class='jqx-clear jqx-position-absolute' id='horizontalScrollBar" + h.element.id + "'></div><div class='jqx-clear jqx-position-absolute jqx-border-reset' id='bottomRight'></div><div class='jqx-clear jqx-position-absolute' id='addrow'></div><div class='jqx-clear jqx-position-absolute' id='statusbar'></div><div class='jqx-clear jqx-position-absolute' id='pager' style='z-index: 20;'></div></div></div>";
			h.element.innerHTML = "";
			if (b.jqx.utilities.scrollBarSize != 15) {
				h.scrollbarsize = b.jqx.utilities.scrollBarSize
			}
			if (h.source) {
				if (!h.source.dataBind) {
					if (!b.jqx.dataAdapter) {
						throw new Error("jqxGrid: Missing reference to jqxdata.js")
					}
					h.source = new b.jqx.dataAdapter(h.source)
				}
				var d = h.source._source.datafields;
				if (d && d.length > 0) {
					h.editmode = h.editmode.toLowerCase();
					h.selectionmode = h.selectionmode.toLowerCase()
				}
			}
			h.host.attr("role", "grid");
			h.host.attr("align", "left");
			h.element.innerHTML = g;
			h.host.addClass(h.toTP("jqx-grid"));
			//olbius add class if no pageable toolbar
			if(!h.pageable)
				h.host.addClass(h.toTP("jqx-grid-nopageable"));
			h.host.addClass(h.toTP("jqx-reset"));
			h.host.addClass(h.toTP("jqx-rc-all"));
			h.host.addClass(h.toTP("jqx-widget"));
			h.host.addClass(h.toTP("jqx-widget-content"));
			h.wrapper = h.host.find("#wrapper" + h.element.id);
			h.content = h.host.find("#content" + h.element.id);
			h.content.addClass(h.toTP("jqx-reset"));
			var k = h.host.find("#verticalScrollBar" + h.element.id);
			var o = h.host.find("#horizontalScrollBar" + h.element.id);
			h.bottomRight = h.host.find("#bottomRight").addClass(h.toTP("jqx-grid-bottomright")).addClass(h.toTP("jqx-scrollbar-state-normal"));
			if (!k.jqxScrollBar) {
				throw new Error("jqxGrid: Missing reference to jqxscrollbar.js");
				return
			}
			h.editors = new Array();
			h.vScrollBar = k.jqxScrollBar({
				vertical : true,
				rtl : h.rtl,
				touchMode : h.touchmode,
				step : h.verticalscrollbarstep,
				largestep : h.verticalscrollbarlargestep,
				theme : h.theme,
				_triggervaluechanged : false
			});
			h.hScrollBar = o.jqxScrollBar({
				vertical : false,
				rtl : h.rtl,
				touchMode : h.touchmode,
				step : h.horizontalscrollbarstep,
				largestep : h.horizontalscrollbarlargestep,
				theme : h.theme,
				_triggervaluechanged : false
			});
			h.addnewrow = h.host.find("#addrow");
			h.addnewrow[0].id = "addrow" + h.element.id;
			h.addnewrow.addClass(h.toTP("jqx-widget-header"));
			h.pager = h.host.find("#pager");
			h.pager[0].id = "pager" + h.element.id;
			h.toolbar = h.host.find("#toolbar");
			h.toolbar[0].id = "toolbar" + h.element.id;
			h.toolbar.addClass(h.toTP("jqx-grid-toolbar"));
			h.toolbar.addClass(h.toTP("jqx-widget-header"));
			h.statusbar = h.host.find("#statusbar");
			h.statusbar[0].id = "statusbar" + h.element.id;
			h.statusbar.addClass(h.toTP("jqx-grid-statusbar"));
			h.statusbar.addClass(h.toTP("jqx-widget-header"));
			h.pager.addClass(h.toTP("jqx-grid-pager"));
			h.pager.addClass(h.toTP("jqx-widget-header"));
			h.groupsheader = h.host.find("#groupsheader");
			h.groupsheader.addClass(h.toTP("jqx-grid-groups-header"));
			h.groupsheader.addClass(h.toTP("jqx-widget-header"));
			h.groupsheader[0].id = "groupsheader" + h.element.id;
			h.vScrollBar.css("visibility", "hidden");
			h.hScrollBar.css("visibility", "hidden");
			h.vScrollInstance = b.data(h.vScrollBar[0], "jqxScrollBar").instance;
			h.hScrollInstance = b.data(h.hScrollBar[0], "jqxScrollBar").instance;
			h.gridtable = null;
			h.isNestedGrid = h.host.parent() ? h.host.parent().css("z-index") == 9999 : false;
			h.touchdevice = h.isTouchDevice();
			if (h.localizestrings) {
				h.localizestrings();
				if (h.localization != null) {
					h.localizestrings(h.localization, false)
				}
			}
			if (h.rowdetailstemplate) {
				if (undefined == h.rowdetailstemplate.rowdetails) {
					h.rowdetailstemplate.rowdetails = "<div></div>"
				}
				if (undefined == h.rowdetailstemplate.rowdetailsheight) {
					h.rowdetailstemplate.rowdetailsheight = 200
				}
				if (undefined == h.rowdetailstemplate.rowdetailshidden) {
					h.rowdetailstemplate.rowdetailshidden = true
				}
			}
			if (h.showfilterrow && !h.filterable) {
				throw new Error('jqxGrid: "showfilterrow" requires setting the "filterable" property to true!');
				h.host.remove();
				return
			}
			if (h.autorowheight && !h.autoheight && !h.pageable) {
				throw new Error('jqxGrid: "autorowheight" requires setting the "autoheight" or "pageable" property to true!');
				h.host.remove();
				return
			}
			if (h.virtualmode && h.rendergridrows == null) {
				throw new Error('jqxGrid: "virtualmode" requires setting the "rendergridrows"!');
				h.host.remove();
				return
			}
			if (h.virtualmode && !h.pageable && h.groupable) {
				throw new Error('jqxGrid: "grouping" in "virtualmode" without paging is not supported!');
				h.host.remove();
				return
			}
			if (h._testmodules()) {
				return
			}
			h._builddataloadelement();
			h._cachedcolumns = h.columns;
			if (h.columns && h.columns.length > 299) {
				h.headerZIndex = h.columns.length + 100
			}
			if (h.rowsheight != 25) {
				h._measureElement("cell")
			}
			if (h.columnsheight != 25 || h.columngroups) {
				h._measureElement("column")
			}
			if (h.source) {
				var d = h.source.datafields;
				if (d == null && h.source._source) {
					d = h.source._source.datafields
				}
				if (d) {
					for (var e = 0; e < h.columns.length; e++) {
						var f = h.columns[e];
						if (f && f.cellsformat && f.cellsformat.length > 2) {
							for (var n = 0; n < d.length; n++) {
								if (d[n].name == f.datafield && !d[n].format) {
									d[n].format = f.cellsformat;
									break
								}
							}
						}
					}
				}
			}
			h.databind(h.source);
			if (h.showtoolbar) {
				h.toolbar.css("visibility", "inherit")
			}
			if (h.showstatusbar) {
				h.statusbar.css("visibility", "inherit")
			}
			h._arrange();
			if (h.pageable && h._initpager) {
				h._initpager()
			}
			h.tableheight = null;
			var j = h.that;
			var l = function() {
				if (j.content) {
					j.content[0].scrollTop = 0;
					j.content[0].scrollLeft = 0
				}
				if (j.gridcontent) {
					j.gridcontent[0].scrollLeft = 0;
					j.gridcontent[0].scrollTop = 0
				}
			};
			h.addHandler(h.content, "mousedown", function() {
				l()
			});
			h.addHandler(h.content, "scroll", function(m) {
				l();
				return false
			});
			if (!h.showfilterrow) {
				if (!h.showstatusbar && !h.showtoolbar) {
					h.host.addClass("jqx-disableselect")
				}
				h.content.addClass("jqx-disableselect")
			}
			if (h.enablebrowserselection) {
				h.content.removeClass("jqx-disableselect");
				h.host.removeClass("jqx-disableselect")
			}
			h._resizeWindow();
			if (h.disabled) {
				h.host.addClass(h.toThemeProperty("jqx-fill-state-disabled"))
			}
			h.hasTransform = b.jqx.utilities.hasTransform(h.host);
			if (h.scrollmode == "logical") {
				h.vScrollInstance.thumbStep = h.rowsheight;
				h.vScrollInstance.step = h.rowsheight
			}
			if (!b.jqx.isHidden(h.host)) {
				if (h.filterable || h.groupable || h.sortable) {
					h._initmenu()
				}
			}
		},
		_testmodules : function() {
			var k = "";
			var h = this.that;
			var d = function() {
				if (k.length != "") {
					k += ","
				}
			};
			if (this.columnsmenu && !this.host.jqxMenu && (this.sortable || this.groupable || this.filterable)) {
				d();
				k += " jqxmenu.js"
			}
			if (!this.host.jqxScrollBar) {
				d();
				k += " jqxscrollbar.js"
			}
			if (!this.host.jqxButton) {
				d();
				k += " jqxbuttons.js"
			}
			if (!b.jqx.dataAdapter) {
				d();
				k += " jqxdata.js"
			}
			if (this.pageable && !this.gotopage) {
				d();
				k += "jqxgrid.pager.js"
			}
			if (this.filterable && !this.applyfilters) {
				d();
				k += " jqxgrid.filter.js"
			}
			if (this.groupable && !this._initgroupsheader) {
				d();
				k += " jqxgrid.grouping.js"
			}
			if (this.columnsresize && !this.autoresizecolumns) {
				d();
				k += " jqxgrid.columnsresize.js"
			}
			if (this.columnsreorder && !this.setcolumnindex) {
				d();
				k += " jqxgrid.columnsreorder.js"
			}
			if (this.sortable && !this.sortby) {
				d();
				k += " jqxgrid.sort.js"
			}
			if (this.editable && !this.begincelledit) {
				d();
				k += " jqxgrid.edit.js"
			}
			if (this.showaggregates && !this.getcolumnaggregateddata) {
				d();
				k += " jqxgrid.aggregates.js"
			}
			if (this.keyboardnavigation && !this.selectrow) {
				d();
				k += " jqxgrid.selection.js"
			}
			if (k != "" || this.editable || this.filterable || this.pageable) {
				var f = [];
				var j = function(i) {
					switch(i) {
					case"checkbox":
						if (!h.host.jqxCheckBox && !f.checkbox) {
							f.checkbox = true;
							d();
							k += " jqxcheckbox.js"
						}
						break;
					case"numberinput":
						if (!h.host.jqxNumberInput && !f.numberinput) {
							f.numberinput = true;
							d();
							k += " jqxnumberinput.js"
						}
						break;
					case"datetimeinput":
						if (!h.host.jqxDateTimeInput && !f.datetimeinput) {
							d();
							f.datetimeinput = true;
							k += " jqxdatetimeinput.js(requires: jqxcalendar.js)"
						} else {
							if (!h.host.jqxCalendar && !f.calendar) {
								d();
								k += " jqxcalendar.js"
							}
						}
						break;
					case"combobox":
						if (!h.host.jqxComboBox && !f.combobox) {
							d();
							f.combobox = true;
							k += " jqxcombobox.js(requires: jqxlistbox.js)"
						} else {
							if (!h.host.jqxListBox && !f.listbox) {
								d();
								f.listbox = true;
								k += " jqxlistbox.js"
							}
						}
						break;
					case"dropdownlist":
						if (!h.host.jqxDropDownList && !f.dropdownlist) {
							d();
							f.dropdownlist = true;
							k += " jqxdropdownlist.js(requires: jqxlistbox.js)"
						} else {
							if (!h.host.jqxListBox && !f.listbox) {
								d();
								f.listbox = true;
								k += " jqxlistbox.js"
							}
						}
						break
					case 'olbiusdropgrid':
						if (!h.host.jqxDropDownButton && !f.olbiusdropgrid) {
							d();
							f.olbiusdropgrid = true;
							k += ' jqxdropdownbutton.js'
						} else {
							if (!h.host.jqxListBox && !f.listbox) {
								d();
								f.listbox = true;
								k += ' jqxlistbox.js'
							}
						}
						break
					}

				};
				if (this.filterable || this.pageable) {
					j("dropdownlist")
				}
				for (var e = 0; e < this.columns.length; e++) {
					if (this.columns[e] == undefined) {
						continue
					}
					var g = this.columns[e].columntype;
					j(g);
					if (this.filterable && this.showfilterrow) {
						var g = this.columns[e].filtertype;
						if (g == "checkedlist" || g == "bool") {
							j("checkbox")
						}
						if (g == "date") {
							j("datetimeinput")
						}
						if (g == 'olbiusdropgrid') {
							j('olbiusdropgrid');
						}
					}
				}
				if (k != "") {
					throw new Error("jqxGrid: Missing references to the following module(s): " + k);
					this.host.remove();
					return true
				}
			}
			return false
		},
		_renderemptyrow : function() {
			if (this._loading) {
				return
			}
			if (this.dataview.records.length == 0 && this.showemptyrow) {
				var l = false;
				var e = this.toTP("jqx-grid-cell");
				if (this.table && this.table.length > 0 && this.table[0].rows && this.table[0].rows.length > 0) {
					var k = this.table[0].rows[0];
					this.table[0].style.top = "0px";
					for (var f = 0; f < k.cells.length; f++) {
						var d = b(k.cells[f]);
						if (d.css("display") != "none" && !l) {
							d[0].checkbox = null;
							d[0].button = null;
							d[0].className = e;
							l = true;
							d[0].innerHTML = "";
							var g = b("<span style='white-space: nowrap; float: left; margin-left: 50%; position: relative;'></span>");
							g.text(this.gridlocalization.emptydatastring);
							d.append(g);
							var j = 0;
							if (!this.oldhscroll) {
								j = parseInt(this.table[0].style.marginLeft);
								if (this.rtl) {
									d.css("z-index", 999);
									d.css("overflow", "visible")
								}
							}
							// g.css("left", -j - (g.width() / 2));
							g.css("left", '-30px');
							g.css("top", this._gettableheight() / 2 - g.height() / 2);
							if (b.jqx.browser.msie && b.jqx.browser.version < 8) {
								g.css("margin-left", "0px");
								g.css("left", this.host.width() / 2 - g.width() / 2)
							}
							var h = Math.abs(parseInt(this.table[0].style.top));
							if (isNaN(h)) {
								h = 0
							}
							b(k).height(this._gettableheight() + h);
							//olbius - cuongbt fix empty row width;
							var relp = $(d.parents('.jqx-widget-content')[0]);
							var w = relp.width();
							g.width(w);
							d.width(w);
							if (this.table.width() < this.host.width()) {
								this.table.width(this.host.width())
							}
						}
						d.addClass(this.toThemeProperty("jqx-grid-empty-cell"))
					}
				}
			}
		},
		_postrender : function(f) {
			var self = this;
			var showed = [];
			var hided = [];
			if (f == "filter" || f == "sort" || f == "group") {
				if (this.rowdetails && this.detailsVisibility && this.detailsVisibility.length > 0) {
					this._detailsUpdate = true;
					for (var e = 0; e < this.detailsVisibility.length; e++) {
						if (false === this.detailsVisibility[e]) {
							showed.push(e);
						} else {
							if (true === this.detailsVisibility[e]) {
								hided.push(e);
							}
						}
					}
					var react = function(){
						var delay = 100;
						if(isNaN(self.waited)){
							self.waited = 0;
						}else{
							self.waited += delay;
						}
						if(self.waited > 2000){
							return;
						}
						var time = setTimeout(function(){
							if(!self._loading){
								for(var e in showed){
									self.showrowdetails(showed[e]);
								}
								for(var e in hided){
									self.hiderowdetails(hided[e]);
								}
								self.waited = 0;
							}else{
								react();
							}
							clearTimeout(time);
						}, delay);
					};
					if(showed.length || hided.length){
						try{
							react();
						}catch(e){
							console.log(e);
						}
					}
					this._detailsUpdate = false;
					this.rendergridcontent(true);
					this._updatecolumnwidths();
					this._updatecellwidths();
					this._renderrows(this.virtualsizeinfo)
				}
			}
			if (this.groupable && this.groups.length > 0) {
				if (f == "filter" || f == "sort") {
					for (var e = 0; e < this.dataview.loadedgroups.length; e++) {
						var h = this.dataview.loadedgroups[e];
						var d = 0;
						for (var g in this.groupsVisibility) {
							if (g == h.group && this.groupsVisibility[g]) {
								suspendgroupevents = true;
								this._setgroupstate(h, true, false);
								d++
							}
						}
						if (d > 0) {
							suspendgroupevents = false;
							var j = this.vScrollBar[0].style.visibility;
							this.rendergridcontent(true, false);
							if (j != this.vScrollBar[0].style.visibility || this._hiddencolumns) {
								this._updatecolumnwidths();
								this._updatecellwidths();
								this._renderrows(this.virtualsizeinfo)
							}
						}
					}
				} else {
					if (f == "group") {
						this.groupsVisibility = new Array()
					}
				}
			}
		},
	});
	b.jqx.dataview = function() {
        this.self = this;
        this.grid = null;
        this.uniqueId = "id";
        this.records = [];
        this.rows = [];
        this.columns = [];
        this.groups = [];
        this.filters = new Array();
        this.updated = null;
        this.update = null;
        this.suspend = false;
        this.pagesize = 0;
        this.pagenum = 0;
        this.totalrows = 0;
        this.totalrecords = 0;
        this.groupable = true;
        this.loadedrecords = [];
        this.loadedrootgroups = [];
        this.loadedgroups = [];
        this.loadedgroupsByKey = [];
        this.virtualmode = true;
        this._cachegrouppages = new Array();
        this.source = null;
        this.changedrecords = new Array();
        this.rowschangecallback = null;
        this.that = this;
        this.destroy = function() {
            delete this.self;
            delete this.grid;
            delete this.uniqueId;
            delete this.records;
            delete this.rows;
            delete this.columns;
            delete this.groups;
            delete this.filters;
            delete this.updated;
            delete this.update;
            delete this.suspend;
            delete this.pagesize;
            delete this.pagenum;
            delete this.totalrows;
            delete this.totalrecords;
            delete this.groupable;
            delete this.loadedrecords;
            delete this.loadedrootgroups;
            delete this.loadedgroups;
            delete this.loadedgroupsByKey;
            delete this.virtualmode;
            delete this._cachegrouppages;
            delete this.source;
            delete this.changedrecords;
            delete this.rowschangecallback;
            delete this.that
        }
        ,
        this.suspendupdate = function() {
            this.suspend = true
        }
        ,
        this.isupdating = function() {
            return this.suspend
        }
        ,
        this.resumeupdate = function(d) {
            this.suspend = false;
            if (d == undefined) {
                d = true
            }
            this.refresh(d)
        }
        ,
        this.getrecords = function() {
            return this.records
        }
        ,
        this.clearrecords = function() {
            this.recordids = new Array()
        }
        ;
        this.databind = function(q, l) {
            var p = q._source ? true : false;
            var g = null;
            if (p) {
                g = q;
                q = q._source
            } else {
                g = new b.jqx.dataAdapter(q,{
                    autoBind: false
                })
            }
            var e = function(m) {
                g.recordids = [];
                g.records = new Array();
                g.cachedrecords = new Array();
                g.originaldata = new Array();
                g._options.virtualmode = m.virtualmode;
                g._options.totalrecords = m.totalrecords;
                g._options.originaldata = m.originaldata;
                g._options.recordids = m.recordids;
                g._options.cachedrecords = new Array();
                g._options.pagenum = m.pagenum;
                g._options.pageable = m.pageable;
                if (q.type != undefined) {
                    g._options.type = q.type
                }
                if (q.formatdata != undefined) {
                    g._options.formatData = q.formatdata
                }
                if (q.contenttype != undefined) {
                    g._options.contentType = q.contenttype
                }
                if (q.async != undefined) {
                    g._options.async = q.async
                }
                if (q.updaterow != undefined) {
                    g._options.updaterow = q.updaterow
                }
                if (q.addrow != undefined) {
                    g._options.addrow = q.addrow
                }
                if (q.deleterow != undefined) {
                    g._options.deleterow = q.deleterow
                }
                if (m.pagesize == 0) {
                    m.pagesize = 10
                }
                g._options.pagesize = m.pagesize
            };
            var t = function(C) {
                C.totalrecords = g.totalrecords;
                if (!C.virtualmode) {
                    C.originaldata = g.originaldata;
                    C.records = g.records;
                    C.recordids = g.recordids;
                    C.cachedrecords = g.cachedrecords
                } else {
                    var x = {
                        startindex: C.pagenum * C.pagesize,
                        endindex: (C.pagenum * C.pagesize + C.pagesize)
                    };
                    if (q.recordstartindex != undefined) {
                        x.startindex = parseInt(q.recordstartindex)
                    }
                    if (q.recordendindex != undefined) {
                        x.endindex = parseInt(q.recordendindex)
                    } else {
                        if (!C.grid.pageable) {
                            x.endindex = x.startindex + 100;
                            if (C.grid.autoheight) {
                                x.endindex = x.startindex + C.totalrecords
                            }
                        }
                    }
                    if (!q.recordendindex) {
                        if (!C.grid.pageable) {
                            x.endindex = x.startindex + 100;
                            if (C.grid.autoheight) {
                                x.endindex = x.startindex + C.totalrecords
                            }
                        } else {
                            x = {
                                startindex: C.pagenum * C.pagesize,
                                endindex: (C.pagenum * C.pagesize + C.pagesize)
                            }
                        }
                    }
                    x.data = g.records;
                    if (C.grid.rendergridrows && C.totalrecords > 0) {
                        var E = 0;
                        q.records = C.grid.rendergridrows(x);
                        if (q.records.length) {
                            E = q.records.length
                        }
                        if (q.records && !q.records[x.startindex]) {
                            var m = new Array();
                            var D = x.startindex;
                            b.each(q.records, function() {
                                m[D] = this;
                                D++;
                                E++
                            });
                            q.records = m
                        }
                        if (E == 0) {
                            if (q.records) {
                                b.each(q.records, function() {
                                    E++
                                })
                            }
                        }
                        if (E > 0 && E < x.endindex - x.startindex && !C.grid.groupable) {
                            var A = q.records[0];
                            for (var z = 0; z < x.endindex - x.startindex - E; z++) {
                                var B = {};
                                for (obj in A) {
                                    B[obj] = ""
                                }
                                if (q.records.push) {
                                    q.records.push(B)
                                }
                            }
                        }
                    }
                    if (!q.records || C.totalrecords == 0) {
                        q.records = new Array()
                    }
                    C.originaldata = q.records;
                    C.records = q.records;
                    C.cachedrecords = q.records
                }
            };
            e(this);
            this.source = q;
            if (l !== undefined) {
                uniqueId = l
            }
            var y = this.that;
            switch (q.datatype) {
            case "local":
            case "array":
            default:
                if (q.localdata == null) {
                    q.localdata = []
                }
                if (q.localdata != null) {
                    g.unbindBindingUpdate(y.grid.element.id);
                    if ((!y.grid.autobind && y.grid.isInitialized) || y.grid.autobind) {
                        g.dataBind()
                    }
                    var k = function(x) {
                        if (x != undefined && x != "") {
                            var z = g._changedrecords[0];
                            if (z) {
                                var A = new Array();
                                b.each(g._changedrecords, function(E) {
                                    var B = this.index;
                                    var C = this.record;
                                    y.grid._updateFromAdapter = true;
                                    switch (x) {
                                    case "update":
                                        var D = y.grid.getrowid(B);
                                        if (E == g._changedrecords.length - 1) {
                                            y.grid.updaterow(D, C)
                                        } else {
                                            y.grid.updaterow(D, C, false)
                                        }
                                        y.grid._updateFromAdapter = false;
                                        return;
                                    case "add":
                                        y.grid.addrow(null, C);
                                        y.grid._updateFromAdapter = false;
                                        return;
                                    case "remove":
                                        var D = y.grid.getrowid(B);
                                        A.push(D);
                                        return
                                    }
                                });
                                if (A.length > 0) {
                                    y.grid.deleterow(A, false);
                                    y.grid._updateFromAdapter = false
                                }
                            }
                            if (x == "update") {
                                return
                            }
                        }
                        var m = y.totalrecords;
                        t(y, x);
                        if (q.localdata.notifier === null && q.localdata.name == "observableArray") {
                            q.localdata.notifier = function(F) {
                                if (this._updating) {
                                    return
                                }
                                this._updating = true;
                                var E = y.grid.getrowid(F.index);
                                switch (F.type) {
                                case "add":
                                    var B = b.extend({}, F.object[F.index]);
                                    var D = g.getid(q.id, B, F.index);
                                    if (F.index === 0) {
                                        y.grid.addrow(D, B, "first")
                                    } else {
                                        y.grid.addrow(D, B)
                                    }
                                    break;
                                case "delete":
                                    y.grid.deleterow(E);
                                    break;
                                case "update":
                                    if (F.path && F.path.split(".").length > 1) {
                                        var C = F.path.split(".");
                                        y.grid.setcellvalue(F.index, C[C.length - 1], F.newValue)
                                    } else {
                                        var B = b.extend({}, F.object[F.index]);
                                        y.grid.updaterow(E, B)
                                    }
                                    break
                                }
                                this._updating = false
                            }
                        }
                        if (x == "updateData") {
                            y.refresh();
                            y.grid._updateGridData()
                        } else {
                            if (q.recordstartindex && this.virtualmode) {
                                y.updateview(q.recordstartindex, q.recordstartindex + y.pagesize)
                            } else {
                                y.refresh()
                            }
                            y.update(m != y.totalrecords)
                        }
                    };
                    k();
                    g.bindBindingUpdate(y.grid.element.id, k)
                }
                break;
            case "json":
            case "jsonp":
            case "xml":
            case "xhtml":
            case "script":
            case "text":
            case "csv":
            case "tab":
                if (q.localdata != null) {
                    g.unbindBindingUpdate(y.grid.element.id);
                    if ((!y.grid.autobind && y.grid.isInitialized) || y.grid.autobind) {
                        g.dataBind()
                    }
                    var k = function(x) {
                        var m = y.totalrecords;
                        t(y);
                        if (x == "updateData") {
                            y.refresh();
                            y.grid._updateGridData()
                        } else {
                            if (q.recordstartindex && y.virtualmode) {
                                y.updateview(q.recordstartindex, q.recordstartindex + y.pagesize)
                            } else {
                                y.refresh()
                            }
                            y.update(m != y.totalrecords)
                        }
                    };
                    k();
                    g.bindBindingUpdate(y.grid.element.id, k);
                    return
                }
                var u = {};
                var o = 0;
                var v = {};
                for (var i = 0; i < this.filters.length; i++) {
                    var f = this.filters[i].datafield;
                    var j = this.filters[i].filter;
                    if (!j.getfilters) {
                        continue
                    }
                    var h = j.getfilters();
                    v[f + "operator"] = j.operator;
                    for (var s = 0; s < h.length; s++) {
                        h[s].datafield = f;
                        var n = h[s].value;
                        if (h[s].type == "datefilter") {
                            if (h[s].value && h[s].value.toLocaleString) {
                                var d = this.grid.getcolumn(h[s].datafield);
                                if (d && d.cellsformat) {
                                    var r = this.grid.source.formatDate(h[s].value, d.cellsformat, this.grid.gridlocalization);
                                    if (r) {
                                        v["filtervalue" + o] = r
                                    } else {
                                        v["filtervalue" + o] = h[s].value.toLocaleString()
                                    }
                                } else {
                                    v["filtervalue" + o] = n.toString()
                                }
                            } else {
                                v["filtervalue" + o] = n.toString()
                            }
                        } else {
                            v["filtervalue" + o] = n.toString();
                            if (h[s].data) {
                                v["filterid" + o] = h[s].data.toString()
                            }
                            if (h[s].id) {
                                v["filterid" + o] = h[s].id.toString()
                            }
                        }
                        v["filtercondition" + o] = h[s].condition;
                        v["filteroperator" + o] = h[s].operator;
                        v["filterdatafield" + o] = f;
                        o++
                    }
                }
                v.filterscount = o;
                v.groupscount = y.groups.length;
                for (var i = 0; i < y.groups.length; i++) {
                    v["group" + i] = y.groups[i]
                }
                if (q.recordstartindex == undefined) {
                    q.recordstartindex = 0
                }
                if (q.recordendindex == undefined || q.recordendindex == 0) {
                    if (y.grid.height && y.grid.height.toString().indexOf("%") == -1) {
                        q.recordendindex = parseInt(y.grid.height) / y.grid.rowsheight;
                        q.recordendindex += 2;
                        q.recordendindex = parseInt(q.recordendindex)
                    } else {
                        q.recordendindex = b(window).height() / y.grid.rowsheight;
                        q.recordendindex = parseInt(q.recordendindex)
                    }
                    if (this.pageable) {
                        q.recordendindex = this.pagesize
                    }
                }
                if (this.pageable) {
                    q.recordstartindex = (this.pagenum) * this.pagesize;
                    q.recordendindex = (this.pagenum + 1) * this.pagesize
                }
                b.extend(v, {
                    sortdatafield: y.sortfield,
                    sortorder: y.sortfielddirection,
                    pagenum: y.pagenum,
                    pagesize: y.grid.pagesize,
                    recordstartindex: q.recordstartindex,
                    recordendindex: q.recordendindex
                });
                var w = g._options.data;
                if (g._options.data) {
                    b.extend(g._options.data, v)
                } else {
                    if (q.data) {
                        b.extend(v, q.data)
                    }
                    g._options.data = v
                }
                var k = function() {
                    var x = b.jqx.browser.msie && b.jqx.browser.version < 9;
                    var z = function() {
                        var A = y.totalrecords;
                        t(y);
                        if (q.recordstartindex && y.virtualmode) {
                            y.updateview(q.recordstartindex, q.recordstartindex + y.pagesize)
                        } else {
                            y.refresh()
                        }
                        y.update(A != y.totalrecords)
                    };
                    if (x) {
                        try {
                            z()
                        } catch (m) {}
                    } else {
                        z()
                    }
                };
                g.unbindDownloadComplete(y.grid.element.id);
                g.bindDownloadComplete(y.grid.element.id, k);
                if ((!y.grid.autobind && y.grid.isInitialized) || y.grid.autobind) {
                    g.dataBind()
                } else {
                    if (!y.grid.isInitialized && !y.grid.autobind) {
                        k()
                    }
                }
                g._options.data = w
            }
        }
        ;
        this.getid = function(g, e, f) {
            if (b(g, e).length > 0) {
                return b(g, e).text()
            }
            if (g) {
                if (g.toString().length > 0) {
                    var d = b(e).attr(g);
                    if (d != null && d.toString().length > 0) {
                        return d
                    }
                }
            }
            return f
        }
        ;
        this.getvaluebytype = function(g, d) {
            var e = g;
            if (d.type == "date") {
                var f = new Date(g);
                if (f.toString() == "NaN" || f.toString() == "Invalid Date") {
                    if (b.jqx.dataFormat) {
                        g = b.jqx.dataFormat.tryparsedate(g)
                    } else {
                        g = f
                    }
                } else {
                    g = f
                }
                if (g == null) {
                    g = e
                }
            } else {
                if (d.type == "float") {
                    var g = parseFloat(g);
                    if (isNaN(g)) {
                        g = e
                    }
                } else {
                    if (d.type == "int") {
                        var g = parseInt(g);
                        if (isNaN(g)) {
                            g = e
                        }
                    } else {
                        if (d.type == "bool") {
                            if (g != null) {
                                if (g.toLowerCase() == "false") {
                                    g = false
                                } else {
                                    if (g.toLowerCase() == "true") {
                                        g = true
                                    }
                                }
                            }
                            if (g == 1) {
                                g = true
                            } else {
                                if (g == 0) {
                                    g = false
                                } else {
                                    g = ""
                                }
                            }
                        }
                    }
                }
            }
            return g
        }
        ;
        this.setpaging = function(d) {
            if (d.pageSize != undefined) {
                this.pagesize = d.pageSize
            }
            if (d.pageNum != undefined) {
                this.pagenum = Math.min(d.pageNum, Math.ceil(this.totalrows / this.pagesize))
            }
            this.refresh()
        }
        ;
        this.getpagingdetails = function() {
            return {
                pageSize: this.pagesize,
                pageNum: this.pagenum,
                totalrows: this.totalrows
            }
        }
        ;
        this._clearcaches = function() {
            this.sortcache = {};
            this.sortdata = null;
            this.changedrecords = new Array();
            this.records = new Array();
            this.rows = new Array();
            this.cacheddata = new Array();
            this.originaldata = new Array();
            this.bounditems = new Array();
            this.loadedrecords = new Array();
            this.loadedrootgroups = new Array();
            this.loadedgroups = new Array();
            this.loadedgroupsByKey = new Array();
            this._cachegrouppages = new Array();
            this.recordsbyid = new Array();
            this.cachedrecords = new Array();
            this.recordids = new Array()
        }
        ;
        this.addfilter = function(g, f) {
            var e = -1;
            for (var d = 0; d < this.filters.length; d++) {
                if (this.filters[d].datafield == g) {
                    e = d;
                    break
                }
            }
            if (e == -1) {
                this.filters[this.filters.length] = {
                    filter: f,
                    datafield: g
                }
            } else {
                this.filters[e] = {
                    filter: f,
                    datafield: g
                }
            }
        }
        ;
        this.removefilter = function(e) {
            for (var d = 0; d < this.filters.length; d++) {
                if (this.filters[d].datafield == e) {
                    this.filters.splice(d, 1);
                    break
                }
            }
        }
        ;
        this.getItemFromIndex = function(d) {
            return this.records[d]
        }
        ;
        this.updaterow = function(d, n, l) {
            var e = this.filters && this.filters.length > 0 && !this.virtualmode;
            if (!e && n != undefined && d != undefined) {
                n.uid = d;
                if (!(n[this.source.id])) {
                    n[this.source.id] = n.uid
                }
                var j = this.recordsbyid["id" + d];
                var k = this.records.indexOf(j);
                if (k == -1) {
                    return false
                }
                this.records[k] = n;
                if (this.cachedrecords) {
                    this.cachedrecords[k] = n
                }
                if (l == true || l == undefined) {
                    this.refresh()
                }
                this.changedrecords[n.uid] = {
                    Type: "Update",
                    OldData: j,
                    Data: n
                };
                return true
            } else {
                if (this.filters && this.filters.length > 0) {
                    var f = this.cachedrecords;
                    var j = null;
                    var k = -1;
                    for (var h = 0; h < f.length; h++) {
                        if (f[h].uid == d) {
                            j = f[h];
                            k = h;
                            break
                        }
                    }
                    if (j) {
                        var m = this.that;
                        for (var g in n) {
                            m.cachedrecords[k][g] = n[g]
                        }
                        if (l == true || l == undefined) {
                            this.refresh()
                        }
                        return true
                    }
                }
            }
            return false
        }
        ;
        this.addrow = function(h, i, d, g) {
            if (i != undefined) {
                if (b.isEmptyObject(i)) {
                    if (this.source && this.source.datafields) {
                        b.each(this.source.datafields, function() {
                            var j = "";
                            if (this.type == "number") {
                                j = null
                            }
                            if (this.type == "date") {
                                j = null
                            }
                            if (this.type == "bool" || this.type == "boolean") {
                                j = false
                            }
                            i[this.name] = j
                        })
                    }
                }
                if (!h || this.recordsbyid["id" + h]) {
                    i.uid = this.getid(this.source.id, i, this.totalrecords);
                    var e = this.recordsbyid["id" + i.uid];
                    while (e != null) {
                        var f = Math.floor(Math.random() * 10000).toString();
                        i.uid = f;
                        e = this.recordsbyid["id" + f]
                    }
                } else {
                    i.uid = h
                }
                if (!(i[this.source.id])) {
                    if (this.source.id != undefined) {
                        i[this.source.id] = i.uid
                    }
                }
                if (d == "last") {
                    this.records.push(i)
                } else {
                    if (typeof d === "number" && isFinite(d)) {
                        this.records.splice(d, 0, i)
                    } else {
                        this.records.splice(0, 0, i)
                    }
                }
                if (this.filters && this.filters.length > 0) {
                    if (d == "last") {
                        this.cachedrecords.push(i)
                    } else {
                        if (typeof d === "number" && isFinite(d)) {
                            this.cachedrecords.splice(d, 0, i)
                        } else {
                            this.cachedrecords.splice(0, 0, i)
                        }
                    }
                }
                this.totalrecords++;
                if (this.virtualmode) {
                    this.source.totalrecords = this.totalrecords
                }
                if (g == true || g == undefined) {
                    this.refresh()
                }
                this.changedrecords[i.uid] = {
                    Type: "New",
                    Data: i
                };
                return true
            }
            return false
        }
        ;
        this.deleterow = function(j, h) {
            if (j != undefined) {
                var d = this.filters && this.filters.length > 0;
                if (this.recordsbyid["id" + j] && !d) {
                    var e = this.recordsbyid["id" + j];
                    var k = this.records.indexOf(e);
                    this.changedrecords[j] = {
                        Type: "Delete",
                        Data: this.records[k]
                    };
                    this.records.splice(k, 1);
                    this.totalrecords--;
                    if (this.virtualmode) {
                        this.source.totalrecords = this.totalrecords
                    }
                    if (h == true || h == undefined) {
                        this.refresh()
                    }
                    return true
                } else {
                    if (this.filters && this.filters.length > 0) {
                        var f = this.cachedrecords;
                        var e = null;
                        var k = -1;
                        for (var g = 0; g < f.length; g++) {
                            if (f[g].uid == j) {
                                e = f[g];
                                k = g;
                                break
                            }
                        }
                        if (e) {
                            this.cachedrecords.splice(k, 1);
                            if (h == true || h == undefined) {
                                this.totalrecords = 0;
                                this.records = this.cachedrecords;
                                this.refresh()
                            }
                            return true
                        }
                    }
                }
                return false
            }
            return false
        }
        ;
        this.reload = function(f, d, r, g, h, u, t) {
            var m = this.that;
            var l = new Array();
            var o = f;
            var i = d;
            var j = r;
            var p = g;
            var k = i.length;
            var w = 0;
            var e = 0;
            var s, n;
            this.columns = [];
            this.bounditems = new Array();
            this.loadedrecords = new Array();
            this.loadedrootgroups = new Array();
            this.loadedgroups = new Array();
            this.loadedgroupsByKey = new Array();
            this._cachegrouppages = new Array();
            this.recordsbyid = {};
            if (this.totalrecords == 0) {
                Object.size = function(z) {
                    var y = 0, x;
                    for (x in z) {
                        if (z.hasOwnProperty(x)) {
                            y++
                        }
                    }
                    return y
                }
                ;
                var v = Object.size(o);
                this.totalrecords = v;
                b.each(this.records, function(y) {
                    var z = this;
                    var x = 0;
                    b.each(z, function(A, B) {
                        m.columns[x++] = A
                    });
                    return false
                })
            }
            if (this.virtualmode) {
                if (this.pageable) {
                    this.updateview();
                    return
                }
                var u = 0;
                if (!this.groupable) {
                    this.updateview();
                    return
                } else {
                    var t = this.totalrecords
                }
            } else {
                var u = 0;
                var t = this.totalrecords
            }
            if (this.groupable && this.groups.length > 0 && this.loadgrouprecords) {
                var q = u;
                q = this.loadgrouprecords(0, u, t, j, e, p, i, k, l)
            } else {
                w = this.loadflatrecords(u, t, j, e, p, i, k, l)
            }
            if (k > e) {
                i.splice(e, k - e)
            }
            if (this.groups.length > 0 && this.groupable) {
                this.totalrows = q
            } else {
                this.totalrows = w
            }
            return l
        }
        ;
        this.loadflatrecords = function(d, o, e, p, l, u, n, q) {
            var t = this.that;
            var k = d;
            var m = d;
            o = Math.min(o, this.totalrecords);
            var g = this.sortdata != null;
            var f = this.source.id && (this.source.datatype == "local" || this.source.datatype == "array" || this.source.datatype == "");
            var j = g ? this.sortdata : this.records;
            for (var h = d; h < o; h++) {
                var s = {};
                if (!g) {
                    s = b.extend({}, j[h]);
                    id = s[t.uniqueId];
                    s.boundindex = k;
                    t.loadedrecords[k] = s;
                    if (s.uid == undefined) {
                        s.uid = t.getid(t.source.id, s, k)
                    }
                    t.recordsbyid["id" + s.uid] = j[h];
                    s.uniqueid = t.generatekey();
                    t.bounditems[this.bounditems.length] = s
                } else if (j[h]){
                    s = b.extend({}, j[h].value);
                    id = s[t.uniqueId];
                    s.boundindex = j[h].index;
                    if (s.uid == undefined) {
                        s.uid = t.getid(t.source.id, s, s.boundindex)
                    }
                    t.recordsbyid["id" + s.uid] = j[h].value;
                    t.loadedrecords[k] = s;
                    s.uniqueid = t.generatekey();
                    t.bounditems[s.boundindex] = s
                }
                if (p >= n || id != u[p][t.uniqueId] || (l && l[id])) {
                    q[q.length] = p
                }
                u[p] = s;
                p++;
                s.visibleindex = m;
                m++;
                k++
            }
            if (t.grid.summaryrows) {
                var r = k;
                b.each(t.grid.summaryrows, function() {
                    var i = b.extend({}, this);
                    i.boundindex = o++;
                    t.loadedrecords[r] = i;
                    i.uniqueid = t.generatekey();
                    t.bounditems[t.bounditems.length] = i;
                    u[p] = i;
                    p++;
                    i.visibleindex = m;
                    m++;
                    r++
                })
            }
            return m
        }
        ,
        this.updateview = function(o, p) {
            var r = this.that;
            var k = this.pagesize * this.pagenum;
            var n = 0;
            var s = new Array();
            var e = this.filters;
            var j = this.updated;
            var l = s.length;
            if (this.pageable) {
                if (this.virtualmode) {
                    if (!this.groupable || this.groups.length == 0) {
                        this.loadflatrecords(this.pagesize * this.pagenum, this.pagesize * (1 + this.pagenum), e, n, j, s, l, []);
                        this.totalrows = s.length
                    } else {
                        if (this.groupable && this.groups.length > 0 && this.loadgrouprecords) {
                            if (this._cachegrouppages[this.pagenum + "_" + this.pagesize] != undefined) {
                                this.rows = this._cachegrouppages[this.pagenum + "_" + this.pagesize];
                                this.totalrows = this.rows.length;
                                return
                            }
                            var m = this.pagesize * (1 + this.pagenum);
                            if (m > this.totalrecords) {
                                m = this.totalrecords
                            }
                            this.loadgrouprecords(0, this.pagesize * this.pagenum, m, e, n, j, s, l, []);
                            this._cachegrouppages[this.pagenum + "_" + this.pagesize] = this.rows;
                            this.totalrows = this.rows.length;
                            return
                        }
                    }
                }
            } else {
                if (this.virtualmode && (!this.groupable || this.groups.length == 0)) {
                    var g = this.pagesize;
                    if (g == 0) {
                        g = Math.min(100, this.totalrecords)
                    }
                    var d = g * this.pagenum;
                    if (this.loadedrecords.length == 0) {
                        d = 0
                    }
                    if (o != null && p != null) {
                        this.loadflatrecords(o, p, e, n, j, s, l, [])
                    } else {
                        this.loadflatrecords(this.pagesize * this.pagenum, this.pagesize * (1 + this.pagenum), e, n, j, s, l, [])
                    }
                    this.totalrows = this.loadedrecords.length;
                    this.rows = s;
                    if (s.length >= g) {
                        return
                    }
                }
            }
            if (this.groupable && this.pageable && this.groups.length > 0 && this._updategroupsinpage) {
                s = this._updategroupsinpage(r, e, k, n, l, this.pagesize * this.pagenum, this.pagesize * (1 + this.pagenum))
            } else {
                for (var h = this.pagesize * this.pagenum; h < this.pagesize * (1 + this.pagenum); h++) {
                    var q = h < this.loadedrecords.length ? this.loadedrecords[h] : null;
                    if (q == null) {
                        continue
                    }
                    if (!this.pagesize || (k >= this.pagesize * this.pagenum && k <= this.pagesize * (this.pagenum + 1))) {
                        s[n] = q;
                        n++
                    }
                    k++
                }
            }
            if ((s.length == 0 || s.length < this.pagesize) && !this.pageable && this.virtualmode) {
                n = s.length;
                var f = s.length;
                for (var h = this.pagesize * this.pagenum; h < this.pagesize * (1 + this.pagenum) - f; h++) {
                    var q = {};
                    q.boundindex = h + f;
                    q.visibleindex = h + f;
                    q.uniqueid = r.generatekey();
                    q.empty = true;
                    r.bounditems[h + f] = q;
                    s[n] = q;
                    n++
                }
            }
            this.rows = s
        }
        ;
        this.generatekey = function() {
            var d = function() {
                return ( ((1 + Math.random()) * 16) | 0)
            };
            return ( "" + d() + d() + "-" + d() + "-" + d() + "-" + d() + "-" + d() + d() + d())
        }
        ;
        this.reloaddata = function() {
            this.reload(this.records, this.rows, this.filter, this.updated, true)
        }
        ;
        this.refresh = function(h) {
            if (this.suspend) {
                return
            }
            if (h == undefined) {
                h = true
            }
            var l = this.rows.length;
            var k = this.totalrows;
            if (this.filters.length > 0 && !this.virtualmode) {
                var e = "";
                var g = this.cachedrecords.length;
                var s = new Array();
                this.totalrecords = 0;
                var n = this.cachedrecords;
                this._dataIndexToBoundIndex = new Array();
                var f = this.filters.length;
                if (this.source != null && this.source.filter != undefined && this.source.localdata != undefined) {
                    s = this.source.filter(this.filters, n, g);
                    if (s == undefined) {
                        s = new Array()
                    }
                    this.records = s
                } else {
                    if (this.source.filter == null || this.source.filter == undefined) {
                        for (var u = 0; u < g; u++) {
                            var o = n[u];
                            var d = undefined;
                            for (var m = 0; m < f; m++) {
                                var e = this.filters[m].filter;
                                var r = o[this.filters[m].datafield];
                                var t = e.evaluate(r);
                                if (d == undefined) {
                                    d = t
                                } else {
                                    if (e.operator == "or") {
                                        d = d || t
                                    } else {
                                        d = d && t
                                    }
                                }
                            }
                            if (d) {
                                s[s.length] = b.extend({
                                    dataindex: u
                                }, o);
                                this._dataIndexToBoundIndex[u] = {
                                    boundindex: s.length - 1
                                }
                            } else {
                                this._dataIndexToBoundIndex[u] = null
                            }
                        }
                        this.records = s
                    }
                }
                if (this.sortdata) {
                    var i = this.sortfield;
                    if (this.sortcache[i]) {
                        this.sortdata = null;
                        var p = this.sortcache[i].direction;
                        this.sortcache[i] = null;
                        this.sortby(this.sortfield, p);
                        return
                    }
                }
            } else {
                if (this.filters.length == 0 && !this.virtualmode) {
                    if (this.cachedrecords) {
                        this.totalrecords = 0;
                        var n = this.cachedrecords;
                        this.records = n;
                        if (this.sortdata) {
                            var i = this.sortfield;
                            if (this.sortcache[i]) {
                                this.sortdata = null;
                                var p = this.sortcache[i].direction;
                                this.sortcache[i] = null;
                                this.sortby(this.sortfield, p);
                                return
                            }
                        }
                    }
                }
            }
            var q = this.reload(this.records, this.rows, this.filter, this.updated, h);
            this.updated = null;
            if (this.rowschangecallback != null) {
                if (k != totalrows) {
                    this.rowschangecallback({
                        type: "PagingChanged",
                        data: getpagingdetails()
                    })
                }
                if (l != rows.length) {
                    this.rowschangecallback({
                        type: "RowsCountChanged",
                        data: {
                            previous: l,
                            current: rows.length
                        }
                    })
                }
                if (q.length > 0 || l != rows.length) {
                    this.rowschangecallback({
                        type: "RowsChanged",
                        data: {
                            previous: l,
                            current: rows.length,
                            diff: q
                        }
                    })
                }
            }
        }
        ;
        return this
    }
})(jqxBaseFramework);