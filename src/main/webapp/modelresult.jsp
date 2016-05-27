<!DOCTYPE html>
<%@page import="de.unirostock.sems.M2CAT.Config"%>
<%@page import="de.unirostock.sems.M2CAT.RetrievalFactory"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ page import="de.unirostock.sems.M2CAT.beans.*" %>
<jsp:useBean id="model" class="de.unirostock.sems.M2CAT.beans.ModelResult" scope="request" />
<jsp:setProperty name="model" property="id" value="<%=request.getParameter(\"id\") %>" />
<%
	// starting the job
	RetrievalFactory.getInstance().startRetrieving( model.getId() );
%>
<html lang="en">
<head>
	<meta charset="utf-8" />
	<meta http-equiv="X-UA-Compatible" content="IE=edge" />
	<meta name="viewport" content="width=device-width, initial-scale=1" />
	<!-- The above 3 meta tags *must* come first in the head; any other head content must come *after* these tags -->
	<% //model.gatherInformation(); %>
	<title>M2CAT <%=model.getModel() != null ? "- " + model.getModel().getModelName() : "" %></title>
	
	<!-- Bootstrap -->
	<link href="resources/css/bootstrap.min.css" rel="stylesheet" />
	
	<!-- main css -->
	<link href="resources/css/main.css" rel="stylesheet" /> 
	<!-- index css -->
	<link href="resources/css/modelresult.css" rel="stylesheet" />
	
	<script type="text/javascript">
		var modelId = "<%=model.getId() %>";
		var restUrl = "rest/";
		
		var baseUrl = getBaseUrl();
		function getBaseUrl() {
			var base = location.protocol+"//"+location.host;
			var pattern = /^\/?([^\/]+\/)*([^\/]+)$/;
			if( location.pathname != null && location.pathname != undefined ) {
				var match = pattern.exec(location.pathname);
				console.log("matched: " + location.pathname);
				console.log(match);
				base += match[1] != undefined ? "/" + match[1] : "/";
			}
			
			return base;
		}
	</script>
	
	<!-- HTML5 shim and Respond.js for IE8 support of HTML5 elements and media queries -->
	<!-- WARNING: Respond.js doesn't work if you view the page via file:// -->
	<!--[if lt IE 9]>
	      <script src="https://oss.maxcdn.com/html5shiv/3.7.2/html5shiv.min.js"></script>
	      <script src="https://oss.maxcdn.com/respond/1.4.2/respond.min.js"></script>
	    <![endif]-->
</head>
<body>
	<!-- Fixed navbar -->
	<nav class="navbar navbar-default">
		<div class="container">
			<div class="navbar-header">
				<button type="button" class="navbar-toggle collapsed" 
					data-toggle="collapse" data-target="#navbar" aria-expanded="false"
					aria-controls="navbar">
					<span class="sr-only">Toggle navigation</span>
					<span class="icon-bar"></span>
					<span class="icon-bar"></span>
					<span class="icon-bar"></span>
				</button>
				<a class="navbar-brand" href="/#">M2CAT</a>
			</div>
			<div id="navbar" class="collapse navbar-collapse">
				<ul class="nav navbar-nav">
					<li><a href="search">Search</a></li>
<!-- 					<li><a href="about">About</a></li> -->
<!-- 					<li><a href="impress">Impress</a></li> -->
				</ul>
			</div>
		</div>
	</nav>
	
	<div id="model-wait" class="container">
		<div class="progress">
			<div id="model-wait-progress" class="progress-bar" role="progressbar" aria-valuenow="60"
				aria-valuemin="0" aria-valuemax="100" style="width: 00%;">
				<span class="sr-only">Please wait...</span>
			</div>
		</div>
	</div>
	
	<div id="model-result">
		<div class="container">
			<h1 id="model-result-name">{{# print(info.model.modelName); }}&nbsp;&nbsp;</h1>
			<div class="row">
				<div class="col-md-3">model name:</div>
				<div class="col-md-9">{{# print(info.model.modelName); }}&nbsp;&nbsp;</div>
				
				<div class="col-md-3">model id:</div>
				<div class="col-md-9">{{# print(info.model.modelId); }}&nbsp;&nbsp;</div>
				
				<div class="col-md-3">model url:</div>
				<div class="col-md-9"><a href="{{# print(info.model.documentURI); }}" target="_blank">{{# print(info.model.documentURI); }}</a></div>
				
				<div class="col-md-3 col-md-offset-9 download-icons">
					<a class="img-link" href="download/{{# print(info.id); }}" target="_blank" title="Download CombineArchive">
						<img src="resources/img/ca-box-download.png" alt="Download" />
					</a>
					<% if( Config.getConfig().getWebCatUrl() != null ) { %>
					<a class="img-link" href="<%= Config.getConfig().getWebCatUrl() %>/rest/import?type=HTTP&remote={{# print(baseUrl); }}download/{{# print(info.id); }}" target="_blank" title="Open CombineArchive in webCAT">
						<img src="resources/img/ca-box.png" alt="open in webCAT" />
					</a>
					<% } %>
				</div>
			</div>
			
			<h2>Files</h2>
			{{# _.each(info.additionFiles, function(file) { }}
			
				<h3>{{# print( file.type == "publication" ? file.title : file.fileName ); }}</h3>
				<div class="row">
					<div class="col-md-3">file name:</div>
					<div class="col-md-9">{{# print(file.fileName); }}</div>
					
					{{# if( file.type != "publication" ) { }}
						<div class="col-md-3">file url:</div>
						<div class="col-md-9"><a href="{{# print(file.fileSource); }}" target="_blank">{{# print(file.fileSource); }}</a></div>
					{{# } else { }}
						<div class="col-md-3">pubmed urn:</div>
						<div class="col-md-9">{{# print(file.pubmedUrn); }}</div>
						
						<div class="col-md-3">journal:</div>
						<div class="col-md-9">{{# print(file.journal); }}</div>
						
						<div class="col-md-3">abstract:</div>
						<div class="col-md-9 abstract-text">{{# print(file.abstractText); }}</div>
					{{# } }}
					
					{{# if ( file.metaData != null ) { }}
						{{# if( file.metaData.type == "omex" ) { }}
						<div class="col-md-3">authors:</div>
						<div class="col-md-9">
							{{# _.each(file.metaData.creators, function(person) { }}
								{{# console.log(person); }}
								<b>{{# print(person.givenName); }} {{# print(person.familyName); }}</b> <i>{{# print(person.organization); }}</i><br />
							{{# }); }}
						</div>
						{{# } }}
					{{# } }}
				</div>
			{{# }); }}
		</div>
		
	</div>
	
<!-- 	<div id="model-json" class="container"> -->
<!-- 		<h2>JSON</h2> -->
<!-- 		<div class="row"> -->
<!-- 			<pre class="col-md-12" id="model-result-json"></pre> -->
<!-- 		</div> -->
<!-- 	</div> -->
	
	<footer class="footer">
		<div class="container">
			<p class="text-muted">(c) 2015 SEMS @ University Rostock</p>
		</div>
	</footer>

	<!-- jQuery (necessary for Bootstrap's JavaScript plugins) -->
	<script src="resources/js/jquery.min.js"></script>
	<!-- Include all compiled plugins (below), or include individual files as needed -->
	<script src="resources/js/bootstrap.min.js"></script>
	<!-- underscore.js -->
	<script src="resources/js/underscore.min.js"></script>
	<!-- common stuff -->
	<script src="resources/js/common.js"></script>
	<!-- Result stuff -->
	<script src="resources/js/modelresult.js"></script>
</body>
</html>