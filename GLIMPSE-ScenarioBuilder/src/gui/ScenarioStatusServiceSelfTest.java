package gui;

import java.lang.reflect.Method;

/**
 * Lightweight regression checks for ScenarioStatusService running-period extraction.
 */
public class ScenarioStatusServiceSelfTest {

	public static void main(String[] args) throws Exception {
		ScenarioStatusService service = new ScenarioStatusService(null, null, null);
		Method extractor = ScenarioStatusService.class.getDeclaredMethod("extractRunningPeriod", String.class);
		extractor.setAccessible(true);

		assertExtracted(extractor, service, "period 11: 2060", "11,2060");
		assertExtracted(extractor, service, "9) Solving", "9");
		assertExtracted(extractor, service, "INFO: 9) Solving", "");
		assertExtracted(extractor, service, "201) Not valid", "");
		assertExtracted(extractor, service, "period 8: 1949", "");

		System.out.println("ScenarioStatusServiceSelfTest: OK");
	}

	private static void assertExtracted(Method extractor, ScenarioStatusService service, String line, String expected)
			throws Exception {
		String actual = (String) extractor.invoke(service, line);
		if (!expected.equals(actual)) {
			throw new RuntimeException("Expected running period '" + expected + "' but got '" + actual + "' for line: " + line);
		}
	}
}
