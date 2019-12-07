<div id="UploadFileSalesStatementEdited" class="hide">
    <div>${uiLabelMap.BSUploadSourceFileSalesStatement}</div>
    <div class='form-window-content' style="position: relative;">
        <div class='form-window-content'>
            <div class="row-fluid">
                <div id="wizardAddNew" class="row-fluid hide" data-target="#containerAddNew">
                    <ul class="wizard-steps wizard-steps-square">
                        <li data-target="#fileSalesStatementExcelUpload" class="active">
                            <span class="step">1. ${uiLabelMap.BSFileSalesStatmentEdited}</span>
                        </li>
                        <li data-target="#testDataSalesStatementUpload">
                            <span class="step">2. ${uiLabelMap.TestData}</span>
                        </li>
                        <li data-target="#joinColumnDataSalesStatement">
                            <span class="step">3. ${uiLabelMap.ColumnExcelImportMap}</span>
                        </li>
                    </ul>
                </div><!--#fuelux-wizard-->
                <div class="step-content row-fluid position-relative" id="containerAddNew">
                    <div class="step-pane active" id="fileSalesStatementExcelUpload">
                        <div class="span12">
                            <div class='row-fluid'>
                                <div class="span4 text-algin-right">
                                    <label class="asterisk">${StringUtil.wrapString(uiLabelMap.BSSourceFileSalesStatementName)}</label>
                                </div>
                                <div class="span8" id="sourceFileSalesStatementImportContainer">
                                    <form class="no-margin" action="" class="row-fluid" id="upLoadFileForm"  method="post" enctype="multipart/form-data">
                                        <input type="hidden" name="_uploadedFile_fileName" id="_uploadedFile_fileName" value="" />
                                        <input type="hidden" name="_uploadedFile_contentType" id="_uploadedFile_contentType" value="" />
                                        <div class="row-fluid">
                                            <div class="span12" style="">
                                                <input type="file" id="sourceFileSalesStatementImport" />
                                            </div>
                                        </div>
                                    </form>
                                </div>
                            </div>
                            <div class='row-fluid margin-bottom10'>
                                <div class="span4 text-algin-right">
                                    <label class="asterisk">${StringUtil.wrapString(uiLabelMap.SheetImportExcel)}</label>
                                </div>
                                <div class="span8">
                                    <div id="sheetImportDropDown"></div>
                                </div>
                            </div>
                            <div class='row-fluid margin-bottom10'>
                                <div class="span4 text-algin-right">
                                    <label class="asterisk">${StringUtil.wrapString(uiLabelMap.NumberLineTitle)}</label>
                                </div>
                                <div class="span8">
                                    <div id="numberLineTitle"></div>
                                </div>
                            </div>
                            <div class='row-fluid margin-bottom10'>
                                <div class="span4 text-algin-right">
                                </div>
                                <div class="span8">
                                    <a href="javascript:OlbCRUDSalesStatement.exportExcel();"><i class="fa fa-download"></i>${uiLabelMap.DownloadFileTemplate}</a>
                                </div>
                            </div>
                        </div>
                    </div>
                    <div class="step-pane" id="testDataSalesStatementUpload">
                        <div class="row-fluid">
                            <div class="span12">
                                <div id="sourceFileSalesStatementGrid"></div>
                            </div>
                        </div>
                    </div>
                    <div class="step-pane" id="joinColumnDataSalesStatement">
                        <div class="row-fluid">
                            <div id="joinColumnGrid"></div>
                        </div>
                    </div>
                    <div class="row-fluid no-left-margin">
                        <div id="loadingCreateNew" class="hide" style="z-index: 9999999999; position: absolute; top: 50%; left: 50%">
                            <div class="loader-page-common-custom" id="spinnerCreateNew"></div>
                        </div>
                    </div>
                </div>
                <div class="form-action wizard-actions">
                    <button class="btn btn-next btn-success form-action-button pull-right" data-last="${uiLabelMap.CommonSave}" id="btnNext">
                    ${uiLabelMap.CommonNext}
                        <i class="icon-arrow-right icon-on-right"></i>
                    </button>
                    <button class="btn btn-prev form-action-button pull-right" id="btnPrev">
                        <i class="icon-arrow-left"></i>
                    ${uiLabelMap.CommonPrevious}
                    </button>
                </div>
            </div>
        </div>
    </div>
</div>
<script type="text/javascript" src="/salesmtlresources/js/sales_statement/AddNewSalesStatementDetailExcelFile.js"></script>
<script type="text/javascript" src="/salesmtlresources/js/sales_statement/AddNewSalesStatementDetail.js"></script>