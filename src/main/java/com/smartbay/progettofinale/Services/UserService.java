package com.smartbay.progettofinale.Services;

import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.smartbay.progettofinale.DTO.UserDTO;
import com.smartbay.progettofinale.Models.User;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public interface UserService {
    void saveUser(UserDTO userDto, RedirectAttributes redirectAttributes, HttpServletRequest request, HttpServletResponse response);
    User findUserByEmail(String email);
    User find(Long id);
}
