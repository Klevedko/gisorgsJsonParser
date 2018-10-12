package map;

import com.google.gson.JsonElement;

public class RegionMap {
    private String regionJson;

    public RegionMap(String regionJson) {
        this.regionJson = regionJson;
    }

    public RegionMap() {
    }

    public String getRegionJson() {
        return "[\n" +regionJson + "\n]";
    }

    public void setRegionJson(JsonElement regionJson) {
        this.regionJson = this.regionJson == null ? regionJson.toString() : this.regionJson + "\n" + ',' + regionJson;
    }
}
