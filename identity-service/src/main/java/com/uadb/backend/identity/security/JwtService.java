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
        List<String> roles = utilisateur.getRoles()
                .stream()
                .map(Enum::name)
                .toList();

        return construireToken(utilisateur, roles, expiration, "ACCESS");
    }

    public String genererTokenPremiereConnexion(UtilisateurEntity utilisateur) {
        return construireToken(
                utilisateur,
                List.of("PREMIERE_CONNEXION"),
                900_000,
                "FIRST_LOGIN"
        );
    }

    private String construireToken(
            UtilisateurEntity utilisateur,
            List<String> roles,
            long duree,
            String type) {

        Instant maintenant = Instant.now();

        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer(issuer)
                .issuedAt(maintenant)
                .expiresAt(maintenant.plusMillis(duree))
                .subject(utilisateur.getEmail())
                .claim("userId", utilisateur.getId())
                .claim("roles", roles)
                .claim("type", type)
                .build();

        JwsHeader header = JwsHeader.with(MacAlgorithm.HS256).build();

        return jwtEncoder.encode(
                JwtEncoderParameters.from(header, claims)
        ).getTokenValue();
    }
}
