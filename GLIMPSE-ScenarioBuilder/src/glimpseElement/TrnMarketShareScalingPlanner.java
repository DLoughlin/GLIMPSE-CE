package glimpseElement;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import glimpseUtil.GLIMPSEUtils;

/**
 * Computes consistent, order-of-magnitude scaling factors {@code K = 10^n} per
 * transportation market-share policy instance without JavaFX UI dependencies.
 * <p>
 * Every row participating in the same GCAM policy market ({@code use_this_policy_name})
 * — across denominator technologies (Part 1) and numerator secondary outputs (Part 2),
 * as well as across subsectors, regions, and vintages bound to that market — must share
 * the exact same {@code K} so the physical vehicle-km share constraint and shadow-price
 * pass-through remain algebraically invariant.
 * </p>
 */
final class TrnMarketShareScalingPlanner {

    @FunctionalInterface
    interface UnscaledOutputRatioLookup {
        Double getUnscaledOutputRatio(String region, String sector, String subsector, String tech, int year);
    }

    static final class TechSelection {
        final String sector;
        final String subsector;
        final String tech;
        final boolean nested;

        TechSelection(String sector, String subsector, String tech, boolean nested) {
            this.sector = sector;
            this.subsector = subsector;
            this.tech = tech;
            this.nested = nested;
        }
    }

    private static final class PolicyMagnitudeStats {
        double minPositiveCoef = Double.POSITIVE_INFINITY;
        boolean hasZeroCoef = false;
        double minUnscaledOutputRatio = Double.POSITIVE_INFINITY;
    }

    private TrnMarketShareScalingPlanner() {
    }

    /**
     * Builds the policy/market instance identifier used in Parts 1, 2, and 3 of {@link TabMarketShare}.
     */
    static String buildPolicyInstanceName(String basePolicyName, String treatment, int regionCount, String region,
            String appliedTo, int year) {
        String resolved = basePolicyName;
        if ("each selected region".equals(treatment) && regionCount >= 2) {
            resolved = region + "_" + basePolicyName;
        }
        if ("sales".equals(appliedTo)) {
            resolved += "-" + year;
        }
        return resolved;
    }

    /**
     * Parses technology selector strings ({@code sector : subsector : tech ...}) into structured entries.
     */
    static List<TechSelection> parseTechSelections(List<String> rawItems) {
        if (rawItems == null || rawItems.isEmpty()) {
            return Collections.emptyList();
        }
        List<TechSelection> parsed = new ArrayList<>();
        for (String item : rawItems) {
            if (item == null || item.indexOf(':') < 0) {
                continue;
            }
            String[] parts = item.split(":");
            if (parts.length < 3) {
                continue;
            }
            String sector = parts[0].trim();
            String subsector = parts[1].trim();
            String tech = parts[2].trim();
            boolean nested = subsector.contains("=>") || tech.contains("=>");
            parsed.add(new TechSelection(sector, subsector, tech, nested));
        }
        return parsed;
    }

