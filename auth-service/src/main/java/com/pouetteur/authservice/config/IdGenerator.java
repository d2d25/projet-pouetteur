package com.pouetteur.authservice.config;

import com.pouetteur.authservice.model.Community;
import com.pouetteur.authservice.model.User;
import org.hibernate.HibernateException;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.id.IdentifierGenerator;

import java.util.UUID;

@GenericGenerator(name = "IdGenerator", strategy = "com.pouetteur.authservice.config.IdGenerator")
public class IdGenerator implements IdentifierGenerator {
    @Override
    public String generate(SharedSessionContractImplementor sharedSessionContractImplementor, Object o) throws HibernateException {
        //Pour les User c'est "m*" + UUID et pour les Community c'est "c*" + UUID sinon erreur
        if (o instanceof User) {
            return "m*" + UUID.randomUUID();
        } else if (o instanceof Community) {
            return "c*" + UUID.randomUUID();
        } else {
            throw new HibernateException("Object is not a User or a Community but : " + o.getClass().getName());
        }

    }
}
