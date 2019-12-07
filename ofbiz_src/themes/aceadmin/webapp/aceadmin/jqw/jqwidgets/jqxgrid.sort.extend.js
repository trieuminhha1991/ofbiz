/*
 jQWidgets v3.5.0 (2014-Sep-15)
 Copyright (c) 2011-2014 jQWidgets.
 License: http://jqwidgets.com/license/
 */

(function(a) {
	a.extend(a.jqx._jqxGrid.prototype, {
		_rendersortcolumn: function() {
            var b = this.that;
            var d = this.getsortcolumn();
            if (this.sortdirection) {
                var c = function(f, g) {
                    var e = b.getcolumn(f);
                    if (e) {
                        if (g.ascending) {
                            a.jqx.aria(e.element, "aria-sort", "ascending")
                        } else {
                            if (g.descending) {
                                a.jqx.aria(e.element, "aria-sort", "descending")
                            } else {
                                a.jqx.aria(e.element, "aria-sort", "none")
                            }
                        }
                    }
                }
                ;
                if (this._oldsortinfo) {
                    if (this._oldsortinfo.column) {
                        c(this._oldsortinfo.column, {
                            ascending: false,
                            descending: false
                        })
                    }
                }
                c(d, this.sortdirection)
            }
            this._oldsortinfo = {
                column: d,
                direction: this.sortdirection
            };
            if (this.sortdirection) {
            	if (this.columns.records) {
            		a.each(this.columns.records, function(f, g) {
                        var e = a.data(document.body, "groupsortelements" + this.displayfield);
                        if (d == null || this.displayfield != d) {
                            a(this.sortasc).hide();
                            a(this.sortdesc).hide();
                            if (e != null ) {
                                e.sortasc.hide();
                                e.sortdesc.hide()
                            }
                        } else {
                            if (b.sortdirection.ascending) {
                                a(this.sortasc).show();
                                a(this.sortdesc).hide();
                                if (e != null ) {
                                    e.sortasc.show();
                                    e.sortdesc.hide()
                                }
                            } else {
                                a(this.sortasc).hide();
                                a(this.sortdesc).show();
                                if (e != null ) {
                                    e.sortasc.hide();
                                    e.sortdesc.show()
                                }
                            }
                        }
                    })
            	}
            }
        },
	});
})(jqxBaseFramework);