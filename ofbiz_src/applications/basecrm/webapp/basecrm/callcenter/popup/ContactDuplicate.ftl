<div id="jqxwindowContactInformation" style="display:none;">
	<div>${uiLabelMap.ContactInformation}</div>
	<div style="overflow-x: hidden;">
		<div class="margin-bottom10"><label style="display: inline;">${uiLabelMap.RegisteredPhoneNumber}</label>&nbsp;&nbsp;<label class="green" id="phoneDuplicate" style="display: inline;"></label></div>
		<div class="row-fluid">
			<div id="jqxGridContactInformation"></div>
		</div>
		<hr style="margin: 10px 0px 0px 0px; border-top: 1px solid grey;">
		<div class="row-fluid">
			<div class="span12 margin-top10">
				<button id='cancelContactInformation' class="btn btn-danger form-action-button pull-right"><i class='icon-remove'></i>${uiLabelMap.CommonClose}</button>
				<button id='ForceSave' class="btn btn-primary form-action-button pull-right"><i class='fa-check'></i>${uiLabelMap.ForceSave}</button>
			</div>
		</div>
	</div>
</div>

<script>
	$(document).ready(function() {
		ContactDuplicate.init();
	});
	if (typeof (ContactDuplicate) == "undefined") {
		var ContactDuplicate = (function() {
			var initJqxElements = function() {
				$("#jqxwindowContactInformation").jqxWindow({ theme: 'olbius',
				    width: 950, height: 300, resizable: false,  isModal: true, autoOpen: false, cancelButton: $("#cancelContactInformation"), modalOpacity: 0.7
				});
			};
			var renderGrid = function(grid, data) {
				var source =
			    {
					localdata: data,
	                datatype: "local",
			        datafields: [
								{ name: 'partyId', type: 'string'},
								{ name: 'partyFullName', type: 'string'},
								{ name: 'gender', type: 'string'},
								{ name: 'birthDate', type: 'date', other: 'date'},
								{ name: 'idNumber', type: 'string'},
								{ name: 'phoneHome', type: 'string'},
								{ name: 'phoneWork', type: 'string'},
								{ name: 'phoneMobile', type: 'string'},
								{ name: 'emailAddress', type: 'string'},
								{ name: 'familyId', type: 'string'},
								{ name: 'familyName', type: 'string'},
								{ name: 'address', type: 'string'}
							]
			    };
			    var dataAdapter = new $.jqx.dataAdapter(source);
			    grid.jqxGrid({
			        width: '100%',
			        localization: getLocalization(),
			        source: dataAdapter,
			        columnsresize: true,
			        pageable: true,
	                autoheight: false,
	                height: 150,
	                autorowheight: true,
			        columns: [
							{ text: '${StringUtil.wrapString(uiLabelMap.DmsPartyId)}', datafield: 'partyId', width: 150 },
							{ text: '${StringUtil.wrapString(uiLabelMap.DmsFirstName)}' , datafield: 'partyFullName', width: 200 },
							{ text: '${StringUtil.wrapString(uiLabelMap.DmsAddress)}', datafield: 'address', width: 250},
							{ text: '${StringUtil.wrapString(uiLabelMap.Phone1)}', datafield: 'phoneHome', width: 200},
							{ text: '${StringUtil.wrapString(uiLabelMap.Phone2)}', datafield: 'phoneWork', width: 200},
							{ text: '${StringUtil.wrapString(uiLabelMap.Phone3)}', datafield: 'phoneMobile', width: 200},
							{ text: '${StringUtil.wrapString(uiLabelMap.DmsIdentification)}', datafield: 'idNumber', width: 200},
							{ text: '${StringUtil.wrapString(uiLabelMap.DmsEmail)}', datafield: 'emailAddress', width: 200}
					]
			    });
			}
			var bindEvents = function() {
				$("#ForceSave").click(function() {
					Processor.saveCustomer(true);
					$("#jqxwindowContactInformation").jqxWindow('close');
				});
			};
			var setPhoneDuplicate = function(text) {
				$("#phoneDuplicate").text(text);
			};
			var open = function() {
				var wtmp = window;
		    	var tmpwidth = $('#jqxwindowContactInformation').jqxWindow('width');
		        $("#jqxwindowContactInformation").jqxWindow({ position: { x: (wtmp.outerWidth - tmpwidth)/2, y: pageYOffset + 120 }});
		    	$("#jqxwindowContactInformation").jqxWindow('open');
			}
			return {
				init: function() {
					initJqxElements();
					bindEvents();
				},
				renderGrid: renderGrid,
				open: open,
				setPhoneDuplicate: setPhoneDuplicate
			}
		})();
	}
</script>