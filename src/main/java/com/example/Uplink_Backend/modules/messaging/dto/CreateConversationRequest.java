package com.example.Uplink_Backend.modules.messaging.dto;

import jakarta.validation.constraints.NotEmpty;
import java.util.List;
import java.util.UUID;

public class CreateConversationRequest {

    private String type = "DIRECT"; // DIRECT, GROUP

    @NotEmpty(message = "Participant IDs are required")
    private List<UUID> participantIds;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<UUID> getParticipantIds() {
        return participantIds;
    }

    public void setParticipantIds(List<UUID> participantIds) {
        this.participantIds = participantIds;
    }
}
