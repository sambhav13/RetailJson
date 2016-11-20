import org.scalatra._

class MyServlet extends ScalatraServlet  {

  get("/") {
    <html>
      <body>
        <h1>Hello, world!</h1>
        Say <a href="hello-scalate">hello to Scalate</a>.
      </body>
    </html>
  }

}

/*libraryDependencies += "org.eclipse.jetty" %"jetty-servlet" % "9.1.5.v20140505"
libraryDependencies += "com.sun.jersey.contribs" % "com.sun.jersey.contribs" % "1.18.1" 
libraryDependencies += "com.sun.jersey" % "jersey-json" %  "1.18.1"*/