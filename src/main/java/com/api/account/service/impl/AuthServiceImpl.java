package com.api.account.service.impl;

import com.api.account.mapper.UserMapper;
import com.api.account.model.Role;
import com.api.account.model.User;
import com.api.account.model.UserVerification;
import com.api.account.model.dto.request.LoginRequest;
import com.api.account.model.dto.request.RefreshTokenRequest;
import com.api.account.model.dto.request.RegisterRequest;
import com.api.account.model.dto.request.VerificationRequest;
import com.api.account.model.dto.response.AuthResponse;
import com.api.account.model.dto.response.RegisterResponse;
import com.api.account.repository.RoleRepository;
import com.api.account.repository.UserRepository;
import com.api.account.repository.UserVerificationRepository;
import com.api.account.service.AuthService;
import com.api.account.utils.RandomUtil;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.security.oauth2.server.resource.authentication.BearerTokenAuthenticationToken;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationProvider;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    private final UserVerificationRepository userVerificationRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    private final DaoAuthenticationProvider daoAuthenticationProvider;
    private final JwtAuthenticationProvider jwtAuthenticationProvider;

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    // send mail
    private final UserVerificationRepository verificationRepository;
    // send mail

    private final JavaMailSender javaMailSender;
    private final JwtEncoder accessTokenJwtEncoder;
    private final JwtEncoder refreshTokenJwtEncoder;

    // inject sen mail
    @Value("${spring.mail.username}")
    private String adminEmail;
    @Override
    public AuthResponse login(LoginRequest loginRequest) {
        // auth of spring security
        Authentication auth = new UsernamePasswordAuthenticationToken(loginRequest.phoneNumber(),loginRequest.password());
        auth = daoAuthenticationProvider.authenticate(auth);
        // log auth
        log.info("Auth: {}", auth.getAuthorities());

        // log role user
        auth.getAuthorities()
                .forEach(grantedAuthority ->
                        log.info("Authorities: {}", grantedAuthority.getAuthority()));

        // ROLE_USER ROLE_ADMIN
        String scope = auth
                .getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)// map and get authority
                .collect(Collectors.joining(" ")); // map spring join service
        log.info("Scope: {}", scope);
        // Generate JWT Token by JwtEncoder
        // 1. Define JwtClaimsSet (payload)
        Instant now = Instant.now();
        JwtClaimsSet jwtAccessClaimsSet = JwtClaimsSet.builder()
                .id(auth.getName())
                .subject("Access APIs")
                .issuer(auth.getName())
                .issuedAt(now)
                .expiresAt(now.plus(30, ChronoUnit.MINUTES))
                .audience(List.of("Next js","Android","ios"))
                //custom
                .claim("isAdmin",true)
                .claim("studentId","ISTAD0022")
                .claim("scope",scope)
                .build();
        // refresh token
        JwtClaimsSet jwtRefreshClaimsSet = JwtClaimsSet.builder()
                .id(auth.getName())
                .subject("Refresh Token")
                .issuer(auth.getName())
                .issuedAt(now)
                .expiresAt(now.plus(7, ChronoUnit.DAYS))
                .audience(List.of("Next js","Android","ios"))
                //custom
                .claim("scope",scope)
                .build();
        // 2. Generate Token
        // generate access
        String accessToken = accessTokenJwtEncoder
                .encode(JwtEncoderParameters.from(jwtAccessClaimsSet))
                .getTokenValue();

        // generate refresh
        String refreshToken = refreshTokenJwtEncoder
                .encode(JwtEncoderParameters.from(jwtRefreshClaimsSet))
                .getTokenValue();
        log.info("Access Token : {}",accessToken);
        log.info("Refresh Token : {}",refreshToken);
        return AuthResponse.builder()
                .tokenType("Bearer")
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    @Override
    public AuthResponse refreshToken(RefreshTokenRequest refreshTokenRequest) {
        String refreshToken = refreshTokenRequest.refreshToken();
        Authentication auth = new BearerTokenAuthenticationToken(refreshToken);
        auth= jwtAuthenticationProvider.authenticate(auth);
        log.info("Auth: {}", auth.getPrincipal());
        // ROLE_USER ROLE_ADMIN
        String scope = auth
                .getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)// map and get authority
                .collect(Collectors.joining(" ")); // map spring join service
        log.info("Scope: {}", scope);
        // Generate JWT Token by JwtEncoder
        // 1. Define JwtClaimsSet (payload)
        Jwt jwt = (Jwt) auth.getCredentials();
        Instant now = Instant.now();
        JwtClaimsSet jwtAccessClaimsSet = JwtClaimsSet.builder()
                .id(jwt.getId())
                .subject("Access APIs")
                .issuer(jwt.getId())
                .issuedAt(now)
                .expiresAt(now.plus(10, ChronoUnit.SECONDS))
                .audience(jwt.getAudience())
                //custom
                .claim("isAdmin",true)
                .claim("studentId","ISTAD0022")
                .claim("scope",jwt.getClaimAsString("scope"))
                .build();

