package com.endava.cats.args;

import com.endava.cats.model.CatsRequest;
import com.endava.cats.util.CatsUtil;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

/**
 * Holds all args related to Authentication details.
 */
@Component
@Getter
public class AuthArguments {
    private final List<CatsArg> args = new ArrayList<>();
    private final String sslKeystoreHelp = "Location of the keystore holding certificates used when authenticating calls using one-way or two-way SSL";
    private final String sslKeystorePwdHelp = "The password of the sslKeystore";
    private final String sslKeyPwdHelp = "The password of the private key from the sslKeystore";
    private final String basicAuthHelp = "Supplies a `username:password` pair, in case the service uses basic auth";
    private final String proxyHostHelp = "HOST the proxy server's host name";
    private final String proxyPortHelp = "PORT the proxy server's port number";

    @Value("${sslKeystore:empty}")
    private String sslKeystore;
    @Value("${sslKeystorePwd:empty}")
    private String sslKeystorePwd;
    @Value("${sslKeyPwd:empty}")
    private String sslKeyPwd;
    @Value("${basicauth:empty}")
    private String basicAuth;
    @Value("${proxyHost:empty}")
    private String proxyHost;
    @Value("${proxyPort:0}")
    private int proxyPort;

    @PostConstruct
    public void init() {
        args.add(CatsArg.builder().name("proxyPort").value(String.valueOf(proxyPort)).help(proxyPortHelp).build());
        args.add(CatsArg.builder().name("proxyHost").value(proxyHost).help(proxyHostHelp).build());
        args.add(CatsArg.builder().name("sslKeystore").value(sslKeystore).help(sslKeystoreHelp).build());
        args.add(CatsArg.builder().name("sslKeystorePwd").value(sslKeystorePwd).help(sslKeystorePwdHelp).build());
        args.add(CatsArg.builder().name("sslKeyPwd").value(sslKeyPwd).help(sslKeyPwdHelp).build());
        args.add(CatsArg.builder().name("basicauth").value(basicAuth).help(basicAuthHelp).build());
    }

    public boolean isBasicAuthSupplied() {
        return CatsUtil.isArgumentValid(basicAuth);
    }

    public boolean isMutualTls() {
        return CatsUtil.isArgumentValid(sslKeystore);
    }

    public boolean isProxySupplied() {
        return CatsUtil.isArgumentValid(proxyHost);
    }

    /**
     * Returns the Proxy if set or NO_PROXY otherwise.
     *
     * @return the Proxy settings supplied through args
     */
    public Proxy getProxy() {
        Proxy proxy = Proxy.NO_PROXY;
        if (isProxySupplied()) {
            proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(proxyHost, proxyPort));
        }
        return proxy;
    }

    /**
     * Returns the basic auth header if supplied. This method does not do any checks. It assumes the calling method already performed
     * the {@link #isBasicAuthSupplied()} check.
     *
     * @return the basic auth Header
     */
    public CatsRequest.Header getBasicAuthHeader() {
        byte[] encodedAuth = Base64.getEncoder().encode(this.basicAuth.getBytes(StandardCharsets.UTF_8));
        String authHeader = "Basic " + new String(encodedAuth, StandardCharsets.UTF_8);
        return new CatsRequest.Header("Authorization", authHeader);
    }
}
