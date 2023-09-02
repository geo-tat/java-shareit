package ru.practicum.shareit.request.dto;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;

@Data
@Builder
public class ItemRequestDto {
    private int id;
    @NotBlank(message = "Не заполнено поле - description.")
    private String description;
    private LocalDateTime created;

}
