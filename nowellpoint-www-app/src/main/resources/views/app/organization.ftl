<#import "template.html" as t>
    <@t.page>
        <div class="container-fluid p-3">
            <div class="dashhead">
                <div class="dashhead-titles">
                    <h6 class="dashhead-subtitle">${labels["organization"]}</h6>
                    <h3 class="dashhead-title">${labels["details"]}</h3>
                </div>
            </div>
            <#include "organization-information.ftl" />
            <!--
            <div class="card border-light">
                <div id="organization-information" class="card-body text-secondary">
                    <#include "organization-information.ftl" />
                    <div class="card border-light">
                        <div class="card-header pt-3">
                            <div class="row">
                                <div class="col-3">
                                    <span class="grey-text">${labels["plan.name"]}</span>
                                </div>
                                <div class="col-3">
                                    <span class="grey-text">${labels["price"]} (${organization.subscription.billingFrequency})</span>
                                </div>
                                <div class="col-3">
                                    <span class="grey-text">${labels["next.billing.date"]}</span>
                                </div>
                                <div class="col-3"></div>
                            </div>
                        </div>
                        <div id="organization-subscription" class="card-body">
                            <#include "organization-subscription.ftl" />
                            <div class="row">
                                <div class="col-6 d-inline-block pt-3 pb-3">
                                    <i class="fa fa-arrow-circle-right fa-lg"></i>&nbsp;<span class="grey-text">${labels["billing.address"]}</span>
                                </div>
                                <div class="col-6 d-inline-block pt-3 pb-3">
                                    <i class="fa fa-arrow-circle-right fa-lg"></i>&nbsp;<span class="grey-text">${labels["payment.method"]}</span>
                                </div>
                            </div>
                            <div class="row">
                                <div class="col-6">
                                    <#include "organization-billing-address.ftl" />
                                    <div class="row">
                                        <div class="col-12 text-right">
                                            <button type="button" class="btn btn-success">${labels["change"]}</button>
                                        </div>
                                    </div>
                                </div>
                                <div class="col-6">
                                    <#include "organization-payment-method.ftl" />
                                    <div class="row">
                                        <div class="col-12 text-right">
                                            <button type="button" class="btn btn-success">${labels["change"]}</button>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
 -->
        </div>

    </@t.page>