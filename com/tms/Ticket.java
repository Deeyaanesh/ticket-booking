package com.tms;

import java.util.List;

class Ticket {

    int ticketNumber;
    String boardingPoint;
    String destinationPoint;
    List<PassengerDetails> passengers;

    public List<PassengerDetails> getPassengers() {
        return passengers;
    }

    public void setPassengers(List<PassengerDetails> passengers) {
        this.passengers = passengers;
    }

    public int getTicketNumber() {
        return ticketNumber;
    }

    public void setTicketNumber(int ticketNumber) {
        this.ticketNumber = ticketNumber;
    }

    public String getBoardingPoint() {
        return boardingPoint;
    }

    public void setBoardingPoint(String boardingPoint) {
        this.boardingPoint = boardingPoint;
    }

    public String getDestinationPoint() {
        return destinationPoint;
    }

    public void setDestinationPoint(String destinationPoint) {
        this.destinationPoint = destinationPoint;
    }

    public Ticket(int ticketNumber, String boardingPoint, String destinationPoint) {
        this.ticketNumber = ticketNumber;
        this.boardingPoint = boardingPoint;
        this.destinationPoint = destinationPoint;
    }
}


