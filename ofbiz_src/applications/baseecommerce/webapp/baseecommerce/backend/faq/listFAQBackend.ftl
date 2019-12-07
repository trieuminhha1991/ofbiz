<#include "component://basecrm/webapp/basecrm/callcenter/ftLlibraryVariable.ftl"/>
<script type="text/javascript" src="/ecommerceresources/js/backend/faq/listFAQ.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxeditor.js"></script>

<#assign initrowdetailsDetail = "function (index, parentElement, gridElement, datarecord) {

	var answerFAQ = DataAccess.getData({
				url: 'getAnswerFAQ',
				data: {contentId: datarecord.contentId},
				source: 'answerFAQ'});

	var answer = $($(parentElement).children()[0]);
	$(answer).css('width', '95%');
	var container = $(\"<div style='margin-top: 60px;float: right;'></div>\");
    var question = $(\"<div class='question'><b>${uiLabelMap.BSQuestion}:</b>&nbsp;&nbsp;<label class='inline'>\" + datarecord.longDescription + \"</label></div>\");
	var input = $(\"<a id='input\" + index + \"' style='cursor: pointer;' onclick='Answer.send(&quot;\" + index + \"&quot;,&quot;\" + datarecord.contentId + \"&quot;)'><i class='fa fa-reply'></i>\" + multiLang.BSReply + \"</a>\");
	var editor = $(\"<textarea id='editor\" + index + \"'></textarea>\");
	var contentId = $(\"<input type='hidden' id='contentId\" + index + \"'/>\");
	$(answer).append(container);
	$(answer).append(question);
	container.append(input);
	$(answer).append(editor);
	$(answer).append(contentId);
	editor.jqxEditor({
		theme: 'olbiuseditor',
        height: 200,
        width: '100%'
    });
	if (!_.isEmpty(answerFAQ)) {
		editor.jqxEditor('val', answerFAQ.longDescription);
		Answer.updateMode({
			index: index,
			contentId: answerFAQ.contentId
		});
	}
}"/>
<#assign dataField="[{ name: 'contentId', type: 'string' },
					 { name: 'contentTypeId', type: 'string'},
					 { name: 'topicName', type: 'string'},
					 { name: 'contentName', type: 'string'},
					 { name: 'description', type: 'string'},
					 { name: 'longDescription', type: 'string'},
					 { name: 'statusId', type: 'string'},
					 { name: 'createdStamp', type: 'number'},
					 { name: 'numberOfComments', type: 'number', other: 'Long'}]"/>

<#assign columnlist="{ text: '${uiLabelMap.BSTopicFAQ}', datafield: 'contentTypeId', filtertype: 'checkedlist', width: 200,
						cellsrenderer: function(row, colum, value){
							value?value=mapFAQCategory[value]:value;
							return '<span>' + value + '</span>';
						 },createfilterwidget: function (column, htmlElement, editor) {
							editor.jqxDropDownList({ autoDropDownHeight: true, source: FAQCategories, displayMember: 'contentTypeId', valueMember: 'contentTypeId' ,
				                renderer: function (index, label, value) {
									if (index == 0) {
										return value;
									}
								    return mapFAQCategory[value];
				                }
							});
							editor.jqxDropDownList('checkAll');
						 }
					 },
					 { text: '${uiLabelMap.BSAskedId}', datafield: 'contentId', width: 150},
					 { text: '${uiLabelMap.BSStatus}', datafield: 'statusId', filtertype: 'checkedlist', width: 200,
						 cellsrenderer: function(row, colum, value){
								value?value=mapStatusItem[value]:value;
								return '<span>' + value + '</span>';
						 },createfilterwidget: function (column, htmlElement, editor) {
							editor.jqxDropDownList({ autoDropDownHeight: true, source: listStatusItem, displayMember: 'statusId', valueMember: 'statusId' ,
	                            renderer: function (index, label, value) {
									if (index == 0) {
										return value;
									}
								    return mapStatusItem[value];
				                }
							});
							editor.jqxDropDownList('checkAll');
						 }
					 },
					 { text: '${uiLabelMap.BSPartyAsked}', datafield: 'contentName'},
					 { text: '${uiLabelMap.BSTimeComment}', datafield: 'createdStamp', filtertype: 'input', width: 150, filterable: false, sortable: false,
						 cellsrenderer: function(row, colum, value){
								if (value) {
									value = TimeAgo.convert(value);
								}
								return '<span>' + value + '</span>';
						 }
					 },
					 { text: '${uiLabelMap.BSNumberOfReplies}', datafield: 'numberOfComments', cellsalign: 'right', filtertype: 'number', width: 150}"/>

<@jqGrid id="ListFAQ" addType="popup" dataField=dataField columnlist=columnlist clearfilteringbutton="true" showtoolbar="true"
	url="jqxGeneralServicer?sname=JQGetListFAQ" contextMenuId="contextMenu" mouseRightMenu="true"
	initrowdetails="true" initrowdetailsDetail=initrowdetailsDetail rowdetailsheight="300"
	updateUrl="jqxGeneralServicer?jqaction=U&sname=updateContent" editColumns="contentId;statusId"/>


<div id='contextMenu' style="display:none;">
	<ul>
		<li id='activate'><i class="fa-frown-o"></i>&nbsp;&nbsp;${uiLabelMap.DmsDeactivate}</li>
	</ul>
</div>


<div id="jqxNotificationNested">
	<div id="notificationContentNested">
	</div>
</div>

<#assign listStatusItem = delegator.findList("StatusItem", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("statusTypeId", "CONTENT_STATUS"), null, null, null, false) />
<#assign FAQCategories = delegator.findList("FAQCategory", null, null, null, null, false) />

<script>
var listStatusItem = [<#if listStatusItem?exists><#list listStatusItem as item><#if item.statusId == "CTNT_PUBLISHED" || item.statusId == "CTNT_DEACTIVATED">{
	statusId: '${item.statusId?if_exists}',
	description: '${StringUtil.wrapString(item.get("description", locale)?if_exists)}'
},</#if></#list></#if>];
var mapStatusItem = {<#if listStatusItem?exists><#list listStatusItem as item>
	'${item.statusId?if_exists}': '${StringUtil.wrapString(item.get("description", locale)?if_exists)}',
</#list></#if>};

var FAQCategories = [<#if FAQCategories?exists><#list FAQCategories as item>{
	contentTypeId: '${item.contentTypeId?if_exists}',
	topicName: '${StringUtil.wrapString(item.get("topicName", locale)?if_exists)}'
},</#list></#if>];

var mapFAQCategory = {<#if FAQCategories?exists><#list FAQCategories as item>
'${item.contentTypeId?if_exists}': '${StringUtil.wrapString(item.get("topicName", locale)?if_exists)}',
</#list></#if>};
</script>