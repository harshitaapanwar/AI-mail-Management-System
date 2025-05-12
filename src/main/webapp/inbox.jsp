<%@ page import="java.util.List" %>
<%@ page import="com.google.api.services.gmail.model.Message" %>
<%@ page import="com.google.api.services.gmail.model.MessagePartHeader" %>

...

<form action="send" method="post" class="space-y-4">
  <input type="hidden" name="csrfToken" value="<%= request.getAttribute("csrfToken") %>">
  ...
</form>

...

<%
  List<Message> messages = (List<Message>) request.getAttribute("messages");
  if (messages != null && !messages.isEmpty()) {
    for (Message message : messages) {
      String from = "";
      String subject = "";
      String snippet = message.getSnippet();

      for (MessagePartHeader header : message.getPayload().getHeaders()) {
        if (header.getName().equals("From")) {
          from = header.getValue();
        } else if (header.getName().equals("Subject")) {
          subject = header.getValue();
        }
      }
%>
  <div class="border-b py-4">
    <p><strong>From:</strong> <%= from %></p>
    <p><strong>Subject:</strong> <%= subject %></p>
    <p><strong>Snippet:</strong> <%= snippet %></p>
  </div>
<%
    }
  } else {
%>
  <p class="text-gray-500">No messages found.</p>
<%
  }
%>