//        // 2. Generate Token
//
        String accessToken = accessTokenJwtEncoder
                .encode(JwtEncoderParameters.from(jwtAccessClaimsSet))
                .getTokenValue();
        Instant expiresAt = jwt.getExpiresAt();
        long remainingDays = Duration.between(now, expiresAt).toDays();
        log.info("remainingDays: {}", remainingDays);
        if(remainingDays<=1){
            // refresh token
            JwtClaimsSet jwtRefreshClaimsSet = JwtClaimsSet.builder()
                    .id(auth.getName())
                    .subject("Refresh Token")
                    .issuer(auth.getName())
                    .issuedAt(now)
                    .expiresAt(now.plus(1, ChronoUnit.DAYS))
                    .audience(List.of("Next js","Android","ios"))
                    //custom
                    .claim("scope",jwt.getClaimAsString("scope"))
                    .build();
            refreshToken = refreshTokenJwtEncoder
                    .encode(JwtEncoderParameters.from(jwtRefreshClaimsSet))
                    .getTokenValue();
        }

        return AuthResponse.builder()
                .tokenType("Bearer")
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    @Override
    public RegisterResponse register(RegisterRequest registerRequest) {
        if(userRepository.existsByPhoneNumber(registerRequest.phoneNumber())){
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Phone number already exists");
        }
        if(userRepository.existsByEmail(registerRequest.email())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email already exists");
        }
        if(!registerRequest.password().equals(registerRequest.confirmedPassword())){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Passwords do not match");
        }
        // validate national id card
        if(userRepository.existsByNationalCardId(registerRequest.nationalCardId())){
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "National card already been used");
        }
        // validate term and policy
        if(!registerRequest.acceptTerm()){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "You must accept the term");
        }
        //step 2 map data
        User user =  userMapper.fromRegisterRequest(registerRequest);
        user.setUuid(UUID.randomUUID().toString());
        user.setPassword(passwordEncoder.encode(user.getPassword()));// encode password
        user.setProfileImage("profile/default-user.png");
        user.setIsBlocked(false);
        user.setIsDeleted(false);
        user.setIsVerified(false);
        //set to system
        Role roleUser = roleRepository.findRoleUser();// default rol
        // add role in list and set
        List<Role> roles = List.of(roleUser);
        user.setRoles(roles);
        //save to model
        userRepository.save(user);

        return  RegisterResponse.builder()
                .message("You have registered successfully, please verify your email")
                .email(user.getEmail())
                .build();
    }
