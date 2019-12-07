<#include "component://basecrm/webapp/basecrm/callcenter/ftLlibraryVariable.ftl"/>
<@jqGridMinimumLib/>
<@jqOlbCoreLib hasDropDownButton=true hasGrid=true hasValidator=true/>

<#assign dataField="[
				{name: 'opponentEventId', type: 'string'},
				{name: 'partyId', type: 'string'},
				{name: 'groupName', type: 'string'},
				{name: 'comment', type: 'string'},
				{name: 'description', type: 'string'},
                {name: 'image', type: 'string'}
			]"/>
<#assign columnlist = "
                { text: '${StringUtil.wrapString(uiLabelMap.BSOpponentId)}', datafield: 'opponentEventId', width: '10%', filterable: true, pinned: true,
                	cellsrenderer: function(row, column, value, a, b, data){
                		var link = 'opponentInfo?opponentEventId=' + data.opponentEventId + '&partyId=' + data.partyId;
                		return '<div style=\"margin:4px\"><a href=\"' + link + '\">' + value + '</a></div>';
                	}
                },
                {text: '${StringUtil.wrapString(uiLabelMap.BSOpponentType)}', datafield: 'groupName', width: '20%', sortable: false},
                {text: '${StringUtil.wrapString(uiLabelMap.BSOPComment)}', datafield: 'comment', width: '25%', sortable: false},
                {text: '${StringUtil.wrapString(uiLabelMap.BSDescription)}', datafield: 'description', width: '20%', sortable: false},
                {text: '${StringUtil.wrapString(uiLabelMap.BSImage)}', datafield: 'image', width: '25%', sortable: false},
			"/>
<@jqGrid id="jqxgridListOpponentInfo" url="jqxGeneralServicer?sname=JQGetListOpponentInfo" dataField=dataField columnlist=columnlist filterable="true" clearfilteringbutton="true"
		 showtoolbar="true" filtersimplemode="true" addrow="true" addType="popup" alternativeAddPopup="alterpopupWindow"/>

<div id="alterpopupWindow" style="display:none;">
    <div>${uiLabelMap.BSAddNewOpponent}</div>
    <div style="overflow: hidden;">
		<div class='row-fluid form-window-content'>
			<form id="formAdd">
    			<div class='row-fluid margin-bottom10'>
    				<div class='span4 align-right asterisk'>
    					${uiLabelMap.BSOpponentType}
    				</div>
    				<div class='span8'>
    				    <div class="container-add-plus">
                            <div id="partyId">
                                <div id="partyIdGrid"></div>
                            </div>
                            <a id="quickAddNewTypeOpponent" tabindex="-1" href="javascript:void(0);" class="add-value"><i class="fa fa-plus"></i></a>
                        </div>
    				</div>
				</div>
    			<div class='row-fluid margin-bottom10'>
    				<div class='span4 align-right'>
    					${uiLabelMap.BSComment}
    				</div>
    				<div class='span8'>
    					<input type="text" id="comment" value="" style="width: 80%;"/>
    				</div>
				</div>
    			<div class='row-fluid margin-bottom10'>
    				<div class='span4 align-right'>
    					${uiLabelMap.BSDescription}
    				</div>
    				<div class='span8'>
    					<input type="text" id="description" value="" style="width: 80%;"/>
    				</div>
				</div>
    			<div class='row-fluid margin-bottom10'>
    				<div class='span4 align-right'>
    					${uiLabelMap.BSImage}
    				</div>
    				<div class='span8'>
    					<div class="logo-company" style="width: 85%;">
    					    <input type="file" id="logoImageUrl" style="visibility:hidden;" accept="image/*"/>
    					    <img src="/salesmtlresources/logo/LOGO_demo.png" id="logoImage"/>
    					</div>
    				</div>
				</div>
			</form>
		</div>
		<div class="form-action">
			<div class='row-fluid'>
				<div class="span12 margin-top10">
					<button id="cancel" class='btn btn-danger form-action-button pull-right'><i class='fa-remove'></i> ${uiLabelMap.Cancel}</button>
					<button id="save" class='btn btn-primary form-action-button pull-right'><i class='fa-check'></i> ${uiLabelMap.Save}</button>
				</div>
			</div>
		</div>
	</div>
