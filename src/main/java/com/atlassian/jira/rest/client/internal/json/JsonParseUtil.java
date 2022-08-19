

package com.atlassian.jira.rest.client.internal.json;

import com.atlassian.jira.rest.client.api.ExpandableProperty;
import com.atlassian.jira.rest.client.api.OptionalIterable;
import com.atlassian.jira.rest.client.api.RestClientException;
import com.atlassian.jira.rest.client.api.domain.BasicUser;
import com.google.common.base.Optional;
import com.google.common.collect.Maps;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import javax.annotation.Nullable;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;

public class JsonParseUtil {
    public static final String JIRA_DATE_TIME_PATTERN = "yyyy-MM-dd'T'HH:mm:ss.SSSZ";
    public static final DateTimeFormatter JIRA_DATE_TIME_FORMATTER = DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
    public static final DateTimeFormatter JIRA_DATE_FORMATTER = ISODateTimeFormat.date();
    public static final String SELF_ATTR = "self";

    public static <T> Collection<T> parseJsonArray(JSONArray jsonArray, JsonObjectParser<T> jsonParser) throws JSONException {
        Collection<T> res = new ArrayList(jsonArray.length());

        for(int i = 0; i < jsonArray.length(); ++i) {
            res.add(jsonParser.parse(jsonArray.getJSONObject(i)));
        }

        return res;
    }

    public static <T> OptionalIterable<T> parseOptionalJsonArray(JSONArray jsonArray, JsonObjectParser<T> jsonParser) throws JSONException {
        return jsonArray == null ? OptionalIterable.absent() : new OptionalIterable(parseJsonArray(jsonArray, jsonParser));
    }

    public static <T> T parseOptionalJsonObject(JSONObject json, String attributeName, JsonObjectParser<T> jsonParser) throws JSONException {
        JSONObject attributeObject = getOptionalJsonObject(json, attributeName);
        return attributeObject != null ? jsonParser.parse(attributeObject) : null;
    }

    public static <T> ExpandableProperty<T> parseExpandableProperty(JSONObject json, JsonObjectParser<T> expandablePropertyBuilder) throws JSONException {
        return parseExpandableProperty(json, false, expandablePropertyBuilder);
    }

    @Nullable
    public static <T> ExpandableProperty<T> parseOptionalExpandableProperty(@Nullable JSONObject json, JsonObjectParser<T> expandablePropertyBuilder) throws JSONException {
        return parseExpandableProperty(json, true, expandablePropertyBuilder);
    }

    @Nullable
    private static <T> ExpandableProperty<T> parseExpandableProperty(@Nullable JSONObject json, Boolean optional, JsonObjectParser<T> expandablePropertyBuilder) throws JSONException {
        if (json == null) {
            if (!optional) {
                throw new IllegalArgumentException("json object cannot be null while optional is false");
            } else {
                return null;
            }
        } else {
            int numItems = json.getInt("size");
            JSONArray itemsJa = json.getJSONArray("items");
            ArrayList items;
            if (itemsJa.length() > 0) {
                items = new ArrayList(numItems);

                for(int i = 0; i < itemsJa.length(); ++i) {
                    T item = expandablePropertyBuilder.parse(itemsJa.getJSONObject(i));
                    items.add(item);
                }
            } else {
                items = null;
            }

            return new ExpandableProperty(numItems, items);
        }
    }

    public static URI getSelfUri(JSONObject jsonObject) throws JSONException {
        return parseURI(jsonObject.getString("self"));
    }

    public static URI optSelfUri(JSONObject jsonObject, URI defaultUri) throws JSONException {
        String selfUri = jsonObject.optString("self", (String)null);
        return selfUri != null ? parseURI(selfUri) : defaultUri;
    }

    public static JSONObject getNestedObject(JSONObject json, String... path) throws JSONException {
        String[] arr$ = path;
        int len$ = path.length;

        for(int i$ = 0; i$ < len$; ++i$) {
            String s = arr$[i$];
            json = json.getJSONObject(s);
        }

        return json;
    }

    @Nullable
    public static JSONObject getNestedOptionalObject(JSONObject json, String... path) throws JSONException {
        for(int i = 0; i < path.length - 1; ++i) {
            String s = path[i];
            json = json.getJSONObject(s);
        }

        return json.optJSONObject(path[path.length - 1]);
    }

    public static JSONArray getNestedArray(JSONObject json, String... path) throws JSONException {
        for(int i = 0; i < path.length - 1; ++i) {
            String s = path[i];
            json = json.getJSONObject(s);
        }

        return json.getJSONArray(path[path.length - 1]);
    }

    public static JSONArray getNestedOptionalArray(JSONObject json, String... path) throws JSONException {
        for(int i = 0; json != null && i < path.length - 1; ++i) {
            String s = path[i];
            json = json.optJSONObject(s);
        }

        return json == null ? null : json.optJSONArray(path[path.length - 1]);
    }

    public static String getNestedString(JSONObject json, String... path) throws JSONException {
        for(int i = 0; i < path.length - 1; ++i) {
            String s = path[i];
            json = json.getJSONObject(s);
        }

        return json.getString(path[path.length - 1]);
    }

    public static boolean getNestedBoolean(JSONObject json, String... path) throws JSONException {
        for(int i = 0; i < path.length - 1; ++i) {
            String s = path[i];
            json = json.getJSONObject(s);
        }

        return json.getBoolean(path[path.length - 1]);
    }

