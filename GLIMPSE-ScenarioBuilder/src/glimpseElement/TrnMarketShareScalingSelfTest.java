package glimpseElement;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import glimpseUtil.GLIMPSEUtils;
import glimpseUtil.GLIMPSEVariables;

/**
 * Lightweight regression checks for dynamic magnitude-based transportation market-share scaling.
 */
public class TrnMarketShareScalingSelfTest {

    public static void main(String[] args) {
        testMagnitudeSensitivityAcrossShareAndLoadFactor();
        testBoundsAndEdgeInputs();
        testSalesMarketsScalePerTargetYear();
        testMultiSubsectorMarketSharesSingleConsistentK();
        testAllStockAcrossRegionsSharesSingleConsistentK();
        testNonTransportAndNestedMarketsAreNotScaled();
        testGcam85PlusPart1AndPart2AlgebraicInvariance();
        testPreGcam85UnitConversionsAndScalingInvariance();
        testGcamVersionFlagSwitchesBetweenPre85And85Plus();
        System.out.println("TrnMarketShareScalingSelfTest: OK");
    }

    private static void testMagnitudeSensitivityAcrossShareAndLoadFactor() {
        // Passenger car (L = 1.58 -> 1/L ~= 0.6329): 0.03 share gets 10x larger K than 0.30 share.
        double carUnscaledRatio = 1.0 / 1.58;
        double kCar003 = GLIMPSEUtils.calculateTrnMarketShareScalingFactor(0.03, carUnscaledRatio);
        double kCar030 = GLIMPSEUtils.calculateTrnMarketShareScalingFactor(0.30, carUnscaledRatio);
        assertEquals(1000.0, kCar003, 1e-9, "Expected K=1000 for LDV car at s=0.03");
        assertEquals(100.0, kCar030, 1e-9, "Expected K=100 for LDV car at s=0.30");

        // Heavy truck (L = 11.0 -> 1/L ~= 0.0909): smaller output-ratio requires 10x larger K than car.
        double hvyUnscaledRatio = 1.0 / 11.0;
        double kHvy003 = GLIMPSEUtils.calculateTrnMarketShareScalingFactor(0.03, hvyUnscaledRatio);
        double kHvy030 = GLIMPSEUtils.calculateTrnMarketShareScalingFactor(0.30, hvyUnscaledRatio);
        assertEquals(10000.0, kHvy003, 1e-9, "Expected K=10000 for heavy truck at s=0.03");
        assertEquals(1000.0, kHvy030, 1e-9, "Expected K=1000 for heavy truck at s=0.30");

        // Light freight truck (L = 0.27 -> 1/L ~= 3.7037): larger output-ratio needs smaller K.
        double ltUnscaledRatio = 1.0 / 0.27;
        double kLt003 = GLIMPSEUtils.calculateTrnMarketShareScalingFactor(0.03, ltUnscaledRatio);
        double kLt030 = GLIMPSEUtils.calculateTrnMarketShareScalingFactor(0.30, ltUnscaledRatio);
        assertEquals(100.0, kLt003, 1e-9, "Expected K=100 for light truck at s=0.03");
        assertEquals(10.0, kLt030, 1e-9, "Expected K=10 for light truck at s=0.30");

        // Freight rail (L = 3220 -> 1/L ~= 3.1e-4): high load factor scales up to keep residuals > 1e-4.
        double railUnscaledRatio = 1.0 / 3220.0;
        double kRail030 = GLIMPSEUtils.calculateTrnMarketShareScalingFactor(0.30, railUnscaledRatio);
        assertEquals(100000.0, kRail030, 1e-9, "Expected K=100000 for freight rail at s=0.30");
    }

    private static void testBoundsAndEdgeInputs() {
        // Already large product needs no scaling beyond K = 1.
        assertEquals(1.0, GLIMPSEUtils.calculateTrnMarketShareScalingFactor(0.80, 20.0), 1e-9,
                "Large unscaled product should clamp to K=1");
        // Extremely tiny product clamps cleanly at 1e6.
        assertEquals(1.0e6, GLIMPSEUtils.calculateTrnMarketShareScalingFactor(1.0e-5, 1.0e-5), 1e-9,
                "Tiny unscaled product should clamp to K=1e6");
        // Missing metadata falls back cleanly to 1000.
        assertEquals(1000.0, GLIMPSEUtils.calculateTrnMarketShareScalingFactor(Double.NaN, Double.NaN), 1e-9,
                "Missing inputs should fall back to default K=1000");
    }

