<#if orderHeader?has_content>
<div id="notes-tab" class="tab-pane">
	<h4 class="smaller lighter green" style="display:inline-block">
		<#-- <i class="fa-file"></i> -->
		${uiLabelMap.OrderNotes}
	</h4>
	<div>
     	<#if security.hasEntityPermission("ORDERMGR", "_NOTE", session)>
      		<a class="btn btn-primary btn-mini margin-bottom8" href="<@ofbizUrl>createNewNote?${paramString}</@ofbizUrl>">${uiLabelMap.OrderNotesCreateNew}</a>
	  	</#if>
    	<#if orderNotes?has_content>
            <table class="table table-striped table-bordered table-hover" cellspacing='0'>
              	<#list orderNotes as note>
                	<tr>
                  		<td valign="top" width="30%">
                    		<#if note.noteParty?has_content>
                      			<div>&nbsp;${uiLabelMap.CommonBy}:&nbsp;${Static["org.ofbiz.party.party.PartyHelper"].getPartyName(delegator, note.noteParty, true)}</div>
                    		</#if>
                    		<div>&nbsp;${uiLabelMap.DAAt}:&nbsp;<#if note.noteDateTime?has_content>${Static["org.ofbiz.base.util.UtilFormatOut"].formatDateTime(note.noteDateTime, "", locale, timeZone)!}</#if></div>
                  		</td>
                  		<td valign="top" width="50%">
                    		${note.noteInfo?replace("\n", "<br/>")}
                  		</td>
                  		<td align="right" valign="top" width="20%">
		                    <#if note.internalNote?if_exists == "N">
		                        ${uiLabelMap.OrderPrintableNote}
		                        <form name="privateNotesForm_${note_index}" method="post" action="<@ofbizUrl>updateOrderNote</@ofbizUrl>">
		                          	<input type="hidden" name="orderId" value="${orderId}"/>
		                          	<input type="hidden" name="noteId" value="${note.noteId}"/>
		                          	<input type="hidden" name="internalNote" value="Y"/>
		                          	<a href="javascript:document.privateNotesForm_${note_index}.submit()" class="btn btn-mini btn-primary">${uiLabelMap.OrderNotesPrivate}</a>
		                        </form>
		                    </#if>
                    		<#if note.internalNote?if_exists == "Y">
		                        ${uiLabelMap.OrderNotPrintableNote}
		                        <form name="publicNotesForm_${note_index}" method="post" action="<@ofbizUrl>updateOrderNote</@ofbizUrl>">
		                          	<input type="hidden" name="orderId" value="${orderId}"/>
		                          	<input type="hidden" name="noteId" value="${note.noteId}"/>
		                          	<input type="hidden" name="internalNote" value="N"/>
		                          	<a href="javascript:document.publicNotesForm_${note_index}.submit()" class="btn btn-mini btn-primary">${uiLabelMap.OrderNotesPublic}</a>
		                        </form>
                    		</#if>
                  		</td>
                	</tr>
                	<#--<#if note_has_next>
                  		<tr><td colspan="3"><hr/></td></tr>
            		</#if>-->
              	</#list>
        	</table>
        <#else>
          	<div> <p class="alert alert-info">${uiLabelMap.OrderNoNotes}.</p></div>
        </#if>
	</div>
</div><!--#notes-tab-->
</#if>
