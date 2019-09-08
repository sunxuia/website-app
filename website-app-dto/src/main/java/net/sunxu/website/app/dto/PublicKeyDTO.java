package net.sunxu.website.app.dto;

import java.io.Serializable;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class PublicKeyDTO implements Serializable {

    private static final long serialVersionUID = -1L;

    private String type;

    private String publicKey;

}
