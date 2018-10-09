<#import "template.html" as t>
    <@t.page>

        <script>
            var billingContact = {
                firstName: "${(organization.subscription.billingContact.firstName)!}",
                lastName: "${(organization.subscription.billingContact.lastName)!}",
                email: "${(organization.subscription.billingContact.email)!}",
                phone: "${(organization.subscription.billingContact.phone)!}"
            }

            var billingAddress = {
                street: "${(organization.subscription.billingAddress.street)!}",
                city: "${(organization.subscription.billingAddress.city)!}",
                state: "${(organization.subscription.billingAddress.state)!}",
                postalCode: "${(organization.subscription.billingAddress.postalCode)!}",
                countryCode: "${(organization.subscription.billingAddress.countryCode)!}"
            }

            var organization = {
                id: "${organization.id}",
                cardholdername: "${(organization.subscription.creditCard.cardholderName)!}",
                lastFour: "${(organization.subscription.creditCard.lastFour)!}",
                cardType: "${(organization.subscription.creditCard.cardType)!}",
                expirationMonth: "${(organization.subscription.creditCard.expirationMonth)!}",
                expirationYear: "${(organization.subscription.creditCard.expirationYear)!}",
                billingContact: billingContact,
                billingAddress: billingAddress
            }

            sessionStorage.setItem("organization", JSON.stringify(organization));
        </script>

        <div class="container-fluid m-3">
            <div class="card border-light mb-3">
                <div class="card-header pt-3">
                    <div class="row">
                        <div class="col-12 align-self-center">
                            <h6><i class="fa fa-info-circle fa-lg light-blue-text"></i>&emsp;${labels["organization.information"]}</h6>
                        </div>
                    </div>
                </div>
                <div id="organization-information" class="card-body">
                    <#include "organization-information.html" />
                </div>
            </div>
            <div class="card border-light mb-3">
                <div class="card-header pt-3">
                    <div class="row">
                        <div class="col-12 align-self-center">
                            <h6><i class="fa fa-barcode fa-lg light-blue-text"></i>&emsp;${labels["subscription"]}</h6>
                        </div>
                    </div>
                </div>
                <div id="organization-subscription" class="card-body">
                    <#include "organization-subscription.html" />
                </div>
            </div>
        </div>

        <#include "success-popup.html" />

        <!-- JQuery -->
        <script type="text/javascript" src="/js/jquery-3.2.1.min.js"></script>
        <!-- Bootstrap tooltips -->
        <script type="text/javascript" src="/js/popper.min.js"></script>
        <!-- Bootstrap core JavaScript -->
        <script type="text/javascript" src="/js/bootstrap.min.js"></script>
        <!-- MDB core JavaScript -->
        <script type="text/javascript" src="/js/mdb.min.js"></script>
        <!-- Custom JavaScript -->
        <script type="text/javascript" src="/js/organization.js"></script>

    </@t.page>