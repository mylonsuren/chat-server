

console.log("test");

$(function(){
  function getData() {
      var dataToBeSent  = {
        uName : 'test123' , //
        passwd: 'helloworld'
      }; // you can change parameter name

      $.ajax({
          url : '../link.jsp', // Your Servlet mapping or JSP(not suggested)
          data : dataToBeSent, 
          type : 'POST',
          dataType : 'html', // Returns HTML as plain text; included script tags are evaluated when inserted in the DOM.
          success : function(response) {
              console.log("SUCCESS");// create an empty div in your page with some id
          },
          error : function(request, textStatus, errorThrown) {
              console.error(errorThrown);
          }
      });
  }
});

