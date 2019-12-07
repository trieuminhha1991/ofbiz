/*
 jQWidgets v4.0.0 (2016-Jan)
 Copyright (c) 2011-2016 jQWidgets.
 License: http://jqwidgets.com/license/
 */

(function(i) {
	i.extend(i.jqx.dataAdapter.prototype, {
		loadjson : function(ae, af, R) {
			if ( typeof (ae) == "string") {
				ae = i.parseJSON(ae)
			}
			if (R.root == undefined) {
				R.root = ""
			}
			if (R.record == undefined) {
				R.record = ""
			}
			var ae = ae || af;
			if (!ae) {
				ae = []
			}
			var ad = this;
			if (R.root != "") {
				var K = R.root.split(ad.mapChar);
				if (K.length > 1) {
					var aa = ae;
					for (var Q = 0; Q < K.length; Q++) {
						if (aa != undefined) {
							aa = aa[K[Q]]
						}
					}
					ae = aa
				} else {
					if (ae[R.root] !== undefined) {
						ae = ae[R.root]
					} else {
						if (ae[0] && ae[0][R.root] !== undefined) {
							ae = ae[0][R.root]
						} else {
							i.each(ae, function(ah) {
								var ag = this;
								if (this == R.root) {
									ae = this;
									return false
								} else {
									if (this[R.root] != undefined) {
										ae = this[R.root]
									}
								}
							})
						}
					}
					if (!ae) {
						var K = R.root.split(ad.mapChar);
						if (K.length > 0) {
							var aa = ae;
							for (var Q = 0; Q < K.length; Q++) {
								if (aa != undefined) {
									aa = aa[K[Q]]
								}
							}
							ae = aa
						}
					}
				}
			} else {
				if (!ae.length) {
					for (obj in ae) {
						if (i.isArray(ae[obj])) {
							ae = ae[obj];
							break
						}
					}
				}
			}
			if (ae != null && ae.length == undefined) {
				ae = i.makeArray(ae)
			}
			//olbius fix bug convert json to value dmm grid
			if (!ae || ae.length == undefined) {
				throw new Error("jqxDataAdapter: JSON Parse error! Invalid JSON. Please, check your JSON or your jqxDataAdapter initialization!");
				return
			}
			if (ae.length == 0) {
				this.totalrecords = 0;
				return
			}
			var J = ae.length;
			this.totalrecords = this.virtualmode ? (R.totalrecords || J) : J;
			this.records = new Array();
			this.originaldata = new Array();
			var W = this.records;
			var T = !this.pageable ? R.recordstartindex : this.pagesize * this.pagenum;
			this.recordids = new Array();
			if (R.loadallrecords) {
				T = 0;
				J = this.totalrecords
			}
			var P = 0;
			if (this.virtualmode) {
				T = !this.pageable ? R.recordstartindex : this.pagesize * this.pagenum;
				P = T;
				T = 0;
				J = this.totalrecords
			}
			var Y = R.datafields ? R.datafields.length : 0;
			if (Y == 0) {
				var e = ae[0];
				var ab = new Array();
				for (obj in e) {
					var H = obj;
					ab[ab.length] = {
						name : H
					}
				}
				R.datafields = ab;
				R.generatedfields = R.datafields;
				Y = ab.length
			}
			var M = T;
			for (var V = T; V < J; V++) {
				var I = ae[V];
				if (I == undefined) {
					break
				}
				if (R.record && R.record != "") {
					I = I[R.record];
					if (I == undefined) {
						continue
					}
				}
				var ac = this.getid(R.id, I, V);
				if ( typeof (ac) === "object") {
					ac = V
				}
				if (!this.recordids[ac]) {
					this.recordids[ac] = I;
					var L = {};
					for (var U = 0; U < Y; U++) {
						var N = R.datafields[U];
						var S = "";
						if (undefined == N || N == null) {
							continue
						}
						if (N.map) {
							if (i.isFunction(N.map)) {
								S = N.map(I)
							} else {
								var K = N.map.split(ad.mapChar);
								if (K.length > 0) {
									var Z = I;
									for (var Q = 0; Q < K.length; Q++) {
										if (Z != undefined) {
											Z = Z[K[Q]]
										}
									}
									S = Z
								} else {
									S = I[N.map]
								}
							}
							if (S != undefined && S != null) {
								S = this.getvaluebytype(S, N)
							} else {
								if (S == undefined && S != null) {
									S = ""
								}
							}
						}
						if (S == "" && !N.map) {
							S = I[N.name];
							if (S == undefined && S != null) {
								S = ""
							}
							if (N.value != undefined) {
								if (S != undefined) {
									var X = S[N.value];
									if (X != undefined) {
										S = X
									}
								}
							}
						}
						S = this.getvaluebytype(S, N);
						if (N.displayname != undefined) {
							L[N.displayname] = S
						} else {
							L[N.name] = S
						}
						if (N.type === "array") {
							var O = function(aj) {
								if (!aj) {
									return
								}
								for (var ap = 0; ap < aj.length; ap++) {
									var am = aj[ap];
									if (!am) {
										continue
									}
									for (var an = 0; an < Y; an++) {
										var ai = R.datafields[an];
										var ao = "";
										if (undefined == ai || ai == null) {
											continue
										}
										if (ai.map) {
											if (i.isFunction(ai.map)) {
												ao = ai.map(am)
											} else {
												var ag = ai.map.split(ad.mapChar);
												if (ag.length > 0) {
													var al = am;
													for (var ah = 0; ah < ag.length; ah++) {
														if (al != undefined) {
															al = al[ag[ah]]
														}
													}
													ao = al
												} else {
													ao = am[ai.map]
												}
											}
											if (ao != undefined && ao != null) {
												ao = this.getvaluebytype(ao, ai)
											} else {
												if (ao == undefined && ao != null) {
													ao = ""
												}
											}
										}
										if (ao == "" && !ai.map) {
											ao = am[ai.name];
											if (ao == undefined && ao != null) {
												ao = ""
											}
											if (ai.value != undefined) {
												if (ao != undefined) {
													var ak = ao[ai.value];
													if (ak != undefined) {
														ao = ak
													}
												}
											}
										}
										ao = this.getvaluebytype(ao, ai);
										if (ai.displayname != undefined) {
											am[ai.displayname] = ao
										} else {
											am[ai.name] = ao
										}
										if (ai.type === "array") {
											O.call(this, ao)
										}
									}
								}
							};
							O.call(this, S)
						}
					}
					if (R.recordendindex <= 0 || T < R.recordendindex) {
						W[P + M] = i.extend({}, L);
						W[P + M].uid = ac;
						this.originaldata[P + M] = i.extend({}, W[V]);
						M++
					}
				}
			}
			this.records = W;
			this.cachedrecords = this.records
		},
	});
})(jqxBaseFramework);
