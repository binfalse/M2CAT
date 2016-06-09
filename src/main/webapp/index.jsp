<!DOCTYPE html>
<%@page import="de.unirostock.sems.M2CAT.Util"%>
<html lang="en">
<head>
	<meta charset="utf-8" />
	<meta http-equiv="X-UA-Compatible" content="IE=edge" />
	<meta name="viewport" content="width=device-width, initial-scale=1" />
	<!-- The above 3 meta tags *must* come first in the head; any other head content must come *after* these tags -->
	<title>M2CAT</title>
	
	<!-- Bootstrap -->
	<link href="resources/css/bootstrap.min.css" rel="stylesheet" />
	
	<!-- main css -->
	<link href="resources/css/main.css" rel="stylesheet" /> 
	<!-- index css -->
	<link href="resources/css/index.css" rel="stylesheet" />
	
	<!-- HTML5 shim and Respond.js for IE8 support of HTML5 elements and media queries -->
	<!-- WARNING: Respond.js doesn't work if you view the page via file:// -->
	<!--[if lt IE 9]>
	      <script src="https://oss.maxcdn.com/html5shiv/3.7.2/html5shiv.min.js"></script>
	      <script src="https://oss.maxcdn.com/respond/1.4.2/respond.min.js"></script>
	    <![endif]-->
</head>
<body>
	<!-- feedback button -->
	<% String feedbackUrl = Util.getFeedbackUrl(request); 
	if( feedbackUrl != null) { %>
	<div id="feedback">
		<a href="<%=feedbackUrl%>" title="feedback"></a>
	</div>
	<% } %>
	
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
					<li class="active"><a href="search">Search</a></li>
<!-- 					<li><a href="about">About</a></li> -->
<!-- 					<li><a href="impress">Impress</a></li> -->
				</ul>
			</div>
		</div>
	</nav>

	<!-- corporate logos -->
	<div class="container hidden-xs corporate-container">
		<div class="row">
			<div class="corporate-row col-md-4 col-sm-6">
				<img src="resources/img/uni.png" class="img-responsive" alt="University Rostock" />
			</div>
			<div class="corporate-row col-md-4 col-md-offset-4 col-sm-6">
				<img src="resources/img/sems.png" class="img-responsive" alt="Simulation Experiment Management System" />
			</div>
		</div>
	</div>
	
	<!-- search form -->
	<div class="container">
		<div id="logo-row" class="row">
			<div class="col-md-6 col-md-offset-3 col-sm-8 col-sm-offset-2 col-xs-10 col-xs-offset-1">
				<img src="resources/img/m2cat.png" class="img-responsive logo" alt="M2CAT" />
			</div>
		</div>
		
		<div id="searchform-row" class="row">
			<div class="col-md-6 col-md-offset-3 col-sm-8 col-sm-offset-2 col-xs-10 col-xs-offset-1">
				<form class="form-inline" action="search" method="GET">
					<input type="text" class="form-control" name="q" placeholder="search term" />
					<input type="submit" class="btn btn-default btn-primary" value="Search" />
				
				  	<div>
					<label> Aggregation Type:</label> <br>
					
       				<label class="radio">
         				<input type="radio" name="aggregationType" class="toggle" value="DEFAULT" checked="checked"> Default
       				</label> <br>
       				<label class="radio">
         				<input type="radio" name="aggregationType" class="toggle" value="ADJACENT_PAIRS"> Adjacent Pairs
      				 </label> <br>
       				<label class="radio">
         				<input type="radio" name="aggregationType" class="toggle" value="COMB_MNZ"> CombMNZ
       				</label> <br>
       				<label class="radio">
         				<input type="radio" name="aggregationType" class="toggle" value="LOCAL_KEMENIZATION"> Local Kemenization
       				</label> <br>
       				<label class="radio">
         				<input type="radio" name="aggregationType" class="toggle" value="SUPERVISED_LOCAL_KEMENIZATION"> Supervised Local Kemenization
       				</label> <br>
       				</div>
       				<div>
					<label> Rankers weights (1 - 99):</label> <br>
					<input type="text" id="weight1" name="modelRankerWeight" placeholder="Model Index" onkeypress="return validate(event)"/> <br>
					<input type="text" id="weight2" name="annotationRankerWeight" placeholder="Annotation Index" onkeypress="return validate(event)"/> <br>
					<input type="text" id="weight3" name="personRankerWeight" placeholder="Person Index" onkeypress="return validate(event)"/> <br>
					<input type="text" id="weight4" name="publicationRankerWeight" placeholder="Publication Index" onkeypress="return validate(event)"/> <br>
					<script type="text/javascript">
					function validate(evt){
					    var charCode = (evt.which) ? evt.which : event.keyCode;
					    	    if (charCode > 31 && ((charCode <48) || (charCode > 57)))
					    	        return false
					    	    var weight = parseInt(document.getElementById("weight1").value);
					    	    if(weight > 99)
					    	    	return false;
					    	    var weight = parseInt(document.getElementById("weight2").value);
					    	    if(weight > 99)
					    	    	return false;
					    	    var weight = parseInt(document.getElementById("weight3").value);
					    	    if(weight > 99)
					    	    	return false;
					    	    var weight = parseInt(document.getElementById("weight4").value);
					    	    if(weight > 99)
					    	    	return false;
					    	    return true;
					    	}
					</script>
					</div> 
				</form>
			</div>  
		</div>
	</div>    

	<footer class="footer">
		<div class="container">
			<p class="text-muted">(c) 2015 SEMS @ University Rostock</p>
		</div>
	</footer>

	<!-- jQuery (necessary for Bootstrap's JavaScript plugins) -->
	<script src="resources/js/jquery.min.js"></script>
	<!-- Include all compiled plugins (below), or include individual files as needed -->
	<script src="resources/js/bootstrap.min.js"></script>
</body>
</html>