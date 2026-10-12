package glimpseUtil;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;

/**
 * Lightweight regression checks for UtilsStatus running-period extraction.
 */
public class UtilsStatusSelfTest {

	public static void main(String[] args) throws Exception {
		testPeriodWithYear();
		testPeriodPrefixLowVerbosity();
		testLogLevelPrefixThenPeriodPrefix();
		testOutOfRangePeriodIsIgnored();
		System.out.println("UtilsStatusSelfTest: OK");
	}

	private static void testPeriodWithYear() throws Exception {
		assertLatestPeriod(
				Arrays.asList("INFO: period 7: 2055"),
				"7,2055");
	}

	private static void testPeriodPrefixLowVerbosity() throws Exception {
		assertLatestPeriod(
				Arrays.asList("3) Solving markets", "12) Solving markets"),
				"12");
	}

	private static void testLogLevelPrefixThenPeriodPrefix() throws Exception {
		assertLatestPeriod(
				Arrays.asList("INFO: DEBUG: 14) Solving period"),
				"14");
	}

	private static void testOutOfRangePeriodIsIgnored() throws Exception {
		assertLatestPeriod(
				Arrays.asList("201) Not a valid GCAM period"),
				"");
	}

	private static void assertLatestPeriod(List<String> lines, String expected) throws Exception {
		Path temp = Files.createTempFile("utils-status-period-", ".log");
		try {
			Files.write(temp, lines, StandardCharsets.UTF_8);
			UtilsStatus status = new UtilsStatus();
			status.init(null, new GLIMPSEFiles());
			String actual = status.getLatestRunningPeriod(new File(temp.toString()));
			if (!expected.equals(actual)) {
				throw new RuntimeException("Expected latest period '" + expected + "' but got '" + actual + "' for lines: " + lines);
			}
		} finally {
			Files.deleteIfExists(temp);
		}
	}
}
