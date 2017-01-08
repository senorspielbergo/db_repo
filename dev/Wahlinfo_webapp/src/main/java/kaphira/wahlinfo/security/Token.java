package kaphira.wahlinfo.security;

/**
 *
 * @author theralph
 */
public class Token {
    
    private String value;
    private long age;
    private boolean isValid;

    public Token(String value) {
        this.value = value;
        this.isValid = true;
        this.age = System.currentTimeMillis();
    }

    public String getValue() {
        return value;
    }

    public long getAge() {
        return age;
    }

    public boolean isValid() {
        return isValid;
    }
    
    public void invalidate() {
        this.isValid = false;
    }
    
    
    
}
