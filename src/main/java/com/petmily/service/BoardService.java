package com.petmily.service;

import com.petmily.domain.builder.BoardBuilder;
import com.petmily.domain.core.Member;
import com.petmily.domain.core.board.Board;
import com.petmily.domain.dto.board.ModifyBoardForm;
import com.petmily.domain.dto.board.WriteBoardForm;
import com.petmily.domain.enum_type.BoardType;
import com.petmily.repository.BoardRepository;
import com.petmily.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Locale;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class BoardService {

    private final PictureService pictureService;
    private final BoardRepository boardRepository;
    private final MemberRepository memberRepository;
    private final MessageSource ms;

    // 게시글 등록
    @Transactional
    public Long write(Long memberId, BoardType boardType, WriteBoardForm form) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException(getMessage("exception.member.null")));

        Board board = writeBoard(boardType, form, member);

        if (hasPicture(form.getPictures())) {
            pictureService.store(form.getPictures(), board);
        }

        boardRepository.save(board);

        return board.getId();
    }

    private static Board writeBoard(BoardType boardType, WriteBoardForm form, Member member) {
        return new BoardBuilder(member, boardType)
                .setTitle(form.getTitle())
                .setContent(form.getContent())
                .setShownAll(form.getShownAll())
                .build();
    }

    private static boolean hasPicture(List<MultipartFile> form) {
        if (form == null) {
            return false;
        }

        return !form.get(0).isEmpty();
    }

    // 게시글 조회
    public Optional<Board> findOne(Long id) {
        return boardRepository.findById(id);
    }

    // 전체 게시글 조회
    public List<Board> findAll() {
        return boardRepository.findAll();
    }

    // 회원으로 전체 게시글 조회
    public List<Board> findAll(Member member) {
        return boardRepository.findAllByMemberOrderByBoardType(member);
    }

    // 게시글 수정
    @Transactional
    public Long modifyBoardInfo(Long id, ModifyBoardForm form) {
        Board board = boardRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException(getMessage("exception.board.null")));

        board.clearPicture();

        if (hasPicture(form.getPictures())) {
            pictureService.store(form.getPictures(), board);
        }

        board.changeInfo(form);

        return board.getId();
    }

    // 게시글 삭제
    @Transactional
    public void deleteBoard(Long id) {
        boardRepository.deleteById(id);
    }

    private String getMessage(String code) {
        return ms.getMessage(code, null, Locale.KOREA);
    }
}
