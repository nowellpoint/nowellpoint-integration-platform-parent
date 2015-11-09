<#include "header.ftl"/>



<body>
  <form name="user" action="hello" method="post">
  	Id: <input type="text" name="id" /> <br/>
    Name: <input type="text" name="name" /> <br/>
    City: <input type="text" name="city" />       <br/>
    <input type="submit" value="Save" />
    <a href="<#if (content.rootpath)??>${content.rootpath}<#else></#if>https://rsbjc5t3q4.execute-api.us-east-1.amazonaws.com/v1/salesforce/oauth/authorize">Sign-In</a>
    
    <button type="button" class="btn btn-default" aria-label="Left Align">
  <span class="glyphicon glyphicon-align-left" aria-hidden="true"></span>Sign-In
</button>
    
  </form>
</body>
</html>