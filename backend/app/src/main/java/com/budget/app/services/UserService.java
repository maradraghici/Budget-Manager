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

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;


import com.budget.app.security.SecurityConfig;
import com.budget.app.repository.UserRepository;
import com.budget.app.repository.UserLoginRepository;
import com.budget.app.model.UserLogin;
import com.budget.app.model.User;
import com.budget.app.vo.UserRequestVo;
import com.budget.app.vo.UserTokenResponseVo;
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
        User user = userRepository.findUserByStatusAndName(userRequestVo.getUsername());
        if (user != null &&
                bCryptPasswordEncoder.matches(userRequestVo.getPassword(),
                user.getPassword())) {
            //String token=  RandomStringUtils.random(25, true, true);
            String token = createJsonWebToken(userRequestVo.getUsername());
            UserLogin userLogin = UserLogin.builder()
                    .user(user)
                    .token(token)
                    .tokenExpireTime(getCurrentTimeStamp())
                    .build();
            userLoginRepository.save(userLogin);
            UserTokenResponseVo userTokenResponseVo = new UserTokenResponseVo();
            userTokenResponseVo.setToken(token);
            userTokenResponseVo.setUsername(userRequestVo.getUsername());

            return userTokenResponseVo;
        } else {
            throw new RuntimeException("User not found");
        }
    }

    public UserAuthorizeResponseVo authorizeV1(UserRequestVo userRequestVo) throws ParseException {
        UserLogin userLogin = userLoginRepository.findByUserAndToken(userRequestVo.getUsername(),
                userRequestVo.getToken());
        if (userLogin != null) {
            DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date date = format.parse(userLogin.getTokenExpireTime());

            if (new Date().compareTo(date) < 1) {
                return new UserAuthorizeResponseVo(userRequestVo.getUsername(), true);
            } else {
                return new UserAuthorizeResponseVo(userRequestVo.getUsername(), false);
            }
        }
        return new UserAuthorizeResponseVo(userRequestVo.getUsername(), false);
    }

    public UserAuthorizeResponseVo authorizeV2(UserRequestVo userRequestVo) throws ParseException {
        String userName = extractUserNameFromToken(userRequestVo.getToken());
        UserLogin userLogin = userLoginRepository.findByUserAndToken(userName, userRequestVo.getToken());
        if (userLogin != null) {
            return new UserAuthorizeResponseVo(userRequestVo.getUsername(),
                    verifyToken(userRequestVo.getUsername(), userRequestVo.getToken()));
        }
        return new UserAuthorizeResponseVo(userRequestVo.getUsername(), false);
    }


    public String getCurrentTimeStamp() {
        Date newDate = DateUtils.addHours(new Date(), 3);
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(newDate);
    }

    public static String createJsonWebToken(String username) {
        return JWT.create()
                .withSubject(username)
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

    public static boolean verifyToken(String user, String token) {
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
