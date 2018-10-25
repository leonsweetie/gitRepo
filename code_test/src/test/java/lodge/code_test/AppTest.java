package lodge.code_test;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import org.json.JSONArray;
import org.json.JSONObject;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Unit test for simple App.
 */
public class AppTest extends TestCase {
	List<JSONObject> jList;

	/**
	 * Create the test case
	 *
	 * @param testName name of the test case
	 * @throws FileNotFoundException
	 */
	public AppTest(String testName) throws FileNotFoundException {
		super(testName);

		String jsonFile = "./log-data.json";
		InputStream is = new FileInputStream(jsonFile);
		Scanner sc = new Scanner(is);
		String jsonContent = sc.nextLine();
		sc.close();

		jList = new ArrayList<JSONObject>();
		JSONArray jArray = new JSONArray(jsonContent);
		for (int i = 0; i < jArray.length(); i++) {
			jList.add(jArray.getJSONObject(i));
		}
	}

	/**
	 * @return the suite of tests being tested
	 */
	public static Test suite() {
		return new TestSuite(AppTest.class);
	}

	public void test_parseErrors_filterByID() throws ParseException {
		OpenTracingParser.parseErrors_filterByID(jList);
		assertTrue(true);
	}

	public void test_parseErrors_sortAll() throws ParseException {
		OpenTracingParser.parseErrors_sortAll(jList);
		assertTrue(true);
	}

	public void test_parseTimestamp() throws ParseException {
		String s = "2018-10-29T18:10:32+11:00";
		OpenTracingParser.parseTimestamp(s);

		assertTrue(true);
	}

	public void test_sort() throws ParseException {
		OpenTracingParser.sort(jList);

		assertEquals("start is not same", jList.get(0).get("trace_id"), "00063d48-57bf-451b-bdef-97a284c82f58");
		assertEquals("end is not same", jList.get(jList.size() - 1).get("trace_id"), "fdab19d8-824e-4b6b-8a43-899a9c174837");
	}

	public void test_getErrorTraceID() throws ParseException {
		List<String> idList = OpenTracingParser.getErrorTraceID(jList);

		assertEquals("fail: error msg count are not correct", idList.size(), 50);
	}
}
