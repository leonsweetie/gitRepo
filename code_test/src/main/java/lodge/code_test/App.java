package lodge.code_test;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Scanner;

import org.json.JSONArray;
import org.json.JSONObject;

public class App {
	public static void main(String[] args) {
		String jsonFile = "./log-data.json";
		if (args.length > 0) {
			jsonFile = args[0];
		}
		System.out.println(jsonFile);
		
		InputStream is;
		try {
			is = new FileInputStream(jsonFile);
			Scanner sc = new Scanner(is);
			String jsonContent = sc.nextLine();
			sc.close();
			
			ArrayList<JSONObject> jList = new ArrayList<JSONObject>();
			JSONArray jArray = new JSONArray(jsonContent);
			for (int i = 0; i < jArray.length(); i++) {
				jList.add(jArray.getJSONObject(i));
			}
			
			OpenTracingParser.parseErrors_filterByID(jList);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
}