    private static void testSalesMarketsScalePerTargetYear() {
        List<Integer> modelYears = Arrays.asList(2021, 2025, 2030, 2050);
        String[] regions = { "NJ" };
        String[] targetYears = { "2025", "2050" };
        double[] targetValues = { 0.03, 0.30 };
        List<String> subset = Arrays.asList("trn_freight_road : Heavy truck : BEV");
        List<String> superset = Arrays.asList(
                "trn_freight_road : Heavy truck : BEV",
                "trn_freight_road : Heavy truck : Liquids");

        Map<String, Double> scalingByPolicy = TrnMarketShareScalingPlanner.resolveScalingByPolicy(
                "mktShr_HDV_Hvy",
                "each selected region",
                "sales",
                regions,
                modelYears,
                2021,
                targetYears,
                targetValues,
                subset,
                superset,
                (region, sector, subsector, tech, year) -> Double.valueOf(1.0 / (year == 2025 ? 10.7 : 11.9)));

        assertEquals(10000.0,
                TrnMarketShareScalingPlanner.getScalingFactorForPolicy(scalingByPolicy, "mktShr_HDV_Hvy-2025"),
                1e-9, "2025 sales market (s=0.03) should use K=10000");
        assertEquals(1000.0,
                TrnMarketShareScalingPlanner.getScalingFactorForPolicy(scalingByPolicy, "mktShr_HDV_Hvy-2050"),
                1e-9, "2050 sales market (s=0.30) should use K=1000");
    }

    private static void testMultiSubsectorMarketSharesSingleConsistentK() {
        List<Integer> modelYears = Arrays.asList(2021, 2030);
        String[] regions = { "NJ" };
        String[] targetYears = { "2030" };
        double[] targetValues = { 0.30 };
        List<String> subset = Arrays.asList(
                "trn_freight_road : Light truck : BEV",
                "trn_freight_road : Medium truck : BEV",
                "trn_freight_road : Heavy truck : BEV");
        List<String> superset = Arrays.asList(
                "trn_freight_road : Light truck : BEV",
                "trn_freight_road : Light truck : Liquids",
                "trn_freight_road : Medium truck : BEV",
                "trn_freight_road : Medium truck : Liquids",
                "trn_freight_road : Heavy truck : BEV",
                "trn_freight_road : Heavy truck : Liquids");

        Map<String, Double> loadBySubsector = new HashMap<>();
        loadBySubsector.put("Light truck", Double.valueOf(0.27));
        loadBySubsector.put("Medium truck", Double.valueOf(1.84));
        loadBySubsector.put("Heavy truck", Double.valueOf(11.0));

        Map<String, Double> scalingByPolicy = TrnMarketShareScalingPlanner.resolveScalingByPolicy(
                "mktShr_HDV_EV",
                "each selected region",
                "sales",
                regions,
                modelYears,
                2021,
                targetYears,
                targetValues,
                subset,
                superset,
                (region, sector, subsector, tech, year) -> Double.valueOf(1.0 / loadBySubsector.get(subsector).doubleValue()));

        // Heavy truck (1/11.0) governs the shared market scaling factor K=1000 for all three truck classes.
        double sharedK = TrnMarketShareScalingPlanner.getScalingFactorForPolicy(scalingByPolicy, "mktShr_HDV_EV-2030");
        assertEquals(1000.0, sharedK, 1e-9, "Multi-subsector HDV market must use a single shared K=1000");
    }

