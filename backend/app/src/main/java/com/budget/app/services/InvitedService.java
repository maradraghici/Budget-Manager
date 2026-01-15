package com.budget.app.services;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.budget.app.model.Invited;
import com.budget.app.model.InvitedLogin;
import com.budget.app.repository.InvitedLoginRepository;
import com.budget.app.repository.InvitedRepository;
import com.budget.app.vo.InvitedAuthorizeResponseVo;
import com.budget.app.vo.InvitedRequestVo;
import com.budget.app.vo.InvitedResponseVo;
import com.budget.app.vo.InvitedTokenResponseVo;
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

    public void create(InvitedVo vo) {
        invitedRepository.save(
                Invited.builder()
                        .invitedName(vo.getInvitedName())
                        .phoneNumber(vo.getPhoneNumber())
                        .build()
        );
    }

    public void delete(Long id) {
        invitedRepository.deleteById(id);
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

            String token = createJsonWebToken(invited.getInvitedName());

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

        } else {
            throw new RuntimeException("Invited not found with phone number");
        }
    }


    public InvitedAuthorizeResponseVo authorizeV2(InvitedRequestVo requestVo) {

        String invitedId = extractInvitedIdFromToken(requestVo.getToken());

        InvitedLogin invitedLogin =
                invitedLoginRepository.findByInvitedAndToken(invitedId, requestVo.getToken());

        if (invitedLogin != null) {
            return new InvitedAuthorizeResponseVo(
                    invitedId,
                    verifyToken(invitedId, requestVo.getToken())
            );
        }
        return new InvitedAuthorizeResponseVo(invitedId, false);
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

    public static String extractInvitedIdFromToken(String token) throws JWTVerificationException {

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
