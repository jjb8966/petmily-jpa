package com.petmily.service;

import com.petmily.domain.core.Member;
import com.petmily.domain.dto.member.LoginForm;
import com.petmily.domain.dto.member.ModifyMemberForm;
import com.petmily.exception.DuplicateLoginIdException;
import com.petmily.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final ApplicationService applicationService;
    private final ReplyService replyService;
    private final BoardService boardService;

    // 회원 가입
    @Transactional
    public Long join(Member member) {
        duplicateCheck(member.getLoginId());
        memberRepository.save(member);

        return member.getId();
    }

    // 로그인
    public Optional<Member> login(LoginForm loginForm) {
        return memberRepository.findByLoginId(loginForm.getLoginId())
                .filter(member -> member.getPassword().equals(loginForm.getPassword()))
                .stream()
                .findAny();
    }

    private void duplicateCheck(String loginId) {
        List<Member> allMember = findAll();

        allMember.stream()
                .filter(m -> m.getLoginId().equals(loginId))
                .findAny()
                .ifPresent(m -> {
                    throw new DuplicateLoginIdException("중복된 아이디입니다.");
                });
    }

    // 회원 조회
    public Optional<Member> findOne(Long id) {
        return memberRepository.findById(id);
    }

    // 전체 회원 조회
    public List<Member> findAll() {
        return memberRepository.findAll();
    }

    // 회원 정보 변경
    @Transactional
    public Long modify(Long id, ModifyMemberForm form) {
        Member member = memberRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다."));

        member.changeInfo(form);

        return member.getId();
    }

    // 회원 탈퇴
    @Transactional
    public void withdrawMember(Long id) {
        Member member = memberRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다."));

        deleteApplicationsAboutMember(id);
        deleteRepliesAboutMember(id);
        deleteBoardsAboutMember(id);

        memberRepository.deleteById(id);
    }

    private void deleteApplicationsAboutMember(Long memberId) {
        applicationService.findAll().stream()
                .filter(application -> application.getMember().getId().equals(memberId))
                .forEach(application -> applicationService.deleteApplication(application.getId()));
    }

    private void deleteRepliesAboutMember(Long memberId) {
        replyService.findAll().stream()
                .filter(reply -> reply.getMember().getId().equals(memberId))
                .forEach(reply -> replyService.deleteReply(reply.getId()));
    }

    private void deleteBoardsAboutMember(Long memberId) {
        boardService.findAll().stream()
                .filter(board -> board.getMember().getId().equals(memberId))
                .forEach(board -> boardService.deleteBoard(board.getId()));
    }
}
