package co.com.crediya.api.openapi;


import co.com.crediya.api.dto.SaveUserDTO;
import lombok.experimental.UtilityClass;
import org.springframework.http.MediaType;

import static org.springdoc.core.fn.builders.apiresponse.Builder.responseBuilder;
import static org.springdoc.core.fn.builders.requestbody.Builder.requestBodyBuilder;
import static org.springdoc.core.fn.builders.content.Builder.contentBuilder;
import static org.springdoc.core.fn.builders.schema.Builder.schemaBuilder;

import org.springdoc.core.fn.builders.operation.Builder;

@UtilityClass
public class UserApiDoc {

    public Builder saveUserDoc(Builder builder) {
        return builder
                .operationId("saveUser")
                .description("Create a new user")
                .tag("User")
                .requestBody(requestBodyBuilder()
                        .required(true)
                        .content(contentBuilder()
                                .mediaType(MediaType.APPLICATION_JSON_VALUE)
                                .schema(schemaBuilder().implementation(SaveUserDTO.class))))
                .response(responseBuilder().responseCode("201").description("User created successfully")
                        .content(contentBuilder()
                                .mediaType(MediaType.APPLICATION_JSON_VALUE)
                                .schema(schemaBuilder().type("string"))))
                .response(responseBuilder().responseCode("400").description("Invalid input data")
                        .content(contentBuilder()
                                .mediaType(MediaType.APPLICATION_JSON_VALUE)
                                .schema(schemaBuilder().type("string"))));
    }

    public Builder getUserByIdentificationDoc(Builder builder) {
        return builder
                .operationId("getUserByIdentification")
                .description("Retrieve a user by identification")
                .tag("User")
                .response(responseBuilder().responseCode("200").description("User found")
                        .content(contentBuilder()
                                .mediaType(MediaType.APPLICATION_JSON_VALUE)
                                .schema(schemaBuilder().type("string"))))
                .response(responseBuilder().responseCode("404").description("User not found")
                        .content(contentBuilder()
                                .mediaType(MediaType.APPLICATION_JSON_VALUE)
                                .schema(schemaBuilder().type("string"))));
    }

    public Builder getEmailByIdentificationDoc(Builder builder) {
        return builder
                .operationId("getEmailByIdentification")
                .description("Retrieve user email by identification")
                .tag("User")
                .response(responseBuilder().responseCode("200").description("Email found")
                        .content(contentBuilder()
                                .mediaType(MediaType.APPLICATION_JSON_VALUE)
                                .schema(schemaBuilder().type("string"))))
                .response(responseBuilder().responseCode("404").description("User not found")
                        .content(contentBuilder()
                                .mediaType(MediaType.APPLICATION_JSON_VALUE)
                                .schema(schemaBuilder().type("string"))));
    }

    public Builder getUserByEmail(Builder builder) {
        return builder
                .operationId("getUserByEmail")
                .description("Retrieve a user by email")
                .tag("User")
                .response(responseBuilder().responseCode("200").description("User found")
                        .content(contentBuilder()
                                .mediaType(MediaType.APPLICATION_JSON_VALUE)
                                .schema(schemaBuilder().type("string"))))
                .response(responseBuilder().responseCode("404").description("User not found")
                        .content(contentBuilder()
                                .mediaType(MediaType.APPLICATION_JSON_VALUE)
                                .schema(schemaBuilder().type("string"))));

    }


}
