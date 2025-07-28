package com.smartbay.progettofinale.Services;

import com.smartbay.progettofinale.Models.CareerRequest;
import com.smartbay.progettofinale.Models.User;

public interface CareerRequestService {
    boolean isRoleAlreadyAssigned(User user, CareerRequest careerRequest);
    void save(CareerRequest careerRequest, User user);
    void careerAccept(Long requestId);
    CareerRequest find(Long id);

}