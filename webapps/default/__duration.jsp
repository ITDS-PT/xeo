<%@ page contentType="text/plain;charset=UTF-8"%>
<%
  String parameter = request.getParameter("id");
  String operation = request.getParameter("operation");
  if(parameter != null && !"".equals(parameter) && operation != null && !"".equals(operation))
  {
      String[] info = parameter.split("__");
      String id = info[1].concat(info[3]);
      if("set".equals(operation))
      {
        request.getSession().setAttribute(id,new java.util.Date());
      }
      else if("get".equals(operation))
      {
          Object oldDate = request.getSession().getAttribute(id);
          if(oldDate != null)
          {
              long time = new java.util.Date().getTime() - ((java.util.Date)oldDate).getTime();
              long minutes = Math.round((time)/1000/60*10)/10;
              out.print(minutes);
          }
      }
  }
%>