    /**
     * Returns {@code true} only when all selected numerator and denominator items belong to
     * non-nested transportation sectors that support {@code unit-price-conv} scaling.
     */
    static boolean canScaleMarket(List<String> subsetItems, List<String> supersetItems) {
        List<TechSelection> subset = parseTechSelections(subsetItems);
        List<TechSelection> superset = parseTechSelections(supersetItems);
        if (subset.isEmpty() || superset.isEmpty()) {
            return false;
        }
        for (TechSelection sel : subset) {
            if (sel.nested || !isTrnSector(sel.sector)) {
                return false;
            }
        }
        for (TechSelection sel : superset) {
            if (sel.nested || !isTrnSector(sel.sector)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Pre-computes one scaling factor {@code K} per active policy market instance ({@code use_this_policy_name})
     * based on the smallest positive share coefficient and smallest unscaled numerator output-ratio ({@code 1 / L})
     * participating in that market.
     */
    static Map<String, Double> resolveScalingByPolicy(
            String basePolicyName,
            String treatment,
            String appliedTo,
            String[] selectedRegions,
            List<Integer> modelYears,
            int calibrationYear,
            String[] targetYears,
            double[] targetValues,
            List<String> subsetItems,
            List<String> supersetItems,
            UnscaledOutputRatioLookup outputRatioLookup) {

        if (selectedRegions == null || selectedRegions.length == 0
                || modelYears == null || modelYears.isEmpty()
                || targetYears == null || targetValues == null
                || targetYears.length == 0 || targetYears.length != targetValues.length) {
            return Collections.emptyMap();
        }

        if (!canScaleMarket(subsetItems, supersetItems)) {
            return Collections.emptyMap();
        }

        List<TechSelection> subset = parseTechSelections(subsetItems);
        int[] parsedTargetYears = new int[targetYears.length];
        int minTargetYear = Integer.MAX_VALUE;
        int maxTargetYear = Integer.MIN_VALUE;
        double globalMinPositiveCoef = Double.POSITIVE_INFINITY;

        for (int i = 0; i < targetYears.length; i++) {
            int yr = Integer.parseInt(targetYears[i].trim());
            parsedTargetYears[i] = yr;
            if (yr < minTargetYear) {
                minTargetYear = yr;
            }
            if (yr > maxTargetYear) {
                maxTargetYear = yr;
            }
            double absVal = Math.abs(targetValues[i]);
            if (absVal > 0.0 && !Double.isNaN(absVal) && !Double.isInfinite(absVal) && absVal < globalMinPositiveCoef) {
                globalMinPositiveCoef = absVal;
            }
        }

        String normalizedTreatment = treatment == null ? "" : treatment.toLowerCase().trim();
        String normalizedAppliedTo = appliedTo == null ? "" : appliedTo.toLowerCase().trim();
        Map<String, PolicyMagnitudeStats> statsByPolicy = new LinkedHashMap<>();

        // Pass 1: gather active target share coefficients per policy instance (matches Part 1 row scope).
        for (String region : selectedRegions) {
            for (int t : modelYears) {
                String policyKey = buildPolicyInstanceName(
                        basePolicyName, normalizedTreatment, selectedRegions.length, region, normalizedAppliedTo, t);
                for (int i = 0; i < parsedTargetYears.length; i++) {
                    boolean activeInPart1 = ("all stock".equals(normalizedAppliedTo) && t <= parsedTargetYears[i])
                            || ("sales".equals(normalizedAppliedTo) && t == parsedTargetYears[i]);
                    if (!activeInPart1) {
                        continue;
                    }
                    PolicyMagnitudeStats stats = getOrCreateStats(statsByPolicy, policyKey);
                    double absCoef = Math.abs(targetValues[i]);
                    if (absCoef > 0.0 && !Double.isNaN(absCoef) && !Double.isInfinite(absCoef)) {
                        if (absCoef < stats.minPositiveCoef) {
                            stats.minPositiveCoef = absCoef;
                        }
                    } else if (absCoef == 0.0) {
                        stats.hasZeroCoef = true;
                    }
                }
            }
        }

        // Pass 2: gather active unscaled numerator output-ratios (1 / loadFactor) per policy instance (matches Part 2 row scope).
        for (String region : selectedRegions) {
            for (int t : modelYears) {
                boolean activeInPart2 = ("all stock".equals(normalizedAppliedTo) && t <= maxTargetYear && t >= calibrationYear)
                        || ("sales".equals(normalizedAppliedTo) && t <= maxTargetYear && t >= minTargetYear);
                if (!activeInPart2) {
                    continue;
                }
                String policyKey = buildPolicyInstanceName(
                        basePolicyName, normalizedTreatment, selectedRegions.length, region, normalizedAppliedTo, t);
                PolicyMagnitudeStats stats = getOrCreateStats(statsByPolicy, policyKey);
                if (outputRatioLookup != null) {
                    for (TechSelection sel : subset) {
                        Double unscaledOutRatio = outputRatioLookup.getUnscaledOutputRatio(
                                region, sel.sector, sel.subsector, sel.tech, t);
                        if (unscaledOutRatio != null) {
                            double ratioVal = unscaledOutRatio.doubleValue();
                            if (ratioVal > 0.0 && !Double.isNaN(ratioVal) && !Double.isInfinite(ratioVal)
                                    && ratioVal < stats.minUnscaledOutputRatio) {
                                stats.minUnscaledOutputRatio = ratioVal;
                            }
                        }
                    }
                }
            }
        }

        // Pass 3: compute one power-of-10 scaling factor K per policy market instance.
        Map<String, Double> scalingByPolicy = new LinkedHashMap<>();
        double fallbackGlobalCoef = globalMinPositiveCoef < Double.POSITIVE_INFINITY ? globalMinPositiveCoef : Double.NaN;
        for (Map.Entry<String, PolicyMagnitudeStats> entry : statsByPolicy.entrySet()) {
            PolicyMagnitudeStats stats = entry.getValue();
            double coefForScale;
            if (stats.minPositiveCoef < Double.POSITIVE_INFINITY) {
                coefForScale = stats.minPositiveCoef;
            } else if (stats.hasZeroCoef) {
                coefForScale = 0.0;
            } else {
                coefForScale = fallbackGlobalCoef;
            }
            double outRatioForScale = stats.minUnscaledOutputRatio < Double.POSITIVE_INFINITY
                    ? stats.minUnscaledOutputRatio
                    : Double.NaN;
            double k = GLIMPSEUtils.calculateTrnMarketShareScalingFactor(coefForScale, outRatioForScale);
            scalingByPolicy.put(entry.getKey(), Double.valueOf(k));
        }
        return scalingByPolicy;
    }

    /**
     * Resolves the scaling factor for a given policy market instance, returning {@code 1.0}
     * when scaling is disabled for the market (for example, non-transport or mixed markets).
     */
    static double getScalingFactorForPolicy(Map<String, Double> scalingByPolicy, String policyInstanceName) {
        if (scalingByPolicy == null || scalingByPolicy.isEmpty() || policyInstanceName == null) {
            return 1.0;
        }
        Double k = scalingByPolicy.get(policyInstanceName);
        return k != null ? k.doubleValue() : GLIMPSEUtils.DEFAULT_TRN_MARKET_SHARE_SCALING_FACTOR;
    }

    private static PolicyMagnitudeStats getOrCreateStats(Map<String, PolicyMagnitudeStats> map, String key) {
        PolicyMagnitudeStats stats = map.get(key);
        if (stats == null) {
            stats = new PolicyMagnitudeStats();
            map.put(key, stats);
        }
        return stats;
    }

    private static boolean isTrnSector(String sector) {
        return sector != null && sector.startsWith("trn");
    }
}
