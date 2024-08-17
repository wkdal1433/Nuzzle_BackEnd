package com.nuzzle.backend.family.service.impl;

import com.nuzzle.backend.family.domain.Family;
import com.nuzzle.backend.family.dto.FamilyDTO;
import com.nuzzle.backend.family.repository.FamilyRepository;
import com.nuzzle.backend.family.service.FamilyService;
import com.nuzzle.backend.pet.dto.PetDTO;
import com.nuzzle.backend.user.domain.User;
import com.nuzzle.backend.user.dto.UserDTO;
import com.nuzzle.backend.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class FamilyServiceImpl implements FamilyService {
    @Autowired
    private FamilyRepository familyRepository;

    @Autowired
    private UserRepository userRepository;

    @Transactional
    @Override
    public Family createFamily(User user) {
        // 새로운 가족 생성
        Family family = new Family();
        family.setFamilyStatus("Active"); // 가족 상태 설정
        family.setInvitationCode(UUID.randomUUID().toString()); // 초대 코드 생성
        family = familyRepository.save(family);

        // 유저를 생성된 가족에 할당
        user.setFamily(family);
        userRepository.save(user);

        return family;
    }

    @Transactional
    @Override
    public Family joinFamily(User user, String invitationCode) {
        // 이미 가족이 있는 경우 예외 발생
        if (user.getFamily() != null) {
            throw new IllegalStateException("User is already in a family");
        }

        // 초대 코드로 가족 찾기
        Optional<Family> family = familyRepository.findByInvitationCode(invitationCode);
        if (!family.isPresent()) {
            throw new IllegalArgumentException("Invalid invitation code");
        }

        // 유저를 가족에 할당
        user.setFamily(family.get());
        userRepository.save(user);

        return family.get();
    }

    @Transactional
    @Override
    public void leaveFamily(User user) {
        // 유저가 가족에 속해 있지 않은 경우 예외 발생
        if (user.getFamily() == null) {
            throw new IllegalStateException("User is not in a family");
        }

        // 유저를 가족에서 제거
        user.setFamily(null);
        userRepository.save(user);
    }

    @Override
    public FamilyDTO getFamily(Long familyId) {
        // 가족 ID로 가족 정보 가져오기
        Family family = familyRepository.findById(familyId).orElseThrow(() -> new IllegalArgumentException("가족을 찾지 못했습니다."));
        return convertToDTO(family);
    }


    @Override
    public String getInvitationCode(Long familyId) {
        // 가족 ID로 초대 코드 가져오기
        Family family = familyRepository.findById(familyId).orElseThrow(() -> new IllegalArgumentException("가족을 찾지 못했습니다."));
        return family.getInvitationCode();
    }

    //----------------------DTO 변환용 메소드 -------------------------------------
    private FamilyDTO convertToDTO(Family family) {
        FamilyDTO familyDTO = new FamilyDTO();
        familyDTO.setFamilyId(family.getFamilyId());
        familyDTO.setPetName(family.getPetName());
        familyDTO.setPetColor(family.getPetColor());
        familyDTO.setFamilyStatus(family.getFamilyStatus());
        familyDTO.setInvitationCode(family.getInvitationCode());

        if (family.getPet() != null) {
            PetDTO petDTO = new PetDTO();
            petDTO.setPetId(family.getPet().getPetId());
            petDTO.setPetType(family.getPet().getPetType());
            petDTO.setPetImg(family.getPet().getPetImg());
            familyDTO.setPet(petDTO);
        }

        if (family.getUsers() != null) {
            List<UserDTO> userDTOs = family.getUsers().stream().map(this::convertToUserDTO).collect(Collectors.toList());
            familyDTO.setUsers(userDTOs);
        }

        return familyDTO;
    }

    private UserDTO convertToUserDTO(User user) {
        UserDTO userDTO = new UserDTO();
        userDTO.setUserId(user.getUserId());
        userDTO.setUserName(user.getUserName());
        userDTO.setGender(user.getGender());
        userDTO.setSerialId(user.getSerialId());
        userDTO.setPassword(user.getPassword());
        userDTO.setRole(user.getRole());
        userDTO.setBirthDate(user.getBirthDate().toString());

        // 사용자 DTO는 Family를 포함하지 않음
        return userDTO;
    }
}