</div>

<script type="text/javascript">
    $.jqx.theme = 'olbius';
	theme = $.jqx.theme;
	var action = (function(){
	    var opponentDDB;
	    var initElement = function(){
            var configTypeOpponent = {
                useUrl: true,
                root: 'results',
                widthButton: '83%',
                showdefaultloadelement: false,
                autoshowloadelement: false,
                datafields: [{name: 'partyId', type: 'string'}, {name: 'partyCode', type: 'string'}, {name: 'groupName', type: 'string'}],
                columns: [
                    {text: "${uiLabelMap.BSOpponentId}", datafield: 'partyCode', width: '40%'},
                    {text: "${uiLabelMap.BSOpponentType}", datafield: 'groupName', width: '60%'},
                ],
                url: "getPartyTypeOpponent",
                useUtilFunc: true,
                key: 'partyId',
                keyCode: 'partyCode',
                description: ['groupName'],
                autoCloseDropDown: true,
                filterable: true,
                sortable: true,
                showfilterrow: true,
            };
            opponentDDB = new OlbDropDownButton($("#partyId"), $("#partyIdGrid"), null, configTypeOpponent, []);
            initjqxWindow();
	    }

        var initjqxWindow = function(){
			$("#alterpopupWindow").jqxWindow({
		        width: 530,height :440,  resizable: false,  isModal: true, autoOpen: false, cancelButton: $("#cancel"), modalOpacity: 0.7, theme: theme
		    });
		}

		var initRules = function(){
            $('#formAdd').jqxValidator({
				rules : [
                    {input : '#partyId',message : '${StringUtil.wrapString(uiLabelMap.BSFieldRequired?default(''))}',action : 'change,close',rule : function(input){
                    	var val = opponentDDB.getValue();
                    	if(!val) return false;
                    	return true;
                    }}
				]
			})
		}

		var save = function(){
            var partyId = opponentDDB.getValue();
            var logoImageUrl;
            if ($('#logoImageUrl').prop('files')[0]) {
                logoImageUrl = DataAccess.uploadFile($('#logoImageUrl').prop('files')[0]);
            }else{
                logoImageUrl = "";
            }
			if(!$('#formAdd').jqxValidator('validate')){return;};
            $.ajax({
                    type: 'POST',
                    url: "submitInfoOpponentSetting",
                    data: {
                        partyId:partyId,
                        comment:$("#comment").val(),
                        description: $("#description").val(),
                        image: logoImageUrl
                    },
                    beforeSend: function(){
                        $("#loader_page_common").show();
                    },
                    success: function(data){
                        $("#jqxgridListOpponentInfo").jqxGrid("updatebounddata");
                    },
                    error: function(data){
                        alert("Send request is error");
                    },
                    complete: function(data){
                        $("#loader_page_common").hide();
                    },
            });
            return true;
		}

		var clear = function(){
			opponentDDB.clearAll();
			$("#comment").val("");
			$("#description").val("");
		};

		var bindEvent = function(){
		    $("#quickAddNewTypeOpponent").on("click", function(){
		    	if (typeof(OlbPartyTypeOpponentNew) != "undefined") {
		    		OlbPartyTypeOpponentNew.openWindow();
		    	}
		    });
			$("#logoImage").click(function() {
				$("#logoImageUrl").click();
			});
			$("#logoImageUrl").change(function(){
				Images.readURL(this, $("#logoImage"));
			});
			$("#save").click(function () {
		    	if(save())  $("#alterpopupWindow").jqxWindow('close');
		    });
		    $("#alterpopupWindow").on('open',function(){
                opponentDDB.getGrid().updateBoundData()
            });
		    $("#alterpopupWindow").on('close',function(){
		        $("#logoImage").attr("src","/salesmtlresources/logo/LOGO_demo.png");
		    	clear();
		    });
		}

		return {
			init : function(){
				initElement();
				bindEvent();
				initRules();
			}
		}
	}())

	if (typeof (Images) == "undefined") {
		var Images = (function() {
			var readURL = function(input, img) {
				if (input.files && input.files[0]) {
			        var reader = new FileReader();
			        reader.onload = function (e) {
			            img.attr("src", e.target.result);
			        }
			        reader.readAsDataURL(input.files[0]);
			    }
			};
			return {
				readURL: readURL,
			};
		})();
	}


	$(document).ready(function(){
        action.init();
    });
