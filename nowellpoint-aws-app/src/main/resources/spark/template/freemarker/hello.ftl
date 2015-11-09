<#include "header.ftl"/>



<body>
  <form name="user" action="hello" method="post">
  	Id: <input type="text" name="id" /> <br/>
    Name: <input type="text" name="name" /> <br/>
    City: <input type="text" name="city" />       <br/>
    <input type="submit" value="Save" />
  </form>

  <table class="datatable">
    <tr>
        <th>ID</th>  <th>Name</th> <th>City</th>
    </tr>
    
  </table>
</body>
</html>