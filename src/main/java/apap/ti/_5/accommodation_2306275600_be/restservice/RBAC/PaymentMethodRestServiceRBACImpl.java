package apap.ti._5.accommodation_2306275600_be.restservice.RBAC;

import apap.ti._5.accommodation_2306275600_be.exceptions.AccessDeniedException;
import apap.ti._5.accommodation_2306275600_be.external.AuthService;
import apap.ti._5.accommodation_2306275600_be.external.AuthServiceMock;
import apap.ti._5.accommodation_2306275600_be.repository.PaymentMethodRepository;
import apap.ti._5.accommodation_2306275600_be.restdto.auth.UserProfileDTO;
import apap.ti._5.accommodation_2306275600_be.restdto.request.paymentmethod.CreatePaymentMethodRequestDTO;
import apap.ti._5.accommodation_2306275600_be.restdto.request.paymentmethod.UpdatePaymentMethodStatusRequestDTO;
import apap.ti._5.accommodation_2306275600_be.restdto.response.paymentmethod.PaymentMethodResponseDTO;
import apap.ti._5.accommodation_2306275600_be.restservice.PaymentMethodRestServiceImpl;

import java.util.List;

public class PaymentMethodRestServiceRBACImpl extends PaymentMethodRestServiceImpl implements PaymentMethodRestServiceRBAC {

    private final AuthService authService;
    private final AuthServiceMock authServiceMock;

    public PaymentMethodRestServiceRBACImpl(
            PaymentMethodRepository paymentMethodRepository,
            AuthService authService,
            AuthServiceMock authServiceMock
        ) {
        super(paymentMethodRepository);
        this.authService = authService;
        this.authServiceMock = authServiceMock;
    }

    // [POST] Create Payment Method - PBI-BE-TU7 - Superadmin
    @Override
    public PaymentMethodResponseDTO createPaymentMethod(CreatePaymentMethodRequestDTO dto) throws AccessDeniedException {
        UserProfileDTO user = authServiceMock.getSuperAdminUser();
        
        boolean hasAccess = authService.isSuperAdmin(user);
        
        if (!hasAccess) {
            throw new AccessDeniedException("Anda tidak memiliki akses ke resource ini, role : " + user.role());
        }
        
        return super.createPaymentMethod(dto);
    }

    // [GET] Get All Payment Methods - PBI-BE-TU6 - Superadmin
    @Override
    public List<PaymentMethodResponseDTO> getAllPaymentMethods() throws AccessDeniedException {
        UserProfileDTO user = authServiceMock.getSuperAdminUser();
        
        boolean hasAccess = authService.isSuperAdmin(user);
        
        if (!hasAccess) {
            throw new AccessDeniedException("Anda tidak memiliki akses ke resource ini, role : " + user.role());
        }
        
        return super.getAllPaymentMethods();
    }

    // [GET] Get Payment Method by ID - Superadmin
    @Override
    public PaymentMethodResponseDTO getPaymentMethodById(String id) throws AccessDeniedException {
        UserProfileDTO user = authServiceMock.getSuperAdminUser();
        
        boolean hasAccess = authService.isSuperAdmin(user);
        
        if (!hasAccess) {
            throw new AccessDeniedException("Anda tidak memiliki akses ke resource ini, role : " + user.role());
        }
        
        return super.getPaymentMethodById(id);
    }

    // [PUT] Update Payment Method Status - PBI-BE-TU8 - Superadmin
    @Override
    public PaymentMethodResponseDTO updatePaymentMethodStatus(UpdatePaymentMethodStatusRequestDTO dto) throws AccessDeniedException {
        UserProfileDTO user = authServiceMock.getSuperAdminUser();
        
        boolean hasAccess = authService.isSuperAdmin(user);
        
        if (!hasAccess) {
            throw new AccessDeniedException("Anda tidak memiliki akses ke resource ini, role : " + user.role());
        }
        
        return super.updatePaymentMethodStatus(dto);
    }

    // [DELETE] Delete Payment Method - PBI-BE-TU9 - Superadmin
    @Override
    public void deletePaymentMethod(String id) throws AccessDeniedException {
        UserProfileDTO user = authServiceMock.getSuperAdminUser();
        
        boolean hasAccess = authService.isSuperAdmin(user);
        
        if (!hasAccess) {
            throw new AccessDeniedException("Anda tidak memiliki akses ke resource ini, role : " + user.role());
        }
        
        super.deletePaymentMethod(id);
    }
}
