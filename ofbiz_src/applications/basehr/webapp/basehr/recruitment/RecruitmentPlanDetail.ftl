<div id="RecruitmentPlanDetail" class="hide">
    <div>${uiLabelMap.RecruitmentPlanDetail}</div>
    <div class='form-window-container' >
        <div class="form-window-content">
            <div id="contentPanel_Detail">
                <div class='row-fluid margin-bottom10' style="margin-top: 15px">
                    <div class='span4 align-right'>
                        <label class="asterisk">${uiLabelMap.recruitmentPlanName}</label>
                    </div>
                    <div class='span8'>
                        <input type="text" id="CriteriaId_Detail">
                    </div>
                </div>
                <div class='row-fluid margin-bottom10' style="margin-top: 15px">
                    <div class='span4 align-right'>
                        <label class="asterisk">${uiLabelMap.RecruitingPosition}</label>
                    </div>
                    <div class='span8'>
                        <div class="row-fluid">
                            <div class="span12">
                                <div class="span10">
                                    <div id="CriteriaType_Detail"></div>
                                </div>
                                <div class="span2">
                                    <button class="btn btn-mini btn-primary" style="width: 80%" type="button" id="viewListCriteriaTypeBtn_Detail"
                                            title="${StringUtil.wrapString(uiLabelMap.ViewListCriteriaType)}">
                                        <i class="icon-only icon-align-justify"></i>
                                    </button>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
                <div class='row-fluid margin-bottom10'>
                    <div class='span4 align-right'>
                        <label class="asterisk">${uiLabelMap.CommonDepartment}</label>
                    </div>
                    <div class='span8'>
                        <input type="text" id="CriteriaName_Detail">
                    </div>
                </div>
                <div class='row-fluid margin-bottom10'>
                    <div class='span4 align-right'>
                        <label class="">${uiLabelMap.CommonQty}</label>
                    </div>
                    <div class='span8'>
                        <textarea id="descriptionKPI_Detail"></textarea>
                    </div>
                </div>
                <div class='row-fluid margin-bottom10'>
                    <div class='span4 align-right'>
                        <label class="asterisk">${uiLabelMap.TimeRecruiting}</label>
                    </div>
                    <div class='span8'>
                        <div id="periodTypeNew_Detail"></div>
                    </div>
                </div>
                <div class='row-fluid margin-bottom10'>
                    <div class='span4 align-right'>
                        <label class="asterisk">${uiLabelMap.CreatedByRecruitmentRequirement}</label>
                    </div>
                    <div class='span8'>
                        <div id="targetNumberNew_Detail"></div>
                    </div>
                </div>
                <div class='row-fluid margin-bottom10'>
                    <div class='span4 align-right'>
                        <label class="asterisk">${uiLabelMap.RecruitmentBoard}</label>
                    </div>
                    <div class='span8'>
                        <div class="row-fluid">
                            <div class="span12">
                                <div class="span10">
                                    <div id="uomIdNew_Detail"></div>
                                </div>
                                <div class="span2">
                                    <button id="addNewUomId_Detail" style="width: 80%" title="${uiLabelMap.AddNew}" class="btn btn-mini btn-primary">
                                        <i class="icon-only icon-plus open-sans" style="font-size: 15px"></i>
                                    </button>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
                <div class='row-fluid'>
                    <div class='span4 align-right'>
                        <label class="asterisk">${uiLabelMap.RecruitmentRound}</label>
                    </div>
                    <div class='span8'>
                        <div id="perfCriDevelopmetTypeNew_Detail"></div>
                    </div>
                </div>
                <div class='row-fluid'>
                    <div class='span4 align-right'>
                        <label class=""></label>
                    </div>
                    <div class='span8'>
                        <#if perfCriDevelopmentTypeList?has_content>
                            <#list perfCriDevelopmentTypeList as perfCriDevelopmentType>
                                <div class="row-fluid" style="margin-right: 3px">
                                    <b>+ ${perfCriDevelopmentType.perfCriDevelopmetName}: </b>${StringUtil.wrapString(perfCriDevelopmentType.description)}
                                </div>
                                <div class="row-fluid">
                                    <span style="color: crimson">${uiLabelMap.HRFormula}: ${perfCriDevelopmentType.formula}</span>
                                </div>
                            </#list>
                        </#if>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>
<script type="text/javascript" src="/hrresources/js/recruitment/RecruitmentPlanDetail.js"></script>
