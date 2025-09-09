package co.com.crediya.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "DTO for creating a new user in the system")
public class SaveUserDTO {

    @NotBlank(message = "Name is mandatory")
    @Schema(description = "User's first name", example = "John")
    private String name;

    @NotBlank(message = "Lastname is mandatory")
    @Schema(description = "User's last name", example = "Doe")
    private String lastname;

    @NotBlank(message = "Identification is mandatory")
    @Schema(description = "Unique identification number of the user", example = "1020304050")
    private String identification;

    @NotNull(message = "Birthdate is mandatory")
    @Schema(description = "User's date of birth (ISO-8601 format)", example = "1995-08-15")
    private LocalDate birthdate;

    @NotBlank(message = "Address is mandatory")
    @Schema(description = "Residential address of the user", example = "123 Main St, Apt 4B")
    private String address;

    @NotBlank(message = "Phone is mandatory")
    @Schema(description = "User's phone number", example = "+1 555-123-4567")
    private String phone;

    @Email(message = "Invalid email")
    @NotBlank(message = "Email is mandatory")
    @Schema(description = "User's email address", example = "john.doe@example.com")
    private String email;

    @NotBlank(message = "Password is mandatory")
    @Schema(description = "User's Password", example = "1234SecuredPassword")
    private String password;

    @NotNull(message = "Income is mandatory")
    @DecimalMin(value = "0.0", inclusive = true, message = "Income must be positive")
    @DecimalMax(value = "15000000.0", inclusive = true, message = "Income must be equal or less than 15,000,000")
    @Schema(description = "Monthly income declared by the user (max 15,000,000)", example = "4500.00")
    private BigDecimal income;

    @NotNull(message = "Role is mandatory")
    @Schema(description = "Role assigned to the user. Must exist in the roles table", example = "CLIENT")
    private String role;
}