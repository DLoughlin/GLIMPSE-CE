package glimpseElement;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Builds year segments for shareweight interpolation tables without JavaFX dependencies.
 */
final class ShareweightInterpolationPlanner {

    static final String FUNCTION_LINEAR = "linear";
    static final String FUNCTION_FIXED = "fixed";

    private ShareweightInterpolationPlanner() {
    }

    static List<InterpolationSegment> buildSegments(List<Integer> sortedYears, int calibrationYear, int finalModelYear) {
        if (sortedYears == null || sortedYears.isEmpty()) {
            return Collections.emptyList();
        }

        List<InterpolationSegment> segments = new ArrayList<>();
        Integer firstPostCalibrationYear = getFirstPostCalibrationYear(sortedYears, calibrationYear);
        int lastYear = sortedYears.get(sortedYears.size() - 1);

        if (firstPostCalibrationYear != null) {
            // First emitted rule clears existing interpolation behavior for this region-tech path.
            segments.add(new InterpolationSegment(calibrationYear, firstPostCalibrationYear, FUNCTION_LINEAR,
                    firstPostCalibrationYear, true));

            int startIndex = sortedYears.indexOf(firstPostCalibrationYear);
            for (int i = startIndex; i < sortedYears.size() - 1; i++) {
                int toYear = sortedYears.get(i + 1);
                segments.add(new InterpolationSegment(sortedYears.get(i), toYear, FUNCTION_LINEAR, toYear, false));
            }

            segments.add(new InterpolationSegment(Math.max(lastYear, calibrationYear), finalModelYear, FUNCTION_FIXED,
                    lastYear, false));
        } else {
            // No post-calibration anchor exists; emit one fixed segment and make it the delete rule.
            segments.add(new InterpolationSegment(Math.max(lastYear, calibrationYear), finalModelYear, FUNCTION_FIXED,
                    lastYear, true));
        }

        return segments;
    }

    static Integer getFirstPostCalibrationYear(List<Integer> years, int calibrationYear) {
        if (years == null) {
            return null;
        }
        for (Integer year : years) {
            if (year != null && year > calibrationYear) {
                return year;
            }
        }
        return null;
    }

    static final class InterpolationSegment {
        private final int fromYear;
        private final int toYear;
        private final String functionName;
        private final int toValueYear;
        private final boolean deleteRule;

        InterpolationSegment(int fromYear, int toYear, String functionName, int toValueYear, boolean deleteRule) {
            this.fromYear = fromYear;
            this.toYear = toYear;
            this.functionName = functionName;
            this.toValueYear = toValueYear;
            this.deleteRule = deleteRule;
        }

        int getFromYear() {
            return fromYear;
        }

        int getToYear() {
            return toYear;
        }

        String getFunctionName() {
            return functionName;
        }

        int getToValueYear() {
            return toValueYear;
        }

        boolean isDeleteRule() {
            return deleteRule;
        }
    }
}
