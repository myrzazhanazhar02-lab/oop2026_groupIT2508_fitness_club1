package com.fitnessclub.service;

import com.fitnessclub.exception.BookingAlreadyExistsException;
import com.fitnessclub.exception.ClassFullException;
import com.fitnessclub.exception.MembershipExpiredException;
import com.fitnessclub.exception.NotFoundException;
import com.fitnessclub.model.ClassBooking;
import com.fitnessclub.model.FitnessClass;
import com.fitnessclub.model.Member;
import com.fitnessclub.repository.ClassBookingRepository;
import com.fitnessclub.repository.FitnessClassRepository;
import com.fitnessclub.repository.MemberRepository;

import java.time.LocalDate;
import java.util.List;

public class BookingService {
    private final ClassBookingRepository bookingRepository;
    private final MemberRepository memberRepository;
    private final FitnessClassRepository fitnessClassRepository;
    private final NotificationService notificationService;

    public BookingService(ClassBookingRepository bookingRepository,
                          MemberRepository memberRepository,
                          FitnessClassRepository fitnessClassRepository,
                          NotificationService notificationService) {
        this.bookingRepository = bookingRepository;
        this.memberRepository = memberRepository;
        this.fitnessClassRepository = fitnessClassRepository;
        this.notificationService = notificationService;
    }

    public ClassBooking book(int memberId, int classId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new NotFoundException("Member not found: " + memberId));
        FitnessClass fitnessClass = fitnessClassRepository.findById(classId)
                .orElseThrow(() -> new NotFoundException("Class not found: " + classId));

        if (member.getMembershipEndDate() == null || member.getMembershipEndDate().isBefore(LocalDate.now())) {
            throw new MembershipExpiredException("Membership expired for member " + member.getName());
        }

        if (bookingRepository.existsByMemberAndClass(memberId, classId)) {
            throw new BookingAlreadyExistsException("Booking already exists for this class");
        }

        int current = bookingRepository.countBookingsForClass(classId);
        if (current >= fitnessClass.getCapacity()) {
            throw new ClassFullException("Class is full: " + fitnessClass.getName());
        }

        ClassBooking booking = bookingRepository.create(memberId, classId);
        notificationService.send("Booked " + fitnessClass.getName() + " for " + member.getName());
        return booking;
    }

    public List<ClassBooking> listBookingsForMember(int memberId) {
        if (memberRepository.findById(memberId).isEmpty()) {
            throw new NotFoundException("Member not found: " + memberId);
        }
        return bookingRepository.findByMember(memberId);
    }
}