</script>

<div id="alterPopupPartyTypeOpponentNew" style="display:none">
	<div>${uiLabelMap.BSAddNewOpponentType}</div>
	<div class="form-window-container">
		<div class="form-window-content">
			<div class="row-fluid form-window-content-custom">
				<div class="row-fluid">
					<div class="span4">
						<label class="required">${uiLabelMap.BSOpponentType}</label>
					</div>
					<div class="span8">
						<input type="text" id="wn_partyCode" maxlength="20" value=""/>
			   		</div>
				</div>
				<div class="row-fluid">
					<div class="span4">
						<label class="required">${uiLabelMap.BSOpponentName}</label>
					</div>
					<div class="span8">
						<input type="text" id="wn_partyName" maxlength="20" value=""/>
			   		</div>
				</div>
			</div>
		</div>
		<div class="form-action">
			<div class="pull-right form-window-content-custom">
				<button id="wn_alterSave" class="btn btn-primary form-action-button"><i class="fa-check"></i> ${uiLabelMap.CommonSave}</button>
				<button id="wn_alterCancel" class="btn btn-danger form-action-button"><i class="fa-remove"></i> ${uiLabelMap.CommonCancel}</button>
			</div>
		</div>
	</div>
</div>

<script type="text/javascript">
	$(function(){
		OlbPartyTypeOpponentNew.init();
	});

	var OlbPartyTypeOpponentNew = (function(){
		var validatorVAL;

		var init = function(){
			initElement();
			initValidateForm();
			initEvent();
		};
		var initElement = function(){
			jOlbUtil.windowPopup.create($("#alterPopupPartyTypeOpponentNew"), {width: 450, height: 200, cancelButton: $("#wn_alterCancel")});

			jOlbUtil.input.create($("#wn_partyCode"));
			jOlbUtil.input.create($("#wn_partyName"));
		};

		var saveOpponentType = function(){
            $.ajax({
                    type: 'POST',
                    url: "submitInfoOpponentType",
                    data: {
                        partyCode: $("#wn_partyCode").val(),
                        partyName: $("#wn_partyName").val(),
                    },
                    beforeSend: function(){
                        $("#loader_page_common").show();
                    },
                    success: function(data){
                        $("#jqxgridListOpponentInfo").jqxGrid("updatebounddata");
                    },
                    error: function(data){
                        alert("Send request is error");
                    },
                    complete: function(data){
                        $("#loader_page_common").hide();
                    },
            });
            return true;
		}

		var initEvent = function(){
			$("#wn_alterSave").on("click", function(){
				if (!validatorVAL.validate()) return false;
				if(saveOpponentType())
				    $("#alterPopupPartyTypeOpponentNew").jqxWindow("close");
			});

			$("#alterPopupPartyTypeOpponentNew").on("close", function(){
				$("#wn_partyCode").val("");
				$("#wn_partyName").val("");
				opponentDDB.getGrid().updateBoundData()
			});
			$("#alterPopupPartyTypeOpponentNew").on("open", function(){
				$("#wn_partyCode").focus();
			});
		};
		var initValidateForm = function(){
			var mapRules = [
				{input: "#wn_partyCode", type: "validInputNotNull"},
				{input: "#wn_partyName", type: "validInputNotNull"},
			];
			validatorVAL = new OlbValidator($("#alterPopupPartyTypeOpponentNew"), mapRules);
		};
		var openWindow= function(){
			$("#alterPopupPartyTypeOpponentNew").jqxWindow("open");
		};
		var closeWindow= function(){
			$("#alterPopupPartyTypeOpponentNew").jqxWindow("close");
		};
		var getValidator = function(){
			return validatorVAL;
		};
		var getValue = function(){
			var rowDataNew = {
				"partyCode": $("#wn_partyCode").val(),
				"partyName": $("#wn_partyName").val(),
			};
			return rowDataNew;
		};
		return {
			init: init,
			openWindow: openWindow,
			closeWindow: closeWindow,
			getValidator: getValidator,
			getValue: getValue,
		}
	}());
</script>