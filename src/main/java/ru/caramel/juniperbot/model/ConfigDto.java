package ru.caramel.juniperbot.model;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;

@Getter
@Setter
public class ConfigDto implements Serializable {

    private static final long serialVersionUID = 5706403671642660929L;

    @Size(max = 20, message = "{validation.config.prefix.Size.message}")
    @NotBlank(message = "{validation.config.prefix.NotBlank.message}")
    private String prefix;

    private Long musicChannelId;

    @NotNull
    private Boolean musicPlaylistEnabled;

    @Min(0)
    private Long musicQueueLimit;

    @Min(0)
    private Long musicDurationLimit;

    @Min(0)
    private Long musicDuplicateLimit;

}