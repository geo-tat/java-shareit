package ru.practicum.shareit.item.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ItemForRequestDto {
   private int id;
   private String name;
   private String description;
   private Integer requestId;
   private Boolean available;
}
