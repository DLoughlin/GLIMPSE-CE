package glimpseElement;

import java.util.Arrays;
import java.util.List;

/**
 * Lightweight regression test for shareweight interpolation segment planning.
 */
public class ShareweightInterpolationPlannerSelfTest {

    public static void main(String[] args) {
        testPostCalibrationAnchor();
        testNoPostCalibrationYear();
        testSkipPreCalibrationYears();
        System.out.println("ShareweightInterpolationPlannerSelfTest: OK");
    }

    private static void testPostCalibrationAnchor() {
        List<Integer> years = Arrays.asList(2020, 2025, 2030);
        List<ShareweightInterpolationPlanner.InterpolationSegment> segments =
                ShareweightInterpolationPlanner.buildSegments(years, 2020, 2100);

        assertTrue(segments.size() == 3, "Expected 3 segments, got: " + segments.size());
        assertSegment(segments.get(0), 2020, 2025, ShareweightInterpolationPlanner.FUNCTION_LINEAR, 2025, true);
        assertSegment(segments.get(1), 2025, 2030, ShareweightInterpolationPlanner.FUNCTION_LINEAR, 2030, false);
        assertSegment(segments.get(2), 2030, 2100, ShareweightInterpolationPlanner.FUNCTION_FIXED, 2030, false);
        assertTrue(segments.get(0).getToYear() > 2020,
                "First segment to-year must be post-calibration: " + segments.get(0).getToYear());
    }

    private static void testNoPostCalibrationYear() {
        List<Integer> years = Arrays.asList(2015, 2020);
        List<ShareweightInterpolationPlanner.InterpolationSegment> segments =
                ShareweightInterpolationPlanner.buildSegments(years, 2020, 2100);

        assertTrue(segments.size() == 1, "Expected 1 segment, got: " + segments.size());
        assertSegment(segments.get(0), 2020, 2100, ShareweightInterpolationPlanner.FUNCTION_FIXED, 2020, true);
    }

    private static void testSkipPreCalibrationYears() {
        List<Integer> years = Arrays.asList(2010, 2015, 2020, 2030);
        List<ShareweightInterpolationPlanner.InterpolationSegment> segments =
                ShareweightInterpolationPlanner.buildSegments(years, 2020, 2100);

        assertTrue(segments.size() == 2, "Expected 2 segments, got: " + segments.size());
        assertSegment(segments.get(0), 2020, 2030, ShareweightInterpolationPlanner.FUNCTION_LINEAR, 2030, true);
        assertSegment(segments.get(1), 2030, 2100, ShareweightInterpolationPlanner.FUNCTION_FIXED, 2030, false);
    }

    private static void assertSegment(ShareweightInterpolationPlanner.InterpolationSegment actual,
                                      int fromYear,
                                      int toYear,
                                      String function,
                                      int toValueYear,
                                      boolean isDelete) {
        assertTrue(actual.getFromYear() == fromYear,
                "fromYear mismatch, expected " + fromYear + " got " + actual.getFromYear());
        assertTrue(actual.getToYear() == toYear,
                "toYear mismatch, expected " + toYear + " got " + actual.getToYear());
        assertTrue(function.equals(actual.getFunctionName()),
                "function mismatch, expected " + function + " got " + actual.getFunctionName());
        assertTrue(actual.getToValueYear() == toValueYear,
                "toValueYear mismatch, expected " + toValueYear + " got " + actual.getToValueYear());
        assertTrue(actual.isDeleteRule() == isDelete,
                "delete flag mismatch, expected " + isDelete + " got " + actual.isDeleteRule());
    }

    private static void assertTrue(boolean condition, String message) {
        if (!condition) {
            throw new RuntimeException(message);
        }
    }
}
