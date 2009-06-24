<%@ page contentType="text/html;charset=UTF-8"%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>untitled</title>
</head>
<body>
<pre>
<%

java.util.Properties props =     System.getProperties();
java.util.Enumeration oEnum = props.keys();
out.println( Runtime.getRuntime().freeMemory() );
while(oEnum.hasMoreElements()) {
    String key = (String)oEnum.nextElement();
    out.println(key + "=" + props.get(key));
    
}

//netgest.system.ngtContext xxx = new netgest.system.ngtContext(null,null);


%>
</pre>

</body>
</html>
