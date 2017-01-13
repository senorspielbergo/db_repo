package kaphira.wahlinfo.security;

import java.io.Serializable;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.ActionEvent;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.faces.bean.ApplicationScoped;
import javax.faces.bean.ManagedBean;

/**
 *
 * @author theralph
 */
@ManagedBean
@ApplicationScoped
public class TokenBean implements Serializable {

    private final Logger logger = Logger.getLogger(TokenBean.class.getName());
    private static final SecureRandom RANDOM = new SecureRandom();
    private static final long THRESHOLD = 600000; //10min in milliseconds
    
    private final Object semaphor = new Object();
    private Thread cleanerThread;
    private List<Token> currentTokens; 
    
    
    @PostConstruct
    private void init() {
        currentTokens = new ArrayList<>();
        cleanerThread = createCleanerThread();
        cleanerThread.start();
    }
    
    @PreDestroy
    private void onClose() {
        cleanerThread.interrupt();
    }
    
    public String generateToken(int districtId) {
        synchronized(semaphor) {
            Token token = newToken(districtId);
            currentTokens.add(token);
            return token.getValue();
        }
    }
    
    public int decodeDistrictId(String tokenValue) {
        return Integer.parseInt(tokenValue.substring(5, 8), 16);
    }
    
    /**
     * Konvertiert eine Wahlkreis - ID in einen dreistelligen HEX-Code
     * @param districtId
     * @return 
     */
    private String encodeDistrictId(int districtId) {
        String hex = Integer.toHexString(districtId);
        String formattedHex = ("000" + hex).substring(hex.length());
        
        return formattedHex;
    }
    
    
    
    
    public boolean authenticate(String tokenValue) {
        for (Token token : currentTokens) {
            if (token.getValue().equals(tokenValue)) {
                if (token.isValid()) {
                    token.invalidate();
                    return true;
                }
                return false;
            }
        }
        return false;
    }


    private void removeInvalidTokens() {
        synchronized(semaphor){
            List<Token> tokensToBeDeleted = new ArrayList<>();
            
            //Collecting all outdated tokens
            for (Token token : currentTokens) {
                if (!token.isValid() || isOutdated(token)) {
                    tokensToBeDeleted.add(token);
                }
            }
            
            //Deleting them
            for (Token token : tokensToBeDeleted) {
                currentTokens.remove(token);
            }
        }
    }
    
    private boolean isOutdated(Token token) {
        long age = System.currentTimeMillis() - token.getAge();
        if (age > THRESHOLD) {
            return true;
        }
        return false;
    }
    
    private Token newToken(int districtId) {
        
        String districtHex = encodeDistrictId(districtId);
        
        long random = RANDOM.nextLong() * System.nanoTime() * 37;
   
        String hexString = Long.toHexString(random).substring(0, 10);
               hexString = hexString.substring(0, 5) + districtHex + hexString.substring(5);
               hexString = hexString.toUpperCase();
               
        Token token = new Token(hexString);
        
        return token;
    }

    private Thread createCleanerThread() {
        Thread cleaner = new Thread(new Runnable() {
            @Override
            public void run() {
                logger.log(Level.INFO, "THREAD STARTED");
                while (!Thread.interrupted()) {
                    try {
                        Thread.sleep(THRESHOLD);
                        removeInvalidTokens();
                        
                    } catch (Exception e) {
                        logger.log(Level.SEVERE, "Unable to remove invalid tokens");
                    }
                }
            }
        });
        
        return cleaner;
    }

    

}
