package glimpseElement;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Lightweight end-to-end regression checks for shareweight interpolation CSV block assembly.
 */
public class ShareweightInterpolationCsvSelfTest {

    private static final String INTERP_HEADER_ID_DELETE = "GLIMPSETechSWInterpToDelete";
    private static final String INTERP_HEADER_ID = "GLIMPSETechSWInterpTo";
    private static final String INTERP_HEADER_ID_NEST_DELETE = "GLIMPSENestTechSWInterpToDelete-Nest";
    private static final String INTERP_HEADER_ID_NEST = "GLIMPSENestTechSWInterpTo-Nest";
    private static final String INTERP_COLUMNS = "region,sector,subsector,technology,from-year,to-year,name,overwrite-policy,to-value,scenario,scenario/world";
    private static final String INTERP_COLUMNS_NEST = "region,sector,subsector1,subsector2,technology,from-year,to-year,name,overwrite-policy,to-value,scenario,scenario/world";
    private static final String OVERWRITE_POLICY = "ALWAYS";

    public static void main(String[] args) {
        testFlatCsvBlocksIncludeDeleteFirstRule();
        testNestedCsvBlocksIncludeDeleteFirstRule();
        testNoPostCalibrationStillUsesDeleteBlock();
        System.out.println("ShareweightInterpolationCsvSelfTest: OK");
    }

    private static void testFlatCsvBlocksIncludeDeleteFirstRule() {
        int calibrationYear = 2020;
        List<Integer> years = Arrays.asList(2020, 2025, 2030);
        Map<Integer, String> valuesByYear = new LinkedHashMap<>();
        valuesByYear.put(2020, "1.0");
        valuesByYear.put(2025, "2.0");
        valuesByYear.put(2030, "3.0");

        List<ShareweightInterpolationPlanner.InterpolationSegment> segments =
                ShareweightInterpolationPlanner.buildSegments(years, calibrationYear, 2100);

        List<String> deleteRows = new ArrayList<>();
        List<String> nonDeleteRows = new ArrayList<>();
        for (ShareweightInterpolationPlanner.InterpolationSegment segment : segments) {
            String row = toFlatRow("USA", "industry", "cement", "kiln", segment,
                    valuesByYear.get(segment.getToValueYear()));
            if (segment.isDeleteRule()) {
                deleteRows.add(row);
            } else {
                nonDeleteRows.add(row);
            }
        }

        ArrayList<String> blocks = new ArrayList<>();
        appendTable(blocks, INTERP_HEADER_ID_DELETE, INTERP_COLUMNS, deleteRows);
        appendTable(blocks, INTERP_HEADER_ID, INTERP_COLUMNS, nonDeleteRows);

        String csv = String.join("\n", blocks);

        assertContains(csv, "Variable ID\n" + INTERP_HEADER_ID_DELETE);
        assertContains(csv, "USA,industry,cement,kiln,2020,2025,linear,ALWAYS,2.0,,");
        assertContains(csv, "Variable ID\n" + INTERP_HEADER_ID);
        assertContains(csv, "USA,industry,cement,kiln,2025,2030,linear,ALWAYS,3.0,,");
        assertContains(csv, "USA,industry,cement,kiln,2030,2100,fixed,ALWAYS,3.0,,");

        // Guard against regressions where first to-year equals calibration year.
        assertTrue(!csv.contains(",2020,2020,"), "Found invalid interpolation segment ending at calibration year:\n" + csv);

        for (String row : deleteRows) {
            assertTrue(countColumns(row) == 11, "Delete row must have 11 columns: " + row);
        }
        for (String row : nonDeleteRows) {
            assertTrue(countColumns(row) == 11, "Non-delete row must have 11 columns: " + row);
        }
    }

    private static void testNoPostCalibrationStillUsesDeleteBlock() {
        List<Integer> years = Arrays.asList(2015, 2020);
        List<ShareweightInterpolationPlanner.InterpolationSegment> segments =
                ShareweightInterpolationPlanner.buildSegments(years, 2020, 2100);

        assertTrue(segments.size() == 1, "Expected one segment when there is no post-calibration year");
        assertTrue(segments.get(0).isDeleteRule(), "Single fallback segment must be the delete rule");
        assertTrue(segments.get(0).getToYear() == 2100, "Fallback segment should extend to final model year");
    }

