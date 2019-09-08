package net.sunxu.website.app.dto;

import java.io.Serializable;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class AppInfoCreationResultDTO implements Serializable {

    private static final long serialVersionUID = -1L;

    private Long id;

    private String name;

    private String password;

}
