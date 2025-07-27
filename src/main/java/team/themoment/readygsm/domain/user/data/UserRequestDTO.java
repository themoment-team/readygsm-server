package team.themoment.readygsm.domain.user.data;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
public class UserRequestDTO {

    private Long id;
    private String email;
    private String name;
    private String role;

    public UserRequestDTO(Long id, String email, String name, String role) {
        this.id = id;
        this.email = email;
        this.name = name;
        this.role = role;
    }
}