//
//    @Override
//    public void sendVerification(String email) throws MessagingException {
//        // validate email
//        User user = userRepository.findByEmail(email)
//                .orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND,
//                        "User have been not found"));
//        //set to db
//        UserVerification userVerification = new UserVerification();
//        userVerification.setUser(user);
//        userVerification.setVerificationCode(RandomUtil.random6Digits());
//        userVerification.setExpiryTime(LocalTime.now().plusMinutes(1));
//
//        userVerificationRepository.save(userVerification);// sent to db
//        // prepare for send email
//        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
//        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage);
//        helper.setTo(email);
//        helper.setFrom(adminEmail);
//
//        //goal
//        helper.setSubject("Verification email brother");
//        helper.setText(userVerification.getVerificationCode());
//        // bos method
//        javaMailSender.send(mimeMessage);
//    }

    @Override
    public void verify(VerificationRequest verificationRequest) throws MessagingException {
        // validate email
        User user = userRepository.findByEmail(verificationRequest.email())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,"User have not been found"));
        // validate verified code
        UserVerification userVerification=userVerificationRepository.findByUserAndVerificationCode(user,verificationRequest.verifiedCode()).
                orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,"User verification code has not been found"));
        // Is verified code expired?
        if(LocalTime.now().isAfter(userVerification.getExpiryTime())){
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED,"Verification code has expired");
        }

        user.setIsVerified(true);
        userRepository.save(user);
        // update again
        userVerificationRepository.delete(userVerification);
    }

//    @Override
//    public void resendVerification(String email) throws MessagingException {
//        // validate email
//        User user = userRepository.findByEmail(email)
//                .orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND,
//                        "User have not been found"));
//        //set to db
//        UserVerification userVerification = userVerificationRepository.findByUser(user)
//                .orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND,
//                        "User has not been found"));
//        // set only two data
//        userVerification.setVerificationCode(RandomUtil.random6Digits());
//        userVerification.setExpiryTime(LocalTime.now().plusMinutes(1));
//
//        userVerificationRepository.save(userVerification);// sent to db
//        // prepare for send email
//        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
//        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage);
//        helper.setTo(email);
//        helper.setFrom(adminEmail);
//
//        //goal
//        helper.setSubject("Verification email brother");
//        helper.setText(userVerification.getVerificationCode());
//        // bos method
//        javaMailSender.send(mimeMessage);
//    }
    @Override
    public void sendVerification(String email) throws MessagingException {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "User not found"));

        String code = RandomUtil.random6Digits();

        UserVerification verification = new UserVerification();
        verification.setUser(user);
        verification.setVerificationCode(code);
        verification.setExpiryTime(LocalTime.now().plusMinutes(5));

        userVerificationRepository.save(verification);

        String htmlContent = buildOtpVerificationEmail(user, code);

        sendEmail(email, "Email Verification Code", htmlContent);
    }
    @Override
    public void resendVerification(String email) throws MessagingException {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "User not found"));

        UserVerification verification = userVerificationRepository.findByUser(user)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Verification not found"));

        String newCode = RandomUtil.random6Digits();

        verification.setVerificationCode(newCode);
        verification.setExpiryTime(LocalTime.now().plusMinutes(5));

        userVerificationRepository.save(verification);

        String htmlContent = buildOtpVerificationEmail(user, newCode);

        sendEmail(email, "Resend Verification Code", htmlContent);
    }
    private void sendEmail(String to, String subject, String htmlContent)
            throws MessagingException {

        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);

        helper.setTo(to);
        helper.setFrom(adminEmail);
        helper.setSubject(subject);
        helper.setText(htmlContent, true);

        javaMailSender.send(mimeMessage);
    }
    private String buildOtpVerificationEmail(User user, String code) {

        return """
        <div style="margin:0;padding:0;background-color:#f4f6f8;font-family:Arial,sans-serif;">
            <table width="100%%" cellpadding="0" cellspacing="0" style="padding:40px 0;">
                <tr>
                    <td align="center">

                        <table width="600" cellpadding="0" cellspacing="0"
                               style="background:#ffffff;border-radius:12px;
                               overflow:hidden;box-shadow:0 8px 20px rgba(0,0,0,0.05);">

                            <!-- Header -->
                            <tr>
                                <td style="background:#1e293b;padding:20px 30px;color:#ffffff;">
                                    <table width="100%%">
                                        <tr>
                                            <td style="font-size:18px;font-weight:bold;">
                                                Banking System
                                            </td>
                                            <td align="right" style="font-size:12px;color:#cbd5e1;">
                                                Security Verification
                                            </td>
                                        </tr>
                                    </table>
                                </td>
                            </tr>

                            <!-- Body -->
                            <tr>
                                <td style="padding:35px 30px;">

                                    <h2 style="margin:0 0 15px 0;color:#111827;">
                                        Verify Your Account
                                    </h2>

                                    <p style="color:#4b5563;font-size:14px;line-height:1.6;">
                                        Hello <strong>%s</strong>,
                                    </p>

                                    <p style="color:#4b5563;font-size:14px;line-height:1.6;">
                                        Use the verification code below to complete your authentication process.
                                    </p>

                                    <!-- OTP BOX -->
                                    <div style="margin:30px 0;text-align:center;">
                                        <span style="
                                            display:inline-block;
                                            padding:18px 35px;
                                            font-size:26px;
                                            font-weight:bold;
                                            letter-spacing:6px;
                                            background:#2563eb;
                                            color:#ffffff;
                                            border-radius:10px;">
                                            %s
                                        </span>
                                    </div>

                                    <!-- Info Box -->
                                    <div style="margin:20px 0;padding:15px;
                                                background:#eff6ff;
                                                border:1px solid #3b82f6;
                                                border-radius:8px;
                                                color:#1e3a8a;
                                                font-size:13px;">
                                        🔒 This verification code will expire in 5 minutes.
                                    </div>

                                    <p style="color:#6b7280;font-size:13px;line-height:1.6;margin-top:25px;">
                                        If you did not request this code, please ignore this email.
                                    </p>

                                    <hr style="margin:30px 0;border:none;border-top:1px solid #e5e7eb;" />

                                    <p style="font-size:13px;color:#374151;">
                                        Support:
                                        <a href="mailto:nindeveloper01@gmail.com"
                                           style="color:#2563eb;text-decoration:none;">
                                           nindeveloper01@gmail.com
                                        </a>
                                    </p>

                                </td>
                            </tr>

                            <!-- Footer -->
                            <tr>
                                <td style="background:#f9fafb;padding:20px;text-align:center;
                                           font-size:12px;color:#9ca3af;">
                                    © 2026 Banking System. This is an automated security message.
                                </td>
                            </tr>

                        </table>

                    </td>
                </tr>
            </table>
        </div>
        """.formatted(user.getName(), code);
    }

