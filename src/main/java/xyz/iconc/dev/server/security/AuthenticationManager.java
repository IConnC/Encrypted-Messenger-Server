package xyz.iconc.dev.server.security;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.env.BasicIniEnvironment;
import org.apache.shiro.env.Environment;

public class AuthenticationManager {
    private static String INI_PATH = "classpath:shiro.ini";

    public AuthenticationManager() {
        loadNewSecurityManager();
    }

    private void loadNewSecurityManager() {
        Environment env = new BasicIniEnvironment(INI_PATH);

        SecurityUtils.setSecurityManager(env.getSecurityManager());
    }

    


    public static void main(String[] args) {
        AuthenticationManager authenticationManager = new AuthenticationManager();
    }

}
