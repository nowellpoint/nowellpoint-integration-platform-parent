<#import "template.ftl" as t>
    <@t.page>
        <div class="container pt-5">
            <div class="row">
                <div class="col-2"></div>
                <div class="col-10">
                    <!--Section heading-->
                    <h2 class="section-heading h1 pt-4">${labels["contact.us"]}</h2>
                    <!--Section description-->
                    <p class="section-description">${labels["contact.us.headline"]}</p>
                </div>
            </div>
            <br>
            <div class="row">
                <div class="col-2"></div>
                <!--Grid column-->
                <div class="col-5">
                    <!-- <#if lead??> -->
                    <p class="text-success">${lead.successMessage!}</p>

                    <div class="center-on-small-only">
                        <a href="/" class="btn btn-success">${messages['done']}</a>
                    </div>

                    <!-- <#else> -->
                    <form id="contact-form" name="contact-form" action="/contact/" method="POST">
                        <div class="form-group">
                            <label for="firstName" class="">${labels['first.name']}</label>
                            <input type="text" id="firstName" name="firstName" class="form-control form-control-lg" autofocus>
                        </div>
                        <div class="form-group">
                            <label for="lastName" class="">${labels['last.name']}</label>
                            <input type="text" id="lastName" name="lastName" class="form-control form-control-lg">
                        </div>
                        <div class="form-group">
                            <label for="email" class="">${labels['email']}</label>
                            <input type="text" id="email" name="email" class="form-control form-control-lg">
                        </div>
                        <div class="form-group">
                            <label for="message">${labels['message']}</label>
                            <textarea type="text" id="message" name="message" rows="2" class="form-control form-control-lg"></textarea>
                        </div>
                        <div class="form-group">
                            <div class="text-right">
                                <a href="/">${messages['cancel']}</a>&emsp;
                                <button type="submit" id="submit" class="btn btn-primary">${labels['send.message']}</button>
                            </div>
                        </div>
                    </form>
                    <!-- </#if> -->
                </div>
                <!--Grid column-->
                <!--Grid column-->
                <div class="col-1"></div>
                <!--Grid column-->
                <!--Grid column-->
                <div class="col-4">
                    <!--Grid row-->
                    <div class="row mb-2">
                        <div class="col-2 text-center">
                            <i class="fa fa-2x fa-map-marker blue-grey-text"></i>
                        </div>
                        <div class="col-10 text-left">
                            <p class="grey-text">${labels["address.street"]}<br>${labels["address.city"]}, ${labels["address.state"]} ${labels["address.postal.code"]}<br>${labels["address.country"]}</p>
                        </div>
                    </div>

                    <!--Grid row-->
                    <div class="row mb-2">
                        <div class="col-2 text-center">
                            <i class="fa fa-2x fa-phone blue-grey-text"></i>
                        </div>
                        <div class="col-10 text-left">
                            <p class="grey-text">${labels["phone.number"]}</p>
                        </div>
                    </div>

                    <!--Grid row-->
                    <div class="row mb-2">
                        <div class="col-2 text-center">
                            <i class="fa fa-2x fa-envelope blue-grey-text"></i>
                        </div>
                        <div class="col-10 text-left">
                            <p class="grey-text"><a href="mailto:${labels['contact.email.address']}">${labels["contact.email.address"]}</a></p>
                        </div>
                    </div>
                </div>
                <!--Grid column-->

            </div>
        </div>
 </@t.page>