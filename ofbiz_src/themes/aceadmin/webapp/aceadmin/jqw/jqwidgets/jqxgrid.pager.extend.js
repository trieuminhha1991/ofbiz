/*
jQWidgets v4.0.0 (2016-Jan)
Copyright (c) 2011-2016 jQWidgets.
License: http://jqwidgets.com/license/
*/

(function(a) {
    a.extend(a.jqx._jqxGrid.prototype, {
        gotopage: function(d) {
            if (d == null || d == undefined) {
                d = 0
            }
            if (d == -1) {
                d = 0
            }
            if (d < 0) {
                return
            }
            var c = this.dataview.totalrecords;
            if (this.summaryrows) {
                c += this.summaryrows.length
            }
            var e = this.pagenum;
            this._raiseEvent(25, {
                oldpagenum: this.dataview.pagenum,
                pagenum: d,
                pagesize: this.dataview.pagesize
            });
            var b = Math.ceil(c / this.pagesize);
            if (d >= b) {
                if (this.dataview.totalrecords == 0) {
                    this.dataview.pagenum = 0;
                    this.updatepagerdetails()
                }
                if (d > 0) {
                    d = b - 1
                }
                if (d < 0) {
                    d = 0
                }
            }
            if (this.dataview.pagenum != d || this._requiresupdate) {
                if (this.pageable) {
                    if (this.source.pager) {
                    	this.source.pager(d, this.dataview.pagesize, this.dataview.pagenum)
                    }
                    if (this.source._source.pager) {
                        this.source._source.pager(d, this.dataview.pagesize, this.dataview.pagenum)
                    }
                    this.dataview.pagenum = d;
                    if (this.virtualmode) {
                        this.hiddens = new Array();
                        this.expandedgroups = new Array();
                        if (this.rendergridrows) {
                            var h = d * this.dataview.pagesize;
                            var g = h + this.dataview.pagesize;
                            if (h != null && g != null) {
                                if (this.pagerrightbutton) {
                                    this.pagerrightbutton.jqxButton({
                                        disabled: true
                                    });
                                    this.pagerleftbutton.jqxButton({
                                        disabled: true
                                    });
                                    this.pagershowrowscombo.jqxDropDownList({
                                        disabled: true
                                    })
                                }
                                if (this.pagerfirstbutton) {
                                    this.pagerfirstbutton.jqxButton({
                                        disabled: true
                                    });
                                    this.pagerlastbutton.jqxButton({
                                        disabled: true
                                    })
                                }
                                this.updatebounddata("pagechanged");
                                this._raiseEvent(9, {
                                    pagenum: d,
                                    oldpagenum: e,
                                    pagesize: this.dataview.pagesize
                                });
                                this.updatepagerdetails();
                                if (this.autosavestate) {
                                    if (this.savestate) {
                                        this.savestate()
                                    }
                                }
                                return
                            }
                        }
                    } else {
                        this.dataview.updateview()
                    }
                    this._loadrows();
                    this._updatepageviews();
                    this.tableheight = null;
                    this._updatecolumnwidths();
                    this._updatecellwidths();
                    this._renderrows(this.virtualsizeinfo);
                    this.updatepagerdetails();
                    if (this.autoheight || this.autorowheight) {
                        var f = this.host.height() - this._gettableheight();
                        height = f + this._pageviews[0].height;
                        if (height != this.host.height()) {
                            this._arrange();
                            this._updatepageviews();
                            if (this.autorowheight) {
                                this._renderrows(this.virtualsizeinfo)
                            }
                        }
                    }
                    if (this.editcell != null && this.endcelledit) {
                        this.endcelledit(this.editcell.row, this.editcell.column, false, false)
                    }
                    this.focus();
                    this._raiseEvent(9, {
                        pagenum: d,
                        oldpagenum: e,
                        pagesize: this.dataview.pagesize
                    });
                    if (this.autosavestate) {
                        if (this.savestate) {
                            this.savestate()
                        }
                    }
                }
            }
        },
    })
})(jqxBaseFramework);
