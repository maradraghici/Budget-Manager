package com.budget.app.services;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.springframework.beans.factory.annotation.Autowired;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Date;


import com.budget.app.security.SecurityConfig;
import com.budget.app.repository.UserRepository;
import com.budget.app.repository.UserLoginRepository;
import com.budget.app.model.UserLogin;
import com.budget.app.model.User;
import com.budget.app.vo.UserRequestVo;
import com.budget.app.vo.UserTokenResponseVo;
import com.budget.app.vo.UserUpdateVo;
import com.budget.app.vo.UserAuthorizeResponseVo;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserLoginRepository userLoginRepository;

    @Autowired
    private PasswordEncoder bCryptPasswordEncoder;

    @Autowired
    private SecurityConfig securityConfig;

    public UserRequestVo findByUserId(Long userId) {

        User user = userRepository.findByUserId(userId);

        return UserRequestVo.builder()
                .username(user.getUserName())
                .email(user.getEmail())
                .build();
    }

    public void registerNewUser(UserRequestVo userRequestVo) {
        
        if (userRequestVo.getPassword() == null || userRequestVo.getPassword().isEmpty()) {
            throw new IllegalArgumentException("Password cannot be null or empty");
        }

        User user = User.builder()
                .userName(userRequestVo.getUsername())
                .password(securityConfig.passwordEncoder().encode(userRequestVo.getPassword()))
                .email(userRequestVo.getEmail())
                .build();
        userRepository.save(user);
    }

    public UserTokenResponseVo validateUserCredentialsAndGenerateToken(UserRequestVo userRequestVo) {
        // print userRequestVo
        System.out.println("Validating user: " + userRequestVo.getUsername());
        User user = userRepository.findByUserName(userRequestVo.getUsername());
        System.out.println("User found: " + (user != null ? user.getUserName() : "null"));
        if (user != null &&
                bCryptPasswordEncoder.matches(userRequestVo.getPassword(),
                user.getPassword())) {
            //String token=  RandomStringUtils.random(25, true, true);
            String token = createJsonWebToken(user.getUserId());
            UserLogin userLogin = UserLogin.builder()
                    .user(user)
                    .token(token)
                    .tokenExpireTime(getCurrentTimeStamp())
                    .build();
            UserLogin last_userLogin = userLoginRepository.findByUser_UserId(user.getUserId());
            if (last_userLogin != null){
                userLoginRepository.deleteById(last_userLogin.getUserLoginId());
            }
            userLoginRepository.save(userLogin);
            UserTokenResponseVo userTokenResponseVo = new UserTokenResponseVo();
            userTokenResponseVo.setToken(token);
            userTokenResponseVo.setUsername(userRequestVo.getUsername());

            return userTokenResponseVo;
        } else {
            throw new RuntimeException("User not found");
        }
    }

    public UserAuthorizeResponseVo authorizeV2(UserRequestVo userRequestVo) {
        try {
            Long userId = extractUserIdFromToken(userRequestVo.getToken());

            UserLogin userLogin = userLoginRepository
                    .findByUser_UserIdAndToken(userId, userRequestVo.getToken());

            if (userLogin != null) {
                return new UserAuthorizeResponseVo(
                    userId, 
                    verifyToken(userRequestVo.getToken()));
            }

            return new UserAuthorizeResponseVo(userId, false);
        } catch (JWTVerificationException e) {
            return new UserAuthorizeResponseVo(null, false);
        }
    }

    public void updateUser(String token, UserUpdateVo vo) {
        // 1️⃣ Vérification + extraction userId
        Long userId;
        try {
            userId = extractUserIdFromToken(token);
        } catch (JWTVerificationException e) {
            throw new RuntimeException("Invalid token");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        // 2️⃣ Update username
        if (vo.getUsername() != null && !vo.getUsername().isBlank()) {
            user.setUserName(vo.getUsername());
        }

        // 3️⃣ Update phone
        if (vo.getPhoneNumber() != null && !vo.getPhoneNumber().isBlank()) {
            user.setPhoneNumber(vo.getPhoneNumber());
        }

        // 4️⃣ Update password (SECURISÉ)
        if (vo.getNewPassword() != null && !vo.getNewPassword().isBlank()) {

            if (vo.getOldPassword() == null || vo.getOldPassword().isBlank()) {
                throw new IllegalArgumentException("Old password is required");
            }

            if (!bCryptPasswordEncoder.matches(vo.getOldPassword(), user.getPassword())) {
                throw new IllegalArgumentException("Old password is incorrect");
            }

            user.setPassword(
                    bCryptPasswordEncoder.encode(vo.getNewPassword())
            );

            // Optionnel mais recommandé : invalider les anciens tokens
            // userLoginRepository.deleteByUser_UserId(userId);
        }

        userRepository.save(user);
    }


    public String getCurrentTimeStamp() {
        Date newDate = DateUtils.addHours(new Date(), 3);
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(newDate);
    }

    public static String createJsonWebToken(Long userId) {
        return JWT.create()
                .withSubject(String.valueOf(userId))
                .withIssuer("auth0")
                .withExpiresAt(DateUtils.addHours(new Date(), 3))
                .sign(Algorithm.HMAC256("secret"));
    }

    public static String extractUserNameFromToken(String token) throws JWTVerificationException {

        Algorithm algorithm = Algorithm.HMAC256("secret");
        JWTVerifier verifier = JWT.require(algorithm)
                .withIssuer("auth0")
                .build();
        DecodedJWT jwt = verifier.verify(token);
        return jwt.getSubject();

    }

    public static Long extractUserIdFromToken(String token) {
        Algorithm algorithm = Algorithm.HMAC256("secret");
        JWTVerifier verifier = JWT.require(algorithm)
                .withIssuer("auth0")
                .build();

        DecodedJWT jwt = verifier.verify(token);
        return Long.valueOf(jwt.getSubject());
    }

    public static boolean verifyToken(String token) {
        try {
            Algorithm algorithm = Algorithm.HMAC256("secret");
            JWTVerifier verifier = JWT.require(algorithm)
                    .withIssuer("auth0")
                    .build();
            DecodedJWT jwt = verifier.verify(token);
            Date dateTheTokenWillExpire = jwt.getExpiresAt();
            if (new Date().compareTo(dateTheTokenWillExpire) < 1) {
                return true;
            } else {
                return false;
            }
        } catch (JWTVerificationException exception) {
            return false;
        }

    }
}