    private static void testAllStockAcrossRegionsSharesSingleConsistentK() {
        List<Integer> modelYears = Arrays.asList(2021, 2025, 2030, 2050);
        String[] regions = { "NJ", "NY" };
        String[] targetYears = { "2025", "2050" };
        double[] targetValues = { 0.03, 0.50 };
        List<String> subset = Arrays.asList("trn_pass_road_LDV_4W : Car : BEV");
        List<String> superset = Arrays.asList(
                "trn_pass_road_LDV_4W : Car : BEV",
                "trn_pass_road_LDV_4W : Car : Liquids");

        Map<String, Double> scalingByPolicy = TrnMarketShareScalingPlanner.resolveScalingByPolicy(
                "mktShr_LDV_Car_stock",
                "across selected regions",
                "all stock",
                regions,
                modelYears,
                2021,
                targetYears,
                targetValues,
                subset,
                superset,
                (region, sector, subsector, tech, year) -> Double.valueOf(1.0 / 1.58));

        assertTrue(scalingByPolicy.size() == 1, "Across-regions all-stock policy must have exactly one policy key");
        assertEquals(1000.0,
                TrnMarketShareScalingPlanner.getScalingFactorForPolicy(scalingByPolicy, "mktShr_LDV_Car_stock"),
                1e-9, "All-stock policy should use K=1000 driven by smallest positive share 0.03");
    }

    private static void testNonTransportAndNestedMarketsAreNotScaled() {
        List<Integer> modelYears = Arrays.asList(2021, 2030);
        String[] regions = { "NJ" };
        String[] targetYears = { "2030" };
        double[] targetValues = { 0.30 };

        List<String> eguSubset = Arrays.asList("base load generation : solar=>pv : pv");
        List<String> eguSuperset = Arrays.asList("base load generation : coal=>pul : coal");
        assertTrue(!TrnMarketShareScalingPlanner.canScaleMarket(eguSubset, eguSuperset),
                "Nested EGU market must not use transport scaling");
        Map<String, Double> empty = TrnMarketShareScalingPlanner.resolveScalingByPolicy(
                "mktShr_RPS", "each selected region", "all stock", regions, modelYears, 2021,
                targetYears, targetValues, eguSubset, eguSuperset, null);
        assertTrue(empty.isEmpty(), "Non-transport market scaling map must be empty");
        assertEquals(1.0, TrnMarketShareScalingPlanner.getScalingFactorForPolicy(empty, "mktShr_RPS"), 1e-9,
                "Disabled scaling map should return K=1.0");
    }

    private static void testGcam85PlusPart1AndPart2AlgebraicInvariance() {
        GLIMPSEUtils testUtils = new GLIMPSEUtils() {
            @Override
            public boolean shouldApplyTrnUnitPriceConversion(String sector) {
                return false; // GCAM 8.5+ mode
            }

            @Override
            public String getLoadFactor(String region, String sector, String subsector, String tech, String year) {
                return "10.7";
            }
        };

        double targetShare = 0.03;
        Double unscaledOutRatio = testUtils.getUnscaledTrnOutputRatio("NJ", "trn_freight_road", "Heavy truck", "BEV", 2025);
        assertTrue(unscaledOutRatio != null, "Expected non-null unscaled output-ratio");

        double k = testUtils.getTrnMarketShareScalingFactor(targetShare, unscaledOutRatio.doubleValue());
        assertEquals(10000.0, k, 1e-9, "Expected K=10000 for s=0.03, L=10.7");

        // Part 1 (GCAM 8.5+): effective multiplier == K, scaled coef * price-unit-conv == targetShare
        double effectiveMult = testUtils.getEffectiveTrnPart1Multiplier("trn_freight_road", k);
        assertEquals(k, effectiveMult, 1e-9, "GCAM 8.5+ Part-1 multiplier should equal K");
        double scaledCoef = targetShare * effectiveMult;
        double priceUnitConv = Double.parseDouble(testUtils.formatTrnPriceUnitConversion(effectiveMult));
        assertEquals(targetShare, scaledCoef * priceUnitConv, 1e-9,
                "Denominator cost pass-through must equal unscaled target share");

        // Part 2 (GCAM 8.5+): output-ratio == K / L and pMultiplier == L / K
        String conversions = testUtils.getSubsectorConversions("NJ", "trn_freight_road", "Heavy truck", "BEV", 2025, k);
        assertTrue(conversions != null, "Expected non-null conversion string");
        String[] parts = conversions.split(",");
        assertTrue(parts.length == 2, "Expected output-ratio,pMultiplier");
        double outputRatio = Double.parseDouble(parts[0]);
        double pMultiplier = Double.parseDouble(parts[1]);

        assertEquals(k / 10.7, outputRatio, 1e-2, "output-ratio must match K / L");
        assertEquals(10.7 / k, pMultiplier, 1e-7, "pMultiplier must match L / K");
        assertEquals(1.0, outputRatio * pMultiplier, 1e-5, "output-ratio * pMultiplier must remain ~1.0");
        assertEquals(targetShare * 10.7, scaledCoef / outputRatio, 1e-4,
                "Market clearing ratio (adjcoef / output-ratio) must equal s * L_BEV");
    }

