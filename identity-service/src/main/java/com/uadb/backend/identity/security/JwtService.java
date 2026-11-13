package com.uadb.backend.identity.security;

import com.uadb.backend.identity.entity.UtilisateurEntity;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
public class JwtService {

    private final JwtEncoder jwtEncoder;

    @Value("${app.jwt.expiration}")
    private long expiration;

    @Value("${app.jwt.issuer}")
    private String issuer;

    public JwtService(JwtEncoder jwtEncoder) {
        this.jwtEncoder = jwtEncoder;
    }

    public String genererToken(UtilisateurEntity utilisateur) {
        return construireToken(utilisateur, expiration);
    }

    private String construireToken(
            UtilisateurEntity utilisateur,
            long duree) {

        Instant maintenant = Instant.now();
        List<String> roles = utilisateur.getRoles()
                .stream()
                .map(Enum::name)
                .toList();

        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer(issuer)
                .issuedAt(maintenant)
                .expiresAt(maintenant.plusMillis(duree))
                .subject(utilisateur.getEmail())
                .claim("userId", utilisateur.getId())
                .claim("roles", roles)
                .build();

        JwsHeader header = JwsHeader.with(MacAlgorithm.HS256).build();

        return jwtEncoder.encode(
                JwtEncoderParameters.from(header, claims)
        ).getTokenValue();
    }
}
