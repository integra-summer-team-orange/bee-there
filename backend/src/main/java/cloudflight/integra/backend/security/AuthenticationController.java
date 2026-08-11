package cloudflight.integra.backend.security;

import cloudflight.integra.backend.user.UserMapper;
import cloudflight.integra.backend.user.UserService;
import cloudflight.integra.backend.user.model.User;
import cloudflight.integra.backend.user.model.UserRequestDto;
import cloudflight.integra.backend.user.model.UserResponseDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthenticationController {

    private UserService service;
    private UserMapper mapper;

    public AuthenticationController(UserService service,UserMapper mapper) {
        this.service = service;
        this.mapper = mapper;
    }

    @PostMapping("/register")
    public ResponseEntity<UserResponseDto> register(@RequestBody UserRequestDto dto){
        User user = service.create(mapper.fromDto(dto));
        return ResponseEntity.status(HttpStatus.CREATED).body(mapper.toDto(user));
    }



}
