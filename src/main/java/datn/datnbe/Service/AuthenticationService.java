package datn.datnbe.Service;


import datn.datnbe.Entity.User;
import datn.datnbe.Exception.AppException;
import datn.datnbe.Exception.ErrorCode;
import datn.datnbe.Repository.UserRepository;
import datn.datnbe.dto.request.AuthenticationRequest;
import datn.datnbe.dto.request.IntrospectRequest;
import datn.datnbe.dto.response.AuthenticationRespone;
import datn.datnbe.dto.response.IntrospectRespone;
import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Optional;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
@Builder
public class AuthenticationService {
    UserRepository userRepository;
    private static final String SIGNER_KEY = "0aPglnnROU/zGjIuvAA32LpDzmqEY2O7J4fgQ4Eh+4KuJaSCXQIFQgBv6a69Pvkt";

    public AuthenticationRespone authenticate(AuthenticationRequest request){
        var user = userRepository.findByEmail(request.getEmail()).orElseThrow(()
                -> new AppException(ErrorCode.USER_NOTFOUND));
        BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder(10);
        boolean authenticated = bCryptPasswordEncoder.matches(request.getPassword(),user.getPassword());
        if(!authenticated){
            throw new AppException(ErrorCode.UNAUTHENTICATED);
        }

        var token = genToken(request.getEmail());
        AuthenticationRespone authenticationRespone = new AuthenticationRespone().builder()
                .token(token)
                .authenticated(authenticated)
                .build();
        return authenticationRespone;
    }

    public String genToken(String username){
        Optional<User> user =  userRepository.findByEmail(username);
        JWSHeader header = new JWSHeader(JWSAlgorithm.HS512);
        JWTClaimsSet jwtClaimsSet = new JWTClaimsSet.Builder()
                .subject(user.get().getEmail())
                .issuer("kedo")
                .issueTime(new Date())
                .expirationTime(new Date(Instant.now().plus(1, ChronoUnit.HOURS).toEpochMilli()))
                .claim("scope", user.get().getRole())
                .build();
        Payload payload = new Payload(jwtClaimsSet.toJSONObject());
        JWSObject jwsObject = new JWSObject(header, payload);


//        Ky token(Thuat toan ky, )

        try {
            jwsObject.sign(new MACSigner(SIGNER_KEY.getBytes()));
            return jwsObject.serialize();
        } catch (JOSEException e) {
            throw new RuntimeException(e);
        }
    }
    public IntrospectRespone introspect(IntrospectRequest request) throws ParseException, JOSEException {
        var token = request.getToken();
        JWSVerifier verifier = new MACVerifier(SIGNER_KEY.getBytes());
        SignedJWT signJWT = SignedJWT.parse(token);
        boolean verified = signJWT.verify(verifier);
        Date expityTime = signJWT.getJWTClaimsSet().getExpirationTime();

        return IntrospectRespone.builder()
                .authenticated(verified && expityTime.after(new Date()))
                .build();
    }
}