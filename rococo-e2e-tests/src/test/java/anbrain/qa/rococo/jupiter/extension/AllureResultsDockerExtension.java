package anbrain.qa.rococo.jupiter.extension;

import anbrain.qa.rococo.model.allure.AllureResult;
import anbrain.qa.rococo.model.allure.AllureResults;
import anbrain.qa.rococo.service.rest.AllureApiClient;
import lombok.NonNull;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.stream.Stream;

public class AllureResultsDockerExtension implements SuiteExtension {

    private static final Logger log = LoggerFactory.getLogger(AllureResultsDockerExtension.class);
    private static final boolean isDocker = "docker".equals(System.getProperty("test.env"));
    private static final AllureApiClient allureApiClient = new AllureApiClient();
    private static final String PROJECT_ID = "anbrain-rococo";
    private static final String ALLURE_RESULTS_DIRECTORY = "./rococo-e2e-tests/build/allure-results";

    @Override
    public void beforeSuite(ExtensionContext context) {
        if (isDocker) {
            allureApiClient.createProjectIfNotExist(PROJECT_ID);
            allureApiClient.cleanProject(PROJECT_ID);
        }
    }

    @Override
    public void afterSuite() {
        if (!isDocker) {
            return;
        }

        Path resultsDir = Paths.get(ALLURE_RESULTS_DIRECTORY);
        if (!Files.exists(resultsDir)) {
            log.warn("Allure results directory {} does not exist", resultsDir);
            return;
        }

        try {
            List<AllureResult> results = collectAllureResults(resultsDir);
            if (!results.isEmpty()) {
                sendResultsAndGenerateReport(results);
                log.info("Successfully processed {} Allure results", results.size());
            } else {
                log.warn("No Allure results found in {}", resultsDir);
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to process Allure results", e);
        }
    }

    @NonNull
    private List<AllureResult> collectAllureResults(Path resultsDir) throws IOException {
        final Base64.Encoder encoder = Base64.getEncoder();
        final List<AllureResult> results = new ArrayList<>();

        try (Stream<Path> filePathStream = Files.walk(resultsDir)) {
            filePathStream
                    .filter(Files::isRegularFile)
                    .forEach(filePath -> processResultFile(filePath, encoder, results));
        }
        return results;
    }

    private void processResultFile( @NonNull Path filePath,  @NonNull Base64.Encoder encoder,  @NonNull List<AllureResult> results) {
        try (InputStream is = Files.newInputStream(filePath)) {
            results.add(new AllureResult(
                    filePath.getFileName().toString(),
                    encoder.encodeToString(is.readAllBytes())
            ));
        } catch (IOException e) {
            throw new RuntimeException("Failed to process file: " + filePath, e);
        }
    }

    private void sendResultsAndGenerateReport(List<AllureResult> results) {
        allureApiClient.sendResults(PROJECT_ID, new AllureResults(results));

        allureApiClient.generateReport(
                PROJECT_ID,
                System.getenv("HEAD_COMMIT_MESSAGE"),
                System.getenv("BUILD_URL"),
                System.getenv("EXECUTION_TYPE")
        );
    }
}
