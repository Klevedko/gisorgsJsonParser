package map;

import com.google.gson.JsonElement;

public class JsonMap {
    private JsonElement orgJson;

    public JsonMap(JsonElement orgJson) {
        this.orgJson = orgJson;
    }

    public JsonElement getOrgJson() {
        return orgJson;
    }

    public void setOrgJson(JsonElement orgJson) {
        this.orgJson = orgJson;
    }
}
