package gl.exercise.UserCrud.services;

import gl.exercise.UserCrud.dto.request.PhoneRequestDTO;
import gl.exercise.UserCrud.models.Phone;
import gl.exercise.UserCrud.models.User;
import gl.exercise.UserCrud.repositories.PhoneRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PhoneService {

    @Autowired
    private PhoneRepository phoneRepository;

    public Phone createPhone(PhoneRequestDTO phoneDTO, User user) {
        Phone phone = Phone.builder()
                .owner(user)
                .number(phoneDTO.getNumber())
                .cityCode(phoneDTO.getCityCode())
                .countryCode(phoneDTO.getCountryCode())
                .build();

        return phoneRepository.save(phone);
    }

    public void assignPhoneToUser(List<PhoneRequestDTO> phones, User owner) {
        phones.forEach(
                phoneDTO -> this.createPhone(phoneDTO, owner)
        );
    }
}
