package co.com.crediya.model.user;

import lombok.*;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)

public class Role {
    
    private UUID id;
    private String name;

}
