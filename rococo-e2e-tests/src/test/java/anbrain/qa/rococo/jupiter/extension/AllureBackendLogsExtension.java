package anbrain.qa.rococo.jupiter.extension;

import io.qameta.allure.Allure;
import io.qameta.allure.AllureLifecycle;
import io.qameta.allure.model.TestResult;
import lombok.SneakyThrows;

import javax.annotation.ParametersAreNonnullByDefault;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@ParametersAreNonnullByDefault
public class AllureBackendLogsExtension implements SuiteExtension {

    private static final String caseName = "Rococo backend logs";
    private static final Set<String> services = Set.of(
            "rococo-auth",
            "rococo-artist",
            "rococo-gateway",
            "rococo-museum",
            "rococo-painting",
            "rococo-userdata"
    );

    @SneakyThrows
    @Override
    public void afterSuite() {
        final AllureLifecycle allureLifecycle = Allure.getLifecycle();
        final String caseId = UUID.randomUUID().toString();
        allureLifecycle.scheduleTestCase(new TestResult().setUuid(caseId).setName(caseName));
        allureLifecycle.startTestCase(caseId);

        for (String serviceName : services) {
            addAttachmentForService(allureLifecycle, serviceName);
        }

        allureLifecycle.stopTestCase(caseId);
        allureLifecycle.writeTestCase(caseId);
    }

    private static void addAttachmentForService(AllureLifecycle allureLifecycle, String serviceName) {
        List<Path> possiblePaths = new ArrayList<>();
        possiblePaths.add(Paths.get(".", "logs", serviceName, "app.log"));
        possiblePaths.add(Paths.get("..", "logs", serviceName, "app.log"));

        for (Path logPath : possiblePaths) {
            if (Files.exists(logPath)) {
                try {
                    byte[] logContent = Files.readAllBytes(logPath);
                    allureLifecycle.addAttachment(
                            serviceName + " log",
                            "text/plain",
                            ".log",
                            new ByteArrayInputStream(logContent)
                    );
                    return;
                } catch (IOException e) {
                    System.err.println("Не удалось прочитать файл логов: " + logPath);
                }
            }
        }
    }
}
