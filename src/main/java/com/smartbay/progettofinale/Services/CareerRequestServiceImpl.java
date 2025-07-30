package com.smartbay.progettofinale.Services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.smartbay.progettofinale.Models.CareerRequest;
import com.smartbay.progettofinale.Models.Role;
import com.smartbay.progettofinale.Models.User;
import com.smartbay.progettofinale.Repositories.CareerRequestRepository;
import com.smartbay.progettofinale.Repositories.RoleRepository;
import com.smartbay.progettofinale.Repositories.UserRepository;


@Service
public class CareerRequestServiceImpl implements CareerRequestService{


    @Autowired
    private CareerRequestRepository careerRequestRepository;

    @Autowired
    private EmailService emailService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;
    
    @Transactional
    public boolean isRoleAlreadyAssigned(User user, CareerRequest careerRequest) {
        List<Long> allUserIds = careerRequestRepository.findAllUserIds();

        if (!allUserIds.contains(user.getId())) {
            return false;
        }

        List<Long> request = careerRequestRepository.findByUserId(user.getId());
        return request.stream().anyMatch(roleId -> roleId.equals(careerRequest.getRole().getId()));
    }

    public void save(CareerRequest careerRequest, User user) {
        careerRequest.setUser(user);
        careerRequest.setIsChecked(false);
        careerRequestRepository.save(careerRequest);

        emailService.sendSimpleEmail("admin@smartbay.com", "Request for the role: " + careerRequest.getRole().getName(), "new request for collaboration from" + user.getUsername());
    }

    @Override
    public void careerAccept(Long requestId) {
        CareerRequest request = careerRequestRepository.findById(requestId).get();

        User user = request.getUser();
        Role role = request.getRole();

        List<Role> rolesUser = user.getRoles();
        Role newRole = roleRepository.findByName(role.getName());
        rolesUser.add(newRole);

        user.setRoles(rolesUser);
        userRepository.save(user);
        request.setIsChecked(true);
        careerRequestRepository.save(request);

        emailService.sendSimpleEmail(user.getEmail(), "Role enabled", "Hello, we inform you that our administration has accepted your request for collaboration... Welcome!");
    }

    @Override
    public CareerRequest find(Long id) {
        return careerRequestRepository.findById(id).get();
    }

    


    
}