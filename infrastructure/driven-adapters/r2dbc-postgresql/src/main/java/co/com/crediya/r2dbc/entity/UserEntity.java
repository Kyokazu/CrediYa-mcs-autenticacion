package co.com.crediya.r2dbc.entity;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Table("users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserEntity {

    @Id
    @Column("id")
    private UUID id;
    private String name;
    private String lastname;
    private String identification;
    private LocalDate birthdate;
    private String address;
    private String phone;
    private String email;
    private BigDecimal income;

}