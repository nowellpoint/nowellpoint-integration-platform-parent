<#import "template.ftl" as t>
    <@t.page>
        <div class="container pt-5">
            <div class="row">
                <div class="col-2"></div>
                <div class="col-10">
                    <!--Section heading-->
                    <h2 class="section-heading h1 pt-4">${labels['step2']}</h2>
                    <!--Section description-->
                    <p class="section-description">${labels["check.email"]}:&nbsp;${registration.email}. ${labels['token.instruction']}&nbsp;${labels['misplaced.activation.email']}&nbsp;<a href="${ACCOUNT_ACTIVATION_RESEND_URI}">${labels['send.activation.email']}</a></p>
                </div>
            </div>
            <#if errorMessage??>
            <div class="row">
                <div class="col-2"></div>
                <div class="col-10">
                    <div class="alert alert-danger" role="alert">
                        ${errorMessage}
                        <button type="button" class="close" data-dismiss="alert" aria-label="Close">
                            <span aria-hidden="true">&times;</span>
                        </button>
                    </div>
                </div>
            </div>
            <#else>
            <br>
            </#if>
            <div class="row">
                <div class="col-2"></div>
                <!--Grid column-->
                <div class="col-5">
                    <form id="activate-form" role="form" method="post" action="${ACCOUNT_ACTIVATE_URI}">
                        <div class="form-group">
                            <input type="text" name="activationToken" id="activationToken" class="form-control form-control-lg" autofocus>
                        </div>
                        <div class="form-group">
                            <div class="text-right">
                                <button type="submit" id="submit" class="btn btn-primary">${labels['activate']}</button>
                            </div>
                        </div>
                    </form>
                </div>
                <div class="col-1"></div>
                <div class="col-4">
                    <!--Grid row-->
                    <div class="row mb-2">
                        <div class="col-2 text-center">
                            <i class="fa fa-2x fa-check-square-o blue-grey-text"></i>
                        </div>
                        <div class="col-10 text-left">
                            <h5 class="font-weight-bold">${labels['step1.label']}</h5>
                            <p class="grey-text">${labels['step1']}</p>
                        </div>
                    </div>
                    <!--Grid row-->
                    <div class="row mb-2">
                        <div class="col-2 text-center">
                            <i class="fa fa-2x fa-arrow-right blue-grey-text"></i>
                        </div>
                        <div class="col-10 text-left">
                            <h5 class="font-weight-bold">${labels['step2.label']}</h5>
                            <p class="grey-text">${labels['step2']}</p>
                        </div>
                    </div>
                    <div class="row mb-2">
                        <div class="col-2 text-center">
                            <i class="fa fa-2x fa-square-o blue-grey-text"></i>
                        </div>
                        <div class="col-10 text-left">
                            <h5 class="font-weight-bold">${labels['step3.label']}</h5>
                            <p class="grey-text">${labels['step3']}</p>
                        </div>
                    </div>

                    <!--Grid row-->
                    <div class="row mb-2">
                        <div class="col-2 text-center">
                            <i class="fa fa-2x fa-square-o blue-grey-text"></i>
                        </div>
                        <div class="col-10 text-left">
                            <h5 class="font-weight-bold">${labels['step4.label']}</h5>
                            <p class="grey-text">${labels['step4']}</p>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </@t.page>