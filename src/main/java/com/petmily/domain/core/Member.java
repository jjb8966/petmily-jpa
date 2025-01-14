package com.petmily.domain.core;

import com.petmily.domain.builder.MemberBuilder;
import com.petmily.domain.core.application.Application;
import com.petmily.domain.core.board.Board;
import com.petmily.domain.dto.member.ModifyMemberForm;
import com.petmily.domain.embeded_type.Email;
import com.petmily.domain.embeded_type.PhoneNumber;
import com.petmily.domain.enum_type.MemberGrade;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.List;

@Entity
@Getter
@ToString(of = {"loginId", "password", "name", "birth", "email", "phone", "grade"})
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member extends BaseEntity {

    @Id
    @GeneratedValue
    @Column(name = "member_id")
    private Long id;

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Board> boards;

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Reply> replies;

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Application> applications;

    @Enumerated(EnumType.STRING)
    private MemberGrade grade;

    @Embedded
    private PhoneNumber phoneNumber;

    @Embedded
    private Email email;

    private String loginId;
    private String password;
    private String name;
    private LocalDate birth;

    public Member(MemberBuilder builder) {
        this.boards = builder.getBoards();
        this.replies = builder.getReplies();
        this.applications = builder.getApplies();
        this.grade = builder.getGrade();
        this.loginId = builder.getLoginId();
        this.password = builder.getPassword();
        this.name = builder.getName();
        this.birth = builder.getBirth();
        this.email = builder.getEmail();
        this.phoneNumber = builder.getPhoneNumber();
    }

    public void changeInfo(ModifyMemberForm form) {
        this.loginId = form.getLoginId();
        this.password = form.getPassword();
        this.name = form.getName();
        this.email = form.getEmail();
        this.phoneNumber = form.getPhoneNumber();
    }

}