package com.stockpilot.backend.org.service;

import com.stockpilot.backend.common.builders.BranchBuilders;
import com.stockpilot.backend.common.builders.UserBuilder;
import com.stockpilot.backend.identity.domain.entity.User;
import com.stockpilot.backend.identity.domain.repository.UserRepository;
import com.stockpilot.backend.org.dto.request.CreateBranchRequest;
import com.stockpilot.backend.org.dto.request.UpdateBranchRequest;
import com.stockpilot.backend.org.dto.request.UpdateBranchStatusRequest;
import com.stockpilot.backend.org.dto.response.BranchDto;
import com.stockpilot.backend.org.dto.response.DefaultBranchResponse;
import com.stockpilot.backend.org.entity.Branch;
import com.stockpilot.backend.org.enums.BranchStatus;
import com.stockpilot.backend.org.enums.BranchType;
import com.stockpilot.backend.org.event.BranchStatusChangedEvent;
import com.stockpilot.backend.org.mapper.BranchMapper;
import com.stockpilot.backend.org.repository.BranchRepository;
import com.stockpilot.backend.org.service.impl.BranchServiceImpl;
import com.stockpilot.backend.shared.exception.base.BusinessRuleException;
import com.stockpilot.backend.shared.exception.base.DuplicateResourceException;
import com.stockpilot.backend.identity.exception.InvalidOperationException;
import com.stockpilot.backend.shared.exception.base.ResourceNotFoundException;
import com.stockpilot.backend.shared.utils.TenantContext;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BranchServiceImplTest {

    @Mock
    private BranchRepository branchRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private BranchMapper branchMapper;

    @Mock
    private TenantContext tenantContext;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    @InjectMocks
    private BranchServiceImpl branchService;
    private UUID tenantId;

    @BeforeEach
    void setUp() {
        tenantId = UUID.randomUUID();
        TenantContext.setTenantId(tenantId);
    }

    @AfterEach
    void tearDown() {
        TenantContext.clear();
    }
    @Nested
    @DisplayName("CreateBranchTests")
    class CreateBranchTests {

        @Test
        @DisplayName("Should create branch successfully")
        void shouldCreateBranchSuccessfully() {

            CreateBranchRequest request = BranchBuilders.createBranchRequest();

            Branch branch = BranchBuilders.aBranch().build();

            Branch savedBranch = BranchBuilders.aBranch().build();

            BranchDto dto = BranchBuilders.branchDto();


            when(branchRepository.existsByNameAndTenantIdAndDeletedFalse(
                    request.name(),
                    tenantId
            )).thenReturn(false);

            when(branchRepository.existsByCodeAndTenantIdAndDeletedFalse(
                    request.code(),
                    tenantId
            )).thenReturn(false);

            when(branchMapper.toEntity(request))
                    .thenReturn(branch);

            when(branchRepository.save(branch))
                    .thenReturn(savedBranch);

            when(branchMapper.toDto(savedBranch))
                    .thenReturn(dto);

            BranchDto result = branchService.createBranch(request);

            assertThat(result).isEqualTo(dto);

            verify(branchRepository).save(branch);

            verify(branchMapper).toEntity(request);

            verify(branchMapper).toDto(savedBranch);

            verify(userRepository, never())
                    .findByIdAndTenantId(any(), any());
        }

        @Test
        @DisplayName("Should throw when branch name already exists")
        void shouldThrowWhenBranchNameAlreadyExists() {

            CreateBranchRequest request = BranchBuilders.createBranchRequest();

            when(branchRepository.existsByNameAndTenantIdAndDeletedFalse(
                    request.name(),
                    tenantId
            )).thenReturn(true);

            assertThatThrownBy(() -> branchService.createBranch(request))
                    .isInstanceOf(DuplicateResourceException.class)
                    .hasMessage("Branch name already exists.");

            verify(branchRepository)
                    .existsByNameAndTenantIdAndDeletedFalse(
                            request.name(),
                            tenantId
                    );

            verify(branchRepository, never())
                    .existsByCodeAndTenantIdAndDeletedFalse(any(), any());

            verify(branchMapper, never()).toEntity(any());

            verify(branchRepository, never()).save(any());

            verify(branchMapper, never()).toDto(any());
        }

        @Test
        @DisplayName("Should throw when branch code already exists")
        void shouldThrowWhenBranchCodeAlreadyExists() {

            CreateBranchRequest request = BranchBuilders.createBranchRequest();

            when(branchRepository.existsByNameAndTenantIdAndDeletedFalse(
                    request.name(),
                    tenantId
            )).thenReturn(false);

            when(branchRepository.existsByCodeAndTenantIdAndDeletedFalse(
                    request.code(),
                    tenantId
            )).thenReturn(true);

            assertThatThrownBy(() -> branchService.createBranch(request))
                    .isInstanceOf(DuplicateResourceException.class)
                    .hasMessage("Branch code already exists.");

            verify(branchRepository)
                    .existsByNameAndTenantIdAndDeletedFalse(
                            request.name(),
                            tenantId
                    );

            verify(branchRepository)
                    .existsByCodeAndTenantIdAndDeletedFalse(
                            request.code(),
                            tenantId
                    );

            verify(branchMapper, never()).toEntity(any());

            verify(branchRepository, never()).save(any());

            verify(branchMapper, never()).toDto(any());
        }

        @Test
        @DisplayName("Should assign manager when manager_id provided")
        void shouldAssignManagerWhenManagerIdProvided() {

            User manager = UserBuilder.aUser()
                    .tenantId(tenantId)
                    .build();

            CreateBranchRequest request = new CreateBranchRequest(
                    "Head Office",
                    "HQ001",
                    BranchType.RETAIL,
                    "+263771234567",
                    "branch@test.com",
                    "123 Main Street",
                    "Harare",
                    manager.getId()
            );

            Branch branch = BranchBuilders.aBranch().build();
            Branch savedBranch = BranchBuilders.aBranch()
                    .manager(manager)
                    .build();
            BranchDto dto = BranchBuilders.branchDto();

            when(branchRepository.existsByNameAndTenantIdAndDeletedFalse(
                    request.name(),
                    tenantId
            )).thenReturn(false);

            when(branchRepository.existsByCodeAndTenantIdAndDeletedFalse(
                    request.code(),
                    tenantId
            )).thenReturn(false);

            when(branchMapper.toEntity(request))
                    .thenReturn(branch);

            when(userRepository.findByIdAndTenantId(
                    manager.getId(),
                    tenantId
            )).thenReturn(Optional.of(manager));

            when(branchRepository.save(branch))
                    .thenReturn(savedBranch);

            when(branchMapper.toDto(savedBranch))
                    .thenReturn(dto);

            BranchDto result = branchService.createBranch(request);

            assertThat(result).isEqualTo(dto);
            assertThat(branch.getManager()).isEqualTo(manager);

            verify(userRepository)
                    .findByIdAndTenantId(manager.getId(), tenantId);

            verify(branchRepository).save(branch);
        }

        @Test
        @DisplayName("Should create branch without manager")
        void shouldCreateBranchWithoutManager() {

            CreateBranchRequest request = BranchBuilders.createBranchRequest();

            Branch branch = BranchBuilders.aBranch().build();
            Branch savedBranch = BranchBuilders.aBranch().build();
            BranchDto dto = BranchBuilders.branchDto();

            when(branchRepository.existsByNameAndTenantIdAndDeletedFalse(
                    request.name(),
                    tenantId
            )).thenReturn(false);

            when(branchRepository.existsByCodeAndTenantIdAndDeletedFalse(
                    request.code(),
                    tenantId
            )).thenReturn(false);

            when(branchMapper.toEntity(request))
                    .thenReturn(branch);

            when(branchRepository.save(branch))
                    .thenReturn(savedBranch);

            when(branchMapper.toDto(savedBranch))
                    .thenReturn(dto);

            BranchDto result = branchService.createBranch(request);

            assertThat(result).isEqualTo(dto);
            assertThat(branch.getManager()).isNull();

            verify(userRepository, never())
                    .findByIdAndTenantId(any(), any());

            verify(branchRepository).save(branch);
        }

        @Test
        @DisplayName("Should throw when manager does not exist")
        void shouldThrowWhenManagerDoesNotExist() {

            UUID managerId = UUID.randomUUID();

            CreateBranchRequest request = new CreateBranchRequest(
                    "Head Office",
                    "HQ001",
                    BranchType.RETAIL,
                    "+263771234567",
                    "branch@test.com",
                    "123 Main Street",
                    "Harare",
                    managerId
            );

            Branch branch = BranchBuilders.aBranch().build();

            when(branchRepository.existsByNameAndTenantIdAndDeletedFalse(
                    request.name(),
                    tenantId
            )).thenReturn(false);

            when(branchRepository.existsByCodeAndTenantIdAndDeletedFalse(
                    request.code(),
                    tenantId
            )).thenReturn(false);

            when(branchMapper.toEntity(request))
                    .thenReturn(branch);

            when(userRepository.findByIdAndTenantId(
                    managerId,
                    tenantId
            )).thenReturn(Optional.empty());

            assertThatThrownBy(() -> branchService.createBranch(request))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessage(
                            "Branch manager with ID " + managerId + " not found."
                    );

            verify(branchRepository, never()).save(any());

            verify(branchMapper, never()).toDto(any());
        }

    }

    @Nested
    @DisplayName("UpdateBranchTests")
    class UpdateBranchTests {

        @Test
        @DisplayName("Should update branch successfully")
        void shouldUpdateBranchSuccessfully() {

            UUID branchId = UUID.randomUUID();

            UpdateBranchRequest request = BranchBuilders.updateBranchRequest();

            Branch existingBranch = BranchBuilders.aBranch().build();
            existingBranch.setId(branchId);
            existingBranch.setTenantId(tenantId);

            Branch updatedBranch = BranchBuilders.aBranch().build();
            updatedBranch.setId(branchId);
            updatedBranch.setTenantId(tenantId);
            updatedBranch.setName(request.name());
            updatedBranch.setPhone(request.phone());
            updatedBranch.setEmail(request.email());
            updatedBranch.setAddressLine1(request.addressLine1());
            updatedBranch.setCity(request.city());

            BranchDto dto = BranchBuilders.branchDto();

            when(branchRepository.findByIdAndTenantIdAndDeletedFalse(
                    branchId,
                    tenantId
            )).thenReturn(Optional.of(existingBranch));

            when(branchRepository.existsByNameAndTenantIdAndDeletedFalse(
                    request.name(),
                    tenantId
            )).thenReturn(false);

            when(branchRepository.save(existingBranch))
                    .thenReturn(updatedBranch);

            when(branchMapper.toDto(updatedBranch))
                    .thenReturn(dto);

            BranchDto result = branchService.updateBranch(branchId, request);

            assertThat(result).isEqualTo(dto);

            verify(branchRepository)
                    .findByIdAndTenantIdAndDeletedFalse(
                            branchId,
                            tenantId
                    );

            verify(branchRepository)
                    .existsByNameAndTenantIdAndDeletedFalse(
                            request.name(),
                            tenantId
                    );

            verify(branchMapper)
                    .updateEntityFromRequest(request, existingBranch);

            verify(branchRepository).save(existingBranch);

            verify(branchMapper).toDto(updatedBranch);

            verify(userRepository, never())
                    .findByIdAndTenantId(any(), any());
        }

        @Test
        @DisplayName("Should update branch without changing manager")
        void shouldUpdateBranchWithoutChangingManager() {

            UUID branchId = UUID.randomUUID();

            User existingManager = UserBuilder.aUser()
                    .tenantId(tenantId)
                    .build();

            UpdateBranchRequest request = BranchBuilders.updateBranchRequest(); // managerId == null

            Branch branch = BranchBuilders.aBranch().build();
            branch.setId(branchId);
            branch.setTenantId(tenantId);
            branch.setManager(existingManager);

            BranchDto dto = BranchBuilders.branchDto();

            when(branchRepository.findByIdAndTenantIdAndDeletedFalse(
                    branchId,
                    tenantId
            )).thenReturn(Optional.of(branch));

            when(branchRepository.existsByNameAndTenantIdAndDeletedFalse(
                    request.name(),
                    tenantId
            )).thenReturn(false);

            when(branchRepository.save(branch))
                    .thenReturn(branch);

            when(branchMapper.toDto(branch))
                    .thenReturn(dto);

            BranchDto result = branchService.updateBranch(branchId, request);

            assertThat(result).isEqualTo(dto);

            assertThat(branch.getManager()).isEqualTo(existingManager);

            verify(branchMapper)
                    .updateEntityFromRequest(request, branch);

            verify(userRepository, never())
                    .findByIdAndTenantId(any(), any());

            verify(branchRepository).save(branch);

            verify(branchMapper).toDto(branch);
        }

        @Test
        @DisplayName("Should assign new manager")
        void shouldAssignNewManager() {

            UUID branchId = UUID.randomUUID();

            User currentManager = UserBuilder.aUser()
                    .tenantId(tenantId)
                    .build();

            User newManager = UserBuilder.aUser()
                    .id(UUID.randomUUID())
                    .tenantId(tenantId)
                    .active(true)
                    .build();

            UpdateBranchRequest request = new UpdateBranchRequest(
                    "Head Office Updated",
                    "+263771111111",
                    "updated@test.com",
                    "456 Second Street",
                    "Bulawayo",
                    newManager.getId()
            );

            Branch branch = BranchBuilders.aBranch().build();
            branch.setId(branchId);
            branch.setTenantId(tenantId);
            branch.setManager(currentManager);

            BranchDto dto = BranchBuilders.branchDto();

            when(branchRepository.findByIdAndTenantIdAndDeletedFalse(
                    branchId,
                    tenantId
            )).thenReturn(Optional.of(branch));

            when(branchRepository.existsByNameAndTenantIdAndDeletedFalse(
                    request.name(),
                    tenantId
            )).thenReturn(false);

            when(userRepository.findByIdAndTenantId(
                    newManager.getId(),
                    tenantId
            )).thenReturn(Optional.of(newManager));

            when(branchRepository.save(branch))
                    .thenReturn(branch);

            when(branchMapper.toDto(branch))
                    .thenReturn(dto);

            BranchDto result = branchService.updateBranch(branchId, request);

            assertThat(result).isEqualTo(dto);
            assertThat(branch.getManager()).isEqualTo(newManager);

            verify(branchMapper)
                    .updateEntityFromRequest(request, branch);

            verify(userRepository)
                    .findByIdAndTenantId(
                            newManager.getId(),
                            tenantId
                    );

            verify(branchRepository).save(branch);

            verify(branchMapper).toDto(branch);
        }

        @Test
        @DisplayName("Should throw when branch not found")
        void shouldThrowWhenBranchNotFound() {

            UUID branchId = UUID.randomUUID();

            UpdateBranchRequest request = BranchBuilders.updateBranchRequest();

            when(branchRepository.findByIdAndTenantIdAndDeletedFalse(
                    branchId,
                    tenantId
            )).thenReturn(Optional.empty());

            assertThatThrownBy(() ->
                    branchService.updateBranch(branchId, request)
            )
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessage(
                            "Branch with ID " + branchId + " not found."
                    );

            verify(branchRepository, never())
                    .save(any());

            verify(branchMapper, never())
                    .updateEntityFromRequest(any(), any());

            verify(branchMapper, never())
                    .toDto(any());

            verify(userRepository, never())
                    .findByIdAndTenantId(any(), any());
        }


        @Test
        @DisplayName("Should throw when duplicate branch name exists")
        void shouldThrowWhenDuplicateBranchNameExists() {

            UUID branchId = UUID.randomUUID();

            UpdateBranchRequest request = BranchBuilders.updateBranchRequest();

            Branch existingBranch = BranchBuilders.aBranch().build();
            existingBranch.setId(branchId);
            existingBranch.setTenantId(tenantId);
            existingBranch.setName("Head Office");

            when(branchRepository.findByIdAndTenantIdAndDeletedFalse(
                    branchId,
                    tenantId
            )).thenReturn(Optional.of(existingBranch));

            when(branchRepository.existsByNameAndTenantIdAndDeletedFalse(
                    request.name(),
                    tenantId
            )).thenReturn(true);

            assertThatThrownBy(() ->
                    branchService.updateBranch(branchId, request)
            )
                    .isInstanceOf(DuplicateResourceException.class)
                    .hasMessage("Branch name already exists.");

            verify(branchRepository, never()).save(any());

            verify(branchMapper, never())
                    .updateEntityFromRequest(any(), any());

            verify(userRepository, never())
                    .findByIdAndTenantId(any(), any());

            verify(branchMapper, never()).toDto(any());
        }

        @Test
        @DisplayName("Should throw when manager not found")
        void shouldThrowWhenManagerNotFound() {

            UUID branchId = UUID.randomUUID();
            UUID managerId = UUID.randomUUID();

            UpdateBranchRequest request = new UpdateBranchRequest(
                    "Head Office Updated",
                    "+263771111111",
                    "updated@test.com",
                    "456 Second Street",
                    "Bulawayo",
                    managerId
            );

            Branch existingBranch = BranchBuilders.aBranch().build();
            existingBranch.setId(branchId);
            existingBranch.setTenantId(tenantId);

            when(branchRepository.findByIdAndTenantIdAndDeletedFalse(
                    branchId,
                    tenantId
            )).thenReturn(Optional.of(existingBranch));

            when(branchRepository.existsByNameAndTenantIdAndDeletedFalse(
                    request.name(),
                    tenantId
            )).thenReturn(false);

            when(userRepository.findByIdAndTenantId(
                    managerId,
                    tenantId
            )).thenReturn(Optional.empty());

            assertThatThrownBy(() ->
                    branchService.updateBranch(branchId, request)
            )
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessage(
                            "Branch manager with ID " + managerId + " not found."
                    );

            verify(branchMapper, never())
                    .toDto(any());

            verify(branchRepository, never())
                    .save(any());

            verify(userRepository)
                    .findByIdAndTenantId(managerId, tenantId);
        }

        @Test
        @DisplayName("Should throw when inactive manager provided")
        void shouldThrowWhenInactiveManagerProvided() {

            UUID branchId = UUID.randomUUID();

            User inactiveManager = UserBuilder.aUser()
                    .id(UUID.randomUUID())
                    .tenantId(tenantId)
                    .active(false)
                    .build();

            UpdateBranchRequest request = new UpdateBranchRequest(
                    "Head Office Updated",
                    "+263771111111",
                    "updated@test.com",
                    "456 Second Street",
                    "Bulawayo",
                    inactiveManager.getId()
            );

            Branch existingBranch = BranchBuilders.aBranch().build();
            existingBranch.setId(branchId);
            existingBranch.setTenantId(tenantId);

            when(branchRepository.findByIdAndTenantIdAndDeletedFalse(
                    branchId,
                    tenantId
            )).thenReturn(Optional.of(existingBranch));

            when(branchRepository.existsByNameAndTenantIdAndDeletedFalse(
                    request.name(),
                    tenantId
            )).thenReturn(false);

            when(userRepository.findByIdAndTenantId(
                    inactiveManager.getId(),
                    tenantId
            )).thenReturn(Optional.of(inactiveManager));

            assertThatThrownBy(() ->
                    branchService.updateBranch(branchId, request)
            )
                    .isInstanceOf(BusinessRuleException.class)
                    .hasMessage("Inactive users cannot be assigned as branch managers.");

            verify(branchRepository, never()).save(any());

            verify(branchMapper, never()).toDto(any());

            verify(userRepository)
                    .findByIdAndTenantId(
                            inactiveManager.getId(),
                            tenantId
                    );
        }

    }

    @Nested
    @DisplayName("updateBranchStatusTests")
    class UpdateBranchStatusTests {

        @Test
        @DisplayName("Should activate draft branch")
        void shouldActivateDraftBranch() {

            UUID branchId = UUID.randomUUID();

            UpdateBranchStatusRequest request =
                    new UpdateBranchStatusRequest(BranchStatus.ACTIVE);

            Branch branch = BranchBuilders.aBranch().build();
            branch.setId(branchId);
            branch.setTenantId(tenantId);
            branch.setStatus(BranchStatus.DRAFT);

            BranchDto dto = BranchBuilders.branchDto();

            when(branchRepository.findByIdAndTenantIdAndDeletedFalse(
                    branchId,
                    tenantId
            )).thenReturn(Optional.of(branch));

            when(branchRepository.save(branch))
                    .thenReturn(branch);

            when(branchMapper.toDto(branch))
                    .thenReturn(dto);

            BranchDto result = branchService.updateBranchStatus(
                    branchId,
                    request
            );

            assertThat(result).isEqualTo(dto);
            assertThat(branch.getStatus()).isEqualTo(BranchStatus.ACTIVE);

            verify(branchRepository)
                    .save(branch);

            ArgumentCaptor<BranchStatusChangedEvent> captor =
                    ArgumentCaptor.forClass(BranchStatusChangedEvent.class);

            verify(eventPublisher).publishEvent(captor.capture());

            BranchStatusChangedEvent event = captor.getValue();

            assertThat(event.branchId()).isEqualTo(branchId);
            assertThat(event.tenantId()).isEqualTo(tenantId);
            assertThat(event.previousStatus()).isEqualTo(BranchStatus.DRAFT);
            assertThat(event.currentStatus()).isEqualTo(BranchStatus.ACTIVE);
            assertThat(event.occurredAt()).isNotNull();

            verify(branchMapper)
                    .toDto(branch);
        }

        @Test
        @DisplayName("Should deactivate active branch")
        void shouldDeactivateActiveBranch() {

            UUID branchId = UUID.randomUUID();

            UpdateBranchStatusRequest request =
                    new UpdateBranchStatusRequest(BranchStatus.INACTIVE);

            Branch branch = BranchBuilders.aBranch().build();
            branch.setId(branchId);
            branch.setTenantId(tenantId);
            branch.setStatus(BranchStatus.ACTIVE);

            BranchDto dto = BranchBuilders.branchDto();

            when(branchRepository.findByIdAndTenantIdAndDeletedFalse(
                    branchId,
                    tenantId
            )).thenReturn(Optional.of(branch));

            when(branchRepository.countByTenantIdAndStatusAndDeletedFalse(
                    tenantId,
                    BranchStatus.ACTIVE
            )).thenReturn(2L);

            when(branchRepository.save(branch))
                    .thenReturn(branch);

            when(branchMapper.toDto(branch))
                    .thenReturn(dto);

            BranchDto result = branchService.updateBranchStatus(
                    branchId,
                    request
            );

            assertThat(result).isEqualTo(dto);
            assertThat(branch.getStatus()).isEqualTo(BranchStatus.INACTIVE);

            verify(branchRepository).save(branch);

            ArgumentCaptor<BranchStatusChangedEvent> captor =
                    ArgumentCaptor.forClass(BranchStatusChangedEvent.class);

            verify(eventPublisher).publishEvent(captor.capture());

            BranchStatusChangedEvent event = captor.getValue();

            assertThat(event.branchId()).isEqualTo(branchId);
            assertThat(event.tenantId()).isEqualTo(tenantId);
            assertThat(event.previousStatus()).isEqualTo(BranchStatus.ACTIVE);
            assertThat(event.currentStatus()).isEqualTo(BranchStatus.INACTIVE);
            assertThat(event.occurredAt()).isNotNull();

            verify(branchMapper).toDto(branch);
        }

        @Test
        @DisplayName("Should reactivate inactive branch")
        void shouldReactivateInactiveBranch() {

            UUID branchId = UUID.randomUUID();

            UpdateBranchStatusRequest request =
                    new UpdateBranchStatusRequest(BranchStatus.ACTIVE);

            Branch branch = BranchBuilders.aBranch().build();
            branch.setId(branchId);
            branch.setTenantId(tenantId);
            branch.setStatus(BranchStatus.INACTIVE);

            BranchDto dto = BranchBuilders.branchDto();

            when(branchRepository.findByIdAndTenantIdAndDeletedFalse(
                    branchId,
                    tenantId
            )).thenReturn(Optional.of(branch));

            when(branchRepository.save(branch))
                    .thenReturn(branch);

            when(branchMapper.toDto(branch))
                    .thenReturn(dto);

            BranchDto result = branchService.updateBranchStatus(
                    branchId,
                    request
            );

            assertThat(result).isEqualTo(dto);
            assertThat(branch.getStatus()).isEqualTo(BranchStatus.ACTIVE);

            verify(branchRepository).save(branch);

            ArgumentCaptor<BranchStatusChangedEvent> captor =
                    ArgumentCaptor.forClass(BranchStatusChangedEvent.class);

            verify(eventPublisher).publishEvent(captor.capture());

            BranchStatusChangedEvent event = captor.getValue();

            assertThat(event.branchId()).isEqualTo(branchId);
            assertThat(event.tenantId()).isEqualTo(tenantId);
            assertThat(event.previousStatus()).isEqualTo(BranchStatus.INACTIVE);
            assertThat(event.currentStatus()).isEqualTo(BranchStatus.ACTIVE);
            assertThat(event.occurredAt()).isNotNull();

            verify(branchMapper).toDto(branch);
        }

        @Test
        @DisplayName("Should archive inactive branch")
        void shouldArchiveInactiveBranch() {

            UUID branchId = UUID.randomUUID();

            UpdateBranchStatusRequest request =
                    new UpdateBranchStatusRequest(BranchStatus.ARCHIVED);

            Branch branch = BranchBuilders.aBranch().build();
            branch.setId(branchId);
            branch.setTenantId(tenantId);
            branch.setStatus(BranchStatus.INACTIVE);

            BranchDto dto = BranchBuilders.branchDto();

            when(branchRepository.findByIdAndTenantIdAndDeletedFalse(
                    branchId,
                    tenantId
            )).thenReturn(Optional.of(branch));

            when(branchRepository.save(branch))
                    .thenReturn(branch);

            when(branchMapper.toDto(branch))
                    .thenReturn(dto);

            BranchDto result = branchService.updateBranchStatus(
                    branchId,
                    request
            );

            assertThat(result).isEqualTo(dto);
            assertThat(branch.getStatus()).isEqualTo(BranchStatus.ARCHIVED);

            verify(branchRepository).save(branch);

            ArgumentCaptor<BranchStatusChangedEvent> captor =
                    ArgumentCaptor.forClass(BranchStatusChangedEvent.class);

            verify(eventPublisher).publishEvent(captor.capture());

            BranchStatusChangedEvent event = captor.getValue();

            assertThat(event.branchId()).isEqualTo(branchId);
            assertThat(event.tenantId()).isEqualTo(tenantId);
            assertThat(event.previousStatus()).isEqualTo(BranchStatus.INACTIVE);
            assertThat(event.currentStatus()).isEqualTo(BranchStatus.ARCHIVED);
            assertThat(event.occurredAt()).isNotNull();

            verify(branchMapper).toDto(branch);
        }

        @Test
        @DisplayName("Should throw when transition is invalid")
        void shouldThrowWhenTransitionIsInvalid() {

            UUID branchId = UUID.randomUUID();

            UpdateBranchStatusRequest request =
                    new UpdateBranchStatusRequest(BranchStatus.ARCHIVED);

            Branch branch = BranchBuilders.aBranch().build();
            branch.setId(branchId);
            branch.setTenantId(tenantId);
            branch.setStatus(BranchStatus.ACTIVE);

            when(branchRepository.findByIdAndTenantIdAndDeletedFalse(
                    branchId,
                    tenantId
            )).thenReturn(Optional.of(branch));

            assertThatThrownBy(() ->
                    branchService.updateBranchStatus(branchId, request)
            )
                    .isInstanceOf(InvalidOperationException.class)
                    .hasMessage("Invalid branch status transition.");

            verify(branchRepository, never()).save(any());

            verify(eventPublisher, never()).publishEvent(any());

            verify(branchMapper, never()).toDto(any());
        }

    }

    @Nested
    @DisplayName("setDefaultBranchTests")
    class SetDefaultBranchTests {

        @Test
        @DisplayName("Should set new default branch")
        void shouldSetNewDefaultBranch() {

            UUID branchId = UUID.randomUUID();

            Branch newDefaultBranch = BranchBuilders.aBranch().build();
            newDefaultBranch.setId(branchId);
            newDefaultBranch.setTenantId(tenantId);
            newDefaultBranch.setStatus(BranchStatus.ACTIVE);
            newDefaultBranch.setDefaultBranch(false);

            BranchDto newDefaultDto = BranchBuilders.branchDto();

            when(branchRepository.findByIdAndTenantIdAndDeletedFalse(
                    branchId,
                    tenantId
            )).thenReturn(Optional.of(newDefaultBranch));

            when(branchRepository.findByTenantIdAndDefaultBranchTrueAndDeletedFalse(
                    tenantId
            )).thenReturn(Optional.empty());

            when(branchMapper.toDto(newDefaultBranch))
                    .thenReturn(newDefaultDto);

            DefaultBranchResponse response =
                    branchService.setDefaultBranch(branchId);

            assertThat(response.previousDefault()).isNull();
            assertThat(response.newDefault()).isEqualTo(newDefaultDto);

            assertThat(newDefaultBranch.isDefaultBranch()).isTrue();

            verify(branchMapper).toDto(newDefaultBranch);

            verify(branchRepository)
                    .findByTenantIdAndDefaultBranchTrueAndDeletedFalse(tenantId);
        }

        @Test
        @DisplayName("Should do nothing when already default")
        void shouldDoNothingWhenAlreadyDefault() {

            UUID branchId = UUID.randomUUID();

            Branch defaultBranch = BranchBuilders.aBranch().build();
            defaultBranch.setId(branchId);
            defaultBranch.setTenantId(tenantId);
            defaultBranch.setStatus(BranchStatus.ACTIVE);
            defaultBranch.setDefaultBranch(true);

            BranchDto dto = BranchBuilders.branchDto();

            when(branchRepository.findByIdAndTenantIdAndDeletedFalse(
                    branchId,
                    tenantId
            )).thenReturn(Optional.of(defaultBranch));

            when(branchRepository.findByTenantIdAndDefaultBranchTrueAndDeletedFalse(
                    tenantId
            )).thenReturn(Optional.of(defaultBranch));

            when(branchMapper.toDto(defaultBranch))
                    .thenReturn(dto);

            DefaultBranchResponse response =
                    branchService.setDefaultBranch(branchId);

            assertThat(response.previousDefault()).isEqualTo(dto);
            assertThat(response.newDefault()).isEqualTo(dto);

            assertThat(defaultBranch.isDefaultBranch()).isTrue();

            verify(branchRepository)
                    .findByIdAndTenantIdAndDeletedFalse(branchId, tenantId);

            verify(branchRepository)
                    .findByTenantIdAndDefaultBranchTrueAndDeletedFalse(tenantId);

            verify(branchMapper, times(2))
                    .toDto(defaultBranch);
        }

        @Test
        @DisplayName("Should clear previous default branch")
        void shouldClearPreviousDefaultBranch() {

            UUID newBranchId = UUID.randomUUID();

            Branch previousDefault = BranchBuilders.aBranch().build();
            previousDefault.setId(UUID.randomUUID());
            previousDefault.setTenantId(tenantId);
            previousDefault.setStatus(BranchStatus.ACTIVE);
            previousDefault.setDefaultBranch(true);

            Branch newDefault = BranchBuilders.aBranch().build();
            newDefault.setId(newBranchId);
            newDefault.setTenantId(tenantId);
            newDefault.setStatus(BranchStatus.ACTIVE);
            newDefault.setDefaultBranch(false);

            BranchDto previousDto = BranchBuilders.branchDto();
            BranchDto currentDto = BranchBuilders.branchDto();

            when(branchRepository.findByIdAndTenantIdAndDeletedFalse(
                    newBranchId,
                    tenantId
            )).thenReturn(Optional.of(newDefault));

            when(branchRepository.findByTenantIdAndDefaultBranchTrueAndDeletedFalse(
                    tenantId
            )).thenReturn(Optional.of(previousDefault));

            when(branchMapper.toDto(previousDefault))
                    .thenReturn(previousDto);

            when(branchMapper.toDto(newDefault))
                    .thenReturn(currentDto);

            DefaultBranchResponse response =
                    branchService.setDefaultBranch(newBranchId);

            assertThat(response.previousDefault()).isEqualTo(previousDto);
            assertThat(response.newDefault()).isEqualTo(currentDto);

            assertThat(previousDefault.isDefaultBranch()).isFalse();
            assertThat(newDefault.isDefaultBranch()).isTrue();

            verify(branchRepository)
                    .findByIdAndTenantIdAndDeletedFalse(newBranchId, tenantId);

            verify(branchRepository)
                    .findByTenantIdAndDefaultBranchTrueAndDeletedFalse(tenantId);

            verify(branchMapper).toDto(previousDefault);
            verify(branchMapper).toDto(newDefault);
        }

        @Test
        @DisplayName("Should throw when branch not found")
        void shouldThrowWhenBranchNotFound() {

            UUID branchId = UUID.randomUUID();

            when(branchRepository.findByIdAndTenantIdAndDeletedFalse(
                    branchId,
                    tenantId
            )).thenReturn(Optional.empty());

            assertThatThrownBy(() ->
                    branchService.setDefaultBranch(branchId)
            )
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessage("Branch with ID " + branchId + " not found.");

            verify(branchRepository, never())
                    .findByTenantIdAndDefaultBranchTrueAndDeletedFalse(any());

            verify(branchMapper, never())
                    .toDto(any());

            verifyNoInteractions(eventPublisher);
        }

        @Test
        @DisplayName("Should throw when branch is not active")
        void shouldThrowWhenBranchIsNotActive() {

            UUID branchId = UUID.randomUUID();

            Branch branch = BranchBuilders.aBranch().build();
            branch.setId(branchId);
            branch.setTenantId(tenantId);
            branch.setStatus(BranchStatus.DRAFT);

            when(branchRepository.findByIdAndTenantIdAndDeletedFalse(
                    branchId,
                    tenantId
            )).thenReturn(Optional.of(branch));

            assertThatThrownBy(() ->
                    branchService.setDefaultBranch(branchId)
            )
                    .isInstanceOf(InvalidOperationException.class)
                    .hasMessage("Only active branches can be set as the default branch.");

            verify(branchRepository, never())
                    .findByTenantIdAndDefaultBranchTrueAndDeletedFalse(any());

            verify(branchMapper, never())
                    .toDto(any());
        }
    }

    @Nested
    @DisplayName("GetBranchesTests")
    class GetBranchesTests {

        @Test
        @DisplayName("Should return paged branches")
        void shouldReturnPagedBranches() {

            Pageable pageable = PageRequest.of(0, 10);

            Branch branch = BranchBuilders.aBranch().build();

            BranchDto dto = BranchBuilders.branchDto();

            Page<Branch> branchPage = new PageImpl<>(
                    List.of(branch),
                    pageable,
                    1
            );

            when(branchRepository.findAll(
                    any(Specification.class),
                    eq(pageable)
            )).thenReturn(branchPage);

            when(branchMapper.toDto(branch))
                    .thenReturn(dto);

            Page<BranchDto> result = branchService.getBranches(
                    null,
                    pageable
            );

            assertThat(result).hasSize(1);
            assertThat(result.getContent())
                    .containsExactly(dto);

            assertThat(result.getTotalElements()).isEqualTo(1);
            assertThat(result.getNumber()).isEqualTo(0);
            assertThat(result.getSize()).isEqualTo(10);

            verify(branchRepository)
                    .findAll(any(Specification.class), eq(pageable));

            verify(branchMapper)
                    .toDto(branch);
        }

        @Test
        @DisplayName("Should filter branches by status")
        void shouldFilterByStatus() {

            Pageable pageable = PageRequest.of(0, 10);

            Branch activeBranch = BranchBuilders.aBranch().build();
            activeBranch.setStatus(BranchStatus.ACTIVE);

            BranchDto dto = BranchBuilders.branchDto();

            Page<Branch> page = new PageImpl<>(
                    List.of(activeBranch),
                    pageable,
                    1
            );

            when(branchRepository.findAll(
                    any(Specification.class),
                    eq(pageable)
            )).thenReturn(page);

            when(branchMapper.toDto(activeBranch))
                    .thenReturn(dto);

            Page<BranchDto> result = branchService.getBranches(
                    BranchStatus.ACTIVE,
                    pageable
            );

            assertThat(result.getContent()).hasSize(1);
            assertThat(result.getContent()).containsExactly(dto);

            verify(branchRepository)
                    .findAll(any(Specification.class), eq(pageable));

            verify(branchMapper)
                    .toDto(activeBranch);
        }

        @Test
        @DisplayName("Should return empty page when no branches exist")
        void shouldReturnEmptyPageWhenNoBranchesExist() {

            Pageable pageable = PageRequest.of(0, 10);

            Page<Branch> emptyPage = Page.empty(pageable);

            when(branchRepository.findAll(
                    any(Specification.class),
                    eq(pageable)
            )).thenReturn(emptyPage);

            Page<BranchDto> result = branchService.getBranches(
                    null,
                    pageable
            );

            assertThat(result).isEmpty();
            assertThat(result.getTotalElements()).isZero();
            assertThat(result.getContent()).isEmpty();

            verify(branchRepository)
                    .findAll(any(Specification.class), eq(pageable));

            verifyNoInteractions(branchMapper);
        }
    }
}