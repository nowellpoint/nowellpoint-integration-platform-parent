<!DOCTYPE html>
<html lang="en">

<head>
    <meta charset="utf-8" />

    <title>${messages["application.title"]}</title>
    <meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1">
    <link rel="stylesheet" href="css/theme.css" type="text/css" />
    <link rel="stylesheet" href="css/media.css" type="text/css" />
    <link rel="stylesheet" href="css/font-awesome.min.css" type="text/css" />

    <link href='https://fonts.googleapis.com/css?family=Open+Sans:400,300,600,600italic,400italic,800,700' rel='stylesheet' type='text/css'>
    <link href='https://fonts.googleapis.com/css?family=Oswald:400,700,300' rel='stylesheet' type='text/css'>

</head>

<body>

    <div class="DesignHolder">
        <div class="LayoutFrame">
            <header>
                <div class="Center">
                    <div class="site-logo">
                        <h1><a href="#">Nowell<span>point</span></a></h1>
                    </div>
                    <div id="mobile_sec">
                        <div class="mobile"><i class="fa fa-bars"></i><i class="fa fa-times"></i></div>
                        <div class="menumobile">
                            <nav class="Navigation">
                                <ul>
                                    <li class="active">
                                        <a href="#home">${labels["home"]}</a>
                                        <span class="menu-item-bg"></span>
                                    </li>
                                    <li>
                                        <a href="#about">${labels["about"]}</a>
                                        <span class="menu-item-bg"></span>
                                    </li>
                                    <li>
                                        <a href="#services">${labels["services"]}</a>
                                        <span class="menu-item-bg"></span>
                                    </li>
                                    <li>
                                        <a href="#pricing">${labels["pricing"]}</a>
                                        <span class="menu-item-bg"></span>
                                    </li>
                                    <li>
                                        <a href="/contact/">${labels["contact"]}</a>
                                        <span class="menu-item-bg"></span>
                                    </li>
                                </ul>
                            </nav>
                        </div>
                    </div>
                    <div class="clear"></div>
                </div>
            </header>

            <div id="home">
                <div class="Banner_sec">
                    <div class="bannerside">
                        <div class="Center">
                            <div class="leftside">
                                <h3>${labels["headline"]}</h3>
                                <h4>${labels["tagline"]}</h4>
                                <a href="${links['free.account']}/">${labels["get.started"]}</a>&emsp;
                                <a href="${links['login']}/">${labels["log.in"]}</a>
                            </div>
                        </div>
                    </div>
                    <div class="clear"></div>
                </div>
            </div>

            <div id="container">
                <div class="About_sec" id="about">
                    <div class="Center">
                        <h2>${labels["about.us"]}</h2>
                        <p>${labels["about.us.headline"]}</p>
                        <div class="Line"></div>
                        <div class="Tabside">
                            <ul>
                                <li><a href="javascript:;" class="tabLink activeLink" id="cont-1">${labels["mission"]}</a></li>
                                <li><a href="javascript:;" class="tabLink" id="cont-2">${labels["vision"]}</a></li>
                            </ul>
                            <div class="clear"></div>
                            <div class="tabcontent" id="cont-1-1">
                                <div class="TabImage">
                                    <div class="img1">
                                        <figure><img src="images/code.png" alt="image"></figure>
                                    </div>
                                    <div class="img2">
                                        <figure><img src="images/screen.png" alt="image"></figure>
                                    </div>
                                </div>
                                <div class="Description">
                                    <h3>${labels["mission.statement"]}<span>${labels["mission.statement.tagline"]}</span></h3>
                                    <p>${labels["mission.statement.description"]}</p>
                                </div>
                            </div>
                            <div class="tabcontent hide" id="cont-2-1">
                                <div class="TabImage">
                                    <div class="img1">
                                        <figure><img src="img/about-img2.jpg" alt="image"></figure>
                                    </div>
                                    <div class="img2">
                                        <figure><img src="img/about-img1.jpg" alt="image"></figure>
                                    </div>
                                </div>
                                <div class="Description">
                                    <h3>${labels["vision.statement"]}<span>${labels["vision.statement.tagline"]}</span></h3>
                                    <p>${labels["vision.statement.description"]}</p>
                                </div>
                            </div>
                            <div class="clear"></div>
                        </div>
                        <!-- // End Tab Side // -->
                    </div>
                </div>
                <!-- // End About Section // -->
                <!-- \\ Begin Services Section \\ -->
                <div class="Services_sec" id="services">
                    <div class="Center">
                        <h2>${labels["our.services"]}</h2>
                        <p>${labels["our.services.description"]}</p>
                        <div class="Line"></div>
                        <!-- \\ Begin Services Side  \\ -->
                        <div class="Serviceside">
                            <ul>
                                <li class="Development"><a href="#services"><h4>DEVELOPMENT</h4></a></li>
                                <li class="Design"><a href="#services"><h4>DESIGN</h4></a></li>
                                <li class="Concept"><a href="#services"><h4>CONCEPT</h4></a></li>
                                <li class="System"><a href="#services"><h4>SYSTEM</h4></a></li>
                            </ul>
                        </div>
                        <!-- // End Services Side // -->
                    </div>
                </div>
                <!-- // End Services Section // -->
                <!-- \\ Begin Pricing Section \\ -->
                <div class="Pricing_sec" id="pricing">
                    <div class="Center">
                        <h2>${labels["pricing"]}</h2>
                        <p>${labels["pricing.headline"]}</p>
                        <div class="Line"></div>
                        <!-- \\ Begin Pricing Side \\ -->
                        <div class="pricingside">
                            <ul>
                                <#list planList as plan>

                                    <li>
                                        <div class="Basic">
                                            <h5>${plan.planName}</h5>
                                        </div>

                                        <div class="Dollar">
                                            <h2><sup>${plan.price.currencySymbol}</sup>${plan.price.unitPrice?string["0.00"]}</h2>
                                        </div>

                                        <#list plan.features?sort_by( "sortOrder") as feature>

                                            <#if feature.code=="SALESFORCE_ORGANIZATIONS">
                                                <div class="Band">
                                                    <p>${feature.quantity} <span>${labels["organizations"]}</span></p>
                                                </div>
                                            </#if>

                                            <#if feature.code=="SALESFORCE_SANDBOXES">
                                                <div class="Band">
                                                    <p>${feature.quantity} <span>${labels["sandboxes"]}</span></p>
                                                </div>
                                            </#if>

                                            <#if feature.code=="USERS">
                                                <div class="Band">
                                                    <p>${feature.quantity} <span>${labels["users"]}</span></p>
                                                </div>
                                            </#if>

                                            <#if feature.code=="DISK_SPACE">
                                                <div class="Band">
                                                    <p>${feature.quantity} <span>${labels["disk.space"]}</span></p>
                                                </div>
                                            </#if>

                                        </#list>
                                        <div class="Order">
                                            <a href="/signup?planId=${plan.id}">${labels["order.now"]}</a>
                                        </div>
                                    </li>
                                </#list>
                            </ul>
                        </div>
                    </div>
                </div>

                <footer>
                    <div class="Cntr">
                        <p>${messages["copyright"]}</p>
                    </div>
                </footer>
            </div>
        </div>
    </div>

    <div id="loader-wrapper">
        <div id="loader"></div>
        <div class="loader-section section-left"></div>
        <div class="loader-section section-right"></div>
    </div>

    <script type="text/javascript" src="js/jquery-1.11.0.min.js"></script>
    <script type="text/javascript" src="js/jquery.sudoSlider.min.js"></script>
    <script type="text/javascript" src="js/global.js"></script>
    <script type="text/javascript" src="js/modernizr.js"></script>
    <script type="text/javascript" src="js/jquery.validate.min.js"></script>
    <script type="text/javascript" src="js/index.js"></script>

</body>

</html>