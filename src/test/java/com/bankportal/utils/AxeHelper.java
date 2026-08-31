package com.bankportal.utils;

import com.deque.html.axecore.results.Results;
import com.deque.html.axecore.results.Rule;
import com.deque.html.axecore.selenium.AxeBuilder;
import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Utility class for axe-core accessibility scanning.
 *
 * Injects the axe-core JavaScript engine into the current browser page
 * via Selenium's JavascriptExecutor and runs a WCAG 2.1 accessibility
 * scan. Returns structured results containing violations, passes,
 * and incomplete checks.
 *
 * axe-core catches approximately 30-40% of WCAG 2.1 issues automatically.
 * Manual testing with assistive technologies (VoiceOver, NVDA) is required
 * for complete coverage.
 */
public class AxeHelper {

    private static final Logger log =
            LoggerFactory.getLogger(AxeHelper.class);

    private AxeHelper() {}

    /**
     * Runs a full axe-core WCAG 2.1 scan against the current page.
     *
     * @param driver WebDriver instance with the page already loaded
     * @return Results object containing violations, passes, incomplete
     */
    public static Results runScan(WebDriver driver) {
        log.info("Running axe-core accessibility scan on: {}",
                driver.getCurrentUrl());

        Results results = new AxeBuilder()
                .withTags(List.of("wcag2a", "wcag2aa", "wcag21a", "wcag21aa"))
                .analyze(driver);

        log.info("Scan complete — Violations: {}, Passes: {}, Incomplete: {}",
                results.getViolations().size(),
                results.getPasses().size(),
                results.getIncomplete().size());

        return results;
    }

    /**
     * Checks whether the scan results contain any critical or serious
     * violations. These are the two highest severity levels in axe-core:
     *
     * critical — must fix, causes complete barrier for some users
     * serious  — must fix, causes significant barrier for some users
     *
     * moderate and minor violations are flagged in logs but do not
     * fail the test — appropriate for a baseline portfolio project.
     *
     * @param results Results from runScan()
     * @return true if no critical or serious violations found
     */
    public static boolean hasNoCriticalOrSeriousViolations(
            Results results) {

        List<Rule> violations = results.getViolations();

        if (violations.isEmpty()) {
            log.info("No accessibility violations found");
            return true;
        }

        // Log all violations for visibility
        logViolations(violations);

        // Check for critical or serious violations only
        long blockers = violations.stream()
                .filter(v -> "critical".equals(v.getImpact())
                        || "serious".equals(v.getImpact()))
                .count();

        if (blockers > 0) {
            log.error("{} critical/serious accessibility violations found",
                    blockers);
            return false;
        }

        log.warn("Moderate/minor violations found but not blocking — " +
                "review separately");
        return true;
    }

    /**
     * Logs all violations with impact level, description and help URL
     * so failures are diagnosable from the test output alone.
     */
    private static void logViolations(List<Rule> violations) {
        log.warn("===== ACCESSIBILITY VIOLATIONS =====");
        violations.forEach(violation -> {
            log.warn("Impact: {} | ID: {} | Description: {}",
                    violation.getImpact(),
                    violation.getId(),
                    violation.getDescription());
            log.warn("Help: {}", violation.getHelpUrl());
            log.warn("Affected elements: {}",
                    violation.getNodes().size());
            // Log the specific CSS selector for each affected element
            violation.getNodes().forEach(node -> {
                log.warn("  Failing element: {}", node.getTarget());
                log.warn("  Failure summary: {}", node.getFailureSummary());
            });
        });
        log.warn("====================================");
    }

    /**
     * Returns a formatted summary of violations for assertion messages.
     * Used in test assertions so failures show meaningful output.
     */
    public static String formatViolationSummary(Results results) {
        List<Rule> violations = results.getViolations();
        if (violations.isEmpty()) {
            return "No violations found";
        }

        StringBuilder summary = new StringBuilder();
        summary.append(String.format("%d violation(s) found:%n",
                violations.size()));

        violations.stream()
                .filter(v -> "critical".equals(v.getImpact())
                        || "serious".equals(v.getImpact()))
                .forEach(v -> summary.append(
                        String.format("  [%s] %s — %s%n",
                                v.getImpact().toUpperCase(),
                                v.getId(),
                                v.getDescription())));

        return summary.toString();
    }
}