    private static void testPreGcam85UnitConversionsAndScalingInvariance() {
        GLIMPSEUtils pre85Utils = new GLIMPSEUtils() {
            @Override
            public boolean shouldApplyTrnUnitPriceConversion(String sector) {
                return sector != null && sector.startsWith("trn"); // Pre-GCAM 8.5 mode
            }

            @Override
            public String getLoadFactor(String region, String sector, String subsector, String tech, String year) {
                return "10.7";
            }
        };

        double loadFactor = 10.7;
        double targetShare = 0.03;

        // 1. Verify exact legacy pre-GCAM 8.5 unit conversions when K = 1.0
        double baseMult = pre85Utils.getEffectiveTrnPart1Multiplier("trn_freight_road", 1.0);
        assertEquals(1000.0, baseMult, 1e-9, "Pre-8.5 base Part-1 multiplier must be 1000");
        assertEquals(0.001, Double.parseDouble(pre85Utils.formatTrnPriceUnitConversion(baseMult)), 1e-12,
                "Pre-8.5 base unit-price-conv must be 1e-3");

        String baseConv = pre85Utils.getSubsectorConversions("NJ", "trn_freight_road", "Heavy truck", "BEV", 2025, 1.0);
        assertTrue(baseConv != null, "Expected non-null pre-8.5 base conversion string");
        String[] baseParts = baseConv.split(",");
        double baseOutRatio = Double.parseDouble(baseParts[0]);
        double basePMult = Double.parseDouble(baseParts[1]);
        assertEquals((1.0e-6 / loadFactor) * 1.055, baseOutRatio, 1e-12,
                "Pre-8.5 base output-ratio must match (1e-6 / L) * 1.055");
        assertEquals(1.0e6, basePMult, 1e-6, "Pre-8.5 base pMultiplier must be 1e6");

        // 2. Verify pre-GCAM 8.5 unit conversions composed with dynamic K = 10000
        double k = pre85Utils.getTrnMarketShareScalingFactor(targetShare, 1.0 / loadFactor);
        assertEquals(10000.0, k, 1e-9, "Expected K=10000 for s=0.03, L=10.7");

        double scaledMult = pre85Utils.getEffectiveTrnPart1Multiplier("trn_freight_road", k);
        assertEquals(1.0e7, scaledMult, 1e-3, "Pre-8.5 scaled Part-1 multiplier must be 1000 * K");
        double scaledCoef = targetShare * scaledMult;
        double scaledPriceConv = Double.parseDouble(pre85Utils.formatTrnPriceUnitConversion(scaledMult));
        assertEquals(targetShare, scaledCoef * scaledPriceConv, 1e-9,
                "Pre-8.5 denominator cost pass-through must remain invariant at s");

        String scaledConv = pre85Utils.getSubsectorConversions("NJ", "trn_freight_road", "Heavy truck", "BEV", 2025, k);
        assertTrue(scaledConv != null, "Expected non-null pre-8.5 scaled conversion string");
        String[] scaledParts = scaledConv.split(",");
        double scaledOutRatio = Double.parseDouble(scaledParts[0]);
        double scaledPMult = Double.parseDouble(scaledParts[1]);

        assertEquals(k * (1.0e-6 / loadFactor) * 1.055, scaledOutRatio, 1e-9,
                "Pre-8.5 scaled output-ratio must match K * (1e-6 / L) * 1.055");
        assertEquals(1.0e6 / k, scaledPMult, 1e-7,
                "Pre-8.5 scaled pMultiplier must match 1e6 / K");
        assertEquals(1.055 / loadFactor, scaledOutRatio * scaledPMult, 1e-6,
                "Pre-8.5 output-ratio * pMultiplier must preserve 1.055 / L");

        // Verify GCAM pre-8.5 internal BTU/vkt -> EJ/million-service-km demand balance:
        // (scaledCoef * 1.055e-9 / L) == targetShare * scaledOutRatio
        double internalDemandPerMillionServiceKm = scaledCoef * (1.055e-9 / loadFactor);
        assertEquals(targetShare * scaledOutRatio, internalDemandPerMillionServiceKm, 1e-9,
                "Pre-8.5 internal demand per million service-km must equal s * output-ratio");
    }

