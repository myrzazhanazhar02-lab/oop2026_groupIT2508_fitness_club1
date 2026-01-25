package com.fitnessclub;
import com.fitnessclub.db.GymConfig;
import com.fitnessclub.db.SchemaInitializer;
import com.fitnessclub.exception.InvalidInputException;
import com.fitnessclub.model.ClassBooking;
import com.fitnessclub.model.FitnessClass;
import com.fitnessclub.model.Member;
import com.fitnessclub.model.MembershipType;
import com.fitnessclub.repository.ClassBookingRepository;
import com.fitnessclub.repository.FitnessClassRepository;
import com.fitnessclub.repository.MemberRepository;
import com.fitnessclub.repository.MembershipTypeRepository;
import com.fitnessclub.repository.jdbc.JdbcClassBookingRepository;
import com.fitnessclub.repository.jdbc.JdbcFitnessClassRepository;
import com.fitnessclub.repository.jdbc.JdbcMemberRepository;
import com.fitnessclub.repository.jdbc.JdbcMembershipTypeRepository;
import com.fitnessclub.service.BookingService;
import com.fitnessclub.service.ConsoleNotificationService;
import com.fitnessclub.service.MembershipService;
import com.fitnessclub.service.NotificationService;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Scanner;

public class Main {
    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter DATE_TIME_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    public static void main(String[] args) {
        var database = GymConfig.INSTANCE.database();
        new SchemaInitializer(database).ensureSchema();
        new SchemaInitializer(database).seedDefaults();

        NotificationService notificationService = new ConsoleNotificationService();
        MemberRepository memberRepository = new JdbcMemberRepository(database);
        MembershipTypeRepository membershipTypeRepository = new JdbcMembershipTypeRepository(database);
        FitnessClassRepository fitnessClassRepository = new JdbcFitnessClassRepository(database);
        ClassBookingRepository classBookingRepository = new JdbcClassBookingRepository(database);

        MembershipService membershipService = new MembershipService(memberRepository, membershipTypeRepository, notificationService);
        BookingService bookingService = new BookingService(classBookingRepository, memberRepository, fitnessClassRepository, notificationService);

        Scanner scanner = new Scanner(System.in);
        boolean running = true;
        System.out.println("=== Fitness Club CLI ===");
        while (running) {
            printMenu();
            String choice = scanner.nextLine();
            try {
                switch (choice) {
                    case "1" -> listMembershipTypes(membershipService);
                    case "2" -> createMember(scanner, membershipService);
                    case "3" -> listMembers(membershipService);
                    case "4" -> buyMembership(scanner, membershipService);
                    case "5" -> extendMembership(scanner, membershipService);
                    case "6" -> listClasses(fitnessClassRepository);
                    case "7" -> bookClass(scanner, bookingService);
                    case "8" -> listBookings(scanner, bookingService);
                    case "0" -> running = false;
                    default -> System.out.println("Unknown option");
                }
            } catch (Exception ex) {
                System.out.println("Error: " + ex.getMessage());
                if (ex.getCause() != null) {
                    System.out.println("Cause: " + ex.getCause());
                }
                ex.printStackTrace(System.out);
            }
        }
        System.out.println("Bye!");
    }

    private static void printMenu() {
        System.out.println();
        System.out.println("1) List membership types");
        System.out.println("2) Create member");
        System.out.println("3) List members");
        System.out.println("4) Buy membership");
        System.out.println("5) Extend membership");
        System.out.println("6) List classes");
        System.out.println("7) Book class");
        System.out.println("8) View member bookings (attendance)");
        System.out.println("0) Exit");
        System.out.print("Choose option: ");
    }

    private static void listMembershipTypes(MembershipService service) {
        List<MembershipType> types = service.listMembershipTypes();
        if (types.isEmpty()) {
            System.out.println("No membership types configured.");
            return;
        }
        types.forEach(t -> System.out.printf("%d) %s | %d days | $%.2f | visit limit: %s%n",
                t.getId(), t.getName(), t.getDurationDays(), t.getPrice(),
                t.getVisitLimit() == null ? "unlimited" : t.getVisitLimit()));
    }

