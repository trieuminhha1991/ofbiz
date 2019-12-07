var recruitmenRoundListObj = (function(){
	var _recruitmentPlanId = "";
	var init = function(){
		initJqxSplitter();
		initJqxListBox();
		initJqxGrid();
		initNotification();
		initJqxWindow();
		create_spinner($("#spinnerListCandidateRound"));
	};
	
	var initNotification = function(){
		$("#ntfRecruitRoundCandidateGrid").jqxNotification({
	        width: "100%", position: "top-left", opacity: 1, appendContainer: "#appendNtfrecruitRoundCandidateGrid",
	        autoOpen: false, autoClose: true
	    });
	};
	
	var initJqxSplitter = function(){
		$('#roundMainSplitter').jqxSplitter({ width: 960, height: 530, panels: [{ size: 180}], splitBarSize: 3});
		$('#roundMainSplitter').on('expanded', function (event) {  
			if(globalVar.hasPermissionAdmin){
				setTimeout(function(){ 
						recruitListRoundContextMenuObj.attachContextMenu();//recruitListRoundContextMenuObj is defined in RecruitmentListRoundContextMenu.js
					}
				, 300);
			}
		});
		
		$('#roundMainSplitter').on('collapsed', function (event) {       
			//detachContextMenu();
		});
	};
	
	var initJqxListBox = function(){
		var source = {
			datatype: "json",
			datafields: [
                { name: 'roundOrder' },
                { name: 'roundName' },
                {name: 'recruitmentPlanId'},
                {name: 'enumRoundTypeId'}
            ],
            id: 'id',
            url: "",
            root: "listReturn",
            
        };
        var dataAdapter = new $.jqx.dataAdapter(source,{
        	beforeSend: function (xhr) {
    			$("#loadingListCandidateRound").show();
    		},
    		loadComplete: function (records){
    			$("#loadingListCandidateRound").hide();
    			$("#listBoxRoundRec").jqxListBox('selectIndex', 0);
    		},
    		beforeLoadComplete: function (records) {
    			
            }
        });
		$("#listBoxRoundRec").jqxListBox({ source: dataAdapter, displayMember: "roundName", valueMember: "roundOrder", 
			width: '100%', height: '99.5%', itemHeight: 30});
		$("#listBoxRoundRec").on('select', function(event){
			var args = event.args;
		    if (args) {
		        var value = args.item.value;
		        refreshGrid(_recruitmentPlanId, value);
		        if(globalVar.hasPermissionAdmin){
		        	wizardEditCandidate.setRoundOrder(value);//wizardEditCandidate is defined in RecruitmentCreateCandidate.js
		        }
		    }
		});
		$("#listBoxRoundRec").on('bindingComplete', function (event) {
			if(globalVar.hasPermissionAdmin){
				recruitListRoundContextMenuObj.attachContextMenu();//recruitListRoundContextMenuObj is defined in RecruitmentListRoundContextMenu.js
			}
		});
		
		$("#listBoxRoundRec").on('contextmenu', function(e) {
			return false;
	    });
	};
	
    var initJqxGrid = function(){
    	var datafield = candidateRecUtilObj.getDataFieldCandidateGrid();//candidateRecUtilObj is denfined in RecruitListCandidates.js
		var columns = candidateRecUtilObj.getColumnsCandidateGrid();
		var grid = $("#recruitRoundCandidateGrid");
		var rendertoolbar = function (toolbar){
			toolbar.html("");
			var id = "recruitRoundCandidateGrid";
			var me = this;
			var jqxheader = $("<div id='toolbarcontainer" + id +"' class='widget-header'><h5>" + uiLabelMap.RecruitmentCandidatesList + "</h5><div id='toolbarButtonContainer" + id + "' class='pull-right'></div></div>");
			toolbar.append(jqxheader);
	     	var container = $('#toolbarButtonContainer' + id);
	        var maincontainer = $("#toolbarcontainer" + id);
	        if(globalVar.hasPermissionAdmin){
        		Grid.createAddRowButton(
        				grid, container, uiLabelMap.CommonAddNew, {
        					type: "popup",
        					container: $("#addRecruitCandidateWindow"),
        				}
        		);
	        }
	        Grid.createFilterButton(grid, container, uiLabelMap.accRemoveFilter);
		};
		var config = {
				url: '',
				rendertoolbar : rendertoolbar,
				showtoolbar : true,
				width : '100%',
				virtualmode: true,
				editable: false,
				filterable: true,
				localization: getLocalization(),
		};
		Grid.initGrid(config, datafield, columns, null, grid);
		if(globalVar.hasPermissionAdmin){
			Grid.createContextMenu(grid, $("#contextMenuRoundRecParty"), false);
		}
    };
    
    var refreshGrid = function(recruitmentPlanId, roundOrder){
    	refreshBeforeReloadGrid($("#recruitRoundCandidateGrid"));
		var tempS = $("#recruitRoundCandidateGrid").jqxGrid('source');
		tempS._source.url = "jqxGeneralServicer?sname=JQGetListCandidateInRecruitRound&recruitmentPlanId=" + recruitmentPlanId + "&roundOrder=" + roundOrder;
		$("#recruitRoundCandidateGrid").jqxGrid('source', tempS);
		recruitmentCandidateListObj.setRecruitmentPlanId(recruitmentPlanId); //recruitmentCandidateListObj is defined in RecruitListCandidates.js
    };
	
	var initJqxWindow = function(){
		createJqxWindow($("#recruitmentRoundWindow"), 980, 580);
		$("#recruitmentRoundWindow").on('open', function(event){
			if(globalVar.hasPermissionAdmin){
				wizardEditCandidate.setFunctionAfterCreateCandidate(functionAfterCreateCandidate);//wizardEditCandidate is defined in RecruitmentCreateCandidate.js
			}
		});
		$("#recruitmentRoundWindow").on('close', function(event){
			if(globalVar.hasPermissionAdmin){
				wizardEditCandidate.setRoundOrder(null);//wizardEditCandidate is defined in RecruitmentCreateCandidate.js
			}
		});
	};
	
	var functionAfterCreateCandidate = function(message){
		$("#ntfRecruitRoundCandidateGrid").jqxNotification('closeLast');
		$("#ntfTextRecruitRoundCandidateGrid").text(message);
		$("#ntfRecruitRoundCandidateGrid").jqxNotification({ template: 'info' });
		$("#ntfRecruitRoundCandidateGrid").jqxNotification('open');
		$("#recruitRoundCandidateGrid").jqxGrid('updatebounddata');
	};
	
	var setRecruitmentPlanId = function(recruitmentPlanId){
		_recruitmentPlanId = recruitmentPlanId;
		var source = $("#listBoxRoundRec").jqxListBox('source');
		source._source.url = "getListRecruitmentRound?recruitmentPlanId=" + recruitmentPlanId; 
		$("#listBoxRoundRec").jqxListBox('source', source);
		/*$("#listBoxRoundRec").jqxListBox('refresh');
		$("#listBoxRoundRec").jqxListBox('selectIndex', 0);*/
	};
	
	var getRecruitmentPlanId = function(){
		return _recruitmentPlanId;
	};
	
	var openWindow = function(){
		openJqxWindow($("#recruitmentRoundWindow"));
	}
	return{
		init: init,
		openWindow: openWindow,
		setRecruitmentPlanId: setRecruitmentPlanId,
		getRecruitmentPlanId: getRecruitmentPlanId
	}
}());


$(document).ready(function(){
	recruitmenRoundListObj.init();
});