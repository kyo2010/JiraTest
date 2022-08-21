# JiraTest

Demonstration:
https://youtu.be/zvdDsboQ9X8

Two clients for automation:
<pre>
JiraAutomationJavaAPI - based on Java Jira API
JiraAutomationRest - based on Jira API
</pre>

<pre>
API problem:
artifactId: jira-rest-java-client-core
version: 4.0.0
</pre>
<code>
Project prj = getRestClient().getProjectClient().getProject("PR").claim();
</code>

Error message:
<code>
org.codehaus.jettison.json.JSONException: JSONObject["name"] not found
</code>

For Qucik fix please include this file to your project:
<code>
com.atlassian.jira.rest.client.internal.json.JsonParseUtil
</code>

or please use:
<pre>
artifactId: jira-rest-java-client-core 
Version: 5.2.0
</pre>

Add an alternative Rest Client without Jira Java API.