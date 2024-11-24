package com.tms;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

public class TMS {

    private static ArrayList<Ticket> ticketList = new ArrayList<>();
    private static Random random = new Random();

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        int choice;
        System.out.println("Welcome to TMS!");
        do {
            System.out.println("1. Book Ticket");
            System.out.println("2. List Tickets");
            System.out.println("3. Cancel Ticket");
            System.out.println("4. View Ticket");
            System.out.println("5. Exit");
            System.out.print("Enter your choice: ");
            choice = scanner.nextInt();
            switch (choice) {
                case 1:
                    bookTicket1(scanner);
                    break;
                case 2:
                    listTickets1();
                    break;
                case 3:
                    cancelTicket1(scanner);
                    break;
                case 4:
                    viewTicket1(scanner);
                    break;
                case 5:
                    System.out.println("Exiting... Thank you!");
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        } while (choice != 0);
        scanner.close();
    }

    private static void bookTicket(Scanner scanner) {

        System.out.print("Enter the number of passengers: ");
        int numPassengers = scanner.nextInt();//2
        scanner.nextLine();

        System.out.print("Boarding Point: ");
        String boardingPoint = scanner.nextLine();

        System.out.print("Destination Point: ");
        String destinationPoint = scanner.nextLine();

        int ticketNumber = random.nextInt(10000) + 1;
        List<PassengerDetails> passengers = new ArrayList<>();

        for (int i = 1; i <= numPassengers; i++) {

            PassengerDetails passenger = new PassengerDetails();
            System.out.println("Enter details for passenger " + (i) + ":");

            System.out.print("Name: ");
            String name = scanner.nextLine();
            passenger.setPassengerName(name);
            System.out.print("Age: ");
            int age = scanner.nextInt();
            passenger.setPassengerAge(age);
            scanner.nextLine();
            passengers.add(passenger);
        }

        Ticket ticket = new Ticket(ticketNumber, boardingPoint, destinationPoint);
        ticket.setPassengers(passengers);
        ticketList.add(ticket);
        System.out.println("Thanks for booking! Your PNR is: " + ticketNumber);
    }

