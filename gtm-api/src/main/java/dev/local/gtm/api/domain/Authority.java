package dev.local.gtm.api.domain;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;

/**
 * Spring Security 中要求的角色对象
 *
 * @author Peng Wang (wpcfan@gmail.com)
 */
@Data
@RequiredArgsConstructor
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "api_authority")
public class Authority implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    private String id;

    @NonNull
    @NotNull
    @Size(max = 50)
    @Indexed(unique = true)
    private String name;

}
