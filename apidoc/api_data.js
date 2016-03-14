define({ "api": [  {    "type": "get",    "url": "/identity/:id",    "title": "Get Identity",    "name": "getIdentity",    "version": "1.0.0",    "group": "Identity",    "header": {      "fields": {        "Header": [          {            "group": "Header",            "type": "String",            "optional": false,            "field": "authorization",            "description": "<p>Authorization with the value of Bearer access_token from authenticate</p>"          }        ]      }    },    "parameter": {      "fields": {        "Parameter": [          {            "group": "Parameter",            "type": "String",            "optional": false,            "field": "id",            "description": "<p>The Identity's unique id</p>"          }        ]      }    },    "filename": "nowellpoint-aws-api/src/main/java/com/nowellpoint/aws/api/resource/IdentityResource.java",    "groupTitle": "Identity"  },  {    "type": "get",    "url": "/identity/:id/picture",    "title": "Get Profile Picture",    "name": "getPicture",    "version": "1.0.0",    "group": "Identity",    "parameter": {      "fields": {        "Parameter": [          {            "group": "Parameter",            "type": "String",            "optional": false,            "field": "id",            "description": "<p>The Identity's unique id</p>"          }        ]      }    },    "filename": "nowellpoint-aws-api/src/main/java/com/nowellpoint/aws/api/resource/IdentityResource.java",    "groupTitle": "Identity"  }] });
