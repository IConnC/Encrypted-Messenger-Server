package xyz.iconc.dev.server.security;

import org.apache.shiro.authc.*;
import org.apache.shiro.authc.credential.CredentialsMatcher;
import org.apache.shiro.authc.credential.HashedCredentialsMatcher;
import org.apache.shiro.config.ConfigurationException;
import org.apache.shiro.realm.Realm;
import org.apache.shiro.util.Nameable;

import java.util.logging.Logger;

public class DatabaseRealm implements Realm, Nameable {

    String name;
    HashedCredentialsMatcher credentialsMatcher;





    public CredentialsMatcher getCredentialsMatcher() {
        return credentialsMatcher;
    }

    private void setCredentialsMatcher(CredentialsMatcher credentialsMatcher) {
        if (!(credentialsMatcher instanceof HashedCredentialsMatcher))
            throw new ConfigurationException("Instance HashedCredentialsMatcher required");

        this.credentialsMatcher = (HashedCredentialsMatcher) credentialsMatcher;
    }



    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public void setName(String s) {
        this.name = s;
    }

    @Override
    public boolean supports(AuthenticationToken token) {
        return false ;
    }

    @Override
    public AuthenticationInfo getAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {
        return new SimpleAuthenticationInfo(token.getPrincipal(), token.getCredentials(), getName());
    }


}
