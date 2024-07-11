PHP 8.1 application to manage Source games similar to SourceBans++

Structure:
  User facing endpoints go to index.php, redirected through the .htaccess file.
  $fromIndex will be true. Data rendering is controled by the files in render/
  after being processed by files in processor/.

  The processor file needs a process() function that returns an array with
  ['Error'=>str and 'HttpCode'=>int] or an arbitrary object that's passed into
  output(). For html rendering htmlHeader() is called with that data first,
  if that function exists, to generate additional tags in the header, before
  htmlRender() is called with that data to generate the body.
  htmlRender() should return a breadcrumb string.

  The api endpoints are targeting action.php directly with the ?do=ACTION
  parameter. $fromAction will be true. The actions are handled by files in
  action/.

  For actions the reply should be similar to the result of processors. The
  client side js understands an object with 'Error' set, otherwise any object
  goes.