//    private String buildVerificationEmail(User user, String code) {
//
//        return """
//        <div style="font-family: Arial, sans-serif; background-color: #f4f6f8; padding: 40px;">
//            <div style="max-width: 500px; margin: auto; background: #ffffff;
//                        border-radius: 10px; padding: 30px;
//                        box-shadow: 0 4px 10px rgba(0,0,0,0.05);">
//
//                <h2 style="color: #333; text-align: center;">
//                    Email Verification
//                </h2>
//
//                <p style="color: #555; font-size: 14px;">
//                    Hello <strong>%s</strong>,
//                </p>
//
//                <p style="color: #555; font-size: 14px;">
//                    Use the verification code below to complete your authentication.
//                </p>
//
//                <div style="text-align: center; margin: 30px 0;">
//                    <span style="
//                        display: inline-block;
//                        background-color: #2563eb;
//                        color: #ffffff;
//                        padding: 15px 30px;
//                        font-size: 22px;
//                        font-weight: bold;
//                        letter-spacing: 4px;
//                        border-radius: 8px;">
//                        %s
//                    </span>
//                </div>
//
//                <p style="color: #999; font-size: 12px; text-align: center;">
//                    This code will expire in 5 minutes.
//                </p>
//
//            </div>
//        </div>
//        """.formatted(user.getName(), code);
//    }
}
