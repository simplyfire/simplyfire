package utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.ebean.SqlRow;
import play.libs.Json;

import java.util.ArrayList;
import java.util.List;

public class ApiHandler {

    public static ObjectNode createResponse(List<SqlRow> result)
    {
        ObjectNode jsonResponse= Json.newObject();
        ObjectNode jsonBody=Json.newObject();
        jsonBody.put("count",result.size());
        jsonBody.set("items",Json.toJson(result));
        jsonResponse.set("response",jsonBody);
        return jsonResponse;
    }

    public static ObjectNode createMissingParams(List<String> missingParams)
    {
        ObjectNode jsonResponse= Json.newObject();
        ArrayNode jsonBody=Json.newArray();
        ObjectNode jsonMissingParams=Json.newObject();
        jsonBody.addObject()
                .put("status","400")
                .put("message","Missing required params")
                .set("missed",Json.toJson(missingParams));
        jsonResponse.set("error",jsonBody);
        return jsonResponse;
    }

    public static ObjectNode createNoResult(String emptyParam)
    {
        ObjectNode jsonResponse= Json.newObject();
        ArrayNode jsonBody=Json.newArray();
        jsonBody.addObject().put("status","404").put("message","Object not found").put("not found",emptyParam);
        jsonResponse.set("error",jsonBody);
        return jsonResponse;
    }

    public static ObjectNode createAlreadyExist(String param)
    {
        ObjectNode jsonResponse= Json.newObject();
        ArrayNode jsonBody=Json.newArray();
        jsonBody.addObject().put("status","400").put("message","Object already exist").put("param",param);
        jsonResponse.set("error",jsonBody);
        return jsonResponse;
    }

    public static ObjectNode createErrorWithMessage(Integer code,String message)
    {
        ObjectNode jsonResponse= Json.newObject();
        ArrayNode jsonBody=Json.newArray();
        jsonBody.addObject().put("status", code.toString()).put("message",message);
        jsonResponse.set("error",jsonBody);
        return jsonResponse;
    }

    public static ObjectNode createResponseWithMessage(String message)
    {
        ObjectNode jsonResponse= Json.newObject();
        jsonResponse.put("response",message);
        return jsonResponse;
    }

    public static ObjectNode createResponseWithMessage(JsonNode message)
    {
        ObjectNode jsonResponse= Json.newObject();
        jsonResponse.set("response", message);
        return jsonResponse;
    }
}
