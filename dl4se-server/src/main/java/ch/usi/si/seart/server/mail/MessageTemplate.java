package ch.usi.si.seart.server.mail;

public enum MessageTemplate {

    PASSWORD, TASK, VERIFICATION;

    @Override
    public String toString() {
        return name().toLowerCase();
    }
}
