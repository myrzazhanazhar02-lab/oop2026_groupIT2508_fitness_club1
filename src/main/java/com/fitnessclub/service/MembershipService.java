package com.fitnessclub.service;

import com.fitnessclub.exception.InvalidInputException;
import com.fitnessclub.exception.NotFoundException;
import com.fitnessclub.model.Member;
import com.fitnessclub.model.MembershipType;
import com.fitnessclub.model.MembershipTypeBuilder;
import com.fitnessclub.model.MembershipTypeFactory;
import com.fitnessclub.repository.MemberRepository;
import com.fitnessclub.repository.MembershipTypeRepository;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class MembershipService {
    private final MemberRepository memberRepository;
    private final MembershipTypeRepository membershipTypeRepository;
    private final NotificationService notificationService;

    public MembershipService(MemberRepository memberRepository,
                             MembershipTypeRepository membershipTypeRepository,
                             NotificationService notificationService) {
        this.memberRepository = memberRepository;
        this.membershipTypeRepository = membershipTypeRepository;
        this.notificationService = notificationService;
    }

    public Member createMember(String name, String email, String phone, Integer membershipTypeId) {
        validateString(name, "Name is required");
        validateString(email, "Email is required");
        LocalDate endDate = null;
        if (membershipTypeId != null) {
            MembershipType type = membershipTypeRepository.findById(membershipTypeId)
                    .orElseThrow(() -> new NotFoundException("Membership type not found: " + membershipTypeId));
            endDate = LocalDate.now().plusDays(type.getDurationDays());
        }
        Member member = memberRepository.create(name.trim(), email.trim(), phone == null ? null : phone.trim(),
                membershipTypeId, endDate);
        notificationService.send("New member created: " + member.getName());
        return member;
    }

    public List<Member> findActiveMembersToday() {
        return memberRepository.findActiveOn(LocalDate.now())
                .stream()
                .sorted(Comparator.comparing(Member::getMembershipEndDate))
                .collect(Collectors.toList());
    }

    public MembershipType createMembershipType(String name, int durationDays, double price, Integer visitLimit) {
        validateString(name, "Name is required");
        MembershipType type = new MembershipTypeBuilder()
                .withName(name.trim())
                .withDurationDays(durationDays)
                .withPrice(price)
                .withVisitLimit(visitLimit)
                .build();
        return membershipTypeRepository.create(type);
    }

    public MembershipType createMonthlyUnlimited(String displayName, double price) {
        MembershipType type = MembershipTypeFactory.unlimitedMonthly(displayName, price);
        return membershipTypeRepository.create(type);
    }

    public Member buyMembership(int memberId, int membershipTypeId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new NotFoundException("Member not found: " + memberId));
        MembershipType type = membershipTypeRepository.findById(membershipTypeId)
                .orElseThrow(() -> new NotFoundException("Membership type not found: " + membershipTypeId));
        LocalDate newEndDate = LocalDate.now().plusDays(type.getDurationDays());
        Member updated = memberRepository.updateMembership(member.getId(), type.getId(), newEndDate);
        notificationService.send("Membership purchased for " + member.getName() + " until " + newEndDate);
        return updated;
    }

    public Member extendMembership(int memberId, int membershipTypeId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new NotFoundException("Member not found: " + memberId));
        MembershipType type = membershipTypeRepository.findById(membershipTypeId)
                .orElseThrow(() -> new NotFoundException("Membership type not found: " + membershipTypeId));
        LocalDate startDate = member.getMembershipEndDate() != null && member.getMembershipEndDate().isAfter(LocalDate.now())
                ? member.getMembershipEndDate()
                : LocalDate.now();
        LocalDate newEndDate = startDate.plusDays(type.getDurationDays());
        Member updated = memberRepository.updateMembership(member.getId(), type.getId(), newEndDate);
        notificationService.send("Membership extended for " + member.getName() + " until " + newEndDate);
        return updated;
    }

    public List<Member> listMembers() {
        return memberRepository.findAll();
    }

    public List<MembershipType> listMembershipTypes() {
        return membershipTypeRepository.findAll();
    }

    public Member getMember(int id) {
        return memberRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Member not found: " + id));
    }

    private void validateString(String value, String message) {
        if (value == null || value.trim().isEmpty()) {
            throw new InvalidInputException(message);
        }
    }
}
