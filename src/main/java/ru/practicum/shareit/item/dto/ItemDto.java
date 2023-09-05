package ru.practicum.shareit.item.dto;


import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.booking.dto.BookingLightDto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
@Builder
public class ItemDto {
    int id;
    @NotBlank
   private String name;
    @NotBlank
   private String description;
    @NotNull
   private Boolean available;
   private Integer requestId;
   private BookingLightDto lastBooking;
   private BookingLightDto nextBooking;
   private List<CommentDto> comments;
}
