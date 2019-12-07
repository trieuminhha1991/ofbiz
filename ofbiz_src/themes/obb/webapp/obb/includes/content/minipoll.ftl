<#assign groupName = page.randomSurveyGroup?if_exists>
<#if groupName?has_content>
  <#assign randomSurvey = Static["org.ofbiz.product.store.ProductStoreWorker"].getRandomSurveyWrapper(request, "testSurveyGroup")?if_exists>
</#if>
<#if randomSurvey?has_content>
	<div class="block block-poll">
		<div class="block-title">
		    <strong><span>${randomSurvey.getSurveyName()?if_exists}</span></strong>
		</div>
		<form id="pollForm" action="<@ofbizUrl>minipoll<#if requestAttributes._CURRENT_VIEW_?exists>/${requestAttributes._CURRENT_VIEW_}</#if></@ofbizUrl>" method="post">
		    <div class="block-content">
			<#-- <p class="block-subtitle">What is your favorite color</p>
	                <ul id="poll-answers">
	                    <li class="odd">
			                <input type="radio" name="vote" class="radio poll_vote" id="vote_1" value="1">
			                <span class="label"><label for="vote_1">Green</label></span>
			            </li>
			                            <li class="even">
			                <input type="radio" name="vote" class="radio poll_vote" id="vote_2" value="2">
			                <span class="label"><label for="vote_2">Red</label></span>
			            </li>
			                            <li class="odd">
			                <input type="radio" name="vote" class="radio poll_vote" id="vote_3" value="3">
			                <span class="label"><label for="vote_3">Black</label></span>
			            </li>
			                            <li class="last even">
			                <input type="radio" name="vote" class="radio poll_vote" id="vote_4" value="4">
			                <span class="label"><label for="vote_4">Magenta</label></span>
			            </li>
	                </ul>
		            <div class="actions">
			            <button type="submit" title="Vote" class="button"><span><span>Vote</span></span></button>
			        </div> -->
		        ${randomSurvey.render()}
		    </div>
		</form>
	</div>
</#if>