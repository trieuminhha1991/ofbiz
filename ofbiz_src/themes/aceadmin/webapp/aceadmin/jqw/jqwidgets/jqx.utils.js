if(typeof(Grid) == "undefined"){
/*init object cache*/
	if (!dataGridCache) var dataGridCache = {};
	var getDataIsInit = function(gridId) {
		var keyName = gridId + "IsInit";
		var isInit = dataGridCache[keyName];
		if (typeof isInit == 'undefined' || isInit == null) isInit = true;
		return isInit;
	}
	var setDataIsInit = function(gridId, isInit) {
		var keyName = gridId + "IsInit";
		dataGridCache[keyName] = !!isInit;
	}
	var getDataTotalRows = function(gridId) {
		var keyName = gridId + "TotalRows";
		return dataGridCache[keyName];
	}
	var setDataTotalRows = function(gridId, totalRows) {
		var keyName = gridId + "TotalRows";
		dataGridCache[keyName] = totalRows;
	}
	
	var _instanceList = (function(){
		var $list = new Array();
		function getInstance(){
			return $list;
		};
		function setInstance(obj){
			$list.push(obj)
		};
		return {
			setInstance : setInstance,
			getInstance : getInstance
		}
	}())
	
	var _cache = function(){};
		_cache.prototype = {
				_caches : new Object(),
				keys : new Array(),
				$editColumns : "", 
				addCache : function(object,keys){
					
					if(object.hasOwnProperty('$window')) return;
					
					var originKey = this.generateKeys(object,keys);
					if(_.isEmpty(originKey)) return;
					if(originKey != null){
						if(this._caches['' + originKey] !== undefined) return
						
							this._caches['' + originKey] = object;
						
					}
				},
				removeCache : function(object,keys){
					
					var originKey = this.generateKeys(object,keys);
					
					if(originKey != null && this._caches[originKey] !== undefined){
						try{
							delete this._caches[originKey];
						}catch (e) {
							throw e;
						}
					}
				},
				_updateCache:function(obj){
					var $parent = this;
					var _key = this.generateKeys.call(this,obj,this.keys);
					var _flag = this._caches[_key] !== undefined && this._caches[_key] instanceof Object;
					var _cols = this._getCols();
					if(_flag) 
						if(_cols && typeof _cols == "string")
							this._setVal.apply(this,[_key,_cols,obj[_cols]]);
						else if(_cols && Array.isArray(_cols))
							{
								$.each(_cols,function(){
									$parent._setVal.apply($parent,[this,obj[this]]);
								})
							}
				},
				_updateGrid : function(grid,_index,field,newValue){
					try {
						return grid.jqxGrid('setcellvalue', _index, field,newValue);
					} catch (e) {
						throw e
					}
				},
				_setVal : function(key,$key,$newValue){
					if(this._caches[key].hasOwnProperty($key)){
						this._caches[key][$key] = $newValue;
					}else throw new Error("can't set value of key no contains maps");
				},
				_getCols :function(){
					if(this.$editColumns.indexOf(',') != -1)
						return this.$editColumns.split(',') ;
					return 	this.$editColumns || "";
				},
				_setCols : function(columns){
					this.$editColumns = columns || "";
				},
				_getKeys : function(){
					return this.keys;
				},
				_setKeys : function(keys){
					this.keys = keys || "";
				},
				_getCacheMode : function(){
					return this.cacheMode || null
				},
				_setCacheMode : function(mode){
					this.cacheMode =  mode || null
				},
				getInstance : function(){
					return this._grid;
				},
				setInstance : function(instance){
					if(instance&&instance.length > 0)
						this._grid = instance.attr('id');
						_instanceList.setInstance(this);
				},
				generateKeys : function(object,keys){
					var arr = [];
					var rs = [];
					if(keys.indexOf(',')){
						arr = keys.split(',');
						for(var k in arr){
							if(_.indexOf(rs,object[arr[k]]) == -1)
								rs.push(object[arr[k]]);
							else continue
						}
						return rs.join('-').trim()
					}else return keys.trim()
					return null;
				}
				,_initEventDefault : function($object){
					var $parent = this;
					$object.on('checkedAll',function(event,flag){
							var _dataPage = $object.jqxGrid('getboundrows');
							for(var k in _dataPage){
								if(_dataPage[k] !== undefined && _dataPage[k].$window == undefined)
									{
										if(flag){
											$parent.addCache.call($parent,_dataPage[k],$parent._getKeys());
										}else{
											$parent.removeCache.call($parent,_dataPage[k],$parent._getKeys());
										}
										
									}
							}
					}).on('rowselect', function (event) 
							{
								if(event.args !== undefined){
									var args = event.args;
								    var $object = args.row;
								    if($object !== undefined){
								    	$object.rowindex = args.rowindex;
									    if($object !== undefined && $object !== null){
									    	$parent.addCache.call($parent,$object,$parent.keys);
									    }
								    }
								}
							}).on('rowunselect', function (event) 
							{
							    var args = event.args;
							    var $object = args.row;
							    if($object !== undefined && $object !== null){
							    	$parent.removeCache.call($parent,$object,$parent.keys);
							    }
							}).on('bindingcomplete', function (event) 
							{
//								$object.jqxGrid('clearSelection');
								var _dataPage = $object.jqxGrid('getboundrows');
								if(_dataPage && _dataPage.length > 0)
									for(var k in _dataPage){
										if(_dataPage[k] !== undefined && _dataPage[k].$window == undefined)
											var _key = $parent.generateKeys(_dataPage[k],$parent.keys);
											if($parent._caches[_key] !== undefined){
												var r = $object.jqxGrid('selectrow', _dataPage[k].uid);
												if($parent._caches[_key].rowindex != _dataPage[k].uid){
													if($parent._caches[_key].rowindex != _dataPage[k].rowindex){
														$object.jqxGrid('selectrow', $parent._caches[_key].rowindex);
													}else $object.jqxGrid('selectrow', _dataPage[k].rowindex);
												} 
												/*try update columns after grid reload*/
												var _editCols = $parent._getCols();
												var _index =  _dataPage[k].uid;
												if(_editCols)
													if(typeof _editCols == "string")
														$parent._updateGrid.call($parent,$object,_index,_editCols,$parent._caches[_key][_editCols]);
															if($parent._caches[_key].rowindex != _index)
															$parent._updateGrid.call($parent,$object,$parent._caches[_key].rowindex,_editCols,$parent._caches[_key][_editCols]);
													else
														for(var k in _editCols){
															$parent._updateGrid.call($parent,$object,_index,_editCols[k],$parent._caches[_key][_editCols[k]]);
															if($parent._caches[_key].rowindex != _index)
																$parent._updateGrid.call($parent,$object,$parent._caches[_key].rowindex,_editCols[k],$parent._caches[_key][_editCols[k]]);
														}
										}
									}
							}).on('cellendedit', function (event) 
							{
							    var args = event.args;
							    if(args !== undefined){
							    	var _objEdits = args.row;
							    	$parent.addCache.call($parent,_objEdits,$parent._getKeys());
								    if(args.value != args.oldvalue)
								    	if($parent._getCacheMode() == 'save')
								    		$parent._updateCache.call($parent,_objEdits);
							    }
							})
				},
				run : function($grid,keys,keyEdit,cacheMode){
					this.setInstance($grid);
					this._setKeys(keys);
					this._setCols(keyEdit);
					this._setCacheMode(cacheMode);
					if($grid.length > 0) 
						this._initEventDefault($grid);
				}
		};		
	
var GridClass = function() {
		var isUseLocalData = false;
		var combobox = [];
        var dropdownGrid = [];
		var sourceAdapters = {};
		var self = {};
		var initDropDownButton = function(config, datafields, columns, formatData, grid, dropdown, key, description) {
			var dropdownConfig = config.dropdown ? config.dropdown : {};
			var width = dropdownConfig.width ? dropdownConfig.width : 200;
			var height = dropdownConfig.height ? dropdownConfig.height : 25;
			var dropDownHorizontalAlignment = dropdownConfig.dropDownHorizontalAlignment ? 'right' : 'left';
			var theme = dropdownConfig.theme ? dropdownConfig.theme : 'olbius';
			dropdown.jqxDropDownButton({
				theme : theme,
				width : width,
				height: height,
				dropDownHorizontalAlignment : dropDownHorizontalAlignment
			});
			if(config.url && config.source && !config.source.url){
				config.source.url = config.url;
			}else if(config.url && !config.source){
				config.source = {
					url : config.url
				};
			}
			initGrid(config, datafields, columns, formatData, grid);
			var rowSelect;
			grid.on('rowselect', function(event) {
				var args = event.args;
				if(typeof(key) == "function"){
					key(dropdown, grid);
				}else{
					var row = grid.jqxGrid('getrowdata', args.rowindex);
					if(typeof(description) == "function"){
						var val = description(row);
						initTooltipDropdown(dropdown, val);
						dropdown.data('value', row[key]);
					}else if(typeof(description) == "string"){
						initTooltipDropdown(dropdown, row[description]);
						dropdown.data('value', row[key]);
					}else{
						initTooltipDropdown(dropdown, row[key]);
						dropdown.data('value', row[key]);
					}
					if (config.closeOnSelect != "N") {
						dropdown.jqxDropDownButton("close");
					}
				}
			});
			var clearOnClose = config.clearOnClose?true:false;
			if (clearOnClose) {
				dropdown.on("close", function (event) {
					if (event.target.innerText.trim()) {
						setTimeout(function() {
							grid.jqxGrid('clearselection');
						}, 100);
					}
				});
			}
		};
		var getDropDownValue = function(dropdown){
			var val = dropdown.data('value');
			if(val){
				return val;
			}
			return dropdown.val();
		};
		var setDropDownValue = function(dropdown, val, label){
			if(dropdown.length && val){
				dropdown.data('value', val);
			}
			if(dropdown.length && label){
				initTooltipDropdown(dropdown, label);
			}
		};
		var cleanDropDownValue = function(dropdown){
			dropdown.data('value', "");
			dropdown.val("");
			initTooltipDropdown(dropdown, "");
		};
		var initTooltipDropdown = function(dropdown, label){
			dropdown.jqxDropDownButton('setContent', "<div class='custom-dropdown' style='position: relative; margin-left: 3px; margin-top: 5px;'>" + label + "</div>");
			dropdown.find('.custom-dropdown').jqxTooltip({ content: "<div style='max-width: 300px;height: auto;'>" + label + "</div>", position: 'mouse', name: 'movieTooltip'});
		};
		
		var $initCache = function(id,keys,keyEdit,cacheMode){
			var _grid = $('#' + id);
			var cache = new _cache();
			if(cache)
				cache.run(_grid,keys,keyEdit,cacheMode);
		}
		
		var getCache = function(id){
			if(typeof _instanceList == "undefined") return null;
			var _list = _instanceList.getInstance();
			var _currentCache;
			if(_list && Array.isArray(_list)){
				$.each(_list,function(index){
					if(_list[index] instanceof _cache)
						var _id = _list[index].getInstance();
						if(_id == id)
							_currentCache = _list[index];
							return false;
				})
				if(_currentCache && _currentCache instanceof _cache)
					return _currentCache;
			}
			return null;
		}
		
		var initGrid = function(config, datafields, columns, formatData, grid) {
		    var dtConfig = {};
			if(typeof(formatData) == "function"){
				dtConfig.formatData = formatData;
			}else if(formatData && typeof(formatData) == "object"){
				dtConfig = formatData;
			}else if(formatData == 'olap'){
				dtConfig.replaceFields = config.replaceFields ? config.replaceFields : null;
			}
			dtConfig.otherParams = config && config.otherParams ? config.otherParams : "";
			/** add by kieuanhvu **/
			dtConfig.isSaveFormData = config && config.isSaveFormData ? config.isSaveFormData : false;
			dtConfig.formData = config && config.formData ? config.formData : null;
			/** ./end **/
			var sourceG = initSource(config, config.url, datafields, grid);
			sourceG.grid = grid;
			var dataAdapter = initDataAdapter(sourceG, dtConfig);
			grid.attr('data-update', true);
			grid.jqxGrid({
				source : dataAdapter,
				columnsheight: typeof(config.columnsheight) != 'undefined' ? config.columnsheight : 30,
				showfilterrow : typeof(config.filterable) != 'undefined' ? config.filterable : false,
				filterable : typeof(config.filterable) != 'undefined' ? config.filterable : false,
				autoshowfiltericon: true,
				showdefaultloadelement : typeof(config.showdefaultloadelement) != "undefined" ? config.showdefaultloadelement : false,
				autoshowloadelement : typeof(config.autoshowloadelement) != "undefined" ? config.autoshowloadelement : false,
				editable : typeof(config.editable) != 'undefined' ? config.editable : false,
				rowsheight : typeof(config.rowsheight) != 'undefined' ? config.rowsheight : 25,
				autorowheight : typeof(config.autorowheight) != 'undefined' ? config.autorowheight : false,
				localization: config.localization ? config.localization : getLocalization(),
				altrows: typeof(altrows) != "undefined" ? config.altrows : false,
				groupable: typeof(config.groupable) != 'undefined' ? config.groupable : false,
				groups: typeof(config.groups) != 'undefined' ? config.groups : [],
				groupsrenderer: typeof(config.groupsrenderer) != 'undefined' ? config.groupsrenderer : null,
				showgroupsheader: typeof(config.showgroupsheader) != 'undefined' ? config.showgroupsheader : false,
				editmode : typeof(config.editmode) != 'undefined' ? config.editmode : 'selectedrow',
				selectionmode : typeof(config.selectionmode) != 'undefined' ? config.selectionmode : 'singlerow',
				width : typeof(config.width) != 'undefined'? config.width : 600,
				showtoolbar : typeof(config.showtoolbar) != 'undefined' ? config.showtoolbar : false,
				showstatusbar : typeof(config.showstatusbar) != 'undefined' ? config.showstatusbar : false,
				statusbarheight : typeof(config.statusbarheight) != 'undefined' ? config.statusbarheight : 0,
				handlekeyboardnavigation : typeof(config.handlekeyboardnavigation) != 'undefined' ? config.handlekeyboardnavigation : handleKeyboardNavigation,
				showaggregates : typeof(config.showaggregates) != 'undefined' ? config.showaggregates : false,
				theme : typeof(config.theme) != 'undefined' ? config.theme : 'olbius',
				autoheight : typeof(config.autoheight) != 'undefined' ? config.autoheight : true,
				height : typeof(config.height) != 'undefined' ? config.height : "",
				pageable : typeof(config.pageable) != 'undefined' ? config.pageable : true,
				pagesizeoptions: typeof(config.pagesizeoptions) != "undefined" ? config.pagesizeoptions : [5, 10, 15, 20, 25, 50, 100],
				columnsresize: typeof(config.columnsresize) != 'undefined' ? config.columnsresize : true,
                columnsreorder: typeof(config.columnsreorder) != 'undefined' ? config.columnsreorder : true,
				sortable: typeof(config.sortable) != 'undefined' ? config.sortable : false,
				scrollmode: typeof(config.scrollmode) != 'undefined' ? config.scrollmode : "deferred",
				virtualmode : typeof(config.virtualmode) != 'undefined' ? config.virtualmode : true,
				pagesize : typeof(config.pagesize) != 'undefined' ? config.pagesize : 15,
				enablemousewheel : typeof(config.enablemousewheel) != 'undefined' ? config.enablemousewheel : true,
				rtl: typeof(config.rtl) != 'undefined' ? config.rtl : false,
				rendergridrows : function(obj) {
					// return dataAdapterG.records;
					return obj.data;
				},
				ready: typeof(config.ready) == "function" ? config.ready : null,
				rendertoolbar : typeof (config.rendertoolbar) == "function" ? config.rendertoolbar : null,
				renderstatusbar : typeof (config.renderstatusbar) == "function" ? config.renderstatusbar : null,
				rowdetails: typeof (config.rowdetails) != 'undefined' ? config.rowdetails : false,
                rowdetailstemplate: typeof (config.rowdetailstemplate) != 'undefined' ? config.rowdetailstemplate : null, 
                initrowdetails: typeof (config.initrowdetails) == 'function' ? config.initrowdetails : null,
				columns : columns,
				columngroups : typeof (config.columngroups) != 'undefined' ? config.columngroups : null,
				enabletooltips: typeof(config.enabletooltips) != "undefined" ? config.enabletooltips : false,
			});
			grid.on('rowselect', function(event){
				var obj = grid.data('selected');
				var selected = obj ? obj : [];
			    selected = self.selectData(grid, selected, event);
				grid.data('selected', selected);
			});
			grid.on('rowunselect', function(event){
				var obj = grid.data('selected');
				var selected = obj ? obj : [];
				selected = self.unselectData(grid, selected, event);
				grid.data('selected', selected);
			});
			setTimeout(function(){
				grid.trigger('resize');
			}, 50);
		};
		var initSource = function(allconfig, url, datafields, grid) {
			var config = allconfig.source ? allconfig.source : {};
			var url = "";
			if(config.dataUrl){
				url = config.dataUrl;
			}else if(config.baseUrl && config.url){
				 url = config.baseUrl + '/' + config.url;
			}else if(config.url){
				 url = 'jqxGeneralServicer?sname=' + config.url;
			}else if(allconfig.url){
				 url = 'jqxGeneralServicer?sname=' + allconfig.url;
			}
			if(config.addColumns && config.createUrl){
				grid.attr('data-create', true);
			}
			if(config.updateUrl && config.editColumns){
				grid.attr('data-update', true);
			}
			if(config.removeUrl && config.deleteColumns){
				grid.attr('data-delete', true);
			}
			if(allconfig.toPrint){
				grid.attr('data-print', true);
			}
			if(allconfig.excelExport){
				grid.attr('data-excel', true);
			}
			if(allconfig.clearfilteringbutton){
				grid.attr('data-clear', true);
			}
			var sourceG = {
				id: config.id ? config.id : "",
				datafields : datafields,
				cache : config.cache ? config.cache : false,
				datatype : config.dataType ? config.dataType : 'json',
				type : config.type ? config.type : 'POST',
				beforeprocessing : config.beforeprocessing ? config.beforeprocessing : function(data) {
					var res = {};
				    var gridId = grid.attr("id");
                    var mTotalRows = data.TotalRows;
                    var mIsInit = getDataIsInit(gridId);
                    if (mIsInit) {
                    	res.totalrecords = mTotalRows;
                    	setDataTotalRows(gridId, mTotalRows);
                    } else {
                    	res.totalrecords = getDataTotalRows(gridId);
                    }
                    res.records = data.results;
                    
                    //res.totalrecords = data.TotalRows;
                    //res.records = data.results;
                    // source${id}.totalrecords = data.TotalRows;
                    return res;
				},
				filter : function() {
					if(allconfig.filterable){
						grid.jqxGrid('updatebounddata');
					}
				},
				pager : function(pagenum, pagesize, oldpagenum) {
					// callback called when a page or page size is changed.
					setDataIsInit(grid.attr("id"), false);
				},
				sort : function() {
					if(allconfig.sortable){
						grid.jqxGrid('updatebounddata');
					}
				},
				addrow : function(rowid, rowdata, position, commit) {
					if(!allconfig.virtualmode){
						commit(true);
						grid.trigger('createcompleted');
						return;
					}
					if(config.createUrl && config.addColumns){
						var addColumns = config.addColumns ? config.addColumns : "";
						var createUrl = config.createUrl ? config.createUrl : "";
						var data = processData(rowdata, addColumns);
						if(createUrl.indexOf('jqxGeneralServicer') == -1){
							createUrl = "jqxGeneralServicer?jqaction=C&sname=" + createUrl;	
						}
						addRow(grid, createUrl, data, commit, config.functionAfterAddRow,config._customMessErr);
					}else{
						commit(true);
					}
				},
				updaterow : function(rowid, rowdata, commit) {
					if(!allconfig.virtualmode){
						commit(true);
						grid.trigger('updatecompleted');
						return;
					}
					if(config.updateUrl && config.editColumns){
						for(var n in rowdata){
	                        var tmpExisted = grid.jqxGrid('getcolumnindex', n);
	                        if(tmpExisted != -1){
	                        	var column = grid.jqxGrid('getcolumnproperty', n, 'columntype');
	    	                    if(column == 'combobox' && combobox.length){
	    	                    	rowdata[n] = JSON.stringify(combobox);
	    	                    	break;
	    	                    }
	    	                    var infoCl;
	    	                    if(localStorage.getItem('infoColumnDetail')){
	    	                    	infoCl = $.parseJSON(localStorage.getItem('infoColumnDetail'));
	    	                    }
	    	                    if(infoCl){
	    	                    	if(column == 'custom' && dropdownGrid && infoCl.gridname == '${id?if_exists}' && infoCl.field == 'paymentId' && infoCl.columntype == 'custom' && infoCl.type == 'dropdownGrid'){
	    	                    		rowdata[n] = dropdownGrid.paymentId ? dropdownGrid.paymentId : null;
	    	                    		break;
	    	                    	}
	    	                    }
	                        }
	                    }
						var editUrl = config.updateUrl ? config.updateUrl : "";
						var editColumns = config.editColumns ? config.editColumns : "";
						var data = processDataPurpose('update', rowdata, editColumns);
						if(editUrl.indexOf('jqxGeneralServicer') == -1){
							editUrl = "jqxGeneralServicer?jqaction=U&sname=" + editUrl;	
						}
						updateRow(grid, editUrl, data, commit, config.functionAfterUpdate);
					}else{
						commit(true);
					}
				},
				deleterow : function(rowid, commit) {
					var del = function(rowid){
						var rowdata = grid.jqxGrid('getrowdatabyid', rowid);
						var deleteColumns = config.deleteColumns ? config.deleteColumns : "";
						var url = config.removeUrl ? config.removeUrl : "";
						var data = processData(rowdata, deleteColumns);
						if(url.indexOf('jqxGeneralServicer') == -1){
							url = "jqxGeneralServicer?jqaction=D&sname=" + url;	
						}
						deleteRow(grid, url, data, commit, config.deletesuccessfunction, function(){
							grid.jqxGrid('clearSelection');
						});	
					};
					if(!allconfig.virtualmode){
						if(config.deleteFuncCustom !== undefined && typeof config.deleteFuncCustom === 'function') {
							config.deleteFuncCustom(rowid,commit);
						}
						commit(true);
						grid.trigger('deletecompleted');
						return;
					}
					if(config.removeUrl && config.deleteColumns){
						if(rowid.length){
							for(var x in rowid){
								del(rowid[x]);
							}
						}else{
							del(rowid);							
						}
					}else{
						commit(true);
					}
				},
				sortcolumn : config.sortcolumn ? config.sortcolumn : "",
				sortdirection : config.sortdirection ? config.sortdirection : "asc",
				data : config.data ? config.data : {
					noConditionFind : 'Y',
					conditionsFind : 'N',
				},
				pagenum: config.viewIndex ? config.viewIndex : 0,
				pagesize : config.pagesize ? config.pagesize : 15,
				contentType : config.contentType ? config.contentType : 'application/x-www-form-urlencoded',
			};
			if(typeof(allconfig.virtualmode) != undefined && typeof(config.localdata) != undefined && config.localdata != null && allconfig.virtualmode == false){
				sourceG.localdata = config.localdata ? config.localdata : [];
				sourceG.datatype = config.datatype ? config.datatype : "array";
				if(sourceG.hasOwnProperty('filter')) delete sourceG.filter;
				isUseLocalData = true;
			}else {
				sourceG.url = url;
				isUseLocalData = false;
			}
			// sourceAdapters[url] = sourceG;
			return sourceG;
		};
		var initDataAdapter = function(sourceG, config) {
			var grid = sourceG.grid;
			var datafields = sourceG.datafields;
			var formatData = config && config.formatData ? config.formatData : null;
			var defaultFormat = function(data) {
				data.isInit = getDataIsInit(grid.attr("id"));
				if((typeof outFilterCondition === 'undefined' || outFilterCondition =="") && (typeof alterData === 'undefined' || alterData =="" || $.isEmptyObject(alterData))){
					var filterListFields = "";
                    if (data.filterscount) {
                        var tmpFieldName = "";
                        for (var i = 0; i < data.filterscount; i++) {
                            var filterValue = data["filtervalue" + i];
                    		if(filterValue == ""){
                        		continue;
                        	}
                            var filterCondition = data["filtercondition" + i];
                            var filterDataField = data["filterdatafield" + i];
                            var filterOperator = data["filteroperator" + i];
                            if(getFieldType(datafields,filterDataField)=='number'){
                                filterListFields += "|OLBIUS|" + filterDataField + "(BigDecimal)";
                            }else if(getFieldType(datafields,filterDataField)=='date'){
                                filterListFields += "|OLBIUS|" + filterDataField + "(Date)";
                            }else if(getFieldType(datafields,filterDataField)=='Timestamp'){
                            	if(getFieldPattern(datafields,filterDataField) != ''){
                            		filterListFields += "|OLBIUS|" + filterDataField + "(Timestamp)[" + getFieldPattern(datafields,filterDataField) + "]";
                            	}else{
                            		filterListFields += "|OLBIUS|" + filterDataField + "(Timestamp)[dd/MM/yyyy HH:mm:ss]";
                            	}
                            }else if(getFieldType(datafields,filterDataField)=='Long'){//add type long
                            	filterListFields += "|OLBIUS|" + filterDataField + "(Long)";
                            }else if(getFieldType(datafields,filterDataField)=='Double'){//add type Double
                            	filterListFields += "|OLBIUS|" + filterDataField + "(Double)";
                            }else if(getFieldType(datafields,filterDataField) == 'olap'){
                            	if(typeof config.replaceFields !== undefined){
                            		for(field in config.replaceFields){
                            			if(filterDataField == field){
                            				filterListFields += "|OLBIUS|" + config.replaceFields[field + ''];
                                    	}else continue;
                            		}
                            	}else  filterListFields += "|OLBIUS|" + filterDataField;
                            }
                            else{
                                filterListFields += "|OLBIUS|" + filterDataField;
                            }
                            //if has hh:mm:ss fix
                            if(getFieldType(datafields,filterDataField)=='Timestamp' && getFieldPattern(datafields,filterDataField) === ''){
                                if(tmpFieldName != filterDataField){
									if(filterValue.length > 11){
										filterListFields += "|SUIBLO|" + filterValue;
									}else{
										filterListFields += "|SUIBLO|" + filterValue + " 00:00:00";
									}
                                }else{
									if(filterValue.length > 11){
										filterListFields += "|SUIBLO|" + filterValue;
									}else{
										filterListFields += "|SUIBLO|" + filterValue + " 23:59:59";
									}
                                }
                            }else{
                                filterListFields += "|SUIBLO|" + filterValue;
                            }
                            filterListFields += "|SUIBLO|" + filterCondition;
                            filterListFields += "|SUIBLO|" + filterOperator;
                            tmpFieldName = filterDataField;
                        }
                    }
                    filterListFields += self.getAllCondition(grid);
                    data.filterListFields = filterListFields;
                }else if(!(typeof alterData === 'undefined' || alterData =="")){
                    var tmppn = data.pagenum;
                    data = alterData;
                    data.pagenum = tmppn;
                }else{
                    data.filterListFields = outFilterCondition;
                    outFilterCondition = "";
                }
                data.$skip = data.pagenum * data.pagesize;
                data.$top = data.pagesize;
                data.$inlinecount = "allpages";
				data.otherParams = config && config.otherParams ? config.otherParams : null;
				/** add by kieuanhvu **/
				if(config.isSaveFormData && config.formData){
					config.formData.data = _.extend({}, data);
				}
				/** ./end **/
                return data;
			};
			var format = typeof (formatData) == "function" ? formatData : defaultFormat;
			var dataAdapterG = new $.jqx.dataAdapter(sourceG, {
				formatData : format,
				loadError : function(xhr, status, error) {
				},
				downloadComplete : function(data, status, xhr) {
					var grid = sourceG.grid;
					setDataIsInit(grid.attr("id"), true);
					if(data !== undefined && data.records !== undefined) localStorage.total_records = data.records.length;
				},
				beforeLoadComplete : function(records) {
				}
			});
			return dataAdapterG;
		};
		self.getAdapter = function(grid){
			var adapter = grid.jqxGrid('source');
			return adapter;
		};
		self.setAdapter = function(grid, source) {
			var adapter = grid.jqxGrid('source');
			grid.jqxGrid('source', source);
		};
		var handleKeyboardNavigation = function(event){
            var key = event.charCode ? event.charCode : event.keyCode ? event.keyCode : 0;
            var obj = $(event.currentTarget);
            var id = obj.attr('id');
            var grid = $('#' + id);
            var update = grid.attr('data-update');
            var del = grid.attr('data-delete');
            var add = grid.attr('data-create');
            // var filter = grid.attr('data-filter');
            var clear = grid.attr('data-clear');
            var print = grid.attr('data-print');
            var excel = grid.attr('data-excel');
            // F2    
            if (update && update == "true")
            if (key == 113) {
                $("#updaterowbutton" + id).click();
                return true;
            };
            // F8
            if (del && del == "true")
            if (key == 119) {
                $("#deleterowbutton" + id).click();
                return true;
            };
            // F5
            if (clear && clear == "true")
            if (key == 116) {
                $("#filterbutton" + id).click();
                return true;
            };
            // Ctrl + F
            // if (clear && clear == "true")
            // if (key == 70 && event.ctrlKey) {
		// event.preventDefault();
                // $('#clearfilteringbutton' + id).click();
                // return true;
            // }

            // Ctrl + I
            if (add && add == "true")
            if (key == 73 && event.ctrlKey) {
                $('#addrowbutton' + id).click();
                return true;
            }
            // F9
            // if ("${updatemultiplerows}" == "true")
            // if (key == 120) {
                // $('#updatemultiplerows').click();
                // return true;
            // }
           // F7
           if (print && print=="true")
            if (key == 118) {
                $('#print').click();
                return true;
            }                      
           // Ctrl + E
            if (excel && excel=="true")
            if (key == 69 && event.ctrlKey) {
                $('#excelExport').click();
                return true;
            }                                                                        
		};
		var getFieldType = function(datafields, fName){
			for (i=0;i < datafields.length;i++) {
				if(datafields[i]['name'] == fName){
					if(!(typeof datafields[i]['other'] === 'undefined' || datafields[i]['other'] =="")){
						return  datafields[i]['other'];
					}else{
						return  datafields[i]['type'];
					}
					
				}
			}
		};
		var getFieldPattern = function(datafields, fName){
            for (i=0;i < datafields.length;i++) {
               if(datafields[i]['name'] == fName){
                    if(!(typeof datafields[i]['pattern'] === 'undefined' || datafields[i]['pattern'] =="")){
                        return  datafields[i]['pattern'];
                    }else{
                        return  '';
                    }
                    
               }
            }
        };
		var addRow = function(grid, url, data, commit, callback,_customMessErr) {
			sendRequest(grid, url, data, commit, wgaddsuccess, callback,_customMessErr);
		};
		var updateRow = function(grid, url, data, commit, callback) {
			sendRequest(grid, url, data, commit, wgupdatesuccess, callback);
		};
		var deleteRow = function(grid, url, data, commit, callback) {
			var commitDelete = function(flag){
				if (isUseLocalData) {
					commit(flag);
				}
			}
			sendRequest(grid, url, data, commitDelete, wgdeletesuccess, callback);
		};
		var sendRequest = function(grid, url, data, commit, message, callback,_customMessErr) {
			var id = grid.attr("id");
			grid.jqxGrid('showloadelement');
			$.ajax({
				type : "POST",
				url : url,
				data : data,
				success : function(data, status, xhr) {
					if (data.responseMessage == "error") {
						var errorMessage = "";
						if(_customMessErr && typeof _customMessErr == "function"){
							var messTemp = "";
							if (data.errorMessageList) {
								messTemp = data.errorMessageList[0];
							} else if (data.errorMessage) {
								messTemp = data.errorMessage;
							}	
							errorMessage = _customMessErr(messTemp);
						}else{
							errorMessage = data.responseMessage;
							if (data.errorMessageList) {
								errorMessage = data.errorMessageList[0];
							} else if (data.errorMessage) {
								errorMessage = data.errorMessage;
							}
						}
						if ( typeof (commit) == "function") {
							commit(false);
						}
						renderMessage(id, errorMessage, {
							autoClose : true,
							template : 'error',
							appendContainer : "#container" + id,
							opacity : 1,
						});
					} else {
						if ( typeof (commit) == "function") {
							commit(true);
						}
						if ( typeof (callback) == "function") {
							callback(data);
						}
						renderMessage(id, message, {
							autoClose : true,
							template : 'success',
							appendContainer : "#container" + id,
							opacity : 1,
							icon : {
								width : 25,
								height : 25,
								url : '/aceadmin/assets/images/info.jpg'
							}
						});
						grid.jqxGrid('updatebounddata');
					}
				},
				error : function() {
					if (commit) {
						commit(false);
					}
				},
				complete : function() {
					grid.jqxGrid('hideloadelement');
				}
			});
		};
		var processDataPurpose = function(action, rowdata, columns) {
			switch(action) {
			case "update" :
				var tmp = processData(rowdata, columns);
				return {
					rl : 1,
					columnValues0 : tmp.columnValues,
					columnList0 : tmp.columnList
				};
			default:
				return processData(rowdata, columns);
			};
		};
		var processData = function(rowdata, addColumns) {
			var keysData = Object.keys(rowdata).toString();
			var arrKeysData = keysData.split(",");
			var columns = addColumns.split(";");
			var tmpAddclm = "";
			var spl = "";
			var splCol = "";
			var data = {};
			var tmpcolValue = "";
			for (var i = 0; i < columns.length; i++) {
				if (i != 0) {
					spl = "#;";
					splCol = ";";
				}
				tmpKey = columns[i];
				if (columns[i].indexOf('(') > -1) {
					tmpKey = columns[i].substring(0, columns[i].indexOf('('));
				} else if (columns[i].indexOf('[') > -1) {
					tmpKey = columns[i].substring(0, columns[i].indexOf('['));
				}
				if (columns[i].indexOf('[') > -1) {
					if (columns[i].indexOf(".Timestamp)") > -1 || columns[i].indexOf(".Date)") > -1) {
						var tmstr = columns[i].substring(columns[i].indexOf('[') + 1, columns[i].length - 1);
						if (tmstr) {
							var tmpdate = new Date(tmstr);
							var tmps = isNaN(tmpdate.getTime()) ? "" : tmpdate.getTime();
							tmpcolValue += spl + tmps;
						} else {
							tmpcolValue += spl;
						}
					} else {
						tmpcolValue += spl + columns[i].substring(columns[i].indexOf('[') + 1, columns[i].length - 1);
					}
				} else {
					if (columns[i].indexOf(".Timestamp)") > -1 || columns[i].indexOf(".Date)") > -1) {
						var tmstr = rowdata[tmpKey];
						if (tmstr) {
							var tmpdate = new Date(tmstr);
							var tmps = isNaN(tmpdate.getTime()) ? "" : tmpdate.getTime();
							tmpcolValue += spl + tmps;
						} else {
							tmpcolValue += spl;
						}
					} else {
						tmpcolValue += spl + rowdata[tmpKey];
					}
				}
				if (columns[i].indexOf('[') > -1) {
					tmpAddclm += splCol + columns[i].substring(0, columns[i].indexOf('['));
				} else {
					tmpAddclm += splCol + columns[i];
				}
			}
			data = {
				columnValues : tmpcolValue,
				columnList : tmpAddclm
			};
			return data;
		};
		var hideGridMessage = function(id) {
			$('#container' + id).empty();
		};
		var updateGridMessage = function(id, template, message) {
			$('#container' + id).empty();
			$('#jqxNotification' + id).jqxNotification({
				template : template
			});
			$("#notificationContent" + id).text(message);
			$("#jqxNotification" + id).jqxNotification("open");
		};
		var displayEditSuccessMessage = function(id, config) {
			$('#container' + id).empty();
			$('#jqxNotification' + id).jqxNotification({
				template : 'success',
				appendContainer : "#container" + id
			});
			$("#notificationContent" + id).text("${StringUtil.wrapString(uiLabelMap.wgupdatesuccess)}");
			$("#jqxNotification" + id).jqxNotification("open");
			if(typeof(config) != 'undefined'){
				var delaytime = config.delaytime ? config.delaytime : 3;
			}
		};
		var displayDeleteSuccessMessage = function(id) {
			$('#container' + id).empty();
			$('#jqxNotification' + id).jqxNotification({
				template : 'success',
				appendContainer : "#container" + id
			});
			$("#notificationContent" + id).text("${StringUtil.wrapString(uiLabelMap.wgdeletesuccess)}");
			$("#jqxNotification" + id).jqxNotification("open");
		};
		var renderMessage = function(id, message, template) {
			$('#container' + id).empty();
			var containernotify = $('#jqxNotification' + id);
			var notifycontent = $("#notificationContent" + id);
			var obj;
			if(typeof(template) == 'string'){
				obj = {
					autoClose : true,
					template : template,
					appendContainer : "#container" + id,
					opacity : 0.9
				};
			}else obj = template;
			if(!containernotify.length){
				var str = "<div id='jqxNotification"+id+"'><div id='notificationContent"+id+"'>" + message + "</div></div>";
				$('#' + id).prepend(str);
				containernotify = $('#jqxNotification' + id);
				notifycontent = $("#notificationContent" + id);
			}
			containernotify.jqxNotification(obj);
			notifycontent.text(message);
			containernotify.jqxNotification("open");
		};
		var clearDropDownButton = function(dropdown, grid) {
			if(typeof(dropdown.data('value')) != 'undefined') dropdown.data('value','');
			dropdown.jqxDropDownButton("setContent", '');
			grid.jqxGrid("clearselection");
		};
		var clearForm = function(par){
			var child = par.find('*[class^="jqx"]');
			var tmp = "";
			par.find(':input').not(':button, :submit, :reset, :hidden, :checkbox, :radio').val('');
			par.find('textarea').val('');
   			par.find(':checkbox, :radio').prop('checked', false);
			for(var x = 0; x < child.length; x++){
				var obj = $(child[x]);
				if(!obj){
					continue;
				}
				tmp = obj.attr('id');
				if(tmp){
					tmp = obj.attr('class');
					if(!tmp) {continue;}
					if(tmp.indexOf('dropdownlist') != -1 && tmp.indexOf('jqx-editor-') == -1){
						tmp = obj.attr('aria-owns');
						if(!tmp) {continue;}
						if(typeof(obj.data('value')) != 'undefined'){
							obj.data('value','');
						};
						if(tmp.indexOf('dropDownButton') != -1 && obj.jqxDropDownButton){
							obj.jqxDropDownButton('setContent','');
							obj.jqxDropDownButton('close');	
							tmp = obj.attr('data-grid');
							if(tmp && $('#'+tmp).jqxGrid){
								$('#'+tmp).jqxGrid('clearselection');
							}
						}else if(obj.jqxDropDownList){
							obj.jqxDropDownList('clearSelection');
							obj.jqxDropDownList('close');	
						}
					}else if(tmp.indexOf('combobox') != -1 && obj.jqxComboBox){
						obj.jqxComboBox('clearSelection');
						obj.jqxComboBox('close');	
					}else if(tmp.indexOf('maskedinput') != -1 && obj.jqxMaskedInput){
						obj.jqxMaskedInput('clear');
					}else if(tmp.indexOf('datetimeinput') != -1 && obj.jqxDateTimeInput){
						obj.jqxDateTimeInput('val', null);
						obj.jqxDateTimeInput('close');
					}else if(tmp.indexOf('numberinput') != -1 && obj.jqxNumberInput){
						obj.jqxNumberInput('clear');
					}else if(tmp.indexOf('grid') != -1 && obj.jqxGrid){
						obj.jqxGrid('clearselection');
					}else if(tmp.indexOf('input') != -1 || tmp.indexOf('editor')){
						obj.val('');
					}
				}
			}
			if(par.jqxValidator){
				par.jqxValidator('hide');
			}
		}; 
		createAddRowButton = function(grid, container, label, popup){
			var id = grid.attr('id');
			var bu = '<button id="addrowbutton'+id+'" style="margin-left:20px;" title="(Ctrl+I)"><i class="icon-plus open-sans"></i><span>'+label+'</span></button>';
            container.append(bu);
            var obj = $("#addrowbutton" + id);
            obj.jqxButton({ theme: theme });
            // create new row.
            obj.on('click', function () {
                if(popup.type && popup.type == 'popup' && popup.container){
                	var wi = popup.container;
                	var tmpwidth = wi.jqxWindow('width');
                    wi.jqxWindow({ position: { x: (window.outerWidth - tmpwidth)/2, y: pageYOffset + 30 } });
                    wi.jqxWindow('open');
                    wi.on('close', function (event) { 
                        if(wi.jqxValidator){
                        	wi.jqxValidator('hide');	
                        }
                    }); 
                }else if(popup.type && popup.type == 'popup'){
                	var tmpwidth = $('#popupWindow' + id).jqxWindow('width');
                    var wi = $("#popupWindow" + id); 
                    wi.jqxWindow({ position: { x: (wtmp.outerWidth - tmpwidth)/2, y: pageYOffset + 30 } });
                    wi.jqxWindow('open');
                    wi.on('close', function (event) { 
                    	if(wi.jqxValidator){
                    		wi.jqxValidator('hide');
                    	}
                    }); 
                }else if(popup.type && popup.type != 'popup'){
                	var selectedrowindex;
	                if(grid.jqxGrid('getselectedrowindex') == null){
	                    selectedrowindex = 0;
	                }else{
	                    selectedrowindex = grid.jqxGrid('getselectedrowindex').rowindex;
	                }
	                var dataRecord = grid.jqxGrid('getrowdata', selectedrowindex);

	                var row;
	                if(dataRecord == null){
	                    row = popup.data ? popup.data : {};
	                }else{
	                    var primaryKey = dataRecord[popup.data];                                               
	                    row = {};
	                    row[popup.data] = primaryKey;
	                }
                    console.log('row', row, 'selectedrowindex', selectedrowindex, 'dataRecord', dataRecord);
                	grid.jqxGrid('addRow', null, row, "first");
                    grid.jqxGrid('clearSelection');                        
                    grid.jqxGrid('selectRow', 0);    
                }
            });
		};
		
		var addUpdateRowButton = function(grid,container,label){
			
		};
		
		var createCustomControlButton = function(grid, container, value){
			var tmpStr = value.split("@");
			var id = grid.attr('id');
			var str = '';
			var group = $('.custom-control-toolbar').length + 1;
            if(tmpStr.length == 4){
            	if(tmpStr[1] == '_last_update_'){
            		str = '<div class="custom-control-toolbar">' + '&nbsp;<span id=' + tmpStr[1] +'></span>&nbsp;'
    					+'<a id="customcontrol' + id + group + '" style="color:#438eb9;" href="' + tmpStr[2] +'" onclick="' + tmpStr[3] + '">'
    					+'<i class="' + tmpStr[0] +'"></i></a><span id="_status_update_" style="float: right;margin-right: 4px; color: #4383b4;display:none;"><image src="/images/ajax-loader.gif"></span></div>';
    			}else{
    				str = '<div class="custom-control-toolbar">'
    					+'<a id="customcontrol' + id + group + '" style="color:#438eb9;" href="' + tmpStr[2] +'" onclick="' + tmpStr[3] + '">'
    					+'<i class="' + tmpStr[0] +'"></i>&nbsp;<span>' + tmpStr[1] +'</span></a></div>';
    			}
                container.append(str);
            }else{
		var tmp = tmpStr[2];
		var link = tmpStr[2];
		var target = "";
		if(tmp.indexOf("$") != "-1"){
			var arr = tmp.split("$");
			link = arr.shift();
			target = arr.pop();
		}
				str = '<div class="custom-control-toolbar"><a id="customcontrol' + id + group +'" style="color:#438eb9;" href="' + link +'" '+target+'>'
					+'<i class="' + tmpStr[0] +'"></i>&nbsp;<span>' + tmpStr[1] +'</span></a></div>';
                container.append(str);
            }
		};
		var createFilterButton = function(grid, container, label){
			var id = grid.attr('id');
			var str = '<button id="filterbutton'+id+'" style="margin-left:20px; display:inline-block;" class="pull-right"><i class="icon-filter open-sans"></i><span>'+label+'</span></button>';
			container.append(str);
            var obj = $('#filterbutton' + id); 
            obj.jqxButton();
            obj.click(function () {
		var filterable = grid.jqxGrid('filterable');
		if(filterable){
			grid.jqxGrid('clearfilters');
		}else{
					grid.jqxGrid('updatebounddata');
		}
            });
		};
		var triggerToolbarEvent = function(grid, container, content){
			container.append("<div style='float:right;margin-left:20px;margin-top: 4px; font-size: 14px; font-weight: normal;'>"+content+"</div>");
        	grid.trigger('loadCustomControlAdvance');
		};
		var createCustomAction = function(grid, container, customtoolbaraction){
        	if(typeof(customtoolbaraction) == "function"){
        		customtoolbaraction(container);
        	}
		};
		var createSettingButton = function(grid, container, showlist){
			var strSList = [];
			var id = grid.attr('id');
			if(typeof(showlist) == 'string' && showlist != "true"){
				strSList = showlist.split(";");
			}else if(typeof(showlist) == 'string' && showlist == "true"){
				var columns = grid.jqxGrid('columns');
            	if(typeof(columns) == 'undefined'){
            		return;
            	}
                var allFields = columns.records;
                var strSList = [];
                for(i = 0; allFields != undefined && i < allFields.length;i++){
                    strSList[i] = allFields[i].datafield;
                }
			}
            var strNList = [];
            for(i=0; i < strSList.length;i++){
                strNList[i] = $('<textarea />').html($('#' + id).jqxGrid('getcolumn', strSList[i]).text).text();
            }
            var listSource = [];
            for(i=0; i < strSList.length;i++){
            	var tmpVL;
//            	fix config to hidden column by hoangominh
            	if (!strNList[i]) {
					continue;
				}
            	if (grid.jqxGrid('getcolumnproperty', strSList[i], 'hidden')) {
            		tmpVL = {label: strNList[i], value: strSList[i], checked: false};
				} else {
					tmpVL = {label: strNList[i], value: strSList[i], checked: true};
				}
                listSource[i] = tmpVL;
            }
            $("#showSL" + id).jqxDropDownList({ checkboxes: true, source: listSource, autoDropDownHeight : true , displayMember: "label", valueMember: "value"});
            $("#frozenSL" + id).jqxDropDownList({ checkboxes: true, source: listSource, autoDropDownHeight : true, displayMember: "label", valueMember: "value", placeHolder: uiLabelMap.filterchoosestring});
            $("#frozenSL" + id).jqxDropDownList('uncheckAll');
            var str = '<button id="btngridsetting'+id+'" class="btn btn-mini btngridsetting pull-right" onclick="openJqxConfigWindow(\''+id+'\');" style="display:inline-block; text-shadow: none"><i class="fa-cogs"></i></button>'; 
            container.append(str);
            // init window
            $("#jqxconfig" + id).jqxWindow({
                width: 400, height: 180, resizable: false, isModal: true, autoOpen: false, cancelButton: $("#configCancel" + id), modalOpacity: 0.7, modalZIndex: 10000          
            });
            
            $("#configSave" + id).click(function () {
                var i = 0;
                var dislayList = $("#showSL" + id).jqxDropDownList('getCheckedItems');
                var frozenList = $("#frozenSL" + id).jqxDropDownList('getCheckedItems');
                var dislayListValue = [];
                var frozenListValue = [];
                for(i = 0; i < dislayList.length; i++){
                    dislayListValue[i] = dislayList[i].value;
                }
                for(i = 0; i < strSList.length; i++){
                    if(dislayListValue.indexOf(strSList[i]) < 0){
                        $("#" + id).jqxGrid('hidecolumn', strSList[i]);
                    }else{
                        if(!$('#' + id).jqxGrid('iscolumnvisible', strSList[i])){
                            $("#" + id).jqxGrid('showcolumn', strSList[i]);
                        }
                    }
                }
                for(i = 0; i < frozenList.length; i++){
                    frozenListValue[i] = frozenList[i].value;
                }
                for(i = 0; i < strSList.length; i++){
                    if(frozenListValue.indexOf(strSList[i]) > -1){
                        $("#" + id).jqxGrid('pincolumn', strSList[i]);
                    }else{
                        if($('#' + id).jqxGrid('iscolumnpinned', strSList[i])){
                            $("#" + id).jqxGrid('unpincolumn', strSList[i]);
                        }
                    }
                }
                $("#jqxconfig" + id).jqxWindow('close');
            });
		};
		var createPrintButton = function(grid, container, label){
			var id = grid.attr('id');
			container.append('<input style="margin-left: 20px;" id="print${id}" type="button" value="<span>'+label+'</span>" />');
            var obj = $("#print" + id);
            obj.jqxButton();
            obj.click(function () {
                var gridContent = grid.jqxGrid('exportdata', 'html');
                var newWindow = window.open('', '', 'width=800, height=500'),
                document = newWindow.document.open(),
                pageContent =
                    '<!DOCTYPE html>\n' +
                    '<html>\n' +
                    '<head>\n' +
                    '<meta charset="utf-8" />\n' +
                    '<title>jQWidgets Grid</title>\n' +
                    '</head>\n' +
                    '<body>\n' + gridContent + '\n</body>\n</html>';
                document.write(pageContent);
                document.close();
                newWindow.print();
            });
		};
		var createExcelExport = function(grid, container, label){
			var id = grid.attr('id');
			var str = '<input style="margin-left: 20px;" id="excelExport'+id+'" type="button" value="'+label+'" />';
            container.append(str);
            var obj = $("#excelExport" + id);
            obj.jqxButton();
            $("#excelExport").click(function () {
                grid.jqxGrid('exportdata', 'xls', 'jqxGrid');           
            }); 
		};
		var createUpdateMultiRowButton = function(grid, container, label){
			var id = grid.attr('id');
			var str = '<button style="margin-left: 20px;" id="updatemultiplerows'+id+'"><i class="fa fa-check"></i><span>'+label+'</span></button>';
            container.append(str);
            var obj = $("#updatemultiplerows" + id);
            obj.jqxButton();                    
            obj.on('click', function () {
                var rowscount = grid.jqxGrid('getdatainformation').rowscount;
                var status = "true";
                var selectedrowindex = grid.jqxGrid('getselectedcell').rowindex;
                var column =  grid.jqxGrid('getselectedcell').column;
                var value = grid.jqxGrid('getselectedcell').value;
                var dataRecord = grid.jqxGrid('getrowdata', selectedrowindex);
                var columnNames = grid.jqxGrid('getcolumn',  column).text;
                editrow = selectedrowindex;
                $("#dialog-message" + id).text("These all items in Column: " + columnNames + " := " + value +  " will be modify. Are you sure?" );
                $("#dialog-message" + id).dialog({
                  resizable: false,
                  height:180,
                  modal: true,
                  buttons: {
                    "Save": function() {                                  
                    $(this).dialog( "close" );
                    var offset = grid.offset();
                     $("#popupModifyWindow" + id).jqxWindow({ position: { x: parseInt(offset.left) + 60, y: parseInt(offset.top) + 20 } });
                     $("#newValueUpdate" + id).val(value);
                     $("#popupModifyWindow" + id).jqxWindow('open');                                                                   
                    },
                    Cancel: function() {
                      $(this).dialog( "close" );
                    }
                  }
                });
            });                             
		};
		var createDeleteRowButton = function(grid, container, label, deleteConditionFunction, successMessage, errorMessage, warningMessage, confirmLabel, cancelLabel){
			var id = grid.attr('id');
			var str = '<button style="margin-left: 20px;" id="deleterowbutton'+id+'"><i class="icon-trash open-sans"></i><span>'+label+'</span></button>';
            container.append(str);
            var obj = $("#deleterowbutton" + id);
            obj.jqxButton();
            obj.on('click', function () {   
            	if(typeof(deleteConditionFunction) == "function"){
            		var res = deleteConditionFunction();
            		if(typeof(res) == "boolean" && !res){
            			var message = successMessage;
            			if(!message){
            				message = errorMessage;	
            			}
            			bootbox.alert(message);
            			return;
            		}else if(typeof(res) == "string"){
            			bootbox.alert(res);
            			return;
            		}
            	}
            	var selectedrowindexes = grid.data('selected');
                var rowscount = grid.jqxGrid('getdatainformation').rowscount;
                if (selectedrowindexes && selectedrowindexes.length) {
                    bootbox.dialog(warningMessage, [{
			            "label"   : cancelLabel,
			            "icon"    : 'fa fa-remove',
			            "class"   : 'btn  btn-danger form-action-button pull-right',
			            "callback": function() {
		                  bootbox.hideAll();
		                }
			        }, {
			            "label"   : confirmLabel,
			            "icon"    : 'fa-check',
			            "class"   : 'btn btn-primary form-action-button pull-right',
			            "callback": function() {
		                  	bootbox.hideAll();
							var rows = [];
							for(x in selectedrowindexes){
								var selectedrowindex = selectedrowindexes[x];
								var rowid = grid.jqxGrid('getrowid', selectedrowindex);
								if(!rowid){
									var row = grid.jqxGrid('getrowdatabyid', selectedrowindex);
									rows.push(row.uid);
								}else{
									rows.push(rowid);
								}
							}
							grid.jqxGrid('deleterow', rows);
		               	}
			        }]);
                }
            });
		};
		var createUpdateRowButton = function(grid, container, label, editColumns){
			var btn = '<input style="margin-left: 20px;" id="updaterowbutton${id}" type="button" value="'+label+'" />';
			var id = grid.attr('id');
            container.append(btn);                                                            
            var obj = $("#updaterowbutton" + id); 
            obj.jqxButton();
            obj.on('click', function () {   
                var selectedrowindex = grid.jqxGrid('getselectedcell').rowindex;                                                                             
                var rowscount = grid.jqxGrid('getdatainformation').rowscount;                 
                if (selectedrowindex >= 0 && selectedrowindex < rowscount) {
                   openUpdateSingleRowPopup(grid, editColumns);
                }                                                
           }); 
		};
		var openUpdateSingleRowPopup = function(grid, editColumns){
			var offset = grid.offset();
			var id = grid.attr('id');
			$("#popupWindow" + id).jqxWindow({ position: { x: parseInt(offset.left) + 60, y: parseInt(offset.top) + 20 } });
			var dataRecord = grid.jqxGrid('getrowdata', selectedrowindex);     
			var tmp = editColumns.split(';');
			for(var x in editColumns){
				$("#" + editColumns[x]).val(dataRecord[editColumns[x]]);
			}    
			$("#popupWindow" + id).jqxWindow('open');  
		};
		var createClearFilterButton = function(grid, container, label){
			var id = grid.attr('id');
            var btn = '<button id="clearfilteringbutton'+id
            		+'" style="margin-left:20px;" title="(Ctrl+F)"><span style="color:red;font-size:80%;left:5px;position:relative;">x</span><i class="fa-filter"></i></span> '
            		+label+'</button>';
            container.append(btn);
            var obj = $('#clearfilteringbutton' + id);
            obj.jqxButton();
            obj.click(function () {
		var filterable = grid.jqxGrid('filterable');
		if(filterable){
			grid.jqxGrid('clearfilters');
		}else{
					grid.jqxGrid('updatebounddata');
		}
            }); 
		};
		var createRefreshButton = function(grid, container, label){
			var id = grid.attr('id');
            var btn = '<button id="createRefreshButton'+id
			+'" class="btn btn-mini custom-control-toolbar pull-right" title="(Ctrl+L)"><i class="fa-refresh"></i></span> '
			+label+'</button>';
            container.append(btn);
            var obj = $('#createRefreshButton' + id);
            obj.jqxButton();
            obj.click(function () {
                grid.jqxGrid('updatebounddata');
            });
		};
		var createAddMultiRowButton = function(grid, container, label){
			var id = grid.attr('id');
			container.append('<input style="margin-left: 20px;" id="addmultiplerowsbutton'+id+'" type="button" value="'+label+'" />');
            var obj = $("#addmultiplerowsbutton" + id);
            obj.jqxButton();
            obj.on('click', function () {
                grid.jqxGrid('beginupdate');
                for (var i = 0; i < 10; i++) {
                    var datarow = generaterow();
                    var commit = grid.jqxGrid('addrow', null, datarow);
                }
                grid.jqxGrid('endupdate');
            });
		};
		var createContextMenu = function(grid, contextmenu, allGridMenu){
			self.addContextMenuHoverStyle(grid, contextmenu);
			grid.on('contextmenu', function () {
                return false;
            });
			if(allGridMenu == "true"){
				grid.on('mousedown', function (event) {
					var adapter = grid.jqxGrid('source');
		            var record;
		            if(adapter){
				var source = adapter._source;
			            record = adapter.records;
			            if(!record || !record.length){
							return;
						}
		            }
                    if (event.which == 3) {
                        var scrollTop = $(window).scrollTop();
                        var scrollLeft = $(window).scrollLeft();
                        contextmenu.jqxMenu('open', parseInt(event.clientX) + 5 + scrollLeft, parseInt(event.clientY) + 5 + scrollTop);
                        return false;   
                    }
                });
			}else{
				grid.on('rowclick', function (event) {
					var adapter = grid.jqxGrid('source');
		            var record;
		            if(adapter){
				var source = adapter._source;
			            record = adapter.records;
			            if(!record || !record.length){
							return;
						}
		            }

                    if (event.args.rightclick) { 
                        grid.jqxGrid('selectrow', event.args.rowindex);
                        var scrollTop = $(window).scrollTop();
                        var scrollLeft = $(window).scrollLeft();
                        contextmenu.jqxMenu('open', parseInt(event.args.originalEvent.clientX) + 5 + scrollLeft, parseInt(event.args.originalEvent.clientY) + 5 + scrollTop);
                        return false;   
                    }
                }); 
			}
		};
		var createCollapseButton = function(grid){
			var id = grid.attr('id');
			var toolbar = $('#toolbarcontainer' + id);
			var str = $('<i class="fa fa-plus-square collapse-icon" id="collapseGrid'+id+'">&nbsp;</i>');
			str.click(function(){
				var status = grid.attr("data-collapse");
				if(status == "true"){
					grid.trigger('gridClose');
				}else{
					grid.attr("data-collapse", true);
					grid.trigger('gridCollapse');
				}
			});
			toolbar.prepend(str);
		};
		var bindResize = function(grid){
			var par = grid.parent();
			var resizeAction = function(){
				var tmpWidth = par.width();
				var id = grid.attr('id');
				var toolbar = grid.find('.widget-header');
				var header = toolbar.find('h4');
				var tool = toolbar.find('.pull-right');
				// var sibar = $('#sidebar');
				// if(sibar.css("display") != "none"){
                    // if(sibar.hasClass("menu-min") != null){
                        // grid.jqxGrid({ width: par.width()});
                    // }else{
                        // grid.jqxGrid({ width: par.width()});
                    // }
                // }else{
                    // grid.jqxGrid({ width: tmpWidth });
                // }
                grid.jqxGrid({ width: tmpWidth });
                var total = header.width() + tool.width() + 50;
                if(total > tmpWidth){
			toolbar.find('span').hide();
                }
				initGridNotificationContainer(grid);
                AutoMeasureGridHeight(grid);
			};
            par.bind('resize', function() {
				resizeAction();                
            }).trigger('resize');
            $("#sidebar-collapse").on('click', function(){
            	resizeAction();  
            });
		};
		var initGridNotificationContainer = function(grid){
			var id = grid.attr('id');
			var tmpWidth = 'auto'; //grid.jqxGrid('width');
            $("#container" + id).width(tmpWidth);
            $("#jqxNotification" + id).jqxNotification({
				width: tmpWidth,
				appendContainer: "#container" + id,
				opacity: 1,
				autoClose: true,
				template: "success",
				icon: { width: 25, height: 25, url: '/aceadmin/assets/images/info.jpg'},
				appendContainer: "#container" + id
			});
		};
		var bindDoubleClick = function(grid, editColumns){
			grid.on('rowDoubleClick', function (event) {
				var id = grid.attr('id');
	            var args = event.args;
	            var row = args.rowindex;
	         	editrow = row;
	         	var offset = grid.offset();
	         	$("#popupWindow" + id).jqxWindow({ position: { x: parseInt(offset.left) + 60, y: parseInt(offset.top) + 20 } });
	         	var dataRecord = grid.jqxGrid('getrowdata', editrow);
	         	var tmp = editColumns.split(';');
	         	for(var x in tmp){
	         		$("#" + tmp[x]).val(dataRecord[tmp[x]]);
	         	}
         		$("#popupWindow" + id).jqxWindow('open');    
			});
		};
		var initPopupUpdateMultiple = function(grid){
			var id = grid.attr('id');
			var popup = $("#popupModifyWindow" + id); 
			var save = $("#SaveModify" + id);
			var cancel = $("#CancelModify" + id);
			popup.jqxWindow({
                width: 450, resizable: false,  isModal: true, autoOpen: false, cancelButton: cancel, modalOpacity: 0.7         
            });
            popup.on('open', function () {
                $("#newValueUpdate").jqxInput('selectAll');
            });
            cancel.jqxButton({ theme: theme });
            save.jqxButton({ theme: theme });
            // update the edited row when the user clicks the 'Save' button.
            save.click(function () {
                if (editrow >= 0) {
                       var selectedrowindex = grid.jqxGrid('getselectedcell').rowindex;
                        var columnvalue =  grid.jqxGrid('getselectedcell').column;
                        var value = grid.jqxGrid('getselectedcell').value;
                        var dataRecord = grid.jqxGrid('getrowdata', selectedrowindex);
                        var columnNames = grid.jqxGrid('getcolumn',  columnvalue).text;                
                    var row = { 
                        columnName: columnvalue,
                        newValue: $("#newValueUpdate" + id).val(),
                        oldValue: value 
                    };                    
                    var rowID = grid.jqxGrid('getrowid', editrow);
                    grid.jqxGrid('updaterow', rowID, row);
                    popup.jqxWindow('hide'); 
                } 
            });
		};
		var initPopupUpdateSingleRow = function(grid){
			var id = grid.attr('id');
			// initialize the popup window and buttons.
			var popup = $("#popupWindow" + id); 
			var cancel = $("#Cancel" + id);
			var save = $("#Save" + id);
            popup.jqxWindow({
                width: 450, resizable: false,  isModal: true, autoOpen: false, cancelButton: cancel, modalOpacity: 0.7        
            });
            cancel.jqxButton({ theme: theme });
            save.jqxButton({ theme: theme });
            // update the edited row when the user clicks the 'Save' button.
            save.click(function () {
                if (editrow >= 0) {
                    var rowID = $('#${id}').jqxGrid('getrowid', editrow);
                    grid.jqxGrid('updaterow', rowID, row);
                    popup.jqxWindow('hide');
                }
            });
		};
		var fixSelectAll = function(dataList, label) {
			var sourceST = {
		        localdata: dataList,
		        datatype: "array"
		    };
			var filterBoxAdapter2 = new $.jqx.dataAdapter(sourceST, {autoBind: true});
	        var uniqueRecords2 = filterBoxAdapter2.records;
			uniqueRecords2.splice(0, 0, label);
			return uniqueRecords2;
		};
		var initInput = function(config){
			
		};
		self.addContextMenuHoverStyle = function(grid, contextmenu){
			var dm = $(contextmenu);
			if(contextmenu && dm.length){
				grid.addClass('jqx-grid-context-menu');
			}
		};
		self.removeContextMenuHoverStyle = function(grid){
			grid.on("bindingcomplete", function (event) {
				grid.removeClass('jqx-grid-context-menu');
			});
		};
		self.setFilterCondition = function(grid, key, value, condition){
			grid.data('value999999' + key, value);
			var c = 'condition999999' + key;
			if(condition){
				grid.data(c, condition);
			}else{
				grid.data(c, "and");
			}
		};
		self.getFilterCondition = function(grid, key){
			var c = grid.data('condition999999'+ key);
			var data = grid.data('value999999' + key);
			var str = "|OLBIUS|"+key+"|SUIBLO|" + data +"|SUIBLO|EQUAL|SUIBLO|"+c;
			return str;
		};
		self.clearFilterCondition = function(grid, key){
			var c = grid.removeData('condition999999'+ key);
			grid.removeData('value999999' + key);
		};
		self.clearAllFilterCondition = function(grid){
			var data = grid.data();
			var all = "";
			if(data){
				for(var x in data){
					if(x && x.indexOf('value999999') != -1){
						var key = x.replace("value999999", "");
						if(key){
							self.clearFilterCondition(grid, key);
						}
					}
				}
			}
		};
		self.getAllCondition = function(grid){
			var data = grid.data();
			var all = "";
			if(data){
				for(var x in data){
					if(x && x.indexOf('value999999') != -1){
						var key = x.replace("value999999", "");
						if(key){
							var str = self.getFilterCondition(grid, key);
							all += str;
						}
					}
				}
			}
			return all;
		};
		self.getBoundRows = function(grid, key){
			var rows = grid.jqxGrid('getboundrows');
			var res = [];
			if(rows && rows.length){
				if(key){
					for(var x in rows){
						if (rows[x][key]) res.push(rows[x][key]);
					}
				}else{
					for(var x in rows){
						if (rows[x].uid) res.push(rows[x].uid);
					}
				}
			}
			return res;
		};
		self.getRowsData = function(grid){
			var rows = grid.jqxGrid('getselectedrowindexes');
			var data = [];
			for(var x in rows){
				var obj = grid.jqxGrid('getrowdata', rows[x]);
				if(obj){
					data.push(obj);
				}
			}
			return data;
		};
		self.getRowDataWithIndexes = function(grid, indexes){
			var data = [];
			for(var x in indexes){
				var obj = grid.jqxGrid('getrowdata', indexes[x]);
				if(obj){
					data.push(obj);
				}
			}
			return data;
		};
		self.selectData = function(grid, data, event){
			var args = event.args;
		    var obj = args.row;
		    var res = data;
		    var tmp = Grid.getRowsData(grid);
		    var indexes = args.rowindex;
		    self.isCheckAll(grid, event);
		    if(!obj && indexes.length){
				if(tmp.length){
					var tmpParty = [];
					for(var x in tmp){
						tmpParty.push(tmp[x].uid);
					}
					res = _.union(res, tmpParty);
				}else{
					res = [];
				}
		    }else if(obj && !_.contains(res, obj.uid)){
			if(tmp.length <= 1){
				res = [];
			}
				res.push(obj.uid);
		    }
		    return res;
		};
		self.unselectData = function(grid, data, event){
			var args = event.args;
		    var indexes = args.rowindex;
		    var obj = args.row;
		    var res = data;
		    self.isCheckAll(grid, event);
		    if(indexes == -9999){
				grid.jqxGrid('clearselection');
				res = [];
		    }else if(obj && _.contains(res, obj.uid)){
				res = _.without(res, obj.uid);
		    }
		    return res;
		};
		self.isCheckAll = function(grid, event){
			var owner = event.owner;
			var source = owner.source;
		    var indexes = args.rowindex;
		    var totalrows = parseInt(source.totalrecords);
		    if(indexes && indexes.length && indexes.length == totalrows){
			grid.data('isCheckedAll', true);
		    }else{
			grid.removeData('isCheckedAll');
		    }
		};
		self = $.extend(self, {
			initGrid : initGrid,
			initDropDownButton: initDropDownButton,
			addRow : addRow,
			updateRow : updateRow,
			deleteRow : deleteRow,
			sendRequest : sendRequest,
			processData : processData,
			renderMessage : renderMessage,
			clearDropDownButton : clearDropDownButton,
			hideGridMessage : hideGridMessage,
			sourceAdapters : sourceAdapters,
			clearForm : clearForm,
			createAddRowButton: createAddRowButton,
			createCustomControlButton : createCustomControlButton,
			triggerToolbarEvent: triggerToolbarEvent,
			createCustomAction : createCustomAction,
			createSettingButton : createSettingButton,
			createExcelExport: createExcelExport,
			createPrintButton: createPrintButton,
			createUpdateMultiRowButton : createUpdateMultiRowButton,
			createUpdateRowButton: createUpdateRowButton,
			createClearFilterButton: createClearFilterButton,
			createRefreshButton: createRefreshButton,
			createAddMultiRowButton: createAddMultiRowButton,
			createContextMenu: createContextMenu,
			createDeleteRowButton: createDeleteRowButton,
			createFilterButton: createFilterButton,
			addUpdateRowButton : addUpdateRowButton,
			createCollapseButton: createCollapseButton,
			bindResize: bindResize,
			initPopupUpdateSingleRow: initPopupUpdateSingleRow,
			initPopupUpdateMultiple: initPopupUpdateMultiple,
			initGridNotificationContainer: initGridNotificationContainer,
			openUpdateSingleRowPopup: openUpdateSingleRowPopup,
			updateGridMessage : updateGridMessage,
			getDropDownValue: getDropDownValue,
			setDropDownValue: setDropDownValue,
			cleanDropDownValue: cleanDropDownValue,
			fixSelectAll : fixSelectAll,
			$initCache : $initCache,
			getCache : getCache,
			initTooltipDropdown: initTooltipDropdown
		});
		return self;
	};
	var Grid = GridClass();
}
var Popup = (function(){
	var self = {};
	self.getHeader = function(popup){
		var header = popup.find('.jqx-window-header').find('div').first();
		return header;
	};
	self.appendHeader = function(popup, h, oldh){
		var header = self.getHeader(popup);
		try{
			if(!oldh){
				var oldh = popup.data('header');
				if(!oldh){
					oldh = header.text();
					popup.data('header', oldh);
				}
			}
			if(h){
				var cur = oldh + " - " + h;
				popup.jqxWindow('setTitle', cur);
			}else{
				popup.jqxWindow('setTitle', oldh);
			}
		}catch(e){
			oldh = popup.data('header');
			if(oldh){
				popup.jqxWindow('setTitle', oldh);
			}
		}

	};
	self.changeHeader = function(popup, h, oldh){
		var header = self.getHeader(popup);
		try{
			if(!oldh){
				var oldh = popup.data('header');
				if(!oldh){
					oldh = header.text();
					popup.data('header', oldh);
				}
			}
			if(h){
				popup.jqxWindow('setTitle', h);
			}else{
				popup.jqxWindow('setTitle', oldh);
			}
		}catch(e){
			oldh = popup.data('header');
			if(oldh){
				popup.jqxWindow('setTitle', oldh);
			}
			//console.log(e);
		}

	};
	return self;
})();
function openJqxConfigWindow(id){
    var wtmp = window;
    var tmpwidth = $('#jqxconfig' + id).jqxWindow('width');
    $("#jqxconfig" + id).jqxWindow({ position: { x: (wtmp.outerWidth - tmpwidth)/2, y: pageYOffset + 80 } });
    $('#jqxconfig' + id).jqxWindow('open');
};
function updateGridMessage(id, template, message){
    $('#container' + id).empty();
    $('#jqxNotification' + id).jqxNotification({ template: template});
    $("#notificationContent" + id).text(message);
    $("#jqxNotification" + id).jqxNotification("open");
}
function displayEditSuccessMessage(id){
    $('#container' + id).empty();
    $('#jqxNotification' + id).jqxNotification({ template: 'success'});
    $("#notificationContent" + id).text(wgupdatesuccess);
    $("#jqxNotification" + id).jqxNotification("open");
}
function displayDeleteSuccessMessage(id){
    $('#container' + id).empty();
    $('#jqxNotification' + id).jqxNotification({ template: 'success'});
    $("#notificationContent" + id).text(wgdeletesuccess);
    $("#jqxNotification" + id).jqxNotification("open");
}
function CallbackFocusFilter(){
	var previousId = sessionStorage.getItem('previousInputFilter');
	var inpt = $($("#" + previousId).find('input')).get(0);
	if(inpt) inpt.focus();
}
function AutoMeasureGridHeight(grid){
	var x = Math.abs($('#page-content').innerHeight() - $('#page-content').height());
	var tmpheight = $(window).height() - $('#nav').height() - $('.breadcrumb-inner').height() - x - 10;
	grid.jqxGrid({ height: tmpheight });
}
var formatcurrency = function(num, uom, hideSymbol, decimalNum){
    if(num == null || typeof(num) == "undefined"){
        return "";
    }
    decimalseparator = ",";
    thousandsseparator = ".";
    currencysymbol = "đ";
    if(typeof(uom) == "undefined" || uom == null){
        uom = "${defaultOrganizationPartyCurrencyUomId?if_exists}";
    }
    if(uom == "USD"){
        currencysymbol = "$";
        decimalseparator = ".";
        thousandsseparator = ",";
    }else if(uom == "EUR"){
        currencysymbol = "€";
        decimalseparator = ".";
        thousandsseparator = ",";
    }
    var str = num.toString().replace(currencysymbol, ""), parts = false, output = [], i = 1, formatted = null;
    if(str.indexOf(".") > 0) {
        parts = str.split(".");
        str = parts[0];
    }
    str = str.split("").reverse();
    var c;
    for(var j = 0, len = str.length; j < len; j++) {
        if(str[j] != ",") {
        	if(str[j] == '-'){
        		if(output && output.length > 1){
        			if(output[output.length - 1] == '.'){
        				output.splice(output.length - 1,1);
        			}
            		c = true;
            		break;
        		}
        	} 
            output.push(str[j]);
            if(i%3 == 0 && j < (len - 1)) {
            	output.push(thousandsseparator);
            }
            i++;
        }
    }
    if(c) output.push("-");
    formatted = output.reverse().join("");
    
    var decimalfraction = "";
    
	//decimalfraction = decimalseparator + parts[1].substr(0, 2);
    if (!decimalNum) decimalNum = 2;
	var dectmp = (parts) ? parts[1].substr(0, decimalNum) : "";
	var numberZero = decimalNum - dectmp.length;
	if (numberZero > 0) {
		for (var i = 0; i < numberZero; i++) {
			dectmp += "0";
		}
	}
	decimalfraction = decimalseparator + dectmp;
	
    //var returnValue = (formatted ? formatted : "0") + ((parts) ? decimalseparator + parts[1].substr(0, 2) : "");
	var returnValue = (formatted ? formatted : "0") + decimalfraction;
    if (!hideSymbol) {
    	returnValue += " " + currencysymbol;
    }
    return returnValue;
};
var formatnumber = function(num, locale, decimalNum){
	if(num == 0){
    	return "0";
    }
	if(num == null || num == ""){
        return "";
    }
    if (decimalNum == null) decimalNum = 2;
    decimalseparator = ",";
    thousandsseparator = ".";
    if(typeof(locale) == "undefined" || locale == null){
    	locale = "vi";
    }
    if(locale == "en"){
        decimalseparator = ".";
        thousandsseparator = ",";
    }
    var str = Math.abs(num).toString(), parts = false, output = [], i = 1, formatted = null;
    if(str.indexOf(".") > 0) {
        parts = str.split(".");
        str = parts[0];
    }
    str = str.split("").reverse();
    for(var j = 0, len = str.length; j < len; j++) {
        if(str[j] != ",") {
            output.push(str[j]);
            if(i%3 == 0 && j < (len - 1)) {
                output.push(thousandsseparator);
            }
            i++;
        }
    }
    
    formatted = output.reverse().join("");
    if (num < 0) formatted = "-" + formatted;
    return(formatted + ((parts) ? decimalseparator + parts[1].substr(0, decimalNum) : ""));
};
var checkDataUpdate = function(data1, data2){
    var arr = JSON.parse(JSON.stringify(data1));
    for(var x in data1){
        var obj = data1[x];
        for(var y in data2){
            var obj2 = data2[y];
            var i = 0;
            var j = 0;
            for(var z in obj2){
                if(obj[z].value && obj[z].value == obj2[z].value){
                    i++;
                }
                j++;
            }
            if(i == j){
                arr.splice(x, 1);
            }
        }
    }
    return arr;
};
var clearData = function(grid){
	var id = grid.attr('id');
	localStorage.removeItem('previousInputFilter');
    localStorage.removeItem("localGridUpdate" + id);
    localStorage.removeItem("localGridDelete" + id);
    localStorage.removeItem("localGridCreate" + id);
};

function getFieldType(datafield, fName){
    for (i=0;i < datafield.length;i++) {
       if(datafield[i]['name'] == fName){
            if(!(typeof datafield[i]['other'] === 'undefined' || datafield[i]['other'] =="")){
                return  datafield[i]['other'];
            }else{
                return  datafield[i]['type'];
            }
            
       }
    }
}