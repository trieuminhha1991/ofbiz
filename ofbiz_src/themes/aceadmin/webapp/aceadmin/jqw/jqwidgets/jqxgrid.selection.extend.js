/*
 jQWidgets v3.5.0 (2014-Sep-15)
 Copyright (c) 2011-2014 jQWidgets.
 License: http://jqwidgets.com/license/
 */

(function(a) {
	a.extend(a.jqx._jqxGrid.prototype, {
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
					rowindex : -9999
				})
			}
			var $griD = $('#' + this.element.id);$griD.trigger('checkedAll',[false]);
		},
		selectallrows:function(){
			//olbius custom trigger when select All rows
			var $griD = $('#' + this.element.id);$griD.trigger('checkedAll',[true]);this._trigger=false;var d=this.virtualmode?this.dataview.totalrecords:this.dataview.loadedrecords.length;this.selectedrowindexes=new Array();var e=this.dataview.loadedrecords;for(var c=0;c<d;c++){var f=e[c];if(!f){this.selectedrowindexes[c]=c;continue}var b=this.getboundindex(f);if(b!=undefined){this.selectedrowindexes[c]=b}}if(this.selectionmode=="checkbox"&&!this._checkboxcolumnupdating){if(this._checkboxcolumn){this._checkboxcolumn.checkboxelement.jqxCheckBox({checked:true})}}this._renderrows(this.virtualsizeinfo);this._trigger=true;if(this.selectionmode=="checkbox"){this._raiseEvent(2,{rowindex:this.selectedrowindexes})}}
		,selectrowwithcache : function(i,c){
			if(c!==false){
				this._updatecheckboxselection()
			}
		},
		clearselection: function(c, d) {
            this._trigger = false;
            this.selectedrowindex = -1;
            this._oldselectedcell = null;
            if (d !== false) {
                for (var b = 0; b < this.selectedrowindexes.length; b++) {
                    this._raiseEvent(3, {
                        rowindex: this.selectedrowindexes[b]
                    })
                }
            }
            this.selectedrowindexes = new Array();
            this.selectedcells = new Array();
            this.selectedcell = null;
            if (this.selectionmode == "checkbox" && !this._checkboxcolumnupdating &&  this._checkboxcolumn) {
                this._checkboxcolumn.checkboxelement.jqxCheckBox({
                    checked: false
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
                    rowindex: this.selectedrowindexes
                })
            }
        },
	});
})(jqxBaseFramework);