package br.com.fiap.techchallengefase02.service;

import br.com.fiap.techchallengefase02.dto.request.UserPatchDTO;
import br.com.fiap.techchallengefase02.dto.request.UserRequestDTO;
import br.com.fiap.techchallengefase02.dto.response.UserResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface UserService {

    UserResponseDTO create(UserRequestDTO dto);

    UserResponseDTO patch(UUID id, UserPatchDTO dto);

    UserResponseDTO findById(UUID id);

    Page<UserResponseDTO> searchByName(String name, Pageable pageable);

    void delete(UUID id);
}
