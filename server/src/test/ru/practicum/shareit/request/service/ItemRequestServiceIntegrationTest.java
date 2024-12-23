package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.request.dto.ItemRequestCreateDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDtoWithAnswers;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Transactional
class ItemRequestServiceIntegrationTest {
    private final ItemRequestServiceImpl itemRequestService;
    private final ItemRequestRepository itemRequestRepository;
    private final UserRepository userRepository;

    private User user;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .name("TestUser")
                .email("TestUserEmail@test.com")
                .build();

        userRepository.save(user);
    }

    @Test
    void createItemRequestTest() {
        ItemRequestCreateDto requestCreateDtoDto = ItemRequestCreateDto.builder()
                .description("Test request description")
                .build();

        ItemRequestDto savedRequestDto = itemRequestService.create(user.getId(), requestCreateDtoDto);

        Optional<ItemRequest> requestFromRepository = itemRequestRepository.findById(savedRequestDto.getId());
        assertThat(requestFromRepository).isPresent();
        assertThat(requestFromRepository.get().getDescription()).isEqualTo("Test request description");
    }

    @Test
    void getAllUserRequestsByUserIdTest() {
        ItemRequest itemRequest = ItemRequest.builder()
                .description("Test request by user")
                .created(LocalDateTime.now())
                .requestor(user)
                .build();

        itemRequestRepository.save(itemRequest);

        List<ItemRequestDtoWithAnswers> requests = itemRequestService.getAllUserRequests(user.getId());

        assertThat(requests).hasSize(1);
        assertThat(requests.getFirst().getDescription().equals("Test request by user"));
    }

    @Test
    void getAllRequestsExceptUser() {
        User otherUser  = User.builder()
                .name("TestOtherUser")
                .email("TestUserEmail@test.com")
                .build();

        userRepository.save(otherUser);

        ItemRequest itemRequest = ItemRequest.builder()
                .description("Test request by other user")
                .created(LocalDateTime.now())
                .requestor(otherUser)
                .build();

        itemRequestRepository.save(itemRequest);

        List<ItemRequestDto> requests = itemRequestService.getAllRequestsExceptUser(user.getId());

        assertThat(requests).hasSize(1);
        assertThat(requests.getFirst().getDescription().equals("Test request by other user"));
    }

    @Test
    void getRequestById() {
        ItemRequest itemRequest = ItemRequest.builder()
                .description("Test request description")
                .created(LocalDateTime.now())
                .requestor(user)
                .build();

        itemRequestRepository.save(itemRequest);

        ItemRequestDtoWithAnswers returnedRequest = itemRequestService.getRequestById(itemRequest.getId());

        assertThat(returnedRequest.getId()).isEqualTo(itemRequest.getId());
        assertThat(returnedRequest.getDescription()).isEqualTo("Test request description");
    }
}