    public static URI parseURI(String str) {
        try {
            return new URI(str);
        } catch (URISyntaxException var2) {
            throw new RestClientException(var2);
        }
    }

    @Nullable
    public static URI parseOptionalURI(JSONObject jsonObject, String attributeName) {
        String s = getOptionalString(jsonObject, attributeName);
        return s != null ? parseURI(s) : null;
    }

    @Nullable
    public static BasicUser parseBasicUser(@Nullable JSONObject json) throws JSONException {
        if (json == null) {
            return null;
        } else {
            // 2022-08-19, name is not found
            //String username = username = json.getString("name");

            // 2022-08-19, KKV fix, name is empty
            String username = null;
            try {
                username = json.getString("name");
            }catch(Exception e){}
            try {
                if (username==null) username = json.getString("displayName");
            }catch(Exception e){}

            if (!json.has("self") && "Anonymous".equals(username)) {
                return null;
            } else {
                URI selfUri = optSelfUri(json, BasicUser.INCOMPLETE_URI);
                return new BasicUser(selfUri, username, json.optString("displayName", (String)null));
            }
        }
    }

    public static DateTime parseDateTime(JSONObject jsonObject, String attributeName) throws JSONException {
        return parseDateTime(jsonObject.getString(attributeName));
    }

    @Nullable
    public static DateTime parseOptionalDateTime(JSONObject jsonObject, String attributeName) throws JSONException {
        String s = getOptionalString(jsonObject, attributeName);
        return s != null ? parseDateTime(s) : null;
    }

    public static DateTime parseDateTime(String str) {
        try {
            return JIRA_DATE_TIME_FORMATTER.parseDateTime(str);
        } catch (Exception var2) {
            throw new RestClientException(var2);
        }
    }

    public static DateTime parseDateTimeOrDate(String str) {
        try {
            return JIRA_DATE_TIME_FORMATTER.parseDateTime(str);
        } catch (Exception var4) {
            try {
                return JIRA_DATE_FORMATTER.parseDateTime(str);
            } catch (Exception var3) {
                throw new RestClientException(var3);
            }
        }
    }

    public static DateTime parseDate(String str) {
        try {
            return JIRA_DATE_FORMATTER.parseDateTime(str);
        } catch (Exception var2) {
            throw new RestClientException(var2);
        }
    }

    public static String formatDate(DateTime dateTime) {
        return JIRA_DATE_FORMATTER.print(dateTime);
    }

    public static String formatDateTime(DateTime dateTime) {
        return JIRA_DATE_TIME_FORMATTER.print(dateTime);
    }

    @Nullable
    public static String getNullableString(JSONObject jsonObject, String attributeName) throws JSONException {
        Object o = jsonObject.get(attributeName);
        return o == JSONObject.NULL ? null : o.toString();
    }

    @Nullable
    public static String getOptionalString(JSONObject jsonObject, String attributeName) {
        Object res = jsonObject.opt(attributeName);
        return res != JSONObject.NULL && res != null ? res.toString() : null;
    }

    @Nullable
    public static <T> T getOptionalJsonObject(JSONObject jsonObject, String attributeName, JsonObjectParser<T> jsonParser) throws JSONException {
        JSONObject res = jsonObject.optJSONObject(attributeName);
        return res != JSONObject.NULL && res != null ? jsonParser.parse(res) : null;
    }

    @Nullable
    public static JSONObject getOptionalJsonObject(JSONObject jsonObject, String attributeName) {
        JSONObject res = jsonObject.optJSONObject(attributeName);
        return res != JSONObject.NULL && res != null ? res : null;
    }

    public static Collection<String> toStringCollection(JSONArray jsonArray) throws JSONException {
        ArrayList<String> res = new ArrayList(jsonArray.length());

        for(int i = 0; i < jsonArray.length(); ++i) {
            res.add(jsonArray.getString(i));
        }

        return res;
    }

    public static Integer parseOptionInteger(JSONObject json, String attributeName) throws JSONException {
        return json.has(attributeName) ? json.getInt(attributeName) : null;
    }

    @Nullable
    public static Long getOptionalLong(JSONObject jsonObject, String attributeName) throws JSONException {
        return jsonObject.has(attributeName) ? jsonObject.getLong(attributeName) : null;
    }

    public static Optional<JSONArray> getOptionalArray(JSONObject jsonObject, String attributeName) throws JSONException {
        return jsonObject.has(attributeName) ? Optional.of(jsonObject.getJSONArray(attributeName)) : Optional.absent();
    }

    public static Map<String, URI> getAvatarUris(JSONObject jsonObject) throws JSONException {
        Map<String, URI> uris = Maps.newHashMap();
        Iterator iterator = jsonObject.keys();

        while(iterator.hasNext()) {
            Object o = iterator.next();
            if (!(o instanceof String)) {
                throw new JSONException("Cannot parse URIs: key is expected to be valid String. Got " + (o == null ? "null" : o.getClass()) + " instead.");
            }

            String key = (String)o;
            uris.put(key, parseURI(jsonObject.getString(key)));
        }

        return uris;
    }

    public static Iterator<String> getStringKeys(JSONObject json) {
        return json.keys();
    }

    public static Map<String, String> toStringMap(JSONArray names, JSONObject values) throws JSONException {
        Map<String, String> result = Maps.newHashMap();

        for(int i = 0; i < names.length(); ++i) {
            String key = names.getString(i);
            result.put(key, values.getString(key));
        }

        return result;
    }
}