    private static void createMember(Scanner scanner, MembershipService service) {
        System.out.print("Name: ");
        String name = scanner.nextLine();
        System.out.print("Email: ");
        String email = scanner.nextLine();
        System.out.print("Phone (optional): ");
        String phone = scanner.nextLine();
        System.out.print("Membership type id (blank for none): ");
        String typeInput = scanner.nextLine();
        Integer typeId = typeInput.isBlank() ? null : parsePositiveInt(typeInput);

        Member member = service.createMember(name, email, phone, typeId);
        System.out.println("Member created with id " + member.getId());
    }

    private static void listMembers(MembershipService service) {
        List<Member> members = service.listMembers();
        if (members.isEmpty()) {
            System.out.println("No members yet.");
            return;
        }
        members.forEach(m -> System.out.printf(
                "%d) %s | email: %s | membership type: %s | ends: %s%n",
                m.getId(), m.getName(), m.getEmail(),
                m.getMembershipTypeId() == null ? "-" : m.getMembershipTypeId(),
                m.getMembershipEndDate() == null ? "-" : m.getMembershipEndDate().format(DATE_FMT)
        ));
    }

    private static void buyMembership(Scanner scanner, MembershipService service) {
        System.out.print("Member id: ");
        int memberId = parsePositiveInt(scanner.nextLine());
        System.out.print("Membership type id: ");
        int typeId = parsePositiveInt(scanner.nextLine());
        Member updated = service.buyMembership(memberId, typeId);
        System.out.println("Membership active until " + updated.getMembershipEndDate().format(DATE_FMT));
    }

    private static void extendMembership(Scanner scanner, MembershipService service) {
        System.out.print("Member id: ");
        int memberId = parsePositiveInt(scanner.nextLine());
        System.out.print("Membership type id: ");
        int typeId = parsePositiveInt(scanner.nextLine());
        Member updated = service.extendMembership(memberId, typeId);
        System.out.println("Membership now active until " + updated.getMembershipEndDate().format(DATE_FMT));
    }

    private static void listClasses(FitnessClassRepository repository) {
        List<FitnessClass> classes = repository.findAll();
        if (classes.isEmpty()) {
            System.out.println("No classes configured.");
            return;
        }
        classes.forEach(c -> System.out.printf(
                "%d) %s | capacity: %d | start: %s%n",
                c.getId(), c.getName(), c.getCapacity(), c.getStartTime().format(DATE_TIME_FMT)
        ));
    }

    private static void bookClass(Scanner scanner, BookingService service) {
        System.out.print("Member id: ");
        int memberId = parsePositiveInt(scanner.nextLine());
        System.out.print("Class id: ");
        int classId = parsePositiveInt(scanner.nextLine());
        ClassBooking booking = service.book(memberId, classId);
        System.out.println("Booked with id " + booking.getId());
    }

    private static void listBookings(Scanner scanner, BookingService service) {
        System.out.print("Member id: ");
        int memberId = parsePositiveInt(scanner.nextLine());
        List<ClassBooking> bookings = service.listBookingsForMember(memberId);
        if (bookings.isEmpty()) {
            System.out.println("No bookings for this member.");
            return;
        }
        bookings.forEach(b -> System.out.printf("Booking %d | class id: %d | at: %s%n",
                b.getId(), b.getClassId(), b.getBookedAt().format(DATE_TIME_FMT)));
    }

    private static int parsePositiveInt(String value) {
        try {
            int parsed = Integer.parseInt(value.trim());
            if (parsed <= 0) {
                throw new InvalidInputException("Value must be positive");
            }
            return parsed;
        } catch (NumberFormatException ex) {
            throw new InvalidInputException("Invalid number: " + value);
        }
    }
}

