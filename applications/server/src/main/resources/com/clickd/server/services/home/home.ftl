<#-- @ftlvariable name="" type="com.clickd.server.services.home.HomeView" -->
<html>
    <body>
        <!-- calls getName() and sanitizes it -->
        <h1>Hello, ${name?html}!</h1>
    </body>
</html>