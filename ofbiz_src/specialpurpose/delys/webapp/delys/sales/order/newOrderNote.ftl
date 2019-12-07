<style type="text/css">
	#OrderNewNote label span.lbl {
		font-family: 'Open Sans';
  		font-size: 13px;
	}
	#view-more-note {
		margin-left:45%;
		margin-top:-65px;
	}
	textarea { resize: vertical; }
</style>
<h4 class="smaller green" style="display:inline-block; margin-top:25px">
	<#-- <i class="fa-file"></i> -->
	${uiLabelMap.OrderNotes} (<#if orderNotes?exists>${orderNotes?size}<#else>0</#if>)
</h4>
<div style="border: 1px solid #ddd;"><#--border: 1px solid #dcebf7;-->
	<#if orderNotes?has_content>
	    <#assign hasColor = true>
	  	<#list orderNotes as note>
	  		<#if note_index == 4>
	  			<div id="content-note-more" style="display:none">
	  		</#if>
	  		<div style="<#if hasColor>background-color:#f9f9f9; </#if>padding:5px 10px 10px"><#--background-color:#edf3f4; -->
	  			<div class="row-fluid">
	  				<div class="span11" style="min-height:0px">
	  					${note.noteInfo?replace("\n", "<br/>")}
	  				</div>
	  				<div class="span1" style="min-height:0px">
	  					<span style="color:#aaa"><#if note.internalNote == "Y"><i class="fa fa-lock"></i><#else><i class="fa fa-globe"></i></#if></span>
	  					<span class="pull-right">#${orderNotes?size - note_index}</span>
	  				</div>
	  			</div>
	  			<div class="row-fluid">
	  				<div class="span12" style="min-height:0px">
	  					<span class="pull-right">
  							<#if note.noteParty?has_content>
		          				<font style="font-weight:500">${Static["org.ofbiz.party.party.PartyHelper"].getPartyName(delegator, note.noteParty, true)}</font>
		        			</#if>
		        		 	- <#if note.noteDateTime?has_content>${Static["org.ofbiz.base.util.UtilFormatOut"].formatDateTime(note.noteDateTime, "", locale, timeZone)!}</#if>
	        		 	</span>
	  				</div>
	  			</div>
	  		</div>
	  		<#if !note_has_next && orderNotes?size &gt; 4>
	  		</div>
	  		<div>
  				<hr/>
  				<button id="view-more-note" class="btn btn-mini btn-default">${uiLabelMap.DAViewMore}</button>
  			</div>
	  		</#if>
	  		<#assign hasColor = !hasColor>
	  		<#--
	  		<td align="right" valign="top" width="20%">
	            <#if note.internalNote?if_exists == "N">
	                ${uiLabelMap.OrderPrintableNote}
	                <form name="privateNotesForm_${note_index}" method="post" action="<@ofbizUrl>updateOrderNote</@ofbizUrl>">
	                  	<input type="hidden" name="orderId" value="${orderId}"/>
	                  	<input type="hidden" name="noteId" value="${note.noteId}"/>
	                  	<input type="hidden" name="internalNote" value="Y"/>
	                  	<a href="javascript:document.privateNotesForm_${note_index}.submit()" class="btn btn-mini btn-primary">${uiLabelMap.DANotesPrivate}</a>
	                </form>
	            </#if>
	    		<#if note.internalNote?if_exists == "Y">
	                ${uiLabelMap.OrderNotPrintableNote}
	                <form name="publicNotesForm_${note_index}" method="post" action="<@ofbizUrl>updateOrderNote</@ofbizUrl>">
	                  	<input type="hidden" name="orderId" value="${orderId}"/>
	                  	<input type="hidden" name="noteId" value="${note.noteId}"/>
	                  	<input type="hidden" name="internalNote" value="N"/>
	                  	<a href="javascript:document.publicNotesForm_${note_index}.submit()" class="btn btn-mini btn-primary">${uiLabelMap.DANotesPublic}</a>
	                </form>
	    		</#if>
	  		</td>
	  		-->
	    	<#--<#if note_has_next>
	      		<tr><td colspan="3"><hr/></td></tr>
			</#if>-->
	  	</#list>
	<#else>
	  	<div><p style="color:#3a87ad;padding:5px 10px">${uiLabelMap.DAHaveNotNotesYet}.</p></div>
	</#if>
	<#if security.hasEntityPermission("ORDERMGR", "_NOTE", session)>
		<div style="background-color:#ddd;padding:5px 10px;border-top: 1px solid #f7fbff;"><#--background-color:#edf3f4-->
			<form name="OrderNewNote" id="OrderNewNote" action="<@ofbizUrl>createOrderNoteAjax</@ofbizUrl>" method="post" class="form-horizontal basic-custom-form form-size-mini" style="padding-top:0">
			    <input type="hidden" name="orderId" value="${orderId?if_exists}"/>
			    <div class="row-fluid">
			    	<div class="span12">
						<textarea name="note" id="noteArea" rows="2" style="width: calc(97% - 120px); margin-top:0; display:inline-block;vertical-align: top;"></textarea>
			    		<div style="width:100px;display:inline-block;font-family: 'Open Sans';font-size: 13px;margin-left:10px">
			    			<div style="height:35px">
			    				<#if isPrivateOrg?if_exists && isPrivateOrg>
				    				<label>
										<input name="internalNote" type="radio" value="Y" checked="checked"/>
										<span class="lbl"> ${uiLabelMap.DAInternal}</span>
									</label>
					
									<label>
										<input name="internalNote" type="radio" value="N"/>
										<span class="lbl"> ${uiLabelMap.DAPublic}</span>
									</label>
								<#else>
									<label>
										<input name="internalNote" type="radio" value="N" checked="checked"/>
										<span class="lbl"> ${uiLabelMap.DAPublic}</span>
									</label>
								</#if>
			    			</div>
			    		</div>
			    	</div>
			    	<div class="span12">
			    		<div style="width: calc(97% - 120px); margin-top:0; display:inline-block; margin-top:5px">
			    			<button id="createOrderNote" type="button" class="btn btn-mini btn-primary pull-right" style="height: 24px;margin-top: 1px; margin-left:5px; width:75px">
			    				${uiLabelMap.DASend}
							</button>
			    		</div>
			    	</div>
			    </div><!--.row-fluid-->
			</form>
		</div>
		<#--<a class="btn btn-primary btn-mini margin-bottom8" href="<@ofbizUrl>createNewNote?${paramString}</@ofbizUrl>">${uiLabelMap.OrderNotesCreateNew}</a>-->
		
		<script type="text/javascript">
			$(function() {
				$("#view-more-note").on("click", function() {
					if ($("#content-note-more").hasClass("active")) {
						$("#view-more-note").text("${StringUtil.wrapString(uiLabelMap.DAViewMore)}");
						$("#content-note-more").removeClass("active");
						$("#content-note-more").hide("blind", 500);
					} else {
						$("#view-more-note").text("${StringUtil.wrapString(uiLabelMap.DAViewLess)}");
						$("#content-note-more").addClass("active");
						$("#content-note-more").show("blind", 500);
					}
				});
				$("#createOrderNote").on("click", function() {
					var note = $("#noteArea").val();
					if (note && !(/^\s*$/.test(note))) {
						var data = $("#OrderNewNote").serialize();
						$.ajax({
							type: "POST", 
							url: "createOrderNoteAjax",
							data: data, 
							beforeSend: function () {
								$("#info_loader").show();
							}, 
							success: function (data) {
								$("#order-notes-container").html(data);
								//$('#container').empty();
							}, 
							error: function() {
								//commit(false);
							},
							complete: function() {
						        $("#info_loader").hide();
						    }
						});
					} else {
						bootbox.dialog("${uiLabelMap.DANoteContentCannotEmpty}!", [{
							"label" : "OK",
							"class" : "btn-small btn-primary",
							}]
						);
						return false;
					}
				});
			})
		</script>
	</#if>
</div>