    private static void testNestedCsvBlocksIncludeDeleteFirstRule() {
        List<Integer> years = Arrays.asList(2020, 2025, 2030);
        Map<Integer, String> valuesByYear = new LinkedHashMap<>();
        valuesByYear.put(2020, "0.5");
        valuesByYear.put(2025, "0.6");
        valuesByYear.put(2030, "0.7");

        List<ShareweightInterpolationPlanner.InterpolationSegment> segments =
                ShareweightInterpolationPlanner.buildSegments(years, 2020, 2100);

        List<String> deleteRows = new ArrayList<>();
        List<String> nonDeleteRows = new ArrayList<>();
        for (ShareweightInterpolationPlanner.InterpolationSegment segment : segments) {
            String row = toNestedRow("USA", "industry", "materials", "cement", "kiln", segment,
                    valuesByYear.get(segment.getToValueYear()));
            if (segment.isDeleteRule()) {
                deleteRows.add(row);
            } else {
                nonDeleteRows.add(row);
            }
        }

        ArrayList<String> blocks = new ArrayList<>();
        appendTable(blocks, INTERP_HEADER_ID_NEST_DELETE, INTERP_COLUMNS_NEST, deleteRows);
        appendTable(blocks, INTERP_HEADER_ID_NEST, INTERP_COLUMNS_NEST, nonDeleteRows);

        String csv = String.join("\n", blocks);
        assertContains(csv, "Variable ID\n" + INTERP_HEADER_ID_NEST_DELETE);
        assertContains(csv, "USA,industry,materials,cement,kiln,2020,2025,linear,ALWAYS,0.6,,");
        assertContains(csv, "Variable ID\n" + INTERP_HEADER_ID_NEST);
        assertContains(csv, "USA,industry,materials,cement,kiln,2025,2030,linear,ALWAYS,0.7,,");
        assertContains(csv, "USA,industry,materials,cement,kiln,2030,2100,fixed,ALWAYS,0.7,,");

        for (String row : deleteRows) {
            assertTrue(countColumns(row) == 12, "Nested delete row must have 12 columns: " + row);
        }
        for (String row : nonDeleteRows) {
            assertTrue(countColumns(row) == 12, "Nested non-delete row must have 12 columns: " + row);
        }
    }

    private static String toFlatRow(String region,
                                    String sector,
                                    String subsector,
                                    String tech,
                                    ShareweightInterpolationPlanner.InterpolationSegment segment,
                                    String toValue) {
        String safeToValue = toValue == null ? "" : toValue;
        return region + "," + sector + "," + subsector + "," + tech + ","
                + segment.getFromYear() + "," + segment.getToYear() + ","
                + segment.getFunctionName() + "," + OVERWRITE_POLICY + "," + safeToValue + ",,";
    }

    private static String toNestedRow(String region,
                                      String sector,
                                      String subsector1,
                                      String subsector2,
                                      String tech,
                                      ShareweightInterpolationPlanner.InterpolationSegment segment,
                                      String toValue) {
        String safeToValue = toValue == null ? "" : toValue;
        return region + "," + sector + "," + subsector1 + "," + subsector2 + "," + tech + ","
                + segment.getFromYear() + "," + segment.getToYear() + ","
                + segment.getFunctionName() + "," + OVERWRITE_POLICY + "," + safeToValue + ",,";
    }

    private static void appendTable(List<String> rows, String variableId, String columns, List<String> dataRows) {
        if (dataRows == null || dataRows.isEmpty()) {
            return;
        }
        rows.add("");
        rows.add("INPUT_TABLE");
        rows.add("Variable ID");
        rows.add(variableId);
        rows.add("");
        rows.add(columns);
        rows.addAll(dataRows);
        rows.add("");
    }

    private static int countColumns(String row) {
        return row.split(",", -1).length;
    }

    private static void assertContains(String haystack, String needle) {
        assertTrue(haystack.contains(needle), "Expected to find:\n" + needle + "\nIn:\n" + haystack);
    }

    private static void assertTrue(boolean condition, String message) {
        if (!condition) {
            throw new RuntimeException(message);
        }
    }
}
