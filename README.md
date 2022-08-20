# JiraTest

Demonstration:
https://youtu.be/zvdDsboQ9X8


API problem:
artifactId: jira-rest-java-client-core
version: 4.0.0

<code>
Project prj = getRestClient().getProjectClient()
             .getProject("PR").claim();
</code>

Error message:

<code>
org.codehaus.jettison.json.JSONException: JSONObject["name"] not found
</code>

For Qucik fix please include this file to your project:

<code>
com.atlassian.jira.rest.client.internal.json.JsonParseUtil
</code>