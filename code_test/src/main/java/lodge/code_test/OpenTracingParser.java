package lodge.code_test;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Stack;

import org.json.JSONException;
import org.json.JSONObject;

public class OpenTracingParser {
	public static void parseErrors_filterByID(List<JSONObject> jList) {
		List<String> idList = getErrorTraceID(jList);
		for (String s : idList) {
			List<JSONObject> oList = new ArrayList<JSONObject>();
			for (JSONObject jo : jList) {
				String tmp_id = jo.getString(LogKeywords.TRACE_ID);
				if (s.equalsIgnoreCase(tmp_id)) {
					oList.add(jo);
				}
			}

			sort(oList);
			printCascade(oList);
		}
	}

	static List<String> getErrorTraceID(List<JSONObject> jList) {
		List<String> idList = new ArrayList<String>();
		for (JSONObject jo : jList) {
			boolean errorKey = false;
			try {
				errorKey = jo.getBoolean(LogKeywords.ERROR);
			} catch (JSONException e) {
			}
			if (errorKey) {
				idList.add(jo.getString(LogKeywords.TRACE_ID));
			}
		}

		return idList;
	}

	public static void parseErrors_sortAll(List<JSONObject> jList) {
		sort(jList);

		boolean hasError = false;
		String id = "";
		List<JSONObject> oList = new ArrayList<JSONObject>();
		for (JSONObject jo : jList) {
			String tmp_id = jo.getString(LogKeywords.TRACE_ID);
			if (!tmp_id.equalsIgnoreCase(id)) {
				if (hasError) {
					printCascade(oList);
				}
				hasError = false;
				id = tmp_id;
				oList = new ArrayList<JSONObject>();
			}

			oList.add(jo);
			boolean errorKey = false;
			try {
				errorKey = jo.getBoolean(LogKeywords.ERROR);
			} catch (JSONException e) {
			}
			if (errorKey) {
				hasError = true;
			}
		}
	}

	/**
	 * output format: - <time> <app> <component> <msg>.
	 * 
	 * @param jList
	 */
	static void printCascade(List<JSONObject> jList) {
		Stack<String> stack = new Stack<String>();
		for (JSONObject jo : jList) {
			String tmp_id = jo.getString(LogKeywords.SPAN_ID);
			int pos = stack.search(tmp_id);
			if (pos == -1) {
				stack.push(tmp_id);
			}
			for (int i = 1; i < pos; i++) {
				stack.pop();
			}

			String prefix = "";
			for (int i = 0; i < stack.size() - 1; i++) {
				prefix += "\t";
			}

			System.out.println(prefix +
					"- " + jo.getString(LogKeywords.TIME) +
					" " + jo.getString(LogKeywords.APP) +
					" " + jo.getString(LogKeywords.COMPONENT) +
					" " + jo.getString(LogKeywords.MSG));
		}
	}

	/*
	 * static void print(List<JSONObject> jList) {
	 * for (JSONObject jo : jList) {
	 * System.out.println(jo.getString(LogKeywords.TRACE_ID) + ", " + jo.getString(LogKeywords.TIME)
	 * + ", " + jo.getString("span_id")
	 * + ", " + jo.getString("msg"));
	 * }
	 * System.out.println();
	 * }
	 */

	/**
	 * Sort the OpenTracing msg by:
	 * (1) "trace_id"
	 * (2) "time"
	 * 
	 * @param jArray
	 * @return
	 */
	static void sort(List<JSONObject> jList) {
		jList.sort((JSONObject a, JSONObject b) -> {
			int result = a.getString(LogKeywords.TRACE_ID).compareTo(b.getString(LogKeywords.TRACE_ID));
			if (result == 0) {
				try {
					Date a_ts = parseTimestamp(a.getString(LogKeywords.TIME));
					Date b_ts = parseTimestamp(b.getString(LogKeywords.TIME));
					result = a_ts.compareTo(b_ts);
				} catch (Exception e) {
					e.printStackTrace();
					result = 0;
				}
			}
			return result;
		});
	}

	/**
	 * transfer ISO8601 format from 2018-10-29T18:10:32+11:00
	 * to 2018-10-29T18:10:32+1100, so that let java can parse
	 * 
	 * @throws ParseException
	 */
	static Date parseTimestamp(String ts) throws ParseException {
		int idx = ts.lastIndexOf(':');
		String tmp = ts.substring(0, idx) + ts.substring(idx + 1);
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");

		return format.parse(tmp);
	}
}
