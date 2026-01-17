package com.budget.app.services;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.budget.app.exception.ResourceNotFoundException;
import com.budget.app.exception.UnauthorizedException;
import com.budget.app.model.Invited;
import com.budget.app.model.InvitedLogin;
import com.budget.app.repository.InvitedLoginRepository;
import com.budget.app.repository.InvitedRepository;
import com.budget.app.vo.InvitedAuthorizeResponseVo;
import com.budget.app.vo.InvitedRequestVo;
import com.budget.app.vo.InvitedResponseVo;
import com.budget.app.vo.InvitedTokenResponseVo;
import com.budget.app.vo.InvitedUpdatePhoneVo;
import com.budget.app.vo.InvitedVo;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.time.DateUtils;
// import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class InvitedService {

    private final InvitedRepository invitedRepository;
    private final InvitedLoginRepository invitedLoginRepository;

    public InvitedService(InvitedRepository invitedRepository, InvitedLoginRepository invitedLoginRepository) {
        this.invitedRepository = invitedRepository;
        this.invitedLoginRepository = invitedLoginRepository;
    }

    public InvitedResponseVo getById(Long id) {
        Invited invited = invitedRepository.findByInvitedId(id);
        if (invited == null) {
            throw new IllegalArgumentException("Invited not found: " + id);
        }
        return toVo(invited);
    }

    public List<InvitedResponseVo> getAll() {
        return invitedRepository.findAll()
                .stream()
                .map(this::toVo)
                .toList();
    }

    public InvitedTokenResponseVo create(InvitedVo vo) {

        if (vo.getInvitedName() == null || vo.getInvitedName().isBlank()){
            throw new IllegalArgumentException("Invited name is required");
        }

        if (vo.getPhoneNumber() == null || vo.getPhoneNumber().isBlank()){
            throw new IllegalArgumentException("Phone number is required");
        }

        Invited invited = invitedRepository.save(
                Invited.builder()
                        .invitedName(vo.getInvitedName())
                        .phoneNumber(vo.getPhoneNumber())
                        .build()
        );

        String token = createJsonWebToken(invited.getInvitedId());

        InvitedLogin invitedLogin = InvitedLogin.builder()
            .invited(invited)
            .token(token)
            .tokenExpireTime(getCurrentTimeStamp())
            .build();

        invitedLoginRepository.save(invitedLogin);

        return InvitedTokenResponseVo.builder()
                    .invitedId(invited.getInvitedId())
                    .token(token)
                    .build();
    }

    public void delete(Long id) {
        Invited invited = invitedRepository.findByInvitedId(id);
        if (invited == null){
            throw new IllegalArgumentException("Invited not found: " + id);
        }

        InvitedLogin Inv_login = invitedLoginRepository.findByInvited_InvitedId(id);
        invitedLoginRepository.delete(Inv_login);

        invitedRepository.delete(invited);
    }

    private InvitedResponseVo toVo(Invited invited) {
        return InvitedResponseVo.builder()
                .invitedId(invited.getInvitedId())
                .invitedName(invited.getInvitedName())
                .phoneNumber(invited.getPhoneNumber())
                .build();
    }

    public InvitedTokenResponseVo validateInvitedPhoneAndGenerateToken(
            InvitedVo requestVo
    ) {

        System.out.println("Validating invited phone: " + requestVo.getPhoneNumber());

        Invited invited = invitedRepository.findByPhoneNumber(requestVo.getPhoneNumber());

        System.out.println("Invited found: " + (invited != null ? invited.getInvitedName() : "null"));

        if (invited != null) {

            String token = createJsonWebToken(invited.getInvitedId());

            InvitedLogin invitedLogin = InvitedLogin.builder()
                    .invited(invited)
                    .token(token)
                    .tokenExpireTime(getCurrentTimeStamp())
                    .build();

            InvitedLogin last_InvitedLogin = invitedLoginRepository.findByInvited_InvitedId(invited.getInvitedId());
            if (last_InvitedLogin != null){
                invitedLoginRepository.deleteById(last_InvitedLogin.getInvitedLoginId());
            }
            invitedLoginRepository.save(invitedLogin);

            return InvitedTokenResponseVo.builder()
                    .invitedId(invited.getInvitedId())
                    .token(token)
                    .build();

        } else {
            throw new IllegalArgumentException("Invited not found with phone number");
        }
    }

    public void updatePhoneNumber(String bearerToken, InvitedUpdatePhoneVo vo) {

        String token = bearerToken.replace("Bearer ", "");

        Long invitedId;
        try {
            invitedId = extractInvitedIdFromToken(token);
        } catch (JWTVerificationException e) {
            throw new UnauthorizedException("Invalid token");
        }

        if (!verifyToken(token)) {
            throw new UnauthorizedException("Token expired or invalid");
        }

        Invited invited = invitedRepository.findByInvitedId(invitedId);
        if (invited == null) {
            throw new ResourceNotFoundException("Invited not found");
        }

        invited.setPhoneNumber(vo.getPhoneNumber());
        invitedRepository.save(invited);
    }


    public InvitedAuthorizeResponseVo authorizeV2(InvitedRequestVo requestVo) {

        try {
            Long invitedId = extractInvitedIdFromToken(requestVo.getToken());

            InvitedLogin invitedLogin =
                    invitedLoginRepository
                            .findByInvited_InvitedIdAndToken(invitedId, requestVo.getToken());

            if (invitedLogin != null) {
                return new InvitedAuthorizeResponseVo(
                        invitedId,
                        verifyToken(requestVo.getToken())
                );
            }

            return new InvitedAuthorizeResponseVo(invitedId, false);

        } catch (JWTVerificationException e) {
            return new InvitedAuthorizeResponseVo(null, false);
        }
    }


    public String getCurrentTimeStamp() {
        Date newDate = DateUtils.addHours(new Date(), 3);
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(newDate);
    }

    public static String createJsonWebToken(Long invitedId) {
        return JWT.create()
                .withSubject(String.valueOf(invitedId))
                .withIssuer("auth0")
                .withExpiresAt(DateUtils.addHours(new Date(), 3))
                .sign(Algorithm.HMAC256("secret"));
    }

    public static Long extractInvitedIdFromToken(String token) {
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

            verifier.verify(token); // expiration vérifiée ici
            return true;

        } catch (JWTVerificationException exception) {
            return false;
        }

    }
}
