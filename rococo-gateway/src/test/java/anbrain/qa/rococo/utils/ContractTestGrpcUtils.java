package anbrain.qa.rococo.utils;

import com.google.protobuf.Message;
import com.google.protobuf.util.JsonFormat;

import javax.annotation.Nonnull;
import java.nio.file.Files;
import java.nio.file.Paths;

public class ContractTestGrpcUtils {

    public static <T extends com.google.protobuf.Message> T loadProtoResponse(
            String path,
            @Nonnull java.util.function.Supplier<com.google.protobuf.Message.Builder> builderSupplier
    ) throws Exception {
        return (T) loadResponseFromJson(path, builderSupplier.get()).build();
    }

    public static <T extends Message.Builder> T loadResponseFromJson(String path, T builder) {
        try {
            String responseJson = Files.readString(Paths.get(path));
            JsonFormat.parser().merge(responseJson, builder);
            return builder;
        } catch (Exception e) {
            throw new RuntimeException("Failed to load gRPC response from JSON: " + path, e);
        }
    }

    public static String loadRequestJson(String path) throws Exception {
        return Files.readString(Paths.get(path));
    }

}
