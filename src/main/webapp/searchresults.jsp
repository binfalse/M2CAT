<!DOCTYPE html>
<%@page import="de.unirostock.sems.M2CAT.Config"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ page import="de.unirostock.sems.M2CAT.beans.*" %>
<jsp:useBean id="search" class="de.unirostock.sems.M2CAT.beans.MorreSearch" scope="request" />
<jsp:setProperty name="search" property="request" value="<%=request.getParameter(\"q\") %>" />
<jsp:setProperty name="search" property="aggregationType" value="<%=request.getParameter(\"aggregationType\") %>" />
<jsp:setProperty name="search" property="modelRankerWeight" value="<%=request.getParameter(\"modelRankerWeight\") %>" />
<jsp:setProperty name="search" property="annotationRankerWeight" value="<%=request.getParameter(\"annotationRankerWeight\") %>" />
<jsp:setProperty name="search" property="personRankerWeight" value="<%=request.getParameter(\"personRankerWeight\") %>" />
<jsp:setProperty name="search" property="publicationRankerWeight" value="<%=request.getParameter(\"publicationRankerWeight\") %>" />


<html lang="en">
<head>
	<meta charset="utf-8" />
	<meta http-equiv="X-UA-Compatible" content="IE=edge" />
	<meta name="viewport" content="width=device-width, initial-scale=1" />
	<!-- The above 3 meta tags *must* come first in the head; any other head content must come *after* these tags -->
	
	<% if(search.isValid()) {
		// trigger the search
		search.doAggregatedSearch();
	%>
		<title>M2CAT - Results for <jsp:getProperty property="request" name="search"/></title>
	<% } else {
		// redirect to start page, if no search request was transmitted
		response.setStatus( HttpServletResponse.SC_MOVED_TEMPORARILY );
		response.setHeader("Location", "./");
	}
	%>
	<script type="text/javascript">
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
		
	<!-- Bootstrap -->
	<link href="resources/css/bootstrap.min.css" rel="stylesheet" />
	
	<!-- main css -->
	<link href="resources/css/main.css" rel="stylesheet" /> 
	<!-- searchresults css -->
	<link href="resources/css/searchresults.css" rel="stylesheet" />
	
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
	
	<div class="container search-result-header">
		<h1>Found <jsp:getProperty name="search" property="resultCount" /> Results for <jsp:getProperty property="request" name="search" />: </h1>
	</div>
	
	<% int count = 1; %>
	<c:forEach items="${search.results}" var="result">
		<jsp:useBean id="result" type="de.unirostock.sems.morre.client.dataholder.ModelResult" />
		
		<div class="container search-result-container">
			<div class="row">
				<div id="result-<%=count%>" class="col-md-2 col-xs-6 search-result-count">
					<a href="#result-<%=count%>">#<%=count%></a>
					<% count++; %>
				</div>
				<div class="col-md-2 col-md-offset-8 col-xs-6 search-result-score">
				<% if( search.getAggregationType().equals("DEFAULT") ||  search.getAggregationType().equals("COMB_MNZ") ) { %>
					<fmt:formatNumber value="${result.score * 100}" minFractionDigits="1" maxFractionDigits="1" var="score" />
					score: ${score}%
					<% } %>
				</div>
			</div>
			<% String id = search.getIdFromResult(result); %>
			<h2><a href="model?id=<%=id %>" alt="${result.modelName}">${result.modelName}</a></h2>
			<div class="row">
				<div class="col-md-9 col-xs-9">
					<a href="${result.documentURI}" target="_blank">${result.documentURI}</a>
				</div>
				<div class="col-md-3 col-xs-3 download-icons">
					<a class="img-link" href="download/<%=id %>" target="_blank" title="Download CombineArchive">
						<img src="resources/img/ca-box-download.png" alt="Download" />
					</a>
					<% if( Config.getConfig().getWebCatUrl() != null ) { %>
					<a class="img-link cat-link" href="<%= Config.getConfig().getWebCatUrl() %>/rest/import?type=HTTP&remote={{#=baseUrl }}download/<%=id %>" target="_blank" title="Open CombineArchive in webCAT">
						<img src="resources/img/ca-box.png" alt="open in webCAT" />
					</a>
					<% } %>
				</div>
			</div>
		</div>
	</c:forEach>
	
	<!-- jQuery (necessary for Bootstrap's JavaScript plugins) -->
	<script src="resources/js/jquery.min.js"></script>
	<!-- Include all compiled plugins (below), or include individual files as needed -->
	<script src="resources/js/bootstrap.min.js"></script>
	<!-- underscore.js -->
	<script src="resources/js/underscore.min.js"></script>
	<!-- common stuff -->
	<script src="resources/js/common.js"></script>
	<script type="text/javascript">
		$(document).ready(function () {
			baseUrl = getBaseUrl();
			$(".cat-link").each(function () {
				var href = $(this).attr("href");
				href= href.replace(/{{#=\s*baseUrl\s*}}/, baseUrl);
				$(this).attr("href", href).show();
			});
			
			shortLinkText();
		});
	</script>
</body>
</html>