    private static void bookTicket1(Scanner scanner) {
        System.out.print("Enter the number of passengers: ");
        int numPassengers = scanner.nextInt();
        scanner.nextLine();

        System.out.print("Boarding Point: ");
        String boardingPoint = scanner.nextLine();

        System.out.print("Destination Point: ");
        String destinationPoint = scanner.nextLine();

        int ticketNumber = generateTicketNumber();

        List<PassengerDetails> passengers = new ArrayList<>();
        for (int i = 1; i <= numPassengers; i++) {
            PassengerDetails passenger = new PassengerDetails();
            System.out.println("Enter details for passenger " + i + ":");
            System.out.print("Name: ");
            String name = scanner.nextLine();
            passenger.setPassengerName(name);

            System.out.print("Age: ");
            int age = scanner.nextInt();
            scanner.nextLine();
            passenger.setPassengerAge(age);

            passengers.add(passenger);
        }

        try {
            Connection conn = DbConfigure.getConnection();
            String insertQuery = "INSERT INTO tickets (ticket_number, boarding_point, destination_point) VALUES (?, ?, ?)";
            PreparedStatement ticketStmt = conn.prepareStatement(insertQuery);
            ticketStmt.setInt(1, ticketNumber);
            ticketStmt.setString(2, boardingPoint);
            ticketStmt.setString(3, destinationPoint);
            ticketStmt.executeUpdate();

            String insertPassengerQuery = "INSERT INTO passengers (ticket_number, passenger_name, passenger_age) VALUES (?, ?, ?)";
            PreparedStatement passengerStmt = conn.prepareStatement(insertPassengerQuery);

            for (PassengerDetails passenger : passengers) {
                passengerStmt.setInt(1, ticketNumber);
                passengerStmt.setString(2, passenger.getPassengerName());
                passengerStmt.setInt(3, passenger.getPassengerAge());
                passengerStmt.executeUpdate();
            }

            System.out.println("Data saved successfully to the database.");
            System.out.println("Thanks for booking! Your PNR is: " + ticketNumber);

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    private static int generateTicketNumber() {
        Random random = new Random();
        return random.nextInt(10000) + 1;
    }

    public static void listTickets() {
        if (ticketList.isEmpty()) {
            System.out.println("No tickets booked yet.");
        } else {
            System.out.println("List of booked tickets:");
            for (Ticket ticket : ticketList) {
                System.out.println(ticket);
            }
        }

    }

    public static void listTickets1() {

        try {

            Connection con = DbConfigure.getConnection();
            String selectQuary = "SELECT * FROM tickets";
            PreparedStatement st = con.prepareStatement(selectQuary);
            ResultSet resultSet = st.executeQuery();
            while (resultSet.next()) {
                int ticketNumber = resultSet.getInt("ticket_number");
                String boardingPoint = resultSet.getString("boarding_point");
                String destinationPoint = resultSet.getString("destination_point");
                System.out.printf(ticketNumber + " " + boardingPoint + " " + destinationPoint);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public static void cancelTicket(Scanner scanner) {
        if (ticketList.isEmpty()) {
            System.out.println("No tickets booked yet.");
        }

        System.out.print("Enter PNR to cancel ticket: ");
        int pnrToCancel = scanner.nextInt();
        boolean found = false;
        for (Ticket ticket : ticketList) {
            if (ticket.ticketNumber == pnrToCancel) {
                ticketList.remove(ticket);
                System.out.println("Ticket with PNR " + pnrToCancel + " canceled successfully.");
                found = true;
                break;
            }
        }
        if (!found) {
            System.out.println("Ticket with PNR " + pnrToCancel + " not found.");
        }
    }

    private static void cancelTicket1(Scanner scanner) {
        System.out.print("Enter ticket number to cancel: ");
        int ticketNumberToCancel = scanner.nextInt();
        scanner.nextLine();

        try ( Connection conn = DbConfigure.getConnection()) {
            if (isTicketExists(conn, ticketNumberToCancel)) {
                String deleteTicketQuery = "DELETE FROM tickets WHERE ticket_number = ?";
                PreparedStatement deleteTicketStmt = conn.prepareStatement(deleteTicketQuery);
                deleteTicketStmt.setInt(1, ticketNumberToCancel);
                int rowsAffected = deleteTicketStmt.executeUpdate();

                if (rowsAffected > 0) {
                    System.out.println("Ticket with ticket number " + ticketNumberToCancel + " has been cancelled.");
                    String deletePassengersQuery = "DELETE FROM passengers WHERE ticket_number = ?";
                    PreparedStatement deletePassengersStmt = conn.prepareStatement(deletePassengersQuery);
                    deletePassengersStmt.setInt(1, ticketNumberToCancel);
                    deletePassengersStmt.executeUpdate();
                } else {
                    System.out.println("Ticket with ticket number " + ticketNumberToCancel + " not found. No changes made.");
                }
            }
            else {
                System.out.println("Ticket with ticket number " + ticketNumberToCancel + " does not exist.");
            }

        } catch (SQLException e) {
            System.out.println("Error cancelling ticket: " + e.getMessage());
        }
    }

    private static boolean isTicketExists(Connection conn, int ticketNumber) throws SQLException {
        String checkTicketQuery = "SELECT COUNT(*) AS count FROM tickets WHERE ticket_number = ?";
        PreparedStatement checkTicketStmt = conn.prepareStatement(checkTicketQuery);
        checkTicketStmt.setInt(1, ticketNumber);
        ResultSet resultSet = checkTicketStmt.executeQuery();

        if (resultSet.next()) {
            int count = resultSet.getInt("count");
            return count > 0;
        }

        return false;
    }

    private static void viewTicket(Scanner scanner) {
        System.out.print("Enter the ticket number to view: ");
        int ticketNumber = scanner.nextInt();

        for (Ticket ticket : ticketList) {//arraylist objects
            if (ticket.ticketNumber == ticketNumber) {
                System.out.println("Ticket Details:");
                System.out.println("Ticket Number: " + ticket.ticketNumber);
                System.out.println("Boarding Point: " + ticket.boardingPoint);
                System.out.println("Destination Point: " + ticket.destinationPoint);
                int count = 1;
                for (PassengerDetails passenger : ticket.getPassengers()) {
                    System.out.println("Passenger " + (count) + " Name: " + passenger.passengerName);
                    System.out.println("Passenger " + (count) + " Age: " + passenger.passengerAge);
                    count++;
                }
                return;
            }
        }

        System.out.println("Ticket number not found...");
    }

    private static void viewTicket1(Scanner scanner) {
        System.out.print("Enter ticket number to view details: ");
        int ticketNumberToView = scanner.nextInt();
        scanner.nextLine();

        try {
            Connection conn = DbConfigure.getConnection();
            String selectQuery = "SELECT * FROM tickets WHERE ticket_number = ?";
            PreparedStatement selectStmt = conn.prepareStatement(selectQuery);
            selectStmt.setInt(1, ticketNumberToView);
            ResultSet resultSet = selectStmt.executeQuery();

            if (resultSet.next()) {
                String boardingPoint = resultSet.getString("boarding_point");
                String destinationPoint = resultSet.getString("destination_point");

                System.out.println("Ticket Details for Ticket Number " + ticketNumberToView );
                System.out.println("Boarding Point    : " + boardingPoint);
                System.out.println("Destination Point : " + destinationPoint);
                displayPassengers(conn, ticketNumberToView);
            } else {
                System.out.println("Ticket with ticket number " + ticketNumberToView + " does not exist.");
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    private static void displayPassengers(Connection conn, int ticketNumber) throws SQLException {
        String selectPassengersQuery = "SELECT * FROM passengers WHERE ticket_number = ?";
        PreparedStatement selectPassengersStmt = conn.prepareStatement(selectPassengersQuery);
        selectPassengersStmt.setInt(1, ticketNumber);
        ResultSet passengersResultSet = selectPassengersStmt.executeQuery();

        while (passengersResultSet.next()) {
            String passengerName = passengersResultSet.getString("passenger_name");
            int passengerAge = passengersResultSet.getInt("passenger_age");
            System.out.println("PassengerName   :"+ "  "+passengerName);
            System.out.println( "PassengerAge   :"+"  "+passengerAge);
        }
    }

}
