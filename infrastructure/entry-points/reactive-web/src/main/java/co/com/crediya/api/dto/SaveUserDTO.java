package co.com.crediya.api.dto;

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
public class SaveUserDTO {

    @NotBlank(message = "Name is mandatory")
    private String name;

    @NotBlank(message = "Lastname is mandatory")
    private String lastname;

    @NotNull(message = "Birthdate is mandatory")
    private LocalDate birthdate;

    @NotBlank(message = "Address is mandatory")
    private String address;

    @NotBlank(message = "Phone is mandatory")
    private String phone;

    @Email(message = "Invalid email")
    @NotBlank(message = "Email is mandatory")
    private String email;

    @NotNull(message = "Income is mandatory")
    @DecimalMin(value = "0.0", inclusive = true, message = "Income must be positive")
    @DecimalMax(value = "15000000.0", inclusive = true, message = "Income must be equal or lesser than 15000000")
    private BigDecimal income;


}
