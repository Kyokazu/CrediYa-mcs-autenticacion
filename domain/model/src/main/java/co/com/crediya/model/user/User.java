package co.com.crediya.model.user;

import lombok.Builder;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class User {

    private UUID id;
    private String name;
    private String identification;
    private String lastname;
    private LocalDate birthdate;
    private String address;
    private String phone;
    private String email;
    private String password;
    private BigDecimal income;
    private UUID roleId;

}
