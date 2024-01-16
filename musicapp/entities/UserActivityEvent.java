package com.example.musicapp.entities;

public class UserActivityEvent {

    private String eventDescription, eventType;
    private User eventCreator;

    public UserActivityEvent() {
    }

    public String getEventDescription() {
        return eventDescription;
    }

    public void setEventDescription(String eventDescription) {
        this.eventDescription = eventDescription;
    }

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public User getEventCreator() {
        return eventCreator;
    }

    public void setEventCreator(User eventCreator) {
        this.eventCreator = eventCreator;
    }

    @Override
    public String toString() {
        return "UserActivityEvent{" +
                "eventDescription='" + eventDescription + '\'' +
                ", eventType='" + eventType + '\'' +
                ", eventCreator=" + eventCreator +
                '}';
    }
}
