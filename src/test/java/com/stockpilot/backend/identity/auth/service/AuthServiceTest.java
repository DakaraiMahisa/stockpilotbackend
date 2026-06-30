package com.stockpilot.backend.identity.auth.service;

import com.stockpilot.backend.common.builders.RegisterOrganizationRequestBuilder;
import com.stockpilot.backend.common.builders.RoleBuilder;
import com.stockpilot.backend.common.builders.TenantBuilder;
import com.stockpilot.backend.identity.api.request.RegisterOrganizationRequest;
import com.stockpilot.backend.identity.application.service.AuthService;
import com.stockpilot.backend.identity.application.service.RoleProvisioningService;
import com.stockpilot.backend.identity.audits.context.RequestAuditContext;
import com.stockpilot.backend.identity.audits.events.UserRegisteredEvent;
import com.stockpilot.backend.identity.domain.entity.Role;
import com.stockpilot.backend.identity.domain.entity.User;
import com.stockpilot.backend.identity.domain.enums.RoleName;
import com.stockpilot.backend.identity.domain.repository.PermissionRepository;
import com.stockpilot.backend.identity.domain.repository.RefreshTokenRepository;
import com.stockpilot.backend.identity.domain.repository.RoleRepository;
import com.stockpilot.backend.identity.domain.repository.UserRepository;
import com.stockpilot.backend.identity.infrastructure.security.jwt.JwtService;
import com.stockpilot.backend.identity.usermanagement.repository.InvitationTokenRepository;
import com.stockpilot.backend.identity.usermanagement.repository.UserSessionRepository;
import com.stockpilot.backend.org.entity.Organization;
import com.stockpilot.backend.org.service.OrganizationProvisioningService;
import com.stockpilot.backend.shared.exception.DuplicateResourceException;
import com.stockpilot.backend.shared.exception.ResourceNotFoundException;
import com.stockpilot.backend.tenant.domain.entity.Tenant;
import com.stockpilot.backend.tenant.domain.repository.TenantRepository;
import com.stockpilot.backend.tenant.service.TenantCodeGenerator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.crypto.password.PasswordEncoder;
import static org.assertj.core.api.Assertions.assertThat;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AuthServiceTest {

    @InjectMocks
    private AuthService authService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private TenantRepository tenantRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @Mock
    private RoleProvisioningService roleProvisioningService;

    @Mock
    private OrganizationProvisioningService organizationProvisioningService;

    @Mock
    private RefreshTokenRepository refreshTokenRepository;

    @Mock
    private UserSessionRepository userSessionRepository;

    @Mock
    private InvitationTokenRepository invitationTokenRepository;

    @Mock
    private PermissionRepository permissionRepository;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    @Mock
    private TenantCodeGenerator tenantCodeGenerator;

    @Mock
    private RequestAuditContext requestContext;

    @Nested
    @DisplayName("Register Organization")
    class RegisterOrganizationTests {

        @Test
        @DisplayName("Should register organization successfully")
        void shouldRegisterOrganizationSuccessfully() {

            RegisterOrganizationRequest request =
                    RegisterOrganizationRequestBuilder
                            .withDefaults()
                            .build();

            UUID tenantId = UUID.randomUUID();

            Tenant savedTenant = TenantBuilder.aTenant()
                    .id(tenantId)
                    .name(request.getOrganizationName())
                    .code("STOCKPILOT")
                    .build();

            Role ownerRole = RoleBuilder.ownerRole()
                    .tenantId(tenantId)
                    .build();

            when(userRepository.existsByEmail(request.getEmail()))
                    .thenReturn(false);

            when(tenantCodeGenerator.generate(request.getOrganizationName()))
                    .thenReturn("STOCKPILOT");

            when(tenantRepository.existsByCode("STOCKPILOT"))
                    .thenReturn(false);

            when(tenantRepository.save(any(Tenant.class)))
                    .thenReturn(savedTenant);
            doNothing().when(organizationProvisioningService)
                    .provisionDefaults(any(Tenant.class), any(RegisterOrganizationRequest.class));
            when(roleRepository.findByNameAndTenantId(
                    RoleName.OWNER,
                    tenantId))
                    .thenReturn(Optional.of(ownerRole));

            when(passwordEncoder.encode(request.getPassword()))
                    .thenReturn("encoded-password");


            authService.registerOrganization(request);


            ArgumentCaptor<Tenant> tenantCaptor =
                    ArgumentCaptor.forClass(Tenant.class);

            verify(tenantRepository)
                    .save(tenantCaptor.capture());

            Tenant persistedTenant = tenantCaptor.getValue();

            assertThat(persistedTenant.getName())
                    .isEqualTo(request.getOrganizationName());

            assertThat(persistedTenant.getCode())
                    .isEqualTo("STOCKPILOT");

            assertThat(persistedTenant.getTimezone())
                    .isEqualTo(request.getTimezone());

            assertThat(persistedTenant.getCurrencyCode())
                    .isEqualTo(request.getCurrencyCode());

            assertThat(persistedTenant.isActive())
                    .isTrue();

            verify(organizationProvisioningService)
                    .provisionDefaults(
                            tenantCaptor.capture(),
                            eq(request)
                    );

            Tenant provisionedTenant = tenantCaptor.getValue();

            assertThat(provisionedTenant.getId())
                    .isEqualTo(tenantId);

            assertThat(provisionedTenant.getName())
                    .isEqualTo(request.getOrganizationName());

            assertThat(provisionedTenant.getCode())
                    .isEqualTo("STOCKPILOT");

            verify(roleProvisioningService)
                    .provisionDefaultRoles(tenantId);

            ArgumentCaptor<User> userCaptor =
                    ArgumentCaptor.forClass(User.class);

            verify(userRepository)
                    .save(userCaptor.capture());

            User persistedUser = userCaptor.getValue();

            assertThat(persistedUser.getTenantId())
                    .isEqualTo(tenantId);

            assertThat(persistedUser.getEmail())
                    .isEqualTo(request.getEmail());

            assertThat(persistedUser.getPasswordHash())
                    .isEqualTo("encoded-password");

            assertThat(persistedUser.getFirstName())
                    .isEqualTo(request.getFirstName());

            assertThat(persistedUser.getLastName())
                    .isEqualTo(request.getLastName());

            assertThat(persistedUser.getRole())
                    .isEqualTo(ownerRole);

            assertThat(persistedUser.getActive())
                    .isFalse();

            assertThat(persistedUser.getEmailVerified())
                    .isFalse();

            assertThat(persistedUser.getMfaEnabled())
                    .isFalse();


            ArgumentCaptor<UserRegisteredEvent> eventCaptor =
                    ArgumentCaptor.forClass(UserRegisteredEvent.class);

            verify(eventPublisher)
                    .publishEvent(eventCaptor.capture());

            UserRegisteredEvent event = eventCaptor.getValue();

            assertThat(event.getUser().getEmail())
                    .isEqualTo(request.getEmail());


            InOrder inOrder = inOrder(
                    tenantRepository,
                    organizationProvisioningService,
                    roleProvisioningService,
                    roleRepository,
                    userRepository,
                    eventPublisher
            );

            inOrder.verify(tenantRepository)
                    .save(any(Tenant.class));

            inOrder.verify(organizationProvisioningService)
                    .provisionDefaults(any(Tenant.class), eq(request));

            inOrder.verify(roleProvisioningService)
                    .provisionDefaultRoles(tenantId);

            inOrder.verify(roleRepository)
                    .findByNameAndTenantId(RoleName.OWNER, tenantId);

            inOrder.verify(userRepository)
                    .save(any(User.class));

            inOrder.verify(eventPublisher)
                    .publishEvent(any(UserRegisteredEvent.class));

            verifyNoMoreInteractions(
                    tenantRepository,
                    organizationProvisioningService,
                    roleRepository,
                    userRepository,
                    roleProvisioningService,
                    eventPublisher
            );
        }


        @Test
        @DisplayName("Should throw when email already exists")
        void shouldThrowWhenEmailAlreadyExists() {


            RegisterOrganizationRequest request =
                    RegisterOrganizationRequestBuilder
                            .withDefaults()
                            .build();

            when(userRepository.existsByEmail(request.getEmail()))
                    .thenReturn(true);


            assertThatThrownBy(() ->
                    authService.registerOrganization(request)
            )
                    .isInstanceOf(DuplicateResourceException.class)
                    .hasMessage(
                            "User with email " + request.getEmail() + " already exists."
                    );

            verify(userRepository)
                    .existsByEmail(request.getEmail());


            verifyNoInteractions(
                    tenantCodeGenerator,
                    tenantRepository,
                    roleProvisioningService,
                    roleRepository,
                    passwordEncoder,
                    eventPublisher
            );

            verify(userRepository, never())
                    .save(any(User.class));
        }

        @Test
        @DisplayName("Should generate unique tenant code when duplicate exists")
        void shouldGenerateUniqueTenantCodeWhenDuplicateExists() {

            RegisterOrganizationRequest request =
                    RegisterOrganizationRequestBuilder
                            .withDefaults()
                            .organizationName("StockPilot")
                            .build();

            UUID tenantId = UUID.randomUUID();

            Tenant savedTenant = TenantBuilder.aTenant()
                    .id(tenantId)
                    .name(request.getOrganizationName())
                    .code("STOCKPILOT-1")
                    .build();

            Role ownerRole = RoleBuilder.ownerRole()
                    .tenantId(tenantId)
                    .build();

            when(userRepository.existsByEmail(request.getEmail()))
                    .thenReturn(false);

            when(tenantCodeGenerator.generate(request.getOrganizationName()))
                    .thenReturn("STOCKPILOT");

            when(tenantRepository.existsByCode("STOCKPILOT"))
                    .thenReturn(true);

            when(tenantRepository.existsByCode("STOCKPILOT-1"))
                    .thenReturn(false);

            when(tenantRepository.save(any(Tenant.class)))
                    .thenReturn(savedTenant);

            when(roleRepository.findByNameAndTenantId(
                    RoleName.OWNER,
                    tenantId))
                    .thenReturn(Optional.of(ownerRole));

            when(passwordEncoder.encode(anyString()))
                    .thenReturn("encoded-password");

            authService.registerOrganization(request);


            ArgumentCaptor<Tenant> tenantCaptor =
                    ArgumentCaptor.forClass(Tenant.class);

            verify(tenantRepository)
                    .save(tenantCaptor.capture());

            Tenant persistedTenant =
                    tenantCaptor.getValue();

            assertThat(persistedTenant.getCode())
                    .isEqualTo("STOCKPILOT-1");

            verify(tenantRepository)
                    .existsByCode("STOCKPILOT");

            verify(tenantRepository)
                    .existsByCode("STOCKPILOT-1");
        }

        @Test
        @DisplayName("Should apply default timezone when not provided")
        void shouldApplyDefaultTimezoneWhenNotProvided() {

            RegisterOrganizationRequest request =
                    RegisterOrganizationRequestBuilder
                            .withoutOptionalFields()
                            .build();

            UUID tenantId = UUID.randomUUID();

            Tenant savedTenant = TenantBuilder.aTenant()
                    .id(tenantId)
                    .timezone("Asia/Kolkata")
                    .currencyCode("INR")
                    .build();

            Role ownerRole = RoleBuilder.ownerRole()
                    .tenantId(tenantId)
                    .build();

            when(userRepository.existsByEmail(request.getEmail()))
                    .thenReturn(false);

            when(tenantCodeGenerator.generate(request.getOrganizationName()))
                    .thenReturn("STOCKPILOT");

            when(tenantRepository.existsByCode("STOCKPILOT"))
                    .thenReturn(false);

            when(tenantRepository.save(any(Tenant.class)))
                    .thenReturn(savedTenant);

            when(roleRepository.findByNameAndTenantId(
                    RoleName.OWNER,
                    tenantId))
                    .thenReturn(Optional.of(ownerRole));

            when(passwordEncoder.encode(anyString()))
                    .thenReturn("encoded-password");


            authService.registerOrganization(request);

            ArgumentCaptor<Tenant> tenantCaptor =
                    ArgumentCaptor.forClass(Tenant.class);

            verify(tenantRepository)
                    .save(tenantCaptor.capture());

            Tenant persistedTenant =
                    tenantCaptor.getValue();

            assertThat(persistedTenant.getTimezone())
                    .isEqualTo("Asia/Kolkata");
        }

        @Test
        @DisplayName("Should apply default currency when not provided")
        void shouldApplyDefaultCurrencyWhenNotProvided() {

            RegisterOrganizationRequest request =
                    RegisterOrganizationRequestBuilder
                            .withoutOptionalFields()
                            .build();

            UUID tenantId = UUID.randomUUID();

            Tenant savedTenant = TenantBuilder.aTenant()
                    .id(tenantId)
                    .timezone("Asia/Kolkata")
                    .currencyCode("INR")
                    .build();

            Role ownerRole = RoleBuilder.ownerRole()
                    .tenantId(tenantId)
                    .build();

            when(userRepository.existsByEmail(request.getEmail()))
                    .thenReturn(false);

            when(tenantCodeGenerator.generate(request.getOrganizationName()))
                    .thenReturn("STOCKPILOT");

            when(tenantRepository.existsByCode("STOCKPILOT"))
                    .thenReturn(false);

            when(tenantRepository.save(any(Tenant.class)))
                    .thenReturn(savedTenant);

            when(roleRepository.findByNameAndTenantId(
                    RoleName.OWNER,
                    tenantId))
                    .thenReturn(Optional.of(ownerRole));

            when(passwordEncoder.encode(anyString()))
                    .thenReturn("encoded-password");


            authService.registerOrganization(request);

            ArgumentCaptor<Tenant> tenantCaptor =
                    ArgumentCaptor.forClass(Tenant.class);

            verify(tenantRepository)
                    .save(tenantCaptor.capture());

            Tenant persistedTenant = tenantCaptor.getValue();

            assertThat(persistedTenant.getCurrencyCode())
                    .isEqualTo("INR");
        }

        @Test
        @DisplayName("Should throw when OWNER role does not exist")
        void shouldThrowWhenOwnerRoleDoesNotExist() {

            RegisterOrganizationRequest request =
                    RegisterOrganizationRequestBuilder
                            .withDefaults()
                            .build();

            UUID tenantId = UUID.randomUUID();

            Tenant savedTenant = TenantBuilder.aTenant()
                    .id(tenantId)
                    .build();

            when(userRepository.existsByEmail(request.getEmail()))
                    .thenReturn(false);

            when(tenantCodeGenerator.generate(request.getOrganizationName()))
                    .thenReturn("STOCKPILOT");

            when(tenantRepository.existsByCode("STOCKPILOT"))
                    .thenReturn(false);

            when(tenantRepository.save(any(Tenant.class)))
                    .thenReturn(savedTenant);

            when(roleRepository.findByNameAndTenantId(
                    RoleName.OWNER,
                    tenantId
            )).thenReturn(Optional.empty());

            assertThatThrownBy(() ->
                    authService.registerOrganization(request)
            )
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessage("OWNER role not found for tenant.");

            verify(roleProvisioningService)
                    .provisionDefaultRoles(tenantId);

            verify(roleRepository)
                    .findByNameAndTenantId(
                            RoleName.OWNER,
                            tenantId
                    );


            verify(userRepository, never())
                    .save(any(User.class));

            verify(passwordEncoder, never())
                    .encode(anyString());

            verifyNoInteractions(eventPublisher);
        }

        @Test
        @DisplayName("Should encode password before saving user")
        void shouldEncodePasswordBeforeSavingUser() {

            RegisterOrganizationRequest request =
                    RegisterOrganizationRequestBuilder
                            .withDefaults()
                            .password("Password@123")
                            .build();

            UUID tenantId = UUID.randomUUID();

            Tenant savedTenant = TenantBuilder.aTenant()
                    .id(tenantId)
                    .build();

            Role ownerRole = RoleBuilder.ownerRole()
                    .tenantId(tenantId)
                    .build();

            when(userRepository.existsByEmail(request.getEmail()))
                    .thenReturn(false);

            when(tenantCodeGenerator.generate(request.getOrganizationName()))
                    .thenReturn("STOCKPILOT");

            when(tenantRepository.existsByCode("STOCKPILOT"))
                    .thenReturn(false);

            when(tenantRepository.save(any(Tenant.class)))
                    .thenReturn(savedTenant);

            when(roleRepository.findByNameAndTenantId(
                    RoleName.OWNER,
                    tenantId))
                    .thenReturn(Optional.of(ownerRole));

            when(passwordEncoder.encode(request.getPassword()))
                    .thenReturn("encoded-password");

            authService.registerOrganization(request);

            verify(passwordEncoder)
                    .encode(request.getPassword());

            ArgumentCaptor<User> userCaptor =
                    ArgumentCaptor.forClass(User.class);

            verify(userRepository)
                    .save(userCaptor.capture());

            User persistedUser = userCaptor.getValue();

            assertThat(persistedUser.getPasswordHash())
                    .isEqualTo("encoded-password");

            assertThat(persistedUser.getPasswordHash())
                    .isNotEqualTo(request.getPassword());
        }

        @Test
        @DisplayName("Should publish UserRegisteredEvent after successful registration")
        void shouldPublishUserRegisteredEvent() {

            // Arrange
            RegisterOrganizationRequest request =
                    RegisterOrganizationRequestBuilder
                            .withDefaults()
                            .build();

            UUID tenantId = UUID.randomUUID();

            Tenant savedTenant = TenantBuilder.aTenant()
                    .id(tenantId)
                    .build();

            Role ownerRole = RoleBuilder.ownerRole()
                    .tenantId(tenantId)
                    .build();

            when(userRepository.existsByEmail(request.getEmail()))
                    .thenReturn(false);

            when(tenantCodeGenerator.generate(request.getOrganizationName()))
                    .thenReturn("STOCKPILOT");

            when(tenantRepository.existsByCode("STOCKPILOT"))
                    .thenReturn(false);

            when(tenantRepository.save(any(Tenant.class)))
                    .thenReturn(savedTenant);

            when(roleRepository.findByNameAndTenantId(
                    RoleName.OWNER,
                    tenantId))
                    .thenReturn(Optional.of(ownerRole));

            when(passwordEncoder.encode(anyString()))
                    .thenReturn("encoded-password");


            authService.registerOrganization(request);


            ArgumentCaptor<UserRegisteredEvent> eventCaptor =
                    ArgumentCaptor.forClass(UserRegisteredEvent.class);

            verify(eventPublisher)
                    .publishEvent(eventCaptor.capture());

            UserRegisteredEvent publishedEvent =
                    eventCaptor.getValue();

            assertThat(publishedEvent)
                    .isNotNull();

            assertThat(publishedEvent.getSource())
                    .isEqualTo(authService);

            User eventUser = publishedEvent.getUser();

            assertThat(eventUser)
                    .isNotNull();

            assertThat(eventUser.getTenantId())
                    .isEqualTo(tenantId);

            assertThat(eventUser.getEmail())
                    .isEqualTo(request.getEmail().trim().toLowerCase());

            assertThat(eventUser.getFirstName())
                    .isEqualTo(request.getFirstName());

            assertThat(eventUser.getLastName())
                    .isEqualTo(request.getLastName());

            assertThat(eventUser.getRole())
                    .isEqualTo(ownerRole);

            assertThat(eventUser.getPasswordHash())
                    .isEqualTo("encoded-password");

            assertThat(eventUser.getActive())
                    .isFalse();

            assertThat(eventUser.getEmailVerified())
                    .isFalse();

            assertThat(eventUser.getMfaEnabled())
                    .isFalse();
        }
    }
}