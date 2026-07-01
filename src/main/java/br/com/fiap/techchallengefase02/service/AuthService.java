package br.com.fiap.techchallengefase02.service;

import br.com.fiap.techchallengefase02.dto.request.ChangePasswordDTO;
import br.com.fiap.techchallengefase02.dto.request.LoginRequestDTO;
import br.com.fiap.techchallengefase02.dto.response.TokenResponseDTO;

import java.util.UUID;

public interface AuthService {

    TokenResponseDTO login(LoginRequestDTO dto);

    void changePassword(UUID id, ChangePasswordDTO dto);

}
