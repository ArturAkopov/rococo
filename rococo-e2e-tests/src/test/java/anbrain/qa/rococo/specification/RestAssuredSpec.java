package anbrain.qa.rococo.specification;

import anbrain.qa.rococo.api.core.CodeInterceptor;
import anbrain.qa.rococo.config.Config;
import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;

import static io.restassured.RestAssured.with;
import static io.restassured.http.ContentType.*;

public class RestAssuredSpec {

    private static final Config CFG = Config.getInstance();
    private static final CodeInterceptor CODE_INTERCEPTOR = new CodeInterceptor();

    public static RequestSpecification forService(String baseUrl, ContentType contentType, ContentType accept) {
        return with()
                .baseUri(baseUrl)
                .contentType(contentType)
                .accept(accept)
                .filter(CODE_INTERCEPTOR)
                .log().all();
    }

    public static final RequestSpecification authRequestSpec =
            forService(CFG.authUrl(), URLENC, HTML);



    public static RequestSpecification requestJsonSpec = with()
            .contentType(JSON)
            .accept(JSON)
            .log().all();

    public static ResponseSpecification response200 = new ResponseSpecBuilder()
            .expectStatusCode(200)
            .log(LogDetail.ALL)
            .build();

    public static ResponseSpecification response201 = new ResponseSpecBuilder()
            .expectStatusCode(201)
            .log(LogDetail.ALL)
            .build();

    public static ResponseSpecification response302 = new ResponseSpecBuilder()
            .expectStatusCode(302)
            .log(LogDetail.ALL)
            .build();

    public static ResponseSpecification response400 = new ResponseSpecBuilder()
            .expectStatusCode(400)
            .log(LogDetail.ALL)
            .build();

    public static ResponseSpecification response401 = new ResponseSpecBuilder()
            .expectStatusCode(401)
            .log(LogDetail.ALL)
            .build();
}