    private static void testGcamVersionFlagSwitchesBetweenPre85And85Plus() {
        GLIMPSEVariables vars = GLIMPSEVariables.getInstance();
        boolean prevFlag = vars.isGcamVersionPre8_5();
        try {
            GLIMPSEUtils utilsWithVars = new GLIMPSEUtils() {
                @Override
                public String getLoadFactor(String region, String sector, String subsector, String tech, String year) {
                    return "1.58";
                }
            };
            utilsWithVars.vars = vars;

            vars.setGcamVersionPre8_5(true);
            assertTrue(utilsWithVars.shouldApplyTrnUnitPriceConversion("trn_pass_road_LDV_4W"),
                    "Pre-8.5 flag=true must enable TrnUnitPriceConversion");
            assertEquals(1000.0 * 100.0, utilsWithVars.getEffectiveTrnPart1Multiplier("trn_pass_road_LDV_4W", 100.0), 1e-9,
                    "Pre-8.5 Part-1 multiplier must include 1000x unit conversion * K");
            String pre85Part2 = utilsWithVars.getSubsectorConversions("NJ", "trn_pass_road_LDV_4W", "Car", "BEV", 2030, 100.0);
            assertEquals(100.0 * (1.0e-6 / 1.58) * 1.055, Double.parseDouble(pre85Part2.split(",")[0]), 1e-9,
                    "Pre-8.5 Part-2 output-ratio must use MMBTU/million-service-km conversion * K");
            assertEquals(1.0e6 / 100.0, Double.parseDouble(pre85Part2.split(",")[1]), 1e-6,
                    "Pre-8.5 Part-2 pMultiplier must be 1e6 / K");

            vars.setGcamVersionPre8_5(false);
            assertTrue(!utilsWithVars.shouldApplyTrnUnitPriceConversion("trn_pass_road_LDV_4W"),
                    "Pre-8.5 flag=false must disable TrnUnitPriceConversion");
            assertEquals(100.0, utilsWithVars.getEffectiveTrnPart1Multiplier("trn_pass_road_LDV_4W", 100.0), 1e-9,
                    "GCAM 8.5+ Part-1 multiplier must equal K without 1000x MMBTU factor");
            String gcam85Part2 = utilsWithVars.getSubsectorConversions("NJ", "trn_pass_road_LDV_4W", "Car", "BEV", 2030, 100.0);
            assertEquals(100.0 / 1.58, Double.parseDouble(gcam85Part2.split(",")[0]), 1e-3,
                    "GCAM 8.5+ Part-2 output-ratio must equal K / L");
            assertEquals(1.58 / 100.0, Double.parseDouble(gcam85Part2.split(",")[1]), 1e-7,
                    "GCAM 8.5+ Part-2 pMultiplier must equal L / K");
        } finally {
            vars.setGcamVersionPre8_5(prevFlag);
        }
    }

    private static void assertEquals(double expected, double actual, double tol, String message) {
        if (Math.abs(expected - actual) > tol) {
            throw new RuntimeException(message + " (expected=" + expected + ", actual=" + actual + ")");
        }
    }

    private static void assertTrue(boolean condition, String message) {
        if (!condition) {
            throw new RuntimeException(message);
        }
    }
}
