Configure M2CAT
===============

M2CAT exposes some crucial settings as Java `ContextVariables`. Those can be set e.g. by creating a new context config in Tomcat.

```xml
<!--
	This is a sample context configuration for tomcat.
	It can be inserted into the context.xml, but it highly recommended to store it in
	
	$CATALINA_BASE/conf/[enginename]/[hostname]/M2CAT.xml
	
-->
<Context>
	
	<!-- sets the log level. available levels: DEBUG, INFO, WARN, ERROR, NONE -->
	<Parameter name="LOGLEVEL" value="ERROR" override="0" />
	
	<!-- URL to the neo4j bolt api endpoint -->
	<Parameter name="NEO4J_URL" value="bolt://localhost" override="0" />
	<!-- URL to the MaSyMoS/Morre endpoint -->
	<Parameter name="MORRE_URL" value="http://localhost:7474/morre/query" override="0" />
	<!-- URL to webCAT -->
	<Parameter name="WEBCAT_URL" value="http://webcat.sems.uni-rostock.de" override="0" />
	<!-- URL to a feedback form. {0} is the current url -->
	<Parameter name="FEEDBACK_URL" value="https://github.com/SemsProject/M2CAT/issues/new?from={0}" override="0" />
	
	<!-- prefix for temp files/folders -->
	<Parameter name="TEMP_PREFIX" value="m2cat" override="0" />
	<!-- minimal size of the thread pool -->
	<Parameter name="THREADPOOL_SIZE" value="2" override="0" />
	<!-- maximum size of the thread pool -->
	<Parameter name="THREADPOOL_MAX_SIZE" value="5" override="0" />
	<!-- time to keep unused threads alive -->
	<Parameter name="THREADPOOL_KEEPALIVE" value="60" override="0" />

</Context>
```

### `LOGLEVEL`
Sets the level of detail in the logs. Possible values are `DEBUG`, `INFO`, `WARN`, `ERROR`, `NONE`.

### `NEO4J_URL`
URL to Neo4j, running [MaSyMoS](). It is adviced to use the Neo4j Bolt driver. Currently no authentification is supported.

### `MORRE_URL`
HTTP URL to the MaSyMoS/Morre query Endpoint.

### `WEBCAT_URL`
URL to a webCAT instance, so created CombineArchives can be viewed directly there.

### `FEEDBACK_URL`
URL to a feedback form, e.g. an issue tracker. `{0}`is substituted with the URL the user is coming from.

### `TEMP_PREFIX`
Prefix to be used, when creating temporary files.

### `THREADPOOL_SIZE`
Minimum size of the thread pool for the worker, which are responsible for crawling and composing the CombineArchives

### `THREADPOOL_MAX_SIZE`
Maximum size of the thread pool, to which it can scale in case of heavy load/many requests.

### `THREADPOOL_KEEPALIVE`
Time in seconds a thread can idle before it is killed.
