package ch.usi.si.seart.server.mail;

public enum MessageTemplate {

    PASSWORD_RESET,
    TASK_NOTIFICATION,
    VERIFICATION;

    @Override
    public String toString() {
        return name().toLowerCase();
    }
}
