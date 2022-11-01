package com.petmily.service;

import com.petmily.domain.builder.AbandonedAnimalBuilder;
import com.petmily.domain.builder.BoardBuilder;
import com.petmily.domain.builder.MemberBuilder;
import com.petmily.domain.core.AbandonedAnimal;
import com.petmily.domain.core.Board;
import com.petmily.domain.core.Member;
import com.petmily.domain.core.application.Adopt;
import com.petmily.domain.dto.application.ApplyAdoptDto;
import com.petmily.domain.dto.board.WriteBoardDto;
import com.petmily.domain.dto.member.ChangeMemberDto;
import com.petmily.domain.dto.reply.ReplyDto;
import com.petmily.domain.core.enum_type.BoardType;
import com.petmily.exception.DuplicateLoginIdException;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Transactional
@Slf4j
class MemberServiceTest {

    @Autowired
    MemberService memberService;

    @Autowired
    AbandonedAnimalService animalService;

    @Autowired
    BoardService boardService;

    @Autowired
    ReplyService replyService;

    @Autowired
    ApplicationService applicationService;

    @Test
    @DisplayName("회원 가입 및 id를 통한 조회를 할 수 있다.")
    void join_findMember() {
        //given
        Member member = new MemberBuilder("abc", "abc")
                .setName("aaa")
                .setPhone("123")
                .build();

        //when
        Long joinId = memberService.join(member);
        Member findMember = memberService.findOne(joinId).get();

        //then
        assertThat(findMember).isEqualTo(member);
    }

    @Test
    @DisplayName("이미 존재하는 로그인 아이디로 회원 가입 시 예외가 발생해 중복 회원 가입을 방지할 수 있다.")
    void duplicateCheck() {
        //given
        Member member1 = new MemberBuilder("abc", "111").build();
        Member member2 = new MemberBuilder("abc", "222").build();

        //when
        memberService.join(member1);

        //then
        assertThatThrownBy(() -> memberService.join(member2))
                .isInstanceOf(DuplicateLoginIdException.class)
                .hasMessage("중복된 아이디입니다.");
    }

    @Test
    @DisplayName("모든 회원을 조회할 수 있다.")
    void findAllMember() {
        //given
        Member member1 = new MemberBuilder("memberA", "111").build();
        Member member2 = new MemberBuilder("memberB", "222").build();
        Member member3 = new MemberBuilder("memberC", "333").build();
        memberService.join(member1);
        memberService.join(member2);
        memberService.join(member3);

        //when
        List<Member> allMember = memberService.findAll();

        //then
        assertThat(allMember.size()).isEqualTo(3);
        assertThat(allMember).containsExactly(member1, member2, member3);
    }

    @Test
    @DisplayName("회원 정보를 변경할 수 있다.")
    void changeInfo() {
        //given
        Member member = new MemberBuilder("memberA", "111")
                .setName("A")
                .setPhone("111")
                .build();

        Long joinId = memberService.join(member);

        ChangeMemberDto changeMemberDto = new ChangeMemberDto();
        changeMemberDto.setLoginId("memberA");
        changeMemberDto.setPassword("222");
        changeMemberDto.setName("B");
        changeMemberDto.setPhone("222");

        //when
        memberService.changeMemberInfo(joinId, changeMemberDto);

        //then
        assertThat(member.getPassword()).isEqualTo("222");
    }

    @Test
    @DisplayName("회원 탈퇴를 할 수 있다.")
    void withdraw() {
        //given
        Member member = new MemberBuilder("memberA", "111").build();
        memberService.join(member);

        //when
        memberService.withdrawMember(member.getId());

        //then
        boolean isPresent = memberService.findOne(member.getId()).isPresent();
        assertThat(isPresent).isFalse();
    }

    @Test
    @DisplayName("회원 탈퇴 시 작성한 지원서, 게시글, 댓글은 모두 삭제된다.")
    void withdraw2() {
        //given
        Member member = new MemberBuilder("memberA", "111").build();
        AbandonedAnimal animal = new AbandonedAnimalBuilder().setName("animalA").build();
        memberService.join(member);
        animalService.register(animal);

        Long boardId = boardService.write(member.getId(), new WriteBoardDto());
        Long replyId = replyService.reply(member.getId(), boardId, new ReplyDto());
        Long adoptId = applicationService.adopt(member.getId(), animal.getId(), new ApplyAdoptDto());

        //when
        memberService.withdrawMember(member.getId());

        //then
        assertThat(memberService.findOne(member.getId()).isEmpty()).isTrue();
        assertThat(boardService.findOne(boardId).isEmpty()).isTrue();
        assertThat(replyService.findOne(replyId).isEmpty()).isTrue();
        assertThat(applicationService.findOne(adoptId, Adopt.class).isEmpty()).isTrue();
        assertThat(animalService.findOne(animal.getId()).get().getApplications().isEmpty()).isTrue();
    }

    @Test
    @DisplayName("영속성 전이를 통해 member를 영속 상태로 만들면 board도 영속 상태가 된다.")
    void test() {
        Member member = new MemberBuilder("memberA", "a")
                .setName("hi")
                .setPhone("010")
                .setEmail("a@a")
                .build();

        Board board = new BoardBuilder(member, BoardType.FREE)
                .setTitle("board title")
                .setContent("board content")
                .build();

        memberService.join(member);

        Board findBoard = boardService.findOne(board.getId()).orElseThrow();
        log.info("findBoard = {}", findBoard);

        assertThat(boardService.findOne(board.getId()).isPresent()).isTrue();
    }
}