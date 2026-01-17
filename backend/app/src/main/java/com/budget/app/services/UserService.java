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
import com.budget.app.exception.ResourceNotFoundException;
import com.budget.app.exception.UnauthorizedException;
import com.budget.app.model.User;
import com.budget.app.vo.UserRequestVo;
import com.budget.app.vo.UserTokenResponseVo;
import com.budget.app.vo.UserUpdateVo;
import com.budget.app.vo.UserAuthorizeRequestVo;
import com.budget.app.vo.UserAuthorizeResponseVo;
import com.budget.app.vo.UserLoginRequestVo;

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

        User user = userRepository.findById(userId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("User not found: " + userId));

        return UserRequestVo.builder()
                .username(user.getUserName())
                .email(user.getEmail())
                .build();
        }

    public UserTokenResponseVo registerNewUser(UserRequestVo userRequestVo) {
        
        if (userRequestVo.getPassword() == null || userRequestVo.getPassword().isEmpty()) {
            throw new IllegalArgumentException("Password cannot be null or empty");
        }

        if (userRequestVo.getUsername() == null || userRequestVo.getUsername().isBlank()) {
            throw new IllegalArgumentException("Username is required");
        }

        if (userRequestVo.getPhoneNumber() == null || userRequestVo.getPhoneNumber().isBlank()) {
            throw new IllegalArgumentException("Phone number is required");
        }

        User user = User.builder()
                .userName(userRequestVo.getUsername())
                .password(securityConfig.passwordEncoder().encode(userRequestVo.getPassword()))
                .phoneNumber(userRequestVo.getPhoneNumber())
                .email(userRequestVo.getEmail())
                .build();
        User savedUser = userRepository.save(user);

        String token = createJsonWebToken(savedUser.getUserId());

            UserLogin userLogin = UserLogin.builder()
                .user(savedUser)
                .token(token)
                .tokenExpireTime(getCurrentTimeStamp())
                .build();

        userLoginRepository.save(userLogin);
        
        return UserTokenResponseVo.builder()
            .userId(savedUser.getUserId())
            .token(token)
            .build();
    }

    public UserTokenResponseVo validateUserCredentialsAndGenerateToken(UserLoginRequestVo userRequestVo) {

        // 1️⃣ Validation des inputs
        if (userRequestVo.getUsername() == null || userRequestVo.getUsername().isBlank()
                || userRequestVo.getPassword() == null || userRequestVo.getPassword().isBlank()) {
            throw new IllegalArgumentException("Username and password are required");
        }

        // 2️⃣ Recherche utilisateur
        User user = userRepository.findByUserName(userRequestVo.getUsername());

        if (user == null) {
            throw new IllegalArgumentException("Invalid credentials");
        }

        // 3️⃣ Vérification mot de passe
        if (!bCryptPasswordEncoder.matches(
                userRequestVo.getPassword(),
                user.getPassword())) {
            throw new IllegalArgumentException("Invalid credentials");
        }

        // 4️⃣ Génération du JWT avec userId
        String token = createJsonWebToken(user.getUserId());

        // 5️⃣ Invalidation ancien token (1 session max)
        UserLogin lastLogin = userLoginRepository
                .findByUser_UserId(user.getUserId());

        if (lastLogin != null) {
            userLoginRepository.deleteById(lastLogin.getUserLoginId());
        }

        // 6️⃣ Sauvegarde nouveau token
        UserLogin userLogin = UserLogin.builder()
                .user(user)
                .token(token)
                .tokenExpireTime(getCurrentTimeStamp())
                .build();

        userLoginRepository.save(userLogin);

        // 7️⃣ Réponse
        return UserTokenResponseVo.builder()
                .userId(user.getUserId())
                .token(token)
                .build();
    }

    public UserAuthorizeResponseVo authorizeV2(UserAuthorizeRequestVo userRequestVo) {
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

        Long userId;
        try {
            userId = extractUserIdFromToken(token);
        } catch (JWTVerificationException e) {
            throw new UnauthorizedException("Invalid or expired token");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("User not found"));

        if (vo.getUsername() != null && !vo.getUsername().isBlank()) {
            user.setUserName(vo.getUsername());
        }

        if (vo.getPhoneNumber() != null && !vo.getPhoneNumber().isBlank()) {
            user.setPhoneNumber(vo.getPhoneNumber());
        }

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
        }

        userRepository.save(user);
    }

    public void deleteUser(Long userId){
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new ResourceNotFoundException("User not found: " + userId));

        // Supprimer tous les tokens liés à ce user
        UserLogin logins = userLoginRepository.findByUser_UserId(userId);
        userLoginRepository.delete(logins);

        // Maintenant on peut supprimer l'utilisateur
        userRepository.delete(user);
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
