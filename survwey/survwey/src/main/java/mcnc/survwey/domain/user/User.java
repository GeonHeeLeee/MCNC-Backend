package mcnc.survwey.domain.user;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import mcnc.survwey.domain.respond.Respond;
import mcnc.survwey.domain.enums.Gender;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "user")
public class User {

    @Id
    @Column(nullable = false, unique = true)
    private String userId;

    @Setter
    @Column(nullable = false, unique = true)
    private String email;

    @Setter
    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Gender gender;

    @Column(nullable = false)
    private LocalDate birth;

    @Column(nullable = false)
    private LocalDateTime registerDate;

    @Setter
    @Column(nullable = false)
    private String name;

    @OneToMany(mappedBy = "user")
    @JsonIgnore
    @Builder.Default
    private List<Respond> respondList = new ArrayList<>();
}
