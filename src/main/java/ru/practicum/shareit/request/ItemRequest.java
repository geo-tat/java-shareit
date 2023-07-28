package ru.practicum.shareit.request;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class ItemRequest {
    int id;
    String description;
    int requester;
    LocalDateTime created;
}
