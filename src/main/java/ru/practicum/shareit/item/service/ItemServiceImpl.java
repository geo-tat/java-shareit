package ru.practicum.shareit.item.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingLightDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.exception.AvailableException;
import ru.practicum.shareit.exception.ItemNotFoundException;
import ru.practicum.shareit.exception.NotYourItemException;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemRepository repository;
    private final UserRepository userRepo;
    private final BookingRepository bookingRepo;
    private final CommentRepository commentRepo;

    @Override
    @Transactional
    public ItemDto create(ItemDto itemDto, int userId) {
        Item item = ItemMapper.toItem(itemDto);
        User user = userRepo.findById(userId).stream()
                .findAny()
                .orElseThrow(() -> new UserNotFoundException("Пользователь c ID=" + userId + " не найден"));
        item.setOwner(user);
        return ItemMapper.toItemDto(repository.save(item));
    }

    @Override
    @Transactional
    public ItemDto update(ItemDto itemDto, int userId, int itemId) {
        Item item = ItemMapper.toItem(itemDto);
        User user = userRepo.findById(userId).stream()
                .findAny()
                .orElseThrow(() -> new UserNotFoundException("Пользователь c ID=" + userId + " не найден"));
        Item itemToUpdate = repository.findById(itemId).stream()
                .findAny()
                .orElseThrow(() -> new ItemNotFoundException("Предмет c ID=" + itemId + " не найден"));
        if (itemToUpdate.getOwner().getId() != user.getId()) {
            throw new NotYourItemException("Вы не являетесь владельцем данного предмета.");
        }
        if (item.getName() != null) {
            itemToUpdate.setName(item.getName());
        }
        if (item.getDescription() != null) {
            itemToUpdate.setDescription(item.getDescription());
        }
        if (item.getAvailable() != null) {
            itemToUpdate.setAvailable(item.getAvailable());
        }
        return ItemMapper.toItemDto(repository.save(itemToUpdate));
    }

    @Override
    @Transactional(readOnly = true)
    public Collection<ItemDto> getItems(int userId) {
        Collection<ItemDto> result = repository.findAllItemsByOwnerId(userId)
                .stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());

        Collection<Integer> itemIds = result.stream()
                .map(ItemDto::getId)
                .collect(Collectors.toList());

        Map<Integer, BookingLightDto> lastBookingsMap = bookingRepo.findLastBookingsForItems(itemIds,
                        LocalDateTime.now(),
                        Status.APPROVED)
                .stream()
                .collect(Collectors.toMap(
                        booking -> booking.getItem().getId(),
                        BookingMapper::toBookingLightDto
                ));
        Map<Integer, BookingLightDto> nextBookingsMap = bookingRepo.findNextBookingsForItems(itemIds,
                        LocalDateTime.now(),
                        Status.APPROVED)
                .stream()
                .collect(Collectors.toMap(
                        booking -> booking.getItem().getId(),
                        BookingMapper::toBookingLightDto, (existing, replacement) -> existing
                ));
        Map<ItemDto, List<CommentDto>> allComments = commentRepo.findAllForItems(itemIds)
                .stream()
                .collect(Collectors.groupingBy(
                        comment -> ItemMapper.toItemDto(comment.getItem()),
                        Collectors.mapping(CommentMapper::toCommentDto, Collectors.toList())
                ));
        for (ItemDto itemDto : result) {
            itemDto.setLastBooking(lastBookingsMap.get(itemDto.getId()));
            itemDto.setNextBooking(nextBookingsMap.get(itemDto.getId()));
            itemDto.setComments(commentRepo.findAllByItemId(itemDto.getId())
                    .stream()
                    .map(CommentMapper::toCommentDto)
                    .collect(Collectors.toList()));
        }

        return result.stream()
                .sorted(Comparator.comparingInt(ItemDto::getId))
                .collect(Collectors.toList());
    }


    @Override
    @Transactional(readOnly = true)
    public ItemDto get(int id, int userId) {
        Item item = repository.findById(id)
                .stream()
                .findAny()
                .orElseThrow(() -> new ItemNotFoundException("Предмет c ID=" + id + " не найден"));
        ItemDto result = ItemMapper.toItemDto(item);
        if (item.getOwner().getId() == userId) {
            //     result.setLastBooking(BookingMapper
            //             .toBookingLightDto(bookingRepo.findFirstByItemAndStartBeforeAndStatusOrderByEndDesc(item,
            //                     LocalDateTime.now(),
            //                     Status.APPROVED)));
            Booking last = bookingRepo.findFirstByItemAndStartBeforeAndStatusOrderByEndDesc(item,
                    LocalDateTime.now(),
                    Status.APPROVED);
            if (last != null) {
                BookingLightDto last2 = BookingMapper.toBookingLightDto(last);
                result.setLastBooking(last2);
            }
            //    result.setNextBooking(BookingMapper
            //            .toBookingLightDto(bookingRepo.findFirstByItemAndStartAfterAndStatusOrderByStartAsc(item,
            //                    LocalDateTime.now(),
            //                     Status.APPROVED)));
            Booking next = bookingRepo.findFirstByItemAndStartAfterAndStatusOrderByStartAsc(item,
                    LocalDateTime.now(),
                    Status.APPROVED);
            if (next != null) {
                BookingLightDto next2 = BookingMapper.toBookingLightDto(next);
                result.setNextBooking(next2);
            }
        }
        result.setComments(commentRepo.findAllByItemId(result.getId())
                .stream()
                .map(CommentMapper::toCommentDto)
                .collect(Collectors.toList()));
        return result;
    }

    @Override
    @Transactional
    public void delete(int id) {
        repository.deleteById(id);
    }

    @Override
    public Collection<ItemDto> search(String text) {
        if (text.isBlank()) {
            return new ArrayList<>();
        }
        return repository.search(text).stream().map(ItemMapper::toItemDto).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public CommentDto addComment(int userId, int itemId, CommentDto commentDto) {
        User user = userRepo.findById(userId).stream()
                .findAny()
                .orElseThrow(() -> new UserNotFoundException("Пользователь c ID=" + userId + " не найден"));
        Item item = repository.findById(itemId)
                .stream()
                .findAny()
                .orElseThrow(() -> new ItemNotFoundException("Предмет c ID=" + itemId + " не найден"));
        Comment comment = CommentMapper.toComment(commentDto);
        if (bookingRepo.findAllByBookerIdAndItemIdAndStatusEqualsAndEndIsBefore(userId, itemId, Status.APPROVED,
                LocalDateTime.now()).isEmpty()) {
            throw new AvailableException("Невозможно создать комментарий!");
        }
        comment.setItem(item);
        comment.setAuthor(user);
        commentRepo.save(comment);
        return CommentMapper.toCommentDto(comment);
    }


}
