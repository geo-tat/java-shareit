package ru.practicum.shareit.item.dto;


import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.booking.dto.BookingLightDto;

import java.util.List;

@Data
@Builder
public class ItemDto {
    int id;
   private String name;
   private String description;
   private Boolean available;
   private Integer requestId;
   private BookingLightDto lastBooking;
   private BookingLightDto nextBooking;
   private List<CommentDto> comments;
}
