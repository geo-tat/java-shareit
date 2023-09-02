package ru.practicum.shareit.request.service;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.RequestNotFoundException;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.item.dto.ItemForRequestDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestFullDto;
import ru.practicum.shareit.request.mapper.RequestMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class ItemRequestServiceImpl implements ItemRequestService {
    private final ItemRequestRepository repository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    private final Sort sort = Sort.by(Sort.Direction.ASC, "created");

    @Override
    public ItemRequestDto addRequest(int userId, ItemRequestDto body) {
        User requester = userRepository.findById(userId)
                .stream()
                .findAny()
                .orElseThrow(() -> new UserNotFoundException("Пользователь c ID=" + userId + " не найден"));
        ItemRequest request = RequestMapper.toRequest(body);
        request.setRequester(requester);

        return RequestMapper.toDto(repository.save(request));
    }

    @Override
    public Collection<ItemRequestFullDto> getOwnRequests(int ownerId) {
        User owner = userRepository.findById(ownerId)
                .stream()
                .findAny()
                .orElseThrow(() -> new UserNotFoundException("Пользователь c ID=" + ownerId + " не найден"));
        Collection<ItemRequest> requests = repository.findAllByRequesterId(ownerId, sort);
        return getRequestsUtil(requests, itemRepository);
    }

    @Override
    public Collection<ItemRequestFullDto> getAllRequests(int userId, PageRequest pageRequest) {
        User user = userRepository
                .findById(userId)
                .orElseThrow(() -> new UserNotFoundException("Пользователь c ID=" + userId + " не найден."));
        Collection<ItemRequest> requests = repository.findAllByRequesterNot(user, pageRequest);
        return getRequestsUtil(requests, itemRepository);
    }

    @Override
    public ItemRequestFullDto getById(int requestId, int userId) {
        User user = userRepository
                .findById(userId)
                .orElseThrow(() -> new UserNotFoundException("Пользователь c ID=" + userId + " не найден."));
        ItemRequestFullDto request = RequestMapper.toDtoFull(repository
                .findById(requestId)
                .orElseThrow(() -> new RequestNotFoundException("Запрос с ID=" + requestId + " не найден.")));
        request.setItems(itemRepository.findAllByRequestId(requestId)
                .stream()
                .map(ItemMapper::toItemForRequestDto)
                .collect(Collectors.toList()));
        return request;
    }

    private static Collection<ItemRequestFullDto> getRequestsUtil(Collection<ItemRequest> requests, ItemRepository itemRepository) {
        Collection<Integer> requestIds = requests.stream()
                .map(ItemRequest::getId)
                .collect(Collectors.toList());
        Collection<Item> items = itemRepository.findAllItemsForRequestIds(requestIds);
        Map<Integer, List<ItemForRequestDto>> itemMap = items.stream()
                .collect(Collectors.groupingBy(
                        item -> item.getRequest().getId(),
                        Collectors.mapping(ItemMapper::toItemForRequestDto, Collectors.toList())
                ));
        Collection<ItemRequestFullDto> result = requests.stream()
                .map(RequestMapper::toDtoFull).collect(Collectors.toList());
        for (ItemRequestFullDto itemRequestFullDto : result) {

            if (itemMap.containsKey(itemRequestFullDto.getId())) {
                itemRequestFullDto.setItems(itemMap.get(itemRequestFullDto.getId()));
            } else {
                itemRequestFullDto.setItems(new ArrayList<>());
            }
        }
        return result;
    }
}
