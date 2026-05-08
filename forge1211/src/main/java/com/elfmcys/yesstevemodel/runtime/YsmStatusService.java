package com.elfmcys.yesstevemodel.runtime;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public final class YsmStatusService {
    private final List<String> recentMessages = new ArrayList<>();
    private String currentStatus = "YSM client runtime ready";

    public synchronized void setStatus(String currentStatus) {
        this.currentStatus = Objects.requireNonNullElse(currentStatus, this.currentStatus);
        addRecent(this.currentStatus);
    }

    public synchronized void pushMessage(String message) {
        addRecent(Objects.requireNonNullElse(message, ""));
    }

    public synchronized String getCurrentStatus() {
        return this.currentStatus;
    }

    public synchronized List<String> recentMessages() {
        return List.copyOf(this.recentMessages);
    }

    private void addRecent(String message) {
        this.recentMessages.add(message);
        while (this.recentMessages.size() > 6) {
            this.recentMessages.remove(0);
        }
    }
}
