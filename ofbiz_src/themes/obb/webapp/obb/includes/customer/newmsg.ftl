<div class="skin-default contacts-index-index">
	<div id="jm-container" class="jm-col1-layout wrap not-breadcrumbs clearfix">
		<div class="main clearfix">
			<div id="jm-mainbody" class="clearfix">
				<div id="jm-main">
					<div class="inner clearfix">
						<div id="jm-current-content" class="clearfix">
						  <div class="jm-contacts">
							<div class="page-title">
								<h1>${uiLabelMap.CommonMessages}</h1>
							</div>

						    <div class="ct-contacts clearfix">
							<h3>Gửi thông tin tới bhappy</h3>
								<div id="messages_product_view">
									<#if showMessageLinks?default("false")?upper_case == "TRUE">
						                <a href="<@ofbizUrl>messagelist</@ofbizUrl>" class="submenutextright">${uiLabelMap.ObbViewList}</a>
						            </#if>
								</div>
								<div class="contact-form">
								    <div class="contact-inner">
								      <form name="contactus" method="post" action="<@ofbizUrl>${submitRequest}</@ofbizUrl>" style="margin: 0;">
								        <input type="hidden" name="partyIdFrom" value="${userLogin.partyId}"/>
								        <input type="hidden" name="contactMechTypeId" value="WEB_ADDRESS"/>
								        <input type="hidden" name="communicationEventTypeId" value="WEB_SITE_COMMUNICATI"/>
								        <#if productStore?has_content>
								          <input type="hidden" name="partyIdTo" value="${productStore.payToPartyId?if_exists}"/>
								        </#if>
								        <input type="hidden" name="note" value="${Static["org.ofbiz.base.util.UtilHttp"].getFullRequestUrl(request).toString()}"/>
								        <#if message?has_content>
								          <input type="hidden" name="parentCommEventId" value="${communicationEvent.communicationEventId}"/>
								          <#if (communicationEvent.origCommEventId?exists && communicationEvent.origCommEventId?length > 0)>
								            <#assign orgComm = communicationEvent.origCommEventId>
								          <#else>
								            <#assign orgComm = communicationEvent.communicationEventId>
								          </#if>
								          <input type="hidden" name="origCommEventId" value="${orgComm}"/>
								        </#if>
								        <table width="100%" border='0' cellspacing='0' cellpadding='0' class='boxbottom'>
								          <tr>
								            <td colspan="3">&nbsp;</td>
								          </tr>
								          <tr>
								            <td width="5">&nbsp;</td>
								            <td align="right"><div class="tableheadtext">${uiLabelMap.CommonFrom}:</div></td>
								            <td><div>&nbsp;${sessionAttributes.autoName?if_exists} [${userLogin.partyId}] (${uiLabelMap.CommonNotYou}?&nbsp;<a href="<@ofbizUrl>autoLogout</@ofbizUrl>" class="buttontext">${uiLabelMap.CommonClickHere}</a>)</div></td>
								          </tr>
								          <#if partyIdTo?has_content>
								            <#assign partyToName = Static["org.ofbiz.party.party.PartyHelper"].getPartyName(delegator, partyIdTo, true)>
								            <input type="hidden" name="partyIdTo" value="${partyIdTo}"/>
								            <tr>
								              <td colspan="3">&nbsp;</td>
								            </tr>
								            <tr>
								              <td width="5">&nbsp;</td>
								              <td align="right" style="vertical-align:middle;">${uiLabelMap.CommonTo}:</td>
								              <td><div>&nbsp;${partyToName}</div></td>
								            </tr>
								          </#if>
								          <tr>
								            <td colspan="3">&nbsp;</td>
								          </tr>
								          <#assign defaultSubject = (communicationEvent.subject)?default("")>
								          <#if (defaultSubject?length == 0)>
								            <#assign replyPrefix = "RE: ">
								            <#if parentEvent?has_content>
								              <#if !parentEvent.subject?default("")?upper_case?starts_with(replyPrefix)>
								                <#assign defaultSubject = replyPrefix>
								              </#if>
								              <#assign defaultSubject = defaultSubject + parentEvent.subject?default("")>
								            </#if>
								          </#if>
								          <tr>
								            <td width="5">&nbsp;</td>
								            <td align="right" style="vertical-align:middle;">${uiLabelMap.ObbSubject}:</td>
								            <td><input type="input" class="input-text" style="width:215px;" name="subject" size="20" value="${defaultSubject}"/>
								          </tr>
								          <tr>
								            <td colspan="3">&nbsp;</td>
								          </tr>
								          <tr>
								            <td width="5">&nbsp;</td>
								            <td align="right" style="vertical-align:middle;">${uiLabelMap.CommonMessage}:</td>
								            <td><textarea name="content" class="textAreaBox" style="width: 100%;height: 170px;"></textarea></td>
								          </tr>
								          <tr>
										<td colspan="2">&nbsp;</td>
								            <td>
							                    <div style="margin-top:10px;"><button style="float:right;" type="submit" class="button" title="${uiLabelMap.CommonSend}" name="send" id="send2"><span><span>${uiLabelMap.CommonSend}</span></span></button></div>
											</td>
								          </tr>
								        </table>
								      </form>
								    </div>
								</div>
								<div class="contact-info">
									<div class="inner"><iframe src="https://www.google.com/maps/embed?pb=!1m18!1m12!1m3!1d5248.720782590947!2d2.332451662314348!3d48.87040590815822!2m3!1f0!2f0!3f0!3m2!1i1024!2i768!4f13.1!3m3!1m2!1s0x47e66e3bcba3deb1%3A0xa204c2b159245312!2s2+Rue+de+Marivaux%2C+75002+Paris%2C+Ph%C3%A1p!5e0!3m2!1svi!2s!4v1457503620135" width="600" height="450" frameborder="0" style="border:0" allowfullscreen></iframe>
									<div class="info-inner">
										<p>${uiLabelMap.ObbAboutUsContent2}</p>
										<div class="info-inner2">
											<ul class="list-info">
												<li class="address">
													<em class="fa fa-home">&nbsp;</em><span>Địa chỉ: </span>2 rue Marivaux, Paris, France
												</li>
												<li class="phone"><em class="fa fa-phone">&nbsp;</em><span>Điện thoại:</span> +33 763 571 929</li>
												<li class="email"><em class="fa fa-envelope">&nbsp;</em><span>Thư điện tử:</span> <a href="mailto:bhappy.vn.contact@gmail.com">bhappy.vn.contact@gmail.com</a></li>
											</ul>
										</div>
									</div>
								</div>
							</div>
						</div>
					</div>
				</div>
			</div>
		</div>
	</div>
</div>