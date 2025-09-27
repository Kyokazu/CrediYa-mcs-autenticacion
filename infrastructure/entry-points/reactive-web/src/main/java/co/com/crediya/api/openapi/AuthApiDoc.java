package co.com.crediya.api.openapi;


import co.com.crediya.api.dto.ApiErrorDTO;
import co.com.crediya.api.dto.LoggedUserDTO;
import co.com.crediya.api.dto.LoginDTO;
import co.com.crediya.model.user.UserTokenInfo;
import lombok.experimental.UtilityClass;
import org.springdoc.core.fn.builders.operation.Builder;
import org.springframework.http.MediaType;

import static org.springdoc.core.fn.builders.apiresponse.Builder.responseBuilder;
import static org.springdoc.core.fn.builders.content.Builder.contentBuilder;
import static org.springdoc.core.fn.builders.requestbody.Builder.requestBodyBuilder;
import static org.springdoc.core.fn.builders.schema.Builder.schemaBuilder;

@UtilityClass
public class AuthApiDoc {

    public Builder loginDoc(Builder builder) {
        return builder
                .operationId("login")
                .description("Authenticate a user and generate a JWT")
                .tag("Auth")
                .requestBody(requestBodyBuilder()
                        .required(true)
                        .content(contentBuilder()
                                .mediaType(MediaType.APPLICATION_JSON_VALUE)
                                .schema(schemaBuilder().implementation(LoginDTO.class))))
                .response(responseBuilder().responseCode("200").description("Authentication successful, returns user token info")
                        .content(contentBuilder()
                                .mediaType(MediaType.APPLICATION_JSON_VALUE)
                                .schema(schemaBuilder().implementation(UserTokenInfo.class))))
                .response(responseBuilder().responseCode("400").description("Invalid input data or credentials")
                        .content(contentBuilder()
                                .mediaType(MediaType.APPLICATION_JSON_VALUE)
                                .schema(schemaBuilder().implementation(ApiErrorDTO.class))))
                .response(responseBuilder().responseCode("404").description("User not found")
                        .content(contentBuilder()
                                .mediaType(MediaType.APPLICATION_JSON_VALUE)
                                .schema(schemaBuilder().implementation(ApiErrorDTO.class))));
    }

    public Builder validateUserDoc(Builder builder) {
        return builder
                .operationId("validateUser")
                .description("Validate a user's JWT and return their details")
                .tag("Auth")
                .response(responseBuilder().responseCode("200").description("Token is valid, returns logged user details")
                        .content(contentBuilder()
                                .mediaType(MediaType.APPLICATION_JSON_VALUE)
                                .schema(schemaBuilder().implementation(LoggedUserDTO.class))))
                .response(responseBuilder().responseCode("401").description("Invalid or expired token")
                        .content(contentBuilder()
                                .mediaType(MediaType.APPLICATION_JSON_VALUE)
                                .schema(schemaBuilder().implementation(ApiErrorDTO.class))));
    }

}
