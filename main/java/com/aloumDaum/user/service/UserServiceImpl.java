package com.aloumDaum.user.service;

import com.aloumDaum.user.data.DeviceEntity;
import com.aloumDaum.user.data.LoginEntity;
import com.aloumDaum.user.data.UserEntity;
import com.aloumDaum.user.exception.ValidationException;
import com.aloumDaum.user.model.request.LoginRequest;
import com.aloumDaum.user.model.request.OTPRequest;
import com.aloumDaum.user.model.request.PasswordChangeRequestModel;
import com.aloumDaum.user.model.request.ResetPasswordRequest;
import com.aloumDaum.user.model.request.UpdateRequest;
import com.aloumDaum.user.model.request.UserCreateRequestModel;
import com.aloumDaum.user.model.request.UserInfo;
import com.aloumDaum.user.model.response.Data;
import com.aloumDaum.user.model.response.LoginResponse;
import com.aloumDaum.user.model.response.LogoutResponse;
import com.aloumDaum.user.model.response.ResponseModel;
import com.aloumDaum.user.model.response.UserResponse;
import com.aloumDaum.user.model.response.UserViewResponseModel;
import com.aloumDaum.user.model.response.UsersViewResponseModel;
import com.aloumDaum.user.redis.RedisUtility;
import com.aloumDaum.user.repository.DeviceRepository;
import com.aloumDaum.user.repository.UserRepository;
import com.aloumDaum.user.utils.ConstantUtils;
import com.aloumDaum.user.utils.MailSenderService;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.client.HttpClientErrorException;
import javax.persistence.EntityNotFoundException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    UserRepository userRepository;

    @Value("${auth.token.secret.key}")
    private String authSecret;

    @Autowired
    RedisUtility redisUtility;

    @Value("${master.otp}")
    private String masterOtp;

    @Value("${spring.mail.username}")
    private String mail;

    @Autowired
    MailSenderService mailSenderService;

    @Autowired
    private ModelMapper mapper;

    @Autowired
    private Random random;

    private BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();

    private static final Logger LOGGER = LoggerFactory.getLogger(UserServiceImpl.class);

    @Autowired
    DeviceRepository deviceRepository;

    @Override
    public ResponseEntity loginUser(LoginRequest loginRequest, MultiValueMap<String, String> headers) {
        Data<LoginResponse> ui = new Data<>();
        UserEntity userEntity = userRepository.findByEmail(loginRequest.getEmail());
        if (userEntity != null) {
            if (bCryptPasswordEncoder.matches(loginRequest.getPassword(), userEntity.getPassword())) {
                String token = generateAdminToken(userEntity, headers);

                LoginEntity login = userEntity.getLoginEntity();
                LoginResponse loginResponse = new LoginResponse();
                if (login == null) {
                    loginResponse.setExistingUser(false);
                    login = new LoginEntity();
                } else {
                    loginResponse.setExistingUser(true);
                }
                loginResponse.setToken(token);
                ui.setData(loginResponse);
                userEntity.setLoginEntity(login);
                login.setUserEntity(userEntity);


                List<DeviceEntity> deviceDetailsEntityList = login.getDeviceDetails();
                DeviceEntity deviceDetails = new DeviceEntity();
                if (deviceDetailsEntityList == null) {
                    deviceDetailsEntityList = new ArrayList<DeviceEntity>();
                }
                deviceDetails.setUserId(userEntity.getUserId());
                deviceDetails.setIsActive(true);
                deviceDetails.setToken(token);
                deviceDetails.setLoginEntity(login);
                deviceDetailsEntityList.add(deviceDetails);
                login.setDeviceDetails(deviceDetailsEntityList);

                try {
                    userRepository.save(userEntity);
                } catch (HttpClientErrorException.BadRequest exception) {
                    exception.printStackTrace();
                }
            } else {

                return new ResponseEntity(new ResponseModel<>(HttpStatus.UNAUTHORIZED, "Incorrect password ", null, null), HttpStatus.UNAUTHORIZED);
            }
        }
        else
           return new ResponseEntity(new ResponseModel<>(HttpStatus.BAD_REQUEST, "Email doesn't exists ", null, null), HttpStatus.BAD_REQUEST);
        return  new ResponseEntity(new ResponseModel<>(HttpStatus.OK, "Login successfully", null, ui), HttpStatus.OK);
    }




    @Override
    public ResponseEntity<ResponseModel<?>> sendOtp(OTPRequest otpRequest) {
        UserEntity userEntity = userRepository.findByEmail(otpRequest.getEmail());
        if (userEntity != null) {
            Random rnd = new Random();
            int number = rnd.nextInt(9999);
            String otp = String.format("%06d", number);

            String key = otpRequest.getEmail() + "_OTP";
            String email = otpRequest.getEmail();
            redisUtility.setData(key, otp);

            try {
                mailSenderService.setMailSender(email, "OTP", otp);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            throw new EntityNotFoundException("Invalid Email");
        }
        return new ResponseEntity(new ResponseModel<>(HttpStatus.OK, "Otp send successfully", null, null), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<ResponseModel<String>> setPassword(ResetPasswordRequest resetPasswordRequest) {
        UserEntity userEntity = userRepository.findByEmail(resetPasswordRequest.getEmail());
        if (userEntity != null) {
            String email = resetPasswordRequest.getEmail();
            String otp = resetPasswordRequest.getOtp();
            String key = email + "_OTP";
            String dbOTP = redisUtility.getData(key);

            if (dbOTP != null || otp != null) {

                if (otp.equals(dbOTP) || (otp.equals(masterOtp))) {
                    redisUtility.delete(key);
                    userEntity.setPassword(bCryptPasswordEncoder.encode(resetPasswordRequest.getPassword()));
                    userRepository.save(userEntity);
                } else {
                    throw new ValidationException("Invalid Otp");
                }
            }

            return ResponseEntity.ok(new ResponseModel<>(HttpStatus.OK, "Password Updated Successfully", null, null));
        } else {
            throw new EntityNotFoundException("Invalid Email");
        }
    }


    @Override
    public LogoutResponse logout(String token, UserInfo userInfo) {
        String[] Token = token.split(" ");
        token = String.valueOf(Token[1]);
        DeviceEntity device = deviceRepository.getByUserId(userInfo.getUserId(),token);
        if (device != null) {
            if (Token.length == 2 && device.getToken().equals(token)) {

                device.setIsActive(false);
               deviceRepository.save(device);
                return getLogoutResponse(true, "User Logged out");
            } else {
                return getLogoutResponse(false, "Invalid token");
            }
        }

            throw new EntityNotFoundException("Invalid UserId");
    }

    @Override
    public ResponseEntity<ResponseModel<String>> updateUser(UpdateRequest updateRequest) {
        UserEntity updatedUser = userRepository.findByEmail(updateRequest.getEmail());
        if (updatedUser == null) {
            throw new EntityNotFoundException("Email with id " + updateRequest.getEmail() + " does not exist.");
        }
        if (updateRequest.getPhoneNumber() != null && !updateRequest.getPhoneNumber().isEmpty() || updateRequest.getAddress() != null
                && !updateRequest.getAddress().isEmpty()) {
            validatePhoneNumber(updateRequest.getPhoneNumber());
            updatedUser.setPhoneNumber(updateRequest.getPhoneNumber());
            updatedUser.setAddress(updateRequest.getAddress());
            userRepository.save(updatedUser);
        }

            return ResponseEntity.ok(new ResponseModel<>(HttpStatus.OK, "User Updated Successfully", null, null));
        }


    private String generateAdminToken(UserEntity userEntity, MultiValueMap<String, String> headers) {
        final Map<String, Object> claims = new ConcurrentHashMap<>();
        final List<String> pagePermissions = new ArrayList<>();
        if (userEntity.getRoles() != null && !CollectionUtils.isEmpty(userEntity.getRoles().getPermissions())) {
            userEntity.getRoles().getPermissions().forEach(permission -> {
                pagePermissions.add(permission.getPageName() + "_" + permission.getPagePermission());
            });
        }
        String userAgent = headers.getFirst(ConstantUtils.USER_AGENT);
        if (!StringUtils.isEmpty(userAgent)) {
            claims.put(ConstantUtils.USER_AGENT, userAgent);
        }
        String email = userEntity.getEmail();
        if (!StringUtils.isEmpty(email)) {
            claims.put(ConstantUtils.EMAIL, email);
        }
        claims.put("iat", new Date());
        claims.put("issuer", "aloumdaum");
        claims.put("permissions", pagePermissions);
        claims.put("userId", userEntity.getUserId());
        if (userEntity.getRoles() != null)
            claims.put("role", userEntity.getRoles().getRoleName());
        return Jwts.builder().setSubject((userEntity.getEmail())).addClaims(claims).signWith(SignatureAlgorithm.HS512, authSecret).compact();
    }

    private LogoutResponse getLogoutResponse(boolean successStatus, String message) {
        LogoutResponse logoutResponse = new LogoutResponse();
        logoutResponse.setMessage(message);
        logoutResponse.setSuccess(successStatus);
        return logoutResponse;
    }

    private void validatePhoneNumber(final String phoneNo) {
        final Pattern pattern = Pattern.compile("^((91)|(\\+91)|0?)[6-9]{1}\\d{9}$");
        final Matcher matcher = pattern.matcher(phoneNo);
        if (!(matcher.find() && matcher.group().equals(phoneNo))) {
            throw new ValidationException("Invalid phone number.");
        }
    }


    @Override
    public void createUser(UserCreateRequestModel userCreateRequest) {
        final UserEntity userEntity = userRepository.findByEmail(userCreateRequest.getEmail());
        if (userEntity != null) {
            throw new javax.validation.ValidationException("Email invalid or already exists");
        }
        final ModelMapper mapper = new ModelMapper();
        UserEntity entity = mapper.map(userCreateRequest, UserEntity.class);
        final String generatedPassword = generatePassword();
        entity.setPassword(bCryptPasswordEncoder.encode(generatedPassword));
        try {
            userRepository.save(entity);
        } catch (HttpClientErrorException.BadRequest exception) {
            exception.printStackTrace();
        }
        try {
            mailSenderService.setMailSender(userCreateRequest.getEmail(), "Login Credentials", "Email : "
                    + userCreateRequest.getEmail() + " , Password : " + generatedPassword);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Data findAll(String userId, Pageable pageable) {
        final UsersViewResponseModel usersViewResponseModel = new UsersViewResponseModel();
        final UserViewResponseModel userViewResponseModel = new UserViewResponseModel();
        Data data = new Data();

        if (userId == null || userId.isEmpty()) {
            final List<UserEntity> users = new ArrayList<>();
            final Pageable page = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(),
                    Sort.by("createdAt").descending());
            List<UserResponse> userRes = new ArrayList<>();
            final Page<UserEntity> userEntities = userRepository.findAll(page);
            if (userEntities.hasContent()) {
                usersViewResponseModel.setTotalElements(userEntities.getTotalElements());
                usersViewResponseModel.setTotalPages(userEntities.getTotalPages());
                userEntities.getContent().forEach(user -> {
                    userRes.add(prepareUserViewResponse(user));
                });
                usersViewResponseModel.setUsers(userRes);

                data.setData(usersViewResponseModel);
            }
        } else {
            UserEntity user = userRepository.findByUserId(userId);
            if (user != null) {

                UserResponse userResponse = new UserResponse();
                userResponse = mapper.map(user, UserResponse.class);
                userViewResponseModel.setUserEntity(userResponse);
                data.setData(userViewResponseModel);

            }
        }

        return data;
    }

    private UserResponse prepareUserViewResponse(final UserEntity entity) {
        final ModelMapper mapper = new ModelMapper();
        UserResponse response = mapper.map(entity, UserResponse.class);

        return response;

    }

    public void changePassword(PasswordChangeRequestModel passChangeRequest, UserInfo userInfo) {
        UserEntity userEntity = userRepository.findByEmail(userInfo.getEmail());
        if (userEntity == null) {
            throw new ValidationException("User not found");
        }
        String oldPass = userEntity.getPassword();
        if (bCryptPasswordEncoder.matches(passChangeRequest.getOldPassword(), oldPass)) {
            try {
                userEntity.setPassword(bCryptPasswordEncoder.encode(passChangeRequest.getNewPassword()));
                userRepository.save(userEntity);
            } catch (ValidationException e) {
                e.printStackTrace();
            }
        } else {
            throw new ValidationException("Incorrect Password");
        }

    }

    private String generatePassword() {
        final String upperCases = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        final String lowerCases = "abcdefghijklmnopqrstuvwxyz";
        final String numbers = "0123456789";
        final String symbols = "!@#$%^&*_=+-/.?<>)";

        final String values = upperCases + lowerCases + numbers + symbols;

        final int length = 8;
        final char[] passwordChar = new char[length];

        for (int i = 0; i < length; i++) {
            passwordChar[i] = values.charAt(this.random.nextInt(values.length()));

        }
        String password = new String(passwordChar);
        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("password is =" + password);
        }
        return password;
    }

}
