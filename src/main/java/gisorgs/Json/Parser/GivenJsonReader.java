package gisorgs.Json.Parser;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import map.JsonMap;
import map.RegionMap;
import org.json.simple.JSONArray;
import org.apache.commons.io.FileUtils;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class GivenJsonReader {
    public static ArrayList<JsonMap> jsonElements = new ArrayList<JsonMap>();
    public static ArrayList<RegionMap> regionsJson = new ArrayList<RegionMap>();
    public static Multimap<String, String> global_tag_exceptions = HashMultimap.create();
    public static Multimap<String, String> federalSubject_tag_exceptions = HashMultimap.create();
    public static Multimap<String, String> output_dirs = HashMultimap.create();
    public static String jsonPath,outputPath;

    public static void main(String[] args) {
        try {
            jsonPath = args[0];
            outputPath = args[1];
            /*jsonPath = "C:\\IdeaProjects\\gisorgsJsonParser\\gisorgs.json";
            outputPath = "C:\\IdeaProjects\\gisorgsJsonParser\\target\\";*/
        } catch (Exception argu) {
            System.out.println("Please run with 2 params : path to json and path to output");
            System.exit(1);
        }
        System.out.println("start " + new Date());
        System.out.println(" -- making maps of output folders and json tag's exceptions ---");
        full_exceptions();
        System.out.println(" -- recreate output dir ---");
        delete_and_create_output_dirs();
        System.out.println(" -- reading json --");
        read_given_file();
        // ------------------------------------ by_id block ------------------------------------
        System.out.println("by_id block");
        System.out.println("  1. making output...");
        create_output_files_by_id();
        System.out.println("end by_id block");
        // ------------------------------------ END by_id block ------------------------------------
        // ------------------------------------ by_subject block ------------------------------------
        System.out.println("by_subject block");
        System.out.println("  1. create empty REGION elements with empty JSONS...");
        create_empty_regions_json_map();
        System.out.println("  2. fill them...");
        fill_regions_json_map();
        System.out.println("  3. making output...");
        create_output_files_by_subject();
        System.out.println("end by_subject block");
        // ------------------------------------ END by_subject block ------------------------------------
        // ------------------------------------ all block ------------------------------------
        System.out.println("all block");
        System.out.println("  1. making output...");
        all();
        System.out.println("end all block");
        // ------------------------------------ END all block ------------------------------------
        System.out.println("End " + new Date());
    }

    public static void read_given_file() {
        try {
            JsonElement ele = new JsonParser().parse(new InputStreamReader(
                    new FileInputStream(jsonPath), StandardCharsets.UTF_8));
            JsonArray ar = ele.getAsJsonArray();
            for (int i = 0; i < ar.size(); i++) {
                jsonElements.add(new JsonMap(ar.get(i)));
            }
        } catch (Exception s) {
            System.out.println("read_given_file = " + s);
        }
    }

    public static void create_empty_regions_json_map() {
        for (int i = 0; i <= 200; i++)
            regionsJson.add(new RegionMap());
    }

    public static void fill_regions_json_map() {
        try {
            for (int i = 0; i < jsonElements.size(); i++) {
                JsonObject x = jsonElements.get(i).getOrgJson().getAsJsonObject();
                int parsed = Integer.parseInt(x.getAsJsonObject("federalSubject").get("gibdd_code").toString().replaceAll("\"", ""));
                // we read gibdd_code and .get regionsJson(parsed) - and append it : .setRegionJson(x) in there
                regionsJson.get(parsed).setRegionJson(remover(x));
            }
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    public static void create_output_files_by_subject() {
        for (int f = 0; f < regionsJson.size(); f++) {
            try {
                String end = regionsJson.get(f).getRegionJson();
                if (!end.equals("[\nnull\n]")) {
                    PrintWriter writer = new PrintWriter(outputPath + "/by_subject/" + String.format("%02d", f) + ".json", "UTF-8");
                    writer.println(end);
                    writer.close();
                }
            } catch (Exception x) {
                System.out.println("create_output_files_by_subject =" + x);
            }
        }
    }

    public static void create_output_files_by_id() {
        for (int f = 0; f < jsonElements.size(); f++) {
            try {
                JsonElement x = jsonElements.get(f).getOrgJson();
                int parsed = Integer.parseInt(x.getAsJsonObject().get("id").toString().replaceAll("\"", ""));
                String filename = outputPath + "/by_id/" + parsed + ".json";
                PrintWriter writer;
                if (new File(filename).exists())
                    writer = new PrintWriter(outputPath + "/by_id/" + "_duplucate_by_id_" + parsed + System.currentTimeMillis() + ".json");
                else
                    writer = new PrintWriter(filename);
                writer.println(x);
                writer.close();
            } catch (Exception x) {
                System.out.println("create_output_files_by_id =" + x);
            }
        }
    }

    public static void all() {
        JSONArray playerIds = new JSONArray();
        JsonParser parser = new JsonParser();
        try {
            for (int f = 0; f < jsonElements.size(); f++) {
                JsonObject e = jsonElements.get(f).getOrgJson().getAsJsonObject();
                JsonElement element = parser.parse(e.toString());
                JsonObject obj = element.getAsJsonObject(); //since you know it's a JsonObject
                playerIds.add(remover(obj));
            }
            PrintWriter writer = new PrintWriter(outputPath + "/all/" + "short.json", "UTF-8");
            writer.println(playerIds);
            writer.close();
        } catch (Exception ex) {
            System.out.println("all= " + ex);
        }
    }

    // delete from JsonObject all tags BUT except we need
    public static JsonObject remover(JsonObject obj) {
        Set<Map.Entry<String, JsonElement>> entries = obj.entrySet();//will return members of your object
        List<String> keyList = new ArrayList<String>();
        for (Map.Entry<String, JsonElement> entry : entries) {
            if (!global_tag_exceptions.containsValue(entry.getKey())) {
                keyList.add(entry.getKey());
            }
        }
        for (int i = 0; i < keyList.size(); i++) {
            obj.getAsJsonObject().remove(keyList.get(i));
        }
        Set<Map.Entry<String, JsonElement>> federalSubject = obj.getAsJsonObject("federalSubject").entrySet();//will return members of your object
        List<String> keyListfederalSubject = new ArrayList<String>();
        for (Map.Entry<String, JsonElement> entry : federalSubject) {
            if (!federalSubject_tag_exceptions.containsValue(entry.getKey())) {
                keyListfederalSubject.add(entry.getKey());
            }
        }
        for (int i = 0; i < keyListfederalSubject.size(); i++) {
            obj.getAsJsonObject("federalSubject").remove(keyListfederalSubject.get(i));
        }
        return obj;
    }

    public static void full_exceptions() {
        global_tag_exceptions.put("tag1", "id");
        global_tag_exceptions.put("tag2", "full_name");
        global_tag_exceptions.put("tag3", "short_name");
        global_tag_exceptions.put("tag4", "latitude");
        global_tag_exceptions.put("tag5", "longitude");
        global_tag_exceptions.put("tag6", "beds_all");
        global_tag_exceptions.put("tag7", "beds_all_vzr");
        global_tag_exceptions.put("tag8", "beds_all_ch");
        global_tag_exceptions.put("tag9", "gastroent_beds_vzr");
        global_tag_exceptions.put("tag10", "gastroent_beds_vzr_chs");
        global_tag_exceptions.put("tag11", "beds_all_chs");
        global_tag_exceptions.put("tag12", "beds_all_vzr_chs");
        global_tag_exceptions.put("tag13", "beds_all_ch_chs");
        global_tag_exceptions.put("tag14", "gastroent_beds_ch");
        global_tag_exceptions.put("tag15", "gastroent_beds_ch_chs");
        global_tag_exceptions.put("tag16", "beds_infectious");
        global_tag_exceptions.put("tag17", "beds_infectious_chs");
        global_tag_exceptions.put("tag18", "free_beds");
        global_tag_exceptions.put("tag19", "federalSubject");

        global_tag_exceptions.put("tag19", "address");
        global_tag_exceptions.put("tag19", "age_contingent");
        global_tag_exceptions.put("tag19", "departmental_affiliation");
        global_tag_exceptions.put("tag19", "email_rec");
        global_tag_exceptions.put("tag19", "tel_rec_rab");
        global_tag_exceptions.put("tag19", "tel_rec_fax");
        global_tag_exceptions.put("tag19", "travm_level");
        global_tag_exceptions.put("tag19", "infectious_vzr");
        global_tag_exceptions.put("tag19", "infectious_vzr_chs");
        global_tag_exceptions.put("tag19", "infectious_ch_chs");

        federalSubject_tag_exceptions.put("tag1", "code");
        federalSubject_tag_exceptions.put("tag1", "gibdd_code");

        output_dirs.put("folder1", "by_id");
        output_dirs.put("folder1", "by_subject");
        output_dirs.put("folder1", "all");
    }

    public static void delete_and_create_output_dirs() {
        for (Object value : output_dirs.values()) {
            File dir = new File(outputPath + value);
            if (dir.exists()) {
                try {
                    FileUtils.forceDelete(dir);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            new File(outputPath + value).mkdirs();
        }
    }
}