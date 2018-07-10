package com.feiliks.common;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import org.springframework.security.jwt.crypto.sign.RsaSigner;
import org.springframework.security.jwt.crypto.sign.RsaVerifier;
import org.springframework.security.jwt.crypto.sign.SignatureVerifier;
import org.springframework.security.jwt.crypto.sign.Signer;

public class KeyPairProvider {

    private Signer signer;
    private SignatureVerifier verifier;
    private String sshPrivateKeyFile = null;
    private String sshPublicKeyFile = null;

    private String readKeyFile(String file) throws FileNotFoundException, IOException {
        StringBuilder sb = new StringBuilder();
        BufferedReader br = new BufferedReader(new InputStreamReader(
                new FileInputStream(file)));
        String line = null;
        do {
            if (line != null) {
                sb.append('\n');
            }
            line = br.readLine();
            if (line != null) {
                sb.append(line);
            }
        } while (line != null);
        return sb.toString();
    }

    public void setSshPrivateKey(String file) {
        sshPrivateKeyFile = file;
    }

    public String getSshPrivateKey() {
        return sshPrivateKeyFile;
    }

    public void setSshPublicKey(String file) {
        sshPublicKeyFile = file;
    }

    public String getSshPublicKey() {
        return sshPublicKeyFile;
    }

    public Signer getSigner() throws IOException {
        if (signer == null) {
            signer = new RsaSigner(readKeyFile(sshPrivateKeyFile));
        }
        return signer;
    }

    public SignatureVerifier getVerifier() throws IOException {
        if (verifier == null) {
            verifier = new RsaVerifier(readKeyFile(sshPublicKeyFile));
        }
        return verifier;
    }
}
